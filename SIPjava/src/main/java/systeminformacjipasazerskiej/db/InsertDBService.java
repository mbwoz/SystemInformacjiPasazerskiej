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
            System.out.println("Error with station insert.");
            throw new InsertWagonException();
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




    public static class UpdateStationOverflowException extends Exception {}
    public static class UpdateStationLengthException extends Exception {}
    public static class UpdateStationException extends Exception {}
    public static class InsertStationException extends Exception {}
    public static class NoStationException extends Exception {}
    public static class InsertOdcinekException extends Exception {}
    public static class InsertWagonException extends Exception {}
}
