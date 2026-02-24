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

    private final GoogleMapsService googleMapsService = new GoogleMapsService();

    public void setData(Evento evento) {
        textNombre.setText(evento.getNombre());
        textDescripcion.setText(evento.getDescripcion());
        if(evento.getLatitud() != null && evento.getLongitud() != null) {
            try {
                textUbicacion.setText(googleMapsService.obtenerDireccion(evento.getLatitud(), evento.getLongitud()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            textUbicacion.setText("Error API Google Maps");
        }
        textFecha.setText(evento.getFecha().toString());
        textOrganizador.setText(evento.getCreador().getNombreUsuario());
    }
}
