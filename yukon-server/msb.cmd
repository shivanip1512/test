@setlocal
@echo off

PATH ..\yukon-build\server-build;%PATH%
call "%VS140COMNTOOLS%vsvars32.bat"

:: defaults
set conf=/p:Configuration=Release
set mp=/m:8
set verbose=/v:m

for %%x in (%*) do (
  if "%%x" == "clean" set clean=/t:clean
  if "%%x" == "debug" set conf=/p:Configuration=Debug
  if "%%x" == "single" set mp=
  if "%%x" == "quiet" set verbose=/v:q
  if "%%x" == "min" set verbose=/v:m
  if "%%x" == "norm" set verbose=/v:n
  if "%%x" == "detail" set verbose=/v:d
  if "%%x" == "diag" set verbose=/v:diag
)

msbuild yukon-server.sln %conf% %mp% %verbose% %clean% 
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
