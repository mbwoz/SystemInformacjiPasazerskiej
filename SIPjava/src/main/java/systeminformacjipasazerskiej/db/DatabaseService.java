package systeminformacjipasazerskiej.db;

import systeminformacjipasazerskiej.model.Stacja;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseService {
    Connection connection;

    public DatabaseService() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/michal",
                            "michal", "123qwe");

            System.out.println("Opened database successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

    public ArrayList<Stacja> getAllStations() {
        ArrayList<Stacja> stacje = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM stacje ORDER BY nazwa_stacji;"
            );

            while(resultSet.next()) {
                Stacja stacja = new Stacja();
                stacja.setIdStacji(resultSet.getInt("id_stacji"));
                stacja.setNazwaStacji(resultSet.getString("nazwa_stacji"));
                stacja.setLiczbaTorow(resultSet.getInt("liczba_torow"));
                stacja.setLiczbaPeronow(resultSet.getInt("liczba_peronow"));
                stacja.setDlugoscPeronu(resultSet.getDouble("dlugosc_peronu"));

                stacje.add(stacja);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stacje;
    }

    public void getConnections(String fromStation, String toStation, String day, String time) {
        if(fromStation.equals(toStation))
            return;

        // TODO: get all suitable connections and return then
    }
}
