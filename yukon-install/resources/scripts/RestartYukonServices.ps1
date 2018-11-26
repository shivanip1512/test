# To execute this PowerShell Script, we need to execute this administrator only
# Execute following command if we have ExecutionPolicy set to Restricted
# Set-ExecutionPolicy -ExecutionPolicy RemoteSigned

# Valid operations are start, stop, restart
param([switch]$Elevated,[string]$Operation='restart',[switch]$ExitWhenComplete)

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

# Just Add/Remove any service here if any change comes The first service in the list is stopped first and started last.
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
"YukonMessageBroker"

[System.Collections.ArrayList]$ServicesToRestart = @()

$global:STOPPED = "Stopped"
$global:RUNNING = "Running"
$global:STOPPENDING = "StopPending"
$global:SERVICES_STOPPED = $True

# Adding only those services to ServicesToRestart list if they are set start automatically or currently running #
foreach($YukonService in $YukonServices)
{
    $ServiceStartMode = (Get-WmiObject Win32_Service -filter "Name='$YukonService'").StartMode

    If($ServiceStartMode -eq "Auto")
    {
        [void]$ServicesToRestart.Add($YukonService)
    }
    elseIf(Get-Service $YukonService -ErrorAction SilentlyContinue)
    {
        $Service = Get-Service $YukonService    
        
        If($Service.Status -eq $RUNNING)
        {
            [void]$ServicesToRestart.Add($YukonService)
        }
    }
}

Write-Host "Services configured for automatic start or currently running: `r`n`r`n$($ServicesToRestart -join "`r`n")`r`n"

<#
    CheckServiceStatus function check running and stopped status of services.
    Returns 
        -- $True   - If all services have been stopped in case of $serviceStatus = Stopped
                   - If all services are running in case of $serviceStatus = Running  
        -- $False  - If any service is still not stopped in case of $serviceStatus = Stopped
                   - If any service is still not running in case of $serviceStatus = Running 
#>
Function CheckServiceStatus($ServiceStatus) {
    If($ServiceStatus -eq $STOPPED)
    {
        foreach($Service in $ServicesToRestart)
        {
            $Service = Get-Service $Service
            If($Service.Status -ne $STOPPED)
            {
                $SERVICES_STOOPED = $False
                Write-Host "Service" $Service "has not yet stopped, waiting..."
                return $SERVICES_STOOPED
            }
        }    
        $SERVICES_STOOPED = $True
        Write-Host "All Services have been stopped successfully."
        return $SERVICES_STOOPED
    } 
    ElseIf($ServiceStatus -eq $RUNNING)
    {
    $AllRunning = $True
        foreach($Service in $ServicesToRestart)
        {
            $Svc = Get-Service $Service
            If($Svc.Status -ne $RUNNING)
            {
               Write-Host "Service - $Service is not running."
               $AllRunning = $False
            }
        }
       return $AllRunning
    }
}

<#
    StopServices function stops all services if it is not already stopped.
    Wait for max 15 seconds for service to stop
#>
Function StopServices {
    Write-Host "-------------------------------------------" 
    Write-Host "Stopping Services"
    Write-Host "-------------------------------------------" 

    foreach($ServiceToStop in $ServicesToRestart)
    {
        $Service = Get-Service $ServiceToStop
        $timeoutSeconds = 15
        $timespan = New-Object -TypeName System.Timespan -ArgumentList 0,0,$timeoutSeconds
        $CurrentServiceStatus = $Service.Status
        If($CurrentServiceStatus -ne $STOPPED)
        {
            Write-Host "Stop Service $ServiceToStop"
            $Service.Stop()
            try {
                $Service.WaitForStatus([ServiceProcess.ServiceControllerStatus]::Stopped, $timespan)
                Write-Host "$ServiceToStop stopped."
            }
            catch [ServiceProcess.TimeoutException] {
                Write-Host "Timeout stopping service $($Service.Name)"
            }
        }
    }
}

<#
    StartServices function starts all services if it is not already running.
    Gives delay of 3 seconds after starting every service
#>
Function StartServices {
# Reverse stop order to get start order 
$ServicesToRestart.Reverse()

    Write-Host "-------------------------------------------" 
    Write-Host "Starting Services" 
    Write-Host "-------------------------------------------" 

    foreach($ServiceToStart in $ServicesToRestart)
    {
        $Service = Get-Service $ServiceToStart
        $CurrentServiceStatus = $Service.Status
        If($CurrentServiceStatus -ne $RUNNING)
        {
            # The Yukon web service causes problems for other services on slow vms
            # This delay can help prevent this.
            If($ServiceToStart -eq 'YukonWebApplicationService')
            {
                Start-Sleep -s 10
            }
            Write-Host "Start Service $ServiceToStart" 
            Start-Service $ServiceToStart
            Write-Host "Service $ServiceToStart started !!" 
            Start-Sleep -s 3
        }
    }
}

<#
    KillServices function kills services if it is still not stopped.
#>
Function KillServices {
    Write-Host "-------------------------------------------" 
    Write-Host "Killing Services" 
    Write-Host "-------------------------------------------" 

    foreach($ServiceToKill in $ServicesToRestart)
    {
        $Service = Get-Service $ServiceToKill
        $CurrentServiceStatus = $Service.Status
        If($CurrentServiceStatus -ne $STOPPED)
        {
            Write-Host "Kill Service $ServiceToKill" 
            $ServicePID = (get-wmiobject win32_service | Where-Object { $_.name -eq $ServiceToKill}).processID
            Stop-Process $ServicePID -Force
        }
    }
}


# Setting max waiting time to 5 minutes for services to stop #
$startTime = Get-Date
"Start time = " + $startTime
$maxWaitingTime = $startTime.AddMinutes(5)
"Max waiting time = " + $maxWaitingTime

# For all operations but start, we do a stop (restart, stop)
If($Operation -ne 'start')
{
# Call StopService function to stop services #
    StopServices

    # Continuously checking if any service is still not stopped up to 5 minutes #
    Write-Host "-------------------------------------------" 
    Write-Host "Checking Services Status - $STOPPED"
    Write-Host "-------------------------------------------" 

    do
    {
        Start-Sleep -s 7
        $SERVICES_STOPPED = CheckServiceStatus($STOPPED)
        if($SERVICES_STOPPED)
        {
             break
        }
    }
    while((Get-Date) -lt ($maxWaitingTime))

    If(-Not($SERVICES_STOPPED))
    {
        # Waited for 5 minutes, kill Services if they are still not stopped #
        KillServices
        Start-Sleep -s 15
        $SERVICES_STOPPED = CheckServiceStatus($STOPPED)

        If(-Not($SERVICES_STOPPED))
        {
            Write-Host "At least one service still running"
        }
    }
}

# For all operations except stop, we start the services
If($Operation -ne 'stop')
{
    # Calling StartServices function to start services
    StartServices

    # Wait for 1 minute and again check if any service is not running 
    Write-Host "Waiting 1 minute to check Running status of all services.."
    Start-Sleep -s 60

    Write-Host "-------------------------------------------" 
    Write-Host "Checking Services Status $RUNNING"
    Write-Host "-------------------------------------------" 

    $AllRunning = CheckServiceStatus($RUNNING)
    if($AllRunning)
    {
        Write-Host "All Services are running."
    }
}
Write-Host ""

If(-Not($ExitWhenComplete)) 
{
    Read-Host -Prompt "Press Enter to exit"
}