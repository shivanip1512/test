include ..\common\global.inc
include ..\common\rwglobal.inc

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
;$(RTDB)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)



CCU_SIMULATOR_OBJS=\
ccu_simulator.obj \
CommInterface.obj \
PortLogger.obj \
PlcInfrastructure.obj \
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
$(COMPILEBASE)\lib\cticparms.lib \
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


ALL:            $(CTIPROGS)


$(SIMULATOR_FULLBUILD) :
	@touch $@
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(CCU_SIMULATOR_OBJS)]


ccu_simulator.exe:      $(SIMULATOR_FULLBUILD) $(CCU_SIMULATOR_OBJS) makeexe.mak
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(RWLINKFLAGS) $(INCLPATHS) /Fe..\$@ $(CCU_SIMULATOR_OBJS) -link $(CCU_SIMULATOR_LIBS) $(RWLIBS) $(BOOST_LIBS)
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
bchbehavior.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		BchBehavior.h PlcBehavior.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h CtiPCPtrQueue.h
ccu710.obj:	yukon.h precompiled.h types.h ctidbgmem.h Ccu710.h \
		portlogger.h logger.h dlldefs.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h emetconwords.h PlcTransmitter.h \
		CommInterface.h ctinexus.h netports.h cticonnect.h \
		BehaviorCollection.h CommsBehavior.h Simulator.h \
		PlcInfrastructure.h Mct410.h smartmap.h boostutil.h dllbase.h \
		dsm2.h dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h
ccu711.obj:	yukon.h precompiled.h types.h ctidbgmem.h ccu711.h \
		ccu710.h portlogger.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h CtiPCPtrQueue.h emetconwords.h PlcTransmitter.h \
		CommInterface.h ctinexus.h netports.h cticonnect.h \
		BehaviorCollection.h CommsBehavior.h CcuIDLC.h boostutil.h \
		cti_asmc.h color.h ctidate.h simulator.h PlcInfrastructure.h \
		Mct410.h smartmap.h dllbase.h dsm2.h dsm2err.h words.h \
		optional.h readers_writer_lock.h critical_section.h \
		PlcBehavior.h
ccu721.obj:	yukon.h precompiled.h types.h ctidbgmem.h Ccu721.h \
		CcuIDLC.h PlcTransmitter.h CommInterface.h ctinexus.h \
		dlldefs.h netports.h cticonnect.h BehaviorCollection.h \
		CommsBehavior.h portlogger.h logger.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h CtiPCPtrQueue.h EmetconWords.h ctidate.h simulator.h \
		PlcInfrastructure.h Mct410.h smartmap.h boostutil.h dllbase.h \
		dsm2.h dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h cti_asmc.h
ccuidlc.obj:	Yukon.h precompiled.h types.h ctidbgmem.h CcuIDLC.h \
		PlcTransmitter.h CommInterface.h ctinexus.h dlldefs.h \
		netports.h cticonnect.h BehaviorCollection.h CommsBehavior.h \
		portlogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
ccusimsvc.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h ccusimsvc.h cservice.h ctibase.h ctinexus.h \
		netports.h cticonnect.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h queues.h numstr.h dsm2err.h \
		words.h optional.h
ccu_simulator.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PlcInfrastructure.h Mct410.h EmetconWords.h ctitime.h \
		dlldefs.h smartmap.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dllbase.h dsm2.h mutex.h \
		guard.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h \
		BehaviorCollection.h Ccu710.h portlogger.h logger.h thread.h \
		CtiPCPtrQueue.h PlcTransmitter.h CommInterface.h ctinexus.h \
		CommsBehavior.h Ccu711.h CcuIDLC.h Ccu721.h ctidate.h \
		DelayBehavior.h BchBehavior.h cparms.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h
comminterface.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		CommInterface.h ctinexus.h dlldefs.h netports.h cticonnect.h \
		BehaviorCollection.h CommsBehavior.h
delaybehavior.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		DelayBehavior.h CommsBehavior.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h CtiPCPtrQueue.h
emetconwords.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		EmetconWords.h dlldefs.h cti_asmc.h cticalls.h os2_2w32.h
mct410.obj:	yukon.h precompiled.h types.h ctidbgmem.h Mct410.h \
		EmetconWords.h ctitime.h dlldefs.h logger.h thread.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h dev_mct410.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h sema.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h tbl_base.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		config_data_mct.h ctidate.h dev_mct410_commands.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h
plcinfrastructure.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		plcinfrastructure.h Mct410.h EmetconWords.h ctitime.h \
		dlldefs.h smartmap.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dllbase.h dsm2.h mutex.h \
		guard.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h \
		BehaviorCollection.h
plctransmitter.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PlcTransmitter.h CommInterface.h ctinexus.h dlldefs.h \
		netports.h cticonnect.h BehaviorCollection.h CommsBehavior.h \
		portlogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
portlogger.obj:	yukon.h precompiled.h types.h ctidbgmem.h portlogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
simulator_main.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctitime.h dlldefs.h ccusimsvc.h cservice.h CServiceConfig.h \
		ctibase.h ctinexus.h netports.h cticonnect.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h
test_bch_behavior.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h numstr.h BehaviorCollection.h BchBehavior.h \
		PlcBehavior.h logger.h thread.h mutex.h guard.h \
		CtiPCPtrQueue.h
test_behavior_collection.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h numstr.h BehaviorCollection.h
test_ccusim.obj:	ctitime.h dlldefs.h ctidate.h logger.h thread.h \
		mutex.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h CCU711.h ccu710.h \
		portlogger.h emetconwords.h PlcTransmitter.h CommInterface.h \
		ctinexus.h netports.h cticonnect.h yukon.h precompiled.h \
		ctidbgmem.h BehaviorCollection.h CommsBehavior.h CcuIDLC.h \
		Ccu721.h Mct410.h
test_delay_behavior.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h numstr.h BehaviorCollection.h DelayBehavior.h \
		CommsBehavior.h logger.h thread.h mutex.h guard.h \
		CtiPCPtrQueue.h
test_mct410_sim.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h numstr.h Mct410.h EmetconWords.h
#ENDUPDATE#

