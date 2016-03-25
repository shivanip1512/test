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
rem batch scripts don't like to handle parameters with () in them.  Use this instead.
        if "%%p" == "version.external.filenameSafe" (
        set my_version=%%q
        )
        if "%%p" == "version.internal" (
            set build_version_details=%%q
        )
    )
)

rem parse the version into nodes.
for /f "tokens=1,2,3 delims=." %%p in ("%build_version_details%") do (
        set my_version_maj=%%p
        set my_version_min=%%q
        set my_version_rev=%%r
)

rem parse the build number out of the external version
for /f "tokens=4 delims=_" %%p in ("%my_version%") do (
        set my_version_build=%%p
)

if not "%BUILD_NUMBER%" == "" goto haveBuildNumber
  rem Get BUILD_NUMBER to the SVN global revison number if it's not already set.
  FOR /F "tokens=* USEBACKQ" %%F IN (`svnversion %~dp0`) DO (
  SET BUILD_NUMBER=%%F
  )
:haveBuildNumber

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

rem Use this file for communicating revision stuff to the .cpp and .rc build
set versionFileName=%cd%\common\include\version.h

rem Only update if we need to.
set updateVersion=0

rem version.h exist?
if not exist %versionFileName% set updateVersion=1

setlocal enabledelayedexpansion
rem if it exists, check if it needs updating
if "%updateVersion%" == "0" (
  for /f "tokens=2* delims== " %%p in (%versionFileName%) do (
      if "%%p" == "D_FILE_VERSION_STR" (
          call :trim %%q rev
          if not "!rev!" == "%my_version_maj%.%my_version_min%.%my_version_rev%.%BUILD_NUMBER%" set updateVersion=1
      )
  )
)

if "%updateVersion%" == "1" (
  echo Updating %versionFileName%
  echo #define D_FILE_VERSION %my_version_maj%,%my_version_min%,%my_version_rev%,%BUILD_NUMBER% >%versionFileName%
  echo #define D_FILE_VERSION_STR %my_version_maj%.%my_version_min%.%my_version_rev%.%BUILD_NUMBER% >>%versionFileName%
  echo #define D_PRODUCT_VERSION %my_version_maj%,%my_version_min%,%my_version_rev%,%BUILD_NUMBER% >>%versionFileName%
  echo #define D_PRODUCT_VERSION_STR %my_version_maj%.%my_version_min%.%my_version_rev%.%BUILD_NUMBER% >>%versionFileName%
  echo.>>%versionFileName%

  echo #define BUILD_VERSION %my_version_maj%.%my_version_min% ^(build %my_version_build%^) >>%versionFileName%
  echo #define BUILD_VERSION_DETAILS %build_version_details% >>%versionFileName%
)

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

rem For some strange reason, the for parser leaves a trailing space on the parameter.  This strips it off.
:trim
set %2=%1
goto :EOF
