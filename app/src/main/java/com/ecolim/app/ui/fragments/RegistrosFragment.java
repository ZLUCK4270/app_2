package com.ecolim.app.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecolim.app.R;
import com.ecolim.app.data.db.dao.RegistroDao;
import com.ecolim.app.data.model.RegistroResiduo;
import com.ecolim.app.ui.adapter.RegistroAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.Locale;

/**
 * Fragmento para historial de registros, filtros por material, búsqueda y edición en BottomSheet.
 * app/src/main/java/com/ecolim/app/ui/fragments/RegistrosFragment.java
 */
public class RegistrosFragment extends Fragment {

    private RecyclerView rvRegistros;
    private RegistroAdapter adapter;
    private EditText etBuscador;
    private ChipGroup chipGroupFiltros;
    private RegistroDao dao;
    private String materialSeleccionado = "Todos";
    private String queryTexto = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registros, container, false);

        dao = new RegistroDao(requireContext());
        rvRegistros = v.findViewById(R.id.rv_registros_residuos);
        etBuscador = v.findViewById(R.id.et_buscar_registro);
        chipGroupFiltros = v.findViewById(R.id.chip_group_filtro_lista);

        if (rvRegistros != null) {
            rvRegistros.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapter = new RegistroAdapter(this::mostrarFichaTecnicaModal);
            rvRegistros.setAdapter(adapter);
        }

        // Búsqueda en tiempo real
        if (etBuscador != null) {
            etBuscador.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    queryTexto = s.toString();
                    recargarLista();
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // Filtro de chips
        if (chipGroupFiltros != null) {
            chipGroupFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) {
                    materialSeleccionado = "Todos";
                } else {
                    int id = checkedIds.get(0);
                    if (id == R.id.chip_filtro_plastico) materialSeleccionado = "Plástico";
                    else if (id == R.id.chip_filtro_carton) materialSeleccionado = "Cartón";
                    else materialSeleccionado = "Todos";
                }
                recargarLista();
            });
        }

        recargarLista();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        recargarLista();
    }

    private void recargarLista() {
        List<RegistroResiduo> lista = dao.obtenerTodos(materialSeleccionado, queryTexto);
        adapter.setRegistros(lista);
    }

    private void mostrarFichaTecnicaModal(RegistroResiduo r) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = getLayoutInflater().inflate(R.layout.sheet_detalle, null);
        dialog.setContentView(sheet);

        android.widget.TextView tvLote = sheet.findViewById(R.id.tv_sheet_codigo_lote);
        android.widget.TextView tvPeso = sheet.findViewById(R.id.tv_sheet_peso);
        MaterialButton btnEditar = sheet.findViewById(R.id.btn_editar_peso);
        MaterialButton btnEliminar = sheet.findViewById(R.id.btn_eliminar_registro);

        tvLote.setText("Lote " + r.getCodigoLote());
        tvPeso.setText(String.format(Locale.US, "%.2f kg", r.getPesoKg()));

        btnEliminar.setOnClickListener(v -> {
            dao.eliminar(r.getId());
            dialog.dismiss();
            recargarLista();
        });

        dialog.show();
    }
}
