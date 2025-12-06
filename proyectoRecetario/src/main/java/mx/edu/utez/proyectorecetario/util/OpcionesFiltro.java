package mx.edu.utez.proyectorecetario.util;

public class OpcionesFiltro {
    private int categoria;
    private String tiempo;

    public OpcionesFiltro(int categoria, String tiempo) {
        this.categoria = categoria;
        this.tiempo = tiempo;
    }

    public int getCategoria() { return categoria; }
    public String getTiempo() { return tiempo; }
    @Override
    public String toString() {
        return "Filtro: "+categoria+" | "+tiempo;
    }
}
