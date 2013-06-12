# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(LOADMANAGEMENT)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(SERVER)\include \
-I$(BOOST_INCLUDE) \
-I$(RW) \
-I$(SQLAPI)\include \
-I$(THRIFT_INCLUDE) \
-I$(MSG)\Serialization


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(LOADMANAGENT)\include \
;$(CPARMS)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(MSG)\include \


LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctiseasondb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctithriftmsg.lib \
$(THRIFT_LIB)


BASEOBJS= \
clistener.obj \
executor.obj \
ConstraintViolation.obj \
lmconstraint.obj \
lmcontrolarea.obj \
lmcontrolareastore.obj \
lmcontrolareatrigger.obj \
lmcicustomerbase.obj \
lmcurtailcustomer.obj \
lmenergyexchangecustomer.obj \
lmenergyexchangecustomerreply.obj \
lmenergyexchangehourlycustomer.obj \
lmenergyexchangehourlyoffer.obj \
lmenergyexchangeoffer.obj \
lmenergyexchangeofferrevision.obj \
lmfactory.obj \
lmgroupbase.obj \
lmgroupdigisep.obj \
lmgroupemetcon.obj \
lmgroupexpresscom.obj \
lmgroupmacro.obj \
lmgroupmct.obj \
lmgrouppoint.obj \
lmgroupripple.obj \
lmgroupsa305.obj \
lmgroupsa105.obj \
lmgroupsa205.obj \
lmgroupsadigital.obj \
lmgroupgolay.obj \
lmgroupversacom.obj \
lmmessage.obj \
lmprogrambase.obj \
lmprogramcontrolwindow.obj \
lmprogramcurtailment.obj \
lmprogramdirect.obj \
lmprogramdirectgear.obj \
lmprogramenergyexchange.obj \
lmprogramthermostatgear.obj \
lmservice.obj \
loadmanager.obj \
lmutility.obj \
main.obj \
sepcyclegear.obj \
septempoffsetgear.obj \
lmprogrambeatthepeakgear.obj \
lm_message_serialization.obj \
lm_program_serialization.obj \
lm_group_serialization.obj


TARGS = loadmanagement.exe


LOADMANAGEMENT_FULLBUILD = $[Filename,$(OBJ),LoadManagementFullBuild,target]


PROGS_VERSION=\
$(TARGS)


ALL:          $(TARGS)


$(LOADMANAGEMENT_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(BASEOBJS)]


loadmanagement.exe: $(LOADMANAGEMENT_FULLBUILD) $(BASEOBJS) Makefile $(OBJ)\loadmanagement.res
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(RWCPPINVOKE) $(CFLAGS) -D_DEBUG_MEMORY $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOST_LIBS) $(LINKFLAGS) loadmanagement.res
              @echo:
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
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
               $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS)  $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
#UPDATE#
clistener.obj:	precompiled.h clistener.h connection_server.h \
		connection.h dlldefs.h exchange.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h connection_listener.h \
		lmmessage.h lmcontrolarea.h dbmemobject.h observe.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmcontrolareastore.h lmid.h ctibase.h \
		ctinexus.h executor.h ctdpcptrq.h msg_server_req.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		amq_constants.h
constraintviolation.obj:	precompiled.h ConstraintViolation.h ctitime.h \
		dlldefs.h collectable.h lmid.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h
executor.obj:	precompiled.h msg_server_req.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_server_resp.h msg_signal.h executor.h ctdpcptrq.h \
		CtiPCPtrQueue.h lmmessage.h lmcontrolarea.h dbmemobject.h \
		connection.h exchange.h logger.h thread.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h observe.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h clistener.h \
		connection_server.h connection_listener.h \
		lmcontrolareastore.h lmid.h loadmanager.h connection_client.h \
		ctibase.h ctinexus.h lmprogramenergyexchange.h \
		lmenergyexchangeoffer.h lmenergyexchangeofferrevision.h \
		lmenergyexchangehourlyoffer.h lmenergyexchangecustomer.h \
		lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lmprogramcontrolwindow.h \
		devicetypes.h lmconstraint.h lmutility.h \
		GroupControlInterface.h BeatThePeakControlInterface.h \
		BeatThePeakAlertLevel.h
lmcicustomerbase.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmcicustomerbase.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		lmid.h logger.h thread.h CtiPCPtrQueue.h loadmanager.h \
		connection_client.h connection.h exchange.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h msg_cmd.h \
		lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h ctibase.h \
		ctinexus.h resolvers.h db_entry_defines.h
