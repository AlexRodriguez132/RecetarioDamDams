package mx.edu.utez.proyectorecetario.dao;

import javafx.scene.control.Alert;
import mx.edu.utez.proyectorecetario.model.Usuario;
import mx.edu.utez.proyectorecetario.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDAO {

    public Usuario validarLogin(String usuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre_usuario(rs.getString("nombre_usuario"));
                u.setContrasena(rs.getString("contrasena"));
                u.setEmail(rs.getString("email"));
                u.setFechaRegistro(rs.getDate("fecha_registro"));
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existeCorreo(String correo){
        String sql = "SELECT * FROM usuarios WHERE email = ?";
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
}
