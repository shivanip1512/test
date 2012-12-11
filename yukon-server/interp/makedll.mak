!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(TCL)\include \
-I$(RW) \
-I$(BOOST_INCLUDE) \

INTERPOBJS=\
interp.obj \
interp_pool.obj


CTILIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(TCL_LIBS)

CTIPROGS=\
interp.dll


PROGS_VERSION=\
$(CTIPROGS)


ALL:            $(CTIPROGS)

interp.dll:  $(INTERPOBJS) Makefile $(OBJ)\interp.res
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(INTERPOBJS) -link $(RWLIBS) $(BOOST_LIBS) $(CTILIBS) advapi32.lib interp.res
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

include $(COMPILEBASE)\versioninfo.inc
