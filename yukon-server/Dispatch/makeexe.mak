include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(SERVICE)\include \
-I$(MSG)\include \
-I$(RW) \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(DBGHELP)\include \
-I$(ACTIVEMQ)\include \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
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


TSTEST=\
tstest.obj

SIGTESTOBJS=\
sigtest.obj

PORTERPOKE=\
porterpoker.obj

SIGSRCTESTOBJS=\
sigsrctest.obj


VGOBJS= \
id_vg.obj \
dispmain.obj \
dispsvc.obj \
con_mgr_vg.obj \
vgexe_factory.obj \
vangogh.obj


WINLIBS=kernel32.lib user32.lib
SOCKSLIB=wsock32.lib

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
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(VGOBJS) id_vg.obj -link $(RWLIBS) $(BOOST_LIBS) $(VGLIBS) dispatch.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


sigsinktest.exe: $(SIGTESTOBJS) makeexe.mak $(OBJ)\sigsinktest.res
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(SIGTESTOBJS) -link $(RWLIBS) $(BOOST_LIBS) $(TESTLIBS) sigsinktest.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

poker.exe: $(PORTERPOKE) makeexe.mak $(OBJ)\poker.res
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(PORTERPOKE) -link $(RWLIBS) $(BOOST_LIBS) $(TESTLIBS) poker.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


sigsrctest.exe: $(SIGSRCTESTOBJS) makeexe.mak $(OBJ)\sigsrctest.res
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(SIGSRCTESTOBJS) -link $(RWLIBS) $(BOOST_LIBS) $(TESTLIBS) sigsrctest.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


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


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################


#UPDATE#
applist.obj:	precompiled.h applist.h con_mgr.h connection.h dlldefs.h \
		exchange.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ctibase.h ctinexus.h
control_history_association.obj:	precompiled.h \
		control_history_association.h tbl_lm_controlhist.h \
		row_reader.h ctitime.h dlldefs.h dbmemobject.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_connection.h
con_mgr_vg.obj:	precompiled.h con_mgr_vg.h exchange.h dlldefs.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		vgexe_factory.h executor.h exe_ptchg.h executorfactory.h \
		exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h ctibase.h \
		ctinexus.h
ctivangogh.obj:	precompiled.h collectable.h counter.h guard.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h mutex.h cparms.h rwutil.h yukon.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h queent.h queue.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h con_mgr.h connection.h \
		exchange.h message.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h ctibase.h ctinexus.h \
		msg_cmd.h msg_pcrequest.h msg_signal.h msg_commerrorhistory.h \
		msg_notif_alarm.h msg_server_req.h msg_server_resp.h \
		ctivangogh.h con_mgr_vg.h vgexe_factory.h executor.h \
		exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h server_b.h \
		critical_Section.h smartmap.h readers_writer_lock.h \
		dev_base_lite.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_lmcontrolhistory.h msg_tag.h \
		pendingopthread.h pendable.h pending_info.h \
		tbl_lm_controlhist.h pt_numeric.h pt_base.h tbl_pt_base.h \
		resolvers.h db_entry_defines.h desolvers.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pttrigger.h \
		mgr_point.h tbl_pt_trigger.h tagmanager.h tbl_dyn_pttag.h \
		tbl_tag.h tbl_taglog.h tbl_state_grp.h tbl_state.h \
		tbl_alm_ngroup.h tbl_commerrhist.h tbl_pt_limit.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		tbl_pao_lite.h tbl_rtcomm.h tbl_static_paoinfo.h encryption.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_dyn_ptalarming.h thread_monitor.h thread_register_data.h \
		ThreadStatusKeeper.h mgr_ptclients.h ptconnect.h \
		tbl_pt_property.h database_transaction.h database_writer.h \
		row_writer.h dllvg.h dllyukon.h ctidate.h debug_timer.h \
		millisecond_timer.h
dispmain.obj:	precompiled.h ctitime.h dlldefs.h dispsvc.h cservice.h \
		dllvg.h CServiceConfig.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h ctibase.h ctinexus.h logger.h thread.h \
		CtiPCPtrQueue.h
dispsvc.obj:	precompiled.h dispsvc.h cservice.h dlldefs.h
dllvg.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h dllvg.h logger.h thread.h CtiPCPtrQueue.h \
		cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h
