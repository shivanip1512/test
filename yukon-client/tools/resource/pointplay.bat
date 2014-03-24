rem Loops over a days worth of archived point data sending each change to dispatch
rem Use pointrecord.bat to generate a file
rem pointrecord.bat c:\8-12-03.pdata

cd %YUKON_BASE%\client\bin
call setjavapath.bat
java -cp tools.jar;esub-editor.jar com.cannontech.datagenerator.PointChangePlayer2 %1 %2