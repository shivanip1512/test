
rem Set the Configuration information
set build_mode=RELEASE
for %%I in (%*) do (
  if "%%I" == "Debug" set build_mode=DEBUG
)

rem  Look for the build_version.properties based on the location of this
rem  script.  %~dp0 resolves out to the scripts directory
for /f "tokens=1* delims== " %%p in (%~dp0..\yukon-build\build_version.properties) do (

rem It's nigh impossible to handle variables with () in then in a batch 
rem script so instead of using version.external we use 
rem version.external.filenameSafe
    if "%%p" == "version.external.filenameSafe" (
        set my_version=%%q
    )
    if "%%p" == "version.internal" (
        set my_version_details=%%q
    )
)

rem parse the version into nodes.
for /f "tokens=1,2,3 delims=." %%p in ("%my_version_details%") do (
        set my_version_maj=%%p
        set my_version_min=%%q
        set my_version_rev=%%r
)

rem parse the build number out of the external version
for /f "tokens=4 delims=_" %%p in ("%my_version%") do (
        set my_version_build=%%p
)

rem Get the SVN global revison number
FOR /F "tokens=* USEBACKQ" %%F IN (`svnversion %~dp0`) DO (
SET my_version_svn=%%F
)

rem Use this file for communicating revision stuff to the .cpp and .rc build
set versionFileName=%~dp0common\include\version.h

echo +---------------------------------------
echo ^|
echo ^| Build version : %my_version_maj%.%my_version_min% ^(build %my_version_build%^)
echo ^| Build details : %my_version_details%
echo ^| Build mode    : %build_mode%
echo ^| Build SVN revision: %my_version_svn%
echo ^|
echo +---------------------------------------

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
      if not "!rev!" == "%my_version_maj%.%my_version_min%.%my_version_rev%.%my_version_svn%" set updateVersion=1
    )
  )
)

if "%updateVersion%" == "1" (
  echo Updating %versionFileName%
  echo #define D_FILE_VERSION %my_version_maj%,%my_version_min%,%my_version_rev%,%my_version_svn% >%versionFileName%
  echo #define D_FILE_VERSION_STR %my_version_maj%.%my_version_min%.%my_version_rev%.%my_version_svn% >>%versionFileName%
  echo #define D_PRODUCT_VERSION %my_version_maj%,%my_version_min%,%my_version_rev%,%my_version_svn% >>%versionFileName%
  echo #define D_PRODUCT_VERSION_STR %my_version_maj%.%my_version_min%.%my_version_rev%.%my_version_svn% >>%versionFileName%
  echo.>>%versionFileName%

  echo #define BUILD_VERSION %my_version_maj%.%my_version_min% ^(build %my_version_build%^) >>%versionFileName%
  echo #define BUILD_VERSION_DETAILS %my_version_details% >>%versionFileName%
)

goto :EOF

rem For some strange reason, the for parser leaves a trailing space on the parameter.  This strips it off.
:trim
set %2=%1
goto :EOF
