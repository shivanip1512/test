# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SERVICE)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(RW) \
-I$(R_COMMON)\include \
-I$(R_SERVICE)\include \
-I$(R_CPARMS)\include \

.PATH.cpp = .;$(R_SERVICE)

DLLOBJS=\
cservice.obj \
cserviceconfig.obj \
eventlog.obj

CPPFLAGS=\
-D_DLL_SERVICE

LIBS=\
advapi32.lib

ALL:        service.dll


service.dll:  $(DLLOBJS) Makedll.mak
              @echo Building  $@
              @%cd $(OBJ)
              $(CC) /Gd $(DLLFLAGS) $(DLLOBJS) $(INCLPATHS) $(LIBS) $(RWLIBS) /Fe..\$@
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              -@if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
              -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
              -@if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
              @%cd $(CWD)

copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\service.dll copy bin\service.dll $(YUKONOUTPUT)
           -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
           -if exist bin\service.lib copy bin\service.lib $(COMPILEBASE)\lib


deps:
                scandeps -Output makedll.mak *.cpp


.cpp.obj :
                @echo:
                @echo Compiling: $<
                @echo C-Options: $(DLLFLAGS)
                @echo Output   : bin\$@
                @echo:
                $(RWCPPINVOKE) $(CPPFLAGS) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

#UPDATE#
#ENDUPDATE#
