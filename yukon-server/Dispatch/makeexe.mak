include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(SERVICE)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(DBGHELP)\include \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
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


SIGTESTOBJS=\
$(PRECOMPILED_OBJ) \
sigtest.obj

PORTERPOKE=\
$(PRECOMPILED_OBJ) \
porterpoker.obj

SIGSRCTESTOBJS=\
$(PRECOMPILED_OBJ) \
sigsrctest.obj


VGOBJS= \
$(PRECOMPILED_OBJ) \
id_vg.obj \
dispmain.obj \
dispsvc.obj \
con_mgr_vg.obj \
vgexe_factory.obj \
vangogh.obj


CTIPROGS=\
dispatch.exe \
poker.exe \
sigsinktest.exe \
sigsrctest.exe

VGLIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\service.lib \
$(DBGHELP_LIBS) \

TESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \


PROGS_VERSION=\
$(CTIPROGS)


ALL:            $(CTIPROGS)

dispatch.exe:   $(VGOBJS) makeexe.mak $(OBJ)\dispatch.res
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$@ \
$(VGOBJS) id_vg.obj -link /LARGEADDRESSAWARE $(BOOST_LIBS) $(VGLIBS) dispatch.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	       -copy ..\bin\$(@B).pdb $(YUKONDEBUG)
                @%cd $(CWD)


sigsinktest.exe: $(SIGTESTOBJS) makeexe.mak $(OBJ)\sigsinktest.res
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$@ \
$(SIGTESTOBJS) -link /LARGEADDRESSAWARE $(BOOST_LIBS) $(TESTLIBS) sigsinktest.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	       -copy ..\bin\$(@B).pdb $(YUKONDEBUG)
                @%cd $(CWD)

poker.exe: $(PORTERPOKE) makeexe.mak $(OBJ)\poker.res
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$@ \
$(PORTERPOKE) -link /LARGEADDRESSAWARE $(BOOST_LIBS) $(TESTLIBS) poker.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	       -copy ..\bin\$(@B).pdb $(YUKONDEBUG)
                @%cd $(CWD)


sigsrctest.exe: $(SIGSRCTESTOBJS) makeexe.mak $(OBJ)\sigsrctest.res
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$@ \
$(SIGSRCTESTOBJS) -link /LARGEADDRESSAWARE $(BOOST_LIBS) $(TESTLIBS) sigsrctest.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	       -copy ..\bin\$(@B).pdb $(YUKONDEBUG)
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
               -@if exist bin\*.pdb copy bin\*.pdb $(YUKONDEBUG)


deps:
                scandeps -Output makeexe.mak *.cpp



