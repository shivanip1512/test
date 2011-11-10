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
;$(RW)



LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib

BASEOBJS= \
capcontroller.obj \
cccapbank.obj \
ccclientconn.obj \
ccclientlistener.obj \
ccexecutor.obj \
ccfeeder.obj \
ccmain.obj \
ccmessage.obj \
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
GangOperatedVoltageRegulator.obj \
PhaseOperatedVoltageRegulator.obj \
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
MsgChangeOpState.obj \
MsgCapControlEventLog.obj \
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
ExecutorFactory.obj

TARGS = capcontrol.exe


CAPCONTROL_FULLBUILD = $[Filename,$(OBJ),CapControlFullBuild,target]


ALL:          $(TARGS)


$(CAPCONTROL_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(BASEOBJS)]



capcontrol.exe: $(CAPCONTROL_FULLBUILD) $(BASEOBJS) Makefile
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOST_LIBS) $(LINKFLAGS)
              @echo:
	      mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
              @%cd $(CWD)
              @echo Done building Target ..\$@
              @echo:

copy:       $(TARGS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)

deps:
                scandeps -Output makeexe.mak *.cpp


clean:
        -del \
*.pdb \
$(OBJ)\*.obj \
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
               $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
#UPDATE#
capcontrolcparms.obj:	precompiled.h ccid.h CParms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h
capcontroldispatchconnection.obj:	precompiled.h \
		CapControlDispatchConnection.h DispatchConnection.h \
		connection.h dlldefs.h exchange.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		MessageListener.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		sorted_vector.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h
capcontroller.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h sema.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		sorted_vector.h regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h ccstate.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccid.h msg_signal.h \
		msg_tag.h msg_pcreturn.h msg_dbchg.h configparms.h \
		capcontroller.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h ccsubstationbusstore.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h ctibase.h \
		ctinexus.h ctdpcptrq.h resolvers.h db_entry_defines.h \
		mgr_paosched.h pao_schedule.h pao_event.h dbmemobject.h \
		thread_monitor.h smartmap.h thread_register_data.h \
		ThreadStatusKeeper.h ccclientconn.h ccclientlistener.h
capcontrolpao.obj:	precompiled.h CapControlPao.h yukon.h types.h \
		ctidbgmem.h row_reader.h ctitime.h dlldefs.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h boost_time.h boostutil.h resolvers.h \
		pointtypes.h db_entry_defines.h
capcontrolpointdatahandler.obj:	precompiled.h \
		CapControlPointDataHandler.h yukon.h types.h ctidbgmem.h \
		PointDataHandler.h PointDataListener.h msg_pdata.h dlldefs.h \
		pointdefs.h pointtypes.h message.h ctitime.h collectable.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h MessageListener.h DispatchConnection.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h msg_multi.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h capcontroller.h \
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
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccstatsobject.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h \
		ccexecutor.h msg_signal.h ctibase.h ctinexus.h ctdpcptrq.h
ccarea.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h sema.h msg_signal.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		ccarea.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h TimeOfDayStrategy.h ccid.h \
		capcontroller.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_dbchg.h configparms.h \
		ccsubstationbusstore.h ccsparea.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h ctibase.h \
		ctinexus.h ctdpcptrq.h resolvers.h db_entry_defines.h
cccapbank.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h sema.h cccapbank.h \
		msg_cmd.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_pdata.h pointdefs.h pointtypes.h \
		ccmonitorpoint.h observe.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h cctwowaycbcpoints.h msg_ptreg.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h ccoriginalparent.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h ccid.h resolvers.h \
		db_entry_defines.h database_writer.h row_writer.h \
		PointResponseDao.h DatabaseDaoFactory.h \
		PointResponseDatabaseDao.h
ccclientconn.obj:	precompiled.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		CtiPCPtrQueue.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h observe.h ccmessage.h \
		MsgCapControlCommand.h message.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccsubstation.h connection.h exchange.h logger.h \
		thread.h string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		sorted_vector.h regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h ccstate.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccid.h ccexecutor.h \
		msg_signal.h VoltageRegulatorManager.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccsubstationbusstore.h \
		ccstatsobject.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ctibase.h ctinexus.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_dbchg.h configparms.h \
		thread_monitor.h smartmap.h thread_register_data.h
