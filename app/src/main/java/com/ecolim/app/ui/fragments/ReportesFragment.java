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

import com.ecolim.app.R;
import com.ecolim.app.data.db.dao.RegistroDao;
import com.ecolim.app.data.model.RegistroResiduo;
import com.ecolim.app.data.model.ResumenTipo;
import com.ecolim.app.ui.view.BarChartView;
import com.ecolim.app.util.CsvExporter;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Fragmento de Reportes con métricas ambientales consolidadas y exportación CSV.
 * app/src/main/java/com/ecolim/app/ui/fragments/ReportesFragment.java
 */
public class ReportesFragment extends Fragment {

    private TextView tvTotalKgReporte, tvCo2Reporte, tvArbolesSalvados;
    private BarChartView barChartReporte;
    private MaterialButton btnExportarCsv;
    private RegistroDao dao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reportes, container, false);

        dao = new RegistroDao(requireContext());

        tvTotalKgReporte = v.findViewById(R.id.tv_reporte_total_kg);
        tvCo2Reporte = v.findViewById(R.id.tv_reporte_co2_kg);
        tvArbolesSalvados = v.findViewById(R.id.tv_reporte_arboles);
        barChartReporte = v.findViewById(R.id.chart_reportes);
        btnExportarCsv = v.findViewById(R.id.btn_exportar_csv);

        actualizarMetricas();

        btnExportarCsv.setOnClickListener(v1 -> {
            try {
                List<RegistroResiduo> todos = dao.obtenerTodos("Todos", "");
                File csv = CsvExporter.exportar(todos, requireContext().getExternalFilesDir(null));
                Toast.makeText(requireContext(), "CSV exportado en: " + csv.getName(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error al exportar CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    private void actualizarMetricas() {
        double totalKg = dao.obtenerTotalKgHoy();
        double co2 = totalKg * 2.25;
        int arboles = (int) (totalKg / 18.0);

        tvTotalKgReporte.setText(String.format(Locale.US, "%.1f kg", totalKg));
        tvCo2Reporte.setText(String.format(Locale.US, "%.1f kg", co2));
        tvArbolesSalvados.setText(String.valueOf(arboles));

        List<ResumenTipo> resumen = dao.obtenerResumenPorTipo();
        barChartReporte.setDatos(resumen);
    }
}
