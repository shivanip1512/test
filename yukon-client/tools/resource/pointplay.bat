rem Loops over a days worth of archived point data sending each change to dispatch
rem Use pointrecord.bat to generate a file
rem pointrecord.bat c:\8-12-03.pdata

java -cp .;%YUKON_BASE%/server/web;../../lib/tools.jar;../../lib/common.jar;../../lib/classes12.jar;../../lib/jtds.jar;../../lib/SqlServer.jar;../../lib/log4j-1.2.4.jar;../../lib/yukonappserver.jar;../../lib/j2ee.jar;esub-editor.jar com.cannontech.datagenerator.PointChangePlayer2 %1 %2