call setjavapath.bat
java -Dsun.java2d.noddraw=true -Djava.class.path=.;%YUKON_BASE%/SERVER/WEB;tools.jar;common.jar;yukonappserver.jar;jtds.jar;classes12.jar;j2ee.jar;log4j-1.2.4.jar;SqlServer.jar; com.cannontech.capcontrol.CapControlImporter gui