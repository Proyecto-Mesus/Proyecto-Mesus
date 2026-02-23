package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Carta;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Evento;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.GoogleMapsService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EventoItemController {

    @FXML private Label textNombre;
    @FXML private Label textDescripcion;
    @FXML private Label textUbicacion;
    @FXML private Label textFecha;
    @FXML private Label textOrganizador;

    GoogleMapsService maps = new GoogleMapsService();

    public void setData(Evento evento) {
        textNombre.setText(evento.getNombre());
        textDescripcion.setText(evento.getDescripcion());
        try {
            textUbicacion.setText(maps.obtenerDireccion(evento.getLatitud(), evento.getLongitud()));
        } catch (Exception e) {
            textUbicacion.setText("Error API Google Maps");
            System.out.println(e.getMessage());
        }
        textFecha.setText(evento.getFecha().toString());
        textOrganizador.setText(evento.getCreador().getNombreUsuario());
    }
}
