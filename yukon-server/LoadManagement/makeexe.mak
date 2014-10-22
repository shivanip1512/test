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
lmgroupecobee.obj \
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
ecobeeCycleGear.obj \
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
              $(MANIFEST_TOOL) -manifest ..\$@.manifest -outputresource:..\$@;1
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
		connection.h dlldefs.h message.h ctitime.h ctidbgmem.h \
		collectable.h loggable.h msg_multi.h msg_pdata.h yukon.h \
		types.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h connection_listener.h \
		lmmessage.h lmcontrolarea.h dbmemobject.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmcontrolareastore.h lmid.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		executor.h msg_server_req.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h amq_constants.h
constraintviolation.obj:	precompiled.h ConstraintViolation.h ctitime.h \
		dlldefs.h collectable.h lmid.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h
ecobeecyclegear.obj:	precompiled.h GroupControlInterface.h \
		EcobeeControlInterface.h ecobeeCycleGear.h lmprogramdirect.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		lmprogrambase.h dbmemobject.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h loggable.h \
		lmgroupbase.h msg_pcrequest.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h logger.h streamBuffer.h \
		string_util.h exception_helper.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h msg_cmd.h row_reader.h \
		database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirectgear.h \
		lmcontrolarea.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h tbl_lmprogramhistory.h \
		smartgearbase.h
executor.obj:	precompiled.h msg_server_req.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h loggable.h \
		msg_server_resp.h msg_signal.h yukon.h types.h executor.h \
		lmmessage.h lmcontrolarea.h dbmemobject.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		string_utility.h readers_writer_lock.h connection_base.h \
		worker_thread.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h clistener.h \
		connection_server.h connection_listener.h \
		lmcontrolareastore.h lmid.h loadmanager.h connection_client.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h lmprogramenergyexchange.h \
		lmenergyexchangeoffer.h lmenergyexchangeofferrevision.h \
		lmenergyexchangehourlyoffer.h lmenergyexchangecustomer.h \
		lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lmprogramcontrolwindow.h \
		devicetypes.h lmconstraint.h lmutility.h \
		GroupControlInterface.h BeatThePeakControlInterface.h \
		BeatThePeakAlertLevel.h
lmcicustomerbase.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmcicustomerbase.h msg_pcrequest.h message.h \
		collectable.h row_reader.h lmid.h loadmanager.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_connection.h database_reader.h \
		boost_time.h configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		msg_cmd.h lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h lmcontrolareatrigger.h \
		ctidate.h executor.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		resolvers.h db_entry_defines.h
lmconstraint.obj:	precompiled.h lmconstraint.h lmprogramdirect.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		lmprogrambase.h dbmemobject.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h loggable.h \
		lmgroupbase.h msg_pcrequest.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h logger.h streamBuffer.h \
		string_util.h exception_helper.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h msg_cmd.h row_reader.h \
		database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirectgear.h \
		lmcontrolarea.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h tbl_lmprogramhistory.h \
		lmutility.h lmmessage.h ConstraintViolation.h \
		lmprogramcontrolwindow.h lmid.h mgr_season.h mgr_holiday.h
lmcontrolarea.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h msg_signal.h message.h collectable.h \
		lmcontrolarea.h dbmemobject.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmid.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		lmprogramcontrolwindow.h devicetypes.h loadmanager.h \
		connection_client.h lmcontrolareastore.h executor.h \
		msg_server_req.h lmmessage.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h resolvers.h db_entry_defines.h lmconstraint.h \
		lmutility.h database_writer.h row_writer.h database_util.h
lmcontrolareastore.obj:	precompiled.h mgr_holiday.h ctidate.h \
		dlldefs.h ctitime.h mutex.h guard.h utility.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h atomic.h \
		mgr_season.h msg_signal.h message.h collectable.h \
		lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		string_utility.h readers_writer_lock.h connection_base.h \
		worker_thread.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h lmid.h lmcurtailcustomer.h \
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
		ecobeeCycleGear.h resolvers.h db_entry_defines.h desolvers.h \
		devicetypes.h database_util.h database_writer.h row_writer.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h msg_dbchg.h loadmanager.h connection_client.h \
		executor.h msg_server_req.h lmmessage.h ConstraintViolation.h \
		lmfactory.h tbl_paoexclusion.h debug_timer.h clistener.h \
		connection_server.h connection_listener.h \
		lmprogrambeatthepeakgear.h
