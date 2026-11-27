package com.example.practica_ii_fagr.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practica_ii_fagr.R;
import com.example.practica_ii_fagr.models.Libro;
import com.example.practica_ii_fagr.utils.Estilos;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Adapter del catalogo (libros que llegan de la API).
 */
public class LibrosAdapter extends RecyclerView.Adapter<LibrosAdapter.ViewHolder> {

    public interface Listener {
        void onLibroClick(Libro libro);

        void onFavoritoClick(Libro libro, boolean eraFavorito);
    }

    private final List<Libro> todos = new ArrayList<>();
    private final List<Libro> visibles = new ArrayList<>();
    private final Set<Long> favoritos = new HashSet<>();
    private final Listener listener;

    private String busqueda = "";
    private boolean soloDisponibles = false;

    public LibrosAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setLibros(List<Libro> libros) {
        todos.clear();
        todos.addAll(libros);
        aplicarFiltros();
    }

    public void setFavoritos(Set<Long> ids) {
        favoritos.clear();
        favoritos.addAll(ids);
        notifyDataSetChanged();
    }

    public void marcarFavorito(long id, boolean esFavorito) {
        if (esFavorito) favoritos.add(id); else favoritos.remove(id);
        notifyDataSetChanged();
    }

    public void setBusqueda(String texto) {
        busqueda = normalizar(texto.trim());
        aplicarFiltros();
    }

    public void setSoloDisponibles(boolean soloDisponibles) {
        this.soloDisponibles = soloDisponibles;
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        visibles.clear();
        for (Libro libro : todos) {
            if (soloDisponibles && !libro.isDisponible()) continue;
            if (!busqueda.isEmpty()
                    && !normalizar(libro.getTitulo()).contains(busqueda)
                    && !normalizar(libro.getAutor()).contains(busqueda)
                    && !normalizar(libro.getEditorial()).contains(busqueda)) {
                continue;
            }
            visibles.add(libro);
        }
        notifyDataSetChanged();
    }

    public int getTotal() { return todos.size(); }
    public int getVisibles() { return visibles.size(); }

    public int getDisponibles() {
        int n = 0;
        for (Libro l : todos) if (l.isDisponible()) n++;
        return n;
    }

    // minusculas y sin acentos para buscar mas facil
    private static String normalizar(String texto) {
        String n = Normalizer.normalize(texto.toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libro, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Libro libro = visibles.get(position);
        boolean esFavorito = favoritos.contains(libro.getId());

        Estilos.pintarPortada(h.portada, libro.getId());
        h.tvInicial.setText(Estilos.inicial(libro.getTitulo()));
        h.tvId.setText(h.itemView.getContext().getString(R.string.id_libro, libro.getId()));
        h.tvTitulo.setText(libro.getTitulo());
        h.tvAutor.setText(libro.getAutor());
        h.tvDetalles.setText(libro.getEditorial() + " · "
                + h.itemView.getContext().getString(R.string.paginas_corto, libro.getPaginas()));
        Estilos.pintarDisponibilidad(h.tvDisponible, libro.isDisponible());

        // corazon lleno si ya esta en Room
        h.btnFavorito.setImageResource(esFavorito ? R.drawable.ic_favorito : R.drawable.ic_favorito_borde);
        h.btnFavorito.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(
                h.itemView.getContext(), esFavorito ? R.color.favorito : R.color.icono)));
        h.btnFavorito.setContentDescription(h.itemView.getContext().getString(
                esFavorito ? R.string.quitar_favorito : R.string.agregar_favorito));

        h.itemView.setOnClickListener(v -> listener.onLibroClick(libro));
        h.btnFavorito.setOnClickListener(v -> listener.onFavoritoClick(libro, esFavorito));
    }

    @Override
    public int getItemCount() {
        return visibles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View portada;
        final TextView tvInicial, tvId, tvTitulo, tvAutor, tvDetalles, tvDisponible;
        final ImageButton btnFavorito;

        ViewHolder(@NonNull View v) {
            super(v);
            portada = v.findViewById(R.id.portada);
            tvInicial = v.findViewById(R.id.tvInicial);
            tvId = v.findViewById(R.id.tvId);
            tvTitulo = v.findViewById(R.id.tvTitulo);
            tvAutor = v.findViewById(R.id.tvAutor);
            tvDetalles = v.findViewById(R.id.tvDetalles);
            tvDisponible = v.findViewById(R.id.tvDisponible);
            btnFavorito = v.findViewById(R.id.btnFavorito);
        }
    }
}
