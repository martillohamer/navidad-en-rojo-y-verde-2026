package reto.extension.k8s;

import java.util.ArrayList;
import java.util.List;

public class ElfoOperator {

    // Especificacion deseada del Custom Resource "ElfoCluster".
    public record ElfoSpec(String nombre, int replicas, String version) {
        public ElfoSpec {
            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("El nombre del cluster no puede estar vacio");
            }
            if (replicas < 0) {
                throw new IllegalArgumentException("Las replicas no pueden ser negativas: " + replicas);
            }
            version = version == null || version.isBlank() ? "v1" : version;
        }
    }

    // Estado observado de un pod elfo.
    public record PodElfo(String nombre, String estado, int reinicios) {
        public static final String ESTADO_RUNNING = "Running";
        public static final String ESTADO_CRASHED = "CrashLoopBackOff";
        public static final String ESTADO_PENDIENTE = "Pending";

        public PodElfo {
            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("El nombre del pod no puede estar vacio");
            }
            if (estado == null || estado.isBlank()) {
                throw new IllegalArgumentException("El estado del pod no puede estar vacio");
            }
            if (reinicios < 0) {
                throw new IllegalArgumentException("Los reinicios no pueden ser negativos: " + reinicios);
            }
        }

        public boolean estaSano() {
            return ESTADO_RUNNING.equals(estado);
        }
    }

    // El Custom Resource completo: spec deseada + pods observados.
    public record ElfoRecurso(String nombre, ElfoSpec spec, List<PodElfo> pods) {
        public ElfoRecurso {
            if (spec == null) {
                throw new IllegalArgumentException("La spec no puede ser nula");
            }
            pods = pods == null ? List.of() : List.copyOf(pods);
        }

        public int podsSanos() {
            return (int) pods.stream().filter(PodElfo::estaSano).count();
        }
    }

    // Resultado de una reconciliacion: acciones aplicadas y estado resultante.
    public record Reconciliacion(boolean cambiosAplicados, List<String> acciones, ElfoRecurso estadoResultante) {
        public Reconciliacion {
            acciones = List.copyOf(acciones);
        }
    }

    // Contrato generico de reconciliacion estilo Kubernetes.
    public interface Reconciler<T> {
        Reconciliacion reconciliar(T actual);
    }

    private final boolean autoReparacion;

    public ElfoOperator() {
        this(true);
    }

    public ElfoOperator(boolean autoReparacion) {
        this.autoReparacion = autoReparacion;
    }

    // Bucle de reconciliacion: lleva el estado actual hacia la spec deseada.
    public Reconciliacion reconciliar(ElfoRecurso actual) {
        if (actual == null) {
            throw new IllegalArgumentException("El recurso actual no puede ser nulo");
        }
        List<String> acciones = new ArrayList<>();
        List<PodElfo> pods = new ArrayList<>(actual.pods());
        int deseadas = actual.spec().replicas();

        if (autoReparacion) {
            pods = repararPods(pods, acciones);
        }

        while (pods.size() < deseadas) {
            String nombre = actual.spec().nombre() + "-" + pods.size();
            pods.add(new PodElfo(nombre, PodElfo.ESTADO_RUNNING, 0));
            acciones.add("crear:" + nombre);
        }

        while (pods.size() > deseadas) {
            PodElfo expulsado = pods.remove(pods.size() - 1);
            acciones.add("eliminar:" + expulsado.nombre());
        }

        boolean cambios = !acciones.isEmpty();
        ElfoRecurso resultado = new ElfoRecurso(actual.nombre(), actual.spec(), pods);
        return new Reconciliacion(cambios, acciones, resultado);
    }

    // Auto-reparacion: reinicia los pods caidos (CrashLoopBackOff).
    private List<PodElfo> repararPods(List<PodElfo> pods, List<String> acciones) {
        List<PodElfo> reparados = new ArrayList<>();
        for (PodElfo pod : pods) {
            if (PodElfo.ESTADO_CRASHED.equals(pod.estado())) {
                PodElfo nuevo = new PodElfo(pod.nombre(), PodElfo.ESTADO_RUNNING, pod.reinicios() + 1);
                reparados.add(nuevo);
                acciones.add("reiniciar:" + pod.nombre());
            } else {
                reparados.add(pod);
            }
        }
        return reparados;
    }

    // Comprueba si un recurso ya esta en el estado deseado (sin cambios pendientes).
    public boolean estaConvergido(ElfoRecurso recurso) {
        if (recurso == null) {
            return false;
        }
        if (recurso.pods().size() != recurso.spec().replicas()) {
            return false;
        }
        if (autoReparacion && recurso.pods().stream().anyMatch(p -> !p.estaSano())) {
            return false;
        }
        return true;
    }

    public boolean autoReparacion() {
        return autoReparacion;
    }
}
