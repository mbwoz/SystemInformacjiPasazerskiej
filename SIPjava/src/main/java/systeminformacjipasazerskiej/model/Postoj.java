package systeminformacjipasazerskiej.model;

public class Postoj {
    private int idKursu;
    private Stacja stacja;
    private String przyjazd;
    private String odjazd;
    private int nastepnySklad;

    public int getIdKursu() {
        return idKursu;
    }

    public void setIdKursu(int idKursu) {
        this.idKursu = idKursu;
    }

    public Stacja getStacja() {
        return stacja;
    }

    public void setStacja(Stacja stacja) {
        this.stacja = stacja;
    }

    public String getNazwaStacji() {
        return stacja.getNazwaStacji();
    }

    public String getPrzyjazd() {
        return przyjazd;
    }

    public void setPrzyjazd(String przyjazd) {
        this.przyjazd = przyjazd;
    }

    public String getOdjazd() {
        return odjazd;
    }

    public void setOdjazd(String odjazd) {
        this.odjazd = odjazd;
    }

    public int getNastepnySklad() {
        return nastepnySklad;
    }

    public void setNastepnySklad(int nastepnySklad) {
        this.nastepnySklad = nastepnySklad;
    }

    @Override
    public String toString() {
        return "Stacja: " + stacja.getNazwaStacji() +
            "\t\tPrzyj. " + przyjazd +
            "\tOdj. " + odjazd;
    }

}
