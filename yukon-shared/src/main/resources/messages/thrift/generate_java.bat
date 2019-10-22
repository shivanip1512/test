set THRIFT_VERSION=0.12.0
set JAVA_OUTPUT_DIR=..\..\..\..\..\..\yukon-client\common\src
set THRIFT_EXE=%DEV_ENVIRONMENT%\libraries\apache\thrift\%THRIFT_VERSION%\bin\thrift-%THRIFT_VERSION%.exe
if [%1] == [] (set FILES=*.thrift) ELSE (set FILES=%*)
for %%f in (%FILES%) do %THRIFT_EXE% -out %JAVA_OUTPUT_DIR% --gen java:beans %%f