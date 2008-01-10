@echo off

call setjavapath.bat
java -cp tools.jar com.cannontech.tools.bulk.MissListConverter %1 %2 %3 %4
