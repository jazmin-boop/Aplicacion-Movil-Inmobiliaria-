package com.example.inmobiliaria.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inmobiliaria.MainActivity;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private TextView tvHomeTotalPropiedades, tvHomeTotalClientes, tvHomeTotalAgentes, tvHomeTotalVentas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        tvHomeTotalPropiedades = view.findViewById(R.id.tvHomeTotalPropiedades);
        tvHomeTotalClientes = view.findViewById(R.id.tvHomeTotalClientes);
        tvHomeTotalAgentes = view.findViewById(R.id.tvHomeTotalAgentes);
        tvHomeTotalVentas = view.findViewById(R.id.tvHomeTotalVentas);

        View cardModulePropiedades = view.findViewById(R.id.cardModulePropiedades);
        View cardModuleClientes = view.findViewById(R.id.cardModuleClientes);
        View cardModuleAgentes = view.findViewById(R.id.cardModuleAgentes);
        View cardModuleVisitas = view.findViewById(R.id.cardModuleVisitas);
        View cardModuleVentas = view.findViewById(R.id.cardModuleVentas);

        MaterialButton btnBannerGoPropiedades = view.findViewById(R.id.btnBannerGoPropiedades);

        if (cardModulePropiedades != null) {
            cardModulePropiedades.setOnClickListener(v -> navigateTo(new PropiedadesFragment()));
        }
        if (cardModuleClientes != null) {
            cardModuleClientes.setOnClickListener(v -> navigateTo(new ClientesFragment()));
        }
        if (cardModuleAgentes != null) {
            cardModuleAgentes.setOnClickListener(v -> navigateTo(new AgentesFragment()));
        }
        if (cardModuleVisitas != null) {
            cardModuleVisitas.setOnClickListener(v -> navigateTo(new VisitasFragment()));
        }
        if (cardModuleVentas != null) {
            cardModuleVentas.setOnClickListener(v -> navigateTo(new VentasFragment()));
        }
        if (btnBannerGoPropiedades != null) {
            btnBannerGoPropiedades.setOnClickListener(v -> navigateTo(new PropiedadesFragment()));
        }

        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void navigateTo(Fragment fragment) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openModule(fragment);
        }
    }

    private void loadData() {
        if (dbHelper != null) {
            int numProps = dbHelper.getAllPropiedades().size();
            int numClis = dbHelper.getAllClientes().size();
            int numAges = dbHelper.getAllAgentes().size();
            int numVens = dbHelper.getAllVentas().size();

            if (tvHomeTotalPropiedades != null) tvHomeTotalPropiedades.setText(String.valueOf(numProps));
            if (tvHomeTotalClientes != null) tvHomeTotalClientes.setText(String.valueOf(numClis));
            if (tvHomeTotalAgentes != null) tvHomeTotalAgentes.setText(String.valueOf(numAges));
            if (tvHomeTotalVentas != null) tvHomeTotalVentas.setText(String.valueOf(numVens));
        }
    }
}
