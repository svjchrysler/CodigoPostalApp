package com.codigopostal.svjchrysler.codigopostal.Models;

/**
 * Created by salguero on 13-07-16.
 */
public class Province {
    public String id;
    public String nombre;

    public Province(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String toString() {
        return this.nombre;
    }
}