lmconstraint.obj:	precompiled.h lmconstraint.h lmprogramdirect.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h lmprogrambase.h dbmemobject.h \
		observe.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		tbl_lmprogramhistory.h lmutility.h lmmessage.h \
		ConstraintViolation.h lmprogramcontrolwindow.h lmid.h \
		mgr_season.h mgr_holiday.h
lmcontrolarea.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h msg_signal.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		lmcontrolarea.h dbmemobject.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h observe.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmid.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		lmprogramcontrolwindow.h devicetypes.h loadmanager.h \
		connection_client.h lmcontrolareastore.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h lmconstraint.h lmutility.h \
		database_writer.h row_writer.h
lmcontrolareastore.obj:	precompiled.h mgr_holiday.h ctidate.h \
		dlldefs.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h mgr_season.h msg_signal.h message.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		lmcontrolareastore.h observe.h lmcontrolarea.h dbmemobject.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmid.h lmcurtailcustomer.h \
		lmcicustomerbase.h lmenergyexchangecustomer.h \
		lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangeoffer.h lmprogramcurtailment.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h lmprogramthermostatgear.h \
		lmprogramenergyexchange.h lmgroupversacom.h lmgroupemetcon.h \
		lmgroupexpresscom.h BeatThePeakControlInterface.h \
		BeatThePeakAlertLevel.h lmgroupmct.h lmgroupripple.h \
		lmgrouppoint.h lmgroupsa105.h lmgroupsa205.h lmgroupsa305.h \
		lmgroupsadigital.h lmgroupgolay.h lmprogramcontrolwindow.h \
		sepcyclegear.h smartgearbase.h septempoffsetgear.h \
		resolvers.h db_entry_defines.h desolvers.h devicetypes.h \
		database_transaction.h ctibase.h ctinexus.h msg_dbchg.h \
		loadmanager.h connection_client.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h ConstraintViolation.h \
		lmfactory.h tbl_paoexclusion.h database_writer.h row_writer.h \
		debug_timer.h clistener.h connection_server.h \
		connection_listener.h lmprogrambeatthepeakgear.h
lmcontrolareatrigger.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmcontrolareatrigger.h row_reader.h observe.h \
		database_connection.h collectable.h lmid.h lmprogrambase.h \
		dbmemobject.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h rwutil.h database_reader.h \
		boost_time.h boostutil.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h ctidate.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection_client.h connection.h exchange.h \
		string_utility.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h lmcontrolareastore.h lmcontrolarea.h \
		executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h database_writer.h row_writer.h
lmcurtailcustomer.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmcurtailcustomer.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		lmcicustomerbase.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection_client.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h msg_cmd.h lmcontrolareastore.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
		lmcontrolareatrigger.h ctidate.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h ctibase.h \
		ctinexus.h resolvers.h db_entry_defines.h database_writer.h \
		row_writer.h
lmenergyexchangecustomer.obj:	precompiled.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmenergyexchangecustomer.h observe.h lmcicustomerbase.h \
		msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h ctibase.h \
		ctinexus.h resolvers.h db_entry_defines.h
lmenergyexchangecustomerreply.obj:	precompiled.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmenergyexchangecustomerreply.h observe.h \
		lmenergyexchangehourlycustomer.h row_reader.h collectable.h \
		lmid.h logger.h thread.h CtiPCPtrQueue.h loadmanager.h \
		connection_client.h connection.h exchange.h string_utility.h \
		message.h rwutil.h database_connection.h database_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h msg_cmd.h lmcontrolareastore.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
		msg_pcrequest.h lmcontrolareatrigger.h ctidate.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h database_writer.h row_writer.h
lmenergyexchangehourlycustomer.obj:	precompiled.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmenergyexchangehourlycustomer.h observe.h row_reader.h \
		collectable.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection_client.h connection.h exchange.h \
		string_utility.h message.h rwutil.h database_connection.h \
		database_reader.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h database_writer.h row_writer.h
lmenergyexchangehourlyoffer.obj:	precompiled.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmenergyexchangehourlyoffer.h observe.h row_reader.h \
		collectable.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection_client.h connection.h exchange.h \
		string_utility.h message.h rwutil.h database_connection.h \
		database_reader.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h database_writer.h row_writer.h
lmenergyexchangeoffer.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h lmid.h \
		logger.h thread.h CtiPCPtrQueue.h lmenergyexchangeoffer.h \
		observe.h lmenergyexchangeofferrevision.h \
		lmenergyexchangehourlyoffer.h row_reader.h collectable.h \
		rwutil.h database_connection.h database_reader.h boost_time.h \
		boostutil.h database_writer.h row_writer.h
