@echo off
cls

echo:
echo Executing %~df0 %*
echo:


..\yukon-client\build\ant\bin\ant -f build.xml %*
