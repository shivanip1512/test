echo:
echo Executing %~df0 %*
echo:

set ANT_OPTS=-Xmx800m
%~dp0..\ant\bin\ant -f %~dp0cyme_oracle_backup.xml %*
if not "%ERRORLEVEL%" == "0" exit %ERRORLEVEL%