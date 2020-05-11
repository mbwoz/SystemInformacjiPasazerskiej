package systeminformacjipasazerskiej.controller;

import javafx.fxml.Initializable;
import systeminformacjipasazerskiej.db.DatabaseService;

import java.net.URL;
import java.util.ResourceBundle;

public class InsertController implements Initializable {

    DatabaseService db;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setDB(DatabaseService db) {
        this.db = db;
        System.out.println("insert db ready");
    }

}
