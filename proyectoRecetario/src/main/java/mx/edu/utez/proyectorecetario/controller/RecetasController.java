package mx.edu.utez.proyectorecetario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import mx.edu.utez.proyectorecetario.dao.RecetaDAO;
import mx.edu.utez.proyectorecetario.model.Receta;
import mx.edu.utez.proyectorecetario.util.Sesion;

import java.io.IOException;
import java.util.List;

public class RecetasController {

    @FXML
    private FlowPane cardsContainer;

    @FXML
    public void initialize() {
        cargarRecetasUsuario();
    }

    @FXML
    private void onEnterBuscar(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER -> cargarRecetasUsuario();
        }
    }

    @FXML
    private void onScreenCrearReceta(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/CrearRecetas/crearreceta-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Regresar atras");
            stage.setMaximized(true);
            stage.show();
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarRecetasUsuario() {
        cardsContainer.getChildren().clear();
        if (!Sesion.haySesionActiva()) {
            return;
        }
        int idUsuario = Sesion.getUsuarioActual().getId_usuario();
        RecetaDAO dao = new RecetaDAO();
        List<Receta> recetas = dao.obtenerRecetasPorUsuario(idUsuario);
        if (recetas == null || recetas.isEmpty()) {
            return;
        }
        for (Receta r : recetas) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/Cards/recetacard.fxml")
                );
                Parent card = loader.load();
                RecetaCardController controller = loader.getController();
                controller.setContenedorPadre(cardsContainer);
                controller.setReceta(r, dao.obtenerCategoriasPorReceta(r.getId_receta()));
                cardsContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
