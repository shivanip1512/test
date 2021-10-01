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
[System.Collections.ArrayList]$YukonServiceGroups =
@(@("YukonWatchdogService",
"YukonWebApplicationService",
"YukonSimulatorsService",
"Yukon Field Simulator Service"),
@("NMServer"),
@("Yukon Load Management Service",
"Yukon Cap Control Service",
"Yukon Calc-Logic Service",
"Yukon Real-Time Scan Service",
"Yukon Foreign Data Service",
"Yukon MAC Scheduler Service"),
@("YukonNotificationServer"),
@("Yukon Port Control Service"),
@("Yukon Dispatch Service"),
@("YukonServiceMgr"),
@("YukonCloudService",
"YukonMessageBroker"))

[System.Collections.ArrayList]$ServicesToRestart = @()

$global:STOPPED = "Stopped"
$global:RUNNING = "Running"
$global:STOPPENDING = "StopPending"
$global:SERVICES_STOPPED = $True

# Adding only those services to ServicesToRestart list if they are set start automatically or currently running #
foreach($ServiceGroup in $YukonServiceGroups)
{
    [System.Collections.ArrayList]$ActiveServicesFromThisGroup = @()
    
    foreach($YukonService in $ServiceGroup)
    {
	    $ServiceStartMode = (Get-WmiObject Win32_Service -filter "Name='$YukonService'").StartMode

        If($ServiceStartMode -eq "Auto")
        {
            [void]$ActiveServicesFromThisGroup.Add($YukonService)
        }
        elseIf(Get-Service $YukonService -ErrorAction SilentlyContinue)
        {
            $Service = Get-Service $YukonService    
        
            If($Service.Status -eq $RUNNING)
            {
               [void]$ActiveServicesFromThisGroup.Add($YukonService)
            }
        }
    }
    
    [void]$ServicesToRestart.Add($ActiveServicesFromThisGroup)
}

Write-Host "Services configured for automatic start or currently running: `r`n" 
foreach($Services in $ServicesToRestart) {
    foreach($Service in $Services) {
        Write-Host $Service
    }
}
Write-Host "-------------------------------------------" 

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
        foreach($ServiceGroup in $ServicesToRestart)
        {
            foreach($Service in $ServiceGroup)
            {
                $Service = Get-Service $Service
                If($Service.Status -ne $STOPPED)
                {
                   $SERVICES_STOOPED = $False
                   Write-Host "Service" $Service "has not yet stopped, waiting..."
                   return $SERVICES_STOOPED
                }
            }   
        }    
        $SERVICES_STOOPED = $True
        Write-Host "All Services have been stopped successfully."
        return $SERVICES_STOOPED
    } 
    ElseIf($ServiceStatus -eq $RUNNING)
    {
    $AllRunning = $True
        foreach($ServiceGroup in $ServicesToRestart)
        {
            foreach($Service in $ServiceGroup)
            {
               $Svc = Get-Service $Service
               If($Svc.Status -ne $RUNNING)
               {
                 Write-Host "Service - $Service is not running."
                 $AllRunning = $False
               }
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
    foreach($ServiceGroupToStop in $ServicesToRestart)
    {
        foreach($ServiceToStop in $ServiceGroupToStop)
        {
            $Service = Get-Service $ServiceToStop
            $CurrentServiceStatus = $Service.Status
            If($CurrentServiceStatus -ne $STOPPED)
            {
               Write-Host "Stop Service $ServiceToStop"
               $Service.Stop()
            }       
        }
        Start-Sleep -s 15
        foreach($ServiceToStop in $ServiceGroupToStop)
        {
            $Service = Get-Service $ServiceToStop
            $timeoutSeconds = 0
            $timespan = New-Object -TypeName System.Timespan -ArgumentList 0,0,$timeoutSeconds
            $CurrentServiceStatus = $Service.Status
            If($CurrentServiceStatus -ne $STOPPED)
            {
                try {
                    $Service.WaitForStatus([ServiceProcess.ServiceControllerStatus]::Stopped,$timespan)
                     Write-Host "$ServiceToStop stopped."
                }
                catch [ServiceProcess.TimeoutException] {
                     Write-Host "Timeout stopping service $($Service.Name)"
                }
            }
            else
            {
                Write-Host "$ServiceToStop stopped."
            }
        }
        Write-Host "-------------------------------------------" 
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

    foreach($ServiceGroupToStart in $ServicesToRestart)
    {
        foreach($ServiceToStart in $ServiceGroupToStart)
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
}

<#
    KillServices function kills services if it is still not stopped.
#>
Function KillServices {
    Write-Host "-------------------------------------------" 
    Write-Host "Killing Services" 
    Write-Host "-------------------------------------------" 

    foreach($ServiceGroupToKill in $ServicesToRestart)
    {
        foreach($ServiceToKill in $ServiceGroupToKill)
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
}


# Setting max waiting time to 7 minutes for services to stop #
$startTime = Get-Date
"Start time = " + $startTime
$maxWaitingTime = $startTime.AddMinutes(7)
"Max waiting time = " + $maxWaitingTime

# For all operations but start, we do a stop (restart, stop)
If($Operation -ne 'start')
{
# Call StopService function to stop services #
    StopServices

    # Continuously checking if any service is still not stopped up to 7 minutes #
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
        # Waited for 7 minutes, kill Services if they are still not stopped #
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
    
    Write-Host "Waiting 5 minutes to check Running status of all services.."

    Write-Host "-------------------------------------------" 

    Write-Host "Checking Services Status $RUNNING"
    Write-Host "-------------------------------------------" 
    Write-Host "Checking Services Statuses for 5 minutes"
    # In the time span of 5 minute we check running status of each service in one minute.
    $timeout = new-timespan -Minutes 5
    $sw = [diagnostics.stopwatch]::StartNew()
    while ($sw.elapsed -lt $timeout)
    {
         start-sleep -seconds 60
         $AllRunning = CheckServiceStatus($RUNNING)
         if($AllRunning)
         {
             Write-Host "All services running, checking again after 1 minute"
         }
    }
    $AllRunning = CheckServiceStatus($RUNNING)
    if($AllRunning)
    {
       Write-Host "All Services are running."
    }
    else
    {
       throw 'ERROR'
    }
}
Write-Host ""

If(-Not($ExitWhenComplete)) 
{
    Read-Host -Prompt "Press Enter to exit"
}