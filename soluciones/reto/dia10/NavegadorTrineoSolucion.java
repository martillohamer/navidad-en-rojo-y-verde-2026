package reto.dia10;

public class NavegadorTrineo {

    public Posicion calcularDestino(String movimientos) {
        int x = 0;
        int y = 0;
        if (movimientos == null || movimientos.isEmpty()) {
            return new Posicion(x, y);
        }
        String[] pasos = movimientos.split(",");
        for (String paso : pasos) {
            switch (paso.trim().toLowerCase()) {
                case "norte": y++; break;
                case "sur": y--; break;
                case "este": x++; break;
                case "oeste": x--; break;
            }
        }
        return new Posicion(x, y);
    }
}