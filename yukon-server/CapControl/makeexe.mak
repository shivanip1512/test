# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(CAPCONTROL)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(PROCLOG)\include \
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
;$(PROCLOG)\include \
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
ccarea.obj

TARGS = capcontrol.exe

ALL:          $(TARGS)

capcontrol.exe:     $(BASEOBJS) Makefile
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOSTLIBS)
              @echo:
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
		CtiPCPtrQueue.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h ccmessage.h ccarea.h observe.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		msg_pcrequest.h ccstrategy.h ccstate.h msg_signal.h \
		msg_pcreturn.h msg_dbchg.h pointtypes.h configparms.h \
		capcontroller.h ccsubstationbusstore.h ccid.h ccexecutor.h \
		ctdpcptrq.h ctibase.h ctinexus.h devicetypes.h resolvers.h \
		db_entry_defines.h mgr_paosched.h pao_schedule.h pao_event.h \
		dbmemobject.h thread_monitor.h smartmap.h hashkey.h \
		hash_functions.h thread_register_data.h ccclientconn.h \
		ccclientlistener.h
ccarea.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		msg_signal.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h ccarea.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h utility.h queues.h sorted_vector.h observe.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		msg_pcrequest.h ccstrategy.h ccid.h pointtypes.h \
		capcontroller.h configparms.h ccsubstationbusstore.h \
		ccstate.h ccmessage.h ccexecutor.h ctdpcptrq.h ctibase.h \
		ctinexus.h resolvers.h db_entry_defines.h
cccapbank.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		cccapbank.h msg_cmd.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h ccpointresponse.h observe.h utility.h \
		queues.h sorted_vector.h ccmonitorpoint.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h cctwowaycbcpoints.h ccid.h \
		pointdefs.h resolvers.h pointtypes.h db_entry_defines.h
ccclientconn.obj:	yukon.h precompiled.h ctidbgmem.h ccclientconn.h \
		ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h mutex.h guard.h \
		numstr.h clrdump.h observe.h types.h utility.h ctitime.h \
		queues.h sorted_vector.h ccmessage.h message.h collectable.h \
		rwutil.h boost_time.h ccarea.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h cticonnect.h netports.h sema.h \
		connection.h exchange.h logger.h thread.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h msg_pcrequest.h \
		ccstrategy.h ccstate.h ccexecutor.h ccsubstationbusstore.h \
		ccid.h ctibase.h ctinexus.h capcontroller.h pointtypes.h \
		configparms.h thread_monitor.h smartmap.h hashkey.h \
		hash_functions.h thread_register_data.h
ccclientlistener.obj:	yukon.h precompiled.h ctidbgmem.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		CtiPCPtrQueue.h mutex.h guard.h numstr.h clrdump.h observe.h \
		types.h utility.h ctitime.h queues.h sorted_vector.h \
		ccstate.h connection.h exchange.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h cticonnect.h netports.h logger.h thread.h \
		message.h collectable.h rwutil.h boost_time.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ccmessage.h ccarea.h \
		dbaccess.h sema.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h msg_pcrequest.h \
		ccstrategy.h ccsubstationbusstore.h ccid.h configparms.h \
		ctibase.h ctinexus.h ccexecutor.h thread_monitor.h smartmap.h \
		hashkey.h hash_functions.h thread_register_data.h
ccexecutor.obj:	yukon.h precompiled.h ctidbgmem.h msg_signal.h \
		message.h collectable.h dlldefs.h rwutil.h ctitime.h \
		boost_time.h ccclientlistener.h ccclientconn.h ctdpcptrq.h \
		CtiPCPtrQueue.h mutex.h guard.h numstr.h clrdump.h observe.h \
		types.h utility.h queues.h sorted_vector.h ccstate.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h logger.h thread.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ccexecutor.h ccmessage.h \
		ccarea.h dbaccess.h sema.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h msg_pcrequest.h \
		ccstrategy.h ccsubstationbusstore.h ccid.h capcontroller.h \
		pointtypes.h configparms.h ctibase.h ctinexus.h
ccfeeder.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		msg_signal.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h ccsubstationbus.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h utility.h queues.h sorted_vector.h \
		observe.h ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		msg_pcrequest.h ccstrategy.h ccid.h pointtypes.h \
		capcontroller.h configparms.h ccsubstationbusstore.h ccarea.h \
		ccstate.h ccmessage.h ccexecutor.h ctdpcptrq.h ctibase.h \
		ctinexus.h mgr_holiday.h resolvers.h db_entry_defines.h \
		msg_lmcontrolhistory.h
ccmain.obj:	yukon.h precompiled.h ctidbgmem.h ccsubstationbusstore.h \
		observe.h types.h dlldefs.h utility.h ctitime.h queues.h \
		numstr.h sorted_vector.h ccarea.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h sema.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h message.h collectable.h \
		rwutil.h boost_time.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h msg_pcrequest.h ccstrategy.h ccid.h \
		ccstate.h ccmessage.h capcontroller.h pointtypes.h \
		configparms.h ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h \
		ccservice.h ccclientlistener.h ccclientconn.h cservice.h \
		precomp.h Monitor.h CServiceConfig.h rtdb.h hashkey.h \
		hash_functions.h
ccmessage.obj:	yukon.h precompiled.h ctidbgmem.h ccmessage.h ctitime.h \
		dlldefs.h message.h collectable.h rwutil.h boost_time.h \
		ccarea.h dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h utility.h queues.h sorted_vector.h observe.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h msg_cmd.h \
		ctidate.h cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		msg_pcrequest.h ccstrategy.h ccstate.h ccid.h
ccmonitorpoint.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h ccid.h ccmonitorpoint.h msg_cmd.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h observe.h \
		utility.h queues.h sorted_vector.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h pointdefs.h resolvers.h pointtypes.h \
		db_entry_defines.h
