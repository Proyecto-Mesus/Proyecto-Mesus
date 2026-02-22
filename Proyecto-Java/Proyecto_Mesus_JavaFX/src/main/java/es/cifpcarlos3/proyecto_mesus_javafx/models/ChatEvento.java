package es.cifpcarlos3.proyecto_mesus_javafx.models;

import java.util.List;

public class ChatEvento {
    private int id;
    private Evento evento;
    private List<MensajeChatEvento> mensajes;

    public ChatEvento() {
    }

    public ChatEvento(int id, Evento evento, List<MensajeChatEvento> mensajes) {
        this.id = id;
        this.evento = evento;
        this.mensajes = mensajes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public List<MensajeChatEvento> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<MensajeChatEvento> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public String toString() {
        return "ChatEvento{" +
                "id=" + id +
                ", evento=" + evento +
                "}"; // avoid infinite recursion with mensajes
    }
}
