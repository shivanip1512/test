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
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)


TESTOBJS=\
test_ccusim.obj \


CCU_SIMULATOR_BASE_OBJS=\
$(OBJ)\ccu711.obj \
$(OBJ)\SharedFunctions.obj \
$(OBJ)\EmetconWord.obj \
$(OBJ)\SimulatedCCU.obj \
$(OBJ)\CCU711Manager.obj \
$(OBJ)\CCU710Manager.obj \
$(OBJ)\ccu710.obj \
$(OBJ)\EmetconWordBase.obj \
$(OBJ)\EmetconWordD1.obj \
$(OBJ)\EmetconWordB.obj \
$(OBJ)\EmetconWordC.obj \
$(OBJ)\EmetconWordDn.obj \
$(OBJ)\EmetconWordFactory.obj \
$(OBJ)\Mct410Sim.obj \

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib

ALL:            ctibasetest

ctibasetest:    $(TESTOBJS) Makefile

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
	@echo Creating Executable $(OBJ)\$(@B).exe
        @echo:
	$(CC) $(CFLAGS) $(INCLPATHS) $(PCHFLAGS) $(RWCPPFLAGS) $(RWLINKFLAGS)  /Fe$(BIN)\$(@B).exe \
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\clrdump.lib $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(CCU_SIMULATOR_BASE_OBJS) $(BOOSTTESTLIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
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
		ctitime.h dlldefs.h ctinexus.h netports.h cticonnect.h \
		numstr.h cticalls.h os2_2w32.h types.h color.h logger.h \
		thread.h mutex.h guard.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h SharedFunctions.h
ccu710manager.obj:	yukon.h precompiled.h ctidbgmem.h CCU710Manager.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h CCU710.h \
		EmetconWord.h ctitime.h SimulatedCCU.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h
ccu711.obj:	yukon.h precompiled.h ctidbgmem.h CCU711.h CCU710.h \
		EmetconWord.h ctitime.h dlldefs.h ctiDate.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		mctStruct.h EmetconWordB.h EmetconWordBase.h Mct410Sim.h \
		cti_asmc.h ctinexus.h netports.h cticonnect.h color.h \
		SharedFunctions.h EmetconWordC.h EmetconWordFactory.h
ccu711manager.obj:	yukon.h precompiled.h ctidbgmem.h CCU711Manager.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h SimulatedCCU.h \
		ctiTime.h CCU711.h CCU710.h EmetconWord.h ctiDate.h logger.h \
		thread.h mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h \
		sorted_vector.h mctStruct.h EmetconWordB.h EmetconWordBase.h \
		Mct410Sim.h cti_asmc.h
ccusimulator.obj:	yukon.h precompiled.h ctidbgmem.h cticalls.h \
		os2_2w32.h dlldefs.h types.h ctinexus.h netports.h \
		cticonnect.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		color.h ctiTime.h dbaccess.h dllbase.h sema.h mctStruct.h \
		SimulatedCCU.h CCU711Manager.h CCU711.h CCU710.h \
		EmetconWord.h ctiDate.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h EmetconWordB.h \
		EmetconWordBase.h Mct410Sim.h cti_asmc.h CCU710Manager.h
clientnexus.obj:	yukon.h precompiled.h ctidbgmem.h clientNexus.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h numstr.h \
		cticalls.h os2_2w32.h types.h
emetconword.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWord.h \
		ctitime.h dlldefs.h cticalls.h os2_2w32.h types.h cti_asmc.h \
		numstr.h SharedFunctions.h
emetconwordb.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWordB.h \
		EmetconWordBase.h SharedFunctions.h ctitime.h dlldefs.h
emetconwordbase.obj:	yukon.h precompiled.h ctidbgmem.h \
		EmetconWordBase.h
emetconwordc.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWordC.h \
		EmetconWordBase.h
emetconwordd1.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWordD1.h \
		EmetconWordBase.h dlldefs.h cti_asmc.h
emetconworddn.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWordDn.h \
		EmetconWordBase.h dlldefs.h cti_asmc.h
emetconwordfactory.obj:	yukon.h precompiled.h ctidbgmem.h \
		EmetconWordFactory.h EmetconWordBase.h EmetconWordD1.h \
		EmetconWordB.h EmetconWordC.h
mct410sim.obj:	yukon.h precompiled.h ctidbgmem.h Mct410Sim.h ctitime.h \
		dlldefs.h cti_asmc.h SharedFunctions.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		EmetconWordDn.h EmetconWordBase.h EmetconWordD1.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
sharedfunctions.obj:	yukon.h precompiled.h ctidbgmem.h \
		SharedFunctions.h ctitime.h dlldefs.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h
simulatedccu.obj:	yukon.h precompiled.h ctidbgmem.h SimulatedCCU.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h ctiTime.h
test_ccusim.obj:	ctitime.h dlldefs.h ctidate.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		CCU711.h CCU710.h EmetconWord.h mctStruct.h EmetconWordB.h \
		EmetconWordBase.h Mct410Sim.h cti_asmc.h yukon.h \
		precompiled.h ctidbgmem.h rwutil.h boost_time.h boostutil.h \
		SharedFunctions.h
#ENDUPDATE#
