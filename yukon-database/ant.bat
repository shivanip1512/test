@echo off
REM
REM This duplicates the ant.bat file in yukon-client, allowing this project to
REM   use the same version of ant that yukon-client uses.
REM
set JAVA=%JAVA_HOME%\bin\java
set cp=..\yukon-client\build\ant.jar;%JAVA_HOME%\lib\tools.jar;
for %%i in (..\yukon-client\build\*.jar) do call ..\yukon-client\build\cp.bat %%i
%JAVA% -Xmx256M -classpath %CP% -Dant.home=. org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9 -buildfile build.xml

