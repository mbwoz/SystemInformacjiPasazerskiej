package systeminformacjipasazerskiej.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import systeminformacjipasazerskiej.db.DeleteDBService;
import systeminformacjipasazerskiej.db.QueryDBService;
import systeminformacjipasazerskiej.model.Stacja;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DeleteController implements Initializable {

    DeleteDBService ddb;
    QueryDBService qdb;

    @FXML
    private Button deleteStationButton;
    @FXML
    private ComboBox<String> deleteStationBox;

    @FXML
    private Button deleteTruckButton;
    @FXML
    private ComboBox<String> deleteTruckBox;

    @FXML
    private Button deleteTrainButton;
    @FXML
    private ComboBox<String> deleteTrainBox;

    @FXML
    private Button deleteRideButton;
    @FXML
    private ComboBox<String> deleteRideFromBox;
    @FXML
    private ComboBox<String> deleteRideToBox;

    private ObservableList<String> allStationsNames = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Delete Station
        deleteStationBox.setItems(allStationsNames);
        deleteStationBox.setPromptText("np. Kraków Główny");
        deleteStationButton.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdź wybór");
            alert.setHeaderText("Czy na pewno chcesz usunąć tę stację?");
            alert.setContentText("Usunięcie stacji spowoduje usunięcie wszystkich postójów i odcinków z nią związanych.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                ddb.deleteStation(deleteStationBox.getValue());
                allStationsNames.clear();
                allStationsNames.addAll(
                    qdb.getAllStations()
                        .stream()
                        .map(Stacja::getNazwaStacji)
                        .collect(Collectors.toList()));
            }
        });

        //*************
    }

    public void setDB(DeleteDBService ddb) {
        this.ddb = ddb;
    }

    public void setDB(QueryDBService qdb) {
        this.qdb = qdb;

        allStationsNames.addAll(
            qdb.getAllStations()
                .stream()
                .map(Stacja::getNazwaStacji)
                .collect(Collectors.toList()));

        System.out.println("delete db ready");
    }
}
