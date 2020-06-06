package systeminformacjipasazerskiej.db;

import java.sql.*;

public class DatabaseService {
    private Connection connection;

    public DatabaseService(String host, String port, String database, String user, String password)
        throws Exception {

        Class.forName("org.postgresql.Driver");
        connection = DriverManager
                .getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database,
                        user, password);

        System.out.println("Opened database successfully");
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
