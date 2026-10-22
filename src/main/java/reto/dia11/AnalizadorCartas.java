package reto.dia11;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorCartas {

    private static final Pattern CODIGO_POSTAL = Pattern.compile("\\b\\d{5}\\b");

    public String extraerCodigoPostal(String texto) {
        Matcher m = CODIGO_POSTAL.matcher(texto);
        return m.find() ? m.group() : null;
    }
}