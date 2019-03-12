<# 
.SYNOPSIS
    Configures and starts the EIM server
.DESCRIPTION 
    If the folder 'C:/Program Files/Apache Software Foundation/Tomcat 9.0' exists, copies api.xml and starts the "Apache Tomcat 9.0 Tomcat9" service.
.EXAMPLE
    ConfigureAndStartEIM
#>
Function Start-EIM() {
    $TARGETDIR = 'C:/Program Files/Apache Software Foundation/Tomcat 9.0'
    If(Test-Path -Path $TARGETDIR){
        Write-Host "Found EIM folder, installing"
        
        New-Item -ItemType "directory" -ErrorAction SilentlyContinue `
                                        -Path "C:/Program Files/Apache Software Foundation/Tomcat 9.0/conf/Catalina", 
                                              "C:/Program Files/Apache Software Foundation/Tomcat 9.0/conf/Catalina/localhost"

        Copy-Item "C:/Yukon/Server/Extras/Enterprise Integration Module/api.xml" `
                   -Destination "C:/Program Files/Apache Software Foundation/Tomcat 9.0/conf/Catalina/localhost/api.xml"

        Start-Service "Apache Tomcat 9.0 Tomcat9"
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
Function Start-Simulators() {
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
    Stop-Service "Apache Tomcat 9.0 Tomcat9"
}

<# 
.SYNOPSIS
    Runs DBUpdater to update the database
.DESCRIPTION 
    This runs DB Updater on this system to update the database. The database type is used to choose which scripts to run. This requires a valid master.cfg pointing to the database.
.EXAMPLE
    Update-YukonDatabase -DatabaseType "sqlserver"
#>
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
    $p = Start-Process C:\Yukon\Runtime\bin\java.exe -ArgumentList "-cp C:/Yukon/Client/bin/tools.jar com.cannontech.dbtools.updater.DBUpdater -Dsrc_path=$($sourcePath) -Dverbose=false -DignoreError=true" -Wait -PassThru -NoNewWindow
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

<#
.SYNOPSIS
    Removes all installed Yukon features
.DESCRIPTION 
    Removes simulators, EIM, and runs Yukon uninstaller.
.EXAMPLE
    Uninstall-Yukon
#>
Function Uninstall-Yukon() {
    StopAllServices
    Write-Host "Uninstall Java Simulator"
    C:\Yukon\Runtime\bin\java.exe -jar c:\Yukon\Client\bin\wrapper.jar -removeWait c:\Yukon\Client\bin\simulators.conf

    Write-Host "Uninstall Field Simulator"
    sc.exe delete "Yukon Field Simulator Service"

    Write-Host  "Uninstall EIM"
    Remove-Item -Path "C:/Program Files/Apache Software Foundation/Tomcat 9.0/conf/Catalina" -Recurse

    # Note that Start-Process is used here to ensure the script waits while the uninstall runs.
    Write-Host "Uninstall Yukon"
    $result = Start-Process C:\Yukon\YukonInstall\setup.exe -ArgumentList "-uninst -s -f1C:\uninstall.iss" -Wait -PassThru
}

<#
.SYNOPSIS
    Starts all Yukon services that are set to start automatically
.DESCRIPTION 
    Runs the standard Yukon startup script. Requires Yukon to be installed.
.EXAMPLE
    Start-YukonServices
#>
Function Start-YukonServices() {
    Write-Host "Starting All Services"
    C:\Yukon\Server\Bin\RestartYukonServices.ps1 -Operation start -ExitWhenComplete
}