echo:
echo Executing %~df0 %*
echo:

:: Set Maximum heap for Ant process.
set ANT_OPTS=-Xmx2048m

:: Get current file path using ~dp and based on that find ant to execute the target passed as parameter from groovy script.
%~dp0..\..\yukon-client\build\ant\bin\ant -f %~dp0build.xml %*
if not "%ERRORLEVEL%" == "0" exit %ERRORLEVEL%

