# Día 4 – El taller de duendes

## 🎅 La historia
En el corazón del Polo Norte, los duendes fabrican juguetes sin parar. Cada hora anotan en una lista cuántos juguetes ha hecho cada uno. Hoy el jefe del taller necesita dos cosas: saber la **producción total** y quedarse solo con los duendes más trabajadores. Pero ojo: si alguien apunta una cifra negativa, algo se ha roto en el registro y el taller debe dar la alarma. 🚨

## 🧠 Piensa antes de programar
- Si la lista es `[10, 20, 15, 30]`, ¿cuánto suman? ¿Cómo lo harías con lápiz y papel, número a número?
- ¿Qué pasaría si una cifra fuera `-5`? ¿Debería el programa seguir sumando como si nada?
- Para filtrar: ¿qué lista saldría de `[5, 25, 12, 30, 18]` si solo quieres los números **mayores que 15**? ¿Se mantiene el orden?
- En la vida real es como contar las cajas que pasan por una cinta, con una alarma que suena si alguna viene mal etiquetada. En Java, esa alarma se llama **excepción**.

## ✍️ Pseudocódigo guiado
**calcularProduccionTotal**
1. Empieza con un total en 0.
2. Recorre la lista, un número cada vez.
3. Si el número es negativo, lanza `InvalidProductionException` con un mensaje claro.
4. Si no, súmalo al total.
5. Al terminar el recorrido, devuelve el total.

**filtrarAltaProduccion**
1. Crea una lista nueva y vacía.
2. Recorre la lista original.
3. Si el número es **mayor** que el umbral (no igual), añádelo a la lista nueva.
4. Devuelve la lista nueva.

## 💡 Pista
<details>
<summary>Haz clic para ver la pista</summary>
Para recorrer una lista: `for (int p : producciones) { ... }`. Para dar la alarma: `throw new InvalidProductionException("Producción negativa: " + p);`. Para la lista nueva: `new ArrayList<>()` y el método `.add(...)`.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia04/TallerDuendes.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia04/Dia04Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia04Test
```
Verás 3 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢.

## 🚀 Reto extra opcional
Haz que `filtrarAltaProduccion` también lance `InvalidProductionException` si el umbral es negativo. ¿Qué test escribirías primero para comprobarlo?
