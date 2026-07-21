package fr.eshome.watersort.game;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

/**
 * This class stores the id of two tubes and determines which id is the first entered and
 * which is the second entered. The same id entered twice means that this id has to be
 * removed from this FromTo.
 *
 * @author eshome33 - july 2026
 */
public class FromTo {
    private int from;
    private int to;

    public boolean accepte;

    /**
     * Create a new FromTo with no stored ids and color set to Grey.
     */
    public FromTo() {
        this.from = -1;
        this.to = -1;
        updateColor();
        accepte = true;
    }

    /**
     * Observable color property: 3 colors depending on the stored ids.
     */
    private final SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.GREY);

    public int getFrom() {
        return this.from;
    }

    public int getTo() {
        return this.to;
    }

    /**
     * Check if this Tube id could be stored in this FromTo.
     *
     * @param id a Tube id
     * @return true, if this id could be stored in this FromTo, false otherwise
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
        return this.from == -1 || this.to == -1;
    }

    /**
     * Store the given Tube id in this FromTo.
     *
     * @param id a Tube id
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

    /**
     * Getter for the observable color property
     */
    public SimpleObjectProperty<Color> colorProperty() {
        return colorProperty;
    }

    /**
     * the state of from and to decides of the color.
     *
     * @return Green if both from and to are set, Orange if only one is set, Grey otherwise.
     */
    public Color getColor() {
        if (from == -1 && to == -1)
            return Color.GREY;
        if (from == -1 || to == -1)
            return Color.ORANGE;
        return Color.GREEN;
    }

    /**
     * Helper method to update the observable property when color changes
     */
    private void updateColor() {
        colorProperty.set(getColor());
    }

    /**
     * Reset the FromTo object to its initial state,
     * i.e., no tubes selected, ready to accept new tube selections and color reset to gray.
     */
    public void reset() {
        from = -1;
        to = -1;
        updateColor();
        accepte = true;
    }
}
