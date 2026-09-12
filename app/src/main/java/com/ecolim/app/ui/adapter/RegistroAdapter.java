package com.ecolim.app.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecolim.app.R;
import com.ecolim.app.data.model.RegistroResiduo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador RecyclerView para listado de pesajes de residuos de campo.
 * app/src/main/java/com/ecolim/app/ui/adapter/RegistroAdapter.java
 */
public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(RegistroResiduo registro);
    }

    private List<RegistroResiduo> registros = new ArrayList<>();
    private final OnItemClickListener listener;

    public RegistroAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setRegistros(List<RegistroResiduo> lista) {
        this.registros = lista != null ? lista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroResiduo item = registros.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo, tvMaterialSubtipo, tvContenedorHora, tvPeso, tvSyncBadge;
        ImageView ivIcon;

        ViewHolder(View v) {
            super(v);
            tvCodigo = v.findViewById(R.id.tv_item_codigo);
            tvMaterialSubtipo = v.findViewById(R.id.tv_item_material_subtipo);
            tvContenedorHora = v.findViewById(R.id.tv_item_contenedor_hora);
            tvPeso = v.findViewById(R.id.tv_item_peso);
            tvSyncBadge = v.findViewById(R.id.tv_item_sync_badge);
            ivIcon = v.findViewById(R.id.iv_item_categoria);
        }

        void bind(RegistroResiduo r, OnItemClickListener listener) {
            tvCodigo.setText(r.getCodigoLote());
            tvMaterialSubtipo.setText(r.getMaterial() + " · " + r.getSubtipo());
            
            String hora = (r.getFechaCreacion() != null && r.getFechaCreacion().length() > 15) 
                    ? r.getFechaCreacion().substring(11, 16) : "08:30";
            tvContenedorHora.setText("Contenedor " + r.getContenedor() + " · " + hora);
            
            tvPeso.setText(String.format(Locale.US, "%.2f kg", r.getPesoKg()));

            if (r.isSincronizado()) {
                tvSyncBadge.setText("Sincronizado");
                tvSyncBadge.setTextColor(Color.parseColor("#00513A"));
                tvSyncBadge.setBackgroundResource(R.drawable.bg_badge_sync_ok);
            } else {
                tvSyncBadge.setText("Pendiente");
                tvSyncBadge.setTextColor(Color.parseColor("#8A5100"));
                tvSyncBadge.setBackgroundResource(R.drawable.bg_badge_sync_pending);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(r);
            });
        }
    }
}
