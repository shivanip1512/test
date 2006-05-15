# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

ALL:           copy
		
copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if not exist $(YUKONOUTPUT)\ClrDump.dll copy .\DLL\ClrDump.dll $(YUKONOUTPUT)
               -@if not exist $(YUKONOUTPUT)\dbghelp.dll copy .\DLL\dbghelp.dll $(YUKONOUTPUT)
	       -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -@if not exist $(COMPILEBASE)\lib\clrdump.lib copy .\DLL\clrdump.lib $(COMPILEBASE)\lib
	       
