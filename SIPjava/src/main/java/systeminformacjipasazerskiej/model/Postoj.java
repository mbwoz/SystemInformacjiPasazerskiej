package systeminformacjipasazerskiej.model;

public class Postoj {
    private int idKursu;
    private int idStacji;
    private String przyjazd;
    private String odjazd;
    private int nastepnySklad;

    public int getIdKursu() {
        return idKursu;
    }

    public void setIdKursu(int idKursu) {
        this.idKursu = idKursu;
    }

    public int getIdStacji() {
        return idStacji;
    }

    public void setIdStacji(int idStacji) {
        this.idStacji = idStacji;
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

    // TODO: print actual results
    @Override
    public String toString() {
        return "Postoj{" +
                "idKursu=" + idKursu +
                ", idStacji=" + idStacji +
                ", przyjazd='" + przyjazd + '\'' +
                ", odjazd='" + odjazd + '\'' +
                ", nastepnySklad=" + nastepnySklad +
                "}\n";
    }
}
