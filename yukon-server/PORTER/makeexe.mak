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
-I$(RW) \
-I$(PROT)\include \


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
;$(RW)


BASEOBJS=\
disp_thd.obj \
perform3.obj \
phlidlc.obj \
plidlc.obj \
portconf.obj \
portcont.obj \
portentry.obj \
porter.obj \
portersu.obj \
portfield.obj \
portfill.obj \
portgui.obj \
portload.obj \
portmain.obj \
portperf.obj \
portpil.obj \
portque.obj \
portsvc.obj \
porttime.obj \
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
                $(RWCPPINVOKE) $(CFLAGS) $(BASEOBJS) id_porter.obj $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ -link $(LIBS) $(RWLIBS) $(PORTERLIBS)
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
contest.obj -link $(LIBS) $(RWLIBS) $(COMPILEBASE)\lib\ctibase.lib
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
                $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

deps:
                scandeps -Output makeexe.mak *.cpp



# The lines below accomplish the ID'ing of the project!
id:
            @cid .\include\id_porter.h
            @$(MAKE) -nologo -f $(_InputFile) id_porter.obj

id_porter.obj:    id_porter.cpp include\id_porter.h


#UPDATE#
contest.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h master.h ilex.h perform.h portglob.h tcpsup.h \
		ctinexus.h logger.h thread.h color.h
dialup.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h master.h portsup.h tcpsup.h ctinexus.h perform.h \
		portglob.h logger.h thread.h dialup.h dupreq.h port_base.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		pointtypes.h yukon.h dllbase.h db_entry_defines.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h
disp_thd.obj:	os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h ctinexus.h porter.h dsm2err.h devicetypes.h queues.h \
		cparms.h netports.h queent.h pil_conmgr.h exchange.h \
		dllbase.h message.h collectable.h con_mgr.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h pil_exefct.h \
		executorfactory.h executor.h exe_cmd.h exe_reg.h pilserver.h \
		server_b.h cmdopts.h msg_pcrequest.h mgr_device.h rtdb.h \
		hashkey.h dev_base.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h slctdev.h rte_base.h tbl_rtcomm.h \
		mgr_route.h msg_pcreturn.h msg_dbchg.h msg_cmd.h portglob.h \
		tcpsup.h dll_msg.h
dllmain.obj:	os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h portglob.h tcpsup.h ctinexus.h porter.h dsm2err.h \
		devicetypes.h queues.h logger.h thread.h utility.h
dupaplus.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h master.h portsup.h tcpsup.h ctinexus.h perform.h \
		portglob.h logger.h thread.h dialup.h dupaplus.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h pointtypes.h \
		yukon.h dllbase.h db_entry_defines.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h
dupschl.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h master.h portsup.h tcpsup.h ctinexus.h perform.h \
		portglob.h logger.h thread.h dialup.h dupreq.h dupschl.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		pointtypes.h yukon.h dllbase.h db_entry_defines.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h
fisherp.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h master.h portglob.h tcpsup.h ctinexus.h logger.h \
		thread.h mgr_device.h rtdb.h hashkey.h dllbase.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h pointtypes.h \
		yukon.h db_entry_defines.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		slctdev.h rte_base.h ctibase.h message.h collectable.h \
		tbl_rtcomm.h mgr_port.h port_base.h tbl_port_base.h \
		porttypes.h tbl_port_settings.h tbl_port_statistics.h \
		tbl_port_timing.h xfer.h dialup.h slctprt.h pt_base.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h
id_pgdll.obj:	id_pgdll.h utility.h dlldefs.h dsm2.h mutex.h guard.h \
		id_vinfo.h
id_porter.obj:	id_porter.h utility.h dlldefs.h dsm2.h mutex.h guard.h \
		id_vinfo.h
perform2.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		drp.h elogger.h alarmlog.h porter.h portglob.h tcpsup.h \
		ctinexus.h logger.h thread.h c_port_interface.h group.h \
		perform.h
perform3.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		drp.h elogger.h alarmlog.h porter.h portglob.h tcpsup.h \
		ctinexus.h logger.h thread.h c_port_interface.h group.h \
		perform.h
phlidlc.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		parsevalue.h ctibase.h ctinexus.h dllbase.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h desolvers.h logger.h \
		thread.h portglob.h tcpsup.h port_base.h dev_base.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		mgr_port.h rtdb.h hashkey.h slctprt.h pt_base.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h trx_711.h trx_info.h
