package mx.edu.utez.proyectorecetario.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import mx.edu.utez.proyectorecetario.dao.RecetaDAO;
import mx.edu.utez.proyectorecetario.model.Receta;
import mx.edu.utez.proyectorecetario.util.OpcionesFiltro;
import mx.edu.utez.proyectorecetario.util.Sesion;

import java.io.IOException;
import java.util.List;

public class HomeController {
    @FXML private TextField tfBuscar;
    @FXML private FlowPane cardsContainer;
    @FXML private Button btnFiltrar;
    private RecetaDAO recetaDAO = new RecetaDAO();
    private ObservableList<Receta> recetas = FXCollections.observableArrayList();
    private FilteredList<Receta> recetasFiltradas;
    private Popup filterPopup;

    @FXML
    private void initialize() {
        cargarDatosDeRecetas();
        recetasFiltradas = new FilteredList<>(recetas);
        tfBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            String prefijo = newValue.toLowerCase();
            recetasFiltradas.setPredicate( receta -> receta.getIngredientes().toLowerCase().contains(prefijo));
            cargarRecetas();
        });
        inicializarPopup();
    }
    @FXML
    private void onFiltroPopUp() {
        if(!filterPopup.isShowing()){
            var bounds = btnFiltrar.localToScreen(btnFiltrar.getBoundsInLocal());
            filterPopup.show(btnFiltrar, bounds.getMinX(), bounds.getMaxY() + 5);

            Scene scene = btnFiltrar.getScene();
            if (scene != null && scene.getWindow() != null) {

                scene.getWindow().focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        filterPopup.hide();
                    }
                });
            }
        } else {
            filterPopup.hide();
        }
    }
    private void inicializarPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/Filtrado/filtros-view.fxml")
            );
            Parent menuContent = loader.load();
            FiltrosController filtrosController = loader.getController();
            filtrosController.setOnFilterChanged(opcionesFiltro -> {
                System.out.println("Recibido: "+opcionesFiltro.toString());
                aplicarFiltros(opcionesFiltro);
            });
            filterPopup = new Popup();
            filterPopup.getContent().add(menuContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void aplicarFiltros(OpcionesFiltro opcionesFiltro){
        recetasFiltradas.setPredicate( receta -> {

            if(opcionesFiltro.getCategoria() != 0) {
                for(Integer id :  receta.getId_por_categoria()) {
                    System.out.println("Platillo: "+receta.getTitulo()+"\nId categoria: "+id);
                    if(!id.equals(opcionesFiltro.getCategoria())) {
                        return false;
                    }
                }
            }

            if(opcionesFiltro.getTiempo() != null && !opcionesFiltro.getTiempo().equalsIgnoreCase("cualquiera")) {
                if(opcionesFiltro.getTiempo().equalsIgnoreCase("< 15 min")) {
                    if(!receta.getDuracion().equalsIgnoreCase("< 15 min")) return false;
                }
                else if(opcionesFiltro.getTiempo().equalsIgnoreCase("15 - 30 min")) {
                    if(!receta.getDuracion().equalsIgnoreCase("15 - 30 min")) return false;
                }
                else if(opcionesFiltro.getTiempo().equalsIgnoreCase("30 - 60 min")) {
                    if(!receta.getDuracion().equalsIgnoreCase("30 - 60 min")) return false;
                }
                else if(opcionesFiltro.getTiempo().equalsIgnoreCase("> 60 min")) {
                    if(!receta.getDuracion().equalsIgnoreCase("> 60 min")) return false;
                }
            }
            return true;
        });
        cargarRecetas();
    }
    private void cargarDatosDeRecetas() {
        cardsContainer.setVisible(false);
        int idUsuario = Sesion.getUsuarioActual().getId_usuario();
        Task<List<Receta>> consulta = new Task<>() {
            @Override
            protected List<Receta> call() throws Exception {
                return recetaDAO.obtenerRecetasDeOtrosUsuarios(idUsuario);
            }
        };
        consulta.setOnSucceeded(event -> {
            List<Receta> listaRecetas  = consulta.getValue();
            recetas.clear();
            recetas.addAll(listaRecetas);
            cargarRecetas();
            cardsContainer.setVisible(true);
        });
        consulta.setOnFailed(event -> {
            Throwable error = consulta.getException();
            error.printStackTrace();
        });

        new Thread(consulta).start();
    }
    private void cargarRecetas() {
        cardsContainer.getChildren().clear();
        if (!Sesion.haySesionActiva()) {
            return;
        }

        if (recetasFiltradas == null || recetasFiltradas.isEmpty()) {
            cardsContainer.getChildren().add(new Label("No se encontraron coincidencias"));
            return;
        }
        for (Receta r : recetasFiltradas) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/mx/edu/utez/proyectorecetario/view/App/Cards/recetacard.fxml")
                );
                Parent card = loader.load();
                RecetaCardController controller = loader.getController();
                controller.setContenedorPadre(cardsContainer);
                controller.setReceta(r, r.getId_por_categoria());
                cardsContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onEnterBuscar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            realizarBusqueda();
        }
    }

    private void realizarBusqueda() {
        String texto = tfBuscar.getText().trim();

    }
}
