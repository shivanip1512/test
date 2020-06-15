<#
.SYNOPSIS
    Unzip the Yukon Artifact
.DESCRIPTION 
    Unzip the Yukon Artifact to get the required binaries. These binaries will be sent to symbols store.
.EXAMPLE
    Expand-Artifact
#>
Function Expand-Artifact () {
    Write-Host "Unzip Artifact - Start"
    # Unzip the yukon artifact file to get the required binaries.
    Expand-Archive -Path "yukon-build\dist\yukon*.zip" -DestinationPath "yukon-artifact" -Force
    # Unzip the pdb and other binaries so that the same can be sent to symbols store.
    Expand-Archive -Path "yukon-artifact\YukonInstall\*.zip" -DestinationPath "yukon-server" -Force
    Write-Host "Unzip Artifact - Completed"
}

<#
.SYNOPSIS
    Store Symbols to the drive.
.DESCRIPTION 
    Connect with shared drive and call ant target to send the SYMBOLS.
.EXAMPLE
    Store-Symbols
#>
Function Store-Symbols () {
    Write-Host "Connect to Symbol store drive"
    net use p: \\pspl0003.eaton.ad.etn.com\Public /user:eaton\psplsoftwarebuild 13aq4xHAB
    Write-Host "Send Symbols to the drive"
    yukon-build\go.bat init symstore
    Write-Host "Disconnect Symbol store drive"
    net use p: /delete
    Write-Host "Symbols are saved to drive"

}

<#
.SYNOPSIS
    Remove files.
.DESCRIPTION 
    Remove binaries file from the Yukon Installer zip as they are stored separately and not needed in Yukon installer zip.
.EXAMPLE
    Remove-Files
#>
Function Remove-Files () {
    # Remove yukon-server.zip file from yukon artifact.
    $zip = Get-ChildItem yukon-build\dist -Filter yukon*.zip
    add-type -AssemblyName 'System.IO.Compression.filesystem'
    
    # Remove the zip file which consist of pdb and other file as they are not needed for release artifact.
    foreach ($file in $zip){
        Write-Verbose "Updating $($file.name)" -Verbose
        $tempz = [io.compression.zipfile]::Open($file.FullName,'Update')
        $entry = $tempz.Entries | Where-Object {
        $_.fullname -match 'YukonInstall/yukon-server.zip'}
        foreach ($file in $entry){$file.Delete()}
        $tempz.Dispose()
    }
}

<#
.SYNOPSIS
    Copy Yukon Server binaries.
.DESCRIPTION 
    Copy Yukon server binaries to dist folder to save as an artifact.
.EXAMPLE
    Artifact-Binaries
#>
Function Artifact-Binaries () {
    Copy-Item -Path "yukon-artifact\YukonInstall\*.zip" -Destination 'yukon-build\dist'
}
