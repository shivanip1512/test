!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(CPARMS)\include \
-I$(COMMON)\include \
-I$(RW) \


.PATH.cpp = .;$(R_CPARMS)


CPARMOBJS=\
dllmain.obj \
configval.obj \
configkey.obj \
cparms.obj \


CTIPROGS=\
cticparms.dll



ALL:            $(CTIPROGS)

cticparms.dll:  $(CPARMOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(CPARMOBJS) -link $(RWLIBS) advapi32.lib
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -@if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -@if exist bin\*.dll copy bin\*.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\*.lib copy bin\*.lib $(COMPILEBASE)\lib


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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_CPARM -I..\include $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################

