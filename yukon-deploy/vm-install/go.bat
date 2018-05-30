echo:
echo Executing %~df0 %*
echo:

set ANT_OPTS=-Xmx800m
call %~dp0..\ant\bin\ant.bat -f %~dp0vmsetup.xml %*
if not "%ERRORLEVEL%" == "0" exit /b %ERRORLEVEL%
