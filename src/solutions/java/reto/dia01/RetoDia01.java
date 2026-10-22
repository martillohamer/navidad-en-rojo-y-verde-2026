package reto.dia01;

public class RetoDia01 {

    public String saludoNavideno(String nombre) {
        String destinatario = (nombre == null || nombre.isBlank()) ? "invitado misterioso" : nombre;
        return "\u00A1Feliz Navidad, " + destinatario + "!";
    }
}