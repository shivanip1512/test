echo:
echo Executing %~df0 %*
echo:

set ANT_OPTS=-Xmx2048m
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_152
set PATH=C:\Program Files\Java\jdk1.8.0_152\bin;%PATH%
%~dp0..\yukon-client\build\ant\bin\ant -f %~dp0build.xml %*
if not "%ERRORLEVEL%" == "0" exit %ERRORLEVEL%
