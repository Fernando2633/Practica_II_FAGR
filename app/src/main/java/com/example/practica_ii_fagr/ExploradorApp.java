package com.example.practica_ii_fagr;

import android.app.Application;

import com.example.practica_ii_fagr.utils.Preferencias;

/**
 * Se ejecuta al abrir la app: lee el tema guardado en SharedPreferences
 * y lo aplica antes de mostrar cualquier pantalla.
 */
public class ExploradorApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Preferencias preferencias = new Preferencias(this);
        Preferencias.aplicarTema(preferencias.getTema());
    }
}
