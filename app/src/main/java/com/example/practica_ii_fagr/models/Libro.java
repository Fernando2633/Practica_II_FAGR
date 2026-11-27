package com.example.practica_ii_fagr.models;

import com.google.gson.annotations.SerializedName;

/**
 * DTO de un libro tal como lo manda la API (Gson lo llena solo).
 */
public class Libro {

    @SerializedName("id")
    private long id;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("autor")
    private String autor;

    @SerializedName("editorial")
    private String editorial;

    @SerializedName("paginas")
    private int paginas;

    @SerializedName("disponible")
    private boolean disponible;

    public Libro() {
    }

    public Libro(long id, String titulo, String autor, String editorial, int paginas, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.paginas = paginas;
        this.disponible = disponible;
    }

    public long getId() { return id; }

    // si algun dato viene null se regresa vacio para no tronar la app
    public String getTitulo() { return titulo != null ? titulo : ""; }
    public String getAutor() { return autor != null ? autor : ""; }
    public String getEditorial() { return editorial != null ? editorial : ""; }
    public int getPaginas() { return paginas; }
    public boolean isDisponible() { return disponible; }
}
