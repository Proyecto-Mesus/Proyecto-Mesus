package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.IOException;


public class MenuController {

    @FXML private Tab tabMisColecciones;
    @FXML private Tab tabBuscarColecciones;
    @FXML private Tab tabEventos;

    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/mis_colecciones_view.fxml")
            );
            Parent root = loader.load();

            MisColeccionesController misColeccionesController = loader.getController();
            misColeccionesController.setMenuController(this);

            tabMisColecciones.setContent(root);

            loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/buscar_colecciones_view.fxml")
            );
            root = loader.load();

            BuscarColeccionesController buscarColeccionesController = loader.getController();
            buscarColeccionesController.setMenuController(this);


            tabBuscarColecciones.setContent(root);


            tabEventos.setContent(FXMLLoader.load(getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/eventos_view.fxml")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void cargarColeccionPropia(Coleccion coleccion) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/coleccion_propia_view.fxml")
            );

            Parent root = loader.load();

            ColeccionPropiaController controller = loader.getController();
            controller.setMenuController(this);
            controller.setColeccion(coleccion);

            tabMisColecciones.setContent(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void cargarMisColecciones() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/mis_colecciones_view.fxml")
            );

            Parent root = loader.load();

            MisColeccionesController controller = loader.getController();
            controller.setMenuController(this);

            tabMisColecciones.setContent(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void cargarBuscarColecciones() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/buscar_colecciones_view.fxml")
            );

            Parent root = loader.load();

            BuscarColeccionesController controller = loader.getController();
            controller.setMenuController(this);

            tabBuscarColecciones.setContent(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void cargarColeccionesAjenas(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/colecciones_ajenas_view.fxml")
            );

            Parent root = loader.load();

            ColeccionesAjenasController controller = loader.getController();
            controller.setMenuController(this);
            controller.setUsuario(usuario);

            tabBuscarColecciones.setContent(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void cargarColeccionAjena(Usuario usuario, Coleccion coleccion) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/cifpcarlos3/proyecto_mesus_javafx/views/coleccion_ajena_view.fxml")
            );

            Parent root = loader.load();

            ColeccionAjenaController controller = loader.getController();
            controller.setMenuController(this);
            controller.setUsuario(usuario);
            controller.setColeccion(coleccion);

            tabBuscarColecciones.setContent(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
