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
-I$(ACTIVEMQ)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(PROT)\include \
-I$(THRIFT_INCLUDE) \
-I$(MSG)\Serialization \

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
$(COMPILEBASE)\lib\ctithriftmsg.lib \
$(THRIFT_LIB)


CAPCTRLTESTOBJS= \
test_main.obj \
test_ControlStrategies.obj \
test_StrategyManager.obj \
test_ZoneManager.obj \
test_VoltageRegulatorManager.obj \
test_capcontroller.obj \
test_ccexecutor.obj \
test_ccsubstationbus.obj \
test_ccFeeder.obj \
test_ccutil.obj \
test_likeDayControl.obj \
test_pointHolder.obj \
test_IVVCAlgorithm.obj \
test_GangOperatedVoltageRegulator.obj \
test_PhaseOperatedVoltageRegulator.obj \
test_utils.obj \
test_TwoWayCBCPoints.obj \
test_BusOptimizedSort.obj


CAPCTRLBASEOBJS= \
$(PRECOMPILED_OBJ) \
cc_message_serialization.obj \
ccservice.obj \
capcontroller.obj \
cccapbank.obj \
ccclientconn.obj \
ccclientlistener.obj \
ccexecutor.obj \
ccfeeder.obj \
ccstate.obj \
ccsubstationbus.obj \
ccsubstationbusstore.obj \
pao_schedule.obj \
pao_event.obj \
mgr_paosched.obj \
Controllable.obj \
ControlStrategy.obj \
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
GangOperatedVoltageRegulator.obj \
PhaseOperatedVoltageRegulator.obj \
ccmonitorpoint.obj \
cctwowaycbcpoints.obj \
ccareabase.obj \
ccarea.obj \
ccsparea.obj \
ccsubstation.obj \
ccoperationstats.obj \
ccconfirmationstats.obj \
ccstatsobject.obj \
ccoriginalparent.obj \
CapControlPao.obj \
CapControlPointDataHandler.obj \
PointValueHolder.obj \
ccutil.obj \
IVVCAlgorithm.obj \
IVVCState.obj \
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
ExecutorFactory.obj

CAPCONTROL_TEST_FULLBUILD = $[Filename,$(OBJ),CapControlTestFullBuild,target]

ALL:     test_capcontrol.exe \
         cc_server_client_serialization_test.exe

$(CAPCONTROL_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(CAPCTRLTESTOBJS)]

