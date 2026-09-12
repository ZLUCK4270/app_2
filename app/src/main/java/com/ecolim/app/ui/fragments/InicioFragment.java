package com.ecolim.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecolim.app.R;
import com.ecolim.app.data.db.dao.RegistroDao;
import com.ecolim.app.data.model.ResumenTipo;
import com.ecolim.app.ui.adapter.ResumenTipoAdapter;
import com.ecolim.app.ui.view.BarChartView;
import com.ecolim.app.util.SessionManager;

import java.util.List;
import java.util.Locale;

/**
 * Fragmento de Inicio con resumen métrico (142.5 kg), gráfica de barras y distribución.
 * app/src/main/java/com/ecolim/app/ui/fragments/InicioFragment.java
 */
public class InicioFragment extends Fragment {

    private TextView tvTotalKg, tvCo2, tvLotesCount, tvOperadorNombre;
    private BarChartView barChartView;
    private RecyclerView rvDesglose;
    private ResumenTipoAdapter adapter;
    private RegistroDao registroDao;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inicio, container, false);

        registroDao = new RegistroDao(requireContext());
        session = new SessionManager(requireContext());

        tvTotalKg = v.findViewById(R.id.tv_inicio_total_kg);
        
        // Elementos removidos en el rediseño XML:
        // tvCo2 = v.findViewById(R.id.tv_dash_co2_kg);
        // tvLotesCount = v.findViewById(R.id.tv_dash_lotes_count);
        // tvOperadorNombre = v.findViewById(R.id.tv_operador_nombre_banner);
        // barChartView = v.findViewById(R.id.bar_chart_residuos);
        // rvDesglose = v.findViewById(R.id.rv_resumen_materiales);

        // if (rvDesglose != null) {
        //     rvDesglose.setLayoutManager(new LinearLayoutManager(requireContext()));
        //     adapter = new ResumenTipoAdapter();
        //     rvDesglose.setAdapter(adapter);
        // }

        cargarDatos();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarDatos();
    }

    private void cargarDatos() {
        double totalKg = registroDao.obtenerTotalKgHoy();
        
        if (tvTotalKg != null) {
            tvTotalKg.setText(String.format(Locale.US, "%.1f kg", totalKg));
        }

        // Elementos de UI desactivados temporalmente porque no existen en fragment_inicio.xml
        /*
        double co2 = totalKg * 2.25;
        List<ResumenTipo> resumen = registroDao.obtenerResumenPorTipo();
        tvCo2.setText(String.format(Locale.US, "%.1f kg CO₂", co2));
        tvLotesCount.setText(resumen.size() + " Tipos de Residuo");
        if (tvOperadorNombre != null) {
            tvOperadorNombre.setText("Operador: " + session.getOperadorNombre());
        }
        if (barChartView != null) barChartView.setDatos(resumen);
        if (adapter != null) adapter.setDatos(resumen);
        */
    }
}
