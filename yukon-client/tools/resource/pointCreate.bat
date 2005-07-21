@echo off
if "%1" == "" goto usage

call setjavapath.bat
java -Djava.class.path=.;%YUKON_BASE%/server/web;classes12.jar;tools.jar;common.jar;jtds.jar;SqlServer.jar;j2ee.jar;log4j-1.2.4.jar;yukonappserver.jar; com.cannontech.datagenerator.point.PointCreate %1 %2 %3 %4
goto done

:usage
echo:
echo Usage:	pointcreate.bat [ p[owerfail] d[isconnect] mct410 lg[oadgroup] analog cbop] [(with analog only) devID count]
echo:
echo Ex1:  pointcreate.bat p d     - creates POWERFAIL and DISCONNECT points
echo Ex2:  pointcreate.bat analog devID count     - creates count ANALOG points for devID
echo Ex3:  pointcreate.bat disc    - creates only DISCONNECT STATUS points
echo Ex4:  pointcreate.bat power   - creates only POWER FAIL points
echo Ex4:  pointcreate.bat mct410  - adds missing points (kw,kwh,volts,minVolts,maxVolts,peakKw,kwLP,voltLP to MCT410 devices
echo Ex5:  pointcreate.bat lg   - creates history and control points for every LoadGroup
echo Ex6:  pointcreate.bat cbop  - creates operations count points for every Switched Cap Bank
echo:
:done