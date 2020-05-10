package systeminformacjipasazerskiej;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import systeminformacjipasazerskiej.controller.MainController;
import systeminformacjipasazerskiej.db.DatabaseService;

public class SIPApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));

        Pane pane = loader.load();
        stage.setTitle("System Informacji Pasażerskiej");
        stage.setScene(new Scene(pane, 800, 600));
        stage.setResizable(false);
        stage.show();

        DatabaseService db = new DatabaseService();
        MainController mainController = loader.getController();
        mainController.setDB(db);
    }

    public static void main(String[] args) {
        launch();
    }
}
