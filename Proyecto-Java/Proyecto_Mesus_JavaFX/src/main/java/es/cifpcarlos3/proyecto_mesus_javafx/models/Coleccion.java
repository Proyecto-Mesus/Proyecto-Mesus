package es.cifpcarlos3.proyecto_mesus_javafx.models;

import java.util.List;

public class Coleccion {
    private int id;
    private String nombre;
    private Usuario usuario;
    private Juego juego;
    private Boolean publica;
    private List<Carta> cartas;

    public Coleccion() {
    }

    public Coleccion(int id, String nombre, Usuario usuario, Juego juego, Boolean publica, List<Carta> cartas) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.juego = juego;
        this.publica = publica;
        this.cartas = cartas;
    }

    public Coleccion(String nombre, Usuario usuario, Juego juego, Boolean publica) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.juego = juego;
        this.publica = publica;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Juego getJuego() {
        return juego;
    }

    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    public Boolean getPublica() {
        return publica;
    }

    public void setPublica(Boolean publica) {
        this.publica = publica;
    }

    public List<Carta> getCartas() {
        return cartas;
    }

    public void setCartas(List<Carta> cartas) {
        this.cartas = cartas;
    }

    @Override
    public String toString() {
        return "Coleccion{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", usuario=" + (usuario != null ? usuario.getId() : null) +
                ", juego=" + (juego != null ? juego.getId() : null) +
                ", publica=" + publica +
                '}';
    }
}
