rem updatedrawings.bat
rem Starting in a given directory it recursively loads and saves all the jlx files it finds.
rem Run this file from the client bin directory
java -classpath .;%YUKON_BASE%/server/web;esub-editor.jar;../../lib/common.jar;../../lib/jtds.jar;../../lib/SqlServer.jar;../../lib/classes12.jar;../../lib/yukonappserver.jar;../../lib/jloox2.0.2.jar com.cannontech.esub.tool.UpdateDrawings %1