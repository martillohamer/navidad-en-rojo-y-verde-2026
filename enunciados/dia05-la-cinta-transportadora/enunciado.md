# Día 5 – La cinta transportadora

## 🎅 La historia
Los regalos terminados viajan por una cinta transportadora hacia el almacén de Santa. Pero la cinta es antigua y solo aguanta un peso máximo: si se acumula demasiado, ¡se rompe y los regalos acaban por el suelo! Tu trabajo es simular la cinta paquete a paquete, saltarte los envoltorios vacíos y avisar antes de que ocurra el desastre.

## 🧠 Piensa antes de programar
- Si los paquetes pesan `10, 20, 15` y la cinta aguanta `100`, ¿cuánto pesa lo que ha pasado en total?
- ¿Cuándo hay que dar la alarma: al terminar de sumar todo o **en cuanto** se supera el límite?
- ¿Qué haces con un paquete que pesa `0`? ¿Cuenta para algo?
- Es como un ascensor con un cartel de "máximo 300 kg": entra gente poco a poco y, en el momento en que se pasa el límite, suena el aviso.

## ✍️ Pseudocódigo guiado
1. Empieza con un total en 0.
2. Recorre los pesos, uno cada vez.
3. Si el peso es 0, sáltalo y pasa al siguiente (`continue`).
4. Suma el peso al total.
5. Si el total **supera** la capacidad máxima, lanza `ConveyorCapacityException`.
6. Al terminar, devuelve el total.

## 💡 Pista
<details>
<summary>Haz clic para ver la pista</summary>
La palabra `continue` dentro de un bucle significa "no sigas con esta vuelta, pasa a la siguiente". Fíjate bien en la comparación: llegar **justo** al límite no es pasarse, hay que superarlo (`>`).
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia05/CintaTransportadora.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia05/Dia05Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia05Test
```
Verás 3 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢.

## 🚀 Reto extra opcional
Haz que el mensaje de la excepción diga cuánto se ha pasado la cinta, por ejemplo: `"Te has pasado por 40 kg"`.
