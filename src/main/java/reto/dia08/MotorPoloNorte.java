package reto.dia08;

import java.util.List;
import java.util.Objects;
import reto.dia04.InvalidProductionException;
import reto.dia04.TallerDuendes;
import reto.dia05.CintaTransportadora;
import reto.dia05.ConveyorCapacityException;

public class MotorPoloNorte {

    // La jornada es valida si la produccion no tiene negativos Y la cinta aguanta los paquetes
    public boolean validarJornadaCompleta(List<Integer> produccion, int capacidadCinta, List<Integer> paquetes) {
        Objects.requireNonNull(produccion, "produccion no puede ser null");
        Objects.requireNonNull(paquetes, "paquetes no puede ser null");
        try {
            new TallerDuendes().calcularProduccionTotal(produccion);
            new CintaTransportadora().simularCinta(paquetes, capacidadCinta);
            return true;
        } catch (InvalidProductionException | ConveyorCapacityException e) {
            return false;
        }
    }
}