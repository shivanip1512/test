# Promote CI Build to Release Build by performing required task like code signing, store symbols etc...
function Test-Admin {
    $currentUser = New-Object Security.Principal.WindowsPrincipal $([Security.Principal.WindowsIdentity]::GetCurrent())
    $currentUser.IsInRole([Security.Principal.WindowsBuiltinRole]::Administrator)
}

if ((Test-Admin) -eq $false) {
    if ($elevated) {
        # tried to elevate, did not work, aborting
    }
    else {
        Start-Process powershell.exe -Verb RunAs -ArgumentList ('-noprofile -noexit -file "{0}" -elevated' -f ($myinvocation.MyCommand.Definition))
    }

    exit
}

Get-ChildItem "..\yukon-install\sign-jar" -Filter *.jar | 
Foreach-Object {
try {
    $originalName = $_.FullName
    $unsigned = $_.basename + '-unsigned' + $_.extension
    Rename-Item –path $_.Fullname –Newname ($unsigned)
    $unsignedFilePath = Get-Item -Path "..\yukon-install\sign-jar\$unsigned"
    Write-Host "Original File: $originalName"
    Write-Host "Unsigned File: $unsignedFilePath"
    
    $signingCommand = ("$ENV:SIGNSERVER_HOME\bin\signclient.cmd signdocument -clientside " +
            "-workername SW-YUKON-JAR-CMS -servlet /signserver/worker/SW-YUKON-JAR-CMS " +
            "-hosts signserverp1.tcc.etn.com,signserverp2.tcc.etn.com -port 8443 " +
            "-username psplsoftwarebuild -password 13aq4xHAB " +
            "-digestalgorithm SHA-256 " +
            "-truststore $ENV:SIGNSERVER_HOME\eaton-truststore.jks -truststorepwd eaton " +
            "-infile $unsignedFilePath -outfile $originalName")
    Write-Host "Signing Jar File: $originalName"
    Invoke-Expression -Command $signingCommand
    if (Test-Path $originalName -PathType leaf) {
        Remove-Item -Path $unsignedFilePath
    }
    } 
    catch {
    "Error Occurred while signing file: $originalName"
}
}

Copy-Item "..\yukon-install\sign-jar\*" -Destination "..\yukon-install\build\Yukon Base\Client\bin\" -Force -Verbose


