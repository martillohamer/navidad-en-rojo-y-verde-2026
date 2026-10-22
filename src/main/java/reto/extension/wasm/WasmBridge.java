package reto.extension.wasm;

import java.util.ArrayList;
import java.util.List;

public final class WasmBridge {

    // Contrato de evento intercambiable con el entorno Wasm: tipo y payload de texto.
    public record WasmEvento(String tipo, String payload) {
        public WasmEvento {
            if (tipo == null || tipo.isBlank()) {
                throw new IllegalArgumentException("El tipo de evento no puede estar vacio");
            }
            payload = payload == null ? "" : payload;
        }
    }

    private WasmBridge() {
    }

    // Suma los pesos de dos lotes de regalos. Logica pura, sin dependencias de JVM.
    public static double sumarPesos(double[] a, double[] b) {
        double total = 0.0;
        total += sumar(a);
        total += sumar(b);
        return total;
    }

    // Cuenta cuantos nodos alcanzan consenso segun votos afirmativos y umbral BFT.
    public static int nodosConConsenso(boolean[] votos, int minimoRequerido) {
        if (votos == null || minimoRequerido <= 0) {
            return 0;
        }
        int afirmativos = 0;
        for (boolean voto : votos) {
            if (voto) {
                afirmativos++;
            }
        }
        return afirmativos >= minimoRequerido ? afirmativos : 0;
    }

    // Determina si hay consenso BFT: al menos 2/3 de votos afirmativos.
    public static boolean hayConsensoBft(boolean[] votos) {
        if (votos == null || votos.length == 0) {
            return false;
        }
        int afirmativos = 0;
        for (boolean voto : votos) {
            if (voto) {
                afirmativos++;
            }
        }
        return afirmativos * 3 >= votos.length * 2;
    }

    // Hash determinista y ligero (FNV-1a de 64 bits) apto para entornos Wasm sin crypto.
    public static long hashLigero(String texto) {
        if (texto == null) {
            return 0L;
        }
        long hash = 0xcbf29ce484222325L;
        for (int i = 0; i < texto.length(); i++) {
            hash ^= texto.charAt(i);
            hash *= 0x100000001b3L;
        }
        return hash;
    }

    // Serializa un lote de regalos a un payload de texto estable para el entorno Wasm.
    public static String serializarRegalos(List<String> regalos) {
        if (regalos == null || regalos.isEmpty()) {
            return "";
        }
        return String.join("|", regalos);
    }

    // Reconstruye el lote de regalos desde un payload serializado.
    public static List<String> deserializarRegalos(String payload) {
        if (payload == null || payload.isEmpty()) {
            return List.of();
        }
        return List.of(payload.split("\\|", -1));
    }

    // Construye un evento Wasm serializado a partir de tipo y datos.
    public static WasmEvento crearEvento(String tipo, String... datos) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de evento no puede estar vacio");
        }
        List<String> limpios = new ArrayList<>();
        if (datos != null) {
            for (String dato : datos) {
                limpios.add(dato == null ? "" : dato);
            }
        }
        return new WasmEvento(tipo, String.join(";", limpios));
    }

    // Serializa un evento a la representacion textual "tipo:payload".
    public static String serializarEvento(WasmEvento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        return evento.tipo() + ":" + evento.payload();
    }

    // Reconstruye un evento desde su representacion textual "tipo:payload".
    public static WasmEvento deserializarEvento(String texto) {
        if (texto == null || texto.isEmpty()) {
            throw new IllegalArgumentException("El texto del evento no puede estar vacio");
        }
        int separador = texto.indexOf(':');
        if (separador <= 0) {
            throw new IllegalArgumentException("Formato de evento invalido: " + texto);
        }
        return new WasmEvento(texto.substring(0, separador), texto.substring(separador + 1));
    }

    // Comprueba si una cadena puede usarse como payload Wasm (ASCII imprimible).
    public static boolean esPayloadSeguro(String texto) {
        if (texto == null) {
            return false;
        }
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c < 0x20 || c > 0x7E) {
                return false;
            }
        }
        return true;
    }

    private static double sumar(double[] valores) {
        if (valores == null) {
            return 0.0;
        }
        double total = 0.0;
        for (double v : valores) {
            total += v;
        }
        return total;
    }
}
