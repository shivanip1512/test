@echo off

rem  This logic for this script is based from these assumptions:
rem
rem  1)  When invoked from a subdirectory, buildhere should set the base directory properly
rem      and then build inside the original subdirectory.
rem  2)  The base directory contains a Makefile.
rem  3)  The buildable subdirectories contain a Makefile.
rem  4)  Other subdirectories exist that contain no Makefiles - if this script is invoked inside them,
rem        it should check the parent directory only

set cwd=%cd%
echo.
echo +---------------------------------------
echo ^|

rem ---- check to see if we should exit if the build has an error

if "%~1" equ "/exit" (
        set exit=true
        shift
) else (
        set exit=
)

if "%~1" equ "/basedir" (
        if "%~2" neq "" cd %~2\yukon\yukon-server
        shift
        shift
) else (
        set exit=
)

rem ---- look for build labels

if "%~1" equ "/labels" (
        if "%~2" neq "" (
        if "%~3" neq "" (
                set build_version=%~2
                set build_version_details=%~3
        ) else (
                echo ^| Must specify build_version and build_version_details if using "/labels"
                goto failed
        )
        ) else (
                echo ^| Must specify build_version and build_version_details if using "/labels"
                goto failed
        )
)

echo %*

rem ---- look for Makefile in the current directory - if found, we can build here

if     exist Makefile   ( goto makefile_found )

rem ---- didn't find a Makefile - maybe we're in an subdirectory (/bin or /include, for example)
rem ----   or maybe we're in SQL or one of those others without a Makefile, in which case we want to build in the parent anyway

echo ^| Makefile not found in %cd%.
echo ^|
echo ^| Checking parent directory.

cd .. > nul 2<&1

if "%cwd%" equ "%cd%"   ( echo ^| Cannot change to parent directory.
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

if "%cd%" equ "%build_directory%"  ( echo ^| Cannot change to parent directory.
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
echo +----------
echo ^|
echo ^| Project root is %cd%.
echo ^|

set compilebase=%cd%
set yukonoutput=%cd%\bin

if not defined build_version (
if not defined build_version_details (

        if exist %cd%\..\..\yukon-build\setenv.exe (

                %cd%\..\..\yukon-build\setenv.exe %cd%\..\..\yukon-build\build_version.properties internal build_version_details external build_version > env.bat
                call env.bat
                del env.bat

                if %errorlevel% neq 0 (

                        echo ^| %cd%\..\..\yukon-build\setenv.exe exited with error %errorlevel%.
                        goto failed
                )
        )
) )

rem ----  we have to have both build labels or neither

        echo +---------------------------------------
        echo ^|

if defined build_version (
        if defined build_version_details (

                echo ^| Build version: %build_version%
                echo ^| Build version details: %build_version_details%
                echo ^|

        ) else (
                echo ^| Build version: %build_version%
                echo ^| Build version details: [ ^!^! not set ^!^! ]
                echo ^|
                echo ^| Build version set, but build version details not set.
                goto failed

        )
) else (
        if defined build_version_details (

                echo ^| Build version: [ ^!^! not set ^!^! ]
                echo ^| Build version details: %build_version_details%
                echo ^|
                echo ^| Build version details set, but build version not set.
                goto failed

        ) else (
                echo ^| Build version and details unset - build will be untagged
        )
)

if not exist %yukonoutput% md %yukonoutput%
cd %build_directory%

echo ^|
echo ^| Building in %build_directory%.
echo ^|
echo +---------------------------------------
echo.

build
SET _ERRORLEVEL=%ERRORLEVEL%
rem cd /d %compilebase%
rem title %cd%
echo.
echo.
echo Began: %datetime%
echo Ended: %date% %time%.
goto cleanup





:failed
rem ---- we've failed, reset the environment

echo ^|
echo +----------
echo ^|
echo ^| Build failed.
echo ^|
echo +---------------------------------------

goto cleanup




:cleanup
rem ---- put any other cleanup tasks here

cd %cwd%
set cwd=

if "%exit%" neq "" (
if "%_ERRORLEVEL%" neq "0" exit %_ERRORLEVEL%
)