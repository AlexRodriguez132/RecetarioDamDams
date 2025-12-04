package mx.edu.utez.proyectorecetario.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
import java.util.ArrayList;
import java.util.List;

public class RecetaCardController {

    @FXML private ImageView imgReceta;
    @FXML private Label lblTitulo;
    @FXML private Label lblTituloOverlay;
    @FXML private HBox boxCategorias;
    @FXML private Label lblDescripcion;
    @FXML private Label lblDificultad;
    @FXML private Label lblDuracion;
    @FXML private Label lblUsuario;
    @FXML private StackPane contenedorImagen;
    @FXML private StackPane overlay;
    @FXML private ImageView imgFavorito;
    @FXML private FlowPane flowFavorito;

    private Receta receta;
    private static final String RUTA_IMAGENES = System.getProperty("user.home") + "/Recetario/imagenes/";
    private static final List<RecetaCardController> tarjetas = new ArrayList<>();
    private boolean overlayActivo = false;
    private boolean esFavorito = false;

    private FlowPane contenedorPadre;
    private Node rootCard;

    public void setContenedorPadre(FlowPane contenedorPadre) {
        this.contenedorPadre = contenedorPadre;
    }


    public void setReceta(Receta receta, List<Integer> idsCategorias) {
        this.receta = receta;

        lblTitulo.setText(receta.getTitulo() == null ? "Sin título" : receta.getTitulo());
        lblTituloOverlay.setText(receta.getTitulo() == null ? "Sin título" : receta.getTitulo());
        lblDescripcion.setText(receta.getDescripcion() == null ? "" : receta.getDescripcion());
        lblDificultad.setText(receta.getDificultad() == null ? "" : receta.getDificultad());
        lblDuracion.setText(receta.getDuracion() == null ? "" : receta.getDuracion());

        Usuario autor = new UsuarioDAO().buscarPorId(receta.getId_usuario());
        lblUsuario.setText(autor != null ? autor.getNombre_usuario() : "Desconocido");

        cargarCategorias(idsCategorias);
        cargarImagen(receta.getImagen());
        cargarEstadoFavorito();

        tarjetas.add(this);


        if (overlay != null && overlay.getParent() != null) {
            rootCard = (Node) overlay.getParent();
        }


        if (contenedorImagen != null && contenedorImagen.getParent() != null) {
            contenedorImagen.getParent().setOnMouseClicked(event -> toggleOverlay());
        }


        if (flowFavorito != null) {
            flowFavorito.setPickOnBounds(true);
            flowFavorito.setMouseTransparent(false);
            flowFavorito.setOnMouseClicked(e -> {
                e.consume();
                toggleFavorito(e);
            });
        }

        if (imgFavorito != null) {
            actualizarIconoFavorito();
        }
    }

    private void toggleOverlay() {
        if (!overlayActivo) {

            for (RecetaCardController t : tarjetas) {
                if (t != this && t.overlay != null) {
                    t.overlay.setVisible(false);
                    t.overlayActivo = false;
                }
            }
            if (overlay != null) overlay.setVisible(true);
            overlayActivo = true;
        } else {
            if (overlay != null) overlay.setVisible(false);
            overlayActivo = false;
        }
    }

