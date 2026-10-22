<div align="center">

# 🎄 Navidad en Rojo y Verde 2026

**Gimnasio de TDD en Java: 24 retos de lógica que empiezan en rojo y terminan en verde.**

[![CI](https://github.com/martillohamer/navidad-en-rojo-y-verde-2026/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/martillohamer/navidad-en-rojo-y-verde-2026/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit-5.10.2-25A162?logo=junit5&logoColor=white)
![Build](https://img.shields.io/badge/build-BUILD%20SUCCESS-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

[Inicio rápido](#-inicio-rápido) · [Arquitectura](#-arquitectura-del-gimnasio-tdd) · [Los 24 retos](#-los-24-retos) · [CI/CD](docs/CI_WORKFLOW.md)

</div>

Rojo y verde son los colores de la Navidad y también los del ciclo **TDD** (*Test-Driven Development*). Cada día de este calendario de adviento nació como un test JUnit 5 que fallaba: la misión era escribir el código mínimo para ponerlo en verde sin tocar la especificación. **Los 24 retos están resueltos y la suite completa pasa en verde**; el repositorio queda como gimnasio de referencia para estudiar cada solución y volver a practicarla desde cero.

El repositorio está pensado para **principiantes absolutos** y todo el material está en castellano. No hay frameworks, ni magia, ni dependencias ocultas: solo Java 21, Maven y JUnit 5.

---

## 🚀 Inicio rápido

### Requisitos

- **JDK 21 o superior** (el proyecto compila con `maven.compiler.release=21`; se ha verificado con JDK 21 y 24).
- **Maven 3.9+** en el `PATH`, o una distribución local del wrapper de Maven.

```bash
git clone https://github.com/martillohamer/navidad-en-rojo-y-verde-2026.git
cd navidad-en-rojo-y-verde-2026

# Compila las implementaciones y ejecuta los 77 tests: 24 retos en verde (¡calendario completo!)
mvn clean test
```

Si Maven no está en el `PATH` y usas la distribución local del wrapper, en PowerShell:

```powershell
$mvn = Get-ChildItem "$HOME\.m2\wrapper\dists\apache-maven-*\*\bin\mvn.cmd" | Select-Object -First 1
& $mvn.FullName clean test
```

### Entrena un reto desde cero

El estado de `main` ya está en verde. Para practicar de verdad:

1. Lee la historia y las pistas: `enunciados/dia01-el-despertar/enunciado.md`.
2. Vacía el método en `src/main/java/reto/dia01/RetoDia01.java` dejando el cuerpo original del esqueleto:

```java
throw new UnsupportedOperationException("TODO: Implementa el saludo navideño");
```

3. Ejecuta solo ese día y trabaja hasta ponerlo en verde:

```bash
mvn test -Dtest=Dia01Test
```

Cuando pase, compara tu solución con `src/solutions/java/reto/dia01/RetoDia01.java` y repite con el día siguiente. El resto del calendario no se toca.

---

## 🏗️ Arquitectura del gimnasio TDD

El proyecto separa deliberadamente **lo que practicas** de **la respuesta canónica**, y lo hace con dos árboles de fuentes simétricos gobernados por una única propiedad de Maven.

```text
navidad-en-rojo-y-verde-2026/
├── src/
│   ├── main/java/reto/diaNN/       implementación del reto (resuelta en main)
│   ├── solutions/java/reto/diaNN/  solución de referencia (verde canónico)
│   └── test/java/reto/diaNN/       especificación JUnit 5 (idéntica para ambos modos)
├── enunciados/diaNN-nombre/        historia, preguntas, pseudocódigo, pista y reto extra
├── scripts/
│   ├── comprobar-estructura        invariante de simetría y byte-identidad
│   └── progreso                    árbol navideño con el estado de los 24 retos
├── plantilla-reto/                 plantilla para incorporar un día nuevo
├── docs/CI_WORKFLOW.md             contrato de integración continua
└── pom.xml                         propiedad clases.dir + perfil soluciones
```

### Un `pom.xml`, dos modos de ejecución

| Modo | Origen de clases | Comando | Estado esperado |
|---|---|---|---|
| **Reto** | `src/main/java` | `mvn test` | 🔴 rojo si vacías un reto; 🟢 verde en el estado actual |
| **Soluciones** | `src/solutions/java` | `mvn -Psoluciones test` | 🟢 verde siempre (lo exige el CI) |

El `pom.xml` define `clases.dir` como `sourceDirectory` y el perfil `soluciones` la reescribe. Los **tests son exactamente los mismos** en ambos modos: si una solución de referencia no pasa la suite, el repositorio está roto.

### La dualidad simétrica y su invariante

Por cada clase de `src/main/java` debe existir su gemela en `src/solutions/java`, con **el mismo paquete y el mismo nombre de fichero**. `scripts/comprobar-estructura` verifica cuatro reglas:

1. **No falta ninguna solución** (`FALTA la solucion`).
2. **Ninguna solución lanza `UnsupportedOperationException`** (`SOLUCION INCOMPLETA`).
3. **Las clases de apoyo son byte-idénticas** entre ambos árboles (`CLASE DE APOYO DISTINTA`).
4. **No sobran ficheros en soluciones** (`SOBRA en soluciones`).

Cuando un reto está resuelto, su implementación de `src/main/java` se copia literalmente a `src/solutions/java`, de modo que ambos ficheros son **byte a byte iguales**. Ese es el contrato que mantiene el gimnasio coherente: la solución que ve el alumno es la misma que valida la suite.

### Estado verificado

| Validación | Comando | Resultado |
|---|---|---|
| Suite completa (modo reto) | `mvn clean test` | ✅ 77 tests, 0 fallos |
| Suite completa (soluciones) | `mvn -Psoluciones test` | ✅ 77 tests, 0 fallos |
| Invariante estructural | `bash scripts/comprobar-estructura` | ✅ 31 ficheros emparejados |

---

## 📅 Los 24 retos

| Día | Reto | Componente principal | Concepto técnico |
|:---:|---|---|---|
| **01** | El despertar | `RetoDia01.saludoNavideno` | Cadenas, concatenación y defensa ante `null` |
| **02** | Abrigo o camiseta | `RetoDia02.ropaSegunTemperatura` | Condicionales y umbrales |
| **03** | La cuenta atrás | `RetoDia03.cuentaAtras` | Bucles `for` y `StringBuilder` |
| **04** | El taller de duendes | `TallerDuendes` | Colecciones, `for-each` y excepción personalizada |
| **05** | La cinta transportadora | `CintaTransportadora` | Acumuladores, `continue` y corte temprano |
| **06** | El trineo de Santa | `TrineoSanta` | Condicionales y operador ternario |
| **07** | La montaña de cartas | `GestorCartas` | Objetos, `Comparator` y orden estable |
| **08** | El gran reencuentro | `MotorPoloNorte` | Reutilización de clases y `try/catch` multi-catch |
| **09** | La lista de deseos | `ListaDeseos` | `HashMap`, `Map.merge` y conteo de frecuencias |
| **10** | El mapa del recorrido | `NavegadorTrineo` | `split`, `switch` y normalización de texto |
| **11** | La carta perdida | `AnalizadorCartas` | Expresiones regulares con `Pattern` y `Matcher` |
| **12** | El gran informe | `GeneradorInformes` | Streams: `filter`, `mapToInt` y `sum` |
| **13** | Control de acceso | `ControlAcceso` | Excepciones personalizadas y reglas de umbral |
| **14** | El inventario secreto | `ParserInventario` | Parseo de CSV y `Integer.parseInt` |
| **15** | La flota de trineos | `TrineoMagico` | Interfaces y polimorfismo |
| **16** | El motor de reglas | `MotorReglas` | Reglas de decisión y orden de evaluación |
| **17** | El optimizador de rutas | `OptimizadorRutas` | Coordenadas cartesianas, geometría euclidiana (`Math.sqrt`), `record Punto` y listas |
| **18** | El cifrador polar | `CifradorPolar` | Recorrido de `String`, aritmética de `char`, módulo y cifrado César |
| **19** | El calculador de carga | `CalculadorCarga` | `record Regalo`, acumulación de `double` y excepción personalizada |
| **20** | Control de calidad | `ControlCalidad` | Streams: `filter`, `groupingBy`, `counting` y `record Juguete` |
| **21** | El monitor de tormentas | `AlertaTormenta` | Streams: `filter`, `sorted` con `Comparator`, `anyMatch` y umbrales |
| **22** | El asignador de talleres | `AsignadorTalleres` | Streams: `groupingBy`, `maxBy` y reparto round-robin |
| **23** | La compresión de historiales | `CompresorRegalos` | Compresión RLE, escape de caracteres y reversibilidad |
| **24** | El simulador del gran despegue | `SimuladorNavidad` | Orquestación de clases, `record InformeMision` y lógica combinada |

Cada enunciado incluye historia, preguntas de razonamiento, pseudocódigo guiado, una pista plegable y un reto extra. El calendario está **completo**: los **24 retos** están disponibles.

## 🧪 Comandos de trabajo

| Objetivo | Comando |
|---|---|
| Compilar y ejecutar toda la suite | `mvn clean test` |
| Ejecutar un único reto | `mvn test -Dtest=Dia07Test` |
| Validar las soluciones de referencia | `mvn -Psoluciones test` |
| Comprobar la simetría de los árboles | `bash scripts/comprobar-estructura` |
| Ver tu árbol de progreso | `./scripts/progreso` |

En Windows, `scripts\progreso.bat` ejecuta el mismo árbol (requiere `bash`, por ejemplo Git Bash).

## 🔁 Integración continua

Cada `push` a `main` y cada Pull Request contra `main` o `feat/*` disparan el workflow **CI Navidad Roja y Verde**, con dos jobs:

- **`validar-soluciones`** — ejecuta `bash scripts/comprobar-estructura` y `mvn -Psoluciones test`. Es el guardián del repositorio: si falla, no se fusiona.
- **`progreso`** — ejecuta la suite en modo reto ignorando los fallos esperados y publica el árbol de progreso.

El contrato completo (ciclo de vida del PR, reglas invariantes, diagnóstico de fallos y diagrama de flujo) está en **[docs/CI_WORKFLOW.md](docs/CI_WORKFLOW.md)**.

## 🤝 Contribuir

- Nunca se hace `push` directo a `main`: **rama + Pull Request**.
- Ramas de trabajo con el patrón `code/diaNN` (por ejemplo, `code/dia17`).
- Commits en castellano con el formato `feat(diaNN): ...` o `docs: ...`.
- No se fusiona nada con el CI en rojo.
- Para añadir un día, sigue el checklist de [HANDOFF.md](HANDOFF.md) y usa `plantilla-reto/`.

## 🧠 Créditos

Idea inspirada en el reto **Cookie Codebook** de OverTheWire Advent 2019 (recetas escritas como código). Aquí le damos la vuelta: código real, ejecutable y con tests. No se ha copiado código de aquel repositorio.

## 📜 Licencia

Distribuido bajo licencia **MIT**. Consulta [LICENSE](LICENSE).
