# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(SIGNAL)\include \
-I$(SERVICE)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
;$(RTDB)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \



CTIVGLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\ctidbres.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\service.lib


DLLOBJS=\
ctivangogh.obj \
con_mgr_vg.obj \
dllvg.obj \
exe_ptchg.obj \
pending_info.obj \
pendingopthread.obj \
ptconnect.obj \
mgr_ptclients.obj \
signalmanager.obj \
tagmanager.obj \
vgexe_factory.obj \
control_history_association.obj \


DISPATCH_VG_FULLBUILD = $[Filename,$(OBJ),DispatchVGFullBuild,target]


PROGS_VERSION=\
ctivg.dll


ALL:            ctivg.dll


$(DISPATCH_VG_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) /DCTIVANGOGH $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DLLOBJS)]



ctivg.dll:      $(DISPATCH_VG_FULLBUILD) $(DLLOBJS) Makedll.mak $(OBJ)\ctivg.res
                @build -nologo -f $(_InputFile) id
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(DLLOBJS) id_vgdll.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIVGLIBS) /Fe..\$@ ctivg.res
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\ctivg.dll copy bin\ctivg.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\ctivg.lib copy bin\ctivg.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp


deps:
                scandeps -Output makedll.mak *.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /DCTIVANGOGH $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<


# The lines below accomplish the ID'ing of the project!
id:
            @build -nologo -f $(_InputFile) id_vgdll.obj

id_vgdll.obj:    id_vgdll.cpp include\id_vgdll.h


#UPDATE#
applist.obj:	precompiled.h applist.h con_mgr.h connection_server.h \
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
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h connection_listener.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h
control_history_association.obj:	precompiled.h \
		control_history_association.h tbl_lm_controlhist.h \
		row_reader.h ctitime.h dlldefs.h dbmemobject.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_connection.h
con_mgr_vg.obj:	precompiled.h con_mgr_vg.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h loggable.h \
		vgexe_factory.h executor.h yukon.h types.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection_server.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h
ctivangogh.obj:	precompiled.h collectable.h counter.h guard.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h critical_section.h atomic.h mutex.h cparms.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h queue.h \
		con_mgr.h connection_server.h connection.h message.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h readers_writer_lock.h connection_base.h \
		worker_thread.h connection_listener.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		msg_cmd.h msg_pcrequest.h msg_signal.h msg_notif_alarm.h \
		msg_server_req.h msg_server_resp.h ctivangogh.h con_mgr_vg.h \
		vgexe_factory.h executor.h exe_ptchg.h executorfactory.h \
		exe_cmd.h exe_reg.h server_b.h smartmap.h dev_base_lite.h \
		dbmemobject.h msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
		msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
		pending_info.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		connection_client.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h pt_analog.h tbl_pt_analog.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h tbl_pao_lite.h \
		tbl_rtcomm.h tbl_static_paoinfo.h encryption.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h tbl_dyn_ptalarming.h \
		thread_monitor.h thread_register_data.h ThreadStatusKeeper.h \
		mgr_ptclients.h mgr_point.h ptconnect.h tbl_pt_property.h \
		database_transaction.h database_writer.h database_util.h \
		database_exceptions.h dllvg.h dllyukon.h ctidate.h \
		debug_timer.h millisecond_timer.h std_helper.h \
		amq_constants.h module_util.h
dispmain.obj:	precompiled.h ctitime.h dlldefs.h dispsvc.h cservice.h \
		dllvg.h CServiceConfig.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h logManager.h module_util.h \
		connection_base.h
dispsvc.obj:	precompiled.h dispsvc.h cservice.h dlldefs.h
dllvg.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dllvg.h module_util.h cparms.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h
exe_ptchg.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h loggable.h con_mgr_vg.h vgexe_factory.h \
		executor.h yukon.h types.h exe_ptchg.h executorfactory.h \
		exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h connection_server.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h ctivangogh.h server_b.h \
		smartmap.h dev_base_lite.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h msg_lmcontrolhistory.h \
		msg_tag.h pendingopthread.h pendable.h pending_info.h \
		msg_signal.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		connection_client.h
exe_signal.obj:	precompiled.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h con_mgr_vg.h \
		vgexe_factory.h executor.h yukon.h types.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection_server.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h ctivangogh.h server_b.h \
		smartmap.h dev_base_lite.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h msg_lmcontrolhistory.h \
		msg_tag.h pendingopthread.h pendable.h pending_info.h \
		msg_signal.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		connection_client.h exe_signal.h
id_vg.obj:	precompiled.h id_vg.h module_util.h dlldefs.h ctitime.h
id_vgdll.obj:	precompiled.h id_vgdll.h module_util.h dlldefs.h \
		ctitime.h
