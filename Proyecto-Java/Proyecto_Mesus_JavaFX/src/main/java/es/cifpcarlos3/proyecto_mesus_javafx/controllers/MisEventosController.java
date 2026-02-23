package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.ChatEvento;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Evento;
import es.cifpcarlos3.proyecto_mesus_javafx.services.ChatEventoService;
import es.cifpcarlos3.proyecto_mesus_javafx.services.EventoService;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MisEventosController {
    @FXML
    private ListView<Evento> listViewEventos;
    @FXML
    private Button btnChat;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnBorrar;


    private EventosController eventosController;

    private final EventoService eventoService = new EventoService();
    private final ChatEventoService chatEventoService = new ChatEventoService();

    public void setEventosController(EventosController eventosController) {
        this.eventosController = eventosController;
    }

    @FXML
    public void initialize() {
        btnChat.setDisable(true);
        btnEditar.setDisable(true);
        btnBorrar.setDisable(true);

        listViewEventos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean haySeleccion = newVal != null;
            btnChat.setDisable(!haySeleccion);
            btnEditar.setDisable(!haySeleccion || newVal.getCreador().getId() != SessionManager.getUsuarioActual().getId());
            btnBorrar.setDisable(!haySeleccion || newVal.getCreador().getId() != SessionManager.getUsuarioActual().getId());
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

        cargarEventos();
    }

    @FXML
    private void abrirCrearEvento() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/crear_evento_view.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Crear evento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana principal

            // pasa el stage al controlador
            CrearEventoController controller = loader.getController();
            controller.setStage(stage);

            stage.showAndWait();

            cargarEventos();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void abrirChatEvento() {
        try {
            eventosController.cargarChatEvento(chatEventoService.getChatEventoByEventoId(listViewEventos.getSelectionModel().getSelectedItem().getId()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void abrirEditarEvento() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/editar_evento_view.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Modificar evento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana principal

            // pasa el stage al controlador
            EditarEventoController controller = loader.getController();
            controller.setStage(stage);
            controller.setEvento(listViewEventos.getSelectionModel().getSelectedItem());

            stage.showAndWait();

            cargarEventos();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void borrarEvento() {
        Evento evento = listViewEventos.getSelectionModel().getSelectedItem();
        if (evento != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText("¿Estás seguro de que quieres borrar este evento?");
            alert.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    eventoService.deleteEvento(evento.getId());
                    listViewEventos.getItems().setAll(
                            eventoService.getEventosByUsuarioId(SessionManager.getUsuarioActual().getId())
                    );
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void cargarEventos() {
        try {
            listViewEventos.getItems().setAll(
                    eventoService.getEventosByUsuarioId(SessionManager.getUsuarioActual().getId())
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
