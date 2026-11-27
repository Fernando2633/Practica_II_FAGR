package com.example.practica_ii_fagr.utils;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Deja espacio para la barra de estado, la de navegacion y el teclado
 * (la app se dibuja de orilla a orilla).
 */
public class Pantalla {

    public static void ajustarBordes(View raiz) {
        int izq = raiz.getPaddingLeft();
        int arriba = raiz.getPaddingTop();
        int der = raiz.getPaddingRight();
        int abajo = raiz.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(izq + barras.left, arriba + barras.top, der + barras.right,
                    abajo + Math.max(barras.bottom, teclado.bottom));
            return insets;
        });
    }
}
