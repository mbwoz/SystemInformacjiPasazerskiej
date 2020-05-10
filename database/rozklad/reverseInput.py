def main():
    fin = open("input.in", "r")
    fout = open("input.out", "w")

    for line in fin.read().splitlines():
        print(line, file = fout)

        input = line.split('|')
        
        liczba_pociagow = int(input[0])
        n = int(input[1])
        stacje = input[(n+1):1:-1]
        postoje = input[(2*n+1):(n+1):-1]
        przejazdy = input[(3*n):(2*n+1):-1]

        print(liczba_pociagow, n, '|'.join(stacje), '|'.join(postoje), '|'.join(przejazdy), sep = '|', file = fout)

    fin.close()
    fout.close()


if __name__ == "__main__":
    main()