rem Export a pao object to a file for later importing

java -cp .;%YUKON_BASE%/server/web;../../lib/tools.jar;../../lib/common.jar;../../lib/yukonappserver.jar;../../lib/j2ee.jar;../../lib/classes12.jar;../../lib/jtds.jar;../../lib/SqlServer.jar;../../lib/log4j-1.2.4.jar com.cannontech.dbtools.tools.ExportPAO %1 %2