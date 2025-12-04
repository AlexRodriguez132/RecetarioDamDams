package mx.edu.utez.proyectorecetario.util;

import mx.edu.utez.proyectorecetario.model.Usuario;

public class Sesion {
    private static Usuario usuarioActual;

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}
