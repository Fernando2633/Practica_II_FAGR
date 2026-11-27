package com.example.practica_ii_fagr.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Todo lo que se guarda con SharedPreferences (clave - valor).
 * Solo datos basicos de configuracion, nada de contraseñas ni datos sensibles.
 */
public class Preferencias {

    private static final String ARCHIVO = "preferencias_explorador";

    private static final String KEY_NOMBRE = "nombre_usuario";
    private static final String KEY_TEMA = "tema";
    private static final String KEY_ULTIMA_SECCION = "ultima_seccion";
    private static final String KEY_ULTIMA_VISITA = "ultima_visita";

    public static final String TEMA_CLARO = "claro";
    public static final String TEMA_OSCURO = "oscuro";
    public static final String TEMA_SISTEMA = "sistema";

    public static final String SECCION_CATALOGO = "catalogo";
    public static final String SECCION_FAVORITOS = "favoritos";

    private final SharedPreferences prefs;

    public Preferencias(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    // ---- nombre o alias ----
    public String getNombre() {
        return prefs.getString(KEY_NOMBRE, "");
    }

    public void setNombre(String nombre) {
        prefs.edit().putString(KEY_NOMBRE, nombre).apply();
    }

    // ---- tema visual ----
    public String getTema() {
        return prefs.getString(KEY_TEMA, TEMA_SISTEMA);
    }

    public void setTema(String tema) {
        prefs.edit().putString(KEY_TEMA, tema).apply();
    }

    // ---- ultima seccion visitada (catalogo o favoritos) ----
    public String getUltimaSeccion() {
        return prefs.getString(KEY_ULTIMA_SECCION, null);
    }

    public void setUltimaSeccion(String seccion) {
        prefs.edit().putString(KEY_ULTIMA_SECCION, seccion).apply();
    }

    // ---- fecha de la ultima vez que se abrio la app ----
    public long getUltimaVisita() {
        return prefs.getLong(KEY_ULTIMA_VISITA, 0);
    }

    public void registrarVisita() {
        prefs.edit().putLong(KEY_ULTIMA_VISITA, System.currentTimeMillis()).apply();
    }

    // aplica el tema a toda la app (claro, oscuro o el del sistema)
    public static void aplicarTema(String tema) {
        if (TEMA_CLARO.equals(tema)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (TEMA_OSCURO.equals(tema)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
