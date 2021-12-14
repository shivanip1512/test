echo:
echo Executing %~df0 %*
echo:

set ANT_OPTS=-Xmx2048m
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.312.7-hotspot
set PATH=C:\Program Files\Eclipse Adoptium\jdk-8.0.312.7-hotspot\bin;%PATH%
%~dp0..\yukon-client\build\ant\bin\ant -f %~dp0build.xml %*
if not "%ERRORLEVEL%" == "0" exit %ERRORLEVEL%
