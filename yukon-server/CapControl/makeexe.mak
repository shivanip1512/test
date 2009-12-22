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
ccstrategy.obj \
ccmonitorpoint.obj \
ccpointresponse.obj \
cctwowaycbcpoints.obj \
LtcPointHolder.obj \
PointOffsetValueHolder.obj \
ccarea.obj \
ccsparea.obj \
ccsubstation.obj \
ccoperationstats.obj \
ccconfirmationstats.obj \
ccstatsobject.obj \
ccoriginalparent.obj \
CapControlPao.obj \
LoadTapChanger.obj \
ccutil.obj


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
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOSTLIBS) $(LINKFLAGS)
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
capcontroller.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h connection.h exchange.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ccmessage.h \
		ccsubstation.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h msg_pcrequest.h ccstrategy.h \
		regression.h ccsubstationbus.h ccarea.h ccsparea.h ccstate.h \
		msg_signal.h msg_tag.h msg_pcreturn.h msg_dbchg.h \
		configparms.h capcontroller.h DispatchConnection.h \
		ccsubstationbusstore.h ccid.h ccstatsobject.h \
		LoadTapChanger.h LtcPointHolder.h PointOffsetValueHolder.h \
		CapControlPao.h UpdatablePao.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h ccexecutor.h \
		ctdpcptrq.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h mgr_paosched.h pao_schedule.h pao_event.h \
		dbmemobject.h thread_monitor.h smartmap.h \
		readers_writer_lock.h critical_section.h \
		thread_register_data.h ccclientconn.h ccclientlistener.h
capcontrolpao.obj:	yukon.h precompiled.h ctidbgmem.h CapControlPao.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h resolvers.h pointtypes.h db_entry_defines.h
ccarea.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		msg_signal.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		ccarea.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		msg_pcrequest.h ccstrategy.h regression.h ccid.h \
		capcontroller.h DispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccsparea.h ccstate.h ccmessage.h \
		ccsubstation.h ccstatsobject.h LoadTapChanger.h \
		LtcPointHolder.h PointOffsetValueHolder.h CapControlPao.h \
		UpdatablePao.h PointDataHandler.h PointDataListener.h \
		ccutil.h devicetypes.h ccexecutor.h ctdpcptrq.h ctibase.h \
		ctinexus.h resolvers.h db_entry_defines.h
cccapbank.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		cccapbank.h msg_cmd.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h boostutil.h utility.h queues.h \
		sorted_vector.h ccpointresponse.h observe.h ccmonitorpoint.h \
		ctidate.h logger.h thread.h CtiPCPtrQueue.h \
		cctwowaycbcpoints.h msg_ptreg.h ccoperationstats.h \
		ccConfirmationStats.h ccoriginalparent.h ccid.h pointdefs.h \
		resolvers.h pointtypes.h db_entry_defines.h
ccclientconn.obj:	yukon.h precompiled.h ctidbgmem.h ccclientconn.h \
		ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h mutex.h guard.h \
		numstr.h clrdump.h observe.h types.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h sorted_vector.h ccmessage.h \
		message.h collectable.h rwutil.h boost_time.h boostutil.h \
		ccsubstation.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		netports.h sema.h connection.h exchange.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccoriginalparent.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h msg_pcrequest.h \
		ccstrategy.h regression.h ccsubstationbus.h ccarea.h \
		ccsparea.h ccstate.h ccexecutor.h msg_signal.h ccutil.h \
		devicetypes.h ccsubstationbusstore.h ccid.h ccstatsobject.h \
		LoadTapChanger.h LtcPointHolder.h PointOffsetValueHolder.h \
		CapControlPao.h UpdatablePao.h PointDataHandler.h \
		PointDataListener.h ctibase.h ctinexus.h capcontroller.h \
		DispatchConnection.h configparms.h thread_monitor.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		thread_register_data.h
ccclientlistener.obj:	yukon.h precompiled.h ctidbgmem.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		CtiPCPtrQueue.h mutex.h guard.h numstr.h clrdump.h observe.h \
		types.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		sorted_vector.h ccstate.h connection.h exchange.h dllbase.h \
		dsm2.h cticonnect.h netports.h logger.h thread.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ccmessage.h \
		ccsubstation.h dbaccess.h sema.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h msg_pcrequest.h ccstrategy.h \
		regression.h ccsubstationbus.h ccarea.h ccsparea.h \
		ccsubstationbusstore.h ccid.h ccstatsobject.h \
		LoadTapChanger.h LtcPointHolder.h PointOffsetValueHolder.h \
		CapControlPao.h UpdatablePao.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h configparms.h \
		ctibase.h ctinexus.h ccexecutor.h msg_signal.h \
		thread_monitor.h smartmap.h readers_writer_lock.h \
		critical_section.h thread_register_data.h
