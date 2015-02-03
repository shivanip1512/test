@echo off

rem  This logic for this script is based from these assumptions:
rem
rem  1)  When invoked from a subdirectory, buildhere should set the base directory properly
rem      and then build inside the original subdirectory.
rem  2)  The base directory contains a Makefile.
rem  3)  The buildable subdirectories contain a Makefile.
rem  4)  Other subdirectories exist that contain no Makefiles - if this script is invoked inside them,
rem        it should check the parent directory only

setlocal

set cwd=%cd%

rem Make Visual Studio toolset available.

if exist "C:\Program Files\Microsoft Visual Studio 12.0\VC\vcvarsall.bat" (
        call "C:\Program Files\Microsoft Visual Studio 12.0\VC\vcvarsall.bat"
) else (
        if exist "C:\Program Files (x86)\Microsoft Visual Studio 12.0\VC\vcvarsall.bat" (
                call "C:\Program Files (x86)\Microsoft Visual Studio 12.0\VC\vcvarsall.bat"
        ) else (
                echo Couldn't locate Visual Studio toolset.
        )
)

rem Preset the exit code to failure.  This will only be set to 0 on a successful compilation.

set _ERRORLEVEL=1

rem Process the command line arguments.  It will accept the following:
rem     --exit
rem             exits the script after completion and returns the build.exe return value.
rem     --debug
rem             build in debug mode.  Release mode is the default.
rem     --basedir directory
rem             sets the base directory to the supplied directory
rem     --labels build-version build-version-details
rem             set the corresponding build information labels

set debug=
set build_version=
set build_version_details=
set build_mode=RELEASE
set exit=0
set _build_args=

:Process_Args

if "%~1" == "" goto Done_Processing

    if /i "%~1" == "--exit" (
        set exit=1
        shift
        goto Process_Args
    )

    if /i "%~1" == "--debug" (
        set debug=true
        set build_mode=DEBUG
        shift
        goto Process_Args
    )

    if /i "%~1" == "--basedir" (
        if "%~2" == "" (
            echo Missing Argument to --basedir:  --basedir directory
            goto failed
        ) else (
            cd "%~2\yukon-server"
            shift
            shift
        )
        goto Process_Args
    )

    if /i "%~1" == "--labels" (
        if "%~3" == "" (
            echo Missing Argument to --labels:  --labels build-version build-version-details
            goto failed
        ) else (
rem The following preserves any special characters that may be passed in on the command line. eg: ()
            for /f "tokens=*" %%s in (%2) do set build_version=%%~s
            for /f "tokens=*" %%s in (%3) do set build_version_details=%%~s
            shift
            shift
            shift
        )
        goto Process_Args
    )

    set _build_args=%_build_args% %~1
    shift
    goto Process_Args

:Done_Processing

echo.
echo +---------------------------------------
echo ^|

rem ---- look for Makefile in the current directory - if found, we can build here

if exist Makefile       ( goto makefile_found )

rem ---- didn't find a Makefile - maybe we're in an subdirectory (/bin or /include, for example)
rem ----   or maybe we're in SQL or one of those others without a Makefile, in which case we want to build in the parent anyway

echo ^| Makefile not found in %cd%.
echo ^|
echo ^| Checking parent directory.

cd .. > nul 2<&1

if "%cwd%" == "%cd%"    ( echo ^| Cannot change to parent directory.
                          goto failed )

if exist Makefile       ( goto makefile_found )

echo ^| Makefile not found in %cd%.

goto failed


:makefile_found

echo ^| Makefile found in %cd%.
echo ^|

set build_directory=%cd%

echo ^| Checking if this is a subdirectory off the project root.

cd ..

if "%cd%" == "%build_directory%"    ( echo ^| Cannot change to parent directory.
                                      goto build )

if     exist Makefile   ( echo ^| Makefile found in %cd%.
                          goto build )
if not exist Makefile   ( echo ^| Makefile not found in %cd%.
                          cd %build_directory%
                          goto build )

:build
rem ---- do the build

set datetime=%date% %time%
echo ^|
echo +---------------------------------------
echo ^|
echo ^| Project root is %cd%.
echo ^|

set compilebase=%cd%
set yukonoutput=%cd%\bin

rem -- build_version and build_version_details track each other - if one is set they
rem     both are.  if one is unset they both are unset, so just check build_version.

if not defined build_version (

    for /f "tokens=1* delims== " %%p in (%cd%\..\yukon-build\build_version.properties) do (

        if "%%p" == "version.external" (
            set build_version=%%q
        )
        if "%%p" == "version.internal" (
            set build_version_details=%%q
        )
    )
)

echo +---------------------------------------
echo ^|
echo ^| Build version: %build_version%
echo ^| Build details: %build_version_details%
echo ^| Build mode   : %build_mode%
echo ^|
echo ^| Hudson BUILD_ID: %BUILD_ID%

if not exist %yukonoutput% md %yukonoutput%
cd %build_directory%

echo ^|
echo ^| Building in %build_directory%.
echo ^|
echo +---------------------------------------
echo.

build  %_build_args%

SET _ERRORLEVEL=%ERRORLEVEL%


if %_ERRORLEVEL% neq 0 goto failed

echo ^|
echo +---------------------------------------
echo ^|
echo ^| Build succeeded.
echo ^|
echo +---------------------------------------

goto cleanup


:failed

echo ^|
echo +---------------------------------------
echo ^|
echo ^| Build failed.
echo ^|
echo +---------------------------------------

rem ---- put any other cleanup tasks here
:cleanup

echo.
echo Began: %datetime%
echo Ended: %date% %time%.
echo.

cd %cwd%
set cwd=

if %exit% equ 1 (
    if %_ERRORLEVEL% neq 0 exit %_ERRORLEVEL%
)

rem -- Return a real exit code without closing the shell.

exit /b %_ERRORLEVEL%

