package com.example.inmobiliaria.adapter;

import android.graphics.Color;
import android.text.TextUtils;
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
import com.example.inmobiliaria.model.Visita;

import java.util.List;

public class VisitaAdapter extends RecyclerView.Adapter<VisitaAdapter.VisitaViewHolder> {

    private List<Visita> lista;
    private OnVisitaActionListener listener;

    public interface OnVisitaActionListener {
        void onItemClick(Visita visita);
        void onHistory(Visita visita);
        void onEdit(Visita visita);
        void onDelete(Visita visita);
    }

    public VisitaAdapter(List<Visita> lista, OnVisitaActionListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public void updateList(List<Visita> newList) {
        this.lista = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VisitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visita, parent, false);
        return new VisitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitaViewHolder holder, int position) {
        Visita item = lista.get(position);

        // Load property image if available
        if (item.getImagenUrlPropiedad() != null && !item.getImagenUrlPropiedad().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImagenUrlPropiedad())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.ivPropiedad);
        } else {
            holder.ivPropiedad.setImageResource(R.drawable.ic_placeholder);
        }

        holder.tvPropiedad.setText(item.getTituloPropiedad() != null ? item.getTituloPropiedad() : "Propiedad ID: " + item.getIdPropiedad());

        String dir = item.getDireccionPropiedad() != null && !item.getDireccionPropiedad().isEmpty() ? item.getDireccionPropiedad() : "Ubicación no especificada";
        holder.tvDireccion.setText(dir);

        String clienteAgente = "Cliente: " + (item.getNombreCliente() != null ? item.getNombreCliente() : "-") +
                " | Agente: " + (item.getNombreAgente() != null ? item.getNombreAgente() : "-");
        holder.tvClienteAgente.setText(clienteAgente);

        holder.tvFechaHora.setText(item.getFechaHora() != null ? item.getFechaHora() : "Por definir");

        String comments = !TextUtils.isEmpty(item.getComentarios()) ? item.getComentarios() : "Sin observaciones adicionales";
        holder.tvComentarios.setText(comments);

        String estado = item.getEstado() != null ? item.getEstado() : "Programada";
        holder.tvEstado.setText(estado);

        if ("Realizada".equalsIgnoreCase(estado)) {
            holder.tvEstado.setBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvEstado.setTextColor(Color.parseColor("#2E7D32"));
        } else if ("Cancelada".equalsIgnoreCase(estado)) {
            holder.tvEstado.setBackgroundColor(Color.parseColor("#FFEBEE"));
            holder.tvEstado.setTextColor(Color.parseColor("#C62828"));
        } else {
            holder.tvEstado.setBackgroundColor(Color.parseColor("#E8EAF6"));
            holder.tvEstado.setTextColor(Color.parseColor("#283593"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        if (holder.btnHistory != null) {
            holder.btnHistory.setOnClickListener(v -> {
                if (listener != null) listener.onHistory(item);
            });
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class VisitaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPropiedad;
        TextView tvPropiedad, tvDireccion, tvClienteAgente, tvFechaHora, tvComentarios, tvEstado;
        ImageButton btnHistory, btnEdit, btnDelete;

        public VisitaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPropiedad = itemView.findViewById(R.id.ivPropiedad);
            tvPropiedad = itemView.findViewById(R.id.tvPropiedad);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvClienteAgente = itemView.findViewById(R.id.tvClienteAgente);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvComentarios = itemView.findViewById(R.id.tvComentarios);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnHistory = itemView.findViewById(R.id.btnHistory);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
