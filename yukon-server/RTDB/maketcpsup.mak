include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(PORTER)\include \
-I$(MSG)\include \
-I$(PROT)\include \
-I$(CPARMS)\include \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
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



TCPOBJS=\
tcpsup.obj

CTIPROGS=\
tcpsup.dll

WINLIBS=kernel32.lib user32.lib wsock32.lib



ALL:            $(CTIPROGS)


tcpsup.dll:    $(TCPOBJS) $(COMPILEBASE)\lib\ctidbsrc.lib Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(DLLFLAGS) -Fe..\$@ $(TCPOBJS) id_tcpdll.obj -link $(RWLIBS) $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\ctiprtdb.lib $(COMPILEBASE)\lib\ctidbsrc.lib
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy bin\tcpsup.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\tcpsup.lib copy bin\tcpsup.lib $(COMPILEBASE)\lib


clean:
        -del *.obj *.pch *.pdb *.sdb *.adb *.ilk *.exe

allclean:   clean all

deps:
                scandeps -Output maketcpsup.mak *.cpp


# The lines below accomplish the ID'ing of the project!
id:
            @cid .\include\id_tcpdll.h
            @$(MAKE) -nologo -f $(_InputFile) id_tcpdll.obj

id_tcpdll.obj:    id_tcpdll.cpp include\id_tcpdll.h


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) /D_DLL_TCPSUP $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
device.obj:	dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		dlldefs.h guard.h dev_base.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h types.h pointtypes.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h connection.h exchange.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h \
		tbl_carrier.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		mgr_route.h rtdb.h hashkey.h slctdev.h prot_emetcon.h \
		msg_cmd.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h dev_remote.h portsup.h tbl_dialup.h \
		tbl_direct.h dev_meter.h dev_ied.h ctitypes.h tbl_dv_ied.h \
		xfer.h dialup.h
devtest.obj:	hashkey.h mgr_device.h dlldefs.h rtdb.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h slctdev.h rte_base.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_rtcomm.h
dev_710.obj:	cmdparse.h dlldefs.h parsevalue.h dev_710.h dev_idlc.h \
		ctitypes.h types.h os2_2w32.h dsm2.h mutex.h guard.h \
		dev_remote.h portsup.h dev_single.h dev_base.h tbl_base.h \
		resolvers.h pointtypes.h yukon.h dllbase.h cticalls.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		queues.h connection.h exchange.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h porter.h dsm2err.h \
		devicetypes.h trx_711.h prot_emetcon.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h master.h device.h prot_711.h
dev_a1.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h porter.h dsm2err.h devicetypes.h \
		queues.h dev_alpha.h ctitypes.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h dev_ied.h dev_remote.h portsup.h \
		dev_single.h dev_base.h cmdparse.h parsevalue.h tbl_base.h \
		logger.h thread.h tbl_2way.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h \
		tbl_direct.h tbl_dv_ied.h xfer.h dialup.h mgr_point.h rtdb.h \
		hashkey.h pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h \
		device.h dev_a1.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_pt_limit.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		dupreq.h
dev_alpha.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h dev_alpha.h ctitypes.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h portsup.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h logger.h thread.h tbl_2way.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h xfer.h dialup.h \
		mgr_point.h rtdb.h hashkey.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h slctpnt.h device.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h
dev_aplus.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h dev_alpha.h ctitypes.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h portsup.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h logger.h thread.h tbl_2way.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h xfer.h dialup.h \
		mgr_point.h rtdb.h hashkey.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h slctpnt.h device.h dev_aplus.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_pt_limit.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_base.obj:	dev_base.h dsm2.h mutex.h dlldefs.h guard.h cmdparse.h \
		parsevalue.h tbl_base.h resolvers.h types.h pointtypes.h \
		yukon.h dllbase.h os2_2w32.h cticalls.h db_entry_defines.h \
		logger.h thread.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h queues.h \
		mgr_route.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_rtcomm.h rtdb.h hashkey.h slctdev.h \
		mgr_point.h pt_base.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		slctpnt.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h msg_signal.h porter.h dsm2err.h devicetypes.h \
		utility.h
dev_base_lite.obj:	dev_base_lite.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbmemobject.h
dev_cbc.obj:	dsm2.h mutex.h dlldefs.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h types.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h dbaccess.h desolvers.h master.h mgr_route.h \
		rte_base.h cmdparse.h parsevalue.h ctibase.h ctinexus.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h logger.h \
		thread.h rtdb.h hashkey.h slctdev.h dev_base.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h mgr_point.h slctpnt.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		msg_signal.h dev_cbc.h tbl_dv_cbc.h device.h numstr.h
