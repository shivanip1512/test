# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(CAPCONTROL)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(SERVICE)\include \
-I$(SERVER)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(PROT)\include \
-I$(THRIFT_INCLUDE) \
-I$(MSG)\Serialization



.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CAPCONTROL)\include \
;$(DATABASE)\include \
;$(SERVICE)\include \
;$(RTDB)\include \
;$(SERVER)\include \
;$(MSG)\include \
;$(DEVICECONFIGURATION)\include \
;$(PROT)\include \



LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\cticonfig.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctithriftmsg.lib \
$(THRIFT_LIB)


BASEOBJS= \
$(PRECOMPILED_OBJ) \
cc_message_serialization.obj \
capcontroller.obj \
cccapbank.obj \
ccclientconn.obj \
ccclientlistener.obj \
ccexecutor.obj \
ccfeeder.obj \
ccmain.obj \
ccservice.obj \
ccstate.obj \
ccsubstationbus.obj \
ccsubstationbusstore.obj \
pao_schedule.obj \
pao_event.obj \
mgr_paosched.obj \
ccmonitorpoint.obj \
cctwowaycbcpoints.obj \
PointValueHolder.obj \
ccareabase.obj \
ccarea.obj \
ccsparea.obj \
ccsubstation.obj \
ccoperationstats.obj \
ccconfirmationstats.obj \
ccstatsobject.obj \
ccoriginalparent.obj \
CapControlPao.obj \
ControlStrategy.obj \
Controllable.obj \
IVVCAlgorithm.obj \
IVVCState.obj \
IVVCStrategy.obj \
KVarStrategy.obj \
MultiVoltStrategy.obj \
MultiVoltVarStrategy.obj \
NoStrategy.obj \
PFactorKWKVarStrategy.obj \
PFactorKWKQStrategy.obj \
TimeOfDayStrategy.obj \
VoltStrategy.obj \
StrategyLoader.obj \
StrategyManager.obj \
Zone.obj \
ZoneLoader.obj \
ZoneManager.obj \
VoltageRegulator.obj \
VoltageRegulatorLoader.obj \
VoltageRegulatorManager.obj \
CapControlPointDatahandler.obj \
ccutil.obj \
CapControlDispatchConnection.obj \
CapControlCParms.obj \
PointResponseDatabaseDao.obj \
PointResponse.obj \
PointResponseManager.obj \
DynamicCommand.obj \
DynamicCommandExecutor.obj \
MsgCapControlCommand.obj \
MsgCapControlMessage.obj \
MsgItemCommand.obj \
MsgVerifyInactiveBanks.obj \
MsgVerifySelectedBank.obj \
MsgChangeOpState.obj \
MsgObjectMove.obj \
MsgBankMove.obj \
MsgSubstationBus.obj \
MsgCapBankStates.obj \
MsgAreas.obj \
MsgSpecialAreas.obj \
MsgSubstations.obj \
MsgVoltageRegulator.obj \
ExecChangeOpState.obj \
ExecVerification.obj \
MsgVerifyBanks.obj \
MsgDeleteItem.obj \
MsgSystemStatus.obj \
MsgCapControlServerResponse.obj \
MsgCapControlShutdown.obj \
ExecutorFactory.obj \
Policy.obj \
StandardControlPolicy.obj \
KeepAlivePolicy.obj \
NoKeepAlivePolicy.obj \
CountdownKeepAlivePolicy.obj \
IncrementingKeepAlivePolicy.obj \
ScanPolicy.obj \
LoadOnlyScanPolicy.obj \
StandardScanPolicy.obj \
RegulatorEvents.obj \
LastControlReason.obj \
IgnoredControlReason.obj \
DynamicData.obj



TARGS = capcontrol.exe


CAPCONTROL_FULLBUILD = $[Filename,$(OBJ),CapControlFullBuild,target]

PROGS_VERSION=\
$(TARGS)

ALL:          $(TARGS)


