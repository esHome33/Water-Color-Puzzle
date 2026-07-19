package fr.eshome.watersort;

import fr.eshome.watersort.game.WaterSortGame;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    private WaterSortGame game;

    @FXML
    public ImageView winner;
    @FXML
    public Pane colorProp;
    @FXML
    private HBox tubesContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        game = new WaterSortGame(tubesContainer, colorProp);
        game.solvedState.addListener(this::suiviSolution);
        winner.setVisible(false);
        tubesContainer.minWidth(300d);
        tubesContainer.minHeight(400d);
        colorProp.setStyle("-fx-background-color: grey;");
    }

    public void suiviSolution(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
        winner.setVisible(game.isSolved());
    }

    public void onReloadButtonClick() {
        game.solvedState.removeListener(this::suiviSolution);
        game = WaterSortGame.newGame(tubesContainer, colorProp);
        game.solvedState.addListener(this::suiviSolution);
        winner.setVisible(false);
        tubesContainer.minWidth(300d);
        tubesContainer.minHeight(400d);
    }
}
