package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Carta;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CartaItemController {
    @FXML
    private ImageView img;
    @FXML
    private Label textNombre;
    @FXML private Label textSet;
    @FXML private Label textNumeroSet;

    public void setData(Carta carta) {
        textNombre.setText(carta.getNombre());
        textSet.setText(carta.getSet());
        textNumeroSet.setText(carta.getNumeroSet());
        img.setImage(new Image(carta.getImagen(), true));
    }
}
