package com.example.practica_ii_fagr.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Respuesta de GET /api/v1/libros. La API usa HATEOAS, asi que la lista
 * viene adentro de "_embedded" -> "libroDtoList".
 */
public class LibrosResponse {

    @SerializedName("_embedded")
    private Embedded embedded;

    public List<Libro> getLibros() {
        if (embedded == null || embedded.libros == null) {
            return new ArrayList<>();
        }
        return embedded.libros;
    }

    public static class Embedded {
        @SerializedName("libroDtoList")
        List<Libro> libros;
    }
}
