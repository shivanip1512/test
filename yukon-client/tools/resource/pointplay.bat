rem Loops over a days worth of archived point data sending each change to dispatch
rem Use pointrecord.bat to generate a file
rem pointrecord.bat c:\8-12-03.pdata

java -cp .;..\Config;tools.jar;common.jar;classes12.zip;jtds.jar;SqlServer.jar;log4j-1.2.4.jar;yukonappserver.jar;j2ee.jar;esub-editor.jar com.cannontech.datagenerator.PointChangePlayer2 %1 %2