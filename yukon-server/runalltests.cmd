@echo off

set _ERRORLEVEL=0

for /f %%a in ('dir /b bin\test_*.exe') do call :docall bin\%%a
if %_ERRORLEVEL%==1 echo THERE WAS AN ERROR!!!!
exit /B

:docall
echo Running %1...
call %%1
if not "%ERRORLEVEL%" == "0" set _ERRORLEVEL=1
