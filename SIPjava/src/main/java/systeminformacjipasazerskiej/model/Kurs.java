package systeminformacjipasazerskiej.model;

import systeminformacjipasazerskiej.converter.TimeConverter;

import java.util.ArrayList;

public class Kurs {
    private int idKursu;
    private int idPociagu;
    private ArrayList<Postoj> listaPostojow = new ArrayList<>();
    private String czasPrzejazdu;
    private Sklad skladKursu;

    public int getIdKursu() {
        return idKursu;
    }

    public void setIdKursu(int idKursu) {
        this.idKursu = idKursu;
    }

    public int getIdPociagu() {
        return idPociagu;
    }

    public void setIdPociagu(int idPociagu) {
        this.idPociagu = idPociagu;
    }

    public ArrayList<Postoj> getListaPostojow() {
        return listaPostojow;
    }

    public void setListaPostojow(ArrayList<Postoj> listaPostojow) {
        this.listaPostojow = listaPostojow;
    }

    public String getCzasPrzejazdu() {
        return czasPrzejazdu;
    }

    public void calculateCzasPrzejazdu() {
        int time = 0;

        int lastStop = 0;
        if(listaPostojow.size() > 0)
            lastStop = TimeConverter.convertTime(listaPostojow.get(0).getOdjazd());

        for(int i = 1; i < listaPostojow.size(); i++) {
            int nextStop = TimeConverter.convertTime(listaPostojow.get(i).getPrzyjazd());

            if(nextStop > lastStop)
                time += nextStop - lastStop;
            else
                time += TimeConverter.fullTime - lastStop + nextStop;

            lastStop = nextStop;

            if(i == listaPostojow.size() - 1)
                break;

            nextStop = TimeConverter.convertTime(listaPostojow.get(i).getOdjazd());

            if(nextStop >= lastStop)
                time += nextStop - lastStop;
            else
                time += TimeConverter.fullTime - lastStop + nextStop;

            lastStop = nextStop;
        }

        int h = time / 3600;
        int m = (time / 60) % 60;
        czasPrzejazdu = "" + h + "h" + ((m < 10) ? "0" : "") + m + "m";
    }

    public Sklad getSkladKursu() {
        return skladKursu;
    }

    public void setSkladKursu(Sklad skladKursu) {
        this.skladKursu = skladKursu;
    }

    @Override
    public String toString() {
        return "Z: " + listaPostojow.get(0).getStacja().getNazwaStacji() +
            "   Do: " + listaPostojow.get(listaPostojow.size()-1).getStacja().getNazwaStacji() +
            "      Odj. " + listaPostojow.get(0).getOdjazd() +
            "   Przyj. " + listaPostojow.get(listaPostojow.size()-1).getPrzyjazd() +
            "      Czas: " + czasPrzejazdu;
    }

}
