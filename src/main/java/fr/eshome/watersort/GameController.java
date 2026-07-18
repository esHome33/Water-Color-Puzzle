package fr.eshome.watersort;

import fr.eshome.watersort.game.WaterSortGame;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    private WaterSortGame game;

    @FXML
    public Pane colorProp;

    @FXML
    private Label welcomeText;

    @FXML
    private HBox tubesContainer;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        game = new WaterSortGame(tubesContainer, colorProp);
        welcomeText.setText("game " + game + " solved ? " + (game.isSolved() ? " oui" : " non"));
    }
}
