setlocal

if [%1] == [-all] (
    set _all=-OnlyRecentlyModified 0
    shift
)

if not [%1] == [] (
    set _files=-Files """%*"""
)

powershell -Command .\generate.ps1 -Type "cpp,java" %_all% %_files%