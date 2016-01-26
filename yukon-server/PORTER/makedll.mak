# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(PORTER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(PROT)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


.PATH.cpp = .

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
;$(BOOST_INCLUDE) \


DLLOBJS=\
$(PRECOMPILED_OBJ) \
port_shr.obj \
port_shr_ip.obj \
portglob.obj \
dllmain.obj


PORTER_DLL_FULLBUILD = $[Filename,$(OBJ),PorterDllFullBuild,target]


PROGS_VERSION=\
portglob.dll


ALL:            portglob.dll


$(PORTER_DLL_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(CC) $(CCOPTS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) /D_DLL_PORTGLOB $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DLLOBJS)]



portglob.dll:  $(PORTER_DLL_FULLBUILD) $(DLLOBJS) Makedll.mak $(OBJ)\portglob.res
               @build -nologo -f $(_InputFile) id
               @echo Building  ..\$@
               @%cd $(OBJ)
               $(CC) $(CCOPTS) $(DLLFLAGS) $(DLLOBJS) id_pgdll.obj $(INCLPATHS) /Fe..\$@ -link $(LIBS) $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\ctiprtdb.lib $(COMPILEBASE)\lib\ctidbsrc.lib $(BOOST_LIBS) portglob.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	       -if exist ..\$(@B).pdb copy ..\$(@B).pdb $(YUKONDEBUG)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\portglob.dll copy bin\portglob.dll $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
               -if exist bin\portglob.pdb copy bin\portglob.pdb $(YUKONDEBUG)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\portglob.lib copy bin\portglob.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp

deps:
                scandeps -Output makedll.mak *.cpp

.cpp.obj :
                @echo:
                @echo Compiling: $< Output: $@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_PORTGLOB $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<


# The lines below accomplish the ID'ing of the project!
id:
            @build -nologo -f $(_InputFile) id_pgdll.obj

id_pgdll.obj:    id_pgdll.cpp include\id_pgdll.h


#UPDATE#
disp_thd.obj:	precompiled.h pilserver.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h loggable.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		server_b.h con_mgr.h connection_server.h connection.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		connection_listener.h dllbase.h smartmap.h msg_pcrequest.h \
		mgr_device.h rtdb.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		config_device.h rte_base.h dbmemobject.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h slctdev.h \
		mgr_point.h mgr_route.h repeaterrole.h mgr_config.h \
		devicetypes.h amq_constants.h mgr_rfn_request.h \
		prot_e2eDataTransfer.h dev_rfn.h rfn_identifier.h cmd_rfn.h \
		cmd_device.h dev_single.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h xfer.h config_exceptions.h exceptions.h \
		rfn_asid.h rfn_e2e_messenger.h RfnE2eDataIndicationMsg.h \
		RfnE2eMsg.h RfnE2eDataConfirmMsg.h NetworkManagerMessaging.h \
		RfnE2eDataRequestMsg.h NetworkManagerRequest.h msg_dbchg.h \
		msg_cmd.h mgr_port.h port_base.h logManager.h tbl_port_base.h \
		connection_client.h thread_monitor.h thread.h \
		thread_register_data.h boost_time.h portglob.h \
		streamSocketListener.h socket_helper.h win_helper.h \
		streamSocketConnection.h porter.h unsolicited_handler.h \
		millisecond_timer.h StatisticsManager.h PaoStatistics.h \
		PaoStatisticsRecord.h ThreadStatusKeeper.h
dllmain.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h dsm2.h streamConnection.h netports.h \
		timing_util.h loggable.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h portglob.h streamSocketListener.h \
		socket_helper.h win_helper.h streamSocketConnection.h \
		porter.h devicetypes.h
encodingfilterfactory.obj:	precompiled.h EncodingFilterFactory.h \
		EncodingFilter.h port_udp.h port_serial.h port_base.h \
		logManager.h dlldefs.h module_util.h ctitime.h version.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h tbl_port_base.h dbmemobject.h dbaccess.h \
		dllbase.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h tbl_pao_lite.h tbl_paoexclusion.h xfer.h \
		dev_base.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h config_device.h rte_base.h message.h \
		collectable.h tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h devicetypes.h tbl_port_settings.h \
		tbl_port_timing.h tbl_port_tcpip.h encryption_lantronix.h \
		encryption_noop.h
encryption_lantronix.obj:	precompiled.h encryption_lantronix.h \
		EncodingFilter.h logger.h dlldefs.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h
