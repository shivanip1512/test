rem Takes a property file as an argument describing LogRat's operation
%YUKON_BASE%\Runtime\bin\java -Djava.class.path=.;%YUKON_BASE%/server/web;classes12.jar;common.jar;tools.jar;jtds.jar;SqlServer.jar;yukonappserver.jar;log4j-1.2.4.jar;j2ee.jar; com.cannontech.tools.email.LogRat %1