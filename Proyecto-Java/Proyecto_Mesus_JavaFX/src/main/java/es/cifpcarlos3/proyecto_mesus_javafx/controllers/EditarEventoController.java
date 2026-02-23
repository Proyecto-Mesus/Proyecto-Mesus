package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Evento;
import es.cifpcarlos3.proyecto_mesus_javafx.services.ChatEventoService;
import es.cifpcarlos3.proyecto_mesus_javafx.services.EventoService;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class EditarEventoController {
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
    private Button btnConfirmar;
    @FXML
    private Button btnCancelar;

    private Evento evento;
    private Stage stage;
    private final EventoService eventoService = new EventoService();
    private final ChatEventoService chatEventoService = new ChatEventoService();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
        cargarDatos();
    }

    @FXML
    public void initialize() {
        spinnerHora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        spinnerMinuto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        datePicker.getEditor().setDisable(true);
        datePicker.setEditable(false);
    }

    @FXML
    private void editarEvento() {
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
            Evento eventoModificado = eventoService.updateEvento(evento.getId(), new Evento(nombre, descripcion, latitudFloat, longitudFloat, fechaHora));

            if (eventoModificado != null) {
                showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Evento modificado exitosamente",
                        "¡Evento " + eventoModificado.getNombre() + " modificado correctamente!");

                Stage stage = (Stage) btnCancelar.getScene().getWindow(); stage.close();
                stage.close();

            } else {
                showAlert(Alert.AlertType.ERROR, "Error de edición", "No se pudo modificar el evento");
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
        textFieldNombre.setText(evento.getNombre());
        textFieldDescripcion.setText(evento.getDescripcion());
        textFieldLatitud.setText(evento.getLatitud().toString());
        textFieldLongitud.setText(evento.getLongitud().toString());

        datePicker.setValue(evento.getFecha().toLocalDate());
        spinnerHora.getValueFactory().setValue(evento.getFecha().getHour());
        spinnerMinuto.getValueFactory().setValue(evento.getFecha().getMinute());
    }
}
