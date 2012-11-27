cd /d %YUKON_BASE%\client\bin
call setjavapath.bat
java -Djava.class.path=.;%YUKON_BASE%/server/web;billing.jar;common.jar;yukonappserver.jar;rwav.jar;log4j-1.2.4.jar com.cannontech.billing.mainprograms.BillingFile outputfile=billing.csv collectiongroup=Default %1 %2 %3 %4 %5 %6 %7 %8 %9
