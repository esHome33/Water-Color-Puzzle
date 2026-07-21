package fr.eshome.watersort.state;

import fr.eshome.watersort.game.Color;
import fr.eshome.watersort.game.FromTo;
import fr.eshome.watersort.game.Tube;

import java.util.ArrayList;

public class TubeState {
    ArrayList<Integer> segments;
    int capacity;
    int number;

    /**
     * Create a tube state from tube properties.
     *
     * @param capacity the total capacity of the tube
     * @param segments the list of colors indexes in the tube
     * @param number   the id of the tube
     */
    public TubeState(int capacity, ArrayList<Integer> segments, int number) {
        this.capacity = capacity;
        this.segments = (ArrayList<Integer>) segments.clone();
        this.number = number;
    }

    /**
     * Create a tube state from a tube.
     *
     * @param tube the tube to create the state from
     * @return the tube state
     */
    static public TubeState fromTube(Tube tube) {
        ArrayList<Integer> seg = new ArrayList<>();
        for (Color c : tube.getSegments().reversed()) {
            seg.add(c.ordinal());
        }
        return new TubeState(tube.capacity(), seg, tube.getNumber());
    }

    /**
     * Create a tube from this tube state (the tube's UI is also created).
     *
     * @param fromTo the FromTo associated with the new tube
     * @return a newly created tube from this state
     */
    public Tube createTube(FromTo fromTo) {
        Tube tube = Tube.createEmptyTube(capacity, number, fromTo);
        tube.setSegments(segments);
        tube.refreshUI(false);
        return tube;
    }
}
