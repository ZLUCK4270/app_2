package com.ecolim.app.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecolim.app.R;
import com.ecolim.app.data.model.ResumenTipo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para filas de desglose de materiales y porcentajes en InicioFragment y ReportesFragment.
 * app/src/main/java/com/ecolim/app/ui/adapter/ResumenTipoAdapter.java
 */
public class ResumenTipoAdapter extends RecyclerView.Adapter<ResumenTipoAdapter.ViewHolder> {

    private List<ResumenTipo> resumenList = new ArrayList<>();

    public void setDatos(List<ResumenTipo> lista) {
        this.resumenList = lista != null ? lista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resumen_tipo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResumenTipo item = resumenList.get(position);
        holder.tvNombre.setText(item.getMaterial());
        holder.tvKg.setText(String.format(Locale.US, "%.1f kg", item.getTotalKg()));
        holder.tvPorcentaje.setText(String.format(Locale.US, "%.1f%%", item.getPorcentaje()));
        holder.progressBar.setProgress((int) item.getPorcentaje());
        try {
            holder.dotColor.setBackgroundColor(Color.parseColor(item.getColorHex()));
        } catch (Exception ignored) {}
    }

    @Override
    public int getItemCount() {
        return resumenList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvKg, tvPorcentaje;
        ProgressBar progressBar;
        View dotColor;

        ViewHolder(View v) {
            super(v);
            tvNombre = v.findViewById(R.id.tv_resumen_material);
            tvKg = v.findViewById(R.id.tv_resumen_kg);
            tvPorcentaje = v.findViewById(R.id.tv_resumen_porcentaje);
            progressBar = v.findViewById(R.id.progress_resumen_barra);
            dotColor = v.findViewById(R.id.view_color_dot);
        }
    }
}
