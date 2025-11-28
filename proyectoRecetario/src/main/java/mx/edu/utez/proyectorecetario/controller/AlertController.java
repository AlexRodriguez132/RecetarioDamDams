package mx.edu.utez.proyectorecetario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AlertController {
    @FXML private Label lblTitulo;
    @FXML private Label lblMensaje;
    @FXML private Button btnAceptar;

    public void configurar(String titulo, String mensaje, Runnable accion){
        lblTitulo.setText(titulo);
        lblMensaje.setText(mensaje);

        btnAceptar.setOnAction(e -> {
            if(accion != null) accion.run();
            if (stage != null) stage.close();

        });
    }

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


}
