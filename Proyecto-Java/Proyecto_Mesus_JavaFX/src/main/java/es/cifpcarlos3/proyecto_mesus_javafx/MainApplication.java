package es.cifpcarlos3.proyecto_mesus_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    private static Stage stage;


    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        setRoot("login_view");
        stage.setTitle("CardVault");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        Scene scene = new Scene(loadFXML(fxml)); stage.setScene(scene);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        return new FXMLLoader(MainApplication.class.getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/" + fxml + ".fxml")).load();
    }
}
