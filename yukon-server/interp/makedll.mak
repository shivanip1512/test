!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(TCLINC) \
-I$(RW) \
-I$(BOOST) \

INTERPOBJS=\
interp.obj \
interp_pool.obj


CTILIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\$(TCL_LIB).lib



CTIPROGS=\
interp.dll


ALL:            $(CTIPROGS)

interp.dll:  $(INTERPOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(INTERPOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(CTILIBS) advapi32.lib
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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) /D_DLL_INTERP -I..\include $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################