exe_ptchg.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h rwutil.h yukon.h types.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h con_mgr_vg.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h vgexe_factory.h \
		executor.h exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h \
		msg_cmd.h con_mgr.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ctibase.h ctinexus.h \
		ctivangogh.h server_b.h critical_Section.h smartmap.h \
		readers_writer_lock.h dev_base_lite.h dbmemobject.h \
		msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
		msg_commerrorhistory.h msg_lmcontrolhistory.h msg_tag.h \
		pendingopthread.h pendable.h pending_info.h msg_signal.h \
		tbl_lm_controlhist.h pt_numeric.h pt_base.h tbl_pt_base.h \
		resolvers.h db_entry_defines.h desolvers.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pttrigger.h \
		mgr_point.h tbl_pt_trigger.h tagmanager.h tbl_dyn_pttag.h \
		tbl_tag.h tbl_taglog.h tbl_state_grp.h tbl_state.h \
		tbl_alm_ngroup.h tbl_commerrhist.h tbl_pt_limit.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h
exe_signal.obj:	precompiled.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		con_mgr_vg.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ctibase.h ctinexus.h ctivangogh.h server_b.h \
		critical_Section.h smartmap.h readers_writer_lock.h \
		dev_base_lite.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_commerrorhistory.h msg_lmcontrolhistory.h \
		msg_tag.h pendingopthread.h pendable.h pending_info.h \
		msg_signal.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h desolvers.h \
		tbl_pt_unit.h tbl_unitmeasure.h signalmanager.h \
		pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		pttrigger.h mgr_point.h tbl_pt_trigger.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_commerrhist.h tbl_pt_limit.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		exe_signal.h
id_vg.obj:	precompiled.h id_vg.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h
id_vgdll.obj:	precompiled.h id_vgdll.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h
mgr_ptclients.obj:	precompiled.h dllvg.h dlldefs.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h ctitime.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h netports.h \
		mutex.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h pointdefs.h \
		logger.h thread.h CtiPCPtrQueue.h mgr_ptclients.h mgr_point.h \
		smartmap.h boostutil.h readers_writer_lock.h \
		critical_section.h msg_pdata.h message.h collectable.h \
		rwutil.h database_connection.h database_reader.h boost_time.h \
		ptconnect.h hashkey.h hash_functions.h con_mgr.h connection.h \
		exchange.h string_utility.h msg_multi.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ctibase.h ctinexus.h \
		server_b.h pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		tbl_pt_limit.h rtdb.h tbl_rawpthistory.h tbl_pt_property.h \
		database_transaction.h devicetypes.h msg_pcreturn.h \
		msg_signal.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h tbl_pt_control.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h debug_timer.h \
		con_mgr_vg.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h
pendingopthread.obj:	precompiled.h counter.h guard.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h mutex.h cparms.h rwutil.h yukon.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h dllvg.h mgr_ptclients.h mgr_point.h \
		pt_base.h dbmemobject.h tbl_pt_base.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h pointdefs.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		msg_pdata.h message.h collectable.h ptconnect.h hashkey.h \
		hash_functions.h con_mgr.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h msg_multi.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h server_b.h \
		pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		tbl_pt_limit.h rtdb.h tbl_rawpthistory.h tbl_pt_property.h \
		msg_cmd.h pendingopthread.h pendable.h pending_info.h \
		msg_signal.h tbl_lm_controlhist.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h millisecond_timer.h \
		database_transaction.h control_history_association.h \
		amq_connection.h ControlHistoryAssociationResponse.h
pending_info.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h pending_info.h msg_pdata.h \
		yukon.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_signal.h tbl_lm_controlhist.h dbmemobject.h
porterpoker.obj:	precompiled.h queue.h cparms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h exchange.h \
		message.h collectable.h msg_cmd.h msg_reg.h msg_dbchg.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h counter.h pt_accum.h pt_numeric.h pt_base.h \
		dbmemobject.h tbl_pt_base.h resolvers.h db_entry_defines.h \
		desolvers.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h
ptconnect.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h msg_pcreturn.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h pt_base.h dbmemobject.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h desolvers.h \
		ptconnect.h hashkey.h hash_functions.h con_mgr.h connection.h \
		exchange.h string_utility.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ctibase.h ctinexus.h \
		server_b.h critical_Section.h smartmap.h \
		readers_writer_lock.h
