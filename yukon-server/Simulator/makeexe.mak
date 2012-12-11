include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(RTDB)\include \
-I$(PROT)\include \
-I$(DATABASE)\include \
-I$(SERVICE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
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
;$(RTDB)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)



CCU_SIMULATOR_OBJS=\
SimulatorUtils.obj \
Simulator.obj \
ccu_simulator.obj \
CommInterface.obj \
SimulatorLogger.obj \
PortLogger.obj \
ScopedLogger.obj \
PlcInfrastructure.obj \
DeviceMemoryManager.obj \
FrozenReadParityBehavior.obj \
FrozenPeakTimestampBehavior.obj \
RandomConsumptionBehavior.obj \
EmetconWords.obj \
PlcTransmitter.obj \
Ccu710.obj \
Ccu711.obj \
Ccu721.obj \
CcuIDLC.obj \
Mct410.obj \
DelayBehavior.obj \
BchBehavior.obj \
simulator_main.obj \
ccusimsvc.obj \

CCU_SIMULATOR_LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiprtdb.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\service.lib \


CTIPROGS=\
ccu_simulator.exe


SIMULATOR_FULLBUILD = $[Filename,$(OBJ),SimulatorFullBuild,target]


PROGS_VERSION=\
$(CTIPROGS)


ALL:            $(CTIPROGS)


$(SIMULATOR_FULLBUILD) :
	@touch $@
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(CCU_SIMULATOR_OBJS)]


ccu_simulator.exe:      $(SIMULATOR_FULLBUILD) $(CCU_SIMULATOR_OBJS) makeexe.mak $(OBJ)\ccu_simulator.res
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(RWLINKFLAGS) $(INCLPATHS) /Fe..\$@ $(CCU_SIMULATOR_OBJS) -link $(CCU_SIMULATOR_LIBS) $(RWLIBS) $(BOOST_LIBS) ccu_simulator.res
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
                -@copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
                scandeps -Output makeexe.mak *.cpp



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<


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
		PlcInfrastructure.h Mct410.h DeviceMemoryManager.h \
		MctBehavior.h ScopedLogger.h smartmap.h boostutil.h dllbase.h \
		dsm2.h dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h
ccu711.obj:	precompiled.h ccu711.h ccu710.h PlcTransmitter.h \
		CommInterface.h ctinexus.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h BehaviorCollection.h \
		SimulatorLogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h CommsBehavior.h portlogger.h emetconwords.h \
		CcuIDLC.h boostutil.h cti_asmc.h color.h ctidate.h \
		simulator.h PlcInfrastructure.h Mct410.h \
		DeviceMemoryManager.h MctBehavior.h ScopedLogger.h smartmap.h \
		dllbase.h dsm2.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h
ccu721.obj:	precompiled.h Ccu721.h CcuIDLC.h PlcTransmitter.h \
		CommInterface.h ctinexus.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h BehaviorCollection.h \
		SimulatorLogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h CommsBehavior.h portlogger.h EmetconWords.h \
		ctidate.h simulator.h PlcInfrastructure.h Mct410.h \
		DeviceMemoryManager.h MctBehavior.h ScopedLogger.h smartmap.h \
		boostutil.h dllbase.h dsm2.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h \
		cti_asmc.h
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
ccu_simulator.obj:	precompiled.h SimulatorUtils.h types.h Simulator.h \
		PlcInfrastructure.h Mct410.h EmetconWords.h ctitime.h \
		dlldefs.h SimulatorLogger.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h DeviceMemoryManager.h BehaviorCollection.h \
		MctBehavior.h ScopedLogger.h smartmap.h boostutil.h dllbase.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h \
		words.h optional.h readers_writer_lock.h critical_section.h \
		PlcBehavior.h Ccu711.h ccu710.h PlcTransmitter.h \
		CommInterface.h ctinexus.h CommsBehavior.h portlogger.h \
		CcuIDLC.h Ccu721.h ctidate.h DelayBehavior.h BchBehavior.h \
		cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h
comminterface.obj:	precompiled.h CommInterface.h ctinexus.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		BehaviorCollection.h SimulatorLogger.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h CtiPCPtrQueue.h CommsBehavior.h
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
		MctBehavior.h ScopedLogger.h dev_mct410.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		config_data_mct.h ctidate.h dev_mct410_commands.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h \
		FrozenReadParityBehavior.h FrozenPeakTimestampBehavior.h \
		RandomConsumptionBehavior.h
plcinfrastructure.obj:	precompiled.h plcinfrastructure.h Mct410.h \
		EmetconWords.h types.h ctitime.h dlldefs.h SimulatorLogger.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		DeviceMemoryManager.h BehaviorCollection.h MctBehavior.h \
		ScopedLogger.h smartmap.h boostutil.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h readers_writer_lock.h critical_section.h \
		PlcBehavior.h
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
		optional.h readers_writer_lock.h critical_section.h \
		PlcBehavior.h
simulatorlogger.obj:	precompiled.h SimulatorLogger.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h ScopedLogger.h
simulatorutils.obj:	precompiled.h SimulatorUtils.h types.h
simulator_main.obj:	precompiled.h ctitime.h dlldefs.h ccusimsvc.h \
		cservice.h CServiceConfig.h ctibase.h ctinexus.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h dllbase.h dsm2.h \
		mutex.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h
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
		optional.h readers_writer_lock.h critical_section.h \
		PlcBehavior.h Simulator.h Ccu710.h PlcTransmitter.h \
		CommInterface.h ctinexus.h CommsBehavior.h portlogger.h
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
		MctBehavior.h ctidate.h
test_random_consumption_behavior.obj:	RandomConsumptionBehavior.h \
		MctBehavior.h SimulatorLogger.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		BehaviorCollection.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
