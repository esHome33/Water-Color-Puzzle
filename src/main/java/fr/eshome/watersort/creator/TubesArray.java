package fr.eshome.watersort.creator;

import fr.eshome.watersort.game.Tube;
import fr.eshome.watersort.game.WaterSortGame;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is a UI container (HBox) for FillableTubes. It contains a list of FillableTubes.
 * Under each FillableTube, there are two buttons to add or remove the current color.
 */
public class TubesArray {
    private Color currentColor;
    private final ArrayList<FillableTube> tubes;
    private final int capacity;
    /**
     * The stage is an HBox that contains a VBox made of a FillableTube and an HBox containing two buttons.
     */
    private final HBox stage;

    /**
     * A map containing the number of segments for each color.
     */
    public HashMap<Integer, SimpleIntegerProperty> nbSegments;

    /**
     * A property indicating if the configuration of the different colors is OK (true if
     * the number of all colors is minus or equal to the capacity)
     */
    public final SimpleBooleanProperty configuration_OK = new SimpleBooleanProperty(false);

    public TubesArray(int numberOfTubes, int capacity) {
        double btnSize = 28d;
        tubes = new ArrayList<>(numberOfTubes);
        initializeSegments(numberOfTubes);
        stage = new HBox();
        stage.setMinHeight(250d);
        stage.setPrefHeight(250d);
        stage.setMinWidth(43d * numberOfTubes);
        stage.setPrefWidth(46d * numberOfTubes);
        stage.setAlignment(Pos.BOTTOM_CENTER);
        stage.setSpacing(5d);
        this.capacity = capacity;
        currentColor = Color.RED;
        for (int i = 0; i < numberOfTubes; i++) {
            VBox unit = new VBox();
            FillableTube tube = new FillableTube(i, capacity);
            tubes.add(tube);
            unit.setSpacing(10d);
            unit.setMinWidth(2 * btnSize + 5d);
            unit.setMinHeight(180d);
            unit.setPrefHeight(200d);
            //unit.setStyle("-fx-border-color: blue;");
            unit.setAlignment(Pos.BOTTOM_CENTER);
            // add the Tube UI to the unit
            unit.getChildren().add(tube.getUI());
            HBox buttonContainer = new HBox(5);
            buttonContainer.setAlignment(Pos.CENTER);
            buttonContainer.setStyle("-fx-border-color: black;");
            Button btnAdd = new Button("+");
            btnAdd.setMinWidth(btnSize);
            btnAdd.setMinHeight(btnSize);
            btnAdd.setPrefWidth(btnSize);
            btnAdd.setPrefHeight(btnSize);
            btnAdd.setOnAction(e -> {
                tube.addColor(currentColor);
                countColors();
            });
            Button btnDel = new Button("-");
            btnDel.setOnAction(e -> {
                tube.removeColor();
                countColors();
            });
            btnDel.setMinWidth(btnSize);
            btnDel.setMinHeight(btnSize);
            btnDel.setPrefWidth(btnSize);
            btnDel.setPrefHeight(btnSize);
            buttonContainer.getChildren().add(btnAdd);
            buttonContainer.getChildren().add(btnDel);
            // add the button container to the unit
            unit.getChildren().add(buttonContainer);
            // add the unit to the stage
            stage.getChildren().add(unit);
            HBox.setMargin(stage, new Insets(5, 5, 20, 5));
        }
    }

    private void initializeSegments(int numberOfTubes) {
        nbSegments = new HashMap<>();
        for (int i = 0; i < numberOfTubes; i++) {
            nbSegments.put(i, new SimpleIntegerProperty(0));
        }
    }

    private void countColors() {
        // reset all counters
        for (int i = 0; i < nbSegments.size(); i++) {
            nbSegments.get(i).set(0);
        }
        // count each color in each tube
        for (FillableTube tube : tubes) {
            tube.countColors(nbSegments);
        }
        // check if the configuration is OK
        boolean configOK = true;
        for (int i = 0; i < nbSegments.size(); i++) {
            if (capacity < nbSegments.get(i).get()) {
                configOK = false;
                break;
            }
        }
        configuration_OK.set(configOK);
    }

    public void setCurrentColor(Color color) {
        currentColor = color;
    }

    public HBox getUI() {
        return stage;
    }

    public String saveTubes() {
        StringBuilder resu = new StringBuilder("{\"tubes\":[");
        String endTubes = "]";
        for (FillableTube tube : tubes) {
            resu.append(tube.toJSON());
            resu.append(",");
        }
        resu.deleteCharAt(resu.length() - 1);
        resu.append(endTubes);
        resu.append(",");
        resu.append("\"num_of_tubes\":");
        resu.append(tubes.size());
        resu.append(",");
        resu.append("\"tube_capacity\":");
        resu.append(capacity);
        resu.append(",");
        resu.append("\"is_solved\":false,");
        resu.append("\"nbCoups\":0}");

        return resu.toString();
    }

    /**
     * Copies all the segments from a given water sort game.
     *
     * @param game The WaterSortGame instance containing the tubes to be initialized with.
     *             The game's tubes will be used to populate this TubesArray's tubes.
     */
    public void initWithGame(WaterSortGame game) {
        ArrayList<Tube> gameTubes = game.getTubes();
        for (int i = 0; i < gameTubes.size(); i++) {
            FillableTube tube = tubes.get(i);
            Tube gameTube = gameTubes.get(i);
            tube.replaceWith(gameTube.getSegments().reversed());
        }
        // refresh the segment's count
        countColors();
    }
}
