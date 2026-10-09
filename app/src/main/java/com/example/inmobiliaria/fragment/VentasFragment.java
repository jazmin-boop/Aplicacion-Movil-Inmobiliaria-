package com.example.inmobiliaria.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmobiliaria.MainActivity;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.adapter.VentaAdapter;
import com.example.inmobiliaria.database.DatabaseHelper;
import com.example.inmobiliaria.model.Agente;
import com.example.inmobiliaria.model.Cliente;
import com.example.inmobiliaria.model.Propiedad;
import com.example.inmobiliaria.model.Venta;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VentasFragment extends Fragment implements VentaAdapter.OnVentaActionListener {

    private DatabaseHelper dbHelper;
    private VentaAdapter adapter;
    private List<Venta> listaVentas = new ArrayList<>();

    private EditText etSearchVenta;
    private ChipGroup chipGroupFilter;
    private Chip chipTodos, chipAnioActual;

    private TextView tvTotalVentas, tvMontoTotal, tvTotalComisiones, tvTicketPromedio;

    private final String[] metodosPago = {"Transferencia", "Efectivo", "Tarjeta", "Crédito Hipotecario"};
    private String selectedFilter = "ALL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ventas, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showMainPanel();
                }
            });
        }

        MaterialButton btnBannerAdd = view.findViewById(R.id.btnBannerAdd);
        if (btnBannerAdd != null) {
            btnBannerAdd.setOnClickListener(v -> showVentaDialog(null));
        }

        tvTotalVentas = view.findViewById(R.id.tvTotalVentas);
        tvMontoTotal = view.findViewById(R.id.tvMontoTotal);
        tvTotalComisiones = view.findViewById(R.id.tvTotalComisiones);
        tvTicketPromedio = view.findViewById(R.id.tvTicketPromedio);

        etSearchVenta = view.findViewById(R.id.etSearchVenta);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        chipTodos = view.findViewById(R.id.chipTodos);
        chipAnioActual = view.findViewById(R.id.chipAnioActual);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new VentaAdapter(new ArrayList<>(), this);
            recyclerView.setAdapter(adapter);
        }

        setupSearchAndFilters();
        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        listaVentas = dbHelper.getAllVentas();

        int total = listaVentas.size();
        double sumMonto = 0.0;
        double sumComision = 0.0;

        for (Venta v : listaVentas) {
            sumMonto += v.getMontoFinal();
            sumComision += v.getComision();
        }

        double ticketProm = total > 0 ? (sumMonto / total) : 0.0;

        if (tvTotalVentas != null) tvTotalVentas.setText(String.valueOf(total));
        if (tvMontoTotal != null) tvMontoTotal.setText(String.format(Locale.getDefault(), "$ %,.0f", sumMonto));
        if (tvTotalComisiones != null) tvTotalComisiones.setText(String.format(Locale.getDefault(), "$ %,.0f", sumComision));
        if (tvTicketPromedio != null) tvTicketPromedio.setText(String.format(Locale.getDefault(), "$ %,.0f", ticketProm));

        if (chipTodos != null) chipTodos.setText(String.format(Locale.getDefault(), "Todas (%d)", total));
        if (chipAnioActual != null) chipAnioActual.setText(String.format(Locale.getDefault(), "Año Actual (%d)", total));

        applyFilters();
    }

    private void setupSearchAndFilters() {
        if (etSearchVenta != null) {
            etSearchVenta.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyFilters();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (chipGroupFilter != null) {
            chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.contains(R.id.chipAnioActual)) {
                    selectedFilter = "ANIO_ACTUAL";
                } else {
                    selectedFilter = "ALL";
                }
                applyFilters();
            });
        }
    }

    private void applyFilters() {
        String query = (etSearchVenta != null && etSearchVenta.getText() != null)
                ? etSearchVenta.getText().toString().trim().toLowerCase() : "";
        List<Venta> filteredList = new ArrayList<>();

        for (Venta v : listaVentas) {
            boolean matchesSearch = TextUtils.isEmpty(query) ||
                    (v.getTituloPropiedad() != null && v.getTituloPropiedad().toLowerCase().contains(query)) ||
                    (v.getNombreCliente() != null && v.getNombreCliente().toLowerCase().contains(query)) ||
                    (v.getNombreAgente() != null && v.getNombreAgente().toLowerCase().contains(query)) ||
                    (v.getFechaVenta() != null && v.getFechaVenta().toLowerCase().contains(query)) ||
                    (v.getMetodoPago() != null && v.getMetodoPago().toLowerCase().contains(query));

            boolean matchesFilter = true;
            if ("ANIO_ACTUAL".equalsIgnoreCase(selectedFilter)) {
                String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
                matchesFilter = v.getFechaVenta() != null && v.getFechaVenta().startsWith(currentYear);
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(v);
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }

    @Override
    public void onItemClick(Venta venta) {
        showVentaOverviewModal(venta);
    }

    private void showVentaOverviewModal(Venta v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_venta_overview, null);

        builder.setView(view);
        AlertDialog modalDialog = builder.create();

        ImageView ivDetailProperty = view.findViewById(R.id.ivDetailProperty);
        TextView tvDetailStatusBadge = view.findViewById(R.id.tvDetailStatusBadge);
        TextView tvDetailIdBadge = view.findViewById(R.id.tvDetailIdBadge);
        TextView tvDetailProperty = view.findViewById(R.id.tvDetailProperty);
        TextView tvDetailLocation = view.findViewById(R.id.tvDetailLocation);

        TextView tvDetailPrice = view.findViewById(R.id.tvDetailPrice);
        TextView tvDetailCommission = view.findViewById(R.id.tvDetailCommission);

        ImageView ivPaymentMethodIcon = view.findViewById(R.id.ivPaymentMethodIcon);
        TextView tvDetailPaymentMethod = view.findViewById(R.id.tvDetailPaymentMethod);

        TextView tvDetailDate = view.findViewById(R.id.tvDetailDate);
        TextView tvDetailBuyer = view.findViewById(R.id.tvDetailBuyer);
        TextView tvDetailAgent = view.findViewById(R.id.tvDetailAgent);

        View btnCloseModal = view.findViewById(R.id.btnCloseModal);
        if (btnCloseModal != null) {
            btnCloseModal.setOnClickListener(v1 -> modalDialog.dismiss());
        }

        View btnCloseOverviewBottom = view.findViewById(R.id.btnCloseOverviewBottom);
        if (btnCloseOverviewBottom != null) {
            btnCloseOverviewBottom.setOnClickListener(v1 -> modalDialog.dismiss());
        }

        if (ivDetailProperty != null) {
            if (v.getImagenUrlPropiedad() != null && !v.getImagenUrlPropiedad().isEmpty()) {
                Glide.with(requireContext())
                        .load(v.getImagenUrlPropiedad())
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(ivDetailProperty);
            } else {
                ivDetailProperty.setImageResource(R.drawable.ic_placeholder);
            }
        }

        if (tvDetailStatusBadge != null) tvDetailStatusBadge.setText("Vendido");
        if (tvDetailIdBadge != null) tvDetailIdBadge.setText(String.format(Locale.getDefault(), "ID: VEN-%05d", v.getId()));
        if (tvDetailProperty != null) tvDetailProperty.setText(v.getTituloPropiedad() != null ? v.getTituloPropiedad() : "Propiedad #" + v.getIdPropiedad());
        if (tvDetailLocation != null) tvDetailLocation.setText(v.getDireccionPropiedad() != null ? v.getDireccionPropiedad() : "Ubicación registrada");
        if (tvDetailPrice != null) tvDetailPrice.setText(String.format(Locale.getDefault(), "$ %,.0f", v.getMontoFinal()));
        if (tvDetailCommission != null) tvDetailCommission.setText(String.format(Locale.getDefault(), "$ %,.0f", v.getComision()));

        String metodo = v.getMetodoPago() != null ? v.getMetodoPago() : "Transferencia";
        if (tvDetailPaymentMethod != null) tvDetailPaymentMethod.setText(metodo);

        if (ivPaymentMethodIcon != null) {
            if ("Efectivo".equalsIgnoreCase(metodo)) {
                ivPaymentMethodIcon.setImageResource(R.drawable.ic_sale);
                ivPaymentMethodIcon.setBackgroundColor(Color.parseColor("#E8F5E9"));
                ivPaymentMethodIcon.setColorFilter(Color.parseColor("#2E7D32"));
            } else if ("Transferencia".equalsIgnoreCase(metodo) || "Transferencia Bancaria".equalsIgnoreCase(metodo)) {
                ivPaymentMethodIcon.setImageResource(R.drawable.ic_copy);
                ivPaymentMethodIcon.setBackgroundColor(Color.parseColor("#E3F2FD"));
                ivPaymentMethodIcon.setColorFilter(Color.parseColor("#1565C0"));
            } else if ("Tarjeta".equalsIgnoreCase(metodo)) {
                ivPaymentMethodIcon.setImageResource(R.drawable.ic_sale);
                ivPaymentMethodIcon.setBackgroundColor(Color.parseColor("#F3E5F5"));
                ivPaymentMethodIcon.setColorFilter(Color.parseColor("#7B1FA2"));
            } else {
                ivPaymentMethodIcon.setImageResource(R.drawable.ic_property);
                ivPaymentMethodIcon.setBackgroundColor(Color.parseColor("#FFF3E0"));
                ivPaymentMethodIcon.setColorFilter(Color.parseColor("#E65100"));
            }
        }

        if (tvDetailDate != null) tvDetailDate.setText(v.getFechaVenta() != null ? v.getFechaVenta() : "-");
        if (tvDetailBuyer != null) tvDetailBuyer.setText(v.getNombreCliente() != null ? v.getNombreCliente() : "-");
        if (tvDetailAgent != null) tvDetailAgent.setText(v.getNombreAgente() != null ? v.getNombreAgente() : "-");

        View btnShareSale = view.findViewById(R.id.btnShareSale);
        if (btnShareSale != null) {
            btnShareSale.setOnClickListener(v1 -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Comprobante de Venta Inmobiliaria:\n" +
                        "Propiedad: " + v.getTituloPropiedad() + "\n" +
                        "Comprador: " + v.getNombreCliente() + "\n" +
                        "Agente: " + v.getNombreAgente() + "\n" +
                        "Monto Final: " + String.format(Locale.getDefault(), "$ %,.0f", v.getMontoFinal()) + "\n" +
                        "Método de Pago: " + metodo + "\n" +
                        "Fecha de Cierre: " + v.getFechaVenta());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, "Compartir Comprobante de Venta");
                startActivity(shareIntent);
            });
        }

        modalDialog.show();
    }

    private void showVentaDialog(@Nullable Venta ventaToEdit) {
        List<Propiedad> propiedades = dbHelper.getAllPropiedades();
        List<Cliente> clientes = dbHelper.getAllClientes();
        List<Agente> agentes = dbHelper.getAllAgentes();

        if (propiedades.isEmpty() || clientes.isEmpty() || agentes.isEmpty()) {
            Toast.makeText(getContext(), "Debe registrar al menos una propiedad, cliente y agente antes de registrar ventas.", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_venta, null);

        TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        Spinner spPropiedad = view.findViewById(R.id.spPropiedad);
        Spinner spCliente = view.findViewById(R.id.spCliente);
        Spinner spAgente = view.findViewById(R.id.spAgente);
        EditText etFechaVenta = view.findViewById(R.id.etFechaVenta);
        EditText etMontoFinal = view.findViewById(R.id.etMontoFinal);
        EditText etComision = view.findViewById(R.id.etComision);
        Spinner spMetodoPago = view.findViewById(R.id.spMetodoPago);

        ArrayAdapter<Propiedad> propAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, propiedades);
        spPropiedad.setAdapter(propAdapter);

        ArrayAdapter<Cliente> cliAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, clientes);
        spCliente.setAdapter(cliAdapter);

        ArrayAdapter<Agente> ageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, agentes);
        spAgente.setAdapter(ageAdapter);

        ArrayAdapter<String> metodoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, metodosPago);
        if (spMetodoPago != null) {
            spMetodoPago.setAdapter(metodoAdapter);
        }

        // Auto prefill price and commission when selecting property if creating new sale
        spPropiedad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                if (ventaToEdit == null) {
                    Propiedad selected = propiedades.get(position);
                    etMontoFinal.setText(String.valueOf(selected.getPrecio()));
                    double estimatedCommission = selected.getPrecio() * 0.03;
                    etComision.setText(String.format(Locale.US, "%.2f", estimatedCommission));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (ventaToEdit != null) {
            if (tvDialogTitle != null) tvDialogTitle.setText("Editar Venta");
            etFechaVenta.setText(ventaToEdit.getFechaVenta());
            etMontoFinal.setText(String.valueOf(ventaToEdit.getMontoFinal()));
            etComision.setText(String.valueOf(ventaToEdit.getComision()));

            for (int i = 0; i < propiedades.size(); i++) {
                if (propiedades.get(i).getId() == ventaToEdit.getIdPropiedad()) {
                    spPropiedad.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < clientes.size(); i++) {
                if (clientes.get(i).getId() == ventaToEdit.getIdCliente()) {
                    spCliente.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < agentes.size(); i++) {
                if (agentes.get(i).getId() == ventaToEdit.getIdAgente()) {
                    spAgente.setSelection(i);
                    break;
                }
            }
            if (spMetodoPago != null) {
                for (int i = 0; i < metodosPago.length; i++) {
                    if (metodosPago[i].equalsIgnoreCase(ventaToEdit.getMetodoPago())) {
                        spMetodoPago.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            if (tvDialogTitle != null) tvDialogTitle.setText("Nueva Venta");
            String defaultDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            etFechaVenta.setText(defaultDate);
        }

        builder.setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String fechaVenta = etFechaVenta.getText() != null ? etFechaVenta.getText().toString().trim() : "";
            String montoStr = etMontoFinal.getText() != null ? etMontoFinal.getText().toString().trim() : "";
            String comisionStr = etComision.getText() != null ? etComision.getText().toString().trim() : "";

            if (TextUtils.isEmpty(fechaVenta) || TextUtils.isEmpty(montoStr)) {
                Toast.makeText(getContext(), "Fecha y Monto Final son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            double monto;
            double comision = 0.0;
            try {
                monto = Double.parseDouble(montoStr);
                if (!TextUtils.isEmpty(comisionStr)) {
                    comision = Double.parseDouble(comisionStr);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Ingrese un monto y comisión válidos", Toast.LENGTH_SHORT).show();
                return;
            }

            Propiedad selProp = (Propiedad) spPropiedad.getSelectedItem();
            Cliente selCli = (Cliente) spCliente.getSelectedItem();
            Agente selAge = (Agente) spAgente.getSelectedItem();
            String metodoSel = (spMetodoPago != null && spMetodoPago.getSelectedItem() != null)
                    ? spMetodoPago.getSelectedItem().toString() : metodosPago[0];

            if (ventaToEdit == null) {
                Venta nueva = new Venta(selProp.getId(), selCli.getId(), selAge.getId(), fechaVenta, monto, comision);
                nueva.setMetodoPago(metodoSel);
                dbHelper.insertVenta(nueva);
                if (selProp != null) {
                    selProp.setEstado("Vendido");
                    dbHelper.updatePropiedad(selProp);
                }
                Toast.makeText(getContext(), "Venta registrada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                ventaToEdit.setIdPropiedad(selProp.getId());
                ventaToEdit.setIdCliente(selCli.getId());
                ventaToEdit.setIdAgente(selAge.getId());
                ventaToEdit.setFechaVenta(fechaVenta);
                ventaToEdit.setMontoFinal(monto);
                ventaToEdit.setComision(comision);
                ventaToEdit.setMetodoPago(metodoSel);
                dbHelper.updateVenta(ventaToEdit);
                if (selProp != null) {
                    selProp.setEstado("Vendido");
                    dbHelper.updatePropiedad(selProp);
                }
                Toast.makeText(getContext(), "Venta actualizada correctamente", Toast.LENGTH_SHORT).show();
            }

            loadData();
            dialog.dismiss();
        });
    }

    @Override
    public void onEdit(Venta venta) {
        showVentaDialog(venta);
    }

    @Override
    public void onDelete(Venta venta) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Venta")
                .setMessage("¿Está seguro de eliminar este registro de venta?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.deleteVenta(venta.getId());
                    Toast.makeText(getContext(), "Registro de venta eliminado", Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
