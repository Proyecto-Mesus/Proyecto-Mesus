package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Evento;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Juego;
import es.cifpcarlos3.proyecto_mesus_javafx.services.ChatEventoService;
import es.cifpcarlos3.proyecto_mesus_javafx.services.ColeccionService;
import es.cifpcarlos3.proyecto_mesus_javafx.services.EventoService;
import es.cifpcarlos3.proyecto_mesus_javafx.services.JuegoService;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CrearEventoController {
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldDescripcion;
    @FXML
    private TextField textFieldLongitud;
    @FXML
    private TextField textFieldLatitud;
    @FXML
    DatePicker datePicker;
    @FXML
    Spinner<Integer> spinnerHora;
    @FXML
    Spinner<Integer> spinnerMinuto;

    @FXML
    private Button btnCrearEvento;
    @FXML
    private Button btnCancelar;

    private Stage stage;
    private final EventoService eventoService = new EventoService();
    private final ChatEventoService chatEventoService = new ChatEventoService();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        spinnerHora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        spinnerMinuto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        datePicker.getEditor().setDisable(true);
        datePicker.setEditable(false);
    }

    @FXML
    private void crearEvento() {
        String nombre = textFieldNombre.getText();
        String descripcion = textFieldDescripcion.getText();
        String longitud = textFieldLongitud.getText();
        String latitud = textFieldLatitud.getText();
        float longitudFloat;
        float latitudFloat;

        LocalDate fecha = datePicker.getValue();
        int hora = spinnerHora.getValue();
        int minuto = spinnerMinuto.getValue();

        LocalDateTime fechaHora = fecha.atTime(hora, minuto);



        if(nombre == null || nombre.trim().isEmpty() ||
                descripcion == null || descripcion.trim().isEmpty() ||
                longitud == null || longitud.trim().isEmpty() ||
                latitud == null || latitud.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, rellene todos los campos.");
            return;
        }

        try {
            longitudFloat = Float.parseFloat(longitud);
            latitudFloat = Float.parseFloat(latitud);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            showAlert(Alert.AlertType.WARNING, "Ubicación errónea", "Por favor, la longitud y latitud deben ser float.");
            return;
        }

        if(fechaHora.isBefore(LocalDateTime.now())) {
            showAlert(Alert.AlertType.WARNING, "Fecha errónea", "Por favor, la fecha del evento debe de ser posterior a la fecha actual.");
            return;
        }

        try {
            Evento evento = eventoService.createEvento(new Evento(nombre, descripcion, latitudFloat, longitudFloat, fechaHora,
                    SessionManager.getUsuarioActual()));

            if (evento != null) {
                chatEventoService.createChatEvento(evento.getId());
                eventoService.addUsuario(evento.getId(), SessionManager.getUsuarioActual().getId());
                showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Evento creado exitosamente",
                        "¡Evento " + evento.getNombre() + " creado correctamente!");


                Stage stage = (Stage) btnCancelar.getScene().getWindow(); stage.close();
                stage.close();

            } else {
                showAlert(Alert.AlertType.ERROR, "Error de creación", "No se pudo crear el evento");
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