encryption_noop.obj:	precompiled.h encryption_noOp.h EncodingFilter.h
id_pgdll.obj:	precompiled.h id_pgdll.h module_util.h dlldefs.h \
		ctitime.h version.h
id_porter.obj:	precompiled.h id_porter.h module_util.h dlldefs.h \
		ctitime.h version.h
mgr_port.obj:	precompiled.h mgr_port.h dlldefs.h smartmap.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h dllbase.h critical_section.h \
		readers_writer_lock.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h port_base.h \
		logManager.h tbl_port_base.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		tbl_pao_lite.h tbl_paoexclusion.h xfer.h dev_base.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		config_device.h rte_base.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h port_direct.h port_serial.h tbl_port_settings.h \
		tbl_port_timing.h port_dialable.h port_modem.h \
		tbl_port_dialup.h tbl_port_serial.h port_dialin.h \
		port_dialout.h port_pool_out.h port_rf_da.h rfn_identifier.h \
		port_tcpipdirect.h tbl_port_tcpip.h port_tcp.h port_udp.h \
		database_reader.h database_util.h database_writer.h \
		row_writer.h
paostatistics.obj:	precompiled.h PaoStatistics.h PaoStatisticsRecord.h \
		ctitime.h dlldefs.h yukon.h types.h ctidbgmem.h \
		database_reader.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h critical_section.h guard.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h row_reader.h \
		InvalidReaderException.h
paostatisticsrecord.obj:	precompiled.h PaoStatisticsRecord.h ctitime.h \
		dlldefs.h yukon.h types.h ctidbgmem.h database_writer.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h critical_section.h guard.h utility.h queues.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h row_writer.h database_util.h \
		database_exceptions.h ctidate.h dsm2err.h
phlidlc.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h queues.h constants.h dsm2.h \
		streamConnection.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h dllbase.h message.h collectable.h \
		tbl_pao_lite.h row_reader.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h port_base.h \
		logManager.h tbl_port_base.h tbl_paoexclusion.h xfer.h \
		dev_base.h dev_exclusion.h config_device.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h ThreadStatusKeeper.h \
		thread_register_data.h boost_time.h thread_monitor.h \
		smartmap.h readers_writer_lock.h cparms.h queue.h thread.h \
		portglob.h streamSocketListener.h socket_helper.h \
		win_helper.h streamSocketConnection.h mgr_port.h trx_711.h \
		trx_info.h
plidlc.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h cti_asmc.h queues.h constants.h dsm2.h \
		streamConnection.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h portglob.h streamSocketListener.h \
		socket_helper.h win_helper.h streamSocketConnection.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		config_device.h dllbase.h rte_base.h dbmemobject.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h trx_info.h cparms.h
portconf.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h cparms.h queues.h constants.h dsm2.h \
		streamConnection.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h master.h elogger.h portglob.h \
		streamSocketListener.h socket_helper.h win_helper.h \
		streamSocketConnection.h c_port_interface.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h config_device.h dllbase.h \
		rte_base.h dbmemobject.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h portdecl.h port_base.h \
		logManager.h tbl_port_base.h xfer.h mgr_device.h rtdb.h \
		slctdev.h smartmap.h readers_writer_lock.h mgr_route.h \
		repeaterrole.h
portdialback.obj:	precompiled.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h dlldefs.h cparms.h dsm2.h streamConnection.h \
		netports.h timing_util.h loggable.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		connection_client.h connection.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h mgr_device.h rtdb.h dllbase.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h config_device.h rte_base.h \
		dbmemobject.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h slctdev.h smartmap.h mgr_port.h \
		port_base.h logManager.h tbl_port_base.h xfer.h devicetypes.h \
		msg_cmd.h pilserver.h server_b.h con_mgr.h \
		connection_server.h connection_listener.h msg_pcrequest.h \
		mgr_point.h mgr_route.h repeaterrole.h mgr_config.h \
		amq_constants.h mgr_rfn_request.h prot_e2eDataTransfer.h \
		dev_rfn.h rfn_identifier.h cmd_rfn.h cmd_device.h \
		dev_single.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		config_exceptions.h exceptions.h rfn_asid.h \
		rfn_e2e_messenger.h RfnE2eDataIndicationMsg.h RfnE2eMsg.h \
		RfnE2eDataConfirmMsg.h NetworkManagerMessaging.h \
		RfnE2eDataRequestMsg.h NetworkManagerRequest.h portglob.h \
		streamSocketListener.h socket_helper.h win_helper.h \
		streamSocketConnection.h porter.h
