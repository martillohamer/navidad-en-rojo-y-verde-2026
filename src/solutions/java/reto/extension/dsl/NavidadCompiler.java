package reto.extension.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NavidadCompiler {

    // Una instruccion compilada del DSL: regalo, prioridad y destino.
    public record InstruccionRegalo(String regalo, String prioridad, String destino) {
    }

    // Prioridades admitidas por el lenguaje.
    public static final Set<String> PRIORIDADES = Set.of("ALTA", "MEDIA", "BAJA");

    // Gramatica: REGALO "nombre" [Prioridad: NIVEL] -> DESTINO: Ciudad
    private static final Pattern PATRON = Pattern.compile(
            "^REGALO\\s+\"([^\"]+)\"\\s+\\[Prioridad:\\s*(ALTA|MEDIA|BAJA)\\]\\s*->\\s*DESTINO:\\s*(.+?)\\s*$");

    // Compila un unico script en una instruccion; lanza excepcion si la sintaxis es invalida.
    public InstruccionRegalo compilar(String linea) {
        if (linea == null || linea.isBlank()) {
            throw new SintaxisNavidadException("La instruccion esta vacia");
        }
        Matcher matcher = PATRON.matcher(linea.trim());
        if (!matcher.matches()) {
            throw new SintaxisNavidadException("Sintaxis invalida: " + linea);
        }
        String regalo = matcher.group(1);
        String prioridad = matcher.group(2);
        String destino = matcher.group(3).trim();
        if (regalo.isBlank() || destino.isBlank()) {
            throw new SintaxisNavidadException("Regalo o destino vacios: " + linea);
        }
        return new InstruccionRegalo(regalo, prioridad, destino);
    }

    // Compila un script multilinea ignorando lineas vacias.
    public List<InstruccionRegalo> compilarScript(String script) {
        if (script == null) {
            throw new SintaxisNavidadException("El script no puede ser nulo");
        }
        List<InstruccionRegalo> instrucciones = new ArrayList<>();
        for (String linea : script.split("\\R")) {
            if (!linea.isBlank()) {
                instrucciones.add(compilar(linea));
            }
        }
        return instrucciones;
    }

    // Indica si una linea tiene sintaxis valida sin lanzar excepcion.
    public boolean esValida(String linea) {
        return linea != null && PATRON.matcher(linea.trim()).matches();
    }

    // Excepcion propia para errores de sintaxis del DSL.
    public static class SintaxisNavidadException extends RuntimeException {
        public SintaxisNavidadException(String mensaje) {
            super(mensaje);
        }
    }
}
