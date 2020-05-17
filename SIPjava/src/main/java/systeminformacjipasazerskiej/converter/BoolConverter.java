package systeminformacjipasazerskiej.converter;

public class BoolConverter {
    public static boolean convertBool(String s) {
        return s.equals("T");
    }

    public static String convertBool(boolean b) {
        return (b ? "T" : "N");
    }
}
