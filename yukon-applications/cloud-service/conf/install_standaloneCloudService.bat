cd /d %~dp0
call setjavapath.bat
setx YUKON_BASE %YUKON_BASE% /M
%YUKON_BASE%\Runtime\bin\java -jar %YUKON_BASE%\Client\bin\wrapper.jar -i %YUKON_BASE%\Client\bin\cloudService.conf
icacls %YUKON_BASE% /grant LocalService:(OI)(CI)F /T
timeout 10