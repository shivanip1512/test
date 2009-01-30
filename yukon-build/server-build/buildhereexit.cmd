@echo off
rem title %cd%


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

rem ---- look for Makefile in the current directory - if found, we can build here

if     exist Makefile   ( goto makefile_found )

rem ---- didn't find a Makefile - maybe we're in an subdirectory (/bin or /include, for example)
rem ----   or maybe we're in SQL or one of those others without a Makefile, in which case we want to build in the parent anyway

echo ^| Makefile not found in %cd%.
echo ^|
echo ^| Checking parent directory.

cd .. > nul 2<&1

if %cwd%==%cd%          ( echo ^| Cannot change to parent directory.
                          goto failed )

if     exist Makefile   ( goto makefile_found )

echo ^| Makefile not found in %cd%.

goto failed



:makefile_found

echo ^| Makefile found in %cd%.
echo ^|

set build_directory=%cd%

echo ^| Checking if this is a subdirectory off the project root.

cd ..

if %cd%==%build_directory%      ( echo ^| Cannot change to parent directory.
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

set compilebase=%cd%
set yukonoutput=%cd%\bin
if not exist %yukonoutput% md %yukonoutput%
cd %build_directory%

echo ^| Building in %build_directory%.
echo ^|
echo +---------------------------------------
echo.

build buildlabel=%1
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

if not "%_ERRORLEVEL%" == "0" exit %_ERRORLEVEL%