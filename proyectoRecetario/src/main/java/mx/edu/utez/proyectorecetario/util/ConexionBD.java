package mx.edu.utez.proyectorecetario.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/GESTOR_RECETAS";
    private static final String USER = "root";
    private static final String PASSWORD = "Alex_132";


    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a MySQL.");
            return conn;

        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver JDBC de MySQL.");
            throw new SQLException("Driver JDBC no encontrado.", e);

        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            throw e;
        }
    }

}