package fr.eshome.watersort.creator;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class FillableTube {

    private final int id;
    private final int capacity;
    private final ArrayList<Color> segments;
    private final VBox container;

    /**
     * Create a new fillable tube (logical structure and UI) with the given id and capacity.
     *
     * @param id       the id of the tube
     * @param capacity the capacity of the tube
     */
    public FillableTube(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.segments = new ArrayList<>();
        container = new VBox();
        container.setSpacing(1);
        container.setPrefWidth(10);
        container.setStyle("-fx-background-color: yellow;");
        container.setMinWidth(40d);
        container.setMinHeight(200d);
        container.setAlignment(Pos.BOTTOM_CENTER);
    }

    /**
     * Add one color segment to this tube (and create the corresponding UI element).
     *
     * @param color the color to add
     */
    public void addColor(Color color) {
        segments.add(color);
        Pane segment = new Pane();
        segment.setStyle("-fx-background-color: " + fr.eshome.watersort.game.Color.getHTMLColor(color));
        segment.setMinWidth(40d);
        segment.setMinHeight(20d);
        container.getChildren().addFirst(segment);
    }

    /**
     * Remove the last inserted color segment from this tube (and remove the corresponding UI element).
     */
    public void removeColor() {
        if (!segments.isEmpty()) {
            segments.removeLast();
            container.getChildren().removeFirst();
        }
    }

    public VBox getUI() {
        return container;
    }

    /**
     * Get the JSON representation of this tube.
     *
     * @return the JSON representation of this tube
     */
    public String toJSON() {
        return "{" +
                "\"segments\": " + convertToString(segments) + "," +
                "\"capacity\": " + capacity + "," +
                "\"number\": " + id +
                "}";
    }

    /**
     * Convert a list of colors to a list of integers (ordinal values in Color enumeration).
     *
     * @param colorList the list of colors to convert
     * @return the list of integers
     */
    private ArrayList<Integer> convertToString(ArrayList<Color> colorList) {
        ArrayList<Integer> resu = new ArrayList<>();
        for (Color color : colorList) {
            resu.add(fr.eshome.watersort.game.Color.getOrdinal(color));
        }
        return resu;
    }

}
