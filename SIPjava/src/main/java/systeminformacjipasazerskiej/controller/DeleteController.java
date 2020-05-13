package systeminformacjipasazerskiej.controller;

import javafx.fxml.Initializable;
import systeminformacjipasazerskiej.db.DeleteDBService;

import java.net.URL;
import java.util.ResourceBundle;

public class DeleteController implements Initializable {

    DeleteDBService ddb;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setDB(DeleteDBService ddb) {
        this.ddb = ddb;
        System.out.println("delete db ready");
    }
}
