set THRIFT_VERSION=0.11.0
set CPP_OUTPUT_DIR=..\..\..\..\..\..\yukon-server\Message\Serialization\Thrift
set THRIFT_EXE=%DEV_ENVIRONMENT%\libraries\apache\thrift\%THRIFT_VERSION%\bin\thrift-%THRIFT_VERSION%.exe
for %%f in (*.thrift) do %THRIFT_EXE% -out %CPP_OUTPUT_DIR% --gen cpp %%f