ccconfirmationstats.obj:	yukon.h precompiled.h ctidbgmem.h \
		msg_signal.h message.h collectable.h dlldefs.h rwutil.h \
		ctitime.h boost_time.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		pointtypes.h msg_pdata.h pointdefs.h dbaccess.h dllbase.h \
		dsm2.h mutex.h guard.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h CtiPCPtrQueue.h resolvers.h \
		db_entry_defines.h ccconfirmationstats.h msg_cmd.h observe.h \
		ccoperationstats.h
ccexecutor.obj:	yukon.h precompiled.h ctidbgmem.h ccclientlistener.h \
		ccclientconn.h ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h mutex.h \
		guard.h numstr.h clrdump.h observe.h types.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h sorted_vector.h \
		ccstate.h connection.h exchange.h dllbase.h dsm2.h \
		cticonnect.h netports.h logger.h thread.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ccexecutor.h \
		ccmessage.h ccsubstation.h dbaccess.h sema.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccoriginalparent.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h msg_pcrequest.h \
		ccstrategy.h regression.h ccsubstationbus.h ccarea.h \
		ccsparea.h msg_signal.h ccutil.h devicetypes.h \
		ccsubstationbusstore.h ccid.h ccstatsobject.h \
		LoadTapChanger.h LtcPointHolder.h PointOffsetValueHolder.h \
		CapControlPao.h UpdatablePao.h PointDataHandler.h \
		PointDataListener.h capcontroller.h DispatchConnection.h \
		configparms.h ctibase.h ctinexus.h
ccfeeder.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		msg_signal.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		ccid.h cccapbank.h msg_cmd.h ccpointresponse.h observe.h \
		ccmonitorpoint.h ctidate.h logger.h thread.h CtiPCPtrQueue.h \
		cctwowaycbcpoints.h msg_ptreg.h ccoperationstats.h \
		ccConfirmationStats.h ccoriginalparent.h ccfeeder.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h msg_pcrequest.h ccstrategy.h regression.h \
		capcontroller.h DispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h ccsparea.h \
		ccstate.h ccmessage.h ccsubstation.h ccstatsobject.h \
		LoadTapChanger.h LtcPointHolder.h PointOffsetValueHolder.h \
		CapControlPao.h UpdatablePao.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h ccexecutor.h \
		ctdpcptrq.h ctibase.h ctinexus.h mgr_holiday.h resolvers.h \
		db_entry_defines.h msg_lmcontrolhistory.h tbl_pt_alarm.h \
		dbmemobject.h desolvers.h
ccmain.obj:	yukon.h precompiled.h ctidbgmem.h ccsubstationbusstore.h \
		observe.h types.h dlldefs.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h ccarea.h \
		dbaccess.h dllbase.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h sema.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h message.h collectable.h \
		rwutil.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccoriginalparent.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h msg_pcrequest.h \
		ccstrategy.h regression.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h ccsubstation.h ccstatsobject.h LoadTapChanger.h \
		LtcPointHolder.h PointOffsetValueHolder.h CapControlPao.h \
		UpdatablePao.h PointDataHandler.h PointDataListener.h \
		ccutil.h devicetypes.h capcontroller.h DispatchConnection.h \
		configparms.h ccexecutor.h msg_signal.h ctdpcptrq.h ctibase.h \
		ctinexus.h ccservice.h ccclientlistener.h ccclientconn.h \
		cservice.h precomp.h Monitor.h CServiceConfig.h rtdb.h \
		hashkey.h hash_functions.h
ccmessage.obj:	yukon.h precompiled.h ctidbgmem.h ccmessage.h ctitime.h \
		dlldefs.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h ccsubstation.h dbaccess.h dllbase.h \
		dsm2.h mutex.h guard.h clrdump.h cticonnect.h netports.h \
		sema.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h msg_pcrequest.h ccstrategy.h \
		regression.h ccsubstationbus.h ccarea.h ccsparea.h ccstate.h \
		ccid.h