ccclientlistener.obj:	precompiled.h ccclientlistener.h ccclientconn.h \
		ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h observe.h ccstate.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		dsm2err.h words.h optional.h logger.h thread.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccid.h ccsubstationbusstore.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h configparms.h ctibase.h \
		ctinexus.h ccexecutor.h msg_signal.h thread_monitor.h \
		smartmap.h thread_register_data.h ThreadStatusKeeper.h
ccconfirmationstats.obj:	precompiled.h msg_signal.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h types.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h pointtypes.h msg_pdata.h pointdefs.h logger.h \
		thread.h CtiPCPtrQueue.h resolvers.h db_entry_defines.h \
		ccconfirmationstats.h msg_cmd.h observe.h ccoperationstats.h
ccexecutor.obj:	precompiled.h AttributeService.h LitePoint.h dlldefs.h \
		pointtypes.h PointAttribute.h yukon.h types.h ctidbgmem.h \
		DynamicCommandExecutor.h DynamicCommand.h ccexecutor.h \
		ccmessage.h MsgCapControlCommand.h message.h ctitime.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ccsubstation.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h observe.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccutil.h devicetypes.h \
		msg_pcrequest.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h PointValueHolder.h ccoperationstats.h \
		ccConfirmationStats.h cctypes.h CapControlPao.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		ccsubstationbus.h TimeOfDayStrategy.h ccarea.h ccsparea.h \
		ccstate.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccid.h msg_signal.h \
		VoltageRegulatorManager.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h \
		ccsubstationbusstore.h ccstatsobject.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_dbchg.h configparms.h \
		ctibase.h ctinexus.h
ccfeeder.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h sema.h msg_signal.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		ccid.h cccapbank.h msg_cmd.h msg_pdata.h pointdefs.h \
		pointtypes.h ccmonitorpoint.h observe.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h cctwowaycbcpoints.h msg_ptreg.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h ccoriginalparent.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h ccfeeder.h connection.h \
		exchange.h string_utility.h msg_multi.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		sorted_vector.h regression.h Controllable.h capcontroller.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_dbchg.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccsparea.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h ctibase.h \
		ctinexus.h ctdpcptrq.h mgr_holiday.h resolvers.h \
		db_entry_defines.h msg_lmcontrolhistory.h tbl_pt_alarm.h \
		dbmemobject.h desolvers.h
ccmain.obj:	precompiled.h ccsubstationbusstore.h observe.h types.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h ccarea.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h mutex.h guard.h dsm2err.h \
		words.h optional.h sema.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		sorted_vector.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_dbchg.h configparms.h \
		ccexecutor.h msg_signal.h ctibase.h ctinexus.h ctdpcptrq.h \
		ccservice.h ccclientlistener.h ccclientconn.h cservice.h \
		precomp.h Monitor.h CServiceConfig.h rtdb.h hashkey.h \
		hash_functions.h
ccmessage.obj:	precompiled.h ccmessage.h MsgCapControlCommand.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h types.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ccsubstation.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
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
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h ccstate.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccid.h
ccmonitorpoint.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h sema.h \
		ccid.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ccmonitorpoint.h msg_cmd.h observe.h \
		ctidate.h logger.h thread.h CtiPCPtrQueue.h pointdefs.h \
		resolvers.h pointtypes.h db_entry_defines.h database_writer.h \
		row_writer.h
ccoperationstats.obj:	precompiled.h msg_signal.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h pointtypes.h msg_pdata.h pointdefs.h logger.h \
		thread.h CtiPCPtrQueue.h resolvers.h db_entry_defines.h \
		ccid.h ccoperationstats.h msg_cmd.h observe.h \
		database_writer.h row_writer.h
ccoriginalparent.obj:	precompiled.h ccoriginalparent.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h sema.h logger.h thread.h CtiPCPtrQueue.h \
		row_reader.h ccid.h database_writer.h database_connection.h \
		row_writer.h
ccserver.obj:	precompiled.h ccserver.h ccclientlistener.h \
		ccclientconn.h ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h observe.h ccstate.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		dsm2err.h words.h optional.h logger.h thread.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccid.h ctibase.h ctinexus.h configparms.h
