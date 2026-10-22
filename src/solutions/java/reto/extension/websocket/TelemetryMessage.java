package reto.extension.websocket;

import java.time.Instant;
import java.util.Objects;

public record TelemetryMessage(String id, String metricName, double value, Instant timestamp) {

    public TelemetryMessage {
        id = requerirTexto(id, "id");
        metricName = requerirTexto(metricName, "metricName");
        Objects.requireNonNull(timestamp, "El timestamp no puede ser null");
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("El valor no puede ser NaN ni infinito: " + value);
        }
    }

    public static TelemetryMessage ahora(String id, String metricName, double value) {
        return new TelemetryMessage(id, metricName, value, Instant.now());
    }

    private static String requerirTexto(String texto, String campo) {
        Objects.requireNonNull(texto, "El campo " + campo + " no puede ser null");
        String recortado = texto.strip();
        if (recortado.isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacio");
        }
        return recortado;
    }

    public String aJson() {
        return "{\"id\":\"" + id + "\",\"metricName\":\"" + metricName
                + "\",\"value\":" + value + ",\"timestamp\":\"" + timestamp + "\"}";
    }

    public static TelemetryMessage desdeJson(String json) {
        Objects.requireNonNull(json, "El json no puede ser null");
        return new TelemetryMessage(
                extraerTexto(json, "id"),
                extraerTexto(json, "metricName"),
                Double.parseDouble(extraerNumero(json, "value")),
                Instant.parse(extraerTexto(json, "timestamp")));
    }

    private static String extraerTexto(String json, String campo) {
        String marca = "\"" + campo + "\":\"";
        int inicio = json.indexOf(marca);
        if (inicio < 0) {
            throw new IllegalArgumentException("Falta el campo " + campo + " en: " + json);
        }
        inicio += marca.length();
        int fin = json.indexOf('"', inicio);
        if (fin < 0) {
            throw new IllegalArgumentException("Campo " + campo + " sin cierre en: " + json);
        }
        return json.substring(inicio, fin);
    }

    private static String extraerNumero(String json, String campo) {
        String marca = "\"" + campo + "\":";
        int inicio = json.indexOf(marca);
        if (inicio < 0) {
            throw new IllegalArgumentException("Falta el campo " + campo + " en: " + json);
        }
        inicio += marca.length();
        int fin = inicio;
        while (fin < json.length() && "-+.0123456789eE".indexOf(json.charAt(fin)) >= 0) {
            fin++;
        }
        return json.substring(inicio, fin);
    }
}
