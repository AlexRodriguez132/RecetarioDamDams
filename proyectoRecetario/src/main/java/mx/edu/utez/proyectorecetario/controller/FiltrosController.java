package mx.edu.utez.proyectorecetario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import mx.edu.utez.proyectorecetario.util.OpcionesFiltro;

import java.util.function.Consumer;

public class FiltrosController {
    @FXML
    private FlowPane categoriesContainer;
    @FXML
    private FlowPane tiemposContainer;
    private ToggleGroup grupoCategorias;
    private ToggleGroup grupoTiempos;
    private ToggleButton ultimoBtnCategoria;
    private ToggleButton ultimoBtnTiempo;

    private int categoriaSeleccionada = 0;
    private String tiempoSeleccionado = "cualquiera";
    private Consumer<OpcionesFiltro> listenerFiltro;
    @FXML
    public void initialize() {
        grupoCategorias = new ToggleGroup();
        grupoTiempos = new ToggleGroup();
        for(Node categoriaBtn : categoriesContainer.getChildren()) {
            if(categoriaBtn instanceof ToggleButton) {
                ((ToggleButton) categoriaBtn).setToggleGroup(grupoCategorias);
            }
        }
        for(Node tiempoBtn : tiemposContainer.getChildren()) {
            if(tiempoBtn instanceof ToggleButton) {
                ((ToggleButton) tiempoBtn).setToggleGroup(grupoTiempos);
            }
        }
        grupoCategorias.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                ToggleButton seleccionado = (ToggleButton) newValue;
                ultimoBtnCategoria = seleccionado;
                switch(seleccionado.getText().toLowerCase()) {
                    case "desayuno":
                        categoriaSeleccionada = 1;
                        break;
                    case "comida":
                        categoriaSeleccionada = 2;
                        break;
                    case "cena":
                        categoriaSeleccionada = 3;
                        break;
                    case "postre":
                        categoriaSeleccionada = 4;
                        break;
                    case "bebidas":
                        categoriaSeleccionada = 5;
                        break;
                    case "vegetariano":
                        categoriaSeleccionada = 6;
                        break;
                    case "saludables":
                        categoriaSeleccionada = 7;
                        break;
                    case "internacional":
                        categoriaSeleccionada = 8;
                        break;
                    case "carnes":
                        categoriaSeleccionada = 9;
                        break;
                    case "pescados":
                        categoriaSeleccionada = 10;
                        break;
                    default:
                        categoriaSeleccionada = 0;
                }
            } else {
                categoriaSeleccionada = 0;
                ultimoBtnCategoria = null;
            }
            notificarCambios();
        });
        grupoTiempos.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                ToggleButton seleccionado = (ToggleButton) newValue;
                tiempoSeleccionado = seleccionado.getText();
                ultimoBtnTiempo = seleccionado;
            } else {
                tiempoSeleccionado = "cualquiera";
                ultimoBtnTiempo = null;
            }
            notificarCambios();
        });
    }
    @FXML
    private void onLimpiarFiltros() {
        if(ultimoBtnCategoria != null){
            ultimoBtnCategoria.setSelected(false);
        }
        if(ultimoBtnTiempo != null){
            ultimoBtnTiempo.setSelected(false);
        }
        categoriaSeleccionada = 0;
        tiempoSeleccionado = "cualquiera";
    }
    public void setOnFilterChanged(Consumer<OpcionesFiltro> listener) {
        this.listenerFiltro = listener;
    }
    private void notificarCambios() {
        if(listenerFiltro != null) {
            OpcionesFiltro opciones = new OpcionesFiltro(categoriaSeleccionada, tiempoSeleccionado);
            listenerFiltro.accept(opciones);
        }
    }
}