test_capcontrol.exe:  $(CAPCONTROL_TEST_FULLBUILD) $(CAPCTRLTESTOBJS) Makefile
	@echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$(BIN)\$(_TargetF) \
	$(CAPCTRLTESTOBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(CAPCTRLBASEOBJS) $(BOOST_TEST_LIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
	@%cd $(CWD)
	@echo.

cc_server_client_serialization_test.exe:  $(CAPCONTROL_TEST_FULLBUILD) cc_server_client_serialization_test.obj Makefile
	@echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$(BIN)\$(_TargetF) \
	cc_server_client_serialization_test.obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(CAPCTRLBASEOBJS) $(BOOST_TEST_LIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
	@%cd $(CWD)
	@echo.

copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
        scandeps -Output maketest.mak test_*.cpp


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(_SourceB).obj
        @echo:
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $(_Source)


######################################################################################
#UPDATE#
test_busoptimizedsort.obj:	ccfeeder.h Controllable.h CapControlPao.h \
		yukon.h types.h ctidbgmem.h cctypes.h StrategyManager.h \
		readers_writer_lock.h dlldefs.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		ControlStrategy.h ccOperationStats.h message.h collectable.h \
		ccConfirmationStats.h ccOriginalParent.h regression.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h ctidate.h PointResponse.h PointResponseManager.h \
		Exceptions.h EventLogEntry.h sorted_vector.h
test_capcontroller.obj:	capcontroller.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		connection_client.h connection.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h StrategyManager.h ControlStrategy.h \
		ccOperationStats.h ccConfirmationStats.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h ctdpcptrq.h CtiPCPtrQueue.h ccunittestutil.h \
		PointDataRequest.h PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h msg_pcreturn.h
test_ccexecutor.obj:	capcontroller.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		connection_client.h connection.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h StrategyManager.h ControlStrategy.h \
		ccOperationStats.h ccConfirmationStats.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h ctdpcptrq.h CtiPCPtrQueue.h ccunittestutil.h \
		PointDataRequest.h PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h executorfactory.h
test_ccfeeder.obj:	ccfeeder.h Controllable.h CapControlPao.h yukon.h \
		types.h ctidbgmem.h cctypes.h StrategyManager.h \
		readers_writer_lock.h dlldefs.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		ControlStrategy.h ccOperationStats.h message.h collectable.h \
		ccConfirmationStats.h ccOriginalParent.h regression.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h ctidate.h PointResponse.h PointResponseManager.h \
		Exceptions.h EventLogEntry.h sorted_vector.h \
		ccsubstationbus.h TimeOfDayStrategy.h ccsubstationbusstore.h \
		ccarea.h ccAreaBase.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h MsgDeleteItem.h MsgSystemStatus.h \
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
		cparms.h configkey.h configval.h worker_thread.h \
		DispatchConnection.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h ccexecutor.h msg_signal.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		ctdpcptrq.h CtiPCPtrQueue.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h
test_ccsubstationbus.obj:	ccsubstationbus.h Controllable.h \
		CapControlPao.h yukon.h types.h ctidbgmem.h cctypes.h \
		StrategyManager.h readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h ControlStrategy.h ccOperationStats.h message.h \
		collectable.h ccConfirmationStats.h regression.h ccfeeder.h \
		ccOriginalParent.h cccapbank.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		mgr_paosched.h amq_connection.h thread.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h ctidate.h PointResponse.h PointResponseManager.h \
		Exceptions.h EventLogEntry.h sorted_vector.h \
		TimeOfDayStrategy.h ccsubstationbusstore.h ccarea.h \
		ccAreaBase.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h MsgDeleteItem.h MsgSystemStatus.h \
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
		cparms.h configkey.h configval.h worker_thread.h \
		DispatchConnection.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h ccexecutor.h msg_signal.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		ctdpcptrq.h CtiPCPtrQueue.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h ExecutorFactory.h MsgVerifyBanks.h
test_ccutil.obj:	ccutil.h pointattribute.h yukon.h types.h ctidbgmem.h \
		dlldefs.h devicetypes.h msg_pcrequest.h message.h ctitime.h \
		collectable.h loggable.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h CapControlOperationMessage.h \
		pointdefs.h
test_controlstrategies.obj:	ControlStrategy.h NoStrategy.h \
		IVVCSTrategy.h IVVCAlgorithm.h ccsubstationbus.h \
		Controllable.h CapControlPao.h yukon.h types.h ctidbgmem.h \
		cctypes.h StrategyManager.h readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h ccOperationStats.h message.h collectable.h \
		ccConfirmationStats.h regression.h ccfeeder.h \
		ccOriginalParent.h cccapbank.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		mgr_paosched.h amq_connection.h thread.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h ctidate.h PointResponse.h PointResponseManager.h \
		Exceptions.h EventLogEntry.h sorted_vector.h \
		TimeOfDayStrategy.h IVVCState.h PointDataRequest.h \
		PointDataRequestFactory.h DispatchConnection.h \
		connection_client.h connection.h msg_multi.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		worker_thread.h MessageListener.h ZoneManager.h Zone.h
test_gangoperatedvoltageregulator.obj:	capcontroller.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h connection_client.h connection.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h ccsubstationbusstore.h ccarea.h ccAreaBase.h \
		Controllable.h CapControlPao.h cctypes.h StrategyManager.h \
		ControlStrategy.h ccOperationStats.h ccConfirmationStats.h \
		ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h ctdpcptrq.h CtiPCPtrQueue.h \
		GangOperatedVoltageRegulator.h
test_ivvcalgorithm.obj:	ccUnitTestUtil.h ccsubstationbusstore.h \
		ccarea.h ccAreaBase.h Controllable.h CapControlPao.h yukon.h \
		types.h ctidbgmem.h cctypes.h StrategyManager.h \
		readers_writer_lock.h dlldefs.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		ControlStrategy.h ccOperationStats.h message.h collectable.h \
		ccConfirmationStats.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h database_connection.h \
		dbaccess.h dllbase.h PointResponseDatabaseDao.h \
		database_reader.h row_reader.h database_writer.h row_writer.h \
		capcontroller.h connection_client.h connection.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h worker_thread.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccexecutor.h msg_signal.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h ctdpcptrq.h CtiPCPtrQueue.h \
		PointDataRequest.h PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h IVVCStrategy.h IVVCAlgorithm.h \
		IVVCState.h
test_likedaycontrol.obj:	ccfeeder.h Controllable.h CapControlPao.h \
		yukon.h types.h ctidbgmem.h cctypes.h StrategyManager.h \
		readers_writer_lock.h dlldefs.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		ControlStrategy.h ccOperationStats.h message.h collectable.h \
		ccConfirmationStats.h ccOriginalParent.h regression.h \
		cccapbank.h ccmonitorpoint.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h mgr_paosched.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h pointtypes.h msg_pdata.h pointdefs.h \
		LitePoint.h ctidate.h PointResponse.h PointResponseManager.h \
		Exceptions.h EventLogEntry.h sorted_vector.h \
		ccsubstationbus.h TimeOfDayStrategy.h ccUnitTestUtil.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h AttributeService.h MsgDeleteItem.h \
		MsgSystemStatus.h MsgCapControlServerResponse.h \
		MsgCapControlShutdown.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h dbaccess.h dllbase.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h capcontroller.h \
		connection_client.h connection.h msg_multi.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		worker_thread.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccexecutor.h msg_signal.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h ctdpcptrq.h CtiPCPtrQueue.h \
		PointDataRequest.h PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h VoltStrategy.h
test_main.obj:	amq_connection.h thread.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h critical_section.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h
test_phaseoperatedvoltageregulator.obj:	capcontroller.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h connection_client.h connection.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h ccsubstationbusstore.h ccarea.h ccAreaBase.h \
		Controllable.h CapControlPao.h cctypes.h StrategyManager.h \
		ControlStrategy.h ccOperationStats.h ccConfirmationStats.h \
		ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h cctwowaycbcpoints.h \
		PointValueHolder.h LitePoint.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h ctdpcptrq.h CtiPCPtrQueue.h \
		PhaseOperatedVoltageRegulator.h
test_pointholder.obj:	PointValueHolder.h yukon.h types.h ctidbgmem.h \
		pointtypes.h ctitime.h dlldefs.h msg_pdata.h pointdefs.h \
		message.h collectable.h loggable.h
test_strategymanager.obj:	StrategyManager.h readers_writer_lock.h \
		dlldefs.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		ControlStrategy.h StrategyLoader.h KVarStrategy.h \
		NoStrategy.h PFactorKWKVarStrategy.h ccunittestutil.h \
		ccsubstationbusstore.h ccarea.h ccAreaBase.h Controllable.h \
		CapControlPao.h cctypes.h ccOperationStats.h message.h \
		collectable.h ccConfirmationStats.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h CapControlOperationMessage.h \
		cctwowaycbcpoints.h PointValueHolder.h pointtypes.h \
		msg_pdata.h pointdefs.h LitePoint.h ctidate.h PointResponse.h \
		PointResponseManager.h Exceptions.h EventLogEntry.h \
		sorted_vector.h TimeOfDayStrategy.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		AttributeService.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h dbaccess.h dllbase.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h capcontroller.h \
		connection_client.h connection.h msg_multi.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		worker_thread.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		ccexecutor.h msg_signal.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h ctdpcptrq.h CtiPCPtrQueue.h \
		PointDataRequest.h PointDataRequestFactory.h
test_twowaycbcpoints.obj:	cctwowaycbcpoints.h types.h PointAttribute.h \
		yukon.h ctidbgmem.h dlldefs.h PointValueHolder.h pointtypes.h \
		ctitime.h msg_pdata.h pointdefs.h message.h collectable.h \
		loggable.h LitePoint.h ctidate.h
test_utils.obj:	ccutil.h pointattribute.h yukon.h types.h ctidbgmem.h \
		dlldefs.h devicetypes.h msg_pcrequest.h message.h ctitime.h \
		collectable.h loggable.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h amq_connection.h thread.h \
		StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h CapControlOperationMessage.h
test_voltageregulatormanager.obj:	GangOperatedVoltageRegulator.h \
		VoltageRegulator.h yukon.h types.h ctidbgmem.h \
		CapControlPao.h cctypes.h UpdatablePao.h msg_pdata.h \
		dlldefs.h pointdefs.h pointtypes.h message.h ctitime.h \
		collectable.h loggable.h LitePoint.h PointValueHolder.h \
		AttributeService.h PointAttribute.h ccid.h ccutil.h \
		devicetypes.h msg_pcrequest.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h constants.h \
		numstr.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h mgr_paosched.h amq_connection.h \
		thread.h StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h CapControlOperationMessage.h \
		EventTypes.h ccsubstationbusstore.h ccarea.h ccAreaBase.h \
		Controllable.h StrategyManager.h readers_writer_lock.h \
		ControlStrategy.h ccOperationStats.h ccConfirmationStats.h \
		ccsparea.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h ccsubstationbus.h \
		regression.h ccfeeder.h ccOriginalParent.h cccapbank.h \
		ccmonitorpoint.h cctwowaycbcpoints.h ctidate.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		EventLogEntry.h sorted_vector.h TimeOfDayStrategy.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h MsgDeleteItem.h \
		MsgSystemStatus.h MsgCapControlServerResponse.h \
		MsgCapControlShutdown.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h database_connection.h dbaccess.h dllbase.h \
		PointResponseDatabaseDao.h database_reader.h row_reader.h \
		database_writer.h row_writer.h
test_zonemanager.obj:	ZoneManager.h Zone.h ccutil.h pointattribute.h \
		yukon.h types.h ctidbgmem.h dlldefs.h devicetypes.h \
		msg_pcrequest.h message.h ctitime.h collectable.h loggable.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		mgr_paosched.h amq_connection.h thread.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h readers_writer_lock.h \
		ZoneLoader.h
#ENDUPDATE#

