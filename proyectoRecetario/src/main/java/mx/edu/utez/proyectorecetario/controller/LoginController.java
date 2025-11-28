package mx.edu.utez.proyectorecetario.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.edu.utez.proyectorecetario.dao.LoginDAO;
import javafx.event.ActionEvent;

import java.io.IOException;

public class LoginController {

    private final LoginDAO loginDAO = new LoginDAO();

    @FXML
    private TextField txtNombreUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private TextField txtCorreoElectronico;

    private String codigoSimulado;

    @FXML
    private void onLogin(ActionEvent event) {
        String usuario = txtNombreUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Porfavor completa todos los campos.", null);
            txtNombreUsuario.clear();
            txtContrasena.clear();
            return;
        }

        if (loginDAO.validarLogin(usuario, contrasena)) {
            mostrarAlertaExito("¡Login exitoso!", "Bienvenido nuevamente.", () -> onScreenHome(event));
            txtNombreUsuario.clear();
            txtContrasena.clear();
            return;

        } else {
            mostrarAlertaError("Error", "No se ha podido iniciar sesión.", null);
            txtNombreUsuario.clear();
            txtContrasena.clear();
        }
    }

    @FXML
    private void onScreenLandingPage(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/landingpage-view.fxml"));
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

    @FXML
    private void onScreenLogin(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/Ingresar/login-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onForgotPassword(ActionEvent event) {
        String correo = txtCorreoElectronico.getText().trim();

        if (correo.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setHeaderText(null);
            alerta.setContentText("¡Por favor ingresa tu correo!");
            alerta.showAndWait();
            return;
        }

        if (loginDAO.existeCorreo(correo)) {
            // Simulamos envío de correo con código
            codigoSimulado = loginDAO.enviarCodigoSimulado(correo);

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setHeaderText(null);
            alerta.setContentText("Se ha enviado un código de verificación a tu correo (simulado). Código: " + codigoSimulado);
            alerta.showAndWait();

        } else {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText(null);
            alerta.setContentText("El correo ingresado no está registrado.");
            alerta.showAndWait();
        }
    }
    @FXML
    private void onScreenForgotPassword(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/Ingresar/forgotpassword-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Runnable accion){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/Alertas/alert-alert.fxml"));
            Parent alertRoot = loader.load();
            AlertController controller = loader.getController();
            controller.configurar(titulo, mensaje, accion);

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setScene(new Scene(alertRoot));
            stage.setResizable(false);
            stage.setTitle(titulo);
            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void mostrarAlertaExito(String titulo, String mensaje, Runnable accion){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/Alertas/alert-ingresoexitoso.fxml"));
            Parent alertRoot = loader.load();
            AlertController controller = loader.getController();
            controller.configurar(titulo, mensaje, accion);

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setScene(new Scene(alertRoot));
            stage.setResizable(false);
            stage.setTitle(titulo);
            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje, Runnable accion){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/Alertas/alert-error.fxml"));
            Parent alertRoot = loader.load();
            AlertController controller = loader.getController();
            controller.configurar(titulo, mensaje, accion);

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setScene(new Scene(alertRoot));
            stage.setResizable(false);
            stage.setTitle(titulo);
            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void onScreenHome(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/home-view.fxml"));
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
