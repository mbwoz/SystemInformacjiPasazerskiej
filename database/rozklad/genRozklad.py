import random
import string
import math

def createHeader(fpostoje, fstacje, fodcinki, ftrasyodcinki, ftrasy, fpociagi, frozklady):
    print("\nCOPY postoje FROM STDIN DELIMITER '|';", file = fpostoje)
    print("\nCOPY stacje FROM STDIN DELIMITER '|';", file = fstacje)
    print("\nCOPY odcinki FROM STDIN DELIMITER '|';", file = fodcinki)
    print("\nCOPY trasy_odcinki FROM STDIN DELIMITER '|';", file = ftrasyodcinki)
    print("\nCOPY trasy FROM STDIN DELIMITER '|';", file = ftrasy)
    print("\nCOPY pociagi FROM STDIN DELIMITER '|';", file = fpociagi)
    print("\nCOPY rozklady FROM STDIN DELIMITER '|';", file = frozklady)


def addStacje(stacje, stacjeCollection):
    for s in stacje:
        if not s in stacjeCollection:
            stacjeCollection[s] = len(stacjeCollection) + 1


def addOdcinki(stacje, odcinkiCollection, stacjeCollection):
    for i in range(0, len(stacje)-1):
        odcinek = (stacjeCollection[stacje[i]], stacjeCollection[stacje[i+1]])
        if not odcinek in odcinkiCollection:
            odcinkiCollection[odcinek] = len(odcinkiCollection) + 1


def createTrasy(ftrasy, id_trasy):
    przypieszona = ["T", "N"]
    czy_przyspieszona = przypieszona[random.randint(0, len(przypieszona)-1)]

    print(id_trasy, czy_przyspieszona, sep = '|', file = ftrasy)


def createTrasyOdcinki(ftrasyodcinki, id_trasy, stacje, odcinkiCollection, stacjeCollection):
    for i in range(0, len(stacje)-1):
        odcinek = (stacjeCollection[stacje[i]], stacjeCollection[stacje[i+1]])
        
        print(id_trasy, odcinkiCollection[odcinek], sep = '|', file = ftrasyodcinki)


def randomString(stringLength):
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(stringLength))


def createPociagi(fpociagi, id_pociagu, id_trasy):
    pociagi = ["pospieszny", "ekspres", "pendolino"]
    typ_pociagu = pociagi[random.randint(0, len(pociagi)-1)]

    nazwa_pociagu = randomString(8)

    print(id_pociagu, id_trasy, nazwa_pociagu, typ_pociagu, sep = '|', file = fpociagi)

    return typ_pociagu


def createRozklady(frozklady, id_kursu, id_pociagu, dzien_tygodnia):
    print(id_kursu, id_pociagu, dzien_tygodnia, sep = '|', file = frozklady)


def odchylenieCzasu(czas):
    return int(czas) * (random.random() * 0.4 + 0.8)


