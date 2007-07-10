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
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST) \
;$(RW)


DLLOBJS=\
port_shr.obj \
port_shr_ip.obj \
portglob.obj \
dllmain.obj



ALL:            portglob.dll


portglob.dll:  $(DLLOBJS) Makedll.mak
               @$(MAKE) -nologo -f $(_InputFile) id
               @echo Building  ..\$@
               @%cd $(OBJ)
               $(CC) $(RWCPPFLAGS) $(DLLFLAGS) $(DLLOBJS) id_pgdll.obj $(INCLPATHS) /Fe..\$@ -link $(LIBS) $(COMPILEBASE)\lib\tcpsup.lib $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\clrdump.lib $(COMPILEBASE)\lib\ctiprtdb.lib $(COMPILEBASE)\lib\ctidbsrc.lib $(COMPILEBASE)\lib\cticparms.lib $(RWLIBS) $(BOOSTLIBS)
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
            @$(MAKE) -nologo -f $(_InputFile) id_pgdll.obj

id_pgdll.obj:    id_pgdll.cpp include\id_pgdll.h id_vinfo.h


#UPDATE#
contest.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h master.h ilex.h perform.h \
		portglob.h tcpsup.h ctinexus.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h statistics.h \
		counter.h ctidate.h color.h
disp_thd.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h ctinexus.h porter.h \
		dsm2err.h devicetypes.h queues.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h queent.h pil_conmgr.h \
		exchange.h dllbase.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h sorted_vector.h message.h collectable.h con_mgr.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h ctibase.h pil_exefct.h executorfactory.h \
		executor.h exe_cmd.h exe_reg.h pilserver.h server_b.h \
		cmdopts.h argkey.h argval.h critical_Section.h smartmap.h \
		hashkey.h hash_functions.h msg_pcrequest.h mgr_device.h \
		rtdb.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h rte_base.h dbmemobject.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h slctdev.h mgr_route.h repeaterrole.h \
		mgr_config.h msg_pcreturn.h msg_dbchg.h msg_cmd.h mgr_port.h \
		port_base.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h slctprt.h thread_monitor.h \
		thread_register_data.h mgr_point.h slctpnt.h portglob.h \
		tcpsup.h statistics.h ctidate.h port_udp.h dev_single.h \
		tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h
dllmain.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h portglob.h tcpsup.h ctinexus.h \
		porter.h dsm2err.h devicetypes.h queues.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h sorted_vector.h \
		statistics.h counter.h ctidate.h
id_pgdll.obj:	yukon.h precompiled.h ctidbgmem.h id_pgdll.h utility.h \
		ctitime.h dlldefs.h queues.h types.h numstr.h sorted_vector.h \
		id_vinfo.h
id_porter.obj:	yukon.h precompiled.h ctidbgmem.h id_porter.h utility.h \
		ctitime.h dlldefs.h queues.h types.h numstr.h sorted_vector.h \
		id_vinfo.h
perform3.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h drp.h elogger.h alarmlog.h porter.h \
		portglob.h tcpsup.h ctinexus.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h statistics.h \
		counter.h ctidate.h c_port_interface.h group.h perform.h
phlidlc.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctibase.h ctinexus.h dllbase.h message.h collectable.h \
		rwutil.h ctitime.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		sorted_vector.h msg_signal.h port_base.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h dialup.h critical_section.h \
		tbl_port_statistics.h cparms.h configkey.h configval.h \
		portglob.h tcpsup.h statistics.h ctidate.h mgr_port.h \
		smartmap.h hashkey.h hash_functions.h slctprt.h trx_711.h \
		trx_info.h
plidlc.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cti_asmc.h queues.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h portglob.h tcpsup.h \
		ctinexus.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h sorted_vector.h statistics.h counter.h ctidate.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h dllbase.h \
		config_base.h config_resolvers.h rte_base.h dbmemobject.h \
		ctibase.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h trx_info.h
portconf.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cparms.h rwutil.h ctitime.h boost_time.h \
		configkey.h configval.h queues.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h master.h group.h elogger.h \
		portglob.h tcpsup.h ctinexus.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h statistics.h \
		counter.h ctidate.h c_port_interface.h alarmlog.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h dllbase.h config_base.h \
		config_resolvers.h rte_base.h dbmemobject.h ctibase.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h portdecl.h \
		port_base.h tbl_port_base.h xfer.h dialup.h \
		critical_section.h tbl_port_statistics.h hashkey.h \
		hash_functions.h mgr_device.h rtdb.h slctdev.h smartmap.h \
		mgr_route.h repeaterrole.h
