package fr.eshome.watersort.game;

import fr.eshome.watersort.ui.TubeView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class WaterSortGame {
    private static final int NB_TUBES = 5;
    private static final int TAILLE_TUBES = 10;
    private final List<Tube> tubes;
    private final List<TubeView> conteneur;

    private int start_number = -1;
    private int dest_number = -1;

    private final HBox ui_container;
    private FromTo fromTo = new FromTo();


    public WaterSortGame(HBox conteneur, Pane colorProp) {
        ui_container = conteneur;
        // init of lists
        this.conteneur = new ArrayList<>(NB_TUBES);
        this.tubes = new ArrayList<>(NB_TUBES);
        // create the given number of tubes
        for (int i = 0; i < NB_TUBES; i++) {
            Tube t = new Tube(TAILLE_TUBES, i, this.fromTo);
            this.tubes.add(t);
            TubeView tubeView = t.getTubeView();
            this.conteneur.add(tubeView);
            tubeView.isSelected.addListener((_, _, _) -> {
                fromTo.storeId(tubeView.getNumber());
            });
        }
        // add all tubeviews to the ui container
        ui_container.getChildren().addAll(this.conteneur);
        // link colorProperty with
        fromTo.colorProperty().addListener((_, _, newValue) -> {
            System.out.println("Color changed " + newValue.toString());
            colorProp.setBackground(new Background(new BackgroundFill(newValue, null, null)));
        });
    }

    public List<Tube> getTubes() {
        return tubes;
    }

    public boolean move(int fromIndex, int toIndex) {
        Tube from = tubes.get(fromIndex);
        Tube to = tubes.get(toIndex);
        if (!from.canPourInto(to)) return false;
        from.pourInto(to);
        return true;
    }

    public boolean isSolved() {
        for (Tube t : tubes) {
            if (!t.isSolved()) return false;
        }
        return true;
    }

    public void print() {
        for (int i = 0; i < tubes.size(); i++) {
            System.out.println("Tube " + i + ": " + tubes.get(i));
        }
    }

}
