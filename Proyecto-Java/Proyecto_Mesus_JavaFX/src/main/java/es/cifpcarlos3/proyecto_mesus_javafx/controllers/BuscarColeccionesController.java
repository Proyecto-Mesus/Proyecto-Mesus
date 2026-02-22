package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Carta;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;
import es.cifpcarlos3.proyecto_mesus_javafx.services.UsuarioService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.List;

public class BuscarColeccionesController {
    @FXML
    private ListView<Usuario> listViewUsuarios;

    @FXML
    private TextField textFieldUsuario;

    private final UsuarioService usuarioService = new UsuarioService();

    private MenuController menuController;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    @FXML
    public void initialize() {
        listViewUsuarios.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/usuario_item.fxml"));
                        Parent root = loader.load();

                        UsuarioItemController controller = loader.getController();
                        controller.setData(item);

                        setGraphic(root);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });

        listViewUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Usuario seleccionado = listViewUsuarios.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    menuController.cargarColeccionesAjenas(seleccionado);
                }
            }
        });

        textFieldUsuario.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldValue, String newValue) {
                buscarUsuarios(newValue);
            }
        });
    }

    private void buscarUsuarios(String nombre) {
        if (nombre != null && !nombre.isEmpty()) {
            try {
                listViewUsuarios.getItems().setAll(usuarioService.getUsuariosByNombre(nombre));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            listViewUsuarios.getItems().clear();
        }
    }

}
