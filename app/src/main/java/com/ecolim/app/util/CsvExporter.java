package com.ecolim.app.util;

import com.ecolim.app.data.model.RegistroResiduo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Exportador nativo a formato CSV para auditoría y hojas de cálculo.
 * app/src/main/java/com/ecolim/app/util/CsvExporter.java
 */
public class CsvExporter {

    public static File exportar(List<RegistroResiduo> registros, File directorioDestino) throws IOException {
        String nombreArchivo = "reporte_ecolim_" + System.currentTimeMillis() + ".csv";
        File archivo = new File(directorioDestino, nombreArchivo);

        try (FileWriter writer = new FileWriter(archivo)) {
            // Encabezados CSV
            writer.append("ID,Codigo_Lote,Material,Subtipo,Peso_KG,Contenedor,Punto_Acopio,Operador_ID,Bascula_ID,Latitud,Longitud,RFID,Sincronizado,Fecha\n");

            for (RegistroResiduo r : registros) {
                writer.append(sanitizar(r.getId())).append(",");
                writer.append(sanitizar(r.getCodigoLote())).append(",");
                writer.append(sanitizar(r.getMaterial())).append(",");
                writer.append(sanitizar(r.getSubtipo())).append(",");
                writer.append(String.valueOf(r.getPesoKg())).append(",");
                writer.append(sanitizar(r.getContenedor())).append(",");
                writer.append(sanitizar(r.getPuntoAcopio())).append(",");
                writer.append(sanitizar(r.getOperadorId())).append(",");
                writer.append(sanitizar(r.getBasculaId())).append(",");
                writer.append(r.getLatitud() != null ? String.valueOf(r.getLatitud()) : "").append(",");
                writer.append(r.getLongitud() != null ? String.valueOf(r.getLongitud()) : "").append(",");
                writer.append(sanitizar(r.getRfidTag())).append(",");
                writer.append(r.isSincronizado() ? "1" : "0").append(",");
                writer.append(sanitizar(r.getFechaCreacion())).append("\n");
            }
            writer.flush();
        }

        return archivo;
    }

    private static String sanitizar(String campo) {
        if (campo == null) return "";
        return campo.replace(",", ";").replace("\n", " ");
    }
}
