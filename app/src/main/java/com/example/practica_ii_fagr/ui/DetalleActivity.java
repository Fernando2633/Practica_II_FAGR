package com.example.practica_ii_fagr.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.practica_ii_fagr.R;
import com.example.practica_ii_fagr.api.RetrofitClient;
import com.example.practica_ii_fagr.db.FavoritosRepository;
import com.example.practica_ii_fagr.db.LibroEntity;
import com.example.practica_ii_fagr.models.Libro;
import com.example.practica_ii_fagr.utils.Errores;
import com.example.practica_ii_fagr.utils.Estilos;
import com.example.practica_ii_fagr.utils.Pantalla;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla 3: Detalle de un libro. Lo consulta en la API (GET /libros/{id});
 * si no hay internet usa la copia guardada en Room.
 */
public class DetalleActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_TITULO = "titulo";
    public static final String EXTRA_AUTOR = "autor";
    public static final String EXTRA_EDITORIAL = "editorial";
    public static final String EXTRA_PAGINAS = "paginas";
    public static final String EXTRA_DISPONIBLE = "disponible";

    private static final int ESTADO_CONTENIDO = 0;
    private static final int ESTADO_CARGANDO = 1;
    private static final int ESTADO_ERROR = 2;

    private long idLibro;
    private Libro libroActual;
    private boolean esFavorito;

    private FavoritosRepository repositorio;
    private Call<Libro> llamada;

    private View contenido, estadoCargando, estadoError, bannerFuente, portada;
    private TextView tvPortadaTitulo, tvTitulo, tvAutor, tvDisponible, tvFuente, tvErrorDetalle, tvEstadoFavorito;
    private TextView tvDetId, tvDetTitulo, tvDetAutor, tvDetEditorial, tvDetPaginas, tvDetDisponible;
    private MaterialButton btnFavorito;

    // para abrir esta pantalla con los datos que ya se tienen (se ven al instante)
    public static Intent crearIntent(Context context, Libro libro) {
        Intent intent = new Intent(context, DetalleActivity.class);
        intent.putExtra(EXTRA_ID, libro.getId());
        intent.putExtra(EXTRA_TITULO, libro.getTitulo());
        intent.putExtra(EXTRA_AUTOR, libro.getAutor());
        intent.putExtra(EXTRA_EDITORIAL, libro.getEditorial());
        intent.putExtra(EXTRA_PAGINAS, libro.getPaginas());
        intent.putExtra(EXTRA_DISPONIBLE, libro.isDisponible());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle);
        Pantalla.ajustarBordes(findViewById(R.id.main));

        repositorio = new FavoritosRepository(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        contenido = findViewById(R.id.contenido);
        estadoCargando = findViewById(R.id.estadoCargando);
        estadoError = findViewById(R.id.estadoError);
        bannerFuente = findViewById(R.id.bannerFuente);
        portada = findViewById(R.id.portada);
        tvPortadaTitulo = findViewById(R.id.tvPortadaTitulo);
        tvTitulo = findViewById(R.id.tvTitulo);
        tvAutor = findViewById(R.id.tvAutor);
        tvDisponible = findViewById(R.id.tvDisponible);
        tvFuente = findViewById(R.id.tvFuente);
        tvErrorDetalle = findViewById(R.id.tvErrorDetalle);
        tvEstadoFavorito = findViewById(R.id.tvEstadoFavorito);
        tvDetId = findViewById(R.id.tvDetId);
        tvDetTitulo = findViewById(R.id.tvDetTitulo);
        tvDetAutor = findViewById(R.id.tvDetAutor);
        tvDetEditorial = findViewById(R.id.tvDetEditorial);
        tvDetPaginas = findViewById(R.id.tvDetPaginas);
        tvDetDisponible = findViewById(R.id.tvDetDisponible);
        btnFavorito = findViewById(R.id.btnFavorito);

        idLibro = getIntent().getLongExtra(EXTRA_ID, -1);
        if (idLibro < 0) {
            finish();
            return;
        }

        // si ya traemos los datos del catalogo o de favoritos se muestran mientras llega el detalle
        if (getIntent().hasExtra(EXTRA_TITULO)) {
            libroActual = new Libro(idLibro,
                    getIntent().getStringExtra(EXTRA_TITULO),
                    getIntent().getStringExtra(EXTRA_AUTOR),
                    getIntent().getStringExtra(EXTRA_EDITORIAL),
                    getIntent().getIntExtra(EXTRA_PAGINAS, 0),
                    getIntent().getBooleanExtra(EXTRA_DISPONIBLE, false));
            mostrarLibro(libroActual);
            mostrarEstado(ESTADO_CONTENIDO);
        } else {
            mostrarEstado(ESTADO_CARGANDO);
        }

        repositorio.esFavorito(idLibro, favorito -> {
            esFavorito = favorito;
            pintarBotonFavorito();
        });

        btnFavorito.setOnClickListener(v -> cambiarFavorito());
        findViewById(R.id.btnReintentar).setOnClickListener(v -> {
            mostrarEstado(ESTADO_CARGANDO);
            consultarDetalle();
        });

        consultarDetalle();
    }

    @Override
    protected void onDestroy() {
        if (llamada != null) llamada.cancel();
        super.onDestroy();
    }

    // GET /api/v1/libros/{id}
    private void consultarDetalle() {
        llamada = RetrofitClient.getApi().obtenerLibro(idLibro);
        llamada.enqueue(new Callback<Libro>() {
            @Override
            public void onResponse(Call<Libro> call, Response<Libro> response) {
                if (isFinishing() || isDestroyed()) return;
                if (response.isSuccessful() && response.body() != null) {
                    libroActual = response.body();
                    mostrarLibro(libroActual);
                    bannerFuente.setVisibility(View.GONE);
                    mostrarEstado(ESTADO_CONTENIDO);
                    // si es favorito se actualiza la copia de Room
                    repositorio.actualizarSiEsFavorito(libroActual);
                } else if (response.isSuccessful()) {
                    usarCopiaLocal(getString(R.string.error_respuesta_vacia));
                } else {
                    usarCopiaLocal(Errores.deRespuesta(DetalleActivity.this, response));
                }
            }

            @Override
            public void onFailure(Call<Libro> call, Throwable t) {
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;
                usarCopiaLocal(Errores.deFallo(DetalleActivity.this, t));
            }
        });
    }

    // si falla la API se busca el libro en Room (modo sin conexion)
    private void usarCopiaLocal(String mensajeError) {
        Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show();
        repositorio.obtenerPorId(idLibro, guardado -> {
            if (guardado != null) {
                libroActual = guardado.aLibro();
                mostrarLibro(libroActual);
                mostrarFuente(getString(R.string.fuente_local));
                mostrarEstado(ESTADO_CONTENIDO);
            } else if (libroActual != null) {
                mostrarFuente(getString(R.string.fuente_catalogo));
                mostrarEstado(ESTADO_CONTENIDO);
            } else {
                tvErrorDetalle.setText(mensajeError);
                mostrarEstado(ESTADO_ERROR);
            }
        });
    }

    private void mostrarLibro(Libro libro) {
        Estilos.pintarPortada(portada, libro.getId());
        tvPortadaTitulo.setText(libro.getTitulo());
        tvTitulo.setText(libro.getTitulo());
        tvAutor.setText(libro.getAutor());
        Estilos.pintarDisponibilidad(tvDisponible, libro.isDisponible());

        tvDetId.setText(String.valueOf(libro.getId()));
        tvDetTitulo.setText(libro.getTitulo());
        tvDetAutor.setText(libro.getAutor());
        tvDetEditorial.setText(libro.getEditorial());
        tvDetPaginas.setText(String.valueOf(libro.getPaginas()));
        tvDetDisponible.setText(libro.isDisponible() ? R.string.disponible : R.string.no_disponible);
    }

    private void mostrarFuente(String texto) {
        tvFuente.setText(texto);
        bannerFuente.setVisibility(View.VISIBLE);
    }

    private void mostrarEstado(int estado) {
        contenido.setVisibility(estado == ESTADO_CONTENIDO ? View.VISIBLE : View.GONE);
        estadoCargando.setVisibility(estado == ESTADO_CARGANDO ? View.VISIBLE : View.GONE);
        estadoError.setVisibility(estado == ESTADO_ERROR ? View.VISIBLE : View.GONE);
    }

    // agrega o quita de Room
    private void cambiarFavorito() {
        if (libroActual == null) return;
        if (esFavorito) {
            repositorio.eliminar(idLibro, () -> {
                esFavorito = false;
                pintarBotonFavorito();
                Snackbar.make(btnFavorito, R.string.quitado_de_favoritos, Snackbar.LENGTH_SHORT).show();
            });
        } else {
            repositorio.agregar(LibroEntity.desde(libroActual), () -> {
                esFavorito = true;
                pintarBotonFavorito();
                Snackbar.make(btnFavorito, R.string.guardado_en_favoritos, Snackbar.LENGTH_SHORT).show();
            });
        }
    }

    private void pintarBotonFavorito() {
        int fondo = esFavorito ? R.color.primario_suave : R.color.primario;
        int texto = esFavorito ? R.color.on_primario_suave : R.color.on_primario;
        btnFavorito.setText(esFavorito ? R.string.quitar_favorito : R.string.agregar_favorito);
        btnFavorito.setIconResource(esFavorito ? R.drawable.ic_favorito : R.drawable.ic_favorito_borde);
        btnFavorito.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, fondo)));
        btnFavorito.setTextColor(ContextCompat.getColor(this, texto));
        btnFavorito.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(this,
                esFavorito ? R.color.favorito : R.color.on_primario)));
        tvEstadoFavorito.setText(esFavorito ? R.string.estado_es_favorito : R.string.estado_no_favorito);
    }
}