    private void cargarCategorias(List<Integer> idsCategorias) {
        boxCategorias.getChildren().clear();
        if (idsCategorias == null || idsCategorias.isEmpty()) return;

        String[] todasCategorias = {"desayuno", "comida", "cena", "postre", "bebida",
                "vegetariano", "saludable", "internacional", "carne", "pescado"};
        int maxMostrar = 2;

        for (int i = 0; i < idsCategorias.size() && i < maxMostrar; i++) {
            int idCat = idsCategorias.get(i);
            if (idCat >= 1 && idCat <= todasCategorias.length) {
                String nombre = todasCategorias[idCat - 1];

                Label lbl = new Label(nombre);
                String cssClass = "categoria-" + nombre.toLowerCase().replace(" ", "");
                lbl.getStyleClass().add(cssClass);

                lbl.setMaxWidth(140);
                lbl.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);

                String rutaImagen = "/mx/edu/utez/proyectorecetario/images/categorias/" + nombre + ".png";
                InputStream is = getClass().getResourceAsStream(rutaImagen);
                if (is != null) {
                    ImageView icon = new ImageView(new Image(is));
                    icon.setFitWidth(18);
                    icon.setFitHeight(18);
                    lbl.setGraphic(icon);
                    lbl.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);
                }

                boxCategorias.getChildren().add(lbl);
            }
        }

        if (idsCategorias.size() > maxMostrar) {
            Label mas = new Label("+" + (idsCategorias.size() - maxMostrar));
            boxCategorias.getChildren().add(mas);
        }
    }

    private void cargarImagen(String nombreArchivo) {
        try {
            if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
                File file = new File(RUTA_IMAGENES + nombreArchivo);
                if (file.exists()) {
                    Image img = new Image(file.toURI().toString());
                    imgReceta.setImage(img);

                    Rectangle clip = new Rectangle();
                    clip.setArcWidth(60);
                    clip.setArcHeight(60);

                    // bind al fitWidth/fitHeight para que el clip siempre siga el tamaño de la ImageView
                    clip.widthProperty().bind(imgReceta.fitWidthProperty());
                    clip.heightProperty().bind(imgReceta.fitHeightProperty());

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
                    "-fx-background-color: #F7B32D; " +
                            "-fx-background-radius: 30 30 0 0; " +
                            "-fx-border-radius: 30 30 0 0;"
            );
        }

        InputStream is = getClass().getResourceAsStream(
                "/mx/edu/utez/proyectorecetario/images/recetas/imagecard.png"
        );

        if (is != null && imgReceta != null) {
            Image img = new Image(is);
            imgReceta.setImage(img);

            Rectangle clip = new Rectangle();
            clip.setArcWidth(30);
            clip.setArcHeight(30);

            clip.widthProperty().bind(imgReceta.fitWidthProperty());
            clip.heightProperty().bind(imgReceta.fitHeightProperty());

            imgReceta.setClip(clip);
        }
    }

    private void cargarEstadoFavorito() {
        if (!Sesion.haySesionActiva() || receta == null) {
            esFavorito = false;
            actualizarIconoFavorito();
            return;
        }

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

        if (esFavorito) {
            eliminarFavorito();
        } else {
            guardarFavorito();
        }
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

            if (contenedorPadre != null && rootCard != null) {
                Platform.runLater(() -> {
                    contenedorPadre.getChildren().remove(rootCard);
                    contenedorPadre.requestLayout();
                });
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

    @FXML
    public void onEditar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/mx/edu/utez/proyectorecetario/view/App/EditarRecetas/editarreceta-view.fxml"
                    )
            );

            Parent root = loader.load();

            EditarRecetaController controller = loader.getController();
            controller.setReceta(receta);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            Object src = event.getSource();
            if (src instanceof Node) {
                ((Node) src).getScene().getWindow().hide();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEliminar(ActionEvent event) {
        mostrarAlerta("Confirmación", "¿Deseas eliminar esta receta?", () -> {
            if (receta == null) return;

            RecetaDAO dao = new RecetaDAO();
            boolean exito = dao.eliminarReceta(receta.getId_receta());

            if (exito && contenedorPadre != null && rootCard != null) {
                Platform.runLater(() -> {
                    contenedorPadre.getChildren().remove(rootCard);
                    contenedorPadre.requestLayout();
                });
                mostrarAlertaExito("Éxito", "Receta eliminada correctamente", null);
            } else {
                mostrarAlertaError("Error", "No se pudo eliminar la receta", null);
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje, Runnable accion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mx/edu/utez/proyectorecetario/view/Alertas/alert-alert.fxml"));
            Parent alertRoot = loader.load();

            AlertController controller = loader.getController();
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

}
