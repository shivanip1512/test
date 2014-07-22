rem Tests the Radius Login Server - All parameters are optional!
rem The final param 'yukon', uses yukon db, etc. to authorize, otherwise, just a login test.
rem loginTest.bat radius=127.0.0.1 user=yukon pass=yukon secret=yukon authport=1812 acctport=1813 yukon

call setjavapath.bat
java -classpath %YUKON_BASE%/client/bin/tools.jar;%YUKON_BASE%/client/bin/jradius-client.jar com.cannontech.test.radius.LoginTest user=yukon pass=yukon rad=127.0.0.1 sec=yukon %1 %2 %3 %4 %5 %6