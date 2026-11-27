package com.example.practica_ii_fagr.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practica_ii_fagr.R;
import com.example.practica_ii_fagr.db.LibroEntity;
import com.example.practica_ii_fagr.utils.Estilos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter de favoritos (los datos vienen de Room, no de internet).
 */
public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {

    public interface Listener {
        void onFavoritoClick(LibroEntity libro);

        void onEliminarClick(LibroEntity libro);
    }

    private final List<LibroEntity> favoritos = new ArrayList<>();
    private final Listener listener;
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());

    public FavoritosAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setFavoritos(List<LibroEntity> lista) {
        favoritos.clear();
        favoritos.addAll(lista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorito, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        LibroEntity libro = favoritos.get(position);

        Estilos.pintarPortada(h.portada, libro.id);
        h.tvInicial.setText(Estilos.inicial(libro.titulo));
        h.tvId.setText(h.itemView.getContext().getString(R.string.id_libro, libro.id));
        h.tvTitulo.setText(libro.titulo);
        h.tvAutor.setText(libro.autor);
        h.tvDetalles.setText(libro.editorial + " · "
                + h.itemView.getContext().getString(R.string.paginas_corto, libro.paginas));
        Estilos.pintarDisponibilidad(h.tvDisponible, libro.disponible);
        h.tvGuardado.setText(h.itemView.getContext().getString(R.string.guardado_el,
                formatoFecha.format(new Date(libro.fechaGuardado))));

        h.itemView.setOnClickListener(v -> listener.onFavoritoClick(libro));
        h.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(libro));
    }

    @Override
    public int getItemCount() {
        return favoritos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View portada;
        final TextView tvInicial, tvId, tvTitulo, tvAutor, tvDetalles, tvDisponible, tvGuardado;
        final ImageButton btnEliminar;

        ViewHolder(@NonNull View v) {
            super(v);
            portada = v.findViewById(R.id.portada);
            tvInicial = v.findViewById(R.id.tvInicial);
            tvId = v.findViewById(R.id.tvId);
            tvTitulo = v.findViewById(R.id.tvTitulo);
            tvAutor = v.findViewById(R.id.tvAutor);
            tvDetalles = v.findViewById(R.id.tvDetalles);
            tvDisponible = v.findViewById(R.id.tvDisponible);
            tvGuardado = v.findViewById(R.id.tvGuardado);
            btnEliminar = v.findViewById(R.id.btnEliminar);
        }
    }
}