portdialback.obj:	yukon.h precompiled.h ctidbgmem.h cticalls.h \
		os2_2w32.h dlldefs.h types.h ctitypes.h cparms.h rwutil.h \
		ctitime.h boost_time.h configkey.h configval.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		connection.h exchange.h dllbase.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h mgr_device.h rtdb.h hashkey.h \
		hash_functions.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h config_base.h config_resolvers.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h slctdev.h smartmap.h \
		mgr_port.h port_base.h tbl_port_base.h xfer.h dialup.h \
		critical_section.h tbl_port_statistics.h slctprt.h msg_cmd.h \
		pilserver.h server_b.h con_mgr.h cmdopts.h argkey.h argval.h \
		msg_pcrequest.h mgr_route.h repeaterrole.h mgr_config.h \
		portglob.h tcpsup.h porter.h dsm2err.h devicetypes.h \
		statistics.h ctidate.h
portentry.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h ctitime.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h connection.h exchange.h dllbase.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		dsm2err.h device.h devicetypes.h routes.h porter.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h port_base.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		master.h ilex.h perform.h portglob.h tcpsup.h statistics.h \
		ctidate.h color.h c_port_interface.h group.h elogger.h \
		alarmlog.h mgr_port.h smartmap.h hashkey.h hash_functions.h \
		slctprt.h mgr_device.h rtdb.h slctdev.h dev_lcu.h ctitypes.h \
		dev_idlc.h dev_remote.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h mgr_point.h slctpnt.h CtiLocalConnect.h \
		prot_emetcon.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h tbl_route.h tbl_carrier.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h config_parts.h
porter.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h color.h cparms.h rwutil.h ctitime.h \
		boost_time.h configkey.h configval.h queues.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h dllbase.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h sorted_vector.h \
		msg_signal.h port_base.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h config_base.h \
		config_resolvers.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		portverify.h queue.h verification_objects.h master.h \
		elogger.h alarmlog.h drp.h thread_monitor.h smartmap.h \
		hashkey.h hash_functions.h thread_register_data.h \
		CtiLocalConnect.h systemmsgthread.h connection.h exchange.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h mgr_device.h \
		rtdb.h slctdev.h mgr_port.h slctprt.h perform.h das08.h \
		portgui.h portglob.h tcpsup.h statistics.h ctidate.h \
		c_port_interface.h group.h mgr_route.h repeaterrole.h \
		mgr_config.h mgr_point.h slctpnt.h port_shr.h port_shr_ip.h \
		dlldev.h msg_dbchg.h msg_trace.h eventlog.h configparms.h \
		trx_711.h trx_info.h dllyukon.h pilserver.h server_b.h \
		con_mgr.h cmdopts.h argkey.h argval.h msg_pcrequest.h
portersu.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h color.h connection.h exchange.h dllbase.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h message.h collectable.h \
		rwutil.h boost_time.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h master.h elogger.h alarmlog.h drp.h perform.h \
		das08.h portglob.h tcpsup.h ctinexus.h statistics.h counter.h \
		ctidate.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		c_port_interface.h group.h rtdb.h hashkey.h hash_functions.h \
		mgr_port.h smartmap.h slctprt.h mgr_device.h slctdev.h \
		msg_commerrorhistory.h
