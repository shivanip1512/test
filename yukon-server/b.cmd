
@echo off
setlocal

rem Set the path to the directory where the build tools live.

if not exist ..\Makefile (
        pushd ..\yukon-build\server-build
) else (
        pushd ..\..\yukon-build\server-build
)
set path=%cd%;%path%
popd

set compilebase=%cd%
pushd ..
set sourcebase=%cd%
popd

rem Build it.

buildhere.cmd %*

