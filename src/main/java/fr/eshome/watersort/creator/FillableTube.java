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
    private final double myWidth = 28d;


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
        container.setMinWidth(myWidth);
        container.setPrefWidth(myWidth);
        container.setMaxWidth(myWidth + 5d);
        container.setStyle("-fx-background-color: white; -fx-border-color: #ccedfc; -fx-border-radius: 4; -fx-background-radius: 4;");
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
        String style = " -fx-background-color: "
                + fr.eshome.watersort.game.Color.getHTMLColor(color)
                + "; "
                + "  -fx-border-color: "
                + fr.eshome.watersort.game.Color.getHTMLColor(color)
                + " ;"
                + "  -fx-border-radius: 4 ; "
                + "  -fx-background-radius: 4 ; ";
        segment.setStyle(style);
        segment.setMinHeight(20d);
        segment.setPrefSize(myWidth, 20d);
        segment.setMaxWidth(myWidth);

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

    public void replaceWith(ArrayList<fr.eshome.watersort.game.Color> couleurs) {
        segments.clear();
        for (fr.eshome.watersort.game.Color col : couleurs) {
            addColor(col.getJavaFXColor());
        }
    }
}
