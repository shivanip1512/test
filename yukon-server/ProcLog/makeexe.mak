# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(PROCLOG)\include \
-I$(COMMON)\include \
-I$(RW) \
-I$(R_COMMON)\include \
-I$(R_PROCLOG)\include \


.PATH.cpp = .;$(R_PROCLOG)


PROCLIBS=$(COMPILEBASE)\lib\ctibase.lib


ALL:            plog.exe plogtest.exe


plog.exe:       plog.obj Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
plog.obj -link $(LIBS) $(RWLIBS) $(COMPILEBASE)\lib\proclog.lib
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target $@
                @echo:
                @%cd $(CWD)

plogtest.exe:     test.obj Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
test.obj -link $(LIBS) $(RWLIBS) $(COMPILEBASE)\lib\proclog.lib $(COMPILEBASE)\lib\ctibase.lib
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target $@
                @echo:
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


clean:
                -del *.obj
                -del *.dll
                -del *.ilk
                -del *.pdb
                -del *.lib
                -del *.exp

deps:
                scandeps -Output makeexe.mak *.cpp

.cpp.obj:
                @echo:
                @echo Compiling: $<
                @echo C-Options: $(CFLAGS)
                @echo Output   : $@
                @echo:
                $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

#UPDATE#
id_proclog.obj:	id_proclog.h
#ENDUPDATE#
