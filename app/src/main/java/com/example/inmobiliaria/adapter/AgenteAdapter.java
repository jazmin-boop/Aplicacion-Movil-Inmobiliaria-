package com.example.inmobiliaria.adapter;

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
import com.example.inmobiliaria.model.Agente;

import java.util.List;

public class AgenteAdapter extends RecyclerView.Adapter<AgenteAdapter.AgenteViewHolder> {

    private List<Agente> lista;
    private OnAgenteActionListener listener;

    public interface OnAgenteActionListener {
        void onItemClick(Agente agente);
        void onEdit(Agente agente);
        void onDelete(Agente agente);
    }

    public AgenteAdapter(List<Agente> lista, OnAgenteActionListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public void updateList(List<Agente> newList) {
        this.lista = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AgenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agente, parent, false);
        return new AgenteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgenteViewHolder holder, int position) {
        Agente item = lista.get(position);

        if (holder.ivAvatar != null) {
            if (item.getImagenUrl() != null && !item.getImagenUrl().trim().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(item.getImagenUrl())
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(holder.ivAvatar);
            } else {
                holder.ivAvatar.setImageResource(R.drawable.ic_placeholder);
            }
        }

        holder.tvNombre.setText(item.getNombre());
        String contacto = item.getTelefono() + " | " + item.getEmail();
        holder.tvContacto.setText(contacto);
        holder.tvEspecialidad.setText(item.getEspecialidad() != null ? item.getEspecialidad() : "Residencial");

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
                if (listener != null) listener.onEdit(item);
            });
        }

        if (holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(item);
            });
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class AgenteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvNombre, tvContacto, tvEspecialidad;
        ImageButton btnView, btnEdit, btnDelete;

        public AgenteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvContacto = itemView.findViewById(R.id.tvContacto);
            tvEspecialidad = itemView.findViewById(R.id.tvEspecialidad);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
