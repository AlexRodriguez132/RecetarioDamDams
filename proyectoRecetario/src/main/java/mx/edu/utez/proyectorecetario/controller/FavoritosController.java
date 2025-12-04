package mx.edu.utez.proyectorecetario.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import mx.edu.utez.proyectorecetario.dao.RecetaDAO;
import mx.edu.utez.proyectorecetario.model.Receta;
import mx.edu.utez.proyectorecetario.util.Sesion;

import java.util.List;

public class FavoritosController {

    @FXML
    private FlowPane cardsContainer;

    private final RecetaDAO recetaDAO = new RecetaDAO();

    @FXML
    public void initialize() {
        cargarFavoritos();
    }

    private void cargarFavoritos() {
        cardsContainer.getChildren().clear();

        if (!Sesion.haySesionActiva()) {
            System.out.println("NO HAY SESIÓN ACTIVA");
            return;
        }

        int idUsuario = Sesion.getUsuarioActual().getId_usuario();

        List<Receta> favoritos = recetaDAO.obtenerFavoritosPorUsuario(idUsuario);

        System.out.println("Favoritos encontrados: " + favoritos.size());

        for (Receta receta : favoritos) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/mx/edu/utez/proyectorecetario/view/App/Cards/recetaVistaCard.fxml"
                        )
                );

                StackPane card = loader.load();

                RecetaVistaCardController controller = loader.getController();
                controller.setReceta(receta);

                cardsContainer.getChildren().add(card);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
