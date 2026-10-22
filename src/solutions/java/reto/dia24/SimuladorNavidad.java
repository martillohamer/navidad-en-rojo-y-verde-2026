package reto.dia24;

import java.util.List;

import reto.dia17.OptimizadorRutas;
import reto.dia19.CalculadorCarga;
import reto.dia19.ExcesoCapacidadException;
import reto.dia21.AlertaTormenta;

public class SimuladorNavidad {

    // Informe consolidado de la mision de Nochebuena.
    public record InformeMision(boolean despegueAutorizado, double pesoTotal,
            int totalParadas, String estadoClimatico) {
    }

    private final double pesoMaximo;
    private final double umbralViento;

    public SimuladorNavidad(double pesoMaximo, double umbralViento) {
        this.pesoMaximo = pesoMaximo;
        this.umbralViento = umbralViento;
    }

    // Orquesta la mision: pesa la carga, calcula la ruta y revisa el clima.
    public InformeMision simular(List<CalculadorCarga.Regalo> regalos,
            List<OptimizadorRutas.Punto> ruta,
            List<AlertaTormenta.Medicion> clima) {

        double pesoTotal = sumaSegura(regalos);
        boolean cargaOk = pesoTotal <= pesoMaximo;

        int totalParadas = ruta == null ? 0 : ruta.size();
        boolean rutaOk = ruta != null && !ruta.isEmpty();

        boolean climaCritico = new AlertaTormenta(umbralViento).hayAlertaRojaGlobal(clima);
        String estadoClimatico = climaCritico ? "CRITICO" : "FAVORABLE";

        boolean autorizado = cargaOk && rutaOk && !climaCritico;
        return new InformeMision(autorizado, pesoTotal, totalParadas, estadoClimatico);
    }

    private static double sumaSegura(List<CalculadorCarga.Regalo> regalos) {
        if (regalos == null) {
            return 0.0;
        }
        double total = 0.0;
        for (CalculadorCarga.Regalo regalo : regalos) {
            total += regalo.peso();
        }
        return total;
    }
}
