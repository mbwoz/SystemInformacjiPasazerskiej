package systeminformacjipasazerskiej.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import systeminformacjipasazerskiej.db.InsertDBService;
import systeminformacjipasazerskiej.db.QueryDBService;
import systeminformacjipasazerskiej.model.Sklad;
import systeminformacjipasazerskiej.model.Stacja;
import systeminformacjipasazerskiej.model.Wagon;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class InsertController implements Initializable {

    InsertDBService idb;
    QueryDBService qdb;

    QueryController queryController;
    DeleteController deleteController;

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

    @FXML
    private TextField insertWagonName;
    @FXML
    private ChoiceBox<String> insertWagonType;
    @FXML
    private TextField insertWagonLength;
    @FXML
    private TextField insertWagon1klasa;
    @FXML
    private TextField insertWagon2klasa;
    @FXML
    private TextField insertWagonBikes;
    @FXML
    private CheckBox insertWagonPrzedzialy;
    @FXML
    private CheckBox insertWagonAC;
    @FXML
    private CheckBox insertWagonWifi;
    @FXML
    private CheckBox insertWagonNiepelnosprawni;
    @FXML
    private Button insertWagonButton;

    @FXML
    private TextField insertSkladNumber;
    @FXML
    private CheckBox insertSkladPrzesylki;
    @FXML
    private Button insertSkladNumberOK;
    @FXML
    private Button insertSkladNumberClean;
    @FXML
    private Button insertSkladButton;
    @FXML
    private VBox insertSkladVBox;
    private ArrayList<ComboBox<String>> insertSkladRodzaj = new ArrayList<>();
    private ArrayList<TextField> insertSkladIlosc = new ArrayList<>();

    @FXML
    private TextField insertTrasaNumber;
    @FXML
    private CheckBox insertTrasaCzyPrzyspieszona;
    @FXML
    private Button insertTrasaNumberOK;
    @FXML
    private Button insertTrasaNumberClean;
    @FXML
    private Button insertTrasaButton;
    @FXML
    private VBox insertTrasaVBox;
    private ArrayList<ComboBox<String>> insertTrasaStacja = new ArrayList<>();


    private ObservableList<String> allStationsNames = FXCollections.observableArrayList();
    private ObservableList<String> allWagonyTypes = FXCollections.observableArrayList("sypialny", "kuszetka", "barowy", "osobowy", "business");
    private ObservableList<String> allWagonyDescriptions = FXCollections.observableArrayList();

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
            if(dlugosc < 0.01 || dlugosc > 999.99)
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
                        info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        info.showAndWait();
                        return;
                    } catch (InsertDBService.UpdateStationLengthException e) {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setHeaderText("Modyfikacja zakończona niepowodzeniem.");
                        info.setContentText("Operacja nie powiodła się - zbyt krótkie perony.");
                        info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
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
                        updateStationNames();
                        queryController.updateStationNames();
                        deleteController.updateStationNames();

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

            if(start == null || end == null || start.isBlank() || end.isBlank()) {
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


        //insert wagon
        insertWagonType.setItems(allWagonyTypes);
        insertWagonType.getSelectionModel().select(0);
        insertWagonName.setPromptText("np. Bautzen 86");
        insertWagonLength.setPromptText("0.01 - 99.99");
        insertWagon1klasa.setPromptText("0 - 99");
        insertWagon2klasa.setPromptText("0 - 99");
        insertWagonBikes.setPromptText("0 - 99");

        insertWagonButton.setOnMouseClicked(event -> {
            String nazwa = "";
            String typ = "";
            int klasa1 = 0, klasa2 = 0;
            int rowery = 0;
            double dlugosc = 0;
            boolean przedzialy = false, klimatyzacja = false, wifi = false, niepelnosprawni = false;
            boolean dataCorrectness = true;

            try {
                nazwa = insertWagonName.getText();
                typ = insertWagonType.getValue();
                if(!insertWagon1klasa.getText().isBlank())
                    klasa1 = Integer.parseInt(insertWagon1klasa.getText());
                if(!insertWagon2klasa.getText().isBlank())
                    klasa2 = Integer.parseInt(insertWagon2klasa.getText());
                if(!insertWagonBikes.getText().isBlank())
                    rowery = Integer.parseInt(insertWagonBikes.getText());
                dlugosc = Double.parseDouble(insertWagonLength.getText());
                przedzialy = insertWagonPrzedzialy.isSelected();
                klimatyzacja = insertWagonAC.isSelected();
                wifi = insertWagonWifi.isSelected();
                niepelnosprawni = insertWagonNiepelnosprawni.isSelected();
            } catch(NumberFormatException nfe) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane");
                alert.showAndWait();
                return;
            }

            if(nazwa == null || nazwa.isBlank())
                dataCorrectness = false;
            if(klasa1 < 0 || klasa1 >= 100)
                dataCorrectness = false;
            if(klasa2 < 0 || klasa2 >= 100)
                dataCorrectness = false;
            if(rowery < 0 || rowery >= 100)
                dataCorrectness = false;
            if(dlugosc < 0.01 || dlugosc > 99.99)
                dataCorrectness = false;

            if(!dataCorrectness) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane");
                alert.showAndWait();
                return;
            }

            DecimalFormat df = new DecimalFormat("#.##");
            dlugosc = Double.valueOf(df.format(dlugosc));

            Wagon wagon = new Wagon();
            wagon.setModel(nazwa);
            wagon.setTyp(typ);
            wagon.setMiejscaI(klasa1);
            wagon.setMiejscaII(klasa2);
            wagon.setRowery(rowery);
            wagon.setCzyKlimatyzacja(klimatyzacja);
            wagon.setCzyPrzedzialowy(przedzialy);
            wagon.setCzyWifi(wifi);
            wagon.setCzyNiepelnosprawni(niepelnosprawni);
            wagon.setDlugosc(dlugosc);

            if(idb.checkWagonExistence(wagon)) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText("Dodawanie zakończona niepowodzeniem.");
                info.setContentText("Operacja nie powiodła się - podany wagon już istnieje.");
                info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                info.showAndWait();
                return;
            }


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdź wybór");
            alert.setHeaderText("Czy na pewno chcesz dodać ten wagon?");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK) {
                try {
                    idb.insertWagon(wagon);
                } catch (InsertDBService.InsertWagonException e) {
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


        //insert sklady
        insertSkladNumber.setPromptText("1-99");
        insertSkladNumberOK.setOnMouseClicked(event -> {
            int num = 0;
            try {
                num = Integer.parseInt(insertSkladNumber.getText());
            } catch(NumberFormatException nfe) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane");
                alert.showAndWait();
                return;
            }

            if(num <= 0 || num >= 100) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane");
                alert.showAndWait();
                return;
            }

            insertSkladVBox.getChildren().clear();
            insertSkladRodzaj.clear();
            insertSkladIlosc.clear();
            for(int i = 0; i < num; i++) {
                HBox hbox = new HBox();
                hbox.setSpacing(10.0);
                hbox.setAlignment(Pos.CENTER_LEFT);

                Label rodzajText = new Label("Rodzaj wagonu:");
                ComboBox<String> rodzajBox = new ComboBox<>();
                rodzajBox.setEditable(false);
                rodzajBox.setItems(allWagonyDescriptions);
                rodzajBox.getSelectionModel().select(0);
                insertSkladRodzaj.add(rodzajBox);

                Label iloscText = new Label("Liczba wagonów:");
                iloscText.setPadding(new Insets(0,0,0,20));
                TextField iloscField = new TextField();
                iloscField.setPromptText("1-99");
                insertSkladIlosc.add(iloscField);

                hbox.getChildren().addAll(rodzajText, rodzajBox, iloscText, iloscField);
                insertSkladVBox.getChildren().add(hbox);
            }
        });
        insertSkladNumberClean.setOnMouseClicked(event -> {
            insertSkladVBox.getChildren().clear();
            insertSkladRodzaj.clear();
            insertSkladIlosc.clear();
            insertSkladNumber.setText(null);
        });

        insertSkladButton.setOnMouseClicked(event -> {
            int number = insertSkladRodzaj.size();
            boolean przesylki = insertSkladPrzesylki.isSelected();
            ArrayList<Integer> skladIlosc = new ArrayList<>();
            ArrayList<Integer> skladIdWagonu = new ArrayList<>();

            if(number == 0)
                return;

            HashSet<String> hashSet = new HashSet<>();

            for(ComboBox<String> box : insertSkladRodzaj) {
                String s = box.getValue();
                hashSet.add(s);
            }
            if(hashSet.size() != number) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane.");
                alert.setContentText("Rodzaje wagonów muszą się różnić.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                return;
            }

            try {
                for(TextField field : insertSkladIlosc) {
                    int toAdd = Integer.parseInt(field.getText());
                    skladIlosc.add(toAdd);

                    if(toAdd >= 100 || toAdd <= 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Błędne dane");
                        alert.showAndWait();
                        return;
                    }
                }
            } catch(NumberFormatException nfe) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane");
                alert.showAndWait();
                return;
            }

            ArrayList<Wagon> allWagony = idb.getAllWagony();
            for(ComboBox<String> box : insertSkladRodzaj) {
                String s = box.getValue();
                int id = idb.getIdWagonuByDescription(s, allWagony);

                if(id == 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Błędne dane");
                    alert.setContentText("Nie znaleziono wagonu.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }

                skladIdWagonu.add(id);
            }

            Alert info = new Alert(Alert.AlertType.CONFIRMATION);
            info.setTitle("Potwierdź wybór");
            info.setHeaderText("Czy na pewno chcesz dodać ten skład?");
            Optional<ButtonType> result = info.showAndWait();

            if(result.get() == ButtonType.OK) {
                Sklad sklad = new Sklad();
                sklad.setCzyPrzesylki(przesylki);
                sklad.setIdWagonow(skladIdWagonu);
                sklad.setLiczbaWagonow(skladIlosc);

                try {
                    idb.insertSklad(sklad);
                } catch (InsertDBService.InsertSkladException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończona niepowodzeniem.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                } catch (InsertDBService.InsertSkladExistsException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończona niepowodzeniem.");
                    alert.setContentText("Operacja nie powiodła się - podany sklad już istnieje.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Dodanie zakończona powodzeniem.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                return;

            }
        });


        //insert trasa
        insertTrasaNumber.setPromptText("2-99");
        insertTrasaNumberOK.setOnMouseClicked(event -> {
            int num = 0;

            try {
                num = Integer.parseInt(insertTrasaNumber.getText());
            } catch(NumberFormatException nfe) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane");
                alert.showAndWait();
                return;
            }

            if(num <= 1 || num >= 100) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane");
                alert.showAndWait();
                return;
            }

            insertTrasaVBox.getChildren().clear();
            insertTrasaStacja.clear();
            for(int i = 0; i < num; i++) {
                HBox hbox = new HBox();
                hbox.setSpacing(10.0);
                hbox.setAlignment(Pos.CENTER_LEFT);

                Label stacjaText = new Label("Stacja:");
                ComboBox<String> stacjaBox = new ComboBox<>();
                stacjaBox.setEditable(true);
                stacjaBox.setItems(allStationsNames);
                insertTrasaStacja.add(stacjaBox);

                hbox.getChildren().addAll(stacjaText, stacjaBox);
                insertTrasaVBox.getChildren().add(hbox);
            }
        });
        insertTrasaNumberClean.setOnMouseClicked(event -> {
            insertTrasaVBox.getChildren().clear();
            insertTrasaStacja.clear();
            insertTrasaNumber.setText(null);
        });

        insertTrasaButton.setOnMouseClicked(event -> {
            ArrayList<Integer> insertStacjeId = new ArrayList<>();
            HashSet<Integer> hashSet = new HashSet<>();
            boolean przyspieszona = insertTrasaCzyPrzyspieszona.isSelected();

            for(ComboBox<String> box : insertTrasaStacja) {
                String s = box.getValue();
                if(s == null || s.isBlank()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Błędne dane.");
                    alert.setContentText("Nie znaleziono stacji.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }

                try {
                    int id = idb.getStationId(s);
                    insertStacjeId.add(id);
                    hashSet.add(id);
                } catch (InsertDBService.NoStationException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Błędne dane.");
                    alert.setContentText("Nie znaleziono stacji.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }
            }

            if(hashSet.size() != insertStacjeId.size()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane.");
                alert.setContentText("Każda stacja może pojawić się maksymalnie raz.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                return;
            }

            Alert info = new Alert(Alert.AlertType.CONFIRMATION);
            info.setTitle("Potwierdź wybór");
            info.setHeaderText("Czy na pewno chcesz dodać tę trasę?");
            info.setContentText("Wszystkie nieistniejące odcinki dostaną automatycznie dodane.");
            info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            Optional<ButtonType> result = info.showAndWait();

            if(result.get() == ButtonType.OK) {
                try {
                    idb.insertTrasa(insertStacjeId, przyspieszona);
                } catch (InsertDBService.InsertTrasaException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończona niepowodzeniem.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                } catch (InsertDBService.InsertTrasaExistsException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończona niepowodzeniem.");
                    alert.setContentText("Operacja nie powiodła się - podana trasa już istnieje.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Dodanie zakończona powodzeniem.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                return;
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

        allWagonyDescriptions.addAll(
                idb.getAllWagony()
                        .stream()
                        .map(Wagon::getDescription)
                        .collect(Collectors.toList()));

        System.out.println("insert db ready");
    }

    public void updateStationNames() {
        allStationsNames.clear();
        allStationsNames.addAll(
                qdb.getAllStations()
                        .stream()
                        .map(Stacja::getNazwaStacji)
                        .collect(Collectors.toList()));
    }

    public void setQueryController(QueryController queryController) {
        this.queryController = queryController;
    }

    public void setDeleteController(DeleteController deleteController) {
        this.deleteController = deleteController;
    }

}
