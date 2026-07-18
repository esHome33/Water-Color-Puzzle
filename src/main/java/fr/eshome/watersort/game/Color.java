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
}
