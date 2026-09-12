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
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private BarcodeScanner barcodeScanner;
    private String lastDetectedCode = "";
    private String lastMaterial = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear);

        clasificador = new ClasificadorTFLite(this);
        cameraExecutor = Executors.newSingleThreadExecutor();
        
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);

        tvCodigoDetectado = findViewById(R.id.tv_scanner_codigo);
        tvClasificacionAi = findViewById(R.id.tv_scanner_ia_label);
        btnTorch = findViewById(R.id.btn_scanner_torch);
        viewFinder = findViewById(R.id.camera_preview_fullscreen);

        tvCodigoDetectado.setText("Apuntando...");
        tvClasificacionAi.setText("IA: Esperando captura...");

        findViewById(R.id.btn_scanner_back).setOnClickListener(v -> finish());

        btnTorch.setOnClickListener(v -> {
            if (cameraControl != null) {
                isTorchOn = !isTorchOn;
                cameraControl.enableTorch(isTorchOn);
                Toast.makeText(this, isTorchOn ? "Linterna encendida" : "Linterna apagada", Toast.LENGTH_SHORT).show();
            }
        });

        MaterialButton btnContinuarPesaje = findViewById(R.id.btn_scanner_pesar);
        btnContinuarPesaje.setOnClickListener(v -> {
            if (lastDetectedCode.isEmpty() || lastMaterial.isEmpty()) {
                Toast.makeText(this, "Escanee un código para detectar material", Toast.LENGTH_SHORT).show();
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

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::processImageProxy);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis);
                
                cameraControl = camera.getCameraControl();

            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error al iniciar cámara.", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @androidx.camera.core.ExperimentalGetImage
    private void processImageProxy(ImageProxy imageProxy) {
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            barcodeScanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            if (rawValue != null && !rawValue.equals(lastDetectedCode)) {
                                lastDetectedCode = rawValue;
                                tvCodigoDetectado.setText(rawValue);
                                
                                // Al detectar el código, clasificar el material de la imagen capturada
                                Bitmap bitmap = imageProxy.toBitmap();
                                ClasificadorTFLite.Reconocimiento r = clasificador.clasificar(bitmap);
                                lastMaterial = r.getEtiqueta();
                                tvClasificacionAi.setText("IA: " + r.getEtiqueta() + " (" + (int)(r.getConfianza() * 100) + "%)");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Ignorar fallos de lectura continuos
                    })
                    .addOnCompleteListener(task -> imageProxy.close());
        } else {
            imageProxy.close();
        }
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