lmcontrolareatrigger.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmcontrolareatrigger.h row_reader.h \
		database_connection.h collectable.h lmid.h lmprogrambase.h \
		dbmemobject.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h ctidate.h loadmanager.h connection_client.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_reader.h boost_time.h configkey.h configval.h \
		string_utility.h readers_writer_lock.h connection_base.h \
		worker_thread.h lmcontrolareastore.h lmcontrolarea.h \
		executor.h msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		database_writer.h row_writer.h database_util.h
lmcurtailcustomer.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmcurtailcustomer.h msg_pcrequest.h message.h \
		collectable.h lmcicustomerbase.h row_reader.h \
		database_connection.h lmid.h loadmanager.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h msg_cmd.h \
		lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h lmcontrolareatrigger.h \
		ctidate.h executor.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h resolvers.h db_entry_defines.h \
		database_writer.h row_writer.h database_util.h
lmenergyexchangecustomer.obj:	precompiled.h dbaccess.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmenergyexchangecustomer.h lmcicustomerbase.h \
		msg_pcrequest.h message.h collectable.h row_reader.h \
		lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lmid.h loadmanager.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_connection.h database_reader.h \
		boost_time.h configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		msg_cmd.h lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h lmcontrolareatrigger.h \
		ctidate.h executor.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		resolvers.h db_entry_defines.h
lmenergyexchangecustomerreply.obj:	precompiled.h dbaccess.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h row_reader.h collectable.h \
		lmid.h loadmanager.h connection_client.h connection.h \
		message.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		msg_cmd.h lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h executor.h msg_server_req.h \
		lmmessage.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h resolvers.h db_entry_defines.h database_writer.h \
		row_writer.h database_util.h
lmenergyexchangehourlycustomer.obj:	precompiled.h dbaccess.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmenergyexchangehourlycustomer.h row_reader.h \
		collectable.h lmid.h loadmanager.h connection_client.h \
		connection.h message.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		msg_cmd.h lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h executor.h msg_server_req.h \
		lmmessage.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h resolvers.h db_entry_defines.h database_writer.h \
		row_writer.h database_util.h
lmenergyexchangehourlyoffer.obj:	precompiled.h dbaccess.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmenergyexchangehourlyoffer.h row_reader.h \
		collectable.h lmid.h loadmanager.h connection_client.h \
		connection.h message.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		msg_cmd.h lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h executor.h msg_server_req.h \
		lmmessage.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h resolvers.h db_entry_defines.h database_writer.h \
		row_writer.h database_util.h
lmenergyexchangeoffer.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmid.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		row_reader.h collectable.h rwutil.h database_connection.h \
		database_reader.h boost_time.h database_writer.h row_writer.h \
		database_util.h
lmenergyexchangeofferrevision.obj:	precompiled.h dbaccess.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmenergyexchangeofferrevision.h \
		lmenergyexchangehourlyoffer.h row_reader.h collectable.h \
		lmid.h loadmanager.h connection_client.h connection.h \
		message.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		msg_cmd.h lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h executor.h msg_server_req.h \
		lmmessage.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h resolvers.h db_entry_defines.h database_writer.h \
		row_writer.h database_util.h
lmfactory.obj:	precompiled.h lmfactory.h lmgroupbase.h boostutil.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		dbmemobject.h msg_pcrequest.h message.h collectable.h \
		loggable.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h string_util.h exception_helper.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		resolvers.h pointtypes.h db_entry_defines.h devicetypes.h \
		lmgroupversacom.h lmgroupdigisep.h GroupControlInterface.h \
		SepControlInterface.h lmgroupemetcon.h lmgroupexpresscom.h \
		BeatThePeakControlInterface.h BeatThePeakAlertLevel.h \
		lmgroupmct.h lmgroupripple.h lmgrouppoint.h lmgroupsa105.h \
		lmgroupsa205.h lmgroupsa305.h lmgroupsadigital.h \
		lmgroupgolay.h lmgroupmacro.h lmgroupecobee.h \
		ecobeeControlInterface.h
