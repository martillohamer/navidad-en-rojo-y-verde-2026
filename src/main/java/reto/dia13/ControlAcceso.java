package reto.dia13;

public class ControlAcceso {

    // Un duende "junior" necesita nivel 3 o mas; los demas, nivel 1 o mas
    public void verificarPase(String tipoDuende, int nivelPermiso) {
        int requerido = "junior".equalsIgnoreCase(tipoDuende) ? 3 : 1;
        if (nivelPermiso < requerido) {
            throw new AccesoDenegadoException("Acceso denegado a " + tipoDuende + " con nivel " + nivelPermiso);
        }
    }
}