package reto.dia10;

public class NavegadorTrineo {

    public Posicion calcularDestino(String movimientos) {
        int x = 0;
        int y = 0;
        if (movimientos != null && !movimientos.isBlank()) {
            for (String paso : movimientos.split(",")) {
                switch (paso.trim().toLowerCase()) {
                    case "norte" -> y++;
                    case "sur" -> y--;
                    case "este" -> x++;
                    case "oeste" -> x--;
                    default -> { }
                }
            }
        }
        return new Posicion(x, y);
    }
}