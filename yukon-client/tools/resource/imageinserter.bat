call setjavapath.bat
java -classpath .;%YUKON_BASE%/server/web;tools.jar;common.jar;log4j-1.2.4.jar;yukonappserver.jar com.cannontech.dbtools.image.ImageInserter %1