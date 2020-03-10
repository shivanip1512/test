# To execut this PowerShell Script, we need to execute this administrator only
# Execute following command if we have ExecutionPolicy set to Restricted
# Set-ExecutionPolicy -ExecutionPolicy RemoteSigned

# Unzip the yukon zip file to get the required binaries
Expand-Archive -Path "dist\yukon*.zip" -DestinationPath "yukon-pdb"

# Unzip the pdb and other bianries so that the same can be sent to symbols store
Expand-Archive -Path "yukon-pdb\YukonInstall\*.zip" -DestinationPath "symbols"

#Remove the zip file whihc consist of pdb and other file as they are not needed for release artifact
$zip = Get-ChildItem dist -Filter *.zip
add-type -AssemblyName 'System.IO.Compression.filesystem'

# Find and remove the yukon-pdb.zip file
foreach ($z in $zip){
    Write-Verbose "Updating $($z.name)" -Verbose
    $tempz = [io.compression.zipfile]::Open($z.FullName,'Update')
    $entry = $tempz.Entries | Where-Object {
    $_.fullname -match 'YukonInstall/yukon-pdb.zip'}
    foreach ($e in $entry){$e.Delete()}
    $tempz.Dispose()
}