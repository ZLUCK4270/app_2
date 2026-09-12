package com.ecolim.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecolim.app.R;
import com.ecolim.app.data.db.dao.RegistroDao;
import com.ecolim.app.data.model.RegistroResiduo;
import com.ecolim.app.ui.adapter.RegistroAdapter;
import com.ecolim.app.util.Constants;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento de Sincronización en segundo plano con el servidor Python.
 * app/src/main/java/com/ecolim/app/ui/fragments/SincronizarFragment.java
 */
public class SincronizarFragment extends Fragment {

    private TextView tvPendientesCount, tvEstadoRed;
    private RecyclerView rvPendientes;
    private MaterialButton btnSincronizarAhora;
    private RegistroAdapter adapter;
    private RegistroDao dao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sincronizar, container, false);

        dao = new RegistroDao(requireContext());

        tvPendientesCount = v.findViewById(R.id.tv_pendientes_contador_badge);
        btnSincronizarAhora = v.findViewById(R.id.btn_sincronizar_ahora);
        
        // Elementos removidos del rediseño
        // tvEstadoRed = v.findViewById(R.id.tv_estado_red);
        // rvPendientes = v.findViewById(R.id.rv_sync_cola);

        // if (rvPendientes != null) {
        //     rvPendientes.setLayoutManager(new LinearLayoutManager(requireContext()));
        //     adapter = new RegistroAdapter(null);
        //     rvPendientes.setAdapter(adapter);
        // }

        cargarPendientes();

        if (btnSincronizarAhora != null) {
            btnSincronizarAhora.setOnClickListener(v1 -> sincronizarConServidorPython());
        }

        return v;
    }

    private void cargarPendientes() {
        List<RegistroResiduo> pendientes = dao.obtenerPendientesSync();
        // if (adapter != null) adapter.setRegistros(pendientes);
        
        if (tvPendientesCount != null) {
            tvPendientesCount.setText(pendientes.size() + " Pendientes");
        }
        
        if (btnSincronizarAhora != null) {
            btnSincronizarAhora.setEnabled(!pendientes.isEmpty());
        }
    }

    private void sincronizarConServidorPython() {
        List<RegistroResiduo> pendientes = dao.obtenerPendientesSync();
        if (pendientes.isEmpty()) {
            Toast.makeText(requireContext(), "No hay registros pendientes", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSincronizarAhora.setEnabled(false);
        Toast.makeText(requireContext(), "Enviando datos al servidor Python...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                URL url = new URL(Constants.PYTHON_BACKEND_URL + "/sync");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("dispositivo_id", Constants.DISPOSITIVO_ID);
                JSONArray ids = new JSONArray();
                List<String> idList = new ArrayList<>();
                for (RegistroResiduo r : pendientes) {
                    ids.put(r.getId());
                    idList.add(r.getId());
                }
                payload.put("ids", ids);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    dao.marcarComoSincronizados(idList);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "✓ " + idList.size() + " registros sincronizados con SQLite y Python", Toast.LENGTH_LONG).show();
                            cargarPendientes();
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Error de respuesta del servidor: HTTP " + responseCode, Toast.LENGTH_SHORT).show();
                            if (btnSincronizarAhora != null) btnSincronizarAhora.setEnabled(true);
                        });
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (btnSincronizarAhora != null) btnSincronizarAhora.setEnabled(true);
                    });
                }
            }
        }).start();
    }
}
