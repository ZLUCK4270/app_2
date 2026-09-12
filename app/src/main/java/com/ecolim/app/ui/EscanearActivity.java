package com.ecolim.app.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Size;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ecolim.app.R;
import com.ecolim.app.util.ClasificadorTFLite;
import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;

/**
 * Actividad para escaneo visual de código de barras/RFID y clasificación TFLite.
 * app/src/main/java/com/ecolim/app/ui/EscanearActivity.java
 */
public class EscanearActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private TextView tvCodigoDetectado;
    private TextView tvClasificacionAi;
    private ImageButton btnTorch;
    private PreviewView viewFinder;
    
    private boolean isTorchOn = false;
    private ClasificadorTFLite clasificador;
    
    private ExecutorService cameraExecutor;
    private CameraControl cameraControl;
    private String lastDetectedCode = "";
    private String lastMaterial = "";
    private MaterialButton btnContinuarPesaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear);

        clasificador = new ClasificadorTFLite(this);
        cameraExecutor = Executors.newSingleThreadExecutor();
        
        tvCodigoDetectado = findViewById(R.id.tv_scanner_codigo);
        tvClasificacionAi = findViewById(R.id.tv_scanner_ia_label);
        btnTorch = findViewById(R.id.btn_scanner_torch);
        viewFinder = findViewById(R.id.camera_preview_fullscreen);
        btnContinuarPesaje = findViewById(R.id.btn_scanner_pesar);
        MaterialButton btnCapturar = findViewById(R.id.btn_scanner_capturar);

        tvCodigoDetectado.setText("Esperando Captura...");
        tvClasificacionAi.setText("Enfoque el contenedor y presione Capturar");

        btnCapturar.setOnClickListener(v -> capturarYClasificar());

        findViewById(R.id.btn_scanner_back).setOnClickListener(v -> finish());

        btnTorch.setOnClickListener(v -> {
            if (cameraControl != null) {
                isTorchOn = !isTorchOn;
                cameraControl.enableTorch(isTorchOn);
                Toast.makeText(this, isTorchOn ? "Linterna encendida" : "Linterna apagada", Toast.LENGTH_SHORT).show();
            }
        });

        btnContinuarPesaje.setOnClickListener(v -> {
            if (lastDetectedCode.isEmpty() || lastMaterial.isEmpty()) {
                Toast.makeText(this, "Debe capturar una imagen primero", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, NuevoRegistroActivity.class);
            intent.putExtra("codigo_lote", lastDetectedCode);
            intent.putExtra("material_sugerido", lastMaterial);
            startActivity(intent);
            finish();
        });

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview);
                
                cameraControl = camera.getCameraControl();

            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error al iniciar cámara.", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void capturarYClasificar() {
        Bitmap bitmap = viewFinder.getBitmap();
        if (bitmap == null) {
            Toast.makeText(this, "No se pudo obtener la imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar un lote aleatorio ya que no usamos QR
        Random rnd = new Random();
        int numLote = 10000 + rnd.nextInt(90000);
        lastDetectedCode = "LOTE-" + numLote;
        tvCodigoDetectado.setText(lastDetectedCode);

        // Clasificar la imagen capturada
        ClasificadorTFLite.Reconocimiento r = clasificador.clasificar(bitmap);
        lastMaterial = r.getEtiqueta();
        int confianza = (int)(r.getConfianza() * 100);
        
        tvClasificacionAi.setText("IA: " + lastMaterial + " (" + confianza + "% Confianza)");
        btnContinuarPesaje.setEnabled(true);
        Toast.makeText(this, "Contenedor clasificado exitosamente", Toast.LENGTH_SHORT).show();
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
