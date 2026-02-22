package es.cifpcarlos3.proyecto_mesus_javafx.models;

public class Carta {
    private int id;
    private String nombre;
    private String set;
    private String numeroSet;
    private Coleccion coleccion;
    private String imagen;

    public Carta() {
    }

    public Carta(int id, String nombre, String set, String numeroSet, Coleccion coleccion, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.set = set;
        this.numeroSet = numeroSet;
        this.coleccion = coleccion;
        this.imagen = imagen;
    }
    public Carta(String nombre, String set, String numeroSet, Coleccion coleccion, String imagen) {
        this.nombre = nombre;
        this.set = set;
        this.numeroSet = numeroSet;
        this.coleccion = coleccion;
        this.imagen = imagen;
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

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getNumeroSet() {
        return numeroSet;
    }

    public void setNumeroSet(String numeroSet) {
        this.numeroSet = numeroSet;
    }

    public Coleccion getColeccion() {
        return coleccion;
    }

    public void setColeccion(Coleccion coleccion) {
        this.coleccion = coleccion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "Carta{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", set='" + set + '\'' +
                ", numeroSet='" + numeroSet + '\'' +
                ", coleccion=" + coleccion +
                ", imagen='" + imagen + '\'' +
                '}';
    }
}
