
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
