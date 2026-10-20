# 🎄 Navidad en Rojo y Verde 2026

![CI](https://github.com/martillohamer/navidad-en-rojo-y-verde-2026/actions/workflows/ci.yml/badge.svg)

Calendario de adviento de retos de lógica de programación en **Java**, pensado para principiantes absolutos y escrito en castellano.

Cada día es un **test en rojo** 🔴 que falla. Tu misión es programar la solución hasta ponerlo en **verde** 🟢. Rojo y verde: los colores de la Navidad y los del TDD (desarrollo guiado por tests).

## 🚀 Empieza en 3 minutos
Necesitas **Java 21** y **Maven** instalados.

```bash
git clone https://github.com/martillohamer/navidad-en-rojo-y-verde-2026.git
cd navidad-en-rojo-y-verde-2026
mvn test -Dtest=Dia01Test
```

Verás el test en rojo. Ahora:
1. Lee el enunciado: `enunciados/dia01-el-despertar/enunciado.md`
2. Programa tu solución en `src/main/java/reto/dia01/RetoDia01.java`
3. Vuelve a ejecutar el test. Cuando se ponga en verde, pasa al día siguiente.

## 📅 Calendario
| Día | Reto | Qué practicas | Estado |
|---|---|---|---|
| 01 | El despertar | Cadenas y concatenación | ✅ Disponible |
| 02 | Abrigo o camiseta | Condicionales (if / else) | ✅ Disponible |
| 03 | La cuenta atrás | Bucles (for) | ✅ Disponible |
| 04 | El taller de duendes | Listas, for-each y excepciones | ✅ Disponible |
| 05 | La cinta transportadora | Acumuladores, continue y excepciones | ✅ Disponible |
| 06 | El trineo de Santa | Condicionales anidados y operador ternario | ✅ Disponible |
| 07 | La montaña de cartas | Objetos, getters y ordenación | ✅ Disponible |
| 08 | El gran reencuentro | Reutilizar clases y try / catch | ✅ Disponible |
| 09 | La lista de deseos | Mapas (Map) y conteo | ✅ Disponible |
| 10 | El mapa del recorrido | Texto, split y switch | ✅ Disponible |
| 11 | La carta perdida | Expresiones regulares | ✅ Disponible |
| 12 | El gran informe | Streams y filtros | ✅ Disponible |
| 13 | Control de acceso | Excepciones personalizadas | ✅ Disponible |
| 14 | El inventario secreto | Procesar texto en formato CSV | ✅ Disponible |
| 15 | La flota de trineos | Interfaces | ✅ Disponible |
| 16 | El motor de reglas | Reglas de decisión | ✅ Disponible |
| 17–24 | Por anunciar | Ideas más avanzadas | 🚧 En preparación |

## 🗂️ Estructura del proyecto
```text
src/main/java/reto/diaNN/       aquí programas tú (esqueletos que empiezan en rojo)
src/test/java/reto/diaNN/       los tests de cada día (no hace falta tocarlos)
src/solutions/java/reto/diaNN/  soluciones de referencia (¡no mires antes de intentarlo!)
enunciados/diaNN-nombre/        la historia y las pistas de cada día
plantilla-reto/                 plantilla para crear nuevos retos
scripts/                        herramientas (progreso, comprobar-estructura)
```

## 🧪 Rojo y verde: cómo funciona
- `mvn test` prueba **tus** clases (`src/main/java`). Al principio fallan con `UnsupportedOperationException`.
- `mvn -Psoluciones test` prueba las **soluciones de referencia** (`src/solutions/java`) y debe salir todo en verde. Es lo que comprueba el CI en cada cambio.

## 🎄 Mira tu progreso
Ejecuta `./scripts/progreso` (en Windows, `scripts\progreso.bat`; necesita `bash`, por ejemplo Git Bash) para ver cuántos retos llevas resueltos.

## 🤝 Contribuir
Rama + Pull Request, commits en castellano y CI en verde. Las reglas y la guía para añadir un día nuevo están en [HANDOFF.md](HANDOFF.md).

## 🧠 Agradecimiento
Idea inspirada en el reto **Cookie Codebook** de OverTheWire Advent 2019 (recetas escritas como código). Aquí le damos la vuelta: código real, ejecutable y con tests. No se ha copiado código de aquel repo.

## 📜 Licencia
Licencia MIT.
