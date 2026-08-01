package fr.eshome.watersort.ui;

import fr.eshome.watersort.game.Color;
import fr.eshome.watersort.game.FromTo;
import fr.eshome.watersort.game.Tube;
import fr.eshome.watersort.game.WaterSortGame;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * The view of a Tube. It can be selected to receive/give the top's colors
 * from/to another tube.
 */
public class TubeView extends VBox {
    private static final double SEGMENT_WIDTH = 30.0;
    private static final double SEGMENT_HEIGHT = 20.0;

    private final List<javafx.scene.paint.Color> segments;

    private final int my_number;

    private final VBox innerVBox;

    public final BooleanProperty isSelected = new SimpleBooleanProperty(false);

    public TubeView(Tube tube, int number, FromTo fromTo) {
        my_number = number;
        Label labelId = new Label(String.valueOf(number + 1));
        labelId.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-font-style: italic;");
        innerVBox = new VBox();
        getChildren().add(innerVBox);
        getChildren().add(labelId);
        setAlignment(Pos.BOTTOM_CENTER);
        setOnMouseClicked(evt -> {
            if (fromTo.tryStoreId(my_number)) {
                toggleSelect();
            }
        });
        segments = new ArrayList<>();
        settingsForInnerVBox(innerVBox, tube);
        setSpacing(3.0);
        setMinWidth(SEGMENT_WIDTH + 4d);
        setMinHeight(SEGMENT_HEIGHT * 11 + 20d);
    }

    /**
     * Drawing the tube (inner VBox)
     *
     * @param box  the inner VBox
     * @param tube the tube to draw
     */
    private void settingsForInnerVBox(VBox box, Tube tube) {
        box.setAlignment(Pos.BOTTOM_CENTER);
        box.setSpacing(1.0);
        box.setMinWidth(SEGMENT_WIDTH + 2d);
        box.setMinHeight(SEGMENT_HEIGHT * (WaterSortGame.TAILLE_TUBES + 1));
        box.setStyle("-fx-border-color: white;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-background-color: white;");
        // set padding
        box.setPadding(new Insets(0, 1, 2, 1));
        for (Color color : tube.getSegments()) {
            Rectangle rect = new Rectangle(SEGMENT_WIDTH - 1d, SEGMENT_HEIGHT);
            rect.setArcWidth(5d);
            rect.setArcHeight(5d);
            javafx.scene.paint.Color couleur = color.getJavaFXColor();
            rect.setFill(couleur);
            segments.add(couleur);
            box.getChildren().add(rect);
        }
    }

    public void toggleSelect() {
        isSelected.setValue(!isSelected.getValue());
        if (isSelected.getValue()) {
            innerVBox.setStyle("-fx-border-color: blue;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-background-color: white;");
        } else {
            innerVBox.setStyle("-fx-border-color: white;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-background-color: white;");
        }
    }

    public int getNumber() {
        return my_number;
    }


    private String colorName(javafx.scene.paint.Color color) {
        if (javafx.scene.paint.Color.RED.equals(color)) return "RED";
        if (javafx.scene.paint.Color.GREEN.equals(color)) return "GREEN";
        if (javafx.scene.paint.Color.BLUE.equals(color)) return "BLUE";
        if (javafx.scene.paint.Color.CHOCOLATE.equals(color) || javafx.scene.paint.Color.YELLOW.equals(color))
            return "CHOCOLATE";
        if (javafx.scene.paint.Color.ORANGE.equals(color)) return "ORANGE";
        if (javafx.scene.paint.Color.PURPLE.equals(color)) return "PURPLE";

        return color.toString();
    }

    /**
     * Recreates all the TubeViews by checking all the segments of the tube
     *
     * @param tube   a Tube
     * @param toggle set to true if you want to toggle the selection state of this TubeView
     */
    public void refreshUI(Tube tube, boolean toggle) {
        innerVBox.getChildren().clear();
        segments.clear();
        for (Color color : tube.getSegments()) {
            Rectangle rect = new Rectangle(SEGMENT_WIDTH - 1d, SEGMENT_HEIGHT);
            rect.setArcWidth(5d);
            rect.setArcHeight(5d);
            javafx.scene.paint.Color couleur = color.getJavaFXColor();
            rect.setFill(couleur);
            segments.add(couleur);
            innerVBox.getChildren().add(rect);
        }
        if (toggle) toggleSelect();
    }

    @Override
    public String toString() {
        return "TV (" + my_number + ")" + (isSelected.getValue() ? "* [" : " [") + segments.stream()
                .map(this::colorName)
                .toList() + "]";
    }
}
