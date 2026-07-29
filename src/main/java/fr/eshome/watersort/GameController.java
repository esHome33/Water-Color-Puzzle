package fr.eshome.watersort;

import fr.eshome.watersort.game.WaterSortGame;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    private WaterSortGame game;

    @FXML
    public Label txtCoups;
    @FXML
    public ImageView winner;
    @FXML
    public ListView<String> stats_lv;
    @FXML
    public Pane colorProp;
    @FXML
    private HBox tubesContainer;

    @FXML
    private TitledPane titledPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Switcher.startWithRandom) {
            game = WaterSortGame.createGameWithRandomTubes(tubesContainer, colorProp);
        } else {
            game = WaterSortGame.createFromSavedGame(tubesContainer, colorProp);
        }
        initUI();
    }

    public void suiviSolution(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
        boolean solved = game.isSolved();
        winner.setVisible(solved);
        winner.setManaged(solved);
    }

    public void onNewGame() {
        game.solvedState.removeListener(this::suiviSolution);
        game = WaterSortGame.createGameWithRandomTubes(tubesContainer, colorProp);
        initUI();
    }

    private void initUI() {
        game.solvedState.addListener(this::suiviSolution);
        winner.setVisible(false);
        winner.setManaged(false);
        tubesContainer.minWidth(300d);
        tubesContainer.minHeight(400d);
        colorProp.setStyle("-fx-background-color: grey;");
        stats_lv.getItems().clear();
        stats_lv.getItems().addAll(game.getStats());
        txtCoups.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    int value = game.nbCoups.get();
                    return 1 == value ? "1 coup" : value + " coups";
                },
                game.nbCoups
        ));
        titledPane.setText("Statistiques (" + game.getStatsRuptures() + ")");
    }

    public void onRestartSameGame() {
        try {
            game.returnToStartState();
            winner.setVisible(false);
            winner.setManaged(false);
            stats_lv.getItems().clear();
            stats_lv.getItems().addAll(game.getStats());
        } catch (IOException e) {
            System.out.println("Error loading game state: " + e.getMessage());
        }
    }

    /**
     * Launches the creator window.
     */
    public void onCreatorLaunch() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("creator-view.fxml"));
            tubesContainer.getScene().setRoot(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Error loading creator window: " + e.getMessage());
        }
    }


}