ccpointresponse.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h ccpointresponse.h observe.h utility.h ctitime.h \
		queues.h sorted_vector.h ccid.h pointdefs.h logger.h thread.h \
		CtiPCPtrQueue.h resolvers.h pointtypes.h db_entry_defines.h
ccserver.obj:	yukon.h precompiled.h ctidbgmem.h ccserver.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		CtiPCPtrQueue.h mutex.h guard.h numstr.h clrdump.h observe.h \
		types.h utility.h ctitime.h queues.h sorted_vector.h \
		ccstate.h connection.h exchange.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h cticonnect.h netports.h logger.h thread.h \
		message.h collectable.h rwutil.h boost_time.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ccmessage.h ccarea.h \
		dbaccess.h sema.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h msg_pcrequest.h \
		ccstrategy.h ctibase.h ctinexus.h configparms.h
ccservice.obj:	yukon.h precompiled.h ctidbgmem.h ccservice.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		CtiPCPtrQueue.h mutex.h guard.h numstr.h clrdump.h observe.h \
		types.h utility.h ctitime.h queues.h sorted_vector.h \
		ccstate.h connection.h exchange.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h cticonnect.h netports.h logger.h thread.h \
		message.h collectable.h rwutil.h boost_time.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h cservice.h capcontroller.h \
		dbaccess.h sema.h msg_cmd.h pointtypes.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h ctidate.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h msg_pcrequest.h ccstrategy.h ccid.h \
		ccmessage.h ccexecutor.h ctibase.h ctinexus.h eventlog.h \
		rtdb.h hashkey.h hash_functions.h
ccstate.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		ccid.h ccstate.h connection.h exchange.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h utility.h \
		queues.h sorted_vector.h observe.h
ccstrategy.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		msg_signal.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h ccstrategy.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h utility.h queues.h sorted_vector.h observe.h \
		msg_pcrequest.h ccid.h pointtypes.h capcontroller.h msg_cmd.h \
		configparms.h ccsubstationbusstore.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h ctidate.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h ccstate.h \
		ccmessage.h ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h mgr_holiday.h
ccsubstationbus.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h msg_signal.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h ccsubstationbus.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h observe.h ccfeeder.h ccmonitorpoint.h \
		msg_cmd.h ctidate.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h msg_pcrequest.h ccstrategy.h ccid.h \
		pointtypes.h capcontroller.h configparms.h \
		ccsubstationbusstore.h ccarea.h ccstate.h ccmessage.h \
		ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h mgr_holiday.h mgr_paosched.h \
		pao_schedule.h pao_event.h dbmemobject.h
ccsubstationbusstore.obj:	yukon.h precompiled.h ctidbgmem.h \
		ccsubstationbusstore.h observe.h types.h dlldefs.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h ccarea.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h clrdump.h cticonnect.h netports.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h message.h \
		collectable.h rwutil.h boost_time.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h msg_cmd.h ctidate.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h msg_pcrequest.h \
		ccstrategy.h ccid.h ccstate.h ccmessage.h desolvers.h \
		db_entry_defines.h pointtypes.h resolvers.h devicetypes.h \
		ctibase.h ctinexus.h configparms.h msg_dbchg.h msg_signal.h \
		capcontroller.h ccexecutor.h ctdpcptrq.h thread_monitor.h \
		smartmap.h hashkey.h hash_functions.h thread_register_data.h \
		ctistring.h
cctwowaycbcpoints.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h cctwowaycbcpoints.h msg_cmd.h message.h collectable.h \
		rwutil.h ctitime.h boost_time.h observe.h utility.h queues.h \
		sorted_vector.h ccid.h pointdefs.h logger.h thread.h \
		CtiPCPtrQueue.h resolvers.h pointtypes.h db_entry_defines.h
mgr_paosched.obj:	yukon.h precompiled.h ctidbgmem.h mgr_paosched.h \
		pao_schedule.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h connection.h exchange.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h message.h \
		collectable.h rwutil.h boost_time.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h utility.h queues.h sorted_vector.h \
		ctibase.h ctinexus.h pao_event.h dbmemobject.h \
		capcontroller.h msg_cmd.h pointtypes.h configparms.h \
		ccsubstationbusstore.h observe.h ccarea.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h ctidate.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h msg_pcrequest.h \
		ccstrategy.h ccid.h ccstate.h ccmessage.h ccexecutor.h \
		ctdpcptrq.h ctitokenizer.h thread_monitor.h smartmap.h \
		hashkey.h hash_functions.h thread_register_data.h \
		msg_signal.h
pao_event.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		connection.h exchange.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h msg_dbchg.h capcontroller.h msg_cmd.h \
		pointtypes.h configparms.h ccsubstationbusstore.h observe.h \
		ccarea.h ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		ctidate.h cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		msg_pcrequest.h ccstrategy.h ccid.h ccstate.h ccmessage.h \
		ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h pao_schedule.h \
		pao_event.h dbmemobject.h
pao_schedule.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h connection.h exchange.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h msg_dbchg.h capcontroller.h msg_cmd.h \
		pointtypes.h configparms.h ccsubstationbusstore.h observe.h \
		ccarea.h ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		ctidate.h cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		msg_pcrequest.h ccstrategy.h ccid.h ccstate.h ccmessage.h \
		ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h pao_schedule.h \
		pao_event.h dbmemobject.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
test_ccsubstationbus.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h message.h collectable.h \
		rwutil.h boost_time.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h utility.h queues.h sorted_vector.h observe.h \
		ccfeeder.h ccmonitorpoint.h msg_cmd.h ctidate.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h msg_pcrequest.h \
		ccstrategy.h
#ENDUPDATE#

