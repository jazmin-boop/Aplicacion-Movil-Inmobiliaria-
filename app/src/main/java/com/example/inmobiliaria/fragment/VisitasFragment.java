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
import com.example.inmobiliaria.MainActivity;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.adapter.VisitaAdapter;
import com.example.inmobiliaria.database.DatabaseHelper;
import com.example.inmobiliaria.model.Agente;
import com.example.inmobiliaria.model.Cliente;
import com.example.inmobiliaria.model.Propiedad;
import com.example.inmobiliaria.model.Visita;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VisitasFragment extends Fragment implements VisitaAdapter.OnVisitaActionListener {

    private DatabaseHelper dbHelper;
    private VisitaAdapter adapter;
    private List<Visita> listaVisitas = new ArrayList<>();

    private EditText etSearchVisita;
    private ChipGroup chipGroupFilter;
    private Chip chipTodos, chipProgramadas, chipRealizadas, chipCanceladas;

    private TextView tvTotalVisitas, tvVisitasProgramadas, tvVisitasRealizadas, tvVisitasCanceladas;

    private final String[] estadosVisita = {"Programada", "Realizada", "Cancelada"};
    private String selectedEstadoFilter = "ALL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitas, container, false);

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
            btnBannerAdd.setOnClickListener(v -> showVisitaDialog(null));
        }

        tvTotalVisitas = view.findViewById(R.id.tvTotalVisitas);
        tvVisitasProgramadas = view.findViewById(R.id.tvVisitasProgramadas);
        tvVisitasRealizadas = view.findViewById(R.id.tvVisitasRealizadas);
        tvVisitasCanceladas = view.findViewById(R.id.tvVisitasCanceladas);

        etSearchVisita = view.findViewById(R.id.etSearchVisita);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        chipTodos = view.findViewById(R.id.chipTodos);
        chipProgramadas = view.findViewById(R.id.chipProgramadas);
        chipRealizadas = view.findViewById(R.id.chipRealizadas);
        chipCanceladas = view.findViewById(R.id.chipCanceladas);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new VisitaAdapter(new ArrayList<>(), this);
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
        listaVisitas = dbHelper.getAllVisitas();

        int total = listaVisitas.size();
        int countProg = 0;
        int countReal = 0;
        int countCanc = 0;

        for (Visita v : listaVisitas) {
            String st = v.getEstado() != null ? v.getEstado() : "";
            if ("Programada".equalsIgnoreCase(st)) {
                countProg++;
            } else if ("Realizada".equalsIgnoreCase(st)) {
                countReal++;
            } else if ("Cancelada".equalsIgnoreCase(st)) {
                countCanc++;
            }
        }

        if (tvTotalVisitas != null) tvTotalVisitas.setText(String.valueOf(total));
        if (tvVisitasProgramadas != null) tvVisitasProgramadas.setText(String.valueOf(countProg));
        if (tvVisitasRealizadas != null) tvVisitasRealizadas.setText(String.valueOf(countReal));
        if (tvVisitasCanceladas != null) tvVisitasCanceladas.setText(String.valueOf(countCanc));

        if (chipTodos != null) chipTodos.setText(String.format(Locale.getDefault(), "Todos (%d)", total));
        if (chipProgramadas != null) chipProgramadas.setText(String.format(Locale.getDefault(), "Programadas (%d)", countProg));
        if (chipRealizadas != null) chipRealizadas.setText(String.format(Locale.getDefault(), "Realizadas (%d)", countReal));
        if (chipCanceladas != null) chipCanceladas.setText(String.format(Locale.getDefault(), "Canceladas (%d)", countCanc));

        applyFilters();
    }

    private void setupSearchAndFilters() {
        if (etSearchVisita != null) {
            etSearchVisita.addTextChangedListener(new TextWatcher() {
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
                if (checkedIds.contains(R.id.chipProgramadas)) {
                    selectedEstadoFilter = "PROGRAMADA";
                } else if (checkedIds.contains(R.id.chipRealizadas)) {
                    selectedEstadoFilter = "REALIZADA";
                } else if (checkedIds.contains(R.id.chipCanceladas)) {
                    selectedEstadoFilter = "CANCELADA";
                } else {
                    selectedEstadoFilter = "ALL";
                }
                applyFilters();
            });
        }
    }

    private void applyFilters() {
        String query = (etSearchVisita != null && etSearchVisita.getText() != null)
                ? etSearchVisita.getText().toString().trim().toLowerCase() : "";
        List<Visita> filteredList = new ArrayList<>();

        for (Visita v : listaVisitas) {
            boolean matchesSearch = TextUtils.isEmpty(query) ||
                    (v.getTituloPropiedad() != null && v.getTituloPropiedad().toLowerCase().contains(query)) ||
                    (v.getNombreCliente() != null && v.getNombreCliente().toLowerCase().contains(query)) ||
                    (v.getNombreAgente() != null && v.getNombreAgente().toLowerCase().contains(query)) ||
                    (v.getComentarios() != null && v.getComentarios().toLowerCase().contains(query));

            boolean matchesEstado = true;
            String st = v.getEstado() != null ? v.getEstado() : "";

            if ("PROGRAMADA".equalsIgnoreCase(selectedEstadoFilter)) {
                matchesEstado = "Programada".equalsIgnoreCase(st);
            } else if ("REALIZADA".equalsIgnoreCase(selectedEstadoFilter)) {
                matchesEstado = "Realizada".equalsIgnoreCase(st);
            } else if ("CANCELADA".equalsIgnoreCase(selectedEstadoFilter)) {
                matchesEstado = "Cancelada".equalsIgnoreCase(st);
            }

            if (matchesSearch && matchesEstado) {
                filteredList.add(v);
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }

    @Override
    public void onItemClick(Visita visita) {
        showVisitaOverviewModal(visita);
    }

    @Override
    public void onHistory(Visita visita) {
        showVisitaOverviewModal(visita);
    }

    private void showVisitaOverviewModal(Visita v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_historial_visita, null);

        builder.setView(view);
        AlertDialog modalDialog = builder.create();

        ImageView ivDetailProperty = view.findViewById(R.id.ivDetailProperty);
        TextView tvDetailStatusBadge = view.findViewById(R.id.tvDetailStatusBadge);
        TextView tvDetailIdBadge = view.findViewById(R.id.tvDetailIdBadge);
        TextView tvDetailTitle = view.findViewById(R.id.tvDetailTitle);
        TextView tvDetailLocation = view.findViewById(R.id.tvDetailLocation);
        TextView tvDetailPrice = view.findViewById(R.id.tvDetailPrice);

        TextView tvDetailFechaHora = view.findViewById(R.id.tvDetailFechaHora);
        TextView tvDetailCliente = view.findViewById(R.id.tvDetailCliente);
        TextView tvDetailAgente = view.findViewById(R.id.tvDetailAgente);
        TextView tvDetailComentarios = view.findViewById(R.id.tvDetailComentarios);

        View btnCloseModal = view.findViewById(R.id.btnCloseModal);
        if (btnCloseModal != null) {
            btnCloseModal.setOnClickListener(v1 -> modalDialog.dismiss());
        }

        if (v.getImagenUrlPropiedad() != null && !v.getImagenUrlPropiedad().isEmpty()) {
            Glide.with(requireContext())
                    .load(v.getImagenUrlPropiedad())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(ivDetailProperty);
        } else {
            ivDetailProperty.setImageResource(R.drawable.ic_placeholder);
        }

        if (tvDetailStatusBadge != null) tvDetailStatusBadge.setText(v.getEstado() != null ? v.getEstado() : "Programada");
        if (tvDetailIdBadge != null) tvDetailIdBadge.setText(String.format(Locale.getDefault(), "ID: VIS-%05d", v.getId()));
        if (tvDetailTitle != null) tvDetailTitle.setText(v.getTituloPropiedad() != null ? v.getTituloPropiedad() : "Propiedad #" + v.getIdPropiedad());
        if (tvDetailLocation != null) tvDetailLocation.setText(v.getDireccionPropiedad() != null ? v.getDireccionPropiedad() : "Ubicación registrada");
        if (tvDetailPrice != null) tvDetailPrice.setText(String.format(Locale.getDefault(), "$ %,.0f", v.getPrecioPropiedad()));

        if (tvDetailFechaHora != null) tvDetailFechaHora.setText(v.getFechaHora() != null ? v.getFechaHora() : "-");
        if (tvDetailCliente != null) tvDetailCliente.setText(v.getNombreCliente() != null ? v.getNombreCliente() : "-");
        if (tvDetailAgente != null) tvDetailAgente.setText(v.getNombreAgente() != null ? v.getNombreAgente() : "-");
        if (tvDetailComentarios != null) tvDetailComentarios.setText(!TextUtils.isEmpty(v.getComentarios()) ? v.getComentarios() : "Sin observaciones adicionles");

        View btnCloseOverviewBottom = view.findViewById(R.id.btnCloseOverviewBottom);
        if (btnCloseOverviewBottom != null) {
            btnCloseOverviewBottom.setOnClickListener(v1 -> modalDialog.dismiss());
        }

        View btnMarkRealizada = view.findViewById(R.id.btnMarkRealizada);
        if (btnMarkRealizada != null) {
            if ("Realizada".equalsIgnoreCase(v.getEstado()) || "Cancelada".equalsIgnoreCase(v.getEstado())) {
                btnMarkRealizada.setVisibility(View.GONE);
            } else {
                btnMarkRealizada.setVisibility(View.VISIBLE);
                btnMarkRealizada.setOnClickListener(v1 -> {
                    v.setEstado("Realizada");
                    dbHelper.updateVisita(v);
                    Toast.makeText(getContext(), "Visita marcada como REALIZADA", Toast.LENGTH_SHORT).show();
                    loadData();
                    modalDialog.dismiss();
                });
            }
        }

        View btnShareVisit = view.findViewById(R.id.btnShareVisit);
        if (btnShareVisit != null) {
            btnShareVisit.setOnClickListener(v1 -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Historial de Visita Inmobiliaria:\n" +
                        "Propiedad: " + v.getTituloPropiedad() + "\n" +
                        "Fecha y Hora: " + v.getFechaHora() + "\n" +
                        "Cliente: " + v.getNombreCliente() + "\n" +
                        "Agente: " + v.getNombreAgente() + "\n" +
                        "Estado: " + v.getEstado());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, "Compartir Historial de Visita");
                startActivity(shareIntent);
            });
        }

        modalDialog.show();
    }

    private void showVisitaDialog(@Nullable Visita visitaToEdit) {
        List<Propiedad> propiedades = dbHelper.getAllPropiedades();
        List<Cliente> clientes = dbHelper.getAllClientes();
        List<Agente> agentes = dbHelper.getAllAgentes();

        if (propiedades.isEmpty() || clientes.isEmpty() || agentes.isEmpty()) {
            Toast.makeText(getContext(), "Debe registrar al menos una propiedad, cliente y agente antes de agendar visitas.", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_visita, null);

        TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        Spinner spPropiedad = view.findViewById(R.id.spPropiedad);
        Spinner spCliente = view.findViewById(R.id.spCliente);
        Spinner spAgente = view.findViewById(R.id.spAgente);
        EditText etFechaHora = view.findViewById(R.id.etFechaHora);
        EditText etComentarios = view.findViewById(R.id.etComentarios);
        Spinner spEstado = view.findViewById(R.id.spEstado);

        ArrayAdapter<Propiedad> propAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, propiedades);
        spPropiedad.setAdapter(propAdapter);

        ArrayAdapter<Cliente> cliAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, clientes);
        spCliente.setAdapter(cliAdapter);

        ArrayAdapter<Agente> ageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, agentes);
        spAgente.setAdapter(ageAdapter);

        ArrayAdapter<String> estAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, estadosVisita);
        spEstado.setAdapter(estAdapter);

        if (visitaToEdit != null) {
            if (tvDialogTitle != null) tvDialogTitle.setText("Editar Visita");
            etFechaHora.setText(visitaToEdit.getFechaHora());
            etComentarios.setText(visitaToEdit.getComentarios());

            for (int i = 0; i < propiedades.size(); i++) {
                if (propiedades.get(i).getId() == visitaToEdit.getIdPropiedad()) {
                    spPropiedad.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < clientes.size(); i++) {
                if (clientes.get(i).getId() == visitaToEdit.getIdCliente()) {
                    spCliente.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < agentes.size(); i++) {
                if (agentes.get(i).getId() == visitaToEdit.getIdAgente()) {
                    spAgente.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < estadosVisita.length; i++) {
                if (estadosVisita[i].equalsIgnoreCase(visitaToEdit.getEstado())) {
                    spEstado.setSelection(i);
                    break;
                }
            }
        } else {
            if (tvDialogTitle != null) tvDialogTitle.setText("Nueva Visita");
            String defaultDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            etFechaHora.setText(defaultDate);
        }

        builder.setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String fechaHora = etFechaHora.getText() != null ? etFechaHora.getText().toString().trim() : "";
            String comentarios = etComentarios.getText() != null ? etComentarios.getText().toString().trim() : "";

            if (TextUtils.isEmpty(fechaHora)) {
                Toast.makeText(getContext(), "Ingrese la fecha y hora de la visita", Toast.LENGTH_SHORT).show();
                return;
            }

            Propiedad selProp = (Propiedad) spPropiedad.getSelectedItem();
            Cliente selCli = (Cliente) spCliente.getSelectedItem();
            Agente selAge = (Agente) spAgente.getSelectedItem();
            String estadoSel = spEstado.getSelectedItem() != null ? spEstado.getSelectedItem().toString() : estadosVisita[0];

            if (visitaToEdit == null) {
                Visita nueva = new Visita(selProp.getId(), selCli.getId(), selAge.getId(), fechaHora, comentarios, estadoSel);
                dbHelper.insertVisita(nueva);
                Toast.makeText(getContext(), "Visita agendada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                visitaToEdit.setIdPropiedad(selProp.getId());
                visitaToEdit.setIdCliente(selCli.getId());
                visitaToEdit.setIdAgente(selAge.getId());
                visitaToEdit.setFechaHora(fechaHora);
                visitaToEdit.setComentarios(comentarios);
                visitaToEdit.setEstado(estadoSel);
                dbHelper.updateVisita(visitaToEdit);
                Toast.makeText(getContext(), "Visita actualizada correctamente", Toast.LENGTH_SHORT).show();
            }

            loadData();
            dialog.dismiss();
        });
    }

    @Override
    public void onEdit(Visita visita) {
        showVisitaDialog(visita);
    }

    @Override
    public void onDelete(Visita visita) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Visita")
                .setMessage("¿Está seguro de eliminar esta visita?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.deleteVisita(visita.getId());
                    Toast.makeText(getContext(), "Visita eliminada", Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