dev_ccu.obj:	cmdparse.h dlldefs.h parsevalue.h dsm2.h mutex.h guard.h \
		porter.h dsm2err.h devicetypes.h queues.h types.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dbaccess.h desolvers.h master.h \
		dev_ccu.h ctitypes.h dev_idlc.h dev_remote.h portsup.h \
		dev_single.h dev_base.h tbl_base.h logger.h thread.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		mgr_point.h rtdb.h hashkey.h slctpnt.h device.h prot_711.h \
		prot_emetcon.h
dev_dct501.obj:	device.h devicetypes.h dlldefs.h dev_dct501.h \
		dev_mct24x.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h guard.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h types.h \
		pointtypes.h yukon.h dllbase.h os2_2w32.h cticalls.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		queues.h connection.h exchange.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h tbl_carrier.h \
		rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h mgr_route.h \
		rtdb.h hashkey.h slctdev.h prot_emetcon.h msg_cmd.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_pt_limit.h mgr_point.h \
		slctpnt.h porter.h dsm2err.h numstr.h
dev_dlcbase.obj:	devicetypes.h dev_dlcbase.h dev_single.h dsm2.h \
		mutex.h dlldefs.h guard.h dev_base.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h types.h pointtypes.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h connection.h exchange.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h \
		tbl_carrier.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		mgr_route.h rtdb.h hashkey.h slctdev.h prot_emetcon.h \
		msg_cmd.h
dev_dr87.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h dev_ied.h ctitypes.h dev_remote.h \
		portsup.h dev_single.h dev_base.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h pointtypes.h db_entry_defines.h \
		logger.h thread.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h xfer.h dialup.h \
		dev_dr87.h dev_meter.h tbl_metergrp.h vcomdefs.h mgr_point.h \
		rtdb.h hashkey.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		slctpnt.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_pt_limit.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		dupreq.h
dev_fulcrum.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h dev_schlum.h ctitypes.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h portsup.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h logger.h thread.h tbl_2way.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h xfer.h dialup.h \
		mgr_point.h rtdb.h hashkey.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h slctpnt.h dev_fulcrum.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_pt_limit.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_grp_emetcon.obj:	dsm2.h mutex.h dlldefs.h guard.h porter.h \
		dsm2err.h devicetypes.h queues.h types.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h pt_base.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h dbaccess.h desolvers.h pt_status.h \
		tbl_pt_status.h master.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h logger.h thread.h mgr_route.h \
		rte_base.h cmdparse.h parsevalue.h ctibase.h ctinexus.h \
		tbl_pao.h tbl_rtcomm.h rtdb.h hashkey.h slctdev.h dev_base.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		msg_pcrequest.h msg_pcreturn.h dev_grp_emetcon.h dev_grp.h \
		msg_lmcontrolhistory.h tbl_dv_emetcon.h numstr.h
dev_grp_ripple.obj:	dev_grp_ripple.h dev_base.h dsm2.h mutex.h \
		dlldefs.h guard.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h types.h pointtypes.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h db_entry_defines.h logger.h thread.h dbmemobject.h \
		tbl_2way.h dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h \
		tbl_pao.h queues.h dev_grp.h msg_lmcontrolhistory.h \
		pointdefs.h message.h collectable.h msg_pdata.h msg_signal.h \
		pt_status.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_status.h tbl_dv_lmg_ripple.h mgr_route.h rte_base.h \
		ctibase.h ctinexus.h tbl_rtcomm.h rtdb.h hashkey.h slctdev.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_cmd.h \
		pt_numeric.h tbl_pt_unit.h tbl_pt_limit.h porter.h dsm2err.h \
		devicetypes.h device.h
dev_grp_vcserial.obj:	dsm2.h mutex.h dlldefs.h guard.h porter.h \
		dsm2err.h devicetypes.h queues.h types.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h pt_base.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h dbaccess.h desolvers.h master.h logger.h \
		thread.h connection.h exchange.h message.h collectable.h \
		msg_multi.h msg_pdata.h msg_signal.h msg_ptreg.h msg_reg.h \
		queue.h mgr_route.h rte_base.h cmdparse.h parsevalue.h \
		ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h rtdb.h hashkey.h \
		slctdev.h dev_base.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h msg_pcrequest.h msg_pcreturn.h \
		dev_grp_vcserial.h dev_grp.h msg_lmcontrolhistory.h \
		pt_status.h tbl_pt_status.h tbl_dv_lmvcserial.h vcomdefs.h