portfield.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cti_asmc.h cparms.h rwutil.h ctitime.h \
		boost_time.h configkey.h configval.h color.h queues.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dsm2err.h device.h devicetypes.h dev_lcu.h ctitypes.h \
		dev_idlc.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h dllbase.h \
		config_base.h config_resolvers.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h \
		trx_info.h porter.h trx_711.h mgr_point.h smartmap.h \
		hashkey.h hash_functions.h slctpnt.h dev_tap.h \
		tbl_dv_tappaging.h dev_ied.h tbl_dv_ied.h dev_snpp.h \
		dev_modbus.h prot_modbus.h tbl_dv_address.h \
		dev_pagingreceiver.h tbl_dv_pagingreceiver.h dev_tnpp.h \
		tbl_dv_tnpp.h dev_rtc.h tbl_dv_rtc.h dev_rtm.h \
		verification_objects.h dev_fmu.h dev_wctp.h routes.h master.h \
		portdecl.h port_base.h tbl_port_base.h critical_section.h \
		tbl_port_statistics.h tcpsup.h perform.h tapterm.h portglob.h \
		statistics.h ctidate.h prot_sa3rdparty.h protocol_sa.h \
		portverify.h c_port_interface.h group.h elogger.h alarmlog.h \
		mgr_port.h slctprt.h mgr_device.h rtdb.h slctdev.h \
		dev_cbc6510.h dev_dnp.h prot_dnp.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h dev_schlum.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dev_kv2.h \
		prot_ansi_kv2.h prot_ansi.h ansi_application.h \
		ansi_datalink.h ansi_billing_table.h std_ansi_tbl_base.h \
		std_ansi_tbl_zero_zero.h std_ansi_tbl_zero_one.h \
		std_ansi_tbl_zero_eight.h std_ansi_tbl_one_zero.h \
		std_ansi_tbl_one_one.h std_ansi_tbl_one_two.h \
		std_ansi_tbl_one_three.h std_ansi_tbl_one_four.h \
		std_ansi_tbl_one_five.h std_ansi_tbl_one_six.h \
		std_ansi_tbl_two_one.h std_ansi_tbl_two_two.h \
		std_ansi_tbl_two_three.h std_ansi_tbl_two_five.h \
		std_ansi_tbl_two_seven.h std_ansi_tbl_two_eight.h \
		std_ansi_tbl_three_one.h std_ansi_tbl_three_two.h \
		std_ansi_tbl_three_three.h std_ansi_tbl_five_one.h \
		std_ansi_tbl_five_two.h std_ansi_tbl_six_one.h \
		std_ansi_tbl_six_two.h std_ansi_tbl_six_three.h \
		std_ansi_tbl_six_four.h ansi_kv2_mtable_zero.h \
		ansi_kv2_mtable_seventy.h ansi_kv2_mtable_onehundredten.h \
		dllyukon.h dev_mct.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h config_parts.h \
		dev_sentinel.h prot_ansi_sentinel.h dev_mark_v.h \
		prot_transdata.h transdata_application.h transdata_tracker.h \
		transdata_datalink.h prot_ymodem.h transdata_data.h msg_cmd.h \
		msg_trace.h pilserver.h server_b.h con_mgr.h cmdopts.h \
		argkey.h argval.h mgr_route.h repeaterrole.h mgr_config.h \
		port_udp.h prot_711.h
portfill.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cparms.h rwutil.h ctitime.h boost_time.h \
		configkey.h configval.h queues.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctibase.h ctinexus.h dllbase.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h msg_signal.h \
		port_base.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h config_base.h \
		config_resolvers.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		master.h portglob.h tcpsup.h statistics.h ctidate.h \
		c_port_interface.h group.h elogger.h alarmlog.h rtdb.h \
		hashkey.h hash_functions.h mgr_device.h slctdev.h smartmap.h \
		mgr_port.h slctprt.h mgr_route.h repeaterrole.h dev_tcu.h \
		ctitypes.h dev_idlc.h dev_remote.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h prot_base.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		mgr_point.h slctpnt.h dev_tap.h tbl_dv_tappaging.h dev_ied.h \
		tbl_dv_ied.h dev_snpp.h dev_tnpp.h tbl_dv_tnpp.h \
		dev_pagingreceiver.h tbl_dv_pagingreceiver.h dev_wctp.h \
		prot_versacom.h expresscom.h ctistring.h
portglob.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h das08.h tcpsup.h ctinexus.h \
		portglob.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h sorted_vector.h statistics.h counter.h ctidate.h
portgui.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dllbase.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h master.h ilex.h perform.h \
		thread_monitor.h smartmap.h hashkey.h hash_functions.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		sorted_vector.h cparms.h rwutil.h boost_time.h configkey.h \
		configval.h queue.h thread_register_data.h portglob.h \
		tcpsup.h ctinexus.h statistics.h counter.h ctidate.h color.h \
		portgui.h
portgw.obj:	yukon.h precompiled.h ctidbgmem.h connection.h dlldefs.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h dllyukon.h \
		mgr_device.h rtdb.h hashkey.h hash_functions.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h slctdev.h smartmap.h dev_gateway.h \
		ctitypes.h dev_gwstat.h dev_ied.h dev_remote.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h gateway.h pending_stat_operation.h \
		dev_grp_energypro.h dev_grp.h msg_lmcontrolhistory.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h tbl_dv_expresscom.h vcomdefs.h mgr_port.h \
		port_base.h tbl_port_base.h critical_section.h \
		tbl_port_statistics.h slctprt.h mgr_route.h repeaterrole.h \
		portdecl.h dsm2err.h porter.h devicetypes.h portglob.h \
		tcpsup.h statistics.h ctidate.h thread_monitor.h \
		thread_register_data.h
