module fr.eshome.watersort {
    requires javafx.controls;
    requires javafx.fxml;


    requires com.google.gson;

    opens fr.eshome.watersort to javafx.fxml;
    opens fr.eshome.watersort.ui to javafx.fxml, com.google.gson;
    opens fr.eshome.watersort.game to com.google.gson;
    opens fr.eshome.watersort.state to com.google.gson;

    exports fr.eshome.watersort;
    exports fr.eshome.watersort.ui;
    exports fr.eshome.watersort.game;
}