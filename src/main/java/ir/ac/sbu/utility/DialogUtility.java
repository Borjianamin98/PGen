package ir.ac.sbu.utility;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class DialogUtility {
    private DialogUtility() {
    }

    public static Optional<String> showInputDialog(String defaultValue, String title) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        setIcon(dialog, "assets/dialog/Information.png");
        return dialog.showAndWait();
    }

    public static Optional<ButtonType> showConfirmationDialog(String title, String message) {
        Dialog<ButtonType> dialog = CustomDialogWithLabel(title, "assets/dialog/Information.png",
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        setContent(dialog, new Label(message));
        return dialog.showAndWait();
    }

    public static File showSaveDialog(Window owner, String title, String... extensions) {
        SingletonFileChooser.setExtensions(extensions);
        SingletonFileChooser.setTitle(title);
        return SingletonFileChooser.showSaveDialog(owner);
    }

    public static File showOpenDialog(Window owner, String... extensions) {
        SingletonFileChooser.setExtensions(extensions);
        SingletonFileChooser.setTitle("Open a File");
        return SingletonFileChooser.showOpenDialog(owner);
    }

    public static File showDirectoryDialog(Window owner) {
        SingletonDirectoryChooser.setTitle("Select a directory");
        return SingletonDirectoryChooser.showDialog(owner);
    }

    public static void showSuccessDialog(String message) {
        Dialog<ButtonType> dialog = CustomDialogWithLabel("Result", "assets/dialog/Success.png", ButtonType.OK);
        setContent(dialog, new Label(message));
        dialog.showAndWait();
    }

    public static void showErrorDialog(String message) {
        showErrorDialog("Error", message);
    }

    public static void showErrorDialog(String title, String message) {
        Dialog<ButtonType> dialog = CustomDialogWithLabel(title, "assets/dialog/Error.png", ButtonType.OK);
        setContent(dialog, new Label(message));
        dialog.showAndWait();
    }

    public static void showErrorDialog(List<String> messages) {
        Dialog<ButtonType> dialog = CustomDialogWithLabel("Result", "assets/dialog/Error.png", ButtonType.OK);
        setContent(dialog, new ListView<>(FXCollections.observableArrayList(messages)));
        dialog.showAndWait();
    }

    public static boolean showWarningDialog(String message) {
        Dialog<ButtonType> dialog = CustomDialogWithLabel("Confirmation", "assets/dialog/Warning.png", ButtonType.YES, ButtonType.NO);
        setContent(dialog, new Label(message));
        Optional<ButtonType> optional = dialog.showAndWait();
        return optional.isPresent() && optional.get() == ButtonType.YES;
    }

    private static Dialog<ButtonType> CustomDialogWithLabel(String title, String iconPath, ButtonType... buttonTypes) {
        // Create the custom dialog.
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        setIcon(dialog, iconPath);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypes);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        dialog.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        return dialog;
    }

    private static void setContent(Dialog<ButtonType> dialog, Node content) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setPrefWidth(450);
        vBox.getChildren().add(content);
        VBox.setVgrow(content, Priority.ALWAYS);
        dialog.getDialogPane().setContent(vBox);

    }

    private static void setIcon(Dialog<?> dialog, String iconPath) {
        // Set the icon
        dialog.setGraphic(null);

        // Set stage icon
        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().addAll(new Image(ResourceUtility.getResourceAsStream(iconPath)));
    }
}
