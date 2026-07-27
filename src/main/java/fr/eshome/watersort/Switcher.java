package fr.eshome.watersort;

/**
 * A global static class to store the startWithRandom value and can be accessed in all the controllers.
 *
 * @author eshome33
 */
public class Switcher {
    /**
     * The value to start the game with random or saved game
     */
    public static boolean startWithRandom = true;

    @Override
    public String toString() {
        return "Switcher [startWithRandom=" + startWithRandom + "]";
    }

    /**
     * Store the <code>true</code> value in startWithRandom
     */
    public static void setStartWithRandom() {
        startWithRandom = true;
    }

    /**
     * Store the <code>false</code> value in startWithRandom
     */
    public static void setStartWithSaved() {
        startWithRandom = false;
    }
}
