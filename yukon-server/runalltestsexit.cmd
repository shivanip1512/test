@echo off

set bindir=bin
if "%1" == "-debug" (
  set bindir=bin-debug
  shift
)

if not exist %bindir%/nul (
  echo %bindir% does not exist
  exit 
)

set _ERRORLEVEL=0

for /f %%a in ('dir /b %bindir%\test_*.exe') do call :docall %bindir%\%%a %%1
exit

:docall
call %%1 --report_format=XML --report_level=short --log_level=all_errors 2> %2%1.xml
if not "%ERRORLEVEL%" == "0" exit %ERRORLEVEL%

