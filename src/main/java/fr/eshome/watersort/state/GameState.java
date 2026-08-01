package fr.eshome.watersort.state;

import fr.eshome.watersort.game.FromTo;
import fr.eshome.watersort.game.Tube;
import fr.eshome.watersort.game.WaterSortGame;
import fr.eshome.watersort.ui.TubeView;

import java.util.ArrayList;

public class GameState {
    final ArrayList<TubeState> tubes;
    final int num_of_tubes;
    final int tube_capacity;
    final boolean is_solved;
    final int nbCoups;


    /**
     * Create a new game state from a WaterSortGame.
     *
     * @param game the WaterSortGame to create the state from
     */
    public GameState(WaterSortGame game) {
        num_of_tubes = game.getNbTubes();
        tubes = new ArrayList<>(num_of_tubes);
        tube_capacity = WaterSortGame.TAILLE_TUBES;
        for (Tube t : game.getTubes()) {
            tubes.add(TubeState.fromTube(t));
        }
        is_solved = game.isSolved();
        nbCoups = game.nbCoups.getValue();
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
        if (is_solved) {
            fromTo.accepte = false;
        }
        game.reloadColorChangeListener();
        for (TubeState t : tubes) {
            Tube newTube = t.createTube(fromTo);
            TubeView tv = newTube.getTubeView();
            tv.isSelected.addListener(
                    (obs, oldValue, newValue) -> fromTo.storeId(tv.getNumber()));
            game.addTube(newTube);
        }
        game.setCoups(nbCoups);
        game.solvedState.set(is_solved);
    }

    /**
     * Get the color game state as a string.
     *
     * @return the color game state as a string
     */
    public String getStringContent() {
        StringBuilder sb = new StringBuilder();
        for (TubeState t : tubes)
            sb.append(t.segmentsString());
        return sb.toString();
    }
}
