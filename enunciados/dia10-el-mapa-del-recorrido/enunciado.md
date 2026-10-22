# Día 10 - El mapa del recorrido mágico

## 🎄 La historia
El trineo de Santa ya vuela solo, pero necesita un piloto automático: un navegador que vaya siguiendo la ruta marcada en el parte de vuelo. Esa ruta llega como un único texto separado por comas, por ejemplo `Norte,Norte,Este,Sur`. El navegador parte del origen `(0, 0)` y necesita calcular en qué punto acaba el trineo. Para eso lleva la cuenta de los pasos en horizontal (X) y en vertical (Y).

## 🧠 Piensa antes de programar
- Después de `Norte,Norte,Este,Sur`, ¿cuánto vale X y cuánto vale Y? ¿Por qué? (Ojo: Norte y Sur mueven la Y, Este y Oeste mueven la X).
- ¿Qué debería hacer el navegador con un paso que no entiende, como `Arriba` o `Volar`? ¿Lanzar un error o ignorarlo?
- ¿Y si la ruta llega con espacios de sobra, como `Norte, Norte ,Este`? ¿Y si llega en minúsculas?
- ¿Qué tiene que devolver el método si la ruta es `null` o está vacía? Piensa en el vuelo automatico: no hay datos, no hay movimiento.

## ✍️ Pseudocódigo guiado
1. Empieza en el origen: `x = 0` e `y = 0`.
2. Si la ruta es `null` o está en blanco, devuelve la posición `(0, 0)`.
3. Separa la ruta en pasos usando las comas.
4. Para cada paso (ignorando espacios y sin distinguir mayúsculas de minúsculas):
   - `norte` → `y` sube en 1.
   - `sur` → `y` baja en 1.
   - `este` → `x` sube en 1.
   - `oeste` → `x` baja en 1.
   - Cualquier otro paso → se ignora y se sigue con el siguiente.
5. Devuelve una `Posicion` con la `x` y la `y` finales.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
Para partir el texto usa `movimientos.split(",")`. Java 21 te permite escribir el `switch` con flechas (`case "norte" -> y++;`), que queda más limpio que los `break`.
Para no distinguir mayúsculas de minúsculas ni espacios: `paso.trim().toLowerCase()`.
La clase `Posicion` ya está creada en `src/main/java/reto/dia10/Posicion.java`: solo tienes que construirla con `new Posicion(x, y)`.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia10/NavegadorTrineo.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia10/Dia10Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia10Test
```
Verás 1 test en rojo 🔴. Tu misión: ponerlo en verde 🟢.

## 🚀 Reto extra opcional
Añade un método `distanciaAlOrigen(String movimientos)` que devuelva cuántos pasos separan el destino del origen (usa `Math.sqrt` y `Math.pow`). ¿Qué necesitas para no repetir el cálculo del recorrido?
