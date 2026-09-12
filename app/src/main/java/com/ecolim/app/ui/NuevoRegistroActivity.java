package com.ecolim.app.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ecolim.app.R;
import com.ecolim.app.data.db.dao.RegistroDao;
import com.ecolim.app.data.model.RegistroResiduo;
import com.ecolim.app.util.DateUtils;
import com.ecolim.app.util.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.util.Locale;

/**
 * Actividad para pesaje de lote, tara a cero e inserción en SQLite local.
 * app/src/main/java/com/ecolim/app/ui/NuevoRegistroActivity.java
 */
public class NuevoRegistroActivity extends AppCompatActivity {

    private EditText etPeso;
    private TextView tvLote;
    private ChipGroup chipGroup;
    private RegistroDao registroDao;
    private SessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_registro);

        registroDao = new RegistroDao(this);
        session = new SessionManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_nuevo);
        toolbar.setNavigationOnClickListener(v -> finish());

        etPeso = findViewById(R.id.et_peso_manual);
        tvLote = findViewById(R.id.tv_detected_code);
        chipGroup = findViewById(R.id.chip_group_materiales);

        String codigoDetectado = getIntent().getStringExtra("codigo_lote");
        if (codigoDetectado != null && !codigoDetectado.isEmpty()) {
            tvLote.setText("Contenedor: " + codigoDetectado);
        }

        // Tara a Cero
        findViewById(R.id.btn_tarar_cero).setOnClickListener(v -> etPeso.setText("0.00"));

        // Atajos de incremento
        findViewById(R.id.btn_inc_05).setOnClickListener(v -> sumarPeso(0.5));
        findViewById(R.id.btn_inc_10).setOnClickListener(v -> sumarPeso(1.0));
        findViewById(R.id.btn_inc_50).setOnClickListener(v -> sumarPeso(5.0));
        findViewById(R.id.btn_inc_100).setOnClickListener(v -> sumarPeso(10.0));

        MaterialButton btnGuardar = findViewById(R.id.btn_guardar_registro);
        MaterialButton btnGuardarOtro = findViewById(R.id.btn_guardar_otro);

        btnGuardar.setOnClickListener(v -> {
            if (guardar()) {
                finish();
            }
        });

        btnGuardarOtro.setOnClickListener(v -> {
            if (guardar()) {
                etPeso.setText("0.00");
                Toast.makeText(this, "Listo para siguiente pesaje", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sumarPeso(double delta) {
        try {
            double val = Double.parseDouble(etPeso.getText().toString());
            etPeso.setText(String.format(Locale.US, "%.2f", val + delta));
        } catch (Exception e) {
            etPeso.setText(String.format(Locale.US, "%.2f", delta));
        }
    }

    private boolean guardar() {
        String textoPeso = etPeso.getText().toString().trim();
        if (textoPeso.isEmpty()) {
            Toast.makeText(this, "Ingrese un peso válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        double peso = Double.parseDouble(textoPeso);
        if (peso <= 0) {
            Toast.makeText(this, "El peso debe ser mayor a 0 kg", Toast.LENGTH_SHORT).show();
            return false;
        }

        String material = "Plástico";
        int checkedId = chipGroup.getCheckedChipId();
        if (checkedId == R.id.chip_carton) material = "Cartón";
        else if (checkedId == R.id.chip_vidrio) material = "Vidrio";
        else if (checkedId == R.id.chip_metal) material = "Metal";
        else if (checkedId == R.id.chip_organico) material = "Orgánico";

        long ts = System.currentTimeMillis();
        String id = "REG-" + (ts / 1000);
        String codigoLote = "ECO-2026-" + (ts % 10000);

        RegistroResiduo reg = new RegistroResiduo(
                id,
                codigoLote,
                material,
                "Lote Reciclable Directo",
                peso,
                "C-04",
                "Punto Acopio Norte",
                session.getOperadorId(),
                "B-04",
                -12.0463,
                -77.0427,
                "RFID-" + (ts % 99999),
                null,
                false, // Sincronizado = false (offline-first)
                DateUtils.ahora()
        );

        registroDao.insertar(reg);
        Toast.makeText(this, "Pesaje " + codigoLote + " (" + peso + " kg) guardado en SQLite", Toast.LENGTH_SHORT).show();
        return true;
    }
}
