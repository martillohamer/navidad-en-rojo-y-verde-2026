package reto.extension.cli;

import java.util.Objects;

public record ComandoCli(String nombre, ArgumentosCli argumentos) {

    public ComandoCli {
        Objects.requireNonNull(nombre, "El nombre del comando no puede ser null");
        nombre = nombre.strip().toLowerCase();
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del comando no puede estar vacio");
        }
        Objects.requireNonNull(argumentos, "Los argumentos no pueden ser null");
    }

    public static ComandoCli parsear(String linea) {
        ArgumentosCli argumentos = ArgumentosCli.parsear(linea);
        if (argumentos.posicionales().isEmpty()) {
            throw new IllegalArgumentException("No se indico ningun comando");
        }
        String nombre = argumentos.posicionales().get(0);
        ArgumentosCli resto = new ArgumentosCli(
                argumentos.posicionales().subList(1, argumentos.posicionales().size()),
                argumentos.opciones());
        return new ComandoCli(nombre, resto);
    }
}
