package com.ecolim.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasificador TensorFlow Lite para reconocimiento automático de material.
 * Utiliza el modelo mobilenet_v1_1.0_224_quant.tflite
 * app/src/main/java/com/ecolim/app/util/ClasificadorTFLite.java
 */
public class ClasificadorTFLite {

    private static final String MODEL_NAME = "mobilenet_v1_1.0_224_quant.tflite";
    private static final String LABEL_NAME = "labels_mobilenet_quant_v1_224.txt";
    private Interpreter interpreter;
    private List<String> labels;

    public static class Reconocimiento {
        private final String etiqueta;
        private final float confianza;

        public Reconocimiento(String etiqueta, float confianza) {
            this.etiqueta = etiqueta;
            this.confianza = confianza;
        }

        public String getEtiqueta() { return etiqueta; }
        public float getConfianza() { return confianza; }
    }

    private final Context context;

    public ClasificadorTFLite(Context context) {
        this.context = context;
        setupClassifier();
    }

    private void setupClassifier() {
        try {
            MappedByteBuffer modelBuffer = loadModelFile(MODEL_NAME);
            Interpreter.Options options = new Interpreter.Options();
            interpreter = new Interpreter(modelBuffer, options);
            labels = loadLabels(LABEL_NAME);
        } catch (IOException e) {
            Log.e("ClasificadorTFLite", "Error al cargar el modelo TFLite", e);
        }
    }

    private MappedByteBuffer loadModelFile(String modelPath) throws IOException {
        android.content.res.AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabels(String labelPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(labelPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    /**
     * Clasifica un fotograma de la cámara y retorna la predicción de material.
     */
    public Reconocimiento clasificar(Bitmap bitmap) {
        if (interpreter == null || bitmap == null || labels == null) {
            return new Reconocimiento("Desconocido", 0.0f);
        }

        // Preprocesar imagen
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .build();

        TensorImage tensorImage = new TensorImage(DataType.UINT8);
        tensorImage.load(bitmap);
        tensorImage = imageProcessor.process(tensorImage);

        // Crear buffer de salida
        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 1001}, DataType.UINT8);

        // Ejecutar inferencia
        interpreter.run(tensorImage.getBuffer(), outputBuffer.getBuffer());

        // Obtener la probabilidad más alta
        int maxIndex = -1;
        float maxProb = -1f;

        // El modelo UINT8 devuelve valores de 0 a 255, debemos dividirlos entre 255.0f para obtener la probabilidad
        int[] intProbabilities = outputBuffer.getIntArray();
        for (int i = 0; i < intProbabilities.length; i++) {
            float prob = (intProbabilities[i] & 0xFF) / 255.0f;
            if (prob > maxProb) {
                maxProb = prob;
                maxIndex = i;
            }
        }

        if (maxIndex != -1 && maxIndex < labels.size()) {
            String label = labels.get(maxIndex);
            String categoriaEcolim = mapearA_Ecolim(label);
            return new Reconocimiento(categoriaEcolim + " (" + label + ")", maxProb);
        }

        return new Reconocimiento("No identificado", 0.0f);
    }

    private String mapearA_Ecolim(String label) {
        String lower = label.toLowerCase();
        if (lower.contains("bottle") || lower.contains("plastic") || lower.contains("cup")) {
            return "Plástico";
        } else if (lower.contains("box") || lower.contains("carton") || lower.contains("paper")) {
            return "Cartón";
        } else if (lower.contains("glass") || lower.contains("wine") || lower.contains("beer")) {
            return "Vidrio";
        } else if (lower.contains("can") || lower.contains("metal") || lower.contains("tin")) {
            return "Metal";
        } else if (lower.contains("fruit") || lower.contains("food") || lower.contains("plant") || lower.contains("apple") || lower.contains("banana") || lower.contains("orange")) {
            return "Orgánico";
        }
        return "Plástico"; // Fallback por defecto
    }
}
