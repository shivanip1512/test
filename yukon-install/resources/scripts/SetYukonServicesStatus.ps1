# To execute this PowerShell Script, we need to execute this administrator only

# Valid operations are manual,auto
param([switch]$Elevated,[string]$Operation='status',[switch]$ExitWhenComplete)

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

# Clear console
Clear-Host

# Just Add/Remove any service here if any change comes .
# These values must be the Service Name, not the Display Name.
[System.Collections.ArrayList]$YukonServices = 
"YukonWatchdogService",
"YukonWebApplicationService",
"YukonSimulatorsService",
"Yukon Field Simulator Service",
"Yukon Load Management Service",
"Yukon Cap Control Service",
"Yukon Calc-Logic Service",
"Yukon Real-Time Scan Service",
"Yukon Foreign Data Service",
"Yukon MAC Scheduler Service",
"YukonNotificationServer",
"Yukon Port Control Service",
"Yukon Dispatch Service", 
"YukonServiceMgr",
"YukonCloudService",
"YukonMessageBroker"

<#
    SetStatusToAuto function set all services Status Type Automatic.
#>
Function SetStatusToAuto{
    foreach($YukonService in $YukonServices)
    {
       if(Get-Service $YukonService -ErrorAction SilentlyContinue)
       {
          sc.exe config $YukonService start= delayed-auto
       }
    }
}

<#
    SetStatusToManual function set all services Status Type Manual.
#>
Function SetStatusToManual{
    foreach($YukonService in $YukonServices)
    {
       if(Get-Service $YukonService -ErrorAction SilentlyContinue)
       {
          Set-Service -Name $YukonService -StartupType Manual
       }
    }
} 

If($Operation -eq 'auto')
{
    Write-Host "All Services status set to be Automatic"

    # Call SetStatusToAuto function to set Status Type Auto #
    SetStatusToAuto
}

If($Operation -eq 'manual')
{
    Write-Host "All Services status set to be Manual"

    # Call SetStatusToManual function to set Status Type Manual #
    SetStatusToManual
}

Write-Host ""

If(-Not($ExitWhenComplete)) 
{
    Read-Host -Prompt "Press Enter to exit"
}
