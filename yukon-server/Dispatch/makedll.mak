# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

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
-I$(CPARMS)\include \
-I$(BOOST) \
-I$(SQLAPI)\include \
-I$(RW) \
-I$(ACTIVEMQ) \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
;$(RTDB)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \
;$(BOOST) \
;$(RW) \
;$(ACTIVEMQ) \
;$(ACTIVEMQ)\cms \
;$(ACTIVEMQ)\activemq\library \



CTIVGLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\ctidbres.lib \
$(COMPILEBASE)\lib\cticparms.lib \
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


ALL:            ctivg.dll


$(DISPATCH_VG_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) /DCTIVANGOGH $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DLLOBJS)]



ctivg.dll:      $(DISPATCH_VG_FULLBUILD) $(DLLOBJS) Makedll.mak
                @build -nologo -f $(_InputFile) id
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(DLLOBJS) id_vgdll.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIVGLIBS) /Fe..\$@
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
applist.obj:	yukon.h precompiled.h types.h ctidbgmem.h applist.h \
		con_mgr.h connection.h dlldefs.h exchange.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h ctibase.h \
		ctinexus.h
cbctest.obj:	yukon.h precompiled.h types.h ctidbgmem.h exchange.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_cmd.h msg_reg.h msg_signal.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_pcreturn.h \
		msg_multi.h
cmdtest.obj:	yukon.h precompiled.h types.h ctidbgmem.h queue.h \
		cparms.h dlldefs.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h exchange.h message.h collectable.h msg_cmd.h \
		msg_reg.h msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h connection.h
control_history_association.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h control_history_association.h \
		tbl_lm_controlhist.h row_reader.h ctitime.h dlldefs.h \
		dbmemobject.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_connection.h
con_mgr_vg.obj:	yukon.h precompiled.h types.h ctidbgmem.h con_mgr_vg.h \
		exchange.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ctibase.h ctinexus.h
ctivangogh.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		collectable.h counter.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h numstr.h sorted_vector.h \
		mutex.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h queent.h queue.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h con_mgr.h \
		connection.h exchange.h message.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h ctibase.h \
		ctinexus.h msg_cmd.h msg_pcrequest.h msg_signal.h \
		msg_commerrorhistory.h msg_notif_alarm.h msg_server_req.h \
		msg_server_resp.h ctivangogh.h con_mgr_vg.h vgexe_factory.h \
		executor.h exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h \
		server_b.h critical_Section.h smartmap.h \
		readers_writer_lock.h dev_base_lite.h dbmemobject.h \
		msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
		msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
		pending_info.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
		resolvers.h db_entry_defines.h pt_dyn_base.h tbl_pt_base.h \
		desolvers.h tbl_pt_property.h tbl_pt_trigger.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		pttrigger.h mgr_point.h fifo_multiset.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_commerrhist.h tbl_pt_limit.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h rte_base.h tbl_pao_lite.h tbl_rtcomm.h \
		tbl_base.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h tbl_dyn_ptalarming.h thread_monitor.h \
		thread_register_data.h ThreadStatusKeeper.h mgr_ptclients.h \
		ptconnect.h slctpnt.h database_writer.h row_writer.h dllvg.h \
		dllyukon.h ctidate.h debug_timer.h millisecond_timer.h
dispmain.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h dispsvc.h cservice.h dllvg.h CServiceConfig.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h ctibase.h ctinexus.h \
		logger.h thread.h CtiPCPtrQueue.h
dispsvc.obj:	yukon.h precompiled.h types.h ctidbgmem.h dispsvc.h \
		cservice.h dlldefs.h
dllvg.obj:	yukon.h precompiled.h types.h ctidbgmem.h configparms.h \
		dlldefs.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h dllvg.h logger.h thread.h \
		CtiPCPtrQueue.h