dev_grp_versacom.obj:	dsm2.h mutex.h dlldefs.h guard.h porter.h \
		dsm2err.h devicetypes.h queues.h types.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h pt_base.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h dbaccess.h desolvers.h pt_status.h \
		tbl_pt_status.h master.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_route.h rte_base.h \
		cmdparse.h parsevalue.h ctibase.h ctinexus.h tbl_pao.h \
		tbl_rtcomm.h logger.h thread.h rtdb.h hashkey.h slctdev.h \
		dev_base.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		msg_pcrequest.h msg_pcreturn.h dev_grp_versacom.h dev_grp.h \
		msg_lmcontrolhistory.h tbl_dv_versacom.h vcomdefs.h utility.h \
		numstr.h
dev_ilex.obj:	dsm2.h mutex.h dlldefs.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h types.h device.h dev_ilex.h ctitypes.h \
		os2_2w32.h dev_idlc.h dev_remote.h portsup.h dev_single.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		pointtypes.h yukon.h dllbase.h cticalls.h db_entry_defines.h \
		logger.h thread.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_pt_limit.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h utility.h
dev_lcu.obj:	cparms.h dlldefs.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h cmdparse.h \
		parsevalue.h device.h devicetypes.h dev_lcu.h ctitypes.h \
		dev_idlc.h dev_remote.h portsup.h dev_single.h dev_base.h \
		tbl_base.h resolvers.h pointtypes.h db_entry_defines.h \
		logger.h thread.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h queues.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		porter.h dsm2err.h trx_711.h mgr_point.h rtdb.h hashkey.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h master.h \
		numstr.h pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_pt_limit.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h utility.h elogger.h \
		mpc.h
dev_lgs4.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h dev_lgs4.h ctitypes.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h portsup.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h logger.h thread.h tbl_2way.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h xfer.h dialup.h \
		mgr_point.h rtdb.h hashkey.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h slctpnt.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_macro.obj:	dsm2.h mutex.h dlldefs.h guard.h devicetypes.h \
		message.h collectable.h msg_pcrequest.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h dllbase.h os2_2w32.h types.h \
		cticalls.h dev_macro.h dev_base.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h pointtypes.h db_entry_defines.h \
		logger.h thread.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h queues.h \
		dev_grp.h msg_lmcontrolhistory.h pt_status.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_status.h dev_grp_ripple.h \
		tbl_dv_lmg_ripple.h hashkey.h pt_numeric.h tbl_pt_unit.h \
		tbl_pt_limit.h numstr.h
dev_mct.obj:	numstr.h dlldefs.h devicetypes.h device.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		guard.h dev_base.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h types.h pointtypes.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h db_entry_defines.h logger.h thread.h dbmemobject.h \
		tbl_2way.h dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h \
		tbl_pao.h queues.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h \
		tbl_carrier.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		mgr_route.h rtdb.h hashkey.h slctdev.h prot_emetcon.h \
		msg_cmd.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_pt_limit.h dev_mct31x.h \
		dev_mct310.h mgr_point.h slctpnt.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h porter.h dsm2err.h utility.h
dev_mct210.obj:	device.h devicetypes.h dlldefs.h dev_mct210.h \
		dev_mct2xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dsm2.h mutex.h guard.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h resolvers.h types.h pointtypes.h \
		yukon.h dllbase.h os2_2w32.h cticalls.h db_entry_defines.h \
		logger.h thread.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h queues.h \
		connection.h exchange.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h \
		queue.h msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h tbl_route.h tbl_carrier.h rte_base.h ctibase.h \
		ctinexus.h tbl_rtcomm.h mgr_route.h rtdb.h hashkey.h \
		slctdev.h prot_emetcon.h msg_cmd.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_unit.h tbl_pt_limit.h \
		mgr_point.h slctpnt.h numstr.h porter.h dsm2err.h utility.h
dev_mct22x.obj:	devicetypes.h dev_mct22X.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		dlldefs.h guard.h dev_base.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h types.h pointtypes.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h connection.h exchange.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h \
		tbl_carrier.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		mgr_route.h rtdb.h hashkey.h slctdev.h prot_emetcon.h \
		msg_cmd.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_pt_limit.h mgr_point.h \
		slctpnt.h porter.h dsm2err.h numstr.h
dev_mct24x.obj:	device.h devicetypes.h dlldefs.h dev_mct24X.h \
		dev_mct2xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dsm2.h mutex.h guard.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h resolvers.h types.h pointtypes.h \
		yukon.h dllbase.h os2_2w32.h cticalls.h db_entry_defines.h \
		logger.h thread.h dbmemobject.h tbl_2way.h dbaccess.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h queues.h \
		connection.h exchange.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h \
		queue.h msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h tbl_route.h tbl_carrier.h rte_base.h ctibase.h \
		ctinexus.h tbl_rtcomm.h mgr_route.h rtdb.h hashkey.h \
		slctdev.h prot_emetcon.h msg_cmd.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_unit.h tbl_pt_limit.h \
		mgr_point.h slctpnt.h porter.h dsm2err.h numstr.h
