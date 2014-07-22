rem Takes a property file as an argument describing LogRat's operation
call setjavapath.bat
java -classpath %YUKON_BASE%/Client/bin/tools.jar com.cannontech.tools.email.LogRat %1