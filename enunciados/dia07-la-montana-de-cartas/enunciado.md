# Día 7 – La montaña de cartas

## 🎅 La historia
Cada diciembre llegan miles de cartas al Polo Norte. Cada una trae el nombre de un niño y una **urgencia** del 1 (puede esperar) al 5 (¡hay que leerla ya!). Santa no puede leerlas todas a la vez, así que necesita una nueva lista con las cartas ordenadas de la más urgente a la menos urgente.

## 🧠 Piensa antes de programar
- Con las urgencias `2, 5, 3`, ¿en qué orden deberían quedar las cartas? ¿Quién va primero?
- ¿Qué pasaría si dos cartas tuvieran la misma urgencia? Lo más justo es respetar el orden en que llegaron.
- Ordenar una lista, ¿debe cambiar la lista original o devolver una lista nueva? Aquí queremos **no tocar la original**.
- Es como el triaje de un hospital: no se atiende por orden de llegada, sino por gravedad.

## ✍️ Pseudocódigo guiado
1. Haz una copia de la lista de cartas.
2. Ordena la copia comparando la urgencia de cada carta, de mayor a menor.
3. Devuelve la copia ordenada.

## 💡 Pista
<details>
<summary>Haz clic para ver la pista</summary>
Una copia de una lista se hace con `new ArrayList<>(cartas)`. Para ordenar por un dato de un objeto, mira `Comparator.comparingInt(...)` y el método `.reversed()` para invertir el orden. La carta te da su urgencia con `getUrgencia()`.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia07/GestorCartas.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia07/Dia07Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia07Test
```
Verás 1 test en rojo 🔴. Tu misión: ponerlo en verde 🟢.

## 🚀 Reto extra opcional
Si dos cartas tienen la misma urgencia, haz que se ordenen por el nombre del niño en orden alfabético. ¿Qué cambia en tu comparador?
