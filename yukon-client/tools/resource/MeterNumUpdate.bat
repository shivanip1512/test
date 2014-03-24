@echo off
if "%1" == "" goto usage

call setjavapath.bat
java -classpath %YUKON_BASE%/Client/bin/tools.jar com.cannontech.tools.custom.MeterNumberUpdate %1 %2
goto done

:usage
echo:
echo		-to:<the path and filename to write the generated SQL statements to>
echo		-from:<the path and filename to read containing accountnumber, meternumber
echo:
echo Usage:	MeterNumUpdate.bat to:xxxx.xxx from:yyyy.yyy
echo:
:done