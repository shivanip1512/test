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
-I$(CPARMS)\include \
-I$(SERVER)\include \
-I$(RW) \
-I$(BOOST) \
-I$(SQLAPI)\include \
-I$(ACTIVEMQ) \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CAPCONTROL)\include \
;$(CPARMS)\include \
;$(DATABASE)\include \
;$(SERVICE)\include \
;$(RTDB)\include \
;$(SERVER)\include \
;$(MSG)\include \
;$(RW) \
;$(ACTIVEMQ) \
;$(ACTIVEMQ)\cms \
;$(ACTIVEMQ)\activemq\library \



LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib

CAPCTRLTESTOBJS= \
test_ControlStrategies.obj \
test_StrategyManager.obj \
test_ZoneManager.obj \
test_VoltageRegulatorManager.obj \
test_ccsubstationbus.obj \
test_ccFeeder.obj \
test_likeDayControl.obj \
test_pointHolder.obj \
test_IVVCAlgorithm.obj \
test_GangOperatedVoltageRegulator.obj \
test_PhaseOperatedVoltageRegulator.obj


CAPCTRLBASEOBJS= \
$(OBJ)\ccservice.obj \
$(OBJ)\capcontroller.obj \
$(OBJ)\cccapbank.obj \
$(OBJ)\ccclientconn.obj \
$(OBJ)\ccclientlistener.obj \
$(OBJ)\ccexecutor.obj \
$(OBJ)\ccfeeder.obj \
$(OBJ)\ccstate.obj \
$(OBJ)\ccsubstationbus.obj \
$(OBJ)\ccsubstationbusstore.obj \
$(OBJ)\pao_schedule.obj \
$(OBJ)\pao_event.obj \
$(OBJ)\mgr_paosched.obj \
$(OBJ)\Controllable.obj \
$(OBJ)\ControlStrategy.obj \
$(OBJ)\IVVCStrategy.obj \
$(OBJ)\KVarStrategy.obj \
$(OBJ)\MultiVoltStrategy.obj \
$(OBJ)\MultiVoltVarStrategy.obj \
$(OBJ)\NoStrategy.obj \
$(OBJ)\PFactorKWKVarStrategy.obj \
$(OBJ)\PFactorKWKQStrategy.obj \
$(OBJ)\TimeOfDayStrategy.obj \
$(OBJ)\VoltStrategy.obj \
$(OBJ)\StrategyLoader.obj \
$(OBJ)\StrategyManager.obj \
$(OBJ)\Zone.obj \
$(OBJ)\ZoneLoader.obj \
$(OBJ)\ZoneManager.obj \
$(OBJ)\VoltageRegulator.obj \
$(OBJ)\VoltageRegulatorLoader.obj \
$(OBJ)\VoltageRegulatorManager.obj \
$(OBJ)\GangOperatedVoltageRegulator.obj \
$(OBJ)\PhaseOperatedVoltageRegulator.obj \
$(OBJ)\ccmonitorpoint.obj \
$(OBJ)\cctwowaycbcpoints.obj \
$(OBJ)\ccarea.obj \
$(OBJ)\ccsparea.obj \
$(OBJ)\ccsubstation.obj \
$(OBJ)\ccoperationstats.obj \
$(OBJ)\ccconfirmationstats.obj \
$(OBJ)\ccstatsobject.obj \
$(OBJ)\ccoriginalparent.obj \
$(OBJ)\CapControlPao.obj \
$(OBJ)\CapControlPointDataHandler.obj \
$(OBJ)\PointValueHolder.obj \
$(OBJ)\ccutil.obj \
$(OBJ)\IVVCAlgorithm.obj \
$(OBJ)\IVVCState.obj \
$(OBJ)\CapControlDispatchConnection.obj \
$(OBJ)\CapControlCParms.obj \
$(OBJ)\PointResponseDatabaseDao.obj \
$(OBJ)\PointResponse.obj \
$(OBJ)\PointResponseManager.obj \
$(OBJ)\DynamicCommand.obj \
$(OBJ)\DynamicCommandExecutor.obj \
$(OBJ)\MsgCapControlCommand.obj \
$(OBJ)\MsgCapControlMessage.obj \
$(OBJ)\MsgItemCommand.obj \
$(OBJ)\MsgVerifyInactiveBanks.obj \
$(OBJ)\MsgChangeOpState.obj \
$(OBJ)\MsgCapControlEventLog.obj \
$(OBJ)\MsgObjectMove.obj \
$(OBJ)\MsgBankMove.obj \
$(OBJ)\MsgSubstationBus.obj \
$(OBJ)\MsgCapBankStates.obj \
$(OBJ)\MsgAreas.obj \
$(OBJ)\MsgSpecialAreas.obj \
$(OBJ)\MsgSubstations.obj \
$(OBJ)\MsgVoltageRegulator.obj \
$(OBJ)\ExecChangeOpState.obj \
$(OBJ)\ExecVerification.obj \
$(OBJ)\MsgVerifyBanks.obj \
$(OBJ)\MsgDeleteItem.obj \
$(OBJ)\MsgSystemStatus.obj \
$(OBJ)\MsgCapControlServerResponse.obj \
$(OBJ)\MsgCapControlShutdown.obj \
$(OBJ)\ExecutorFactory.obj

TARGS = capcontrol.exe

ALL:      capcontrol

