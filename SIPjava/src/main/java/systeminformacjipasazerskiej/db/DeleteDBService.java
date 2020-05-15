package systeminformacjipasazerskiej.db;

import systeminformacjipasazerskiej.model.Kurs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

public class DeleteDBService {
    private Connection connection;

    public DeleteDBService(Connection connection) {
        this.connection = connection;
    }

    public void deleteStation(String s) {
        try {
            Statement statement = connection.createStatement();

            //get id_stacji
            ResultSet resultSet = statement.executeQuery("SELECT id_stacji FROM stacje WHERE nazwa_stacji = '" + s + "';");
            if(resultSet.next());
            int id_stacji = resultSet.getInt("id_stacji");

            //get id_odcinka
            resultSet = statement.executeQuery("SELECT id_odcinka FROM odcinki WHERE stacja_poczatkowa = " + id_stacji + " OR stacja_koncowa = " + id_stacji + "; ");
            ArrayList<Integer> id_odcinka = new ArrayList<>();
            while (resultSet.next()) {id_odcinka.add(resultSet.getInt("id_odcinka"));}

            //get id_trasy
            ArrayList<Integer> id_trasy = new ArrayList<>();
            for (Integer i: id_odcinka) {
                resultSet = statement.executeQuery( "SELECT  id_trasy FROM trasy_odcinki WHERE id_odcinka = " + i + ";");
                while (resultSet.next()) id_trasy.add(resultSet.getInt("id_trasy"));
            }

            //get id_pociagu
            ArrayList<Integer> id_pociagu = new ArrayList<>();
            for(Integer i: id_trasy) {
                resultSet = statement.executeQuery("SELECT id_pociagu FROM pociagi WHERE id_trasy = " + i + ";");
                while (resultSet.next()) id_pociagu.add(resultSet.getInt("id_pociagu"));
            }

            //get id_kursu
            ArrayList<Integer> id_kursu = new ArrayList<>();
            for(Integer i: id_pociagu) {
                resultSet = statement.executeQuery("SELECT id_kursu FROM rozklady WHERE id_pociagu = " + i + ";");
                while (resultSet.next()) id_kursu.add(resultSet.getInt("id_kursu"));
            }

            id_kursu = new ArrayList<>(new HashSet<>(id_kursu)); //remove duplicates

            System.out.println("Stacja: " + id_stacji);
            System.out.println("Odcinki " + id_odcinka.toString());
            System.out.println("Trasy: " + id_trasy.toString());
            System.out.println("Pociagi: " + id_pociagu.toString());
            System.out.println("Kursy: " + id_kursu.toString());

            //DELETE time

            //DELETE postoje
            for(Integer i: id_kursu)
                statement.execute("DELETE FROM postoje WHERE id_kursu = " + i + ";");

            //DELETE rozklady
            for(Integer i: id_kursu)
                statement.execute("DELETE FROM rozklady WHERE id_kursu = " + i + ";");

            //DELETE pociagi
            for(Integer i: id_pociagu)
                statement.execute("DELETE FROM pociagi WHERE id_pociagu = " + i + ";");

            //DELETE trasy_odcinki
            for(Integer i: id_trasy)
                statement.execute("DELETE FROM trasy_odcinki WHERE id_trasy = " + i +";");

            //DELETE trasy
            for(Integer i: id_trasy)
                statement.execute("DELETE FROM trasy WHERE id_trasy = " + i +";");

            //DELETE odcinki
            statement.execute("DELETE FROM odcinki WHERE stacja_poczatkowa = " + id_stacji + " OR stacja_koncowa = " + id_stacji + "; ");

            //DELETE stacje
            statement.execute("DELETE FROM stacje WHERE id_stacji = " + id_stacji + ";");

            resultSet.close();
            statement.close();

            System.out.println("Deleted successfully: " + s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRide(Kurs kurs) {
        int id_pociagu = kurs.getIdPociagu();
        int id_kursu = kurs.getIdKursu();

        try {
            Statement statement = connection.createStatement();

            statement.execute("DELETE FROM postoje WHERE id_kursu = " + id_kursu + ";");
            statement.execute("DELETE FROM rozklady WHERE id_kursu = " + id_kursu + ";");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }



}
