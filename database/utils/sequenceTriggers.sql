
/*Sequence triggers to not modify ids*/

/*Create sequences for id*/

CREATE SEQUENCE wagony_seq;
CREATE SEQUENCE sklady_seq;
CREATE SEQUENCE rozklady_seq;
CREATE SEQUENCE pociagi_seq;
CREATE SEQUENCE trasy_seq;
CREATE SEQUENCE odcinki_seq;
CREATE SEQUENCE stacje_seq;


--insert


CREATE OR REPLACE FUNCTION setIndependentIdWagonu()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_wagonu := nextval('wagony_seq');
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;
    
CREATE TRIGGER independentIdWagonu
    BEFORE INSERT ON wagony
    FOR EACH ROW
    EXECUTE PROCEDURE setIndependentIdWagonu();
    
    
CREATE OR REPLACE FUNCTION setIndependentIdSkladu()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_skladu := nextval('sklady_seq');
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;
    
CREATE TRIGGER independentIdSkladu
    BEFORE INSERT ON sklady
    FOR EACH ROW
    EXECUTE PROCEDURE setIndependentIdSkladu();
    
    
CREATE OR REPLACE FUNCTION setIndependentIdKursu()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_kursu := nextval('rozklady_seq');
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;
    
CREATE TRIGGER independentIdKursu
    BEFORE INSERT ON rozklady
    FOR EACH ROW
    EXECUTE PROCEDURE setIndependentIdKursu();
    
    
CREATE OR REPLACE FUNCTION setIndependentIdPociagu()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_pociagu := nextval('pociagi_seq');
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;
    
CREATE TRIGGER independentIdPociagu
    BEFORE INSERT ON pociagi
    FOR EACH ROW
    EXECUTE PROCEDURE setIndependentIdPociagu();

    
CREATE OR REPLACE FUNCTION setIndependentIdTrasy()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_trasy := nextval('trasy_seq');
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;
    
CREATE TRIGGER independentIdTrasy
    BEFORE INSERT ON trasy
    FOR EACH ROW
    EXECUTE PROCEDURE setIndependentIdTrasy();
    
    
CREATE OR REPLACE FUNCTION setIndependentIdOdcinka()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_odcinka := nextval('odcinki_seq');
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;
    
CREATE TRIGGER independentIdOdcinka
    BEFORE INSERT ON odcinki
    FOR EACH ROW
    EXECUTE PROCEDURE setIndependentIdOdcinka();
    
    
CREATE OR REPLACE FUNCTION setIndependentIdStacji()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_stacji := nextval('stacje_seq');
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;
    
CREATE TRIGGER independentIdStacji
    BEFORE INSERT ON stacje
    FOR EACH ROW
    EXECUTE PROCEDURE setIndependentIdStacji();
    
    
--update


CREATE OR REPLACE FUNCTION disableIdWagonuUpdate()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_wagonu := OLD.id_wagonu;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER noIdWagonuUpdate
    BEFORE UPDATE ON wagony
    FOR EACH ROW
    EXECUTE PROCEDURE disableIdWagonuUpdate();
    
    
CREATE OR REPLACE FUNCTION disableIdSkladuUpdate()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_skladu := OLD.id_skladu;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER noIdSkladuUpdate
    BEFORE UPDATE ON sklady
    FOR EACH ROW
    EXECUTE PROCEDURE disableIdSkladuUpdate();
    

CREATE OR REPLACE FUNCTION disableIdKursuUpdate()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_kursu := OLD.id_kursu;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER noIdKursuUpdate
    BEFORE UPDATE ON rozklady
    FOR EACH ROW
    EXECUTE PROCEDURE disableIdKursuUpdate();
    
    
CREATE OR REPLACE FUNCTION disableIdPociaguUpdate()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_pociagu := OLD.id_pociagu;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER noIdPociaguUpdate
    BEFORE UPDATE ON pociagi
    FOR EACH ROW
    EXECUTE PROCEDURE disableIdPociaguUpdate();
    
    
CREATE OR REPLACE FUNCTION disableIdTrasyUpdate()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_trasy := OLD.id_trasy;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER noIdTrasyUpdate
    BEFORE UPDATE ON trasy
    FOR EACH ROW
    EXECUTE PROCEDURE disableIdTrasyUpdate();
    
    
CREATE OR REPLACE FUNCTION disableIdOdcinkaUpdate()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_odcinka := OLD.id_odcinka;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER noIdOdcinkaUpdate
    BEFORE UPDATE ON odcinki
    FOR EACH ROW
    EXECUTE PROCEDURE disableIdOdcinkaUpdate();
    
    
CREATE OR REPLACE FUNCTION disableIdStacjiUpdate()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.id_stacji := OLD.id_stacji;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER noIdStacjiUpdate
    BEFORE UPDATE ON stacje
    FOR EACH ROW
    EXECUTE PROCEDURE disableIdStacjiUpdate();