clean:
        -del *.obj
        -del *.pch
        -del *.pdb
        -del *.sdb
        -del *.adb
        -del *.ilk
        -del *.exe
        -del *.pdb


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(CC) $(CCOPTS) $(CFLAGS) $(PCHFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################


#UPDATE#
control_history_association.obj:	precompiled.h \
		control_history_association.h tbl_lm_controlhist.h \
		row_reader.h ctitime.h dlldefs.h dbmemobject.h dbaccess.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h database_connection.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h
con_mgr_vg.obj:	precompiled.h con_mgr_vg.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h loggable.h \
		connectionHandle.h vgexe_factory.h executorfactory.h \
		exe_cmd.h executor.h yukon.h types.h msg_cmd.h con_mgr.h \
		connection_server.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h connection_listener.h dllbase.h
ctivangogh.obj:	precompiled.h collectable.h counter.h guard.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		critical_section.h mutex.h cparms.h queue.h con_mgr.h \
		connection_server.h connection.h message.h connectionHandle.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h \
		connection_listener.h dllbase.h msg_cmd.h msg_pcrequest.h \
		dsm2.h streamConnection.h immutable.h dsm2err.h words.h \
		optional.h macro_offset.h msg_signal.h msg_notif_alarm.h \
		msg_server_req.h msg_server_resp.h ctivangogh.h con_mgr_vg.h \
		vgexe_factory.h executorfactory.h exe_cmd.h executor.h \
		server_b.h smartmap.h dev_base_lite.h dbmemobject.h \
		msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
		msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
		pending_info.h tbl_lm_controlhist.h row_reader.h dbaccess.h \
		database_connection.h pt_numeric.h pt_base.h tbl_pt_base.h \
		resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h connection_client.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		tbl_pao_lite.h tbl_rtcomm.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_dyn_ptalarming.h thread_monitor.h thread_register_data.h \
		boost_time.h ThreadStatusKeeper.h mgr_ptclients.h mgr_point.h \
		ptconnect.h tbl_pt_property.h database_reader.h \
		database_transaction.h database_writer.h database_util.h \
		database_exceptions.h dllvg.h dllyukon.h ctidate.h \
		debug_timer.h millisecond_timer.h win_helper.h \
		amq_constants.h desolvers.h
dispmain.obj:	precompiled.h ctitime.h dlldefs.h dispsvc.h cservice.h \
		dllvg.h CServiceConfig.h dllbase.h os2_2w32.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logManager.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h connection_base.h
dispsvc.obj:	precompiled.h dispsvc.h cservice.h dlldefs.h
dllvg.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h dllvg.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h cparms.h
exe_ptchg.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h loggable.h connectionHandle.h con_mgr_vg.h \
		vgexe_factory.h executorfactory.h exe_cmd.h executor.h \
		yukon.h types.h msg_cmd.h con_mgr.h connection_server.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h \
		connection_listener.h dllbase.h ctivangogh.h server_b.h \
		smartmap.h dev_base_lite.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h dsm2.h streamConnection.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
		pending_info.h msg_signal.h tbl_lm_controlhist.h row_reader.h \
		dbaccess.h database_connection.h pt_numeric.h pt_base.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h connection_client.h \
		exe_ptchg.h
exe_signal.obj:	precompiled.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		con_mgr_vg.h vgexe_factory.h executorfactory.h exe_cmd.h \
		executor.h yukon.h types.h msg_cmd.h con_mgr.h \
		connection_server.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h connection_listener.h dllbase.h ctivangogh.h \
		server_b.h smartmap.h dev_base_lite.h dbmemobject.h \
		msg_dbchg.h msg_multiwrap.h msg_pcreturn.h dsm2.h \
		streamConnection.h immutable.h dsm2err.h words.h optional.h \
		macro_offset.h msg_lmcontrolhistory.h msg_tag.h \
		pendingopthread.h pendable.h pending_info.h msg_signal.h \
		tbl_lm_controlhist.h row_reader.h dbaccess.h \
		database_connection.h pt_numeric.h pt_base.h tbl_pt_base.h \
		resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h connection_client.h \
		exe_signal.h
id_vg.obj:	precompiled.h id_vg.h module_util.h dlldefs.h ctitime.h \
		version.h
id_vgdll.obj:	precompiled.h id_vgdll.h module_util.h dlldefs.h \
		ctitime.h version.h
mgr_ptclients.obj:	precompiled.h dllvg.h dlldefs.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h ctitime.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h mgr_ptclients.h mgr_point.h smartmap.h \
		readers_writer_lock.h guard.h msg_pdata.h message.h \
		collectable.h connectionHandle.h ptconnect.h con_mgr.h \
		connection_server.h connection.h msg_multi.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h \
		connection_listener.h server_b.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h database_connection.h \
		tbl_pt_limit.h rtdb.h tbl_rawpthistory.h row_writer.h \
		tbl_pt_property.h database_reader.h database_transaction.h \
		devicetypes.h msg_pcreturn.h dsm2.h streamConnection.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		msg_signal.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h tbl_pt_control.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h debug_timer.h \
		con_mgr_vg.h vgexe_factory.h executorfactory.h exe_cmd.h \
		executor.h msg_cmd.h desolvers.h
pendingopthread.obj:	precompiled.h counter.h guard.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h mutex.h \
		cparms.h dllvg.h mgr_ptclients.h mgr_point.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		smartmap.h readers_writer_lock.h msg_pdata.h message.h \
		collectable.h connectionHandle.h ptconnect.h con_mgr.h \
		connection_server.h connection.h msg_multi.h msg_ptreg.h \
		msg_reg.h queue.h connection_base.h worker_thread.h \
		timing_util.h concurrentSet.h connection_listener.h \
		server_b.h pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		database_connection.h tbl_pt_limit.h rtdb.h \
		tbl_rawpthistory.h row_writer.h tbl_pt_property.h msg_cmd.h \
		pendingopthread.h pendable.h pending_info.h msg_signal.h \
		tbl_lm_controlhist.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h \
		millisecond_timer.h database_reader.h database_transaction.h \
		control_history_association.h amq_connection.h \
		StreamableMessage.h ControlHistoryAssociationResponse.h \
		std_helper.h
pending_info.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h pending_info.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h collectable.h \
		connectionHandle.h msg_signal.h tbl_lm_controlhist.h \
		row_reader.h dbmemobject.h dbaccess.h dllbase.h \
		database_connection.h guard.h
porterpoker.obj:	precompiled.h queue.h cparms.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h message.h \
		collectable.h connectionHandle.h msg_cmd.h msg_reg.h \
		msg_dbchg.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h mutex.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h counter.h \
		pt_accum.h pt_numeric.h row_reader.h pt_base.h dbmemobject.h \
		tbl_pt_base.h dllbase.h dbaccess.h resolvers.h \
		db_entry_defines.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h amq_constants.h \
		logManager.h
ptconnect.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h msg_pcreturn.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h connectionHandle.h dsm2.h \
		streamConnection.h timing_util.h immutable.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h resolvers.h \
		db_entry_defines.h ptconnect.h con_mgr.h connection_server.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		concurrentSet.h connection_listener.h server_b.h smartmap.h
signalmanager.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h guard.h pointdefs.h sema.h signalmanager.h \
		msg_signal.h message.h collectable.h connectionHandle.h \
		msg_multi.h msg_pdata.h pointtypes.h mutex.h \
		tbl_dyn_ptalarming.h dbmemobject.h row_reader.h \
		database_connection.h tbl_pt_alarm.h resolvers.h \
		db_entry_defines.h database_transaction.h
sigsrctest.obj:	precompiled.h queue.h cparms.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h message.h \
		collectable.h connectionHandle.h msg_cmd.h msg_reg.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h dsm2.h streamConnection.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h msg_signal.h msg_ptreg.h connection_client.h \
		connection.h readers_writer_lock.h connection_base.h \
		worker_thread.h concurrentSet.h amq_constants.h
sigtest.obj:	precompiled.h queue.h cparms.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h message.h \
		collectable.h connectionHandle.h msg_cmd.h msg_reg.h \
		msg_signal.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_pcreturn.h msg_multi.h dsm2.h streamConnection.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h connection_client.h connection.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		concurrentSet.h amq_constants.h
tagmanager.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h guard.h pointdefs.h sema.h tagmanager.h msg_tag.h \
		message.h collectable.h connectionHandle.h msg_multi.h \
		msg_pdata.h pointtypes.h mutex.h queue.h cparms.h thread.h \
		tbl_dyn_pttag.h dbmemobject.h row_reader.h \
		database_connection.h tbl_tag.h tbl_taglog.h \
		database_reader.h database_transaction.h
test_mgr_ptclients.obj:	mgr_ptclients.h dlldefs.h mgr_point.h \
		pt_base.h dbmemobject.h tbl_pt_base.h row_reader.h ctitime.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h smartmap.h boostutil.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h readers_writer_lock.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		msg_pdata.h message.h collectable.h connectionHandle.h \
		ptconnect.h con_mgr.h connection_server.h connection.h \
		msg_multi.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h connection_listener.h server_b.h \
		pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		database_connection.h tbl_pt_limit.h rtdb.h \
		tbl_rawpthistory.h row_writer.h tbl_pt_property.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h rtdb_test_helpers.h mgr_dyn_paoinfo.h \
		tbl_dyn_paoinfo.h desolvers.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h dev_single.h dsm2.h streamConnection.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h tbl_pao_lite.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h xfer.h \
		config_exceptions.h exceptions.h test_reader.h \
		boost_test_helpers.h millisecond_timer.h ctidate.h
test_signalmanager.obj:	tbl_pt_alarm.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h ctitime.h signalmanager.h \
		msg_signal.h message.h collectable.h loggable.h \
		connectionHandle.h msg_multi.h msg_pdata.h pointdefs.h \
		mutex.h
test_vangogh.obj:	ctivangogh.h con_mgr.h connection_server.h \
		connection.h dlldefs.h message.h ctitime.h ctidbgmem.h \
		collectable.h loggable.h connectionHandle.h msg_multi.h \
		msg_pdata.h yukon.h types.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h \
		connection_listener.h dllbase.h con_mgr_vg.h vgexe_factory.h \
		executorfactory.h exe_cmd.h executor.h msg_cmd.h server_b.h \
		smartmap.h dev_base_lite.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h dsm2.h streamConnection.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
		pending_info.h msg_signal.h tbl_lm_controlhist.h row_reader.h \
		dbaccess.h database_connection.h pt_numeric.h pt_base.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h connection_client.h \
		ctidate.h
vangogh.obj:	precompiled.h cparms.h dlldefs.h ctivangogh.h con_mgr.h \
		connection_server.h connection.h message.h ctitime.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		msg_multi.h msg_pdata.h yukon.h types.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h mutex.h queue.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h \
		connection_listener.h dllbase.h con_mgr_vg.h vgexe_factory.h \
		executorfactory.h exe_cmd.h executor.h msg_cmd.h server_b.h \
		smartmap.h dev_base_lite.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h dsm2.h streamConnection.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
		pending_info.h msg_signal.h tbl_lm_controlhist.h row_reader.h \
		dbaccess.h database_connection.h pt_numeric.h pt_base.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h connection_client.h dllvg.h \
		stdexcepthdlr.h
vgexe_factory.obj:	precompiled.h vgexe_factory.h executorfactory.h \
		dlldefs.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h executor.h yukon.h types.h \
		exe_ptchg.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
