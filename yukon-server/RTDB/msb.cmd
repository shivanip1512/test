@setlocal
@echo off

PATH ..\..\yukon-build\server-build;%PATH%

:: defaults
set conf=/p:Configuration=Release
set mp=/m:8
set verbose=/v:n

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

set SolutionDir=..\
for %%P in (*.vcxproj) do msbuild %%P %conf% %mp% %verbose% %clean% 2>&1 | tee %%~nP.log

