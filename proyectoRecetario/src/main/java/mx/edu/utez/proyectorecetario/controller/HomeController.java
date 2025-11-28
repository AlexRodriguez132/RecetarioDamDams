package mx.edu.utez.proyectorecetario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class HomeController {
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

    }
}
