module fr.eshome.watersort {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens fr.eshome.watersort to javafx.fxml;
    exports fr.eshome.watersort;
    exports fr.eshome.watersort.ui;
    exports fr.eshome.watersort.game;
    opens fr.eshome.watersort.ui to javafx.fxml;
}