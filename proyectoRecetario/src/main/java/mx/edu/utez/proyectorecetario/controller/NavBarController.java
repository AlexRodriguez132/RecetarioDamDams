package mx.edu.utez.proyectorecetario.controller;

import jakarta.mail.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import mx.edu.utez.proyectorecetario.util.Sesion;

import java.io.IOException;

public class NavBarController {
    @FXML private Button btnHome;
    @FXML private Button btnRecetas;
    @FXML private Button btnFavoritos;


    @FXML
    private void onScreenHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/home-view.fxml"));

            ((Stage)((Node)event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onScreenRecetas(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/recetas-view.fxml"));
            ((Stage)((Node)event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onScreenFavoritos(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/favoritos-view.fxml"));
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button btnMenuPerfil;

    private ContextMenu menuPerfil;

    @FXML
    public void initialize() {
        menuPerfil = new ContextMenu();

        MenuItem opcionPerfil = new MenuItem("Perfil");
        MenuItem opcionSalir = new MenuItem("Salir");

        opcionPerfil.setOnAction(e -> abrirPerfil());
        opcionSalir.setOnAction(e -> salirSistema());

        menuPerfil.getItems().addAll(opcionPerfil, opcionSalir);
    }

    @FXML
    private void mostrarMenuPerfil() {
        if (!menuPerfil.isShowing()) {
            menuPerfil.show(btnMenuPerfil, Side.BOTTOM, 0, 0);
        } else {
            menuPerfil.hide();
        }
    }

    private void abrirPerfil() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/mx/edu/utez/proyectorecetario/view/NavBar/perfil-view.fxml")
            );
            Parent root = loader.load();
            PerfilController controller = loader.getController();
            controller.inicializarPerfil(Sesion.getUsuarioActual());
            Stage stage = new Stage();
            stage.setMaximized(true);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salirSistema() {
        Platform.exit();
    }
}
