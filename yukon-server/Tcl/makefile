# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

ALL:            dllcopy libcopy


dllcopy:
               -@if not exist $(YUKONOUTPUT)\yukon md $(YUKONOUTPUT)
               -@if not exist $(YUKONOUTPUT)\$(TCL_LIB).dll copy $(TCL_LIB).dll $(YUKONOUTPUT)

libcopy:
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -@if not exist $(COMPILEBASE)\lib\$(TCL_LIB).lib copy $(TCL_LIB).lib $(COMPILEBASE)\lib





