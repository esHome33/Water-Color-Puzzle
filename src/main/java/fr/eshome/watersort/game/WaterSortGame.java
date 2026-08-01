package fr.eshome.watersort.game;

import fr.eshome.watersort.state.GameState;
import fr.eshome.watersort.ui.TubeView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/**
 * Represents a Water Sort game logic. It is backed by a GameState object when
 * storing to file is needed.
 */
public class WaterSortGame {
    static private final String TEMP_FILE_NAME = "waterdrop_game_state.json";
    static public final String NEW_GAME_FILE_NAME = "new_game_state.json";
    static public final int NB_TUBES = 7;
    static public final int TAILLE_TUBES = 10;
    private final ArrayList<Tube> tubes;
    private int nbRuptures = 0;

    private final FromTo fromTo = new FromTo();

    public final SimpleIntegerProperty nbCoups = new SimpleIntegerProperty(0);

    public final SimpleIntegerProperty nbCouleurs = new SimpleIntegerProperty(0);

    private final Pane colorPane;
    private final HBox conteneurTubesUI;


    public final SimpleBooleanProperty solvedState = new SimpleBooleanProperty(false);


    /**
     * Creates a new game with random tubes
     *
     * @param ui_container    the HBox container for the tubeUI
     * @param color_indicator the Pane for the color indicator
     * @return the new game
     */
    static public WaterSortGame createGameWithRandomTubes(HBox ui_container, Pane color_indicator) {
        WaterSortGame result = new WaterSortGame(ui_container, color_indicator);
        // fill with random color tubes
        result.initRandomTubes();
        // save initial state of the game
        try {
            result.toJSON();
        } catch (IOException e) {
            System.out.println("Error while saving game state to JSON: " + e.getMessage());
        }
        // count the segments and the colors
        result.countColors();
        return result;
    }

    public static WaterSortGame createFromSavedGame(HBox tubesContainer, Pane colorProp) {
        WaterSortGame result = new WaterSortGame(tubesContainer, colorProp);
        // first try to load saved game state from creator screen
        try {
            result.newGameFromSavedState();
        } catch (IOException e) {
            // in case of a problem, return to the start state
            try {
                result.returnToStartState();
            } catch (IOException ex) {
                // if no start state available, create a game with random tubes
                result = createGameWithRandomTubes(tubesContainer, colorProp);
            }
        }
        // count the segments and the colors
        result.countColors();
        return result;
    }

    public void setCoups(int i) {
        nbCoups.set(i);
    }

    /**
     * Initializes NB_TUBES -3 tubes with random colors (the total quantity of
     * colors in all tubes is globally limited to a random number of colors and a
     * random total quantity of segments)
     */
    private void initRandomTubes() {
        int nbTotalCouleurs = RandomGenerator.getDefault().nextInt(1, fr.eshome.watersort.game.Color.getNbColors());
        int nbTubesNonEmpty = NB_TUBES - 3;
        ArrayList<fr.eshome.watersort.game.Color> couleurs_a_placer = fr.eshome.watersort.game.Color.getNewGameWithRandomColors(nbTotalCouleurs, nbTubesNonEmpty);
        // separate this list in NB_TUBES - 3 segments ... be careful: check that the intervals are lower than capacity!
        int[] randomIndexes = getCutIndices(couleurs_a_placer.size(), nbTubesNonEmpty);

        int index = 0;
        int idx_debut = 0;
        int idx_fin;
        for (int i = 0; i < NB_TUBES; i++) {
            Tube t;
            // let 3 tubes be empty
            if (i == 1 || i == 3 || i == 5) {
                t = Tube.createEmptyTube(TAILLE_TUBES, i, fromTo);
            } else {
                if (index < NB_TUBES - 4) {
                    idx_fin = randomIndexes[index];
                } else {
                    idx_fin = couleurs_a_placer.size() - 1;
                }
                List<fr.eshome.watersort.game.Color> pour_ce_tube = couleurs_a_placer.subList(idx_debut, idx_fin + 1);
                idx_debut = idx_fin + 1;
                index++;
                t = new Tube(TAILLE_TUBES, i, fromTo, pour_ce_tube);
            }
            this.tubes.add(t);
            // add the TubeView to the UI container and connect a listener to the isSelected property
            TubeView tubeView = t.getTubeView();
            conteneurTubesUI.getChildren().add(tubeView);
            tubeView.isSelected.addListener((obs, oldVal, newVal) ->
                    fromTo.storeId(tubeView.getNumber())
            );
        }
    }

