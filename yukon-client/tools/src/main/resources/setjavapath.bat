@ echo off
IF NOT "%YUKON_BASE%"=="" GOTO USAGE
set CURRENT_DIR=%cd%
cd ..
cd ..
set YUKON_BASE=%cd%
:USAGE
set path=%YUKON_BASE%\Runtime\bin;%PATH%