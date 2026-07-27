package fr.eshome.watersort.creator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;

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

    public TubesArray(int numberOfTubes, int capacity) {
        tubes = new ArrayList<>(numberOfTubes);
        stage = new HBox();
        stage.setMinHeight(300d);
        stage.setPrefHeight(300d);
        stage.setAlignment(Pos.BOTTOM_CENTER);
        stage.setSpacing(5d);
        this.capacity = capacity;
        currentColor = Color.RED;
        for (int i = 0; i < numberOfTubes; i++) {
            VBox unit = new VBox();
            FillableTube tube = new FillableTube(i, capacity);
            tubes.add(tube);
            unit.setSpacing(10d);
            unit.setMinWidth(50d);
            unit.setMinHeight(200d);
            unit.setStyle("-fx-border-color: blue;");
            // add the Tube UI to the unit
            unit.getChildren().add(tube.getUI());
            HBox buttonContainer = new HBox(5);
            buttonContainer.setAlignment(Pos.CENTER);
            buttonContainer.setStyle("-fx-border-color: black;");
            Button btnAdd = new Button("+");
            btnAdd.setOnAction(e -> tube.addColor(currentColor));
            Button btnDel = new Button("-");
            btnDel.setOnAction(e -> tube.removeColor());
            buttonContainer.getChildren().add(btnAdd);
            buttonContainer.getChildren().add(btnDel);
            // add the button container to the unit
            unit.getChildren().add(buttonContainer);
            // add the unit to the stage
            stage.getChildren().add(unit);
            HBox.setMargin(stage, new Insets(5, 5, 20, 5));
        }
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
}
