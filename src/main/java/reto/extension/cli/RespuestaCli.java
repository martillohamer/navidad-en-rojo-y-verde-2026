package reto.extension.cli;

import java.util.Objects;

public record RespuestaCli(int codigo, String mensaje) {

    public static final int EXITO = 0;
    public static final int ERROR_USO = 1;

    public RespuestaCli {
        Objects.requireNonNull(mensaje, "El mensaje no puede ser null");
    }

    public static RespuestaCli ok(String mensaje) {
        return new RespuestaCli(EXITO, mensaje);
    }

    public static RespuestaCli error(String mensaje) {
        return new RespuestaCli(ERROR_USO, mensaje);
    }

    public boolean esExito() {
        return codigo == EXITO;
    }
}
