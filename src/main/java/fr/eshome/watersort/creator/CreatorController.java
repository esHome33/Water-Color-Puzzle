package fr.eshome.watersort.creator;

import fr.eshome.watersort.GameApplication;
import fr.eshome.watersort.Switcher;
import fr.eshome.watersort.game.Color;
import fr.eshome.watersort.game.WaterSortGame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreatorController implements Initializable {

    @FXML
    public Button backButton;
    @FXML
    public Button useThisGameButton;
    @FXML
    public Pane pane1;
    @FXML
    public Pane pane2;
    @FXML
    public Pane pane3;
    @FXML
    public Pane pane4;
    @FXML
    public Pane pane5;
    @FXML
    public Pane pane6;
    @FXML
    public HBox tubesLocation;
    @FXML
    public Label nbCol1;
    @FXML
    public Label nbCol2;
    @FXML
    public Label nbCol3;
    @FXML
    public Label nbCol4;
    @FXML
    public Label nbCol5;
    @FXML
    public Label nbCol6;
    @FXML
    public AnchorPane root;

    private TubesArray tubesArray;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preparePane(0, pane1);
        preparePane(1, pane2);
        preparePane(2, pane3);
        preparePane(3, pane4);
        preparePane(4, pane5);
        preparePane(5, pane6);
    }

    private void preparePane(int id, Pane pane) {
        pane.setStyle("-fx-background-color: " + Color.getHTMLColor(id) + ";");
        pane.setUserData(id); // to retrieve the color index when the parent ToggleButton is clicked
    }

    /**
     * Load the initial colors from a given water sort game.
     *
     * @param game The WaterSortGame instance containing the tubes to be initialized with.
     */
    public void initWithGame(WaterSortGame game) {
        tubesArray = new TubesArray(WaterSortGame.NB_TUBES, WaterSortGame.TAILLE_TUBES);
        tubesArray.initWithGame(game);
        tubesLocation.getChildren().clear();
        tubesLocation.getChildren().add(tubesArray.getUI());
        mapLabelsWithTubes();
        useThisGameButton.disableProperty().bind(tubesArray.configuration_OK.not());
    }

    /**
     * Automatically change the labels when the number of segments of each color changes.
     */
    private void mapLabelsWithTubes() {
        if (tubesArray != null && tubesArray.nbSegments != null) {
            nbCol1.textProperty().bind(tubesArray.nbSegments.get(0).asString());
            nbCol2.textProperty().bind(tubesArray.nbSegments.get(1).asString());
            nbCol3.textProperty().bind(tubesArray.nbSegments.get(2).asString());
            nbCol4.textProperty().bind(tubesArray.nbSegments.get(3).asString());
            nbCol5.textProperty().bind(tubesArray.nbSegments.get(4).asString());
            nbCol6.textProperty().bind(tubesArray.nbSegments.get(5).asString());
        }
    }


    /**
     * Change the current color for the segments.
     *
     * @param actionEvent an object storing the source of the event
     */
    public void chooseColor(ActionEvent actionEvent) {
        Object src = actionEvent.getSource();
        if (src instanceof ToggleButton button) {
            // getting the index of the color from the child pane's userData!
            Node childNode = button.getChildrenUnmodifiable().getFirst();
            if (childNode instanceof Pane p) {
                int index = (int) p.getUserData();
                if (index >= 0 && index < 6) {
                    tubesArray.setCurrentColor(Color.getColor(index));
                }
            }
        }
    }

    @FXML
    public void onBack() {
        try {
            // restart a new game with random tubes.
            Switcher.setStartWithRandom();
            FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("main-view.fxml"));
            backButton.getScene().setRoot(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Resource file \"main-view.fxml\" not found");
        }
    }

    /**
     * The user wants to use the current FillableTubes configuration to create a new game.
     * So, store all tubes in a file and memorize it to initialize a new WaterSortGame and
     * go back to the main view.
     */
    public void onCreateGame() {
        String jsonTubesArray = tubesArray.saveTubes();
        saveToFile(jsonTubesArray);
        // restart a new game with saved tubes.
        Switcher.setStartWithSaved();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("main-view.fxml"));
            backButton.getScene().setRoot(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Resource file \"main-view.fxml\" not found");
        }
    }

    private void saveToFile(String jsonTubesArray) {
        try {
            File tempDir = new File(System.getProperty("user.home"), "waterdrop");
            FileWriter writer = new FileWriter(new File(tempDir, WaterSortGame.NEW_GAME_FILE_NAME));
            writer.write(jsonTubesArray);
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save to file \"" + WaterSortGame.NEW_GAME_FILE_NAME + "\".");
        }
    }
}
