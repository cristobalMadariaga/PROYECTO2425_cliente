package es.ieslavereda.baseoficios.activities.model;

public class Usuario {

    private int idUsuario, Oficio_idOficio;
    private String nombre, apellidos;

    public Usuario(int idUsuario, String nombre, String apellidos, int Oficio_idOficio){
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.Oficio_idOficio = Oficio_idOficio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getOficio_idOficio() {
        return Oficio_idOficio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }
}
