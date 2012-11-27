call setjavapath.bat
java -Djava.class.path=.;%YUKON_BASE%/server/web;common.jar;tools.jar;yukonappserver.jar;log4j-1.2.4.jar com.cannontech.dbtools.updater.DBUpdater %1 %2 %3 %4 %5 %6