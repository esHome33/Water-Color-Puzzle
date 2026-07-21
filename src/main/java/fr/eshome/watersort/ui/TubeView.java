package fr.eshome.watersort.ui;

import fr.eshome.watersort.game.Color;
import fr.eshome.watersort.game.FromTo;
import fr.eshome.watersort.game.Tube;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * The view of a Tube. It can be selected in order to receive colors or to
 * give its own top color to another tube.
 */
public class TubeView extends VBox {
    private static final double SEGMENT_WIDTH = 30.0;
    private static final double SEGMENT_HEIGHT = 20.0;

    private final List<javafx.scene.paint.Color> segments;

    private final int my_number;

    public final BooleanProperty isSelected = new SimpleBooleanProperty(false);

    public TubeView(Tube tube, int number, FromTo fromTo) {
        my_number = number;
        setAlignment(Pos.BOTTOM_CENTER);
        setOnMouseClicked(_ -> {
            if (fromTo.tryStoreId(my_number)) {
                toggleSelect();
            }
        });
        setSpacing(1.0);
        setMinWidth(SEGMENT_WIDTH + 2d);
        setMinHeight(SEGMENT_HEIGHT * 11 + 1d);
        setStyle("-fx-border-color: white;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-background-color: white;");
        segments = new ArrayList<>();
        for (Color color : tube.getSegments()) {
            Rectangle rect = new Rectangle(SEGMENT_WIDTH, SEGMENT_HEIGHT);
            rect.setArcWidth(5d);
            rect.setArcHeight(5d);
            javafx.scene.paint.Color couleur = getColor(color);
            rect.setFill(couleur);
            segments.add(couleur);
            getChildren().add(rect);
        }
    }

    public void toggleSelect() {
        isSelected.setValue(!isSelected.getValue());
        if (isSelected.getValue()) {
            setStyle("-fx-border-color: blue;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-background-color: white;");
        } else {
            setStyle("-fx-border-color: white;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-background-color: white;");
        }
    }

    public int getNumber() {
        return my_number;
    }

    private javafx.scene.paint.Color getColor(Color color) {
        return switch (color) {
            case RED -> javafx.scene.paint.Color.RED;
            case GREEN -> javafx.scene.paint.Color.GREEN;
            case BLUE -> javafx.scene.paint.Color.BLUE;
            case YELLOW -> javafx.scene.paint.Color.CHOCOLATE;
            case ORANGE -> javafx.scene.paint.Color.ORANGE;
            case PURPLE -> javafx.scene.paint.Color.PURPLE;
        };
    }

    private String colorName(javafx.scene.paint.Color color) {
        if (javafx.scene.paint.Color.RED.equals(color)) return "RED";
        if (javafx.scene.paint.Color.GREEN.equals(color)) return "GREEN";
        if (javafx.scene.paint.Color.BLUE.equals(color)) return "BLUE";
        if (javafx.scene.paint.Color.CHOCOLATE.equals(color)) return "CHOCOLATE";
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
        getChildren().clear();
        segments.clear();
        for (Color color : tube.getSegments()) {
            Rectangle rect = new Rectangle(SEGMENT_WIDTH, SEGMENT_HEIGHT);
            rect.setArcWidth(5d);
            rect.setArcHeight(5d);
            javafx.scene.paint.Color couleur = getColor(color);
            segments.add(couleur);
            rect.setFill(couleur);
            getChildren().add(rect);
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
