#Build name MUST BE FIRST!!!!

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(RTDB)\include \
-I$(PROT)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)


SIMULATOR_TEST_OBJS=\
test_main.obj \
test_ccusim.obj \
test_behavior_collection.obj \
test_delay_behavior.obj \
test_bch_behavior.obj \
test_frozen_read_parity_behavior.obj \
test_frozen_peak_timestamp_behavior.obj \
test_random_consumption_behavior.obj \
test_mct410_sim.obj \


CCU_SIMULATOR_BASE_OBJS=\
SimulatorUtils.obj \
Simulator.obj \
SimulatorLogger.obj \
Ccu710.obj \
PlcInfrastructure.obj \
PlcTransmitter.obj \
ScopedLogger.obj \
PortLogger.obj \
EmetconWords.obj \
DeviceMemoryManager.obj \
Mct410.obj \
DelayBehavior.obj \
BchBehavior.obj \
FrozenPeakTimestampBehavior.obj \
FrozenReadParityBehavior.obj \
RandomConsumptionBehavior.obj \

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \

SIMULATOR_TEST_FULLBUILD = $[Filename,$(OBJ),SimulatorTestFullBuild,target]

ALL:            test_simulator.exe

$(SIMULATOR_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(SIMULATOR_TEST_OBJS)]

