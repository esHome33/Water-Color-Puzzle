package fr.eshome.watersort.game;

import fr.eshome.watersort.ui.TubeView;

import java.util.*;
import java.util.random.RandomGenerator;

public class Tube {
    private final Deque<Color> segments = new ArrayDeque<>(); // top = first
    private final int capacity;
    private TubeView tubeView;
    private final int my_number;

    /**
     * Create a tube of the given capacity, fully filled with random colors
     *
     * @param capacity the number of different spots of color in this tube
     */
    public Tube(int capacity, int number, FromTo fromTo) {
        my_number = number;
        this.capacity = capacity;
        fillInWithColors();
        tubeView = new TubeView(this, number, fromTo);
    }

    public static Tube getEmptyTube(int capacity, int number, FromTo fromTo) {
        Tube retour = new Tube(capacity, number);
        retour.tubeView = new TubeView(retour, number, fromTo);
        System.out.println("Creating empty tube " + number);
        return retour;
    }

    /**
     * Creates an empty tube of the given capacity and with the given number
     *
     * @param capacity the total capacity of the tube
     * @param number   the number of the tube
     */
    private Tube(int capacity, int number) {
        this.capacity = capacity;
        this.my_number = number;
    }

    private void fillInWithColors() {
        int howMany = RandomGenerator.getDefault().nextInt(1, capacity - 1);
        System.out.println("Filling tube " + my_number + " with " + howMany + " colors");
        for (int i = howMany; i >= 0; i--) {
            segments.push(Color.getRandomColor());
        }
    }

    public TubeView getTubeView() {
        return tubeView;
    }

    public boolean isEmpty() {
        return segments.isEmpty();
    }

    public boolean isFull() {
        return segments.size() == capacity;
    }

    public Color peek() {
        return segments.peek();
    }

    public int size() {
        return segments.size();
    }

    public int capacity() {
        return capacity;
    }

    public List<Color> getSegments() {
        return new ArrayList<>(segments);
    }

    /**
     * Gets the color contained in this tube.
     *
     * @return the color is the tube is uniform, or WHITE if the tube is empty, or null if the tube is not uniform.
     */
    public javafx.scene.paint.Color getColor() {
        if (isUniform()) {
            if (isEmpty()) {
                return javafx.scene.paint.Color.WHITE;
            } else {
                return segments.peek().getJavaFXColor();
            }
        } else {
            return null;
        }
    }

    /**
     * True if all segments are the same color or the tube is empty.
     *
     * @return a boolean value
     */
    public boolean isUniform() {
        return segments.isEmpty() || new HashSet<>(segments).size() == 1;
    }

    /**
     * How many consecutive same-color segments sit on top.
     */
    private int topRunLength() {
        if (segments.isEmpty()) return 0;
        Iterator<Color> it = segments.iterator();
        Color top = it.next();
        int count = 1;
        while (it.hasNext() && it.next().equals(top)) count++;
        return count;
    }

    /**
     * Can we pour from this tube into the given target tube?
     */
    public boolean canPourInto(Tube target) {
        if (isEmpty()) return false;
        if (target.isFull()) return false;
        Color coul1 = target.peek();
        Color coul2 = this.peek();
        if (coul1 == null) return true;
        return coul1.equals(coul2);
    }

    /**
     * Pour the top run of same-colored segments into the target, space permitting.
     */
    public void pourInto(Tube target) {
        if (!canPourInto(target)) {
            throw new IllegalStateException("Illegal pour");
        }
        Color color = this.peek();
        int available = target.capacity - target.size();
        int moving = Math.min(topRunLength(), available);
        for (int i = 0; i < moving; i++) {
            segments.pop();
            target.segments.push(color);
        }
    }

    @Override
    public String toString() {
        List<Color> top2bottom = new ArrayList<>(segments);
        return "Tube " + top2bottom + " (cap. " + capacity + ")";
    }

    public void refreshUI() {
        tubeView.refreshUI(this);
    }

}