exe_ptchg.obj:	yukon.h precompiled.h types.h ctidbgmem.h message.h \
		ctitime.h dlldefs.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h con_mgr_vg.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h vgexe_factory.h executor.h \
		exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h \
		con_mgr.h connection.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ctibase.h ctinexus.h ctivangogh.h \
		server_b.h critical_Section.h smartmap.h \
		readers_writer_lock.h dev_base_lite.h dbmemobject.h \
		msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
		msg_commerrorhistory.h msg_lmcontrolhistory.h msg_tag.h \
		pendingopthread.h pendable.h pending_info.h msg_signal.h \
		tbl_lm_controlhist.h pt_numeric.h pt_base.h resolvers.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		tbl_pt_property.h tbl_pt_trigger.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		pttrigger.h mgr_point.h fifo_multiset.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_commerrhist.h tbl_pt_limit.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h
exe_signal.obj:	yukon.h precompiled.h types.h ctidbgmem.h message.h \
		ctitime.h dlldefs.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h con_mgr_vg.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h vgexe_factory.h executor.h \
		exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h \
		con_mgr.h connection.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ctibase.h ctinexus.h ctivangogh.h \
		server_b.h critical_Section.h smartmap.h \
		readers_writer_lock.h dev_base_lite.h dbmemobject.h \
		msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
		msg_commerrorhistory.h msg_lmcontrolhistory.h msg_tag.h \
		pendingopthread.h pendable.h pending_info.h msg_signal.h \
		tbl_lm_controlhist.h pt_numeric.h pt_base.h resolvers.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		tbl_pt_property.h tbl_pt_trigger.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		pttrigger.h mgr_point.h fifo_multiset.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_commerrhist.h tbl_pt_limit.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		exe_signal.h
id_vg.obj:	yukon.h precompiled.h types.h ctidbgmem.h id_vg.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h
id_vgdll.obj:	yukon.h precompiled.h types.h ctidbgmem.h id_vgdll.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		numstr.h sorted_vector.h
mgr_ptclients.obj:	yukon.h precompiled.h types.h ctidbgmem.h dllvg.h \
		dlldefs.h pt_base.h row_reader.h ctitime.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h dbaccess.h sema.h desolvers.h \
		tbl_pt_property.h tbl_pt_trigger.h logger.h thread.h \
		CtiPCPtrQueue.h mgr_ptclients.h mgr_point.h smartmap.h \
		readers_writer_lock.h critical_section.h fifo_multiset.h \
		msg_pdata.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		ptconnect.h hashkey.h hash_functions.h con_mgr.h connection.h \
		exchange.h string_utility.h msg_multi.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ctibase.h ctinexus.h \
		server_b.h pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		tbl_pt_limit.h rtdb.h tbl_rawpthistory.h devicetypes.h \
		msg_pcreturn.h msg_signal.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h debug_timer.h con_mgr_vg.h vgexe_factory.h \
		executor.h exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h \
		msg_cmd.h
pcmtest.obj:	yukon.h precompiled.h types.h ctidbgmem.h mgr_ptclients.h \
		dlldefs.h mgr_point.h smartmap.h boostutil.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h dllbase.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h fifo_multiset.h pt_base.h row_reader.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h \
		desolvers.h tbl_pt_property.h tbl_pt_trigger.h msg_pdata.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h boost_time.h ptconnect.h hashkey.h \
		hash_functions.h con_mgr.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ctibase.h ctinexus.h server_b.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h tbl_pt_limit.h rtdb.h \
		tbl_rawpthistory.h
pendingopthread.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		counter.h guard.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h mutex.h \
		cparms.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h dllvg.h mgr_ptclients.h \
		mgr_point.h smartmap.h readers_writer_lock.h \
		critical_section.h fifo_multiset.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h desolvers.h tbl_pt_property.h \
		tbl_pt_trigger.h msg_pdata.h message.h collectable.h \
		ptconnect.h hashkey.h hash_functions.h con_mgr.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h server_b.h pt_dyn_dispatch.h tbl_pt_alarm.h \
		tbl_ptdispatch.h tbl_pt_limit.h rtdb.h tbl_rawpthistory.h \
		msg_cmd.h pendingopthread.h pendable.h pending_info.h \
		msg_signal.h tbl_lm_controlhist.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h millisecond_timer.h \
		control_history_association.h amq_connection.h activemqcpp.h \
		ControlHistoryAssociationResponse.h
