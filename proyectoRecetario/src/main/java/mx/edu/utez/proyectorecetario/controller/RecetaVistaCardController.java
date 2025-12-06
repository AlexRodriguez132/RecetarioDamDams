package mx.edu.utez.proyectorecetario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import mx.edu.utez.proyectorecetario.dao.RecetaDAO;
import mx.edu.utez.proyectorecetario.dao.UsuarioDAO;
import mx.edu.utez.proyectorecetario.model.Receta;
import mx.edu.utez.proyectorecetario.model.Usuario;
import mx.edu.utez.proyectorecetario.util.PDFUtil;
import mx.edu.utez.proyectorecetario.util.Sesion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class RecetaVistaCardController {

    @FXML private StackPane root;
    @FXML private ImageView imgReceta;
    @FXML private Label lblTitulo;
    @FXML private HBox boxCategorias;
    @FXML private Label lblDescripcion;
    @FXML private Label lblDuracion;
    @FXML private Label lblDificultad;
    @FXML private Label lblUsuario;
    @FXML private FlowPane flowFavorito;
    @FXML private ImageView imgFavorito;
    @FXML private StackPane contenedorImagen;

    private Receta receta;
    private boolean esFavorito = false;
    private FlowPane contenedorPadre;
    public void setContenedorPadre(FlowPane contenedorPadre) {
        this.contenedorPadre = contenedorPadre;
    }

    private static final String RUTA_IMAGENES =
            System.getProperty("user.home") + "/Recetario/imagenes/";

    @FXML
    public void initialize() {
        if (root != null) root.setCursor(Cursor.HAND);
        if (flowFavorito != null) flowFavorito.setCursor(Cursor.HAND);
    }

    public void setReceta(Receta receta) {
        this.receta = receta;

        lblTitulo.setText(receta.getTitulo());
        lblDescripcion.setText(receta.getDescripcion());
        lblDuracion.setText(receta.getDuracion());
        lblDificultad.setText(receta.getDificultad());

        try {
            Usuario autor = new UsuarioDAO().buscarPorId(receta.getId_usuario());
            lblUsuario.setText(
                    autor != null ? autor.getNombre_usuario() : "Desconocido"
            );
        } catch (Exception e) {
            lblUsuario.setText("Desconocido");
        }

        cargarCategorias();
        cargarImagen(receta.getImagen());
        validarFavorito();
    }

    private void cargarCategorias() {
        try {
            boxCategorias.getChildren().clear();

            List<Integer> ids =
                    new RecetaDAO().obtenerCategoriasPorReceta(
                            receta.getId_receta()
                    );

            String[] todas = {
                    "desayuno","comida","cena","postre","bebida",
                    "vegetariano","saludable","internacional","carne","pescado"
            };

            int max = 2;

            for (int i = 0; i < ids.size() && i < max; i++) {
                int idCat = ids.get(i);
                if (idCat >= 1 && idCat <= todas.length) {
                    String nombre = todas[idCat - 1];

                    Label l = new Label(nombre);
                    l.getStyleClass().add("categoria-" + nombre);

                    String ruta = "/mx/edu/utez/proyectorecetario/images/categorias/" + nombre + ".png";
                    InputStream is = getClass().getResourceAsStream(ruta);
                    if (is != null) {
                        ImageView icon = new ImageView(new Image(is));
                        icon.setFitWidth(18);
                        icon.setFitHeight(18);
                        l.setGraphic(icon);
                    }

                    boxCategorias.getChildren().add(l);
                }
            }

            if (ids.size() > max) {
                boxCategorias.getChildren().add(
                        new Label("+" + (ids.size() - max))
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validarFavorito() {
        if (!Sesion.haySesionActiva()) return;

        RecetaDAO dao = new RecetaDAO();

        esFavorito = dao.esFavorito(
                Sesion.getUsuarioActual().getId_usuario(),
                receta.getId_receta()
        );

        actualizarIconoFavorito();
    }

    @FXML
    private void toggleFavorito(MouseEvent e) {
        e.consume();
        if (!Sesion.haySesionActiva() || receta == null) return;

        if (esFavorito) eliminarFavorito();
        else guardarFavorito();
    }

    private void guardarFavorito() {
        RecetaDAO dao = new RecetaDAO();

        if (dao.agregarFavorito(
                Sesion.getUsuarioActual().getId_usuario(),
                receta.getId_receta()
        )) {
            esFavorito = true;
            actualizarIconoFavorito();
        }
    }

    private void eliminarFavorito() {
        RecetaDAO dao = new RecetaDAO();

        if (dao.eliminarFavorito(
                Sesion.getUsuarioActual().getId_usuario(),
                receta.getId_receta()
        )) {
            esFavorito = false;
            actualizarIconoFavorito();


            if (root.getParent() instanceof FlowPane) {
                FlowPane contenedor = (FlowPane) root.getParent();
                contenedor.getChildren().remove(root);
            }
        }
    }

    private void actualizarIconoFavorito() {
        String ruta = esFavorito
                ? "/mx/edu/utez/proyectorecetario/images/recetas/favorito.png"
                : "/mx/edu/utez/proyectorecetario/images/recetas/nofavorito.png";

        InputStream is = getClass().getResourceAsStream(ruta);

        if (is != null && imgFavorito != null) {
            imgFavorito.setImage(new Image(is));
        }
    }

    private void cargarImagen(String nombreArchivo) {
        try {
            if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
                File file = new File(RUTA_IMAGENES + nombreArchivo);
                if (file.exists()) {
                    Image img = new Image(file.toURI().toString());
                    imgReceta.setImage(img);

                    Rectangle clip = new Rectangle(300, 200);
                    clip.setArcWidth(60);
                    clip.setArcHeight(60);
                    imgReceta.setClip(clip);
                    return;
                }
            }
        } catch (Exception ignored) {}

        mostrarImagenPorDefecto();
    }

    private void mostrarImagenPorDefecto() {

        if (contenedorImagen != null) {
            contenedorImagen.setStyle(
                    "-fx-background-color: #F7B32D;" +
                            "-fx-background-radius: 30 30 0 0;" +
                            "-fx-border-radius: 30 30 0 0;"
            );
        }

        try {
            InputStream is = getClass().getResourceAsStream(
                    "/mx/edu/utez/proyectorecetario/images/recetas/imagecard.png"
            );

            if (is != null) {
                Image img = new Image(is);
                imgReceta.setImage(img);

                Rectangle clip = new Rectangle(300, 200);
                clip.setArcWidth(60);
                clip.setArcHeight(60);
                imgReceta.setClip(clip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onVerReceta(MouseEvent event) {
        try {
            URL url = getClass().getResource(
                    "/mx/edu/utez/proyectorecetario/view/App/VisualizarReceta/recetacompleta-view.fxml"
            );

            FXMLLoader loader = new FXMLLoader(url);
            Parent vista = loader.load();

            RecetaCompletaController controller =
                    loader.getController();

            controller.setReceta(receta);

            root.getScene().setRoot(vista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onExportar(ActionEvent event) {
        try {
            if (receta == null) {
                mostrarAlertaError(
                        "Error",
                        "No hay ninguna receta para exportar.",
                        null
                );
                return;
            }

            String carpetaPath = System.getProperty("user.home") + "/Recetario/PDF/";
            File carpeta = new File(carpetaPath);

            if (!carpeta.exists()) {
                carpeta.mkdirs(); // crea toda la ruta si no existe
            }

            String nombreSeguro = receta.getTitulo()
                    .replaceAll("[^a-zA-Z0-9]", "_");

            String ruta = carpetaPath + nombreSeguro + ".pdf";

            PDFUtil.exportarReceta(ruta, receta);


            mostrarAlertaExito(
                    "Receta Exportada",
                    "La receta se exportó correctamente en:\n" + ruta,
                    () -> System.out.println("PDF creado con elegancia suprema.")
            );

        } catch (Exception e) {
            e.printStackTrace();


            mostrarAlertaError(
                    "Error al Exportar",
                    "Ocurrió un problema al generar el PDF:\n" + e.getMessage(),
                    null
            );
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
}