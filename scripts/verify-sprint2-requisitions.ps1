param(
    [string] $BaseUrl = 'http://127.0.0.1:8080'
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Net.Http

function Write-HttpStatus {
    param([string] $Operation, [int] $StatusCode)
    Write-Host ("{0}: HTTP {1:D3}" -f $Operation, $StatusCode)
}

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
            $json = ConvertTo-Json -InputObject $Body -Compress -Depth 8
            $request.Content = [System.Net.Http.StringContent]::new(
                $json, [System.Text.Encoding]::UTF8, 'application/json')
            $json = $null
        }
        if (-not [string]::IsNullOrWhiteSpace($CsrfToken)) {
            [void] $request.Headers.TryAddWithoutValidation('X-XSRF-TOKEN', $CsrfToken)
        }

        $response = $Client.SendAsync($request).GetAwaiter().GetResult()
        $status = [int] $response.StatusCode
        $content = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        $parsed = $null
        if (-not [string]::IsNullOrWhiteSpace($content)) {
            try { $parsed = ConvertFrom-Json -InputObject $content } catch { $parsed = $null }
        }
        return [pscustomobject] @{ Status = $status; Json = $parsed }
    }
    catch {
        return [pscustomobject] @{ Status = 0; Json = $null }
    }
    finally {
        if ($null -ne $response) { $response.Dispose() }
        $request.Dispose()
        $content = $null
        $parsed = $null
    }
}

