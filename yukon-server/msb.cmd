@setlocal
@echo off

PATH ..\yukon-build\server-build;%PATH%

set vs2017Edition=Enterprise
if exist "C:\Program Files (x86)\Microsoft Visual Studio\2017\Enterprise\" (
  set vs2017Edition=Enterprise
) else if exist "C:\Program Files (x86)\Microsoft Visual Studio\2017\Professional\" (
  set vs2017Edition=Professional
) else if exist "C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\" (
  set vs2017Edition=Community
) else (
  exit Unable to identify Microsoft Visual Studio 2017 edition information.
)
call "C:\Program Files (x86)\Microsoft Visual Studio\2017\%vs2017Edition%\VC\Auxiliary\Build\vcvars32.bat"

:: defaults
set conf=/p:Configuration=Release
set plat=/p:Platform=Win32
set mp=/m:8
set verbose=/v:m
set nonodereuse=/nodeReuse:false

for %%x in (%*) do (
  if "%%x" == "clean" set clean=/t:clean
  if "%%x" == "debug" set conf=/p:Configuration=Debug
  if "%%x" == "Win64" set plat=/p:Platform=Win64
  if "%%x" == "diag" set verbose=/v:diag
  if "%%x" == "diag" set verbose=/v:diag
  if "%%x" == "single" set mp=
  if "%%x" == "quiet" set verbose=/v:q
  if "%%x" == "min" set verbose=/v:m
  if "%%x" == "norm" set verbose=/v:n
  if "%%x" == "detail" set verbose=/v:d
  if "%%x" == "diag" set verbose=/v:diag
)

msbuild yukon-server.sln %conf% %plat% %mp% %verbose% %clean% %nonodereuse%
set rc=%ERRORLEVEL%

if not "%clean%" == "" (
  if "%conf%" == "/p:Configuration=Debug" (
    if exist bin-debug rd /s /q bin-debug
    if exist lib-debug rd /s /q lib-debug
    if exist pdb-debug rd /s /q pdb-debug
  ) else (
    if exist bin rd /s /q bin
    if exist lib rd /s /q lib
    if exist pdb rd /s /q pdb
  )
)

if %rc% gtr 0 echo build failed %rc%

exit /b %rc%