ccmonitorpoint.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h ccid.h ccmonitorpoint.h msg_cmd.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h boostutil.h \
		utility.h queues.h sorted_vector.h observe.h ctidate.h \
		logger.h thread.h CtiPCPtrQueue.h pointdefs.h resolvers.h \
		pointtypes.h db_entry_defines.h
ccoperationstats.obj:	yukon.h precompiled.h ctidbgmem.h msg_signal.h \
		message.h collectable.h dlldefs.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h pointtypes.h \
		msg_pdata.h pointdefs.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h clrdump.h cticonnect.h netports.h sema.h logger.h \
		thread.h CtiPCPtrQueue.h resolvers.h db_entry_defines.h \
		ccid.h ccoperationstats.h msg_cmd.h observe.h
ccoriginalparent.obj:	yukon.h precompiled.h ctidbgmem.h \
		ccoriginalparent.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h ctitime.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h ccid.h
ccpointresponse.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h ccpointresponse.h observe.h utility.h ctitime.h \
		queues.h sorted_vector.h ccid.h pointdefs.h logger.h thread.h \
		CtiPCPtrQueue.h resolvers.h pointtypes.h db_entry_defines.h
ccserver.obj:	yukon.h precompiled.h ctidbgmem.h ccserver.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		CtiPCPtrQueue.h mutex.h guard.h numstr.h clrdump.h observe.h \
		types.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		sorted_vector.h ccstate.h connection.h exchange.h dllbase.h \
		dsm2.h cticonnect.h netports.h logger.h thread.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ccmessage.h \
		ccsubstation.h dbaccess.h sema.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h ccoriginalparent.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h msg_pcrequest.h ccstrategy.h \
		regression.h ccsubstationbus.h ccarea.h ccsparea.h ctibase.h \
		ctinexus.h configparms.h
ccservice.obj:	yukon.h precompiled.h ctidbgmem.h id_capcontrol.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h sorted_vector.h ccservice.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h CtiPCPtrQueue.h \
		mutex.h guard.h clrdump.h observe.h ccstate.h connection.h \
		exchange.h dllbase.h dsm2.h cticonnect.h netports.h logger.h \
		thread.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h cservice.h capcontroller.h dbaccess.h sema.h \
		DispatchConnection.h msg_cmd.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h ctidate.h ccoriginalparent.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h msg_pcrequest.h ccstrategy.h \
		regression.h ccsparea.h ccid.h ccmessage.h ccsubstation.h \
		ccstatsobject.h LoadTapChanger.h LtcPointHolder.h \
		PointOffsetValueHolder.h CapControlPao.h UpdatablePao.h \
		PointDataHandler.h PointDataListener.h ccutil.h devicetypes.h \
		ccexecutor.h msg_signal.h ctibase.h ctinexus.h eventlog.h \
		rtdb.h hashkey.h hash_functions.h
ccsparea.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		msg_signal.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		ccsparea.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h msg_pcrequest.h msg_cmd.h \
		ccstrategy.h ccOperationStats.h ccConfirmationStats.h ccid.h \
		cccapbank.h ccpointresponse.h ccmonitorpoint.h ctidate.h \
		cctwowaycbcpoints.h ccoriginalparent.h capcontroller.h \
		DispatchConnection.h configparms.h ccsubstationbusstore.h \
		ccarea.h ccsubstationbus.h ccfeeder.h regression.h ccstate.h \
		ccmessage.h ccsubstation.h ccstatsobject.h LoadTapChanger.h \
		LtcPointHolder.h PointOffsetValueHolder.h CapControlPao.h \
		UpdatablePao.h PointDataHandler.h PointDataListener.h \
		ccutil.h devicetypes.h ccexecutor.h ctdpcptrq.h ctibase.h \
		ctinexus.h resolvers.h db_entry_defines.h
ccstate.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		ccid.h ccstate.h connection.h exchange.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		message.h collectable.h rwutil.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h observe.h
ccstatsobject.obj:	yukon.h precompiled.h ctidbgmem.h ccstatsobject.h \
		msg_cmd.h dlldefs.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		dbaccess.h dllbase.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h sema.h observe.h
