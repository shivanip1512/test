# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLUDE=%INCLUDE%;\$(INCPATHADDITIONS)

INCLPATHS+= \
-I$(PORTER)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(SCANNER)\include \
-I$(SERVICE)\include \
-I$(PIL)\include \
-I$(SERVER)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
-I$(PROT)\include \
-I$(BOOST) \
-I$(RW) \
-I$(XERCESINC) \


.PATH.cpp = .

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
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST) \
;$(RW)


BASEOBJS=\
disp_thd.obj \
perform3.obj \
phlidlc.obj \
plidlc.obj \
portconf.obj \
portcont.obj \
portdialback.obj \
portentry.obj \
porter.obj \
portersu.obj \
portfield.obj \
portfill.obj \
portgui.obj \
portgw.obj \
portload.obj \
portmain.obj \
portperf.obj \
portpool.obj \
portpil.obj \
portque.obj \
portsvc.obj \
porttime.obj \
portverify.obj \
ptprint.obj \
ripple.obj \

#fisherp.obj \
#dialup.obj \
#versacom.obj \
#tapterm.obj \
#dupaplus.obj \
#dupschl.obj \
#pport.obj \
#portport.obj \
#portpipe.obj \
#porttcp.obj \
#tdmarkv.obj \


PORTERLIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\tcpsup.lib \
$(COMPILEBASE)\lib\portglob.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctipil.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctiprtdb.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(COMPILEBASE)\lib\cticparms.lib \

EXECS=\
porter.exe \
traceset.exe \
contest.exe


ALL:            $(EXECS)

