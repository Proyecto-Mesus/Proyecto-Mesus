package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Carta;
import es.cifpcarlos3.proyecto_mesus_javafx.models.ChatEvento;
import es.cifpcarlos3.proyecto_mesus_javafx.models.MensajeChatEvento;
import es.cifpcarlos3.proyecto_mesus_javafx.services.MensajeChatEventoService;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDateTime;

public class ChatEventoController {
    @FXML
    private ListView<MensajeChatEvento> listViewMensajes;
    @FXML
    private TextField textFieldMensaje;

    private ChatEvento chatEvento;

    private EventosController eventosController;

    private final MensajeChatEventoService mensajeChatEventoService = new MensajeChatEventoService();

    public void setEventosController(EventosController eventosController) {
        this.eventosController = eventosController;
    }
    public void setChatEvento(ChatEvento chatEvento) {
        this.chatEvento = chatEvento;
        obtenerMensajes();
    }

    @FXML
    public void initialize() {
        listViewMensajes.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(MensajeChatEvento item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/mensaje_chat_evento_item.fxml"));
                        Parent root = loader.load();

                        MensajeChatEventoItemController controller = loader.getController();
                        controller.setData(item);

                        setGraphic(root);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    @FXML
    public void enviarMensaje() {
        String contenido = textFieldMensaje.getText();
        if(contenido != null && !contenido.isEmpty()) {
            try {
                textFieldMensaje.setText("");
                MensajeChatEvento mensaje = new MensajeChatEvento(contenido, LocalDateTime.now(), SessionManager.getUsuarioActual().getNombreUsuario(),
                        SessionManager.getUsuarioActual().getId());
                mensajeChatEventoService.createMensajeChatEvento(chatEvento.getId(), mensaje);
                listViewMensajes.getItems().setAll(
                        mensajeChatEventoService.getMensajesChatEventoByChatEventoId(chatEvento.getId())
                );
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    public void abrirMisEventos() {
        eventosController.cargarMisEventos();
    }

    private void obtenerMensajes() {
        try {
            listViewMensajes.getItems().setAll(
                    mensajeChatEventoService.getMensajesChatEventoByChatEventoId(chatEvento.getId())
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
