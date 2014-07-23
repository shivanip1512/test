cd %YUKON_BASE%\client\bin
call setjavapath.bat
java -cp billing.jar com.cannontech.billing.mainprograms.BillingFile format=0 file=c:\yukon\client\export\billing.csv collection=Default,anotherGroup end=03/01/03 demand=30 energy=7 mult=false append=false
