package com.example.practica_ii_fagr.api;

import com.example.practica_ii_fagr.models.Libro;
import com.example.practica_ii_fagr.models.LibrosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Endpoints de la API REST de Biblioteca que usa la app.
 */
public interface LibrosApi {

    // catalogo completo
    @GET("api/v1/libros")
    Call<LibrosResponse> obtenerLibros();

    // detalle de un libro
    @GET("api/v1/libros/{id}")
    Call<Libro> obtenerLibro(@Path("id") long id);
}
