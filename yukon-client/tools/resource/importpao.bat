rem imports a pao object from a binary file

java -cp .;%YUKON_BASE%/server/web;../../lib/tools.jar;../../lib/common.jar;../../lib/yukonappserver.jar;../../lib/j2ee.jar;../../lib/classes12.jar;../../lib/jtds.jar;../../lib/SqlServer.jar;../../lib/log4j-1.2.4.jar com.cannontech.dbtools.tools.ImportPAO %1 %2