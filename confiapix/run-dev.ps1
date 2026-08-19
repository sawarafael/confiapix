# ConfiaPix - subir API local (Postman / Swagger)
# Usa H2 em memoria — nao precisa de PostgreSQL

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

if (-not $env:JAVA_HOME) {
    $candidates = @(
        "C:\Program Files\Java\jdk-21.0.10",
        "C:\Program Files\Eclipse Adoptium\jdk-21*",
        "C:\Program Files\Microsoft\jdk-21*"
    )
    foreach ($pattern in $candidates) {
        $found = Get-Item $pattern -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) {
            $env:JAVA_HOME = $found.FullName
            break
        }
    }
}

if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    Write-Host "JAVA_HOME nao encontrado. Instale JDK 21 ou defina JAVA_HOME." -ForegroundColor Red
    exit 1
}

$projectDir = $PSScriptRoot
$java = Join-Path $env:JAVA_HOME "bin\java.exe"
$wrapperJar = Join-Path $projectDir ".mvn\wrapper\maven-wrapper.jar"

if (-not (Test-Path $wrapperJar)) {
    Write-Host "Baixando maven-wrapper.jar..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path (Split-Path $wrapperJar) | Out-Null
    Invoke-WebRequest -Uri "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar" -OutFile $wrapperJar
}

Write-Host "JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Cyan
Write-Host "Iniciando ConfiaPix (perfil dev, porta 8080)..." -ForegroundColor Green
Write-Host "Swagger: http://localhost:8080/swagger-ui.html" -ForegroundColor Yellow
Write-Host "Postman: POST http://localhost:8080/auth/register" -ForegroundColor Yellow

& $java "-Dmaven.multiModuleProjectDirectory=$projectDir" "-classpath" $wrapperJar org.apache.maven.wrapper.MavenWrapperMain spring-boot:run "-Dspring-boot.run.profiles=dev"
