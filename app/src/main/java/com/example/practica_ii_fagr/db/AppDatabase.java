package com.example.practica_ii_fagr.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Configuracion de la base de datos local (SQLite con Room).
 */
@Database(entities = {LibroEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String NOMBRE_BD = "explorador_libros.db";
    private static volatile AppDatabase instancia;

    public abstract LibroDao libroDao();

    // una sola instancia para toda la app
    public static AppDatabase getInstance(Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, NOMBRE_BD).build();
                }
            }
        }
        return instancia;
    }
}
