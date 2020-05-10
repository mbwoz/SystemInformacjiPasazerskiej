package systeminformacjipasazerskiej.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import systeminformacjipasazerskiej.db.DatabaseService;

import java.net.URL;
import java.util.ResourceBundle;

public class InsertController implements Initializable {

    @FXML
    private Button insertLekarze;

    DatabaseService db;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        insertLekarze.setOnMouseClicked(event -> {
            db.getLekarze();
        });
    }

    public void setDB(DatabaseService db) {
        this.db = db;
        System.out.println("insert db ready");
    }

}
