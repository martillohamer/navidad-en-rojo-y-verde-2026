package reto.dia23;

public class CompresorRegalos {

    // Formato: cada bloque es <caracter><veces>. Para no confundir un caracter con un
    // digito, se escapa el digito y el escape con una barra invertida.
    private static final char ESCAPE = '\\';

    public String comprimir(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        StringBuilder salida = new StringBuilder();
        int i = 0;
        int n = texto.length();
        while (i < n) {
            char actual = texto.charAt(i);
            int veces = 1;
            while (i + veces < n && texto.charAt(i + veces) == actual) {
                veces++;
            }
            salida.append(escapar(actual)).append(veces);
            i += veces;
        }
        return salida.toString();
    }

    public String descomprimir(String comprimido) {
        if (comprimido == null || comprimido.isEmpty()) {
            return comprimido;
        }
        StringBuilder salida = new StringBuilder();
        int i = 0;
        int n = comprimido.length();
        while (i < n) {
            char c = comprimido.charAt(i);
            if (c == ESCAPE) {
                i++;
                if (i >= n) {
                    throw new IllegalArgumentException("Entrada comprimida invalida: escape sin caracter");
                }
                char literal = comprimido.charAt(i);
                int j = i + 1;
                int veces = leerNumero(comprimido, j);
                if (veces <= 0) {
                    throw new IllegalArgumentException("Entrada comprimida invalida: falta el numero de repeticiones");
                }
                salida.append(String.valueOf(literal).repeat(veces));
                i = j + String.valueOf(veces).length();
            } else {
                int j = i + 1;
                int veces = leerNumero(comprimido, j);
                if (veces <= 0) {
                    throw new IllegalArgumentException("Entrada comprimida invalida: falta el numero de repeticiones");
                }
                salida.append(String.valueOf(c).repeat(veces));
                i = j + String.valueOf(veces).length();
            }
        }
        return salida.toString();
    }

    private static String escapar(char c) {
        if (c == ESCAPE || (c >= '0' && c <= '9')) {
            return "" + ESCAPE + c;
        }
        return String.valueOf(c);
    }

    private static int leerNumero(String texto, int inicio) {
        int i = inicio;
        while (i < texto.length() && texto.charAt(i) >= '0' && texto.charAt(i) <= '9') {
            i++;
        }
        if (i == inicio) {
            return -1;
        }
        return Integer.parseInt(texto.substring(inicio, i));
    }
}
