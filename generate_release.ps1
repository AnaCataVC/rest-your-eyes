$keystoreFile = "release.jks"
$propertiesFile = "keystore.properties"
$apkOutput = "app\build\outputs\apk\release\app-release.apk"
$releaseDir = "releases"
$releaseApk = "$releaseDir\RestYourEyes.apk"

# Generate a secure random password for the keystore
function Get-SecureRandomPassword {
    $chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*'
    return -join ($chars.ToCharArray() | Get-Random -Count 16)
}

if (-not (Test-Path $keystoreFile)) {
    Write-Host "Generating production keystore (release.jks)..."
    
    $securePassword = Get-SecureRandomPassword
    
    $keytoolArgs = @(
        "-genkeypair", "-v", 
        "-keystore", $keystoreFile, 
        "-alias", "restyoureyes", 
        "-keyalg", "RSA", 
        "-keysize", "2048", 
        "-validity", "10000", 
        "-storepass", $securePassword, 
        "-keypass", $securePassword, 
        "-dname", "CN=Ana, OU=Dev, O=RestYourEyes, L=City, S=State, C=US"
    )
    & keytool $keytoolArgs
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Creating keystore.properties..."
        @"
storeFile=../release.jks
storePassword=$securePassword
keyAlias=restyoureyes
keyPassword=$securePassword
"@ | Out-File -FilePath $propertiesFile -Encoding UTF8
    } else {
        Write-Host "Error generating keystore. Exiting..."
        exit 1
    }
}

Write-Host "Building production release..."
.\gradlew.bat assembleRelease

if (Test-Path $apkOutput) {
    if (-not (Test-Path $releaseDir)) {
        New-Item -ItemType Directory -Force -Path $releaseDir | Out-Null
    }
    Copy-Item -Path $apkOutput -Destination $releaseApk -Force
    Write-Host "Release generated successfully!"
    Write-Host "Production APK is located at: $releaseApk"
} else {
    Write-Host "Error: Could not find generated APK at $apkOutput"
}
