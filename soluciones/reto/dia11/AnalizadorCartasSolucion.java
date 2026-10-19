package reto.dia11;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorCartasSolucion {
    public String extraerCodigoPostal(String texto) {
        if (texto == null) return null;
        Pattern pattern = Pattern.compile("\\b\\d{5}\\b");
        Matcher matcher = pattern.matcher(texto);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}