dev_mct2xx.obj:	device.h devicetypes.h dlldefs.h dev_mct2XX.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		mutex.h guard.h dev_base.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h types.h pointtypes.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h db_entry_defines.h logger.h thread.h dbmemobject.h \
		tbl_2way.h dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h \
		tbl_pao.h queues.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h \
		tbl_carrier.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		mgr_route.h rtdb.h hashkey.h slctdev.h prot_emetcon.h \
		msg_cmd.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_pt_limit.h mgr_point.h \
		slctpnt.h porter.h dsm2err.h numstr.h
dev_mct310.obj:	device.h devicetypes.h dlldefs.h dev_mct310.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		mutex.h guard.h dev_base.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h types.h pointtypes.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h db_entry_defines.h logger.h thread.h dbmemobject.h \
		tbl_2way.h dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h \
		tbl_pao.h queues.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h \
		tbl_carrier.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		mgr_route.h rtdb.h hashkey.h slctdev.h prot_emetcon.h \
		msg_cmd.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_pt_limit.h mgr_point.h \
		slctpnt.h numstr.h porter.h dsm2err.h utility.h
dev_mct31x.obj:	devicetypes.h dev_mct31X.h dev_mct310.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		dlldefs.h guard.h dev_base.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h types.h pointtypes.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h connection.h exchange.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h \
		tbl_carrier.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		mgr_route.h rtdb.h hashkey.h slctdev.h prot_emetcon.h \
		msg_cmd.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_pt_limit.h mgr_point.h \
		slctpnt.h numstr.h portsup.h
dev_mct3xx.obj:	device.h devicetypes.h dlldefs.h dev_mct3XX.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		mutex.h guard.h dev_base.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h types.h pointtypes.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h db_entry_defines.h logger.h thread.h dbmemobject.h \
		tbl_2way.h dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h \
		tbl_pao.h queues.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h \
		tbl_carrier.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		mgr_route.h rtdb.h hashkey.h slctdev.h prot_emetcon.h \
		msg_cmd.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_pt_limit.h mgr_point.h \
		slctpnt.h porter.h dsm2err.h utility.h
dev_mct_lmt2.obj:	devicetypes.h dev_mct_lmt2.h dev_mct22x.h \
		dev_mct2xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dsm2.h mutex.h dlldefs.h guard.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h types.h \
		pointtypes.h yukon.h dllbase.h os2_2w32.h cticalls.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		queues.h connection.h exchange.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h tbl_carrier.h \
		rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h mgr_route.h \
		rtdb.h hashkey.h slctdev.h prot_emetcon.h msg_cmd.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_pt_limit.h mgr_point.h \
		slctpnt.h numstr.h
dev_meter.obj:	dev_meter.h tbl_metergrp.h yukon.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		vcomdefs.h dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h dev_ied.h ctitypes.h dev_remote.h \
		portsup.h dev_single.h dev_base.h cmdparse.h parsevalue.h \
		tbl_base.h logger.h thread.h tbl_2way.h tbl_stats.h \
		desolvers.h tbl_scanrate.h tbl_pao.h queues.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h xfer.h dialup.h
dev_quantum.obj:	numstr.h dlldefs.h yukon.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h dev_schlum.h ctitypes.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h portsup.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h logger.h thread.h tbl_2way.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h xfer.h dialup.h \
		mgr_point.h rtdb.h hashkey.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h slctpnt.h dev_quantum.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_pt_limit.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_repeater.obj:	device.h devicetypes.h dlldefs.h dev_repeater.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h guard.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h types.h \
		pointtypes.h yukon.h dllbase.h os2_2w32.h cticalls.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		queues.h connection.h exchange.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h tbl_carrier.h \
		rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h mgr_route.h \
		rtdb.h hashkey.h slctdev.h prot_emetcon.h msg_cmd.h \
		mgr_point.h pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h \
		porter.h dsm2err.h utility.h numstr.h
dev_repeater800.obj:	device.h devicetypes.h dlldefs.h \
		dev_repeater800.h dev_repeater.h dev_dlcbase.h dev_single.h \
		dsm2.h mutex.h guard.h dev_base.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h types.h pointtypes.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h connection.h exchange.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h \
		tbl_carrier.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		mgr_route.h rtdb.h hashkey.h slctdev.h prot_emetcon.h \
		msg_cmd.h mgr_point.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		slctpnt.h porter.h dsm2err.h utility.h numstr.h
