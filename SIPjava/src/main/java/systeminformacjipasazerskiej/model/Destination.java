package systeminformacjipasazerskiej.model;

import java.util.ArrayList;

public class Destination {
    private String source;
    private String mainDestination;
    private ArrayList<String> stacjePosrednie = new ArrayList<>();

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMainDestination() {
        return mainDestination;
    }

    public void setMainDestination(String mainDestination) {
        this.mainDestination = mainDestination;
    }

    public ArrayList<String> getStacjePosrednie() {
        return stacjePosrednie;
    }

    public void setStacjePosrednie(ArrayList<String> stacjePosrednie) {
        this.stacjePosrednie = stacjePosrednie;
    }

    @Override
    public String toString() {
        return "Stacja docelowa: " + mainDestination;
    }
}
