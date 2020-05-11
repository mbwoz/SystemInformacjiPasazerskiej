package systeminformacjipasazerskiej.converter;

public class DayConverter {
    public static String convertDay(int day) {
        switch (day) {
            case 0:
                return "Poniedziałek";
            case 1:
                return "Wtorek";
            case 2:
                return "Środa";
            case 3:
                return "Czwartek";
            case 4:
                return "Piątek";
            case 5:
                return "Sobota";
            case 6:
                return "Niedziela";
        }
        return null;
    }

    public static int convertDay(String day) {
        switch (day) {
            case "Poniedziałek":
                return 0;
            case "Wtorek":
                return 1;
            case "Środa":
                return 2;
            case "Czwartek":
                return 3;
            case "Piątek":
                return 4;
            case "Sobota":
                return 5;
            case "Niedziela":
                return 6;
        }
        return -1;
    }
}
