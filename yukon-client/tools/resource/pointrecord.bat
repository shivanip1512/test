rem Records a days worth of points to a binary file
rem example to record all archived point data from 8/12/2003 
rem pointrecord.bat c:\8-12-03.pdata 8/12/2003

java -cp .;..\Config;tools.jar;common.jar;classes12.zip;SqlServer.jar;log4j-1.2.4.jar com.cannontech.datagenerator.PointChangeRecorder %1 %2