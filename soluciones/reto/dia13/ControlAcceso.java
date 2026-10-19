package reto.dia13;

public class ControlAcceso {

    public void verificarPase(String tipoDuende, int nivelPermiso) {
        if ("junior".equalsIgnoreCase(tipoDuende) && nivelPermiso < 3) {
            throw new AccesoDenegadoException("Acceso denegado para duendes junior con nivel menor a 3.");
        }
    }
}