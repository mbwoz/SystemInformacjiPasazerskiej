
/*
functions for queries on db
*/


CREATE OR REPLACE FUNCTION getFirstStation(
    idTrasy trasy.id_trasy%TYPE
) RETURNS stacje.id_stacji%TYPE AS
$$
DECLARE
    idStacji stacje.id_stacji%TYPE;
BEGIN
    idStacji :=
        (WITH trasy_stacje AS (
            SELECT od.stacja_poczatkowa, od.stacja_koncowa
            FROM trasy_odcinki trod
                INNER JOIN odcinki od ON trod.id_odcinka = od.id_odcinka
            WHERE trod.id_trasy = idTrasy
        )
        SELECT ts.stacja_poczatkowa
        FROM trasy_stacje ts
        WHERE ts.stacja_poczatkowa NOT IN 
            (SELECT t.stacja_koncowa FROM trasy_stacje t));
            
    RETURN idStacji;
END;
$$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION getLastStation(
    idTrasy trasy.id_trasy%TYPE
) RETURNS stacje.id_stacji%TYPE AS
$$
DECLARE
    idStacji stacje.id_stacji%TYPE;
BEGIN
    idStacji :=
        (WITH trasy_stacje AS (
            SELECT od.stacja_poczatkowa, od.stacja_koncowa
            FROM trasy_odcinki trod
                INNER JOIN odcinki od ON trod.id_odcinka = od.id_odcinka
            WHERE trod.id_trasy = idTrasy
        )
        SELECT ts.stacja_koncowa
        FROM trasy_stacje ts
        WHERE ts.stacja_koncowa NOT IN 
            (SELECT t.stacja_poczatkowa FROM trasy_stacje t));
            
    RETURN idStacji;
END;
$$
LANGUAGE plpgsql;


----


CREATE OR REPLACE FUNCTION getIdTrasyFromTo(
    fromStationId odcinki.stacja_poczatkowa%TYPE,
    toStationId odcinki.stacja_koncowa%TYPE
) RETURNS TABLE (idTrasy trasy.id_trasy%TYPE) AS
$$
BEGIN
    RETURN QUERY
    WITH RECURSIVE fullTrasa AS (
        SELECT 
            trod.id_trasy AS idTrasy,
            od.stacja_poczatkowa AS stacjaPoczatkowa,
            od.stacja_koncowa AS stacjaKoncowa
        FROM trasy_odcinki trod
            INNER JOIN odcinki od ON trod.id_odcinka = od.id_odcinka
        WHERE
            od.stacja_poczatkowa = fromStationId
        UNION
        SELECT
            trod.id_trasy AS idTrasy,
            ft.stacjaPoczatkowa AS stacjaPoczatkowa,
            od.stacja_koncowa AS stacjaKoncowa
        FROM trasy_odcinki trod
            INNER JOIN odcinki od ON trod.id_odcinka = od.id_odcinka
            INNER JOIN fullTrasa ft ON od.stacja_poczatkowa = ft.stacjaKoncowa
        WHERE trod.id_trasy = ft.idTrasy
    )
    SELECT ft.idTrasy
    FROM fullTrasa ft
    WHERE ft.stacjaPoczatkowa = fromStationId AND
        ft.stacjaKoncowa = toStationId;
END;
$$
LANGUAGE plpgsql;


----


CREATE OR REPLACE FUNCTION getDay(
    idKursu postoje.id_kursu%TYPE,
    idStacji postoje.id_stacji%TYPE
) RETURNS rozklady.dzien_tygodnia%TYPE AS
$$
DECLARE
    idTrasy trasy.id_trasy%TYPE;
    dzienTygodnia rozklady.dzien_tygodnia%TYPE;
    stacja stacje.id_stacji%TYPE;
    lastStop time;
    nextStop time;
BEGIN
    idTrasy = 
        (SELECT po.id_trasy
        FROM pociagi po
        WHERE po.id_pociagu IN
            (SELECT ro.id_pociagu
            FROM rozklady ro
            WHERE ro.id_kursu = idKursu));
    
    dzienTygodnia = 
        (SELECT ro.dzien_tygodnia
        FROM rozklady ro
        WHERE ro.id_kursu = idKursu);
    
    stacja = 
        (SELECT getFirstStation(idTrasy));
        
    lastStop := '00:00:00'::time;
    
    WHILE stacja IS NOT NULL LOOP
        nextStop :=
            (SELECT pos.odjazd 
            FROM postoje pos 
            WHERE pos.id_kursu = idKursu AND pos.id_stacji = stacja);
    
        IF lastStop > nextStop THEN
            dzienTygodnia := (dzienTygodnia + 1) % 7;
        END IF;
        
        EXIT WHEN stacja = idStacji;
        
        lastStop := nextStop;            
    
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
    
    RETURN dzienTygodnia;
END;
$$
LANGUAGE plpgsql;


----


CREATE OR REPLACE FUNCTION getStationsBetween(
    idKursu rozklady.id_kursu%TYPE,
    fromStationId odcinki.stacja_poczatkowa%TYPE,
    toStationId odcinki.stacja_koncowa%TYPE
) RETURNS TABLE (idStacji postoje.id_stacji%TYPE) AS
$$
DECLARE
    idTrasy trasy.id_trasy%TYPE;
BEGIN
    idTrasy = 
        (SELECT po.id_trasy
        FROM pociagi po
        WHERE po.id_pociagu IN
            (SELECT ro.id_pociagu
            FROM rozklady ro
            WHERE ro.id_kursu = idKursu));

    RETURN QUERY
    WITH RECURSIVE betweenTrasa AS (
        SELECT 
            od.stacja_poczatkowa AS stacjaPoczatkowa,
            od.stacja_poczatkowa AS stacjaKoncowa
        FROM trasy_odcinki trod
            INNER JOIN odcinki od ON trod.id_odcinka = od.id_odcinka
        WHERE
            od.stacja_poczatkowa = fromStationId AND
            trod.id_trasy = idTrasy
        UNION
        SELECT
            bt.stacjaPoczatkowa AS stacjaPoczatkowa,
            od.stacja_koncowa AS stacjaKoncowa
        FROM trasy_odcinki trod
            INNER JOIN odcinki od ON trod.id_odcinka = od.id_odcinka
            INNER JOIN betweenTrasa bt ON od.stacja_poczatkowa = bt.stacjaKoncowa
        WHERE
            od.stacja_poczatkowa <> toStationId AND
            trod.id_trasy = idTrasy
    )
    SELECT bt.stacjaKoncowa
    FROM betweenTrasa bt;
END;
$$
LANGUAGE plpgsql;

