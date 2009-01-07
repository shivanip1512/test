echo:
echo Executing %~df0 %*
echo:


%~dp0..\yukon-client\build\ant\bin\ant -f %~dp0build.xml %*
if not "%ERRORLEVEL%" == "0" exit %ERRORLEVEL%