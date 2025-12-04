package mx.edu.utez.proyectorecetario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mx.edu.utez.proyectorecetario.model.Usuario;

import java.io.IOException;

public class PerfilController {

    @FXML
    private void onScreenHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/home-view.fxml"));

            ((Stage)((Node)event.getSource()).getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private Label lblUsuario;
    @FXML private Label lblEmail;

    public void inicializarPerfil(Usuario usuario) {
        lblUsuario.setText(usuario.getNombre_usuario());
        lblEmail.setText(usuario.getEmail());
    }

}
