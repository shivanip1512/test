include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
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



TESTOBJS=\
porttest.obj

RTPTTESTOBJS=\
rtpointtest.obj

PTTESTOBJS=\
pointtest.obj

RTESTOBJS=\
routetest.obj

DEVTESTOBJS=\
devtest.obj


CTIPROGS=\
porttest.exe \
pointtest.exe \
routetest.exe \
devtest.exe \
memtest.exe


ALL:            $(CTIPROGS)

porttest.exe:   $(TESTOBJS) makeexe.mak
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(RWLINKFLAGS) $(INCLPATHS) -o ..\$@ \
$(TESTOBJS) \
-link $(COMPILEBASE)\lib\cticparms.lib $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\ctiprtdb.lib $(COMPILEBASE)\lib\ctisvr.lib $(COMPILEBASE)\lib\clrdump.lib $(RWLIBS) $(BOOSTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

rtpttest.exe:  $(RTPTTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(RWLINKFLAGS) $(INCLPATHS) -o ..\$@ \
$(RTPTTESTOBJS) \
-link $(COMPILEBASE)\lib\cticparms.lib $(COMPILEBASE)\lib\ctisvr.lib $(RWLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

pointtest.exe:  $(PTTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ $(PTTESTOBJS) \
-link $(COMPILEBASE)\lib\cticparms.lib $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\clrdump.lib $(COMPILEBASE)\lib\ctisvr.lib $(COMPILEBASE)\lib\ctidbsrc.lib $(COMPILEBASE)\lib\ctipntdb.lib  $(RWLIBS) $(BOOSTLIBS) 
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

devtest.exe:    $(DEVTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ $(DEVTESTOBJS) \
-link $(COMPILEBASE)\lib\cticparms.lib $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\clrdump.lib $(COMPILEBASE)\lib\ctisvr.lib $(COMPILEBASE)\lib\ctidbsrc.lib $(COMPILEBASE)\lib\ctidevdb.lib  $(RWLIBS) $(BOOSTLIBS) 
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

memtest.exe:    memtest.obj makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ memtest.obj \
-link $(COMPILEBASE)\lib\cticparms.lib $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\clrdump.lib  $(RWLIBS) $(BOOSTLIBS) 
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

routetest.exe:   $(RTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ $(RTESTOBJS) \
-link $(COMPILEBASE)\lib\cticparms.lib $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\clrdump.lib $(COMPILEBASE)\lib\ctisvr.lib $(COMPILEBASE)\lib\ctidbsrc.lib $(COMPILEBASE)\lib\ctidevdb.lib  $(RWLIBS) $(BOOSTLIBS) 
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

copy:           $(CTIPROGS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
                scandeps -Output makeexe.mak *.cpp



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
device.obj:	yukon.h precompiled.h ctidbgmem.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h dlldefs.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h ctitime.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h dev_remote.h \
		tbl_dialup.h tbl_direct.h dev_meter.h dev_ied.h ctitypes.h \
		tbl_dv_ied.h
device_queue_interface.obj:	yukon.h precompiled.h ctidbgmem.h \
		trx_711.h trx_info.h logger.h dlldefs.h thread.h mutex.h \
		guard.h numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h types.h sorted_vector.h dsm2.h \
		cticonnect.h netports.h porter.h dsm2err.h devicetypes.h \
		device_queue_interface.h
devtest.obj:	yukon.h precompiled.h ctidbgmem.h hashkey.h \
		hash_functions.h dlldefs.h mgr_device.h rtdb.h utility.h \
		ctitime.h queues.h types.h numstr.h sorted_vector.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h slctdev.h \
		smartmap.h
dev_710.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h dev_710.h dev_idlc.h \
		ctitypes.h types.h os2_2w32.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dev_remote.h dev_single.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		ctitime.h config_device.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h dllbase.h cticalls.h \
		hashkey.h hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		porter.h dsm2err.h devicetypes.h trx_711.h cti_asmc.h \
		master.h device.h prot_711.h
dev_a1.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dsm2err.h devicetypes.h queues.h types.h dev_alpha.h \
		ctitypes.h os2_2w32.h dev_meter.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h cticalls.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h device.h \
		dev_a1.h pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_trace.h dupreq.h \
		ctidate.h
dev_alpha.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dsm2err.h devicetypes.h queues.h types.h \
		dev_alpha.h ctitypes.h os2_2w32.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dllbase.h cticalls.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h device.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h
dev_aplus.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dsm2err.h devicetypes.h queues.h types.h \
		dev_alpha.h ctitypes.h os2_2w32.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dllbase.h cticalls.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h device.h \
		dev_aplus.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_trace.h dupreq.h \
		ctidate.h
dev_base.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dev_base.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h dllbase.h os2_2w32.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h mgr_route.h repeaterrole.h \
		rtdb.h slctdev.h smartmap.h mgr_point.h slctpnt.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		porter.h dsm2err.h devicetypes.h
dev_base_lite.obj:	yukon.h precompiled.h ctidbgmem.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h dev_base_lite.h dbaccess.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		dbmemobject.h
dev_carrier.obj:	yukon.h precompiled.h ctidbgmem.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h dlldefs.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h ctitime.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h
dev_cbc.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		porter.h dsm2err.h devicetypes.h queues.h types.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dbaccess.h sema.h desolvers.h tbl_pt_trigger.h \
		master.h mgr_route.h repeaterrole.h rte_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h tbl_pao.h \
		tbl_rtcomm.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		sorted_vector.h msg_signal.h rtdb.h hashkey.h \
		hash_functions.h slctdev.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		smartmap.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h dev_cbc.h tbl_dv_cbc.h device.h cparms.h \
		configkey.h configval.h
dev_cbc6510.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		porter.h dsm2err.h devicetypes.h queues.h types.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dbaccess.h sema.h desolvers.h tbl_pt_trigger.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		master.h mgr_route.h repeaterrole.h rte_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h tbl_pao.h \
		tbl_rtcomm.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		sorted_vector.h msg_signal.h rtdb.h hashkey.h \
		hash_functions.h slctdev.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		smartmap.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h dev_cbc6510.h dev_remote.h dev_single.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h dev_dnp.h prot_dnp.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h \
		tbl_dv_address.h tbl_dv_idlcremote.h device.h
dev_cbc7020.obj:	yukon.h precompiled.h ctidbgmem.h numstr.h dlldefs.h \
		ctistring.h rwutil.h ctitime.h boost_time.h dev_cbc7020.h \
		dev_dnp.h dev_remote.h dev_single.h dsm2.h mutex.h guard.h \
		clrdump.h cticonnect.h netports.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		dllbase.h os2_2w32.h cticalls.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h prot_dnp.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h \
		tbl_dv_address.h config_data_cbc.h
dev_ccu.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h porter.h dsm2err.h \
		devicetypes.h queues.h types.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h cticalls.h \
		dbaccess.h sema.h desolvers.h tbl_pt_trigger.h master.h \
		dev_ccu.h ctitypes.h dev_idlc.h dev_remote.h dev_single.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		ctitime.h config_device.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h sorted_vector.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		device_queue_interface.h device.h prot_711.h
dev_ccu721.obj:	yukon.h precompiled.h ctidbgmem.h dev_ccu721.h \
		dev_remote.h dev_single.h dsm2.h mutex.h dlldefs.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h ctitime.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_address.h prot_klondike.h \
		prot_wrap.h prot_idlc.h porter.h dsm2err.h devicetypes.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		pt_status.h tbl_pt_status.h dllyukon.h
dev_davis.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		dev_davis.h dev_ied.h ctitypes.h dev_remote.h dev_single.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h msg_cmd.h \
		msg_lmcontrolhistory.h porter.h dsm2err.h devicetypes.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h
dev_dct501.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h tbl_ptdispatch.h ctibase.h ctinexus.h \
		netports.h cticonnect.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		dbmemobject.h pointdefs.h ctitime.h dev_dct501.h dev_mct24x.h \
		dev_mct2xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h hashkey.h hash_functions.h \
		rte_base.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h porter.h \
		dsm2err.h
dev_dlcbase.obj:	yukon.h precompiled.h ctidbgmem.h dev_dlcbase.h \
		dev_single.h dsm2.h mutex.h dlldefs.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h dev_mct.h \
		dev_carrier.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h devicetypes.h msg_cmd.h \
		porter.h dsm2err.h
dev_dnp.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		porter.h dsm2err.h devicetypes.h queues.h types.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dbaccess.h sema.h desolvers.h tbl_pt_trigger.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		pt_status.h tbl_pt_status.h pt_accum.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h sorted_vector.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h master.h dllyukon.h \
		mgr_route.h repeaterrole.h rte_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		msg_signal.h rtdb.h hashkey.h hash_functions.h slctdev.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h smartmap.h msg_cmd.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		msg_lmcontrolhistory.h dev_dnp.h dev_remote.h dev_single.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h prot_dnp.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h device.h
dev_dr87.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h porter.h dsm2.h cticonnect.h netports.h \
		dsm2err.h devicetypes.h dev_ied.h ctitypes.h os2_2w32.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h dllbase.h cticalls.h \
		hashkey.h hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_dr87.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_exclusion.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h cparms.h \
		rwutil.h ctitime.h boost_time.h configkey.h configval.h \
		dev_exclusion.h tbl_paoexclusion.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
dev_fmu.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dsm2err.h dev_fmu.h dev_ied.h ctitypes.h types.h os2_2w32.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h dllbase.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h verification_objects.h msg_cmd.h porter.h \
		devicetypes.h ctistring.h
dev_foreignporter.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h devicetypes.h numstr.h dev_foreignporter.h \
		dev_remote.h dev_single.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h dllbase.h os2_2w32.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h
dev_fulcrum.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h porter.h dsm2.h cticonnect.h netports.h \
		dsm2err.h devicetypes.h dev_schlum.h ctitypes.h os2_2w32.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		cticalls.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_fulcrum.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_gateway.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dev_gateway.h ctitypes.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_gwstat.h dev_ied.h os2_2w32.h \
		dsm2.h cticonnect.h netports.h dev_remote.h dev_single.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h dllbase.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h gateway.h \
		pending_stat_operation.h
dev_gridadvisor.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dsm2err.h devicetypes.h queues.h types.h \
		tbl_ptdispatch.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		cticalls.h dbmemobject.h pointdefs.h ctitime.h \
		dev_gridadvisor.h dev_dnp.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h message.h collectable.h rwutil.h \
		boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h prot_dnp.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		msg_lmcontrolhistory.h dllyukon.h
dev_grp_emetcon.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		porter.h dsm2err.h devicetypes.h queues.h types.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dbaccess.h sema.h desolvers.h tbl_pt_trigger.h \
		pt_status.h tbl_pt_status.h master.h connection.h exchange.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		sorted_vector.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_route.h repeaterrole.h \
		rte_base.h cmdparse.h ctitokenizer.h parsevalue.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h msg_signal.h rtdb.h \
		hashkey.h hash_functions.h slctdev.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		smartmap.h msg_pcrequest.h msg_pcreturn.h dev_grp_emetcon.h \
		dev_grp.h msg_lmcontrolhistory.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h tbl_dv_emetcon.h
dev_grp_energypro.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h dev_grp_energypro.h \
		dev_base.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h dev_grp.h cparms.h configkey.h \
		configval.h msg_lmcontrolhistory.h msg_pcrequest.h \
		msg_pdata.h msg_multi.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h tbl_dv_expresscom.h vcomdefs.h \
		expresscom.h ctistring.h mgr_route.h repeaterrole.h rtdb.h \
		slctdev.h smartmap.h msg_pcreturn.h
dev_grp_expresscom.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h dev_grp_expresscom.h \
		dev_base.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h dev_grp.h cparms.h configkey.h \
		configval.h msg_lmcontrolhistory.h msg_pcrequest.h \
		msg_pdata.h msg_multi.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h tbl_dv_expresscom.h vcomdefs.h \
		expresscom.h ctistring.h mgr_route.h repeaterrole.h rtdb.h \
		slctdev.h smartmap.h msg_pcreturn.h devicetypes.h
dev_grp_golay.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h dev_grp_golay.h \
		dev_base.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h dev_grp.h cparms.h configkey.h \
		configval.h msg_lmcontrolhistory.h msg_pcrequest.h \
		msg_pdata.h msg_multi.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h prot_sasimple.h prot_base.h \
		xfer.h dialup.h tbl_lmg_sasimple.h expresscom.h ctistring.h \
		mgr_route.h repeaterrole.h rtdb.h slctdev.h smartmap.h \
		msg_pcreturn.h
dev_grp_mct.obj:	yukon.h precompiled.h ctidbgmem.h dev_grp_mct.h \
		dev_grp.h cparms.h rwutil.h ctitime.h dlldefs.h boost_time.h \
		configkey.h configval.h msg_lmcontrolhistory.h pointdefs.h \
		message.h collectable.h msg_pcrequest.h msg_signal.h \
		msg_pdata.h pointtypes.h msg_multi.h pt_status.h pt_base.h \
		dbmemobject.h resolvers.h types.h db_entry_defines.h \
		pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dbaccess.h sema.h desolvers.h tbl_pt_trigger.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h tbl_pao.h \
		tbl_rtcomm.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h tbl_dv_lmgmct.h msg_pcreturn.h porter.h \
		dsm2err.h devicetypes.h prot_emetcon.h
dev_grp_point.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h cparms.h rwutil.h \
		ctitime.h boost_time.h configkey.h configval.h \
		dev_grp_point.h dev_base.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		dllbase.h os2_2w32.h cticalls.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h dev_grp.h \
		msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h tbl_lmg_point.h mgr_route.h repeaterrole.h \
		rtdb.h slctdev.h smartmap.h msg_pcreturn.h
dev_grp_ripple.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h dev_grp_ripple.h dev_base.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		dllbase.h os2_2w32.h cticalls.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h dev_grp.h \
		msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h tbl_dv_lmg_ripple.h mgr_route.h \
		repeaterrole.h rtdb.h slctdev.h smartmap.h msg_pcreturn.h \
		msg_cmd.h porter.h dsm2err.h devicetypes.h device.h
dev_grp_sa105.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h cparms.h rwutil.h \
		ctitime.h boost_time.h configkey.h configval.h \
		dev_grp_sa105.h dev_base.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		dllbase.h os2_2w32.h cticalls.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h dev_grp.h \
		msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h prot_sa105.h prot_base.h xfer.h dialup.h \
		tbl_lmg_sa205105.h expresscom.h ctistring.h mgr_route.h \
		repeaterrole.h rtdb.h slctdev.h smartmap.h msg_pcreturn.h
dev_grp_sa205.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_grp_sa205.h \
		dev_base.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		dllbase.h os2_2w32.h cticalls.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h dev_grp.h \
		msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h prot_sa205.h prot_base.h xfer.h dialup.h \
		tbl_lmg_sa205105.h expresscom.h ctistring.h mgr_route.h \
		repeaterrole.h rtdb.h slctdev.h smartmap.h msg_pcreturn.h \
		prot_sa3rdparty.h protocol_sa.h
dev_grp_sa305.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_grp_sa305.h \
		dev_base.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		dllbase.h os2_2w32.h cticalls.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h dev_grp.h \
		msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h prot_sa305.h tbl_lmg_sa305.h expresscom.h \
		ctistring.h mgr_route.h repeaterrole.h rtdb.h slctdev.h \
		smartmap.h msg_pcreturn.h
dev_grp_sadigital.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h dev_grp_sadigital.h \
		dev_base.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h dev_grp.h cparms.h configkey.h \
		configval.h msg_lmcontrolhistory.h msg_pcrequest.h \
		msg_pdata.h msg_multi.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h prot_sasimple.h prot_base.h \
		xfer.h dialup.h tbl_lmg_sasimple.h expresscom.h ctistring.h \
		mgr_route.h repeaterrole.h rtdb.h slctdev.h smartmap.h \
		msg_pcreturn.h
dev_grp_versacom.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		porter.h dsm2err.h devicetypes.h queues.h types.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dbaccess.h sema.h desolvers.h tbl_pt_trigger.h \
		pt_status.h tbl_pt_status.h master.h connection.h exchange.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		sorted_vector.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_route.h repeaterrole.h \
		rte_base.h cmdparse.h ctitokenizer.h parsevalue.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h msg_signal.h rtdb.h \
		hashkey.h hash_functions.h slctdev.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		smartmap.h msg_pcrequest.h msg_pcreturn.h dev_grp_versacom.h \
		dev_grp.h msg_lmcontrolhistory.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h tbl_dv_versacom.h vcomdefs.h
dev_gwstat.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dev_gwstat.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctitypes.h dev_ied.h types.h os2_2w32.h dev_remote.h \
		dev_single.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		dllbase.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h gateway.h \
		pending_stat_operation.h devicetypes.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_gateway_end_device.h ctidate.h
dev_ilex.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		porter.h dsm2err.h devicetypes.h queues.h types.h device.h \
		dev_ilex.h ctitypes.h os2_2w32.h dev_idlc.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		ctitime.h config_device.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h sorted_vector.h dllbase.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_lmcontrolhistory.h
dev_ion.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dsm2err.h devicetypes.h queues.h types.h tbl_ptdispatch.h \
		ctibase.h ctinexus.h dllbase.h os2_2w32.h cticalls.h \
		dbmemobject.h pointdefs.h ctitime.h dev_ion.h ctitypes.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h message.h collectable.h rwutil.h \
		boost_time.h tbl_pao.h tbl_rtcomm.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h tbl_dv_ied.h tbl_dv_address.h prot_ion.h \
		ion_datastream.h ion_value.h ion_serializable.h \
		ion_value_fixed.h ion_value_numeric.h ion_value_variable.h \
		ion_value_variable_fixedarray.h \
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
		msg_lmcontrolhistory.h dllyukon.h
dev_kv2.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dsm2err.h devicetypes.h queues.h types.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h sorted_vector.h dev_kv2.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		os2_2w32.h cticalls.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h dev_ied.h ctitypes.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h prot_ansi_kv2.h \
		prot_ansi.h ansi_application.h ansi_datalink.h \
		ansi_billing_table.h std_ansi_tbl_base.h \
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
		device.h dllyukon.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h pt_status.h \
		tbl_pt_status.h
dev_lcu.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cmdparse.h ctitokenizer.h parsevalue.h \
		device.h devicetypes.h dev_lcu.h ctitypes.h dev_idlc.h \
		dev_remote.h dev_single.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		porter.h dsm2err.h trx_711.h master.h pt_accum.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h elogger.h
dev_lgs4.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dsm2err.h devicetypes.h queues.h types.h \
		dev_lgs4.h ctitypes.h os2_2w32.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dllbase.h cticalls.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h dupreq.h \
		ctidate.h
dev_lmi.obj:	yukon.h precompiled.h ctidbgmem.h dev_lmi.h dev_remote.h \
		dev_single.h dsm2.h mutex.h dlldefs.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_address.h tbl_dv_seriesv.h \
		prot_lmi.h prot_seriesv.h verification_objects.h porter.h \
		dsm2err.h devicetypes.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h pt_status.h tbl_pt_status.h \
		dllyukon.h
dev_macro.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		devicetypes.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h msg_pcrequest.h msg_pdata.h pointdefs.h \
		pointtypes.h dllbase.h os2_2w32.h types.h cticalls.h \
		dev_macro.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h dev_grp.h cparms.h configkey.h configval.h \
		msg_lmcontrolhistory.h msg_multi.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		dev_grp_ripple.h tbl_dv_lmg_ripple.h
dev_mark_v.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		porter.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dsm2err.h devicetypes.h queues.h \
		types.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		sorted_vector.h dev_mark_v.h prot_transdata.h \
		transdata_application.h xfer.h dialup.h transdata_tracker.h \
		transdata_datalink.h prot_ymodem.h dllbase.h os2_2w32.h \
		cticalls.h ctidate.h transdata_data.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		ctitypes.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h desolvers.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h device.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h msg_cmd.h
dev_mct.obj:	yukon.h precompiled.h ctidbgmem.h numstr.h dlldefs.h \
		devicetypes.h device.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dsm2.h mutex.h guard.h clrdump.h cticonnect.h \
		netports.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h ctitime.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h dllbase.h os2_2w32.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h dev_mct210.h dev_mct2xx.h \
		dev_mct31x.h dev_mct310.h dev_mct410.h dev_mct4xx.h \
		config_data_mct.h dev_mct470.h dev_mct_lmt2.h dev_mct22x.h \
		msg_cmd.h pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		pt_status.h tbl_pt_status.h porter.h dsm2err.h dllyukon.h \
		ctidate.h
dev_mct210.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_mct210.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h ctitime.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h porter.h \
		dsm2err.h
dev_mct22x.obj:	yukon.h precompiled.h ctidbgmem.h devicetypes.h \
		dev_mct22X.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h dlldefs.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h ctitime.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h porter.h \
		dsm2err.h
dev_mct24x.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_mct24X.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h ctitime.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h porter.h \
		dsm2err.h dllyukon.h
dev_mct2xx.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_mct2XX.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h porter.h \
		dsm2err.h
dev_mct310.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_mct310.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h pt_status.h \
		tbl_pt_status.h porter.h dsm2err.h dllyukon.h
dev_mct31x.obj:	yukon.h precompiled.h ctidbgmem.h devicetypes.h \
		tbl_ptdispatch.h ctibase.h ctinexus.h dlldefs.h netports.h \
		cticonnect.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h dbmemobject.h pointdefs.h \
		ctitime.h dev_mct31X.h dev_mct310.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h message.h collectable.h rwutil.h \
		boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h dllyukon.h \
		ctidate.h
dev_mct410.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		dllyukon.h dev_base.h dsm2.h cticonnect.h netports.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h dllbase.h \
		os2_2w32.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h dev_mct410.h \
		dev_mct4xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		config_data_mct.h tbl_ptdispatch.h pt_status.h \
		tbl_pt_status.h portglob.h tcpsup.h porter.h dsm2err.h \
		devicetypes.h statistics.h ctidate.h
dev_mct470.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h tbl_ptdispatch.h ctibase.h ctinexus.h \
		netports.h cticonnect.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		dbmemobject.h pointdefs.h ctitime.h dev_mct470.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		hashkey.h hash_functions.h rte_base.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		config_data_mct.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h pt_status.h tbl_pt_status.h porter.h \
		dsm2err.h dllyukon.h ctistring.h
dev_mct4xx.obj:	yukon.h precompiled.h ctidbgmem.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h ctitime.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h dllbase.h os2_2w32.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h config_data_mct.h \
		dev_mct470.h dev_mct410.h ctistring.h pt_status.h \
		tbl_pt_status.h dllyukon.h ctidate.h
dev_mct_broadcast.obj:	yukon.h precompiled.h ctidbgmem.h \
		dev_mct_broadcast.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h ctitime.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h porter.h dsm2err.h \
		devicetypes.h dev_mct.h dev_carrier.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h dev_mct31x.h \
		dev_mct310.h dev_mct4xx.h config_data_mct.h ctidate.h
dev_mct_lmt2.obj:	yukon.h precompiled.h ctidbgmem.h devicetypes.h \
		dev_mct_lmt2.h dev_mct22x.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h ctitime.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h
dev_meter.obj:	yukon.h precompiled.h ctidbgmem.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dlldefs.h dbmemobject.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		ctitypes.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		hashkey.h hash_functions.h rte_base.h ctibase.h ctinexus.h \
		message.h collectable.h rwutil.h boost_time.h tbl_pao.h \
		tbl_rtcomm.h desolvers.h msg_signal.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h tbl_dv_ied.h
dev_modbus.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		porter.h dsm2err.h devicetypes.h queues.h types.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dbaccess.h sema.h desolvers.h tbl_pt_trigger.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		pt_status.h tbl_pt_status.h pt_analog.h tbl_pt_analog.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		sorted_vector.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h master.h dllyukon.h mgr_route.h \
		repeaterrole.h rte_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h msg_signal.h \
		rtdb.h hashkey.h hash_functions.h slctdev.h dev_base.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h smartmap.h mgr_point.h slctpnt.h msg_cmd.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		msg_lmcontrolhistory.h dev_modbus.h dev_remote.h dev_single.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h prot_modbus.h tbl_dv_address.h device.h
dev_pagingreceiver.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dsm2err.h dev_rtm.h dev_ied.h \
		ctitypes.h types.h os2_2w32.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		dllbase.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h verification_objects.h porter.h devicetypes.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		port_base.h tbl_port_base.h critical_section.h \
		tbl_port_statistics.h mgr_route.h repeaterrole.h rtdb.h \
		slctdev.h smartmap.h msg_trace.h dev_pagingreceiver.h \
		tbl_dv_pagingreceiver.h
dev_quantum.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h porter.h dsm2.h cticonnect.h netports.h \
		dsm2err.h devicetypes.h dev_schlum.h ctitypes.h os2_2w32.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		cticalls.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_quantum.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_repeater.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_repeater.h dev_dlcbase.h \
		dev_single.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		ctitime.h config_device.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h types.h sorted_vector.h dllbase.h \
		os2_2w32.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h porter.h \
		dsm2err.h
dev_repeater800.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h dev_repeater800.h dev_repeater.h \
		dev_dlcbase.h dev_single.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h porter.h \
		dsm2err.h
dev_rtc.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dsm2err.h dev_rtc.h dev_remote.h dev_single.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_rtc.h msg_cmd.h msg_lmcontrolhistory.h porter.h \
		devicetypes.h protocol_sa.h prot_sa305.h prot_sa3rdparty.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		pt_status.h tbl_pt_status.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h verification_objects.h
dev_rtm.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dsm2err.h dev_rtm.h dev_ied.h ctitypes.h types.h os2_2w32.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h dllbase.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h verification_objects.h msg_cmd.h porter.h \
		devicetypes.h protocol_sa.h prot_sa3rdparty.h
dev_schlum.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dsm2err.h devicetypes.h queues.h types.h \
		dev_schlum.h ctitypes.h os2_2w32.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dllbase.h cticalls.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_sentinel.obj:	yukon.h precompiled.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dsm2err.h devicetypes.h queues.h types.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h sorted_vector.h \
		dev_sentinel.h dev_meter.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h os2_2w32.h cticalls.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		ctitypes.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h prot_ansi_sentinel.h \
		prot_ansi.h ansi_application.h ansi_datalink.h \
		ansi_billing_table.h std_ansi_tbl_base.h \
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
		std_ansi_tbl_six_four.h device.h dllyukon.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_status.h tbl_pt_status.h ctidate.h
dev_seriesv.obj:	yukon.h precompiled.h ctidbgmem.h dev_seriesv.h \
		dev_ied.h ctitypes.h types.h os2_2w32.h dlldefs.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		dllbase.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		prot_seriesv.h porter.h dsm2err.h devicetypes.h
dev_single.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dev_single.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h dllbase.h os2_2w32.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h porter.h dsm2err.h devicetypes.h \
		tbl_ptdispatch.h ctidate.h
dev_sixnet.obj:	yukon.h precompiled.h ctidbgmem.h dev_sixnet.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h dev_ied.h \
		ctitypes.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		hashkey.h hash_functions.h rte_base.h ctibase.h ctinexus.h \
		message.h collectable.h rwutil.h boost_time.h tbl_pao.h \
		tbl_rtcomm.h desolvers.h msg_signal.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h tbl_dv_ied.h prot_sixnet.h msg_cmd.h porter.h \
		dsm2err.h devicetypes.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h
dev_snpp.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h porter.h dsm2err.h \
		devicetypes.h cmdparse.h ctitokenizer.h parsevalue.h \
		pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dbaccess.h sema.h desolvers.h \
		tbl_pt_trigger.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h port_base.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		tbl_port_base.h xfer.h dialup.h critical_section.h \
		tbl_port_statistics.h connection.h exchange.h msg_multi.h \
		msg_pdata.h msg_ptreg.h msg_reg.h queue.h mgr_route.h \
		repeaterrole.h rtdb.h slctdev.h smartmap.h msg_pcrequest.h \
		msg_pcreturn.h msg_trace.h verification_objects.h dev_snpp.h \
		tbl_dv_tappaging.h dev_ied.h ctitypes.h dev_remote.h \
		dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h
dev_system.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dev_system.h dev_base.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h dllbase.h os2_2w32.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h connection.h exchange.h msg_ptreg.h \
		msg_reg.h queue.h prot_versacom.h rte_xcu.h smartmap.h \
		porter.h dsm2err.h devicetypes.h mgr_route.h repeaterrole.h \
		rtdb.h slctdev.h
dev_tap.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h porter.h dsm2err.h \
		devicetypes.h cmdparse.h ctitokenizer.h parsevalue.h \
		pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dbaccess.h sema.h desolvers.h \
		tbl_pt_trigger.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h mgr_route.h repeaterrole.h rte_base.h ctibase.h \
		ctinexus.h tbl_pao.h tbl_rtcomm.h msg_signal.h rtdb.h \
		hashkey.h hash_functions.h slctdev.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		smartmap.h msg_pcrequest.h msg_pcreturn.h msg_trace.h \
		verification_objects.h dev_tap.h tbl_dv_tappaging.h dev_ied.h \
		ctitypes.h dev_remote.h dev_single.h tbl_dv_scandata.h \
		tbl_dv_wnd.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h tbl_dv_ied.h
dev_tcu.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h cparms.h rwutil.h \
		ctitime.h boost_time.h configkey.h configval.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h porter.h \
		dsm2err.h devicetypes.h queues.h types.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dbaccess.h sema.h desolvers.h tbl_pt_trigger.h \
		master.h dev_tcu.h ctitypes.h dev_idlc.h dev_remote.h \
		dev_single.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h device.h
dev_tnpp.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dsm2err.h dev_rtm.h dev_ied.h ctitypes.h types.h \
		os2_2w32.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h dllbase.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h verification_objects.h porter.h devicetypes.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		port_base.h tbl_port_base.h critical_section.h \
		tbl_port_statistics.h mgr_route.h repeaterrole.h rtdb.h \
		slctdev.h smartmap.h msg_trace.h dev_tnpp.h tbl_dv_tnpp.h
dev_vectron.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h porter.h dsm2.h cticonnect.h netports.h \
		dsm2err.h devicetypes.h dev_schlum.h ctitypes.h os2_2w32.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		cticalls.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_vectron.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h dupreq.h
dev_wctp.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h porter.h dsm2err.h \
		devicetypes.h pt_base.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h dllbase.h os2_2w32.h cticalls.h dbaccess.h \
		sema.h desolvers.h tbl_pt_trigger.h pt_accum.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h mgr_route.h repeaterrole.h rte_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h tbl_pao.h \
		tbl_rtcomm.h msg_signal.h rtdb.h hashkey.h hash_functions.h \
		slctdev.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h smartmap.h \
		msg_pcrequest.h msg_pcreturn.h msg_trace.h dev_wctp.h \
		tbl_dv_tappaging.h dev_ied.h ctitypes.h dev_remote.h \
		dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		xfer.h dialup.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		verification_objects.h ctidate.h
dev_welco.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h dllyukon.h porter.h dsm2err.h devicetypes.h \
		queues.h types.h device.h dev_welco.h ctitypes.h os2_2w32.h \
		dev_idlc.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h dllbase.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h prot_welco.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_lmcontrolhistory.h
disable_entry.obj:	yukon.h precompiled.h ctidbgmem.h disable_entry.h \
		ctistring.h rwutil.h ctitime.h dlldefs.h boost_time.h
dlldev.obj:	yukon.h precompiled.h ctidbgmem.h mgr_device.h dlldefs.h \
		rtdb.h hashkey.h hash_functions.h utility.h ctitime.h \
		queues.h types.h numstr.h sorted_vector.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h slctdev.h \
		smartmap.h mgr_route.h repeaterrole.h mgr_point.h slctpnt.h \
		devicetypes.h msg_pcrequest.h rte_xcu.h rte_macro.h \
		tbl_rtmacro.h dev_dlcbase.h dev_single.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h dev_grp_emetcon.h \
		dev_grp.h msg_lmcontrolhistory.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h tbl_dv_emetcon.h
id_devdll.obj:	yukon.h precompiled.h ctidbgmem.h id_devdll.h utility.h \
		ctitime.h dlldefs.h queues.h types.h numstr.h sorted_vector.h \
		id_vinfo.h
id_pntdll.obj:	yukon.h precompiled.h ctidbgmem.h id_pntdll.h utility.h \
		ctitime.h dlldefs.h queues.h types.h numstr.h sorted_vector.h \
		id_vinfo.h
id_prtdll.obj:	yukon.h precompiled.h ctidbgmem.h id_prtdll.h utility.h \
		ctitime.h dlldefs.h queues.h types.h numstr.h sorted_vector.h \
		id_vinfo.h
id_tcpdll.obj:	yukon.h precompiled.h ctidbgmem.h id_tcpdll.h utility.h \
		ctitime.h dlldefs.h queues.h types.h numstr.h sorted_vector.h \
		id_vinfo.h
memtest.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h
mgr_config.obj:	yukon.h precompiled.h ctidbgmem.h mgr_config.h \
		dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h mgr_device.h rtdb.h hashkey.h \
		hash_functions.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h rte_base.h dbmemobject.h ctibase.h ctinexus.h \
		message.h collectable.h rwutil.h boost_time.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h slctdev.h smartmap.h
mgr_device.obj:	yukon.h precompiled.h ctidbgmem.h rtdb.h dlldefs.h \
		hashkey.h hash_functions.h utility.h ctitime.h queues.h \
		types.h numstr.h sorted_vector.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h clrdump.h cticonnect.h \
		netports.h mgr_device.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h slctdev.h \
		smartmap.h cparms.h configkey.h configval.h dev_macro.h \
		dev_grp.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h dev_cbc.h tbl_dv_cbc.h dev_dnp.h dev_remote.h \
		dev_single.h msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h xfer.h dialup.h tbl_dialup.h tbl_direct.h \
		prot_dnp.h dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h dev_ion.h \
		ctitypes.h dev_meter.h tbl_metergrp.h vcomdefs.h dev_ied.h \
		tbl_dv_ied.h prot_ion.h ion_datastream.h ion_value.h \
		ion_serializable.h ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable.h ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h dev_gridadvisor.h \
		dev_idlc.h tbl_dv_idlcremote.h trx_info.h porter.h dsm2err.h \
		devicetypes.h trx_711.h dev_ccu721.h prot_klondike.h \
		prot_wrap.h prot_idlc.h dev_carrier.h dev_dlcbase.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h dev_lmi.h tbl_dv_seriesv.h prot_lmi.h \
		prot_seriesv.h verification_objects.h dev_mct.h dev_mct410.h \
		dev_mct4xx.h config_data_mct.h dev_modbus.h prot_modbus.h \
		dev_repeater.h dev_rtc.h tbl_dv_rtc.h dev_rtm.h dev_fmu.h \
		dev_tap.h tbl_dv_tappaging.h dev_snpp.h dev_tnpp.h \
		tbl_dv_tnpp.h dev_pagingreceiver.h tbl_dv_pagingreceiver.h \
		dev_grp_emetcon.h tbl_dv_emetcon.h dev_grp_energypro.h \
		tbl_dv_expresscom.h dev_grp_expresscom.h dev_grp_golay.h \
		prot_sasimple.h tbl_lmg_sasimple.h dev_grp_point.h \
		tbl_lmg_point.h dev_grp_ripple.h tbl_dv_lmg_ripple.h \
		dev_grp_sa105.h prot_sa105.h tbl_lmg_sa205105.h \
		dev_grp_sa305.h prot_sa305.h tbl_lmg_sa305.h dev_grp_sa205.h \
		prot_sa205.h dev_grp_sadigital.h dev_grp_versacom.h \
		tbl_dv_versacom.h dev_grp_mct.h tbl_dv_lmgmct.h \
		dev_mct_broadcast.h
mgr_disabled.obj:	yukon.h precompiled.h ctidbgmem.h mgr_disabled.h \
		dlldefs.h disable_entry.h ctistring.h rwutil.h ctitime.h \
		boost_time.h msg_dbchg.h message.h collectable.h smartmap.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h hashkey.h \
		hash_functions.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h dbaccess.h sema.h
mgr_exclusion.obj:	yukon.h precompiled.h ctidbgmem.h mgr_exclusion.h \
		dlldefs.h dev_base.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		ctitime.h config_device.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h types.h sorted_vector.h dllbase.h \
		os2_2w32.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h smartmap.h
mgr_holiday.obj:	yukon.h precompiled.h ctidbgmem.h mgr_holiday.h \
		ctidate.h dlldefs.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h cticonnect.h netports.h sema.h
mgr_point.obj:	yukon.h precompiled.h ctidbgmem.h pt_base.h \
		dbmemobject.h dlldefs.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dbaccess.h sema.h \
		desolvers.h tbl_pt_trigger.h mgr_point.h smartmap.h hashkey.h \
		hash_functions.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h slctpnt.h devicetypes.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		pt_analog.h tbl_pt_analog.h pt_status.h tbl_pt_status.h \
		tbl_pt_alarm.h rwutil.h boost_time.h cparms.h configkey.h \
		configval.h
mgr_port.obj:	yukon.h precompiled.h ctidbgmem.h mgr_port.h dlldefs.h \
		smartmap.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		hashkey.h hash_functions.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		port_base.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		slctprt.h port_direct.h port_serial.h tbl_port_settings.h \
		tbl_port_timing.h port_dialable.h port_modem.h \
		tbl_port_dialup.h tbl_port_serial.h port_dialout.h \
		port_pool_out.h port_tcpip.h tbl_port_tcpip.h tcpsup.h
mgr_route.obj:	yukon.h precompiled.h ctidbgmem.h mgr_route.h \
		repeaterrole.h dlldefs.h rte_base.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dbmemobject.h \
		cmdparse.h ctitokenizer.h parsevalue.h ctibase.h ctinexus.h \
		dllbase.h os2_2w32.h types.h cticalls.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		msg_signal.h rtdb.h hashkey.h hash_functions.h slctdev.h \
		dev_base.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h smartmap.h \
		rte_xcu.h msg_pcrequest.h rte_ccu.h tbl_rtcarrier.h \
		tbl_rtrepeater.h rte_versacom.h tbl_rtversacom.h master.h \
		dev_remote.h dev_single.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h rte_expresscom.h rte_macro.h tbl_rtmacro.h
mgr_season.obj:	yukon.h precompiled.h ctidbgmem.h mgr_season.h \
		ctidate.h dlldefs.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h cticonnect.h netports.h sema.h
points.obj:	yukon.h precompiled.h ctidbgmem.h pt_accum.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h pt_numeric.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h dbaccess.h sema.h desolvers.h \
		tbl_pt_trigger.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		pt_analog.h tbl_pt_analog.h
pointtest.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		mgr_point.h smartmap.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		cticonnect.h netports.h hashkey.h hash_functions.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h \
		desolvers.h tbl_pt_trigger.h slctpnt.h rtdb.h
porttest.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h mgr_port.h smartmap.h \
		hashkey.h hash_functions.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		port_base.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		slctprt.h rtdb.h
port_base.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		port_base.h dev_base.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h dllbase.h os2_2w32.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h xfer.h \
		dialup.h critical_section.h tbl_port_statistics.h \
		prot_emetcon.h dsm2err.h color.h porter.h devicetypes.h \
		msg_trace.h
port_dialable.obj:	yukon.h precompiled.h ctidbgmem.h port_dialable.h \
		port_base.h dev_base.h dsm2.h mutex.h dlldefs.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h xfer.h \
		dialup.h critical_section.h tbl_port_statistics.h \
		port_modem.h tbl_port_dialup.h
port_dialin.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h port_dialin.h port_base.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h dllbase.h \
		os2_2w32.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		port_dialable.h port_modem.h tbl_port_dialup.h
port_dialout.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h port_dialout.h port_base.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h dllbase.h \
		os2_2w32.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		port_dialable.h port_modem.h tbl_port_dialup.h
port_direct.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		port_direct.h port_serial.h port_base.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		tbl_port_settings.h tbl_port_timing.h port_dialable.h \
		port_modem.h tbl_port_dialup.h tbl_port_serial.h
port_modem.obj:	yukon.h precompiled.h ctidbgmem.h numstr.h dlldefs.h \
		port_modem.h port_base.h dev_base.h dsm2.h mutex.h guard.h \
		clrdump.h cticonnect.h netports.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		ctitime.h config_device.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h types.h sorted_vector.h dllbase.h \
		os2_2w32.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h
port_pool_out.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		port_pool_out.h port_base.h dev_base.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		dllbase.h os2_2w32.h cticalls.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h xfer.h \
		dialup.h critical_section.h tbl_port_statistics.h
port_serial.obj:	yukon.h precompiled.h ctidbgmem.h port_serial.h \
		port_base.h dev_base.h dsm2.h mutex.h dlldefs.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h xfer.h \
		dialup.h critical_section.h tbl_port_statistics.h \
		tbl_port_settings.h tbl_port_timing.h
port_tcpip.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		port_tcpip.h port_serial.h port_base.h dev_base.h dsm2.h \
		cticonnect.h netports.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h dllbase.h os2_2w32.h cticalls.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		tbl_port_settings.h tbl_port_timing.h port_dialable.h \
		port_modem.h tbl_port_dialup.h tbl_port_tcpip.h tcpsup.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
pttrigger.obj:	yukon.h precompiled.h ctidbgmem.h mgr_point.h dlldefs.h \
		smartmap.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		hashkey.h hash_functions.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h \
		desolvers.h tbl_pt_trigger.h slctpnt.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h ctibase.h ctinexus.h \
		pttrigger.h
pt_base.obj:	yukon.h precompiled.h ctidbgmem.h pt_base.h dbmemobject.h \
		dlldefs.h resolvers.h types.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbaccess.h sema.h desolvers.h \
		tbl_pt_trigger.h tbl_pt_alarm.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h
pt_dyn_dispatch.obj:	yukon.h precompiled.h ctidbgmem.h \
		pt_dyn_dispatch.h pt_dyn_base.h dlldefs.h tbl_pt_alarm.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h tbl_ptdispatch.h ctibase.h \
		ctinexus.h pointdefs.h ctitime.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
pt_numeric.obj:	yukon.h precompiled.h ctidbgmem.h device.h \
		devicetypes.h dlldefs.h pt_numeric.h pt_base.h dbmemobject.h \
		resolvers.h types.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbaccess.h sema.h desolvers.h \
		tbl_pt_trigger.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_alarm.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
pt_status.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		pt_status.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h cticonnect.h \
		netports.h dbaccess.h sema.h desolvers.h tbl_pt_trigger.h \
		tbl_pt_status.h tbl_pt_alarm.h
queuetest.obj:	yukon.h precompiled.h ctidbgmem.h queent.h dlldefs.h \
		queue.h cparms.h rwutil.h ctitime.h boost_time.h configkey.h \
		configval.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h
routetest.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		mgr_route.h repeaterrole.h rte_base.h dsm2.h cticonnect.h \
		netports.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		cticalls.h message.h collectable.h rwutil.h boost_time.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		rtdb.h hashkey.h hash_functions.h slctdev.h dev_base.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_trigger.h smartmap.h
rte_ccu.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		rte_xcu.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h ctitime.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h types.h sorted_vector.h dllbase.h os2_2w32.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		smartmap.h master.h dev_remote.h dev_single.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h dialup.h \
		tbl_dialup.h tbl_direct.h dev_ccu.h ctitypes.h dev_idlc.h \
		tbl_dv_idlcremote.h trx_info.h porter.h dsm2err.h \
		devicetypes.h trx_711.h device_queue_interface.h rte_ccu.h \
		tbl_rtcarrier.h tbl_rtrepeater.h prot_versacom.h \
		prot_emetcon.h expresscom.h ctistring.h
rte_expresscom.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h porter.h dsm2err.h devicetypes.h \
		rte_expresscom.h rte_xcu.h dev_base.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h dllbase.h \
		os2_2w32.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		smartmap.h tbl_rtversacom.h master.h dev_remote.h \
		dev_single.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h expresscom.h ctistring.h
rte_macro.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		message.h collectable.h rwutil.h ctitime.h boost_time.h \
		msg_pcrequest.h dllbase.h os2_2w32.h types.h cticalls.h \
		rte_macro.h tbl_rtmacro.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h ctibase.h ctinexus.h rte_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h tbl_pao.h tbl_rtcomm.h \
		msg_signal.h porter.h dsm2err.h devicetypes.h
rte_versacom.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h porter.h dsm2err.h devicetypes.h \
		rte_versacom.h rte_xcu.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h dllbase.h os2_2w32.h \
		cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		smartmap.h tbl_rtversacom.h master.h dev_remote.h \
		dev_single.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h prot_versacom.h
rte_xcu.obj:	yukon.h precompiled.h ctidbgmem.h desolvers.h \
		db_entry_defines.h dlldefs.h types.h pointtypes.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		expresscom.h cmdparse.h ctitokenizer.h parsevalue.h dllbase.h \
		os2_2w32.h cticalls.h ctitime.h ctistring.h rwutil.h \
		boost_time.h rte_xcu.h dev_base.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h msg_pcrequest.h smartmap.h master.h \
		dev_remote.h dev_single.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dialup.h tbl_dialup.h \
		tbl_direct.h dev_tap.h tbl_dv_tappaging.h dev_ied.h \
		ctitypes.h tbl_dv_ied.h dev_snpp.h dev_tnpp.h tbl_dv_tnpp.h \
		dev_pagingreceiver.h tbl_dv_pagingreceiver.h dev_lcu.h \
		dev_idlc.h tbl_dv_idlcremote.h trx_info.h porter.h dsm2err.h \
		devicetypes.h trx_711.h dev_wctp.h prot_versacom.h \
		prot_fpcbc.h prot_sa305.h prot_sa3rdparty.h protocol_sa.h \
		prot_lmi.h prot_seriesv.h verification_objects.h
slctdev.obj:	yukon.h precompiled.h ctidbgmem.h dev_710.h dev_idlc.h \
		ctitypes.h types.h os2_2w32.h dlldefs.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h ctitime.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		dllbase.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dialup.h tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h \
		trx_info.h porter.h dsm2err.h devicetypes.h trx_711.h \
		dev_macro.h dev_grp.h msg_lmcontrolhistory.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		dev_cbc6510.h dev_dnp.h prot_dnp.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h \
		tbl_dv_address.h dev_cbc.h tbl_dv_cbc.h dev_cbc7020.h \
		config_data_cbc.h dev_ccu.h device_queue_interface.h \
		dev_ccu721.h prot_klondike.h prot_wrap.h prot_idlc.h \
		dev_welco.h prot_welco.h dev_ilex.h dev_seriesv.h dev_ied.h \
		tbl_dv_ied.h prot_seriesv.h dev_lmi.h tbl_dv_seriesv.h \
		prot_lmi.h verification_objects.h dev_tcu.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dev_gridadvisor.h dev_modbus.h \
		prot_modbus.h dev_schlum.h dev_fulcrum.h dev_ion.h prot_ion.h \
		ion_datastream.h ion_value.h ion_serializable.h \
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
		dev_snpp.h dev_pagingreceiver.h tbl_dv_pagingreceiver.h \
		dev_tnpp.h tbl_dv_tnpp.h dev_wctp.h dev_grp_emetcon.h \
		tbl_dv_emetcon.h dev_grp_expresscom.h tbl_dv_expresscom.h \
		dev_grp_energypro.h dev_grp_golay.h prot_sasimple.h \
		tbl_lmg_sasimple.h dev_grp_mct.h tbl_dv_lmgmct.h \
		dev_grp_point.h tbl_lmg_point.h dev_grp_ripple.h \
		tbl_dv_lmg_ripple.h dev_grp_sa105.h prot_sa105.h \
		tbl_lmg_sa205105.h dev_grp_sa205.h prot_sa205.h \
		dev_grp_sa305.h prot_sa305.h tbl_lmg_sa305.h \
		dev_grp_sadigital.h dev_grp_versacom.h tbl_dv_versacom.h \
		dev_davis.h dev_system.h dev_aplus.h device.h dev_alpha.h \
		dev_a1.h dev_lgs4.h dev_dr87.h dev_dct501.h dev_mct24x.h \
		dev_mct2xx.h dev_mct.h dev_mct210.h dev_mct22X.h dev_mct310.h \
		dev_mct31X.h dev_mct410.h dev_mct4xx.h config_data_mct.h \
		dev_mct470.h dev_mct_lmt2.h dev_mct_broadcast.h dev_kv2.h \
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
		dllyukon.h dev_sentinel.h prot_ansi_sentinel.h dev_mark_v.h \
		prot_transdata.h transdata_application.h transdata_tracker.h \
		transdata_datalink.h prot_ymodem.h ctidate.h transdata_data.h \
		msg_cmd.h dev_repeater800.h dev_repeater.h dev_rtc.h \
		tbl_dv_rtc.h dev_sixnet.h prot_sixnet.h dev_foreignporter.h \
		rte_macro.h tbl_rtmacro.h rte_ccu.h rte_xcu.h smartmap.h \
		tbl_rtcarrier.h tbl_rtrepeater.h rte_versacom.h \
		tbl_rtversacom.h master.h rte_expresscom.h dev_fmu.h rtdb.h \
		slctdev.h
slctpnt.obj:	yukon.h precompiled.h ctidbgmem.h rtdb.h dlldefs.h \
		hashkey.h hash_functions.h utility.h ctitime.h queues.h \
		types.h numstr.h sorted_vector.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h clrdump.h cticonnect.h \
		netports.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h slctpnt.h pt_base.h dbmemobject.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		tbl_pt_trigger.h
slctprt.obj:	yukon.h precompiled.h ctidbgmem.h port_dialout.h dsm2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h port_base.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
		ctitime.h config_device.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h types.h sorted_vector.h dllbase.h \
		os2_2w32.h cticalls.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h tbl_port_base.h \
		xfer.h dialup.h critical_section.h tbl_port_statistics.h \
		port_dialable.h port_modem.h tbl_port_dialup.h port_dialin.h \
		port_direct.h port_serial.h tbl_port_settings.h \
		tbl_port_timing.h tbl_port_serial.h port_pool_out.h \
		port_tcpip.h tbl_port_tcpip.h tcpsup.h devicetypes.h \
		slctprt.h
tcpsup.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h porter.h dsm2err.h \
		devicetypes.h tcpsup.h ctinexus.h c_port_interface.h group.h \
		elogger.h alarmlog.h rtdb.h hashkey.h hash_functions.h \
		utility.h ctitime.h sorted_vector.h dllbase.h port_base.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h rte_base.h dbmemobject.h ctibase.h \
		message.h collectable.h rwutil.h boost_time.h tbl_pao.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_trigger.h tbl_port_base.h xfer.h dialup.h \
		critical_section.h tbl_port_statistics.h
test_dev_grp.obj:	dev_grp.h cparms.h rwutil.h yukon.h precompiled.h \
		ctidbgmem.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h msg_lmcontrolhistory.h pointdefs.h message.h \
		collectable.h msg_pcrequest.h msg_signal.h msg_pdata.h \
		pointtypes.h msg_multi.h pt_status.h pt_base.h dbmemobject.h \
		resolvers.h types.h db_entry_defines.h pt_dyn_base.h \
		tbl_pt_base.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h dbaccess.h \
		sema.h desolvers.h tbl_pt_trigger.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_analog.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h tbl_pao.h \
		tbl_rtcomm.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h dev_grp_expresscom.h tbl_dv_expresscom.h \
		vcomdefs.h expresscom.h ctistring.h mgr_point.h smartmap.h \
		slctpnt.h devicetypes.h
test_mgr_point.obj:	mgr_point.h dlldefs.h smartmap.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h yukon.h precompiled.h ctidbgmem.h \
		netports.h hashkey.h hash_functions.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dbaccess.h sema.h desolvers.h tbl_pt_trigger.h slctpnt.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h
#ENDUPDATE#
