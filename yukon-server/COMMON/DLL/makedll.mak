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
	       
               -@if not exist $(YUKONOUTPUT)\thr$(BUILDTYPE).dll copy $(RWWORKSPACE)\lib\*.dll $(YUKONOUTPUT)
               -@if not exist $(YUKONOUTPUT)\msvcp90$(MICROSOFTDLL).dll copy $(YUKONBASE)\yukon-3rdparty\Microsoft\msvc*90$(MICROSOFTDLL).dll $(YUKONOUTPUT)
               -@if not exist $(YUKONOUTPUT)\Microsoft.VC90.DebugCRT.manifest copy $(YUKONBASE)\yukon-3rdparty\Microsoft\Microsoft.VC90.*CRT.manifest $(YUKONOUTPUT)
               -@if not exist $(YUKONOUTPUT)\ntwdblib.dll copy $(YUKONBASE)\yukon-3rdparty\Microsoft\ntwdblib.dll $(YUKONOUTPUT)

               -@if not exist $(YUKONOUTPUT)\$(XERCES_DLL).dll copy $(XERCESHOME)\bin\$(XERCES_DLL).dll $(YUKONOUTPUT)
               

clean:
        -del \
$(YUKONOUTPUT)\ClrDump.dll \
$(YUKONOUTPUT)\dbghelp.dll \
$(YUKONOUTPUT)\*15d.dll \
$(YUKONOUTPUT)\*12d.dll \
$(YUKONOUTPUT)\ntwdblib.dll \
$(YUKONOUTPUT)\msvcp90*.dll \
$(YUKONOUTPUT)\$(XERCES_DLL).dll \
$(YUKONOUTPUT)\Microsoft.VC90.*CRT.manifest \
