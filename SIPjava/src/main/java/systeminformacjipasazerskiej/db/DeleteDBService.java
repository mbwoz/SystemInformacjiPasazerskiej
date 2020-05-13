package systeminformacjipasazerskiej.db;

import java.sql.Connection;

public class DeleteDBService {
    private Connection connection;

    public DeleteDBService(Connection connection) {
        this.connection = connection;
    }
}