portload.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h portglob.h tcpsup.h \
		ctinexus.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h sorted_vector.h statistics.h counter.h ctidate.h \
		portdecl.h rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h dllbase.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h dialup.h critical_section.h \
		tbl_port_statistics.h c_port_interface.h group.h elogger.h \
		alarmlog.h mgr_port.h smartmap.h hashkey.h hash_functions.h \
		slctprt.h mgr_device.h rtdb.h slctdev.h mgr_route.h \
		repeaterrole.h rte_ccu.h rte_xcu.h msg_pcrequest.h \
		tbl_rtcarrier.h tbl_rtrepeater.h trx_711.h trx_info.h \
		dev_ccu.h ctitypes.h dev_idlc.h dev_remote.h dev_single.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h mgr_point.h \
		slctpnt.h device_queue_interface.h
portmain.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		portsvc.h cservice.h CServiceConfig.h ctibase.h ctinexus.h \
		netports.h cticonnect.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		portglob.h tcpsup.h porter.h dsm2err.h devicetypes.h queues.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h sorted_vector.h \
		statistics.h counter.h ctidate.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h configparms.h
portperf.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cparms.h rwutil.h ctitime.h boost_time.h \
		configkey.h configval.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h elogger.h portglob.h tcpsup.h \
		ctinexus.h porter.h queues.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h statistics.h \
		counter.h ctidate.h portdecl.h rte_base.h dbmemobject.h \
		cmdparse.h ctitokenizer.h parsevalue.h ctibase.h dllbase.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h dialup.h critical_section.h \
		tbl_port_statistics.h mgr_port.h smartmap.h hashkey.h \
		hash_functions.h slctprt.h mgr_device.h rtdb.h slctdev.h \
		thread_monitor.h queue.h thread_register_data.h
portpil.obj:	yukon.h precompiled.h ctidbgmem.h mgr_device.h dlldefs.h \
		rtdb.h hashkey.h hash_functions.h utility.h ctitime.h \
		queues.h types.h numstr.h sorted_vector.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h \
		config_base.h config_resolvers.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h message.h collectable.h rwutil.h \
		boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h slctdev.h smartmap.h \
		mgr_route.h repeaterrole.h pilserver.h server_b.h con_mgr.h \
		connection.h exchange.h msg_multi.h msg_pdata.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h cmdopts.h \
		argkey.h argval.h critical_Section.h msg_pcrequest.h \
		mgr_config.h dllyukon.h portglob.h tcpsup.h porter.h \
		dsm2err.h devicetypes.h statistics.h ctidate.h \
		thread_monitor.h thread_register_data.h
portpool.obj:	yukon.h precompiled.h ctidbgmem.h connection.h dlldefs.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_device.h rtdb.h hashkey.h hash_functions.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h slctdev.h smartmap.h mgr_port.h port_base.h \
		tbl_port_base.h xfer.h dialup.h critical_section.h \
		tbl_port_statistics.h slctprt.h port_pool_out.h portdecl.h \
		dsm2err.h porter.h devicetypes.h portglob.h tcpsup.h \
		statistics.h ctidate.h
portque.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cparms.h rwutil.h ctitime.h boost_time.h \
		configkey.h configval.h queues.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h elogger.h thread_monitor.h \
		smartmap.h dllbase.h hashkey.h hash_functions.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h sorted_vector.h queue.h \
		thread_register_data.h portglob.h tcpsup.h ctinexus.h \
		statistics.h counter.h ctidate.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctibase.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h dialup.h critical_section.h \
		tbl_port_statistics.h c_port_interface.h group.h alarmlog.h \
		mgr_device.h rtdb.h slctdev.h mgr_port.h slctprt.h port_shr.h \
		trx_711.h trx_info.h dev_ccu.h ctitypes.h dev_idlc.h \
		dev_remote.h dev_single.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h prot_base.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h mgr_point.h \
		slctpnt.h device_queue_interface.h prot_emetcon.h
portsvc.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		portsvc.h cservice.h ctibase.h ctinexus.h netports.h \
		cticonnect.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h portglob.h tcpsup.h \
		porter.h dsm2err.h devicetypes.h queues.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h statistics.h \
		counter.h ctidate.h
