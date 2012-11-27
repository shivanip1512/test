call setjavapath.bat
java -classpath .;%YUKON_BASE%/server/web;tools.jar;common.jar;esub-editor.jar;log4j-1.2.4.jar com.cannontech.tools.esub.ExportDrawings %1 %2 %3