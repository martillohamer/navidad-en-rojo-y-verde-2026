# &#129302; HANDOFF – Navidad en Rojo y Verde 2026

Guía técnica para quien retome el proyecto (personas o IAs). Lee esto antes de tocar nada.

## Qué es
Adviento de retos de lógica en Java para principiantes, en castellano. Cada día = un test en rojo que el alumno pone en verde. Repositorio: `github.com/martillohamer/navidad-en-rojo-y-verde-2026`.

## Estado actual
- Días 1 a 16: esqueleto, test y solución de referencia ✅.
- Días 17 a 24: **todavía no existen** ⏳.
- Enunciados: los de los días 1 a 3 son reales. Los de los días 4 a 16 son texto genérico de relleno y hay que reescribirlos (qué hace la función, ejemplos, pista, reto extra).

## Stack
Java 21, Maven, JUnit 5 (5.10.2), GitHub Actions. Sin más dependencias.

## Estructura (y por qué)
```text
src/main/java/reto/diaNN/       esqueletos (lanzan UnsupportedOperationException) + clases de apoyo
src/test/java/reto/diaNN/       tests JUnit de cada día
src/solutions/java/reto/diaNN/  soluciones: MISMO nombre y paquete que el esqueleto + copia idéntica de las clases de apoyo
enunciados/diaNN-nombre/        enunciado.md de cada día
plantilla-reto/                 plantilla de enunciado, esqueleto y test
scripts/                        progreso, progreso.bat, comprobar-estructura
```
Decisión: se usa la estructura estándar de Maven para que VS Code, IntelliJ y Maven la entiendan sin plugins. Una carpeta de tests con nombres como `dia01-el-despertar` y paquete `reto.dia01` hacía que el editor marcara errores (el paquete debe coincidir con la carpeta).

## Cómo cambia entre rojo y verde
El `pom.xml` usa la propiedad `clases.dir` como `sourceDirectory`:
- Por defecto vale `src/main/java` (esqueletos, rojo).
- El perfil `soluciones` la cambia a `src/solutions/java` (verde).
Los tests son los mismos en los dos modos.

## Comandos
| Qué | Comando |
|---|---|
| Ver los retos en rojo | `mvn test` |
| Un solo día | `mvn test -Dtest=Dia04Test` |
| Validar las soluciones (debe ir todo en verde) | `mvn -Psoluciones test` |
| Comprobar que esqueletos y soluciones están emparejados | `bash scripts/comprobar-estructura` |
| Ver el árbol de progreso | `./scripts/progreso` |

## Reglas de trabajo
- Nunca push directo a `main`: rama + Pull Request.
- Commits y comunicación en castellano.
- TDD: primero el test.
- No fusionar con el CI en rojo.
- No dar nada por terminado sin enseñar la salida real de `mvn` y de `git status`.

## Checklist para añadir un día nuevo (NN)
1. Esqueleto en `src/main/java/reto/diaNN/Clase.java` (lanza `UnsupportedOperationException`).
2. Si hacen falta clases de apoyo (excepciones, modelos, interfaces): en `src/main/java/reto/diaNN/` y una copia **idéntica** en `src/solutions/java/reto/diaNN/`.
3. Test en `src/test/java/reto/diaNN/DiaNNTest.java`.
4. Solución en `src/solutions/java/reto/diaNN/Clase.java` (mismo nombre y paquete que el esqueleto).
5. Enunciado en `enunciados/diaNN-nombre/enunciado.md`: historia, piensa antes de programar, pseudocódigo, pista, dónde programar, cómo comprobarlo y reto extra.
6. Comprobar: `mvn test` (el día en rojo), `mvn -Psoluciones test` (todo verde) y `bash scripts/comprobar-estructura`.
7. Añadir la fila al calendario del `README.md`.
8. Abrir el Pull Request.

## Trampas conocidas
- **PowerShell 5.1 y los emojis:** al escribir ficheros con emojis o tildes puede salir `??`. Escribir con `[System.IO.File]::WriteAllText(ruta, texto, (New-Object System.Text.UTF8Encoding($false)))` y comprobar el resultado.
- **Clases de apoyo duplicadas:** viven en `src/main/java` y en `src/solutions/java`. Si cambias una, cambia las dos. `scripts/comprobar-estructura` (y el CI) lo vigilan.
- **VS Code:** tras cambiar la estructura, ejecutar `Java: Clean Java Language Server Workspace` y recargar.
- **Verificar siempre:** ya hubo un resumen que anunciaba los días 17 a 24 sin que existieran. Comprobar con `git ls-files`.

## CI
- `validar-soluciones`: comprueba la estructura y ejecuta `mvn -Psoluciones test`. Debe ir en verde.
- `progreso`: ejecuta `mvn test` ignorando fallos y muestra el progreso. Los rojos son esperados.
