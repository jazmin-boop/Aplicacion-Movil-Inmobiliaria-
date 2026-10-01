package com.example.inmobiliaria.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmobiliaria.R;
import com.example.inmobiliaria.model.Venta;

import java.util.List;
import java.util.Locale;

public class VentaAdapter extends RecyclerView.Adapter<VentaAdapter.VentaViewHolder> {

    private List<Venta> lista;
    private OnVentaActionListener listener;

    public interface OnVentaActionListener {
        void onItemClick(Venta venta);
        void onEdit(Venta venta);
        void onDelete(Venta venta);
    }

    public VentaAdapter(List<Venta> lista, OnVentaActionListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public void updateList(List<Venta> newList) {
        this.lista = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venta, parent, false);
        return new VentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VentaViewHolder holder, int position) {
        Venta item = lista.get(position);
        holder.tvPropiedad.setText(item.getTituloPropiedad() != null ? item.getTituloPropiedad() : "Propiedad ID: " + item.getIdPropiedad());

        String clienteAgente = (item.getNombreCliente() != null ? item.getNombreCliente() : "-") +
                " | Agente: " + (item.getNombreAgente() != null ? item.getNombreAgente() : "-");
        holder.tvClienteAgente.setText(clienteAgente);

        String metodo = item.getMetodoPago() != null ? item.getMetodoPago() : "Transferencia";
        holder.tvFecha.setText(item.getFechaVenta() + " (" + metodo + ")");

        holder.tvMonto.setText(String.format(Locale.getDefault(), "Monto: $ %,.0f", item.getMontoFinal()));
        holder.tvComision.setText(String.format(Locale.getDefault(), "Comisión: $ %,.0f", item.getComision()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        if (holder.btnView != null) {
            holder.btnView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
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

    static class VentaViewHolder extends RecyclerView.ViewHolder {
        TextView tvPropiedad, tvClienteAgente, tvFecha, tvMonto, tvComision;
        ImageButton btnView, btnEdit, btnDelete;

        public VentaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPropiedad = itemView.findViewById(R.id.tvPropiedad);
            tvClienteAgente = itemView.findViewById(R.id.tvClienteAgente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvMonto = itemView.findViewById(R.id.tvMonto);
            tvComision = itemView.findViewById(R.id.tvComision);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
