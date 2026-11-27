package com.example.practica_ii_fagr.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.practica_ii_fagr.R;

/**
 * Detalles visuales que se repiten: portada de color y etiqueta de disponibilidad.
 */
public class Estilos {

    // pares de colores para las portadas (se escoge segun el ID del libro)
    private static final int[][] PORTADAS = {
            {0xFF6366F1, 0xFF4338CA}, // indigo
            {0xFF0EA5E9, 0xFF0369A1}, // azul
            {0xFF14B8A6, 0xFF0F766E}, // verde azulado
            {0xFFF59E0B, 0xFFB45309}, // ambar
            {0xFFF43F5E, 0xFFBE123C}, // rosa
            {0xFF8B5CF6, 0xFF6D28D9}, // morado
            {0xFF22C55E, 0xFF15803D}, // verde
            {0xFFEF4444, 0xFF991B1B}  // rojo
    };

    public static void pintarPortada(View portada, long id) {
        int[] colores = PORTADAS[(int) (Math.abs(id) % PORTADAS.length)];
        GradientDrawable fondo = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colores);
        fondo.setCornerRadius(8 * portada.getResources().getDisplayMetrics().density);
        portada.setBackground(fondo);
    }

    // primera letra del titulo para la portada chiquita
    public static String inicial(String titulo) {
        String t = titulo.trim();
        return t.isEmpty() ? "?" : t.substring(0, 1).toUpperCase();
    }

    public static void pintarDisponibilidad(TextView etiqueta, boolean disponible) {
        etiqueta.setText(disponible ? R.string.disponible : R.string.no_disponible);
        int texto = disponible ? R.color.disponible_texto : R.color.no_disponible_texto;
        int fondo = disponible ? R.color.disponible_fondo : R.color.no_disponible_fondo;
        etiqueta.setTextColor(ContextCompat.getColor(etiqueta.getContext(), texto));
        etiqueta.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(etiqueta.getContext(), fondo)));
    }
}
