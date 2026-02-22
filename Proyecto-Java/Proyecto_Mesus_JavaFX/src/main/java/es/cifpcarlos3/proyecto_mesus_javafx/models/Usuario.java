package es.cifpcarlos3.proyecto_mesus_javafx.models;

import java.util.List;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String password;
    private String email;
    private List<Coleccion> colecciones;
    private List<Evento> eventos;
    private List<Evento> eventosCreados;

    public Usuario() {
    }

    public Usuario(int id, String nombreUsuario, String password, String email, List<Coleccion> colecciones,
            List<Evento> eventos, List<Evento> eventosCreados) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.email = email;
        this.colecciones = colecciones;
        this.eventos = eventos;
        this.eventosCreados = eventosCreados;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Coleccion> getColecciones() {
        return colecciones;
    }

    public void setColecciones(List<Coleccion> colecciones) {
        this.colecciones = colecciones;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }

    public List<Evento> getEventosCreados() {
        return eventosCreados;
    }

    public void setEventosCreados(List<Evento> eventosCreados) {
        this.eventosCreados = eventosCreados;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
