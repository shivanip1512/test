!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(CPARMS)\include \
-I$(COMMON)\include \
-I$(RW) \


.PATH.cpp = .;$(R_CPARMS)


TESTOBJS=\
conftest.obj


CTIPROGS=\
cparmtest.exe \
conftest.exe

ALL:            $(CTIPROGS)


cparmtest.exe:  cparmtest.obj Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) -I$(RW) -I$(COMMINC) $(RWLINKFLAGS) -o ..\$@ \
cparmtest.obj \
-link $(COMPILEBASE)\lib\cparms.lib $(COMPILEBASE)\lib\cticparms.lib $(RWLIBS)
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -@if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target $@
                @echo:
                @%cd $(CWD)


conftest.exe:   $(TESTOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(TESTOBJS) -link $(COMPILEBASE)\lib\cparms.lib $(RWLIBS)
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -@if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -@if exist conftest.exe copy conftest.exe $(YUKONOUTPUT)
                -@if exist cparmtest.exe copy cparmtest.exe $(YUKONOUTPUT)


clean:
        -del *.obj
        -del *.pch
        -del *.pdb
        -del *.sdb
        -del *.adb
        -del *.ilk
        -del *.exe
        -del *.dll
        -del *.lib


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<



######################################################################################