$(CAPCONTROL_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(CC) $(CFLAGS) $(CCOPTS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(BASEOBJS)]



capcontrol.exe: $(CAPCONTROL_FULLBUILD) $(BASEOBJS) Makefile $(OBJ)\capcontrol.res
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$@ $(BASEOBJS) -link /LARGEADDRESSAWARE $(LIBS) $(BOOST_LIBS) capcontrol.res
              @echo:
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
	      -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
              -if exist ..\$(@B).pdb copy ..\$(@B).pdb $(YUKONDEBUG)
              @%cd $(CWD)
              @echo Done building Target ..\$@
              @echo:

copy:       $(TARGS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)
	   -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
           -if exist bin\*.pdb copy bin\*.pdb $(YUKONDEBUG)

deps:
                scandeps -Output makeexe.mak *.cpp


clean:
        -del \
*.pdb \
$(OBJ)\*.obj \
$(OBJ)\*.res \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.exe


allclean:   clean all


.SUFFIXES:      .cpp .obj

.cpp.obj:
               @echo:
               @echo Compiling: $<
               @echo C-Options: $(CFLAGS)
               @echo Output   : ..\$@
               @echo:
               $(CC) $(CFLAGS) $(CCOPTS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
#UPDATE#
capcontrolcparms.obj:	precompiled.h ccid.h CParms.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h
capcontroldispatchconnection.obj:	precompiled.h \
		CapControlDispatchConnection.h DispatchConnection.h \
		connection_client.h connection.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h loggable.h \
		connectionHandle.h msg_multi.h msg_pdata.h yukon.h types.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h MessageListener.h ccsubstationbusstore.h \
		ccarea.h ccAreaBase.h Controllable.h CapControlPao.h \
		cctypes.h ccOperationStats.h ccConfirmationStats.h \
		StrategyManager.h ControlStrategy.h DynamicData.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		ccsubstationbus.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		mgr_paosched.h amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h dbaccess.h dllbase.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h
capcontroller.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h connection_client.h connection.h message.h \
		ctitime.h collectable.h loggable.h connectionHandle.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h amq_constants.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		CapControlPao.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h ccarea.h ccAreaBase.h Controllable.h \
		StrategyManager.h ControlStrategy.h DynamicData.h ccsparea.h \
		ccstate.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		ccsubstationbus.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		mgr_paosched.h amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ccid.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		msg_cmd.h msg_tag.h msg_pcreturn.h msg_dbchg.h \
		MsgVerifyBanks.h capcontroller.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h \
		ccsubstationbusstore.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h resolvers.h db_entry_defines.h \
		thread_monitor.h smartmap.h thread_register_data.h \
		boost_time.h ThreadStatusKeeper.h ExecutorFactory.h \
		dllyukon.h ccclientconn.h connection_server.h \
		connection_listener.h ccclientlistener.h millisecond_timer.h \
		mgr_config.h config_device.h win_helper.h
capcontrolpao.obj:	precompiled.h CapControlPao.h yukon.h types.h \
		ctidbgmem.h cctypes.h ccOperationStats.h message.h ctitime.h \
		dlldefs.h collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h capcontroller.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h critical_section.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		StrategyManager.h ControlStrategy.h DynamicData.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		ccsubstationbus.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		mgr_paosched.h amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h
capcontrolpointdatahandler.obj:	precompiled.h \
		CapControlPointDataHandler.h yukon.h types.h ctidbgmem.h \
		PointDataHandler.h PointDataListener.h msg_pdata.h dlldefs.h \
		pointdefs.h pointtypes.h message.h ctitime.h collectable.h \
		loggable.h connectionHandle.h MessageListener.h \
		DispatchConnection.h connection_client.h connection.h \
		msg_multi.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h capcontroller.h dbaccess.h dllbase.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h StrategyManager.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h
ccarea.obj:	precompiled.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h yukon.h types.h ctidbgmem.h cctypes.h \
		ccOperationStats.h message.h ctitime.h dlldefs.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h ControlStrategy.h DynamicData.h ccid.h \
		database_util.h database_writer.h database_connection.h \
		dbaccess.h dllbase.h row_writer.h ccsubstationbusstore.h \
		ccsparea.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		ccsubstationbus.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		timing_util.h immutable.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h
ccareabase.obj:	precompiled.h ccareabase.h Controllable.h \
		CapControlPao.h yukon.h types.h ctidbgmem.h cctypes.h \
		ccOperationStats.h message.h ctitime.h dlldefs.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h ControlStrategy.h DynamicData.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h ccid.h \
		row_reader.h ccsubstationbusstore.h ccarea.h ccsparea.h \
		ccstate.h ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h cctwowaycbcpoints.h PointValueHolder.h \
		pointtypes.h msg_pdata.h pointdefs.h LitePoint.h \
		LastControlReason.h IgnoredControlReason.h ctidate.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h database_writer.h row_writer.h
cccapbank.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		cccapbank.h ccmonitorpoint.h ctitime.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h message.h \
		collectable.h loggable.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h guard.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ccoriginalparent.h cctypes.h ctidate.h CapControlPao.h \
		ccOperationStats.h ccConfirmationStats.h PointResponse.h \
		PointResponseManager.h Exceptions.h ccid.h resolvers.h \
		db_entry_defines.h database_reader.h database_connection.h \
		row_reader.h database_writer.h row_writer.h database_util.h \
		PointResponseDao.h DatabaseDaoFactory.h \
		PointResponseDatabaseDao.h
ccclientconn.obj:	precompiled.h capcontroller.h dbaccess.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h connection_client.h connection.h message.h \
		ctitime.h collectable.h loggable.h connectionHandle.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h StrategyManager.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h ccclientconn.h connection_server.h \
		connection_listener.h
ccclientlistener.obj:	precompiled.h ccclientlistener.h ccclientconn.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h connection_server.h \
		connection.h msg_multi.h msg_pdata.h yukon.h types.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h connection_listener.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h CapControlPao.h \
		cctypes.h ccOperationStats.h ccConfirmationStats.h ccarea.h \
		ccAreaBase.h Controllable.h StrategyManager.h \
		ControlStrategy.h DynamicData.h ccsparea.h ccstate.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ccid.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		dllbase.h ThreadStatusKeeper.h thread_register_data.h \
		boost_time.h thread_monitor.h smartmap.h amq_constants.h
ccconfirmationstats.obj:	precompiled.h ccconfirmationstats.h \
		ccoperationstats.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h loggable.h connectionHandle.h pointtypes.h \
		pointdefs.h msg_pdata.h yukon.h types.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h
ccexecutor.obj:	precompiled.h AttributeService.h LitePoint.h dlldefs.h \
		pointtypes.h PointAttribute.h yukon.h types.h ctidbgmem.h \
		ccclientlistener.h ccclientconn.h message.h ctitime.h \
		collectable.h loggable.h connectionHandle.h \
		connection_server.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h connection_listener.h ccexecutor.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		CapControlPao.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h ccarea.h ccAreaBase.h Controllable.h \
		StrategyManager.h ControlStrategy.h DynamicData.h ccsparea.h \
		ccstate.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		ccsubstationbus.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccid.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		VoltageRegulatorManager.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		ccsubstationbusstore.h ccstatsobject.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h row_reader.h database_writer.h row_writer.h \
		capcontroller.h connection_client.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ctdpcptrq.h CtiPCPtrQueue.h ExecutorFactory.h \
		MsgChangeOpState.h ExecChangeOpState.h
ccfeeder.obj:	precompiled.h ccfeeder.h Controllable.h CapControlPao.h \
		yukon.h types.h ctidbgmem.h cctypes.h ccOperationStats.h \
		message.h ctitime.h dlldefs.h collectable.h loggable.h \
		connectionHandle.h ccConfirmationStats.h StrategyManager.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		ccOriginalParent.h regression.h cccapbank.h ccmonitorpoint.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		dsm2.h streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h ccid.h database_util.h database_writer.h \
		database_connection.h dbaccess.h dllbase.h row_writer.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h DynamicData.h \
		ccsparea.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		ccsubstationbus.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		capcontroller.h connection_client.h connection.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h worker_thread.h \
		concurrentSet.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccexecutor.h ctdpcptrq.h CtiPCPtrQueue.h \
		msg_lmcontrolhistory.h tbl_pt_alarm.h dbmemobject.h \
		resolvers.h db_entry_defines.h
ccmain.obj:	precompiled.h ccsubstationbusstore.h ccarea.h ccAreaBase.h \
		Controllable.h CapControlPao.h yukon.h types.h ctidbgmem.h \
		cctypes.h ccOperationStats.h message.h ctitime.h dlldefs.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h ControlStrategy.h DynamicData.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h timing_util.h \
		immutable.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h row_reader.h database_writer.h row_writer.h \
		capcontroller.h connection_client.h connection.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h worker_thread.h \
		concurrentSet.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccexecutor.h ctdpcptrq.h CtiPCPtrQueue.h ccservice.h \
		cservice.h precomp.h Monitor.h CServiceConfig.h rtdb.h \
		logManager.h
ccmonitorpoint.obj:	precompiled.h ccutil.h pointattribute.h yukon.h \
		types.h ctidbgmem.h dlldefs.h devicetypes.h msg_pcrequest.h \
		message.h ctitime.h collectable.h loggable.h \
		connectionHandle.h dsm2.h streamConnection.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h ccmonitorpoint.h ccid.h \
		database_util.h database_writer.h database_connection.h \
		dbaccess.h dllbase.h row_writer.h row_reader.h
ccoperationstats.obj:	precompiled.h ccoperationstats.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h collectable.h loggable.h \
		connectionHandle.h pointtypes.h pointdefs.h msg_pdata.h \
		yukon.h types.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h ccid.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h database_util.h \
		database_writer.h database_connection.h dbaccess.h dllbase.h \
		row_writer.h row_reader.h
ccoriginalparent.obj:	precompiled.h ccoriginalparent.h ccid.h ccutil.h \
		pointattribute.h yukon.h types.h ctidbgmem.h dlldefs.h \
		devicetypes.h msg_pcrequest.h message.h ctitime.h \
		collectable.h loggable.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h database_util.h \
		database_writer.h database_connection.h dbaccess.h dllbase.h \
		row_writer.h row_reader.h
ccservice.obj:	precompiled.h id_capcontrol.h module_util.h dlldefs.h \
		ctitime.h version.h ccservice.h cservice.h dllBase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h ccsubstationbus.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h message.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		guard.h utility.h queues.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		ControlStrategy.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		timing_util.h immutable.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		logManager.h thread_monitor.h smartmap.h cparms.h queue.h \
		thread_register_data.h boost_time.h ExecutorFactory.h \
		ccexecutor.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h ccarea.h ccAreaBase.h DynamicData.h ccsparea.h \
		ccstate.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ccid.h ControlPolicy.h \
		Policy.h msg_signal.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		msg_multi.h VoltageRegulatorManager.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccclientlistener.h \
		ccclientconn.h connection_server.h connection.h msg_ptreg.h \
		msg_reg.h worker_thread.h concurrentSet.h \
		connection_listener.h capcontroller.h dbaccess.h \
		connection_client.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccstatsobject.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ctdpcptrq.h CtiPCPtrQueue.h
ccsparea.obj:	precompiled.h ccsparea.h ccAreaBase.h Controllable.h \
		CapControlPao.h yukon.h types.h ctidbgmem.h cctypes.h \
		ccOperationStats.h message.h ctitime.h dlldefs.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h ControlStrategy.h DynamicData.h ccid.h \
		database_util.h database_writer.h database_connection.h \
		dbaccess.h dllbase.h row_writer.h ccsubstationbusstore.h \
		ccarea.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		ccsubstationbus.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		timing_util.h immutable.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h
ccstate.obj:	precompiled.h ccstate.h collectable.h ccid.h logger.h \
		dlldefs.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h critical_section.h \
		row_reader.h
ccstatsobject.obj:	precompiled.h ccstatsobject.h
ccsubstation.obj:	precompiled.h ccsubstation.h CapControlPao.h yukon.h \
		types.h ctidbgmem.h cctypes.h ccOperationStats.h message.h \
		ctitime.h dlldefs.h collectable.h loggable.h \
		connectionHandle.h ccConfirmationStats.h ccid.h \
		database_util.h database_writer.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h critical_section.h \
		guard.h utility.h queues.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h row_writer.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		StrategyManager.h readers_writer_lock.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h MsgItemCommand.h EventTypes.h \
		MsgBankMove.h MsgObjectMove.h MsgCapControlMessage.h \
		MsgSubstationBus.h ccsubstationbus.h regression.h ccfeeder.h \
		ccOriginalParent.h cccapbank.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		MsgVerifyBanks.h
ccsubstationbus.obj:	precompiled.h ccsubstationbus.h Controllable.h \
		CapControlPao.h yukon.h types.h ctidbgmem.h cctypes.h \
		ccOperationStats.h message.h ctitime.h dlldefs.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h ControlStrategy.h regression.h ccfeeder.h \
		ccOriginalParent.h cccapbank.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h ccid.h database_util.h \
		database_writer.h database_connection.h dbaccess.h dllbase.h \
		row_writer.h ccsubstationbusstore.h ccarea.h ccAreaBase.h \
		DynamicData.h ccsparea.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		capcontroller.h connection_client.h connection.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h worker_thread.h \
		concurrentSet.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccexecutor.h ctdpcptrq.h CtiPCPtrQueue.h tbl_pt_alarm.h \
		dbmemobject.h resolvers.h db_entry_defines.h MsgVerifyBanks.h
ccsubstationbusstore.obj:	precompiled.h ccsubstationbusstore.h \
		ccarea.h ccAreaBase.h Controllable.h CapControlPao.h yukon.h \
		types.h ctidbgmem.h cctypes.h ccOperationStats.h message.h \
		ctitime.h dlldefs.h collectable.h loggable.h \
		connectionHandle.h ccConfirmationStats.h StrategyManager.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h timing_util.h \
		immutable.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h row_reader.h database_writer.h row_writer.h \
		resolvers.h db_entry_defines.h msg_dbchg.h capcontroller.h \
		connection_client.h connection.h msg_multi.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h worker_thread.h concurrentSet.h \
		DispatchConnection.h CapControlDispatchConnection.h msg_cmd.h \
		ccexecutor.h ctdpcptrq.h CtiPCPtrQueue.h mgr_holiday.h \
		thread_monitor.h smartmap.h thread_register_data.h \
		boost_time.h database_transaction.h database_util.h \
		ThreadStatusKeeper.h ExecutorFactory.h mgr_config.h \
		config_device.h std_helper.h
cctwowaycbcpoints.obj:	precompiled.h cctwowaycbcpoints.h types.h \
		PointAttribute.h yukon.h ctidbgmem.h dlldefs.h \
		PointValueHolder.h pointtypes.h ctitime.h msg_pdata.h \
		pointdefs.h message.h collectable.h loggable.h \
		connectionHandle.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ccid.h database_util.h \
		database_writer.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h critical_section.h guard.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h row_writer.h ccutil.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h timing_util.h \
		immutable.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h std_helper.h row_reader.h
ccutil.obj:	precompiled.h ccutil.h pointattribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h devicetypes.h msg_pcrequest.h message.h \
		ctitime.h collectable.h loggable.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h pointdefs.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		ControlStrategy.h DynamicData.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h cctwowaycbcpoints.h PointValueHolder.h \
		pointtypes.h msg_pdata.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h row_reader.h database_writer.h row_writer.h
cc_message_serialization.obj:	precompiled.h cc_message_serialization.h \
		CapControlPao.h yukon.h types.h ctidbgmem.h cctypes.h \
		ccOperationStats.h message.h ctitime.h dlldefs.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h ccarea.h ccAreaBase.h Controllable.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h \
		ControlStrategy.h DynamicData.h cccapbank.h ccmonitorpoint.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		dsm2.h streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ccoriginalparent.h ctidate.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		ccfeeder.h regression.h EventLogEntry.h sorted_vector.h \
		ccsparea.h ccstate.h ccsubstation.h ccsubstationbus.h \
		TimeOfDayStrategy.h DynamicCommand.h MsgCapControlCommand.h \
		GangOperatedVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ccid.h EventTypes.h \
		ControlPolicy.h Policy.h msg_signal.h KeepAlivePolicy.h \
		ScanPolicy.h RegulatorEvents.h MsgAreas.h \
		MsgCapControlMessage.h MsgBankMove.h MsgItemCommand.h \
		MsgCapBankStates.h MsgCapControlServerResponse.h \
		MsgCapControlShutdown.h MsgChangeOpState.h MsgDeleteItem.h \
		MsgObjectMove.h MsgSpecialAreas.h MsgSubStationBus.h \
		MsgSubStations.h MsgSystemStatus.h MsgVerifyBanks.h \
		MsgVerifyInactiveBanks.h MsgVerifySelectedBank.h \
		MsgVoltageRegulator.h PhaseOperatedVoltageRegulator.h
cc_server_client_serialization_test.obj:	precompiled.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h collectable.h loggable.h \
		connectionHandle.h msg_cmd.h msg_dbchg.h yukon.h types.h \
		msg_lmcontrolhistory.h pointdefs.h msg_multi.h msg_pdata.h \
		pointtypes.h msg_notif_alarm.h msg_notif_email.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h dllbase.h \
		msg_notif_lmcontrol.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_pcreturn.h \
		msg_ptreg.h msg_queuedata.h Msg_reg.h msg_requestcancel.h \
		msg_server_req.h msg_server_resp.h msg_signal.h msg_tag.h \
		msg_trace.h connection_server.h connection.h queue.h cparms.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		concurrentSet.h connection_listener.h CapControlPao.h \
		cctypes.h ccOperationStats.h ccConfirmationStats.h ccarea.h \
		ccAreaBase.h Controllable.h StrategyManager.h \
		ControlStrategy.h DynamicData.h cccapbank.h ccmonitorpoint.h \
		ccutil.h pointattribute.h devicetypes.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ccoriginalparent.h ctidate.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		ccfeeder.h regression.h EventLogEntry.h sorted_vector.h \
		ccsparea.h ccstate.h ccsubstation.h ccsubstationbus.h \
		TimeOfDayStrategy.h DynamicCommand.h MsgCapControlCommand.h \
		GangOperatedVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ccid.h EventTypes.h \
		ControlPolicy.h Policy.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h MsgAreas.h MsgCapControlMessage.h \
		MsgBankMove.h MsgItemCommand.h MsgCapBankStates.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		MsgChangeOpState.h MsgDeleteItem.h MsgObjectMove.h \
		MsgSpecialAreas.h MsgSubStationBus.h MsgSubStations.h \
		MsgSystemStatus.h MsgVerifyBanks.h MsgVerifyInactiveBanks.h \
		MsgVerifySelectedBank.h MsgVoltageRegulator.h \
		PhaseOperatedVoltageRegulator.h test_cc_serialization.h \
		strategyLoader.h test_serialization.h \
		test_serialization_helper.h logManager.h
controllable.obj:	precompiled.h ControlStrategy.h NoStrategy.h \
		Controllable.h CapControlPao.h yukon.h types.h ctidbgmem.h \
		cctypes.h ccOperationStats.h message.h ctitime.h dlldefs.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h database_reader.h database_connection.h \
		dbaccess.h dllbase.h row_reader.h
controlstrategy.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h ControlStrategy.h ccid.h \
		mgr_holiday.h ctidate.h mutex.h guard.h
countdownkeepalivepolicy.obj:	precompiled.h CountdownKeepAlivePolicy.h \
		yukon.h types.h ctidbgmem.h KeepAlivePolicy.h Policy.h \
		AttributeService.h LitePoint.h dlldefs.h pointtypes.h \
		PointAttribute.h PointValueHolder.h ctitime.h msg_pdata.h \
		pointdefs.h message.h collectable.h loggable.h \
		connectionHandle.h msg_signal.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
dynamiccommand.obj:	precompiled.h DynamicCommand.h \
		MsgCapControlCommand.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccid.h
dynamiccommandexecutor.obj:	precompiled.h DynamicCommandExecutor.h \
		DynamicCommand.h MsgCapControlCommand.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h loggable.h \
		connectionHandle.h ccexecutor.h ccmessage.h ccsubstation.h \
		CapControlPao.h yukon.h types.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h ccarea.h ccAreaBase.h Controllable.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h \
		ControlStrategy.h DynamicData.h ccsparea.h ccstate.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h timing_util.h \
		immutable.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ccid.h ControlPolicy.h \
		Policy.h msg_signal.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		msg_multi.h VoltageRegulatorManager.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccsubstationbusstore.h \
		ccstatsobject.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h dbaccess.h dllbase.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ExecutorFactory.h
dynamicdata.obj:	precompiled.h DynamicData.h database_reader.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h row_reader.h
execchangeopstate.obj:	precompiled.h ExecChangeOpState.h \
		MsgChangeOpState.h MsgItemCommand.h MsgCapControlCommand.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h ccexecutor.h ccmessage.h \
		ccsubstation.h CapControlPao.h yukon.h types.h cctypes.h \
		ccOperationStats.h ccConfirmationStats.h ccarea.h \
		ccAreaBase.h Controllable.h StrategyManager.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccstate.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		ccsubstationbus.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		timing_util.h immutable.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ccid.h ControlPolicy.h \
		Policy.h msg_signal.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		msg_multi.h VoltageRegulatorManager.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h capcontroller.h \
		dbaccess.h dllbase.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h worker_thread.h \
		concurrentSet.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccstatsobject.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ctdpcptrq.h CtiPCPtrQueue.h
executorfactory.obj:	precompiled.h DynamicCommandExecutor.h \
		DynamicCommand.h MsgCapControlCommand.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h loggable.h \
		connectionHandle.h ccexecutor.h ccmessage.h ccsubstation.h \
		CapControlPao.h yukon.h types.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h ccarea.h ccAreaBase.h Controllable.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h \
		ControlStrategy.h DynamicData.h ccsparea.h ccstate.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h timing_util.h \
		immutable.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ccid.h ControlPolicy.h \
		Policy.h msg_signal.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		msg_multi.h VoltageRegulatorManager.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ExecutorFactory.h \
		ExecVerification.h MsgVerifyBanks.h MsgVerifyInactiveBanks.h \
		MsgVerifySelectedBank.h ExecChangeOpState.h \
		MsgChangeOpState.h
execverification.obj:	precompiled.h ExecVerification.h \
		MsgVerifyBanks.h MsgItemCommand.h MsgCapControlCommand.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h MsgVerifyInactiveBanks.h \
		MsgVerifySelectedBank.h ccexecutor.h ccmessage.h \
		ccsubstation.h CapControlPao.h yukon.h types.h cctypes.h \
		ccOperationStats.h ccConfirmationStats.h ccarea.h \
		ccAreaBase.h Controllable.h StrategyManager.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccstate.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		ccsubstationbus.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		timing_util.h immutable.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ccid.h ControlPolicy.h \
		Policy.h msg_signal.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		msg_multi.h VoltageRegulatorManager.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h capcontroller.h \
		dbaccess.h dllbase.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h worker_thread.h \
		concurrentSet.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccstatsobject.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ctdpcptrq.h CtiPCPtrQueue.h \
		ExecutorFactory.h
ignoredcontrolreason.obj:	precompiled.h IgnoredControlReason.h \
		std_helper.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h streamBuffer.h loggable.h \
		dllyukon.h cctwowaycbcpoints.h PointAttribute.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		message.h collectable.h connectionHandle.h LitePoint.h \
		LastControlReason.h
incrementingkeepalivepolicy.obj:	precompiled.h \
		IncrementingKeepAlivePolicy.h yukon.h types.h ctidbgmem.h \
		KeepAlivePolicy.h Policy.h AttributeService.h LitePoint.h \
		dlldefs.h pointtypes.h PointAttribute.h PointValueHolder.h \
		ctitime.h msg_pdata.h pointdefs.h message.h collectable.h \
		loggable.h connectionHandle.h msg_signal.h msg_pcrequest.h \
		dsm2.h streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
ivvcalgorithm.obj:	precompiled.h IVVCAlgorithm.h ccsubstationbus.h \
		Controllable.h CapControlPao.h yukon.h types.h ctidbgmem.h \
		cctypes.h ccOperationStats.h message.h ctitime.h dlldefs.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h ControlStrategy.h regression.h ccfeeder.h \
		ccOriginalParent.h cccapbank.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h IVVCState.h \
		PointDataRequest.h PointDataRequestFactory.h \
		DispatchConnection.h connection_client.h connection.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		worker_thread.h concurrentSet.h MessageListener.h \
		ZoneManager.h Zone.h VoltageRegulatorManager.h \
		VoltageRegulator.h UpdatablePao.h AttributeService.h ccid.h \
		EventTypes.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h ccarea.h ccAreaBase.h DynamicData.h ccsparea.h \
		ccstate.h MsgItemCommand.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		IVVCStrategy.h capcontroller.h dbaccess.h dllbase.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccstatsobject.h StrategyLoader.h \
		ZoneLoader.h VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h ExecutorFactory.h MsgVerifyBanks.h \
		IVVCAnalysisMessage.h std_helper.h
ivvcstate.obj:	precompiled.h IVVCState.h yukon.h types.h ctidbgmem.h \
		ctitime.h dlldefs.h cccapbank.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h message.h \
		collectable.h loggable.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ccoriginalparent.h cctypes.h ctidate.h CapControlPao.h \
		ccOperationStats.h ccConfirmationStats.h PointResponse.h \
		PointResponseManager.h Exceptions.h PointDataRequest.h
ivvcstrategy.obj:	precompiled.h IVVCStrategy.h IVVCAlgorithm.h \
		ccsubstationbus.h Controllable.h CapControlPao.h yukon.h \
		types.h ctidbgmem.h cctypes.h ccOperationStats.h message.h \
		ctitime.h dlldefs.h collectable.h loggable.h \
		connectionHandle.h ccConfirmationStats.h StrategyManager.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h regression.h \
		ccfeeder.h ccOriginalParent.h cccapbank.h ccmonitorpoint.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		dsm2.h streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h IVVCState.h \
		PointDataRequest.h PointDataRequestFactory.h \
		DispatchConnection.h connection_client.h connection.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		worker_thread.h concurrentSet.h MessageListener.h \
		ZoneManager.h Zone.h VoltageRegulatorManager.h \
		VoltageRegulator.h UpdatablePao.h AttributeService.h ccid.h \
		EventTypes.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h ccarea.h ccAreaBase.h DynamicData.h ccsparea.h \
		ccstate.h MsgItemCommand.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccsubstationbusstore.h ccstatsobject.h StrategyLoader.h \
		ZoneLoader.h VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h dbaccess.h dllbase.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h
keepalivepolicy.obj:	precompiled.h KeepAlivePolicy.h Policy.h yukon.h \
		types.h ctidbgmem.h AttributeService.h LitePoint.h dlldefs.h \
		pointtypes.h PointAttribute.h PointValueHolder.h ctitime.h \
		msg_pdata.h pointdefs.h message.h collectable.h loggable.h \
		connectionHandle.h msg_signal.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
kvarstrategy.obj:	precompiled.h KVarStrategy.h ControlStrategy.h
lastcontrolreason.obj:	precompiled.h LastControlReason.h std_helper.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h streamBuffer.h loggable.h dllyukon.h \
		cctwowaycbcpoints.h PointAttribute.h PointValueHolder.h \
		pointtypes.h msg_pdata.h pointdefs.h message.h collectable.h \
		connectionHandle.h LitePoint.h IgnoredControlReason.h
loadonlyscanpolicy.obj:	precompiled.h LoadOnlyScanPolicy.h yukon.h \
		types.h ctidbgmem.h ScanPolicy.h Policy.h AttributeService.h \
		LitePoint.h dlldefs.h pointtypes.h PointAttribute.h \
		PointValueHolder.h ctitime.h msg_pdata.h pointdefs.h \
		message.h collectable.h loggable.h connectionHandle.h \
		msg_signal.h msg_pcrequest.h dsm2.h streamConnection.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
mgr_paosched.obj:	precompiled.h mgr_paosched.h critical_section.h \
		dlldefs.h pao_schedule.h dbmemobject.h ctitime.h pao_event.h \
		capcontroller.h dbaccess.h dllbase.h os2_2w32.h types.h \
		cticalls.h yukon.h ctidbgmem.h connection_client.h \
		connection.h message.h collectable.h loggable.h \
		connectionHandle.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h StrategyManager.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h amq_connection.h \
		thread.h StreamableMessage.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h LitePoint.h \
		LastControlReason.h IgnoredControlReason.h ctidate.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h ctitokenizer.h database_util.h \
		ThreadStatusKeeper.h thread_register_data.h boost_time.h \
		thread_monitor.h smartmap.h ExecutorFactory.h \
		MsgVerifyBanks.h MsgVerifyInactiveBanks.h
msgareas.obj:	precompiled.h MsgAreas.h MsgCapControlMessage.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h ccarea.h ccAreaBase.h \
		Controllable.h CapControlPao.h yukon.h types.h cctypes.h \
		ccOperationStats.h ccConfirmationStats.h StrategyManager.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		DynamicData.h ccid.h
msgbankmove.obj:	precompiled.h MsgBankMove.h MsgItemCommand.h \
		MsgCapControlCommand.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccid.h
msgcapbankstates.obj:	precompiled.h MsgCapBankStates.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccstate.h utility.h queues.h cticalls.h yukon.h types.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		ccid.h
msgcapcontrolcommand.obj:	precompiled.h MsgCapControlCommand.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h ccid.h
msgcapcontrolmessage.obj:	precompiled.h MsgCapControlMessage.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h
msgcapcontrolserverresponse.obj:	precompiled.h \
		MsgCapControlServerResponse.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccid.h
msgcapcontrolshutdown.obj:	precompiled.h MsgCapControlShutdown.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h ccid.h
msgchangeopstate.obj:	precompiled.h MsgChangeOpState.h \
		MsgItemCommand.h MsgCapControlCommand.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h loggable.h \
		connectionHandle.h ccid.h
msgdeleteitem.obj:	precompiled.h MsgDeleteItem.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccid.h
msgitemcommand.obj:	precompiled.h MsgItemCommand.h \
		MsgCapControlCommand.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccid.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h types.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h
msgobjectmove.obj:	precompiled.h MsgObjectMove.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccid.h
msgspecialareas.obj:	precompiled.h MsgSpecialAreas.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccsparea.h ccAreaBase.h Controllable.h CapControlPao.h \
		yukon.h types.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h ControlStrategy.h DynamicData.h ccid.h
msgsubstationbus.obj:	precompiled.h MsgSubstationBus.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccsubstationbus.h Controllable.h CapControlPao.h yukon.h \
		types.h cctypes.h ccOperationStats.h ccConfirmationStats.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h \
		ControlStrategy.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		timing_util.h immutable.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h ccid.h
msgsubstations.obj:	precompiled.h MsgSubstations.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccsubstation.h CapControlPao.h yukon.h types.h cctypes.h \
		ccOperationStats.h ccConfirmationStats.h ccid.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h
msgsystemstatus.obj:	precompiled.h MsgSystemStatus.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccid.h
msgverifybanks.obj:	precompiled.h MsgVerifyBanks.h MsgItemCommand.h \
		MsgCapControlCommand.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		ccid.h
msgverifyinactivebanks.obj:	precompiled.h MsgVerifyInactiveBanks.h \
		MsgVerifyBanks.h MsgItemCommand.h MsgCapControlCommand.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h ccid.h
msgverifyselectedbank.obj:	precompiled.h MsgVerifySelectedBank.h \
		MsgVerifyBanks.h MsgItemCommand.h MsgCapControlCommand.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h ccid.h
msgvoltageregulator.obj:	precompiled.h MsgVoltageRegulator.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		VoltageRegulator.h yukon.h types.h CapControlPao.h cctypes.h \
		ccOperationStats.h ccConfirmationStats.h UpdatablePao.h \
		msg_pdata.h pointdefs.h pointtypes.h LitePoint.h \
		PointValueHolder.h AttributeService.h PointAttribute.h ccid.h \
		ccutil.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h EventTypes.h ControlPolicy.h \
		Policy.h msg_signal.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h
multivoltstrategy.obj:	precompiled.h MultiVoltStrategy.h \
		ControlStrategy.h
multivoltvarstrategy.obj:	precompiled.h MultiVoltVarStrategy.h \
		ControlStrategy.h
nokeepalivepolicy.obj:	precompiled.h NoKeepAlivePolicy.h yukon.h \
		types.h ctidbgmem.h KeepAlivePolicy.h Policy.h \
		AttributeService.h LitePoint.h dlldefs.h pointtypes.h \
		PointAttribute.h PointValueHolder.h ctitime.h msg_pdata.h \
		pointdefs.h message.h collectable.h loggable.h \
		connectionHandle.h msg_signal.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
nostrategy.obj:	precompiled.h NoStrategy.h ControlStrategy.h
pao_event.obj:	precompiled.h pao_event.h dbmemobject.h row_reader.h \
		ctitime.h dlldefs.h ccutil.h pointattribute.h yukon.h types.h \
		ctidbgmem.h devicetypes.h msg_pcrequest.h message.h \
		collectable.h loggable.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h
pao_schedule.obj:	precompiled.h pao_schedule.h dbmemobject.h ctitime.h \
		dlldefs.h row_reader.h ccutil.h pointattribute.h yukon.h \
		types.h ctidbgmem.h devicetypes.h msg_pcrequest.h message.h \
		collectable.h loggable.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h
pfactorkwkqstrategy.obj:	precompiled.h PFactorKWKQStrategy.h \
		ControlStrategy.h
pfactorkwkvarstrategy.obj:	precompiled.h PFactorKWKVarStrategy.h \
		ControlStrategy.h
pointresponse.obj:	precompiled.h PointResponse.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h ccid.h
pointresponsedatabasedao.obj:	precompiled.h PointResponseDatabaseDao.h \
		PointResponseDao.h PointResponse.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h guard.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h database_reader.h row_reader.h \
		database_writer.h row_writer.h database_util.h ccid.h
pointresponsemanager.obj:	precompiled.h PointResponseManager.h \
		Exceptions.h yukon.h types.h ctidbgmem.h PointResponse.h \
		ccmonitorpoint.h ctitime.h dlldefs.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h message.h \
		collectable.h loggable.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h
pointvalueholder.obj:	precompiled.h PointValueHolder.h yukon.h types.h \
		ctidbgmem.h pointtypes.h ctitime.h dlldefs.h msg_pdata.h \
		pointdefs.h message.h collectable.h loggable.h \
		connectionHandle.h
policy.obj:	precompiled.h Policy.h yukon.h types.h ctidbgmem.h \
		AttributeService.h LitePoint.h dlldefs.h pointtypes.h \
		PointAttribute.h PointValueHolder.h ctitime.h msg_pdata.h \
		pointdefs.h message.h collectable.h loggable.h \
		connectionHandle.h msg_signal.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h std_helper.h ccutil.h devicetypes.h \
		mgr_paosched.h amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h
regulatorevents.obj:	precompiled.h RegulatorEvents.h yukon.h types.h \
		ctidbgmem.h ccutil.h pointattribute.h dlldefs.h devicetypes.h \
		msg_pcrequest.h message.h ctitime.h collectable.h loggable.h \
		connectionHandle.h dsm2.h streamConnection.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h std_helper.h queue.h cparms.h \
		database_reader.h database_connection.h dbaccess.h dllbase.h \
		row_reader.h database_writer.h row_writer.h ccid.h
scanpolicy.obj:	precompiled.h ScanPolicy.h Policy.h yukon.h types.h \
		ctidbgmem.h AttributeService.h LitePoint.h dlldefs.h \
		pointtypes.h PointAttribute.h PointValueHolder.h ctitime.h \
		msg_pdata.h pointdefs.h message.h collectable.h loggable.h \
		connectionHandle.h msg_signal.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
standardcontrolpolicy.obj:	precompiled.h StandardControlPolicy.h \
		yukon.h types.h ctidbgmem.h ControlPolicy.h Policy.h \
		AttributeService.h LitePoint.h dlldefs.h pointtypes.h \
		PointAttribute.h PointValueHolder.h ctitime.h msg_pdata.h \
		pointdefs.h message.h collectable.h loggable.h \
		connectionHandle.h msg_signal.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
standardscanpolicy.obj:	precompiled.h StandardScanPolicy.h yukon.h \
		types.h ctidbgmem.h ScanPolicy.h Policy.h AttributeService.h \
		LitePoint.h dlldefs.h pointtypes.h PointAttribute.h \
		PointValueHolder.h ctitime.h msg_pdata.h pointdefs.h \
		message.h collectable.h loggable.h connectionHandle.h \
		msg_signal.h msg_pcrequest.h dsm2.h streamConnection.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
strategyloader.obj:	precompiled.h ccid.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h ControlStrategy.h \
		StrategyLoader.h StrategyManager.h readers_writer_lock.h \
		guard.h IVVCStrategy.h IVVCAlgorithm.h ccsubstationbus.h \
		Controllable.h CapControlPao.h cctypes.h ccOperationStats.h \
		message.h collectable.h connectionHandle.h \
		ccConfirmationStats.h regression.h ccfeeder.h \
		ccOriginalParent.h cccapbank.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h IVVCState.h \
		PointDataRequest.h PointDataRequestFactory.h \
		DispatchConnection.h connection_client.h connection.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		worker_thread.h concurrentSet.h MessageListener.h \
		ZoneManager.h Zone.h VoltageRegulatorManager.h \
		VoltageRegulator.h UpdatablePao.h AttributeService.h \
		EventTypes.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h ccarea.h ccAreaBase.h DynamicData.h ccsparea.h \
		ccstate.h MsgItemCommand.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		KVarStrategy.h MultiVoltStrategy.h MultiVoltVarStrategy.h \
		NoStrategy.h PFactorKWKVarStrategy.h PFactorKWKQStrategy.h \
		VoltStrategy.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h row_reader.h
strategymanager.obj:	precompiled.h StrategyManager.h \
		readers_writer_lock.h dlldefs.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		StrategyLoader.h NoStrategy.h
test_busoptimizedsort.obj:	ccfeeder.h Controllable.h CapControlPao.h \
		yukon.h types.h ctidbgmem.h cctypes.h ccOperationStats.h \
		message.h ctitime.h dlldefs.h collectable.h loggable.h \
		connectionHandle.h ccConfirmationStats.h StrategyManager.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		ccOriginalParent.h regression.h cccapbank.h ccmonitorpoint.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		dsm2.h streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h
test_capcontroller.obj:	capcontroller.h dbaccess.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h connection_client.h connection.h message.h \
		ctitime.h collectable.h loggable.h connectionHandle.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h StrategyManager.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h ccunittestutil.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h msg_pcreturn.h \
		capcontrol_test_helpers.h mgr_config.h config_device.h
test_ccareas.obj:	test_reader.h row_reader.h ctitime.h dlldefs.h \
		utility.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		ccarea.h ccAreaBase.h Controllable.h CapControlPao.h \
		cctypes.h ccOperationStats.h message.h collectable.h \
		loggable.h connectionHandle.h ccConfirmationStats.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		guard.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		DynamicData.h ccsparea.h
test_ccexecutor.obj:	capcontroller.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h connection_client.h connection.h message.h \
		ctitime.h collectable.h loggable.h connectionHandle.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h StrategyManager.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h ccunittestutil.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h executorfactory.h \
		capcontrol_test_helpers.h mgr_config.h config_device.h
test_ccfeeder.obj:	ccfeeder.h Controllable.h CapControlPao.h yukon.h \
		types.h ctidbgmem.h cctypes.h ccOperationStats.h message.h \
		ctitime.h dlldefs.h collectable.h loggable.h \
		connectionHandle.h ccConfirmationStats.h StrategyManager.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		ccOriginalParent.h regression.h cccapbank.h ccmonitorpoint.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		dsm2.h streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h ccsubstationbus.h TimeOfDayStrategy.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h DynamicData.h \
		ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h row_reader.h database_writer.h row_writer.h \
		ccUnitTestUtil.h capcontroller.h connection_client.h \
		connection.h msg_multi.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h worker_thread.h concurrentSet.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccexecutor.h ctdpcptrq.h CtiPCPtrQueue.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h
test_ccstatistics.obj:	CCStatsObject.h
test_ccsubstationbus.obj:	ccsubstationbus.h Controllable.h \
		CapControlPao.h yukon.h types.h ctidbgmem.h cctypes.h \
		ccOperationStats.h message.h ctitime.h dlldefs.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h ControlStrategy.h regression.h ccfeeder.h \
		ccOriginalParent.h cccapbank.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h ccsubstationbusstore.h \
		ccarea.h ccAreaBase.h DynamicData.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h row_reader.h database_writer.h row_writer.h \
		ccUnitTestUtil.h capcontroller.h connection_client.h \
		connection.h msg_multi.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h worker_thread.h concurrentSet.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccexecutor.h ctdpcptrq.h CtiPCPtrQueue.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h ExecutorFactory.h MsgVerifyBanks.h
test_ccutil.obj:	ccutil.h pointattribute.h yukon.h types.h ctidbgmem.h \
		dlldefs.h devicetypes.h msg_pcrequest.h message.h ctitime.h \
		collectable.h loggable.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h pointdefs.h
test_controlstrategies.obj:	ControlStrategy.h NoStrategy.h \
		IVVCSTrategy.h IVVCAlgorithm.h ccsubstationbus.h \
		Controllable.h CapControlPao.h yukon.h types.h ctidbgmem.h \
		cctypes.h ccOperationStats.h message.h ctitime.h dlldefs.h \
		collectable.h loggable.h connectionHandle.h \
		ccConfirmationStats.h StrategyManager.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h regression.h ccfeeder.h ccOriginalParent.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		timing_util.h immutable.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		IVVCState.h PointDataRequest.h PointDataRequestFactory.h \
		DispatchConnection.h connection_client.h connection.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		worker_thread.h concurrentSet.h MessageListener.h \
		ZoneManager.h Zone.h VoltageRegulatorManager.h \
		VoltageRegulator.h UpdatablePao.h AttributeService.h ccid.h \
		EventTypes.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h ccarea.h ccAreaBase.h DynamicData.h ccsparea.h \
		ccstate.h MsgItemCommand.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		StrategyLoader.h test_reader.h row_reader.h
test_gangoperatedvoltageregulator.obj:	capcontroller.h dbaccess.h \
		dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h connection_client.h \
		connection.h message.h ctitime.h collectable.h loggable.h \
		connectionHandle.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h StrategyManager.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h GangOperatedVoltageRegulator.h mgr_config.h \
		config_device.h std_helper.h capcontrol_test_helpers.h
test_ivvcalgorithm.obj:	ccUnitTestUtil.h ccsubstationbusstore.h \
		ccarea.h ccAreaBase.h Controllable.h CapControlPao.h yukon.h \
		types.h ctidbgmem.h cctypes.h ccOperationStats.h message.h \
		ctitime.h dlldefs.h collectable.h loggable.h \
		connectionHandle.h ccConfirmationStats.h StrategyManager.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h timing_util.h \
		immutable.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h row_reader.h database_writer.h row_writer.h \
		capcontroller.h connection_client.h connection.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h worker_thread.h \
		concurrentSet.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccexecutor.h ctdpcptrq.h CtiPCPtrQueue.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h IVVCStrategy.h IVVCAlgorithm.h \
		IVVCState.h GangOperatedVoltageRegulator.h mgr_config.h \
		config_device.h capcontrol_test_helpers.h
test_likedaycontrol.obj:	ccfeeder.h Controllable.h CapControlPao.h \
		yukon.h types.h ctidbgmem.h cctypes.h ccOperationStats.h \
		message.h ctitime.h dlldefs.h collectable.h loggable.h \
		connectionHandle.h ccConfirmationStats.h StrategyManager.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h ControlStrategy.h \
		ccOriginalParent.h regression.h cccapbank.h ccmonitorpoint.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		dsm2.h streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h ccsubstationbus.h TimeOfDayStrategy.h \
		ccUnitTestUtil.h ccsubstationbusstore.h ccarea.h ccAreaBase.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h row_reader.h database_writer.h row_writer.h \
		capcontroller.h connection_client.h connection.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h worker_thread.h \
		concurrentSet.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccexecutor.h ctdpcptrq.h CtiPCPtrQueue.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h VoltStrategy.h
test_main.obj:	amq_connection.h thread.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h \
		StreamableMessage.h connection_base.h
test_phaseoperatedvoltageregulator.obj:	capcontroller.h dbaccess.h \
		dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h connection_client.h \
		connection.h message.h ctitime.h collectable.h loggable.h \
		connectionHandle.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h \
		ccConfirmationStats.h StrategyManager.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h ControlPolicy.h Policy.h msg_signal.h \
		KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h PhaseOperatedVoltageRegulator.h mgr_config.h \
		config_device.h std_helper.h capcontrol_test_helpers.h
test_pointholder.obj:	PointValueHolder.h yukon.h types.h ctidbgmem.h \
		pointtypes.h ctitime.h dlldefs.h msg_pdata.h pointdefs.h \
		message.h collectable.h loggable.h connectionHandle.h
test_strategymanager.obj:	StrategyManager.h readers_writer_lock.h \
		dlldefs.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h ControlStrategy.h StrategyLoader.h KVarStrategy.h \
		NoStrategy.h PFactorKWKVarStrategy.h ccunittestutil.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h message.h \
		collectable.h connectionHandle.h ccConfirmationStats.h \
		DynamicData.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h timing_util.h \
		immutable.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h LastControlReason.h IgnoredControlReason.h \
		ctidate.h PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h dbaccess.h dllbase.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h capcontroller.h \
		connection_client.h connection.h msg_multi.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h worker_thread.h concurrentSet.h \
		DispatchConnection.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h ccexecutor.h ctdpcptrq.h CtiPCPtrQueue.h \
		PointDataRequest.h PointDataRequestFactory.h
test_twowaycbcpoints.obj:	boost_test_helpers.h millisecond_timer.h \
		dlldefs.h ctitime.h ctidate.h cctwowaycbcpoints.h types.h \
		PointAttribute.h yukon.h ctidbgmem.h PointValueHolder.h \
		pointtypes.h msg_pdata.h pointdefs.h message.h collectable.h \
		loggable.h connectionHandle.h LitePoint.h LastControlReason.h \
		IgnoredControlReason.h std_helper.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h streamBuffer.h
test_utils.obj:	ccutil.h pointattribute.h yukon.h types.h ctidbgmem.h \
		dlldefs.h devicetypes.h msg_pcrequest.h message.h ctitime.h \
		collectable.h loggable.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h
test_voltageregulatormanager.obj:	GangOperatedVoltageRegulator.h \
		VoltageRegulator.h yukon.h types.h ctidbgmem.h \
		CapControlPao.h cctypes.h ccOperationStats.h message.h \
		ctitime.h dlldefs.h collectable.h loggable.h \
		connectionHandle.h ccConfirmationStats.h UpdatablePao.h \
		msg_pdata.h pointdefs.h pointtypes.h LitePoint.h \
		PointValueHolder.h AttributeService.h PointAttribute.h ccid.h \
		ccutil.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h EventTypes.h ControlPolicy.h \
		Policy.h msg_signal.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h ccsubstationbusstore.h ccarea.h \
		ccAreaBase.h Controllable.h StrategyManager.h \
		readers_writer_lock.h ControlStrategy.h DynamicData.h \
		ccsparea.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h cctwowaycbcpoints.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h row_reader.h database_writer.h row_writer.h
test_zonemanager.obj:	boost_test_helpers.h millisecond_timer.h \
		dlldefs.h ctitime.h ctidate.h ZoneManager.h Zone.h ccutil.h \
		pointattribute.h yukon.h types.h ctidbgmem.h devicetypes.h \
		msg_pcrequest.h message.h collectable.h loggable.h \
		connectionHandle.h dsm2.h streamConnection.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h readers_writer_lock.h \
		ZoneLoader.h
timeofdaystrategy.obj:	precompiled.h TimeOfDayStrategy.h \
		ControlStrategy.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h
voltageregulator.obj:	precompiled.h VoltageRegulator.h yukon.h types.h \
		ctidbgmem.h CapControlPao.h cctypes.h ccOperationStats.h \
		message.h ctitime.h dlldefs.h collectable.h loggable.h \
		connectionHandle.h ccConfirmationStats.h UpdatablePao.h \
		msg_pdata.h pointdefs.h pointtypes.h LitePoint.h \
		PointValueHolder.h AttributeService.h PointAttribute.h ccid.h \
		ccutil.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h EventTypes.h ControlPolicy.h \
		Policy.h msg_signal.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h Capcontroller.h dbaccess.h dllbase.h \
		connection_client.h connection.h msg_multi.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h readers_writer_lock.h \
		worker_thread.h concurrentSet.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h ccsubstationbusstore.h ccarea.h ccAreaBase.h \
		Controllable.h StrategyManager.h ControlStrategy.h \
		DynamicData.h ccsparea.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		MsgBankMove.h MsgObjectMove.h MsgCapControlMessage.h \
		MsgSubstationBus.h ccsubstationbus.h regression.h ccfeeder.h \
		ccOriginalParent.h cccapbank.h ccmonitorpoint.h \
		cctwowaycbcpoints.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h ctdpcptrq.h \
		CtiPCPtrQueue.h resolvers.h db_entry_defines.h mgr_config.h \
		config_device.h config_data_regulator.h \
		StandardControlPolicy.h NoKeepAlivePolicy.h \
		CountdownKeepAlivePolicy.h IncrementingKeepAlivePolicy.h \
		LoadOnlyScanPolicy.h StandardScanPolicy.h std_helper.h
voltageregulatorloader.obj:	precompiled.h ccid.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		database_connection.h dbaccess.h dllbase.h guard.h \
		database_reader.h row_reader.h VoltageRegulatorLoader.h \
		VoltageRegulatorManager.h readers_writer_lock.h \
		VoltageRegulator.h CapControlPao.h cctypes.h \
		ccOperationStats.h message.h collectable.h connectionHandle.h \
		ccConfirmationStats.h UpdatablePao.h msg_pdata.h pointdefs.h \
		pointtypes.h LitePoint.h PointValueHolder.h \
		AttributeService.h PointAttribute.h ccutil.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h timing_util.h \
		immutable.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h EventTypes.h ControlPolicy.h \
		Policy.h msg_signal.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h ccarea.h \
		ccAreaBase.h Controllable.h StrategyManager.h \
		ControlStrategy.h DynamicData.h ccsparea.h ccstate.h \
		MsgItemCommand.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h cctwowaycbcpoints.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		GangOperatedVoltageRegulator.h \
		PhaseOperatedVoltageRegulator.h resolvers.h \
		db_entry_defines.h
voltageregulatormanager.obj:	precompiled.h VoltageRegulatorManager.h \
		readers_writer_lock.h dlldefs.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h VoltageRegulator.h \
		CapControlPao.h cctypes.h ccOperationStats.h message.h \
		collectable.h connectionHandle.h ccConfirmationStats.h \
		UpdatablePao.h msg_pdata.h pointdefs.h pointtypes.h \
		LitePoint.h PointValueHolder.h AttributeService.h \
		PointAttribute.h ccid.h ccutil.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h timing_util.h \
		immutable.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h EventTypes.h ControlPolicy.h \
		Policy.h msg_signal.h KeepAlivePolicy.h ScanPolicy.h \
		RegulatorEvents.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h ccarea.h \
		ccAreaBase.h Controllable.h StrategyManager.h \
		ControlStrategy.h DynamicData.h ccsparea.h ccstate.h \
		MsgItemCommand.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h cctwowaycbcpoints.h LastControlReason.h \
		IgnoredControlReason.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		VoltageRegulatorLoader.h
voltstrategy.obj:	precompiled.h VoltStrategy.h ControlStrategy.h
zone.obj:	precompiled.h Zone.h ccutil.h pointattribute.h yukon.h \
		types.h ctidbgmem.h dlldefs.h devicetypes.h msg_pcrequest.h \
		message.h ctitime.h collectable.h loggable.h \
		connectionHandle.h dsm2.h streamConnection.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h
zoneloader.obj:	precompiled.h ccid.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h database_connection.h dbaccess.h \
		dllbase.h guard.h database_reader.h row_reader.h ZoneLoader.h \
		ZoneManager.h Zone.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h message.h collectable.h connectionHandle.h \
		dsm2.h streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h CapControlOperationMessage.h \
		readers_writer_lock.h ccsubstationbusstore.h ccarea.h \
		ccAreaBase.h Controllable.h CapControlPao.h cctypes.h \
		ccOperationStats.h ccConfirmationStats.h StrategyManager.h \
		ControlStrategy.h DynamicData.h ccsparea.h ccstate.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h cctwowaycbcpoints.h PointValueHolder.h \
		pointtypes.h msg_pdata.h pointdefs.h LitePoint.h \
		LastControlReason.h IgnoredControlReason.h ctidate.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h ControlPolicy.h Policy.h \
		msg_signal.h KeepAlivePolicy.h ScanPolicy.h RegulatorEvents.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h
zonemanager.obj:	precompiled.h ZoneManager.h Zone.h ccutil.h \
		pointattribute.h yukon.h types.h ctidbgmem.h dlldefs.h \
		devicetypes.h msg_pcrequest.h message.h ctitime.h \
		collectable.h loggable.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		CapControlOperationMessage.h readers_writer_lock.h \
		ZoneLoader.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
