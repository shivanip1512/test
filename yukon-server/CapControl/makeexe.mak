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
LoadTapChanger.obj \
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
CapControlPointDatahandler.obj \
ccutil.obj \
CapControlDispatchConnection.obj \
CapControlCParms.obj \
PointResponseDatabaseDao.obj \
PointResponse.obj \
PointResponseManager.obj \
DynamicCommand.obj \
DynamicCommandExecutor.obj


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
capcontrolcparms.obj:	yukon.h precompiled.h types.h ctidbgmem.h ccid.h \
		CParms.h dlldefs.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h
capcontroldispatchconnection.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h CapControlDispatchConnection.h \
		DispatchConnection.h connection.h dlldefs.h exchange.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h logger.h thread.h \
		CtiPCPtrQueue.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h MessageListener.h \
		ccsubstationbusstore.h observe.h ccarea.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h
capcontroller.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ccmessage.h ccsubstation.h observe.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		ccsubstationbus.h TimeOfDayStrategy.h ccarea.h ccsparea.h \
		ccstate.h LoadTapChanger.h LitePoint.h PointValueHolder.h \
		UpdatablePao.h msg_signal.h msg_tag.h msg_pcreturn.h \
		msg_dbchg.h configparms.h capcontroller.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h ccsubstationbusstore.h ccid.h \
		ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h mgr_paosched.h pao_schedule.h pao_event.h \
		dbmemobject.h thread_monitor.h smartmap.h \
		thread_register_data.h ccclientconn.h ccclientlistener.h
capcontrolpao.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		CapControlPao.h row_reader.h ctitime.h dlldefs.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h database_reader.h boost_time.h boostutil.h resolvers.h \
		pointtypes.h db_entry_defines.h
capcontrolpointdatahandler.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h msg_pdata.h dlldefs.h pointdefs.h \
		pointtypes.h message.h ctitime.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h MessageListener.h DispatchConnection.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h capcontroller.h \
		CapControlDispatchConnection.h msg_cmd.h configparms.h \
		ccsubstationbusstore.h observe.h ccarea.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h ctidate.h ccoriginalparent.h \
		cccapbank.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h CapControlPao.h PointResponse.h \
		PointResponseManager.h msg_pcrequest.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		regression.h Controllable.h TimeOfDayStrategy.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h ccsubstation.h LoadTapChanger.h \
		LitePoint.h PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		ccutil.h devicetypes.h StrategyLoader.h AttributeService.h \
		PointAttribute.h ccexecutor.h msg_signal.h ctdpcptrq.h \
		ctibase.h ctinexus.h
ccarea.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h msg_signal.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		ccarea.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccid.h capcontroller.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccsparea.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h ctdpcptrq.h \
		ctibase.h ctinexus.h resolvers.h db_entry_defines.h \
		database_writer.h row_writer.h
cccapbank.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h cccapbank.h \
		msg_cmd.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_pdata.h pointdefs.h pointtypes.h \
		ccmonitorpoint.h observe.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h cctwowaycbcpoints.h msg_ptreg.h \
		ccoperationstats.h ccConfirmationStats.h ccoriginalparent.h \
		CapControlPao.h PointResponse.h PointResponseManager.h ccid.h \
		resolvers.h db_entry_defines.h database_writer.h row_writer.h
ccclientconn.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ccclientconn.h ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h sorted_vector.h observe.h ccmessage.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccsubstation.h connection.h exchange.h logger.h \
		thread.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		ccsubstationbus.h TimeOfDayStrategy.h ccarea.h ccsparea.h \
		ccstate.h LoadTapChanger.h LitePoint.h PointValueHolder.h \
		UpdatablePao.h ccexecutor.h msg_signal.h AttributeService.h \
		PointAttribute.h ccutil.h devicetypes.h \
		ccsubstationbusstore.h ccid.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h StrategyLoader.h \
		ctibase.h ctinexus.h capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h configparms.h thread_monitor.h \
		smartmap.h thread_register_data.h
ccclientlistener.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		CtiPCPtrQueue.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h observe.h \
		ccstate.h connection.h exchange.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h logger.h thread.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ccmessage.h ccsubstation.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccoriginalparent.h \
		cccapbank.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h CapControlPao.h PointResponse.h \
		PointResponseManager.h msg_pcrequest.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h LoadTapChanger.h \
		LitePoint.h PointValueHolder.h UpdatablePao.h \
		ccsubstationbusstore.h ccid.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		configparms.h ctibase.h ctinexus.h ccexecutor.h msg_signal.h \
		thread_monitor.h smartmap.h thread_register_data.h
