package mx.edu.utez.proyectorecetario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.edu.utez.proyectorecetario.dao.UsuarioDAO;
import mx.edu.utez.proyectorecetario.model.Usuario;

import java.io.IOException;

public class RegistroController {
    @FXML
    private TextField txtNombreUsuario;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private PasswordField txtConfirmarContrasena;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void onRegister(ActionEvent event) {
        String nombreUsuario = txtNombreUsuario.getText().trim();
        String email = txtEmail.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        String confirmarContrasena = txtConfirmarContrasena.getText().trim();

        if (nombreUsuario.isEmpty() || email.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Porfavor completa todos los campos.", null);
            txtNombreUsuario.clear();
            txtEmail.clear();
            txtContrasena.clear();
            txtConfirmarContrasena.clear();
            return;
        }


        if (!contrasena.equals(confirmarContrasena)) {
            mostrarAlerta("Contraseña incorrecta", "Las contraseñas no coinciden, intentalo de nuevo.", null);
            txtNombreUsuario.clear();
            txtEmail.clear();
            txtContrasena.clear();
            txtConfirmarContrasena.clear();
            return;
        }

        if (usuarioDAO.buscarPorEmail(email) != null) {
            mostrarAlerta("Usuario existente", "Ya hay un usuario registrado con este email.", null);
            txtNombreUsuario.clear();
            txtEmail.clear();
            txtContrasena.clear();
            txtConfirmarContrasena.clear();
            return;
        }

        Usuario nuevo = new Usuario(nombreUsuario, contrasena, email);

        boolean registrado = usuarioDAO.registrarUsuario(nuevo);

        if (registrado) {
            mostrarAlertaExito("¡Registro exitoso!", "Tu cuenta está lista para usar.", () -> onScreenHome(event));
            txtNombreUsuario.clear();
            txtEmail.clear();
            txtContrasena.clear();
            txtConfirmarContrasena.clear();
            return;


        } else {
            mostrarAlertaError("Error", "No se ha podido registrar el usuario.", null);
            txtNombreUsuario.clear();
            txtEmail.clear();
            txtContrasena.clear();
            txtConfirmarContrasena.clear();
        }
    }

    @FXML
    private void onScreenLandingPage(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/landingpage-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Regresar atras");
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
    @FXML
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