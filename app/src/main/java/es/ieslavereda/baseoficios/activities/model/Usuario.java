package es.ieslavereda.baseoficios.activities.model;

import java.io.Serializable;

public class Usuario implements Serializable {

    private int idUsuario, oficio_idOficio;
    private String nombre, apellidos;

    public Usuario(int idUsuario, String nombre, String apellidos, int oficio_idOficio){
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.oficio_idOficio = oficio_idOficio;
    }
    public Usuario(String nombre, String apellidos, int oficio_idOficio){
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.oficio_idOficio = oficio_idOficio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getOficio_idOficio() {
        return oficio_idOficio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }
}
