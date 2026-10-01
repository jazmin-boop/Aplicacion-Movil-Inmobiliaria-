package com.example.inmobiliaria.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.model.Propiedad;

import java.util.List;
import java.util.Locale;

public class PropiedadAdapter extends RecyclerView.Adapter<PropiedadAdapter.PropiedadViewHolder> {

    private List<Propiedad> lista;
    private OnPropiedadActionListener listener;

    public interface OnPropiedadActionListener {
        void onItemClick(Propiedad propiedad);
        void onEditClick(Propiedad propiedad);
        void onDeleteClick(Propiedad propiedad);
    }

    public PropiedadAdapter(List<Propiedad> lista, OnPropiedadActionListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public void updateList(List<Propiedad> newList) {
        this.lista = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PropiedadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_propiedad_card, parent, false);
        return new PropiedadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropiedadViewHolder holder, int position) {
        Propiedad item = lista.get(position);

        holder.tvTitulo.setText(item.getTitulo());
        holder.tvDireccion.setText(item.getDireccion());
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "$ %,.0f", item.getPrecio()));

        holder.tvVistas.setText(String.valueOf(item.getVistas()));
        holder.tvConsultas.setText(String.valueOf(item.getConsultas()));

        String estadoStr = item.getEstado();
        holder.tvEstadoChip.setText(estadoStr);

        if ("Activo".equalsIgnoreCase(estadoStr) || "Disponible".equalsIgnoreCase(estadoStr)) {
            holder.tvEstadoChip.setBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvEstadoChip.setTextColor(Color.parseColor("#2E7D32"));
        } else if ("Pendiente".equalsIgnoreCase(estadoStr) || "Reservado".equalsIgnoreCase(estadoStr) || "En Negociación".equalsIgnoreCase(estadoStr)) {
            holder.tvEstadoChip.setBackgroundColor(Color.parseColor("#FFF3E0"));
            holder.tvEstadoChip.setTextColor(Color.parseColor("#E65100"));
        } else if ("Vendido".equalsIgnoreCase(estadoStr)) {
            holder.tvEstadoChip.setBackgroundColor(Color.parseColor("#FFEBEE"));
            holder.tvEstadoChip.setTextColor(Color.parseColor("#C62828"));
        } else if ("Alquilado".equalsIgnoreCase(estadoStr)) {
            holder.tvEstadoChip.setBackgroundColor(Color.parseColor("#E3F2FD"));
            holder.tvEstadoChip.setTextColor(Color.parseColor("#1565C0"));
        } else {
            holder.tvEstadoChip.setBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.tvEstadoChip.setTextColor(Color.parseColor("#616161"));
        }

        // Load property image URL
        if (item.getImagenUrl() != null && !item.getImagenUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImagenUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.ivPropiedad);
        } else {
            holder.ivPropiedad.setImageResource(R.drawable.ic_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        if (holder.btnView != null) {
            holder.btnView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        }

        if (holder.btnEdit != null) {
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(item);
            });
        }

        if (holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(item);
            });
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class PropiedadViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPropiedad;
        TextView tvTitulo, tvDireccion, tvPrecio, tvEstadoChip, tvVistas, tvConsultas;
        ImageButton btnView, btnEdit, btnDelete;

        public PropiedadViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPropiedad = itemView.findViewById(R.id.ivPropiedad);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvEstadoChip = itemView.findViewById(R.id.tvEstadoChip);
            tvVistas = itemView.findViewById(R.id.tvVistas);
            tvConsultas = itemView.findViewById(R.id.tvConsultas);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