portentry.obj:	precompiled.h connection_client.h connection.h \
		dlldefs.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h msg_multi.h msg_pdata.h yukon.h types.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h dsm2.h \
		streamConnection.h netports.h immutable.h dsm2err.h words.h \
		optional.h macro_offset.h portdecl.h rte_base.h dbmemobject.h \
		cmdparse.h ctitokenizer.h parsevalue.h dllbase.h \
		tbl_pao_lite.h row_reader.h tbl_rtcomm.h dbaccess.h \
		resolvers.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h port_base.h \
		logManager.h tbl_port_base.h tbl_paoexclusion.h xfer.h \
		dev_base.h dev_exclusion.h config_device.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h devicetypes.h porter.h \
		StatisticsManager.h PaoStatistics.h PaoStatisticsRecord.h \
		ThreadStatusKeeper.h thread_register_data.h boost_time.h \
		thread_monitor.h smartmap.h thread.h portglob.h \
		streamSocketListener.h socket_helper.h win_helper.h \
		streamSocketConnection.h c_port_interface.h elogger.h \
		mgr_port.h mgr_device.h rtdb.h slctdev.h \
		streamLocalConnection.h msg_pcrequest.h msg_pcreturn.h \
		prot_emetcon.h trx_711.h trx_info.h
porter.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h color.h cparms.h queues.h constants.h \
		dsm2.h streamConnection.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h dllbase.h message.h collectable.h \
		tbl_pao_lite.h row_reader.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h port_base.h \
		logManager.h tbl_port_base.h tbl_paoexclusion.h xfer.h \
		dev_base.h dev_exclusion.h config_device.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h portverify.h queue.h \
		thread.h verification_objects.h boost_time.h \
		StatisticsThread.h master.h elogger.h thread_monitor.h \
		smartmap.h readers_writer_lock.h thread_register_data.h \
		streamLocalConnection.h streamAmqConnection.h \
		amq_connection.h StreamableMessage.h connection_base.h \
		systemmsgthread.h connection.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h worker_thread.h mgr_device.h rtdb.h \
		slctdev.h mgr_port.h portglob.h streamSocketListener.h \
		socket_helper.h win_helper.h streamSocketConnection.h \
		c_port_interface.h mgr_route.h repeaterrole.h mgr_config.h \
		mgr_point.h mgr_dyn_paoinfo.h port_thread_udp.h \
		unsolicited_handler.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		config_exceptions.h exceptions.h msg_dbchg.h \
		millisecond_timer.h port_udp.h port_serial.h \
		tbl_port_settings.h tbl_port_timing.h tbl_port_tcpip.h \
		EncodingFilterFactory.h EncodingFilter.h port_thread_tcp.h \
		packet_finder.h port_tcp.h tcp_connection_manager.h \
		tcp_connection.h port_thread_rf_da.h port_rf_da.h \
		rfn_identifier.h rfn_e2e_messenger.h rfn_asid.h \
		RfnE2eDataIndicationMsg.h RfnE2eMsg.h RfnE2eDataConfirmMsg.h \
		NetworkManagerMessaging.h RfnE2eDataRequestMsg.h \
		NetworkManagerRequest.h port_shr.h port_shr_ip.h msg_trace.h \
		rte_macro.h tbl_rtmacro.h rte_xcu.h eventlog.h trx_711.h \
		trx_info.h dllyukon.h pilserver.h server_b.h con_mgr.h \
		connection_server.h connection_listener.h amq_constants.h \
		mgr_rfn_request.h prot_e2eDataTransfer.h dev_rfn.h cmd_rfn.h \
		cmd_device.h database_reader.h dev_ccu721.h dev_remote.h \
		tbl_dialup.h tbl_direct.h tbl_dv_address.h \
		device_queue_interface.h prot_klondike.h prot_wrap.h \
		prot_base.h prot_idlc.h connection_client.h \
		porter_message_serialization.h PorterDynamicPaoInfoMsg.h
