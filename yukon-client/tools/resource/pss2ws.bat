@echo off
if "%1" == "" goto usage

call setjavapath.bat
java -Djava.class.path=.;%YUKON_BASE%/server/web;classes12.jar;tools.jar;common.jar;jtds.jar;SqlServer.jar;j2ee.jar;log4j-1.2.4.jar;yukonappserver.jar;axis.jar;commons-discover.jar;commons-httpclient.jar;jaxrpc.jar;saaj.jar;wsdl4j.jar;jaxm-runtime.jar;jaxm-api.jar; com.cannontech.custom.pss2ws.PriceServer %1 %2 %3 %4 %5 %6 %7
goto done

:usage
echo:
echo Usage:	pss2ws.bat pointname:PricePoint export:c:/yukon/server/export/export.txt runtime:60 user:target pass:SW1623a certfile:C:/yukon/server/config/cacerts.jks certpass:epriceLBL
echo:
:done