lmenergyexchangeofferrevision.obj:	precompiled.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		observe.h row_reader.h collectable.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h message.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h msg_cmd.h lmcontrolareastore.h lmcontrolarea.h \
		dbmemobject.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h database_writer.h row_writer.h
lmfactory.obj:	precompiled.h lmfactory.h lmgroupbase.h boostutil.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h dbmemobject.h observe.h msg_pcrequest.h \
		message.h ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		msg_cmd.h logger.h thread.h CtiPCPtrQueue.h resolvers.h \
		pointtypes.h db_entry_defines.h devicetypes.h \
		lmgroupversacom.h lmgroupdigisep.h GroupControlInterface.h \
		SepControlInterface.h lmgroupemetcon.h lmgroupexpresscom.h \
		BeatThePeakControlInterface.h BeatThePeakAlertLevel.h \
		lmgroupmct.h lmgroupripple.h lmgrouppoint.h lmgroupsa105.h \
		lmgroupsa205.h lmgroupsa305.h lmgroupsadigital.h \
		lmgroupgolay.h lmgroupmacro.h
lmgroupbase.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmgroupbase.h boostutil.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h resolvers.h db_entry_defines.h \
		database_writer.h row_writer.h
lmgroupdigisep.obj:	precompiled.h lmgroupdigisep.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		msg_cmd.h GroupControlInterface.h SepControlInterface.h \
		lmid.h logger.h thread.h CtiPCPtrQueue.h amq_connection.h \
		critical_section.h lmsepcontrolmessage.h \
		lmseprestoremessage.h
lmgroupemetcon.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmgroupemetcon.h lmgroupbase.h boostutil.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmgroupexpresscom.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmgroupexpresscom.h lmgroupbase.h boostutil.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h BeatThePeakControlInterface.h logger.h \
		thread.h CtiPCPtrQueue.h BeatThePeakAlertLevel.h lmid.h \
		loadmanager.h connection_client.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h lmcontrolareastore.h lmcontrolarea.h \
		lmprogrambase.h lmcontrolareatrigger.h ctidate.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		ctistring.h ctitokenizer.h
lmgroupgolay.obj:	precompiled.h lmgroupgolay.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h
lmgroupmacro.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmgroupmacro.h lmgroupbase.h boostutil.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmgroupmct.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h lmgroupmct.h \
		lmgroupemetcon.h lmgroupbase.h boostutil.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmgrouppoint.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmgrouppoint.h lmgroupbase.h boostutil.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmgroupripple.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmgroupripple.h lmgroupbase.h boostutil.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmgroupsa105.obj:	precompiled.h lmgroupsa105.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h
lmgroupsa205.obj:	precompiled.h lmgroupsa205.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h
lmgroupsa205or105.obj:	lmgroupSA205or105.h lmgroupbase.h boostutil.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h dbmemobject.h observe.h msg_pcrequest.h \
		message.h ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h
lmgroupsa305.obj:	precompiled.h lmgroupsa305.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h
lmgroupsadigital.obj:	precompiled.h lmgroupsadigital.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h
lmgroupsadigitalorgolay.obj:	lmgroupsadigitalorgolay.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h
lmgroupversacom.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmgroupbase.h boostutil.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmgroupversacom.h lmid.h logger.h \
		thread.h CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmmessage.obj:	precompiled.h lmmessage.h ctitime.h dlldefs.h message.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		lmcontrolarea.h dbmemobject.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h observe.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmid.h
lmprogrambase.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h lmid.h \
		lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramcontrolwindow.h loadmanager.h \
		connection_client.h connection.h exchange.h string_utility.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h lmcontrolareastore.h lmcontrolarea.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h mgr_holiday.h mgr_season.h \
		lmutility.h database_writer.h row_writer.h
lmprogrambeatthepeakgear.obj:	precompiled.h lmprogrambeatthepeakgear.h \
		lmprogramdirect.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h dsm2err.h words.h optional.h database_reader.h \
		row_reader.h boost_time.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		tbl_lmprogramhistory.h smartgearbase.h \
		BeatThePeakAlertLevel.h BeatThePeakControlInterface.h
lmprogramcontrolwindow.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h lmid.h \
		lmprogramcontrolwindow.h observe.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h row_reader.h collectable.h \
		pointdefs.h pointtypes.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h message.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		lmutility.h
