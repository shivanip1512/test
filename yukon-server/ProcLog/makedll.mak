# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(PROCLOG)\include \
-I$(COMMON)\include \
-I$(RW) \

.PATH.cpp = .


BASEOBJS=\
errclient.obj \
errserver.obj \
errmsg.obj \
errlogfile.obj \
dllmain.obj \
proclog.obj

BASECS=\
errclient.cpp \
errserver.cpp \
errmsg.cpp \
errlogfile.cpp \
dllmain.cpp \
proclog.cpp

PROCLIBS=$(COMPILEBASE)\lib\ctibase.lib

PROCLOG_OBJS= \
proclog.obj \
dllmain.obj \
errclient.obj \
errserver.obj \
errmsg.obj

ALL:            proclog.dll

proclog.dll:    $(BASEOBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(BASEOBJS) id_proclog.obj $(INCLPATHS) $(PROCLIBS) /Fe..\$@ \
/link /SECTION:.shr,S $(RWLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\proclog.dll copy bin\proclog.dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\proclog.lib copy bin\proclog.lib $(COMPILEBASE)\lib

clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp

# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_proclog.obj

id_proclog.obj:    id_proclog.cpp include\id_proclog.h


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
                @echo:
                @echo Compiling: $<
                @echo C-Options: $(DLLFLAGS)
                @echo Output   : ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) -D_DLL_PROCLOG $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

######################################################################################


