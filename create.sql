DROP TABLE IF EXISTS wagony CASCADE;
DROP TABLE IF EXISTS sklady CASCADE;
DROP TABLE IF EXISTS postoje CASCADE;
DROP TABLE IF EXISTS rozklady CASCADE;
DROP TABLE IF EXISTS pociagi CASCADE;
DROP TABLE IF EXISTS trasy CASCADE;
DROP TABLE IF EXISTS stacje CASCADE;
DROP TABLE IF EXISTS odcinki CASCADE;

CREATE TABLE wagony (
    id_wagonu numeric(6, 0) NOT NULL,
    model_wagonu numeric(6, 0) NOT NULL,
    typ_wagonu varchar(10) CHECK (typ_wagonu = 'sypialny' OR typ_wagonu = 'jadalny' OR typ_wagonu = 'osobowy') NOT NULL,
    liczba_miejsc_I numeric(3, 0) CHECK (liczba_miejsc_I >= 0) NOT NULL,
    liczba_miejsc_II numeric(3, 0) CHECK (liczba_miejsc_II >= 0) NOT NULL,
    czy_przedzialowy char(1) CHECK (czy_przedzialowy = 'T' OR czy_przedzialowy = 'N') NOT NULL,
    czy_rowery char(1) CHECK (czy_przedzialowy = 'T' OR czy_przedzialowy = 'N') NOT NULL, 
    czy_klimatyzacja char(1) CHECK (czy_przedzialowy = 'T' OR czy_przedzialowy = 'N') NOT NULL,
    czy_wifi char(1) CHECK (czy_przedzialowy = 'T' OR czy_przedzialowy = 'N') NOT NULL,
    czy_przesylki char(1) CHECK (czy_przedzialowy = 'T' OR czy_przedzialowy = 'N') NOT NULL,
    dlugosc_wagonu numeric(4, 2) CHECK (dlugosc_wagonu > 0) NOT NULL
);

CREATE TABLE sklady (
    id_skladu numeric(6, 0) NOT NULL,
    id_wagonu numeric(6, 0) NOT NULL,
    liczba_wagonow numeric(2, 0) CHECK (liczba_wagonow > 0) NOT NULL
);

CREATE TABLE postoje (
    id_kursu numeric(6, 0) NOT NULL,
    id_stacji numeric(6, 0) NOT NULL,
    przyjazd time NOT NULL,
    odjazd time NOT NULL,
    nastepny_sklad numeric(6, 0)
);

CREATE TABLE rozklady (
    id_kursu numeric(6, 0) NOT NULL,
    id_pociagu numeric(6, 0) NOT NULL,
    dzien_tygodnia char(2) CHECK (dzien_tygodnia = 'PN' OR dzien_tygodnia = 'WT' OR dzien_tygodnia = 'SR' OR dzien_tygodnia = 'CZ' OR dzien_tygodnia = 'PT' OR dzien_tygodnia = 'SB' OR dzien_tygodnia = 'ND') NOT NULL
);

CREATE TABLE pociagi (
    id_pociagu numeric(6, 0) NOT NULL,
    id_trasy numeric(6, 0) NOT NULL,
    nazwa_pociagu varchar(100),
    typ_pociagu varchar(10) CHECK (typ_pociagu = 'pospieszny' OR typ_pociagu = 'zwykly')
);

CREATE TABLE trasy (
    id_trasy numeric(6, 0) NOT NULL,
    id_odcinka numeric(6, 0) NOT NULL
);

CREATE TABLE stacje (
    id_stacji numeric(6, 0) NOT NULL,
    nazwa_stacji varchar(100) NOT NULL,
    liczba_torow numeric(2, 0) CHECK (liczba_torow > 0) NOT NULL,
    liczba_peronow numeric(2, 0) CHECK (liczba_peronow > 0) NOT NULL,
    dlugosc_peronu numeric(5, 2) CHECK (dlugosc_peronu > 0) NOT NULL
);

CREATE TABLE odcinki (
    id_odcinka numeric(6, 0) NOT NULL,
    stacja_poczatkowa numeric(6, 0) NOT NULL,
    stacja_koncowa numeric(6, 0) NOT NULL
);

/*Set primary keys*/

ALTER TABLE ONLY wagony
    ADD CONSTRAINT pk_wagony PRIMARY KEY (id_wagonu);

ALTER TABLE ONLY sklady
    ADD CONSTRAINT pk_sklady PRIMARY KEY (id_skladu, id_wagonu);

ALTER TABLE ONLY postoje
    ADD CONSTRAINT pk_postoje PRIMARY KEY (id_kursu, id_stacji);

ALTER TABLE ONLY rozklady
    ADD CONSTRAINT pk_rozklady PRIMARY KEY (id_kursu);

ALTER TABLE ONLY pociagi
    ADD CONSTRAINT pk_pociagi PRIMARY KEY (id_pociagu);

ALTER TABLE ONLY trasy
    ADD CONSTRAINT pk_trasy PRIMARY KEY (id_trasy);

ALTER TABLE ONLY odcinki
    ADD CONSTRAINT pk_odcinki PRIMARY KEY (id_odcinka);

ALTER TABLE ONLY stacje
    ADD CONSTRAINT pk_stacje PRIMARY KEY (id_stacji);

/*Set foreign keys*/

ALTER TABLE ONLY sklady
    ADD CONSTRAINT fk_sklady FOREIGN KEY (id_wagonu) REFERENCES wagony(id_wagonu);

ALTER TABLE ONLY postoje
    ADD CONSTRAINT fk_postoje_kursy FOREIGN KEY (id_kursu) REFERENCES rozklady(id_kursu);
ALTER TABLE ONLY postoje
    ADD CONSTRAINT fk_postoje_stacje FOREIGN KEY (id_stacji) REFERENCES stacje(id_stacji);

ALTER TABLE ONLY rozklady
    ADD CONSTRAINT fk_rozklady_pociagi FOREIGN KEY (id_pociagu) REFERENCES pociagi(id_pociagu);

ALTER TABLE ONLY pociagi
    ADD CONSTRAINT fk_pociagi_trasy FOREIGN KEY (id_trasy) REFERENCES trasy(id_trasy);

ALTER TABLE ONLY trasy
    ADD CONSTRAINT fk_trasy_odcinki FOREIGN KEY (id_odcinka) REFERENCES odcinki(id_odcinka);

ALTER TABLE ONLY odcinki
    ADD CONSTRAINT fk_odcinki_poczatek FOREIGN KEY (stacja_poczatkowa) REFERENCES stacje(id_stacji);
ALTER TABLE ONLY odcinki
    ADD CONSTRAINT fk_odcinki_koniec FOREIGN KEY (stacja_koncowa) REFERENCES stacje(id_stacji);