# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(PIL)\include \
-I$(PROT)\include \
-I$(SERVER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \
-I$(THRIFT_INCLUDE) \
-I$(MSG)\Serialization \

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


DLLLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctithriftmsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(COMPILEBASE)\lib\rfn-e2e.lib \


DLLOBJS=\
exe_pcreq.obj \
pil_conmgr.obj \
pil_exefct.obj \
pilglob.obj \
pilserver.obj \
mgr_rfn_request.obj \


PIL_FULLBUILD = $[Filename,$(OBJ),PilFullBuild,target]


PROGS_VERSION=\
ctipil.dll


ALL:            ctipil.dll


$(PIL_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) /D_DLL_CTIPIL $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DLLOBJS)]


ctipil.dll:     $(PIL_FULLBUILD) $(DLLOBJS) makedll.mak $(OBJ)\ctipil.res
                @echo Building  $@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(DLLOBJS) $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(DLLLIBS) -Fe..\$@ $(LINKFLAGS) ctipil.res
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\ctipil.dll copy bin\ctipil.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\ctipil.lib copy bin\ctipil.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp

deps:
                scandeps -Output makedll.mak *.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: $@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_CTIPIL $(INCLPATHS) \
-DWINDOWS -Fo$(OBJ)\ -c $<


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
		configval.h string_utility.h readers_writer_lock.h \
		connection_base.h worker_thread.h connection_listener.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h
exe_pcreq.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h loggable.h pil_conmgr.h con_mgr.h \
		connection_server.h connection.h msg_multi.h msg_pdata.h \
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
		worker_thread.h connection_listener.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		pilserver.h server_b.h smartmap.h msg_pcrequest.h \
		mgr_device.h rtdb.h hashkey.h hash_functions.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h slctdev.h mgr_point.h mgr_route.h \
		repeaterrole.h mgr_config.h devicetypes.h amq_constants.h \
		mgr_rfn_request.h prot_e2eDataTransfer.h dev_rfn.h \
		rfn_identifier.h cmd_rfn.h cmd_device.h dev_single.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h xfer.h \
		exceptions.h rfn_asid.h rfn_e2e_messenger.h \
		RfnE2eDataIndicationMsg.h RfnE2eMsg.h RfnE2eDataConfirmMsg.h \
		RfnE2eDataRequestMsg.h exe_pcreq.h executor.h
mgr_rfn_request.obj:	precompiled.h mgr_rfn_request.h dlldefs.h \
		prot_e2eDataTransfer.h dev_rfn.h rfn_identifier.h \
		streamBuffer.h loggable.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h rfn_asid.h rfn_e2e_messenger.h \
		RfnE2eDataIndicationMsg.h RfnE2eMsg.h RfnE2eDataConfirmMsg.h \
		RfnE2eDataRequestMsg.h amq_connection.h thread.h \
		StreamableMessage.h RfnBroadcastReplyMessage.h \
		rfn_statistics.h std_helper.h
pilglob.obj:	precompiled.h os2_2w32.h dlldefs.h types.h
pilserver.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h dev_grp_versacom.h dev_base.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h ctitime.h \
		queues.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		dev_grp.h cparms.h configkey.h configval.h \
		msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_dv_versacom.h vcomdefs.h dev_grp_point.h tbl_lmg_point.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		msg_ptreg.h msg_reg.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h dev_rfn.h \
		rfn_identifier.h cmd_rfn.h rfn_asid.h streamLocalConnection.h \
		porter.h devicetypes.h pil_conmgr.h con_mgr.h \
		connection_server.h connection_listener.h pil_exefct.h \
		executorfactory.h executor.h exe_cmd.h exe_reg.h pilserver.h \
		server_b.h smartmap.h mgr_device.h rtdb.h slctdev.h \
		mgr_point.h mgr_route.h repeaterrole.h mgr_config.h \
		amq_constants.h mgr_rfn_request.h prot_e2eDataTransfer.h \
		rfn_e2e_messenger.h RfnE2eDataIndicationMsg.h RfnE2eMsg.h \
		RfnE2eDataConfirmMsg.h RfnE2eDataRequestMsg.h msg_cmd.h \
		rte_ccu.h rte_xcu.h tbl_rtcarrier.h tbl_rtrepeater.h \
		amq_connection.h thread.h StreamableMessage.h \
		RfnBroadcastReplyMessage.h PorterResponseMessage.h \
		ctistring.h debug_timer.h millisecond_timer.h \
		connection_client.h
pil_conmgr.obj:	precompiled.h collectable.h pil_conmgr.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h loggable.h con_mgr.h \
		connection_server.h connection.h msg_multi.h msg_pdata.h \
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
		worker_thread.h connection_listener.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		pil_exefct.h executorfactory.h executor.h exe_cmd.h exe_reg.h \
		msg_cmd.h
pil_exefct.obj:	precompiled.h executorfactory.h collectable.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h loggable.h \
		executor.h yukon.h types.h exe_cmd.h exe_reg.h pil_exefct.h \
		exe_pcreq.h
test_pilserver.obj:	pilserver.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h server_b.h con_mgr.h \
		connection_server.h connection.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h string_utility.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h smartmap.h msg_pcrequest.h \
		mgr_device.h rtdb.h hashkey.h hash_functions.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h slctdev.h mgr_point.h mgr_route.h \
		repeaterrole.h mgr_config.h devicetypes.h amq_constants.h \
		mgr_rfn_request.h prot_e2eDataTransfer.h dev_rfn.h \
		rfn_identifier.h cmd_rfn.h cmd_device.h dev_single.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h xfer.h \
		exceptions.h rfn_asid.h rfn_e2e_messenger.h \
		RfnE2eDataIndicationMsg.h RfnE2eMsg.h RfnE2eDataConfirmMsg.h \
		RfnE2eDataRequestMsg.h cmd_rfn_demandFreeze.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