test_simulator.exe:    $(SIMULATOR_TEST_FULLBUILD) $(SIMULATOR_TEST_OBJS) Makefile
	@echo:
	@echo Creating Executable $(OBJ)\$(@B).exe
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) $(PCHFLAGS) $(RWCPPFLAGS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(@B).exe \
	$(SIMULATOR_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(CCU_SIMULATOR_BASE_OBJS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	$(MANIFEST_TOOL) -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.
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


######################################################################################
#UPDATE#
bchbehavior.obj:	precompiled.h BchBehavior.h PlcBehavior.h types.h \
		SimulatorLogger.h logger.h dlldefs.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
ccu710.obj:	precompiled.h Ccu710.h PlcTransmitter.h CommInterface.h \
		ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h socket_helper.h numstr.h BehaviorCollection.h \
		SimulatorLogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h CtiPCPtrQueue.h \
		CommsBehavior.h portlogger.h emetconwords.h Simulator.h \
		PlcInfrastructure.h Mct410.h DeviceMemoryManager.h \
		MctBehavior.h ScopedLogger.h smartmap.h boostutil.h dllbase.h \
		dsm2.h dsm2err.h words.h optional.h macro_offset.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h
ccu711.obj:	precompiled.h ccu711.h ccu710.h PlcTransmitter.h \
		CommInterface.h ctinexus.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h socket_helper.h numstr.h \
		BehaviorCollection.h SimulatorLogger.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h CtiPCPtrQueue.h CommsBehavior.h portlogger.h \
		emetconwords.h CcuIDLC.h boostutil.h cti_asmc.h color.h \
		ctidate.h simulator.h PlcInfrastructure.h Mct410.h \
		DeviceMemoryManager.h MctBehavior.h ScopedLogger.h smartmap.h \
		dllbase.h dsm2.h dsm2err.h words.h optional.h macro_offset.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h
ccu721.obj:	precompiled.h Ccu721.h CcuIDLC.h PlcTransmitter.h \
		CommInterface.h ctinexus.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h socket_helper.h numstr.h \
		BehaviorCollection.h SimulatorLogger.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h CtiPCPtrQueue.h CommsBehavior.h portlogger.h \
		EmetconWords.h ctidate.h simulator.h PlcInfrastructure.h \
		Mct410.h DeviceMemoryManager.h MctBehavior.h ScopedLogger.h \
		smartmap.h boostutil.h dllbase.h dsm2.h dsm2err.h words.h \
		optional.h macro_offset.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h cti_asmc.h
ccuidlc.obj:	precompiled.h CcuIDLC.h PlcTransmitter.h CommInterface.h \
		ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h socket_helper.h numstr.h BehaviorCollection.h \
		SimulatorLogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h CtiPCPtrQueue.h \
		CommsBehavior.h portlogger.h
ccusimsvc.obj:	precompiled.h ctitime.h dlldefs.h ccusimsvc.h \
		cservice.h ctibase.h ctinexus.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h socket_helper.h numstr.h dllbase.h \
		dsm2.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h dsm2err.h words.h optional.h macro_offset.h
ccu_simulator.obj:	precompiled.h SimulatorUtils.h types.h Simulator.h \
		PlcInfrastructure.h Mct410.h EmetconWords.h ctitime.h \
		dlldefs.h SimulatorLogger.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h DeviceMemoryManager.h BehaviorCollection.h \
		MctBehavior.h ScopedLogger.h smartmap.h boostutil.h dllbase.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h \
		words.h optional.h macro_offset.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h Ccu711.h ccu710.h \
		PlcTransmitter.h CommInterface.h ctinexus.h socket_helper.h \
		CommsBehavior.h portlogger.h CcuIDLC.h Ccu721.h ctidate.h \
		DelayBehavior.h BchBehavior.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h
comminterface.obj:	precompiled.h CommInterface.h ctinexus.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		socket_helper.h numstr.h BehaviorCollection.h \
		SimulatorLogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h CtiPCPtrQueue.h \
		CommsBehavior.h
delaybehavior.obj:	precompiled.h DelayBehavior.h CommsBehavior.h \
		types.h SimulatorLogger.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h CtiPCPtrQueue.h
devicememorymanager.obj:	precompiled.h DeviceMemoryManager.h types.h \
		numstr.h dlldefs.h
emetconwords.obj:	precompiled.h EmetconWords.h types.h dlldefs.h \
		cti_asmc.h cticalls.h os2_2w32.h
frozenpeaktimestampbehavior.obj:	precompiled.h \
		FrozenPeakTimestampBehavior.h MctBehavior.h SimulatorLogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
frozenreadparitybehavior.obj:	precompiled.h FrozenReadParityBehavior.h \
		MctBehavior.h SimulatorLogger.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h
mct410.obj:	precompiled.h Mct410.h EmetconWords.h types.h ctitime.h \
		dlldefs.h SimulatorLogger.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h DeviceMemoryManager.h BehaviorCollection.h \
		MctBehavior.h ScopedLogger.h cparms.h rwutil.h yukon.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h \
		FrozenReadParityBehavior.h FrozenPeakTimestampBehavior.h \
		RandomConsumptionBehavior.h
plcinfrastructure.obj:	precompiled.h plcinfrastructure.h Mct410.h \
		EmetconWords.h types.h ctitime.h dlldefs.h SimulatorLogger.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		DeviceMemoryManager.h BehaviorCollection.h MctBehavior.h \
		ScopedLogger.h smartmap.h boostutil.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h macro_offset.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h
plctransmitter.obj:	precompiled.h PlcTransmitter.h CommInterface.h \
		ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h socket_helper.h numstr.h BehaviorCollection.h \
		SimulatorLogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h CtiPCPtrQueue.h \
		CommsBehavior.h portlogger.h
portlogger.obj:	precompiled.h PortLogger.h SimulatorLogger.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
randomconsumptionbehavior.obj:	precompiled.h \
		RandomConsumptionBehavior.h MctBehavior.h SimulatorLogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
scopedlogger.obj:	precompiled.h ScopedLogger.h SimulatorLogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
simulator.obj:	precompiled.h Simulator.h PlcInfrastructure.h Mct410.h \
		EmetconWords.h types.h ctitime.h dlldefs.h SimulatorLogger.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		DeviceMemoryManager.h BehaviorCollection.h MctBehavior.h \
		ScopedLogger.h smartmap.h boostutil.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h macro_offset.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h
simulatorlogger.obj:	precompiled.h SimulatorLogger.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h ScopedLogger.h
simulatorutils.obj:	precompiled.h SimulatorUtils.h types.h
simulator_main.obj:	precompiled.h ctitime.h dlldefs.h ccusimsvc.h \
		cservice.h CServiceConfig.h ctibase.h ctinexus.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h socket_helper.h \
		numstr.h dllbase.h dsm2.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h \
		connection_base.h
test_bch_behavior.obj:	BehaviorCollection.h SimulatorLogger.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h BchBehavior.h PlcBehavior.h
test_behavior_collection.obj:	BehaviorCollection.h SimulatorLogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
test_ccusim.obj:	EmetconWords.h types.h SimulatorUtils.h \
		PlcInfrastructure.h Mct410.h ctitime.h dlldefs.h \
		SimulatorLogger.h logger.h thread.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		DeviceMemoryManager.h BehaviorCollection.h MctBehavior.h \
		ScopedLogger.h smartmap.h boostutil.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h macro_offset.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h Simulator.h Ccu710.h \
		PlcTransmitter.h CommInterface.h ctinexus.h socket_helper.h \
		CommsBehavior.h portlogger.h
test_delay_behavior.obj:	DelayBehavior.h CommsBehavior.h types.h \
		SimulatorLogger.h logger.h dlldefs.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h BehaviorCollection.h
test_frozen_peak_timestamp_behavior.obj:	FrozenPeakTimestampBehavior.h \
		MctBehavior.h SimulatorLogger.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		BehaviorCollection.h
test_frozen_read_parity_behavior.obj:	FrozenReadParityBehavior.h \
		MctBehavior.h SimulatorLogger.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		BehaviorCollection.h
test_mct410_sim.obj:	Mct410.h EmetconWords.h types.h ctitime.h \
		dlldefs.h SimulatorLogger.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h DeviceMemoryManager.h BehaviorCollection.h \
		MctBehavior.h ctidate.h boost_test_helpers.h \
		millisecond_timer.h
test_random_consumption_behavior.obj:	RandomConsumptionBehavior.h \
		MctBehavior.h SimulatorLogger.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		BehaviorCollection.h
#ENDUPDATE#

