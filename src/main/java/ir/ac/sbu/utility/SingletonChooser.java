package ir.ac.sbu.utility;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

class SingletonDirectoryChooser {
    private static DirectoryChooser instance = null;
    private static SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();

    private SingletonDirectoryChooser() {
    }

    private static DirectoryChooser getInstance() {
        if (instance == null) {
            instance = new DirectoryChooser();
            instance.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
        }
        return instance;
    }


    public static void setTitle(String title) {
        getInstance().setTitle(title);
    }

    public static File showDialog(Window ownerWindow) {
        File chosenDirectory = getInstance().showDialog(ownerWindow);
        if (chosenDirectory != null) {
            lastKnownDirectoryProperty.setValue(chosenDirectory.getAbsoluteFile());
        }
        return chosenDirectory;
    }

}

class SingletonFileChooser {
    private static FileChooser instance = null;
    private static SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();

    private SingletonFileChooser() {
    }

    private static FileChooser getInstance() {
        if (instance == null) {
            instance = new FileChooser();
            instance.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
        }
        return instance;
    }

    public static void setExtensions(String... extensions) {
        getInstance().getExtensionFilters().setAll(new FileChooser.ExtensionFilter("extensions", extensions));
    }

    public static void setTitle(String title) {
        getInstance().setTitle(title);
    }

    public static File showOpenDialog(Window ownerWindow) {
        File chosenFile = getInstance().showOpenDialog(ownerWindow);
        if (chosenFile != null) {
            lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
        }
        return chosenFile;
    }

    public static File showSaveDialog(Window ownerWindow) {
        File chosenFile = getInstance().showSaveDialog(ownerWindow);
        if (chosenFile != null) {
            lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
        }
        return chosenFile;
    }
}
