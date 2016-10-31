@echo off

set bindir=bin
if "%1" == "-debug" (
  set bindir=bin-debug
  shift
)

if not exist %bindir%/nul (
  echo %bindir% does not exist
  exit  /B 1
)

set _ERRORLEVEL=0

for /f %%a in ('dir /b %bindir%\test_*.exe') do call :docall %bindir%\%%a
if %_ERRORLEVEL%==1 echo THERE WAS AN ERROR!!!!
exit /B

:docall
echo Running %1...
call %%1
if not "%ERRORLEVEL%" == "0" set _ERRORLEVEL=1
