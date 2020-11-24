param(
     [Parameter()]
	 [string]$dbName,
	 [string]$dbBackupFileName,
	 [string]$dbType,
	 [string]$releaseFolderName,
	 [string]$dbUser
 )
 
function Test-Admin {
  $currentUser = New-Object Security.Principal.WindowsPrincipal $([Security.Principal.WindowsIdentity]::GetCurrent())
  $currentUser.IsInRole([Security.Principal.WindowsBuiltinRole]::Administrator)
}

if ((Test-Admin) -eq $false)  {
    if ($elevated) 
    {
        # tried to elevate, did not work, aborting
    } 
    else {
        Start-Process powershell.exe -Verb RunAs -ArgumentList ('-noprofile -noexit -file "{0}" -elevated' -f ($myinvocation.MyCommand.Definition))
}

exit
}

Import-Module $PSScriptRoot\..\..\yukon-deploy\vm-install\copy-to-vm\YukonDeploy
Stop-Services
 
if ($dbType -eq "oracle"){
	echo "Oracle DB restore is not implemented yet"
}else{
		echo "Restoring the Database using command : RESTORE DATABASE $dbName FROM DISK='$PSScriptRoot\TestDBs\$releaseFolderName\Sql\$dbBackupFileName' WITH REPLACE"
		sqlcmd -s $env:computername -Q "RESTORE DATABASE $dbName FROM DISK='$PSScriptRoot\TestDBs\$releaseFolderName\Sql\$dbBackupFileName' WITH REPLACE"
		#Below sql command will reset the user login , this is required when you restore a database using a backup file
		sqlcmd -s $env:computername -Q "USE $dbName ;ALTER USER $dbUser WITH LOGIN = $dbUser;"
}
