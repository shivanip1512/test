cd /d %YUKON_BASE%\client\bin
call setjavapath.bat
java -Djava.class.path=%YUKON_BASE%/Client/bin/billing.jar com.cannontech.billing.mainprograms.BillingFile outputfile=billing.csv collectiongroup=Default %1 %2 %3 %4 %5 %6 %7 %8 %9
