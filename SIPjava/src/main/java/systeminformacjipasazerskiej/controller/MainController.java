package systeminformacjipasazerskiej.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import systeminformacjipasazerskiej.db.DatabaseService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML
    private Tab queryTab;
    @FXML
    private Tab insertTab;
    @FXML
    private Tab deleteTab;

    private QueryController queryController;
    private InsertController insertController;
    private DeleteController deleteController;

    DatabaseService dbService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("queryView.fxml"));
            queryTab.setContent(loader.load());
            queryController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("insertView.fxml"));
            insertTab.setContent(loader.load());
            insertController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("deleteView.fxml"));
            deleteTab.setContent(loader.load());
            deleteController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDB(DatabaseService dbService) {
        this.dbService = dbService;
        queryController.setDB(dbService.getQueryDBService());
        insertController.setDB(dbService.getInsertDBService());
        deleteController.setDB(dbService.getDeleteDBService());

        System.out.println("main db ready");
    }
}