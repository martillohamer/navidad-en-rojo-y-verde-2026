package reto.cli;

import java.util.List;

import reto.dia17.OptimizadorRutas;
import reto.dia19.CalculadorCarga;
import reto.dia21.AlertaTormenta;
import reto.dia24.SimuladorNavidad;

public class NorthPoleCommandCenter {

    private static final String RESET = "\u001B[0m";
    private static final String ROJO = "\u001B[31m";
    private static final String VERDE = "\u001B[32m";
    private static final String AMARILLO = "\u001B[33m";
    private static final String CIAN = "\u001B[36m";

    public static void main(String[] args) {
        banner();
        paso("Inicializando el Centro de Mando del Polo Norte...", 400);

        List<CalculadorCarga.Regalo> regalos = List.of(
                new CalculadorCarga.Regalo("tren de madera", 12.5),
                new CalculadorCarga.Regalo("muneca de trapo", 4.0),
                new CalculadorCarga.Regalo("bicicleta", 18.0),
                new CalculadorCarga.Regalo("puzzle", 2.5));

        List<OptimizadorRutas.Punto> ruta = List.of(
                new OptimizadorRutas.Punto(3, 0),
                new OptimizadorRutas.Punto(3, 4),
                new OptimizadorRutas.Punto(-2, 4));

        List<AlertaTormenta.Medicion> clima = List.of(
                new AlertaTormenta.Medicion("norte", 20.0, 10.0),
                new AlertaTormenta.Medicion("sur", 45.0, 6.0));

        paso("Pesando la carga del trineo...", 500);
        double distancia = new OptimizadorRutas().calcularDistanciaTotal(ruta);
        System.out.println(CIAN + "  Distancia total de la ruta: " + distancia + " unidades" + RESET);

        paso("Revisando el clima en los sectores...", 500);
        boolean tormenta = new AlertaTormenta(60.0).hayAlertaRojaGlobal(clima);
        System.out.println(CIAN + "  Tormenta critica detectada: "
                + (tormenta ? ROJO + "SI" : VERDE + "NO") + RESET);

        paso("Orquestando la mision de Nochebuena...", 500);
        SimuladorNavidad.InformeMision informe = new SimuladorNavidad(40.0, 60.0)
                .simular(regalos, ruta, clima);

        veredicto(informe);
    }

    private static void banner() {
        System.out.println(ROJO
                + "  ____            _   _            _       \n"
                + " |  _ \\ ___  _ __| |_| |__    ___ | | ___  \n"
                + " | |_) / _ \\| '__| __| '_ \\  / _ \\| |/ _ \\ \n"
                + " |  __/ (_) | |  | |_| | | || (_) | |  __/ \n"
                + " |_|   \\___/|_|   \\__|_| |_| \\___/|_|\\___| "
                + VERDE + "COMMAND CENTER" + RESET);
        System.out.println(AMARILLO + "  ~ Centro de Mando del Polo Norte ~ Nochebuena ~" + RESET);
        System.out.println();
    }

    private static void paso(String mensaje, long milis) {
        System.out.println(AMARILLO + "[*] " + mensaje + RESET);
        dormir(milis);
    }

    private static void veredicto(SimuladorNavidad.InformeMision informe) {
        System.out.println();
        boolean autorizado = informe.despegueAutorizado();
        String color = autorizado ? VERDE : ROJO;
        System.out.println(color
                + "   /\\   /\\        "
                + (autorizado ? ">> DESPEGUE AUTORIZADO <<" : ">> DESPEGUE ABORTADO <<")
                + RESET);
        System.out.println(color
                + "  /  \\_/  \\       Peso: " + informe.pesoTotal() + " kg"
                + RESET);
        System.out.println(color
                + " (  o   o  )      Paradas: " + informe.totalParadas()
                + RESET);
        System.out.println(color
                + "  \\  ^^  /       Clima: " + informe.estadoClimatico()
                + RESET);
        System.out.println(color
                + "   \\____/        "
                + (autorizado ? "¡Feliz Navidad!" : "Revisa los sistemas antes de volar")
                + RESET);
        System.out.println();
    }

    private static void dormir(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(ROJO + "[!] Simulacion interrumpida." + RESET);
        }
    }
}
