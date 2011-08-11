#Build name MUST BE FIRST!!!!

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(RTDB)\include \
-I$(PROT)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(MSG)\include \
-I$(BOOST) \
-I$(SQLAPI)\include \
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
test_behavior_collection.obj \
test_delay_behavior.obj \
test_bch_behavior.obj \
test_frozen_read_parity_behavior.obj \
test_frozen_peak_timestamp_behavior.obj \
test_random_consumption_behavior.obj \
test_mct410_sim.obj \


CCU_SIMULATOR_BASE_OBJS=\
$(OBJ)\SimulatorLogger.obj \
$(OBJ)\ScopedLogger.obj \
$(OBJ)\PortLogger.obj \
$(OBJ)\EmetconWords.obj \
$(OBJ)\DeviceMemoryManager.obj \
$(OBJ)\Mct410.obj \
$(OBJ)\DelayBehavior.obj \
$(OBJ)\BchBehavior.obj \
$(OBJ)\FrozenPeakTimestampBehavior.obj \
$(OBJ)\FrozenReadParityBehavior.obj \
$(OBJ)\RandomConsumptionBehavior.obj \

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib

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
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(CCU_SIMULATOR_BASE_OBJS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################
#UPDATE#
bchbehavior.obj:	precompiled.h BchBehavior.h PlcBehavior.h types.h \
		SimulatorLogger.h logger.h dlldefs.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
ccu710.obj:	precompiled.h Ccu710.h PlcTransmitter.h CommInterface.h \
		ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h BehaviorCollection.h SimulatorLogger.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		CommsBehavior.h portlogger.h emetconwords.h Simulator.h \
		PlcInfrastructure.h Mct410.h ScopedLogger.h smartmap.h \
		boostutil.h dllbase.h dsm2.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h
ccu711.obj:	precompiled.h ccu711.h ccu710.h PlcTransmitter.h \
		CommInterface.h ctinexus.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h BehaviorCollection.h \
		SimulatorLogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h CommsBehavior.h portlogger.h emetconwords.h \
		CcuIDLC.h boostutil.h cti_asmc.h color.h ctidate.h \
		simulator.h PlcInfrastructure.h Mct410.h ScopedLogger.h \
		smartmap.h dllbase.h dsm2.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h
ccu721.obj:	precompiled.h Ccu721.h CcuIDLC.h PlcTransmitter.h \
		CommInterface.h ctinexus.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h BehaviorCollection.h \
		SimulatorLogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h CommsBehavior.h portlogger.h EmetconWords.h \
		ctidate.h simulator.h PlcInfrastructure.h Mct410.h \
		ScopedLogger.h smartmap.h boostutil.h dllbase.h dsm2.h \
		dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h cti_asmc.h
ccuidlc.obj:	precompiled.h CcuIDLC.h PlcTransmitter.h CommInterface.h \
		ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h BehaviorCollection.h SimulatorLogger.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		CommsBehavior.h portlogger.h
ccusimsvc.obj:	precompiled.h ctitime.h dlldefs.h ccusimsvc.h \
		cservice.h ctibase.h ctinexus.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h dllbase.h dsm2.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h
ccu_simulator.obj:	precompiled.h PlcInfrastructure.h Mct410.h \
		EmetconWords.h types.h ctitime.h dlldefs.h SimulatorLogger.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h ScopedLogger.h \
		smartmap.h boostutil.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h \
		BehaviorCollection.h Ccu710.h PlcTransmitter.h \
		CommInterface.h ctinexus.h CommsBehavior.h portlogger.h \
		Ccu711.h CcuIDLC.h Ccu721.h ctidate.h DelayBehavior.h \
		BchBehavior.h cparms.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h
comminterface.obj:	precompiled.h CommInterface.h ctinexus.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		BehaviorCollection.h SimulatorLogger.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h CtiPCPtrQueue.h CommsBehavior.h
delaybehavior.obj:	precompiled.h DelayBehavior.h CommsBehavior.h \
		types.h SimulatorLogger.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h CtiPCPtrQueue.h
emetconwords.obj:	precompiled.h EmetconWords.h types.h dlldefs.h \
		cti_asmc.h cticalls.h os2_2w32.h
mct410.obj:	precompiled.h Mct410.h EmetconWords.h types.h ctitime.h \
		dlldefs.h SimulatorLogger.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h ScopedLogger.h cparms.h rwutil.h yukon.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h
plcinfrastructure.obj:	precompiled.h plcinfrastructure.h Mct410.h \
		EmetconWords.h types.h ctitime.h dlldefs.h SimulatorLogger.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h ScopedLogger.h \
		smartmap.h boostutil.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h \
		BehaviorCollection.h
plctransmitter.obj:	precompiled.h PlcTransmitter.h CommInterface.h \
		ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h BehaviorCollection.h SimulatorLogger.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		CommsBehavior.h portlogger.h
portlogger.obj:	precompiled.h PortLogger.h SimulatorLogger.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
scopedlogger.obj:	precompiled.h ScopedLogger.h SimulatorLogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
simulatorlogger.obj:	precompiled.h SimulatorLogger.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h ScopedLogger.h
simulator_main.obj:	precompiled.h ctitime.h dlldefs.h ccusimsvc.h \
		cservice.h CServiceConfig.h ctibase.h ctinexus.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h dllbase.h dsm2.h \
		mutex.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h
test_bch_behavior.obj:	precompiled.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		BehaviorCollection.h SimulatorLogger.h logger.h thread.h \
		mutex.h guard.h CtiPCPtrQueue.h BchBehavior.h PlcBehavior.h
test_behavior_collection.obj:	precompiled.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h BehaviorCollection.h SimulatorLogger.h logger.h \
		thread.h mutex.h guard.h CtiPCPtrQueue.h
test_ccusim.obj:	CCU711.h ccu710.h PlcTransmitter.h CommInterface.h \
		ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h BehaviorCollection.h SimulatorLogger.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		CommsBehavior.h portlogger.h emetconwords.h CcuIDLC.h \
		ctidate.h Ccu721.h Mct410.h
test_delay_behavior.obj:	precompiled.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		BehaviorCollection.h SimulatorLogger.h logger.h thread.h \
		mutex.h guard.h CtiPCPtrQueue.h DelayBehavior.h \
		CommsBehavior.h
test_mct410_sim.obj:	precompiled.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		Mct410.h EmetconWords.h SimulatorLogger.h logger.h thread.h \
		mutex.h guard.h CtiPCPtrQueue.h
#ENDUPDATE#

