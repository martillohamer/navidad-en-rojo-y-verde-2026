# Día 24 - El simulador del gran despegue

## 🎄 La historia
Es la medianoche de Nochebuena en el Polo Norte. Durante 23 días has construido, pieza a pieza, todo el sistema: la bascula del taller, la cinta transportadora, el optimizador de rutas, el monitor de tormentas, la lista de deseos... Pero cada reto funcionaba **por separado**. Ahora, por fin, todo tiene que arrancar **a la vez**. Santa sube al trineo, los renos se preparan y la torre de control necesita una única respuesta antes del despegue: **¿se autoriza la misión o se aborta?** Tu misión final es construir el `SimuladorNavidad`, el orquestador que pesa la carga, traza la ruta y revisa el clima para emitir el **informe consolidado** de la Nochebuena.

## 🧠 Piensa antes de programar
- El despegue depende de **tres sistemas** a la vez: la carga no puede exceder el límite, la ruta no puede estar vacía y no puede haber tormenta crítica. ¿Cómo combinas las tres condiciones con operadores lógicos?
- Para pesar la carga hay un `CalculadorCarga` (Día 19), para contar las paradas un `OptimizadorRutas` (Día 17) y para el clima un `AlertaTormenta` (Día 21). ¿Reutilizas esas clases o reescribes la lógica? ¿Por qué es mejor reutilizar?
- El informe debe llevar el **peso total real** aunque la carga se pase del límite. ¿Cómo obtienes el peso sin que la excepción `ExcesoCapacidadException` interrumpa el informe?
- ¿Qué `estadoClimatico` devuelves: una etiqueta como `"FAVORABLE"`/`"CRITICO"`? ¿Cómo la calculas a partir de las mediciones?
- Si la ruta llega `null`, ¿debe fallar el simulador o tratarse como "ruta inválida" y abortar el despegue con un informe coherente?

## ✍️ Pseudocódigo guiado
1. Suma el peso de los regalos (Día 19) en un acumulador. Guarda el `pesoTotal` real, incluso si supera el máximo.
2. `cargaOk = pesoTotal <= pesoMaximo`.
3. `totalParadas = ruta == null ? 0 : ruta.size()` y `rutaOk = ruta != null && !ruta.isEmpty()`.
4. `climaCritico = new AlertaTormenta(umbralViento).hayAlertaRojaGlobal(clima)`.
5. `estadoClimatico = climaCritico ? "CRITICO" : "FAVORABLE"`.
6. `autorizado = cargaOk && rutaOk && !climaCritico`.
7. Devuelve un `record InformeMision(autorizado, pesoTotal, totalParadas, estadoClimatico)`.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
Reutiliza lo que ya construiste: `CalculadorCarga.Regalo`, `OptimizadorRutas.Punto` y `AlertaTormenta.Medicion` viven en sus paquetes `reto.dia19`, `reto.dia17` y `reto.dia21`. Impórtalos con `import reto.dia17.OptimizadorRutas;`, etc.
Cuidado con la excepción: si llamas a `new CalculadorCarga(max).calcularPesoTotal(regalos)`, lanzará `ExcesoCapacidadException` en cuanto se pase. Para el informe **no** quieres que se lance, así que suma los pesos tú mismo en un bucle `for`, o captura la excepción y calcula el peso aparte.
Un `record InformeMision(...)` es la forma perfecta de devolver varios valores a la vez sin crear un objeto a mano: genera constructor, accesores y `equals` de golpe.
La condición de autorización es un `&&` de tres partes: todas deben cumplirse. Un solo sistema en rojo (`false`) aborta la misión.
En los tests, usa `assertEquals(15.0, informe.pesoTotal(), 1e-9)` porque el peso es `double`; compara con margen, no con `==`.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia24/SimuladorNavidad.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia24/Dia24Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia24Test
```
Verás 6 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢 y completar los **24 retos**.

## 🚀 Reto extra opcional
El Polo Norte ha crecido tanto que ya no cabe en una sola clase: el taller, la logística, el clima y la ruta quieren ser **servicios independientes** que se comunican por **eventos**. Imagina una arquitectura en la que cada sistema publica mensajes (`PesoCalculado`, `TormentaDetectada`, `RutaTrazada`) y la torre de control los escucha para decidir el despegue. Diseña las interfaces de esos eventos y un bus de eventos en memoria: ¿cómo garantizas que el informe final espera a recibir **todos** los eventos antes de decidir? Escribe primero el test nuevo: primero el rojo, luego el verde.
