!include ..\common\global.inc
!include ..\common\rwglobal.inc

DLLDEF=..\include\logic.def

INCLPATHS+= \
-I$(COMMON)\include \
-I$(TCL)\include \
-I$(MSG)\include  \
-I$(CPARMS)\include \
-I$(MESSAGE)\include \
-I$(RW) \
-I$(BOOST) \

INTERPOBJS=\
logic.obj

CTILIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \

CTIPROGS=\
logic.dll


ALL:            $(CTIPROGS)

logic.dll:  $(INTERPOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(INTERPOBJS) -link $(RWLIBS) $(BOOST_LIBS) $(CTILIBS) $(TCL_LIBS) advapi32.lib -link /def:$(DLLDEF)
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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_INTERP -I..\include $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################