porttime.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctibase.h ctinexus.h dllbase.h message.h collectable.h \
		rwutil.h ctitime.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		sorted_vector.h msg_signal.h port_base.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h dialup.h critical_section.h \
		tbl_port_statistics.h ilex.h elogger.h portglob.h tcpsup.h \
		statistics.h ctidate.h c_port_interface.h group.h alarmlog.h \
		mgr_port.h smartmap.h hashkey.h hash_functions.h slctprt.h \
		mgr_device.h rtdb.h slctdev.h dev_ccu.h ctitypes.h dev_idlc.h \
		dev_remote.h dev_single.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		mgr_point.h slctpnt.h device_queue_interface.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h config_parts.h \
		mgr_route.h repeaterrole.h thread_monitor.h \
		thread_register_data.h prot_welco.h prot_lmi.h prot_seriesv.h \
		verification_objects.h
portverify.obj:	yukon.h precompiled.h ctidbgmem.h boost_time.h \
		mgr_port.h dlldefs.h smartmap.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h hashkey.h hash_functions.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h port_base.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h config_base.h \
		config_resolvers.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h tbl_port_base.h xfer.h dialup.h \
		critical_section.h tbl_port_statistics.h slctprt.h cparms.h \
		configkey.h configval.h portverify.h queue.h \
		verification_objects.h ctidate.h
port_shr.obj:	yukon.h precompiled.h ctidbgmem.h ctinexus.h dlldefs.h \
		netports.h cticonnect.h types.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h port_shr.h thread.h logger.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		port_base.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		dllbase.h os2_2w32.h cticalls.h config_base.h \
		config_resolvers.h rte_base.h dbmemobject.h ctibase.h \
		message.h collectable.h rwutil.h boost_time.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h tbl_port_base.h xfer.h dialup.h \
		critical_section.h tbl_port_statistics.h tcpsup.h cparms.h \
		configkey.h configval.h
port_shr_ip.obj:	yukon.h precompiled.h ctidbgmem.h types.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h cticalls.h os2_2w32.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		port_shr_ip.h ctinexus.h port_shr.h port_base.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h dllbase.h \
		config_base.h config_resolvers.h rte_base.h dbmemobject.h \
		ctibase.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		prot_welco.h porter.h dsm2err.h devicetypes.h tcpsup.h \
		cti_asmc.h
port_udp.obj:	yukon.h precompiled.h ctidbgmem.h portglob.h dlldefs.h \
		types.h tcpsup.h ctinexus.h netports.h cticonnect.h porter.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h dsm2err.h \
		devicetypes.h queues.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h statistics.h \
		counter.h ctidate.h mgr_device.h rtdb.h hashkey.h \
		hash_functions.h dllbase.h os2_2w32.h cticalls.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h config_base.h \
		config_resolvers.h rte_base.h dbmemobject.h ctibase.h \
		message.h collectable.h rwutil.h boost_time.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h slctdev.h smartmap.h dev_dnp.h dev_remote.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h prot_dnp.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h msg_trace.h \
		msg_dbchg.h port_udp.h port_base.h tbl_port_base.h \
		critical_section.h tbl_port_statistics.h portdecl.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
ptprint.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		os2_2w32.h types.h cticalls.h queues.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h color.h portglob.h tcpsup.h \
		ctinexus.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		sorted_vector.h statistics.h counter.h ctidate.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h dllbase.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h \
		tbl_port_base.h xfer.h dialup.h critical_section.h \
		tbl_port_statistics.h
ripple.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h connection.h exchange.h dllbase.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h port_base.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h config_resolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		master.h lm_auto.h perform.h scanner.h elogger.h drp.h \
		portglob.h tcpsup.h statistics.h ctidate.h c_port_interface.h \
		group.h alarmlog.h dev_lcu.h ctitypes.h dev_idlc.h \
		dev_remote.h dev_single.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		mgr_point.h smartmap.h hashkey.h hash_functions.h slctpnt.h \
		mgr_device.h rtdb.h slctdev.h mgr_port.h slctprt.h
systemmsgthread.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h counter.h guard.h \
		numstr.h clrdump.h mutex.h cparms.h rwutil.h ctitime.h \
		boost_time.h configkey.h configval.h device_queue_interface.h \
		trx_711.h trx_info.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h types.h sorted_vector.h dsm2.h \
		cticonnect.h netports.h porter.h dsm2err.h devicetypes.h \
		msg_cmd.h message.h collectable.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_queuedata.h msg_requestcancel.h systemmsgthread.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		msg_ptreg.h msg_reg.h queue.h mgr_device.h rtdb.h hashkey.h \
		hash_functions.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h config_base.h \
		config_resolvers.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h slctdev.h smartmap.h mgr_port.h port_base.h \
		tbl_port_base.h xfer.h dialup.h critical_section.h \
		tbl_port_statistics.h slctprt.h
traceset.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h
#ENDUPDATE#
