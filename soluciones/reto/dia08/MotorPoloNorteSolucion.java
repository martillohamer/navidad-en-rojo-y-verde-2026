package reto.dia08;

import reto.dia04.TallerDuendes;
import reto.dia05.CintaTransportadora;
import java.util.List;

public class MotorPoloNorte {

    public boolean validarJornadaCompleta(List<Integer> produccion, int capacidadCinta, List<Integer> paquetes) {
        TallerDuendes taller = new TallerDuendes();
        CintaTransportadora cinta = new CintaTransportadora();

        int prodTotal = taller.calcularProduccionTotal(produccion);
        int pesoCinta = cinta.simularCinta(paquetes, capacidadCinta);

        return prodTotal > 0 && pesoCinta <= capacidadCinta;
    }
}