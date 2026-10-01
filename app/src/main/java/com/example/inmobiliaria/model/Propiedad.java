package com.example.inmobiliaria.model;

public class Propiedad {
    private int id;
    private String titulo;
    private String direccion;
    private double precio;
    private String tipo;
    private String estado;
    private String imagenUrl;
    private int vistas;
    private int consultas;
    private int favoritos;
    private int diasActivo;
    private String fechaPublicacion;

    public Propiedad() {}

    public Propiedad(int id, String titulo, String direccion, double precio, String tipo, String estado, String imagenUrl, int vistas, int consultas, int favoritos, int diasActivo, String fechaPublicacion) {
        this.id = id;
        this.titulo = titulo;
        this.direccion = direccion;
        this.precio = precio;
        this.tipo = tipo;
        this.estado = estado;
        this.imagenUrl = imagenUrl;
        this.vistas = vistas;
        this.consultas = consultas;
        this.favoritos = favoritos;
        this.diasActivo = diasActivo;
        this.fechaPublicacion = fechaPublicacion;
    }

    public Propiedad(String titulo, String direccion, double precio, String tipo, String estado, String imagenUrl) {
        this.titulo = titulo;
        this.direccion = direccion;
        this.precio = precio;
        this.tipo = tipo;
        this.estado = estado;
        this.imagenUrl = imagenUrl;
        this.vistas = (int) (Math.random() * 500) + 100;
        this.consultas = (int) (Math.random() * 30) + 5;
        this.favoritos = (int) (Math.random() * 50) + 10;
        this.diasActivo = (int) (Math.random() * 60) + 1;
        this.fechaPublicacion = "2026-06-07";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public int getVistas() {
        return vistas;
    }

    public void setVistas(int vistas) {
        this.vistas = vistas;
    }

    public int getConsultas() {
        return consultas;
    }

    public void setConsultas(int consultas) {
        this.consultas = consultas;
    }

    public int getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(int favoritos) {
        this.favoritos = favoritos;
    }

    public int getDiasActivo() {
        return diasActivo;
    }

    public void setDiasActivo(int diasActivo) {
        this.diasActivo = diasActivo;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    @Override
    public String toString() {
        return titulo + " (" + direccion + ")";
    }
}
