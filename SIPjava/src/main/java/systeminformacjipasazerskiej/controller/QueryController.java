package systeminformacjipasazerskiej.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import systeminformacjipasazerskiej.converter.DayConverter;
import systeminformacjipasazerskiej.db.DatabaseService;
import systeminformacjipasazerskiej.model.Kurs;
import systeminformacjipasazerskiej.model.Stacja;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private ObservableList<Kurs> allMatchingKursy = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fromComboBox.setItems(allStationsNames);
        toComboBox.setItems(allStationsNames);

        dayChoiceBox.setItems(FXCollections.observableArrayList(
            Stream.iterate(0, i -> i+1)
                .limit(7)
                .map(DayConverter::convertDay)
                .collect(Collectors.toList())
        ));

        ArrayList<String> timeArrayList = new ArrayList<>();
        for(int i = 0; i < 24; i++)
            timeArrayList.add(((i < 10) ? "0" + i : i) + ":00");
        timeChoiceBox.setItems(FXCollections.observableArrayList(
            timeArrayList
        ));

        searchConnectionButton.setOnMouseClicked(event -> {
            allMatchingKursy.clear();
            allMatchingKursy.addAll(
                db.getConnections(
                fromComboBox.getValue(),
                toComboBox.getValue(),
                dayChoiceBox.getValue(),
                timeChoiceBox.getValue()));

            System.out.println("db call END\nprint postoje dla kursow");
            allMatchingKursy.forEach(p -> System.out.println(p.getListaPostojow().toString()));
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