# HANDOFF Técnico — Navidad en Rojo y Verde 2026

Este documento constituye el **manual de transferencia técnica definitivo (Handoff)** para el sistema **Navidad en Rojo y Verde 2026**. Está diseñado para proporcionar contexto total y autónomo a cualquier desarrollador humano o agente de Inteligencia Artificial (LLM) que deba comprender, ejecutar, mantener o extender la base de código.

---

## 1. Visión General y Propósito del Proyecto

- **Nombre del Proyecto**: *Navidad en Rojo y Verde 2026* (`navidad-en-rojo-y-verde-2026`).
- **Propósito**: Plataforma táctica multirregión 3D en tiempo real para el seguimiento de la operación logística navideña, integrando cartografía digital vectorial de alta precisión, proyecciones geográficas complejas, simulación de vehículos aéreos (trineo), telemetría por streaming y un ecosistema de alta fiabilidad en Java.
- **Stack Tecnológico**:
  - **Backend**: Java 21, Spring Boot y servidor HTTP nativo concurrente (`QuantumDashboardServer.java`).
  - **Frontend / 3D**: Three.js (módulos offline con `importmap`), D3-Geo (`d3-geo.esm.js`), Polylabel (`polylabel.esm.js`), CSS2DRenderer, HTML5 y CSS3 avanzado.
  - **Transporte / Telemetría**: Server-Sent Events (SSE) y WebSockets (RFC 6455).
  - **Build & Dependency Management**: Apache Maven 3.9+ (con soporte para perfiles duales `main` y `soluciones`).
  - **QA & Testing**: JUnit 5, Mockito, Playwright (capturas E2E).
  - **CI/CD**: GitHub Actions en entorno Ubuntu con JDK 21 (Temurin).

---

## 2. Arquitectura y Estructura del Código

### A. Simetría Binaria Estricta (50/50)
El repositorio mantiene un diseño dual mediante perfiles de Maven:
- **`src/main/java`**: Contiene el código de producción, esqueletos y clases de apoyo.
- **`src/solutions/java`**: Contiene las soluciones completas e implementaciones de referencia.
- **Invariante**: Todo fichero Java en `src/main/java` **debe** tener su gemelo byte-a-byte idéntico en `src/solutions/java`. La conformidad estructural se valida de forma automatizada mediante el script `scripts/comprobar-estructura`.

### B. Subsistemas Cartográficos Activos
1. **Paraguay 3D (`/paraguay`)**:
   - Cartografía oficial extraída de `geoBoundaries` (`PRY ADM1`, 18 entidades administrativas).
   - Simplificada al 5% con `mapshaper` (<300 KB), con reorientación de anillos en convención d3-geo (exterior en sentido horario, agujeros antihorarios).
   - Hidrografía principal (ríos Paraguay, Paraná y Pilcomayo) recortada desde Natural Earth 10m.
2. **España 3D (`/espana`)**:
   - 52 entidades provinciales (50 provincias + Ceuta y Melilla) basadas en Natural Earth Admin-1 (CC BY 4.0).
   - Proyección dual avanzada: Península, Baleares y Norte de África centradas en la vista principal, complementadas con un **recuadro (inset) interactivo para las Islas Canarias** en la esquina inferior izquierda.
3. **Módulo Frontend Compartido (`src/main/resources/static/js/mapa-geo.js`)**:
   - Núcleo cartográfico reutilizable que centraliza la carga de ficheros GeoJSON, proyecciones geográficas, extrusión poligonal 3D (`ExtrudeGeometry`), etiquetas dinámicas con `polylabel`, animación de la ruta del trineo (`CatmullRomCurve3`), estela de partículas y actualización reactiva del panel HUD.

### C. Servidor Backend (`QuantumDashboardServer.java`)
- Servidor HTTP embebido en Java puro basado en `com.sun.net.httpserver.HttpServer`.
- **Rutas dedicadas**: `/`, `/nexus`, `/paraguay`, `/espana`, `/health`, `/api/telemetria`, `/api/vuelo/stream`, `/vendor/*`, `/data/*`.
- **Blindaje de Caché**: Memoria estructurada mediante `ConcurrentHashMap` con clave compuesta estricta `rutaFichero + "|" + recurso`, evitando colisiones absolutas entre activos estáticos de distintas regiones.
- **Control de Seguridad**: Rechazo explícito de rutas con secuencias de retroceso (`..`).

---

## 3. Garantía de Calidad (QA) y Cobertura

- **Suite de Pruebas**: **276 tests unitarios e de integración** pasando al 100% (`BUILD SUCCESS`).
- **Pruebas Clave**:
  - `MapaParaguayTest`: Valida geometrías, 18 entidades, bounding boxes, contención de capitales y consistencia de proyección.
  - `MapaEspanaTest`: Valida el endpoint HTTP 200 de `/espana`, tipos MIME y la integridad del GeoJSON de 52 provincias.
  - `QuantumDashboardServerTest`: Verifica las rutas HTTP, estáticos, tipos de contenido y bloqueo de traversals.
- **Evidencias Visuales E2E**: Capturas de pantalla automatizadas tomadas con Playwright en `docs/paraguay-radar.png` y `docs/espana-radar.png`.

---

## 4. Integración Continua (CI/CD)

- Configurado mediante GitHub Actions en `.github/workflows/ci.yml`.
- Se dispara automáticamente en cada `push` (`main`, `feat/*`) y `pull_request` (`main`).
- Pasos del pipeline:
  1. Descarga del repositorio (`actions/checkout@v4`).
  2. Configuración de Temurin JDK 21 (`actions/setup-java@v4`) con caché Maven activada.
  3. Ejecución de la compilación y testeo del perfil predeterminado (`mvn clean test`).
  4. Ejecución de la compilación y testeo del perfil de soluciones (`mvn -Psoluciones clean test`).

---

## 5. Guía de Inicio Rápido para Desarrolladores y Agentes IA

### Comandos Esenciales (PowerShell 7+ / Bash):
- **Ejecutar tests en perfil `main`**:
  ```powershell
  mvn clean test
  ```
- **Ejecutar tests en perfil `solutions`**:
  ```powershell
  mvn -Psoluciones clean test
  ```
- **Empaquetado completo de producción (JAR)**:
  ```powershell
  mvn clean package
  ```
- **Comprobar simetría estructural 50/50**:
  ```bash
  bash scripts/comprobar-estructura
  ```

### Protocolo para Extender el Sistema (Añadir una Nueva Región):
1. **Datos**: Añadir el GeoJSON optimizado (<600 KB) en `src/main/resources/static/data/`.
2. **Vista**: Crear la plantilla HTML (ej. `src/main/resources/static/italia.html`) importando y configurando `mapa-geo.js`.
3. **Backend**: Registrar la ruta (`/italia`), su recurso estático y su handler en `QuantumDashboardServer.java` (**replicando el cambio de forma idéntica en `src/main/java` y `src/solutions/java`**).
4. **Testing**: Añadir una clase de prueba unitaria en `src/test/java/reto/extension/dashboard/` (ej. `MapaItaliaTest.java`) verificando el endpoint y la estructura de datos.
5. **Verificación**: Comprobar la simetría (`scripts/comprobar-estructura`) y ejecutar `mvn clean test`.