plidlc.obj:	os2_2w32.h dlldefs.h types.h cticalls.h cti_asmc.h \
		queues.h dsm2.h mutex.h guard.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h portglob.h tcpsup.h \
		ctinexus.h logger.h thread.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h resolvers.h pointtypes.h yukon.h \
		dllbase.h db_entry_defines.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		trx_info.h
portconf.obj:	os2_2w32.h dlldefs.h types.h cticalls.h cparms.h \
		queues.h dsm2.h mutex.h guard.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h master.h group.h elogger.h \
		portglob.h tcpsup.h ctinexus.h logger.h thread.h \
		c_port_interface.h alarmlog.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h resolvers.h pointtypes.h yukon.h \
		dllbase.h db_entry_defines.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		portdecl.h rte_base.h ctibase.h message.h collectable.h \
		tbl_rtcomm.h hashkey.h mgr_device.h rtdb.h slctdev.h \
		mgr_route.h
portcont.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h lm_auto.h perform.h scanner.h dllbase.h ilex.h \
		master.h elogger.h portglob.h tcpsup.h ctinexus.h logger.h \
		thread.h c_port_interface.h group.h alarmlog.h hashkey.h \
		mgr_port.h rtdb.h port_base.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h resolvers.h pointtypes.h yukon.h \
		db_entry_defines.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		slctprt.h rte_base.h ctibase.h message.h collectable.h \
		tbl_rtcomm.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h
portentry.obj:	os2_2w32.h dlldefs.h types.h cticalls.h connection.h \
		exchange.h dllbase.h dsm2.h mutex.h guard.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h queues.h \
		dsm2err.h device.h devicetypes.h routes.h porter.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h parsevalue.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		master.h ilex.h perform.h portglob.h tcpsup.h color.h \
		c_port_interface.h group.h elogger.h alarmlog.h mgr_port.h \
		rtdb.h hashkey.h port_base.h dev_base.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h slctprt.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h mgr_device.h slctdev.h dev_lcu.h ctitypes.h \
		dev_idlc.h dev_remote.h portsup.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h mgr_point.h slctpnt.h prot_emetcon.h utility.h
porter.obj:	os2_2w32.h dlldefs.h types.h cticalls.h color.h cparms.h \
		queues.h dsm2.h mutex.h guard.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h parsevalue.h ctibase.h ctinexus.h \
		dllbase.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h yukon.h \
		db_entry_defines.h desolvers.h logger.h thread.h master.h \
		elogger.h alarmlog.h drp.h perform.h das08.h portgui.h \
		portglob.h tcpsup.h c_port_interface.h group.h mgr_port.h \
		rtdb.h hashkey.h port_base.h dev_base.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h slctprt.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h mgr_device.h slctdev.h mgr_route.h port_shr.h \
		port_shr_ip.h dlldev.h msg_dbchg.h msg_trace.h dll_msg.h \
		connection.h exchange.h msg_multi.h msg_pdata.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h eventlog.h configparms.h \
		trx_711.h trx_info.h utility.h
portersu.obj:	os2_2w32.h dlldefs.h types.h cticalls.h color.h queues.h \
		dsm2.h mutex.h guard.h dsm2err.h device.h devicetypes.h \
		routes.h porter.h master.h elogger.h alarmlog.h drp.h \
		perform.h das08.h portglob.h tcpsup.h ctinexus.h logger.h \
		thread.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		parsevalue.h ctibase.h dllbase.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		yukon.h db_entry_defines.h desolvers.h c_port_interface.h \
		group.h rtdb.h hashkey.h dev_base.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h port_base.h tbl_port_base.h \
		porttypes.h tbl_port_settings.h tbl_port_statistics.h \
		tbl_port_timing.h xfer.h dialup.h mgr_port.h slctprt.h \
		pt_base.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		mgr_device.h slctdev.h utility.h
portfield.obj:	os2_2w32.h dlldefs.h types.h cticalls.h cti_asmc.h \
		color.h queues.h dsm2.h mutex.h guard.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h master.h portsup.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h parsevalue.h ctibase.h \
		ctinexus.h dllbase.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h yukon.h \
		db_entry_defines.h desolvers.h logger.h thread.h tcpsup.h \
		perform.h tapterm.h porttcp.h portglob.h c_port_interface.h \
		group.h elogger.h alarmlog.h mgr_port.h rtdb.h hashkey.h \
		port_base.h dev_base.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h slctprt.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h mgr_device.h slctdev.h dev_ied.h ctitypes.h \
		dev_remote.h dev_single.h connection.h exchange.h msg_multi.h \
		msg_pdata.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_schlum.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h mgr_point.h slctpnt.h \
		msg_trace.h port_local_modem.h port_modem.h port_direct.h \
		tbl_port_serial.h tbl_port_dialup.h prot_711.h trx_info.h \
		trx_711.h utility.h
