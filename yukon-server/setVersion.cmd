
rem Set the Configuration information
set build_mode=RELEASE
for %%I in (%*) do (
  if "%%I" == "Debug" set build_mode=DEBUG
)

rem  Look for the build_version.properties based on the location of this
rem  script.  %~dp0 resolves out to the scripts directory
for /F "eol=# tokens=1,2 delims== " %%a in (%~dp0..\yukon-build\build_version.properties) do (
    if NOT "%%a"=="" if NOT "%%b"=="" set props.%%a=%%b
)

rem Retrieve the Git commit hash
FOR /F "tokens=* USEBACKQ" %%F IN (`git rev-parse --short HEAD`) DO (
SET git_hash=%%F
)

rem Build Release Number
FOR /F "tokens=* USEBACKQ" %%F IN (`git rev-list HEAD --count`) DO (
SET git_commit_current_count=%%F
SET /a build_release_number=git_commit_current_count-props.git.commit.init.count
)

SET version_dot_triplet=%props.version.major%.%props.version.minor%.%props.version.revision%
SET version_csv_triplet=%props.version.major%,%props.version.minor%,%props.version.revision%

rem Use this file for communicating revision stuff to the .cpp and .rc build
set version_filename=%~dp0common\include\version.h

echo +---------------------------------------
echo ^|
echo ^| Build version   : %version_dot_triplet% ^(build %build_release_number%^)
echo ^| Build mode      : %build_mode%
echo ^| Build Git hash  : %git_hash%
echo ^|
echo +---------------------------------------

setlocal enabledelayedexpansion
rem if it exists, check if it needs updating
if exist %version_filename% (
  for /f "tokens=2* delims== " %%p in (%version_filename%) do (
    if "%%p" == "D_PRODUCT_VERSION_STR" (
      call :trim %%q rev
      if not "!rev!" == "%version_dot_triplet%.%git_hash%" set do_version_update=1
    )
  )
) else (
  set do_version_update=1
)

if defined do_version_update (
  echo Updating %version_filename%
  echo #define D_FILE_VERSION %version_csv_triplet%,%build_release_number% >%version_filename%
  echo #define D_PRODUCT_VERSION_STR %version_dot_triplet%.%git_hash% >>%version_filename%
  echo.>>%version_filename%

  echo #define BUILD_VERSION %version_dot_triplet% ^(build %build_release_number%^) >>%version_filename%
)

goto :EOF

rem For some strange reason, the for parser leaves a trailing space on the parameter.  This strips it off.
:trim
set %2=%1
goto :EOF