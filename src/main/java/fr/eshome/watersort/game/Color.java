package fr.eshome.watersort.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.random.RandomGenerator;

public enum Color {
    RED, GREEN, BLUE, YELLOW, ORANGE, PURPLE;

    static public Color getRandomColor(int maxNumberOfColors) {
        int rnd = RandomGenerator.getDefault().nextInt(0, maxNumberOfColors);
        return switch (rnd) {
            case 0 -> RED;
            case 1 -> GREEN;
            case 2 -> BLUE;
            case 3 -> YELLOW;
            case 4 -> ORANGE;
            default -> PURPLE;
        };
    }

    /**
     * Returns the number of colors available in this enum.
     *
     * @return the number of colors
     */
    public static int getNbColors() {
        return 6;
    }

    /**
     * Returns the JavaFX color corresponding to this color.
     *
     * @return the JavaFX color
     */
    public javafx.scene.paint.Color getJavaFXColor() {
        return switch (this) {
            case RED -> javafx.scene.paint.Color.RED;
            case GREEN -> javafx.scene.paint.Color.GREEN;
            case BLUE -> javafx.scene.paint.Color.BLUE;
            case YELLOW -> javafx.scene.paint.Color.CHOCOLATE;
            case ORANGE -> javafx.scene.paint.Color.ORANGE;
            default -> javafx.scene.paint.Color.PURPLE;
        };
    }

    /**
     * This function generates random segments that can be hosted in the given number of tubes and
     * doesn't allow the total number of segments of a color to exceed the capacity of the tubes,
     * ensuring that the game is playable.
     *
     * @param numberOfColors how many different colors we want in our game
     * @param numberOfTubes  how many non-empty tubes we want in our game
     * @return a collection of colors that can be poured into the numberOfTubes tubes.
     */
    public static ArrayList<Color> getNewGameColors(int numberOfColors, int numberOfTubes) {
        // first determine how many segments we can host in this number of tubes, but
        // we must not exceed the total number of segments in each tube (set to 10 in WaterSortGame.TAILLE_TUBES)
        int maxNumOfSegments = numberOfTubes * WaterSortGame.TAILLE_TUBES;
        int numOfSegmentsWithNumOfColors = numberOfColors * WaterSortGame.TAILLE_TUBES;
        // if there are only two colors, then we can't have more segments than the capacity of two tubes
        int maxSegments = Math.min(maxNumOfSegments, numOfSegmentsWithNumOfColors);
        // the minimum number of segments that is interesting to play is 3 per tube
        int minSegments = numberOfTubes * 3;
        // choose a random number between minSegments and maxSegments
        // (if minSegments is greater than maxSegments, then we will always choose maxSegments)
        int numOfSegments;
        if (minSegments > maxSegments) {
            numOfSegments = maxSegments;
        } else {
            numOfSegments = (int) (Math.random() * (maxSegments - minSegments + 1) + minSegments);
        }
        // and now, select colors for this number of segments
        HashMap<Color, Integer> color_counter = new HashMap<>(numberOfColors);
        ArrayList<Color> colors = new ArrayList<>();
        for (int i = 0; i < numOfSegments; i++) {
            Color color = getRandomColor(numberOfColors);
            // ensure that this color is not already at its maximum capacity
            while (color_counter.getOrDefault(color, 0) >= WaterSortGame.TAILLE_TUBES)
                color = getRandomColor(numberOfColors);
            // when done, add the color to the list and update the counter
            colors.add(color);
            color_counter.put(color, color_counter.getOrDefault(color, 0) + 1);
        }
        return colors;
    }
}
