package systeminformacjipasazerskiej.db;

import java.sql.*;

public class DatabaseService {
    private Connection connection;

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

    public QueryDBService getQueryDBService() {
        return new QueryDBService(connection);
    }

    public InsertDBService getInsertDBService() {
        return new InsertDBService(connection);
    }

    public DeleteDBService getDeleteDBService() {
        return new DeleteDBService(connection);
    }
}
