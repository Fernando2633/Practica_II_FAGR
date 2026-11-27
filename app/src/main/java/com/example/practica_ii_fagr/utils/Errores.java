package com.example.practica_ii_fagr.utils;

import android.content.Context;

import com.example.practica_ii_fagr.R;
import com.example.practica_ii_fagr.models.ErrorResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Convierte los errores de red / HTTP en mensajes amigables para el usuario.
 */
public class Errores {

    // cuando no hubo respuesta (sin internet, servidor caido, tiempo agotado)
    public static String deFallo(Context context, Throwable t) {
        if (t instanceof UnknownHostException || t instanceof ConnectException) {
            return context.getString(R.string.error_sin_internet);
        }
        if (t instanceof SocketTimeoutException) {
            return context.getString(R.string.error_tiempo);
        }
        // cualquier otro problema de red (modo avion, red inalcanzable, conexion cortada)
        if (t instanceof IOException) {
            return context.getString(R.string.error_sin_internet);
        }
        return context.getString(R.string.error_desconocido);
    }

    // cuando el servidor si respondio pero con un codigo de error (404, 500...)
    public static String deRespuesta(Context context, Response<?> respuesta) {
        int codigo = respuesta.code();
        if (codigo == 404) {
            return context.getString(R.string.error_404);
        }
        if (codigo >= 500) {
            return context.getString(R.string.error_servidor, codigo);
        }
        String detalle = leerDetalle(respuesta);
        String mensaje = context.getString(R.string.error_http, codigo);
        return detalle != null ? mensaje + ": " + detalle : mensaje;
    }

    // lee el JSON de error que manda la API {status, error, mensajes}
    private static String leerDetalle(Response<?> respuesta) {
        try (ResponseBody cuerpo = respuesta.errorBody()) {
            if (cuerpo == null) return null;
            ErrorResponse error = new Gson().fromJson(cuerpo.charStream(), ErrorResponse.class);
            return error != null ? error.getDetalle() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