ccconfirmationstats.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		msg_signal.h message.h ctitime.h dlldefs.h collectable.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h pointtypes.h msg_pdata.h pointdefs.h \
		logger.h thread.h CtiPCPtrQueue.h resolvers.h \
		db_entry_defines.h ccconfirmationstats.h msg_cmd.h observe.h \
		ccoperationstats.h
ccexecutor.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		AttributeService.h LitePoint.h dlldefs.h pointtypes.h \
		PointAttribute.h ccclientlistener.h ccclientconn.h \
		ctdpcptrq.h CtiPCPtrQueue.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h observe.h ccstate.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		logger.h thread.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ccexecutor.h ccmessage.h \
		ccsubstation.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		ccsubstationbus.h TimeOfDayStrategy.h ccarea.h ccsparea.h \
		LoadTapChanger.h PointValueHolder.h UpdatablePao.h \
		msg_signal.h ccutil.h devicetypes.h ccsubstationbusstore.h \
		ccid.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h MessageListener.h \
		StrategyLoader.h capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h configparms.h ctibase.h \
		ctinexus.h
ccfeeder.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h msg_signal.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		ccid.h cccapbank.h msg_cmd.h msg_pdata.h pointdefs.h \
		pointtypes.h ccmonitorpoint.h observe.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h cctwowaycbcpoints.h msg_ptreg.h \
		ccoperationstats.h ccConfirmationStats.h ccoriginalparent.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		ccfeeder.h connection.h exchange.h msg_multi.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h capcontroller.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccsparea.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h ctdpcptrq.h \
		ctibase.h ctinexus.h mgr_holiday.h resolvers.h \
		db_entry_defines.h msg_lmcontrolhistory.h tbl_pt_alarm.h \
		dbmemobject.h desolvers.h database_writer.h row_writer.h
ccmain.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ccsubstationbusstore.h observe.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h ccarea.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h cticonnect.h netports.h dsm2err.h words.h sema.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h configparms.h ccexecutor.h \
		msg_signal.h ctdpcptrq.h ctibase.h ctinexus.h ccservice.h \
		ccclientlistener.h ccclientconn.h cservice.h precomp.h \
		Monitor.h CServiceConfig.h rtdb.h hashkey.h hash_functions.h
ccmessage.obj:	yukon.h precompiled.h types.h ctidbgmem.h ccmessage.h \
		ctitime.h dlldefs.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ccsubstation.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		msg_pcrequest.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h regression.h \
		Controllable.h ccsubstationbus.h TimeOfDayStrategy.h ccarea.h \
		ccsparea.h ccstate.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccid.h
ccmonitorpoint.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h ccid.h ccmonitorpoint.h msg_cmd.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		observe.h ctidate.h logger.h thread.h CtiPCPtrQueue.h \
		pointdefs.h resolvers.h pointtypes.h db_entry_defines.h \
		database_writer.h row_writer.h
ccoperationstats.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		msg_signal.h message.h ctitime.h dlldefs.h collectable.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h pointtypes.h msg_pdata.h pointdefs.h \
		logger.h thread.h CtiPCPtrQueue.h resolvers.h \
		db_entry_defines.h ccid.h ccoperationstats.h msg_cmd.h \
		observe.h database_writer.h row_writer.h
ccoriginalparent.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ccoriginalparent.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h sema.h logger.h thread.h CtiPCPtrQueue.h \
		row_reader.h ccid.h database_writer.h database_connection.h \
		row_writer.h
ccserver.obj:	yukon.h precompiled.h types.h ctidbgmem.h ccserver.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		CtiPCPtrQueue.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h observe.h \
		ccstate.h connection.h exchange.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h logger.h thread.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ccmessage.h ccsubstation.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccoriginalparent.h \
		cccapbank.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h CapControlPao.h PointResponse.h \
		PointResponseManager.h msg_pcrequest.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		regression.h Controllable.h ccsubstationbus.h \
		TimeOfDayStrategy.h ccarea.h ccsparea.h LoadTapChanger.h \
		LitePoint.h PointValueHolder.h UpdatablePao.h ctibase.h \
		ctinexus.h configparms.h