dev_schlum.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h dev_schlum.h ctitypes.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h portsup.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h logger.h thread.h tbl_2way.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h xfer.h dialup.h \
		mgr_point.h rtdb.h hashkey.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h slctpnt.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h
dev_single.obj:	dev_single.h dsm2.h mutex.h dlldefs.h guard.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		types.h pointtypes.h yukon.h dllbase.h os2_2w32.h cticalls.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		queues.h connection.h exchange.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h porter.h dsm2err.h \
		devicetypes.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h numstr.h \
		tbl_ptdispatch.h ctibase.h ctinexus.h utility.h
dev_sixnet.obj:	dev_sixnet.h dev_meter.h tbl_metergrp.h yukon.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h vcomdefs.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		ctitypes.h dev_remote.h portsup.h dev_single.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h logger.h thread.h \
		tbl_2way.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		queues.h connection.h exchange.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h xfer.h dialup.h prot_sixnet.h msg_cmd.h porter.h \
		dsm2err.h devicetypes.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h
dev_system.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dev_system.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		queues.h msg_pcrequest.h message.h collectable.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_versacom.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		rte_xcu.h porter.h dsm2err.h devicetypes.h mgr_route.h rtdb.h \
		hashkey.h slctdev.h numstr.h
dev_tap.obj:	dsm2.h mutex.h dlldefs.h guard.h logger.h thread.h \
		porter.h dsm2err.h devicetypes.h queues.h types.h yukon.h \
		dllbase.h os2_2w32.h cticalls.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dbaccess.h desolvers.h \
		connection.h exchange.h message.h collectable.h msg_multi.h \
		msg_pdata.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		mgr_route.h rte_base.h cmdparse.h parsevalue.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h rtdb.h hashkey.h slctdev.h \
		dev_base.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		msg_pcrequest.h msg_pcreturn.h msg_trace.h dev_tap.h \
		tbl_dv_tappaging.h dev_ied.h ctitypes.h dev_remote.h \
		portsup.h dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h xfer.h dialup.h
dev_tcu.obj:	cmdparse.h dlldefs.h parsevalue.h dsm2.h mutex.h guard.h \
		porter.h dsm2err.h devicetypes.h queues.h types.h yukon.h \
		dllbase.h os2_2w32.h cticalls.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dbaccess.h desolvers.h master.h \
		dev_tcu.h ctitypes.h dev_idlc.h dev_remote.h portsup.h \
		dev_single.h dev_base.h tbl_base.h logger.h thread.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		mgr_point.h rtdb.h hashkey.h slctpnt.h device.h
dev_vectron.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h dev_schlum.h ctitypes.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h portsup.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h logger.h thread.h tbl_2way.h \
		tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h xfer.h dialup.h \
		mgr_point.h rtdb.h hashkey.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h slctpnt.h dev_vectron.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_pt_limit.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h numstr.h
dev_welco.obj:	dsm2.h mutex.h dlldefs.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h types.h device.h dev_welco.h \
		ctitypes.h os2_2w32.h dev_idlc.h dev_remote.h portsup.h \
		dev_single.h dev_base.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h pointtypes.h yukon.h dllbase.h cticalls.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		connection.h exchange.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h \
		queue.h msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h \
		trx_info.h trx_711.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h prot_welco.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		msg_lmcontrolhistory.h utility.h
dlldev.obj:	mgr_device.h dlldefs.h rtdb.h hashkey.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h slctdev.h rte_base.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_rtcomm.h \
		mgr_route.h devicetypes.h msg_pcrequest.h rte_xcu.h \
		rte_macro.h tbl_rtmacro.h dev_dlcbase.h dev_single.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h msg_cmd.h dev_grp_emetcon.h dev_grp.h \
		msg_lmcontrolhistory.h pt_status.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_status.h tbl_dv_emetcon.h utility.h
id_devdll.obj:	id_devdll.h utility.h dlldefs.h dsm2.h mutex.h guard.h \
		id_vinfo.h
id_pntdll.obj:	id_pntdll.h utility.h dlldefs.h dsm2.h mutex.h guard.h \
		id_vinfo.h
id_prtdll.obj:	id_prtdll.h utility.h dlldefs.h dsm2.h mutex.h guard.h \
		id_vinfo.h
id_tcpdll.obj:	id_tcpdll.h utility.h dlldefs.h dsm2.h mutex.h guard.h \
		id_vinfo.h
memtest.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h \
		mutex.h guard.h
