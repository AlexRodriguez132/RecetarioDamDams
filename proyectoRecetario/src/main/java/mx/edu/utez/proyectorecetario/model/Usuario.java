package mx.edu.utez.proyectorecetario.model;

import java.util.Date;

public class Usuario {
    private int id_usuario;
    private String nombre_usuario;
    private String contrasena;
    private String email;
    private Date fechaRegistro;

    public Usuario(){
    }

    public Usuario(int id_usuario, String nombre_usuario, String contrasena, String email, Date fechaRegistro){
        this.id_usuario = id_usuario;
        this.nombre_usuario =  nombre_usuario;
        this.contrasena = contrasena;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
    }

    public Usuario(String nombre_usuario, String contrasena, String email) {
        this.nombre_usuario = nombre_usuario;
        this.contrasena = contrasena;
        this.email = email;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getEmail() {
        return email;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
