rem imports a pao object from a binary file

call setjavapath.bat
java -cp .;%YUKON_BASE%/server/web;tools.jar;common.jar;yukonappserver.jar;j2ee.jar;classes12.jar;jtds.jar;SqlServer.jar;log4j-1.2.4.jar com.cannontech.dbtools.tools.ImportPAO %1 %2