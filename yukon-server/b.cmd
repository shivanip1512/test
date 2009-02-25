
@echo off
setlocal

rem Set the path to the directory where the build tools live.

if not exist ..\Makefile (
        pushd ..
) else (
        pushd ..\..
)

set YUKONBASE=%cd%
popd

set PATH=%YUKONBASE%\yukon-build\server-build;%PATH%

set COMPILEBASE=%cd%
pushd ..
set SOURCEBASE=%cd%
popd

rem Build it.

buildhere.cmd %*

