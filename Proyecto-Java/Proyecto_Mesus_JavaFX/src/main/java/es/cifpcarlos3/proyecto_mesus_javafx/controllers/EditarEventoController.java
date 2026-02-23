package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Evento;
import es.cifpcarlos3.proyecto_mesus_javafx.services.ChatEventoService;
import es.cifpcarlos3.proyecto_mesus_javafx.services.EventoService;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.GoogleMapsService;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.SessionManager;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

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
    private WebView map;
    private WebEngine webEngine;
    @FXML
    private Label textUbicacion;
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

    private Double latitud;
    private Double longitud;

    private Evento evento;
    private Stage stage;
    private final EventoService eventoService = new EventoService();
    private final ChatEventoService chatEventoService = new ChatEventoService();
    private final GoogleMapsService googleMapsService = new GoogleMapsService();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    @FXML
    public void initialize() {
        webEngine = map.getEngine();
        webEngine.load(getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/html/mapa.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("app", this); // expone métodos de Java a JS

                cargarDatos(evento);
            }
        });

        spinnerHora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        spinnerMinuto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        datePicker.getEditor().setDisable(true);
        datePicker.setEditable(false);
    }

    @FXML
    private void editarEvento() {
        String nombre = textFieldNombre.getText();
        String descripcion = textFieldDescripcion.getText();

        LocalDate fecha = datePicker.getValue();
        int hora = spinnerHora.getValue();
        int minuto = spinnerMinuto.getValue();

        LocalDateTime fechaHora = fecha.atTime(hora, minuto);


        if(nombre == null || nombre.trim().isEmpty() ||
                descripcion == null || descripcion.trim().isEmpty() ||
                latitud == null ||longitud == null) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, rellene todos los campos.");
            return;
        }

        if(fechaHora.isBefore(LocalDateTime.now())) {
            showAlert(Alert.AlertType.WARNING, "Fecha errónea", "Por favor, la fecha del evento debe de ser posterior a la fecha actual.");
            return;
        }

        try {
            Evento eventoModificado = eventoService.updateEvento(evento.getId(), new Evento(nombre, descripcion, latitud.floatValue(), longitud.floatValue(), fechaHora));

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

    private void cargarDatos(Evento evento) {
        textFieldNombre.setText(evento.getNombre());
        textFieldDescripcion.setText(evento.getDescripcion());
        webEngine.executeScript("map.setView([" + evento.getLatitud() + ", " + evento.getLongitud() + "], 15);");
        try {
            textUbicacion.setText(googleMapsService.obtenerDireccion(evento.getLatitud(), evento.getLongitud()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        datePicker.setValue(evento.getFecha().toLocalDate());
        spinnerHora.getValueFactory().setValue(evento.getFecha().getHour());
        spinnerMinuto.getValueFactory().setValue(evento.getFecha().getMinute());
    }

    public void onLocationSelected(double lat, double lng) {
        latitud = lat;
        longitud = lng;
        try {
            textUbicacion.setText(googleMapsService.obtenerDireccion(latitud, longitud));
        } catch (Exception e) {
            textUbicacion.setText("Error API Google Maps");
            System.out.println(e.getMessage());
        }
    }
}
