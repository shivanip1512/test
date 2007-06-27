@echo off
cls

echo:
echo Executing %~df0 %1
echo:

if NOT exist "yukon-client\build\ant" (
	echo:
	echo: checking out ant
	echo:
	cvs -Q checkout yukon-client/build/ant
	SET ANT_HOME=yukon-client\build\ant
)

if NOT exist "..\yukon\yukon-client" (
	echo:
	echo performing cvs checkout...
	echo:
	cmd.exe /c %ANT_HOME%\bin\ant -f build.xml checkout
)

%ANT_HOME%\bin\ant -f build.xml build-client