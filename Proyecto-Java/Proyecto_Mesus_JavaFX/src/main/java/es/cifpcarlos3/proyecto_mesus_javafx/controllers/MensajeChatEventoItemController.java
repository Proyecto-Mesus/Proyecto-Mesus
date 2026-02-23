package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Evento;
import es.cifpcarlos3.proyecto_mesus_javafx.models.MensajeChatEvento;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

public class MensajeChatEventoItemController {

    @FXML
    private Label textNombreRemitente;
    @FXML private Label textContenido;
    @FXML private Label textFechaEnvio;

    public void setData(MensajeChatEvento mensajeChatEvento) {
        textNombreRemitente.setText(mensajeChatEvento.getNombreRemitente());
        textContenido.setText(mensajeChatEvento.getContenido());
        textFechaEnvio.setText(mensajeChatEvento.getFechaEnvio().format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}
