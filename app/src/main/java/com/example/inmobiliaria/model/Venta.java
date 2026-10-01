package com.example.inmobiliaria.model;

public class Venta {
    private int id;
    private int idPropiedad;
    private int idCliente;
    private int idAgente;
    private String fechaVenta;
    private double montoFinal;
    private double comision;
    private String metodoPago;

    // Helper display fields
    private String tituloPropiedad;
    private String imagenUrlPropiedad;
    private String direccionPropiedad;
    private String nombreCliente;
    private String nombreAgente;

    public Venta() {}

    public Venta(int id, int idPropiedad, int idCliente, int idAgente, String fechaVenta, double montoFinal, double comision) {
        this.id = id;
        this.idPropiedad = idPropiedad;
        this.idCliente = idCliente;
        this.idAgente = idAgente;
        this.fechaVenta = fechaVenta;
        this.montoFinal = montoFinal;
        this.comision = comision;
    }

    public Venta(int idPropiedad, int idCliente, int idAgente, String fechaVenta, double montoFinal, double comision) {
        this.idPropiedad = idPropiedad;
        this.idCliente = idCliente;
        this.idAgente = idAgente;
        this.fechaVenta = fechaVenta;
        this.montoFinal = montoFinal;
        this.comision = comision;
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

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public double getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(double montoFinal) {
        this.montoFinal = montoFinal;
    }

    public double getComision() {
        return comision;
    }

    public void setComision(double comision) {
        this.comision = comision;
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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
