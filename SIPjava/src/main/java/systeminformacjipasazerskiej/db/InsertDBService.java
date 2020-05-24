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


    public static class UpdateStationOverflowException extends Exception {}
    public static class UpdateStationLengthException extends Exception {}
    public static class UpdateStationException extends Exception {}
    public static class InsertStationException extends Exception {}
}
