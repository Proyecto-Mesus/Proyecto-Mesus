package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Carta;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;
import es.cifpcarlos3.proyecto_mesus_javafx.services.CartaService;
import es.cifpcarlos3.proyecto_mesus_javafx.services.ColeccionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ColeccionAjenaController {
    @FXML
    private ListView<Carta> listViewCartas;

    private Coleccion coleccion;
    private Usuario usuario;

    private MenuController menuController;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    private final CartaService cartaService = new CartaService();

    private final ColeccionService coleccionService = new ColeccionService();

    public void setColeccion(Coleccion coleccion) {
        this.coleccion = coleccion;
        obtenerCartas();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @FXML
    public void initialize() {
        listViewCartas.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Carta item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/carta_item.fxml"));
                        Parent root = loader.load();

                        CartaItemController controller = loader.getController();
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
    private void abrirColeccionesAjenas() {
        menuController.cargarColeccionesAjenas(usuario);
    }

    private void obtenerCartas() {
        try {
            listViewCartas.getItems().setAll(
                    cartaService.getCartasByColeccionId(coleccion.getId())
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
