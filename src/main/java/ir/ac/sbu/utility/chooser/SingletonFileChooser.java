package ir.ac.sbu.utility.chooser;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class SingletonFileChooser {

    private final static SingletonFileChooser INSTANCE = new SingletonFileChooser();

    private final FileChooser fileChooser;
    private final SimpleObjectProperty<File> lastKnownDirectoryProperty;

    private SingletonFileChooser() {
        this.fileChooser = new FileChooser();
        this.lastKnownDirectoryProperty = new SimpleObjectProperty<>();
        this.fileChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
    }

    public static SingletonFileChooser getInstance() {
        return INSTANCE;
    }

    public SingletonFileChooser setExtensions(String... extensions) {
        fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("extensions", extensions));
        return this;
    }

    public SingletonFileChooser setTitle(String title) {
        fileChooser.setTitle(title);
        return this;
    }

    public File showOpenDialog(Window ownerWindow) {
        File chosenFile = fileChooser.showOpenDialog(ownerWindow);
        if (chosenFile != null) {
            lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
        }
        return chosenFile;
    }

    public File showSaveDialog(Window ownerWindow) {
        File chosenFile = fileChooser.showSaveDialog(ownerWindow);
        if (chosenFile != null) {
            lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
        }
        return chosenFile;
    }
}
