param(
     [Parameter()]
	 [string]$dbName,
	 [string]$dbBackupFileName,
	 [string]$dbType,
	 [string]$releaseFolderName,
	 [string]$dbUser
 )
 
Import-Module $PSScriptRoot\..\..\yukon-deploy\vm-install\copy-to-vm\YukonDeploy
Stop-Services

Function RestoreDatabase () {
	Write-Host "----------------------------Starting DB restore-------------------------------------------------"
    Invoke-Sqlcmd -Query "USE MASTER; RESTORE DATABASE $dbName FROM DISK='$PSScriptRoot\TestDBs\$releaseFolderName\Sql\$dbBackupFileName' WITH REPLACE" -Username "yukon" -Password "yukon" -ServerInstance "localhost" -verbose
	Write-Host "----------------------------Completed DB restore-------------------------------------------------"
}

Function ResetLogin () {
	Write-Host "----------------------------Starting Reset User Login-------------------------------------------------"
    Invoke-Sqlcmd -Query "USE $dbName ;ALTER USER $dbUser WITH LOGIN = $dbUser;" -Username "yukon" -Password "yukon" -ServerInstance "localhost" -verbose
	Write-Host "----------------------------Completed Reset User Login-------------------------------------------------"
}
 
if ($dbType -eq "oracle"){
	echo "Oracle DB restore is not implemented yet"
}else{
		RestoreDatabase
		ResetLogin
}