package systeminformacjipasazerskiej.db;

import java.sql.Connection;

public class InsertDBService {
    private Connection connection;

    public InsertDBService(Connection connection) {
        this.connection = connection;
    }
}
