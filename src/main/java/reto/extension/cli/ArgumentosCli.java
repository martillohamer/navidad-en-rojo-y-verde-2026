package reto.extension.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record ArgumentosCli(List<String> posicionales, Map<String, String> opciones) {

    public ArgumentosCli {
        posicionales = List.copyOf(Objects.requireNonNull(posicionales, "posicionales"));
        Objects.requireNonNull(opciones, "opciones");
        opciones = Collections.unmodifiableMap(new LinkedHashMap<>(opciones));
    }

    public static ArgumentosCli parsear(String linea) {
        Objects.requireNonNull(linea, "La linea no puede ser null");
        List<String> posicionales = new ArrayList<>();
        Map<String, String> opciones = new LinkedHashMap<>();
        for (String token : linea.strip().split("\\s+")) {
            if (token.isBlank()) {
                continue;
            }
            if (token.startsWith("--")) {
                String cuerpo = token.substring(2);
                int igual = cuerpo.indexOf('=');
                if (igual < 0) {
                    opciones.put(cuerpo, "true");
                } else {
                    opciones.put(cuerpo.substring(0, igual), cuerpo.substring(igual + 1));
                }
            } else {
                posicionales.add(token);
            }
        }
        return new ArgumentosCli(posicionales, opciones);
    }

    public static ArgumentosCli de(String... tokens) {
        return parsear(String.join(" ", tokens));
    }

    public Optional<String> opcion(String clave) {
        return Optional.ofNullable(opciones.get(clave));
    }

    public int opcionEntera(String clave, int porDefecto) {
        String valor = opciones.get(clave);
        if (valor == null) {
            return porDefecto;
        }
        try {
            return Integer.parseInt(valor.strip());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La opcion --" + clave + " espera un entero: " + valor);
        }
    }

    public double opcionDecimal(String clave, double porDefecto) {
        String valor = opciones.get(clave);
        if (valor == null) {
            return porDefecto;
        }
        try {
            return Double.parseDouble(valor.strip());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La opcion --" + clave + " espera un decimal: " + valor);
        }
    }

    public boolean bandera(String clave) {
        return opciones.containsKey(clave);
    }

    public int tamano() {
        return posicionales.size() + opciones.size();
    }
}
