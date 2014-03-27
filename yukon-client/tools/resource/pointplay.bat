rem Loops over a days worth of archived point data sending each change to dispatch
rem Use pointrecord.bat to generate a file
rem pointrecord.bat c:\8-12-03.pdata

call setjavapath.bat
java -cp %YUKON_BASE%/client/bin/tools.jar com.cannontech.datagenerator.PointChangePlayer2 %1 %2