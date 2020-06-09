<#
.SYNOPSIS
    Unzip the Yukon Artifact
.DESCRIPTION 
    Unzip the Yukon Artifact to get the required binaries. These binaries will be sent to symbols store.
.EXAMPLE
    Unzip-Artifact
#>
Function Unzip-Artifact () {
    Write-Host "Unzip Artifact - Start"
    # Unzip the yukon artifact file to get the required binaries.
    Expand-Archive -Path "..\..\yukon-build\dist\yukon*.zip" -DestinationPath "..\..\yukon-artifact"
    # Unzip the pdb and other binaries so that the same can be sent to symbols store.
    Expand-Archive -Path "..\..\yukon-artifact\YukonInstall\*.zip" -DestinationPath "..\..\yukon-server"
    Write-Host "Unzip Artifact - Completed"
}

<#
.SYNOPSIS
    Creates the yukon_snapshot for use with Reset-DatabaseSnapshot
.DESCRIPTION 
    For the Yukon database on Localhost, creates yukon_snapshot snapshot
.EXAMPLE
    New-DatabaseSnapshot
#>
Function New-DatabaseSnapshot () {
    Invoke-Sqlcmd   -Query  "CREATE DATABASE yukon_snapshot "`
                            "ON (NAME = yukon, FILENAME = 'C:\database\yukon.snap') "`
                            "AS SNAPSHOT OF yukon;"
                    -Username "yukon" -Password "yukon" -ServerInstance "localhost"
GO

}