function Stop-IfUnexpected {
    param([int] $Actual, [int[]] $Expected)
    if ($Actual -notin $Expected) { exit 1 }
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
$loginResponse = $null

try {
    $username = Read-Host 'Application username'
    $securePassword = Read-Host 'Application password' -AsSecureString

    # Obtain the initial CSRF cookie/token, then authenticate with that pair.
    $csrfResponse = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) '/api/auth/csrf'
    Write-HttpStatus 'auth.csrf.initial' $csrfResponse.Status
    Stop-IfUnexpected $csrfResponse.Status @(200)
    $csrfToken = [string] $csrfResponse.Json.token
    if ([string]::IsNullOrWhiteSpace($csrfToken)) { exit 1 }

    $passwordBstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordBstr)
    $loginBody = @{ username = $username; password = $plainPassword }
    $loginResponse = Invoke-Api $client ([System.Net.Http.HttpMethod]::Post) '/api/auth/login' $loginBody $csrfToken
    $loginBody = $null
    $plainPassword = $null
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordBstr)
    $passwordBstr = [IntPtr]::Zero
    Write-HttpStatus 'auth.login' $loginResponse.Status
    Stop-IfUnexpected $loginResponse.Status @(200)

    # Refresh after login in case authentication rotated the CSRF token.
    $csrfResponse = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) '/api/auth/csrf'
    Write-HttpStatus 'auth.csrf.afterLogin' $csrfResponse.Status
    Stop-IfUnexpected $csrfResponse.Status @(200)
    $csrfToken = [string] $csrfResponse.Json.token
    if ([string]::IsNullOrWhiteSpace($csrfToken)) { exit 1 }

    $me = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) '/api/auth/me'
    Write-HttpStatus 'auth.me' $me.Status
    Stop-IfUnexpected $me.Status @(200)

    $list = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) '/api/requisitions'
    Write-HttpStatus 'requisitions.list' $list.Status
    Stop-IfUnexpected $list.Status @(200)

    $options = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) '/api/requisitions/options'
    Write-HttpStatus 'requisitions.options' $options.Status
    Stop-IfUnexpected $options.Status @(200)
    $statuses = @($options.Json.statuses)
    if ($statuses.Count -eq 0) { exit 1 }
    $initialStatus = @($statuses | Where-Object { $_.code -eq 'DRAFT' } | Select-Object -First 1)
    if ($initialStatus.Count -eq 0) { $initialStatus = @($statuses | Select-Object -First 1) }
    $initialCode = [string] $initialStatus[0].code
    $nextStatus = @($statuses | Where-Object { $_.code -eq 'SUBMITTED' -and $_.code -ne $initialCode } | Select-Object -First 1)
    if ($nextStatus.Count -eq 0) { $nextStatus = @($statuses | Where-Object { $_.code -ne $initialCode } | Select-Object -First 1) }
    if ($nextStatus.Count -eq 0) { $nextCode = $initialCode } else { $nextCode = [string] $nextStatus[0].code }

    $suffix = Get-Date -Format 'yyyyMMddHHmmssfff'
    $requisitionNumber = "J14-REQ-VERIFY-$suffix"
    $neededByDate = (Get-Date).AddDays(30).ToString('yyyy-MM-dd')
    $createBody = @{
        requisitionNumber = $requisitionNumber
        title = 'Fictional Sprint 2 API verification'
        justification = 'Fictional test record created for requisition API verification.'
        neededByDate = $neededByDate
        statusCode = $initialCode
    }
    $created = Invoke-Api $client ([System.Net.Http.HttpMethod]::Post) '/api/requisitions' $createBody $csrfToken
    Write-HttpStatus 'requisitions.create' $created.Status
    Stop-IfUnexpected $created.Status @(201)
    $requisitionId = [long] $created.Json.id
    if ($requisitionId -lt 1) { exit 1 }

    $detail = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) "/api/requisitions/$requisitionId"
    Write-HttpStatus 'requisitions.detail' $detail.Status
    Stop-IfUnexpected $detail.Status @(200)

    $updateBody = @{
        requisitionNumber = $requisitionNumber
        title = 'Fictional Sprint 2 API verification updated'
        justification = 'Fictional test record updated through the requisition API.'
        neededByDate = $neededByDate
    }
    $updated = Invoke-Api $client ([System.Net.Http.HttpMethod]::Put) "/api/requisitions/$requisitionId" $updateBody $csrfToken
    Write-HttpStatus 'requisitions.update' $updated.Status
    Stop-IfUnexpected $updated.Status @(200)

    $itemBody = @{
        lineNumber = 1
        itemDescription = 'Fictional radio batteries for verification'
        quantity = 2.500
        unitOfMeasure = 'each'
        estimatedUnitCost = 12.50
    }
    $itemCreate = Invoke-Api $client ([System.Net.Http.HttpMethod]::Post) "/api/requisitions/$requisitionId/items" $itemBody $csrfToken
    Write-HttpStatus 'requisitionItems.create' $itemCreate.Status
    Stop-IfUnexpected $itemCreate.Status @(200, 201)
    $item = @($itemCreate.Json.items | Select-Object -First 1)
    if ($item.Count -eq 0 -or [long] $item[0].id -lt 1) { exit 1 }
    $itemId = [long] $item[0].id

    $itemBody.itemDescription = 'Fictional radio batteries updated for verification'
    $itemBody.quantity = 3.000
    $itemUpdate = Invoke-Api $client ([System.Net.Http.HttpMethod]::Put) "/api/requisitions/$requisitionId/items/$itemId" $itemBody $csrfToken
    Write-HttpStatus 'requisitionItems.update' $itemUpdate.Status
    Stop-IfUnexpected $itemUpdate.Status @(200)

    $statusUpdate = Invoke-Api $client ([System.Net.Http.HttpMethod]::Patch) "/api/requisitions/$requisitionId/status" @{ statusCode = $nextCode } $csrfToken
    Write-HttpStatus 'requisitions.status' $statusUpdate.Status
    Stop-IfUnexpected $statusUpdate.Status @(200)

    $finalDetail = Invoke-Api $client ([System.Net.Http.HttpMethod]::Get) "/api/requisitions/$requisitionId"
    Write-HttpStatus 'requisitions.detail.final' $finalDetail.Status
    Stop-IfUnexpected $finalDetail.Status @(200)
}
finally {
    if ($passwordBstr -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordBstr)
    }
    if ($null -ne $securePassword) { $securePassword.Dispose() }
    $plainPassword = $null
    $client.Dispose()
    $handler.Dispose()
}
