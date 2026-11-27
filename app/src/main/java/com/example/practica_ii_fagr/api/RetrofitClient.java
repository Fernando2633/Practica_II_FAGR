package com.example.practica_ii_fagr.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Crea una sola instancia de Retrofit para toda la app.
 */
public class RetrofitClient {

    public static final String BASE_URL = "https://ejercicio-08-documentacion-y-endpoints.onrender.com/";

    private static LibrosApi api;

    public static synchronized LibrosApi getApi() {
        if (api == null) {
            // el servidor (Render) se duerme y la primera peticion puede tardar,
            // por eso los tiempos de espera son mas largos de lo normal
            OkHttpClient cliente = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(LibrosApi.class);
        }
        return api;
    }
}
