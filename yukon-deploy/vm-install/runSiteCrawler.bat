echo:
echo Executing %~df0 %*
echo:

set ANT_OPTS=-Xmx800m
%~dp0..\ant\bin\ant -f %~dp0site_crawler.xml %*
if not "%ERRORLEVEL%" == "0" exit %ERRORLEVEL%