@echo off
REM $Id: ant.bat,v 1.1 2005/04/05 15:47:11 cplender Exp $
set JAVA=%JAVA_HOME%\bin\java
set cp=..\yukon-client\build\ant.jar;%JAVA_HOME%\lib\tools.jar;
for %%i in (..\yukon-client\build\*.jar) do call ..\yukon-client\build\cp.bat %%i
echo:
echo %cp%
echo:
%JAVA% -Xmx256M -classpath %CP% -Dant.home=. org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9 -buildfile build.xml

