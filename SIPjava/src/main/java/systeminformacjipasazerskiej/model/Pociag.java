package systeminformacjipasazerskiej.model;

public class Pociag {
    private int idPociagu;
    private int idTrasy;
    private String nazwaPociagu;
    private String typPociagu;

    public int getIdPociagu() {
        return idPociagu;
    }

    public void setIdPociagu(int idPociagu) {
        this.idPociagu = idPociagu;
    }

    public int getIdTrasy() {
        return idTrasy;
    }

    public void setIdTrasy(int idTrasy) {
        this.idTrasy = idTrasy;
    }

    public String getNazwaPociagu() {
        return nazwaPociagu;
    }

    public void setNazwaPociagu(String nazwaPociagu) {
        this.nazwaPociagu = nazwaPociagu;
    }

    public String getTypPociagu() {
        return typPociagu;
    }

    public void setTypPociagu(String typPociagu) {
        this.typPociagu = typPociagu;
    }
}