signalmanager.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h pointdefs.h sema.h signalmanager.h \
		msg_signal.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointtypes.h \
		tbl_dyn_ptalarming.h ctibase.h ctinexus.h dbmemobject.h \
		tbl_pt_alarm.h resolvers.h db_entry_defines.h desolvers.h \
		database_transaction.h
sigsrctest.obj:	precompiled.h queue.h cparms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h exchange.h \
		message.h collectable.h msg_cmd.h msg_reg.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_signal.h \
		msg_ptreg.h connection.h
sigtest.obj:	precompiled.h queue.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h exchange.h \
		message.h collectable.h msg_cmd.h msg_reg.h msg_signal.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_pcreturn.h msg_multi.h
tagmanager.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h pointdefs.h sema.h tagmanager.h msg_tag.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointtypes.h queue.h cparms.h \
		configkey.h configval.h string_utility.h tbl_dyn_pttag.h \
		ctibase.h ctinexus.h dbmemobject.h tbl_tag.h tbl_taglog.h \
		database_transaction.h
test_mgr_ptclients.obj:	mgr_ptclients.h dlldefs.h mgr_point.h \
		pt_base.h dbmemobject.h tbl_pt_base.h row_reader.h ctitime.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		pointdefs.h smartmap.h boostutil.h readers_writer_lock.h \
		critical_section.h msg_pdata.h message.h collectable.h \
		rwutil.h database_connection.h database_reader.h boost_time.h \
		ptconnect.h hashkey.h hash_functions.h con_mgr.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ctibase.h ctinexus.h server_b.h \
		pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		tbl_pt_limit.h rtdb.h tbl_rawpthistory.h tbl_pt_property.h \
		pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h
test_signalmanager.obj:	tbl_pt_alarm.h dlldefs.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h row_reader.h signalmanager.h msg_signal.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h
test_vangogh.obj:	ctivangogh.h con_mgr.h connection.h dlldefs.h \
		exchange.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ctibase.h ctinexus.h con_mgr_vg.h vgexe_factory.h \
		executor.h exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h \
		msg_cmd.h server_b.h critical_Section.h smartmap.h \
		readers_writer_lock.h dev_base_lite.h dbmemobject.h \
		msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
		msg_commerrorhistory.h msg_lmcontrolhistory.h msg_tag.h \
		pendingopthread.h pendable.h pending_info.h msg_signal.h \
		tbl_lm_controlhist.h pt_numeric.h pt_base.h tbl_pt_base.h \
		resolvers.h db_entry_defines.h desolvers.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pttrigger.h \
		mgr_point.h tbl_pt_trigger.h tagmanager.h tbl_dyn_pttag.h \
		tbl_tag.h tbl_taglog.h tbl_state_grp.h tbl_state.h \
		tbl_alm_ngroup.h tbl_commerrhist.h tbl_pt_limit.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		ctidate.h
vangogh.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h ctivangogh.h con_mgr.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h ctibase.h ctinexus.h con_mgr_vg.h vgexe_factory.h \
		executor.h exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h \
		msg_cmd.h server_b.h critical_Section.h smartmap.h \
		readers_writer_lock.h dev_base_lite.h dbmemobject.h \
		msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
		msg_commerrorhistory.h msg_lmcontrolhistory.h msg_tag.h \
		pendingopthread.h pendable.h pending_info.h msg_signal.h \
		tbl_lm_controlhist.h pt_numeric.h pt_base.h tbl_pt_base.h \
		resolvers.h db_entry_defines.h desolvers.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pttrigger.h \
		mgr_point.h tbl_pt_trigger.h tagmanager.h tbl_dyn_pttag.h \
		tbl_tag.h tbl_taglog.h tbl_state_grp.h tbl_state.h \
		tbl_alm_ngroup.h tbl_commerrhist.h tbl_pt_limit.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		dllvg.h stdexcepthdlr.h
vgexe_factory.obj:	precompiled.h executorfactory.h collectable.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h rwutil.h yukon.h \
		types.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		executor.h exe_cmd.h exe_reg.h vgexe_factory.h exe_ptchg.h \
		ctibase.h ctinexus.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
