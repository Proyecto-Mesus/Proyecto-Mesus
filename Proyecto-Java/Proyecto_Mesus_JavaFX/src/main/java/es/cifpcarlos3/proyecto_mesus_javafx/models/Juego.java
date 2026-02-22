package es.cifpcarlos3.proyecto_mesus_javafx.models;

import java.util.List;

public class Juego {
    private int id;
    private String nombre;
    private List<Coleccion> colecciones;

    public Juego() {
    }

    public Juego(int id, String nombre, List<Coleccion> colecciones) {
        this.id = id;
        this.nombre = nombre;
        this.colecciones = colecciones;
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

    public List<Coleccion> getColecciones() {
        return colecciones;
    }

    public void setColecciones(List<Coleccion> colecciones) {
        this.colecciones = colecciones;
    }

    @Override
    public String toString() {
        return "Juego{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", colecciones=" + colecciones +
                '}';
    }
}
