rem updatedrawings.bat
rem Starting in a given directory it recursively loads and saves all the jlx files it finds.
rem Run this file from the client bin directory
java -classpath .;esub-editor.jar;common.jar;SqlServer.jar;classes12.jar;yukonappserver.jar;yukonappclient.jar;jloox2.0.2.jar com.cannontech.esub.tool.UpdateDrawings %1