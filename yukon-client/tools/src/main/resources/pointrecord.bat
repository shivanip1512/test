rem Records a days worth of points to a binary file
rem example to record all archived point data from 8/12/2003 to 8/15/2003
rem pointrecord.bat c:\8-12-03.pdata 8/12/2003 8/15/2003

call setjavapath.bat
java -cp %YUKON_BASE%/Client/bin/tools.jar com.cannontech.datagenerator.PointChangeRecorder %1 %2 %3