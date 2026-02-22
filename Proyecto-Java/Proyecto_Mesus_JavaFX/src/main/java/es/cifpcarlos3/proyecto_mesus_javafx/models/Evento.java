package es.cifpcarlos3.proyecto_mesus_javafx.models;

import java.time.LocalDateTime;
import java.util.List;

public class Evento {
    private int id;
    private String nombre;
    private String descripcion;
    private Float latitud;
    private Float longitud;
    private LocalDateTime fecha;
    private List<Usuario> usuarios;
    private ChatEvento chatEvento;
    private Usuario creador;

    public Evento() {
    }

    public Evento(int id, String nombre, String descripcion, Float latitud, Float longitud, LocalDateTime fecha,
            List<Usuario> usuarios, ChatEvento chatEvento, Usuario creador) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
        this.usuarios = usuarios;
        this.chatEvento = chatEvento;
        this.creador = creador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getLatitud() {
        return latitud;
    }

    public void setLatitud(Float latitud) {
        this.latitud = latitud;
    }

    public Float getLongitud() {
        return longitud;
    }

    public void setLongitud(Float longitud) {
        this.longitud = longitud;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public ChatEvento getChatEvento() {
        return chatEvento;
    }

    public void setChatEvento(ChatEvento chatEvento) {
        this.chatEvento = chatEvento;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", fecha=" + fecha +
                ", creador=" + (creador != null ? creador.getId() : null) +
                '}';
    }
}