portfill.obj:	os2_2w32.h dlldefs.h types.h cticalls.h cparms.h \
		queues.h dsm2.h mutex.h guard.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h portdecl.h rte_base.h \
		dbmemobject.h cmdparse.h parsevalue.h ctibase.h ctinexus.h \
		dllbase.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h yukon.h \
		db_entry_defines.h desolvers.h logger.h thread.h master.h \
		portglob.h tcpsup.h c_port_interface.h group.h elogger.h \
		alarmlog.h port_base.h dev_base.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h rtdb.h hashkey.h mgr_port.h slctprt.h \
		pt_base.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		mgr_device.h slctdev.h dev_tcu.h ctitypes.h dev_idlc.h \
		dev_remote.h portsup.h dev_single.h connection.h exchange.h \
		msg_multi.h msg_pdata.h msg_signal.h msg_ptreg.h msg_reg.h \
		queue.h msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h \
		trx_info.h trx_711.h mgr_point.h slctpnt.h dev_tap.h \
		tbl_dv_tappaging.h dev_ied.h tbl_dv_ied.h prot_versacom.h
portglob.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h das08.h tcpsup.h ctinexus.h portglob.h logger.h \
		thread.h
portgui.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h master.h ilex.h perform.h portglob.h tcpsup.h \
		ctinexus.h logger.h thread.h color.h portgui.h
portload.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h portglob.h tcpsup.h ctinexus.h logger.h thread.h \
		portdecl.h rte_base.h dbmemobject.h cmdparse.h parsevalue.h \
		ctibase.h dllbase.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h yukon.h \
		db_entry_defines.h desolvers.h c_port_interface.h group.h \
		elogger.h alarmlog.h mgr_port.h rtdb.h hashkey.h port_base.h \
		dev_base.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		slctprt.h pt_base.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		mgr_device.h slctdev.h mgr_route.h rte_ccu.h rte_xcu.h \
		msg_pcrequest.h tbl_rtcarrier.h tbl_rtrepeater.h trx_711.h \
		trx_info.h dev_ccu.h ctitypes.h dev_idlc.h dev_remote.h \
		portsup.h dev_single.h connection.h exchange.h msg_multi.h \
		msg_pdata.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h mgr_point.h slctpnt.h
portmain.obj:	portsvc.h cservice.h dlldefs.h CServiceConfig.h \
		ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h portglob.h tcpsup.h porter.h dsm2err.h \
		devicetypes.h queues.h logger.h thread.h cparms.h \
		configparms.h
portperf.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h master.h ilex.h elogger.h portglob.h tcpsup.h \
		ctinexus.h logger.h thread.h c_port_interface.h group.h \
		alarmlog.h mgr_port.h rtdb.h hashkey.h dllbase.h port_base.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h dbmemobject.h \
		tbl_2way.h dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h \
		tbl_pao.h tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		slctprt.h rte_base.h ctibase.h message.h collectable.h \
		tbl_rtcomm.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h mgr_device.h slctdev.h
portpil.obj:	mgr_device.h dlldefs.h rtdb.h hashkey.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h slctdev.h rte_base.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_rtcomm.h \
		mgr_route.h pilserver.h server_b.h con_mgr.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h cmdopts.h msg_pcrequest.h \
		dllyukon.h portglob.h tcpsup.h porter.h dsm2err.h \
		devicetypes.h
portque.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h elogger.h portglob.h tcpsup.h ctinexus.h logger.h \
		thread.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		parsevalue.h ctibase.h dllbase.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		yukon.h db_entry_defines.h desolvers.h c_port_interface.h \
		group.h alarmlog.h mgr_device.h rtdb.h hashkey.h dev_base.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h slctdev.h \
		mgr_port.h port_base.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h slctprt.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h trx_711.h trx_info.h
portsvc.obj:	dlldefs.h portsvc.h cservice.h ctibase.h ctinexus.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h portglob.h tcpsup.h porter.h dsm2err.h devicetypes.h \
		queues.h logger.h thread.h
