package ir.ac.sbu.utility.chooser;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

public class SingletonDirectoryChooser {

    private final static SingletonDirectoryChooser INSTANCE = new SingletonDirectoryChooser();

    private final DirectoryChooser directoryChooser;
    private final SimpleObjectProperty<File> lastKnownDirectoryProperty;

    private SingletonDirectoryChooser() {
        this.directoryChooser = new DirectoryChooser();
        this.lastKnownDirectoryProperty = new SimpleObjectProperty<>();
        this.directoryChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
    }

    public static SingletonDirectoryChooser getInstance() {
        return INSTANCE;
    }

    public SingletonDirectoryChooser setTitle(String title) {
        directoryChooser.setTitle(title);
        return this;
    }

    public File showDialog(Window ownerWindow) {
        File chosenDirectory = directoryChooser.showDialog(ownerWindow);
        if (chosenDirectory != null) {
            lastKnownDirectoryProperty.setValue(chosenDirectory.getAbsoluteFile());
        }
        return chosenDirectory;
    }
}
