@echo off
cls

echo:
echo Executing %~df0 %1
echo:


..\yukon-client\build\ant\bin\ant -f build.xml %1