lmprogramcurtailment.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h lmid.h \
		lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		lmcontrolareastore.h lmcontrolarea.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		msg_notif_email.h database_writer.h row_writer.h
lmprogramdirect.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmprogramdirect.h boostutil.h lmprogrambase.h dbmemobject.h \
		observe.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h lmcontrolareatrigger.h ctidate.h \
		logger.h thread.h CtiPCPtrQueue.h lmprogramdirectgear.h \
		lmcontrolarea.h connection.h exchange.h string_utility.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h tbl_lmprogramhistory.h lmgrouppoint.h \
		devicetypes.h lmid.h desolvers.h loadmanager.h \
		connection_client.h lmcontrolareastore.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		msg_signal.h msg_notif_lmcontrol.h lmprogramthermostatgear.h \
		lmprogramcontrolwindow.h lmconstraint.h lmutility.h \
		database_writer.h row_writer.h smartgearbase.h \
		lmgroupdigisep.h GroupControlInterface.h \
		SepControlInterface.h
lmprogramdirectgear.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmprogramdirectgear.h observe.h row_reader.h collectable.h \
		lmid.h lmprogrambase.h dbmemobject.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		lmcontrolareastore.h lmcontrolarea.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmprogramenergyexchange.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h lmid.h \
		lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection_client.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		lmcontrolareastore.h lmcontrolarea.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		lmprogramenergyexchange.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h msg_notif_email.h
lmprogramthermostatgear.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		lmprogramthermostatgear.h observe.h lmprogramdirectgear.h \
		row_reader.h collectable.h lmid.h lmprogrambase.h \
		dbmemobject.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h rwutil.h database_connection.h \
		database_reader.h boost_time.h boostutil.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h lmcontrolareatrigger.h ctidate.h \
		logger.h thread.h CtiPCPtrQueue.h loadmanager.h \
		connection_client.h connection.h exchange.h string_utility.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h lmcontrolareastore.h lmcontrolarea.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h lmprogramdirect.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmserver.obj:	lmserver.h clistener.h connection_server.h connection.h \
		dlldefs.h exchange.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h connection_listener.h \
		lmmessage.h lmcontrolarea.h dbmemobject.h observe.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h ctibase.h ctinexus.h
lmservice.obj:	precompiled.h lmservice.h cservice.h dlldefs.h \
		loadmanager.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h connection_client.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h msg_cmd.h \
		lmcontrolareastore.h observe.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h lmid.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		clistener.h connection_server.h connection_listener.h \
		id_loadmanagement.h eventlog.h rtdb.h hashkey.h \
		hash_functions.h
lmutility.obj:	precompiled.h timeperiod.h dlldefs.h lmutility.h \
		CtiTime.h ctidate.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h lmprogrambase.h dbmemobject.h observe.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h ctidbgmem.h \
		pointdefs.h pointtypes.h message.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmcontrolarea.h connection.h \
		exchange.h string_utility.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h lmprogramcontrolwindow.h
lm_group_serialization.obj:	precompiled.h lm_group_serialization.h \
		lmgroupbase.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h mutex.h guard.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmgroupdigisep.h \
		GroupControlInterface.h SepControlInterface.h \
		lmgroupemetcon.h lmgroupexpresscom.h \
		BeatThePeakControlInterface.h logger.h thread.h \
		CtiPCPtrQueue.h BeatThePeakAlertLevel.h lmgroupgolay.h \
		lmgroupmacro.h lmgroupmct.h lmgrouppoint.h lmgroupripple.h \
		lmgroupsa105.h lmgroupsa205.h lmgroupsa305.h \
		lmgroupsadigital.h lmgroupversacom.h
lm_message_serialization.obj:	precompiled.h lm_program_serialization.h \
		lmprogrambase.h dbmemobject.h observe.h types.h dlldefs.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h ctidbgmem.h \
		pointdefs.h pointtypes.h message.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramcontrolwindow.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		lmprogramdirect.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		tbl_lmprogramhistory.h lmprogramenergyexchange.h \
		lmenergyexchangeoffer.h lmenergyexchangeofferrevision.h \
		lmenergyexchangehourlyoffer.h lmenergyexchangecustomer.h \
		lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lm_message_serialization.h \
		lmmessage.h ConstraintViolation.h