    /**
     * To cut the range [1,upperBound] in p-1 parties, we need to find p-1
     * random distinct indices between 1 and segmentsSize-1.
     *
     * @param upperBound the total number of segments to cut
     * @param p          in how many parts we want to cut the range 0..upperBound
     * @return an array of p-1 random distinct indices between 1 and upperBound-1
     */
    private int[] getCutIndices(int upperBound, int p) {
        Random random = new Random();
        int[] randomIndexes;
        do {
            randomIndexes = random.ints(1, upperBound - 1)
                    .distinct()
                    .limit(p - 1)
                    .toArray();
            // sort these indexes
            Arrays.sort(randomIndexes);
        } while (!verifier(randomIndexes, upperBound));

        System.out.println("Cut indices: " + Arrays.toString(randomIndexes));
        return randomIndexes;
    }

    /**
     * When cutting the range 0..totalNumberOfSegments in nbTubes-1 parts, we need to ensure that each part has
     * at most TAILLE_TUBES size.
     *
     * @param tableau    the array of cut indices
     * @param indexFinal the value of the last index (corresponds to the total number of color segments)
     * @return true if each part has at most TAILLE_TUBES segments, false otherwise
     */
    private boolean verifier(int[] tableau, int indexFinal) {
        int idx_debut = 0;
        for (int elt : tableau) {
            int longueur = elt - idx_debut;
            if (longueur >= TAILLE_TUBES) {
                return false;
            }
            idx_debut = elt + 1;
        }
        return (indexFinal - idx_debut) <= TAILLE_TUBES;
    }

    /**
     * Private constructor of the game: creates a new game with no tubes
     *
     * @param conteneur the UI container of the tubes
     * @param colorProp the pane indicating what kind of moves are ready to be executed
     */
    private WaterSortGame(HBox conteneur, Pane colorProp) {
        // init of important variables
        this.tubes = new ArrayList<>(NB_TUBES);
        this.colorPane = colorProp;
        this.nbCoups.set(0);
        // clear the container from any Tubes
        conteneur.getChildren().clear();
        this.conteneurTubesUI = conteneur;
        // link FromTo colorProperty with a callback that starts the pouring of one tube into another
        fromTo.colorProperty().addListener(this::colorChangeListener);
    }

    /**
     * Callback for the colorProperty of the FromTo object : when the color changes to green,
     * execute the move from one tube to another
     *
     * @param observable the observable value
     * @param oldValue   the old value
     * @param newValue   the new value
     */
    private void colorChangeListener(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
        colorPane.setBackground(new Background(new BackgroundFill(newValue, null, null)));
        if (newValue.equals(Color.GREEN)) {
            executeMove(fromTo);
        }
    }

    /**
     * Executes the move of colors between two tubes.
     * It is the FromTo object that contains the information about the move.
     *
     * @param fromTo the FromTo object containing the information about the
     *               move (id of the tube to move from and id of the tube to move to)
     */
    private void executeMove(FromTo fromTo) {
        move(fromTo.getFrom(), fromTo.getTo());
        setCoups(nbCoups.getValue() + 1);
        tubes.get(fromTo.getFrom()).refreshUI(true);
        tubes.get(fromTo.getTo()).refreshUI(true);
        fromTo.reset();
        boolean s = isSolved();
        solvedState.set(s);
        fromTo.accepte = !s;
    }

    public FromTo getFromTo() {
        return fromTo;
    }

    public void reloadColorChangeListener() {
        fromTo.colorProperty().addListener(this::colorChangeListener);
    }

    /**
     * Returns the fundamental statistics of the game (how many tubes, colors, segments,
     * and the number of segments for each color).
     *
     * @return a list of strings containing the statistics of the game
     */
    public List<String> getStats() {
        countColors();
        List<String> stats = new ArrayList<>();
        stats.add("Nombre de tubes: " + tubes.size());
        List<String> stats_coul = getNbCouleurs();
        int totalColorsNumber = stats_coul.size();
        stats.add("Nombre de couleurs: " + totalColorsNumber);
        int nbSegments = stats_coul.stream().mapToInt(s -> Integer.parseInt(s.split(": ")[1])).sum();
        stats.add("Nombre de segments: " + nbSegments);
        stats.addAll(stats_coul);
        stats.addAll(getColorBreaks());
        return stats;
    }

    /**
     * Count, on each tube, how many color breaks there are.
     *
     * @return a list of strings containing the number of color breaks for each tube
     */
    private ArrayList<String> getColorBreaks() {
        nbRuptures = 0;
        ArrayList<String> colorBreaks = new ArrayList<>();
        for (int i = 0; i < tubes.size(); i++) {
            Tube tube = tubes.get(i);
            int rupt = tube.countColorBreaks();
            nbRuptures += rupt;
            colorBreaks.add("Tube " + (i + 1) + ": " + rupt);
        }
        return colorBreaks;
    }

    /**
     * Returns the number of segments for each color in the game.
     *
     * @return a list of strings containing the number of segments for each color
     */
    private List<String> getNbCouleurs() {
        HashMap<fr.eshome.watersort.game.Color, Integer> compteurCouleurs = countColors();
        return compteurCouleurs.entrySet().stream()
                .map(entry -> entry.getKey().toString() + ": " + entry.getValue())
                .collect(Collectors.toList());
    }