capcontrol:  $(CAPCTRLTESTOBJS) makeexe.mak

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
        @echo           $(OBJ)\$(@B).obj
        @echo:
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

	@echo:
	@echo Creating Executable $(OBJ)\$(@B).exe
        @echo:
	$(CC) $(CFLAGS) $(INCLPATHS) $(PCHFLAGS) $(RWCPPFLAGS) $(RWLINKFLAGS)  /Fe$(BIN)\$(@B).exe \
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(CAPCTRLBASEOBJS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################
#UPDATE#
test_ccfeeder.obj:	precompiled.h ctitime.h dlldefs.h ccfeeder.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		sorted_vector.h regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccsubstationbusstore.h ccarea.h \
		ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h MsgDeleteItem.h \
		MsgSystemStatus.h MsgCapControlServerResponse.h \
		MsgCapControlShutdown.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccUnitTestUtil.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_dbchg.h configparms.h \
		ccexecutor.h msg_signal.h ctibase.h ctinexus.h ctdpcptrq.h \
		PointDataRequest.h PointDataRequestFactory.h \
		PFactorKWKVarStrategy.h
test_ccsubstationbus.obj:	precompiled.h ctitime.h dlldefs.h \
		ccsubstationbus.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h observe.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h TimeOfDayStrategy.h \
		ccsubstation.h ccarea.h capcontroller.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_dbchg.h \
		configparms.h ccsubstationbusstore.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h MsgCapControlCommand.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h MsgDeleteItem.h \
		MsgSystemStatus.h MsgCapControlServerResponse.h \
		MsgCapControlShutdown.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h ctdpcptrq.h mgr_paosched.h \
		pao_schedule.h pao_event.h dbmemobject.h ccUnitTestUtil.h \
		PointDataRequest.h PointDataRequestFactory.h \
		PFactorKWKVarStrategy.h ExecutorFactory.h MsgVerifyBanks.h
test_controlstrategies.obj:	ControlStrategy.h NoStrategy.h
test_gangoperatedvoltageregulator.obj:	precompiled.h capcontroller.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		configparms.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ctidate.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h TimeOfDayStrategy.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h MsgDeleteItem.h \
		MsgSystemStatus.h MsgCapControlServerResponse.h \
		MsgCapControlShutdown.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h ctdpcptrq.h \
		GangOperatedVoltageRegulator.h
test_ivvcalgorithm.obj:	precompiled.h ctitime.h dlldefs.h \
		ccsubstationbus.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h observe.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h TimeOfDayStrategy.h \
		ccsubstation.h ccarea.h capcontroller.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_dbchg.h \
		configparms.h ccsubstationbusstore.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h MsgCapControlCommand.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h MsgDeleteItem.h \
		MsgSystemStatus.h MsgCapControlServerResponse.h \
		MsgCapControlShutdown.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h ctdpcptrq.h mgr_paosched.h \
		pao_schedule.h pao_event.h dbmemobject.h ccUnitTestUtil.h \
		PointDataRequest.h PointDataRequestFactory.h IVVCStrategy.h \
		IVVCAlgorithm.h IVVCState.h
test_likedaycontrol.obj:	precompiled.h ctitime.h dlldefs.h ccfeeder.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		sorted_vector.h regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccUnitTestUtil.h ccsubstationbusstore.h \
		ccarea.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h MsgDeleteItem.h \
		MsgSystemStatus.h MsgCapControlServerResponse.h \
		MsgCapControlShutdown.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h capcontroller.h \
		DispatchConnection.h CapControlDispatchConnection.h \
		msg_dbchg.h configparms.h ccexecutor.h msg_signal.h ctibase.h \
		ctinexus.h ctdpcptrq.h PointDataRequest.h \
		PointDataRequestFactory.h KVarStrategy.h \
		PFactorKWKVarStrategy.h VoltStrategy.h
test_phaseoperatedvoltageregulator.obj:	precompiled.h capcontroller.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		configparms.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ctidate.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h TimeOfDayStrategy.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgObjectMove.h MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h MsgDeleteItem.h \
		MsgSystemStatus.h MsgCapControlServerResponse.h \
		MsgCapControlShutdown.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h ctdpcptrq.h \
		PhaseOperatedVoltageRegulator.h
test_pointholder.obj:	precompiled.h PointValueHolder.h yukon.h types.h \
		ctidbgmem.h pointtypes.h ctitime.h dlldefs.h msg_pdata.h \
		pointdefs.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
test_strategymanager.obj:	StrategyManager.h readers_writer_lock.h \
		dlldefs.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		ControlStrategy.h StrategyLoader.h KVarStrategy.h \
		NoStrategy.h PFactorKWKVarStrategy.h
test_voltageregulatormanager.obj:	GangOperatedVoltageRegulator.h \
		VoltageRegulator.h yukon.h types.h ctidbgmem.h \
		CapControlPao.h row_reader.h ctitime.h dlldefs.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h boost_time.h boostutil.h \
		UpdatablePao.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		collectable.h LitePoint.h PointValueHolder.h \
		AttributeService.h PointAttribute.h ccid.h ccutil.h \
		devicetypes.h msg_pcrequest.h EventTypes.h \
		VoltageRegulatorLoader.h VoltageRegulatorManager.h \
		readers_writer_lock.h critical_section.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h PointResponse.h PointResponseManager.h Exceptions.h \
		StrategyManager.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h ccstate.h \
		MsgItemCommand.h MsgBankMove.h MsgObjectMove.h \
		MsgCapControlMessage.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		MsgDeleteItem.h MsgSystemStatus.h \
		MsgCapControlServerResponse.h MsgCapControlShutdown.h \
		ccsubstationbusstore.h ccstatsobject.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h
test_zonemanager.obj:	ZoneManager.h Zone.h ccutil.h pointattribute.h \
		yukon.h types.h ctidbgmem.h dlldefs.h devicetypes.h \
		msg_pcrequest.h message.h ctitime.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h readers_writer_lock.h critical_section.h \
		ZoneLoader.h
#ENDUPDATE#

