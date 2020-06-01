package systeminformacjipasazerskiej.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import systeminformacjipasazerskiej.db.InsertDBService;
import systeminformacjipasazerskiej.db.QueryDBService;
import systeminformacjipasazerskiej.model.Stacja;

import java.awt.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class InsertController implements Initializable {

    InsertDBService idb;
    QueryDBService qdb;

    @FXML
    private ComboBox<String> insertStationName;
    @FXML
    private TextField insertStationTory;
    @FXML
    private TextField insertStationPerony;
    @FXML
    private TextField insertStationDlugosc;
    @FXML
    private Button insertStationButton;

    @FXML
    private ComboBox<String> insertOdcinekStart;
    @FXML
    private ComboBox<String> insertOdcinekKoniec;
    @FXML
    private Button insertOdcinekButton;


    private ObservableList<String> allStationsNames = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        insertStationName.setItems(allStationsNames);
        insertStationName.setPromptText("np. Kraków Płaszów");
        insertStationTory.setPromptText("1 - 99");
        insertStationPerony.setPromptText("1 - 99");
        insertStationDlugosc.setPromptText("0.01 - 999.99");

        insertStationButton.setOnMouseClicked(event -> {

            String nazwa = "";
            int tory = 0, perony = 0;
            double dlugosc = 0;
            boolean dataCorrectness = true;

            try {
                nazwa = (String) insertStationName.getValue();
                tory = Integer.parseInt(insertStationTory.getText());
                perony = Integer.parseInt(insertStationPerony.getText());
                dlugosc = Double.parseDouble(insertStationDlugosc.getText());
            } catch(NumberFormatException nfe) {
                dataCorrectness = false;
            }



            if(nazwa == null || nazwa.isBlank())
                dataCorrectness = false;
            if(tory <= 0 || tory >= 100)
                dataCorrectness = false;
            if(perony <= 0 || perony >= 100)
                dataCorrectness = false;
            if(dlugosc <= 0 || dlugosc >= 1000)
                dataCorrectness = false;

            if(!dataCorrectness) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane");
                alert.showAndWait();
                return;
            }

            Stacja stacja = new Stacja();
            stacja.setNazwaStacji(nazwa);
            stacja.setLiczbaPeronow(perony);
            stacja.setLiczbaTorow(tory);
            stacja.setDlugoscPeronu(dlugosc);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdź wybór");
            if(idb.checkStationExistence(nazwa)) {
                alert.setHeaderText("Czy na pewno chcesz zmodyfikować tę stację?");
                Optional<ButtonType> result = alert.showAndWait();

                if(result.get() == ButtonType.OK) {
                    try {
                        idb.updateStation(stacja);
                    } catch (InsertDBService.UpdateStationOverflowException e) {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setHeaderText("Modyfikacja zakończona niepowodzeniem.");
                        info.setContentText("Operacja nie powiodła się - zbyt mała liczba torów.");
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        info.showAndWait();
                        return;
                    } catch (InsertDBService.UpdateStationLengthException e) {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setHeaderText("Modyfikacja zakończona niepowodzeniem.");
                        info.setContentText("Operacja nie powiodła się - zbyt krótkie perony.");
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        info.showAndWait();
                        return;
                    } catch (InsertDBService.UpdateStationException e) {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setHeaderText("Modyfikacja zakończona niepowodzeniem.");
                        info.showAndWait();
                        return;
                    }

                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText("Modyfikacja zakończona powodzeniem.");
                    info.showAndWait();
                }

            } else {
                alert.setHeaderText("Czy na pewno chcesz dodać tę stację?");
                Optional<ButtonType> result = alert.showAndWait();

                if(result.get() == ButtonType.OK) {
                    try {
                        idb.insertStation(stacja);
                        allStationsNames.clear();
                        allStationsNames.addAll(
                                qdb.getAllStations()
                                        .stream()
                                        .map(Stacja::getNazwaStacji)
                                        .collect(Collectors.toList()));
                    } catch (InsertDBService.InsertStationException e) {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setHeaderText("Dodanie zakończone niepowodzeniem.");
                        info.showAndWait();
                        return;
                    }

                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText("Dodanie zakończone powodzeniem.");
                    info.showAndWait();
                }
            }
        });



        // insert odcinek
        insertOdcinekStart.setItems(allStationsNames);
        insertOdcinekKoniec.setItems(allStationsNames);
        insertOdcinekStart.setPromptText("np. Kraków Główny");
        insertOdcinekKoniec.setPromptText("np. Warszawa Zachodnia");

        insertOdcinekButton.setOnMouseClicked(event -> {

            String start = (String) insertOdcinekStart.getValue();
            String end = (String) insertOdcinekKoniec.getValue();
            boolean dataCorrectness = true;

            if(start.isBlank() || end.isBlank()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane.");
                alert.showAndWait();
                return;
            }

            int idStart = 0;
            int idEnd = 0;
            try {
                idStart = idb.getStationId(start);
                idEnd = idb.getStationId(end);
            } catch (InsertDBService.NoStationException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono stacji.");
                alert.showAndWait();
                return;
            }

            if(idb.checkOdcinekExistence(idStart, idEnd)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Podany odcinek już istnieje.");
                alert.showAndWait();
                return;
            }
            if(idStart == idEnd) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Stacja początkowa musi różnić się od stacji końcowej");
                alert.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdź wybór");
            alert.setHeaderText("Czy na pewno chcesz dodać ten odcinek?");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK) {
                try {
                    idb.insertOdcinek(idStart, idEnd);
                } catch (InsertDBService.InsertOdcinekException e) {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText("Dodanie zakończone niepowodzeniem.");
                    info.showAndWait();
                    return;
                }

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText("Dodanie zakończone powodzeniem.");
                info.showAndWait();
            }
        });


    }

    public void setDB(QueryDBService qdb) {
        this.qdb = qdb;

        allStationsNames.addAll(
                qdb.getAllStations()
                        .stream()
                        .map(Stacja::getNazwaStacji)
                        .collect(Collectors.toList()));
    }

    public void setDB(InsertDBService idb) {
        this.idb = idb;
        System.out.println("insert db ready");
    }

}
