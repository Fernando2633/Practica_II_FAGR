package com.example.practica_ii_fagr.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Cuerpo que manda la API cuando hay un error (404, 400, 500...).
 */
public class ErrorResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("error")
    private String error;

    @SerializedName("mensajes")
    private Map<String, String> mensajes;

    public int getStatus() { return status; }
    public String getError() { return error; }

    // el primer mensaje que venga, o el texto de "error"
    public String getDetalle() {
        if (mensajes != null && !mensajes.isEmpty()) {
            return mensajes.values().iterator().next();
        }
        return error;
    }
}