ccservice.obj:	precompiled.h id_capcontrol.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		ccservice.h ccclientlistener.h ccclientconn.h ctdpcptrq.h \
		CtiPCPtrQueue.h mutex.h guard.h observe.h ccstate.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h logger.h \
		thread.h string_utility.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h cservice.h capcontroller.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h msg_dbchg.h \
		configparms.h ccsubstationbusstore.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ctidate.h \
		ccutil.h pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h TimeOfDayStrategy.h ccsparea.h \
		ccid.h ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h eventlog.h rtdb.h hashkey.h \
		hash_functions.h thread_monitor.h smartmap.h \
		thread_register_data.h
ccsparea.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h sema.h msg_signal.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		ccsparea.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h msg_pcrequest.h \
		msg_cmd.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h cctypes.h \
		ccOperationStats.h ccConfirmationStats.h Controllable.h \
		CapControlPao.h ccid.h cccapbank.h ccmonitorpoint.h ctidate.h \
		ccutil.h pointattribute.h devicetypes.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoriginalparent.h PointResponse.h PointResponseManager.h \
		Exceptions.h capcontroller.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_dbchg.h \
		configparms.h ccsubstationbusstore.h ccarea.h \
		ccsubstationbus.h ccfeeder.h sorted_vector.h regression.h \
		TimeOfDayStrategy.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h ctibase.h \
		ctinexus.h ctdpcptrq.h resolvers.h db_entry_defines.h
ccstate.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h sema.h ccid.h ccstate.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h
ccstatsobject.obj:	precompiled.h ccstatsobject.h msg_cmd.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h types.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h observe.h
ccsubstation.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h sema.h \
		msg_signal.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ccsubstation.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
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
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccid.h capcontroller.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_dbchg.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccstate.h \
		ccmessage.h MsgCapControlCommand.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h ctibase.h \
		ctinexus.h ctdpcptrq.h resolvers.h db_entry_defines.h
ccsubstationbus.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h sema.h \
		msg_signal.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ccsubstationbus.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
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
		regression.h Controllable.h TimeOfDayStrategy.h ccid.h \
		capcontroller.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_dbchg.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccstate.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h ctibase.h \
		ctinexus.h ctdpcptrq.h resolvers.h db_entry_defines.h \
		mgr_holiday.h mgr_paosched.h pao_schedule.h pao_event.h \
		dbmemobject.h tbl_pt_alarm.h desolvers.h IVVCStrategy.h \
		IVVCAlgorithm.h IVVCState.h PointDataRequest.h \
		PointDataRequestFactory.h
ccsubstationbusstore.obj:	precompiled.h ccsubstationbusstore.h \
		observe.h types.h dlldefs.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h ccarea.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h \
		guard.h dsm2err.h words.h optional.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		sorted_vector.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h \
		desolvers.h resolvers.h db_entry_defines.h ctibase.h \
		ctinexus.h configparms.h msg_dbchg.h msg_signal.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h ccexecutor.h ctdpcptrq.h \
		mgr_holiday.h thread_monitor.h smartmap.h \
		thread_register_data.h ctistring.h ThreadStatusKeeper.h
cctwowaycbcpoints.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h sema.h \
		cctwowaycbcpoints.h msg_cmd.h message.h collectable.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_ptreg.h observe.h \
		AttributeService.h LitePoint.h pointtypes.h PointAttribute.h \
		PointValueHolder.h msg_pdata.h pointdefs.h cccapbank.h \
		ccmonitorpoint.h ctidate.h logger.h thread.h CtiPCPtrQueue.h \
		ccutil.h devicetypes.h msg_pcrequest.h ccoperationstats.h \
		ccConfirmationStats.h ccoriginalparent.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h ccid.h resolvers.h db_entry_defines.h \
		database_writer.h row_writer.h
ccutil.obj:	precompiled.h ccutil.h pointattribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h devicetypes.h msg_pcrequest.h message.h \
		ctitime.h collectable.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h pointdefs.h cccapbank.h \
		msg_cmd.h msg_pdata.h pointtypes.h ccmonitorpoint.h observe.h \
		ctidate.h logger.h thread.h CtiPCPtrQueue.h \
		cctwowaycbcpoints.h msg_ptreg.h AttributeService.h \
		LitePoint.h PointValueHolder.h ccoperationstats.h \
		ccConfirmationStats.h ccoriginalparent.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h