lmgroupbase.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmgroupbase.h dbmemobject.h msg_pcrequest.h \
		message.h collectable.h msg_cmd.h row_reader.h \
		database_connection.h lmid.h loadmanager.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h resolvers.h db_entry_defines.h database_writer.h \
		row_writer.h database_util.h
lmgroupdigisep.obj:	precompiled.h lmgroupdigisep.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		dbmemobject.h msg_pcrequest.h message.h collectable.h \
		loggable.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h string_util.h exception_helper.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		GroupControlInterface.h SepControlInterface.h lmid.h \
		amq_connection.h thread.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h \
		lmsepcontrolmessage.h lmseprestoremessage.h
lmgroupecobee.obj:	precompiled.h lmid.h ctitime.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h amq_connection.h thread.h mutex.h \
		guard.h StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h LMGroupEcobee.h lmgroupbase.h \
		dbmemobject.h msg_pcrequest.h message.h collectable.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		GroupControlInterface.h ecobeeControlInterface.h \
		LMEcobeeMessages.h
lmgroupemetcon.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmgroupemetcon.h lmgroupbase.h dbmemobject.h \
		msg_pcrequest.h message.h collectable.h msg_cmd.h \
		row_reader.h database_connection.h lmid.h loadmanager.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h
lmgroupexpresscom.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmgroupexpresscom.h lmgroupbase.h \
		dbmemobject.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h row_reader.h database_connection.h \
		BeatThePeakControlInterface.h BeatThePeakAlertLevel.h lmid.h \
		loadmanager.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_reader.h boost_time.h \
		configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h ctidate.h executor.h msg_server_req.h \
		lmmessage.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h ctistring.h ctitokenizer.h \
		lmProgramThermostatGear.h
lmgroupgolay.obj:	precompiled.h lmgroupgolay.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		dbmemobject.h msg_pcrequest.h message.h collectable.h \
		loggable.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h string_util.h exception_helper.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmid.h
lmgroupmacro.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmgroupmacro.h lmgroupbase.h dbmemobject.h \
		msg_pcrequest.h message.h collectable.h msg_cmd.h \
		row_reader.h database_connection.h lmid.h loadmanager.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h
lmgroupmct.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmgroupmct.h lmgroupemetcon.h lmgroupbase.h \
		dbmemobject.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h row_reader.h database_connection.h lmid.h \
		loadmanager.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_reader.h boost_time.h \
		configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h ctidate.h executor.h msg_server_req.h \
		lmmessage.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h
lmgrouppoint.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmgrouppoint.h lmgroupbase.h dbmemobject.h \
		msg_pcrequest.h message.h collectable.h msg_cmd.h \
		row_reader.h database_connection.h lmid.h loadmanager.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h
lmgroupripple.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmgroupripple.h lmgroupbase.h dbmemobject.h \
		msg_pcrequest.h message.h collectable.h msg_cmd.h \
		row_reader.h database_connection.h lmid.h loadmanager.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h
lmgroupsa105.obj:	precompiled.h lmgroupsa105.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		dbmemobject.h msg_pcrequest.h message.h collectable.h \
		loggable.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h string_util.h exception_helper.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmid.h
lmgroupsa205.obj:	precompiled.h lmgroupsa205.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		dbmemobject.h msg_pcrequest.h message.h collectable.h \
		loggable.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h string_util.h exception_helper.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmid.h
lmgroupsa305.obj:	precompiled.h lmgroupsa305.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		dbmemobject.h msg_pcrequest.h message.h collectable.h \
		loggable.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h string_util.h exception_helper.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmid.h
lmgroupsadigital.obj:	precompiled.h lmgroupsadigital.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		dbmemobject.h msg_pcrequest.h message.h collectable.h \
		loggable.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h string_util.h exception_helper.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmid.h
lmgroupversacom.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmgroupbase.h dbmemobject.h msg_pcrequest.h \
		message.h collectable.h msg_cmd.h row_reader.h \
		database_connection.h lmgroupversacom.h lmid.h loadmanager.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h msg_server_req.h lmmessage.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h
