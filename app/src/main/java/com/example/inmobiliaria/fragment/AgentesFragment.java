package com.example.inmobiliaria.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmobiliaria.MainActivity;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.adapter.AgenteAdapter;
import com.example.inmobiliaria.database.DatabaseHelper;
import com.example.inmobiliaria.model.Agente;
import com.example.inmobiliaria.model.Propiedad;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AgentesFragment extends Fragment implements AgenteAdapter.OnAgenteActionListener {

    private DatabaseHelper dbHelper;
    private AgenteAdapter adapter;
    private List<Agente> listaAgentes = new ArrayList<>();

    private EditText etSearchAgente;
    private ChipGroup chipGroupFilter;
    private Chip chipTodos, chipResidencial, chipComercial, chipOficinas;

    private TextView tvTotalAgentes, tvAgentesResidencial, tvAgentesComercial, tvAgentesOficinas;

    private String selectedEspecialidadFilter = "ALL";

    private Agente currentAgenteForPhoto;
    private ImageView currentModalAgentImageView;
    private String tempPhotoUriForm = "";

    private String saveUriToInternalStorage(Context context, Uri uri, String prefix) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return uri.toString();
            File file = new File(context.getFilesDir(), prefix + "_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return uri.toString();
        }
    }

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && getContext() != null) {
                    String localPath = saveUriToInternalStorage(getContext(), uri, "age");
                    tempPhotoUriForm = localPath;
                    if (currentModalAgentImageView != null) {
                        Glide.with(requireContext()).load(localPath).into(currentModalAgentImageView);
                    }
                    if (currentAgenteForPhoto != null) {
                        currentAgenteForPhoto.setImagenUrl(localPath);
                        dbHelper.updateAgente(currentAgenteForPhoto);
                        Toast.makeText(getContext(), "Foto de agente guardada correctamente", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(getContext(), "Foto seleccionada para el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agentes, container, false);

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
            btnBannerAdd.setOnClickListener(v -> showAgenteDialog(null));
        }

        tvTotalAgentes = view.findViewById(R.id.tvTotalAgentes);
        tvAgentesResidencial = view.findViewById(R.id.tvAgentesResidencial);
        tvAgentesComercial = view.findViewById(R.id.tvAgentesComercial);
        tvAgentesOficinas = view.findViewById(R.id.tvAgentesOficinas);

        etSearchAgente = view.findViewById(R.id.etSearchAgente);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        chipTodos = view.findViewById(R.id.chipTodos);
        chipResidencial = view.findViewById(R.id.chipResidencial);
        chipComercial = view.findViewById(R.id.chipComercial);
        chipOficinas = view.findViewById(R.id.chipOficinas);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new AgenteAdapter(new ArrayList<>(), this);
            recyclerView.setAdapter(adapter);
        }

        setupSearchAndFilters();
        loadData();

        return view;
    }

    private void loadData() {
        listaAgentes = dbHelper.getAllAgentes();

        int total = listaAgentes.size();
        int countResidencial = 0;
        int countComercial = 0;
        int countOficinas = 0;

        for (Agente a : listaAgentes) {
            String esp = a.getEspecialidad() != null ? a.getEspecialidad() : "";
            if (esp.toLowerCase().contains("residencial")) {
                countResidencial++;
            } else if (esp.toLowerCase().contains("comercial")) {
                countComercial++;
            } else if (esp.toLowerCase().contains("oficina")) {
                countOficinas++;
            }
        }

        if (tvTotalAgentes != null) tvTotalAgentes.setText(String.valueOf(total));
        if (tvAgentesResidencial != null) tvAgentesResidencial.setText(String.valueOf(countResidencial));
        if (tvAgentesComercial != null) tvAgentesComercial.setText(String.valueOf(countComercial));
        if (tvAgentesOficinas != null) tvAgentesOficinas.setText(String.valueOf(countOficinas));

        if (chipTodos != null) chipTodos.setText(String.format(Locale.getDefault(), "Todos (%d)", total));
        if (chipResidencial != null) chipResidencial.setText(String.format(Locale.getDefault(), "Residencial (%d)", countResidencial));
        if (chipComercial != null) chipComercial.setText(String.format(Locale.getDefault(), "Comercial (%d)", countComercial));
        if (chipOficinas != null) chipOficinas.setText(String.format(Locale.getDefault(), "Oficinas (%d)", countOficinas));

        applyFilters();
    }

    private void setupSearchAndFilters() {
        if (etSearchAgente != null) {
            etSearchAgente.addTextChangedListener(new TextWatcher() {
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
                if (checkedIds.contains(R.id.chipResidencial)) {
                    selectedEspecialidadFilter = "RESIDENCIAL";
                } else if (checkedIds.contains(R.id.chipComercial)) {
                    selectedEspecialidadFilter = "COMERCIAL";
                } else if (checkedIds.contains(R.id.chipOficinas)) {
                    selectedEspecialidadFilter = "OFICINAS";
                } else {
                    selectedEspecialidadFilter = "ALL";
                }
                applyFilters();
            });
        }
    }

    private void applyFilters() {
        String query = (etSearchAgente != null && etSearchAgente.getText() != null)
                ? etSearchAgente.getText().toString().trim().toLowerCase() : "";
        List<Agente> filteredList = new ArrayList<>();

        for (Agente a : listaAgentes) {
            boolean matchesSearch = TextUtils.isEmpty(query) ||
                    (a.getNombre() != null && a.getNombre().toLowerCase().contains(query)) ||
                    (a.getTelefono() != null && a.getTelefono().toLowerCase().contains(query)) ||
                    (a.getEmail() != null && a.getEmail().toLowerCase().contains(query)) ||
                    (a.getEspecialidad() != null && a.getEspecialidad().toLowerCase().contains(query));

            boolean matchesEsp = true;
            String esp = a.getEspecialidad() != null ? a.getEspecialidad().toLowerCase() : "";

            if ("RESIDENCIAL".equalsIgnoreCase(selectedEspecialidadFilter)) {
                matchesEsp = esp.contains("residencial");
            } else if ("COMERCIAL".equalsIgnoreCase(selectedEspecialidadFilter)) {
                matchesEsp = esp.contains("comercial");
            } else if ("OFICINAS".equalsIgnoreCase(selectedEspecialidadFilter)) {
                matchesEsp = esp.contains("oficina");
            }

            if (matchesSearch && matchesEsp) {
                filteredList.add(a);
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }

    @Override
    public void onItemClick(Agente agente) {
        showAgenteOverviewModal(agente);
    }

    private void showAgenteOverviewModal(Agente a) {
        currentAgenteForPhoto = a;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agente_overview, null);

        builder.setView(view);
        AlertDialog modalDialog = builder.create();

        ImageView ivDetailAgent = view.findViewById(R.id.ivDetailAgent);
        currentModalAgentImageView = ivDetailAgent;

        TextView tvDetailSpecialtyBadge = view.findViewById(R.id.tvDetailSpecialtyBadge);
        TextView tvDetailIdBadge = view.findViewById(R.id.tvDetailIdBadge);
        TextView tvDetailName = view.findViewById(R.id.tvDetailName);
        TextView tvDetailPhone = view.findViewById(R.id.tvDetailPhone);
        TextView tvDetailEmail = view.findViewById(R.id.tvDetailEmail);

        View btnCloseModal = view.findViewById(R.id.btnCloseModal);
        if (btnCloseModal != null) {
            btnCloseModal.setOnClickListener(v -> modalDialog.dismiss());
        }

        if (a.getImagenUrl() != null && !a.getImagenUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(a.getImagenUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(ivDetailAgent);
        } else {
            ivDetailAgent.setImageResource(R.drawable.ic_placeholder);
        }

        if (tvDetailSpecialtyBadge != null) tvDetailSpecialtyBadge.setText(a.getEspecialidad() != null ? a.getEspecialidad() : "Residencial");
        if (tvDetailIdBadge != null) tvDetailIdBadge.setText(String.format(Locale.getDefault(), "ID: AGE-%05d", a.getId()));
        if (tvDetailName != null) tvDetailName.setText(a.getNombre());
        if (tvDetailPhone != null) tvDetailPhone.setText(a.getTelefono());
        if (tvDetailEmail != null) tvDetailEmail.setText(a.getEmail());

        // Populate Assigned Properties in Portfolio
        TextView tvAgentPropCount = view.findViewById(R.id.tvAgentPropCount);
        ViewGroup containerAssignedProperties = view.findViewById(R.id.containerAssignedProperties);

        List<Propiedad> assignedPropiedades = dbHelper.getPropiedadesByAgente(a.getId());
        if (tvAgentPropCount != null) {
            tvAgentPropCount.setText(String.format(Locale.getDefault(), "%d Propiedades", assignedPropiedades.size()));
        }

        if (containerAssignedProperties != null) {
            containerAssignedProperties.removeAllViews();
            if (assignedPropiedades.isEmpty()) {
                TextView tvEmpty = new TextView(getContext());
                tvEmpty.setText("No hay propiedades asignadas actualmente a este agente.");
                tvEmpty.setTextSize(12);
                tvEmpty.setTextColor(android.graphics.Color.parseColor("#757575"));
                tvEmpty.setPadding(0, 10, 0, 10);
                containerAssignedProperties.addView(tvEmpty);
            } else {
                for (Propiedad p : assignedPropiedades) {
                    View propCard = LayoutInflater.from(getContext()).inflate(R.layout.item_propiedad_card, containerAssignedProperties, false);

                    ImageView ivP = propCard.findViewById(R.id.ivPropiedad);
                    TextView tvTitleP = propCard.findViewById(R.id.tvTitulo);
                    TextView tvDirP = propCard.findViewById(R.id.tvDireccion);
                    TextView tvPriceP = propCard.findViewById(R.id.tvPrecio);
                    TextView tvStatusP = propCard.findViewById(R.id.tvEstadoChip);
                    View btnE = propCard.findViewById(R.id.btnEdit);
                    View btnD = propCard.findViewById(R.id.btnDelete);

                    if (btnE != null) btnE.setVisibility(View.GONE);
                    if (btnD != null) btnD.setVisibility(View.GONE);

                    if (ivP != null && p.getImagenUrl() != null && !p.getImagenUrl().isEmpty()) {
                        Glide.with(requireContext()).load(p.getImagenUrl()).placeholder(R.drawable.ic_placeholder).into(ivP);
                    }
                    if (tvTitleP != null) tvTitleP.setText(p.getTitulo());
                    if (tvDirP != null) tvDirP.setText(p.getDireccion());
                    if (tvPriceP != null) tvPriceP.setText(String.format(Locale.getDefault(), "$ %,.0f", p.getPrecio()));
                    if (tvStatusP != null) tvStatusP.setText(p.getEstado());

                    containerAssignedProperties.addView(propCard);
                }
            }
        }

        View btnCloseOverviewBottom = view.findViewById(R.id.btnCloseOverviewBottom);
        if (btnCloseOverviewBottom != null) {
            btnCloseOverviewBottom.setOnClickListener(v -> modalDialog.dismiss());
        }

        modalDialog.show();
    }

    private void showAgenteDialog(@Nullable Agente agenteToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agente, null);

        TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etTelefono = view.findViewById(R.id.etTelefono);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etEspecialidad = view.findViewById(R.id.etEspecialidad);
        View btnPickPhoto = view.findViewById(R.id.btnPickPhoto);

        if (agenteToEdit != null) {
            tempPhotoUriForm = agenteToEdit.getImagenUrl() != null ? agenteToEdit.getImagenUrl() : "";
            currentAgenteForPhoto = agenteToEdit;
        } else {
            tempPhotoUriForm = "";
            currentAgenteForPhoto = null;
        }

        if (btnPickPhoto != null) {
            btnPickPhoto.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        }

        if (agenteToEdit != null) {
            if (tvDialogTitle != null) tvDialogTitle.setText("Editar Agente");
            etNombre.setText(agenteToEdit.getNombre());
            etTelefono.setText(agenteToEdit.getTelefono());
            etEmail.setText(agenteToEdit.getEmail());
            etEspecialidad.setText(agenteToEdit.getEspecialidad());
        } else {
            if (tvDialogTitle != null) tvDialogTitle.setText("Nuevo Agente");
        }

        builder.setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
            String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String especialidad = etEspecialidad.getText() != null ? etEspecialidad.getText().toString().trim() : "";

            if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(telefono)) {
                Toast.makeText(getContext(), "Nombre y Teléfono son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (agenteToEdit == null) {
                Agente nuevo = new Agente(nombre, telefono, email, especialidad);
                nuevo.setImagenUrl(tempPhotoUriForm);
                dbHelper.insertAgente(nuevo);
                Toast.makeText(getContext(), "Agente registrado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                agenteToEdit.setNombre(nombre);
                agenteToEdit.setTelefono(telefono);
                agenteToEdit.setEmail(email);
                agenteToEdit.setEspecialidad(especialidad);
                agenteToEdit.setImagenUrl(tempPhotoUriForm);
                dbHelper.updateAgente(agenteToEdit);
                Toast.makeText(getContext(), "Agente actualizado correctamente", Toast.LENGTH_SHORT).show();
            }

            loadData();
            dialog.dismiss();
        });
    }

    @Override
    public void onEdit(Agente agente) {
        showAgenteDialog(agente);
    }

    @Override
    public void onDelete(Agente agente) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Agente")
                .setMessage("¿Está seguro de eliminar al agente \"" + agente.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.deleteAgente(agente.getId());
                    Toast.makeText(getContext(), "Agente eliminado", Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
