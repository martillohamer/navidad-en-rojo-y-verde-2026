---
marp: true
theme: gaia
paginate: true
backgroundColor: #111
color: #fff
style: |
  section {
    font-family: 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
  }
  h1, h2 {
    color: #e63946;
  }
  strong {
    color: #2a9d8f;
  }
  code {
    background: #222;
    color: #f4a261;
  }
  a {
    color: #2a9d8f;
  }
---

<!-- _paginate: false -->

# 🎄 Navidad en Rojo y Verde 2026

### Arquitectura de Sistemas Distribuidos, TDD y Ecosistema Full-Stack

**Gimnasio de TDD en Java 21: 24 retos que empiezan en rojo y terminan en verde, más una plataforma distribuida de alta fiabilidad.**

Autoría: **Equipo Navidad en Rojo y Verde**
`github.com/martillohamer/navidad-en-rojo-y-verde-2026`

---

## 📊 Resumen ejecutivo

La plataforma evolucionó de un calendario de 24 retos de lógica a un **ecosistema distribuido completo**, sin salir nunca del ciclo **TDD**.

- 🟢 **100 % TDD**: cada componente nació como un test en rojo.
- ✅ **276 tests en verde absoluto** (`BUILD SUCCESS` en modo reto y soluciones).
- 🔁 **50 ficheros emparejados** con simetría binaria `src/main` ↔ `src/solutions`.
- 🧱 **Cero dependencias externas**: solo Java 21, Maven y JUnit 5.
- 🧩 **Subsistemas avanzados** (`+197 tests` sobre el calendario base) incluyendo mapas 3D interactivos de Paraguay y España con WebSockets y SSE.

> Un único `pom.xml` conmuta entre implementación y solución vía la propiedad `clases.dir`.

---

## 🏛️ Pilares del repositorio

**1. Los 24 retos base** — de "El despertar" a "El simulador del gran despegue": cadenas, colecciones, streams, records y orquestación de clases. **77 tests**.

**2. Extensiones de alta fiabilidad** — sistemas de nivel industrial:
- **BFT**: consenso tolerante a fallos bizantinos sobre red P2P (`GrinchChaosEngine`).
- **Merkle Ledger**: ledger inmutable con SHA-256 (`MerkleLedger`).
- **WebSocket**: telemetría en tiempo real RFC 6455 (`reto.extension.websocket`).
- **Caos**: inyección determinista de latencia, desconexión y corrupción.
- **K8s**: operador con CRD y bucle de reconciliación/auto-reparación (`ElfoOperator`).
- **WASM**: puente de lógica pura JDK-neutral con hash FNV-1a (`WasmBridge`).

**3. Ecosistema Full-Stack / CLI** — radar 3D con SSE y Three.js, dashboard HTTP cuántico y la **consola interactiva `ConsolaNexus`**.

---

## 🛰️ Subsistema WebSocket

**`reto.extension.websocket`** expone telemetría de agentes en tiempo real bajo el endpoint **`/ws/telemetry`**.

- **Servidor nativo en Java puro**: sin Spring, sin Netty, sin librerías externas.
- **RFC 6455 implementado a mano**: handshake `Sec-WebSocket-Accept` = `Base64(SHA-1(key + GUID))` y codificación de *text frames* (`0x81`, longitud corta y extendida).
- **Arquitectura limpia**: `TelemetryMessage` (`record` inmutable con JSON propio), `TelemetryService` (flujo simulado determinista) y `TelemetryWebSocketServer`.

```java
public record TelemetryMessage(String id, String metricName, double value, Instant timestamp) { }
```

> 13 tests: handshake WebSocket **real** con `java.net.http.WebSocket`, framing, validación y parada limpia.

---

## 🗺️ Radares Tácticos 3D: Paraguay y España

**Módulos de visualización cartográfica holográfica en tiempo real (`/paraguay` y `/espana`)**:

- **Núcleo Compartido (`static/js/mapa-geo.js`)**: módulo modular parametrizado que unifica la extrusión 3D, proyecciones d3-geo, etiquetas polylabel, trineo procedural y HUDs de telemetría.
- **España 3D (`/espana`)**: 52 entidades provinciales (50 provincias + Ceuta y Melilla) con proyección dual para la Península y recuadro inferior izquierdo dedicado a las Islas Canarias.
- **Telemetría en Vivo**: streaming por SSE y WebSockets RFC 6455 con tramas estandarizadas de posición, velocidad, altitud y regalos entregados.
- **Pruebas y Evidencias**: validado mediante `MapaParaguayTest` y `MapaEspanaTest` (276 tests en verde, capturas en `docs/`).

**`reto.extension.cli.ConsolaNexus`** es una consola interactiva que orquesta los motores del ecosistema.

- **Argumentos inmutables**: `record ArgumentosCli` y `ComandoCli`, con parser propio de `--clave` / `--clave=valor`.
- **Orquestación de motores**: los comandos llaman a los motores **reales** (`GrinchChaosEngine`, `MerkleLedger`, `TelemetryService`).
- **Patrón REPL**: bucle `nexus> ` con parada en `exit`, con `PrintStream` **inyectado** para testear sin tocar `System.out`.

| Comando | Función |
|---|---|
| `status` | estado de agentes/nodos P2P |
| `chaos` | inyecta caos y valida consenso BFT |
| `bft` | verifica el ledger de Merkle |
| `telemetria` | lee N muestras de telemetría |
| `help` / `exit` | manual interactivo / cierre |

---

## ⚙️ Automatización

El script maestro **`dulcenavidad.ps1`** ejecuta el flujo de validación industrial en tres fases:

1. **Simetría binaria** — `bash scripts/comprobar-estructura` (main ↔ solutions).
2. **Modo reto** — `mvn clean test` (256 tests) con corte si falla.
3. **Modo soluciones** — `mvn -Psoluciones test`, con salida `BUILD SUCCESS`.

```powershell
bash scripts/comprobar-estructura
mvn clean test
mvn -Psoluciones test
```

- ✅ **Corte temprano**: `$LASTEXITCODE` detiene el flujo ante cualquier fallo.
- 🤖 **CI/CD**: GitHub Actions (`validar-soluciones` bloqueante + `progreso`) y certificado de vuelo.

> *"¡Todos los sistemas funcionan al 100 % — BUILD SUCCESS!"*
