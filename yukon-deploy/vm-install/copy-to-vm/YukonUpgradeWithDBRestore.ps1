# To execut this PowerShell Script, we need to execute this administrator only
# Execute following command if we have ExecutionPolicy set to Restricted
# Set-ExecutionPolicy -ExecutionPolicy RemoteSigned

# Valid operations are start, stop, restart
param([switch]$Elevated,[string]$DatabaseType='restart')

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

Import-Module $PSScriptRoot\YukonDeploy
Import-Module $PSScriptRoot\YukonDatabase
 
Uninstall-Yukon
Reset-DatabaseSnapshot
Expand-Installer
Install-Yukon
Update-YukonDatabase -DatabaseType $($DatabaseType)
Start-YukonServices
ConfigureAndStartEIM
InstallAndStartSimulators