@echo off
if "%1" == "" goto usage

cd %YUKON_BASE%\client\bin
call setjavapath.bat
java -classpath tools.jar;commons-logging.jar com.cannontech.custom.pss2ws.PriceServer %1 %2 %3 %4 %5 %6 %7
goto done

:usage
echo:
echo Usage:	pss2ws.bat pointname:PricePoint export:c:/yukon/server/export/export.txt runtime:60 user:target pass:SW1623a certfile:C:/yukon/server/config/cacerts.jks certpass:epriceLBL
echo:
:done