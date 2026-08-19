$ErrorActionPreference = "Stop"

$BaseUrl = if ($env:CONFIAPIX_API_URL) { $env:CONFIAPIX_API_URL } else { "http://localhost:8080" }
$PostgresContainer = if ($env:CONFIAPIX_POSTGRES_CONTAINER) { $env:CONFIAPIX_POSTGRES_CONTAINER } else { "confiapix-postgres" }

$PlatformUser = @{
    tenantName = "ConfiaPix Plataforma"
    name       = "Admin Plataforma"
    email      = "admin@confiapix.platform"
    password   = "123456"
}

$CompanyUser = @{
    tenantName = "Empresa Demo LTDA"
    name       = "Admin Empresa"
    email      = "admin@empresa.demo"
    password   = "123456"
}

function Write-Step([string]$Message) {
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Path,
        [object]$Body = $null,
        [string]$Token = $null
    )

    $headers = @{ "Content-Type" = "application/json" }
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }

    $params = @{
        Uri         = "$BaseUrl$Path"
        Method      = $Method
        Headers     = $headers
        ErrorAction = "Stop"
    }

    if ($null -ne $Body) {
        $params["Body"] = ($Body | ConvertTo-Json -Depth 6)
    }

    try {
        return Invoke-RestMethod @params
    }
    catch {
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $details = $reader.ReadToEnd()
            throw "$Method $Path falhou: $details"
        }
        throw
    }
}

function Register-IfNeeded([hashtable]$User) {
    try {
        Invoke-Api -Method "POST" -Path "/auth/register" -Body $User | Out-Null
        Write-Host "Conta criada: $($User.email)" -ForegroundColor Green
    }
    catch {
        if ($_.ToString() -match "cadastrado|already|409|400") {
            Write-Host "Conta ja existe: $($User.email)" -ForegroundColor Yellow
        }
        else {
            throw
        }
    }
}

function Get-Token([hashtable]$User) {
    $response = Invoke-Api -Method "POST" -Path "/auth/login" -Body @{
        email    = $User.email
        password = $User.password
    }
    return $response.data.token
}

function Set-PlatformOperator {
    $sql = @"
UPDATE tenants
SET platform_operator = FALSE
WHERE id IN (
    SELECT tenant_id FROM users WHERE email <> 'admin@confiapix.platform'
);

UPDATE tenants t
SET platform_operator = TRUE,
    name = 'ConfiaPix Plataforma',
    plan = 'ENTERPRISE',
    active = TRUE
FROM users u
WHERE u.tenant_id = t.id
  AND u.email = 'admin@confiapix.platform';
"@

    docker exec -i $PostgresContainer psql -U confiapix -d confiapix -v ON_ERROR_STOP=1 -c $sql | Out-Null
    Write-Host "Tenant de plataforma marcado como operador." -ForegroundColor Green
}

function Save-Integration {
    param(
        [string]$Token,
        [string]$Provider,
        [hashtable]$Payload
    )

    Invoke-Api -Method "PUT" -Path "/api/v1/integrations/$Provider/credentials" -Body $Payload -Token $Token | Out-Null
    Write-Host "Integracao $Provider salva." -ForegroundColor Green
}

function Seed-DemoPixData {
    $sqlFile = Join-Path $PSScriptRoot "seed-demo-pix.sql"
    if (-not (Test-Path $sqlFile)) {
        throw "Arquivo seed-demo-pix.sql nao encontrado em $PSScriptRoot"
    }

    Get-Content $sqlFile -Raw | docker exec -i $PostgresContainer psql -U confiapix -d confiapix -v ON_ERROR_STOP=1 | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Falha ao inserir dados PIX/conciliacao (psql exit code $LASTEXITCODE)"
    }
    Write-Host "Transacoes PIX e conciliacoes demo inseridas." -ForegroundColor Green
}

Write-Host "ConfiaPix - seed de dados de demonstracao" -ForegroundColor White
Write-Host "API: $BaseUrl"

Write-Step "Verificando Docker Postgres"
docker ps --filter "name=$PostgresContainer" --format "{{.Names}}" | Select-String $PostgresContainer | Out-Null
if (-not $?) {
    throw "Container '$PostgresContainer' nao esta rodando. Execute: docker compose up -d"
}

Write-Step "Verificando API"
try {
    Invoke-WebRequest -Uri "$BaseUrl/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"probe","password":"probe"}' -UseBasicParsing | Out-Null
}
catch {
    if ($_.Exception.Response.StatusCode.value__ -ne 401 -and $_.Exception.Response.StatusCode.value__ -ne 400) {
        throw "API indisponivel em $BaseUrl. Execute: docker compose up -d"
    }
}

Write-Step "Criando contas de demonstracao"
Register-IfNeeded -User $PlatformUser
Register-IfNeeded -User $CompanyUser

Write-Step "Configurando administrador da plataforma"
Set-PlatformOperator

Write-Step "Autenticando empresa demo"
$companyToken = Get-Token -User $CompanyUser

Write-Step "Inserindo integracoes bancarias de amostragem"
Save-Integration -Token $companyToken -Provider "STONE" -Payload @{
    clientSecret = "sk_demo_confiapex_sample_only_01"
    accountRef   = "194047458"
    merchantRef  = "DEMO-STONECODE-001"
    active       = $true
    config       = @{
        authMode      = "API_KEY"
        businessModel = "GATEWAY"
    }
}

Save-Integration -Token $companyToken -Provider "077" -Payload @{
    clientId     = "demo-inter-client-id"
    clientSecret = "demo-inter-client-secret"
    accountRef   = "demo-inter-account-001"
    active       = $true
}

Save-Integration -Token $companyToken -Provider "336" -Payload @{
    clientId     = "demo-c6-client-id"
    clientSecret = "demo-c6-client-secret"
    accountRef   = "demo-c6-account-001"
    active       = $false
}

Write-Step "Inserindo transacoes PIX e conciliacoes de amostragem"
Seed-DemoPixData

Write-Host ""
Write-Host "Seed concluido com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "Contas de acesso:" -ForegroundColor White
Write-Host "  Admin plataforma (total): admin@confiapix.platform / 123456"
Write-Host "  Empresa demo:            admin@empresa.demo / 123456"
Write-Host ""
Write-Host "Integracoes criadas na Empresa Demo LTDA:" -ForegroundColor White
Write-Host "  - Stone (ativa, API_KEY, conta 194047458)"
Write-Host "  - Banco Inter / COMPE 077 (ativa, credenciais demo)"
Write-Host "  - C6 Bank / COMPE 336 (inativa, credenciais demo)"
Write-Host ""
Write-Host "Dados PIX/conciliacao na Empresa Demo LTDA:" -ForegroundColor White
Write-Host "  - 8 transacoes PIX (txid DEMO-TX-001 a DEMO-TX-008)"
Write-Host "  - 7 conciliacoes: 3 MATCHED, 2 DIVERGENT, 2 PENDING"
Write-Host "  - 1 transacao sem conciliacao (DEMO-TX-005)"
Write-Host ""
Write-Host "Obs.: credenciais Stone/Inter/C6 sao ficticias para UI. Troque pelas reais antes de testar conexao." -ForegroundColor Yellow
