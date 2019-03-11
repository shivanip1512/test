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

Function Reset-DatabaseSnapshot () {
    Invoke-Sqlcmd   -Query  "USE MASTER; "`
                            "RESTORE DATABASE yukon "`
                            "FROM DATABASE_SNAPSHOT = 'yukon_snapshot'"
                    -Username "yukon" -Password "yukon" -ServerInstance "localhost"
}

Function New-DatabaseSnapshot () {
    Invoke-Sqlcmd   -Query  "CREATE DATABASE yukon_snapshot "`
                            "ON (NAME = yukon, FILENAME = 'C:\database\yukon.snap') "`
                            "AS SNAPSHOT OF yukon;"
                    -Username "yukon" -Password "yukon" -ServerInstance "localhost"
GO

}

RESTORE DATABASE test FROM DATABASE_SNAPSHOT = 'test_Snapshot'