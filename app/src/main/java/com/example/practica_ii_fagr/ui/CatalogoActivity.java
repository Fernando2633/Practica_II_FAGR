package com.example.practica_ii_fagr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practica_ii_fagr.R;
import com.example.practica_ii_fagr.adapters.LibrosAdapter;
import com.example.practica_ii_fagr.api.RetrofitClient;
import com.example.practica_ii_fagr.db.FavoritosRepository;
import com.example.practica_ii_fagr.db.LibroEntity;
import com.example.practica_ii_fagr.models.Libro;
import com.example.practica_ii_fagr.models.LibrosResponse;
import com.example.practica_ii_fagr.utils.Errores;
import com.example.practica_ii_fagr.utils.Pantalla;
import com.example.practica_ii_fagr.utils.Preferencias;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla 2: Catalogo de libros obtenido de la API con Retrofit.
 */
public class CatalogoActivity extends AppCompatActivity implements LibrosAdapter.Listener {

    private static final int ESTADO_LISTA = 0;
    private static final int ESTADO_CARGANDO = 1;
    private static final int ESTADO_ERROR = 2;

    private MaterialToolbar toolbar;
    private RecyclerView rvLibros;
    private View estadoCargando, estadoError, barraBusqueda;
    private TextView tvResumen, tvErrorDetalle, tvSinCoincidencias;

    private LibrosAdapter adapter;
    private FavoritosRepository repositorio;
    private Preferencias preferencias;
    private Call<LibrosResponse> llamada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_catalogo);
        Pantalla.ajustarBordes(findViewById(R.id.main));

        repositorio = new FavoritosRepository(this);
        preferencias = new Preferencias(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.inflateMenu(R.menu.menu_catalogo);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.accion_actualizar) {
                cargarCatalogo(true);
            } else if (item.getItemId() == R.id.accion_favoritos) {
                startActivity(new Intent(this, FavoritosActivity.class));
            }
            return true;
        });

        rvLibros = findViewById(R.id.rvLibros);
        estadoCargando = findViewById(R.id.estadoCargando);
        estadoError = findViewById(R.id.estadoError);
        barraBusqueda = findViewById(R.id.barraBusqueda);
        tvResumen = findViewById(R.id.tvResumen);
        tvErrorDetalle = findViewById(R.id.tvErrorDetalle);
        tvSinCoincidencias = findViewById(R.id.tvSinCoincidencias);

        adapter = new LibrosAdapter(this);
        rvLibros.setLayoutManager(new LinearLayoutManager(this));
        rvLibros.setAdapter(adapter);

        // buscar y filtrar
        EditText etBuscar = findViewById(R.id.etBuscar);
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.setBusqueda(s.toString());
                actualizarResumen();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        Chip chipDisponibles = findViewById(R.id.chipDisponibles);
        chipDisponibles.setOnCheckedChangeListener((boton, marcado) -> {
            adapter.setSoloDisponibles(marcado);
            actualizarResumen();
        });

        findViewById(R.id.btnReintentar).setOnClickListener(v -> cargarCatalogo(false));
        findViewById(R.id.btnIrFavoritos).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritosActivity.class)));

        cargarCatalogo(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // se recuerda que la ultima seccion fue el catalogo
        preferencias.setUltimaSeccion(Preferencias.SECCION_CATALOGO);
        // se marcan los corazones segun lo que hay en Room
        repositorio.obtenerIds(ids -> adapter.setFavoritos(ids));
    }

    @Override
    protected void onDestroy() {
        if (llamada != null) llamada.cancel();
        super.onDestroy();
    }

    // GET /api/v1/libros (Retrofit lo hace en otro hilo con enqueue)
    private void cargarCatalogo(boolean esActualizacion) {
        if (adapter.getTotal() == 0) {
            mostrarEstado(ESTADO_CARGANDO);
        } else {
            toolbar.setSubtitle(R.string.actualizando);
        }

        if (llamada != null) llamada.cancel();
        llamada = RetrofitClient.getApi().obtenerLibros();
        llamada.enqueue(new Callback<LibrosResponse>() {
            @Override
            public void onResponse(Call<LibrosResponse> call, Response<LibrosResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                // se valida que la respuesta HTTP sea 2xx y que traiga datos
                if (!response.isSuccessful()) {
                    mostrarError(Errores.deRespuesta(CatalogoActivity.this, response));
                    return;
                }
                LibrosResponse cuerpo = response.body();
                if (cuerpo == null) {
                    mostrarError(getString(R.string.error_respuesta_vacia));
                    return;
                }

                List<Libro> libros = cuerpo.getLibros();
                adapter.setLibros(libros);
                mostrarEstado(ESTADO_LISTA);
                actualizarResumen();

                // los favoritos guardados se actualizan con los datos nuevos del servidor
                repositorio.actualizarFavoritosDesde(libros);

                if (esActualizacion) {
                    Toast.makeText(CatalogoActivity.this,
                            getString(R.string.catalogo_actualizado, libros.size()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LibrosResponse> call, Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                mostrarError(Errores.deFallo(CatalogoActivity.this, t));
            }
        });
    }

    // avisa con un Toast; si no hay nada cargado se muestra la pantalla de error
    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        if (adapter.getTotal() == 0) {
            tvErrorDetalle.setText(mensaje);
            mostrarEstado(ESTADO_ERROR);
        } else {
            actualizarResumen();
        }
    }

    private void mostrarEstado(int estado) {
        rvLibros.setVisibility(estado == ESTADO_LISTA ? View.VISIBLE : View.GONE);
        barraBusqueda.setVisibility(estado == ESTADO_LISTA ? View.VISIBLE : View.GONE);
        estadoCargando.setVisibility(estado == ESTADO_CARGANDO ? View.VISIBLE : View.GONE);
        estadoError.setVisibility(estado == ESTADO_ERROR ? View.VISIBLE : View.GONE);
        if (estado != ESTADO_LISTA) {
            tvSinCoincidencias.setVisibility(View.GONE);
            toolbar.setSubtitle(null);
        }
    }

    private void actualizarResumen() {
        int total = adapter.getTotal();
        tvResumen.setText(getString(R.string.resumen_catalogo,
                adapter.getVisibles(), total, adapter.getDisponibles()));
        toolbar.setSubtitle(total > 0 ? getString(R.string.n_libros, total) : null);
        tvSinCoincidencias.setVisibility(total > 0 && adapter.getVisibles() == 0 ? View.VISIBLE : View.GONE);
    }

    // ---- clicks de la lista ----
    @Override
    public void onLibroClick(Libro libro) {
        startActivity(DetalleActivity.crearIntent(this, libro));
    }

    @Override
    public void onFavoritoClick(Libro libro, boolean eraFavorito) {
        if (eraFavorito) {
            repositorio.eliminar(libro.getId(), () -> {
                adapter.marcarFavorito(libro.getId(), false);
                Snackbar.make(rvLibros, R.string.quitado_de_favoritos, Snackbar.LENGTH_SHORT).show();
            });
        } else {
            repositorio.agregar(LibroEntity.desde(libro), () -> {
                adapter.marcarFavorito(libro.getId(), true);
                Snackbar.make(rvLibros, R.string.guardado_en_favoritos, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.ver, v ->
                                startActivity(new Intent(this, FavoritosActivity.class)))
                        .show();
            });
        }
    }
}