def konwersjaCzasu(czas):
    godzina = str(int(czas)//60)
    if int(godzina) < 10:
        godzina = "0" + godzina

    minuta = str(int(czas)%60)
    if int(minuta) < 10:
        minuta = "0" + minuta

    return godzina + ":" + minuta


def chooseSklad(odjazd, typ_pociagu):
    if typ_pociagu == "pospieszny":
        nastepny_sklad = random.randint(1, 300)

        while True:
            nastepny_sklad = random.randint(1, 300)
            if 16 * 60 >= odjazd and nastepny_sklad % 3 == 1:
                break
            if 18 * 60 >= odjazd and odjazd > 16 * 60 and nastepny_sklad % 3 == 2:
                break
            if odjazd > 18 * 60 and nastepny_sklad % 3 == 0:
                break
        
        return nastepny_sklad

    elif typ_pociagu == "ekspres":
        return random.randint(301, 350)

    else:
        return random.randint(351, 351)


def createPostoje(fpostoje, id_kursu, stacje, stacjeCollection, postoje, przejazdy, typ_pociagu):
    mod = 24*60-1
    godzina = random.randint(0, mod-1)+1

    odjazd = godzina
    nastepny_sklad = chooseSklad(odjazd, typ_pociagu)

    for i in range(0, len(stacje)):
        id_stacji = stacjeCollection[stacje[i]]
        przyjazd = godzina

        if random.random() < 0.1:
            nastepny_sklad = chooseSklad(odjazd, typ_pociagu)
            godzina += random.randint(15, 25)

        godzina = (godzina + odchylenieCzasu(postoje[i]))%mod + 1
        odjazd = godzina

        print(id_kursu, id_stacji, konwersjaCzasu(przyjazd), konwersjaCzasu(odjazd), nastepny_sklad, sep = '|', file = fpostoje)

        if i != len(stacje) - 1:
            godzina = (godzina + odchylenieCzasu(przejazdy[i]))%mod + 1


def createStacje(fstacje, stacjeCollection):
    for nazwa_stacji, id_stacji in stacjeCollection.items():
        liczba_torow = random.randint(2, 14)
        liczba_peronow = random.randint(math.ceil(liczba_torow/5), liczba_torow)
        dlugosc_peronu = round((random.random() * 640) + 350, 2)

        print(id_stacji, nazwa_stacji, liczba_torow, liczba_peronow, dlugosc_peronu, sep='|', file = fstacje)


def createOdcinki(fodcinki, odcinkiCollection):
    for (stacja_poczatkowa, stacja_koncowa), id_odcinka in odcinkiCollection.items():
        print(id_odcinka, stacja_poczatkowa, stacja_koncowa, sep = '|', file = fodcinki)


def createFooter(files):
    for f in files:
        print("\.\n", file = f)


def main():
    fin = open("input.out", "r")
    fpostoje = open("postoje.in", "w")
    fstacje = open("stacje.in", "w")
    fodcinki = open("odcinki.in", "w")
    ftrasyodcinki = open("trasy_odcinki.in", "w")
    ftrasy = open("trasy.in", "w")
    fpociagi = open("pociagi.in", "w")
    frozklady = open("rozklady.in", "w")

    createHeader(fpostoje, fstacje, fodcinki, ftrasyodcinki, ftrasy, fpociagi, frozklady)

    stacjeCollection = dict()
    odcinkiCollection = dict()

    id_trasy = 0
    id_pociagu = 0
    id_kursu = 0

    for line in fin.read().splitlines():
        id_trasy += 1

        input = line.split('|')
        
        liczba_pociagow = int(input[0])
        n = int(input[1])
        stacje = input[2:(n+2)]
        postoje = input[(n+2):(2*n+2)]
        przejazdy = input[(2*n+2):(3*n+1)]

        addStacje(stacje, stacjeCollection)
        addOdcinki(stacje, odcinkiCollection, stacjeCollection)
        
        createTrasy(ftrasy, id_trasy)
        createTrasyOdcinki(ftrasyodcinki, id_trasy, stacje, odcinkiCollection, stacjeCollection)

        for p in range(0, liczba_pociagow):
            id_pociagu += 1

            typ_pociagu = createPociagi(fpociagi, id_pociagu, id_trasy)

            ilosc_kursow = random.randint(1, 7)
            dni = [0, 1, 2, 3, 4, 5, 6]
            random.shuffle(dni)

            for kurs in range(0, ilosc_kursow):
                id_kursu += 1

                createRozklady(frozklady, id_kursu, id_pociagu, dni[kurs])
                createPostoje(fpostoje, id_kursu, stacje, stacjeCollection, postoje, przejazdy, typ_pociagu)


    createStacje(fstacje, stacjeCollection)
    createOdcinki(fodcinki, odcinkiCollection)

    files = [fpostoje, fstacje, fodcinki, ftrasyodcinki, ftrasy, fpociagi, frozklady]
    createFooter(files)

    fin.close()
    fpostoje.close()
    fstacje.close()
    fodcinki.close()
    ftrasyodcinki.close()
    ftrasy.close()
    fpociagi.close()
    frozklady.close()


if __name__ == "__main__":
    main() 
