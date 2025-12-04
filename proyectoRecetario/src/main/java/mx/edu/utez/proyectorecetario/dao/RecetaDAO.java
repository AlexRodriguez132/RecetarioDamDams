package mx.edu.utez.proyectorecetario.dao;

import mx.edu.utez.proyectorecetario.model.Receta;
import mx.edu.utez.proyectorecetario.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecetaDAO {

    public boolean insertarReceta(Receta receta, List<Integer> categoriasSeleccionadas) {
        String sqlReceta = "INSERT INTO recetas (titulo, descripcion, imagen, dificultad, duracion, ingredientes, pasos, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement psReceta = conn.prepareStatement(sqlReceta, Statement.RETURN_GENERATED_KEYS)) {

            psReceta.setString(1, receta.getTitulo() == null ? "" : receta.getTitulo());
            psReceta.setString(2, receta.getDescripcion() == null ? "" : receta.getDescripcion());
            psReceta.setString(3, receta.getImagen() == null ? "" : receta.getImagen());
            psReceta.setString(4, receta.getDificultad() == null ? "" : receta.getDificultad());
            psReceta.setString(5, receta.getDuracion() == null ? "" : receta.getDuracion());
            psReceta.setString(6, receta.getIngredientes() == null ? "" : receta.getIngredientes());
            psReceta.setString(7, receta.getPasos() == null ? "" : receta.getPasos());
            psReceta.setInt(8, receta.getId_usuario());

            int filas = psReceta.executeUpdate();
            if (filas == 0) return false;

            ResultSet keys = psReceta.getGeneratedKeys();
            if (keys.next()) {
                receta.setId_receta(keys.getInt(1));
            } else {
                return false;
            }

            if (categoriasSeleccionadas != null && !categoriasSeleccionadas.isEmpty()) {
                String sqlCat = "INSERT INTO recetas_categorias (id_receta, id_categoria) VALUES (?, ?)";
                try (PreparedStatement psCat = conn.prepareStatement(sqlCat)) {
                    for (Integer idCat : categoriasSeleccionadas) {
                        if (idCat == null || idCat <= 0) continue;
                        psCat.setInt(1, receta.getId_receta());
                        psCat.setInt(2, idCat);
                        psCat.addBatch();
                    }
                    psCat.executeBatch();
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarReceta(Receta receta, List<Integer> categoriasSeleccionadas) {
        String sqlUpdate = "UPDATE recetas SET titulo = ?, descripcion = ?, imagen = ?, dificultad = ?, duracion = ?, ingredientes = ?, pasos = ? WHERE id_receta = ?";
        String sqlDeleteCats = "DELETE FROM recetas_categorias WHERE id_receta = ?";
        String sqlInsertCat = "INSERT INTO recetas_categorias (id_receta, id_categoria) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;

        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);

            psUpdate = conn.prepareStatement(sqlUpdate);
            psUpdate.setString(1, receta.getTitulo() == null ? "" : receta.getTitulo());
            psUpdate.setString(2, receta.getDescripcion() == null ? "" : receta.getDescripcion());
            psUpdate.setString(3, receta.getImagen() == null ? "" : receta.getImagen());
            psUpdate.setString(4, receta.getDificultad() == null ? "" : receta.getDificultad());
            psUpdate.setString(5, receta.getDuracion() == null ? "" : receta.getDuracion());
            psUpdate.setString(6, receta.getIngredientes() == null ? "" : receta.getIngredientes());
            psUpdate.setString(7, receta.getPasos() == null ? "" : receta.getPasos());
            psUpdate.setInt(8, receta.getId_receta());

            int updated = psUpdate.executeUpdate();
            if (updated == 0) {
                conn.rollback();
                return false;
            }

            psDelete = conn.prepareStatement(sqlDeleteCats);
            psDelete.setInt(1, receta.getId_receta());
            psDelete.executeUpdate();

            if (categoriasSeleccionadas != null && !categoriasSeleccionadas.isEmpty()) {
                psInsert = conn.prepareStatement(sqlInsertCat);
                for (Integer idCat : categoriasSeleccionadas) {
                    if (idCat == null || idCat <= 0) continue;
                    psInsert.setInt(1, receta.getId_receta());
                    psInsert.setInt(2, idCat);
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            try { if (psInsert != null) psInsert.close(); } catch (SQLException ignored) {}
            try { if (psDelete != null) psDelete.close(); } catch (SQLException ignored) {}
            try { if (psUpdate != null) psUpdate.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }


    public boolean eliminarReceta(int idReceta) {
        String sql = "DELETE FROM recetas WHERE id_receta = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReceta);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Receta> obtenerRecetasPorUsuario(int idUsuario) {
        String sql = "SELECT * FROM recetas WHERE id_usuario = ? ORDER BY id_receta DESC";
        List<Receta> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Receta r = new Receta();
                r.setId_receta(rs.getInt("id_receta"));
                r.setTitulo(rs.getString("titulo"));
                r.setDescripcion(rs.getString("descripcion"));
                r.setImagen(rs.getString("imagen"));
                r.setDificultad(rs.getString("dificultad"));
                r.setDuracion(rs.getString("duracion"));
                r.setIngredientes(rs.getString("ingredientes"));
                r.setPasos(rs.getString("pasos"));
                r.setId_usuario(rs.getInt("id_usuario"));
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Integer> obtenerCategoriasPorReceta(int idReceta) {
        String sql = "SELECT id_categoria FROM recetas_categorias WHERE id_receta = ?";
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReceta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("id_categoria"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public List<Receta> buscarRecetasPorUsuarioYTitulo(int idUsuario, String texto) {
        String sql = "SELECT * FROM recetas WHERE id_usuario = ? AND titulo LIKE ? ORDER BY id_receta DESC";
        List<Receta> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Receta r = new Receta();
                r.setId_receta(rs.getInt("id_receta"));
                r.setTitulo(rs.getString("titulo"));
                r.setDescripcion(rs.getString("descripcion"));
                r.setImagen(rs.getString("imagen"));
                r.setDificultad(rs.getString("dificultad"));
                r.setDuracion(rs.getString("duracion"));
                r.setIngredientes(rs.getString("ingredientes"));
                r.setPasos(rs.getString("pasos"));
                r.setId_usuario(rs.getInt("id_usuario"));
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean agregarFavorito(int idUsuario, int idReceta) {
        String sql = "INSERT INTO favoritos (id_usuario, id_receta, fecha_favorito) VALUES (?, ?, NOW())";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idReceta);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarFavorito(int idUsuario, int idReceta) {
        String sql = "DELETE FROM favoritos WHERE id_usuario = ? AND id_receta = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idReceta);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean esFavorito(int idUsuario, int idReceta) {
        String sql = "SELECT 1 FROM favoritos WHERE id_usuario = ? AND id_receta = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idReceta);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Receta> obtenerFavoritosPorUsuario(int idUsuario) {
        String sql = "SELECT r.* FROM recetas r JOIN favoritos f ON r.id_receta = f.id_receta WHERE f.id_usuario = ? ORDER BY f.fecha_favorito DESC";
        List<Receta> lista = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Receta r = new Receta();
                r.setId_receta(rs.getInt("id_receta"));
                r.setTitulo(rs.getString("titulo"));
                r.setDescripcion(rs.getString("descripcion"));
                r.setImagen(rs.getString("imagen"));
                r.setDificultad(rs.getString("dificultad"));
                r.setDuracion(rs.getString("duracion"));
                r.setIngredientes(rs.getString("ingredientes"));
                r.setPasos(rs.getString("pasos"));
                r.setId_usuario(rs.getInt("id_usuario"));
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
