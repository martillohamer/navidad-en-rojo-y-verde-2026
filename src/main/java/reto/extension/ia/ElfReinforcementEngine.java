package reto.extension.ia;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import reto.extension.caos.GrinchChaosEngine;

public class ElfReinforcementEngine {

    // Estado del agente elfo: numero de intento de entrega y fallos recientes.
    public record Estado(int intento, int fallosRecientes) {
        public Estado {
            if (intento < 0) {
                throw new IllegalArgumentException("El intento no puede ser negativo: " + intento);
            }
            if (fallosRecientes < 0) {
                throw new IllegalArgumentException("Los fallos recientes no pueden ser negativos: " + fallosRecientes);
            }
        }
    }

    // Accion (politica) candidata: cuantos reintentos usar y si enrutar por P2P.
    public record Accion(String id, int reintentos, boolean rutaP2P) {
        public Accion {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("El id de la accion no puede estar vacio");
            }
            if (reintentos < 0) {
                throw new IllegalArgumentException("Los reintentos no pueden ser negativos: " + reintentos);
            }
        }
    }

    // Resultado de un episodio de aprendizaje.
    public record Episodio(Estado estado, Accion accion, double recompensa, boolean exito, double qActualizado) {
    }

    // Recompensas configurables (inmutables).
    public record Recompensas(double exitoLimpio, double exitoConRecuperacion, double penalizacionFallo) {
        public static Recompensas porDefecto() {
            return new Recompensas(10.0, 4.0, -8.0);
        }
    }

    private final List<Accion> acciones;
    private final double learningRate;
    private final double discountFactor;
    private final double epsilonInicial;
    private final double decaimientoEpsilon;
    private final Recompensas recompensas;
    private final Random aleatorio;
    private final Map<Estado, Map<Accion, Double>> tablaQ = new HashMap<>();
    private double epsilon;

    public ElfReinforcementEngine(List<Accion> acciones) {
        this(acciones, 0.3, 0.9, 0.5, 0.99, Recompensas.porDefecto(), new Random(0));
    }

    public ElfReinforcementEngine(List<Accion> acciones, double learningRate, double discountFactor,
            double epsilonInicial, double decaimientoEpsilon, Recompensas recompensas, Random aleatorio) {
        if (acciones == null || acciones.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos una accion");
        }
        validarRango("learningRate", learningRate, 0.0, 1.0);
        validarRango("discountFactor", discountFactor, 0.0, 1.0);
        validarRango("epsilonInicial", epsilonInicial, 0.0, 1.0);
        validarRango("decaimientoEpsilon", decaimientoEpsilon, 0.0, 1.0);
        this.acciones = List.copyOf(acciones);
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.epsilonInicial = epsilonInicial;
        this.decaimientoEpsilon = decaimientoEpsilon;
        this.epsilon = epsilonInicial;
        this.recompensas = Objects.requireNonNull(recompensas, "Las recompensas no pueden ser nulas");
        this.aleatorio = Objects.requireNonNull(aleatorio, "La fuente aleatoria no puede ser nula");
    }

    // Elige una accion con politica epsilon-greedy: explora o explota segun epsilon.
    public Accion elegirAccion(Estado estado) {
        Objects.requireNonNull(estado, "El estado no puede ser nulo");
        if (aleatorio.nextDouble() < epsilon) {
            return acciones.get(aleatorio.nextInt(acciones.size()));
        }
        return politicaRecomendada(estado);
    }

    // Devuelve la accion con mayor valor Q para el estado (explotacion pura, determinista).
    public Accion politicaRecomendada(Estado estado) {
        Objects.requireNonNull(estado, "El estado no puede ser nulo");
        return acciones.stream()
                .max(Comparator.comparingDouble(a -> valorQ(estado, a)))
                .orElse(acciones.get(0));
    }

    // Valor Q de un par (estado, accion). 0.0 si aun no se ha visitado.
    public double valorQ(Estado estado, Accion accion) {
        Map<Accion, Double> fila = tablaQ.get(estado);
        if (fila == null) {
            return 0.0;
        }
        return fila.getOrDefault(accion, 0.0);
    }

    // Calcula la recompensa a partir del resultado del motor de caos.
    public double calcularRecompensa(GrinchChaosEngine.ResultadoCaos<?> resultado) {
        Objects.requireNonNull(resultado, "El resultado no puede ser nulo");
        if (!resultado.recuperado()) {
            return recompensas.exitoLimpio();
        }
        boolean falloRed = resultado.fallosInyectados().contains(GrinchChaosEngine.FaultType.DESCONEXION_NODO)
                || resultado.fallosInyectados().contains(GrinchChaosEngine.FaultType.CORRUPCION_PAQUETE);
        return falloRed ? recompensas.penalizacionFallo() : recompensas.exitoConRecuperacion();
    }

    // Ajusta la tabla Q con la recompensa observada y devuelve el episodio.
    public Episodio ajustar(Estado estado, Accion accion, GrinchChaosEngine.ResultadoCaos<?> resultado) {
        Objects.requireNonNull(estado, "El estado no puede ser nulo");
        Objects.requireNonNull(accion, "La accion no puede ser nula");
        double recompensa = calcularRecompensa(resultado);
        double qViejo = valorQ(estado, accion);
        double qNuevo = qViejo + learningRate * (recompensa - qViejo);
        tablaQ.computeIfAbsent(estado, k -> new HashMap<>()).put(accion, qNuevo);
        boolean exito = recompensa > 0;
        return new Episodio(estado, accion, recompensa, exito, qNuevo);
    }

    // Entrena el agente durante n episodios sobre un entorno de caos dado.
    public List<Episodio> entrenar(int episodios, GrinchChaosEngine motorCaos,
            java.util.function.BiFunction<Estado, Accion, GrinchChaosEngine.ResultadoCaos<?>> simulador) {
        if (episodios <= 0) {
            throw new IllegalArgumentException("El numero de episodios debe ser positivo: " + episodios);
        }
        Objects.requireNonNull(motorCaos, "El motor de caos no puede ser nulo");
        Objects.requireNonNull(simulador, "El simulador no puede ser nulo");

        List<Episodio> historial = new ArrayList<>();
        for (int i = 0; i < episodios; i++) {
            Estado estado = new Estado(i % 5, 0);
            Accion accion = elegirAccion(estado);
            GrinchChaosEngine.ResultadoCaos<?> resultado = simulador.apply(estado, accion);
            historial.add(ajustar(estado, accion, resultado));
            epsilon = Math.max(0.0, epsilon * decaimientoEpsilon);
        }
        return historial;
    }

    // Tasa de acierto (episodios con exito) sobre el historial indicado.
    public double tasaAcierto(List<Episodio> historial) {
        if (historial == null || historial.isEmpty()) {
            return 0.0;
        }
        long exitos = historial.stream().filter(Episodio::exito).count();
        return (double) exitos / historial.size();
    }

    // Politica de reintentos recomendada para un estado.
    public int reintentosRecomendados(Estado estado) {
        return politicaRecomendada(estado).reintentos();
    }

    public double epsilon() {
        return epsilon;
    }

    public double learningRate() {
        return learningRate;
    }

    public double discountFactor() {
        return discountFactor;
    }

    public Recompensas recompensas() {
        return recompensas;
    }

    public List<Accion> acciones() {
        return acciones;
    }

    public int estadosConocidos() {
        return tablaQ.size();
    }

    public Optional<Accion> accionPorId(String id) {
        return acciones.stream().filter(a -> a.id().equals(id)).findFirst();
    }

    private static void validarRango(String nombre, double valor, double min, double max) {
        if (valor < min || valor > max) {
            throw new IllegalArgumentException(
                    "El parametro " + nombre + " debe estar entre " + min + " y " + max + ": " + valor);
        }
    }
}
