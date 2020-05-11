package systeminformacjipasazerskiej.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import systeminformacjipasazerskiej.db.DatabaseService;
import systeminformacjipasazerskiej.model.Stacja;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class QueryController implements Initializable {

    @FXML
    private ComboBox<String> fromComboBox;
    @FXML
    private ComboBox<String> toComboBox;
    @FXML
    private ChoiceBox<String> dayChoiceBox;
    @FXML
    private ChoiceBox<String> timeChoiceBox;
    @FXML
    private Button searchConnectionButton;

    private DatabaseService db;
    private ObservableList<String> allStationsNames = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fromComboBox.setItems(allStationsNames);
        toComboBox.setItems(allStationsNames);

        dayChoiceBox.setItems(FXCollections.observableArrayList(
            "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"
        ));

        ArrayList<String> timeArrayList = new ArrayList<>();
        for(int i = 0; i < 24; i++)
            timeArrayList.add(((i < 10) ? "0" + i : i) + ":00");
        timeChoiceBox.setItems(FXCollections.observableArrayList(
            timeArrayList
        ));

        searchConnectionButton.setOnMouseClicked(event -> {
            db.getConnections(
                fromComboBox.getValue(),
                toComboBox.getValue(),
                dayChoiceBox.getValue(),
                timeChoiceBox.getValue());

            // TODO: show results ftrom search
        });
    }

    public void setDB(DatabaseService db) {
        this.db = db;
        System.out.println("query db ready");

        allStationsNames.addAll(
            db.getAllStations()
            .stream()
            .map(Stacja::getNazwaStacji)
            .collect(Collectors.toList()));
    }



}
