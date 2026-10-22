package reto.extension.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import reto.extension.caos.GrinchChaosEngine;
import reto.extension.ledger.MerkleLedger;
import reto.extension.websocket.TelemetryService;

public final class ConsolaNexus {

    public static final String PROMPT = "nexus> ";
    public static final List<String> COMANDOS =
            List.of("status", "chaos", "bft", "telemetria", "help", "exit");

    private static final String RESET = "\u001B[0m";
    private static final String VERDE = "\u001B[32m";
    private static final String ROJO = "\u001B[31m";
    private static final String CIAN = "\u001B[36m";

    private final PrintStream salida;
    private final GrinchChaosEngine caosBft;
    private final TelemetryService telemetria;

    public ConsolaNexus() {
        this(System.out, new GrinchChaosEngine(0.0), TelemetryService.conSemilla(2026L));
    }

    public ConsolaNexus(PrintStream salida, GrinchChaosEngine caosBft, TelemetryService telemetria) {
        this.salida = Objects.requireNonNull(salida, "La salida no puede ser null");
        this.caosBft = Objects.requireNonNull(caosBft, "El motor BFT no puede ser null");
        this.telemetria = Objects.requireNonNull(telemetria, "El servicio de telemetria no puede ser null");
    }

    public static ConsolaNexus conSemilla(long semilla, PrintStream salida) {
        return new ConsolaNexus(salida,
                new GrinchChaosEngine(0.0, GrinchChaosEngine.PoliticaCaos.limpia(), new Random(semilla)),
                TelemetryService.conSemilla(semilla));
    }

    public RespuestaCli ejecutar(String linea) {
        ComandoCli comando;
        try {
            comando = ComandoCli.parsear(linea);
        } catch (IllegalArgumentException e) {
            return RespuestaCli.error(e.getMessage());
        }
        return despachar(comando);
    }

    public RespuestaCli despachar(ComandoCli comando) {
        return switch (comando.nombre()) {
            case "status" -> status(comando.argumentos());
            case "chaos" -> chaos(comando.argumentos());
            case "bft" -> bft(comando.argumentos());
            case "telemetria" -> telemetria(comando.argumentos());
            case "help" -> help();
            case "exit" -> RespuestaCli.ok("Cerrando la consola del Polo Norte.");
            default -> RespuestaCli.error("Comando desconocido: " + comando.nombre()
                    + ". Prueba 'help'.");
        };
    }

    private RespuestaCli status(ArgumentosCli argumentos) {
        int agentes = argumentos.opcionEntera("agentes", 5);
        if (agentes <= 0) {
            return RespuestaCli.error("El numero de agentes debe ser positivo: " + agentes);
        }
        StringBuilder sb = new StringBuilder(CIAN + "=== Estado de la red P2P ===" + RESET + "\n");
        for (int i = 1; i <= agentes; i++) {
            sb.append("  agente-").append(i).append(": ")
                    .append(VERDE).append("EN LINEA").append(RESET)
                    .append(" | latencia=").append(10 + i * 3).append("ms\n");
        }
        sb.append("  total=").append(agentes).append(" agentes operativos");
        String mensaje = sb.toString();
        salida.println(mensaje);
        return RespuestaCli.ok(mensaje);
    }

    private RespuestaCli chaos(ArgumentosCli argumentos) {
        int nodos = argumentos.opcionEntera("nodos", 5);
        int requeridos = argumentos.opcionEntera("requeridos", Math.max(1, (nodos * 2) / 3 + 1));
        try {
            GrinchChaosEngine.ResultadoConsenso<String> resultado = caosBft.alcanzarConsenso(
                    nodos, requeridos,
                    () -> "paquete-de-regalos-verificado",
                    "sin-consenso");
            String estado = resultado.consenso() ? VERDE + "CONSENSO ALCANZADO" + RESET
                    : ROJO + "SIN CONSENSO (BFT FALLIDO)" + RESET;
            String mensaje = "=== Motor de caos del Grinch ===\n"
                    + "  nodos=" + nodos + " requeridos=" + requeridos + "\n"
                    + "  disponibles=" + resultado.nodosDisponibles() + "\n"
                    + "  valor=" + resultado.valor() + "\n"
                    + "  " + estado;
            salida.println(mensaje);
            return RespuestaCli.ok(mensaje);
        } catch (IllegalArgumentException e) {
            return RespuestaCli.error(e.getMessage());
        }
    }

    private RespuestaCli bft(ArgumentosCli argumentos) {
        List<MerkleLedger.Transaccion> transacciones = List.of(
                new MerkleLedger.Transaccion("regalo-001", "madrid", 1.5),
                new MerkleLedger.Transaccion("regalo-002", "tokio", 3.2),
                new MerkleLedger.Transaccion("regalo-003", "oslo", 0.8));
        String raiz = MerkleLedger.calcularMerkleRoot(transacciones);
        boolean valido = MerkleLedger.verificar(transacciones, raiz);
        String estado = valido ? VERDE + "LEDGER INTEGRO" + RESET : ROJO + "LEDGER CORRUPTO" + RESET;
        String mensaje = "=== Verificacion del ledger de Merkle ===\n"
                + "  transacciones=" + transacciones.size() + "\n"
                + "  raiz=" + raiz + "\n"
                + "  " + estado;
        salida.println(mensaje);
        return RespuestaCli.ok(mensaje);
    }

    private RespuestaCli telemetria(ArgumentosCli argumentos) {
        int muestras = argumentos.opcionEntera("n", 3);
        if (muestras <= 0) {
            return RespuestaCli.error("El numero de muestras debe ser positivo: " + muestras);
        }
        StringBuilder sb = new StringBuilder("=== Telemetria de agentes ===\n");
        for (int i = 0; i < muestras; i++) {
            var m = telemetria.siguiente();
            sb.append("  [").append(m.id()).append("] ")
                    .append(m.metricName()).append("=").append(m.value()).append("\n");
        }
        String mensaje = sb.toString();
        salida.println(mensaje);
        return RespuestaCli.ok(mensaje);
    }

    private RespuestaCli help() {
        String mensaje = "=== Manual de la Consola Nexus ===\n"
                + "  status [--agentes=N]           estado de los agentes/nodos P2P\n"
                + "  chaos [--nodos=N] [--requeridos=M]  inyecta caos y valida consenso BFT\n"
                + "  bft                            verifica el ledger de Merkle\n"
                + "  telemetria [--n=N]             lee N muestras de telemetria\n"
                + "  help                           muestra esta ayuda\n"
                + "  exit                           cierra la consola";
        salida.println(mensaje);
        return RespuestaCli.ok(mensaje);
    }

    public void bucle(InputStream entrada) throws IOException {
        BufferedReader lector = new BufferedReader(
                new InputStreamReader(entrada, StandardCharsets.UTF_8));
        salida.println("Consola Nexus - Polo Norte. Escribe 'help' para ver los comandos.");
        salida.print(PROMPT);
        salida.flush();
        String linea;
        while ((linea = lector.readLine()) != null) {
            if (linea.isBlank()) {
                salida.print(PROMPT);
                salida.flush();
                continue;
            }
            ComandoCli comando;
            try {
                comando = ComandoCli.parsear(linea);
            } catch (IllegalArgumentException e) {
                salida.println(e.getMessage());
                salida.print(PROMPT);
                salida.flush();
                continue;
            }
            despachar(comando);
            if ("exit".equals(comando.nombre())) {
                return;
            }
            salida.print(PROMPT);
            salida.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        new ConsolaNexus().bucle(System.in);
    }
}
