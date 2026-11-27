package com.example.practica_ii_fagr.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.practica_ii_fagr.models.Libro;

/**
 * Tabla de Room con los libros favoritos (para verlos sin internet).
 */
@Entity(tableName = "favoritos")
public class LibroEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public long id;            // mismo ID que tiene en el servidor

    @NonNull
    @ColumnInfo(name = "titulo")
    public String titulo = "";

    @ColumnInfo(name = "autor")
    public String autor;

    @ColumnInfo(name = "editorial")
    public String editorial;

    @ColumnInfo(name = "paginas")
    public int paginas;

    @ColumnInfo(name = "disponible")
    public boolean disponible;

    @ColumnInfo(name = "fecha_guardado")
    public long fechaGuardado;

    public LibroEntity() {
    }

    // convierte el DTO de la API a la entidad de Room
    public static LibroEntity desde(Libro libro) {
        LibroEntity e = new LibroEntity();
        e.id = libro.getId();
        e.titulo = libro.getTitulo();
        e.autor = libro.getAutor();
        e.editorial = libro.getEditorial();
        e.paginas = libro.getPaginas();
        e.disponible = libro.isDisponible();
        e.fechaGuardado = System.currentTimeMillis();
        return e;
    }

    // y al reves, para reutilizar las pantallas
    public Libro aLibro() {
        return new Libro(id, titulo, autor, editorial, paginas, disponible);
    }
}
