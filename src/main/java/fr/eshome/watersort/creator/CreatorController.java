package fr.eshome.watersort.creator;

import fr.eshome.watersort.GameApplication;
import fr.eshome.watersort.Switcher;
import fr.eshome.watersort.game.Color;
import fr.eshome.watersort.game.WaterSortGame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    AnchorPane root;

    private TubesArray tubesArray;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preparePane(0, pane1);
        preparePane(1, pane2);
        preparePane(2, pane3);
        preparePane(3, pane4);
        preparePane(4, pane5);
        preparePane(5, pane6);
        tubesArray = new TubesArray(WaterSortGame.NB_TUBES, WaterSortGame.TAILLE_TUBES);
        tubesLocation.getChildren().add(tubesArray.getUI());

    }

    private void preparePane(int id, Pane pane) {
        pane.setStyle("-fx-background-color: " + Color.getHTMLColor(id) + ";");
    }

    /**
     * Change the current color for the segments.
     *
     * @param actionEvent an object storing the source of the event
     */
    public void chooseColor(ActionEvent actionEvent) {
        Object src = actionEvent.getSource();
        if (src instanceof ToggleButton button) {
            // getting the index of the color from the name of the button!
            String id = button.getId().replace("btn", "");
            int index = Integer.parseInt(id);
            if (index > 0 && index <= 6) {
                tubesArray.setCurrentColor(Color.getColor(index - 1));
            } else {
                System.out.println(" index est pas bon : " + index);
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
