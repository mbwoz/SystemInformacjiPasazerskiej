
/*Clear*/

--id
DROP TRIGGER independentIdWagonu ON wagony;
DROP FUNCTION setIndependentIdWagonu;
DROP TRIGGER independentIdSkladu ON sklady;
DROP FUNCTION setIndependentIdSkladu;
DROP TRIGGER independentIdKursu ON rozklady;
DROP FUNCTION setIndependentIdKursu;
DROP TRIGGER independentIdPociagu ON pociagi;
DROP FUNCTION setIndependentIdPociagu;
DROP TRIGGER independentIdTrasy ON trasy;
DROP FUNCTION setIndependentIdTrasy;
DROP TRIGGER independentIdOdcinka ON odcinki;
DROP FUNCTION setIndependentIdOdcinka;
DROP TRIGGER independentIdStacji ON stacje;
DROP FUNCTION setIndependentIdStacji;

DROP TRIGGER noIdWagonuUpdate ON wagony;
DROP FUNCTION disableIdWagonuUpdate;
DROP TRIGGER noIdSkladuUpdate ON sklady;
DROP FUNCTION disableIdSkladuUpdate;
DROP TRIGGER noIdKursuUpdate ON rozklady;
DROP FUNCTION disableIdKursuUpdate;
DROP TRIGGER noIdPociaguUpdate ON pociagi;
DROP FUNCTION disableIdPociaguUpdate;
DROP TRIGGER noIdTrasyUpdate ON trasy;
DROP FUNCTION disableIdTrasyUpdate;
DROP TRIGGER noIdOdcinkaUpdate ON odcinki;
DROP FUNCTION disableIdOdcinkaUpdate;
DROP TRIGGER noIdStacjiUpdate ON stacje;
DROP FUNCTION disableIdStacjiUpdate;


--triggers
DROP TRIGGER stacjeOverflowUpdateTrigger ON stacje;
DROP FUNCTION stacjeOverflowUpdate;
DROP TRIGGER stacjeLengthUpdateTrigger ON stacje;
DROP FUNCTION stacjeLengthUpdate;


--utils
DROP FUNCTION getFirstStation;
DROP FUNCTION getLastStation;
DROP FUNCTION getIdTrasyFromTo;
DROP FUNCTION getDay;
DROP FUNCTION getStationsBetween;
DROP FUNCTION getStationsBetweenOnTrasa;
DROP FUNCTION checkStationOverflow;
DROP FUNCTION checkStationLength;
DROP FUNCTION getSkladLength;
DROP FUNCTION getPreviousSklad;


--tables
DROP TABLE IF EXISTS wagony CASCADE;
DROP TABLE IF EXISTS sklady CASCADE;
DROP TABLE IF EXISTS sklady_wagony CASCADE;
DROP TABLE IF EXISTS postoje CASCADE;
DROP TABLE IF EXISTS rozklady CASCADE;
DROP TABLE IF EXISTS pociagi CASCADE;
DROP TABLE IF EXISTS trasy_odcinki CASCADE;
DROP TABLE IF EXISTS trasy CASCADE;
DROP TABLE IF EXISTS stacje CASCADE;
DROP TABLE IF EXISTS odcinki CASCADE;

DROP SEQUENCE wagony_seq;
DROP SEQUENCE sklady_seq;
DROP SEQUENCE rozklady_seq;
DROP SEQUENCE pociagi_seq;
DROP SEQUENCE trasy_seq;
DROP SEQUENCE odcinki_seq;
DROP SEQUENCE stacje_seq;
