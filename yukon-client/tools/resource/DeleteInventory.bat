@echo off
if "%1" == "-h" goto usage

call setjavapath.bat
java -Djava.class.path=.;%YUKON_BASE%/server/web;ojdbc6.jar;tools.jar;common.jar;stars.jar;jtds.jar;SqlServer.jar;j2ee.jar;log4j-1.2.4.jar;yukonappserver.jar; com.cannontech.dataCleanup.DeleteInventory %1
goto done

:usage
echo:
echo Usage:	DeleteInventory.bat [inventoryIdFilename]
echo:
echo Ex1:  DeleteInventory.bat c:\inventoryIdList.txt   - deletes all the inventoryIds supplied in the file
echo:
:done