ccservice.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		id_capcontrol.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h ccservice.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h CtiPCPtrQueue.h \
		mutex.h guard.h observe.h ccstate.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		logger.h thread.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h cservice.h capcontroller.h \
		DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h msg_cmd.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h ctidate.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		msg_pcrequest.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h regression.h \
		Controllable.h TimeOfDayStrategy.h ccsparea.h ccid.h \
		ccmessage.h ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h msg_signal.h \
		ctibase.h ctinexus.h eventlog.h rtdb.h hashkey.h \
		hash_functions.h thread_monitor.h smartmap.h \
		thread_register_data.h
ccsparea.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h msg_signal.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		ccsparea.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h msg_pcrequest.h msg_cmd.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h ccOperationStats.h ccConfirmationStats.h \
		Controllable.h CapControlPao.h ccid.h cccapbank.h \
		ccmonitorpoint.h ctidate.h cctwowaycbcpoints.h \
		ccoriginalparent.h PointResponse.h PointResponseManager.h \
		capcontroller.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h ccfeeder.h \
		regression.h TimeOfDayStrategy.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h ctdpcptrq.h \
		ctibase.h ctinexus.h resolvers.h db_entry_defines.h \
		database_writer.h row_writer.h
ccstate.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h ccid.h \
		ccstate.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h
ccstatsobject.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ccstatsobject.h msg_cmd.h dlldefs.h message.h ctitime.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h observe.h
ccsubstation.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h msg_signal.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		ccsubstation.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		msg_pcrequest.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h regression.h \
		Controllable.h ccsubstationbus.h TimeOfDayStrategy.h ccid.h \
		capcontroller.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccstate.h \
		ccmessage.h LoadTapChanger.h LitePoint.h PointValueHolder.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h database_writer.h row_writer.h
ccsubstationbus.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h msg_signal.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ccsubstationbus.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h observe.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccoriginalparent.h \
		cccapbank.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h CapControlPao.h PointResponse.h \
		PointResponseManager.h msg_pcrequest.h StrategyManager.h \
		readers_writer_lock.h critical_section.h ControlStrategy.h \
		regression.h Controllable.h TimeOfDayStrategy.h ccid.h \
		capcontroller.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccstate.h \
		ccmessage.h ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h ctdpcptrq.h \
		ctibase.h ctinexus.h resolvers.h db_entry_defines.h \
		mgr_holiday.h mgr_paosched.h pao_schedule.h pao_event.h \
		dbmemobject.h tbl_pt_alarm.h desolvers.h IVVCStrategy.h \
		IVVCAlgorithm.h IVVCState.h PointDataRequest.h \
		PointDataRequestFactory.h database_writer.h row_writer.h
ccsubstationbusstore.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ccsubstationbusstore.h observe.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h ccarea.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h cticonnect.h netports.h dsm2err.h words.h sema.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		desolvers.h db_entry_defines.h resolvers.h ctibase.h \
		ctinexus.h configparms.h msg_dbchg.h msg_signal.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h ccexecutor.h ctdpcptrq.h \
		mgr_holiday.h thread_monitor.h smartmap.h \
		thread_register_data.h database_writer.h row_writer.h \
		ctistring.h PointResponseDao.h DatabaseDaofactory.h \
		PointResponseDatabaseDao.h
cctwowaycbcpoints.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h cctwowaycbcpoints.h msg_cmd.h message.h collectable.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_ptreg.h observe.h cccapbank.h \
		msg_pdata.h pointdefs.h pointtypes.h ccmonitorpoint.h \
		ctidate.h logger.h thread.h CtiPCPtrQueue.h \
		ccoperationstats.h ccConfirmationStats.h ccoriginalparent.h \
		CapControlPao.h PointResponse.h PointResponseManager.h ccid.h \
		resolvers.h db_entry_defines.h database_writer.h row_writer.h
ccutil.obj:	yukon.h precompiled.h types.h ctidbgmem.h ccutil.h \
		devicetypes.h msg_pcrequest.h dlldefs.h message.h ctitime.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h
controllable.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ControlStrategy.h NoStrategy.h Controllable.h CapControlPao.h \
		row_reader.h ctitime.h dlldefs.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h database_reader.h boost_time.h boostutil.h \
		StrategyManager.h readers_writer_lock.h critical_section.h
