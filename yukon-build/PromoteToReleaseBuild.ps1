# Unzip the yukon artifact file to get the required binaries.
Expand-Archive -Path "yukon-build\dist\yukon*.zip" -DestinationPath "yukon-server"

# Unzip the pdb and other binaries so that the same can be sent to symbols store.
Expand-Archive -Path "yukon-server\YukonInstall\*.zip" -DestinationPath "yukon-server"

# Remove yukon-server.zip file from yukon artifact.
$zip = Get-ChildItem yukon-build\dist -Filter *.zip
add-type -AssemblyName 'System.IO.Compression.filesystem'

# Remove the zip file which consist of pdb and other file as they are not needed for release artifact.
foreach ($z in $zip){
    Write-Verbose "Updating $($z.name)" -Verbose
    $tempz = [io.compression.zipfile]::Open($z.FullName,'Update')
    $entry = $tempz.Entries | Where-Object {
    $_.fullname -match 'YukonInstall/yukon-server.zip'}
    foreach ($e in $entry){$e.Delete()}
    $tempz.Dispose()
}