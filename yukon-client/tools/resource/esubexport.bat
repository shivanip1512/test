cd %YUKON_BASE%\client\bin
call setjavapath.bat
java -cp tools.jar;esub-editor.jar com.cannontech.tools.esub.ExportDrawings %1 %2 %3