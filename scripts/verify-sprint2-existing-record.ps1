param(
    [string] $BaseUrl = 'http://127.0.0.1:8080'
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Net.Http

function Invoke-Api {
    param(
        [System.Net.Http.HttpClient] $Client,
        [System.Net.Http.HttpMethod] $Method,
        [string] $Path,
        [object] $Body = $null,
        [string] $CsrfToken = $null
    )
    $request = [System.Net.Http.HttpRequestMessage]::new($Method, $Path)
    $response = $null
    try {
        if ($null -ne $Body) {
            $json = ConvertTo-Json -InputObject $Body -Compress -Depth 6
            $request.Content = [System.Net.Http.StringContent]::new($json, [System.Text.Encoding]::UTF8, 'application/json')
            $json = $null
        }
        if (-not [string]::IsNullOrWhiteSpace($CsrfToken)) {
            [void] $request.Headers.TryAddWithoutValidation('X-XSRF-TOKEN', $CsrfToken)
        }
        $response = $Client.SendAsync($request).GetAwaiter().GetResult()
        $rawBody = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        $parsedBody = $null
        if (-not [string]::IsNullOrWhiteSpace($rawBody)) {
            try { $parsedBody = ConvertFrom-Json -InputObject $rawBody } catch { $parsedBody = $null }
        }
        return [pscustomobject] @{ Status = [int]$response.StatusCode; Body = $rawBody; Json = $parsedBody }
    }
    catch {
        return [pscustomobject] @{ Status = 0; Body = ''; Json = $null }
    }
    finally {
        if ($null -ne $response) { $response.Dispose() }
        $request.Dispose()
        $rawBody = $null
        $parsedBody = $null
    }
}

function Write-Status {
    param([string] $Name, [int] $Code)
    Write-Host ("{0}: HTTP {1:D3}" -f $Name, $Code)
}

function Stop-OnFailure {
    param([object] $Response, [string] $Name, [switch] $ShowBody)
    if ($Response.Status -ne 200) {
        if ($ShowBody -and -not [string]::IsNullOrWhiteSpace($Response.Body)) {
            Write-Host ("{0} response body: {1}" -f $Name, $Response.Body)
        }
        exit 1
    }
}

$handler = [System.Net.Http.HttpClientHandler]::new()
$handler.CookieContainer = [System.Net.CookieContainer]::new()
$handler.UseCookies = $true
$client = [System.Net.Http.HttpClient]::new($handler)
$client.BaseAddress = [Uri] $BaseUrl
$client.Timeout = [TimeSpan]::FromSeconds(30)
$securePassword = $null
$passwordBstr = [IntPtr]::Zero
$plainPassword = $null

try {
    $username = Read-Host 'Application username'
    $securePassword = Read-Host 'Application password' -AsSecureString

    $csrf = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) '/api/auth/csrf'
    Write-Status 'auth.csrf.initial' $csrf.Status
    Stop-OnFailure $csrf 'auth.csrf.initial'
    $csrfToken = [string]$csrf.Json.token

    $passwordBstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordBstr)
    $loginBody = @{ username = $username; password = $plainPassword }
    $login = Invoke-Api $client ([System.Net.Http.HttpMethod]::Post) '/api/auth/login' $loginBody $csrfToken
    $loginBody = $null
    $plainPassword = $null
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordBstr)
    $passwordBstr = [IntPtr]::Zero
    Write-Status 'auth.login' $login.Status
    Stop-OnFailure $login 'auth.login'

    $csrf = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) '/api/auth/csrf'
    Write-Status 'auth.csrf.afterLogin' $csrf.Status
    Stop-OnFailure $csrf 'auth.csrf.afterLogin'
    $csrfToken = [string]$csrf.Json.token

    $options = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) '/api/requisitions/options'
    Write-Status 'requisitions.options' $options.Status
    Stop-OnFailure $options 'requisitions.options'
    if (-not (@($options.Json.statuses | Where-Object { $_.code -eq 'SUBMITTED' }).Count -gt 0)) { exit 1 }

    $detail = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) '/api/requisitions/21'
    Write-Status 'requisitions.detail.before' $detail.Status
    Stop-OnFailure $detail 'requisitions.detail.before'
    if (-not (@($detail.Json.items | Where-Object { [long]$_.id -eq 21 }).Count -gt 0)) { exit 1 }

    $itemUpdateBody = @{
        lineNumber = 1
        itemDescription = 'Fictional radio batteries updated for verification'
        quantity = [decimal]3.000
        unitOfMeasure = 'each'
        estimatedUnitCost = [decimal]12.50
    }
    $itemUpdate = Invoke-Api $client ([System.Net.Http.HttpMethod]::Put) '/api/requisitions/21/items/21' $itemUpdateBody $csrfToken
    Write-Status 'requisitionItems.update' $itemUpdate.Status
    Stop-OnFailure $itemUpdate 'requisitionItems.update' -ShowBody

    $statusUpdate = Invoke-Api $client ([System.Net.Http.HttpMethod]::new('PATCH')) '/api/requisitions/21/status' @{ statusCode = 'SUBMITTED' } $csrfToken
    Write-Status 'requisitions.status' $statusUpdate.Status
    Stop-OnFailure $statusUpdate 'requisitions.status' -ShowBody

    $finalDetail = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) '/api/requisitions/21'
    Write-Status 'requisitions.detail.final' $finalDetail.Status
    Stop-OnFailure $finalDetail 'requisitions.detail.final'
    $finalItem = @($finalDetail.Json.items | Where-Object { [long]$_.id -eq 21 })
    if (($finalItem.Count -ne 1) -or ($finalDetail.Json.statusCode -ne 'SUBMITTED') -or
            ($finalItem[0].itemDescription -ne 'Fictional radio batteries updated for verification') -or
            ([decimal]$finalItem[0].quantity -ne [decimal]3.000)) { exit 1 }
}
finally {
    if ($passwordBstr -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordBstr) }
    if ($null -ne $securePassword) { $securePassword.Dispose() }
    $plainPassword = $null
    $client.Dispose()
    $handler.Dispose()
}
