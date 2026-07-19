package fr.eshome.watersort.game;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

/**
 * This class stores the id of two tubes and determines which id is the first entered and
 * which is the second entered. The same id entered twice means that this id is removed from
 * this FromTo.
 */
public class FromTo {
    private int from;
    private int to;

    public boolean accepte;

    public FromTo() {
        this.from = -1;
        this.to = -1;
        updateColor();
        accepte = true;
    }

    // Observable color property
    private final SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.GREY);

    public int getFrom() {
        return this.from;
    }

    public int getTo() {
        return this.to;
    }

    /**
     * Check if this id could be stored in this FromTo.
     *
     * @param id an id
     * @return true if this id could be stored in this FromTo, false otherwise
     */
    public boolean tryStoreId(int id) {
        if (!this.accepte) {
            return false;
        }
        if (this.from == id) {
            return true;
        }
        if (this.to == id) {
            return true;
        }
        if (this.from == -1 || this.to == -1) {
            return true;
        }
        return false;
    }

    /**
     * Store the given id in this FromTo.
     *
     * @param id an id
     */
    public void storeId(int id) {
        if (!this.accepte) {
            return;
        }
        if (this.from == id) {
            this.from = -1;
            updateColor();
            return;
        }
        if (this.to == id) {
            this.to = -1;
            updateColor();
            return;
        }

        if (this.from == -1) {
            this.from = id;
        } else if (this.to == -1) {
            this.to = id;
        }
        updateColor();
    }

    // Getter for the observable color property
    public SimpleObjectProperty<Color> colorProperty() {
        return colorProperty;
    }

    public Color getColor() {
        if (from == -1 && to == -1)
            return Color.GREY;
        if (from == -1 || to == -1)
            return Color.ORANGE;
        return Color.GREEN;
    }

    // Helper method to update the observable property when color changes
    private void updateColor() {
        colorProperty.set(getColor());
    }

    public void reset() {
        from = -1;
        to = -1;
        updateColor();
        accepte = true;
    }
}