pending_info.obj:	yukon.h precompiled.h types.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h sorted_vector.h \
		CtiPCPtrQueue.h pending_info.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_signal.h tbl_lm_controlhist.h dbmemobject.h
porterpoker.obj:	yukon.h precompiled.h types.h ctidbgmem.h queue.h \
		cparms.h dlldefs.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h exchange.h message.h collectable.h msg_cmd.h \
		msg_reg.h msg_dbchg.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h counter.h pt_accum.h \
		pt_numeric.h pt_base.h dbmemobject.h resolvers.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		tbl_pt_property.h tbl_pt_trigger.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_accum.h tbl_pt_accumhistory.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
ptconnect.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h msg_pcreturn.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h pt_base.h dbmemobject.h resolvers.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		tbl_pt_property.h tbl_pt_trigger.h ptconnect.h hashkey.h \
		hash_functions.h con_mgr.h connection.h exchange.h \
		string_utility.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ctibase.h ctinexus.h server_b.h \
		critical_Section.h smartmap.h readers_writer_lock.h
signalmanager.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h logger.h thread.h CtiPCPtrQueue.h \
		pointdefs.h signalmanager.h msg_signal.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointtypes.h tbl_dyn_ptalarming.h \
		ctibase.h ctinexus.h dbmemobject.h tbl_pt_alarm.h resolvers.h \
		db_entry_defines.h desolvers.h
sigsrctest.obj:	yukon.h precompiled.h types.h ctidbgmem.h queue.h \
		cparms.h dlldefs.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h exchange.h message.h collectable.h msg_cmd.h \
		msg_reg.h msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_signal.h msg_ptreg.h connection.h
sigtest.obj:	yukon.h precompiled.h types.h ctidbgmem.h queue.h \
		cparms.h dlldefs.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h exchange.h message.h collectable.h msg_cmd.h \
		msg_reg.h msg_signal.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_pcreturn.h msg_multi.h
tagmanager.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h pointdefs.h tagmanager.h \
		msg_tag.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointtypes.h \
		queue.h cparms.h configkey.h configval.h string_utility.h \
		tbl_dyn_pttag.h ctibase.h ctinexus.h dbmemobject.h tbl_tag.h \
		tbl_taglog.h
test2.obj:	yukon.h precompiled.h types.h ctidbgmem.h queue.h cparms.h \
		dlldefs.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h msg_cmd.h msg_pcreturn.h
test_mgr_ptclients.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		mgr_ptclients.h dlldefs.h mgr_point.h smartmap.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h dllbase.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h fifo_multiset.h pt_base.h row_reader.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h \
		desolvers.h tbl_pt_property.h tbl_pt_trigger.h msg_pdata.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h boost_time.h ptconnect.h hashkey.h \
		hash_functions.h con_mgr.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ctibase.h ctinexus.h server_b.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h tbl_pt_limit.h rtdb.h \
		tbl_rawpthistory.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h
test_signalmanager.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_pt_alarm.h dlldefs.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h row_reader.h \
		signalmanager.h msg_signal.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h
vangogh.obj:	yukon.h precompiled.h types.h ctidbgmem.h cparms.h \
		dlldefs.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h ctivangogh.h con_mgr.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h con_mgr_vg.h vgexe_factory.h executor.h \
		exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h \
		server_b.h critical_Section.h smartmap.h \
		readers_writer_lock.h dev_base_lite.h dbmemobject.h \
		msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
		msg_commerrorhistory.h msg_lmcontrolhistory.h msg_tag.h \
		pendingopthread.h pendable.h pending_info.h msg_signal.h \
		tbl_lm_controlhist.h pt_numeric.h pt_base.h resolvers.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		tbl_pt_property.h tbl_pt_trigger.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		pttrigger.h mgr_point.h fifo_multiset.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_commerrhist.h tbl_pt_limit.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		dllvg.h stdexcepthdlr.h
vgexe_factory.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		executorfactory.h collectable.h message.h ctitime.h dlldefs.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h executor.h exe_cmd.h \
		exe_reg.h vgexe_factory.h exe_ptchg.h ctibase.h ctinexus.h
#ENDUPDATE#
