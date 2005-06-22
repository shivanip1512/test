rem Export a pao object to a file for later importing

%YUKON_BASE%\Runtime\bin\java -cp .;%YUKON_BASE%/server/web;tools.jar;common.jar;yukonappserver.jar;j2ee.jar;classes12.jar;jtds.jar;SqlServer.jar;log4j-1.2.4.jar com.cannontech.dbtools.tools.ExportPAO %1 %2