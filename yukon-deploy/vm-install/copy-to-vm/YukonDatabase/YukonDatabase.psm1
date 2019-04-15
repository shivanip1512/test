<#
.SYNOPSIS
    Reset the Yukon database to the snapshot named yukon_snapshot
.DESCRIPTION 
    Requires specific configuration of your database. Only works with localhost.
.EXAMPLE
    Reset-DatabaseSnapshot
#>
Function Reset-DatabaseSnapshot () {
    Write-Host "Restoring database using database snapshot"
    Invoke-Sqlcmd   -Query  "USE MASTER; RESTORE DATABASE yukon FROM DATABASE_SNAPSHOT = 'yukon_snapshot'"`
                    -ServerInstance "localhost" -Database "yukon"  -Username "yukon" -Password "yukon"
    Write-Host "Database restore completed"
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