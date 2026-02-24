package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Carta;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.services.CartaService;
import es.cifpcarlos3.proyecto_mesus_javafx.services.ColeccionService;
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

public class ColeccionPropiaController {
    @FXML
    private ListView<Carta> listViewCartas;

    @FXML
    private Button btnEditarCarta;
    @FXML
    private Button btnBorrarCarta;

    private Coleccion coleccion;

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

    @FXML
    public void initialize() {
        btnEditarCarta.setDisable(true);
        btnBorrarCarta.setDisable(true);

        listViewCartas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean haySeleccion = newVal != null;
            btnEditarCarta.setDisable(!haySeleccion);
            btnBorrarCarta.setDisable(!haySeleccion);
        });

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
    private void abrirMisColecciones() {
        menuController.cargarMisColecciones();
    }

    @FXML
    private void abrirCrearCarta() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/crear_carta_view.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Crear carta");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana principal

            stage.setResizable(false);

            // pasa el stage al controlador
            CrearCartaController controller = loader.getController();
            controller.setColeccion(coleccion);
            controller.setStage(stage);

            stage.showAndWait();

            try {
                listViewCartas.getItems().setAll(
                        cartaService.getCartasByColeccionId(coleccion.getId())
                );
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void abrirEditarCarta() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/editar_carta_view.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Modificar colección");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana principal

            stage.setResizable(false);

            // pasa el stage al controlador
            EditarCartaController controller = loader.getController();
            controller.setStage(stage);
            controller.setColeccion(coleccion);
            controller.setCarta(listViewCartas.getSelectionModel().getSelectedItem());

            stage.showAndWait();

            try {
                listViewCartas.getItems().setAll(
                        cartaService.getCartasByColeccionId(coleccion.getId())
                );
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void borrarCarta() {
        Carta carta = listViewCartas.getSelectionModel().getSelectedItem();
        if (carta != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText("¿Estás seguro de que quieres borrar esta carta?");
            alert.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    cartaService.deleteCarta(carta.getId());
                    listViewCartas.getItems().setAll(
                            cartaService.getCartasByColeccionId(coleccion.getId())
                    );
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
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
