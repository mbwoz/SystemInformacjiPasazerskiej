
/*
functions for inserts or updates
*/


CREATE OR REPLACE FUNCTION getSkladLength(
    sklad integer
) RETURNS numeric
AS $$
DECLARE
    ans numeric;

BEGIN
    ans =
        (SELECT SUM(a.liczba_wagonow * b.dlugosc_wagonu)
        FROM sklady_wagony a LEFT JOIN wagony b ON a.id_wagonu = b.id_wagonu
        WHERE a.id_skladu = sklad);

    RETURN ans;
END;
$$ language plpgsql;


----


-- zwraca sklad ktory wjezdza na stacje, dla stacji poczatkowej zwraca sklad wyjezdzajacy
CREATE OR REPLACE FUNCTION getPreviousSklad(
    idStacji integer,
    idKursu integer
) RETURNS integer
AS $$
DECLARE
    idTrasy trasy.id_trasy%TYPE;
    stacja stacje.id_stacji%TYPE;
    lastSklad integer;
BEGIN
    idTrasy =
        (SELECT po.id_trasy
        FROM pociagi po
        WHERE po.id_pociagu IN
            (SELECT ro.id_pociagu
            FROM rozklady ro
            WHERE ro.id_kursu = idKursu));

    stacja =
        (SELECT getFirstStation(idTrasy));

    lastSklad =
        (SELECT pos.nastepny_sklad
        FROM postoje pos
        WHERE pos.id_kursu = idKursu AND pos.id_stacji = stacja);

    IF stacja = idStacji THEN
        RETURN lastSklad;
    END IF;

    stacja :=
        (SELECT od.stacja_koncowa
        FROM trasy_odcinki trod
            INNER JOIN odcinki od ON trod.id_odcinka = od.id_odcinka
        WHERE trod.id_trasy = idTrasy AND
            stacja = od.stacja_poczatkowa);

    WHILE stacja IS NOT NULL LOOP
        EXIT WHEN stacja = idStacji;

        lastSklad =
            (SELECT pos.nastepny_sklad
            FROM postoje pos
            WHERE pos.id_kursu = idKursu AND pos.id_stacji = stacja);

        stacja :=
            (SELECT od.stacja_koncowa
            FROM trasy_odcinki trod
            INNER JOIN odcinki od ON trod.id_odcinka = od.id_odcinka
            WHERE trod.id_trasy = idTrasy AND
                stacja = od.stacja_poczatkowa);
    END LOOP;

    IF stacja IS NULL THEN
        RETURN NULL;
    END IF;

    RETURN lastSklad;
END;
$$ language plpgsql;


----


-- sprawdzenie, czy dana liczba torow jest wystarczajaca
create or replace function checkStationOverflow(
    stacja integer,
    tory integer
) RETURNS boolean
as $$
declare
    tab integer[10100];
    rec record;
    i integer;
    end_number integer;

begin
    FOR rec in
        SELECT a.id_kursu, a.przyjazd, a.odjazd, getday(id_kursu, id_stacji, 'Przyjazd') as przyjazdDay, getday(id_kursu, id_stacji, 'Odjazd') as odjazdDay
        FROM postoje a
        WHERE id_stacji = stacja
    LOOP
        i := EXTRACT(EPOCH FROM rec.przyjazd::INTERVAL)/60 + (rec.przyjazdDay * 1440);
        end_number := EXTRACT(EPOCH FROM rec.odjazd::INTERVAL)/60 + (rec.odjazdDay * 1440);

        WHILE i != end_number LOOP

            IF tab[i] IS NULL THEN
                tab[i] := 1;
            ELSE
                tab[i] := tab[i] + 1;
            END IF;

            IF tab[i] > tory THEN
--                RAISE NOTICE 'godzina: %, %', i, tab[i];
                RETURN FALSE;
            END IF;

            i := (i + 1) % 10080;
        END LOOP;
    END LOOP;

    RETURN TRUE;
end;
$$ language plpgsql;


----


-- sprawdzenie, czy dana dlugosc peronu jest wystarczajaca
CREATE OR REPLACE FUNCTION checkStationLength(
    stacja integer,
    dlugosc numeric(5,2)
) RETURNS boolean
AS $$
DECLARE
    rec record;
    fir numeric(5,2);
    sec numeric(5,2);
