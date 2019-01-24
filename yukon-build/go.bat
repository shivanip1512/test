echo:
echo Executing %~df0 %*
echo:

set ANT_OPTS=-Xmx2048m
%C:\dev\TRUNKJAVA11\build\ant\bin\ant -f %C:\dev\TRUNKJAVA11\common\build.xml %
pause
if not "%ERRORLEVEL%" == "0" exit %ERRORLEVEL%

