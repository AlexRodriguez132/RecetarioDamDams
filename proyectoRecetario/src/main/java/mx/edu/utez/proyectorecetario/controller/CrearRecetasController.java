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

import java.io.File;
import java.io.FileInputStream;
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

    private byte[] imagenBytes;
    private int idUsuarioActual;

    private final List<ToggleButton> toggleCategorias = new ArrayList<>();

    public void setIdUsuarioActual(int idUsuario) {
        this.idUsuarioActual = idUsuario;
    }

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

            // Clase CSS
            String cssClass = "categoria-" + nombre.toLowerCase().replace(" ", "");
            tb.getStyleClass().add(cssClass);

            // Cargar ícono
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
    }

    @FXML
    public void seleccionarImagen(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(null);
        if (file != null) {
            imagenNombre.setText(file.getName());
            try (FileInputStream fis = new FileInputStream(file)) {
                imagenBytes = fis.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void guardarReceta(ActionEvent event) {
        if (titulo.getText().isEmpty() || duracion.getValue() == null || dificultad.getValue() == null) {
            mostrarAlerta("Campos incompletos",
                    "Debes llenar título, duración y dificultad.",
                    null);
            return;
        }

        List<Integer> categoriasSeleccionadas = new ArrayList<>();
        for (ToggleButton tb : toggleCategorias) {
            if (tb.isSelected()) {
                categoriasSeleccionadas.add((int) tb.getUserData());
            }
        }

        if (categoriasSeleccionadas.isEmpty()) {
            mostrarAlerta("Campos incompletos",
                    "Debes seleccionar al menos una categoría.",
                    null);
            return;
        }

        Receta receta = new Receta();
        receta.setTitulo(titulo.getText());
        receta.setDescripcion(descripcion.getText());
        receta.setDuracion(duracion.getValue());
        receta.setDificultad(dificultad.getValue());
        receta.setIngredientes(ingredientes.getText());
        receta.setPasos(pasos.getText());
        receta.setImagen(imagenBytes);
        receta.setId_usuario(idUsuarioActual);

        RecetaDAO dao = new RecetaDAO();
        boolean exito = dao.insertarReceta(receta, categoriasSeleccionadas);

        if (exito) {
            mostrarAlerta("Éxito", "La receta fue creada correctamente.", this::limpiarCampos);
        } else {
            mostrarAlerta("Error", "Hubo un problema al guardar la receta.", null);
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
        imagenBytes = null;

        for (ToggleButton tb : toggleCategorias) {
            tb.setSelected(false);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Runnable accion){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mx/edu/utez/proyectorecetario/view/Alertas/alert-alert.fxml"));
            Parent alertRoot = loader.load();

            mx.edu.utez.proyectorecetario.controller.AlertController controller =
                    loader.getController();

            Stage stage = new Stage();
            controller.setStage(stage);
            controller.configurar(titulo, mensaje, accion);

            stage.setScene(new Scene(alertRoot));
            stage.setResizable(false);
            stage.setTitle(titulo);
            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void onScreenHome(ActionEvent event) {
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