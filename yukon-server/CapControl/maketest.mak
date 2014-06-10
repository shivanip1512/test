# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(CAPCONTROL)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(SERVICE)\include \
-I$(SERVER)\include \
-I$(RW) \
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
test_utils.obj


CAPCTRLBASEOBJS= \
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
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(CAPCTRLTESTOBJS)]

test_capcontrol.exe:  $(CAPCONTROL_TEST_FULLBUILD) $(CAPCTRLTESTOBJS) Makefile
	@echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(_TargetF) \
	$(CAPCTRLTESTOBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(CAPCTRLBASEOBJS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	$(MANIFEST_TOOL) -manifest $(BIN)\$(_TargetF).manifest -outputresource:$(BIN)\$(_TargetF);1
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
	@%cd $(CWD)
	@echo.

cc_server_client_serialization_test.exe:  $(CAPCONTROL_TEST_FULLBUILD) cc_server_client_serialization_test.obj Makefile
	@echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(_TargetF) \
	cc_server_client_serialization_test.obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(CAPCTRLBASEOBJS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	$(MANIFEST_TOOL) -manifest $(BIN)\$(_TargetF).manifest -outputresource:$(BIN)\$(_TargetF);1
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
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $(_Source)


######################################################################################
#UPDATE#
test_capcontroller.obj:	capcontroller.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h connection_client.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		readers_writer_lock.h critical_section.h connection_base.h \
		worker_thread.h timing_util.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h mgr_paosched.h \
		pao_schedule.h ctibase.h ctinexus.h socket_helper.h \
		pao_event.h dbmemobject.h amq_connection.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h ctidate.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		EventLogEntry.h TimeOfDayStrategy.h ccareabase.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h ccunittestutil.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h msg_pcreturn.h
test_ccexecutor.obj:	capcontroller.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h connection_client.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		readers_writer_lock.h critical_section.h connection_base.h \
		worker_thread.h timing_util.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h mgr_paosched.h \
		pao_schedule.h ctibase.h ctinexus.h socket_helper.h \
		pao_event.h dbmemobject.h amq_connection.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h ctidate.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		EventLogEntry.h TimeOfDayStrategy.h ccareabase.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h ccunittestutil.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h executorfactory.h
test_ccfeeder.obj:	ccfeeder.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h macro_offset.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h worker_thread.h \
		timing_util.h observe.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h mgr_paosched.h \
		pao_schedule.h ctibase.h ctinexus.h socket_helper.h \
		pao_event.h dbmemobject.h amq_connection.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h ccoriginalparent.h cccapbank.h \
		msg_cmd.h cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h ctidate.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		EventLogEntry.h ccsubstationbus.h TimeOfDayStrategy.h \
		ccsubstationbusstore.h ccarea.h ccareabase.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h \
		ccUnitTestUtil.h capcontroller.h connection_client.h \
		DispatchConnection.h CapControlDispatchConnection.h \
		msg_dbchg.h ccexecutor.h msg_signal.h ctdpcptrq.h \
		PointDataRequest.h PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h
test_ccsubstationbus.obj:	ccsubstationbus.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h worker_thread.h \
		timing_util.h observe.h ccfeeder.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h mgr_paosched.h \
		pao_schedule.h ctibase.h ctinexus.h socket_helper.h \
		pao_event.h dbmemobject.h amq_connection.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h ccoriginalparent.h cccapbank.h \
		msg_cmd.h cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h ctidate.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		EventLogEntry.h TimeOfDayStrategy.h ccsubstationbusstore.h \
		ccarea.h ccareabase.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h \
		ccUnitTestUtil.h capcontroller.h connection_client.h \
		DispatchConnection.h CapControlDispatchConnection.h \
		msg_dbchg.h ccexecutor.h msg_signal.h ctdpcptrq.h \
		PointDataRequest.h PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h ExecutorFactory.h MsgVerifyBanks.h
test_ccutil.obj:	ccutil.h pointattribute.h yukon.h types.h ctidbgmem.h \
		dlldefs.h devicetypes.h msg_pcrequest.h message.h ctitime.h \
		collectable.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h mgr_paosched.h \
		pao_schedule.h row_reader.h ctibase.h ctinexus.h \
		socket_helper.h critical_section.h dllbase.h logger.h \
		thread.h CtiPCPtrQueue.h pao_event.h dbmemobject.h \
		amq_connection.h StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h CapControlOperationMessage.h \
		pointdefs.h
test_controlstrategies.obj:	ControlStrategy.h NoStrategy.h \
		IVVCSTrategy.h IVVCAlgorithm.h ccsubstationbus.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h macro_offset.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		readers_writer_lock.h critical_section.h connection_base.h \
		worker_thread.h timing_util.h observe.h ccfeeder.h \
		ccmonitorpoint.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h mgr_paosched.h pao_schedule.h ctibase.h \
		ctinexus.h socket_helper.h pao_event.h dbmemobject.h \
		amq_connection.h StreamableMessage.h \
		RfnBroadcastReplyMessage.h CapControlOperationMessage.h \
		ccoriginalparent.h cccapbank.h msg_cmd.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h ctidate.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h sorted_vector.h regression.h \
		Controllable.h EventLogEntry.h TimeOfDayStrategy.h \
		IVVCState.h PointDataRequest.h PointDataRequestFactory.h \
		DispatchConnection.h connection_client.h MessageListener.h \
		ZoneManager.h Zone.h
test_gangoperatedvoltageregulator.obj:	capcontroller.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h macro_offset.h connection_client.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		readers_writer_lock.h critical_section.h connection_base.h \
		worker_thread.h timing_util.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h mgr_paosched.h \
		pao_schedule.h ctibase.h ctinexus.h socket_helper.h \
		pao_event.h dbmemobject.h amq_connection.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h ctidate.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		EventLogEntry.h TimeOfDayStrategy.h ccareabase.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h GangOperatedVoltageRegulator.h
test_ivvcalgorithm.obj:	ccUnitTestUtil.h ccsubstationbusstore.h \
		observe.h types.h dlldefs.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h ccarea.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h \
		guard.h dsm2err.h words.h optional.h macro_offset.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h worker_thread.h \
		timing_util.h ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		mgr_paosched.h pao_schedule.h ctibase.h ctinexus.h \
		socket_helper.h pao_event.h dbmemobject.h amq_connection.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h ccoriginalparent.h cccapbank.h \
		msg_cmd.h cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h ctidate.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		EventLogEntry.h TimeOfDayStrategy.h ccareabase.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h \
		capcontroller.h connection_client.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_dbchg.h ccexecutor.h \
		msg_signal.h ctdpcptrq.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h IVVCStrategy.h IVVCAlgorithm.h \
		IVVCState.h
test_likedaycontrol.obj:	ccfeeder.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h worker_thread.h \
		timing_util.h observe.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h mgr_paosched.h \
		pao_schedule.h ctibase.h ctinexus.h socket_helper.h \
		pao_event.h dbmemobject.h amq_connection.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h ccoriginalparent.h cccapbank.h \
		msg_cmd.h cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h ctidate.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		EventLogEntry.h ccsubstationbus.h TimeOfDayStrategy.h \
		ccUnitTestUtil.h ccsubstationbusstore.h ccarea.h ccareabase.h \
		ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h \
		capcontroller.h connection_client.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_dbchg.h ccexecutor.h \
		msg_signal.h ctdpcptrq.h PointDataRequest.h \
		PointDataRequestFactory.h kvarstrategy.h \
		pfactorkwkvarstrategy.h VoltStrategy.h
test_main.obj:	amq_connection.h thread.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h critical_section.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h
test_phaseoperatedvoltageregulator.obj:	capcontroller.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h macro_offset.h connection_client.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		readers_writer_lock.h critical_section.h connection_base.h \
		worker_thread.h timing_util.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h mgr_paosched.h \
		pao_schedule.h ctibase.h ctinexus.h socket_helper.h \
		pao_event.h dbmemobject.h amq_connection.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h ctidate.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		EventLogEntry.h TimeOfDayStrategy.h ccareabase.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h PhaseOperatedVoltageRegulator.h
test_pointholder.obj:	PointValueHolder.h yukon.h types.h ctidbgmem.h \
		pointtypes.h ctitime.h dlldefs.h msg_pdata.h pointdefs.h \
		message.h collectable.h
test_strategymanager.obj:	StrategyManager.h readers_writer_lock.h \
		dlldefs.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		ControlStrategy.h StrategyLoader.h KVarStrategy.h \
		NoStrategy.h PFactorKWKVarStrategy.h ccunittestutil.h \
		ccsubstationbusstore.h observe.h ccarea.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h connection_base.h worker_thread.h \
		timing_util.h ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		mgr_paosched.h pao_schedule.h ctibase.h ctinexus.h \
		socket_helper.h pao_event.h dbmemobject.h amq_connection.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h ccoriginalparent.h cccapbank.h \
		msg_cmd.h cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h ctidate.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h sorted_vector.h \
		regression.h Controllable.h EventLogEntry.h \
		TimeOfDayStrategy.h ccareabase.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h VoltageRegulator.h UpdatablePao.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h capcontroller.h \
		connection_client.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_dbchg.h ccexecutor.h \
		msg_signal.h ctdpcptrq.h PointDataRequest.h \
		PointDataRequestFactory.h
test_utils.obj:	ccutil.h pointattribute.h yukon.h types.h ctidbgmem.h \
		dlldefs.h devicetypes.h msg_pcrequest.h message.h ctitime.h \
		collectable.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h mgr_paosched.h \
		pao_schedule.h row_reader.h ctibase.h ctinexus.h \
		socket_helper.h critical_section.h dllbase.h logger.h \
		thread.h CtiPCPtrQueue.h pao_event.h dbmemobject.h \
		amq_connection.h StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h CapControlOperationMessage.h
test_voltageregulatormanager.obj:	GangOperatedVoltageRegulator.h \
		VoltageRegulator.h yukon.h types.h ctidbgmem.h \
		CapControlPao.h row_reader.h ctitime.h dlldefs.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h boost_time.h boostutil.h \
		cctypes.h UpdatablePao.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h collectable.h LitePoint.h PointValueHolder.h \
		AttributeService.h PointAttribute.h ccid.h ccutil.h \
		devicetypes.h msg_pcrequest.h mgr_paosched.h pao_schedule.h \
		ctibase.h ctinexus.h socket_helper.h critical_section.h \
		logger.h thread.h CtiPCPtrQueue.h pao_event.h dbmemobject.h \
		amq_connection.h StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h CapControlOperationMessage.h \
		EventTypes.h ccsubstationbusstore.h observe.h ccarea.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h readers_writer_lock.h worker_thread.h \
		timing_util.h ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		ccoriginalparent.h cccapbank.h msg_cmd.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h ctidate.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		StrategyManager.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h EventLogEntry.h \
		TimeOfDayStrategy.h ccareabase.h ccsparea.h ccstate.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h
test_zonemanager.obj:	ZoneManager.h Zone.h ccutil.h pointattribute.h \
		yukon.h types.h ctidbgmem.h dlldefs.h devicetypes.h \
		msg_pcrequest.h message.h ctitime.h collectable.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h mgr_paosched.h pao_schedule.h row_reader.h \
		ctibase.h ctinexus.h socket_helper.h critical_section.h \
		dllbase.h logger.h thread.h CtiPCPtrQueue.h pao_event.h \
		dbmemobject.h amq_connection.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h \
		CapControlOperationMessage.h readers_writer_lock.h \
		ZoneLoader.h
#ENDUPDATE#