ccstrategy.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		msg_signal.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		ccstrategy.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h msg_pcrequest.h ccid.h \
		capcontroller.h DispatchConnection.h msg_cmd.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h ctidate.h ccoriginalparent.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h regression.h ccsparea.h ccstate.h \
		ccmessage.h ccsubstation.h ccstatsobject.h LoadTapChanger.h \
		LtcPointHolder.h PointOffsetValueHolder.h CapControlPao.h \
		UpdatablePao.h PointDataHandler.h PointDataListener.h \
		ccutil.h devicetypes.h ccexecutor.h ctdpcptrq.h ctibase.h \
		ctinexus.h resolvers.h db_entry_defines.h mgr_holiday.h
ccsubstation.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h msg_signal.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h boostutil.h utility.h queues.h \
		sorted_vector.h ccsubstation.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccoriginalparent.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h msg_pcrequest.h \
		ccstrategy.h regression.h ccsubstationbus.h ccid.h \
		capcontroller.h DispatchConnection.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccstate.h \
		ccmessage.h ccstatsobject.h LoadTapChanger.h LtcPointHolder.h \
		PointOffsetValueHolder.h CapControlPao.h UpdatablePao.h \
		PointDataHandler.h PointDataListener.h ccutil.h devicetypes.h \
		ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h
ccsubstationbus.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h msg_signal.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h boostutil.h utility.h queues.h \
		sorted_vector.h ccsubstationbus.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h ccoriginalparent.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h msg_pcrequest.h \
		ccstrategy.h regression.h ccid.h capcontroller.h \
		DispatchConnection.h configparms.h ccsubstationbusstore.h \
		ccarea.h ccsparea.h ccstate.h ccmessage.h ccsubstation.h \
		ccstatsobject.h LoadTapChanger.h LtcPointHolder.h \
		PointOffsetValueHolder.h CapControlPao.h UpdatablePao.h \
		PointDataHandler.h PointDataListener.h ccutil.h devicetypes.h \
		ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h mgr_holiday.h mgr_paosched.h \
		pao_schedule.h pao_event.h dbmemobject.h tbl_pt_alarm.h \
		desolvers.h
ccsubstationbusstore.obj:	yukon.h precompiled.h ctidbgmem.h \
		ccsubstationbusstore.h observe.h types.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h ccarea.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h clrdump.h cticonnect.h netports.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		msg_pcrequest.h ccstrategy.h regression.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h ccsubstation.h ccstatsobject.h \
		LoadTapChanger.h LtcPointHolder.h PointOffsetValueHolder.h \
		CapControlPao.h UpdatablePao.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h desolvers.h \
		db_entry_defines.h resolvers.h ctibase.h ctinexus.h \
		configparms.h msg_dbchg.h msg_signal.h capcontroller.h \
		DispatchConnection.h ccexecutor.h ctdpcptrq.h mgr_holiday.h \
		thread_monitor.h smartmap.h readers_writer_lock.h \
		critical_section.h thread_register_data.h ctistring.h
cctwowaycbcpoints.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h cctwowaycbcpoints.h msg_cmd.h message.h collectable.h \
		rwutil.h ctitime.h boost_time.h boostutil.h utility.h \
		queues.h sorted_vector.h msg_ptreg.h observe.h cccapbank.h \
		ccpointresponse.h ccmonitorpoint.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h ccoperationstats.h \
		ccConfirmationStats.h ccoriginalparent.h ccid.h pointdefs.h \
		resolvers.h pointtypes.h db_entry_defines.h
loadtapchanger.obj:	yukon.h precompiled.h ctidbgmem.h LoadTapChanger.h \
		LtcPointHolder.h PointOffsetValueHolder.h pointtypes.h \
		msg_pdata.h dlldefs.h pointdefs.h message.h collectable.h \
		rwutil.h ctitime.h boost_time.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h CapControlPao.h UpdatablePao.h
ltcpointholder.obj:	yukon.h precompiled.h ctidbgmem.h LtcPointHolder.h \
		PointOffsetValueHolder.h pointtypes.h msg_pdata.h dlldefs.h \
		pointdefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h
