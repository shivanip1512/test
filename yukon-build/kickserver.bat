@echo off
cls

echo:
echo Executing %~df0 %1
echo:

echo:
echo: updating yukon-build...
echo:
cvs update

if NOT exist "yukon-client\build\ant" (
	echo:
	echo: checking out ant...
	echo:
	cvs -Q checkout yukon-client/build/ant
	SET ANT_HOME=yukon-client\build\ant
) else (
	if NOT defined ant_home (
		echo:
		echo setting ANT_HOME to yukon-client\build\ant
		echo:
		SET ANT_HOME=yukon-client\build\ant
	)
)

if NOT exist "..\yukon\yukon-server" (
	echo:
	echo performing cvs checkout...
	echo:
	cmd.exe /c %ANT_HOME%\bin\ant -f build-verbose.xml checkout-server
)

if "%1" == "noupdate" (
	%ANT_HOME%\bin\ant -f build-verbose.xml build-server_noupdate
) else (
	%ANT_HOME%\bin\ant -f build-verbose.xml build-server
)

