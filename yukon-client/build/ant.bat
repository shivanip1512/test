@echo off
REM $Id: ant.bat,v 1.2 2003/08/12 18:27:43 rneuharth Exp $
set JAVA=%JAVA_HOME%\bin\java
set cp=ant.jar;%JAVA_HOME%\lib\tools.jar;
for %%i in (..\build\*.jar) do call cp.bat %%i
%JAVA% -Xmx256M -classpath %CP% -Dant.home=. org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9 -buildfile build.xml

