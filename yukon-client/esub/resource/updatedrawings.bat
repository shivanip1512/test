rem updatedrawings.bat
rem Starting in a given directory it recursively loads and saves all the jlx files it finds.
rem Run this file from the client bin directory
call setjavapath.bat
java -classpath .;%YUKON_BASE%/server/web;esub-editor.jar;common.jar;yukonappserver.jar;jloox2.0.2.jar com.cannontech.esub.tool.UpdateDrawings %1 %2