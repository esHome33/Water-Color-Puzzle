package fr.eshome.watersort.game;

import java.util.random.RandomGenerator;

public enum Color {
    RED, GREEN, BLUE, YELLOW, ORANGE, PURPLE;

    static public Color getRandomColor() {
        int rnd = RandomGenerator.getDefault().nextInt(0, 6);
        return switch (rnd) {
            case 0 -> RED;
            case 1 -> GREEN;
            case 2 -> BLUE;
            case 3 -> YELLOW;
            case 4 -> ORANGE;
            default -> PURPLE;
        };
    }

    public javafx.scene.paint.Color getJavaFXColor() {
        return switch (this) {
            case RED -> javafx.scene.paint.Color.RED;
            case GREEN -> javafx.scene.paint.Color.GREEN;
            case BLUE -> javafx.scene.paint.Color.BLUE;
            case YELLOW -> javafx.scene.paint.Color.YELLOW;
            case ORANGE -> javafx.scene.paint.Color.ORANGE;
            default -> javafx.scene.paint.Color.PURPLE;
        };
    }
}
