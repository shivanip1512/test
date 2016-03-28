
# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(SIGNAL)\include \
-I$(DATABASE)\include \
-I$(COMMON)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \



.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \



BASEOBJS=\
$(PRECOMPILED_OBJ) \
dbsigsend.obj

TABLETESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctidbres.lib

EXECS=\
sigsend.exe

PROGS_VERSION=\
$(EXECS)

ALL:            $(EXECS)

sigsend.exe:    $(BASEOBJS) $(OBJ)\sigsend.res
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$@ \
$(BASEOBJS) -link /LARGEADDRESSAWARE $(LIBS) $(BOOST_LIBS) $(TABLETESTLIBS) sigsend.res
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	       -copy ..\bin\$(@B).pdb $(YUKONDEBUG)
                @echo Done building Target ..\$@
                @%cd $(CWD)

copy:           $(EXECS)
               -if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
               -@if exist bin\*.pdb copy bin\*.pdb $(YUKONDEBUG)


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
                $(CC) $(CFLAGS) $(CCOPTS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

deps:
                scandeps -Output makeexe.mak *.cpp

include $(COMPILEBASE)\versioninfo.inc