controllable.obj:	precompiled.h ControlStrategy.h NoStrategy.h \
		Controllable.h CapControlPao.h yukon.h types.h ctidbgmem.h \
		row_reader.h ctitime.h dlldefs.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h boost_time.h boostutil.h \
		StrategyManager.h readers_writer_lock.h critical_section.h
controlstrategy.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h ControlStrategy.h ccid.h
dynamiccommand.obj:	precompiled.h DynamicCommand.h ccid.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h
dynamiccommandexecutor.obj:	precompiled.h DynamicCommandExecutor.h \
		DynamicCommand.h ccexecutor.h ccmessage.h \
		MsgCapControlCommand.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccsubstation.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h observe.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h ccstate.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccid.h msg_signal.h \
		VoltageRegulatorManager.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		ccsubstationbusstore.h ccstatsobject.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h
gangoperatedvoltageregulator.obj:	precompiled.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		GangOperatedVoltageRegulator.h VoltageRegulator.h yukon.h \
		ctidbgmem.h CapControlPao.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h boost_time.h boostutil.h UpdatablePao.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h collectable.h \
		LitePoint.h PointValueHolder.h AttributeService.h \
		PointAttribute.h ccid.h ccutil.h devicetypes.h \
		msg_pcrequest.h EventTypes.h capcontroller.h connection.h \
		exchange.h string_utility.h msg_multi.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h configparms.h ccsubstationbusstore.h observe.h \
		ccarea.h ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		ctidate.h ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		MsgBankMove.h MsgCapControlMessage.h MsgObjectMove.h \
		MsgSubstationBus.h MsgCapControlEventLog.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h ctdpcptrq.h
ivvcalgorithm.obj:	precompiled.h IVVCAlgorithm.h ccsubstationbus.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
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
		regression.h Controllable.h TimeOfDayStrategy.h IVVCState.h \
		PointDataRequest.h PointDataRequestFactory.h \
		DispatchConnection.h MessageListener.h ZoneManager.h Zone.h \
		IVVCStrategy.h capcontroller.h CapControlDispatchConnection.h \
		msg_dbchg.h configparms.h ccsubstationbusstore.h ccarea.h \
		ccsparea.h ccid.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h ctdpcptrq.h
ivvcstate.obj:	precompiled.h IVVCState.h yukon.h types.h ctidbgmem.h \
		ctitime.h dlldefs.h cccapbank.h msg_cmd.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_pdata.h pointdefs.h pointtypes.h \
		ccmonitorpoint.h observe.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h cctwowaycbcpoints.h msg_ptreg.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h ccoriginalparent.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h PointDataRequest.h
ivvcstrategy.obj:	precompiled.h IVVCStrategy.h IVVCAlgorithm.h \
		ccsubstationbus.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
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
		regression.h Controllable.h TimeOfDayStrategy.h IVVCState.h \
		PointDataRequest.h PointDataRequestFactory.h \
		DispatchConnection.h MessageListener.h ZoneManager.h Zone.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h
kvarstrategy.obj:	precompiled.h KVarStrategy.h ControlStrategy.h
mgr_paosched.obj:	precompiled.h mgr_paosched.h pao_schedule.h \
		row_reader.h ctitime.h dlldefs.h ctibase.h ctinexus.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h dllbase.h \
		dsm2.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h pao_event.h dbmemobject.h \
		capcontroller.h dbaccess.h sema.h connection.h exchange.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h DispatchConnection.h MessageListener.h \
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
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h ctitokenizer.h thread_monitor.h smartmap.h \
		thread_register_data.h ctistring.h ThreadStatusKeeper.h
msgareas.obj:	precompiled.h MsgAreas.h MsgCapControlMessage.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h types.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ccarea.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h observe.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h AttributeService.h LitePoint.h \
		PointValueHolder.h ccoperationstats.h ccConfirmationStats.h \
		cctypes.h CapControlPao.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		sorted_vector.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccid.h
