# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\ctibase.lib

TABLETESTOBJS=\
tabletest.obj \
tbl.obj \
tbl_route.obj

ALL:            tabletest.exe

tabletest.exe:  $(TABLETESTOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(TABLETESTOBJS) -link $(LIBS) $(RWLIBS)
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist $@ copy $@ $(YUKONOUTPUT)
                @%cd $(CWD)

                @echo Done building Target $@
                @echo:

clean:
                -del *.obj
                -del *.dll
                -del *.ilk
                -del *.pdb
                -del *.lib
                -del *.exp

.cpp.obj:
                @echo:
                @echo Compiling: $<
                @echo C-Options: $(CFLAGS)
                @echo Output   : $@
                @echo:
                $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<



