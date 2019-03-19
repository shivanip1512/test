@echo off
net stop "YukonWatchdogService" &
net stop "YukonSimulatorsService" &
net stop "Yukon Field Simulator Service" &
net stop "Yukon Cap Control Service" &
net stop "Yukon Dispatch Service" &
net stop "Yukon Foreign Data Service" &
net stop "Yukon Load Management Service" &
net stop "Yukon MAC Scheduler Service" &
net stop "Yukon Notification Server" &
net stop "Yukon Port Control Service" &
net stop "Yukon Calc-Logic Service" &
net stop "YukonWebApplicationService" &
net stop "Yukon Real-Time Scan Service" & 
net stop "YukonMessageBroker" &
net stop "YukonServiceMgr" &
net stop "Apache Tomcat 9.0 Tomcat9" &

:: Wait for 30 sec
ping -n 30 127.0.0.1 > NUL

:: Check all Service status and kill if its not stopped

CALL :terminateIfStillRunning "YukonWatchdogService"
CALL :terminateIfStillRunning "YukonSimulatorsService"
CALL :terminateIfStillRunning "Yukon Field Simulator Service"
CALL :terminateIfStillRunning "YukonWebApplicationService" 
CALL :terminateIfStillRunning "YukonMessageBroker"
CALL :terminateIfStillRunning "YukonServiceMgr"
CALL :terminateIfStillRunning "Yukon Cap Control Service"
CALL :terminateIfStillRunning "Yukon Dispatch Service"
CALL :terminateIfStillRunning "Yukon Foreign Data Service"
CALL :terminateIfStillRunning "Yukon Load Management Service"
CALL :terminateIfStillRunning "Yukon MAC Scheduler Service"
CALL :terminateIfStillRunning "YukonNotificationServer"
CALL :terminateIfStillRunning "Yukon Port Control Service"
CALL :terminateIfStillRunning "Yukon Calc-Logic Service"
CALL :terminateIfStillRunning "Yukon Real-Time Scan Service"
CALL :terminateIfStillRunning "Apache Tomcat 9.0 Tomcat9" 

:terminateIfStillRunning
sc QUERY "%~1" | find /I "STATE" | find "STOPPED"
if %ERRORLEVEL% NEQ 0 (
taskkill /F /FI "SERVICES eq %~1"
)
EXIT /B 0