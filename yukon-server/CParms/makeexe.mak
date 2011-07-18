!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(CPARMS)\include \
-I$(COMMON)\include \
-I$(BOOST) \
-I$(SQLAPI)\include \
-I$(RW) \


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
                $(RWCPPINVOKE) -I$(RW) -I$(COMMINC) $(RWLINKFLAGS) /Fe..\$@ \
cparmtest.obj \
-link $(COMPILEBASE)\lib\cparms.lib $(COMPILEBASE)\lib\cticparms.lib $(RWLIBS) $(BOOST_LIBS)
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
                -@if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target $@
                @echo:
                @%cd $(CWD)


conftest.exe:   $(TESTOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(TESTOBJS) -link $(COMPILEBASE)\lib\cparms.lib $(RWLIBS) $(BOOST_LIBS)
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                 mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
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


