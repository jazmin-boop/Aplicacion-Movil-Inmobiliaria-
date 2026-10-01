package com.example.inmobiliaria.model;

public class Visita {
    private int id;
    private int idPropiedad;
    private int idCliente;
    private int idAgente;
    private String fechaHora;
    private String comentarios;
    private String estado;

    // Helper display fields
    private String tituloPropiedad;
    private String imagenUrlPropiedad;
    private String direccionPropiedad;
    private double precioPropiedad;
    private String nombreCliente;
    private String nombreAgente;

    public Visita() {}

    public Visita(int id, int idPropiedad, int idCliente, int idAgente, String fechaHora, String comentarios, String estado) {
        this.id = id;
        this.idPropiedad = idPropiedad;
        this.idCliente = idCliente;
        this.idAgente = idAgente;
        this.fechaHora = fechaHora;
        this.comentarios = comentarios;
        this.estado = estado;
    }

    public Visita(int idPropiedad, int idCliente, int idAgente, String fechaHora, String comentarios, String estado) {
        this.idPropiedad = idPropiedad;
        this.idCliente = idCliente;
        this.idAgente = idAgente;
        this.fechaHora = fechaHora;
        this.comentarios = comentarios;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPropiedad() {
        return idPropiedad;
    }

    public void setIdPropiedad(int idPropiedad) {
        this.idPropiedad = idPropiedad;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdAgente() {
        return idAgente;
    }

    public void setIdAgente(int idAgente) {
        this.idAgente = idAgente;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTituloPropiedad() {
        return tituloPropiedad;
    }

    public void setTituloPropiedad(String tituloPropiedad) {
        this.tituloPropiedad = tituloPropiedad;
    }

    public String getImagenUrlPropiedad() {
        return imagenUrlPropiedad;
    }

    public void setImagenUrlPropiedad(String imagenUrlPropiedad) {
        this.imagenUrlPropiedad = imagenUrlPropiedad;
    }

    public String getDireccionPropiedad() {
        return direccionPropiedad;
    }

    public void setDireccionPropiedad(String direccionPropiedad) {
        this.direccionPropiedad = direccionPropiedad;
    }

    public double getPrecioPropiedad() {
        return precioPropiedad;
    }

    public void setPrecioPropiedad(double precioPropiedad) {
        this.precioPropiedad = precioPropiedad;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreAgente() {
        return nombreAgente;
    }

    public void setNombreAgente(String nombreAgente) {
        this.nombreAgente = nombreAgente;
    }
}
