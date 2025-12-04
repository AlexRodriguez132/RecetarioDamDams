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
import mx.edu.utez.proyectorecetario.util.Sesion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EditarRecetaController {

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
    private final List<ToggleButton> toggleCategorias = new ArrayList<>();
    private Receta recetaActual;

    @FXML
    public void initialize() {
        duracion.getItems().addAll("< 15 min", "15 - 30 min", "30 - 60 min", "> 60 min");
        dificultad.getItems().addAll("Fácil", "Media", "Difícil");

        String[] categorias = {
                "desayuno","comida","cena","postre","bebida",
                "vegetariano","saludable","internacional","carne","pescado"
        };

        for (int i = 0; i < categorias.length; i++) {
            String nombre = categorias[i];

            ToggleButton tb = new ToggleButton(nombre);
            tb.setUserData(i + 1);

            String cssClass = "categoria-" + nombre;
            tb.getStyleClass().add(cssClass);

            String rutaImagen = "/mx/edu/utez/proyectorecetario/images/categorias/" + nombre + ".png";
            InputStream is = getClass().getResourceAsStream(rutaImagen);
            if (is != null) {
                ImageView icon = new ImageView(new Image(is));
                icon.setFitWidth(18);
                icon.setFitHeight(18);
                tb.setGraphic(icon);
                tb.setContentDisplay(ContentDisplay.LEFT);
            }

            toggleCategorias.add(tb);
            categoriasPane.getChildren().add(tb);
        }
    }


    public void setReceta(Receta receta) {
        this.recetaActual = receta;

        if (receta == null) return;

        titulo.setText(receta.getTitulo() == null ? "" : receta.getTitulo());
        descripcion.setText(receta.getDescripcion() == null ? "" : receta.getDescripcion());
        ingredientes.setText(receta.getIngredientes() == null ? "" : receta.getIngredientes());
        pasos.setText(receta.getPasos() == null ? "" : receta.getPasos());
        duracion.setValue(receta.getDuracion());
        dificultad.setValue(receta.getDificultad());

        recetaRutaImagen = receta.getImagen();
        imagenNombre.setText(recetaRutaImagen != null && !recetaRutaImagen.isEmpty() ? recetaRutaImagen : "Sin imagen");


        List<Integer> categorias = new RecetaDAO().obtenerCategoriasPorReceta(receta.getId_receta());
        for (ToggleButton tb : toggleCategorias) {
            int id = (int) tb.getUserData();
            tb.setSelected(categorias != null && categorias.contains(id));
        }
    }

    @FXML
    public void seleccionarImagen(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = chooser.showOpenDialog(null);
        if (file == null) return;

        try {
            File carpeta = new File(RUTA_IMAGENES);
            if (!carpeta.exists()) carpeta.mkdirs();

            String nombreOriginal = file.getName();
            String extension = nombreOriginal.contains(".") ? nombreOriginal.substring(nombreOriginal.lastIndexOf(".")) : ".jpg";
            String nombreUnico = System.currentTimeMillis() + extension;
            File destino = new File(RUTA_IMAGENES + nombreUnico);

            java.nio.file.Files.copy(file.toPath(), destino.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            recetaRutaImagen = nombreUnico;
            imagenNombre.setText(nombreUnico);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo copiar la imagen", null);
        }
    }


    @FXML
    public void guardarReceta(ActionEvent event) {

        guardarCambios(event);
    }


    private void guardarCambios(ActionEvent event) {
        if (recetaActual == null) return;

        if (titulo.getText().isEmpty() || duracion.getValue() == null || dificultad.getValue() == null) {
            mostrarAlerta("Campos incompletos", "Completa los campos obligatorios", null);
            return;
        }

        List<Integer> categoriasSeleccionadas = new ArrayList<>();
        for (ToggleButton tb : toggleCategorias) {
            if (tb.isSelected()) categoriasSeleccionadas.add((int) tb.getUserData());
        }

        recetaActual.setTitulo(titulo.getText());
        recetaActual.setDescripcion(descripcion.getText());
        recetaActual.setDuracion(duracion.getValue());
        recetaActual.setDificultad(dificultad.getValue());
        recetaActual.setIngredientes(ingredientes.getText());
        recetaActual.setPasos(pasos.getText());
        recetaActual.setImagen(recetaRutaImagen);

        RecetaDAO dao = new RecetaDAO();
        boolean exito = dao.actualizarReceta(recetaActual, categoriasSeleccionadas);

        if (exito) {
            mostrarAlertaExito("Actualizado", "La receta fue actualizada correctamente", () -> onScreenMisRecetas(event));
        } else {
            mostrarAlertaError("Error", "No se pudo actualizar la receta", null);
        }
    }


    private void mostrarAlerta(String titulo, String mensaje, Runnable accion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/Alertas/alert-alert.fxml"));
            Parent alertRoot = loader.load();

            AlertController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.configurar(titulo, mensaje, accion);

            stage.setScene(new Scene(alertRoot));
            stage.setResizable(false);
            stage.setTitle(titulo);
            stage.show();

        } catch (IOException e) { e.printStackTrace(); }
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

        } catch (IOException e) { e.printStackTrace(); }
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

        } catch (IOException e) { e.printStackTrace(); }
    }

    public void onScreenMisRecetas(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/recetas-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            if (event != null && event.getSource() != null) {
                ((Node)(event.getSource())).getScene().getWindow().hide();
            } else {
                Stage s = (Stage) titulo.getScene().getWindow();
                if (s != null) s.close();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
