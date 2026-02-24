package es.cifpcarlos3.proyecto_mesus_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MainApplication extends Application {
    private static Stage stage;
    private static Scene scene;


    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;

        Parent root = loadFXML("login_view");
        scene = new Scene(root, 1280, 720);

        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.setTitle("CardCollection");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        return new FXMLLoader(MainApplication.class.getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/" + fxml + ".fxml")).load();
    }
}