    /**
     * Count how many different colors and segments are in the game.
     *
     * @return a HashMap containing the count of each color and the number of segments for each color.
     */
    public HashMap<fr.eshome.watersort.game.Color, Integer> countColors() {
        HashMap<fr.eshome.watersort.game.Color, Integer> compteurCouleurs = new HashMap<>();
        for (Tube t : tubes) {
            List<fr.eshome.watersort.game.Color> seg = t.getSegments();
            for (fr.eshome.watersort.game.Color coul : seg) {
                compteurCouleurs.merge(coul, 1, Integer::sum);
            }
        }
        nbCouleurs.set(compteurCouleurs.size());
        return compteurCouleurs;
    }

    public void move(int fromIndex, int toIndex) {
        Tube from = tubes.get(fromIndex);
        Tube to = tubes.get(toIndex);
        if (from.cannotPourInto(to)) return;
        from.pourInto(to);
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

    public int getNbTubes() {
        return tubes.size();
    }

    public ArrayList<Tube> getTubes() {
        return tubes;
    }

    /**
     * Add a tube to the game (the tube's UI is also added to the game's UI container).
     *
     * @param tube a Tube
     */
    public void addTube(Tube tube) {
        this.conteneurTubesUI.getChildren().add(tube.getTubeView());
        tubes.add(tube);
    }

    public void clearUIContainer() {
        conteneurTubesUI.getChildren().clear();
    }

    /**
     * Populate this WaterSortGame with the saved new game state contained in the json.
     *
     * @param json a new game state in JSON format
     */
    private void convertFromJson(String json) {
        com.google.gson.Gson gson = new com.google.gson.Gson();
        GameState loadedState = gson.fromJson(json, GameState.class);
        loadedState.populate(this);
    }


    /**
     * Saves the current state of the game to a temporary JSON file.
     *
     * @throws IOException if an error occurs while writing to the file
     */
    public void toJSON() throws IOException {
        // Create a json file in ~/waterdrop directory
        String homeDir = System.getProperty("user.home");
        Path tempDir = Path.of(homeDir, "waterdrop");
        Files.createDirectories(tempDir);
        Path gameStateFile = tempDir.resolve(TEMP_FILE_NAME);
        // Convert the state to JSON
        String json = convertToJson();
        // Write to file
        Files.writeString(gameStateFile, json);
        System.out.println("Game state saved to: " + gameStateFile);
    }

    /**
     * Convert this game to a GameState object and convert it to JSON.
     *
     * @return the JSON representation of the game state
     */
    private String convertToJson() {
        com.google.gson.Gson gson = new com.google.gson.Gson();
        GameState state = new GameState(this);
        return gson.toJson(state);
    }


    /**
     * Return this game to its initial state (retrieved in a file in ~/waterdrop/ folder)
     *
     * @throws IOException if the game state file cannot be found or read
     */
    public void returnToStartState() throws IOException {
        tubes.clear();
        conteneurTubesUI.getChildren().clear();
        fromTo.reset();
        File tempDir = new File(System.getProperty("user.home"), "waterdrop");
        if (!tempDir.exists()) {
            throw new IOException("Temp directory not found or game state not saved yet");
        }

        File gameStateFile = new File(tempDir, TEMP_FILE_NAME);
        if (!gameStateFile.exists()) {
            throw new IOException("Game state file not found in temp directory");
        }
        // Read the JSON content
        String json = new String(Files.readAllBytes(gameStateFile.toPath()));
        // Convert from JSON to our data structure
        convertFromJson(json);
    }

    /**
     * Populate this WaterSortGame with the saved new game state (in a file in ~/waterdrop/ folder)
     *
     * @throws IOException in case of error, reading the file
     */
    private void newGameFromSavedState() throws IOException {
        tubes.clear();
        conteneurTubesUI.getChildren().clear();
        fromTo.reset();
        File tempDir = new File(System.getProperty("user.home"), "waterdrop");
        if (!tempDir.exists()) {
            throw new IOException("App working directory not found or game state not saved yet");
        }

        File gameStateFile = new File(tempDir, NEW_GAME_FILE_NAME);
        if (!gameStateFile.exists()) {
            throw new IOException("New game state file not found in app working directory");
        }
        // Read the JSON content
        String json = new String(Files.readAllBytes(gameStateFile.toPath()));
        // Convert from JSON to our data structure
        convertFromJson(json);
        // copy this json into TEMP
        Files.writeString(new File(tempDir, TEMP_FILE_NAME).toPath(), json);
    }

    public String getStatsRuptures() {
        return nbRuptures + " ruptures - " + nbCouleurs.intValue() + " couleurs";
    }

    /**
     * Get the list of colors in all the tubes (for example [1, 1][][2, 2] is a solved game)
     *
     * @return the color game state as a string
     */
    public String getStringContent() {
        GameState state = new GameState(this);
        return state.getStringContent();
    }
}
