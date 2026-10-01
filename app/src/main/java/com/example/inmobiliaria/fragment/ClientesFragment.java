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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.inmobiliaria.adapter.ClienteAdapter;
import com.example.inmobiliaria.database.DatabaseHelper;
import com.example.inmobiliaria.model.Cliente;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientesFragment extends Fragment implements ClienteAdapter.OnClienteActionListener {

    private DatabaseHelper dbHelper;
    private ClienteAdapter adapter;
    private List<Cliente> listaClientes = new ArrayList<>();

    private EditText etSearchCliente;
    private ChipGroup chipGroupFilter;
    private Chip chipTodos, chipComprar, chipAlquilar, chipInversion;

    private TextView tvTotalClientes, tvTotalCompradores, tvTotalInversionistas, tvPresupuestoProm;

    private final String[] intereses = {"Comprar", "Alquilar", "Inversión", "Otro"};
    private String selectedInteresFilter = "ALL";

    private Cliente currentClienteForPhoto;
    private ImageView currentModalClientImageView;
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
                    String localPath = saveUriToInternalStorage(getContext(), uri, "cli");
                    tempPhotoUriForm = localPath;
                    if (currentModalClientImageView != null) {
                        Glide.with(requireContext()).load(localPath).into(currentModalClientImageView);
                    }
                    if (currentClienteForPhoto != null) {
                        currentClienteForPhoto.setImagenUrl(localPath);
                        dbHelper.updateCliente(currentClienteForPhoto);
                        Toast.makeText(getContext(), "Foto de cliente guardada correctamente", Toast.LENGTH_SHORT).show();
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
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

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
            btnBannerAdd.setOnClickListener(v -> showClienteDialog(null));
        }

        tvTotalClientes = view.findViewById(R.id.tvTotalClientes);
        tvTotalCompradores = view.findViewById(R.id.tvTotalCompradores);
        tvTotalInversionistas = view.findViewById(R.id.tvTotalInversionistas);
        tvPresupuestoProm = view.findViewById(R.id.tvPresupuestoProm);

        etSearchCliente = view.findViewById(R.id.etSearchCliente);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        chipTodos = view.findViewById(R.id.chipTodos);
        chipComprar = view.findViewById(R.id.chipComprar);
        chipAlquilar = view.findViewById(R.id.chipAlquilar);
        chipInversion = view.findViewById(R.id.chipInversion);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ClienteAdapter(new ArrayList<>(), this);
            recyclerView.setAdapter(adapter);
        }

        setupSearchAndFilters();
        loadData();

        return view;
    }

    private void loadData() {
        listaClientes = dbHelper.getAllClientes();

        int total = listaClientes.size();
        int countComprar = 0;
        int countAlquilar = 0;
        int countInversion = 0;
        double sumPresupuesto = 0.0;

        for (Cliente c : listaClientes) {
            sumPresupuesto += c.getPresupuesto();
            String interes = c.getInteres() != null ? c.getInteres() : "";
            if ("Comprar".equalsIgnoreCase(interes)) {
                countComprar++;
            } else if ("Alquilar".equalsIgnoreCase(interes)) {
                countAlquilar++;
            } else if ("Inversión".equalsIgnoreCase(interes)) {
                countInversion++;
            }
        }

        double promedioPresupuesto = total > 0 ? (sumPresupuesto / total) : 0.0;

        if (tvTotalClientes != null) tvTotalClientes.setText(String.valueOf(total));
        if (tvTotalCompradores != null) tvTotalCompradores.setText(String.valueOf(countComprar));
        if (tvTotalInversionistas != null) tvTotalInversionistas.setText(String.valueOf(countInversion));
        if (tvPresupuestoProm != null) tvPresupuestoProm.setText(String.format(Locale.getDefault(), "$ %,.0f", promedioPresupuesto));

        if (chipTodos != null) chipTodos.setText(String.format(Locale.getDefault(), "Todos (%d)", total));
        if (chipComprar != null) chipComprar.setText(String.format(Locale.getDefault(), "Comprar (%d)", countComprar));
        if (chipAlquilar != null) chipAlquilar.setText(String.format(Locale.getDefault(), "Alquilar (%d)", countAlquilar));
        if (chipInversion != null) chipInversion.setText(String.format(Locale.getDefault(), "Inversión (%d)", countInversion));

        applyFilters();
    }

    private void setupSearchAndFilters() {
        if (etSearchCliente != null) {
            etSearchCliente.addTextChangedListener(new TextWatcher() {
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
                if (checkedIds.contains(R.id.chipComprar)) {
                    selectedInteresFilter = "COMPRAR";
                } else if (checkedIds.contains(R.id.chipAlquilar)) {
                    selectedInteresFilter = "ALQUILAR";
                } else if (checkedIds.contains(R.id.chipInversion)) {
                    selectedInteresFilter = "INVERSION";
                } else {
                    selectedInteresFilter = "ALL";
                }
                applyFilters();
            });
        }
    }

    private void applyFilters() {
        String query = (etSearchCliente != null && etSearchCliente.getText() != null)
                ? etSearchCliente.getText().toString().trim().toLowerCase() : "";
        List<Cliente> filteredList = new ArrayList<>();

        for (Cliente c : listaClientes) {
            boolean matchesSearch = TextUtils.isEmpty(query) ||
                    (c.getNombre() != null && c.getNombre().toLowerCase().contains(query)) ||
                    (c.getTelefono() != null && c.getTelefono().toLowerCase().contains(query)) ||
                    (c.getEmail() != null && c.getEmail().toLowerCase().contains(query));

            boolean matchesInteres = true;
            String interes = c.getInteres() != null ? c.getInteres() : "";

            if ("COMPRAR".equalsIgnoreCase(selectedInteresFilter)) {
                matchesInteres = "Comprar".equalsIgnoreCase(interes);
            } else if ("ALQUILAR".equalsIgnoreCase(selectedInteresFilter)) {
                matchesInteres = "Alquilar".equalsIgnoreCase(interes);
            } else if ("INVERSION".equalsIgnoreCase(selectedInteresFilter)) {
                matchesInteres = "Inversión".equalsIgnoreCase(interes);
            }

            if (matchesSearch && matchesInteres) {
                filteredList.add(c);
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }

    @Override
    public void onItemClick(Cliente cliente) {
        showClienteOverviewModal(cliente);
    }

    private void showClienteOverviewModal(Cliente c) {
        currentClienteForPhoto = c;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_cliente_overview, null);

        builder.setView(view);
        AlertDialog modalDialog = builder.create();

        ImageView ivDetailClient = view.findViewById(R.id.ivDetailClient);
        currentModalClientImageView = ivDetailClient;

        TextView tvDetailInterestBadge = view.findViewById(R.id.tvDetailInterestBadge);
        TextView tvDetailIdBadge = view.findViewById(R.id.tvDetailIdBadge);
        TextView tvDetailName = view.findViewById(R.id.tvDetailName);
        TextView tvDetailBudget = view.findViewById(R.id.tvDetailBudget);
        TextView tvDetailPhone = view.findViewById(R.id.tvDetailPhone);
        TextView tvDetailEmail = view.findViewById(R.id.tvDetailEmail);

        View btnCloseModal = view.findViewById(R.id.btnCloseModal);
        if (btnCloseModal != null) {
            btnCloseModal.setOnClickListener(v -> modalDialog.dismiss());
        }

        View btnCloseOverviewBottom = view.findViewById(R.id.btnCloseOverviewBottom);
        if (btnCloseOverviewBottom != null) {
            btnCloseOverviewBottom.setOnClickListener(v -> modalDialog.dismiss());
        }

        if (c.getImagenUrl() != null && !c.getImagenUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(c.getImagenUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(ivDetailClient);
        } else {
            ivDetailClient.setImageResource(R.drawable.ic_placeholder);
        }

        if (tvDetailInterestBadge != null) tvDetailInterestBadge.setText(c.getInteres() != null ? c.getInteres() : "Comprar");
        if (tvDetailIdBadge != null) tvDetailIdBadge.setText(String.format(Locale.getDefault(), "ID: CLI-%05d", c.getId()));
        if (tvDetailName != null) tvDetailName.setText(c.getNombre());
        if (tvDetailBudget != null) tvDetailBudget.setText(String.format(Locale.getDefault(), "Presupuesto: $ %,.0f", c.getPresupuesto()));
        if (tvDetailPhone != null) tvDetailPhone.setText(c.getTelefono());
        if (tvDetailEmail != null) tvDetailEmail.setText(c.getEmail());

        modalDialog.show();
    }

    private void showClienteDialog(@Nullable Cliente clienteToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_cliente, null);

        TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etTelefono = view.findViewById(R.id.etTelefono);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPresupuesto = view.findViewById(R.id.etPresupuesto);
        View btnPickPhoto = view.findViewById(R.id.btnPickPhoto);
        Spinner spInteres = view.findViewById(R.id.spInteres);

        if (clienteToEdit != null) {
            tempPhotoUriForm = clienteToEdit.getImagenUrl() != null ? clienteToEdit.getImagenUrl() : "";
            currentClienteForPhoto = clienteToEdit;
        } else {
            tempPhotoUriForm = "";
            currentClienteForPhoto = null;
        }

        if (btnPickPhoto != null) {
            btnPickPhoto.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        }

        ArrayAdapter<String> interesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, intereses);
        spInteres.setAdapter(interesAdapter);

        if (clienteToEdit != null) {
            if (tvDialogTitle != null) tvDialogTitle.setText("Editar Cliente");
            etNombre.setText(clienteToEdit.getNombre());
            etTelefono.setText(clienteToEdit.getTelefono());
            etEmail.setText(clienteToEdit.getEmail());
            etPresupuesto.setText(String.valueOf(clienteToEdit.getPresupuesto()));

            for (int i = 0; i < intereses.length; i++) {
                if (intereses[i].equalsIgnoreCase(clienteToEdit.getInteres())) {
                    spInteres.setSelection(i);
                    break;
                }
            }
        } else {
            if (tvDialogTitle != null) tvDialogTitle.setText("Nuevo Cliente");
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
            String presupuestoStr = etPresupuesto.getText() != null ? etPresupuesto.getText().toString().trim() : "";

            if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(telefono)) {
                Toast.makeText(getContext(), "Nombre y Teléfono son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            double presupuesto = 0.0;
            if (!TextUtils.isEmpty(presupuestoStr)) {
                try {
                    presupuesto = Double.parseDouble(presupuestoStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Ingrese un presupuesto válido", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String interesSelected = spInteres.getSelectedItem() != null ? spInteres.getSelectedItem().toString() : intereses[0];

            if (clienteToEdit == null) {
                Cliente nuevo = new Cliente(nombre, telefono, email, presupuesto, interesSelected);
                nuevo.setImagenUrl(tempPhotoUriForm);
                dbHelper.insertCliente(nuevo);
                Toast.makeText(getContext(), "Cliente registrado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                clienteToEdit.setNombre(nombre);
                clienteToEdit.setTelefono(telefono);
                clienteToEdit.setEmail(email);
                clienteToEdit.setPresupuesto(presupuesto);
                clienteToEdit.setInteres(interesSelected);
                clienteToEdit.setImagenUrl(tempPhotoUriForm);
                dbHelper.updateCliente(clienteToEdit);
                Toast.makeText(getContext(), "Cliente actualizado correctamente", Toast.LENGTH_SHORT).show();
            }

            loadData();
            dialog.dismiss();
        });
    }

    @Override
    public void onEdit(Cliente cliente) {
        showClienteDialog(cliente);
    }

    @Override
    public void onDelete(Cliente cliente) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Cliente")
                .setMessage("¿Está seguro de eliminar al cliente \"" + cliente.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.deleteCliente(cliente.getId());
                    Toast.makeText(getContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
