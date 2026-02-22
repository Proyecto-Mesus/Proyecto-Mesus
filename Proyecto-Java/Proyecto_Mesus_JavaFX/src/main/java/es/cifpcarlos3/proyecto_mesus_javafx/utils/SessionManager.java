package es.cifpcarlos3.proyecto_mesus_javafx.utils;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;

public class SessionManager {

    private static Usuario usuarioActual;

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}
