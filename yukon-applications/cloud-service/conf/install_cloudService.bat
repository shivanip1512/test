IF NOT "%YUKON_BASE%"=="" GOTO USAGE
cd ..
cd ..
mkdir Yukon
setx YUKON_BASE "C:\Yukon" /M
set path=%YUKON_BASE%;%PATH%
:USAGE
%YUKON_BASE%\Runtime\bin\java -jar %YUKON_BASE%\Client\bin\wrapper.jar -i %YUKON_BASE%\Client\bin\cloudService.conf
timeout 10
