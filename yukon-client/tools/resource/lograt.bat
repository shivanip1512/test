rem Takes a property file as an argument describing LogRat's operation
call setjavapath.bat
java -Djava.class.path=.;%YUKON_BASE%/server/web;common.jar;tools.jar;yukonappserver.jar;log4j-1.2.4.jar com.cannontech.tools.email.LogRat %1