lmmessage.obj:	precompiled.h lmmessage.h ctitime.h dlldefs.h message.h \
		ctidbgmem.h collectable.h loggable.h lmcontrolarea.h \
		dbmemobject.h connection.h msg_multi.h msg_pdata.h yukon.h \
		types.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmid.h
lmprogrambase.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmid.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h row_reader.h database_connection.h \
		lmcontrolareatrigger.h ctidate.h lmprogramcontrolwindow.h \
		loadmanager.h connection_client.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_reader.h \
		boost_time.h configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		lmcontrolareastore.h lmcontrolarea.h executor.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		resolvers.h db_entry_defines.h mgr_holiday.h mgr_season.h \
		lmutility.h database_writer.h row_writer.h database_util.h
lmprogrambeatthepeakgear.obj:	precompiled.h lmprogrambeatthepeakgear.h \
		lmprogramdirect.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h lmgroupbase.h \
		msg_pcrequest.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirectgear.h \
		lmcontrolarea.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h tbl_lmprogramhistory.h \
		smartgearbase.h BeatThePeakAlertLevel.h \
		BeatThePeakControlInterface.h
lmprogramcontrolwindow.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmid.h lmprogramcontrolwindow.h ctidate.h \
		row_reader.h collectable.h pointdefs.h pointtypes.h \
		loadmanager.h connection_client.h connection.h message.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_connection.h database_reader.h \
		boost_time.h configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		msg_cmd.h lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h msg_server_req.h \
		lmmessage.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h lmutility.h
lmprogramcurtailment.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmid.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h row_reader.h database_connection.h \
		lmcontrolareatrigger.h ctidate.h loadmanager.h \
		connection_client.h connection.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_reader.h boost_time.h \
		configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		lmcontrolareastore.h lmcontrolarea.h executor.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		msg_notif_email.h database_writer.h row_writer.h \
		database_util.h
lmprogramdirect.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmprogramdirect.h lmprogrambase.h \
		dbmemobject.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h row_reader.h database_connection.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirectgear.h \
		lmcontrolarea.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h tbl_lmprogramhistory.h \
		lmgrouppoint.h devicetypes.h lmid.h desolvers.h loadmanager.h \
		connection_client.h lmcontrolareastore.h executor.h \
		msg_server_req.h lmmessage.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h msg_signal.h msg_notif_lmcontrol.h \
		lmprogramthermostatgear.h lmprogramcontrolwindow.h \
		lmconstraint.h lmutility.h database_writer.h row_writer.h \
		database_util.h smartgearbase.h lmgroupdigisep.h \
		GroupControlInterface.h SepControlInterface.h
lmprogramdirectgear.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmprogramdirectgear.h row_reader.h \
		collectable.h lmid.h lmprogrambase.h dbmemobject.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h database_connection.h \
		lmcontrolareatrigger.h ctidate.h loadmanager.h \
		connection_client.h connection.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_reader.h boost_time.h \
		configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		lmcontrolareastore.h lmcontrolarea.h executor.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h
lmprogramenergyexchange.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmid.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h row_reader.h database_connection.h \
		lmcontrolareatrigger.h ctidate.h loadmanager.h \
		connection_client.h connection.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_reader.h boost_time.h \
		configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		lmcontrolareastore.h lmcontrolarea.h executor.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		lmprogramenergyexchange.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h msg_notif_email.h
lmprogramthermostatgear.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h lmprogramthermostatgear.h \
		lmprogramdirectgear.h row_reader.h collectable.h lmid.h \
		lmprogrambase.h dbmemobject.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h database_connection.h \
		lmcontrolareatrigger.h ctidate.h loadmanager.h \
		connection_client.h connection.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_reader.h boost_time.h \
		configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		lmcontrolareastore.h lmcontrolarea.h executor.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h
lmservice.obj:	precompiled.h lmservice.h cservice.h dlldefs.h \
		loadmanager.h dbaccess.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h connection_client.h connection.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		msg_cmd.h lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h lmid.h executor.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		clistener.h connection_server.h connection_listener.h \
		id_loadmanagement.h module_util.h eventlog.h rtdb.h hashkey.h \
		hash_functions.h logManager.h