mgr_paosched.obj:	yukon.h precompiled.h ctidbgmem.h mgr_paosched.h \
		pao_schedule.h ctibase.h ctinexus.h dlldefs.h netports.h \
		cticonnect.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		pao_event.h dbmemobject.h capcontroller.h dbaccess.h sema.h \
		connection.h exchange.h message.h collectable.h rwutil.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h DispatchConnection.h msg_cmd.h \
		configparms.h ccsubstationbusstore.h observe.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ctidate.h \
		ccoriginalparent.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		msg_pcrequest.h ccstrategy.h regression.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h ccsubstation.h ccstatsobject.h \
		LoadTapChanger.h LtcPointHolder.h PointOffsetValueHolder.h \
		CapControlPao.h UpdatablePao.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h ccexecutor.h \
		msg_signal.h ctdpcptrq.h ctitokenizer.h thread_monitor.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		thread_register_data.h ctistring.h
pao_event.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		connection.h exchange.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_dbchg.h \
		capcontroller.h DispatchConnection.h msg_cmd.h configparms.h \
		ccsubstationbusstore.h observe.h ccarea.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h ctidate.h ccoriginalparent.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h msg_pcrequest.h \
		ccstrategy.h regression.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h ccsubstation.h ccstatsobject.h LoadTapChanger.h \
		LtcPointHolder.h PointOffsetValueHolder.h CapControlPao.h \
		UpdatablePao.h PointDataHandler.h PointDataListener.h \
		ccutil.h devicetypes.h ccexecutor.h msg_signal.h ctdpcptrq.h \
		ctibase.h ctinexus.h pao_schedule.h pao_event.h dbmemobject.h
pao_schedule.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h connection.h exchange.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_dbchg.h \
		capcontroller.h DispatchConnection.h msg_cmd.h configparms.h \
		ccsubstationbusstore.h observe.h ccarea.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h ctidate.h ccoriginalparent.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h msg_pcrequest.h \
		ccstrategy.h regression.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h ccsubstation.h ccstatsobject.h LoadTapChanger.h \
		LtcPointHolder.h PointOffsetValueHolder.h CapControlPao.h \
		UpdatablePao.h PointDataHandler.h PointDataListener.h \
		ccutil.h devicetypes.h ccexecutor.h msg_signal.h ctdpcptrq.h \
		ctibase.h ctinexus.h pao_schedule.h pao_event.h dbmemobject.h
pointoffsetvalueholder.obj:	yukon.h precompiled.h ctidbgmem.h \
		pointOffsetValueHolder.h pointtypes.h msg_pdata.h dlldefs.h \
		pointdefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
test_ccfeeder.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ccfeeder.h dbaccess.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h observe.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		msg_pcrequest.h ccstrategy.h regression.h ccsubstationbus.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h ccsubstation.h ccstatsobject.h LoadTapChanger.h \
		LtcPointHolder.h PointOffsetValueHolder.h CapControlPao.h \
		UpdatablePao.h PointDataHandler.h PointDataListener.h \
		ccutil.h devicetypes.h ccUnitTestUtil.h
test_ccsubstationbus.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h observe.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h ccoriginalparent.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		msg_pcrequest.h ccstrategy.h regression.h ccsubstation.h \
		ccarea.h ccsubstationbusstore.h ccsparea.h ccid.h ccstate.h \
		ccmessage.h ccstatsobject.h LoadTapChanger.h LtcPointHolder.h \
		PointOffsetValueHolder.h CapControlPao.h UpdatablePao.h \
		PointDataHandler.h PointDataListener.h ccutil.h devicetypes.h \
		ccexecutor.h msg_signal.h ctdpcptrq.h mgr_paosched.h \
		pao_schedule.h ctibase.h ctinexus.h pao_event.h dbmemobject.h \
		ccUnitTestUtil.h
test_likedaycontrol.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ccfeeder.h dbaccess.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h observe.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		ccoriginalparent.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		msg_pcrequest.h ccstrategy.h regression.h ccsubstationbus.h \
		ccUnitTestUtil.h ccsubstationbusstore.h ccarea.h ccsparea.h \
		ccid.h ccstate.h ccmessage.h ccsubstation.h ccstatsobject.h \
		LoadTapChanger.h LtcPointHolder.h PointOffsetValueHolder.h \
		CapControlPao.h UpdatablePao.h PointDataHandler.h \
		PointDataListener.h ccutil.h devicetypes.h
test_pointholder.obj:	yukon.h precompiled.h ctidbgmem.h \
		PointOffsetValueHolder.h pointtypes.h msg_pdata.h dlldefs.h \
		pointdefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h LtcPointHolder.h
#ENDUPDATE#

