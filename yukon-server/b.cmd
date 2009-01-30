
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

rem Make Visual Studio 2008 toolset available.

if exist "C:\Program Files\Microsoft Visual Studio 9.0\VC\bin\vcvars32.bat" (
        call "C:\Program Files\Microsoft Visual Studio 9.0\VC\bin\vcvars32.bat"
) else (
        if exist "C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\bin\vcvars32.bat" (
                call "C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\bin\vcvars32.bat"
        ) else (
                echo Couldn't locate Visual Studio 2008 toolset.
        )
)

rem Other configuration settings.

set rw=

rem Builds in debug mode by default.  Pass in 'release' on the command line to change.

set debug=true

rem Build it.

if "%1"=="" (
        buildhere.cmd %*
) else (
        if "%1%"=="release" (
                set debug=
                buildhere.cmd %*
        ) else (
                build.exe %*
        )
)

