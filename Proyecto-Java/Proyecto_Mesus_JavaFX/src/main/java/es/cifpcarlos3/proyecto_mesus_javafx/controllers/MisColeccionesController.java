package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.MainApplication;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
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
import java.util.List;
import java.util.Optional;

public class MisColeccionesController {
    @FXML
    private ListView<Coleccion> listViewColecciones;

    private MenuController menuController;

    @FXML
    Button btnEditarColeccion;

    @FXML
    Button btnBorrarColeccion;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    private final ColeccionService coleccionService = new ColeccionService();

    @FXML
    public void initialize() {
        btnEditarColeccion.setDisable(true);
        btnBorrarColeccion.setDisable(true);

        listViewColecciones.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean haySeleccion = newVal != null;
            btnEditarColeccion.setDisable(!haySeleccion);
            btnBorrarColeccion.setDisable(!haySeleccion);
        });


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
                    menuController.cargarColeccionPropia(seleccionada);
                }
            }
        });


        try {
            listViewColecciones.getItems().setAll(
                    coleccionService.getColeccionesByUsuarioId(SessionManager.getUsuarioActual().getId())
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void abrirCrearColeccion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/crear_coleccion_view.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Crear colección");
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);
            // bloquea la ventana principal

            stage.setResizable(false);

            // pasa el stage al controlador
            CrearColeccionController controller = loader.getController();
            controller.setStage(stage);

            stage.showAndWait();

            try {
                listViewColecciones.getItems().setAll(
                        coleccionService.getColeccionesByUsuarioId(SessionManager.getUsuarioActual().getId())
                );
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void borrarColeccion() {
        Coleccion coleccion = listViewColecciones.getSelectionModel().getSelectedItem();
        if (coleccion != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText("¿Estás seguro de que quieres borrar esta colección?");
            alert.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    coleccionService.deleteColeccion(coleccion.getId());
                    listViewColecciones.getItems().setAll(
                            coleccionService.getColeccionesByUsuarioId(SessionManager.getUsuarioActual().getId())
                    );
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @FXML
    private void abrirEditarColeccion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/editar_coleccion_view.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Modificar colección");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana principal

            stage.setResizable(false);

            // pasa el stage al controlador
            EditarColeccionController controller = loader.getController();
            controller.setStage(stage);
            controller.setColeccion(listViewColecciones.getSelectionModel().getSelectedItem());

            stage.showAndWait();

            try {
                listViewColecciones.getItems().setAll(
                        coleccionService.getColeccionesByUsuarioId(SessionManager.getUsuarioActual().getId())
                );
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
