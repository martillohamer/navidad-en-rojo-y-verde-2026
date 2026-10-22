# Día 22 - El asignador dinámico de elfos

## 🎄 La historia
La central de asignaciones de Papá Noel tiene un problema cada diciembre: hay **cientos de elfos** repartidos por talleres (pintura, montaje, carpintería, envoltorio...) y hay que organizarlos en tiempo récord. El jefe de planta necesita tres listas: **cómo se agrupan** los elfos por taller, **quién es el más veterano** de cada taller (para que lidere el turno) y un **reparto equilibrado** de la plantilla entre los turnos disponibles. Hacer esto a mano sería un caos; con streams y `Collectors` es casi elegante. Tu misión es construir el `AsignadorTalleres`.

## 🧠 Piensa antes de programar
- Para agrupar elfos por una propiedad (la especialidad), `Collectors.groupingBy` hace el trabajo. ¿Qué estructura devuelve: `Map<String, List<Elfo>>` o algo distinto?
- Una vez agrupados, quieres el **más veterano** de cada grupo. ¿Cómo eliges el máximo según `experienciaAnios`? ¿Qué Collector combinas con `groupingBy`?
- ¿Qué devuelve `Collectors.maxBy(...)` cuando un grupo no tiene elementos, un `Elfo` o un `Optional<Elfo>`? ¿Por qué el mapa resultante es `Map<String, Optional<Elfo>>`?
- Para repartir 5 elfos entre 2 turnos "equilibrados", ¿es mejor un reparto por bloques o **round-robin** (uno para cada turno por turnos)? ¿Cómo garantizas que no se pierde ningún elfo?
- ¿Qué debe devolver cada método con una lista `null` o vacía? ¿Y si el número de turnos es `0` o negativo?

## ✍️ Pseudocódigo guiado
1. **Agrupar**: `elfos.stream().collect(Collectors.groupingBy(Elfo::especialidad))`.
2. **Más veterano por taller**: `collect(Collectors.groupingBy(Elfo::especialidad, Collectors.maxBy(Comparator.comparingInt(Elfo::experienciaAnios))))`.
3. **Distribución equilibrada**: recorre los elfos por índice y asigna cada uno al grupo `i % numeroTalleres`, de modo que todos los grupos reciban elfos de forma alterna.
4. Si la lista es `null` o vacía, o `numeroTalleres <= 0`, devuelve `Map.of()`.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
`Collectors.groupingBy` crea un `HashMap` por defecto, así que **no** conserva el orden de aparición de las claves. Si necesitas un orden concreto, usa `Collectors.groupingBy(Elfo::especialidad, LinkedHashMap::new, Collectors.toList())`.
`Collectors.maxBy` devuelve un `Optional`, porque un grupo podría estar vacío. Por eso el tipo es `Map<String, Optional<Elfo>>` y en el test se accede con `.orElseThrow().nombre()`.
Para la distribución round-robin, la clave está en el **índice**, no en el objeto: `i % numeroTalleres` reparte `0,1,0,1,...` y garantiza que ningún grupo queda vacío si hay al menos un elfo por turno.
Fíjate en que `distribuirEquilibrado` no es puramente un stream: el índice hace falta y un bucle `for` es la forma más clara y segura de calcularlo. No todo tiene que ser un stream.
En los tests, comprueba que la **suma de los tamaños** de los grupos es igual al total de elfos: es la mejor garantía de que no se ha perdido ninguno por el camino.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia22/AsignadorTalleres.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia22/Dia22Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia22Test
```
Verás 6 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢.

## 🚀 Reto extra opcional
El reparto round-robin es equilibrado **en número de elfos**, pero la central quiere equilibrar también la **carga de trabajo real**. Imagina que cada taller tiene un número de tareas pendientes y cada elfo una productividad (por ejemplo, su experiencia). Añade un método que asigne cada tarea al taller con menos carga acumulada hasta ese momento (un **balanceo dinámico de colas**, como un heap de mínimos). Empieza con pocos datos y comprueba el orden de asignación. Escribe primero el test nuevo: primero el rojo, luego el verde.
