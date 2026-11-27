package com.example.practica_ii_fagr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.practica_ii_fagr.R;
import com.example.practica_ii_fagr.db.FavoritosRepository;
import com.example.practica_ii_fagr.utils.Pantalla;
import com.example.practica_ii_fagr.utils.Preferencias;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Pantalla 1: Bienvenida y configuracion.
 * Lee y guarda el nombre, el tema y la ultima seccion con SharedPreferences.
 */
public class BienvenidaActivity extends AppCompatActivity {

    private static final String KEY_VISITA_ANTERIOR = "visita_anterior";

    private Preferencias preferencias;
    private FavoritosRepository repositorio;

    private TextView tvSaludo, tvPrefNombre, tvPrefTema, tvPrefSeccion, tvPrefVisita, tvPrefFavoritos;
    private TextInputLayout tilNombre;
    private TextInputEditText etNombre;
    private Button btnFavoritos, btnContinuar;

    private long visitaAnterior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bienvenida);
        Pantalla.ajustarBordes(findViewById(R.id.main));

        preferencias = new Preferencias(this);
        repositorio = new FavoritosRepository(this);

        // la visita anterior se lee antes de guardar la de hoy
        // (si la pantalla se recrea por cambiar el tema se conserva la que ya se leyo)
        if (savedInstanceState == null) {
            visitaAnterior = preferencias.getUltimaVisita();
            preferencias.registrarVisita();
        } else {
            visitaAnterior = savedInstanceState.getLong(KEY_VISITA_ANTERIOR, 0);
        }

        tvSaludo = findViewById(R.id.tvSaludo);
        tvPrefNombre = findViewById(R.id.tvPrefNombre);
        tvPrefTema = findViewById(R.id.tvPrefTema);
        tvPrefSeccion = findViewById(R.id.tvPrefSeccion);
        tvPrefVisita = findViewById(R.id.tvPrefVisita);
        tvPrefFavoritos = findViewById(R.id.tvPrefFavoritos);
        tilNombre = findViewById(R.id.tilNombre);
        etNombre = findViewById(R.id.etNombre);
        btnFavoritos = findViewById(R.id.btnFavoritos);
        btnContinuar = findViewById(R.id.btnContinuar);

        // ---- nombre ----
        etNombre.setText(preferencias.getNombre());
        findViewById(R.id.btnGuardarNombre).setOnClickListener(v -> guardarNombre());
        etNombre.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                guardarNombre();
                return true;
            }
            return false;
        });

        // ---- tema: primero se marca el guardado y luego se escucha el cambio ----
        MaterialButtonToggleGroup grupoTema = findViewById(R.id.grupoTema);
        grupoTema.check(idBotonTema(preferencias.getTema()));
        grupoTema.addOnButtonCheckedListener((grupo, idBoton, marcado) -> {
            if (!marcado) return;
            String tema = temaDeBoton(idBoton);
            if (tema.equals(preferencias.getTema())) return;
            preferencias.setTema(tema);
            Preferencias.aplicarTema(tema); // la pantalla se recrea con el nuevo tema
        });

        // ---- navegacion ----
        findViewById(R.id.btnCatalogo).setOnClickListener(v -> abrir(CatalogoActivity.class));
        btnFavoritos.setOnClickListener(v -> abrir(FavoritosActivity.class));
        btnContinuar.setOnClickListener(v -> {
            if (Preferencias.SECCION_FAVORITOS.equals(preferencias.getUltimaSeccion())) {
                abrir(FavoritosActivity.class);
            } else {
                abrir(CatalogoActivity.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarPreferencias();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_VISITA_ANTERIOR, visitaAnterior);
    }

    private void guardarNombre() {
        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        if (nombre.isEmpty()) {
            tilNombre.setError(getString(R.string.error_nombre_vacio));
            return;
        }
        tilNombre.setError(null);
        preferencias.setNombre(nombre);

        // se esconde el teclado
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etNombre.getWindowToken(), 0);
        etNombre.clearFocus();

        Toast.makeText(this, getString(R.string.nombre_guardado, nombre), Toast.LENGTH_SHORT).show();
        mostrarPreferencias();
    }

    // muestra lo que hay guardado en SharedPreferences (y cuantos favoritos hay en Room)
    private void mostrarPreferencias() {
        String nombre = preferencias.getNombre();
        tvSaludo.setText(nombre.isEmpty() ? getString(R.string.saludo_sin_nombre)
                : getString(R.string.saludo_nombre, nombre));
        tvPrefNombre.setText(nombre.isEmpty() ? getString(R.string.sin_registrar) : nombre);
        tvPrefTema.setText(nombreTema(preferencias.getTema()));

        String seccion = preferencias.getUltimaSeccion();
        if (seccion == null) {
            tvPrefSeccion.setText(R.string.ninguna_todavia);
            btnContinuar.setVisibility(android.view.View.GONE);
        } else {
            String nombreSeccion = getString(Preferencias.SECCION_FAVORITOS.equals(seccion)
                    ? R.string.seccion_favoritos : R.string.seccion_catalogo);
            tvPrefSeccion.setText(nombreSeccion);
            btnContinuar.setText(getString(R.string.continuar_en, nombreSeccion));
            btnContinuar.setVisibility(android.view.View.VISIBLE);
        }

        if (visitaAnterior == 0) {
            tvPrefVisita.setText(R.string.primera_visita);
        } else {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
            tvPrefVisita.setText(formato.format(new Date(visitaAnterior)));
        }

        repositorio.contar(total -> {
            tvPrefFavoritos.setText(String.valueOf(total));
            btnFavoritos.setText(total > 0 ? getString(R.string.mis_favoritos_n, total)
                    : getString(R.string.mis_favoritos));
        });
    }

    private void abrir(Class<?> pantalla) {
        startActivity(new Intent(this, pantalla));
    }

    private int idBotonTema(String tema) {
        if (Preferencias.TEMA_CLARO.equals(tema)) return R.id.btnTemaClaro;
        if (Preferencias.TEMA_OSCURO.equals(tema)) return R.id.btnTemaOscuro;
        return R.id.btnTemaSistema;
    }

    private String temaDeBoton(int idBoton) {
        if (idBoton == R.id.btnTemaClaro) return Preferencias.TEMA_CLARO;
        if (idBoton == R.id.btnTemaOscuro) return Preferencias.TEMA_OSCURO;
        return Preferencias.TEMA_SISTEMA;
    }

    private String nombreTema(String tema) {
        if (Preferencias.TEMA_CLARO.equals(tema)) return getString(R.string.tema_claro);
        if (Preferencias.TEMA_OSCURO.equals(tema)) return getString(R.string.tema_oscuro);
        return getString(R.string.tema_sistema);
    }
}