mgr_device.obj:	rtdb.h dlldefs.h hashkey.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h mgr_device.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h slctdev.h rte_base.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_rtcomm.h \
		dev_macro.h dev_grp.h msg_lmcontrolhistory.h pointdefs.h \
		msg_pdata.h msg_signal.h pt_status.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_status.h dev_cbc.h tbl_dv_cbc.h \
		dev_remote.h portsup.h dev_single.h connection.h exchange.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h \
		tbl_direct.h dev_meter.h tbl_metergrp.h vcomdefs.h dev_ied.h \
		ctitypes.h tbl_dv_ied.h xfer.h dialup.h dev_idlc.h \
		tbl_dv_idlcremote.h trx_info.h porter.h dsm2err.h \
		devicetypes.h trx_711.h dev_carrier.h dev_dlcbase.h \
		tbl_route.h tbl_carrier.h mgr_route.h prot_emetcon.h \
		msg_cmd.h tbl_loadprofile.h tbl_dv_mctiedport.h \
		dev_repeater.h dev_tap.h tbl_dv_tappaging.h dev_grp_emetcon.h \
		tbl_dv_emetcon.h dev_grp_ripple.h tbl_dv_lmg_ripple.h \
		dev_grp_versacom.h tbl_dv_versacom.h
mgr_point.obj:	pt_base.h dbmemobject.h dlldefs.h resolvers.h types.h \
		pointtypes.h yukon.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h dbaccess.h desolvers.h mgr_point.h rtdb.h \
		hashkey.h slctpnt.h devicetypes.h logger.h thread.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_pt_limit.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h pt_status.h tbl_pt_status.h tbl_pt_alarm.h \
		utility.h
mgr_port.obj:	mgr_port.h dlldefs.h rtdb.h hashkey.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		port_base.h dev_base.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h pointtypes.h yukon.h db_entry_defines.h logger.h \
		thread.h dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h \
		desolvers.h tbl_scanrate.h tbl_pao.h queues.h tbl_port_base.h \
		porttypes.h tbl_port_settings.h tbl_port_statistics.h \
		tbl_port_timing.h xfer.h dialup.h slctprt.h rte_base.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_rtcomm.h \
		pt_base.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		port_local_modem.h port_modem.h port_direct.h \
		tbl_port_serial.h portsup.h tbl_port_dialup.h port_tcpip.h \
		tbl_port_tcpip.h tcpsup.h utility.h
mgr_route.obj:	mgr_route.h rte_base.h dsm2.h mutex.h dlldefs.h guard.h \
		dbmemobject.h cmdparse.h parsevalue.h ctibase.h ctinexus.h \
		dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h desolvers.h logger.h \
		thread.h rtdb.h hashkey.h slctdev.h dev_base.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h rte_xcu.h \
		msg_pcrequest.h rte_ccu.h tbl_rtcarrier.h tbl_rtrepeater.h \
		rte_versacom.h tbl_rtversacom.h master.h dev_remote.h \
		portsup.h dev_single.h connection.h exchange.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h \
		queue.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		tbl_dialup.h tbl_direct.h rte_macro.h tbl_rtmacro.h
points.obj:	pt_accum.h dlldefs.h logger.h thread.h mutex.h guard.h \
		pt_numeric.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dbaccess.h desolvers.h tbl_pt_unit.h tbl_pt_limit.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h
pointtest.obj:	logger.h thread.h mutex.h dlldefs.h guard.h mgr_point.h \
		rtdb.h hashkey.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		yukon.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h dbaccess.h desolvers.h slctpnt.h
porttest.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h \
		mutex.h guard.h mgr_port.h rtdb.h hashkey.h port_base.h \
		dev_base.h cmdparse.h parsevalue.h tbl_base.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h slctprt.h rte_base.h ctibase.h ctinexus.h \
		message.h collectable.h tbl_rtcomm.h pt_base.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h
port_base.obj:	port_base.h dev_base.h dsm2.h mutex.h dlldefs.h guard.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h types.h \
		pointtypes.h yukon.h dllbase.h os2_2w32.h cticalls.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		queues.h tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		portsup.h dsm2err.h color.h porter.h devicetypes.h \
		msg_trace.h message.h collectable.h numstr.h
port_direct.obj:	port_direct.h port_base.h dev_base.h dsm2.h mutex.h \
		dlldefs.h guard.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h types.h pointtypes.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h db_entry_defines.h logger.h thread.h dbmemobject.h \
		tbl_2way.h dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h \
		tbl_pao.h queues.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h tbl_port_serial.h portsup.h
