#requires -Version 7.0
<#
    deploy.ps1 - Despliegue y validacion del Polo Norte
    Ejecuta en secuencia: limpieza, suite estandar, suite de soluciones y estado.
#>

$ErrorActionPreference = 'Stop'

if (-not $env:MAVEN_HOME) {
    $env:MAVEN_HOME = "C:\Users\astur\.m2\wrapper\dists\apache-maven-3.9.9\3477a4f1"
}
$mvn = Join-Path $env:MAVEN_HOME 'bin\mvn.cmd'
if (-not (Test-Path -LiteralPath $mvn)) {
    Write-Error "No se encontro Maven en '$mvn'. Define MAVEN_HOME correctamente."
}

function Invoke-Paso {
    param(
        [Parameter(Mandatory)][string]$Titulo,
        [Parameter(Mandatory)][string[]]$ArgumentosMvn
    )
    Write-Host ""
    Write-Host "=== $Titulo ===" -ForegroundColor Cyan
    & $mvn -B @ArgumentosMvn
    if ($LASTEXITCODE -ne 0) {
        Write-Host "FALLO: $Titulo" -ForegroundColor Red
        exit $LASTEXITCODE
    }
    Write-Host "OK: $Titulo" -ForegroundColor Green
}

Write-Host "🎄 Despliegue del Polo Norte - navidad-en-rojo-y-verde-2026" -ForegroundColor Yellow

Invoke-Paso -Titulo "Paso 1/4: limpieza" -ArgumentosMvn @('clean')
Invoke-Paso -Titulo "Paso 2/4: suite estandar (mvn clean test)" -ArgumentosMvn @('clean', 'test')
Invoke-Paso -Titulo "Paso 3/4: suite de soluciones (mvn -Psoluciones clean test)" `
    -ArgumentosMvn @('-Psoluciones', 'clean', 'test')

Write-Host ""
Write-Host "=== Paso 4/4: estado del progreso ===" -ForegroundColor Cyan
& bash scripts/progreso

Write-Host ""
Write-Host "🎁 Despliegue completado: verde absoluto." -ForegroundColor Green
