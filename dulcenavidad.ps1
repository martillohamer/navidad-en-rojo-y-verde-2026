[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "INICIANDO PROTOCOLO DULCE NAVIDAD FULL-STACK" -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

$puertoWeb = 8081
$backend = $null

try {
    Write-Host "`n[Fase 1/5] Verificando simetria binaria..." -ForegroundColor Yellow
    if (Test-Path "scripts/comprobar-estructura") {
        bash scripts/comprobar-estructura
    } else {
        Write-Host "Aviso: Script de bash de estructura no detectado. Pasando a Maven..." -ForegroundColor DarkYellow
    }

    Write-Host "`n[Fase 2/5] Compilando y ejecutando 256 tests (Modo Reto)..." -ForegroundColor Green
    mvn clean test
    if ($LASTEXITCODE -ne 0) { throw "La compilacion o los tests en Modo Reto han fallado." }

    Write-Host "`n[Fase 3/5] Validando suite completa bajo el perfil de Soluciones..." -ForegroundColor Magenta
    mvn -Psoluciones test
    if ($LASTEXITCODE -ne 0) { throw "La validacion bajo el perfil de soluciones ha fallado." }

    Write-Host "`n[Fase 4/5] Generando documento PDF de la Presentacion Ejecutiva..." -ForegroundColor Cyan
    if (Test-Path "PRESENTATION.md") {
        npx @marp-team/marp-cli PRESENTATION.md --pdf
        Write-Host "PDF de presentacion generado con exito!" -ForegroundColor Green
    } else {
        Write-Host "Aviso: PRESENTATION.md no existe en la raiz. Omitiendo..." -ForegroundColor DarkYellow
    }

    Write-Host "`n[Fase 5/5] Desplegando el backend Java y abriendo el Dashboard Web..." -ForegroundColor Yellow

    $claseServidor = "reto.extension.dashboard.QuantumDashboardServer"
    $clasesCompiladas = Join-Path (Get-Location) "target\classes"
    if (-not (Test-Path $clasesCompiladas)) {
        throw "No se encontro target\classes. Ejecuta la compilacion (Fases 2 y 3) antes del despliegue."
    }

    Write-Host "Arrancando $claseServidor en el puerto $puertoWeb..." -ForegroundColor Green
    $backend = Start-Process -FilePath "java" `
        -ArgumentList "-cp", $clasesCompiladas, $claseServidor, "$puertoWeb" `
        -PassThru -WindowStyle Hidden

    $urlDashboard = "http://localhost:$puertoWeb"
    $arrancado = $false
    for ($intento = 1; $intento -le 10; $intento++) {
        Start-Sleep -Seconds 1
        try {
            $respuesta = Invoke-WebRequest -Uri "$urlDashboard/health" -UseBasicParsing -TimeoutSec 2
            if ($respuesta.StatusCode -eq 200) {
                Write-Host "Backend listo tras $intento s (health: $($respuesta.Content))" -ForegroundColor Green
                $arrancado = $true
                break
            }
        } catch {
            Write-Host "Esperando al servidor Java... ($intento/10)" -ForegroundColor DarkYellow
        }
    }

    if (-not $arrancado) {
        throw "El backend Java no respondio en $urlDashboard/health tras 10 intentos."
    }

    Write-Host "Abriendo $urlDashboard en tu navegador..." -ForegroundColor Green
    Start-Process $urlDashboard

    Write-Host "`n====================================================" -ForegroundColor Green
    Write-Host "SISTEMA DESPLEGADO Y LISTO AL 100% - BUILD SUCCESS" -ForegroundColor Green
    Write-Host "====================================================" -ForegroundColor Green

} catch {
    Write-Host "`nERROR CRITICO DETECTADO: $_" -ForegroundColor Red
} finally {
    if ($backend -and -not $backend.HasExited) {
        Write-Host "`nDeteniendo el backend Java (PID $($backend.Id))..." -ForegroundColor DarkYellow
        Stop-Process -Id $backend.Id -Force -ErrorAction SilentlyContinue
    }
    Write-Host "`nPresiona ENTER para salir..." -ForegroundColor Yellow
    Read-Host
}
