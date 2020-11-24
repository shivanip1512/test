param(
     [Parameter()]
	 [string]$dbName,
	 [string]$dbBackupFileName,
	 [string]$dbType,
	 [string]$releaseFolderName,
	 [string]$dbUser
 )
 
Import-Module $PSScriptRoot\..\..\yukon-deploy\vm-install\copy-to-vm\YukonDeploy
#Stop-Services
 
if ($dbType -eq "oracle"){
	echo "Oracle DB restore is not implemented yet"
}else{
		echo "Restoring the Database using command : RESTORE DATABASE $dbName FROM DISK='$PSScriptRoot\TestDBs\$releaseFolderName\Sql\$dbBackupFileName' WITH REPLACE"
		sqlcmd -s localhost -Q "RESTORE DATABASE $dbName FROM DISK='$PSScriptRoot\TestDBs\$releaseFolderName\Sql\$dbBackupFileName' WITH REPLACE"
		#Below sql command will reset the user login , this is required when you restore a database using a backup file
		sqlcmd -s localhost -Q "USE $dbName ;ALTER USER $dbUser WITH LOGIN = $dbUser;"
}
