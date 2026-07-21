package fr.eshome.watersort.game;

import fr.eshome.watersort.state.GameState;
import fr.eshome.watersort.ui.TubeView;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class WaterSortGame {
    private static final String TEMP_FILE_NAME = "waterdrop_game_state.json";
    private static final int NB_TUBES = 7;
    private static final int TAILLE_TUBES = 10;
    private final ArrayList<Tube> tubes;

    private final FromTo fromTo = new FromTo();

    public int countSteps = 0;
    private final Pane colorPane;
    private final HBox conteneurTubesUI;


    public SimpleBooleanProperty solvedState = new SimpleBooleanProperty(false);


    /**
     * Creates an empty new game (without tubes). Tubes can be added later with addTube()
     *
     * @param ui_container    the HBox container for the tubeUI
     * @param color_indicator the Pane for the color indicator
     * @return the new game
     */
    static public WaterSortGame createGame(HBox ui_container, Pane color_indicator) {
        return new WaterSortGame(ui_container, color_indicator);
    }

    /**
     * Creates a new game with random tubes
     *
     * @param ui_container    the HBox container for the tubeUI
     * @param color_indicator the Pane for the color indicator
     * @return the new game
     */
    static public WaterSortGame createGameWithRandomTubes(HBox ui_container, Pane color_indicator) {
        WaterSortGame result = new WaterSortGame(ui_container, color_indicator);
        result.initRandomTubes();
        // save initial state of the game
        try {
            result.toJSON();
        } catch (IOException e) {
            System.out.println("Error while saving game state to JSON: " + e.getMessage());
        }
        return result;
    }

    /**
     * Initializes the tubes with random colors
     */
    private void initRandomTubes() {
        for (int i = 0; i < NB_TUBES; i++) {
            Tube t;
            if (i == 1 || i == 3 || i == 5) {
                t = Tube.createEmptyTube(TAILLE_TUBES, i, fromTo);
            } else {
                t = new Tube(TAILLE_TUBES, i, fromTo);
            }
            this.tubes.add(t);
            // add all TubeViews to the ui container and connect a listener to the isSelected property
            TubeView tubeView = t.getTubeView();
            conteneurTubesUI.getChildren().add(tubeView);
            tubeView.isSelected.addListener((_, _, _) ->
                    fromTo.storeId(tubeView.getNumber())
            );
        }
    }


    /**
     * Private constructor of the game: creates a new game with no tubes
     *
     * @param conteneur the container of the tubes
     * @param colorProp the pane where the colors are displayed
     */
    private WaterSortGame(HBox conteneur, Pane colorProp) {
        // init of important variables
        this.tubes = new ArrayList<>(NB_TUBES);
        this.colorPane = colorProp;
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

    private void executeMove(FromTo fromTo) {
        move(fromTo.getFrom(), fromTo.getTo());
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

    public List<String> getStats() {
        List<String> stats = new ArrayList<>();
        stats.add("Nombre de tubes: " + tubes.size());
        List<String> stats_coul = getNbCouleurs();
        int totalColorsNumber = stats_coul.size();
        stats.add("Nombre de couleurs: " + totalColorsNumber);
        int nbSegments = stats_coul.stream().mapToInt(s -> Integer.parseInt(s.split(": ")[1])).sum();
        stats.add("Nombre de segments: " + nbSegments);
        stats.addAll(stats_coul);
        return stats;
    }

    private List<String> getNbCouleurs() {
        HashMap<fr.eshome.watersort.game.Color, Integer> compteurCouleurs = new HashMap<>();
        for (Tube t : tubes) {
            List<fr.eshome.watersort.game.Color> seg = t.getSegments();
            for (fr.eshome.watersort.game.Color coul : seg) {
                if (!compteurCouleurs.containsKey(coul)) {
                    compteurCouleurs.put(coul, 1);
                } else {
                    compteurCouleurs.put(coul, compteurCouleurs.get(coul) + 1);
                }
            }
        }
        return compteurCouleurs.entrySet().stream()
                .map(entry -> entry.getKey().toString() + ": " + entry.getValue())
                .collect(Collectors.toList());
    }

    public void move(int fromIndex, int toIndex) {
        Tube from = tubes.get(fromIndex);
        Tube to = tubes.get(toIndex);
        if (!from.canPourInto(to)) return;
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

    public int getTubeCapacity() {
        return TAILLE_TUBES;
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
        Files.write(gameStateFile, json.getBytes());
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
     * Return the game to its initial state.
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
        } else {
            System.out.println("Found game state file : " + gameStateFile.getAbsolutePath());
        }

        // Read the JSON content
        String json = new String(Files.readAllBytes(gameStateFile.toPath()));

        // Convert from JSON to our data structure
        convertFromJson(json);

        System.out.println("Game state loaded successfully");
    }


}
