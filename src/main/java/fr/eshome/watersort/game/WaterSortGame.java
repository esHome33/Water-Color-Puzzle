package fr.eshome.watersort.game;

import fr.eshome.watersort.ui.TubeView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WaterSortGame {
    private static final int NB_TUBES = 7;
    private static final int TAILLE_TUBES = 10;
    private final List<Tube> tubes;

    private final FromTo fromTo = new FromTo();

    public SimpleBooleanProperty solvedState = new SimpleBooleanProperty(false);


    public WaterSortGame(HBox conteneur, Pane colorProp) {
        // init of lists
        this.tubes = new ArrayList<>(NB_TUBES);
        // create the given number of tubes
        for (int i = 0; i < NB_TUBES; i++) {
            Tube t;
            if (i == 1 || i == 3 || i == 5) {
                t = Tube.getEmptyTube(TAILLE_TUBES, i, fromTo);
            } else {
                t = new Tube(TAILLE_TUBES, i, fromTo);
            }
            this.tubes.add(t);
            TubeView tubeView = t.getTubeView();
            conteneur.getChildren().add(tubeView);
            tubeView.isSelected.addListener((_, _, _) ->
                    fromTo.storeId(tubeView.getNumber())
            );
        }
        // add all tubeviews to the ui container
        // link FromTo colorProperty with a callback that starts the pouring of one tube into another
        fromTo.colorProperty().addListener((_, _, newValue) -> {
            colorProp.setBackground(new Background(new BackgroundFill(newValue, null, null)));
            if (newValue.equals(Color.GREEN)) {
                boolean resultat = move(fromTo.getFrom(), fromTo.getTo());
                if (resultat) {
                    System.out.println("Move successful");
                } else {
                    System.out.println("Move failed");
                }
                tubes.get(fromTo.getFrom()).refreshUI();
                tubes.get(fromTo.getTo()).refreshUI();
                fromTo.reset();
                boolean s = isSolved();
                System.out.println("Game solved: " + s);
                solvedState.set(s);
                fromTo.accepte = !s;
            }
        });
    }

    public boolean move(int fromIndex, int toIndex) {
        Tube from = tubes.get(fromIndex);
        Tube to = tubes.get(toIndex);
        if (!from.canPourInto(to)) return false;
        from.pourInto(to);
        return true;
    }

    public boolean isSolved() {
        ArrayList<Color> colors = new ArrayList<>();
        for (Tube t : tubes) {
            Color couleur = t.getColor();
            if (null == couleur) return false;
            if (!Color.WHITE.equals(couleur)) {
                colors.add(couleur);
            }
        }
        HashSet<Color> setColor = new HashSet<>(colors);
        return setColor.size() == colors.size();
    }

    public void print() {
        for (int i = 0; i < tubes.size(); i++) {
            System.out.println("Tube " + i + ": " + tubes.get(i));
        }
    }

    public static WaterSortGame newGame(HBox conteneur, Pane colorProp) {
        // restart a new game
        conteneur.getChildren().clear();
        return new WaterSortGame(conteneur, colorProp);
    }
}
