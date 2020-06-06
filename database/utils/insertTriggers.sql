
/*
triggers for inserts or updates
*/

CREATE OR REPLACE FUNCTION stacjeOverflowUpdate()
    RETURNS TRIGGER AS
$$
BEGIN
    IF checkStationOverflow(NEW.id_stacji, NEW.liczba_torow::integer) IS TRUE THEN
        RETURN NEW;
    ELSE
        RAISE EXCEPTION 'Station overflow';
    END IF;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER stacjeOverflowUpdateTrigger
    BEFORE UPDATE ON stacje
    FOR EACH ROW
    EXECUTE PROCEDURE stacjeOverflowUpdate();


CREATE OR REPLACE FUNCTION stacjeLengthUpdate()
    RETURNS TRIGGER AS
$$
BEGIN
    IF checkStationLength(NEW.id_stacji, NEW.dlugosc_peronu) IS TRUE THEN
        RETURN NEW;
    ELSE
        RAISE EXCEPTION 'Station is too short';
    END IF;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER stacjeLengthUpdateTrigger
    BEFORE UPDATE ON stacje
    FOR EACH ROW
    EXECUTE PROCEDURE stacjeLengthUpdate();


CREATE OR REPLACE FUNCTION kursStacjeLengthInsert()
    RETURNS TRIGGER AS
$$
DECLARE
    len stacje.dlugosc_peronu%TYPE;
    bef integer;
BEGIN
    len := (SELECT dlugosc_peronu FROM stacje
        WHERE id_stacji = NEW.id_stacji);
    bef := (SELECT getPreviousSklad(NEW.id_stacji, NEW.id_kursu));

    IF len < getSkladLength(NEW.nastepny_sklad) THEN
        RAISE EXCEPTION USING
            errcode='LENGT',
            message='Station is too short';
    ELSIF len < getSkladLength(bef) THEN
        RAISE EXCEPTION USING
            errcode='LENGT',
            message='Station is too short';
    ELSE
        RETURN NEW;
    END IF;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER kursStacjeLengthInsertTrigger
    AFTER INSERT OR UPDATE ON postoje
    FOR EACH ROW
    EXECUTE PROCEDURE kursStacjeLengthInsert();


CREATE OR REPLACE FUNCTION kursStacjeOverflowInsert()
    RETURNS TRIGGER AS
$$
DECLARE
    tory stacje.liczba_torow%TYPE;
BEGIN
    tory := (SELECT liczba_torow FROM stacje
        WHERE id_stacji = NEW.id_stacji);

    IF checkStationOverflow(NEW.id_stacji, tory::integer) IS TRUE THEN
        RETURN NEW;
    ELSE
        RAISE EXCEPTION USING
            errcode='OVERF',
            message='Station overflow';
    END IF;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER kursStacjeOverflowInsertTrigger
    AFTER INSERT OR UPDATE ON postoje
    FOR EACH ROW
    EXECUTE PROCEDURE kursStacjeOverflowInsert();
