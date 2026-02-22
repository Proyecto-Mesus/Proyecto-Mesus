package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;
import es.cifpcarlos3.proyecto_mesus_javafx.services.ColeccionService;
import es.cifpcarlos3.proyecto_mesus_javafx.services.UsuarioService;
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

public class ColeccionesAjenasController {
    @FXML
    private ListView<Coleccion> listViewColecciones;

    private MenuController menuController;

    private Usuario usuario;

    @FXML
    Button btnEditarColeccion;

    @FXML
    Button btnBorrarColeccion;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        obtenerColecciones();
    }

    private final ColeccionService coleccionService = new ColeccionService();

    @FXML
    public void initialize() {
        listViewColecciones.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Coleccion item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/coleccion_item.fxml"));
                        Parent root = loader.load();

                        ColeccionItemController controller = loader.getController();
                        controller.setData(item);

                        setGraphic(root);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });

        listViewColecciones.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Coleccion seleccionada = listViewColecciones.getSelectionModel().getSelectedItem();
                if (seleccionada != null) {
                    menuController.cargarColeccionAjena(usuario, seleccionada);
                }
            }
        });
    }

    @FXML
    private void abrirBuscarColecciones() {
        menuController.cargarBuscarColecciones();
    }

    private void obtenerColecciones() {
        try {
            listViewColecciones.getItems().setAll(coleccionService.getColeccionesPublicasByUsuarioId(usuario.getId()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
