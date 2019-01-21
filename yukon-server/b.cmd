
@echo off
setlocal

rem Set the path to the directory where the build tools live.

if exist ..\Makefile (
        pushd ..
) else (
        pushd .
)

set SOURCEBASE=%cd%
cd ..
set YUKONBASE=%cd%
popd

set PATH=%YUKONBASE%\yukon-build\server-build;%PATH%

set COMPILEBASE=%cd%

rem Build it.

call msb.cmd %*
set _ERRORLEVEL=%ERRORLEVEL%

set exit=0

:Process_Args

if "%~1" == "" goto Done_Processing

    if /i "%~1" == "--exit" (
        set exit=1
        shift
        goto Process_Args
    )

:Done_Processing


if %_ERRORLEVEL% neq 0 goto cleanup

rem Do not attempt to run the unit tests if we're just cleaning the directories

if "%~1" == "clean" goto cleanup

rem Run Unit Tests on Success

rem ---  commented out on 2019-01-21 - unit tests are automatically run by the build
rem echo SOURCEBASE %SOURCEBASE%
rem pushd %SOURCEBASE%
rem call runalltests.cmd
rem popd

rem This follows the buildhere exit strategy, why exit /b is called despite --exit being set is unknown.

:cleanup
if %exit% equ 1 (
    if %_ERRORLEVEL% neq 0 exit %_ERRORLEVEL%
)

rem -- Return a real exit code without closing the shell.

exit /b %_ERRORLEVEL%
