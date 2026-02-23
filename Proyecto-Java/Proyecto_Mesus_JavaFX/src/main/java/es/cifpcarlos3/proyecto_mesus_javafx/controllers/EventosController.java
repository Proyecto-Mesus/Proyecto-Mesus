package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.ChatEvento;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.IOException;

public class EventosController {

    @FXML private Tab tabBuscarEventos;
    @FXML private Tab tabMisEventos;

    private MisEventosController misEventosController;
    private BuscarEventosController buscarEventosController;

    public void setMisEventosController(MisEventosController misEventosController) {
        this.misEventosController = misEventosController;
    }
    public void setBuscarEventosController(BuscarEventosController buscarEventosController) {
        this.buscarEventosController = buscarEventosController;
    }

    @FXML
    public void initialize() {
        cargarBuscarEventos();
        cargarMisEventos();
    }

    public void cargarBuscarEventos() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/buscar_eventos_view.fxml")
            );

            Parent root = loader.load();

            buscarEventosController = loader.getController();
            buscarEventosController.setEventosController(this);

            tabBuscarEventos.setContent(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void cargarMisEventos() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/mis_eventos_view.fxml")
            );

            Parent root = loader.load();

            misEventosController = loader.getController();
            misEventosController.setEventosController(this);

            tabMisEventos.setContent(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void cargarChatEvento(ChatEvento chatEvento) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/chat_evento_view.fxml")
            );

            Parent root = loader.load();

            ChatEventoController controller = loader.getController();
            controller.setEventosController(this);
            controller.setChatEvento(chatEvento);

            tabMisEventos.setContent(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void refrescarMisEventos() {
        if(misEventosController != null) {
            misEventosController.cargarEventos();
        }
    }
}
