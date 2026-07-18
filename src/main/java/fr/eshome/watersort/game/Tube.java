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


    private void fillInWithColors() {
        int howMany = RandomGenerator.getDefault().nextInt(1, capacity - 1);
        System.out.println("Filling tube with " + howMany + " colors");
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
        List<Color> result = new ArrayList<>(segments);
        Collections.reverse(result);
        return result;
    }

    /**
     * True if all segments are the same color (or tube is empty).
     */
    public boolean isUniform() {
        return segments.isEmpty() || new HashSet<>(segments).size() == 1;
    }

    public boolean isSolved() {
        return isEmpty() || (isFull() && isUniform());
    }

    /**
     * How many consecutive same-color segments sit on top.
     */
    private int topRunLength() {
        if (segments.isEmpty()) return 0;
        Iterator<Color> it = segments.iterator();
        Color top = it.next();
        int count = 1;
        while (it.hasNext() && it.next() == top) count++;
        return count;
    }

    /**
     * Can we pour from this tube into the given target tube?
     */
    public boolean canPourInto(Tube target) {
        if (isEmpty()) return false;
        if (target.isFull()) return false;
        if (!target.isEmpty() && target.peek() != this.peek()) return false;
        return true;
    }

    /**
     * Pour the top run of same-colored segments into target, space permitting.
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
        Collections.reverse(top2bottom); // print bottom -> top
        return top2bottom.toString();
    }

}
