@echo off
if "%1" == "-h" goto usage

call setjavapath.bat
java -cp %YUKON_BASE%/Client/bin/tools.jar com.cannontech.dataCleanup.DeleteInventory %1
goto done

:usage
echo:
echo Usage:	DeleteInventory.bat [inventoryIdFilename]
echo:
echo Ex1:  DeleteInventory.bat c:\inventoryIdList.txt   - deletes all the inventoryIds supplied in the file
echo:
:done