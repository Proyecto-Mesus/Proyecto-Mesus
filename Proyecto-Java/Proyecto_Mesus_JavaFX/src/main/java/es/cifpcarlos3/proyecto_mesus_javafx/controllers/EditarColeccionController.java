package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.MainApplication;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Juego;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;
import es.cifpcarlos3.proyecto_mesus_javafx.services.ColeccionService;
import es.cifpcarlos3.proyecto_mesus_javafx.services.JuegoService;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class EditarColeccionController {

    @FXML
    private TextField textFieldNombre;
    @FXML
    private ComboBox<Juego> comboBoxJuego;
    @FXML
    private CheckBox checkBoxVisibilidad;
    @FXML
    private Button btnConfirmar;
    @FXML
    private Button btnCancelar;

    private Stage stage;
    private final ColeccionService coleccionService = new ColeccionService();
    private final JuegoService juegoService = new JuegoService();

    private Coleccion coleccion;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setColeccion(Coleccion coleccion) {
        this.coleccion = coleccion;
        cargarDatos();
    }

    @FXML
    public void initialize() {
        comboBoxJuego.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Juego item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre());
                }
            }
        });
        comboBoxJuego.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Juego item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre());
                }
            }
        });

        try {
            List<Juego> juegos = juegoService.getAllJuegos();
            comboBoxJuego.getItems().setAll(juegos);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void editarColeccion() {
        String nombre = textFieldNombre.getText();
        Juego juego = comboBoxJuego.getSelectionModel().getSelectedItem();
        Boolean publica = checkBoxVisibilidad.isSelected();

        if(nombre == null || nombre.trim().isEmpty() || juego == null) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, rellene todos los campos.");
            return;
        }

        try {
            Coleccion coleccionModificada = coleccionService.updateColeccion(coleccion.getId(), new Coleccion(nombre, SessionManager.getUsuarioActual(), juego, publica));

            if (coleccionModificada != null) {
                showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Colección modificada exitosamente",
                        "¡Colección " + coleccion.getNombre() + " modificada correctamente!");

                Stage stage = (Stage) btnCancelar.getScene().getWindow(); stage.close();
                stage.close();

            } else {
                showAlert(Alert.AlertType.ERROR, "Error al editar", "No se pudo modificar la colección");
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

    private void cargarDatos() {
        textFieldNombre.setText(coleccion.getNombre());
        comboBoxJuego.getSelectionModel().select(
                comboBoxJuego.getItems().stream()
                        .filter(j -> j.getId() == coleccion.getJuego().getId())
                        .findFirst()
                        .orElse(null)
        );

        checkBoxVisibilidad.setSelected(coleccion.getPublica());
    }
}