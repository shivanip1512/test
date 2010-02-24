# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

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


.PATH.cpp = .
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
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib

CAPCTRLTESTOBJS= \
test_ControlStrategies.obj \
test_StrategyManager.obj \
test_ccsubstationbus.obj \
test_ccFeeder.obj \
test_likeDayControl.obj \
test_pointHolder.obj \
test_IVVCAlgorithm.obj \
test_ccExecuter.obj

CAPCTRLBASEOBJS= \
$(OBJ)\ccservice.obj \
$(OBJ)\capcontroller.obj \
$(OBJ)\cccapbank.obj \
$(OBJ)\ccclientconn.obj \
$(OBJ)\ccclientlistener.obj \
$(OBJ)\ccexecutor.obj \
$(OBJ)\ccfeeder.obj \
$(OBJ)\ccmessage.obj \
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
$(OBJ)\ccmonitorpoint.obj \
$(OBJ)\ccpointresponse.obj \
$(OBJ)\cctwowaycbcpoints.obj \
$(OBJ)\ccarea.obj \
$(OBJ)\ccsparea.obj \
$(OBJ)\ccsubstation.obj \
$(OBJ)\ccoperationstats.obj \
$(OBJ)\ccconfirmationstats.obj \
$(OBJ)\ccstatsobject.obj \
$(OBJ)\ccoriginalparent.obj \
$(OBJ)\CapControlPao.obj \
$(OBJ)\LoadTapChanger.obj \
$(OBJ)\CapControlPointDataHandler.obj \
$(OBJ)\PointValueHolder.obj \
$(OBJ)\ccutil.obj \
$(OBJ)\IVVCAlgorithm.obj \
$(OBJ)\IVVCState.obj \
$(OBJ)\CapControlDispatchConnection.obj

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
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(CAPCTRLBASEOBJS) $(BOOSTTESTLIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################
#UPDATE#
test_ccexecuter.obj:	yukon.h precompiled.h ctidbgmem.h \
		ccsubstationbusstore.h observe.h types.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h ccarea.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h clrdump.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h CapControlPao.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h ccstatsobject.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		ccexecutor.h msg_signal.h ctdpcptrq.h ccUnitTestUtil.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h configparms.h ctibase.h \
		ctinexus.h
test_ccfeeder.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ccfeeder.h dbaccess.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h message.h collectable.h rwutil.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccoriginalparent.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		CapControlPao.h msg_pcrequest.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccsubstationbusstore.h ccarea.h \
		ccsparea.h ccid.h ccstate.h ccmessage.h ccsubstation.h \
		ccstatsobject.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		ccUnitTestUtil.h capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h configparms.h ccexecutor.h \
		msg_signal.h ctdpcptrq.h ctibase.h ctinexus.h \
		PFactorKWKVarStrategy.h
test_ccsubstationbus.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h message.h collectable.h rwutil.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h CapControlPao.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsubstation.h ccarea.h capcontroller.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h ccstatsobject.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h ctibase.h ctinexus.h mgr_paosched.h \
		pao_schedule.h pao_event.h dbmemobject.h ccUnitTestUtil.h \
		PFactorKWKVarStrategy.h
test_controlstrategies.obj:	ControlStrategy.h NoStrategy.h
test_ivvcalgorithm.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h message.h collectable.h rwutil.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h CapControlPao.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsubstation.h ccarea.h capcontroller.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h ccstatsobject.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h ctibase.h ctinexus.h mgr_paosched.h \
		pao_schedule.h pao_event.h dbmemobject.h ccUnitTestUtil.h \
		IVVCStrategy.h IVVCAlgorithm.h IVVCState.h PointDataRequest.h \
		PointDataRequestFactory.h
test_likedaycontrol.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ccfeeder.h dbaccess.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h message.h collectable.h rwutil.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccoriginalparent.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		CapControlPao.h msg_pcrequest.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccUnitTestUtil.h ccsubstationbusstore.h \
		ccarea.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h ccstatsobject.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h configparms.h ccexecutor.h \
		msg_signal.h ctdpcptrq.h ctibase.h ctinexus.h KVarStrategy.h \
		PFactorKWKVarStrategy.h VoltStrategy.h
test_pointholder.obj:	yukon.h precompiled.h ctidbgmem.h \
		PointValueHolder.h pointtypes.h msg_pdata.h dlldefs.h \
		pointdefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h
test_strategymanager.obj:	StrategyManager.h readers_writer_lock.h \
		dlldefs.h critical_section.h guard.h numstr.h clrdump.h \
		ControlStrategy.h StrategyLoader.h KVarStrategy.h \
		NoStrategy.h PFactorKWKVarStrategy.h
#ENDUPDATE#

