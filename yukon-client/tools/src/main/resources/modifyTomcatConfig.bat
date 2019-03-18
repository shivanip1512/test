SET ThisScriptsDirectory=%~dp0
SET PowerShellScriptPath=%ThisScriptsDirectory%modifyTomcatConfig.ps1
PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' '%1'";