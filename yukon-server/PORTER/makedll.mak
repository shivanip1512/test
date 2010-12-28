# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(PORTER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(PROT)\include \
-I$(MSG)\include \
-I$(BOOST) \
-I$(SQLAPI)\include \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
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
;$(BOOST) \
;$(RW)


DLLOBJS=\
port_shr.obj \
port_shr_ip.obj \
portglob.obj \
dllmain.obj


PORTER_DLL_FULLBUILD = $[Filename,$(OBJ),PorterDllFullBuild,target]



ALL:            portglob.dll


$(PORTER_DLL_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) /D_DLL_PORTGLOB $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DLLOBJS)]



portglob.dll:  $(PORTER_DLL_FULLBUILD) $(DLLOBJS) Makedll.mak
               @build -nologo -f $(_InputFile) id
               @echo Building  ..\$@
               @%cd $(OBJ)
               $(CC) $(RWCPPFLAGS) $(DLLFLAGS) $(DLLOBJS) id_pgdll.obj $(INCLPATHS) /Fe..\$@ -link $(LIBS) $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\ctiprtdb.lib $(COMPILEBASE)\lib\ctidbsrc.lib $(COMPILEBASE)\lib\cticparms.lib $(RWLIBS) $(BOOST_LIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\portglob.dll copy bin\portglob.dll $(YUKONOUTPUT)
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
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_PORTGLOB $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<


# The lines below accomplish the ID'ing of the project!
id:
            @build -nologo -f $(_InputFile) id_pgdll.obj

id_pgdll.obj:    id_pgdll.cpp include\id_pgdll.h


#UPDATE#
disp_thd.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h ctinexus.h porter.h \
		devicetypes.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h queent.h \
		pil_conmgr.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		message.h collectable.h con_mgr.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h ctibase.h pil_exefct.h executorfactory.h executor.h \
		exe_cmd.h exe_reg.h pilserver.h server_b.h critical_Section.h \
		smartmap.h readers_writer_lock.h msg_pcrequest.h mgr_device.h \
		rtdb.h hashkey.h hash_functions.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h tbl_static_paoinfo.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h slctdev.h mgr_point.h fifo_multiset.h \
		mgr_route.h repeaterrole.h mgr_config.h msg_pcreturn.h \
		msg_dbchg.h msg_cmd.h mgr_port.h port_base.h tbl_port_base.h \
		xfer.h counter.h tbl_port_statistics.h slctprt.h \
		thread_monitor.h thread_register_data.h portglob.h \
		statistics.h unsolicited_handler.h dev_single.h \
		tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		millisecond_timer.h
dllmain.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h portglob.h ctinexus.h porter.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h statistics.h \
		row_reader.h
encodingfilterfactory.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		EncodingFilterFactory.h EncodingFilter.h port_udp.h \
		port_serial.h port_base.h dev_base.h dsm2.h mutex.h dlldefs.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h sorted_vector.h cticonnect.h netports.h dsm2err.h \
		words.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h sema.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		tbl_port_statistics.h tbl_port_settings.h tbl_port_timing.h \
		tbl_port_tcpip.h encryption_lantronix.h encryption_noop.h
encryption_lantronix.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		encryption_lantronix.h EncodingFilter.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h \
		CtiPCPtrQueue.h
encryption_noop.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		encryption_noOp.h EncodingFilter.h
id_pgdll.obj:	yukon.h precompiled.h types.h ctidbgmem.h id_pgdll.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		numstr.h sorted_vector.h
id_porter.obj:	yukon.h precompiled.h types.h ctidbgmem.h id_porter.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		numstr.h sorted_vector.h
phlidlc.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h queues.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h porter.h devicetypes.h \
		portdecl.h rte_base.h boostutil.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h dllbase.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h msg_signal.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h tbl_base.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h tbl_static_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h tbl_port_base.h xfer.h critical_section.h \
		counter.h tbl_port_statistics.h cparms.h configkey.h \
		configval.h portglob.h statistics.h mgr_port.h smartmap.h \
		readers_writer_lock.h slctprt.h trx_711.h trx_info.h
plidlc.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h cti_asmc.h queues.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h porter.h \
		devicetypes.h portglob.h ctinexus.h logger.h thread.h \
		CtiPCPtrQueue.h statistics.h row_reader.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h sema.h database_reader.h boost_time.h \
		config_device.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h trx_info.h \
		critical_section.h
portconf.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		porter.h devicetypes.h master.h elogger.h portglob.h \
		ctinexus.h logger.h thread.h CtiPCPtrQueue.h statistics.h \
		c_port_interface.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h portdecl.h \
		port_base.h tbl_port_base.h xfer.h critical_section.h \
		counter.h tbl_port_statistics.h mgr_device.h rtdb.h slctdev.h \
		smartmap.h readers_writer_lock.h mgr_route.h repeaterrole.h
portdialback.obj:	yukon.h precompiled.h types.h ctidbgmem.h cticalls.h \
		os2_2w32.h dlldefs.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h mgr_device.h \
		rtdb.h hashkey.h hash_functions.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_property.h tbl_pt_trigger.h slctdev.h smartmap.h \
		readers_writer_lock.h critical_section.h mgr_port.h \
		port_base.h tbl_port_base.h xfer.h counter.h \
		tbl_port_statistics.h slctprt.h msg_cmd.h pilserver.h \
		server_b.h con_mgr.h msg_pcrequest.h mgr_point.h \
		fifo_multiset.h mgr_route.h repeaterrole.h mgr_config.h \
		portglob.h porter.h devicetypes.h statistics.h
portentry.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h ctitime.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h porter.h devicetypes.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		db_entry_defines.h desolvers.h msg_signal.h port_base.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h tbl_static_paoinfo.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h tbl_port_base.h xfer.h critical_section.h \
		counter.h tbl_port_statistics.h master.h portglob.h \
		statistics.h c_port_interface.h elogger.h mgr_port.h \
		smartmap.h readers_writer_lock.h slctprt.h mgr_device.h \
		rtdb.h slctdev.h dev_lcu.h dev_idlc.h dev_remote.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h CtiLocalConnect.h \
		fifo_multiset.h prot_emetcon.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h tbl_route.h tbl_carrier.h \
		cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		config_data_mct.h ctidate.h
porter.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h color.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h porter.h devicetypes.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h msg_signal.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h tbl_base.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h tbl_static_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h tbl_port_base.h xfer.h critical_section.h \
		counter.h tbl_port_statistics.h portverify.h queue.h \
		verification_objects.h master.h elogger.h thread_monitor.h \
		smartmap.h readers_writer_lock.h thread_register_data.h \
		CtiLocalConnect.h fifo_multiset.h systemmsgthread.h \
		connection.h exchange.h msg_multi.h msg_pdata.h msg_ptreg.h \
		msg_reg.h mgr_device.h rtdb.h slctdev.h mgr_port.h slctprt.h \
		portglob.h statistics.h c_port_interface.h mgr_route.h \
		repeaterrole.h mgr_config.h mgr_point.h port_thread_udp.h \
		unsolicited_handler.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		msg_dbchg.h millisecond_timer.h port_udp.h port_serial.h \
		tbl_port_settings.h tbl_port_timing.h tbl_port_tcpip.h \
		EncodingFilterFactory.h EncodingFilter.h port_thread_tcp.h \
		packet_finder.h port_tcp.h tcp_connection_manager.h \
		tcp_connection.h port_shr.h port_shr_ip.h msg_trace.h \
		rte_macro.h tbl_rtmacro.h rte_xcu.h eventlog.h configparms.h \
		trx_711.h trx_info.h dllyukon.h pilserver.h server_b.h \
		con_mgr.h dev_ccu721.h dev_remote.h tbl_dialup.h tbl_direct.h \
		tbl_dv_address.h dev_ccu721_queue_interface.h \
		device_queue_interface.h prot_klondike.h prot_wrap.h \
		prot_idlc.h
portersu.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h color.h connection.h exchange.h \
		dllbase.h dsm2.h mutex.h guard.h utility.h ctitime.h queues.h \
		numstr.h sorted_vector.h cticonnect.h netports.h dsm2err.h \
		words.h logger.h thread.h CtiPCPtrQueue.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h porter.h devicetypes.h master.h elogger.h \
		portglob.h ctinexus.h statistics.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctibase.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		db_entry_defines.h desolvers.h msg_signal.h port_base.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h tbl_static_paoinfo.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h tbl_port_base.h xfer.h critical_section.h \
		counter.h tbl_port_statistics.h c_port_interface.h rtdb.h \
		mgr_port.h smartmap.h readers_writer_lock.h slctprt.h \
		mgr_device.h slctdev.h msg_commerrorhistory.h
portfield.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h cti_asmc.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h color.h dev_lcu.h dev_idlc.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		critical_section.h porter.h devicetypes.h trx_711.h dev_tap.h \
		dev_paging.h tbl_dv_tappaging.h dev_ied.h tbl_dv_ied.h \
		dev_snpp.h dev_modbus.h prot_modbus.h tbl_dv_address.h \
		dev_pagingreceiver.h tbl_dv_pagingreceiver.h dev_tnpp.h \
		tbl_dv_tnpp.h dev_rtc.h tbl_dv_rtc.h dev_rtm.h \
		verification_objects.h dev_fmu.h dev_wctp.h master.h \
		portdecl.h port_base.h tbl_port_base.h counter.h \
		tbl_port_statistics.h tapterm.h portglob.h statistics.h \
		prot_sa3rdparty.h protocol_sa.h portverify.h \
		c_port_interface.h elogger.h mgr_port.h smartmap.h \
		readers_writer_lock.h slctprt.h mgr_device.h rtdb.h slctdev.h \
		dev_cbc6510.h dev_dnp.h prot_dnp.h packet_finder.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h dev_ccu721.h \
		dev_ccu721_queue_interface.h device_queue_interface.h \
		prot_klondike.h prot_wrap.h prot_idlc.h fifo_multiset.h \
		dev_schlum.h dev_meter.h tbl_metergrp.h vcomdefs.h dev_kv2.h \
		prot_ansi_kv2.h prot_ansi.h ansi_application.h \
		ansi_datalink.h ansi_billing_table.h std_ansi_tbl_base.h \
		std_ansi_tbl_00.h std_ansi_tbl_01.h std_ansi_tbl_08.h \
		std_ansi_tbl_10.h std_ansi_tbl_11.h std_ansi_tbl_12.h \
		std_ansi_tbl_13.h std_ansi_tbl_14.h std_ansi_tbl_15.h \
		std_ansi_tbl_16.h std_ansi_tbl_21.h std_ansi_tbl_22.h \
		std_ansi_tbl_23.h std_ansi_tbl_25.h std_ansi_tbl_27.h \
		std_ansi_tbl_28.h std_ansi_tbl_31.h std_ansi_tbl_32.h \
		std_ansi_tbl_33.h std_ansi_tbl_51.h std_ansi_tbl_52.h \
		std_ansi_tbl_61.h std_ansi_tbl_62.h std_ansi_tbl_63.h \
		std_ansi_tbl_64.h ansi_kv2_mtable_000.h ansi_kv2_mtable_070.h \
		ansi_kv2_mtable_110.h dllyukon.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		cmd_dlc.h cmd_base.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h dev_sentinel.h dev_ansi.h pt_analog.h \
		tbl_pt_analog.h pt_status.h tbl_pt_status.h \
		prot_ansi_sentinel.h dev_focus.h prot_ansi_focus.h \
		dev_mark_v.h prot_transdata.h transdata_application.h \
		transdata_tracker.h transdata_datalink.h prot_ymodem.h \
		ctidate.h transdata_data.h msg_cmd.h msg_trace.h pilserver.h \
		server_b.h con_mgr.h mgr_point.h mgr_route.h repeaterrole.h \
		mgr_config.h port_udp.h port_serial.h tbl_port_settings.h \
		tbl_port_timing.h tbl_port_tcpip.h prot_711.h portfield.h
portfill.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		porter.h devicetypes.h portdecl.h rte_base.h dbmemobject.h \
		cmdparse.h ctitokenizer.h parsevalue.h ctibase.h ctinexus.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h CtiPCPtrQueue.h msg_signal.h port_base.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h tbl_static_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_property.h tbl_pt_trigger.h tbl_port_base.h xfer.h \
		critical_section.h counter.h tbl_port_statistics.h master.h \
		portglob.h statistics.h c_port_interface.h elogger.h rtdb.h \
		mgr_device.h slctdev.h smartmap.h readers_writer_lock.h \
		mgr_port.h slctprt.h mgr_route.h repeaterrole.h dev_tcu.h \
		dev_idlc.h dev_remote.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h dev_tap.h \
		dev_paging.h tbl_dv_tappaging.h dev_ied.h tbl_dv_ied.h \
		dev_snpp.h dev_tnpp.h tbl_dv_tnpp.h dev_pagingreceiver.h \
		tbl_dv_pagingreceiver.h dev_wctp.h prot_versacom.h \
		expresscom.h ctistring.h
portglob.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h queues.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h porter.h devicetypes.h \
		portglob.h ctinexus.h logger.h thread.h CtiPCPtrQueue.h \
		statistics.h row_reader.h
portgw.obj:	yukon.h precompiled.h types.h ctidbgmem.h connection.h \
		dlldefs.h exchange.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		logger.h thread.h CtiPCPtrQueue.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h dllyukon.h \
		mgr_device.h rtdb.h hashkey.h hash_functions.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_property.h tbl_pt_trigger.h slctdev.h smartmap.h \
		readers_writer_lock.h critical_section.h mgr_port.h \
		port_base.h tbl_port_base.h xfer.h counter.h \
		tbl_port_statistics.h slctprt.h mgr_route.h repeaterrole.h \
		dev_gateway.h dev_gwstat.h dev_ied.h dev_remote.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h gateway.h pending_stat_operation.h \
		dev_grp_energypro.h dev_grp.h msg_lmcontrolhistory.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_dv_expresscom.h vcomdefs.h portdecl.h porter.h \
		devicetypes.h portglob.h statistics.h thread_monitor.h \
		thread_register_data.h ThreadStatusKeeper.h
portload.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h queues.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h porter.h devicetypes.h \
		portglob.h ctinexus.h logger.h thread.h CtiPCPtrQueue.h \
		statistics.h row_reader.h portdecl.h rte_base.h boostutil.h \
		dbmemobject.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctibase.h dllbase.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		boost_time.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		port_base.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h hashkey.h hash_functions.h tbl_base.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		tbl_port_statistics.h c_port_interface.h elogger.h mgr_port.h \
		smartmap.h readers_writer_lock.h slctprt.h mgr_device.h \
		rtdb.h slctdev.h mgr_route.h repeaterrole.h rte_ccu.h \
		rte_xcu.h msg_pcrequest.h tbl_rtcarrier.h tbl_rtrepeater.h \
		trx_711.h trx_info.h dev_ccu.h dev_idlc.h dev_remote.h \
		dev_single.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h dev_ccu_queue_interface.h \
		device_queue_interface.h dev_ccu721.h tbl_dv_address.h \
		dev_ccu721_queue_interface.h prot_klondike.h prot_wrap.h \
		prot_idlc.h fifo_multiset.h
portmain.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h portsvc.h cservice.h CServiceConfig.h ctibase.h \
		ctinexus.h netports.h cticonnect.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h dsm2err.h words.h portglob.h porter.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h statistics.h \
		row_reader.h cparms.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h boost_time.h boostutil.h \
		configkey.h configval.h configparms.h
portperf.obj:	yukon.h precompiled.h types.h ctidbgmem.h portglob.h \
		dlldefs.h ctinexus.h netports.h cticonnect.h porter.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h sorted_vector.h dsm2err.h words.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h statistics.h \
		row_reader.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h sema.h database_reader.h boost_time.h \
		boostutil.h configkey.h configval.h thread_monitor.h \
		smartmap.h readers_writer_lock.h critical_section.h queue.h \
		thread_register_data.h ThreadStatusKeeper.h debug_timer.h
portpil.obj:	yukon.h precompiled.h types.h ctidbgmem.h mgr_device.h \
		dlldefs.h rtdb.h hashkey.h hash_functions.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h dllbase.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h rte_base.h dbmemobject.h ctibase.h ctinexus.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h tbl_static_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h slctdev.h smartmap.h readers_writer_lock.h \
		critical_section.h mgr_route.h repeaterrole.h mgr_point.h \
		fifo_multiset.h pilserver.h server_b.h con_mgr.h connection.h \
		exchange.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_pcrequest.h \
		mgr_config.h dllyukon.h portglob.h porter.h devicetypes.h \
		statistics.h thread_monitor.h thread_register_data.h \
		ThreadStatusKeeper.h
portpool.obj:	yukon.h precompiled.h types.h ctidbgmem.h connection.h \
		dlldefs.h exchange.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		logger.h thread.h CtiPCPtrQueue.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_device.h rtdb.h hashkey.h hash_functions.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_property.h tbl_pt_trigger.h slctdev.h smartmap.h \
		readers_writer_lock.h critical_section.h mgr_port.h \
		port_base.h tbl_port_base.h xfer.h counter.h \
		tbl_port_statistics.h slctprt.h port_pool_out.h portdecl.h \
		porter.h devicetypes.h portglob.h statistics.h
portque.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		porter.h devicetypes.h elogger.h thread_monitor.h smartmap.h \
		readers_writer_lock.h critical_section.h queue.h logger.h \
		thread.h CtiPCPtrQueue.h thread_register_data.h \
		ThreadStatusKeeper.h portglob.h ctinexus.h statistics.h \
		portdecl.h rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h tbl_base.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h tbl_static_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h tbl_port_base.h xfer.h counter.h \
		tbl_port_statistics.h c_port_interface.h mgr_device.h rtdb.h \
		slctdev.h mgr_port.h slctprt.h port_shr.h trx_711.h \
		trx_info.h dev_ccu.h dev_idlc.h dev_remote.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h dev_ccu_queue_interface.h \
		device_queue_interface.h dev_ccu721.h tbl_dv_address.h \
		dev_ccu721_queue_interface.h prot_klondike.h prot_wrap.h \
		prot_idlc.h fifo_multiset.h prot_emetcon.h
portsvc.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h portsvc.h cservice.h ctibase.h ctinexus.h \
		netports.h cticonnect.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		sorted_vector.h dsm2err.h words.h portglob.h porter.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h statistics.h \
		row_reader.h
porttime.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h queues.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h porter.h devicetypes.h \
		portdecl.h rte_base.h boostutil.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h dllbase.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h msg_signal.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h tbl_base.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h tbl_static_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h tbl_port_base.h xfer.h critical_section.h \
		counter.h tbl_port_statistics.h elogger.h portglob.h \
		statistics.h c_port_interface.h mgr_port.h smartmap.h \
		readers_writer_lock.h slctprt.h mgr_device.h rtdb.h slctdev.h \
		dev_ccu.h dev_idlc.h dev_remote.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h \
		dev_ccu_queue_interface.h device_queue_interface.h \
		dev_ccu721.h tbl_dv_address.h dev_ccu721_queue_interface.h \
		prot_klondike.h prot_wrap.h prot_idlc.h fifo_multiset.h \
		dev_dnp.h prot_dnp.h packet_finder.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h dev_ilex.h \
		dev_mct4xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h ctidate.h mgr_route.h \
		repeaterrole.h thread_monitor.h thread_register_data.h \
		ThreadStatusKeeper.h prot_welco.h prot_lmi.h prot_seriesv.h \
		verification_objects.h
portverify.obj:	yukon.h precompiled.h types.h ctidbgmem.h boost_time.h \
		mgr_port.h dlldefs.h smartmap.h boostutil.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h dllbase.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h readers_writer_lock.h \
		critical_section.h port_base.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h tbl_static_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_property.h tbl_pt_trigger.h tbl_port_base.h xfer.h \
		counter.h tbl_port_statistics.h slctprt.h cparms.h \
		configkey.h configval.h portverify.h queue.h \
		verification_objects.h ctidate.h database_writer.h \
		row_writer.h
port_shr.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctinexus.h \
		dlldefs.h netports.h cticonnect.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h dsm2err.h words.h port_shr.h thread.h \
		logger.h CtiPCPtrQueue.h port_base.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h sema.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		tbl_port_statistics.h cparms.h configkey.h configval.h
port_shr_ip.obj:	yukon.h precompiled.h types.h ctidbgmem.h cparms.h \
		dlldefs.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h port_shr_ip.h ctinexus.h \
		port_shr.h port_base.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		tbl_port_statistics.h prot_welco.h porter.h devicetypes.h \
		cti_asmc.h
port_thread_tcp.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		port_thread_tcp.h unsolicited_handler.h port_base.h \
		dev_base.h dsm2.h mutex.h dlldefs.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h sema.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		tbl_port_statistics.h mgr_device.h rtdb.h slctdev.h \
		smartmap.h readers_writer_lock.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h \
		msg_dbchg.h millisecond_timer.h packet_finder.h port_tcp.h \
		port_serial.h tbl_port_settings.h tbl_port_timing.h \
		tcp_connection_manager.h tcp_connection.h tbl_paoproperty.h \
		c_port_interface.h elogger.h portglob.h porter.h \
		devicetypes.h statistics.h portfield.h prot_gpuff.h \
		prot_dnp.h dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h mgr_port.h slctprt.h
port_thread_udp.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		port_thread_udp.h unsolicited_handler.h port_base.h \
		dev_base.h dsm2.h mutex.h dlldefs.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h sema.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		tbl_port_statistics.h mgr_device.h rtdb.h slctdev.h \
		smartmap.h readers_writer_lock.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h \
		msg_dbchg.h millisecond_timer.h port_udp.h port_serial.h \
		tbl_port_settings.h tbl_port_timing.h tbl_port_tcpip.h \
		EncodingFilterFactory.h EncodingFilter.h c_port_interface.h \
		elogger.h prot_gpuff.h packet_finder.h portglob.h porter.h \
		devicetypes.h statistics.h mgr_port.h slctprt.h dev_dnp.h \
		dev_remote.h tbl_dialup.h tbl_direct.h prot_dnp.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h dev_gridadvisor.h \
		dev_rds.h portdecl.h portfield.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
ripple.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h connection.h exchange.h dllbase.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		logger.h thread.h CtiPCPtrQueue.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h porter.h \
		devicetypes.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h db_entry_defines.h \
		desolvers.h msg_signal.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h tbl_base.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h tbl_static_paoinfo.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h tbl_port_base.h xfer.h critical_section.h \
		counter.h tbl_port_statistics.h master.h scanner.h elogger.h \
		portglob.h statistics.h c_port_interface.h dev_lcu.h \
		dev_idlc.h dev_remote.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h mgr_device.h rtdb.h slctdev.h smartmap.h \
		readers_writer_lock.h mgr_port.h slctprt.h
systemmsgthread.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h counter.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h sorted_vector.h mutex.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h device_queue_interface.h msg_cmd.h \
		message.h collectable.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_queuedata.h msg_requestcancel.h systemmsgthread.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		msg_ptreg.h msg_reg.h queue.h ctilocalconnect.h \
		critical_section.h fifo_multiset.h mgr_device.h rtdb.h \
		hashkey.h hash_functions.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_property.h tbl_pt_trigger.h slctdev.h smartmap.h \
		readers_writer_lock.h mgr_port.h port_base.h tbl_port_base.h \
		xfer.h tbl_port_statistics.h slctprt.h portdecl.h porter.h \
		devicetypes.h
tcp_connection.obj:	yukon.h precompiled.h types.h ctidbgmem.h cparms.h \
		dlldefs.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h tcp_connection.h
tcp_connection_manager.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tcp_connection_manager.h tcp_connection.h ctitime.h dlldefs.h \
		packet_finder.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h
test_lantronixencryption.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h encryption_lantronix.h EncodingFilter.h numstr.h \
		dlldefs.h
traceset.obj:	yukon.h precompiled.h types.h ctidbgmem.h dlldefs.h
unsolicited_handler.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h numstr.h sorted_vector.h unsolicited_handler.h \
		port_base.h dev_base.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h sema.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_static_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		tbl_port_statistics.h mgr_device.h rtdb.h slctdev.h \
		smartmap.h readers_writer_lock.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h \
		msg_dbchg.h millisecond_timer.h prot_gpuff.h packet_finder.h \
		portglob.h porter.h devicetypes.h statistics.h dev_dnp.h \
		dev_remote.h tbl_dialup.h tbl_direct.h prot_dnp.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h msg_trace.h \
		portdecl.h
#ENDUPDATE#