lmutility.obj:	precompiled.h timeperiod.h dlldefs.h lmutility.h \
		CtiTime.h ctidate.h lmprogrambase.h dbmemobject.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h types.h ctidbgmem.h \
		pointdefs.h pointtypes.h message.h loggable.h lmgroupbase.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h msg_pcrequest.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h logger.h streamBuffer.h \
		string_util.h exception_helper.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h msg_cmd.h row_reader.h \
		database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h lmcontrolarea.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_reader.h boost_time.h configkey.h configval.h \
		string_utility.h readers_writer_lock.h connection_base.h \
		worker_thread.h lmprogramcontrolwindow.h
lm_group_serialization.obj:	precompiled.h lm_group_serialization.h \
		lmgroupbase.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h dbmemobject.h msg_pcrequest.h message.h \
		collectable.h loggable.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmgroupdigisep.h GroupControlInterface.h \
		SepControlInterface.h lmgroupecobee.h \
		ecobeeControlInterface.h lmgroupemetcon.h lmgroupexpresscom.h \
		BeatThePeakControlInterface.h BeatThePeakAlertLevel.h \
		lmgroupgolay.h lmgroupmacro.h lmgroupmct.h lmgrouppoint.h \
		lmgroupripple.h lmgroupsa105.h lmgroupsa205.h lmgroupsa305.h \
		lmgroupsadigital.h lmgroupversacom.h
lm_message_serialization.obj:	precompiled.h lm_program_serialization.h \
		lmprogrambase.h dbmemobject.h msg_multi.h collectable.h \
		dlldefs.h msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h ctitime.h loggable.h lmgroupbase.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h msg_pcrequest.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h logger.h streamBuffer.h \
		string_util.h exception_helper.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h msg_cmd.h row_reader.h \
		database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h ctidate.h lmprogramcontrolwindow.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		lmprogramdirect.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_reader.h boost_time.h configkey.h configval.h \
		string_utility.h readers_writer_lock.h connection_base.h \
		worker_thread.h tbl_lmprogramhistory.h \
		lmprogramenergyexchange.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lm_message_serialization.h \
		lmmessage.h ConstraintViolation.h
lm_program_serialization.obj:	precompiled.h lm_group_serialization.h \
		lmgroupbase.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h dbmemobject.h msg_pcrequest.h message.h \
		collectable.h loggable.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmgroupdigisep.h GroupControlInterface.h \
		SepControlInterface.h lmgroupecobee.h \
		ecobeeControlInterface.h lmgroupemetcon.h lmgroupexpresscom.h \
		BeatThePeakControlInterface.h BeatThePeakAlertLevel.h \
		lmgroupgolay.h lmgroupmacro.h lmgroupmct.h lmgrouppoint.h \
		lmgroupripple.h lmgroupsa105.h lmgroupsa205.h lmgroupsa305.h \
		lmgroupsadigital.h lmgroupversacom.h \
		lm_program_serialization.h lmprogrambase.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h lmcontrolareatrigger.h \
		ctidate.h lmprogramcontrolwindow.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h lmprogramdirect.h \
		lmprogramdirectgear.h lmcontrolarea.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_reader.h boost_time.h configkey.h configval.h \
		string_utility.h readers_writer_lock.h connection_base.h \
		worker_thread.h tbl_lmprogramhistory.h \
		lmprogramenergyexchange.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h
lm_server_client_serialization_test.obj:	precompiled.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h collectable.h loggable.h \
		msg_cmd.h msg_dbchg.h yukon.h types.h msg_lmcontrolhistory.h \
		pointdefs.h msg_multi.h msg_pdata.h pointtypes.h \
		msg_notif_alarm.h msg_notif_email.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		msg_notif_lmcontrol.h msg_pcrequest.h msg_pcreturn.h \
		msg_ptreg.h msg_queuedata.h Msg_reg.h msg_requestcancel.h \
		msg_server_req.h msg_server_resp.h msg_signal.h msg_tag.h \
		msg_trace.h connection_server.h connection.h queue.h cparms.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		string_utility.h readers_writer_lock.h connection_base.h \
		worker_thread.h connection_listener.h lmmessage.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
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
		test_serialization.h test_serialization_helper.h logManager.h \
		module_util.h
