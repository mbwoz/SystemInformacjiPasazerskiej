import random
import math

fin = open("stacje.txt", "r")
fout = open("stacje.in", "w")

id_stacji = 10
print("\nCOPY stacje FROM STDIN DELIMITER '|';", file = fout)

for nazwa_stacji in fin.read().splitlines():
    liczba_torow = random.randint(2, 14)
    liczba_peronow = random.randint(math.ceil(liczba_torow/5), liczba_torow)
    dlugosc_peronu = round((random.random() * 650) + 300, 2)
    
    print(id_stacji, nazwa_stacji, liczba_torow, liczba_peronow, dlugosc_peronu, sep='|', file = fout)
    
    id_stacji += 1

print("\.\n", file = fout)
    
fin.close()
fout.close()
