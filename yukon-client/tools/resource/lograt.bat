rem Takes a property file as an argument describing LogRat's operation
call setjavapath.bat
java -Djava.class.path=.;%YUKON_BASE%/server/web;ojdbc6.jar;common.jar;tools.jar;jtds.jar;SqlServer.jar;yukonappserver.jar;log4j-1.2.4.jar;j2ee.jar; com.cannontech.tools.email.LogRat %1