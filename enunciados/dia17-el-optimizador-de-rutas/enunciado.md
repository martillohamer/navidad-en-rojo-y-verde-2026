# Día 17 - El optimizador de rutas de Santa

## 🎄 La historia
Nochebuena está a punto de empezar y Santa tiene un problema nuevo: el combustible mágico de la flota se agota. Antes de salir, el jefe de logística del Polo Norte quiere conocer la **distancia total exacta** que recorrerá el trineo principal. La ruta es sencilla: parte del Polo Norte, que marcamos como el origen `(0, 0)`, visita una serie de paradas en el orden fijado en el cuaderno de reparto y, cuando termina, **regresa siempre a la base** para descargar y cargar el próximo turno. Necesitamos una función que calcule ese trayecto completo sumando la distancia euclidiana de cada tramo.

## 🧠 Piensa antes de programar
- Si hay que ir del punto `(0, 0)` al punto `(3, 4)`, ¿qué distancia hay? (Pista: `3-4-5`... te suena de algo.)
- El trineo no teletransporta: el trayecto es **ida parada a parada** y, al acabar la lista, un último tramo de **vuelta al origen**. ¿Cuántos tramos hay para 3 paradas?
- ¿Qué debe devolver la función si la lista de paradas llega `null` o vacía? ¿Es un error o simplemente no hay recorrido que hacer?
- Los puntos son coordenadas enteras, pero las distancias casi nunca lo son: `Math.sqrt(...)` devuelve un `double`. ¿Cómo comprobarías en un test que un número decimal es correcto sin exigir una igualdad exacta?

## ✍️ Pseudocódigo guiado
1. Si la lista de paradas es `null` o está vacía, no hay nada que recorrer: devuelve `0.0`.
2. Guarda el **punto actual** empezando en el origen `(0, 0)` y un acumulador `total = 0.0`.
3. Recorre cada parada en orden: suma a `total` la distancia del punto actual a esa parada y luego convierte esa parada en el nuevo punto actual.
4. Al terminar la lista, suma la distancia del último punto actual de vuelta al origen `(0, 0)`.
5. Devuelve `total`.

La distancia entre dos puntos `(x1, y1)` y `(x2, y2)` es la raíz de la suma de los cuadrados de las diferencias:

```text
distancia = sqrt((x1 - x2)^2 + (y1 - y2)^2)
```

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
Un `record Punto(int x, int y)` te da el constructor, los accesores `x()` e `y()`, `equals` y `hashCode` sin escribir nada. Añádele un método `distanciaA(Punto otro)` y tendrás la fórmula encapsulada en un solo sitio.
Para la distancia, eleva al cuadrado como `(double) dx * dx` para evitar el desbordamiento de `int` antes de multiplicar, y usa `Math.sqrt(...)` al final.
No dupliques el bucle por el origen: guarda el origen en una variable y, al final, añade `actual.distanciaA(origen)`. Para un punto que ya está en `(0, 0)` ese último tramo vale `0.0` y no altera el total.
El test compara `double` con un margen (`delta`), no con `==`: en coma flotante `10.0` puede no ser exactamente `10.0` tras una raíz cuadrada.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia17/OptimizadorRutas.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia17/Dia17Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia17Test
```
Verás 5 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢.

## 🚀 Reto extra opcional
Este reto se llama "optimizador", pero de momento **respeta el orden** en el que llegan las paradas. Añade un método que, con la misma lista de puntos, devuelva el recorrido de distancia total **mínima** visitando todas las paradas y volviendo al origen (el clásico *problema del viajante*). Empieza por el caso pequeño: con 3 o 4 paradas, ¿probarías todas las permutaciones? Escribe primero el test nuevo: primero el rojo, luego el verde.
