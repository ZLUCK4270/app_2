package com.ecolim.app.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ecolim.app.data.model.ResumenTipo;

import java.util.ArrayList;
import java.util.List;

/**
 * Vista personalizada en Java para dibujar gráficas de barras de distribución de residuos.
 * app/src/main/java/com/ecolim/app/ui/view/BarChartView.java
 */
public class BarChartView extends View {

    private final Paint paintBarra = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectBarra = new RectF();
    private List<ResumenTipo> datos = new ArrayList<>();

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintTexto.setColor(Color.parseColor("#191C1A"));
        paintTexto.setTextSize(32f);
    }

    public void setDatos(List<ResumenTipo> datos) {
        this.datos = datos != null ? datos : new ArrayList<>();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (datos.isEmpty()) return;

        float anchoTotal = getWidth();
        float altoTotal = getHeight();
        float padding = 40f;
        float altoDisponible = altoTotal - padding * 2;
        float anchoDisponible = anchoTotal - padding * 2;

        float anchoBarra = anchoDisponible / (datos.size() * 2f);
        float espacio = anchoBarra;

        double maxKg = 1.0;
        for (ResumenTipo r : datos) {
            if (r.getTotalKg() > maxKg) maxKg = r.getTotalKg();
        }

        float xActual = padding + espacio / 2f;

        for (ResumenTipo r : datos) {
            float alturaBarra = (float) ((r.getTotalKg() / maxKg) * (altoDisponible - 60f));
            float top = altoTotal - padding - alturaBarra - 40f;
            float bottom = altoTotal - padding - 40f;

            try {
                paintBarra.setColor(Color.parseColor(r.getColorHex()));
            } catch (Exception e) {
                paintBarra.setColor(Color.parseColor("#006D38"));
            }

            rectBarra.set(xActual, top, xActual + anchoBarra, bottom);
            canvas.drawRoundRect(rectBarra, 12f, 12f, paintBarra);

            // Etiqueta de material
            paintTexto.setTextSize(26f);
            paintTexto.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(r.getMaterial().substring(0, Math.min(3, r.getMaterial().length())),
                    xActual + anchoBarra / 2f, altoTotal - padding, paintTexto);

            // Valor en kg
            paintTexto.setTextSize(22f);
            canvas.drawText(String.format("%.1f", r.getTotalKg()),
                    xActual + anchoBarra / 2f, top - 10f, paintTexto);

            xActual += anchoBarra + espacio;
        }
    }
}