lm_program_serialization.obj:	precompiled.h lm_group_serialization.h \
		lmgroupbase.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h mutex.h guard.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmgroupdigisep.h \
		GroupControlInterface.h SepControlInterface.h \
		lmgroupemetcon.h lmgroupexpresscom.h \
		BeatThePeakControlInterface.h logger.h thread.h \
		CtiPCPtrQueue.h BeatThePeakAlertLevel.h lmgroupgolay.h \
		lmgroupmacro.h lmgroupmct.h lmgrouppoint.h lmgroupripple.h \
		lmgroupsa105.h lmgroupsa205.h lmgroupsa305.h \
		lmgroupsadigital.h lmgroupversacom.h \
		lm_program_serialization.h lmprogrambase.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h lmcontrolareatrigger.h \
		ctidate.h lmprogramcontrolwindow.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h lmprogramdirect.h \
		lmprogramdirectgear.h lmcontrolarea.h connection.h exchange.h \
		string_utility.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h tbl_lmprogramhistory.h \
		lmprogramenergyexchange.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h
lm_server_client_serialization_test.obj:	precompiled.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h types.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_cmd.h msg_commerrorhistory.h msg_dbchg.h \
		msg_lmcontrolhistory.h pointdefs.h msg_multi.h msg_pdata.h \
		pointtypes.h msg_notif_alarm.h msg_notif_email.h logger.h \
		thread.h CtiPCPtrQueue.h msg_notif_lmcontrol.h \
		msg_pcrequest.h msg_pcreturn.h msg_ptreg.h msg_queuedata.h \
		Msg_reg.h msg_requestcancel.h msg_server_req.h \
		msg_server_resp.h msg_signal.h msg_tag.h msg_trace.h \
		connection_server.h connection.h exchange.h string_utility.h \
		queue.h cparms.h configkey.h configval.h \
		connection_listener.h lmmessage.h lmcontrolarea.h \
		dbmemobject.h observe.h lmprogrambase.h lmgroupbase.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcontrolwindow.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		lmprogramenergyexchange.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lmgroupdigisep.h \
		GroupControlInterface.h SepControlInterface.h \
		lmgroupemetcon.h lmgroupexpresscom.h \
		BeatThePeakControlInterface.h BeatThePeakAlertLevel.h \
		lmgroupgolay.h lmgroupmacro.h lmgroupmct.h lmgrouppoint.h \
		lmgroupripple.h lmgroupsa105.h lmgroupsa205.h lmgroupsa305.h \
		lmgroupsadigital.h lmgroupversacom.h test_lm_serialization.h \
		test_serialization.h test_serialization_helper.h
loadmanager.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		msg_signal.h msg_cmd.h msg_pcrequest.h msg_pcreturn.h \
		msg_dbchg.h loadmanager.h connection_client.h \
		lmcontrolareastore.h observe.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h lmcontrolareatrigger.h \
		ctidate.h lmid.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h resolvers.h db_entry_defines.h \
		devicetypes.h clistener.h connection_server.h \
		connection_listener.h debug_timer.h amq_constants.h
main.obj:	precompiled.h lmcontrolareastore.h observe.h types.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h lmcontrolarea.h dbmemobject.h connection.h \
		exchange.h dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h \
		netports.h mutex.h guard.h dsm2err.h words.h optional.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmid.h loadmanager.h \
		connection_client.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h lmservice.h cservice.h clistener.h \
		connection_server.h connection_listener.h precomp.h Monitor.h \
		CServiceConfig.h rtdb.h hashkey.h hash_functions.h
sepcyclegear.obj:	precompiled.h sepcyclegear.h lmprogramdirect.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h lmprogrambase.h dbmemobject.h \
		observe.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		tbl_lmprogramhistory.h smartgearbase.h \
		GroupControlInterface.h SepControlInterface.h
septempoffsetgear.obj:	precompiled.h septempoffsetgear.h \
		lmprogramdirect.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h dsm2err.h words.h optional.h database_reader.h \
		row_reader.h boost_time.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		tbl_lmprogramhistory.h lmprogramthermostatgear.h \
		smartgearbase.h GroupControlInterface.h SepControlInterface.h \
		ctistring.h
test_lmprogram.obj:	lmprogramdirect.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h dsm2err.h words.h optional.h database_reader.h \
		row_reader.h boost_time.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		tbl_lmprogramhistory.h lmprogramcontrolwindow.h lmutility.h \
		lmconstraint.h lmmessage.h ConstraintViolation.h executor.h \
		ctdpcptrq.h msg_server_req.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h test_reader.h
test_lm_constraintviolations.obj:	ConstraintViolation.h ctitime.h \
		dlldefs.h collectable.h ctidate.h logger.h thread.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h CtiPCPtrQueue.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
