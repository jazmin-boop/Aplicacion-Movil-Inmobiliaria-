package com.example.inmobiliaria.model;

public class Cliente {
    private int id;
    private String nombre;
    private String telefono;
    private String email;
    private double presupuesto;
    private String interes;
    private String imagenUrl;

    public Cliente() {}

    public Cliente(int id, String nombre, String telefono, String email, double presupuesto, String interes) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.presupuesto = presupuesto;
        this.interes = interes;
    }

    public Cliente(String nombre, String telefono, String email, double presupuesto, String interes) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.presupuesto = presupuesto;
        this.interes = interes;
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

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getInteres() {
        return interes;
    }

    public void setInteres(String interes) {
        this.interes = interes;
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
