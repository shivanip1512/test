!include $(COMPILEBASE)\global.inc

DLLDEF=..\include\logic.def

INCLPATHS+= \
-I$(COMMON)\include \
-I$(TCL)\include \
-I$(MSG)\include  \
-I$(MESSAGE)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \

INTERPOBJS=\
$(PRECOMPILED_OBJ) \
logic.obj

CTILIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \

CTIPROGS=\
logic.dll

PROGS_VERSION=\
$(CTIPROGS)


ALL:            $(CTIPROGS)

logic.dll:  $(INTERPOBJS) Makefile $(OBJ)\logic.res
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(CC) $(INCLPATHS) $(DLLFLAGS) -Fe..\$@ $(INTERPOBJS) -link /LARGEADDRESSAWARE $(BOOST_LIBS) $(CTILIBS) $(TCL_LIBS) advapi32.lib -link /def:$(DLLDEF) logic.res
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -@if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
		-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
		-if exist ..\bin\$(@B).pdb copy ..\bin\$(@B).pdb $(YUKONDEBUG)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -@if exist bin\*.dll copy bin\*.dll $(YUKONOUTPUT)
		-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
                -@if exist bin\*.pdb copy bin\*.pdb $(YUKONDEBUG)
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
        $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_INTERP -I..\include $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################

include $(COMPILEBASE)\versioninfo.inc
