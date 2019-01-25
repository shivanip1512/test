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

<# 
.SYNOPSIS
    Configures and starts the EIM server
.DESCRIPTION 
    If the folder 'C:/Program Files/Apache Software Foundation/Tomcat 6.0' exists, copies api.xml and starts the "Apache Tomcat 6.0 Tomcat6" service.
.EXAMPLE
    ConfigureAndStartEIM
#>
Function ConfigureAndStartEIM() {
    $TARGETDIR = 'C:/Program Files/Apache Software Foundation/Tomcat 6.0'
    If(Test-Path -Path $TARGETDIR){
        Write-Host "Found EIM folder, installing"
        
        New-Item -ItemType "directory" -ErrorAction SilentlyContinue `
                                        -Path "C:/Program Files/Apache Software Foundation/Tomcat 6.0/conf/Catalina", 
                                              "C:/Program Files/Apache Software Foundation/Tomcat 6.0/conf/Catalina/localhost"

        Copy-Item "C:/Yukon/Server/Extras/Enterprise Integration Module/api.xml" `
                   -Destination "C:/Program Files/Apache Software Foundation/Tomcat 6.0/conf/Catalina/localhost/api.xml"

        Start-Service "Apache Tomcat 6.0 Tomcat6"
    }
    Else
    {
        Write-Host "No EIM folder found, skipping"
    }
}

<# 
.SYNOPSIS
    Checks to see if Java and C++ simulator files exist, then creates simulator services and starts them.
.DESCRIPTION 
    Checks to see if Java and C++ simulator files exist, then creates simulator services and starts them.
.EXAMPLE
    InstallAndStartSimulators
#>
Function InstallAndStartSimulators() {
    Write-Host "Installing Java Simulators"
    Start-Process -FilePath "C:\Yukon\Client\Bin\install_simulators.bat" -WorkingDirectory "C:\Yukon\Client\Bin\" -Wait -NoNewWindow
    Start-Service "YukonSimulatorsService"
    
    Write-Host "Installing Field Simulator"
    Start-Process -FilePath "C:\Yukon\Server\Bin\field_simulator.exe" -ArgumentList "-install" -WorkingDirectory "C:\Yukon\Server\Bin\" -Wait -NoNewWindow
    Start-Service "Yukon Field Simulator Service"
}


<# 
.SYNOPSIS
    Remove old install files in C:\Yukon\YukonInstall and \YukonMisc\
.DESCRIPTION 
    Completely removes folders C:\Yukon\YukonInstall and \YukonMisc\
.EXAMPLE
    CleanupOldInstaller
#>
Function CleanupOldInstaller() {
    Remove-Item -Path "C:\Yukon\YukonInstall\" -Recurse
    Remove-Item -Path "C:\Yukon\YukonMisc\" -Recurse
}

<# 
.SYNOPSIS
    Unzip the installer and place it in C:\Yukon
.DESCRIPTION 
    Unzips all zip files in C:\ to C:\Yukon, then moves the zip files from C:\ to C:\Yukon\YukonInstall
.EXAMPLE
    UnZipInstaller
#>
Function Expand-Installer() {
    CleanupOldInstaller

    Write-Host "UnZipping all .Zip files in C:\"
    Expand-Archive -Path "C:\*.zip" -DestinationPath "C:\Yukon\"

    Write-Host "Moving installer to C:\Yukon\YukonInstall"
    Move-Item -Path "C:\*.zip" -Destination "C:\Yukon\YukonInstall\"
}


Function StopAllServices () {
    Write-Host "Stopping All Services"
    C:\Yukon\Server\bin\RestartYukonServices.ps1 -Operation stop -ExitWhenComplete
}

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

Function Update-YukonDatabase () {
    Param (
        [string]$DatabaseType
    )

    Write-Host "Database Update Beginning"

    [string]$sourcePath
    if ($databaseType -eq "oracle12" -or $databaseType -eq "oracle") {
        $sourcePath = "C:/Yukon/YukonMisc/YukonDatabase/DatabaseUpdates/Oracle"
    } else {
        $sourcePath = "C:/Yukon/YukonMisc/YukonDatabase/DatabaseUpdates/SqlServer"
    }
    
    Write-Host "Database Script Path: $($sourcePath)"

    Remove-Item -Path "C:\yukon\server\log\DBUpdater.log" -ErrorAction SilentlyContinue
    $p = Start-Process C:\Yukon\Runtime\bin\java.exe -ArgumentList "-cp %YUKON_BASE%/Client/bin/tools.jar com.cannontech.dbtools.updater.DBUpdater -Dsrc_path=$($sourcePath) -Dverbose=false -DignoreError=true" -Wait -PassThru
    Write-Host "Database Update Log"
    Get-Content c:\yukon\server\log\dbupdater.log | foreach {Write-Output $_}

    if ($p.ExitCode -ne 0) {
        Write-Error "Database Update NOT Successful error code $($p.ExitCode)"
        throw "Database Update NOT Successful error code $($p.ExitCode)"
    } else {
        Write-Host "Database Update Successful"
    }
}

<# 
.SYNOPSIS
    Create master.cfg if it does not exist, then run the Yukon setup process
.DESCRIPTION 
    Create master.cfg if it does not exist, then run the Yukon setup process. This runs setup.exe with default values and does NOT start services.
.EXAMPLE
    Install-Yukon
#>
Function Install-Yukon() {
    Write-Host "Creating \Yukon\server\config"
    New-Item -ItemType "directory" -Path "C:\Yukon\Server\Config\" -ErrorAction SilentlyContinue

    Write-Host "Copying master.cfg"
    Copy-Item -Path "C:\master.cfg" -Destination "C:\Yukon\Server\Config\" -ErrorAction SilentlyContinue

    RunSetupExe
}

Function RunSetupExe() {
    Write-Host "Running setup.exe"
    $result = Start-Process C:\Yukon\YukonInstall\setup.exe -ArgumentList "-s -f1C:\setup.iss -f2C:\Yukon\YukonInstall\installLog.log" -Wait -PassThru

    # We seem to have issues with whatever runs after this, perhaps this sleep will fix it.
    Start-Sleep -s 15
}

Function Uninstall-Yukon() {
    StopAllServices
    Write-Host "Uninstall Java Simulator"
    C:\Yukon\Runtime\bin\java.exe -jar c:\Yukon\Client\bin\wrapper.jar -removeWait simulators.conf

    Write-Host "Uninstall Field Simulator"
    sc.exe delete "Yukon Field Simulator Service"

    Write-Host  "Uninstall EIM"
    Remove-Item -Path "C:/Program Files/Apache Software Foundation/Tomcat 6.0/conf/Catalina" -Recurse

    # Note that Start-Process is used here to ensure the script waits while the uninstall runs.
    Write-Host "Uninstall Yukon"
    $result = Start-Process C:\Yukon\YukonInstall\setup.exe -ArgumentList "-uninst -s -f1C:\uninstall.iss" -Wait -PassThru
}

Function Start-YukonServices() {
    Write-Host "Starting All Services"
    C:\Yukon\Server\Bin\RestartYukonServices.ps1 -Operation start -ExitWhenComplete
}

Uninstall-Yukon
Expand-Installer
Install-Yukon
Update-YukonDatabase -DatabaseType $($DatabaseType)
Start-YukonServices
ConfigureAndStartEIM
InstallAndStartSimulators