portersu.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h color.h queues.h constants.h dsm2.h \
		streamConnection.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h master.h elogger.h portglob.h \
		streamSocketListener.h socket_helper.h win_helper.h \
		streamSocketConnection.h portdecl.h rte_base.h dbmemobject.h \
		cmdparse.h ctitokenizer.h parsevalue.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h row_reader.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		port_base.h logManager.h tbl_port_base.h tbl_paoexclusion.h \
		xfer.h dev_base.h dev_exclusion.h config_device.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h ThreadStatusKeeper.h \
		thread_register_data.h boost_time.h thread_monitor.h \
		smartmap.h readers_writer_lock.h cparms.h queue.h thread.h \
		c_port_interface.h rtdb.h mgr_port.h mgr_device.h slctdev.h
porter_message_serialization.obj:	precompiled.h \
		porter_message_serialization.h PorterDynamicPaoInfoMsg.h \
		amq_connection.h thread.h mutex.h dlldefs.h guard.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h \
		StreamableMessage.h connection_base.h std_helper.h
portfield.obj:	precompiled.h c_port_interface.h dlldefs.h elogger.h \
		loggable.h StreamBuffer.h ctitime.h cti_asmc.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h logger.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h master.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h ctitokenizer.h parsevalue.h \
		dllbase.h message.h collectable.h tbl_pao_lite.h row_reader.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h port_base.h logManager.h \
		tbl_port_base.h tbl_paoexclusion.h xfer.h dev_base.h \
		dev_exclusion.h config_device.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h devicetypes.h porter.h \
		StatisticsManager.h PaoStatistics.h PaoStatisticsRecord.h \
		ThreadStatusKeeper.h thread_register_data.h boost_time.h \
		thread_monitor.h smartmap.h readers_writer_lock.h cparms.h \
		queue.h thread.h portglob.h streamSocketListener.h \
		socket_helper.h win_helper.h streamSocketConnection.h \
		portverify.h verification_objects.h mgr_port.h mgr_device.h \
		rtdb.h slctdev.h dev_wctp.h dev_paging.h tbl_dv_tappaging.h \
		dev_ied.h dev_remote.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h \
		connection_base.h worker_thread.h config_exceptions.h \
		exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		encryption_oneway_message.h dev_ansi.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h prot_ansi.h ansi_application.h \
		ansi_datalink.h ansi_billing_table.h std_ansi_tbl_base.h \
		std_ansi_tbl_00.h std_ansi_tbl_01.h std_ansi_tbl_08.h \
		std_ansi_tbl_10.h std_ansi_tbl_11.h std_ansi_tbl_12.h \
		std_ansi_tbl_13.h std_ansi_tbl_14.h std_ansi_tbl_15.h \
		std_ansi_tbl_16.h std_ansi_tbl_21.h std_ansi_tbl_22.h \
		std_ansi_tbl_23.h std_ansi_tbl_25.h std_ansi_tbl_27.h \
		std_ansi_tbl_28.h std_ansi_tbl_31.h std_ansi_tbl_32.h \
		std_ansi_tbl_33.h std_ansi_tbl_51.h std_ansi_tbl_52.h \
		std_ansi_tbl_61.h std_ansi_tbl_62.h std_ansi_tbl_63.h \
		std_ansi_tbl_64.h dllyukon.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_pt_control.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h dev_ccu721.h tbl_dv_address.h \
		device_queue_interface.h prot_klondike.h prot_wrap.h \
		prot_base.h prot_idlc.h dev_lcu.h dev_idlc.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h dev_mark_v.h \
		prot_transdata.h transdata_application.h transdata_tracker.h \
		transdata_datalink.h prot_ymodem.h ctidate.h transdata_data.h \
		msg_cmd.h dev_mct.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_device.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		dev_rtc.h tbl_dv_rtc.h dev_tap.h mgr_route.h repeaterrole.h \
		rte_macro.h tbl_rtmacro.h rte_ccu.h rte_xcu.h tbl_rtcarrier.h \
		tbl_rtrepeater.h prot_711.h streamLocalConnection.h \
		portfield.h connection_client.h desolvers.h
portfill.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h cparms.h queues.h constants.h dsm2.h \
		streamConnection.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h dllbase.h message.h collectable.h \
		tbl_pao_lite.h row_reader.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h port_base.h \
		logManager.h tbl_port_base.h tbl_paoexclusion.h xfer.h \
		dev_base.h dev_exclusion.h config_device.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h master.h portglob.h \
		streamSocketListener.h socket_helper.h win_helper.h \
		streamSocketConnection.h c_port_interface.h elogger.h rtdb.h \
		mgr_device.h slctdev.h smartmap.h readers_writer_lock.h \
		mgr_port.h mgr_route.h repeaterrole.h dev_tcu.h dev_idlc.h \
		dev_remote.h dev_single.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h connection_base.h \
		worker_thread.h config_exceptions.h exceptions.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		dev_tap.h dev_paging.h tbl_dv_tappaging.h dev_ied.h \
		tbl_dv_ied.h encryption_oneway_message.h dev_snpp.h \
		dev_tnpp.h tbl_dv_tnpp.h dev_pagingreceiver.h \
		tbl_dv_pagingreceiver.h boost_time.h dev_wctp.h \
		prot_versacom.h expresscom.h
