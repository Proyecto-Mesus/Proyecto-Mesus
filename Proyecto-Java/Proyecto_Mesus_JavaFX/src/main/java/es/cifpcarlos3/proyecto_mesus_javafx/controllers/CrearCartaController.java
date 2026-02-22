package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Carta;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Juego;
import es.cifpcarlos3.proyecto_mesus_javafx.services.CartaService;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.CloudinaryUploader;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class CrearCartaController {
    @FXML
    private ImageView img;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldSet;
    @FXML
    private TextField textFieldNumeroSet;
    @FXML
    private Button btnCancelar;

    private File imagenSeleccionada;

    private final CartaService cartaService = new CartaService();

    private Coleccion coleccion;
    private Stage stage;

    public void setColeccion(Coleccion coleccion) {
        this.coleccion = coleccion;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) img.getScene().getWindow();
        imagenSeleccionada = fileChooser.showOpenDialog(stage);

        if (imagenSeleccionada != null) {
            img.setImage(new Image(imagenSeleccionada.toURI().toString()));
        }
    }

    @FXML
    private void crearCarta() {
        String nombre = textFieldNombre.getText();
        String set = textFieldSet.getText();
        String numeroSet = textFieldNumeroSet.getText();

        if(nombre == null || nombre.trim().isEmpty() ||
                set == null || set.trim().isEmpty() ||
                numeroSet == null || numeroSet.trim().isEmpty() ||
                imagenSeleccionada == null) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, rellene todos los campos.");
            return;
        }

        try {
            String url = CloudinaryUploader.uploadImage(imagenSeleccionada);

            Carta carta = cartaService.createCarta(new Carta(nombre, set, numeroSet, coleccion, url));

            if (carta != null) {
                showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Carta creada exitosamente",
                        "¡Carta " + carta.getNombre() + " creada correctamente!");

                Stage stage = (Stage) btnCancelar.getScene().getWindow(); stage.close();
                stage.close();

            } else {
                showAlert(Alert.AlertType.ERROR, "Error de creación", "No se pudo crear la carta");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error de conexión", "No se pudo conectar con el servidor de la API.");
        }
    }

    @FXML
    private void salir() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow(); stage.close();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
