c:
cd \yukon\client\bin
java -Djava.class.path=.;%YUKON_BASE%/server/web;billing.jar;common.jar;classes12.jar;jtds.jar;SqlServer.jar;yukonappserver.jar;rwav.jar;log4j-1.2.4.jar;j2ee.jar com.cannontech.billing.mainprograms.BillingFile outputfile=billing.csv collectiongroup=Default %1 %2 %3 %4 %5 %6 %7 %8
