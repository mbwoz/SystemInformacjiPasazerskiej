package systeminformacjipasazerskiej.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import systeminformacjipasazerskiej.converter.DayConverter;

import systeminformacjipasazerskiej.db.QueryDBService;
import systeminformacjipasazerskiej.model.Kurs;
import systeminformacjipasazerskiej.model.Postoj;
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
    private Button swapButton;
    @FXML
    private ComboBox<String> toComboBox;
    @FXML
    private ChoiceBox<String> dayChoiceBox;
    @FXML
    private ChoiceBox<String> timeChoiceBox;
    @FXML
    private CheckBox pospiesznyCheckBox;
    @FXML
    private CheckBox ekspresCheckBox;
    @FXML
    private CheckBox pendolinoCheckBox;
    @FXML
    private CheckBox roweryCheckBox;
    @FXML
    private CheckBox niepelnosprawniCheckBox;
    @FXML
    private Button searchConnectionButton;

    @FXML
    private ListView<Kurs> connectionsListView;

    private QueryDBService qdb;
    private ObservableList<String> allStationsNames = FXCollections.observableArrayList();
    private ObservableList<Kurs> allMatchingKursy = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fromComboBox.setItems(allStationsNames);
        fromComboBox.setPromptText("np. Warszawa Centralna");
        toComboBox.setItems(allStationsNames);
        toComboBox.setPromptText("np. Szczecin Główny");

        swapButton.setOnMouseClicked(event -> {
            String fromCB = fromComboBox.getValue();
            String toCB = toComboBox.getValue();
            fromComboBox.setValue(toCB);
            toComboBox.setValue(fromCB);
        });

        dayChoiceBox.setItems(FXCollections.observableArrayList(
            Stream.iterate(0, i -> i+1)
                .limit(7)
                .map(DayConverter::convertDay)
                .collect(Collectors.toList())
        ));
        dayChoiceBox.getSelectionModel().select(0);

        ArrayList<String> timeArrayList = new ArrayList<>();
        for(int i = 0; i < 24; i++)
            timeArrayList.add(((i < 10) ? "0" + i : i) + ":00");
        timeChoiceBox.setItems(FXCollections.observableArrayList(
            timeArrayList
        ));
        timeChoiceBox.getSelectionModel().select(0);

        searchConnectionButton.setOnMouseClicked(event -> {
            connectionsListView.setVisible(false);
            allMatchingKursy.clear();

            try {
                allMatchingKursy.addAll(qdb.getConnections(
                    fromComboBox.getValue(),
                    toComboBox.getValue(),
                    dayChoiceBox.getValue(),
                    timeChoiceBox.getValue(),
                    pospiesznyCheckBox.isSelected(),
                    ekspresCheckBox.isSelected(),
                    pendolinoCheckBox.isSelected(),
                    roweryCheckBox.isSelected(),
                    niepelnosprawniCheckBox.isSelected()));

                allMatchingKursy.removeIf(k -> k.getSkladKursu().getListaWagonow().size() == 0);
                if(allMatchingKursy.isEmpty())
                    throw new QueryDBService.NoMatchingKursyException();

                connectionsListView.setVisible(true);
            } catch (QueryDBService.NoSuchStationException nss) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono podanej stacji.");
                alert.showAndWait();
            } catch (QueryDBService.NoMatchingKursyException nmk) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono pasujących połączeń.");
                alert.showAndWait();
            }

            System.out.println("db call END\nprint postoje dla kursow");
            allMatchingKursy.forEach(p -> System.out.println(p.getListaPostojow().toString()));
        });

        connectionsListView.setItems(allMatchingKursy);
        connectionsListView.setCursor(Cursor.HAND);
        connectionsListView.setVisible(false);
        connectionsListView.setOnMouseClicked(event -> {
            Kurs kurs = connectionsListView.getSelectionModel().getSelectedItem();
            System.out.println(kurs.getListaPostojow().toString());

            // bonus info
            boolean hasAdditionalInfo = false;
            String bonusInfo = "\n\nDodatkowe informacje:";

            if(kurs.getSkladKursu().checkIfKlimatyzacja()) {
                bonusInfo += "\n - wagony klimatyzowane;";
                hasAdditionalInfo = true;
            }
            if(kurs.getSkladKursu().checkIfNiepelnosprawni()) {
                bonusInfo += "\n - wagony z miejscami dla osób niepełnosprawnych;";
                hasAdditionalInfo = true;
            }
            if(kurs.getSkladKursu().checkIfWifi()) {
                bonusInfo += "\n - wagony z dostępem do wifi;";
                hasAdditionalInfo = true;
            }
            if(kurs.getSkladKursu().isCzyPrzesylki()) {
                bonusInfo += "\n - przesyłki konduktorskie;";
                hasAdditionalInfo = true;
            }
            if(kurs.getSkladKursu().checkIfBarowy()) {
                bonusInfo += "\n - wagon barowy;";
                hasAdditionalInfo = true;
            }

            // train info
            Label trainInfo =
                new Label("Nazwa pociągu: " + kurs.getPociag().getNazwaPociagu() +
                    "   Typ pociągu: " + kurs.getPociag().getTypPociagu() +
                    "\nRelacja: " + qdb.getStacjaById(qdb.getFirstStationFromTrasa(kurs.getPociag().getIdTrasy())).getNazwaStacji() +
                    " - " + qdb.getStacjaById(qdb.getLastStationFromTrasa(kurs.getPociag().getIdTrasy())).getNazwaStacji() +
                    "\nLiczba miejsc w wagonach 1 klasy: " + kurs.getSkladKursu().getLiczbaMiejscI() +
                    "\nLiczba miejsc w wagonach 2 klasy: " + kurs.getSkladKursu().getLiczbaMiejscII() +
                    "\nLiczba miejsc dla rowerów: " + kurs.getSkladKursu().getLiczbaMiejscDlaRowerow() +
                    (hasAdditionalInfo ? bonusInfo : "")
                );

            // timetable
            TableView<Postoj> timetableView = new TableView<>();
            TableColumn<Postoj, String> stacje = new TableColumn<>("Stacja");
            stacje.setCellValueFactory(new PropertyValueFactory<>("nazwaStacji"));

            TableColumn<Postoj, String> przyjazdy = new TableColumn<>("Przyjazd");
            przyjazdy.setCellValueFactory(new PropertyValueFactory<>("przyjazd"));
            przyjazdy.setStyle("-fx-alignment: CENTER;");

            TableColumn<Postoj, String> odjazdy = new TableColumn<>("Odjazd");
            odjazdy.setCellValueFactory(new PropertyValueFactory<>("odjazd"));
            odjazdy.setStyle("-fx-alignment: CENTER;");

            timetableView.getColumns().addAll(stacje, przyjazdy, odjazdy);
            timetableView.getItems().addAll(kurs.getListaPostojow());

            stacje.setMinWidth(300);
            przyjazdy.setMinWidth(125);
            odjazdy.setMinWidth(125);

            VBox popupContent = new VBox();
            popupContent.getChildren().addAll(trainInfo, timetableView);
            popupContent.setSpacing(20);

            Dialog<Kurs> kursDialog = new Dialog<>();
            kursDialog.getDialogPane().setMinWidth(600);
            kursDialog.getDialogPane().setContent(popupContent);
            kursDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            kursDialog.showAndWait();
        });
        allMatchingKursy.addListener((ListChangeListener<Kurs>) change ->
            connectionsListView.setMaxHeight(allMatchingKursy.size() * 24 + 2)
        );
    }

    public void setDB(QueryDBService qdb) {
        this.qdb = qdb;
        System.out.println("query db ready");

        allStationsNames.addAll(
            qdb.getAllStations()
            .stream()
            .map(Stacja::getNazwaStacji)
            .collect(Collectors.toList()));
    }

}
