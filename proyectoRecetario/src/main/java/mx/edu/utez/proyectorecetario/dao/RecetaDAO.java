package mx.edu.utez.proyectorecetario.dao;

import mx.edu.utez.proyectorecetario.model.Receta;
import mx.edu.utez.proyectorecetario.util.ConexionBD;

import java.sql.*;
import java.util.List;

public class RecetaDAO {

    public boolean insertarReceta(Receta receta, List<Integer> categoriasSeleccionadas) {

        String sql = "INSERT INTO recetas " +
                "(titulo, descripcion, imagen, dificultad, duracion, ingredientes, pasos, id_usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, receta.getTitulo());
            ps.setString(2, receta.getDescripcion());

            if (receta.getImagen() != null) {
                ps.setBytes(3, receta.getImagen());
            } else {
                ps.setNull(3, Types.BLOB);
            }

            ps.setString(4, receta.getDificultad());
            ps.setString(5, receta.getDuracion());
            ps.setString(6, receta.getIngredientes());
            ps.setString(7, receta.getPasos());
            ps.setInt(8, receta.getId_usuario());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    receta.setId_receta(keys.getInt(1));
                }

                String sqlCategoria = "INSERT INTO recetas_categorias (id_receta, id_categoria) VALUES (?, ?)";
                try (PreparedStatement psCat = conn.prepareStatement(sqlCategoria)) {

                    for (Integer idCat : categoriasSeleccionadas) {
                        psCat.setInt(1, receta.getId_receta());
                        psCat.setInt(2, idCat);
                        psCat.addBatch();
                    }
                    psCat.executeBatch();
                }

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Receta buscarPorId(int id) {
        String sql = "SELECT * FROM recetas WHERE id_receta = ?";
        Receta receta = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                receta = new Receta();
                receta.setId_receta(rs.getInt("id_receta"));
                receta.setTitulo(rs.getString("titulo"));
                receta.setDescripcion(rs.getString("descripcion"));

                Blob blob = rs.getBlob("imagen");
                receta.setImagen(blob != null ? blob.getBytes(1, (int) blob.length()) : null);

                receta.setDificultad(rs.getString("dificultad"));
                receta.setDuracion(rs.getString("duracion"));
                receta.setIngredientes(rs.getString("ingredientes"));
                receta.setPasos(rs.getString("pasos"));
                receta.setId_usuario(rs.getInt("id_usuario"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return receta;
    }
}
