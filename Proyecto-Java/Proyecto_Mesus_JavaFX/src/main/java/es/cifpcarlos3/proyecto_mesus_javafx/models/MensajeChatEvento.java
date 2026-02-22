package es.cifpcarlos3.proyecto_mesus_javafx.models;

import java.time.LocalDateTime;

public class MensajeChatEvento {
    private int id;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private ChatEvento chatEvento;
    private String nombreRemitente;

    public MensajeChatEvento() {
    }

    public MensajeChatEvento(int id, String contenido, LocalDateTime fechaEnvio, ChatEvento chatEvento,
            String nombreRemitente) {
        this.id = id;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.chatEvento = chatEvento;
        this.nombreRemitente = nombreRemitente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public ChatEvento getChatEvento() {
        return chatEvento;
    }

    public void setChatEvento(ChatEvento chatEvento) {
        this.chatEvento = chatEvento;
    }

    public String getNombreRemitente() {
        return nombreRemitente;
    }

    public void setNombreRemitente(String nombreRemitente) {
        this.nombreRemitente = nombreRemitente;
    }

    @Override
    public String toString() {
        return "MensajeChatEvento{" +
                "id=" + id +
                ", contenido='" + contenido + '\'' +
                ", fechaEnvio=" + fechaEnvio +
                ", chatEvento=" + chatEvento +
                ", nombreRemitente='" + nombreRemitente + '\'' +
                '}';
    }
}
