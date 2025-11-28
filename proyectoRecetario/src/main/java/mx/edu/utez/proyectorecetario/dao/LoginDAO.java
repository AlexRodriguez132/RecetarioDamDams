package mx.edu.utez.proyectorecetario.dao;

import javafx.scene.control.Alert;
import mx.edu.utez.proyectorecetario.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDAO {

    public boolean validarLogin(String usuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();

            return rs.next(); // true si encontró un registro, false si no

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeCorreo(String correo){
        String sql = "SELECT * FROM USUARIOS WHERE email = ?";
        try(Connection conn = ConexionBD.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String enviarCodigoSimulado(String correo) {
        // Generamos un código de 4 dígitos
        int codigo = (int)(Math.random() * 9000) + 1000;
        System.out.println("Enviando código al correo: " + correo + " -> Código: " + codigo);
        // Aquí se simula que se envía, no se hace envío real
        return String.valueOf(codigo);
    }


}