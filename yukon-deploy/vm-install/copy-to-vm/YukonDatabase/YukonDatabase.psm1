# Need to add the IF check to decide if we are doing a backup.
Function DatabaseBackup () {
    Param (
        [string]$databasePassword,
        [string]$databaseLogin,
        [string]$databaseServer,
        [string]$databaseName,
        [string]$backupFolder,
        [integer]$daysToKeepBackup
    )
    
    Invoke-Sqlcmd   -InputFile C:\MaintenanceSolution.sql -Username $databaseLogin -Password $databasePassword -ServerInstance $databaseServer
    Invoke-Sqlcmd   -Query "EXECUTE dbo.DatabaseBackup "`
                            "@Databases = $($databaseName),  "`
                            "@Directory = "$($backupFolder)",  "`
                            "@BackupType = 'FULL',  "`
                            "@Verify = 'Y', " `
                            "@Compress = 'Y', "`
                            "@CleanupTime = $($daysToKeepBackup)" `
                    -Username $databaseLogin -Password $databasePassword -ServerInstance $databaseServer
}

<#
.SYNOPSIS
    Reset the Yukon database to the snapshot named yukon_snapshot
.DESCRIPTION 
    Requires specific configuration of your database. Only works with localhost.
.EXAMPLE
    Reset-DatabaseSnapshot
#>
Function Reset-DatabaseSnapshot () {
    Invoke-Sqlcmd   -Query  "USE MASTER; "`
                            "RESTORE DATABASE yukon "`
                            "FROM DATABASE_SNAPSHOT = 'yukon_snapshot'"
                    -Username "yukon" -Password "yukon" -ServerInstance "localhost"
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