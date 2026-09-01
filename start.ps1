# Levanta todo el proyecto IER en Windows: PostgreSQL (Docker) + backend (:8080) + frontend (:4200).
# Uso:  powershell -ExecutionPolicy Bypass -File start.ps1   (o .\start.ps1 desde PowerShell)
# Ctrl+C detiene backend y frontend; el contenedor de BD queda corriendo.
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

function Test-Backend {
    try {
        Invoke-WebRequest -Uri "http://localhost:8080/api/avisos" -UseBasicParsing -TimeoutSec 2 | Out-Null
        return $true
    } catch { return $false }
}

# ---------- 1. Base de datos ----------
docker start ier-postgres 2>$null | Out-Null
if ($LASTEXITCODE -ne 0) {
    docker run --name ier-postgres -e POSTGRES_PASSWORD=jazmin -e POSTGRES_DB=IER `
        -p 5432:5432 -d postgres:16 | Out-Null
}
do {
    Start-Sleep -Seconds 1
    docker exec ier-postgres pg_isready -U postgres -q 2>$null
} while ($LASTEXITCODE -ne 0)
Write-Host "OK PostgreSQL listo (contenedor ier-postgres)"

# ---------- 2. Backend ----------
$backend = $null
if (Test-Backend) {
    Write-Host "OK Backend ya estaba corriendo en :8080"
} else {
    $log = Join-Path $env:TEMP "ier-backend.log"
    $backend = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/c", "mvnw.cmd spring-boot:run" `
        -WorkingDirectory (Join-Path $PSScriptRoot "backend") `
        -RedirectStandardOutput $log -RedirectStandardError (Join-Path $env:TEMP "ier-backend-err.log") `
        -WindowStyle Hidden -PassThru
    Write-Host "  Backend arrancando (log: $log)..."
    $listo = $false
    for ($i = 0; $i -lt 60; $i++) {
        Start-Sleep -Seconds 2
        if (Test-Backend) { $listo = $true; break }
    }
    if (-not $listo) {
        Write-Error "El backend no respondio; revisa $log"
        taskkill /PID $backend.Id /T /F 2>$null | Out-Null
        exit 1
    }
    Write-Host "OK Backend listo en http://localhost:8080"
}

# ---------- 3. Frontend (primer plano) ----------
try {
    Set-Location (Join-Path $PSScriptRoot "frontend")
    if (-not (Test-Path "node_modules")) { npm install }
    Write-Host "OK Abriendo frontend en http://localhost:4200 (Ctrl+C para detener todo)"
    npm start
} finally {
    # Al salir (Ctrl+C incluido), detener el backend que este script haya lanzado
    if ($backend) { taskkill /PID $backend.Id /T /F 2>$null | Out-Null }
}
