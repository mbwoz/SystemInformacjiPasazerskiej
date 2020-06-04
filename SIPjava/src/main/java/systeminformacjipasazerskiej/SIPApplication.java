package systeminformacjipasazerskiej;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import systeminformacjipasazerskiej.controller.IntroController;

public class SIPApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("intro.fxml"));

        Pane pane = loader.load();
        stage.setTitle("System Informacji Pasażerskiej");
        stage.setScene(new Scene(pane, 1024, 768));
        stage.setResizable(false);

        IntroController introController = loader.getController();
        introController.setStage(stage);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