mgr_ptclients.obj:	precompiled.h dllvg.h dlldefs.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h ctitime.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h mgr_ptclients.h mgr_point.h smartmap.h \
		readers_writer_lock.h msg_pdata.h message.h collectable.h \
		ptconnect.h hashkey.h hash_functions.h con_mgr.h \
		connection_server.h connection.h msg_multi.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_connection.h \
		database_reader.h boost_time.h configkey.h configval.h \
		connection_base.h worker_thread.h connection_listener.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h server_b.h pt_dyn_dispatch.h tbl_pt_alarm.h \
		tbl_ptdispatch.h tbl_pt_limit.h rtdb.h tbl_rawpthistory.h \
		row_writer.h tbl_pt_property.h database_transaction.h \
		devicetypes.h msg_pcreturn.h msg_signal.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_pt_control.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h debug_timer.h con_mgr_vg.h \
		vgexe_factory.h executor.h exe_ptchg.h executorfactory.h \
		exe_cmd.h exe_reg.h msg_cmd.h desolvers.h
pendingopthread.obj:	precompiled.h counter.h guard.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h critical_section.h atomic.h mutex.h cparms.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h dllvg.h \
		mgr_ptclients.h mgr_point.h pt_base.h dbmemobject.h \
		tbl_pt_base.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h smartmap.h readers_writer_lock.h msg_pdata.h \
		message.h collectable.h ptconnect.h hashkey.h \
		hash_functions.h con_mgr.h connection_server.h connection.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h connection_base.h \
		worker_thread.h connection_listener.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		server_b.h pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		tbl_pt_limit.h rtdb.h tbl_rawpthistory.h row_writer.h \
		tbl_pt_property.h msg_cmd.h pendingopthread.h pendable.h \
		pending_info.h msg_signal.h tbl_lm_controlhist.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h thread.h signalmanager.h \
		millisecond_timer.h database_transaction.h \
		control_history_association.h amq_connection.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		ControlHistoryAssociationResponse.h
pending_info.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h pending_info.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h collectable.h msg_signal.h \
		tbl_lm_controlhist.h row_reader.h dbmemobject.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_connection.h
porterpoker.obj:	precompiled.h queue.h cparms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h message.h collectable.h \
		msg_cmd.h msg_reg.h msg_dbchg.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h readers_writer_lock.h connection_base.h \
		worker_thread.h counter.h pt_accum.h pt_numeric.h pt_base.h \
		dbmemobject.h tbl_pt_base.h resolvers.h db_entry_defines.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h amq_constants.h logManager.h \
		module_util.h
ptconnect.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h msg_pcreturn.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h resolvers.h \
		db_entry_defines.h ptconnect.h hashkey.h hash_functions.h \
		con_mgr.h connection_server.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_connection.h \
		database_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h server_b.h smartmap.h
signalmanager.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h pointdefs.h sema.h signalmanager.h \
		msg_signal.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointtypes.h tbl_dyn_ptalarming.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dbmemobject.h row_reader.h database_connection.h \
		tbl_pt_alarm.h resolvers.h db_entry_defines.h \
		database_transaction.h
sigsrctest.obj:	precompiled.h queue.h cparms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h message.h collectable.h \
		msg_cmd.h msg_reg.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_signal.h msg_ptreg.h \
		connection_client.h connection.h readers_writer_lock.h \
		connection_base.h worker_thread.h amq_constants.h
sigtest.obj:	precompiled.h queue.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h message.h collectable.h \
		msg_cmd.h msg_reg.h msg_signal.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_pcreturn.h msg_multi.h \
		connection_client.h connection.h readers_writer_lock.h \
		connection_base.h worker_thread.h amq_constants.h
tagmanager.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h pointdefs.h sema.h tagmanager.h msg_tag.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointtypes.h \
		queue.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h thread.h tbl_dyn_pttag.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dbmemobject.h tbl_tag.h tbl_taglog.h database_transaction.h
test_mgr_ptclients.obj:	mgr_ptclients.h dlldefs.h mgr_point.h \
		pt_base.h dbmemobject.h tbl_pt_base.h row_reader.h ctitime.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h smartmap.h readers_writer_lock.h msg_pdata.h \
		message.h collectable.h ptconnect.h hashkey.h \
		hash_functions.h con_mgr.h connection_server.h connection.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		configkey.h configval.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h server_b.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h tbl_pt_limit.h rtdb.h \
		tbl_rawpthistory.h row_writer.h tbl_pt_property.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h
test_signalmanager.obj:	tbl_pt_alarm.h dlldefs.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h signalmanager.h msg_signal.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h
test_vangogh.obj:	ctivangogh.h con_mgr.h connection_server.h \
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
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h connection_listener.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		con_mgr_vg.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h server_b.h \
		smartmap.h dev_base_lite.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h msg_lmcontrolhistory.h \
		msg_tag.h pendingopthread.h pendable.h pending_info.h \
		msg_signal.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		connection_client.h ctidate.h
vangogh.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h ctivangogh.h con_mgr.h \
		connection_server.h connection.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h connection_listener.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		con_mgr_vg.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h server_b.h \
		smartmap.h dev_base_lite.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h msg_lmcontrolhistory.h \
		msg_tag.h pendingopthread.h pendable.h pending_info.h \
		msg_signal.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		connection_client.h dllvg.h stdexcepthdlr.h module_util.h
vgexe_factory.obj:	precompiled.h executorfactory.h collectable.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h loggable.h \
		executor.h yukon.h types.h exe_cmd.h exe_reg.h \
		vgexe_factory.h exe_ptchg.h ctibase.h \
		streamSocketConnection.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h socket_helper.h win_helper.h dllbase.h dsm2.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
