
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
        SELECT a.przyjazd, a.odjazd, getday(id_kursu, id_stacji, 'Przyjazd') as przyjazdDay, getday(id_kursu, id_stacji, 'Odjazd') as odjazdDay
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