port_local_modem.obj:	port_local_modem.h port_modem.h dev_base.h \
		dsm2.h mutex.h dlldefs.h guard.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h types.h pointtypes.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h port_direct.h port_base.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		tbl_port_serial.h portsup.h tbl_port_dialup.h
port_modem.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		port_modem.h dev_base.h dsm2.h cmdparse.h parsevalue.h \
		tbl_base.h resolvers.h types.h pointtypes.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h db_entry_defines.h dbmemobject.h \
		tbl_2way.h dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h \
		tbl_pao.h queues.h port_direct.h port_base.h tbl_port_base.h \
		porttypes.h tbl_port_settings.h tbl_port_statistics.h \
		tbl_port_timing.h xfer.h dialup.h tbl_port_serial.h portsup.h
port_tcpip.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		port_tcpip.h port_base.h dev_base.h dsm2.h cmdparse.h \
		parsevalue.h tbl_base.h resolvers.h types.h pointtypes.h \
		yukon.h dllbase.h os2_2w32.h cticalls.h db_entry_defines.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h desolvers.h \
		tbl_scanrate.h tbl_pao.h queues.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h tbl_port_tcpip.h tcpsup.h ctinexus.h \
		portsup.h
pt_base.obj:	pt_base.h dbmemobject.h dlldefs.h resolvers.h types.h \
		pointtypes.h yukon.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h dbaccess.h desolvers.h tbl_pt_alarm.h logger.h \
		thread.h
pt_dyn_dispatch.obj:	pt_dyn_dispatch.h pt_dyn_base.h dlldefs.h \
		tbl_pt_alarm.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h desolvers.h \
		tbl_ptdispatch.h ctibase.h ctinexus.h pointdefs.h logger.h \
		thread.h
pt_numeric.obj:	device.h devicetypes.h dlldefs.h pt_numeric.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dbaccess.h desolvers.h tbl_pt_unit.h tbl_pt_limit.h \
		tbl_pt_alarm.h logger.h thread.h
pt_status.obj:	pt_status.h dlldefs.h pt_base.h dbmemobject.h \
		resolvers.h types.h pointtypes.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dbaccess.h \
		desolvers.h tbl_pt_status.h tbl_pt_alarm.h
queuetest.obj:	que_omsg.h queent.h dlldefs.h queue.h cstring.h \
		tooldefs.h defs.h compiler.h fseek.h rwassert.h rwwind.h \
		generic.h rwalloc.h backward.h ref.h rstream.h strmshft.h
routetest.obj:	logger.h thread.h mutex.h dlldefs.h guard.h mgr_route.h \
		rte_base.h dsm2.h dbmemobject.h cmdparse.h parsevalue.h \
		ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h yukon.h db_entry_defines.h \
		desolvers.h rtdb.h hashkey.h slctdev.h dev_base.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h
rte_ccu.obj:	dsm2.h mutex.h dlldefs.h guard.h rte_xcu.h dev_base.h \
		cmdparse.h parsevalue.h tbl_base.h resolvers.h types.h \
		pointtypes.h yukon.h dllbase.h os2_2w32.h cticalls.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		queues.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_rtcomm.h msg_pcrequest.h master.h \
		dev_remote.h portsup.h dev_single.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h tbl_dialup.h tbl_direct.h rte_ccu.h \
		tbl_rtcarrier.h tbl_rtrepeater.h porter.h dsm2err.h \
		devicetypes.h numstr.h prot_versacom.h prot_emetcon.h
rte_macro.obj:	dsm2.h mutex.h dlldefs.h guard.h message.h \
		collectable.h msg_pcrequest.h dllbase.h os2_2w32.h types.h \
		cticalls.h rte_macro.h tbl_rtmacro.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h yukon.h db_entry_defines.h \
		desolvers.h logger.h thread.h ctibase.h ctinexus.h rte_base.h \
		cmdparse.h parsevalue.h tbl_pao.h tbl_rtcomm.h
rte_versacom.obj:	cmdparse.h dlldefs.h parsevalue.h dsm2.h mutex.h \
		guard.h logger.h thread.h porter.h dsm2err.h devicetypes.h \
		queues.h types.h rte_versacom.h rte_xcu.h dev_base.h \
		tbl_base.h resolvers.h pointtypes.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h db_entry_defines.h dbmemobject.h \
		tbl_2way.h dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h \
		tbl_pao.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_rtcomm.h msg_pcrequest.h tbl_rtversacom.h \
		master.h dev_remote.h portsup.h dev_single.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h tbl_direct.h \
		prot_versacom.h numstr.h
