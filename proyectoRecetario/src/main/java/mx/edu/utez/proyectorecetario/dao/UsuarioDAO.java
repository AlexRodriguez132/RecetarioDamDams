package mx.edu.utez.proyectorecetario.dao;

import mx.edu.utez.proyectorecetario.util.ConexionBD;
import mx.edu.utez.proyectorecetario.model.Usuario;

import java.sql.*;


public class UsuarioDAO {
    //Metodo que inserta al usuario
    public boolean insertarUsuario(Usuario usuario){
        /*La variable sql inserta dentro de usuario los valores en cada una de sus columnas correspondientes
         En caso de los signos de ? son para reemplazarlo con un valor del objeto Usuario*/
        String sql = "INSERT INTO usuarios (nombre_usuario, contrasena, email, fecha_registro) VALUES (?, ?, ?, now())";
        //El try connection intenta conectarse a la base de datos y el prepare statement prepara la consulta
        try(Connection conn = ConexionBD.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            //asigna a cada ? dependiendo el numero el valor que obtiene con el getter
            ps.setString(1, usuario.getNombre_usuario());
            ps.setString(2, usuario.getContrasena());
            ps.setString(3,usuario.getEmail());
            //Esta parte ejecuta la consulta y la cantidad de filas afectadas se guardan y si son mayores a 0 lo cual significa que si hubo alteracion retorna true y si no false
            int filas = ps.executeUpdate();
            if (filas > 0) {
                return true;
            } else {
                return false;
            }
        //Si hay un error se hace un catch que imprime el error
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public Usuario buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ?";
        try {
            Connection conn = ConexionBD.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("contrasena"),
                        rs.getString("email"),
                        rs.getDate("fecha_registro")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Usuario buscarPorEmail(String email){
        String sql = "Select * FROM usuarios WHERE email = ?";
        Usuario usuario = null;

        try(Connection conn = ConexionBD.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                usuario = new Usuario();
                usuario.setId_usuario(rs.getInt("id_usuario"));
                usuario.setNombre_usuario(rs.getString("nombre_usuario"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setEmail(rs.getString("email"));
                usuario.setFechaRegistro(rs.getDate("fecha_registro"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre_usuario, contrasena, email, fecha_registro) VALUES (?, ?, ?, NOW())";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre_usuario());
            ps.setString(2, usuario.getContrasena());
            ps.setString(3, usuario.getEmail());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("contrasena"),
                        rs.getString("email"),
                        rs.getDate("fecha_registro")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    public static int obtenerIdPorNombre(String nombre) {
        String sql = "SELECT id_usuario FROM usuarios WHERE nombre_usuario = ?";
        try (Connection conn = ConexionBD.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_usuario");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    */
}