BEGIN

    fir =
        (SELECT MAX(getSkladLength(a.nastepny_sklad))
        FROM postoje a where id_stacji = stacja);

    sec =
        (SELECT MAX(getSkladLength(getPreviousSklad(a.id_stacji, a.id_kursu)))
        FROM postoje a where id_stacji = stacja);

    IF fir > dlugosc THEN
        RETURN FALSE;
    ELSIF sec > dlugosc THEN
        RETURN FALSE;
    END IF;

    RETURN TRUE;
END;
$$ language plpgsql;
----


-- sprawdzanie czy dany sklad istnieje
create or replace function checkSkladQuery(rodzaj integer[], ilosc integer[], n integer, przesylki char(1))
    returns boolean as
$$
declare
    i integer;
    que varchar;
    rec record;
    wyn char(1);
begin
    que := 'SELECT fir.id_skladu as id, fir.suma, sec.ilosc
            FROM (
                SELECT id_skladu, COUNT(*) as suma
                FROM sklady_wagony
                GROUP BY 1
                ORDER BY 1
            ) fir LEFT JOIN (';

    que := que || 'SELECT a.id_skladu, COUNT(*) as ilosc FROM sklady_wagony a WHERE (a.id_wagonu = ' ||
        rodzaj[1]::varchar || ' AND a.liczba_wagonow = ' || ilosc[1]::varchar || ')';

    for i in 2 .. n
    loop
        que := que || ' OR (a.id_wagonu = ' || rodzaj[i]::varchar || ' AND a.liczba_wagonow = ' || ilosc[i]::varchar || ')';
    end loop;
    que := que || ' GROUP BY 1 ) sec ON fir.id_skladu = sec.id_skladu WHERE fir.suma = sec.ilosc AND fir.suma = ' || n;

    for rec in
        execute que
    loop
        i := rec.id;
        SELECT czy_przesylki INTO wyn
            FROM sklady WHERE id_skladu = i;

--        RAISE NOTICE 'Value: %', wyn;

        IF wyn = przesylki THEN
            RETURN TRUE;
        END IF;
    end loop;

    RETURN FALSE;
end;
$$
language plpgsql;
----


-- dodanie danego skladu
create or replace function insertSkladQuery(rodzaj integer[], ilosc integer[], n integer, przesylki char(1))
    returns void as
$$
declare
    val boolean;
    id integer;
    i integer;
begin
    SELECT checkSkladQuery(rodzaj, ilosc, n, przesylki) INTO val;
    IF val = TRUE THEN
        RAISE EXCEPTION 'Sklad exists';
        RETURN;
    END IF;

    INSERT INTO sklady(czy_przesylki) VALUES(przesylki);
    SELECT currval('sklady_seq') INTO id;
    FOR i IN 1 .. n
    LOOP
        EXECUTE 'INSERT INTO sklady_wagony(id_skladu, id_wagonu, liczba_wagonow) VALUES(' ||
            id || ', ' || rodzaj[i] || ', ' || ilosc[i] || ')';
    END LOOP;
end;
$$
language plpgsql;
----


-- sprawdzenie czy dana trasa instnieje
create or replace function checkTrasaQuery(arr integer[], n integer)
    returns boolean as
$$
DECLARE
    i integer;
    que varchar;
    rec record;
begin
    que := 'SELECT fir.id_trasy as id, fir.suma, sec.ilosc
            FROM (
                SELECT id_trasy, COUNT(*) as suma
                FROM trasy_odcinki
                GROUP BY 1
                ORDER BY 1
            ) fir LEFT JOIN (';

    que := que || 'SELECT a.id_trasy, COUNT(*) as ilosc FROM trasy_odcinki a WHERE (a.id_odcinka = ' || arr[1]::varchar || ')';

    for i in 2 .. n
    loop
        que := que || ' OR (a.id_odcinka = ' || arr[i]::varchar || ')';
    end loop;
    que := que || ' GROUP BY 1 ) sec ON fir.id_trasy = sec.id_trasy WHERE fir.suma = sec.ilosc AND fir.suma = ' || n;

    for rec in
        execute que
    loop
        RETURN TRUE;
    end loop;

    RETURN FALSE;

end;
$$
language plpgsql;
----


-- dodanie danej trasy
create or replace function insertTrasaQuery(arr integer[], n integer, przyspieszona char(1))
    returns void as
$$
DECLARE
    id integer[];
    i integer;
    val integer;
    exist boolean;
begin
    FOR i in 2 .. n
    LOOP
        val := (SELECT id_odcinka FROM odcinki
            WHERE stacja_poczatkowa = arr[i-1] AND stacja_koncowa = arr[i]);

        IF val IS NULL THEN
            INSERT INTO odcinki(stacja_poczatkowa, stacja_koncowa)
            VALUES(arr[i-1], arr[i]);

            id[i-1] := (SELECT currval('odcinki_seq'));
        ELSE
            id[i-1] := val;
        END IF;
    END LOOP;

    exist := (SELECT checkTrasaQuery(id, n-1));
    IF exist = TRUE THEN
        RAISE EXCEPTION 'Trasa exists';
        RETURN;
    END IF;

    INSERT INTO trasy(czy_przyspieszona) VALUES(przyspieszona);
    SELECT currval('trasy_seq') INTO val;
    FOR i IN 1 .. (n-1)
    LOOP
        EXECUTE 'INSERT INTO trasy_odcinki(id_trasy, id_odcinka) VALUES(' ||
            val || ', ' || id[i] ||  ')';
    END LOOP;
end;
$$
language plpgsql;
----


CREATE OR REPLACE FUNCTION getIdTrasyExactlyFromTo(
    fromId odcinki.stacja_poczatkowa%TYPE,
    toId odcinki.stacja_koncowa%TYPE
) RETURNS TABLE (idTrasy trasy.id_trasy%TYPE) AS
$$
begin
    RETURN QUERY
    SELECT id_trasy AS do
    FROM trasy
    WHERE getFirstStation(id_trasy) = fromId AND getLastStation(id_trasy) = toId;
end;
$$
language plpgsql;
----


CREATE OR REPLACE FUNCTION getIdPociaguExactlyFromTo(
    fromId odcinki.stacja_poczatkowa%TYPE,
    toId odcinki.stacja_koncowa%TYPE
) RETURNS TABLE (idPociagu pociagi.id_pociagu%TYPE) AS
$$
begin
    RETURN QUERY
    SELECT id_pociagu AS do
    FROM pociagi
    WHERE getFirstStation(id_trasy) = fromId AND getLastStation(id_trasy) = toId;
end;
$$
language plpgsql;
----


-- dodawanie całego kursu
CREATE OR REPLACE FUNCTION insertKurs(
    idPociag integer, dzien integer, przyjazd time[], odjazd time[], sklad integer[]
) RETURNS void AS
$$
declare
    i integer;
    rec record;
    idTrasy integer;
    idKursu integer;
begin
    begin
    INSERT INTO rozklady(id_pociagu, dzien_tygodnia) VALUES(idPociag, dzien);
    idTrasy := (SELECT id_trasy FROM pociagi
        WHERE id_pociagu = idPociag);
    i := 1;
    idKursu := (SELECT currval('rozklady_seq'));
    RAISE NOTICE 'trasa: %', idTrasy;

    FOR rec IN
    SELECT * FROM getStationsBetween(idKursu, getFirstStation(idTrasy), getLastStation(idTrasy))
    LOOP
        RAISE NOTICE 'insert %', i;
        INSERT INTO postoje(id_kursu, id_stacji, przyjazd, odjazd, nastepny_sklad)
        VALUES(idKursu, rec.idStacji, przyjazd[i], odjazd[i], sklad[i]);
        i := i + 1;
    END LOOP;

    EXCEPTION
        WHEN sqlstate 'OVERF' THEN
        RAISE EXCEPTION 'Overflow on stage %', i;
        WHEN sqlstate 'LENGT' THEN
        RAISE EXCEPTION 'Length on stage %', i;
        WHEN OTHERS THEN
        RAISE EXCEPTION 'Unknown erorr on stage %', i;
    end;
end;
$$
language plpgsql;
