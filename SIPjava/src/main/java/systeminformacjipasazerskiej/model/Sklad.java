package systeminformacjipasazerskiej.model;

import java.util.ArrayList;

public class Sklad {
    private int idSkladu;
    private boolean czyPrzesylki;
    private ArrayList<Wagon> listaWagonow;
    private ArrayList<Integer> idWagonow;
    private ArrayList<Integer> liczbaWagonow;

    public int getIdSkladu() {
        return idSkladu;
    }

    public void setIdSkladu(int idSkladu) {
        this.idSkladu = idSkladu;
    }

    public boolean isCzyPrzesylki() {
        return czyPrzesylki;
    }

    public void setCzyPrzesylki(boolean czyPrzesylki) {
        this.czyPrzesylki = czyPrzesylki;
    }

    public ArrayList<Wagon> getListaWagonow() {
        return listaWagonow;
    }

    public void setListaWagonow(ArrayList<Wagon> listaWagonow) {
        this.listaWagonow = listaWagonow;
    }

    public ArrayList<Integer> getIdWagonow() {
        return idWagonow;
    }

    public void setIdWagonow(ArrayList<Integer> idWagonow) {
        this.idWagonow = idWagonow;
    }

    public ArrayList<Integer> getLiczbaWagonow() {
        return liczbaWagonow;
    }

    public void setLiczbaWagonow(ArrayList<Integer> liczbaWagonow) {
        this.liczbaWagonow = liczbaWagonow;
    }

    public int getLiczbaMiejscI() {
        int liczbaMiejscI = 0;
        for(int i = 0; i < listaWagonow.size(); i++)
            liczbaMiejscI += listaWagonow.get(i).getMiejscaI() * liczbaWagonow.get(i);

        return liczbaMiejscI;
    }

    public int getLiczbaMiejscII() {
        int liczbaMiejscII = 0;
        for(int i = 0; i < listaWagonow.size(); i++)
            liczbaMiejscII += listaWagonow.get(i).getMiejscaII() * liczbaWagonow.get(i);

        return liczbaMiejscII;
    }

    public int getLiczbaMiejscDlaRowerow() {
        int liczbaRowerow = 0;
        for(int i = 0; i < listaWagonow.size(); i++)
            liczbaRowerow += listaWagonow.get(i).getRowery() * liczbaWagonow.get(i);

        return liczbaRowerow;
    }

    public boolean checkIfKlimatyzacja() {
        for (Wagon wagon : listaWagonow)
            if (wagon.isCzyKlimatyzacja())
                return true;

        return false;
    }

    public boolean checkIfWifi() {
        for (Wagon wagon : listaWagonow)
            if (wagon.isCzyWifi())
                return true;

        return false;
    }

    public boolean checkIfNiepelnosprawni() {
        for (Wagon wagon : listaWagonow)
            if (wagon.isCzyNiepelnosprawni())
                return true;

        return false;
    }

    public boolean checkIfBarowy() {
        for (Wagon wagon : listaWagonow)
            if (wagon.getTyp().equals("barowy"))
                return true;

        return false;
    }

}
