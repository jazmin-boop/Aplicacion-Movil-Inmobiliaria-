package com.example.inmobiliaria.model;

public class Agente {
    private int id;
    private String nombre;
    private String telefono;
    private String email;
    private String especialidad;
    private String imagenUrl;

    public Agente() {}

    public Agente(int id, String nombre, String telefono, String email, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.especialidad = especialidad;
    }

    public Agente(String nombre, String telefono, String email, String especialidad) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.especialidad = especialidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
