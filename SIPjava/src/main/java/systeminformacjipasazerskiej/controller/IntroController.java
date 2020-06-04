package systeminformacjipasazerskiej.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import systeminformacjipasazerskiej.db.DatabaseService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class IntroController implements Initializable {

    private Stage stage;

    @FXML
    private TextField hostTextField;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField databaseTextField;
    @FXML
    private TextField userTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Button connectButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hostTextField.setPromptText("np. localhost");
        portTextField.setPromptText("np. 5432");
        databaseTextField.setPromptText("np. jan");
        userTextField.setPromptText("np. jan");
        passwordTextField.setPromptText("np. qwerty");

        connectButton.setOnMouseClicked(event -> {
            DatabaseService dbService;

            try {
                dbService = new DatabaseService(
                    hostTextField.getText(),
                    portTextField.getText(),
                    databaseTextField.getText(),
                    userTextField.getText(),
                    passwordTextField.getText()
                );
            } catch (Exception e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Próba połączenia zakończyła się niepowodzeniem.");
                alert.setContentText("Zweryfikuj dane i spróbuj ponownie.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();

                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));

            Pane pane = null;
            try {
                pane = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.getScene().setRoot(pane);

            MainController mainController = loader.getController();
            mainController.setDB(dbService);
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