msgbankmove.obj:	precompiled.h MsgBankMove.h MsgCapControlMessage.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h types.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ccid.h
msgcapbankstates.obj:	precompiled.h MsgCapBankStates.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccstate.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h observe.h ccid.h
msgcapcontrolcommand.obj:	precompiled.h MsgCapControlCommand.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h types.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ccid.h
msgcapcontroleventlog.obj:	precompiled.h MsgCapControlEventLog.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccid.h pointtypes.h
msgcapcontrolmessage.obj:	precompiled.h MsgCapControlMessage.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h types.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h
msgchangeopstate.obj:	precompiled.h MsgChangeOpState.h \
		MsgItemCommand.h MsgCapControlCommand.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccid.h
msgitemcommand.obj:	precompiled.h MsgItemCommand.h \
		MsgCapControlCommand.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccid.h
msgobjectmove.obj:	precompiled.h MsgObjectMove.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccid.h
msgspecialareas.obj:	precompiled.h MsgSpecialAreas.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccsparea.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h observe.h \
		msg_pcrequest.h msg_cmd.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		cctypes.h ccOperationStats.h ccConfirmationStats.h \
		Controllable.h CapControlPao.h ccid.h
msgsubstationbus.obj:	precompiled.h MsgSubstationBus.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccsubstationbus.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h \
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
		regression.h Controllable.h TimeOfDayStrategy.h ccid.h
msgsubstations.obj:	precompiled.h MsgSubstations.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccsubstation.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h observe.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccid.h
msgverifyinactivebanks.obj:	precompiled.h MsgVerifyInactiveBanks.h \
		MsgItemCommand.h MsgCapControlCommand.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccid.h
msgvoltageregulator.obj:	precompiled.h MsgVoltageRegulator.h \
		MsgCapControlMessage.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h VoltageRegulator.h CapControlPao.h UpdatablePao.h \
		msg_pdata.h pointdefs.h pointtypes.h LitePoint.h \
		PointValueHolder.h AttributeService.h PointAttribute.h ccid.h \
		ccutil.h devicetypes.h msg_pcrequest.h EventTypes.h
multivoltstrategy.obj:	precompiled.h MultiVoltStrategy.h \
		ControlStrategy.h
multivoltvarstrategy.obj:	precompiled.h MultiVoltVarStrategy.h \
		ControlStrategy.h
nostrategy.obj:	precompiled.h NoStrategy.h ControlStrategy.h
pao_event.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		msg_dbchg.h capcontroller.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
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
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h ctdpcptrq.h pao_schedule.h pao_event.h \
		dbmemobject.h
pao_schedule.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h sema.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h msg_dbchg.h capcontroller.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h configparms.h \
		ccsubstationbusstore.h observe.h ccarea.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h ctidate.h ccutil.h \
		pointattribute.h devicetypes.h msg_pcrequest.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		AttributeService.h LitePoint.h PointValueHolder.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		Exceptions.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h TimeOfDayStrategy.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h MsgCapControlCommand.h \
		ccsubstation.h MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h ctdpcptrq.h pao_schedule.h pao_event.h \
		dbmemobject.h
pfactorkwkqstrategy.obj:	precompiled.h PFactorKWKQStrategy.h \
		ControlStrategy.h
pfactorkwkvarstrategy.obj:	precompiled.h PFactorKWKVarStrategy.h \
		ControlStrategy.h
phaseoperatedvoltageregulator.obj:	precompiled.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		PhaseOperatedVoltageRegulator.h VoltageRegulator.h yukon.h \
		ctidbgmem.h CapControlPao.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h boost_time.h boostutil.h UpdatablePao.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h collectable.h \
		LitePoint.h PointValueHolder.h AttributeService.h \
		PointAttribute.h ccid.h ccutil.h devicetypes.h \
		msg_pcrequest.h EventTypes.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccoriginalparent.h \
		cccapbank.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h cctypes.h PointResponse.h \
		PointResponseManager.h Exceptions.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		sorted_vector.h regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h ccstate.h \
		MsgItemCommand.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h
