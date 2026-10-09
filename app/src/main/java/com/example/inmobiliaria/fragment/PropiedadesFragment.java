package com.example.inmobiliaria.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.adapter.PropiedadAdapter;
import com.example.inmobiliaria.database.DatabaseHelper;
import com.example.inmobiliaria.model.Agente;
import com.example.inmobiliaria.model.Cliente;
import com.example.inmobiliaria.model.Propiedad;
import com.example.inmobiliaria.model.Venta;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PropiedadesFragment extends Fragment implements PropiedadAdapter.OnPropiedadActionListener {

    private DatabaseHelper dbHelper;
    private PropiedadAdapter propiedadAdapter;

    private List<Propiedad> allPropiedades = new ArrayList<>();

    private EditText etSearchPropiedad;
    private ChipGroup chipGroupFilter;
    private Chip chipTodos, chipActivos, chipPendientes, chipVendidos;

    private TextView tvTotalVistas, tvTotalConsultas, tvTotalFavoritos, tvTotalLeads;

    private final String[] tipos = {"Casa", "Departamento", "Terreno", "Local Comercial", "Oficina"};
    private final String[] estados = {"Activo", "Pendiente", "Reservado", "Vendido", "Alquilado"};

    private String currentSelectedStatusFilter = "ALL"; // ALL, Activo, Pendiente, Vendido/Alquilado

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_propiedades, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() instanceof com.example.inmobiliaria.MainActivity) {
                    ((com.example.inmobiliaria.MainActivity) getActivity()).showMainPanel();
                }
            });
        }

        MaterialButton btnBannerAdd = view.findViewById(R.id.btnBannerAdd);
        if (btnBannerAdd != null) {
            btnBannerAdd.setOnClickListener(v -> showPropiedadAddEditDialog(null));
        }

        tvTotalVistas = view.findViewById(R.id.tvTotalVistas);
        tvTotalConsultas = view.findViewById(R.id.tvTotalConsultas);
        tvTotalFavoritos = view.findViewById(R.id.tvTotalFavoritos);
        tvTotalLeads = view.findViewById(R.id.tvTotalLeads);

        etSearchPropiedad = view.findViewById(R.id.etSearchPropiedad);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        chipTodos = view.findViewById(R.id.chipTodos);
        chipActivos = view.findViewById(R.id.chipActivos);
        chipPendientes = view.findViewById(R.id.chipPendientes);
        chipVendidos = view.findViewById(R.id.chipVendidos);

        RecyclerView rvPropiedades = view.findViewById(R.id.rvPropiedades);
        if (rvPropiedades != null) {
            rvPropiedades.setLayoutManager(new LinearLayoutManager(getContext()));
            propiedadAdapter = new PropiedadAdapter(new ArrayList<>(), this);
            rvPropiedades.setAdapter(propiedadAdapter);
        }

        setupQuickPillsAndTrends(view);
        setupSearchAndFilters();
        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void setupQuickPillsAndTrends(View view) {
        View btnFilterRound = view.findViewById(R.id.btnFilterRound);
        if (btnFilterRound != null && chipGroupFilter != null) {
            btnFilterRound.setOnClickListener(v -> chipGroupFilter.requestFocus());
        }
    }

    private void loadData() {
        allPropiedades = dbHelper.getAllPropiedades();

        int totalVistas = 0;
        int totalConsultas = 0;
        int totalFavoritos = 0;
        int countActivos = 0;
        int countPendientes = 0;
        int countVendidos = 0;

        for (Propiedad p : allPropiedades) {
            totalVistas += p.getVistas();
            totalConsultas += p.getConsultas();
            totalFavoritos += p.getFavoritos();

            String st = p.getEstado();
            if ("Activo".equalsIgnoreCase(st) || "Disponible".equalsIgnoreCase(st)) {
                countActivos++;
            } else if ("Pendiente".equalsIgnoreCase(st) || "En Negociación".equalsIgnoreCase(st)) {
                countPendientes++;
            } else {
                countVendidos++;
            }
        }

        if (tvTotalVistas != null) tvTotalVistas.setText(String.format(Locale.getDefault(), "%,d", totalVistas));
        if (tvTotalConsultas != null) tvTotalConsultas.setText(String.format(Locale.getDefault(), "%,d", totalConsultas));
        if (tvTotalFavoritos != null) tvTotalFavoritos.setText(String.format(Locale.getDefault(), "%,d", totalFavoritos));
        if (tvTotalLeads != null) tvTotalLeads.setText(String.format(Locale.getDefault(), "%,d", totalConsultas * 2 + 10));

        if (chipTodos != null) chipTodos.setText(String.format(Locale.getDefault(), "Todos (%d)", allPropiedades.size()));
        if (chipActivos != null) chipActivos.setText(String.format(Locale.getDefault(), "Activos (%d)", countActivos));
        if (chipPendientes != null) chipPendientes.setText(String.format(Locale.getDefault(), "Pendientes (%d)", countPendientes));
        if (chipVendidos != null) chipVendidos.setText(String.format(Locale.getDefault(), "Cerrados (%d)", countVendidos));

        applyFilters();
    }

    private void setupSearchAndFilters() {
        if (etSearchPropiedad != null) {
            etSearchPropiedad.addTextChangedListener(new TextWatcher() {
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
                if (checkedIds.contains(R.id.chipActivos)) {
                    currentSelectedStatusFilter = "ACTIVO";
                } else if (checkedIds.contains(R.id.chipPendientes)) {
                    currentSelectedStatusFilter = "PENDIENTE";
                } else if (checkedIds.contains(R.id.chipVendidos)) {
                    currentSelectedStatusFilter = "VENDIDO";
                } else {
                    currentSelectedStatusFilter = "ALL";
                }
                applyFilters();
            });
        }
    }

    private void applyFilters() {
        String query = (etSearchPropiedad != null && etSearchPropiedad.getText() != null)
                ? etSearchPropiedad.getText().toString().trim().toLowerCase() : "";
        List<Propiedad> filteredPropiedades = new ArrayList<>();

        for (Propiedad p : allPropiedades) {
            boolean matchesSearch = TextUtils.isEmpty(query) ||
                    (p.getTitulo() != null && p.getTitulo().toLowerCase().contains(query)) ||
                    (p.getDireccion() != null && p.getDireccion().toLowerCase().contains(query)) ||
                    (p.getTipo() != null && p.getTipo().toLowerCase().contains(query));

            boolean matchesStatus = true;
            String st = p.getEstado();

            if ("ACTIVO".equalsIgnoreCase(currentSelectedStatusFilter)) {
                matchesStatus = "Activo".equalsIgnoreCase(st) || "Disponible".equalsIgnoreCase(st);
            } else if ("PENDIENTE".equalsIgnoreCase(currentSelectedStatusFilter)) {
                matchesStatus = "Pendiente".equalsIgnoreCase(st) || "En Negociación".equalsIgnoreCase(st);
            } else if ("VENDIDO".equalsIgnoreCase(currentSelectedStatusFilter)) {
                matchesStatus = !"Activo".equalsIgnoreCase(st) && !"Disponible".equalsIgnoreCase(st) && !"Pendiente".equalsIgnoreCase(st) && !"En Negociación".equalsIgnoreCase(st);
            }

            if (matchesSearch && matchesStatus) {
                filteredPropiedades.add(p);
            }
        }

        if (propiedadAdapter != null) {
            propiedadAdapter.updateList(filteredPropiedades);
        }
    }

    // ==========================================
    // ADAPTER CLICK ACTIONS
    // ==========================================
    @Override
    public void onItemClick(Propiedad propiedad) {
        showPropertyOverviewModal(propiedad);
    }

    @Override
    public void onEditClick(Propiedad propiedad) {
        showPropiedadAddEditDialog(propiedad);
    }

    @Override
    public void onDeleteClick(Propiedad propiedad) {
        confirmDeletePropiedad(propiedad);
    }

    // ==========================================
    // PROPERTY OVERVIEW MODAL
    // ==========================================
    private void showPropertyOverviewModal(Propiedad p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_property_overview, null);

        builder.setView(view);
        AlertDialog modalDialog = builder.create();

        ImageView ivDetailProperty = view.findViewById(R.id.ivDetailProperty);
        TextView tvDetailStatusBadge = view.findViewById(R.id.tvDetailStatusBadge);
        TextView tvDetailIdBadge = view.findViewById(R.id.tvDetailIdBadge);
        TextView tvDetailTitle = view.findViewById(R.id.tvDetailTitle);
        TextView tvDetailPrice = view.findViewById(R.id.tvDetailPrice);
        TextView tvDetailLocation = view.findViewById(R.id.tvDetailLocation);

        TextView tvDetailVistas = view.findViewById(R.id.tvDetailVistas);
        TextView tvDetailConsultas = view.findViewById(R.id.tvDetailConsultas);
        TextView tvDetailFavoritos = view.findViewById(R.id.tvDetailFavoritos);
        TextView tvDetailDiasActivo = view.findViewById(R.id.tvDetailDiasActivo);

        TextView tvDetailFechaPub = view.findViewById(R.id.tvDetailFechaPub);
        TextView tvDetailStatusCard = view.findViewById(R.id.tvDetailStatusCard);
        TextView tvOptionBadgeInquiries = view.findViewById(R.id.tvOptionBadgeInquiries);

        View btnCloseModal = view.findViewById(R.id.btnCloseModal);
        if (btnCloseModal != null) {
            btnCloseModal.setOnClickListener(v -> modalDialog.dismiss());
        }

        View btnCloseOverviewBottom = view.findViewById(R.id.btnCloseOverviewBottom);
        if (btnCloseOverviewBottom != null) {
            btnCloseOverviewBottom.setOnClickListener(v -> modalDialog.dismiss());
        }

        if (p.getImagenUrl() != null && !p.getImagenUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(p.getImagenUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(ivDetailProperty);
        } else {
            ivDetailProperty.setImageResource(R.drawable.ic_placeholder);
        }

        if (tvDetailStatusBadge != null) tvDetailStatusBadge.setText(p.getEstado());
        if (tvDetailIdBadge != null) tvDetailIdBadge.setText(String.format(Locale.getDefault(), "ID: PRP-%05d", p.getId()));
        if (tvDetailTitle != null) tvDetailTitle.setText(p.getTitulo());
        if (tvDetailPrice != null) tvDetailPrice.setText(String.format(Locale.getDefault(), "$ %,.0f", p.getPrecio()));
        if (tvDetailLocation != null) tvDetailLocation.setText(p.getDireccion());

        if (tvDetailVistas != null) tvDetailVistas.setText(String.valueOf(p.getVistas()));
        if (tvDetailConsultas != null) tvDetailConsultas.setText(String.valueOf(p.getConsultas()));
        if (tvDetailFavoritos != null) tvDetailFavoritos.setText(String.valueOf(p.getFavoritos()));
        if (tvDetailDiasActivo != null) tvDetailDiasActivo.setText(String.valueOf(p.getDiasActivo()));

        String fechaPubStr = "Publicado el: " + (p.getFechaPublicacion() != null ? p.getFechaPublicacion() : "2026-06-07");
        if (tvDetailFechaPub != null) tvDetailFechaPub.setText(fechaPubStr);

        String statusCardStr = "• " + p.getEstado();
        if (tvDetailStatusCard != null) tvDetailStatusCard.setText(statusCardStr);
        if (tvOptionBadgeInquiries != null) tvOptionBadgeInquiries.setText(String.valueOf(p.getConsultas()));

        View btnOptionInquiries = view.findViewById(R.id.btnOptionInquiries);
        if (btnOptionInquiries != null) {
            btnOptionInquiries.setOnClickListener(v ->
                Toast.makeText(getContext(), "Total de consultas recibidas: " + p.getConsultas(), Toast.LENGTH_SHORT).show()
            );
        }

        View btnOptionPerformance = view.findViewById(R.id.btnOptionPerformance);
        if (btnOptionPerformance != null) {
            btnOptionPerformance.setOnClickListener(v ->
                Toast.makeText(getContext(), "Rendimiento: " + p.getVistas() + " Vistas acumuladas", Toast.LENGTH_SHORT).show()
            );
        }

        View btnOptionDuplicate = view.findViewById(R.id.btnOptionDuplicate);
        if (btnOptionDuplicate != null) {
            btnOptionDuplicate.setOnClickListener(v -> {
                modalDialog.dismiss();
                Propiedad dupe = new Propiedad(
                        p.getTitulo() + " (Copia)",
                        p.getDireccion(),
                        p.getPrecio(),
                        p.getTipo(),
                        "Activo",
                        p.getImagenUrl()
                );
                dbHelper.insertPropiedad(dupe);
                Toast.makeText(getContext(), "Publicación duplicada correctamente", Toast.LENGTH_SHORT).show();
                loadData();
            });
        }

        View btnShareProperty = view.findViewById(R.id.btnShareProperty);
        if (btnShareProperty != null) {
            btnShareProperty.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Mira esta propiedad: " + p.getTitulo() + " - " + String.format(Locale.getDefault(), "$ %,.0f", p.getPrecio()) + "\nUbicación: " + p.getDireccion());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, "Compartir propiedad vía");
                startActivity(shareIntent);
            });
        }

        modalDialog.show();
    }

    // ==========================================
    // CREATE / EDIT PROPIEDAD DIALOG FORM MODAL
    // ==========================================
    private void showPropiedadAddEditDialog(@Nullable Propiedad propiedadToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_propiedad, null);

        TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        EditText etTitulo = view.findViewById(R.id.etTitulo);
        EditText etDireccion = view.findViewById(R.id.etDireccion);
        EditText etPrecio = view.findViewById(R.id.etPrecio);
        EditText etImagenUrl = view.findViewById(R.id.etImagenUrl);
        Spinner spTipo = view.findViewById(R.id.spTipo);
        Spinner spEstado = view.findViewById(R.id.spEstado);

        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, tipos);
        spTipo.setAdapter(tipoAdapter);

        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, estados);
        spEstado.setAdapter(estadoAdapter);

        if (propiedadToEdit != null) {
            if (tvDialogTitle != null) tvDialogTitle.setText("Editar Propiedad");
            etTitulo.setText(propiedadToEdit.getTitulo());
            etDireccion.setText(propiedadToEdit.getDireccion());
            etPrecio.setText(String.valueOf(propiedadToEdit.getPrecio()));
            etImagenUrl.setText(propiedadToEdit.getImagenUrl());

            for (int i = 0; i < tipos.length; i++) {
                if (tipos[i].equalsIgnoreCase(propiedadToEdit.getTipo())) {
                    spTipo.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < estados.length; i++) {
                if (estados[i].equalsIgnoreCase(propiedadToEdit.getEstado())) {
                    spEstado.setSelection(i);
                    break;
                }
            }
        } else {
            if (tvDialogTitle != null) tvDialogTitle.setText("Nueva Propiedad");
            etImagenUrl.setText("https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800");
        }

        builder.setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String titulo = etTitulo.getText() != null ? etTitulo.getText().toString().trim() : "";
            String direccion = etDireccion.getText() != null ? etDireccion.getText().toString().trim() : "";
            String precioStr = etPrecio.getText() != null ? etPrecio.getText().toString().trim() : "";
            String imgUrl = etImagenUrl.getText() != null ? etImagenUrl.getText().toString().trim() : "";

            if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(direccion) || TextUtils.isEmpty(precioStr)) {
                Toast.makeText(getContext(), "Por favor complete todos los campos requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio;
            try {
                precio = Double.parseDouble(precioStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Ingrese un precio válido", Toast.LENGTH_SHORT).show();
                return;
            }

            String tipoSelected = spTipo.getSelectedItem() != null ? spTipo.getSelectedItem().toString() : tipos[0];
            String estadoSelected = spEstado.getSelectedItem() != null ? spEstado.getSelectedItem().toString() : estados[0];

            int targetPropId = 0;
            if (propiedadToEdit == null) {
                Propiedad nueva = new Propiedad(titulo, direccion, precio, tipoSelected, estadoSelected, imgUrl);
                long newId = dbHelper.insertPropiedad(nueva);
                targetPropId = (int) newId;
                Toast.makeText(getContext(), "Propiedad registrada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                propiedadToEdit.setTitulo(titulo);
                propiedadToEdit.setDireccion(direccion);
                propiedadToEdit.setPrecio(precio);
                propiedadToEdit.setTipo(tipoSelected);
                propiedadToEdit.setEstado(estadoSelected);
                propiedadToEdit.setImagenUrl(imgUrl);
                dbHelper.updatePropiedad(propiedadToEdit);
                targetPropId = propiedadToEdit.getId();
                Toast.makeText(getContext(), "Propiedad actualizada correctamente", Toast.LENGTH_SHORT).show();
            }

            if ("Vendido".equalsIgnoreCase(estadoSelected) && targetPropId > 0) {
                if (!dbHelper.hasVentaForPropiedad(targetPropId)) {
                    List<Cliente> clientes = dbHelper.getAllClientes();
                    List<Agente> agentes = dbHelper.getAllAgentes();
                    int cliId = !clientes.isEmpty() ? clientes.get(0).getId() : 1;
                    int ageId = !agentes.isEmpty() ? agentes.get(0).getId() : 1;
                    Venta autoV = new Venta(targetPropId, cliId, ageId, "2026-10-09", precio, precio * 0.03);
                    autoV.setMetodoPago("Transferencia");
                    dbHelper.insertVenta(autoV);
                }
            }

            loadData();
            dialog.dismiss();
        });
    }

    private void confirmDeletePropiedad(Propiedad p) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Propiedad")
                .setMessage("¿Está seguro de eliminar la propiedad \"" + p.getTitulo() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.deletePropiedad(p.getId());
                    Toast.makeText(getContext(), "Propiedad eliminada correctamente", Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
