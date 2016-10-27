@echo off

set _ERRORLEVEL=0

for /f %%a in ('dir /b bin-debug\test_*.exe') do call :docall bin-debug\%%a %%1
exit

:docall
call %%1 --report_format=XML --report_level=short --log_level=all_errors 2> %2%1.xml
if not "%ERRORLEVEL%" == "0" exit %ERRORLEVEL%

