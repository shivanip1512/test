# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(SERVICE)\include \
-I$(COMMON)\include \
-I$(R_COMMON)\include \
-I$(R_SERVICE)\include \
-I$(R_CPARMS)\include \

.PATH.cpp = .;$(R_SERVICE)

DLLOBJS=\
$(PRECOMPILED_OBJ) \
cservice.obj \
cserviceconfig.obj \
eventlog.obj

CPPFLAGS=\
-D_DLL_SERVICE

LIBS=\
advapi32.lib \
User32.lib

CTIPROGS=\
service.dll


PROGS_VERSION=\
$(CTIPROGS)


ALL:        $(CTIPROGS)


service.dll:  $(DLLOBJS) Makedll.mak $(OBJ)\service.res
              @echo Building  $@
              @%cd $(OBJ)
              $(CC) /Gd $(DLLFLAGS) $(DLLOBJS) $(INCLPATHS) $(LIBS) /Fe..\$@ service.res
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              -@if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
	      -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	      -if exist ..\bin\$(@B).pdb copy ..\bin\$(@B).pdb $(YUKONDEBUG)
              -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
              -@if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
              @%cd $(CWD)

copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\service.dll copy bin\service.dll $(YUKONOUTPUT)
	   -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
           -if exist bin\service.pdb copy bin\service.pdb $(YUKONDEBUG)
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
                $(CC) $(CPPFLAGS) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

#UPDATE#
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
