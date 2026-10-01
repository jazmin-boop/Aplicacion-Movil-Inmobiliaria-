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
import com.example.inmobiliaria.model.Cliente;

import java.util.List;
import java.util.Locale;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> lista;
    private OnClienteActionListener listener;

    public interface OnClienteActionListener {
        void onItemClick(Cliente cliente);
        void onEdit(Cliente cliente);
        void onDelete(Cliente cliente);
    }

    public ClienteAdapter(List<Cliente> lista, OnClienteActionListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public void updateList(List<Cliente> newList) {
        this.lista = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente item = lista.get(position);

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
        holder.tvInteres.setText(item.getInteres() != null ? item.getInteres() : "Comprar");
        holder.tvPresupuesto.setText(String.format(Locale.getDefault(), "Presupuesto: $ %,.0f", item.getPresupuesto()));

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

    static class ClienteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvNombre, tvContacto, tvInteres, tvPresupuesto;
        ImageButton btnView, btnEdit, btnDelete;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvContacto = itemView.findViewById(R.id.tvContacto);
            tvInteres = itemView.findViewById(R.id.tvInteres);
            tvPresupuesto = itemView.findViewById(R.id.tvPresupuesto);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
