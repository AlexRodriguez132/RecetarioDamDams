package mx.edu.utez.proyectorecetario.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.edu.utez.proyectorecetario.dao.LoginDAO;
import javafx.event.ActionEvent;
import mx.edu.utez.proyectorecetario.dao.UsuarioDAO;
import mx.edu.utez.proyectorecetario.model.Usuario;
import mx.edu.utez.proyectorecetario.util.Email;
import mx.edu.utez.proyectorecetario.util.Sesion;

import java.io.IOException;

public class LoginController {

    private final LoginDAO loginDAO = new LoginDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private String codigoSimulado;
    private Usuario usuarioRecuperacion;
    private String correoTemporal;

    @FXML
    private TextField txtNombreUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private TextField txtCorreoElectronico;

    @FXML
    private TextField txtCodigo1;

    @FXML
    private TextField txtCodigo2;

    @FXML
    private TextField txtCodigo3;

    @FXML
    private TextField txtCodigo4;

    private String generarTuPin() {
        return String.valueOf(1000 + (int)(Math.random() * 9000));
    }

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

        Usuario usuarioLogeado = loginDAO.validarLogin(usuario, contrasena);

        if (usuarioLogeado != null) {
            Sesion.iniciarSesion(usuarioLogeado);
            mostrarAlertaExito("¡Login exitoso!", "Bienvenido " + usuarioLogeado.getNombre_usuario(), () -> onScreenHome(event));
            txtNombreUsuario.clear();
            txtContrasena.clear();
        } else {
            mostrarAlertaError("Error", "Usuario o contraseña incorrectos.", null);
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
    private void onConfirmEmail(ActionEvent event) {
        String correo = txtCorreoElectronico.getText().trim();

        if (correo.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Ingresa tu correo.", null);
            return;
        }

        if (!correo.endsWith("@gmail.com") && !correo.endsWith("@utez.edu.mx")) {
            mostrarAlerta("Email inválido", "El correo debe terminar en @gmail.com o @utez.edu.mx", null);
            txtCorreoElectronico.clear();
            return;
        }

        Usuario u = usuarioDAO.buscarPorEmail(correo);
        if (u == null) {
            mostrarAlertaError("Error", "Correo no registrado.", null);
            return;
        }

        correoTemporal = correo;
        codigoSimulado = generarTuPin();
        usuarioRecuperacion = u;

        try {
            Email emailSender = new Email(correo, u.getNombre_usuario(), codigoSimulado);
            emailSender.sendEmail();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/Ingresar/codigoverifiacion-view.fxml"));
            Parent root = fxmlLoader.load();
            LoginController controller = fxmlLoader.getController();
            controller.setCodigoSimulado(codigoSimulado);
            controller.setCorreoTemporal(correoTemporal);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();

            mostrarAlertaCorreo("Correo enviado", "Se envió un código de verificación a tu correo.", null);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo enviar el correo.", null);
        }
    }

    @FXML
    private void onScreenForgotPassword(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/Ingresar/forgotpassword-view.fxml"));
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

    private void mostrarAlertaCorreo(String titulo, String mensaje, Runnable accion){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/Alertas/alert-mail.fxml"));
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

    @FXML
    private void initialize() {
        if (txtCodigo1 != null) limitarATextFieldNumerico(txtCodigo1);
        if (txtCodigo2 != null) limitarATextFieldNumerico(txtCodigo2);
        if (txtCodigo3 != null) limitarATextFieldNumerico(txtCodigo3);
        if (txtCodigo4 != null) limitarATextFieldNumerico(txtCodigo4);
    }

    private void limitarATextFieldNumerico(TextField campo) {
        campo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d")) {
                campo.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (campo.getText().length() > 1) {
                campo.setText(campo.getText().substring(0, 1));
            }
        });
    }

    @FXML
    private void onValidarCodigo(ActionEvent event) {
        if (codigoSimulado == null) {
            mostrarAlertaError("Error", "No se ha generado un código para verificar.", null);
            return;
        }

        String codigoIngresado =
                (txtCodigo1.getText() + txtCodigo2.getText() + txtCodigo3.getText() + txtCodigo4.getText()).trim();

        if (codigoIngresado.length() < 4) {
            mostrarAlerta("Código incompleto", "Debes ingresar los 4 dígitos del código.", null);
            return;
        }

        if (codigoIngresado.equals(codigoSimulado.trim())) {
            mostrarAlertaExito("Código correcto", "Verificación exitosa.", () -> onScreenHome(event));
        } else {
            mostrarAlertaError("Código incorrecto", "El código ingresado no es válido.", null);
            txtCodigo1.clear();
            txtCodigo2.clear();
            txtCodigo3.clear();
            txtCodigo4.clear();
        }
    }

    public void setCodigoSimulado(String codigo) {
        this.codigoSimulado = codigo;
    }

    public void setCorreoTemporal(String correo) {
        this.correoTemporal = correo;
    }

    public String getCorreoTemporal() {
        return correoTemporal;
    }
}
