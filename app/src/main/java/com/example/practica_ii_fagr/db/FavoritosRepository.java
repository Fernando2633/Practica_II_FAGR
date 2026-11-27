package com.example.practica_ii_fagr.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.practica_ii_fagr.models.Libro;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hace las operaciones de Room en un hilo secundario y regresa
 * el resultado al hilo principal (para no congelar la pantalla).
 */
public class FavoritosRepository {

    public interface Callback<T> {
        void onResultado(T resultado);
    }

    private static final ExecutorService HILO_BD = Executors.newSingleThreadExecutor();

    private final LibroDao dao;
    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());

    public FavoritosRepository(Context context) {
        dao = AppDatabase.getInstance(context).libroDao();
    }

    public void obtenerTodos(Callback<List<LibroEntity>> callback) {
        HILO_BD.execute(() -> {
            List<LibroEntity> lista = dao.obtenerTodos();
            hiloPrincipal.post(() -> callback.onResultado(lista));
        });
    }

    public void obtenerIds(Callback<Set<Long>> callback) {
        HILO_BD.execute(() -> {
            Set<Long> ids = new HashSet<>(dao.obtenerIds());
            hiloPrincipal.post(() -> callback.onResultado(ids));
        });
    }

    public void obtenerPorId(long id, Callback<LibroEntity> callback) {
        HILO_BD.execute(() -> {
            LibroEntity libro = dao.obtenerPorId(id);
            hiloPrincipal.post(() -> callback.onResultado(libro));
        });
    }

    public void esFavorito(long id, Callback<Boolean> callback) {
        obtenerPorId(id, libro -> callback.onResultado(libro != null));
    }

    public void contar(Callback<Integer> callback) {
        HILO_BD.execute(() -> {
            int total = dao.contar();
            hiloPrincipal.post(() -> callback.onResultado(total));
        });
    }

    public void agregar(LibroEntity libro, Runnable alTerminar) {
        HILO_BD.execute(() -> {
            dao.insertar(libro);
            if (alTerminar != null) hiloPrincipal.post(alTerminar);
        });
    }

    public void eliminar(long id, Runnable alTerminar) {
        HILO_BD.execute(() -> {
            dao.eliminarPorId(id);
            if (alTerminar != null) hiloPrincipal.post(alTerminar);
        });
    }

    // si el libro esta en favoritos, se actualiza la copia local con lo que mando el servidor
    public void actualizarSiEsFavorito(Libro libro) {
        HILO_BD.execute(() -> {
            LibroEntity guardado = dao.obtenerPorId(libro.getId());
            if (guardado != null) {
                LibroEntity nuevo = LibroEntity.desde(libro);
                nuevo.fechaGuardado = guardado.fechaGuardado; // se respeta la fecha original
                dao.actualizar(nuevo);
            }
        });
    }

    // al recargar el catalogo se actualizan todos los favoritos que vengan en la lista
    public void actualizarFavoritosDesde(List<Libro> libros) {
        for (Libro libro : libros) {
            actualizarSiEsFavorito(libro);
        }
    }
}
