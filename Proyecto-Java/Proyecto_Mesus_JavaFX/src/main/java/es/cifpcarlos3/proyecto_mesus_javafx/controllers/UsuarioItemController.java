package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Carta;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UsuarioItemController {
    @FXML
    private Label textNombre;

    public void setData(Usuario usuario) {
        textNombre.setText(usuario.getNombreUsuario());
    }

}