rte_xcu.obj:	desolvers.h db_entry_defines.h yukon.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h pointtypes.h rte_xcu.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h resolvers.h logger.h thread.h \
		dbmemobject.h tbl_2way.h dbaccess.h tbl_stats.h \
		tbl_scanrate.h tbl_pao.h queues.h rte_base.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_rtcomm.h \
		msg_pcrequest.h master.h dev_remote.h portsup.h dev_single.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h tbl_direct.h \
		dev_tap.h tbl_dv_tappaging.h dev_ied.h ctitypes.h \
		tbl_dv_ied.h xfer.h dialup.h dev_lcu.h dev_idlc.h \
		tbl_dv_idlcremote.h trx_info.h porter.h dsm2err.h \
		devicetypes.h trx_711.h mgr_point.h rtdb.h hashkey.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h numstr.h \
		prot_versacom.h prot_fpcbc.h
slctdev.obj:	dev_710.h dev_idlc.h ctitypes.h types.h os2_2w32.h \
		dlldefs.h dsm2.h mutex.h guard.h dev_remote.h portsup.h \
		dev_single.h dev_base.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h pointtypes.h yukon.h dllbase.h cticalls.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		queues.h connection.h exchange.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h porter.h dsm2err.h \
		devicetypes.h trx_711.h dev_macro.h dev_grp.h \
		msg_lmcontrolhistory.h pt_status.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_status.h dev_cbc.h tbl_dv_cbc.h \
		dev_ccu.h mgr_point.h rtdb.h hashkey.h slctpnt.h dev_welco.h \
		prot_welco.h dev_ilex.h dev_tcu.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dev_ied.h tbl_dv_ied.h xfer.h dialup.h \
		dev_schlum.h dev_fulcrum.h dev_lcu.h dev_quantum.h \
		dev_vectron.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h rte_base.h ctibase.h ctinexus.h tbl_rtcomm.h \
		mgr_route.h slctdev.h prot_emetcon.h msg_cmd.h \
		tbl_loadprofile.h tbl_dv_mctiedport.h dev_tap.h \
		tbl_dv_tappaging.h dev_grp_emetcon.h tbl_dv_emetcon.h \
		dev_grp_ripple.h tbl_dv_lmg_ripple.h dev_grp_versacom.h \
		tbl_dv_versacom.h dev_system.h dev_aplus.h device.h \
		dev_alpha.h dev_a1.h dev_lgs4.h dev_dr87.h dev_dct501.h \
		dev_mct24x.h dev_mct2xx.h dev_mct.h pt_numeric.h \
		tbl_pt_unit.h tbl_pt_limit.h dev_mct210.h dev_mct22X.h \
		dev_mct310.h dev_mct31X.h dev_mct_lmt2.h dev_repeater800.h \
		dev_repeater.h dev_sixnet.h prot_sixnet.h rte_macro.h \
		tbl_rtmacro.h rte_ccu.h rte_xcu.h tbl_rtcarrier.h \
		tbl_rtrepeater.h rte_versacom.h tbl_rtversacom.h master.h
slctpnt.obj:	rtdb.h dlldefs.h hashkey.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbaccess.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h slctpnt.h pt_base.h \
		dbmemobject.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		desolvers.h
slctprt.obj:	port_direct.h port_base.h dev_base.h dsm2.h mutex.h \
		dlldefs.h guard.h cmdparse.h parsevalue.h tbl_base.h \
		resolvers.h types.h pointtypes.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h db_entry_defines.h logger.h thread.h dbmemobject.h \
		tbl_2way.h dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h \
		tbl_pao.h queues.h tbl_port_base.h porttypes.h \
		tbl_port_settings.h tbl_port_statistics.h tbl_port_timing.h \
		xfer.h dialup.h tbl_port_serial.h port_tcpip.h \
		tbl_port_tcpip.h tcpsup.h ctinexus.h port_local_modem.h \
		port_modem.h portsup.h tbl_port_dialup.h devicetypes.h \
		slctprt.h rte_base.h ctibase.h message.h collectable.h \
		tbl_rtcomm.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h
tcpsup.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h porter.h dsm2err.h devicetypes.h tcpsup.h \
		ctinexus.h c_port_interface.h group.h elogger.h alarmlog.h \
		rtdb.h hashkey.h dllbase.h port_base.h dev_base.h cmdparse.h \
		parsevalue.h tbl_base.h resolvers.h pointtypes.h yukon.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h tbl_2way.h \
		dbaccess.h tbl_stats.h desolvers.h tbl_scanrate.h tbl_pao.h \
		tbl_port_base.h porttypes.h tbl_port_settings.h \
		tbl_port_statistics.h tbl_port_timing.h xfer.h dialup.h \
		utility.h
#ENDUPDATE#
