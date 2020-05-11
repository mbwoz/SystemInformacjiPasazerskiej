package systeminformacjipasazerskiej.controller;

import javafx.fxml.Initializable;
import systeminformacjipasazerskiej.db.DatabaseService;

import java.net.URL;
import java.util.ResourceBundle;

public class DeleteController implements Initializable {

    DatabaseService db;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setDB(DatabaseService db) {
        this.db = db;
        System.out.println("delete db ready");
    }
}
