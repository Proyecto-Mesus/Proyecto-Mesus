package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Evento;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;
import es.cifpcarlos3.proyecto_mesus_javafx.services.EventoService;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.SessionManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;

public class BuscarEventosController {
    @FXML
    private ListView<Evento> listViewEventos;
    @FXML
    private Button btnInscribirse;

    private EventosController eventosController;

    private final EventoService eventoService = new EventoService();

    public void setEventosController(EventosController eventosController) {
        this.eventosController = eventosController;
    }

    @FXML
    public void initialize() {
        btnInscribirse.setDisable(true);

        listViewEventos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean haySeleccion = newVal != null;
            btnInscribirse.setDisable(!haySeleccion);
        });

        listViewEventos.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Evento item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/evento_item.fxml"));
                        Parent root = loader.load();

                        EventoItemController controller = loader.getController();
                        controller.setData(item);

                        setGraphic(root);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });

        try {
            listViewEventos.getItems().setAll(
                    eventoService.getEventosByNotUsuarioId(SessionManager.getUsuarioActual().getId())
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void inscribirse() {
        try {
            eventoService.addUsuario(listViewEventos.getSelectionModel().getSelectedItem().getId(), SessionManager.getUsuarioActual().getId());
            showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Inscripción exitosa",
                    "¡Te has registrado correctamente en el evento "+listViewEventos.getSelectionModel().getSelectedItem().getNombre()+ "!");
            listViewEventos.getItems().setAll(
                    eventoService.getEventosByNotUsuarioId(SessionManager.getUsuarioActual().getId())
            );
            eventosController.refrescarMisEventos();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error de conexión", "No se pudo conectar con el servidor de la API.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
