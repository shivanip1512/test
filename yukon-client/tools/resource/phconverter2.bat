call setjavapath.bat
java -cp %YUKON_BASE%/server/web;tools.jar;common.jar;ojdbc6.jar;jtds.jar;sqlserver.jar;log4j-1.2.4.jar;guava-11.0.1.jar com.cannontech.dbconverter.pthistory.PHConverter2 c:\yukon\dsm2 force
