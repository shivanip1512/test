@ECHO OFF
SET PowerShellScriptPath=%~dp0%zip.ps1
PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' '%2' '%3'"