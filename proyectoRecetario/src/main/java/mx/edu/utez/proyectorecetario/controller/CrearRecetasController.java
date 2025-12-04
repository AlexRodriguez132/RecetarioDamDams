package mx.edu.utez.proyectorecetario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.proyectorecetario.dao.RecetaDAO;
import mx.edu.utez.proyectorecetario.model.Receta;
import mx.edu.utez.proyectorecetario.model.Usuario;
import mx.edu.utez.proyectorecetario.util.Sesion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CrearRecetasController {

    @FXML private TextField titulo;
    @FXML private TextArea descripcion;
    @FXML private FlowPane categoriasPane;
    @FXML private ComboBox<String> duracion;
    @FXML private ComboBox<String> dificultad;
    @FXML private TextArea ingredientes;
    @FXML private TextArea pasos;
    @FXML private Label imagenNombre;

    private static final String RUTA_IMAGENES = System.getProperty("user.home") + "/Recetario/imagenes/";
    private String recetaRutaImagen;

    private int idUsuarioActual;
    private final List<ToggleButton> toggleCategorias = new ArrayList<>();

    @FXML
    public void initialize() {

        duracion.getItems().addAll("< 15 min", "15 - 30 min", "30 - 60 min", "> 60 min");
        dificultad.getItems().addAll("Fácil", "Media", "Difícil");

        String[] categorias = {
                "desayuno", "comida", "cena", "postre", "bebida",
                "vegetariano", "saludable", "internacional", "carne", "pescado"
        };

        for (int i = 0; i < categorias.length; i++) {
            String nombre = categorias[i];
            ToggleButton tb = new ToggleButton(nombre);
            tb.setUserData(i + 1);

            String cssClass = "categoria-" + nombre.toLowerCase().replace(" ", "");
            tb.getStyleClass().add(cssClass);

            String rutaImagen = "/mx/edu/utez/proyectorecetario/images/categorias/" + nombre.toLowerCase() + ".png";
            InputStream is = getClass().getResourceAsStream(rutaImagen);
            if (is != null) {
                ImageView icon = new ImageView(new Image(is));
                icon.setFitWidth(18);
                icon.setFitHeight(18);
                tb.setGraphic(icon);
                tb.setContentDisplay(ContentDisplay.LEFT);
            } else {
                System.out.println("¡No se encontró la imagen: " + rutaImagen + "!");
            }

            toggleCategorias.add(tb);
            categoriasPane.getChildren().add(tb);
        }

        Usuario usuarioSesion = Sesion.getUsuarioActual();
        if (usuarioSesion != null) {
            idUsuarioActual = usuarioSesion.getId_usuario();
            System.out.println("[CrearRecetasController] Sesión activa: " + usuarioSesion.getNombre_usuario()
                    + " (id=" + idUsuarioActual + ")");
        } else {
            idUsuarioActual = -1;
            System.out.println("[CrearRecetasController] No hay sesión activa al inicializar CrearRecetasController");
        }
    }

    @FXML
    public void seleccionarImagen(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(null);

        if (file == null) {
            mostrarAlerta("Sin imagen", "No seleccionaste ninguna imagen", null);
            return;
        }

        try {
            File carpeta = new File(RUTA_IMAGENES);
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            String nombreOriginal = file.getName();
            String extension = nombreOriginal.contains(".")
                    ? nombreOriginal.substring(nombreOriginal.lastIndexOf("."))
                    : ".jpg";

            String nombreUnico = System.currentTimeMillis() + extension;

            File destino = new File(RUTA_IMAGENES + nombreUnico);

            java.nio.file.Files.copy(
                    file.toPath(),
                    destino.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            // Guardamos solo el nombre del archivo
            recetaRutaImagen = nombreUnico;
            imagenNombre.setText(nombreUnico);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo guardar la imagen en la carpeta", null);
        }
    }

    @FXML
    public void guardarReceta(ActionEvent event) {
        Usuario usuarioSesion = Sesion.getUsuarioActual();
        if (usuarioSesion != null) {
            idUsuarioActual = usuarioSesion.getId_usuario();
        }

        if (titulo.getText().isEmpty() || duracion.getValue() == null || dificultad.getValue() == null) {
            mostrarAlerta("Campos incompletos", "Debes llenar título, duración y dificultad.", null);
            return;
        }

        if (idUsuarioActual <= 0) {
            mostrarAlertaError("Error Critico", "No se detectó un usuario activo!", null);
            return;
        }

        List<Integer> categoriasSeleccionadas = new ArrayList<>();
        for (ToggleButton tb : toggleCategorias) {
            if (tb.isSelected()) {
                categoriasSeleccionadas.add((int) tb.getUserData());
            }
        }

        if (categoriasSeleccionadas.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Debes seleccionar al menos una categoría.", null);
            return;
        }

        Receta receta = new Receta();
        receta.setTitulo(titulo.getText());
        receta.setDescripcion(descripcion.getText());
        receta.setDuracion(duracion.getValue());
        receta.setDificultad(dificultad.getValue());
        receta.setIngredientes(ingredientes.getText());
        receta.setPasos(pasos.getText());
        receta.setId_usuario(idUsuarioActual);
        receta.setImagen(recetaRutaImagen); // ← esto es crítico

        RecetaDAO dao = new RecetaDAO();
        boolean exito = dao.insertarReceta(receta, categoriasSeleccionadas);

        if (exito) {
            mostrarAlertaExito("Éxito", "La receta fue creada correctamente.", this::limpiarCampos);
        } else {
            mostrarAlertaError("Error", "Hubo un problema al guardar la receta.", null);
        }
    }

    private void limpiarCampos() {
        titulo.clear();
        descripcion.clear();
        duracion.setValue(null);
        dificultad.setValue(null);
        ingredientes.clear();
        pasos.clear();
        imagenNombre.setText("Sin imagen");
        recetaRutaImagen = null;

        for (ToggleButton tb : toggleCategorias) {
            tb.setSelected(false);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Runnable accion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mx/edu/utez/proyectorecetario/view/Alertas/alert-alert.fxml"));
            Parent alertRoot = loader.load();

            mx.edu.utez.proyectorecetario.controller.AlertController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.configurar(titulo, mensaje, accion);

            stage.setScene(new Scene(alertRoot));
            stage.setResizable(false);
            stage.setTitle(titulo);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlertaExito(String titulo, String mensaje, Runnable accion) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje, Runnable accion) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onScreenMisRecetas(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/recetas-view.fxml"));
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