porttcp.obj:	os2_2w32.h dlldefs.h types.h cticalls.h cti_asmc.h \
		queues.h dsm2.h mutex.h guard.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h master.h porttcp.h tcpsup.h \
		ctinexus.h portglob.h logger.h thread.h color.h \
		c_port_interface.h group.h elogger.h alarmlog.h mgr_port.h \
		rtdb.h hashkey.h dllbase.h port_base.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h resolvers.h pointtypes.h yukon.h \
		db_entry_defines.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		slctprt.h rte_base.h ctibase.h message.h collectable.h \
		tbl_rtcomm.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h mgr_device.h slctdev.h
porttime.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		parsevalue.h ctibase.h ctinexus.h dllbase.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h desolvers.h logger.h \
		thread.h ilex.h portsup.h elogger.h portglob.h tcpsup.h \
		c_port_interface.h group.h alarmlog.h port_base.h dev_base.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		mgr_port.h rtdb.h hashkey.h slctprt.h pt_base.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h mgr_device.h slctdev.h \
		mgr_route.h trx_info.h trx_711.h prot_welco.h
port_shr.obj:	ctinexus.h dlldefs.h types.h dsm2.h mutex.h guard.h \
		port_shr.h thread.h logger.h port_base.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h pointtypes.h \
		yukon.h dllbase.h os2_2w32.h cticalls.h db_entry_defines.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h tcpsup.h
port_shr_ip.obj:	types.h cticalls.h os2_2w32.h dlldefs.h dsm2.h \
		mutex.h guard.h logger.h thread.h port_shr_ip.h ctinexus.h \
		port_shr.h port_base.h dev_base.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h pointtypes.h yukon.h dllbase.h \
		db_entry_defines.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h queues.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		prot_welco.h porter.h dsm2err.h devicetypes.h tcpsup.h \
		utility.h numstr.h
pport.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h portsup.h tcpsup.h ctinexus.h portglob.h logger.h \
		thread.h port_base.h dev_base.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h pointtypes.h yukon.h dllbase.h \
		db_entry_defines.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h
ptprint.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h color.h portglob.h tcpsup.h ctinexus.h logger.h \
		thread.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		parsevalue.h ctibase.h dllbase.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		yukon.h db_entry_defines.h desolvers.h
ripple.obj:	os2_2w32.h dlldefs.h types.h cticalls.h cparms.h queues.h \
		dsm2.h mutex.h guard.h dsm2err.h device.h devicetypes.h \
		routes.h porter.h portdecl.h rte_base.h dbmemobject.h \
		cmdparse.h parsevalue.h ctibase.h ctinexus.h dllbase.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h yukon.h db_entry_defines.h \
		desolvers.h logger.h thread.h master.h lm_auto.h perform.h \
		scanner.h elogger.h mpc.h drp.h portglob.h tcpsup.h \
		c_port_interface.h group.h alarmlog.h dev_base.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h dev_lcu.h ctitypes.h \
		dev_idlc.h dev_remote.h portsup.h dev_single.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h mgr_point.h rtdb.h \
		hashkey.h pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h \
		mgr_device.h slctdev.h mgr_port.h port_base.h tbl_port_base.h \
		porttypes.h tbl_port_settings.h tbl_port_statistics.h \
		tbl_port_timing.h xfer.h dialup.h slctprt.h utility.h
tapterm.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h master.h portsup.h tcpsup.h ctinexus.h perform.h \
		tapterm.h portglob.h logger.h thread.h port_base.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h pointtypes.h \
		yukon.h dllbase.h db_entry_defines.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		port_modem.h port_direct.h tbl_port_serial.h dev_tap.h \
		tbl_dv_tappaging.h dev_ied.h ctitypes.h dev_remote.h \
		dev_single.h connection.h exchange.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h
tdmarkv.obj:	os2_2w32.h dlldefs.h types.h cticalls.h cti_asmc.h \
		queues.h dsm2.h mutex.h guard.h dsm2err.h device.h \
		devicetypes.h routes.h porter.h master.h portsup.h tcpsup.h \
		ctinexus.h perform.h portglob.h logger.h thread.h \
		c_port_interface.h group.h elogger.h alarmlog.h port_base.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		pointtypes.h yukon.h dllbase.h db_entry_defines.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h
traceset.obj:	dlldefs.h
versacom.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h master.h portglob.h tcpsup.h ctinexus.h logger.h \
		thread.h c_port_interface.h group.h elogger.h alarmlog.h \
		mgr_port.h rtdb.h hashkey.h dllbase.h port_base.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h pointtypes.h \
		yukon.h db_entry_defines.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		slctprt.h rte_base.h ctibase.h message.h collectable.h \
		tbl_rtcomm.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h
#ENDUPDATE#
