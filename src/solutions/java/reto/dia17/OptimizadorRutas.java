package reto.dia17;

import java.util.List;

public class OptimizadorRutas {

    // Un punto del mapa. Un record genera constructor, getters, equals y hashCode.
    public record Punto(int x, int y) {

        // Distancia euclidiana entre este punto y otro: raiz de ((dx)^2 + (dy)^2).
        public double distanciaA(Punto otro) {
            int dx = this.x - otro.x;
            int dy = this.y - otro.y;
            return Math.sqrt((double) dx * dx + (double) dy * dy);
        }
    }

    // Trayecto completo: sale de (0,0), visita cada parada en orden y vuelve a (0,0).
    public double calcularDistanciaTotal(List<Punto> paradas) {
        if (paradas == null || paradas.isEmpty()) {
            return 0.0;
        }
        Punto origen = new Punto(0, 0);
        Punto actual = origen;
        double total = 0.0;
        for (Punto parada : paradas) {
            total += actual.distanciaA(parada);
            actual = parada;
        }
        total += actual.distanciaA(origen);
        return total;
    }
}
