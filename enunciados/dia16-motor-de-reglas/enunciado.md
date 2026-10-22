# Día 16 - El motor de reglas del módulo de conducta

## 🎄 La historia
El módulo de conducta del Polo Norte decide, niño por niño, qué les toca esta Nochebuena. La decisión sale de dos datos: si el niño **se ha portado bien** y **cuántas cartas** ha enviado al buzón. Con esos dos datos, el motor de reglas devuelve una de tres etiquetas: `REGALO`, `CARBON` o `ESPERA`. Es el motor de reglas más importante de la fábrica: de él depende la felicidad de miles de niños, así que cada regla debe evaluarse en el orden correcto.

## 🧠 Piensa antes de programar
- Con `3` cartas enviadas y buen comportamiento, ¿qué etiqueta devuelve el motor? ¿Y con `0` cartas y buen comportamiento?
- ¿Y si el niño **no** se ha portado bien pero ha enviado `10` cartas? ¿Gana el comportamiento o el número de cartas? (Piensa en cuál de las dos reglas debe comprobarse primero.)
- ¿En qué se diferencia la etiqueta `ESPERA` de la etiqueta `CARBON`? (Una es un premio que aún no se concede; la otra es un castigo.)
- ¿Por qué el orden de los `if` puede cambiar el resultado aunque las reglas sean las mismas? ¿Qué pasaría si inviertieras las dos primeras comprobaciones?

## ✍️ Pseudocódigo guiado
1. Si el niño **no** se ha portado bien → devuelve `"CARBON"`. El comportamiento manda, da igual cuántas cartas haya escrito.
2. Si no, y el niño ha enviado **al menos una** carta → devuelve `"REGALO"`.
3. Si no, el niño se ha portado bien pero no ha enviado ninguna carta todavía → devuelve `"ESPERA"`.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
Escribe un `return` en cada rama: así el compilador te garantiza que el método devuelve siempre un valor y no llega nunca al final sin respuesta.
Los textos deben ser exactamente `"REGALO"`, `"CARBON"` y `"ESPERA"`, en mayúsculas y sin tildes: el test compara cadenas de forma exacta.
El primer `if` puede ser `if (!seHaPortadoBien)`, con el `!` niegas el valor booleano.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia16/MotorReglas.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia16/Dia16Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia16Test
```
Verás 1 test en rojo 🔴. Tu misión: ponerlo en verde 🟢.

## 🚀 Reto extra opcional
El módulo se complica: si el niño se ha portado bien y ha enviado `5` cartas o más, la etiqueta pasa a ser `"REGALO DORADO"`. ¿Dónde tienes que meter esa comprobación para no romper las reglas que ya funcionan? Escribe primero el test nuevo: primero el rojo, luego el verde.
