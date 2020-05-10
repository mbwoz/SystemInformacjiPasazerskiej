package systeminformacjipasazerskiej.db;

import java.sql.*;

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

    public void getLekarze() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM lekarze;");

            while (rs.next()) {
                int id = rs.getInt(1);
                String imie = rs.getString(2);
                String nazwisko = rs.getString(3);
                System.out.println("ID = " + id);
                System.out.println("IMIE = " + imie);
                System.out.println("NAZWISKO = " + nazwisko);
                System.out.println();
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
