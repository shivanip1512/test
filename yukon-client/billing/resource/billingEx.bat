cd %YUKON_BASE%\client\bin
call setjavapath.bat
java -Djava.class.path=.;../config/;billing.jar;common.jar;classes12.zip;SqlServer.jar;yukonappserver.jar;yukonappclient.jar;log4j-1.2.4.jar;j2ee.jar com.cannontech.billing.mainprograms.BillingFile format=0 file=c:\yukon\client\export\billing.csv collection=Default,anotherGroup end=03/01/03 demand=30 energy=7 mult=false append=false
