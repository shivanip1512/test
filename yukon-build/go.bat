echo:
echo Executing %~df0 %*
echo:

set ANT_OPTS=-Xmx2048m
%~dp0..\yukon-client\build\ant\bin\ant -f %~dp0build.xml %*
if not "%ERRORLEVEL%" == "0" exit %ERRORLEVEL%

