set THRIFT_VERSION=0.11.0
set MSVC_VERSION=vc141
set JAVA_OUTPUT_DIR=..\..\..\..\..\..\yukon-client\common\src
set THRIFT_EXE=%DEV_ENVIRONMENT%\libraries\apache\thrift\%THRIFT_VERSION%-%MSVC_VERSION%\bin\thrift-%THRIFT_VERSION%.exe
for %%f in (*.thrift) do %THRIFT_EXE% -out %JAVA_OUTPUT_DIR% --gen java:beans %%f
