package systeminformacjipasazerskiej.controller;

import javafx.fxml.Initializable;
import systeminformacjipasazerskiej.db.InsertDBService;

import java.net.URL;
import java.util.ResourceBundle;

public class InsertController implements Initializable {

    InsertDBService idb;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setDB(InsertDBService idb) {
        this.idb = idb;
        System.out.println("insert db ready");
    }

}
