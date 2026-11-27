package com.example.practica_ii_fagr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practica_ii_fagr.R;
import com.example.practica_ii_fagr.adapters.FavoritosAdapter;
import com.example.practica_ii_fagr.db.FavoritosRepository;
import com.example.practica_ii_fagr.db.LibroEntity;
import com.example.practica_ii_fagr.utils.Pantalla;
import com.example.practica_ii_fagr.utils.Preferencias;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

/**
 * Pantalla 4: Favoritos guardados en Room. No necesita internet.
 */
public class FavoritosActivity extends AppCompatActivity implements FavoritosAdapter.Listener {

    private FavoritosRepository repositorio;
    private Preferencias preferencias;
    private FavoritosAdapter adapter;

    private MaterialToolbar toolbar;
    private RecyclerView rvFavoritos;
    private View estadoVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoritos);
        Pantalla.ajustarBordes(findViewById(R.id.main));

        repositorio = new FavoritosRepository(this);
        preferencias = new Preferencias(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvFavoritos = findViewById(R.id.rvFavoritos);
        estadoVacio = findViewById(R.id.estadoVacio);
        adapter = new FavoritosAdapter(this);
        rvFavoritos.setLayoutManager(new LinearLayoutManager(this));
        rvFavoritos.setAdapter(adapter);

        findViewById(R.id.btnIrCatalogo).setOnClickListener(v ->
                startActivity(new Intent(this, CatalogoActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // se recuerda que la ultima seccion fue favoritos
        preferencias.setUltimaSeccion(Preferencias.SECCION_FAVORITOS);
        cargarFavoritos();
    }

    // consulta Room en un hilo secundario
    private void cargarFavoritos() {
        repositorio.obtenerTodos(lista -> {
            adapter.setFavoritos(lista);
            boolean vacio = lista.isEmpty();
            estadoVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
            rvFavoritos.setVisibility(vacio ? View.GONE : View.VISIBLE);
            toolbar.setSubtitle(vacio ? null : getString(R.string.n_favoritos, lista.size()));
        });
    }

    @Override
    public void onFavoritoClick(LibroEntity libro) {
        startActivity(DetalleActivity.crearIntent(this, libro.aLibro()));
    }

    @Override
    public void onEliminarClick(LibroEntity libro) {
        repositorio.eliminar(libro.id, () -> {
            cargarFavoritos();
            // se puede deshacer: se vuelve a insertar el mismo registro
            Snackbar.make(rvFavoritos, getString(R.string.eliminado_de_favoritos, libro.titulo),
                            Snackbar.LENGTH_LONG)
                    .setAction(R.string.deshacer, v -> repositorio.agregar(libro, this::cargarFavoritos))
                    .show();
        });
    }
}