pointresponse.obj:	precompiled.h PointResponse.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h ccid.h
pointresponsedatabasedao.obj:	precompiled.h PointResponseDatabaseDao.h \
		PointResponseDao.h PointResponse.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		database_writer.h row_writer.h database_util.h logger.h \
		thread.h CtiPCPtrQueue.h ccid.h
pointresponsemanager.obj:	precompiled.h PointResponseManager.h \
		Exceptions.h PointResponse.h ccmonitorpoint.h msg_cmd.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h types.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h observe.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h ccutil.h pointattribute.h \
		devicetypes.h msg_pcrequest.h
pointvalueholder.obj:	precompiled.h PointValueHolder.h yukon.h types.h \
		ctidbgmem.h pointtypes.h ctitime.h dlldefs.h msg_pdata.h \
		pointdefs.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
strategyloader.obj:	precompiled.h ccid.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h ControlStrategy.h \
		StrategyLoader.h StrategyManager.h readers_writer_lock.h \
		critical_section.h IVVCStrategy.h IVVCAlgorithm.h \
		ccsubstationbus.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		sema.h connection.h exchange.h string_utility.h message.h \
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
		Exceptions.h sorted_vector.h regression.h Controllable.h \
		TimeOfDayStrategy.h IVVCState.h PointDataRequest.h \
		PointDataRequestFactory.h DispatchConnection.h \
		MessageListener.h ZoneManager.h Zone.h KVarStrategy.h \
		MultiVoltStrategy.h MultiVoltVarStrategy.h NoStrategy.h \
		PFactorKWKVarStrategy.h PFactorKWKQStrategy.h VoltStrategy.h
strategymanager.obj:	precompiled.h StrategyManager.h \
		readers_writer_lock.h dlldefs.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h ControlStrategy.h StrategyLoader.h NoStrategy.h
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
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h \
		ccUnitTestUtil.h capcontroller.h DispatchConnection.h \
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
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h ctdpcptrq.h mgr_paosched.h \
		pao_schedule.h pao_event.h dbmemobject.h ccUnitTestUtil.h \
		PointDataRequest.h PointDataRequestFactory.h \
		PFactorKWKVarStrategy.h
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
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccstatsobject.h \
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
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h StrategyLoader.h \
		ZoneManager.h Zone.h ZoneLoader.h VoltageRegulatorManager.h \
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
		EventTypes.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h VoltageRegulator.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h ZoneManager.h Zone.h ZoneLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h msg_dbchg.h configparms.h \
		ccexecutor.h msg_signal.h ctibase.h ctinexus.h ctdpcptrq.h \
		PointDataRequest.h PointDataRequestFactory.h KVarStrategy.h \
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
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccstatsobject.h \
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
		MsgItemCommand.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h ccsubstationbusstore.h \
		ccstatsobject.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h
test_zonemanager.obj:	ZoneManager.h Zone.h ccutil.h pointattribute.h \
		yukon.h types.h ctidbgmem.h dlldefs.h devicetypes.h \
		msg_pcrequest.h message.h ctitime.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h readers_writer_lock.h critical_section.h \
		ZoneLoader.h
timeofdaystrategy.obj:	precompiled.h TimeOfDayStrategy.h \
		ControlStrategy.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h
voltageregulator.obj:	precompiled.h VoltageRegulator.h yukon.h types.h \
		ctidbgmem.h CapControlPao.h row_reader.h ctitime.h dlldefs.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h boost_time.h boostutil.h \
		UpdatablePao.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		collectable.h LitePoint.h PointValueHolder.h \
		AttributeService.h PointAttribute.h ccid.h ccutil.h \
		devicetypes.h msg_pcrequest.h EventTypes.h logger.h thread.h \
		CtiPCPtrQueue.h msg_signal.h Capcontroller.h connection.h \
		exchange.h string_utility.h msg_multi.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		msg_dbchg.h configparms.h ccsubstationbusstore.h observe.h \
		ccarea.h ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		ctidate.h ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h sorted_vector.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccstate.h ccmessage.h \
		MsgCapControlCommand.h ccsubstation.h MsgItemCommand.h \
		MsgBankMove.h MsgCapControlMessage.h MsgObjectMove.h \
		MsgSubstationBus.h MsgCapControlEventLog.h MsgCapBankStates.h \
		MsgAreas.h MsgSpecialAreas.h MsgSubstations.h \
		MsgVoltageRegulator.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h ZoneManager.h Zone.h \
		ZoneLoader.h VoltageRegulatorManager.h \
		VoltageRegulatorLoader.h DatabaseDaoFactory.h \
		PointResponseDao.h PointResponseDatabaseDao.h \
		database_writer.h row_writer.h ccexecutor.h ctibase.h \
		ctinexus.h ctdpcptrq.h
