@echo off
if "%1" == "" goto usage

java -Djava.class.path=.;../config;classes12.zip;tools.jar;common.jar;SqlServer.jar;j2ee.jar;log4j-1.2.4.jar;yukonappserver.jar;yukonappclient.jar; com.cannontech.datagenerator.point.PointCreate %1 %2
goto done

:usage
echo:
echo Usage:	pointcreate.bat [ p[owerfail] d[isconnect] ]
echo:
echo Ex1:  pointcreate.bat p d     - creates POWERFAIL and DISCONNECT points
echo Ex2:  pointcreate.bat disc    - creates only DISCONNECT STATUS points
echo Ex3:  pointcreate.bat power   - creates only POWER FAIL points
echo:
:done