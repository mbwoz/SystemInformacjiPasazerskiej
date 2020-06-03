package systeminformacjipasazerskiej.model;

public class Wagon {
    private int idWagonu;
    private String model;
    private String typ;
    private int miejscaI;
    private int miejscaII;
    private int rowery;
    private boolean czyPrzedzialowy;
    private boolean czyKlimatyzacja;
    private boolean czyWifi;
    private boolean czyNiepelnosprawni;
    private double dlugosc;

    public int getIdWagonu() {
        return idWagonu;
    }

    public void setIdWagonu(int idWagonu) {
        this.idWagonu = idWagonu;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public int getMiejscaI() {
        return miejscaI;
    }

    public void setMiejscaI(int miejscaI) {
        this.miejscaI = miejscaI;
    }

    public int getMiejscaII() {
        return miejscaII;
    }

    public void setMiejscaII(int miejscaII) {
        this.miejscaII = miejscaII;
    }

    public int getRowery() {
        return rowery;
    }

    public void setRowery(int rowery) {
        this.rowery = rowery;
    }

    public boolean isCzyPrzedzialowy() {
        return czyPrzedzialowy;
    }

    public void setCzyPrzedzialowy(boolean czyPrzedzialowy) {
        this.czyPrzedzialowy = czyPrzedzialowy;
    }

    public boolean isCzyKlimatyzacja() {
        return czyKlimatyzacja;
    }

    public void setCzyKlimatyzacja(boolean czyKlimatyzacja) {
        this.czyKlimatyzacja = czyKlimatyzacja;
    }

    public boolean isCzyWifi() {
        return czyWifi;
    }

    public void setCzyWifi(boolean czyWifi) {
        this.czyWifi = czyWifi;
    }

    public boolean isCzyNiepelnosprawni() {
        return czyNiepelnosprawni;
    }

    public void setCzyNiepelnosprawni(boolean czyNiepelnosprawni) {
        this.czyNiepelnosprawni = czyNiepelnosprawni;
    }

    public double getDlugosc() {
        return dlugosc;
    }

    public void setDlugosc(double dlugosc) {
        this.dlugosc = dlugosc;
    }

    public String getDescription() {
        char przedziały = (this.isCzyPrzedzialowy()) ? 'T' : 'N';
        char klimatyzacja = (this.isCzyKlimatyzacja()) ? 'T' : 'N';
        char wifi = (this.isCzyWifi()) ? 'T' : 'N';
        char niepelnosprawni = (this.isCzyNiepelnosprawni()) ? 'T' : 'N';

        String ans = "\"" + this.getModel() + "\", " + this.getTyp() + ", " + this.getMiejscaI() + ", " +
                this.getMiejscaII() + ", " + this.getRowery() + ", " + przedziały + ", " +
                klimatyzacja + ", " + wifi + ", " + niepelnosprawni + ", " + this.getDlugosc();
        return ans;
    }
}
