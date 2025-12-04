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
import java.net.URL;
import java.util.List;

public class RecetaCompletaController {

    private static final String RUTA_IMAGENES = System.getProperty("user.home") + "/Recetario/imagenes/";

    @FXML private Label lblTituloReceta;
    @FXML private ImageView imgReceta;
    @FXML private Label lblUsuario;
    @FXML private Label lblDuracion;
    @FXML private Label lblDificultad;
    @FXML private FlowPane boxCategorias;
    @FXML private Label txtDescripcion;
    @FXML private Label txtIngredientes;
    @FXML private Label txtPasos;

    @FXML private Button btnExportar;
    @FXML private ToggleButton btnFavorito;
    @FXML private ImageView imgFavoritoIcon;
    @FXML private StackPane contenedorImagen;

    private Receta receta;
    private boolean esFavorito = false;

    public void setReceta(Receta receta) {
        this.receta = receta;
        cargarDatos();
    }

    private void cargarDatos() {
        if (receta == null) return;

        lblTituloReceta.setText(nonNull(receta.getTitulo(), "Sin título"));
        lblDuracion.setText(nonNull(receta.getDuracion(), ""));
        lblDificultad.setText(nonNull(receta.getDificultad(), ""));
        txtDescripcion.setText(nonNull(receta.getDescripcion(), ""));
        txtIngredientes.setText(nonNull(receta.getIngredientes(), ""));
        txtPasos.setText(nonNull(receta.getPasos(), ""));

        try {
            Usuario autor = new UsuarioDAO().buscarPorId(receta.getId_usuario());
            lblUsuario.setText(autor != null ? autor.getNombre_usuario() : "Desconocido");
        } catch (Exception e) {
            lblUsuario.setText("Desconocido");
            e.printStackTrace();
        }

        cargarCategorias();
        cargarImagen(receta.getImagen());
        actualizarEstadoFavoritoUI();
    }

    private void cargarCategorias() {
        boxCategorias.getChildren().clear();
        try {
            List<Integer> ids = new RecetaDAO().obtenerCategoriasPorReceta(receta.getId_receta());
            if (ids == null || ids.isEmpty()) return;

            String[] todas = {
                    "desayuno","comida","cena","postre","bebida",
                    "vegetariano","saludable","internacional","carne","pescado"
            };

            for (Integer id : ids) {
                if (id == null) continue;
                int idx = id - 1;
                if (idx < 0 || idx >= todas.length) continue;
                String nombre = todas[idx];

                Label badge = new Label(nombre);
                badge.getStyleClass().add("categoria-badge");
                badge.getStyleClass().add("categoria-" + nombre);

                badge.setStyle("-fx-padding: 3 6 3 6; -fx-background-radius: 15; -fx-border-radius: 15;");
                badge.setMaxHeight(30);

                String rutaIcon = "/mx/edu/utez/proyectorecetario/images/categorias/" + nombre + ".png";
                InputStream is = getClass().getResourceAsStream(rutaIcon);
                if (is != null) {
                    ImageView iv = new ImageView(new Image(is));
                    iv.setFitWidth(18);
                    iv.setFitHeight(18);
                    badge.setGraphic(iv);
                    badge.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);
                } else {
                    System.out.println("No se pudo cargar icono de categoría: " + nombre);
                }

                boxCategorias.getChildren().add(badge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarImagen(String nombreArchivo) {
        try {
            if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
                File f = new File(RUTA_IMAGENES + nombreArchivo);
                if (f.exists()) {
                    imgReceta.setImage(new Image(f.toURI().toString()));
                    aplicarClipRedondeado();
                    return;
                }
            }
            mostrarImagenPorDefecto();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarImagenPorDefecto();
        }
    }

    private void mostrarImagenPorDefecto() {
        if (contenedorImagen != null) {
            contenedorImagen.setStyle(
                    "-fx-background-color: #F7B32D; " +
                            "-fx-background-radius: 30 30 30 30; " +
                            "-fx-border-radius: 30 30 30 30;"
            );
        }
        InputStream is = getClass().getResourceAsStream("/mx/edu/utez/proyectorecetario/images/recetas/imagecard.png");
        if (is != null) {
            imgReceta.setImage(new Image(is));
            aplicarClipRedondeado();
        }
    }

    private void aplicarClipRedondeado() {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        clip.widthProperty().bind(imgReceta.fitWidthProperty());
        clip.heightProperty().bind(imgReceta.fitHeightProperty());
        imgReceta.setClip(clip);
    }

    private void actualizarEstadoFavoritoUI() {
        try {
            if (!Sesion.haySesionActiva() || receta == null) {
                esFavorito = false;
            } else {
                RecetaDAO dao = new RecetaDAO();
                esFavorito = dao.esFavorito(Sesion.getUsuarioActual().getId_usuario(), receta.getId_receta());
            }
            if (btnFavorito != null) btnFavorito.setSelected(esFavorito);
            actualizarIconoFavorito();
        } catch (Exception e) {
            e.printStackTrace();
            esFavorito = false;
            if (btnFavorito != null) btnFavorito.setSelected(false);
            actualizarIconoFavorito();
        }
    }

    private void actualizarIconoFavorito() {
        try {
            String ruta = esFavorito
                    ? "/mx/edu/utez/proyectorecetario/images/recetas/favorito.png"
                    : "/mx/edu/utez/proyectorecetario/images/recetas/nofavorito.png";
            InputStream is = getClass().getResourceAsStream(ruta);
            if (is != null && imgFavoritoIcon != null) imgFavoritoIcon.setImage(new Image(is));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onToggleFavorito(ActionEvent event) {
        if (receta == null) return;
        if (!Sesion.haySesionActiva()) {
            if (btnFavorito != null) btnFavorito.setSelected(esFavorito);
            return;
        }
        try {
            RecetaDAO dao = new RecetaDAO();
            int idUsuario = Sesion.getUsuarioActual().getId_usuario();
            if (esFavorito) {
                boolean ok = dao.eliminarFavorito(idUsuario, receta.getId_receta());
                if (ok) esFavorito = false;
            } else {
                boolean ok = dao.agregarFavorito(idUsuario, receta.getId_receta());
                if (ok) esFavorito = true;
            }
            if (btnFavorito != null) btnFavorito.setSelected(esFavorito);
            actualizarIconoFavorito();
        } catch (Exception e) {
            e.printStackTrace();
            if (btnFavorito != null) btnFavorito.setSelected(esFavorito);
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


    @FXML
    private void onScreenHome(ActionEvent event) {
        try {
            URL url = getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/recetas-view.fxml");
            if (url == null) {
                System.out.println("No se encontró recetas-view.fxml");
                return;
            }
            Parent root = FXMLLoader.load(url);
            if (event != null && event.getSource() instanceof Node) {
                ((Node) event.getSource()).getScene().setRoot(root);
            } else {
                Stage st = new Stage();
                st.setScene(new Scene(root));
                st.setMaximized(true);
                st.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String nonNull(String s, String fallback) {
        return s == null ? fallback : s;
    }
}