voltageregulatorloader.obj:	precompiled.h ccid.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h \
		VoltageRegulatorLoader.h VoltageRegulatorManager.h \
		readers_writer_lock.h critical_section.h VoltageRegulator.h \
		CapControlPao.h rwutil.h boost_time.h boostutil.h \
		UpdatablePao.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		collectable.h LitePoint.h PointValueHolder.h \
		AttributeService.h PointAttribute.h ccutil.h devicetypes.h \
		msg_pcrequest.h EventTypes.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h observe.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h cctypes.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		StrategyManager.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h ccstate.h \
		MsgItemCommand.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h \
		GangOperatedVoltageRegulator.h \
		PhaseOperatedVoltageRegulator.h
voltageregulatormanager.obj:	precompiled.h VoltageRegulatorManager.h \
		readers_writer_lock.h dlldefs.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h VoltageRegulator.h yukon.h ctidbgmem.h \
		CapControlPao.h row_reader.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		boost_time.h boostutil.h UpdatablePao.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h collectable.h LitePoint.h \
		PointValueHolder.h AttributeService.h PointAttribute.h ccid.h \
		ccutil.h devicetypes.h msg_pcrequest.h EventTypes.h \
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
		MsgItemCommand.h MsgBankMove.h MsgCapControlMessage.h \
		MsgObjectMove.h MsgSubstationBus.h MsgCapControlEventLog.h \
		MsgCapBankStates.h MsgAreas.h MsgSpecialAreas.h \
		MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulatorLoader.h
voltstrategy.obj:	precompiled.h VoltStrategy.h ControlStrategy.h
zone.obj:	precompiled.h Zone.h ccutil.h pointattribute.h yukon.h \
		types.h ctidbgmem.h dlldefs.h devicetypes.h msg_pcrequest.h \
		message.h ctitime.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
zoneloader.obj:	precompiled.h ccid.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h ZoneLoader.h \
		ZoneManager.h Zone.h ccutil.h pointattribute.h devicetypes.h \
		msg_pcrequest.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h readers_writer_lock.h critical_section.h \
		ccsubstationbusstore.h observe.h ccarea.h connection.h \
		exchange.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccoriginalparent.h \
		cccapbank.h cctwowaycbcpoints.h AttributeService.h \
		LitePoint.h PointValueHolder.h ccoperationstats.h \
		ccConfirmationStats.h cctypes.h CapControlPao.h \
		PointResponse.h PointResponseManager.h Exceptions.h \
		StrategyManager.h ControlStrategy.h sorted_vector.h \
		regression.h Controllable.h TimeOfDayStrategy.h ccsparea.h \
		ccstate.h ccmessage.h MsgCapControlCommand.h ccsubstation.h \
		MsgItemCommand.h EventTypes.h MsgBankMove.h \
		MsgCapControlMessage.h MsgObjectMove.h MsgSubstationBus.h \
		MsgCapControlEventLog.h MsgCapBankStates.h MsgAreas.h \
		MsgSpecialAreas.h MsgSubstations.h MsgVoltageRegulator.h \
		VoltageRegulator.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h StrategyLoader.h \
		VoltageRegulatorManager.h VoltageRegulatorLoader.h \
		DatabaseDaoFactory.h PointResponseDao.h \
		PointResponseDatabaseDao.h database_writer.h row_writer.h
zonemanager.obj:	precompiled.h ZoneManager.h Zone.h ccutil.h \
		pointattribute.h yukon.h types.h ctidbgmem.h dlldefs.h \
		devicetypes.h msg_pcrequest.h message.h ctitime.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h readers_writer_lock.h \
		critical_section.h ZoneLoader.h
#ENDUPDATE#

