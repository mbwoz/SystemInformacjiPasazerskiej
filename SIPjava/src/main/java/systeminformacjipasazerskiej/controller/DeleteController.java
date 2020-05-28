package systeminformacjipasazerskiej.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import systeminformacjipasazerskiej.converter.DayConverter;
import systeminformacjipasazerskiej.db.DeleteDBService;
import systeminformacjipasazerskiej.db.QueryDBService;
import systeminformacjipasazerskiej.model.Kurs;
import systeminformacjipasazerskiej.model.Pociag;
import systeminformacjipasazerskiej.model.Postoj;
import systeminformacjipasazerskiej.model.Stacja;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeleteController implements Initializable {

    DeleteDBService ddb;
    QueryDBService qdb;

    @FXML
    private Button deleteStationButton;
    @FXML
    private ComboBox<String> deleteStationBox;

    @FXML
    private Button deleteRideButton;
    @FXML
    private ComboBox<String> deleteRideFromBox;
    @FXML
    private ComboBox<String> deleteRideToBox;
    @FXML
    private Button deleteTrasaButton;
    @FXML
    private ComboBox<String> deleteTrasaFromBox;
    @FXML
    private ComboBox<String> deleteTrasaToBox;
    @FXML
    private ComboBox<String> deletePociagBox;
    @FXML
    private Button deletePociagButton;
    @FXML
    private ChoiceBox<String> dayRideBox;
    @FXML
    private ListView<Kurs> rideList;
    @FXML
    private ListView<Integer> trasyListView;
    @FXML
    private ComboBox<String> deleteOdcinekFromBox;
    @FXML
    private ComboBox<String> deleteOdcinekToBox;
    @FXML
    private Button deleteOdcinekButton;

    class RideCell extends ListCell<Kurs> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("Usuń");

        public RideCell() {
            super();
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potwierdź wybór");
                alert.setHeaderText("Czy na pewno chcesz usunąć ten kurs?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    Kurs toDel = getItem();
                    getListView().getItems().remove(getItem());
                    ddb.deleteRide(toDel);
                }

            });
        }
        protected void updateItem(Kurs item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                System.out.println("updatingItems");
                ArrayList<Postoj> list = item.getListaPostojow();



                Pociag pociag = qdb.getPociagById(item.getPociag().getIdPociagu());
                int id_trasy = pociag.getIdTrasy();
                Stacja first = qdb.getStacjaById(qdb.getFirstStationFromTrasa(id_trasy));
                Postoj firstPostoj = qdb.getPostojByIds(item.getIdKursu(), first.getIdStacji());
                Stacja last = qdb.getStacjaById(qdb.getLastStationFromTrasa(id_trasy));
                Postoj lastPostoj = qdb.getPostojByIds(item.getIdKursu(), last.getIdStacji());


                label.setText("Z: " + first.getNazwaStacji() + "(" +
                        firstPostoj.getOdjazd()+")  " + "   Do: " +  last.getNazwaStacji() +
                        "(" + lastPostoj.getPrzyjazd() + ")" +
                        "  Pociąg: " + pociag.getNazwaPociagu());
                setGraphic(hbox);
            }
        }
    }

    class TrasaCell extends ListCell<Integer> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("Usuń");

        public TrasaCell() {
            super();
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potwierdź wybór");
                alert.setHeaderText("Czy na pewno chcesz usunąć tę trasę?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    Integer toDel = getItem();
                    getListView().getItems().remove(getItem());
                    ddb.deleteTrasa(toDel);
                }

            });}


            protected void updateItem (Integer item,boolean empty){
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);

                if (item != null && !empty) {
                    System.out.println("updatingItems");
                    int id_trasy = item;
                    int fromStationId = qdb.getFirstStationFromTrasa(id_trasy);
                    int lastStationId = qdb.getLastStationFromTrasa(id_trasy);
                    Stacja firstStation = qdb.getStacjaById(fromStationId);
                    Stacja lastStation = qdb.getStacjaById(lastStationId);
                    label.setText("Z: " + firstStation.getNazwaStacji() + "    Do: " + lastStation.getNazwaStacji());
                    setGraphic(hbox);
                }
            }
        }





    private ObservableList<String> allStationsNames = FXCollections.observableArrayList();
    private ObservableList<Kurs> allMatchingKursy = FXCollections.observableArrayList();
    private ObservableList<String> allPociagNames = FXCollections.observableArrayList();
    private ObservableList<Integer> allTrasy = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Delete Station
        deleteStationBox.setItems(allStationsNames);
        deleteStationBox.setPromptText("np. Kraków Główny");
        deleteStationButton.setOnMouseClicked(event -> {
            if(deleteStationBox.getValue() == null) return;

            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potwierdź wybór");
                alert.setHeaderText("Czy na pewno chcesz usunąć tę stację?");
                alert.setContentText("Usunięcie stacji spowoduje usunięcie wszystkich postójów, odcinków, tras i pociągów z nią związanych.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ddb.deleteStation(deleteStationBox.getValue());
                    allStationsNames.clear();
                    allStationsNames.addAll(
                            qdb.getAllStations()
                                    .stream()
                                    .map(Stacja::getNazwaStacji)
                                    .collect(Collectors.toList()));
                }
            } catch (QueryDBService.NoSuchStationException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono podanej stacji.");
                alert.showAndWait();
            }
        });
        //*****************
        //Delete pociag

        deletePociagBox.setItems(allPociagNames);
        deletePociagBox.setPromptText("np. beuiddxb");
        deletePociagButton.setOnMouseClicked(e -> {
            if(deletePociagBox.getValue() == null) return;
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potwierdź wybór");
                alert.setHeaderText("Czy na pewno chcesz usunąć ten pociąg?");
                alert.setContentText("Usunięcie pociągu spowoduje usunięcie tras i postojów z nim związanych.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ddb.deletePociag(deletePociagBox.getValue());
                    allPociagNames.clear();
                    allPociagNames.addAll(
                            qdb.getAllPociagi()
                                    .stream()
                                    .map(Pociag::getNazwaPociagu)
                                    .collect(Collectors.toList()));
                }

            } catch (QueryDBService.NoSuchTrainException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono podanego pociągu.");
                alert.showAndWait();
            }

        });


        //******************
        //Delete ride
        deleteRideFromBox.setItems(allStationsNames);
        deleteRideFromBox.setPromptText("np. Poznań Główny");
        deleteRideToBox.setItems(allStationsNames);
        deleteRideToBox.setPromptText("np. Wrocław Główny");

        dayRideBox.setItems(FXCollections.observableArrayList(
            Stream.iterate(0, i -> i+1)
                .limit(7)
                .map(DayConverter::convertDay)
                .collect(Collectors.toList())
        ));
        dayRideBox.getSelectionModel().select(0);

        deleteRideButton.setOnMouseClicked(event -> {
            rideList.setVisible(false);
            allMatchingKursy.clear();

            try {
                allMatchingKursy.addAll(qdb.getConnections(
                    deleteRideFromBox.getValue(),
                    deleteRideToBox.getValue(),
                    dayRideBox.getValue(),
                    "Odjazd", "00:00",
                    true, true, true,
                    false, false));

                rideList.setOnMouseClicked(e -> {
                    Kurs kursPart = rideList.getSelectionModel().getSelectedItem();
                    Kurs kurs = qdb.getWholeKursFromPart(kursPart);

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
                    popupContent.getChildren().add(timetableView);
                    popupContent.setSpacing(20);
                    Dialog<Kurs> kursDialog = new Dialog<>();
                    kursDialog.getDialogPane().setMinWidth(600);
                    kursDialog.getDialogPane().setContent(popupContent);
                    kursDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                    kursDialog.showAndWait();
                });
                rideList.setVisible(true);
            } catch (QueryDBService.NoSuchStationException nss) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono podanej stacji.");
                alert.showAndWait();
            } catch (QueryDBService.NoMatchingKursyException nmk) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono pasujących połączeń.");
                alert.showAndWait();
            }
        });
        rideList.setItems(allMatchingKursy);
        rideList.setCursor(Cursor.HAND);
        rideList.setVisible(false);
        rideList.setCellFactory(param -> new RideCell());
        allMatchingKursy.addListener((ListChangeListener<Kurs>) change ->
            rideList.setMaxHeight(allMatchingKursy.size() * 34 + 2)
        );

        //********************************
        //delete trasa
        deleteTrasaFromBox.setItems(allStationsNames);
        deleteTrasaFromBox.setPromptText("np. Warszawa Centralna");
        deleteTrasaToBox.setItems(allStationsNames);
        deleteTrasaToBox.setPromptText("np. Kraków Główny");

        deleteTrasaButton.setOnMouseClicked(e -> {
            try {
                ArrayList<Integer> trasa_id = qdb.getTrasaIdFromTo(deleteTrasaFromBox.getValue(), deleteTrasaToBox.getValue());
                System.out.println("Znalezione trasy:" + trasa_id.stream().collect(Collectors.toList()));

                trasyListView.setVisible(false);
                allTrasy.clear();
                allTrasy.addAll(trasa_id);
                trasyListView.setItems(allTrasy);
                trasyListView.setCursor(Cursor.HAND);

                trasyListView.setOnMouseClicked(event -> {
                    Integer id_trasy = trasyListView.getSelectionModel().getSelectedItem();


                    ArrayList<Stacja> stacje = qdb.getAllStacjeOnTrasa(id_trasy,
                            qdb.getFirstStationFromTrasa(id_trasy), qdb.getLastStationFromTrasa(id_trasy));
                    ListView<String> stacjeList = new ListView<>();
                    stacjeList.setItems(FXCollections.observableArrayList(
                            stacje.stream().map(Stacja::getNazwaStacji).collect(Collectors.toList())));

                    VBox popupContent = new VBox();
                    popupContent.getChildren().addAll(stacjeList);
                    popupContent.setSpacing(20);

                    Dialog<String> kursDialog = new Dialog<>();
                    kursDialog.getDialogPane().setMinWidth(600);
                    kursDialog.getDialogPane().setContent(popupContent);
                    kursDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                    kursDialog.showAndWait();
                });

                 trasyListView.setVisible(true);

            } catch (QueryDBService.NoSuchStationException nss) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono podanej stacji.");
                alert.showAndWait();
            } catch (QueryDBService.NoMatchingTrasyException mnt) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie ma takiej trasy.");
                alert.showAndWait();
            }
        });

        trasyListView.setItems(allTrasy);
        trasyListView.setCursor(Cursor.HAND);
        trasyListView.setVisible(false);
        trasyListView.setCellFactory(param -> new TrasaCell());
        allTrasy.addListener((ListChangeListener<Integer>) change ->
                trasyListView.setMaxHeight(allTrasy.size() * 34 + 2));

        //***************************************
        //delete odcinek
        deleteOdcinekFromBox.setItems(allStationsNames);
        deleteOdcinekFromBox.setPromptText("np. Bochnia");
        deleteOdcinekToBox.setItems(allStationsNames);
        deleteOdcinekToBox.setPromptText("np. Brzesko Okocim");

        deleteOdcinekButton.setOnMouseClicked(e -> {
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potwierdź wybór");
                alert.setHeaderText("Czy na pewno chcesz usunąć ten odcinek?");
                alert.setContentText("Usunięcie odcinka spowoduje usunięcie wszystkich postójów, tras i pociągów z nią związanych.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ddb.deleteOdcinek(deleteOdcinekFromBox.getValue(), deleteOdcinekToBox.getValue());
                    Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                    alertInfo.setHeaderText("Poprawnie usunięto");
                    alertInfo.showAndWait();
                }
            }
            catch (DeleteDBService.NoSuchOdcinekException nso) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie ma takiego odcinka.");
                alert.showAndWait();
            }
            catch (QueryDBService.NoSuchStationException nss) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono podanej stacji.");
                alert.showAndWait();
            }

        });


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
        allPociagNames.addAll(
                qdb.getAllPociagi()
                    .stream()
                    .map(Pociag::getNazwaPociagu)
                    .collect(Collectors.toList()));

        System.out.println("delete db ready");
    }
}


//TODO: Update station list in Query and Insert after deleting station