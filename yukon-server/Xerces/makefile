# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

ALL:            copy


copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist *.dll copy $(XERCES_DLL).dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist *.lib copy $(XERCES_LIB).lib $(COMPILEBASE)\lib





