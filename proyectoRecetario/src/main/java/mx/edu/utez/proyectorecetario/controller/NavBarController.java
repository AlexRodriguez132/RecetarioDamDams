package mx.edu.utez.proyectorecetario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

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

}