portglob.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h queues.h constants.h dsm2.h \
		streamConnection.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h portglob.h streamSocketListener.h \
		socket_helper.h win_helper.h streamSocketConnection.h
portload.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h queues.h constants.h dsm2.h \
		streamConnection.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h portglob.h streamSocketListener.h \
		socket_helper.h win_helper.h streamSocketConnection.h \
		portdecl.h rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		row_reader.h tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h port_base.h logManager.h \
		tbl_port_base.h tbl_paoexclusion.h xfer.h dev_base.h \
		dev_exclusion.h config_device.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h c_port_interface.h elogger.h \
		mgr_port.h smartmap.h readers_writer_lock.h mgr_device.h \
		rtdb.h slctdev.h mgr_route.h repeaterrole.h rte_ccu.h \
		rte_xcu.h msg_pcrequest.h tbl_rtcarrier.h tbl_rtrepeater.h \
		trx_711.h trx_info.h dev_ccu.h dev_idlc.h dev_remote.h \
		dev_single.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h connection_base.h worker_thread.h \
		config_exceptions.h exceptions.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h dev_ccu_queue_interface.h \
		device_queue_interface.h dev_ccu721.h tbl_dv_address.h \
		prot_klondike.h prot_wrap.h prot_base.h prot_idlc.h
portmain.obj:	precompiled.h ctitime.h dlldefs.h portsvc.h cservice.h \
		CServiceConfig.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h portglob.h \
		streamSocketListener.h socket_helper.h guard.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h timing_util.h win_helper.h \
		streamSocketConnection.h streamConnection.h netports.h \
		immutable.h porter.h dsm2.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h devicetypes.h cparms.h logManager.h \
		connection_base.h
portpil.obj:	precompiled.h pilserver.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h loggable.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		server_b.h con_mgr.h connection_server.h connection.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		connection_listener.h dllbase.h smartmap.h msg_pcrequest.h \
		mgr_device.h rtdb.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		config_device.h rte_base.h dbmemobject.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h slctdev.h \
		mgr_point.h mgr_route.h repeaterrole.h mgr_config.h \
		devicetypes.h amq_constants.h mgr_rfn_request.h \
		prot_e2eDataTransfer.h dev_rfn.h rfn_identifier.h cmd_rfn.h \
		cmd_device.h dev_single.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h xfer.h config_exceptions.h exceptions.h \
		rfn_asid.h rfn_e2e_messenger.h RfnE2eDataIndicationMsg.h \
		RfnE2eMsg.h RfnE2eDataConfirmMsg.h NetworkManagerMessaging.h \
		RfnE2eDataRequestMsg.h NetworkManagerRequest.h \
		thread_monitor.h thread.h thread_register_data.h boost_time.h \
		ThreadStatusKeeper.h
portpool.obj:	precompiled.h connection_client.h connection.h dlldefs.h \
		message.h ctitime.h ctidbgmem.h collectable.h loggable.h \
		msg_multi.h msg_pdata.h yukon.h types.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h mgr_device.h \
		rtdb.h dllbase.h dev_base.h dsm2.h streamConnection.h \
		netports.h immutable.h dsm2err.h words.h optional.h \
		macro_offset.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		config_device.h rte_base.h dbmemobject.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h slctdev.h \
		smartmap.h mgr_port.h port_base.h logManager.h \
		tbl_port_base.h xfer.h devicetypes.h port_pool_out.h \
		portdecl.h porter.h portglob.h streamSocketListener.h \
		socket_helper.h win_helper.h streamSocketConnection.h
