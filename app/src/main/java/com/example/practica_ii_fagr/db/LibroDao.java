package com.example.practica_ii_fagr.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Operaciones de la tabla de favoritos (insertar, consultar, actualizar y eliminar).
 */
@Dao
public interface LibroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertar(LibroEntity libro);

    @Query("SELECT * FROM favoritos ORDER BY fecha_guardado DESC")
    List<LibroEntity> obtenerTodos();

    @Query("SELECT * FROM favoritos WHERE id = :id LIMIT 1")
    LibroEntity obtenerPorId(long id);

    @Query("SELECT id FROM favoritos")
    List<Long> obtenerIds();

    @Query("SELECT COUNT(*) FROM favoritos")
    int contar();

    @Update
    int actualizar(LibroEntity libro);

    @Delete
    int eliminar(LibroEntity libro);

    @Query("DELETE FROM favoritos WHERE id = :id")
    int eliminarPorId(long id);
}