controlstrategy.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h CtiPCPtrQueue.h ControlStrategy.h ccid.h
ivvcalgorithm.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		IVVCAlgorithm.h ccsubstationbus.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h observe.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h IVVCState.h PointDataRequest.h \
		PointDataRequestFactory.h DispatchConnection.h \
		MessageListener.h IVVCStrategy.h capcontroller.h \
		CapControlDispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h ctibase.h ctinexus.h
ivvcstate.obj:	yukon.h precompiled.h types.h ctidbgmem.h IVVCState.h \
		ctitime.h dlldefs.h cccapbank.h msg_cmd.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_pdata.h pointdefs.h \
		pointtypes.h ccmonitorpoint.h observe.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h cctwowaycbcpoints.h msg_ptreg.h \
		ccoperationstats.h ccConfirmationStats.h ccoriginalparent.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		PointDataRequest.h
ivvcstrategy.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		IVVCStrategy.h IVVCAlgorithm.h ccsubstationbus.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h observe.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h IVVCState.h PointDataRequest.h \
		PointDataRequestFactory.h DispatchConnection.h \
		MessageListener.h ccutil.h devicetypes.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h StrategyLoader.h AttributeService.h \
		PointAttribute.h
kvarstrategy.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		KVarStrategy.h ControlStrategy.h
loadtapchanger.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		LoadTapChanger.h LitePoint.h dlldefs.h pointtypes.h \
		PointValueHolder.h ctitime.h msg_pdata.h pointdefs.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		CapControlPao.h UpdatablePao.h ccid.h
ltcpointholder.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		LtcPointHolder.h
mgr_paosched.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		mgr_paosched.h pao_schedule.h row_reader.h ctitime.h \
		dlldefs.h ctibase.h ctinexus.h netports.h cticonnect.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h sorted_vector.h dsm2err.h words.h \
		logger.h thread.h CtiPCPtrQueue.h pao_event.h dbmemobject.h \
		capcontroller.h dbaccess.h sema.h connection.h exchange.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		configparms.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ctidate.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h ctitokenizer.h thread_monitor.h smartmap.h \
		thread_register_data.h ctistring.h database_writer.h \
		row_writer.h
multivoltstrategy.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		MultiVoltStrategy.h ControlStrategy.h
multivoltvarstrategy.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		MultiVoltVarStrategy.h ControlStrategy.h
nostrategy.obj:	yukon.h precompiled.h types.h ctidbgmem.h NoStrategy.h \
		ControlStrategy.h
pao_event.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		msg_dbchg.h capcontroller.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		configparms.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ctidate.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h ctibase.h ctinexus.h pao_schedule.h pao_event.h \
		dbmemobject.h
pao_schedule.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		msg_dbchg.h capcontroller.h DispatchConnection.h \
		MessageListener.h CapControlDispatchConnection.h msg_cmd.h \
		configparms.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ctidate.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h StrategyLoader.h \
		AttributeService.h PointAttribute.h ccexecutor.h msg_signal.h \
		ctdpcptrq.h ctibase.h ctinexus.h pao_schedule.h pao_event.h \
		dbmemobject.h
pfactorkwkqstrategy.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PFactorKWKQStrategy.h ControlStrategy.h
pfactorkwkvarstrategy.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PFactorKWKVarStrategy.h ControlStrategy.h
pointresponse.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PointResponse.h
pointresponsedatabasedao.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h PointResponse.h
pointresponsemanager.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PointResponseManager.h PointResponse.h ccmonitorpoint.h \
		msg_cmd.h dlldefs.h message.h ctitime.h collectable.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h observe.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h ccid.h
pointvalueholder.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PointValueHolder.h pointtypes.h ctitime.h dlldefs.h \
		msg_pdata.h pointdefs.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
strategyloader.obj:	yukon.h precompiled.h types.h ctidbgmem.h ccid.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h CtiPCPtrQueue.h ControlStrategy.h \
		StrategyLoader.h StrategyManager.h readers_writer_lock.h \
		critical_section.h IVVCStrategy.h IVVCAlgorithm.h \
		ccsubstationbus.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h connection.h exchange.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h observe.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		regression.h Controllable.h TimeOfDayStrategy.h IVVCState.h \
		PointDataRequest.h PointDataRequestFactory.h \
		DispatchConnection.h MessageListener.h KVarStrategy.h \
		MultiVoltStrategy.h MultiVoltVarStrategy.h NoStrategy.h \
		PFactorKWKVarStrategy.h PFactorKWKQStrategy.h VoltStrategy.h
