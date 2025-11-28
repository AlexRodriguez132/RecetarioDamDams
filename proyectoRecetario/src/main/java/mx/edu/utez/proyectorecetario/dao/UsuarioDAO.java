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

    public Usuario buscarPorEmail(String email){
        String sql = "Select * FROM usuarios WHERE email = ?";
        Usuario usuario = null;

        try(Connection conn = ConexionBD.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            //este set string mejora la seguridad y evita injeccion asi como reemplazar el ? con un valor tipo string
            //El 1 es el parametro y se le agrega a email
            ps.setString(1, email);
            //el executeQuery ejecuta una consulta y revuelve un resultset
            ResultSet rs = ps.executeQuery();
            //rs debe ser o 0 o 1
            //rs.next mueve el cursor a la siguiente fila del rs
            //Devuelve true si existe una fila
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
}
