package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ColeccionItemController {

    @FXML private ImageView img;
    @FXML
    private Label textNombre;
    @FXML private Label textJuego;
    @FXML private Label textVisibilidad;

    public void setData(Coleccion coleccion) {
        textNombre.setText(coleccion.getNombre());
        textJuego.setText(coleccion.getJuego().getNombre());
        textVisibilidad.setText(coleccion.getPublica() ? "Pública" : "Privada");
    }
}