portque.obj:	precompiled.h cparms.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h dsm2.h \
		streamConnection.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h elogger.h thread_monitor.h smartmap.h dllbase.h \
		readers_writer_lock.h queue.h thread.h thread_register_data.h \
		boost_time.h ThreadStatusKeeper.h portglob.h \
		streamSocketListener.h socket_helper.h win_helper.h \
		streamSocketConnection.h portdecl.h rte_base.h dbmemobject.h \
		cmdparse.h ctitokenizer.h parsevalue.h message.h \
		collectable.h tbl_pao_lite.h row_reader.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		port_base.h logManager.h tbl_port_base.h tbl_paoexclusion.h \
		xfer.h dev_base.h dev_exclusion.h config_device.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h c_port_interface.h \
		mgr_device.h rtdb.h slctdev.h mgr_port.h port_shr.h \
		streamLocalConnection.h trx_711.h trx_info.h dev_ccu.h \
		dev_idlc.h dev_remote.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h \
		connection_base.h worker_thread.h config_exceptions.h \
		exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h \
		dev_ccu_queue_interface.h device_queue_interface.h \
		dev_ccu721.h tbl_dv_address.h prot_klondike.h prot_wrap.h \
		prot_base.h prot_idlc.h prot_emetcon.h
portsvc.obj:	precompiled.h ctitime.h dlldefs.h portsvc.h cservice.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h portglob.h streamSocketListener.h \
		socket_helper.h guard.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		timing_util.h win_helper.h streamSocketConnection.h \
		streamConnection.h netports.h immutable.h porter.h dsm2.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		devicetypes.h
porttime.obj:	precompiled.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h dlldefs.h constants.h dsm2.h \
		streamConnection.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h dllbase.h message.h collectable.h \
		tbl_pao_lite.h row_reader.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h port_base.h \
		logManager.h tbl_port_base.h tbl_paoexclusion.h xfer.h \
		dev_base.h dev_exclusion.h config_device.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h elogger.h portglob.h \
		streamSocketListener.h socket_helper.h win_helper.h \
		streamSocketConnection.h c_port_interface.h mgr_port.h \
		smartmap.h readers_writer_lock.h mgr_device.h rtdb.h \
		slctdev.h dev_ccu.h dev_idlc.h dev_remote.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h connection_base.h worker_thread.h \
		config_exceptions.h exceptions.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h \
		dev_ccu_queue_interface.h device_queue_interface.h \
		dev_ccu721.h tbl_dv_address.h prot_klondike.h prot_wrap.h \
		prot_base.h prot_idlc.h dev_dnp.h prot_dnp.h packet_finder.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h dev_ilex.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_device.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h cmd_mct4xx.h ctidate.h \
		mgr_route.h repeaterrole.h thread_monitor.h thread.h \
		thread_register_data.h boost_time.h ThreadStatusKeeper.h \
		config_data_dnp.h prot_welco.h prot_lmi.h prot_seriesv.h \
		verification_objects.h
portverify.obj:	precompiled.h boost_time.h mgr_port.h dlldefs.h \
		smartmap.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h dllbase.h critical_section.h \
		readers_writer_lock.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h port_base.h \
		logManager.h tbl_port_base.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		tbl_pao_lite.h tbl_paoexclusion.h xfer.h dev_base.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		config_device.h rte_base.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h cparms.h portverify.h queue.h thread.h \
		verification_objects.h ctidate.h database_reader.h \
		database_transaction.h database_writer.h row_writer.h
port_shr.obj:	precompiled.h types.h dsm2.h streamConnection.h yukon.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h loggable.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h port_shr.h streamSocketConnection.h \
		socket_helper.h win_helper.h thread.h port_base.h \
		logManager.h tbl_port_base.h dbmemobject.h dbaccess.h \
		dllbase.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h tbl_pao_lite.h tbl_paoexclusion.h xfer.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h config_device.h rte_base.h message.h \
		collectable.h tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h devicetypes.h cparms.h \
		streamSocketListener.h
port_shr_ip.obj:	precompiled.h types.h cparms.h dlldefs.h cticalls.h \
		yukon.h ctidbgmem.h os2_2w32.h dsm2.h streamConnection.h \
		netports.h timing_util.h loggable.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		port_shr_ip.h streamSocketConnection.h socket_helper.h \
		win_helper.h port_shr.h thread.h port_base.h logManager.h \
		tbl_port_base.h dbmemobject.h dbaccess.h dllbase.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		tbl_pao_lite.h tbl_paoexclusion.h xfer.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		config_device.h rte_base.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h prot_welco.h porter.h cti_asmc.h \
		streamSocketListener.h
