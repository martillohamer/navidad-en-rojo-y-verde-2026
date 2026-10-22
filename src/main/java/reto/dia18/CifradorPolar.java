package reto.dia18;

public class CifradorPolar {

    // Alfabeto de referencia: el desplazamiento gira sobre estas 26 letras.
    private static final int TAMANO_ALFABETO = 26;
    private static final char PRIMERA_MINUSCULA = 'a';
    private static final char PRIMERA_MAYUSCULA = 'A';

    // Cifra: avanza cada letra "desplazamiento" posiciones dentro de su alfabeto.
    public String cifrar(String texto, int desplazamiento) {
        return transformar(texto, desplazamiento);
    }

    // Descifra: el camino de vuelta, avanzando el signo contrario.
    public String descifrar(String texto, int desplazamiento) {
        return transformar(texto, -desplazamiento);
    }

    private String transformar(String texto, int desplazamiento) {
        if (texto == null) {
            return null;
        }
        // Normaliza para admitir desplazamientos enormes o negativos.
        int giro = ((desplazamiento % TAMANO_ALFABETO) + TAMANO_ALFABETO) % TAMANO_ALFABETO;
        StringBuilder resultado = new StringBuilder(texto.length());
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c >= PRIMERA_MINUSCULA && c <= PRIMERA_MINUSCULA + TAMANO_ALFABETO - 1) {
                resultado.append(rotar(c, giro, PRIMERA_MINUSCULA));
            } else if (c >= PRIMERA_MAYUSCULA && c <= PRIMERA_MAYUSCULA + TAMANO_ALFABETO - 1) {
                resultado.append(rotar(c, giro, PRIMERA_MAYUSCULA));
            } else {
                // Espacios, digitos y signos viajan sin tocar.
                resultado.append(c);
            }
        }
        return resultado.toString();
    }

    private char rotar(char c, int giro, char base) {
        int posicion = c - base;
        int nueva = (posicion + giro) % TAMANO_ALFABETO;
        return (char) (base + nueva);
    }
}
