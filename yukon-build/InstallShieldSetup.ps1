# This file is used to wrap installshield in a mutex which allows simultaneous install processes to run 

function Get-TimeStamp {
    
    return "[{0:MM/dd/yy} {0:HH:mm:ss}]" -f (Get-Date)
    
}
function Execute-InstallShieldSetup {
    try { 
        $defaultMutexWait = New-TimeSpan -Minutes 4
        $mutex = New-Object System.Threading.Mutex($false, "Installshield Mutex")
        Write-Output "$(Get-TimeStamp) Waiting for Installshield Mutex"
        $mutexAcquired = $mutex.WaitOne($defaultMutexWait)
        Write-Output "$(Get-TimeStamp) Installshield mutex aquired state: $mutexAcquired"
        
        # Do the real work
        cmd /c "`"C:\Program Files (x86)\InstallShield\2012SpringSP1 SAB\System\IsCmdBld.exe`" -p `"yukon-install\YukonSetupX\Yukon Installer.ism`" 2`>`&1"
    } finally {        
        if ($mutexAcquired) {
            Write-Output "$(Get-TimeStamp) Calling ReleaseMutex"
            $mutex.ReleaseMutex()    
        } else {
            Write-Output "$(Get-TimeStamp) Skipped ReleaseMutex"
        }
        
    }
}

Execute-InstallShieldSetup