port_thread_rf_da.obj:	precompiled.h port_thread_rf_da.h yukon.h \
		types.h ctidbgmem.h unsolicited_handler.h port_base.h \
		logManager.h dlldefs.h module_util.h ctitime.h version.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h critical_section.h \
		tbl_port_base.h dbmemobject.h dbaccess.h dllbase.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		tbl_pao_lite.h tbl_paoexclusion.h xfer.h dev_base.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		config_device.h rte_base.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h mgr_device.h rtdb.h slctdev.h smartmap.h \
		readers_writer_lock.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h connection_base.h worker_thread.h \
		config_exceptions.h exceptions.h msg_dbchg.h \
		millisecond_timer.h socket_helper.h win_helper.h port_rf_da.h \
		rfn_identifier.h rfn_e2e_messenger.h rfn_asid.h \
		RfnE2eDataIndicationMsg.h RfnE2eMsg.h RfnE2eDataConfirmMsg.h \
		NetworkManagerMessaging.h RfnE2eDataRequestMsg.h \
		NetworkManagerRequest.h c_port_interface.h elogger.h \
		portglob.h streamSocketListener.h streamSocketConnection.h \
		porter.h mgr_port.h dev_dnp.h dev_remote.h tbl_dialup.h \
		tbl_direct.h prot_dnp.h prot_base.h packet_finder.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h tbl_dv_address.h dev_rds.h \
		encryption_oneway_message.h portfield.h connection_client.h
port_thread_tcp.obj:	precompiled.h port_thread_tcp.h \
		unsolicited_handler.h port_base.h logManager.h dlldefs.h \
		module_util.h ctitime.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h critical_section.h \
		tbl_port_base.h dbmemobject.h dbaccess.h dllbase.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		tbl_pao_lite.h tbl_paoexclusion.h xfer.h dev_base.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		config_device.h rte_base.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h mgr_device.h rtdb.h slctdev.h smartmap.h \
		readers_writer_lock.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h connection_base.h worker_thread.h \
		config_exceptions.h exceptions.h msg_dbchg.h \
		millisecond_timer.h socket_helper.h win_helper.h \
		packet_finder.h port_tcp.h port_serial.h tbl_port_settings.h \
		tbl_port_timing.h tcp_connection_manager.h tcp_connection.h \
		tbl_paoproperty.h c_port_interface.h elogger.h portglob.h \
		streamSocketListener.h streamSocketConnection.h porter.h \
		portfield.h prot_gpuff.h prot_dnp.h prot_base.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h mgr_port.h database_reader.h
port_thread_udp.obj:	precompiled.h port_thread_udp.h yukon.h types.h \
		ctidbgmem.h unsolicited_handler.h port_base.h logManager.h \
		dlldefs.h module_util.h ctitime.h version.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h critical_section.h tbl_port_base.h \
		dbmemobject.h dbaccess.h dllbase.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h tbl_pao_lite.h \
		tbl_paoexclusion.h xfer.h dev_base.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		config_device.h rte_base.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h mgr_device.h rtdb.h slctdev.h smartmap.h \
		readers_writer_lock.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h connection_base.h worker_thread.h \
		config_exceptions.h exceptions.h msg_dbchg.h \
		millisecond_timer.h socket_helper.h win_helper.h port_udp.h \
		port_serial.h tbl_port_settings.h tbl_port_timing.h \
		tbl_port_tcpip.h EncodingFilterFactory.h EncodingFilter.h \
		c_port_interface.h elogger.h prot_gpuff.h packet_finder.h \
		portglob.h streamSocketListener.h streamSocketConnection.h \
		porter.h mgr_port.h dev_dnp.h dev_remote.h tbl_dialup.h \
		tbl_direct.h prot_dnp.h prot_base.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h tbl_dv_address.h dev_gridadvisor.h \
		dev_rds.h encryption_oneway_message.h portfield.h \
		connection_client.h
ripple.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h connection_client.h connection.h \
		message.h ctitime.h collectable.h loggable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h dsm2.h \
		streamConnection.h netports.h immutable.h dsm2err.h words.h \
		optional.h macro_offset.h porter.h devicetypes.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h dllbase.h tbl_pao_lite.h row_reader.h \
		tbl_rtcomm.h dbaccess.h resolvers.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		port_base.h logManager.h tbl_port_base.h tbl_paoexclusion.h \
		xfer.h dev_base.h dev_exclusion.h config_device.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h master.h scanner.h elogger.h \
		portglob.h streamSocketListener.h socket_helper.h \
		win_helper.h streamSocketConnection.h c_port_interface.h \
		dev_lcu.h dev_idlc.h dev_remote.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		config_exceptions.h exceptions.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h mgr_device.h rtdb.h \
		slctdev.h smartmap.h mgr_port.h
