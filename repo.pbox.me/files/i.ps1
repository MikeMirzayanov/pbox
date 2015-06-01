function Get-File {
  param (
    [string]$url,
    [string]$file
  )
  Write-Host "*** Downloading $url to $file"
  $webClient = new-object System.Net.WebClient
  $webClient.Proxy.Credentials=[System.Net.CredentialCache]::DefaultNetworkCredentials;
  $webClient.DownloadFile($url, $file)
}

Write-Host "Downloading PBOX"
$pboxZipUrl = "http://repo.pbox.me/files/pbox.zip"
$pboxTempDir = Join-Path $env:TEMP "pbox"
if (![System.IO.Directory]::Exists($pboxTempDir)) {[System.IO.Directory]::CreateDirectory($pboxTempDir)}
$pboxZipFile = Join-Path $pboxTempDir "pbox.zip"
Get-File $pboxZipUrl $pboxZipFile

Write-Host "Downloading 7-Zip"
$7zaFile = Join-Path $pboxTempDir '7za.exe'
Get-File 'http://repo.pbox.me/files/7za.exe' "$7zaFile"

Write-Host "Extracting PBOX"
$pboxDir = Join-Path $env:ALLUSERSPROFILE "pbox"
Start-Process "$7zaFile" -ArgumentList "x -o`"$pboxDir`" -y `"$pboxZipFile`"" -Wait -NoNewWindow

$setupFile = Join-Path $pboxDir 'bin\pbox-setup.bat'
Start-Process "$setupFile"
$env:Path = $env:Path + ";$pboxDir"

$setcolorFile = Join-Path $pboxDir 'bin\setcolor.exe'
Start-Process "$setcolorFile" -ArgumentList "gi" -Wait -NoNewWindow
Write-Host "PBOX is downloaded and installed to $pboxDir. PATH is updated. Restart console to apply changes. You can type 'pbox install windirstat' after."
Start-Process "$setcolorFile" -ArgumentList "rgb" -Wait -NoNewWindow