loadmanager.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h connection.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h msg_signal.h msg_cmd.h \
		msg_pcrequest.h msg_pcreturn.h msg_dbchg.h loadmanager.h \
		connection_client.h lmcontrolareastore.h lmcontrolarea.h \
		dbmemobject.h lmprogrambase.h lmgroupbase.h \
		lmcontrolareatrigger.h ctidate.h lmid.h executor.h \
		msg_server_req.h lmmessage.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		resolvers.h db_entry_defines.h devicetypes.h clistener.h \
		connection_server.h connection_listener.h debug_timer.h \
		amq_constants.h
main.obj:	precompiled.h lmcontrolareastore.h lmcontrolarea.h \
		dbmemobject.h connection.h dlldefs.h message.h ctitime.h \
		ctidbgmem.h collectable.h loggable.h msg_multi.h msg_pdata.h \
		yukon.h types.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		string_utility.h readers_writer_lock.h connection_base.h \
		worker_thread.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h lmid.h \
		loadmanager.h connection_client.h executor.h msg_server_req.h \
		lmmessage.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h lmservice.h cservice.h clistener.h \
		connection_server.h connection_listener.h precomp.h Monitor.h \
		CServiceConfig.h rtdb.h hashkey.h hash_functions.h \
		module_util.h
sepcyclegear.obj:	precompiled.h sepcyclegear.h lmprogramdirect.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		lmprogrambase.h dbmemobject.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h loggable.h \
		lmgroupbase.h msg_pcrequest.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h logger.h streamBuffer.h \
		string_util.h exception_helper.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h msg_cmd.h row_reader.h \
		database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirectgear.h \
		lmcontrolarea.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h tbl_lmprogramhistory.h \
		smartgearbase.h GroupControlInterface.h SepControlInterface.h
septempoffsetgear.obj:	precompiled.h septempoffsetgear.h \
		lmprogramdirect.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h lmgroupbase.h \
		msg_pcrequest.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirectgear.h \
		lmcontrolarea.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h tbl_lmprogramhistory.h \
		lmprogramthermostatgear.h smartgearbase.h \
		GroupControlInterface.h SepControlInterface.h ctistring.h
test_lmobjects.obj:	devicetypes.h lmutility.h CtiTime.h dlldefs.h \
		ctidate.h lmprogrambase.h dbmemobject.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h types.h ctidbgmem.h \
		pointdefs.h pointtypes.h message.h loggable.h lmgroupbase.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h msg_pcrequest.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h logger.h streamBuffer.h \
		string_util.h exception_helper.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h msg_cmd.h row_reader.h \
		database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h lmcontrolarea.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_reader.h boost_time.h configkey.h configval.h \
		string_utility.h readers_writer_lock.h connection_base.h \
		worker_thread.h test_reader.h lmgroupecobee.h \
		GroupControlInterface.h ecobeeControlInterface.h \
		ecobeeCycleGear.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h smartgearbase.h
test_lmprogram.obj:	lmprogramdirect.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h lmgroupbase.h \
		msg_pcrequest.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirectgear.h \
		lmcontrolarea.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_reader.h boost_time.h configkey.h \
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h tbl_lmprogramhistory.h \
		lmprogramcontrolwindow.h lmutility.h lmconstraint.h \
		lmmessage.h ConstraintViolation.h executor.h msg_server_req.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		test_reader.h
test_lmthermostatgear.obj:	lmutility.h CtiTime.h dlldefs.h ctidate.h \
		lmprogrambase.h dbmemobject.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h loggable.h lmgroupbase.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		msg_pcrequest.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h lmcontrolarea.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_reader.h boost_time.h configkey.h configval.h \
		string_utility.h readers_writer_lock.h connection_base.h \
		worker_thread.h test_reader.h lmProgramThermostatGear.h \
		lmprogramdirectgear.h lmGroupExpresscom.h \
		BeatThePeakControlInterface.h BeatThePeakAlertLevel.h
test_lm_constraintviolations.obj:	ConstraintViolation.h ctitime.h \
		dlldefs.h collectable.h ctidate.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
