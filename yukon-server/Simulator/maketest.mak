#Build name MUST BE FIRST!!!!

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(MSG)\include \
-I$(BOOST) \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)


TESTOBJS=\
test_ccusim.obj \


CCU_SIMULATOR_OBJS=\
EmetconWord.obj \
ccu710.obj \
ccu711.obj \

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib


CMTEST=\
cmdparsetestgenerator.exe


ALL:            ctibasetest

ctibasetest:    $(TESTOBJS) $(CCU_SIMULATOR_OBJS) Makefile

deps:
                scandeps -Output maketest.mak *.cpp

clean:
        -del \
test*.pdb \
$(OBJ)\test*.obj \
$(BIN)\test*.pdb \
$(BIN)\test*.pch \
$(BIN)\test*.ilk \
$(BIN)\test*.exp \
$(BIN)\test*.lib \
$(BIN)\test*.dll \
$(BIN)\test*.exe


allclean:   clean test

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy bin\*.exe $(YUKONOUTPUT)


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

        @echo:
        @echo Creating Executable $(@B).exe
        @echo:
        @%cd $(OBJ)
        $(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(@B).exe \
        $(@B).obj $(CCU_SIMULATOR_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\clrdump.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS) $(LINKFLAGS)

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy ..\$(BIN)\$(@B).exe $(YUKONOUTPUT)
        -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
        -if exist ..\$(BIN)\$(@B).lib copy ..\$(BIN)\$(@B).lib $(COMPILEBASE)\lib
        @%cd $(CWD)
        @echo.

######################################################################################
ctidate.obj:    yukon.h ctidate.h
ctitime.obj:    yukon.h ctitime.h
cmdparse.obj:   yukon.h precompiled.h ctidbgmem.h cmdparse.h dlldefs.h \
                parsevalue.h cparms.h devicetypes.h logger.h thread.h mutex.h \
                guard.h numstr.h pointdefs.h utility.h dsm2.h
test_cmdparse.obj:      cmdparse.h test_cmdparse_input.h test_cmdparse_output.h
cmdparsetestgenerator.obj:  cmdparse.h test_cmdparse_input.h
test_ctidate.obj:  ctidate.h
test_ctitime.obj:  ctitime.h

#UPDATE#
ccu710.obj:	yukon.h precompiled.h ctidbgmem.h CCU710.h EmetconWord.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h numstr.h \
		cticalls.h os2_2w32.h types.h color.h
ccu711.obj:	yukon.h precompiled.h ctidbgmem.h CCU711.h CCU710.h \
		EmetconWord.h ctiTime.h dlldefs.h mctStruct.h ctinexus.h \
		netports.h cticonnect.h numstr.h cticalls.h os2_2w32.h \
		types.h cti_asmc.h color.h
ccusimulator.obj:	yukon.h precompiled.h ctidbgmem.h cticalls.h \
		os2_2w32.h dlldefs.h types.h ctinexus.h netports.h \
		cticonnect.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		color.h ctiTime.h dbaccess.h dllbase.h sema.h mctStruct.h \
		CCU710.h EmetconWord.h CCU711.h
clientnexus.obj:	yukon.h precompiled.h ctidbgmem.h clientNexus.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h numstr.h \
		cticalls.h os2_2w32.h types.h
emetconword.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWord.h \
		cticalls.h os2_2w32.h dlldefs.h types.h cti_asmc.h numstr.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
test_ccusim.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h ctistring.h rwutil.h boost_time.h CCU711.h \
		CCU710.h EmetconWord.h mctStruct.h
#ENDUPDATE#
