@echo off
REM $Id: ant.bat,v 1.1 2002/06/21 00:09:47 alauinger Exp $
set JAVA=%JAVA_HOME%\bin\java
set cp=ant.jar;%JAVA_HOME%\lib\tools.jar;
for %%i in (..\build\*.jar) do call cp.bat %%i
%JAVA% -Xmx256M -classpath %CP% -Dant.home=. org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 -buildfile build.xml

