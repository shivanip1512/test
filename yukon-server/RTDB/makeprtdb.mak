include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(PORTER)\include \
-I$(MSG)\include \
-I$(CPARMS)\include \
-I$(PROT)\include \
-I$(BOOST) \
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
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(PROT)\include \
;$(RW)



YUKONPORTDLLOBJS=\
mgr_port.obj \
port_base.obj \
port_dialable.obj \
port_dialin.obj \
port_dialout.obj \
port_direct.obj \
port_modem.obj \
port_pool_out.obj \
port_serial.obj \
port_tcpip.obj \
slctprt.obj \

#port_local_modem.obj \
#port_modem.obj \

PRTDBLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib \


CTIPROGS=\
ctiprtdb.dll


ALL:            $(CTIPROGS)


ctiprtdb.dll:   $(YUKONPORTDLLOBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(YUKONPORTDLLOBJS) id_prtdll.obj -link $(RWLIBS) $(BOOSTLIBS) $(PRTDBLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @echo:
                @echo Done building Target $@
                @echo:
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\*.dll copy bin\*.dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\*.lib copy bin\*.lib $(COMPILEBASE)\lib


deps:
                scandeps -Output makeprtdb.mak *.cpp


clean:
        -del \
*.pdb \
$(OBJ)\*.obj \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.exe


# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_prtdll.obj

id_prtdll.obj:    id_prtdll.cpp include\id_prtdll.h id_vinfo.h



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) /D_DLL_PRTDB -Fdctiprt.pdb -Fo$(OBJ)\ -c $<


######################################################################################


#UPDATE#
device.obj:	yukon.h precompiled.h ctidbgmem.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h dlldefs.h guard.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h dev_remote.h tbl_dialup.h tbl_direct.h \
		dev_meter.h dev_ied.h ctitypes.h tbl_dv_ied.h
devtest.obj:	yukon.h precompiled.h ctidbgmem.h hashkey.h mgr_device.h \
		dlldefs.h rtdb.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h slctdev.h smartmap.h
dev_710.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h dlldefs.h \
		parsevalue.h dev_710.h dev_idlc.h ctitypes.h types.h \
		os2_2w32.h dsm2.h mutex.h guard.h dev_remote.h dev_single.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		cticalls.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h \
		trx_info.h porter.h dsm2err.h devicetypes.h trx_711.h \
		cti_asmc.h pt_base.h pt_dyn_base.h tbl_pt_base.h master.h \
		device.h prot_711.h
dev_a1.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h mutex.h \
		dlldefs.h guard.h dsm2err.h devicetypes.h queues.h types.h \
		dev_alpha.h ctitypes.h os2_2w32.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dllbase.h cticalls.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h desolvers.h logger.h thread.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h device.h dev_a1.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_trace.h dupreq.h
dev_alpha.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h dsm2err.h devicetypes.h queues.h \
		types.h dev_alpha.h ctitypes.h os2_2w32.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h cticalls.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h device.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_aplus.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h dsm2err.h devicetypes.h queues.h \
		types.h dev_alpha.h ctitypes.h os2_2w32.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h cticalls.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h device.h dev_aplus.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_trace.h dupreq.h
dev_base.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dev_base.h dsm2.h mutex.h guard.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		types.h cticalls.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h utility.h \
		mgr_route.h repeaterrole.h rtdb.h hashkey.h slctdev.h \
		smartmap.h mgr_point.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h slctpnt.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h msg_signal.h porter.h dsm2err.h \
		devicetypes.h
dev_base_lite.obj:	yukon.h precompiled.h ctidbgmem.h dev_base_lite.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h sema.h dbmemobject.h
dev_carrier.obj:	yukon.h precompiled.h ctidbgmem.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h dlldefs.h guard.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h
dev_cbc.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h porter.h dsm2err.h devicetypes.h queues.h \
		types.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dbaccess.h sema.h desolvers.h \
		master.h mgr_route.h repeaterrole.h rte_base.h cmdparse.h \
		parsevalue.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h logger.h thread.h rtdb.h hashkey.h \
		slctdev.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h utility.h smartmap.h mgr_point.h slctpnt.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		msg_signal.h dev_cbc.h tbl_dv_cbc.h device.h numstr.h \
		cparms.h
dev_cbc6510.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h porter.h dsm2err.h devicetypes.h queues.h \
		types.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dbaccess.h sema.h desolvers.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		master.h mgr_route.h repeaterrole.h rte_base.h cmdparse.h \
		parsevalue.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h logger.h thread.h rtdb.h hashkey.h \
		slctdev.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h utility.h smartmap.h mgr_point.h slctpnt.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		msg_signal.h dev_cbc6510.h dev_remote.h dev_single.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h dev_dnp.h prot_dnp.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h \
		tbl_dv_idlcremote.h device.h numstr.h cparms.h
dev_ccu.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h dlldefs.h \
		parsevalue.h dsm2.h mutex.h guard.h porter.h dsm2err.h \
		devicetypes.h queues.h types.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h cticalls.h \
		dbaccess.h sema.h desolvers.h master.h dev_ccu.h ctitypes.h \
		dev_idlc.h dev_remote.h dev_single.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h utility.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h mgr_point.h rtdb.h \
		hashkey.h slctpnt.h device.h prot_711.h
dev_davis.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h dlldefs.h \
		parsevalue.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h dev_davis.h \
		dev_ied.h ctitypes.h dev_remote.h dev_single.h dev_base.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		queues.h utility.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h mgr_point.h rtdb.h \
		hashkey.h pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h \
		msg_cmd.h msg_lmcontrolhistory.h numstr.h porter.h dsm2err.h \
		devicetypes.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h
dev_dct501.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h tbl_ptdispatch.h ctibase.h ctinexus.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbmemobject.h pointdefs.h dev_dct501.h dev_mct24x.h \
		dev_mct2xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h msg_signal.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h mgr_point.h rtdb.h hashkey.h \
		slctpnt.h porter.h dsm2err.h numstr.h
dev_dlcbase.obj:	yukon.h precompiled.h ctidbgmem.h dev_dlcbase.h \
		dev_single.h dsm2.h mutex.h dlldefs.h guard.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h dev_mct.h \
		dev_carrier.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		cparms.h devicetypes.h msg_cmd.h porter.h dsm2err.h numstr.h
dev_dnp.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h porter.h dsm2err.h devicetypes.h queues.h \
		types.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dbaccess.h sema.h desolvers.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		pt_status.h tbl_pt_status.h pt_accum.h logger.h thread.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h master.h dllyukon.h \
		mgr_route.h repeaterrole.h rte_base.h cmdparse.h parsevalue.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h rtdb.h hashkey.h slctdev.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h utility.h smartmap.h mgr_point.h \
		slctpnt.h msg_cmd.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h msg_signal.h msg_lmcontrolhistory.h \
		dev_dnp.h dev_remote.h dev_single.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		prot_dnp.h dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h device.h numstr.h \
		cparms.h
dev_dr87.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h dsm2err.h devicetypes.h queues.h \
		types.h dev_ied.h ctitypes.h os2_2w32.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h dllbase.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h utility.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_dr87.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h mgr_point.h rtdb.h hashkey.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_exclusion.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h dev_exclusion.h mutex.h tbl_paoexclusion.h guard.h \
		logger.h thread.h utility.h dsm2.h
dev_fulcrum.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h dsm2err.h devicetypes.h queues.h \
		types.h dev_schlum.h ctitypes.h os2_2w32.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h cticalls.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h dev_fulcrum.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_gateway.obj:	yukon.h precompiled.h ctidbgmem.h dev_gateway.h \
		ctitypes.h cmdparse.h dlldefs.h parsevalue.h dev_gwstat.h \
		dev_ied.h types.h os2_2w32.h dsm2.h mutex.h guard.h \
		dev_remote.h dev_single.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h dllbase.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h gateway.h \
		pending_stat_operation.h numstr.h
dev_grp_emetcon.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h porter.h dsm2err.h devicetypes.h queues.h \
		types.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dbaccess.h sema.h desolvers.h \
		pt_status.h tbl_pt_status.h master.h connection.h exchange.h \
		logger.h thread.h message.h collectable.h msg_multi.h \
		msg_pdata.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		mgr_route.h repeaterrole.h rte_base.h cmdparse.h parsevalue.h \
		ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h rtdb.h hashkey.h \
		slctdev.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h utility.h smartmap.h msg_pcrequest.h \
		msg_pcreturn.h dev_grp_emetcon.h dev_grp.h cparms.h \
		msg_lmcontrolhistory.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		tbl_dv_emetcon.h numstr.h
dev_grp_energypro.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		dlldefs.h parsevalue.h dev_grp_energypro.h dev_base.h dsm2.h \
		mutex.h guard.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h dev_grp.h cparms.h msg_lmcontrolhistory.h \
		pointdefs.h msg_pcrequest.h msg_pdata.h msg_signal.h \
		msg_multi.h pt_status.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		tbl_dv_expresscom.h vcomdefs.h expresscom.h mgr_route.h \
		repeaterrole.h rtdb.h hashkey.h slctdev.h smartmap.h \
		msg_pcreturn.h numstr.h
dev_grp_expresscom.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		dlldefs.h parsevalue.h dev_grp_expresscom.h dev_base.h dsm2.h \
		mutex.h guard.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h dev_grp.h cparms.h msg_lmcontrolhistory.h \
		pointdefs.h msg_pcrequest.h msg_pdata.h msg_signal.h \
		msg_multi.h pt_status.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		tbl_dv_expresscom.h vcomdefs.h expresscom.h mgr_route.h \
		repeaterrole.h rtdb.h hashkey.h slctdev.h smartmap.h \
		msg_pcreturn.h numstr.h
dev_grp_golay.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		dlldefs.h parsevalue.h dev_grp_golay.h dev_base.h dsm2.h \
		mutex.h guard.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h dev_grp.h cparms.h msg_lmcontrolhistory.h \
		pointdefs.h msg_pcrequest.h msg_pdata.h msg_signal.h \
		msg_multi.h pt_status.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		prot_sasimple.h prot_base.h xfer.h dialup.h \
		tbl_lmg_sasimple.h expresscom.h mgr_route.h repeaterrole.h \
		rtdb.h hashkey.h slctdev.h smartmap.h msg_pcreturn.h numstr.h
dev_grp_mct.obj:	yukon.h precompiled.h ctidbgmem.h dev_grp_mct.h \
		dev_grp.h cparms.h dlldefs.h msg_lmcontrolhistory.h \
		pointdefs.h message.h collectable.h msg_pcrequest.h \
		msg_pdata.h msg_signal.h msg_multi.h pt_status.h pt_base.h \
		dbmemobject.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h dbaccess.h \
		sema.h desolvers.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h tbl_dv_lmgmct.h msg_pcreturn.h porter.h dsm2err.h \
		devicetypes.h numstr.h prot_emetcon.h
dev_grp_ripple.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h dev_grp_ripple.h dev_base.h dsm2.h mutex.h guard.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h dev_grp.h \
		msg_lmcontrolhistory.h pointdefs.h msg_pcrequest.h \
		msg_pdata.h msg_signal.h msg_multi.h pt_status.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h tbl_dv_lmg_ripple.h mgr_route.h \
		repeaterrole.h rtdb.h hashkey.h slctdev.h smartmap.h \
		msg_pcreturn.h msg_cmd.h numstr.h porter.h dsm2err.h \
		devicetypes.h device.h
dev_grp_sa105.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		dlldefs.h parsevalue.h cparms.h dev_grp_sa105.h dev_base.h \
		dsm2.h mutex.h guard.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h dev_grp.h \
		msg_lmcontrolhistory.h pointdefs.h msg_pcrequest.h \
		msg_pdata.h msg_signal.h msg_multi.h pt_status.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h prot_sa105.h prot_base.h xfer.h dialup.h \
		tbl_lmg_sa205105.h expresscom.h mgr_route.h repeaterrole.h \
		rtdb.h hashkey.h slctdev.h smartmap.h msg_pcreturn.h numstr.h
dev_grp_sa205.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h cmdparse.h parsevalue.h dev_grp_sa205.h dev_base.h \
		dsm2.h mutex.h guard.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h dev_grp.h \
		msg_lmcontrolhistory.h pointdefs.h msg_pcrequest.h \
		msg_pdata.h msg_signal.h msg_multi.h pt_status.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h prot_sa205.h prot_base.h xfer.h dialup.h \
		tbl_lmg_sa205105.h expresscom.h mgr_route.h repeaterrole.h \
		rtdb.h hashkey.h slctdev.h smartmap.h msg_pcreturn.h numstr.h \
		prot_sa3rdparty.h protocol_sa.h
dev_grp_sa305.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h cmdparse.h parsevalue.h dev_grp_sa305.h dev_base.h \
		dsm2.h mutex.h guard.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h dev_grp.h \
		msg_lmcontrolhistory.h pointdefs.h msg_pcrequest.h \
		msg_pdata.h msg_signal.h msg_multi.h pt_status.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h prot_sa305.h tbl_lmg_sa305.h expresscom.h \
		mgr_route.h repeaterrole.h rtdb.h hashkey.h slctdev.h \
		smartmap.h msg_pcreturn.h numstr.h
dev_grp_sadigital.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		dlldefs.h parsevalue.h dev_grp_sadigital.h dev_base.h dsm2.h \
		mutex.h guard.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h dev_grp.h cparms.h msg_lmcontrolhistory.h \
		pointdefs.h msg_pcrequest.h msg_pdata.h msg_signal.h \
		msg_multi.h pt_status.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		prot_sasimple.h prot_base.h xfer.h dialup.h \
		tbl_lmg_sasimple.h expresscom.h mgr_route.h repeaterrole.h \
		rtdb.h hashkey.h slctdev.h smartmap.h msg_pcreturn.h numstr.h
dev_grp_versacom.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h porter.h dsm2err.h devicetypes.h queues.h \
		types.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dbaccess.h sema.h desolvers.h \
		pt_status.h tbl_pt_status.h master.h connection.h exchange.h \
		logger.h thread.h message.h collectable.h msg_multi.h \
		msg_pdata.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		mgr_route.h repeaterrole.h rte_base.h cmdparse.h parsevalue.h \
		ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h rtdb.h hashkey.h \
		slctdev.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h utility.h smartmap.h msg_pcrequest.h \
		msg_pcreturn.h dev_grp_versacom.h dev_grp.h cparms.h \
		msg_lmcontrolhistory.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		tbl_dv_versacom.h vcomdefs.h numstr.h
dev_gwstat.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h dev_gwstat.h cmdparse.h parsevalue.h \
		ctitypes.h dev_ied.h types.h os2_2w32.h dev_remote.h \
		dev_single.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h gateway.h pending_stat_operation.h devicetypes.h \
		numstr.h pt_numeric.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_gateway_end_device.h
dev_ilex.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h porter.h dsm2err.h devicetypes.h queues.h \
		types.h device.h dev_ilex.h ctitypes.h os2_2w32.h dev_idlc.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h dllbase.h cticalls.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h \
		trx_info.h trx_711.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		msg_lmcontrolhistory.h
dev_ion.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h mutex.h \
		dlldefs.h guard.h dsm2err.h devicetypes.h queues.h types.h \
		tbl_ptdispatch.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		cticalls.h dbmemobject.h pointdefs.h dev_ion.h ctitypes.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h utility.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h tbl_dv_address.h \
		prot_ion.h ion_datastream.h ion_value.h ion_serializable.h \
		numstr.h ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable.h ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		msg_lmcontrolhistory.h dllyukon.h cparms.h
dev_kv2.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h mutex.h \
		dlldefs.h guard.h dsm2err.h devicetypes.h queues.h types.h \
		logger.h thread.h dev_kv2.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dllbase.h os2_2w32.h cticalls.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		dev_ied.h ctitypes.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h desolvers.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h prot_ansi_kv2.h prot_ansi.h ansi_application.h \
		ansi_datalink.h ansi_billing_table.h std_ansi_tbl_base.h \
		std_ansi_tbl_zero_zero.h std_ansi_tbl_zero_one.h \
		std_ansi_tbl_zero_eight.h std_ansi_tbl_one_zero.h \
		std_ansi_tbl_one_one.h std_ansi_tbl_one_two.h \
		std_ansi_tbl_one_three.h std_ansi_tbl_one_four.h \
		std_ansi_tbl_one_five.h std_ansi_tbl_one_six.h \
		std_ansi_tbl_two_one.h std_ansi_tbl_two_two.h \
		std_ansi_tbl_two_three.h std_ansi_tbl_two_seven.h \
		std_ansi_tbl_two_eight.h std_ansi_tbl_three_one.h \
		std_ansi_tbl_three_two.h std_ansi_tbl_three_three.h \
		std_ansi_tbl_five_one.h std_ansi_tbl_five_two.h \
		std_ansi_tbl_six_one.h std_ansi_tbl_six_two.h \
		std_ansi_tbl_six_three.h std_ansi_tbl_six_four.h \
		ansi_kv2_mtable_zero.h ansi_kv2_mtable_seventy.h \
		ansi_kv2_mtable_onehundredten.h mgr_point.h rtdb.h hashkey.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h device.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h
dev_lcu.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h cmdparse.h \
		parsevalue.h device.h devicetypes.h dev_lcu.h ctitypes.h \
		dev_idlc.h dev_remote.h dev_single.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h msg_pcrequest.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h trx_info.h porter.h \
		dsm2err.h trx_711.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h master.h numstr.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_accum.h tbl_pt_accumhistory.h elogger.h \
		mpc.h
dev_lgs4.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h dsm2err.h devicetypes.h queues.h \
		types.h dev_lgs4.h ctitypes.h os2_2w32.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h cticalls.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_lmi.obj:	yukon.h precompiled.h ctidbgmem.h dev_lmi.h dev_remote.h \
		dev_single.h dsm2.h mutex.h dlldefs.h guard.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_address.h \
		prot_lmi.h prot_seriesv.h verification_objects.h boost_time.h \
		porter.h dsm2err.h devicetypes.h pt_numeric.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h pt_status.h tbl_pt_status.h numstr.h \
		dllyukon.h cparms.h
dev_macro.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h devicetypes.h message.h collectable.h \
		msg_pcrequest.h msg_pdata.h pointdefs.h msg_signal.h \
		dllbase.h os2_2w32.h types.h cticalls.h dev_macro.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h dev_grp.h cparms.h \
		msg_lmcontrolhistory.h msg_multi.h pt_status.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h dev_grp_ripple.h tbl_dv_lmg_ripple.h \
		hashkey.h numstr.h
dev_mark_v.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h dsm2err.h devicetypes.h queues.h \
		types.h logger.h thread.h dev_mark_v.h prot_transdata.h \
		dllbase.h os2_2w32.h cticalls.h transdata_application.h \
		xfer.h dialup.h utility.h transdata_tracker.h numstr.h \
		transdata_datalink.h prot_ymodem.h transdata_data.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		dev_ied.h ctitypes.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h desolvers.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h mgr_point.h rtdb.h \
		hashkey.h pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h \
		device.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h msg_cmd.h
dev_mct.obj:	yukon.h precompiled.h ctidbgmem.h numstr.h dlldefs.h \
		devicetypes.h device.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dsm2.h mutex.h guard.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		dev_mct210.h dev_mct2xx.h dev_mct31x.h dev_mct310.h \
		dev_mct410.h dev_mct470.h dev_mct_lmt2.h dev_mct22x.h \
		mgr_point.h rtdb.h hashkey.h slctpnt.h msg_cmd.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h porter.h dsm2err.h \
		dllyukon.h cparms.h
dev_mct210.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_mct210.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		guard.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		mgr_point.h rtdb.h hashkey.h slctpnt.h numstr.h porter.h \
		dsm2err.h
dev_mct22x.obj:	yukon.h precompiled.h ctidbgmem.h devicetypes.h \
		dev_mct22X.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h dlldefs.h guard.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		mgr_point.h rtdb.h hashkey.h slctpnt.h porter.h dsm2err.h \
		numstr.h
dev_mct24x.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_mct24X.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		guard.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		mgr_point.h rtdb.h hashkey.h slctpnt.h porter.h dsm2err.h \
		numstr.h dllyukon.h
dev_mct2xx.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_mct2XX.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h guard.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		mgr_point.h rtdb.h hashkey.h slctpnt.h porter.h dsm2err.h \
		numstr.h
dev_mct310.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_mct310.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h guard.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		mgr_point.h rtdb.h hashkey.h slctpnt.h pt_status.h \
		tbl_pt_status.h numstr.h porter.h dsm2err.h dllyukon.h
dev_mct31x.obj:	yukon.h precompiled.h ctidbgmem.h devicetypes.h \
		tbl_ptdispatch.h ctibase.h ctinexus.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbmemobject.h pointdefs.h dev_mct31X.h dev_mct310.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		mgr_point.h rtdb.h hashkey.h slctpnt.h numstr.h dllyukon.h
dev_mct410.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_mct410.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h guard.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_ptdispatch.h mgr_point.h rtdb.h hashkey.h slctpnt.h \
		numstr.h porter.h dsm2err.h portglob.h tcpsup.h statistics.h
dev_mct470.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h tbl_ptdispatch.h ctibase.h ctinexus.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbmemobject.h pointdefs.h dev_mct470.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		dev_mct410.h mgr_point.h rtdb.h hashkey.h slctpnt.h numstr.h \
		porter.h dsm2err.h dllyukon.h
dev_mct_broadcast.obj:	yukon.h precompiled.h ctidbgmem.h \
		dev_mct_broadcast.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		dlldefs.h guard.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		types.h cticalls.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h numstr.h porter.h dsm2err.h devicetypes.h \
		dev_mct.h dev_carrier.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h dev_mct31x.h dev_mct310.h dev_mct410.h
dev_mct_lmt2.obj:	yukon.h precompiled.h ctidbgmem.h devicetypes.h \
		dev_mct_lmt2.h dev_mct22x.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		dlldefs.h guard.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		types.h cticalls.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		mgr_point.h rtdb.h hashkey.h slctpnt.h numstr.h
dev_meter.obj:	yukon.h precompiled.h ctidbgmem.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dlldefs.h dbmemobject.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		dev_ied.h ctitypes.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h desolvers.h logger.h \
		thread.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		queues.h utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h
dev_quantum.obj:	yukon.h precompiled.h ctidbgmem.h numstr.h dlldefs.h \
		porter.h dsm2.h mutex.h guard.h dsm2err.h devicetypes.h \
		queues.h types.h dev_schlum.h ctitypes.h os2_2w32.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		cticalls.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h desolvers.h logger.h \
		thread.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h dev_quantum.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_repeater.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_repeater.h dev_dlcbase.h \
		dev_single.h dsm2.h mutex.h guard.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h porter.h dsm2err.h \
		numstr.h
dev_repeater800.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_repeater800.h dev_repeater.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h guard.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h mgr_point.h \
		rtdb.h hashkey.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		slctpnt.h porter.h dsm2err.h numstr.h
dev_rtc.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dsm2err.h dev_rtc.h dev_remote.h dev_single.h dsm2.h mutex.h \
		guard.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_rtc.h msg_cmd.h \
		msg_lmcontrolhistory.h protocol_sa.h prot_sa305.h \
		prot_sa3rdparty.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		pt_status.h tbl_pt_status.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h verification_objects.h boost_time.h \
		numstr.h
dev_rtm.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dsm2err.h dev_rtm.h dev_ied.h ctitypes.h types.h os2_2w32.h \
		dsm2.h mutex.h guard.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h verification_objects.h boost_time.h msg_cmd.h \
		porter.h devicetypes.h protocol_sa.h prot_sa3rdparty.h \
		numstr.h
dev_schlum.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h dsm2err.h devicetypes.h queues.h \
		types.h dev_schlum.h ctitypes.h os2_2w32.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h cticalls.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_sentinel.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h dsm2err.h devicetypes.h queues.h \
		types.h logger.h thread.h dev_sentinel.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h os2_2w32.h \
		cticalls.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h dev_ied.h ctitypes.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		desolvers.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h prot_ansi_sentinel.h prot_ansi.h \
		ansi_application.h ansi_datalink.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_zero_zero.h \
		std_ansi_tbl_zero_one.h std_ansi_tbl_zero_eight.h \
		std_ansi_tbl_one_zero.h std_ansi_tbl_one_one.h \
		std_ansi_tbl_one_two.h std_ansi_tbl_one_three.h \
		std_ansi_tbl_one_four.h std_ansi_tbl_one_five.h \
		std_ansi_tbl_one_six.h std_ansi_tbl_two_one.h \
		std_ansi_tbl_two_two.h std_ansi_tbl_two_three.h \
		std_ansi_tbl_two_seven.h std_ansi_tbl_two_eight.h \
		std_ansi_tbl_three_one.h std_ansi_tbl_three_two.h \
		std_ansi_tbl_three_three.h std_ansi_tbl_five_one.h \
		std_ansi_tbl_five_two.h std_ansi_tbl_six_one.h \
		std_ansi_tbl_six_two.h std_ansi_tbl_six_three.h \
		std_ansi_tbl_six_four.h mgr_point.h rtdb.h hashkey.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h device.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h
dev_seriesv.obj:	yukon.h precompiled.h ctidbgmem.h dev_seriesv.h \
		dev_ied.h ctitypes.h types.h os2_2w32.h dlldefs.h dsm2.h \
		mutex.h guard.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h prot_seriesv.h porter.h dsm2err.h devicetypes.h
dev_single.obj:	yukon.h precompiled.h ctidbgmem.h dev_single.h dsm2.h \
		mutex.h dlldefs.h guard.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		types.h cticalls.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h porter.h dsm2err.h devicetypes.h \
		mgr_point.h rtdb.h hashkey.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h slctpnt.h numstr.h tbl_ptdispatch.h
dev_sixnet.obj:	yukon.h precompiled.h ctidbgmem.h dev_sixnet.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h dev_ied.h ctitypes.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		desolvers.h logger.h thread.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h prot_sixnet.h \
		msg_cmd.h porter.h dsm2err.h devicetypes.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h
dev_snpp.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dsm2.h mutex.h guard.h logger.h thread.h porter.h dsm2err.h \
		devicetypes.h queues.h types.h cmdparse.h parsevalue.h \
		pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dbaccess.h sema.h desolvers.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		port_base.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h utility.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h connection.h exchange.h \
		msg_multi.h msg_pdata.h msg_signal.h msg_ptreg.h msg_reg.h \
		queue.h mgr_route.h repeaterrole.h rtdb.h hashkey.h slctdev.h \
		smartmap.h msg_pcrequest.h msg_pcreturn.h msg_trace.h \
		numstr.h dev_snpp.h tbl_dv_tappaging.h dev_ied.h ctitypes.h \
		dev_remote.h dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h \
		prot_base.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h
dev_system.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dev_system.h dev_base.h dsm2.h mutex.h guard.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_versacom.h rte_xcu.h smartmap.h hashkey.h \
		porter.h dsm2err.h devicetypes.h mgr_route.h repeaterrole.h \
		rtdb.h slctdev.h numstr.h
dev_tap.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dsm2.h mutex.h guard.h logger.h thread.h porter.h dsm2err.h \
		devicetypes.h queues.h types.h cmdparse.h parsevalue.h \
		pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dbaccess.h sema.h desolvers.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		connection.h exchange.h message.h collectable.h msg_multi.h \
		msg_pdata.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		mgr_route.h repeaterrole.h rte_base.h ctibase.h ctinexus.h \
		tbl_pao.h tbl_rtcomm.h rtdb.h hashkey.h slctdev.h dev_base.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h smartmap.h \
		msg_pcrequest.h msg_pcreturn.h msg_trace.h numstr.h dev_tap.h \
		tbl_dv_tappaging.h dev_ied.h ctitypes.h dev_remote.h \
		dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		xfer.h dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h
dev_tcu.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h dlldefs.h \
		parsevalue.h cparms.h dsm2.h mutex.h guard.h porter.h \
		dsm2err.h devicetypes.h queues.h types.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dbaccess.h sema.h desolvers.h master.h dev_tcu.h \
		ctitypes.h dev_idlc.h dev_remote.h dev_single.h dev_base.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h logger.h thread.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h msg_signal.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h mgr_point.h rtdb.h hashkey.h slctpnt.h device.h
dev_vectron.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h dsm2err.h devicetypes.h queues.h \
		types.h dev_schlum.h ctitypes.h os2_2w32.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h cticalls.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h mgr_point.h rtdb.h hashkey.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h dev_vectron.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h numstr.h
dev_wctp.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dsm2.h mutex.h guard.h logger.h thread.h porter.h dsm2err.h \
		devicetypes.h queues.h types.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h cticalls.h \
		dbaccess.h sema.h desolvers.h pt_accum.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_route.h repeaterrole.h \
		rte_base.h cmdparse.h parsevalue.h ctibase.h ctinexus.h \
		tbl_pao.h tbl_rtcomm.h rtdb.h hashkey.h slctdev.h dev_base.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h smartmap.h \
		msg_pcrequest.h msg_pcreturn.h msg_trace.h dev_wctp.h \
		tbl_dv_tappaging.h dev_ied.h ctitypes.h dev_remote.h \
		dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		xfer.h dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h
dev_welco.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dsm2.h mutex.h guard.h dllyukon.h porter.h dsm2err.h \
		devicetypes.h queues.h types.h device.h dev_welco.h \
		ctitypes.h os2_2w32.h dev_idlc.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h mgr_point.h rtdb.h \
		hashkey.h pt_base.h pt_dyn_base.h tbl_pt_base.h slctpnt.h \
		prot_welco.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_lmcontrolhistory.h \
		numstr.h
dlldev.obj:	yukon.h precompiled.h ctidbgmem.h mgr_device.h dlldefs.h \
		rtdb.h hashkey.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h slctdev.h smartmap.h mgr_route.h repeaterrole.h \
		devicetypes.h msg_pcrequest.h rte_xcu.h rte_macro.h \
		tbl_rtmacro.h dev_dlcbase.h dev_single.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h dev_grp_emetcon.h \
		dev_grp.h cparms.h msg_lmcontrolhistory.h pt_status.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h tbl_dv_emetcon.h
id_devdll.obj:	yukon.h precompiled.h ctidbgmem.h id_devdll.h utility.h \
		dsm2.h mutex.h dlldefs.h guard.h id_build.h id_vinfo.h
id_pntdll.obj:	yukon.h precompiled.h ctidbgmem.h id_pntdll.h utility.h \
		dsm2.h mutex.h dlldefs.h guard.h id_build.h id_vinfo.h
id_prtdll.obj:	yukon.h precompiled.h ctidbgmem.h id_prtdll.h utility.h \
		dsm2.h mutex.h dlldefs.h guard.h id_build.h id_vinfo.h
id_tcpdll.obj:	yukon.h precompiled.h ctidbgmem.h id_tcpdll.h utility.h \
		dsm2.h mutex.h dlldefs.h guard.h id_build.h id_vinfo.h
memtest.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h
mgr_device.obj:	yukon.h precompiled.h ctidbgmem.h rtdb.h dlldefs.h \
		hashkey.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h mgr_device.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h slctdev.h smartmap.h \
		cparms.h dev_macro.h dev_grp.h msg_lmcontrolhistory.h \
		pointdefs.h msg_pcrequest.h msg_pdata.h msg_signal.h \
		msg_multi.h pt_status.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h dev_cbc.h \
		tbl_dv_cbc.h dev_dnp.h dev_remote.h dev_single.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h prot_dnp.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h dev_ion.h \
		ctitypes.h dev_meter.h tbl_metergrp.h vcomdefs.h dev_ied.h \
		tbl_dv_ied.h mgr_point.h slctpnt.h prot_ion.h \
		ion_datastream.h ion_value.h ion_serializable.h numstr.h \
		ion_value_fixed.h ion_value_numeric.h ion_value_variable.h \
		ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h dev_idlc.h \
		tbl_dv_idlcremote.h trx_info.h porter.h dsm2err.h \
		devicetypes.h trx_711.h dev_carrier.h dev_dlcbase.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h dev_mct.h dev_mct410.h dev_repeater.h \
		dev_rtc.h tbl_dv_rtc.h dev_rtm.h verification_objects.h \
		boost_time.h dev_tap.h tbl_dv_tappaging.h dev_snpp.h \
		dev_grp_emetcon.h tbl_dv_emetcon.h dev_grp_energypro.h \
		tbl_dv_expresscom.h dev_grp_expresscom.h dev_grp_golay.h \
		prot_sasimple.h tbl_lmg_sasimple.h dev_grp_ripple.h \
		tbl_dv_lmg_ripple.h dev_grp_sa105.h prot_sa105.h \
		tbl_lmg_sa205105.h dev_grp_sa305.h prot_sa305.h \
		tbl_lmg_sa305.h dev_grp_sa205.h prot_sa205.h \
		dev_grp_sadigital.h dev_grp_versacom.h tbl_dv_versacom.h \
		dev_grp_mct.h tbl_dv_lmgmct.h dev_mct_broadcast.h
mgr_exclusion.obj:	yukon.h precompiled.h ctidbgmem.h mgr_exclusion.h \
		dlldefs.h dev_base.h dsm2.h mutex.h guard.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h smartmap.h hashkey.h
mgr_holiday.obj:	yukon.h precompiled.h ctidbgmem.h mgr_holiday.h \
		dlldefs.h mutex.h guard.h logger.h thread.h dbaccess.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h sema.h
mgr_point.obj:	yukon.h precompiled.h ctidbgmem.h pt_base.h \
		dbmemobject.h dlldefs.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		dbaccess.h sema.h desolvers.h mgr_point.h rtdb.h hashkey.h \
		slctpnt.h devicetypes.h logger.h thread.h pt_accum.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h pt_status.h tbl_pt_status.h tbl_pt_alarm.h \
		utility.h
mgr_port.obj:	yukon.h precompiled.h ctidbgmem.h mgr_port.h dlldefs.h \
		smartmap.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h hashkey.h logger.h thread.h port_base.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		queues.h utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h slctprt.h pt_base.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h port_direct.h port_serial.h \
		tbl_port_settings.h tbl_port_timing.h port_dialable.h \
		port_modem.h tbl_port_dialup.h tbl_port_serial.h \
		port_dialout.h port_pool_out.h port_tcpip.h tbl_port_tcpip.h \
		tcpsup.h
mgr_route.obj:	yukon.h precompiled.h ctidbgmem.h mgr_route.h \
		repeaterrole.h dlldefs.h rte_base.h dsm2.h mutex.h guard.h \
		dbmemobject.h cmdparse.h parsevalue.h ctibase.h ctinexus.h \
		dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h rtdb.h hashkey.h slctdev.h dev_base.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h utility.h \
		smartmap.h rte_xcu.h msg_pcrequest.h rte_ccu.h \
		tbl_rtcarrier.h tbl_rtrepeater.h rte_versacom.h \
		tbl_rtversacom.h master.h dev_remote.h dev_single.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h rte_macro.h tbl_rtmacro.h
mgr_season.obj:	yukon.h precompiled.h ctidbgmem.h mgr_season.h \
		dlldefs.h mutex.h guard.h logger.h thread.h dbaccess.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h sema.h
points.obj:	yukon.h precompiled.h ctidbgmem.h pt_accum.h dlldefs.h \
		logger.h thread.h mutex.h guard.h pt_numeric.h pt_base.h \
		dbmemobject.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h dbaccess.h sema.h \
		desolvers.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h
pointtest.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h mgr_point.h rtdb.h hashkey.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h \
		desolvers.h slctpnt.h
porttest.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h \
		mgr_port.h smartmap.h hashkey.h logger.h thread.h port_base.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		queues.h utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h slctprt.h pt_base.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h rtdb.h
port_base.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		port_base.h dev_base.h dsm2.h mutex.h guard.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h prot_emetcon.h dsm2err.h color.h \
		porter.h devicetypes.h msg_trace.h numstr.h
port_dialable.obj:	yukon.h precompiled.h ctidbgmem.h port_dialable.h \
		port_base.h dev_base.h dsm2.h mutex.h dlldefs.h guard.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h port_modem.h tbl_port_dialup.h
port_dialin.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h logger.h thread.h port_dialin.h port_base.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h port_dialable.h port_modem.h \
		tbl_port_dialup.h
port_dialout.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h logger.h thread.h port_dialout.h \
		port_base.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		queues.h utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h port_dialable.h port_modem.h \
		tbl_port_dialup.h
port_direct.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h port_direct.h port_serial.h port_base.h dev_base.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h tbl_port_base.h \
		xfer.h dialup.h tbl_port_statistics.h tbl_port_settings.h \
		tbl_port_timing.h port_dialable.h port_modem.h \
		tbl_port_dialup.h tbl_port_serial.h
port_modem.obj:	yukon.h precompiled.h ctidbgmem.h numstr.h dlldefs.h \
		port_modem.h port_base.h dev_base.h dsm2.h mutex.h guard.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h
port_pool_out.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h port_pool_out.h port_base.h dev_base.h dsm2.h \
		mutex.h guard.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h tbl_port_base.h \
		xfer.h dialup.h tbl_port_statistics.h
port_serial.obj:	yukon.h precompiled.h ctidbgmem.h port_serial.h \
		port_base.h dev_base.h dsm2.h mutex.h dlldefs.h guard.h \
		cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h queues.h utility.h tbl_port_base.h xfer.h \
		dialup.h tbl_port_statistics.h tbl_port_settings.h \
		tbl_port_timing.h
port_tcpip.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		logger.h thread.h mutex.h guard.h port_tcpip.h port_serial.h \
		port_base.h dev_base.h dsm2.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		types.h cticalls.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h tbl_port_base.h \
		xfer.h dialup.h tbl_port_statistics.h tbl_port_settings.h \
		tbl_port_timing.h port_dialable.h port_modem.h \
		tbl_port_dialup.h tbl_port_tcpip.h tcpsup.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
pt_base.obj:	yukon.h precompiled.h ctidbgmem.h pt_base.h dbmemobject.h \
		dlldefs.h resolvers.h types.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h dbaccess.h sema.h \
		desolvers.h tbl_pt_alarm.h logger.h thread.h
pt_dyn_dispatch.obj:	yukon.h precompiled.h ctidbgmem.h \
		pt_dyn_dispatch.h pt_dyn_base.h dlldefs.h tbl_pt_alarm.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h tbl_ptdispatch.h \
		ctibase.h ctinexus.h pointdefs.h logger.h thread.h
pt_numeric.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h pt_numeric.h pt_base.h dbmemobject.h \
		resolvers.h types.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h dbaccess.h sema.h \
		desolvers.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_alarm.h logger.h thread.h
pt_status.obj:	yukon.h precompiled.h ctidbgmem.h pt_status.h dlldefs.h \
		pt_base.h dbmemobject.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		dbaccess.h sema.h desolvers.h tbl_pt_status.h tbl_pt_alarm.h
queuetest.obj:	yukon.h precompiled.h ctidbgmem.h queent.h dlldefs.h \
		queue.h logger.h thread.h mutex.h guard.h
routetest.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h mgr_route.h repeaterrole.h \
		rte_base.h dsm2.h dbmemobject.h cmdparse.h parsevalue.h \
		ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h \
		message.h collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h rtdb.h hashkey.h slctdev.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h smartmap.h
rte_ccu.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h rte_xcu.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h msg_pcrequest.h smartmap.h hashkey.h master.h \
		dev_remote.h dev_single.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		dev_ccu.h ctitypes.h dev_idlc.h tbl_dv_idlcremote.h \
		trx_info.h porter.h dsm2err.h devicetypes.h trx_711.h \
		mgr_point.h rtdb.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		slctpnt.h rte_ccu.h tbl_rtcarrier.h tbl_rtrepeater.h numstr.h \
		prot_versacom.h prot_emetcon.h cparms.h
rte_macro.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h message.h collectable.h msg_pcrequest.h \
		dllbase.h os2_2w32.h types.h cticalls.h rte_macro.h \
		tbl_rtmacro.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		ctibase.h ctinexus.h rte_base.h cmdparse.h parsevalue.h \
		tbl_pao.h tbl_rtcomm.h porter.h dsm2err.h devicetypes.h \
		queues.h
rte_versacom.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		dlldefs.h parsevalue.h dsm2.h mutex.h guard.h logger.h \
		thread.h porter.h dsm2err.h devicetypes.h queues.h types.h \
		rte_versacom.h rte_xcu.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h cticalls.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		msg_pcrequest.h smartmap.h hashkey.h tbl_rtversacom.h \
		master.h dev_remote.h dev_single.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		prot_versacom.h numstr.h
rte_xcu.obj:	yukon.h precompiled.h ctidbgmem.h desolvers.h \
		db_entry_defines.h dlldefs.h types.h pointtypes.h dsm2.h \
		mutex.h guard.h expresscom.h cmdparse.h parsevalue.h \
		dllbase.h os2_2w32.h cticalls.h rte_xcu.h dev_base.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h logger.h \
		thread.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		queues.h utility.h msg_pcrequest.h smartmap.h hashkey.h \
		master.h dev_remote.h dev_single.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		dev_tap.h tbl_dv_tappaging.h dev_ied.h ctitypes.h \
		tbl_dv_ied.h dev_snpp.h dev_lcu.h dev_idlc.h \
		tbl_dv_idlcremote.h trx_info.h porter.h dsm2err.h \
		devicetypes.h trx_711.h mgr_point.h rtdb.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h slctpnt.h dev_wctp.h numstr.h \
		prot_versacom.h prot_fpcbc.h prot_sa305.h prot_sa3rdparty.h \
		protocol_sa.h
slctdev.obj:	yukon.h precompiled.h ctidbgmem.h dev_710.h dev_idlc.h \
		ctitypes.h types.h os2_2w32.h dlldefs.h dsm2.h mutex.h \
		guard.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		cticalls.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h queues.h utility.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h \
		trx_info.h porter.h dsm2err.h devicetypes.h trx_711.h \
		dev_macro.h dev_grp.h cparms.h msg_lmcontrolhistory.h \
		pt_status.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		dev_cbc6510.h dev_dnp.h prot_dnp.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h \
		tbl_dv_address.h dev_cbc.h tbl_dv_cbc.h dev_ccu.h mgr_point.h \
		rtdb.h hashkey.h slctpnt.h dev_welco.h prot_welco.h \
		dev_ilex.h dev_seriesv.h dev_ied.h tbl_dv_ied.h \
		prot_seriesv.h dev_lmi.h prot_lmi.h verification_objects.h \
		boost_time.h dev_tcu.h dev_meter.h tbl_metergrp.h vcomdefs.h \
		dev_schlum.h dev_fulcrum.h dev_ion.h prot_ion.h \
		ion_datastream.h ion_value.h ion_serializable.h numstr.h \
		ion_value_fixed.h ion_value_numeric.h ion_value_variable.h \
		ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h dev_lcu.h dev_quantum.h \
		dev_vectron.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h dev_rtm.h dev_tap.h tbl_dv_tappaging.h \
		dev_snpp.h dev_wctp.h dev_grp_emetcon.h tbl_dv_emetcon.h \
		dev_grp_expresscom.h tbl_dv_expresscom.h dev_grp_energypro.h \
		dev_grp_golay.h prot_sasimple.h tbl_lmg_sasimple.h \
		dev_grp_mct.h tbl_dv_lmgmct.h dev_grp_ripple.h \
		tbl_dv_lmg_ripple.h dev_grp_sa105.h prot_sa105.h \
		tbl_lmg_sa205105.h dev_grp_sa205.h prot_sa205.h \
		dev_grp_sa305.h prot_sa305.h tbl_lmg_sa305.h \
		dev_grp_sadigital.h dev_grp_versacom.h tbl_dv_versacom.h \
		dev_davis.h dev_system.h dev_aplus.h device.h dev_alpha.h \
		dev_a1.h dev_lgs4.h dev_dr87.h dev_dct501.h dev_mct24x.h \
		dev_mct2xx.h dev_mct.h dev_mct210.h dev_mct22X.h dev_mct310.h \
		dev_mct31X.h dev_mct410.h dev_mct470.h dev_mct_lmt2.h \
		dev_mct_broadcast.h dev_kv2.h prot_ansi_kv2.h prot_ansi.h \
		ansi_application.h ansi_datalink.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_zero_zero.h \
		std_ansi_tbl_zero_one.h std_ansi_tbl_zero_eight.h \
		std_ansi_tbl_one_zero.h std_ansi_tbl_one_one.h \
		std_ansi_tbl_one_two.h std_ansi_tbl_one_three.h \
		std_ansi_tbl_one_four.h std_ansi_tbl_one_five.h \
		std_ansi_tbl_one_six.h std_ansi_tbl_two_one.h \
		std_ansi_tbl_two_two.h std_ansi_tbl_two_three.h \
		std_ansi_tbl_two_seven.h std_ansi_tbl_two_eight.h \
		std_ansi_tbl_three_one.h std_ansi_tbl_three_two.h \
		std_ansi_tbl_three_three.h std_ansi_tbl_five_one.h \
		std_ansi_tbl_five_two.h std_ansi_tbl_six_one.h \
		std_ansi_tbl_six_two.h std_ansi_tbl_six_three.h \
		std_ansi_tbl_six_four.h ansi_kv2_mtable_zero.h \
		ansi_kv2_mtable_seventy.h ansi_kv2_mtable_onehundredten.h \
		dev_sentinel.h prot_ansi_sentinel.h dev_mark_v.h \
		prot_transdata.h transdata_application.h transdata_tracker.h \
		transdata_datalink.h prot_ymodem.h transdata_data.h msg_cmd.h \
		dev_repeater800.h dev_repeater.h dev_rtc.h tbl_dv_rtc.h \
		dev_sixnet.h prot_sixnet.h rte_macro.h tbl_rtmacro.h \
		rte_ccu.h rte_xcu.h smartmap.h tbl_rtcarrier.h \
		tbl_rtrepeater.h rte_versacom.h tbl_rtversacom.h master.h \
		slctdev.h
slctpnt.obj:	yukon.h precompiled.h ctidbgmem.h rtdb.h dlldefs.h \
		hashkey.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h slctpnt.h pt_base.h dbmemobject.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h desolvers.h
slctprt.obj:	yukon.h precompiled.h ctidbgmem.h port_dialout.h dsm2.h \
		mutex.h dlldefs.h guard.h port_base.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h types.h cticalls.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h tbl_port_base.h xfer.h dialup.h \
		tbl_port_statistics.h port_dialable.h port_modem.h \
		tbl_port_dialup.h port_dialin.h port_direct.h port_serial.h \
		tbl_port_settings.h tbl_port_timing.h tbl_port_serial.h \
		port_pool_out.h port_tcpip.h tbl_port_tcpip.h tcpsup.h \
		devicetypes.h slctprt.h pt_base.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h
tcpsup.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h porter.h \
		dsm2err.h devicetypes.h tcpsup.h ctinexus.h \
		c_port_interface.h group.h elogger.h alarmlog.h rtdb.h \
		hashkey.h dllbase.h port_base.h dev_base.h cmdparse.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h dbmemobject.h ctibase.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h utility.h \
		tbl_port_base.h xfer.h dialup.h tbl_port_statistics.h
#ENDUPDATE#