porter.exe:     $(BASEOBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(BASEOBJS) id_porter.obj $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ -link $(LIBS) $(RWLIBS) $(PORTERLIBS) $(BOOSTLIBS)
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

traceset.exe:   traceset.obj
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
traceset.obj -link $(LIBS) $(COMPILEBASE)\lib\portglob.lib
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

contest.exe:    contest.obj
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
contest.obj -link $(LIBS) $(RWLIBS) $(BOOSTLIBS) $(COMPILEBASE)\lib\ctibase.lib
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

copy:           $(EXECS)
               -if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


clean:
                -del *.obj
                -del *.dll
                -del *.ilk
                -del *.pdb
                -del *.lib
                -del *.exp

.cpp.obj:
                @echo:
                @echo Compiling: $<
                @echo C-Options: $(CFLAGS)
                @echo Output   : $@
                @echo:
                $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

deps:
                scandeps -Output makeexe.mak *.cpp



# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_porter.obj

id_porter.obj:    id_porter.cpp include\id_porter.h id_vinfo.h


#UPDATE#
contest.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h master.h ilex.h \
		perform.h portglob.h tcpsup.h ctinexus.h logger.h thread.h \
		statistics.h counter.h color.h
disp_thd.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dsm2.h mutex.h guard.h ctinexus.h porter.h \
		dsm2err.h devicetypes.h queues.h cparms.h netports.h queent.h \
		pil_conmgr.h exchange.h dllbase.h logger.h thread.h message.h \
		collectable.h con_mgr.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		ctibase.h pil_exefct.h executorfactory.h executor.h exe_cmd.h \
		exe_reg.h pilserver.h server_b.h cmdopts.h critical_Section.h \
		msg_pcrequest.h mgr_device.h rtdb.h hashkey.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h utility.h slctdev.h smartmap.h \
		mgr_route.h repeaterrole.h msg_pcreturn.h msg_dbchg.h \
		msg_cmd.h mgr_port.h port_base.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h slctprt.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h numstr.h portglob.h tcpsup.h \
		statistics.h
dllmain.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dsm2.h mutex.h guard.h portglob.h tcpsup.h \
		ctinexus.h porter.h dsm2err.h devicetypes.h queues.h logger.h \
		thread.h statistics.h counter.h utility.h
id_pgdll.obj:	yukon.h precompiled.h ctidbgmem.h id_pgdll.h utility.h \
		dsm2.h mutex.h dlldefs.h guard.h id_build.h id_vinfo.h
id_porter.obj:	yukon.h precompiled.h ctidbgmem.h id_porter.h utility.h \
		dsm2.h mutex.h dlldefs.h guard.h id_build.h id_vinfo.h
perform3.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h drp.h elogger.h alarmlog.h \
		porter.h portglob.h tcpsup.h ctinexus.h logger.h thread.h \
		statistics.h counter.h c_port_interface.h group.h perform.h
phlidlc.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h parsevalue.h ctibase.h \
		ctinexus.h dllbase.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h port_base.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		tbl_port_base.h xfer.h dialup.h tbl_port_statistics.h \
		portglob.h tcpsup.h statistics.h mgr_port.h smartmap.h \
		hashkey.h slctprt.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h trx_711.h trx_info.h
plidlc.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cti_asmc.h queues.h dsm2.h mutex.h guard.h \
		dsm2err.h device.h devicetypes.h routes.h porter.h portglob.h \
		tcpsup.h ctinexus.h logger.h thread.h statistics.h counter.h \
		dev_base.h cmdparse.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		dllbase.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		utility.h trx_info.h
portconf.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cparms.h queues.h dsm2.h mutex.h guard.h \
		dsm2err.h device.h devicetypes.h routes.h porter.h master.h \
		group.h elogger.h portglob.h tcpsup.h ctinexus.h logger.h \
		thread.h statistics.h counter.h c_port_interface.h alarmlog.h \
		dev_base.h cmdparse.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		dllbase.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		utility.h portdecl.h port_base.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h hashkey.h mgr_device.h rtdb.h \
		slctdev.h smartmap.h mgr_route.h repeaterrole.h
portcont.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h lm_auto.h perform.h \
		scanner.h dllbase.h ilex.h master.h elogger.h portglob.h \
		tcpsup.h ctinexus.h logger.h thread.h statistics.h counter.h \
		c_port_interface.h group.h alarmlog.h hashkey.h mgr_port.h \
		smartmap.h port_base.h dev_base.h cmdparse.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h slctprt.h pt_base.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h
portdialback.obj:	yukon.h precompiled.h ctidbgmem.h cticalls.h \
		os2_2w32.h dlldefs.h types.h ctitypes.h cparms.h dsm2.h \
		mutex.h guard.h connection.h exchange.h dllbase.h logger.h \
		thread.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		mgr_device.h rtdb.h hashkey.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h slctdev.h \
		smartmap.h mgr_port.h port_base.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h slctprt.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h msg_cmd.h pilserver.h server_b.h \
		con_mgr.h cmdopts.h critical_Section.h msg_pcrequest.h \
		mgr_route.h repeaterrole.h portglob.h tcpsup.h porter.h \
		dsm2err.h devicetypes.h statistics.h
portentry.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cparms.h connection.h exchange.h dllbase.h \
		dsm2.h mutex.h guard.h logger.h thread.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h queues.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h parsevalue.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		port_base.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h master.h ilex.h perform.h portglob.h \
		tcpsup.h statistics.h color.h c_port_interface.h group.h \
		elogger.h alarmlog.h mgr_port.h smartmap.h hashkey.h \
		slctprt.h pt_base.h pt_dyn_base.h tbl_pt_base.h mgr_device.h \
		rtdb.h slctdev.h dev_lcu.h ctitypes.h dev_idlc.h dev_remote.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h mgr_point.h \
		slctpnt.h prot_emetcon.h
porter.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h color.h cparms.h queues.h dsm2.h mutex.h \
		guard.h dsm2err.h device.h devicetypes.h routes.h porter.h \
		portdecl.h rte_base.h dbmemobject.h cmdparse.h parsevalue.h \
		ctibase.h ctinexus.h dllbase.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		port_base.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h portverify.h queue.h \
		verification_objects.h boost_time.h master.h elogger.h \
		alarmlog.h drp.h perform.h das08.h portgui.h numstr.h \
		portglob.h tcpsup.h statistics.h c_port_interface.h group.h \
		mgr_port.h smartmap.h hashkey.h slctprt.h pt_base.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h mgr_device.h rtdb.h \
		slctdev.h mgr_route.h repeaterrole.h port_shr.h port_shr_ip.h \
		dlldev.h msg_dbchg.h msg_trace.h eventlog.h configparms.h \
		trx_711.h trx_info.h dllyukon.h pilserver.h server_b.h \
		con_mgr.h connection.h exchange.h msg_multi.h msg_pdata.h \
		msg_signal.h msg_ptreg.h msg_reg.h cmdopts.h \
		critical_Section.h msg_pcrequest.h
portersu.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h color.h connection.h exchange.h dllbase.h \
		dsm2.h mutex.h guard.h logger.h thread.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h queues.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h master.h elogger.h \
		alarmlog.h drp.h perform.h das08.h portglob.h tcpsup.h \
		ctinexus.h statistics.h counter.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h parsevalue.h ctibase.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h utility.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h c_port_interface.h group.h \
		rtdb.h hashkey.h mgr_port.h smartmap.h slctprt.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h mgr_device.h slctdev.h \
		msg_commerrorhistory.h numstr.h
portfield.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cti_asmc.h cparms.h color.h queues.h \
		dsm2.h mutex.h guard.h dsm2err.h device.h devicetypes.h \
		dev_lcu.h ctitypes.h dev_idlc.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h porter.h trx_711.h mgr_point.h \
		rtdb.h hashkey.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		slctpnt.h dev_tap.h tbl_dv_tappaging.h dev_ied.h tbl_dv_ied.h \
		dev_rtc.h tbl_dv_rtc.h dev_rtm.h verification_objects.h \
		boost_time.h dev_wctp.h routes.h master.h portdecl.h \
		port_base.h tbl_port_base.h tbl_port_statistics.h tcpsup.h \
		perform.h tapterm.h porttcp.h portglob.h statistics.h \
		prot_sa3rdparty.h protocol_sa.h portverify.h \
		c_port_interface.h group.h elogger.h alarmlog.h mgr_port.h \
		smartmap.h slctprt.h mgr_device.h slctdev.h dev_cbc6510.h \
		dev_dnp.h prot_dnp.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h dev_schlum.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dev_kv2.h \
		prot_ansi_kv2.h prot_ansi.h ansi_application.h \
		ansi_datalink.h ansi_billing_table.h std_ansi_tbl_base.h \
		std_ansi_tbl_zero_zero.h std_ansi_tbl_zero_one.h \
		std_ansi_tbl_zero_eight.h std_ansi_tbl_one_zero.h \
		std_ansi_tbl_one_one.h std_ansi_tbl_one_two.h \
		std_ansi_tbl_one_three.h std_ansi_tbl_one_four.h \
		std_ansi_tbl_one_five.h std_ansi_tbl_one_six.h \
		std_ansi_tbl_two_one.h std_ansi_tbl_two_two.h \
		std_ansi_tbl_two_three.h std_ansi_tbl_two_seven.h \
		std_ansi_tbl_two_eight.h std_ansi_tbl_five_one.h \
		std_ansi_tbl_five_two.h std_ansi_tbl_six_one.h \
		std_ansi_tbl_six_two.h std_ansi_tbl_six_three.h \
		std_ansi_tbl_six_four.h ansi_kv2_mtable_zero.h \
		ansi_kv2_mtable_seventy.h ansi_kv2_mtable_onehundredten.h \
		dev_sentinel.h prot_ansi_sentinel.h dev_mark_v.h \
		prot_transdata.h transdata_application.h transdata_tracker.h \
		numstr.h transdata_datalink.h prot_ymodem.h transdata_data.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		msg_cmd.h msg_trace.h pilserver.h server_b.h con_mgr.h \
		cmdopts.h critical_Section.h mgr_route.h repeaterrole.h \
		prot_711.h
portfill.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cparms.h queues.h dsm2.h mutex.h guard.h \
		dsm2err.h device.h devicetypes.h routes.h porter.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h parsevalue.h ctibase.h \
		ctinexus.h dllbase.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h port_base.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		tbl_port_base.h xfer.h dialup.h tbl_port_statistics.h \
		master.h portglob.h tcpsup.h statistics.h c_port_interface.h \
		group.h elogger.h alarmlog.h rtdb.h hashkey.h mgr_device.h \
		slctdev.h smartmap.h mgr_port.h slctprt.h pt_base.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h mgr_route.h \
		repeaterrole.h dev_tcu.h ctitypes.h dev_idlc.h dev_remote.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h \
		trx_info.h trx_711.h mgr_point.h slctpnt.h dev_tap.h \
		tbl_dv_tappaging.h dev_ied.h tbl_dv_ied.h dev_wctp.h \
		prot_versacom.h expresscom.h
portglob.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h das08.h tcpsup.h \
		ctinexus.h portglob.h logger.h thread.h statistics.h \
		counter.h
portgui.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h master.h ilex.h \
		perform.h portglob.h tcpsup.h ctinexus.h logger.h thread.h \
		statistics.h counter.h color.h portgui.h
portgw.obj:	yukon.h precompiled.h ctidbgmem.h connection.h dlldefs.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h dllyukon.h mgr_device.h rtdb.h \
		hashkey.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h slctdev.h smartmap.h dev_gateway.h ctitypes.h \
		dev_gwstat.h dev_ied.h dev_remote.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h gateway.h pending_stat_operation.h \
		dev_grp_energypro.h dev_grp.h msg_lmcontrolhistory.h \
		pt_status.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		tbl_dv_expresscom.h vcomdefs.h mgr_port.h port_base.h \
		tbl_port_base.h tbl_port_statistics.h slctprt.h mgr_route.h \
		repeaterrole.h numstr.h portdecl.h dsm2err.h porter.h \
		devicetypes.h portglob.h tcpsup.h statistics.h
portload.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h portglob.h tcpsup.h \
		ctinexus.h logger.h thread.h statistics.h counter.h \
		portdecl.h rte_base.h dbmemobject.h cmdparse.h parsevalue.h \
		ctibase.h dllbase.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h utility.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h c_port_interface.h group.h \
		elogger.h alarmlog.h mgr_port.h smartmap.h hashkey.h \
		slctprt.h pt_base.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		mgr_device.h rtdb.h slctdev.h mgr_route.h repeaterrole.h \
		rte_ccu.h rte_xcu.h msg_pcrequest.h tbl_rtcarrier.h \
		tbl_rtrepeater.h trx_711.h trx_info.h dev_ccu.h ctitypes.h \
		dev_idlc.h dev_remote.h dev_single.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h mgr_point.h slctpnt.h
portmain.obj:	yukon.h precompiled.h ctidbgmem.h portsvc.h cservice.h \
		dlldefs.h CServiceConfig.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		portglob.h tcpsup.h porter.h dsm2err.h devicetypes.h queues.h \
		logger.h thread.h statistics.h counter.h cparms.h \
		configparms.h
portperf.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cparms.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h elogger.h portglob.h tcpsup.h \
		ctinexus.h porter.h queues.h logger.h thread.h statistics.h \
		counter.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		parsevalue.h ctibase.h dllbase.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h port_base.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		tbl_port_base.h xfer.h dialup.h tbl_port_statistics.h \
		mgr_port.h smartmap.h hashkey.h slctprt.h pt_base.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h mgr_device.h rtdb.h \
		slctdev.h
portpil.obj:	yukon.h precompiled.h ctidbgmem.h mgr_device.h dlldefs.h \
		rtdb.h hashkey.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h slctdev.h smartmap.h mgr_route.h repeaterrole.h \
		pilserver.h server_b.h con_mgr.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h cmdopts.h critical_Section.h \
		msg_pcrequest.h dllyukon.h portglob.h tcpsup.h porter.h \
		dsm2err.h devicetypes.h statistics.h
portpool.obj:	yukon.h precompiled.h ctidbgmem.h connection.h dlldefs.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h mgr_device.h rtdb.h hashkey.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h slctdev.h smartmap.h mgr_port.h port_base.h \
		tbl_port_base.h xfer.h dialup.h tbl_port_statistics.h \
		slctprt.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		port_pool_out.h portdecl.h dsm2err.h porter.h devicetypes.h \
		portglob.h tcpsup.h statistics.h
portque.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h elogger.h portglob.h \
		tcpsup.h ctinexus.h logger.h thread.h statistics.h counter.h \
		portdecl.h rte_base.h dbmemobject.h cmdparse.h parsevalue.h \
		ctibase.h dllbase.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h utility.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h c_port_interface.h group.h \
		alarmlog.h mgr_device.h rtdb.h hashkey.h slctdev.h smartmap.h \
		mgr_port.h slctprt.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h trx_711.h trx_info.h dev_ccu.h ctitypes.h \
		dev_idlc.h dev_remote.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h msg_signal.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h prot_base.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h mgr_point.h slctpnt.h
portsvc.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h portsvc.h \
		cservice.h ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h portglob.h tcpsup.h \
		porter.h dsm2err.h devicetypes.h queues.h logger.h thread.h \
		statistics.h counter.h
porttime.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h parsevalue.h ctibase.h \
		ctinexus.h dllbase.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h port_base.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		tbl_port_base.h xfer.h dialup.h tbl_port_statistics.h ilex.h \
		elogger.h portglob.h tcpsup.h statistics.h c_port_interface.h \
		group.h alarmlog.h mgr_port.h smartmap.h hashkey.h slctprt.h \
		pt_base.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		mgr_device.h rtdb.h slctdev.h dev_ccu.h ctitypes.h dev_idlc.h \
		dev_remote.h dev_single.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h mgr_point.h \
		slctpnt.h dev_mct410.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h mgr_route.h \
		repeaterrole.h prot_welco.h
portverify.obj:	yukon.h precompiled.h ctidbgmem.h boost_time.h \
		mgr_port.h dlldefs.h smartmap.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h hashkey.h logger.h thread.h \
		port_base.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h tbl_port_base.h \
		xfer.h dialup.h tbl_port_statistics.h slctprt.h pt_base.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h rwutil.h cparms.h \
		portverify.h queue.h verification_objects.h
port_shr.obj:	yukon.h precompiled.h ctidbgmem.h ctinexus.h dlldefs.h \
		types.h dsm2.h mutex.h guard.h port_shr.h thread.h logger.h \
		port_base.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h dllbase.h os2_2w32.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h tcpsup.h
port_shr_ip.obj:	yukon.h precompiled.h ctidbgmem.h types.h cticalls.h \
		os2_2w32.h dlldefs.h dsm2.h mutex.h guard.h logger.h thread.h \
		port_shr_ip.h ctinexus.h port_shr.h port_base.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		dllbase.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		queues.h utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h prot_welco.h porter.h dsm2err.h \
		devicetypes.h tcpsup.h numstr.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
ptprint.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h color.h portglob.h \
		tcpsup.h ctinexus.h logger.h thread.h statistics.h counter.h \
		portdecl.h rte_base.h dbmemobject.h cmdparse.h parsevalue.h \
		ctibase.h dllbase.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h utility.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h
ripple.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h connection.h exchange.h dllbase.h dsm2.h \
		mutex.h guard.h logger.h thread.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h queues.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h parsevalue.h ctibase.h ctinexus.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h port_base.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		tbl_port_base.h xfer.h dialup.h tbl_port_statistics.h \
		master.h lm_auto.h perform.h scanner.h elogger.h mpc.h drp.h \
		portglob.h tcpsup.h statistics.h c_port_interface.h group.h \
		alarmlog.h dev_lcu.h ctitypes.h dev_idlc.h dev_remote.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h mgr_point.h rtdb.h \
		hashkey.h pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h \
		mgr_device.h slctdev.h smartmap.h mgr_port.h slctprt.h
traceset.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h
#ENDUPDATE#
