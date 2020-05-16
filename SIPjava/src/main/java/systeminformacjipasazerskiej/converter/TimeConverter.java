package systeminformacjipasazerskiej.converter;

public class TimeConverter {
    public static final int fullTime = 86400;

    public static int convertTime(String time) {
        String[] splited = time.split(":");
        int h = Integer.parseInt(splited[0]);
        int m = Integer.parseInt(splited[1]);
        int s = Integer.parseInt(splited[2]);

        return s + 60 * m + 3600 * h;
    }

    public static String convertTime(int time) {
        int h = (time / 3600) % 24;
        int m = (time / 60) % 60;
        int s = time % 60;

        return "" +
            ((h < 10) ? "0" : "") + h + ":" +
            ((m < 10) ? "0" : "") + m + ":" +
            ((s < 10) ? "0" : "") + s;
    }
}
