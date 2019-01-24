@echo off
if "%OS%" == "Windows_NT" setlocal

set JRE_HOME=%2
::need to do some shenanigans here to remove the outer quotes
set JRE_HOME=%JRE_HOME:"=%

set CURRENT_DIR=%cd%

cd ..
::\Yukon\Server\web
set CATALINA_HOME=%cd%

cd ..
::\Yukon\Server
set YUKON_SERVER_DIR=%cd%

cd %CATALINA_HOME%

set CATALINA_BASE=%CATALINA_HOME%
 
set EXECUTABLE=%CATALINA_HOME%\bin\tomcat9.exe

set SERVICE_NAME=YukonWebApplicationService

set PR_DISPLAYNAME=Yukon Web Application Service

if %1 == install goto doInstall
if %1 == remove goto doRemove
if %1 == uninstall goto doRemove

:doRemove
rem Remove the service
"%EXECUTABLE%"//DS//"%SERVICE_NAME%"
echo The service '%PR_DISPLAYNAME%' has been removed
goto end

:doInstall
rem Install the service
echo Installing the service '%PR_DISPLAYNAME%' ...
echo Using CATALINA_HOME:    %CATALINA_HOME%
echo Using CATALINA_BASE:    %CATALINA_BASE%
echo Using JRE_HOME:        %JRE_HOME%

rem Use the environment variables as an example
rem Each command line option is prefixed with PR_

set PR_DESCRIPTION=Yukon web server 
set PR_INSTALL=%EXECUTABLE%
set PR_LOGPATH=%CATALINA_BASE%\logs
set "PR_CLASSPATH=%CATALINA_HOME%\bin\bootstrap.jar;%CATALINA_BASE%\bin\tomcat-juli.jar;%CATALINA_HOME%\bin\tomcat-juli.jar"

rem Set the client jvm from JRE_HOME
set PR_JVM=%JRE_HOME%\bin\server\jvm.dll
if exist "%PR_JVM%" goto foundJvm

:foundJvm
echo Using JVM:              %PR_JVM%
"%EXECUTABLE%" //IS//%SERVICE_NAME% --Startup auto --StartClass org.apache.catalina.startup.Bootstrap --StopClass org.apache.catalina.startup.Bootstrap --StartParams start --StopParams stop
if not errorlevel 1 goto installed
echo Failed installing '%PR_DISPLAYNAME%' service
goto end

:installed
rem Clear the environment variables. They are not needed any more.
set PR_DISPLAYNAME=
set PR_DESCRIPTION=
set PR_INSTALL=
set PR_LOGPATH=
set PR_CLASSPATH=
set PR_JVM=


rem Set extra parameters using //US// option on already installed service
"%EXECUTABLE%"//US//%SERVICE_NAME% --JvmOptions "-Dcatalina.base=%CATALINA_BASE%;-Dcatalina.home=%CATALINA_HOME%" --StartMode jvm --StopMode jvm

rem More extra parameters
set PR_LOGPATH=%CATALINA_BASE%\logs
set PR_STDOUTPUT=auto
set PR_STDERROR=auto
"%EXECUTABLE%"//US//%SERVICE_NAME% ++JvmOptions "-Djava.io.tmpdir=%CATALINA_BASE%\temp;-XX:+HeapDumpOnOutOfMemoryError;-XX:HeapDumpPath=%YUKON_SERVER_DIR%\Log" rem --JvmMs 256 --JvmMx 512
echo The service 'Yukon Web Application Service' has been installed.

:end
cd %CURRENT_DIR%