statisticsmanager.obj:	precompiled.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h ctitime.h dlldefs.h \
		yukon.h types.h ctidbgmem.h ThreadStatusKeeper.h \
		thread_register_data.h boost_time.h cticalls.h os2_2w32.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h thread_monitor.h smartmap.h dllbase.h \
		critical_section.h readers_writer_lock.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		cparms.h queue.h thread.h mutex.h database_reader.h \
		database_connection.h dbaccess.h row_reader.h \
		database_writer.h row_writer.h database_transaction.h \
		InvalidReaderException.h ctidate.h debug_timer.h \
		c_port_interface.h elogger.h mgr_device.h rtdb.h dev_base.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		pointdefs.h slctdev.h mgr_port.h port_base.h logManager.h \
		tbl_port_base.h xfer.h devicetypes.h
statisticsthread.obj:	precompiled.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h ctitime.h dlldefs.h \
		yukon.h types.h ctidbgmem.h ThreadStatusKeeper.h \
		thread_register_data.h boost_time.h cticalls.h os2_2w32.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h thread_monitor.h smartmap.h dllbase.h \
		critical_section.h readers_writer_lock.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		cparms.h queue.h thread.h mutex.h debug_timer.h \
		millisecond_timer.h portglob.h streamSocketListener.h \
		socket_helper.h timing_util.h win_helper.h \
		streamSocketConnection.h streamConnection.h netports.h \
		immutable.h porter.h dsm2.h dsm2err.h words.h optional.h \
		macro_offset.h devicetypes.h
systemmsgthread.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h counter.h guard.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		critical_section.h mutex.h cparms.h device_queue_interface.h \
		msg_cmd.h message.h collectable.h msg_pcrequest.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_queuedata.h msg_requestcancel.h systemmsgthread.h \
		connection.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		streamLocalConnection.h mgr_device.h rtdb.h dllbase.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		config_device.h rte_base.h dbmemobject.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h slctdev.h \
		smartmap.h mgr_port.h port_base.h logManager.h \
		tbl_port_base.h xfer.h devicetypes.h thread.h portdecl.h \
		porter.h
tcp_connection.obj:	precompiled.h cparms.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h socket_helper.h \
		guard.h timing_util.h win_helper.h tcp_connection.h
tcp_connection_manager.obj:	precompiled.h tcp_connection_manager.h \
		tcp_connection.h ctitime.h dlldefs.h socket_helper.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h timing_util.h \
		win_helper.h packet_finder.h cparms.h
test_gen_reply.obj:	trx_711.h trx_info.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h porter.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		devicetypes.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h dllbase.h message.h collectable.h \
		tbl_pao_lite.h row_reader.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h port_base.h \
		logManager.h tbl_port_base.h tbl_paoexclusion.h xfer.h \
		dev_base.h dev_exclusion.h config_device.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h cparms.h
test_lantronixencryption.obj:	encryption_lantronix.h EncodingFilter.h
test_paostatisticsrecord.obj:	PaoStatisticsRecord.h ctitime.h \
		dlldefs.h yukon.h types.h ctidbgmem.h ctidate.h
traceset.obj:	precompiled.h dlldefs.h
unsolicited_handler.obj:	precompiled.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		unsolicited_handler.h port_base.h logManager.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		critical_section.h tbl_port_base.h dbmemobject.h dbaccess.h \
		dllbase.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h tbl_pao_lite.h tbl_paoexclusion.h xfer.h \
		dev_base.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h config_device.h rte_base.h message.h \
		collectable.h tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h devicetypes.h mgr_device.h rtdb.h \
		slctdev.h smartmap.h readers_writer_lock.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h connection_base.h worker_thread.h \
		config_exceptions.h exceptions.h msg_dbchg.h \
		millisecond_timer.h socket_helper.h win_helper.h prot_gpuff.h \
		packet_finder.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		portglob.h streamSocketListener.h streamSocketConnection.h \
		porter.h dev_dnp.h dev_remote.h tbl_dialup.h tbl_direct.h \
		prot_dnp.h prot_base.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h \
		tbl_dv_address.h msg_trace.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h ThreadStatusKeeper.h \
		thread_register_data.h boost_time.h thread_monitor.h thread.h \
		connection_client.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
