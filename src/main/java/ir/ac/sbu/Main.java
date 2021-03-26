package ir.ac.sbu;

import ir.ac.sbu.utility.ResourceUtility;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(ResourceUtility.getResource("fxml/Main.fxml"));
        getPrimaryStage().setTitle("PGen");
        getPrimaryStage().getIcons().add(new Image(ResourceUtility.getResourceAsStream("assets/Icon.png")));
        getPrimaryStage().setScene(createScene(root));
        getPrimaryStage().setMaximized(true);
        getPrimaryStage().show();
    }

    private static Scene createScene(Parent root) {
        Scene scene = new Scene(root);
        scene.getStylesheets().addAll(ResourceUtility.getResource("styles/styles.css").toString());
        return scene;
    }
}
