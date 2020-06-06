package systeminformacjipasazerskiej.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import systeminformacjipasazerskiej.converter.DayConverter;
import systeminformacjipasazerskiej.db.InsertDBService;
import systeminformacjipasazerskiej.db.QueryDBService;
import systeminformacjipasazerskiej.model.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private Button insertTrasaSwap;
    @FXML
    private Button insertTrasaButton;
    @FXML
    private VBox insertTrasaVBox;
    private ArrayList<ComboBox<String>> insertTrasaStacja = new ArrayList<>();

    @FXML
    private TextField insertPociagName;
    @FXML
    private ComboBox<String> insertPociagType;
    @FXML
    private ComboBox<String> insertPociagOd;
    @FXML
    private ComboBox<String> insertPociagDo;
    @FXML
    private Button insertPociagSearch;
    @FXML
    private ListView<Integer> insertPociagListView;
    @FXML
    private Label insertPociagTrasa;
    @FXML
    private Button insertPociagButton;
    private Integer insertPociagIdTrasy = null;

    @FXML
    private ComboBox<String> insertKursDay;
    @FXML
    private ComboBox<String> insertKursOd;
    @FXML
    private ComboBox<String> insertKursDo;
    @FXML
    private Button insertKursPociagSearch;
    @FXML
    private ListView<Integer> insertKursPociagListView;
    @FXML
    private VBox insertKursPostoje;
    @FXML
    private VBox insertKursSklady;
    @FXML
    private Button insertKursButton;
    private Integer insertKursIdTrasy;
    private Integer insertKursIdPociag;
    private ArrayList<TextField> insertKursPrzyjazd = new ArrayList<>();
    private ArrayList<TextField> insertKursOdjazd = new ArrayList<>();
    private ArrayList<CheckBox> insertKursZmianaSkladu = new ArrayList<>();
    private ArrayList<SkladHBox> insertKursSkladHBox = new ArrayList<>();
    private int insertKursSkladNumber;
    private int insertKursPickedDay;
    private String pat = "^[\\pL\\pN -]+$";

    private ObservableList<String> allStationsNames = FXCollections.observableArrayList();
    private ObservableList<String> allWagonyTypes = FXCollections.observableArrayList("sypialny", "kuszetka", "barowy", "osobowy", "business");
    private ObservableList<String> allWagonyDescriptions = FXCollections.observableArrayList();
    private ObservableList<String> allPociagiTypes = FXCollections.observableArrayList("pospieszny", "ekspres", "pendolino");
    private ObservableList<Integer> allTrasyFromTo = FXCollections.observableArrayList();
    private ObservableList<Integer> allPociagiFromTo = FXCollections.observableArrayList();
    private ObservableList<String> allWagonModels = FXCollections.observableArrayList();

    class TrasaCell extends ListCell<Integer> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("Wybierz");

        public TrasaCell() {
            super();
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(event -> {
                Integer picked = getItem();
                insertPociagIdTrasy = picked;
                allTrasyFromTo.clear();
                insertPociagListView.setVisible(false);

                ArrayList<Stacja> stacje = qdb.getAllStacjeOnTrasa(picked,
                        qdb.getFirstStationFromTrasa(picked), qdb.getLastStationFromTrasa(picked));

                ArrayList<String> stacjeName = (ArrayList<String>) stacje.stream().map(Stacja::getNazwaStacji).collect(Collectors.toList());
                String trasa = "Trasa: " + stacjeName.get(0);
                for(int i = 1; i < stacjeName.size(); i++)
                    trasa += " - " + stacjeName.get(i);

                insertPociagTrasa.setText(trasa);
            });
        }


        protected void updateItem (Integer item, boolean empty){
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
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

    class PociagCell extends ListCell<Integer> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("Wybierz");

        public PociagCell() {
            super();
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(event -> {

                Integer picked = getItem();
                Pociag pociag = qdb.getPociagById(picked);
                Integer day = DayConverter.convertDay(insertKursDay.getValue());

                if(idb.checkKursExistence(picked, day)) {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText("Podany kurs już istnieje.");
                    info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    info.showAndWait();
                    return;
                }

                insertKursIdPociag = picked;
                insertKursIdTrasy = pociag.getIdTrasy();
                insertKursPickedDay = day;
                allPociagiFromTo.clear();
                insertKursPociagListView.setVisible(false);
                ArrayList<Stacja> stacje = qdb.getAllStacjeOnTrasa(insertKursIdTrasy,
                        qdb.getFirstStationFromTrasa(insertKursIdTrasy), qdb.getLastStationFromTrasa(insertKursIdTrasy));

                ArrayList<String> stacjeName = (ArrayList<String>) stacje.stream().map(Stacja::getNazwaStacji).collect(Collectors.toList());
                String name = "Nazwa pociągu: " + pociag.getNazwaPociagu();
                String type = "Typ pociągu: " + pociag.getTypPociagu();

                Label labelName = new Label();
                labelName.setText(name);
                Label labelType = new Label();
                labelType.setText(type);
                Label labelDay = new Label();
                labelDay.setText("Dzień odjazdu ze stacji początkowej: " + insertKursDay.getValue());

                insertKursPostoje.getChildren().clear();
                insertKursPostoje.getChildren().addAll(labelName, labelType, labelDay);
                insertKursPrzyjazd.clear();
                insertKursOdjazd.clear();
                insertKursZmianaSkladu.clear();
                insertKursSklady.getChildren().clear();
                insertKursSkladHBox.clear();

                int i = 0;
                for(String stacja : stacjeName) {
                    HBox hbox = new HBox();
                    hbox.setSpacing(10.0);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    Label stacjaNameLabel = new Label();
                    stacjaNameLabel.setText("           " + stacja);
                    Label stacjaPrzyjazdLabel = new Label();
                    stacjaPrzyjazdLabel.setText("Przyjazd: ");
                    Label stacjaOdjazdLabel = new Label();
                    stacjaOdjazdLabel.setText("Odjazd: ");

                    TextField stacjaPrzyjazdField = new TextField();
                    stacjaPrzyjazdField.setPromptText("__:__");
                    stacjaPrzyjazdField.setMaxWidth(75);
                    TextField stacjaOdjazdField = new TextField();
                    stacjaOdjazdField.setPromptText("__:__");
                    stacjaOdjazdField.setMaxWidth(75);
                    CheckBox stacjaZmiana = new CheckBox();
                    stacjaZmiana.setText("Zmiana składu");

                    if(i == 0)
                        stacjaZmiana.setDisable(true);
                    i++;

                    stacjaZmiana.setOnMouseClicked(event2 -> {
                        System.out.println(stacjaZmiana.isSelected());
                        if(stacjaZmiana.isSelected()) {
                            SkladHBox s = new SkladHBox();
                            insertKursSklady.getChildren().add(s);
                            insertKursSkladHBox.add(s);
                            insertKursSkladNumber++;
                        } else {
                            int size = insertKursSklady.getChildren().size() - 1;
                            insertKursSklady.getChildren().remove(size);
                            insertKursSkladHBox.remove(size);
                            insertKursSkladNumber--;
                        }
                    });

                    hbox.getChildren().addAll(stacjaPrzyjazdLabel, stacjaPrzyjazdField,
                            stacjaOdjazdLabel, stacjaOdjazdField, stacjaZmiana, stacjaNameLabel);
                    insertKursPostoje.getChildren().add(hbox);
                    insertKursPrzyjazd.add(stacjaPrzyjazdField);
                    insertKursOdjazd.add(stacjaOdjazdField);
                    insertKursZmianaSkladu.add(stacjaZmiana);
                }

                SkladHBox s = new SkladHBox();
                insertKursSklady.getChildren().add(s);
                insertKursSkladHBox.add(s);
            });
        }


        protected void updateItem (Integer item, boolean empty){
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                int id_pociagu = item;
                Pociag pociag = qdb.getPociagById(id_pociagu);
                label.setText("Nazwa: " + pociag.getNazwaPociagu() + "    Typ: " + pociag.getTypPociagu());
                setGraphic(hbox);
            }
        }
    }

    class SkladCell extends ListCell<Sklad> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("Wybierz");
        SkladHBox outside;


        public SkladCell(SkladHBox box) {
            super();
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            outside = box;
            button.setOnAction(event -> {
                outside.sklad = getItem();
                outside.label.setText("Skład: wybrano");
            });
        }
        protected void updateItem (Sklad item, boolean empty){
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty) {
                System.out.println("updatingItems");
                ArrayList<Wagon> wagony = item.getListaWagonow();
                ArrayList<String> modele = new ArrayList<>();
                for(Wagon w: wagony) modele.add(w.getModel());
                label.setText("Modele: " + modele.toString() + "   I kl:  " + item.getLiczbaMiejscI() +
                        "   II kl:  " + item.getLiczbaMiejscII());
                setGraphic(hbox);
            }
        }
    }

    class SkladHBox extends HBox {
        Label label = new Label();
        Button wybierz = new Button();
        Button szczegoly = new Button();
        Sklad sklad;

        public SkladHBox() {
            super();
            label.setText("Skład: niewybrano ");
            wybierz.setText("Wybierz");
            szczegoly.setText("Szczegóły");
            getChildren().addAll(label, wybierz, szczegoly);
            setSpacing(10);
            setAlignment(Pos.CENTER_LEFT);
            sklad = null;

            wybierz.setOnMouseClicked(event -> {
                VBox first = new VBox();
                first.setSpacing(10);
                ComboBox<String> insidedeleteSkladBox = new ComboBox<>();
                insidedeleteSkladBox.setEditable(true);
                insidedeleteSkladBox.setPromptText("np. ED250");

                TextField insidedeleteSkladField = new TextField();
                insidedeleteSkladField.setPromptText("1-99");

                ListView<Sklad> insideskladList = new ListView<>();
                insideskladList.setVisible(false);
                ObservableList<Sklad> insideallMatchingSklady = FXCollections.observableArrayList();

                insideskladList.setItems(insideallMatchingSklady);
                allWagonModels.addAll(
                        qdb.getAllWagony()
                                .stream()
                                .map(Wagon::getModel)
                                .distinct()
                                .collect(Collectors.toList()));
                insidedeleteSkladBox.setItems(allWagonModels);

                Button insidedeleteSkladButton = new Button();
                insidedeleteSkladButton.setText("Szukaj");

                HBox one = new HBox();
                one.setSpacing(10);
                one.setAlignment(Pos.CENTER_LEFT);
                HBox two = new HBox();
                two.setSpacing(10);
                two.setAlignment(Pos.CENTER_LEFT);
                HBox three = new HBox();
                three.setSpacing(10);
                three.setAlignment(Pos.CENTER_LEFT);

                Label oneLabel = new Label();
                oneLabel.setText("Liczba różnych modeli: ");
                Label twoLabel = new Label();
                twoLabel.setText("Nazwa modelu występującego: ");
                Label threeLabel = new Label();
                threeLabel.setText("Podanie obu informacji jest opcjonalne, ale zawęża zakres poszukiwań. ");

                one.getChildren().addAll(oneLabel, insidedeleteSkladField);
                two.getChildren().addAll(twoLabel, insidedeleteSkladBox);
                three.getChildren().addAll(threeLabel, insidedeleteSkladButton);
                first.getChildren().addAll(one, two, three, insideskladList);

                insidedeleteSkladButton.setOnMouseClicked(e -> {
                    ArrayList<Sklad> insidesklady;
                    try {
                        if((insidedeleteSkladBox.getValue() == null || insidedeleteSkladBox.getValue().equals("") ) &&
                                insidedeleteSkladField.getText().equals("")) {
                            throw new DeleteController.NoGivenDataException();
                        }
                        if(insidedeleteSkladBox.getValue() != null && !insidedeleteSkladBox.getValue().isBlank() && !insidedeleteSkladBox.getValue().matches(pat)) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Błędne dane.");
                            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                            alert.showAndWait();
                            return;
                        }
                        else if(insidedeleteSkladBox.getValue() == null || insidedeleteSkladBox.getValue().equals("")) {
                            insidesklady = qdb.getSkladByNumber(Integer.parseInt(insidedeleteSkladField.getText()));
                        }
                        else if(insidedeleteSkladField.getText().equals("")) {
                            insidesklady = qdb.getSkladByNumber(insidedeleteSkladBox.getValue());
                        }
                        else insidesklady = qdb.getSkladByNumber(Integer.parseInt(insidedeleteSkladField.getText()), insidedeleteSkladBox.getValue());
                        insideallMatchingSklady.clear();
                        insideallMatchingSklady.addAll(insidesklady);

                        insideskladList.setVisible(true);

                        insideskladList.setOnMouseClicked(click -> {
                            Sklad insidesklad = insideskladList.getSelectionModel().getSelectedItem();


                            Label insideskladInfo =
                                    new Label("\nWłaściwości składu:" +
                                            "\nLiczba miejsc w I klasie: " + insidesklad.getLiczbaMiejscI() +
                                            "\nLiczba miejsc w II klasie: " + insidesklad.getLiczbaMiejscII() +
                                            "\nLiczba miejsc dla rowerów: " + insidesklad.getLiczbaMiejscDlaRowerow() +
                                            "\nMiejsca dla niepełnosprawnych: " + printBool(insidesklad.checkIfNiepelnosprawni()) +
                                            "\nKlimatyzacja: " + printBool(insidesklad.checkIfKlimatyzacja()) +
                                            "\nWiFi: " + printBool(insidesklad.checkIfWifi())
                                    );
                            ArrayList<Wagon> insidewagony = insidesklad.getListaWagonow();
                            ArrayList<Integer> insideliczba = insidesklad.getLiczbaWagonow();
                            int insidecnt = 0;
                            for(Wagon w: insidewagony) {
                                insideskladInfo = new Label(insideskladInfo.getText() + "\n\nModel: " + w.getModel()
                                        +"\nLiczba wagonów: " + insideliczba.get(insidecnt++)
                                        +"\nTyp wagonu: " + w.getTyp() +
                                        "\nLiczba miejsc w I klasie: " + w.getMiejscaI() +
                                        "\nLiczba miejsc w II klasie: " + w.getMiejscaII() +
                                        "\nLiczba miejsc dla rowerów: " + w.getRowery() +
                                        "\nMiejsca dla niepełnosprawnych: " + printBool(w.isCzyNiepelnosprawni()) +
                                        "\nPrzedziałowy: " + printBool(w.isCzyPrzedzialowy()) +
                                        "\nKlimatyzacja: " + printBool(w.isCzyKlimatyzacja()) +
                                        "\nWiFi: " + printBool(w.isCzyWifi())
                                );
                            }


                            VBox popupContent = new VBox();
                            popupContent.getChildren().addAll(insideskladInfo);
                            popupContent.setSpacing(20);
                            ScrollPane scroll = new ScrollPane(popupContent);
                            Dialog<Kurs> kursDialog = new Dialog<>();
                            kursDialog.getDialogPane().setMinWidth(600);
                            kursDialog.getDialogPane().setContent(scroll);
                            kursDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                            kursDialog.showAndWait();
                        });


                    } catch (QueryDBService.NoSuchSkladException ex) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Nie ma takiego składu.");
                        alert.showAndWait();
                    }
                    catch (NumberFormatException nfe) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Zła liczba.");
                        alert.showAndWait();
                    }
                    catch (DeleteController.NoGivenDataException ngd) {}
                });
                insideskladList.setCellFactory(param -> new SkladCell(this));
                insideallMatchingSklady.addListener((ListChangeListener<Sklad>) change ->
                        insideskladList.setMaxHeight(insideallMatchingSklady.size() * 34 + 2)
                );
                insideskladList.setMinWidth(500);

                VBox popupContent2 = new VBox();
                popupContent2.getChildren().addAll(first);
                popupContent2.setSpacing(20);
                ScrollPane scroll2 = new ScrollPane(popupContent2);
                Dialog<Kurs> kursDialog2 = new Dialog<>();
                kursDialog2.getDialogPane().setPrefSize(800,600);
                kursDialog2.getDialogPane().setContent(scroll2);
                kursDialog2.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                kursDialog2.showAndWait();
            });

            szczegoly.setOnMouseClicked(event -> {
                if(sklad == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Nie wybrano żadnego składu");
                    alert.showAndWait();
                    return;
                }

                Label skladInfo =
                        new Label("\nWłaściwości składu:" +
                                "\nLiczba miejsc w I klasie: " + sklad.getLiczbaMiejscI() +
                                "\nLiczba miejsc w II klasie: " + sklad.getLiczbaMiejscII() +
                                "\nLiczba miejsc dla rowerów: " + sklad.getLiczbaMiejscDlaRowerow() +
                                "\nMiejsca dla niepełnosprawnych: " + printBool(sklad.checkIfNiepelnosprawni()) +
                                "\nKlimatyzacja: " + printBool(sklad.checkIfKlimatyzacja()) +
                                "\nWiFi: " + printBool(sklad.checkIfWifi())
                        );
                ArrayList<Wagon> wagony = sklad.getListaWagonow();
                ArrayList<Integer> liczba = sklad.getLiczbaWagonow();
                int cnt = 0;
                for(Wagon w: wagony) {
                    skladInfo = new Label(skladInfo.getText() + "\n\nModel: " + w.getModel()
                            +"\nLiczba wagonów: " + liczba.get(cnt++)
                            +"\nTyp wagonu: " + w.getTyp() +
                            "\nLiczba miejsc w I klasie: " + w.getMiejscaI() +
                            "\nLiczba miejsc w II klasie: " + w.getMiejscaII() +
                            "\nLiczba miejsc dla rowerów: " + w.getRowery() +
                            "\nMiejsca dla niepełnosprawnych: " + printBool(w.isCzyNiepelnosprawni()) +
                            "\nPrzedziałowy: " + printBool(w.isCzyPrzedzialowy()) +
                            "\nKlimatyzacja: " + printBool(w.isCzyKlimatyzacja()) +
                            "\nWiFi: " + printBool(w.isCzyWifi())
                    );
                }


                VBox popupContent = new VBox();
                popupContent.getChildren().addAll(skladInfo);
                popupContent.setSpacing(20);
                ScrollPane scroll = new ScrollPane(popupContent);
                Dialog<Kurs> kursDialog = new Dialog<>();
                kursDialog.getDialogPane().setMinWidth(400);
                kursDialog.getDialogPane().setContent(scroll);
                kursDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                kursDialog.showAndWait();
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        insertStationName.setItems(allStationsNames);
        insertStationName.setPromptText("np. Tarnów, max 100 znaków");
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



            if(nazwa == null || nazwa.isBlank() || !nazwa.matches(pat))
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

            if(start == null || end == null || start.isBlank() || end.isBlank() ||
                !start.matches(pat) || !end.matches(pat)) {
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
        insertWagonName.setPromptText("np. 110A, max 15 znaków");
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

            if(nazwa == null || nazwa.isBlank() || !nazwa.matches(pat))
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
                info.setHeaderText("Dodawanie zakończone niepowodzeniem.");
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
                deleteController.updateModelNames();
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText("Dodanie zakończone powodzeniem.");
                info.showAndWait();
                updateWagonDescriptions();
                deleteController.updateModelNames();
                deleteController.hideTable();
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
                    alert.setHeaderText("Dodanie zakończone niepowodzeniem.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                } catch (InsertDBService.InsertSkladExistsException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończone niepowodzeniem.");
                    alert.setContentText("Operacja nie powiodła się - podany sklad już istnieje.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Dodanie zakończone powodzeniem.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                deleteController.hideTable();
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
        insertTrasaSwap.setOnMouseClicked(event -> {
            int trasaSize = insertTrasaStacja.size();
            ArrayList<String> stationList = new ArrayList<>();
            for(ComboBox<String> box : insertTrasaStacja) {
                stationList.add(box.getValue());
            }
            for(int i = 0; i < trasaSize; i++) {
                insertTrasaStacja.get(i).setValue(stationList.get(trasaSize-1-i));
            }
        });
        insertTrasaButton.setOnMouseClicked(event -> {
            ArrayList<Integer> insertStacjeId = new ArrayList<>();
            HashSet<Integer> hashSet = new HashSet<>();
            boolean przyspieszona = insertTrasaCzyPrzyspieszona.isSelected();

            for(ComboBox<String> box : insertTrasaStacja) {
                String s = box.getValue();
                if(s == null || s.isBlank() || !s.matches(pat)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Błędne dane.");
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
            info.setContentText("Wszystkie nieistniejące odcinki zostaną automatycznie dodane.");
            info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            Optional<ButtonType> result = info.showAndWait();

            if(result.get() == ButtonType.OK) {
                try {
                    idb.insertTrasa(insertStacjeId, przyspieszona);
                } catch (InsertDBService.InsertTrasaException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończone niepowodzeniem.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                } catch (InsertDBService.InsertTrasaExistsException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończone niepowodzeniem.");
                    alert.setContentText("Operacja nie powiodła się - podana trasa już istnieje.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Dodanie zakończone powodzeniem.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                queryController.hideTable();
                deleteController.hideTable();
                return;
            }
        });

        //insert pociag
        insertPociagName.setPromptText("np. Wyspiański");
        insertPociagType.setItems(allPociagiTypes);
        insertPociagOd.setPromptText("np. Przemyśl Główny");
        insertPociagOd.setItems(allStationsNames);
        insertPociagDo.setPromptText("np. Wrocław Główny");
        insertPociagDo.setItems(allStationsNames);
        insertPociagType.getSelectionModel().select(0);


        insertPociagSearch.setOnMouseClicked(event -> {
            insertPociagListView.setVisible(false);
            allTrasyFromTo.clear();
            String odName = insertPociagOd.getValue();
            String doName = insertPociagDo.getValue();
            if(odName == null || odName.isBlank() || !odName.matches(pat) ||
                    doName == null || doName.isBlank() || !doName.matches(pat)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                return;
            }

            try {
                ArrayList<Integer> trasa_id = idb.getTrasaIdExactlyFromTo(odName, doName);
                allTrasyFromTo.addAll(trasa_id);
                insertPociagListView.setItems(allTrasyFromTo);
                insertPociagListView.setCursor(Cursor.HAND);

                insertPociagListView.setOnMouseClicked(event2 -> {
                    Integer id_trasy = insertPociagListView.getSelectionModel().getSelectedItem();

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

                insertPociagListView.setVisible(true);

            } catch (InsertDBService.NoStationException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono danej stacji.");
                alert.showAndWait();
            } catch (InsertDBService.NoMatchingTrasyException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono danej trasy.");
                alert.showAndWait();
            }
        });

        insertPociagListView.setItems(allTrasyFromTo);
        insertPociagListView.setCursor(Cursor.HAND);
        insertPociagListView.setVisible(false);
        insertPociagListView.setCellFactory(param -> new TrasaCell());
        allTrasyFromTo.addListener((ListChangeListener<Integer>) change ->
                insertPociagListView.setMaxHeight(allTrasyFromTo.size() * 34 + 2));

        insertPociagButton.setOnMouseClicked(event -> {
            String name = insertPociagName.getText();
            String type = insertPociagType.getValue();

            if(name == null || name.isBlank() || !name.matches(pat)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                return;
            }

            if(insertPociagIdTrasy == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane.");
                alert.setContentText("Nie wybrano trasy");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                return;
            }

            if(idb.checkPociagExistence(name)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane.");
                alert.setContentText("Podany pociąg już istnieje");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                return;
            }

            Alert info = new Alert(Alert.AlertType.CONFIRMATION);
            info.setTitle("Potwierdź wybór");
            info.setHeaderText("Czy na pewno chcesz dodać ten pociąg?");
            info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            Optional<ButtonType> result = info.showAndWait();

            if(result.get() == ButtonType.OK) {
                Pociag pociag = new Pociag();
                pociag.setIdTrasy(insertPociagIdTrasy);
                pociag.setNazwaPociagu(name);
                pociag.setTypPociagu(type);
                insertPociagIdTrasy = null;
                insertPociagListView.setVisible(false);
                insertPociagTrasa.setText("Trasa: ");

                try {
                    idb.insertPociag(pociag);
                } catch (InsertDBService.InsertPociagException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończone niepowodzeniem.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }
                deleteController.updatePociagNames();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Dodanie zakończone powodzeniem.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();

            }
        });

        //insert kurs
        insertKursDay.setItems(FXCollections.observableArrayList(
                Stream.iterate(0, i -> i+1)
                        .limit(7)
                        .map(DayConverter::convertDay)
                        .collect(Collectors.toList())
        ));
        insertKursDay.getSelectionModel().select(0);
        insertKursOd.setItems(allStationsNames);
        insertKursOd.setPromptText("np. Przemyśl Główny");
        insertKursDo.setItems(allStationsNames);
        insertKursDo.setPromptText("np. Wrocław Główny");

        insertKursPociagSearch.setOnMouseClicked(event -> {
            insertKursPociagListView.setVisible(false);
            allPociagiFromTo.clear();
            String odName = insertKursOd.getValue();
            String doName = insertKursDo.getValue();
            if(odName == null || odName.isBlank() || !odName.matches(pat) ||
                    doName == null || doName.isBlank() || !doName.matches(pat)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Błędne dane.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                return;
            }

            try {
                ArrayList<Integer> trasa_id = idb.getPociagIdExactlyFromTo(odName, doName);
                allPociagiFromTo.addAll(trasa_id);
                insertKursPociagListView.setItems(allPociagiFromTo);
                insertKursPociagListView.setCursor(Cursor.HAND);

                insertKursPociagListView.setOnMouseClicked(event2 -> {
                    Integer id_pociagu = insertKursPociagListView.getSelectionModel().getSelectedItem();
                    Integer id_trasy = qdb.getPociagById(id_pociagu).getIdTrasy();

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

                insertKursPociagListView.setVisible(true);

            } catch (InsertDBService.NoStationException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono danej stacji.");
                alert.showAndWait();
            } catch (InsertDBService.NoMatchingTrasyException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Nie znaleziono pociągu na danej trasie.");
                alert.showAndWait();
            }
        });

        insertKursPociagListView.setItems(allTrasyFromTo);
        insertKursPociagListView.setCursor(Cursor.HAND);
        insertKursPociagListView.setVisible(false);
        insertKursPociagListView.setCellFactory(param -> new PociagCell());
        allPociagiFromTo.addListener((ListChangeListener<Integer>) change ->
                insertKursPociagListView.setMaxHeight(allPociagiFromTo.size() * 34 + 2));

        insertKursButton.setOnMouseClicked(event -> {
            if(insertKursIdPociag == null)
                return;

            String pattern = "^([0-1]\\d|2[0-3]):([0-5]\\d)$";
            ArrayList<String> przyjazd = new ArrayList<>();
            ArrayList<String> odjazd = new ArrayList<>();
            ArrayList<Integer> nextSklad = new ArrayList<>();

            for(TextField t : insertKursPrzyjazd) {
                if(!t.getText().matches(pattern)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Niepoprawna godzina.");
                    alert.showAndWait();
                    return;
                }
                przyjazd.add(t.getText());
            }
            for(TextField t : insertKursOdjazd) {
                if(!t.getText().matches(pattern)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Niepoprawna godzina.");
                    alert.showAndWait();
                    return;
                }
                odjazd.add(t.getText());
            }
            for(SkladHBox s : insertKursSkladHBox) {
                Sklad sklad = s.sklad;
                if(sklad == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Nie wybrano wszystkich składów.");
                    alert.showAndWait();
                    return;
                }
            }
            int size = insertKursSkladHBox.size();
            for(int i = 1; i < size; i++) {
                Integer fir = insertKursSkladHBox.get(i-1).sklad.getIdSkladu();
                Integer sec = insertKursSkladHBox.get(i).sklad.getIdSkladu();
                if(fir.equals(sec)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Każde dwa kolejne składy muszą by różne.");
                    alert.showAndWait();
                    return;
                }
            }
            int stacjeSize = przyjazd.size();
            int ptr = 0;
            for(int i = 0; i < stacjeSize; i++) {
                if(insertKursZmianaSkladu.get(i).isSelected())
                    ptr++;
                nextSklad.add(insertKursSkladHBox.get(ptr).sklad.getIdSkladu());
            }

            System.out.println(nextSklad);
            Alert info = new Alert(Alert.AlertType.CONFIRMATION);
            info.setTitle("Potwierdź wybór");
            info.setHeaderText("Czy na pewno chcesz dodać ten kurs?");
            if(idb.checkKursLength(przyjazd, odjazd))
                info.setContentText("Uwaga: próba dodania kursu trwającego ponad jedną dobę.");
            info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            Optional<ButtonType> result = info.showAndWait();
            if(result.get() == ButtonType.OK) {
                try {
                    idb.insertKurs(insertKursIdPociag, insertKursPickedDay, przyjazd, odjazd, nextSklad);
                } catch (InsertDBService.InsertKursOverflowException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończone niepowodzeniem.");
                    alert.setContentText("Nastąpiło przepełnienie stacji");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                } catch (InsertDBService.InsertKursLengthException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończone niepowodzeniem.");
                    alert.setContentText("Za długie składy");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                } catch (InsertDBService.InsertKursException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Dodanie zakończone niepowodzeniem.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Dodanie zakończone powodzeniem.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                queryController.hideTable();
                deleteController.hideTable();
                insertKursIdPociag = null;
                insertKursPostoje.getChildren().clear();
                insertKursPrzyjazd.clear();
                insertKursOdjazd.clear();
                insertKursZmianaSkladu.clear();
                insertKursSklady.getChildren().clear();
                insertKursSkladHBox.clear();

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

    public void updateWagonDescriptions() {
        allWagonyDescriptions.clear();
        allWagonyDescriptions.addAll(
                idb.getAllWagony()
                        .stream()
                        .map(Wagon::getDescription)
                        .collect(Collectors.toList()));
    }

    public void hideTable() {
        insertKursPociagListView.setMaxHeight(0);
        insertKursPociagListView.setVisible(false);
        insertPociagListView.setMaxHeight(0);
        insertPociagListView.setVisible(false);
        insertKursIdPociag = null;
        insertKursPostoje.getChildren().clear();
        insertKursPrzyjazd.clear();
        insertKursOdjazd.clear();
        insertKursZmianaSkladu.clear();
        insertKursSklady.getChildren().clear();
        insertKursSkladHBox.clear();
    }

    public void setQueryController(QueryController queryController) {
        this.queryController = queryController;
    }

    public void setDeleteController(DeleteController deleteController) {
        this.deleteController = deleteController;
    }

    private String printBool(boolean b) {
        return b ? "Tak" : "Nie";
    }
}
