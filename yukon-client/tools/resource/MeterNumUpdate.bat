@echo off
if "%1" == "" goto usage

java -Djava.class.path=.;%YUKON_BASE%/server/web;classes12.jar;tools.jar;common.jar;jtds.jar;SqlServer.jar;j2ee.jar;log4j-1.2.4.jar;yukonappserver.jar; com.cannontech.tools.custom.MeterNumberUpdate %1 %2
goto done

:usage
echo:
echo		-to:<the path and filename to write the generated SQL statements to>
echo		-from:<the path and filename to read containing accountnumber, meternumber
echo:
echo Usage:	MeterNumUpdate.bat to:xxxx.xxx from:yyyy.yyy
echo:
:done