strategymanager.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		StrategyManager.h readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h \
		ControlStrategy.h StrategyLoader.h NoStrategy.h
test_ccexecuter.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ccsubstationbusstore.h observe.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h ccarea.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h cticonnect.h netports.h dsm2err.h words.h sema.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		TimeOfDayStrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		ccexecutor.h msg_signal.h ctdpcptrq.h ccUnitTestUtil.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h configparms.h ctibase.h \
		ctinexus.h PointDataRequest.h PointDataRequestFactory.h
test_ccfeeder.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h ccfeeder.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		ccsubstationbus.h TimeOfDayStrategy.h ccsubstationbusstore.h \
		ccarea.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		ccUnitTestUtil.h capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h configparms.h ccexecutor.h \
		msg_signal.h ctdpcptrq.h ctibase.h ctinexus.h \
		PointDataRequest.h PointDataRequestFactory.h \
		PFactorKWKVarStrategy.h
test_ccsubstationbus.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctitime.h dlldefs.h ccsubstationbus.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h sema.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		msg_pcrequest.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h regression.h \
		Controllable.h TimeOfDayStrategy.h ccsubstation.h ccarea.h \
		capcontroller.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h LoadTapChanger.h LitePoint.h PointValueHolder.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		ccexecutor.h msg_signal.h ctdpcptrq.h ctibase.h ctinexus.h \
		mgr_paosched.h pao_schedule.h pao_event.h dbmemobject.h \
		ccUnitTestUtil.h PointDataRequest.h PointDataRequestFactory.h \
		PFactorKWKVarStrategy.h
test_controlstrategies.obj:	ControlStrategy.h NoStrategy.h
test_ivvcalgorithm.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctitime.h dlldefs.h ccsubstationbus.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h sema.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		CapControlPao.h PointResponse.h PointResponseManager.h \
		msg_pcrequest.h StrategyManager.h readers_writer_lock.h \
		critical_section.h ControlStrategy.h regression.h \
		Controllable.h TimeOfDayStrategy.h ccsubstation.h ccarea.h \
		capcontroller.h DispatchConnection.h MessageListener.h \
		CapControlDispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h LoadTapChanger.h LitePoint.h PointValueHolder.h \
		UpdatablePao.h ccstatsobject.h CapControlPointDataHandler.h \
		PointDataHandler.h PointDataListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		ccexecutor.h msg_signal.h ctdpcptrq.h ctibase.h ctinexus.h \
		mgr_paosched.h pao_schedule.h pao_event.h dbmemobject.h \
		ccUnitTestUtil.h PointDataRequest.h PointDataRequestFactory.h \
		IVVCStrategy.h IVVCAlgorithm.h IVVCState.h
test_likedaycontrol.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctitime.h dlldefs.h ccfeeder.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h sema.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccoriginalparent.h cccapbank.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h CapControlPao.h \
		PointResponse.h PointResponseManager.h msg_pcrequest.h \
		StrategyManager.h readers_writer_lock.h critical_section.h \
		ControlStrategy.h regression.h Controllable.h \
		ccsubstationbus.h TimeOfDayStrategy.h ccUnitTestUtil.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h ccsubstation.h LoadTapChanger.h LitePoint.h \
		PointValueHolder.h UpdatablePao.h ccstatsobject.h \
		CapControlPointDataHandler.h PointDataHandler.h \
		PointDataListener.h MessageListener.h ccutil.h devicetypes.h \
		StrategyLoader.h AttributeService.h PointAttribute.h \
		capcontroller.h DispatchConnection.h \
		CapControlDispatchConnection.h configparms.h ccexecutor.h \
		msg_signal.h ctdpcptrq.h ctibase.h ctinexus.h \
		PointDataRequest.h PointDataRequestFactory.h KVarStrategy.h \
		PFactorKWKVarStrategy.h VoltStrategy.h
test_pointholder.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PointValueHolder.h pointtypes.h ctitime.h dlldefs.h \
		msg_pdata.h pointdefs.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
test_strategymanager.obj:	StrategyManager.h readers_writer_lock.h \
		dlldefs.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h ControlStrategy.h StrategyLoader.h \
		KVarStrategy.h NoStrategy.h PFactorKWKVarStrategy.h
timeofdaystrategy.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		TimeOfDayStrategy.h ControlStrategy.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h
voltstrategy.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		VoltStrategy.h ControlStrategy.h
#ENDUPDATE#

