# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

ALL:            copy


copy:
               -@if not exist $(YUKONOUTPUT)\yukon md $(YUKONOUTPUT)
               -if exist *.dll copy $(TCL_LIB).dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist *.lib copy $(TCL_LIB).lib $(COMPILEBASE)\lib





