package mx.edu.utez.proyectorecetario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class RecetasController {
    @FXML private TextField tfBuscar;
    @FXML private Button btnFiltrar;

    @FXML
    private void onEnterBuscar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            realizarBusqueda();
        }
    }

    @FXML
    private void onFiltrar(ActionEvent event) {

    }

    private void realizarBusqueda() {
        String texto = tfBuscar.getText().trim();
        // Lógica de filtrado aquí
    }

    @FXML
    private void onScreenCrearReceta(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/CrearRecetas/crearreceta-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
