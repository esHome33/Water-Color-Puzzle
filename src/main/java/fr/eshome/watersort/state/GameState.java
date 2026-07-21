package fr.eshome.watersort.state;

import fr.eshome.watersort.game.FromTo;
import fr.eshome.watersort.game.Tube;
import fr.eshome.watersort.game.WaterSortGame;
import fr.eshome.watersort.ui.TubeView;

import java.util.ArrayList;

public class GameState {
    ArrayList<TubeState> tubes;
    int num_of_tubes;
    int tube_capacity;
    boolean is_solved;


    /**
     * Create a new game state from a WaterSortGame.
     *
     * @param game the WaterSortGame to create the state from
     */
    public GameState(WaterSortGame game) {
        num_of_tubes = game.getNbTubes();
        tubes = new ArrayList<>(num_of_tubes);
        tube_capacity = game.getTubeCapacity();
        for (Tube t : game.getTubes()) {
            tubes.add(TubeState.fromTube(t));
        }
        is_solved = game.isSolved();
    }

    /**
     * Populates a WaterSortGame from a GameState.
     *
     * @param game the WaterSortGame to populate
     */
    public void populate(WaterSortGame game) {
        game.getTubes().clear();
        game.clearUIContainer();
        FromTo fromTo = game.getFromTo();
        fromTo.reset();
        game.reloadColorChangeListener();
        for (TubeState t : tubes) {
            Tube newTube = t.createTube(fromTo);
            TubeView tv = newTube.getTubeView();
            tv.isSelected.addListener(
                    (_, _, _) -> fromTo.storeId(tv.getNumber()));
            game.addTube(newTube);
        }
    }
}
