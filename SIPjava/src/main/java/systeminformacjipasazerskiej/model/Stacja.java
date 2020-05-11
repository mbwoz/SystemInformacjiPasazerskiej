package systeminformacjipasazerskiej.model;

public class Stacja {
    private int idStacji;
    private String nazwaStacji;
    private int liczbaTorow;
    private int liczbaPeronow;
    private double dlugoscPeronu;

    public int getIdStacji() {
        return idStacji;
    }

    public void setIdStacji(int idStacji) {
        this.idStacji = idStacji;
    }

    public String getNazwaStacji() {
        return nazwaStacji;
    }

    public void setNazwaStacji(String nazwaStacji) {
        this.nazwaStacji = nazwaStacji;
    }

    public int getLiczbaTorow() {
        return liczbaTorow;
    }

    public void setLiczbaTorow(int liczbaTorow) {
        this.liczbaTorow = liczbaTorow;
    }

    public int getLiczbaPeronow() {
        return liczbaPeronow;
    }

    public void setLiczbaPeronow(int liczbaPeronow) {
        this.liczbaPeronow = liczbaPeronow;
    }

    public double getDlugoscPeronu() {
        return dlugoscPeronu;
    }

    public void setDlugoscPeronu(double dlugoscPeronu) {
        this.dlugoscPeronu = dlugoscPeronu;
    }
}
