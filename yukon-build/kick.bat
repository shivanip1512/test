@echo off
cls

echo:
echo Executing %~df0 %1
echo:

echo:
echo: updating yukon-build...
echo:
cvs update yukon-build

if NOT exist "yukon-client\build\ant" (
	echo:
	echo: checking out ant...
	echo:
	cvs -Q checkout yukon-client/build/ant
	SET ANT_HOME=yukon-client\build\ant
)

if "%1" == "tag" (
	echo:
	echo: TAG BUILD
	%ANT_HOME%\bin\ant -f build.xml tag-build
)else if "%1" == "branch" (
	echo:
	echo: BRANCH BUILD
	%ANT_HOME%\bin\ant -f build.xml branch-build
) else (
	echo:
	echo: DEFAULT BUILD
	%ANT_HOME%\bin\ant -f build.xml build
)