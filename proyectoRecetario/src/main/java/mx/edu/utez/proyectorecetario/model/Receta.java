package mx.edu.utez.proyectorecetario.model;

import java.util.List;

public class Receta {

    private int id_receta;
    private String titulo;
    private String descripcion;
    private String imagen;
    private String dificultad;
    private String duracion;
    private String ingredientes;
    private String pasos;
    private int id_usuario;
    private Usuario creador;
    private List<Integer> id_por_categoria;
    private boolean esFavorito;

    public int getId_receta() { return id_receta; }
    public void setId_receta(int id_receta) { this.id_receta = id_receta; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public String getIngredientes() { return ingredientes; }
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }

    public String getPasos() { return pasos; }
    public void setPasos(String pasos) { this.pasos = pasos; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public List<Integer> getId_por_categoria() {
        return id_por_categoria;
    }

    public void setId_por_categoria(List<Integer> id_por_categoria) {
        this.id_por_categoria = id_por_categoria;
    }

    public boolean isEsFavorito() {
        return esFavorito;
    }

    public void setEsFavorito(boolean esFavorito) {
        this.esFavorito = esFavorito;
    }
}