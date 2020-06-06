package systeminformacjipasazerskiej.db;

import systeminformacjipasazerskiej.converter.BoolConverter;
import systeminformacjipasazerskiej.converter.DayConverter;
import systeminformacjipasazerskiej.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Pattern;

public class InsertDBService {
    private Connection connection;

    public InsertDBService(Connection connection) {
        this.connection = connection;
    }

    public boolean checkStationExistence(String name) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM stacje WHERE nazwa_stacji = '" + name + "';"
            );

            if(resultSet.next())
                return true;

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkOdcinekExistence(int idStart, int idEnd) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM odcinki WHERE stacja_poczatkowa = " + idStart + " AND stacja_koncowa = " + idEnd + ";"
            );

            if(resultSet.next())
                return true;

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkWagonExistence(Wagon wagon) {
        char przedziały = (wagon.isCzyPrzedzialowy()) ? 'T' : 'N';
        char klimatyzacja = (wagon.isCzyKlimatyzacja()) ? 'T' : 'N';
        char wifi = (wagon.isCzyWifi()) ? 'T' : 'N';
        char niepelnosprawni = (wagon.isCzyNiepelnosprawni()) ? 'T' : 'N';

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM wagony WHERE " +
                            "model_wagonu = '" + wagon.getModel() + "' AND " +
                            "typ_wagonu = '" + wagon.getTyp() + "' AND " +
                            "liczba_miejsc_i = " + wagon.getMiejscaI() + " AND " +
                            "liczba_miejsc_ii = " + wagon.getMiejscaII() + " AND " +
                            "liczba_rowerow = " + wagon.getRowery() + " AND " +
                            "czy_przedzialowy = '" + przedziały + "' AND " +
                            "czy_klimatyzacja = '" + klimatyzacja + "' AND " +
                            "czy_wifi = '" + wifi + "' AND " +
                            "czy_niepelnosprawni = '" + niepelnosprawni + "' AND " +
                            "dlugosc_wagonu = " + wagon.getDlugosc() + ";"
            );

            if(resultSet.next())
                return true;

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkPociagExistence(String name) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM pociagi WHERE nazwa_pociagu = '" + name + "';"
            );

            if(resultSet.next())
                return true;

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkKursExistence(Integer idPociagu, Integer day) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM rozklady WHERE id_pociagu = " + idPociagu +
                            "AND dzien_tygodnia = " + day + ";"
            );

            if(resultSet.next())
                return true;

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void insertStation(Stacja stacja) throws InsertStationException {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO stacje(nazwa_stacji, liczba_torow, liczba_peronow, dlugosc_peronu)"
                    + "VALUES('" + stacja.getNazwaStacji() + "', " + stacja.getLiczbaTorow() + ", " + stacja.getLiczbaPeronow() + ", "
                    + stacja.getDlugoscPeronu() + ");");
        } catch(SQLException e) {
            System.out.println("Error with station insert.");
            throw new InsertStationException();
        }
    }

    public void updateStation(Stacja stacja) throws UpdateStationOverflowException, UpdateStationLengthException, UpdateStationException {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE stacje SET (id_stacji, nazwa_stacji, liczba_torow, liczba_peronow, dlugosc_peronu) = "+
                    " (id_stacji, nazwa_stacji, " + stacja.getLiczbaTorow() + ", " + stacja.getLiczbaPeronow() + ", "
                    + stacja.getDlugoscPeronu() + ") WHERE nazwa_stacji = '" + stacja.getNazwaStacji() + "';");
        } catch(SQLException e) {
            System.out.println("Error with station update.");

            String mes = e.getMessage();

            if(mes.contains("Station overflow"))
                throw new UpdateStationOverflowException();
            else if(mes.contains("Station is too short"))
                throw new UpdateStationLengthException();
            else
                throw new UpdateStationException();
        }
    }

    public void insertOdcinek(int idStart, int idEnd) throws InsertOdcinekException {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO odcinki(stacja_poczatkowa, stacja_koncowa)"
                    + "VALUES(" + idStart + ", " + idEnd + ");");
        } catch(SQLException e) {
            System.out.println("Error with odcinek insert.");
            throw new InsertOdcinekException();
        }
    }


    public void insertWagon(Wagon wagon) throws InsertWagonException {
        char przedziały = (wagon.isCzyPrzedzialowy()) ? 'T' : 'N';
        char klimatyzacja = (wagon.isCzyKlimatyzacja()) ? 'T' : 'N';
        char wifi = (wagon.isCzyWifi()) ? 'T' : 'N';
        char niepelnosprawni = (wagon.isCzyNiepelnosprawni()) ? 'T' : 'N';

        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO wagony(model_wagonu, typ_wagonu, liczba_miejsc_i, liczba_miejsc_ii, liczba_rowerow, " +
                    "czy_przedzialowy, czy_klimatyzacja, czy_wifi, czy_niepelnosprawni, dlugosc_wagonu)" +
                    "VALUES('" + wagon.getModel() + "', '" + wagon.getTyp() + "', " + wagon.getMiejscaI() + ", " +
                    wagon.getMiejscaII() + ", " + wagon.getRowery() + ", '" + przedziały + "', '" +
                    klimatyzacja + "', '" + wifi + "', '" + niepelnosprawni + "', " + wagon.getDlugosc() + ");");
        } catch(SQLException e) {
            System.out.println("Error with wagon insert.");
            System.out.println(e.getMessage());
            throw new InsertWagonException();
        }
    }

    public void insertSklad(Sklad sklad) throws InsertSkladException, InsertSkladExistsException {
        String przesylki = (sklad.isCzyPrzesylki()) ? "'T'" : "'N'";
        int n = sklad.getIdWagonow().size();
        String idWagonow = arrayToQuery(sklad.getIdWagonow());
        String ilosc = arrayToQuery(sklad.getLiczbaWagonow());

        try {
            String query = "SELECT insertSkladQuery(" + idWagonow + ", " + ilosc + ", " + n + ", " + przesylki + ");";
            System.out.println(query);
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch(SQLException e) {
            System.out.println("Error with sklad insert.");
            String mes = e.getMessage();
            System.out.println(mes);

            if(mes.contains("Sklad exists"))
                throw new InsertSkladExistsException();
            else
                throw new InsertSkladException();
        }
    }

    public void insertTrasa(ArrayList<Integer> idStacji, boolean p) throws InsertTrasaException, InsertTrasaExistsException {
        String przyspieszona = (p) ? "'T'" : "'N'";
        String stacje = arrayToQuery(idStacji);
        int n = idStacji.size();

        try {
            String query = "SELECT insertTrasaQuery(" + stacje + ", " + n + ", " + przyspieszona + ");";
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println("Error with trasa insert.");
            String mes = e.getMessage();
//            System.out.println(mes);

            if (mes.contains("Trasa exists"))
                throw new InsertTrasaExistsException();
            else
                throw new InsertTrasaException();
        }
    }

    public void insertPociag(Pociag pociag) throws InsertPociagException{
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO pociagi(id_trasy, nazwa_pociagu, typ_pociagu)"
                    + "VALUES(" + pociag.getIdTrasy() + ", '" + pociag.getNazwaPociagu() + "', '" + pociag.getTypPociagu() + "');");
        } catch(SQLException e) {
            System.out.println("Error with pociag insert.");
            throw new InsertPociagException();
        }
    }

    public void insertKurs(Integer pociag, int day, ArrayList<String> przyjazd, ArrayList<String> odjazd, ArrayList<Integer> nextSklad)
        throws InsertKursOverflowException, InsertKursLengthException, InsertKursException{
        try {
            Statement statement = connection.createStatement();
            String prep = "SELECT insertKurs(" + pociag + ", " + day + ", " + arrayToQuery2(przyjazd) +
                    ", " + arrayToQuery2(odjazd) + ", " + arrayToQuery(nextSklad) + ");";
            System.out.println(prep);
            statement.execute(prep);
        } catch (SQLException e) {
            System.out.println("Error with kurs insert.");

            String mes = e.getMessage();
            System.out.println(mes);

            if(mes.contains("Overflow"))
                throw new InsertKursOverflowException();
            else if(mes.contains("Length"))
                throw new InsertKursLengthException();
            else
                throw new InsertKursException();
        }
    }

    public int getStationId(String stacja) throws NoStationException {
        int ans = 0;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM stacje WHERE nazwa_stacji = '" + stacja + "';"
            );

            if(!resultSet.next())
                throw new NoStationException();

            ans = resultSet.getInt("id_stacji");

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ans;
    }

    public ArrayList<Wagon> getAllWagony() {
        ArrayList<Wagon> wagony = new ArrayList<>();


        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM wagony ORDER BY 3, 2, 4, 5, 6;"
            );

            while(resultSet.next()) {
                Wagon wagon = new Wagon();
                boolean przedzialy = (resultSet.getString("czy_przedzialowy").equals("T"));
                boolean klimatyzacja = (resultSet.getString("czy_klimatyzacja").equals("T"));
                boolean wifi = (resultSet.getString("czy_wifi").equals("T"));
                boolean niepelnosprawni = (resultSet.getString("czy_niepelnosprawni").equals("T"));

                wagon.setIdWagonu(resultSet.getInt("id_wagonu"));
                wagon.setModel(resultSet.getString("model_wagonu"));
                wagon.setTyp(resultSet.getString("typ_wagonu"));
                wagon.setMiejscaI(resultSet.getInt("liczba_miejsc_i"));
                wagon.setMiejscaII(resultSet.getInt("liczba_miejsc_ii"));
                wagon.setRowery(resultSet.getInt("liczba_rowerow"));
                wagon.setCzyPrzedzialowy(przedzialy);
                wagon.setCzyKlimatyzacja(klimatyzacja);
                wagon.setCzyWifi(wifi);
                wagon.setCzyNiepelnosprawni(niepelnosprawni);
                wagon.setDlugosc(resultSet.getDouble("dlugosc_wagonu"));

                wagony.add(wagon);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return wagony;
    }

    public int getIdWagonuByDescription(String desc, ArrayList<Wagon> allWagony) {
        for(Wagon wagon : allWagony) {
            String s = wagon.getDescription();
            if(s.equals(desc))
                return wagon.getIdWagonu();
        }
        return 0;
    }

    public ArrayList<Integer> getTrasaIdExactlyFromTo (String fromStation, String toStation)
            throws NoStationException, NoMatchingTrasyException {
        ArrayList<Integer> id_trasy = new ArrayList<>();

        try {
            int fromStationId, toStationId;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT id_stacji FROM stacje WHERE nazwa_stacji = '" + fromStation + "';"
            );
            if(resultSet.next())
                fromStationId = resultSet.getInt("id_stacji");
            else {
                statement.close();
                resultSet.close();
                throw new NoStationException();
            }

            resultSet = statement.executeQuery(
                    "SELECT id_stacji FROM stacje WHERE nazwa_stacji = '" + toStation + "';"
            );
            if(resultSet.next())
                toStationId = resultSet.getInt("id_stacji");
            else {
                statement.close();
                resultSet.close();
                throw new NoStationException();
            }

            resultSet = statement.executeQuery(
                    "SELECT idTrasy " +
                            "FROM getIdTrasyExactlyFromTo(" + fromStationId + ", " + toStationId + ") " +
                            "ORDER BY idTrasy;"
            );
            while (resultSet.next())
                id_trasy.add(resultSet.getInt("idTrasy"));

            resultSet.close();
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        if(id_trasy.isEmpty())
            throw new NoMatchingTrasyException();

        return id_trasy;
    }

    public ArrayList<Integer> getPociagIdExactlyFromTo (String fromStation, String toStation)
            throws NoStationException, NoMatchingTrasyException {
        ArrayList<Integer> id_pociagu = new ArrayList<>();

        try {
            int fromStationId, toStationId;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT id_stacji FROM stacje WHERE nazwa_stacji = '" + fromStation + "';"
            );
            if(resultSet.next())
                fromStationId = resultSet.getInt("id_stacji");
            else {
                statement.close();
                resultSet.close();
                throw new NoStationException();
            }

            resultSet = statement.executeQuery(
                    "SELECT id_stacji FROM stacje WHERE nazwa_stacji = '" + toStation + "';"
            );
            if(resultSet.next())
                toStationId = resultSet.getInt("id_stacji");
            else {
                statement.close();
                resultSet.close();
                throw new NoStationException();
            }

            resultSet = statement.executeQuery(
                    "SELECT idPociagu " +
                            "FROM getIdPociaguExactlyFromTo(" + fromStationId + ", " + toStationId + ") " +
                            "ORDER BY idPociagu;"
            );
            while (resultSet.next())
                id_pociagu.add(resultSet.getInt("idPociagu"));

            resultSet.close();
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        if(id_pociagu.isEmpty())
            throw new NoMatchingTrasyException();

        return id_pociagu;
    }

    public String arrayToQuery(ArrayList<Integer> array) {
        String ans = "'{" + array.get(0);
        boolean first = false;

        for(Integer i : array) {
            if(!first) {
                first = true;
                continue;
            }

            ans += (", " + i);
        }
        ans += "}'";

        return ans;
    }
    public String arrayToQuery2(ArrayList<String> array) {
        String ans = "'{" + array.get(0);
        boolean first = false;

        for(String i : array) {
            if(!first) {
                first = true;
                continue;
            }

            ans += (", " + i);
        }
        ans += "}'";

        return ans;
    }

    public boolean checkKursLength(ArrayList<String> przyjazd, ArrayList<String> odjazd) {
        int ans = 0;
        int a = toMins(przyjazd.get(0));
        int b = toMins(odjazd.get(0));
        if(b < a)
            b += 1440;
        int last = b;
        int size = przyjazd.size();

        for(int i = 1; i < size; i++) {
            a = toMins(przyjazd.get(i));
            b = toMins(odjazd.get(i));
            while(a <= last)
                a += 1440;
            while(b < a)
                b += 1440;
            last = b;
        }

        ans = last - toMins(przyjazd.get(0));
        if(ans > 1440)
            return true;
        else return false;
    }

    private int toMins(String s) {
        String[] hourMin = s.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        return hoursInMins + mins;
    }


    public static class UpdateStationOverflowException extends Exception {}
    public static class UpdateStationLengthException extends Exception {}
    public static class UpdateStationException extends Exception {}
    public static class InsertStationException extends Exception {}
    public static class NoStationException extends Exception {}
    public static class NoMatchingTrasyException extends Exception {}
    public static class InsertOdcinekException extends Exception {}
    public static class InsertWagonException extends Exception {}
    public static class InsertSkladException extends Exception {}
    public static class InsertSkladExistsException extends Exception {}
    public static class InsertTrasaException extends Exception {}
    public static class InsertTrasaExistsException extends Exception {}
    public static class InsertPociagException extends Exception {}
    public static class InsertKursOverflowException extends Exception {}
    public static class InsertKursLengthException extends Exception {}
    public static class InsertKursException extends Exception {}
}
