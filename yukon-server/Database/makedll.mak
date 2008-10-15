include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(MSG)\include \
-I$(PROT)\include \
-I$(CPARMS)\include \
-I$(RW) \
-I$(BOOST) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)



YUKONDLLOBJS=\
dllyukon.obj \
tbl_2way.obj \
tbl_alm_ndest.obj \
tbl_alm_ngroup.obj \
tbl_base.obj \
tbl_carrier.obj \
tbl_ci_cust.obj \
tbl_commerrhist.obj \
tbl_contact_notification.obj \
tbl_dialup.obj \
tbl_direct.obj \
tbl_dyn_ptalarming.obj \
tbl_dyn_pttag.obj \
tbl_dyn_paoinfo.obj \
tbl_dv_address.obj \
tbl_dv_cbc.obj \
tbl_dv_emetcon.obj \
tbl_dv_expresscom.obj \
tbl_dv_idlcremote.obj \
tbl_dv_ied.obj \
tbl_dv_lmvcserial.obj \
tbl_dv_lmgmct.obj \
tbl_dv_lmg_ripple.obj \
tbl_dv_mctiedport.obj \
tbl_dv_pagingreceiver.obj \
tbl_dv_rtc.obj \
tbl_dv_scandata.obj \
tbl_dv_seriesv.obj \
tbl_dv_tappaging.obj \
tbl_dv_tnpp.obj \
tbl_dv_versacom.obj \
tbl_dv_wnd.obj \
tbl_devicereadjoblog.obj \
tbl_devicereadrequestlog.obj \
tbl_gateway_end_device.obj \
tbl_lm_controlhist.obj \
tbl_lmg_point.obj \
tbl_lmg_sasimple.obj \
tbl_lmg_sa205105.obj \
tbl_lmg_sa305.obj \
tbl_loadprofile.obj \
tbl_metergrp.obj \
tbl_meterreadlog.obj \
tbl_pao.obj \
tbl_pao_lite.obj \
tbl_paoexclusion.obj \
tbl_port_base.obj \
tbl_port_dialup.obj \
tbl_port_serial.obj \
tbl_port_settings.obj \
tbl_port_statistics.obj \
tbl_port_tcpip.obj \
tbl_port_timing.obj \
tbl_pt_accum.obj \
tbl_pt_accumhistory.obj \
tbl_pt_alarm.obj \
tbl_pt_analog.obj \
tbl_pt_property.obj \
tbl_pt_base.obj \
tbl_pt_limit.obj \
tbl_pt_status.obj \
tbl_pt_trigger.obj \
tbl_pt_unit.obj \
tbl_route.obj \
tbl_rtcarrier.obj \
tbl_rtcomm.obj \
tbl_rtmacro.obj \
tbl_rtrepeater.obj \
tbl_rtroute.obj \
tbl_rtversacom.obj \
tbl_pthist.obj \
tbl_ptdispatch.obj \
tbl_scanrate.obj \
tbl_state.obj \
tbl_state_grp.obj \
tbl_stats.obj \
tbl_tag.obj \
tbl_taglog.obj \
tbl_unitmeasure.obj \




DBLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\clrdump.lib \



CTIPROGS=\
ctidbsrc.dll \


ALL:            $(CTIPROGS)

ctidbsrc.dll:   $(YUKONDLLOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ \
$(YUKONDLLOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(DBLIBS) $(LINKFLAGS)
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
                scandeps -Output makedll.mak *.cpp


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



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /DCTIYUKONDB -Fo$(OBJ)\ -c $<


######################################################################################
#UPDATE#
almtest.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		tbl_pt_alarm.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h
dllyukon.obj:	yukon.h precompiled.h ctidbgmem.h tbl_route.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h tbl_rtcarrier.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		tbl_rtcomm.h ctibase.h ctinexus.h tbl_rtmacro.h tbl_rtroute.h \
		tbl_rtrepeater.h tbl_rtversacom.h msg_pcrequest.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h \
		tbl_state_grp.h tbl_state.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
tabletest.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pthist.h \
		dlldefs.h dbmemobject.h ctibase.h ctinexus.h netports.h \
		cticonnect.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h ctitime.h tbl_rtroute.h \
		resolvers.h pointtypes.h db_entry_defines.h rtdb.h hashkey.h \
		hash_functions.h utility.h queues.h sorted_vector.h
tbl.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h
tbl_2way.obj:	yukon.h precompiled.h ctidbgmem.h tbl_2way.h \
		dbmemobject.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_alm_ndest.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h tbl_alm_ndest.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_alm_ngroup.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h tbl_alm_ngroup.h tbl_alm_ndest.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		rwutil.h boost_time.h boostutil.h
tbl_alm_nloc.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h tbl_alm_nloc.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_base.obj:	yukon.h precompiled.h ctidbgmem.h tbl_base.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h rwutil.h boost_time.h boostutil.h
tbl_carrier.obj:	yukon.h precompiled.h ctidbgmem.h tbl_carrier.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_ci_cust.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_ci_cust.h
tbl_commerrhist.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_commerrhist.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		utility.h ctitime.h queues.h sorted_vector.h logger.h \
		thread.h CtiPCPtrQueue.h rwutil.h boost_time.h boostutil.h
tbl_contact_notification.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h tbl_contact_notification.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_devicereadjoblog.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_devicereadjoblog.h ctitime.h dlldefs.h pointdefs.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h logger.h thread.h mutex.h guard.h clrdump.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		netports.h sema.h rwutil.h boost_time.h boostutil.h
tbl_devicereadrequestlog.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_devicereadrequestlog.h ctitime.h dlldefs.h pointdefs.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h logger.h thread.h mutex.h guard.h clrdump.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		netports.h sema.h rwutil.h boost_time.h boostutil.h
tbl_dialup.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dialup.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_direct.obj:	yukon.h precompiled.h ctidbgmem.h tbl_direct.h \
		dbmemobject.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_dv_address.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_address.h \
		types.h logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h sorted_vector.h dllbase.h dsm2.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h
tbl_dv_cbc.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_cbc.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_dv_cicust.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_dv_cicust.h
tbl_dv_emetcon.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_emetcon.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_dv_expresscom.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_dv_expresscom.h vcomdefs.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h rwutil.h boost_time.h boostutil.h
tbl_dv_idlcremote.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_idlcremote.h types.h logger.h dlldefs.h thread.h \
		mutex.h guard.h numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h os2_2w32.h sorted_vector.h \
		dllbase.h dsm2.h cticonnect.h netports.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h rwutil.h boost_time.h boostutil.h
tbl_dv_ied.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_ied.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_dv_lmgmct.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_lmgmct.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_dv_lmg_ripple.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h tbl_dv_lmg_ripple.h \
		dllbase.h dsm2.h cticonnect.h netports.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		rwutil.h boost_time.h boostutil.h
tbl_dv_lmvcserial.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_lmvcserial.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h vcomdefs.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_dv_mctiedport.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_mctiedport.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		rwutil.h boost_time.h boostutil.h
tbl_dv_pagingreceiver.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_pagingreceiver.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_dv_rtc.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_dv_rtc.h rwutil.h boost_time.h \
		boostutil.h
tbl_dv_scandata.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_dv_scandata.h dbmemobject.h \
		rwutil.h boost_time.h boostutil.h
tbl_dv_seriesv.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_seriesv.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_dv_tappaging.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_tappaging.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		rwutil.h boost_time.h boostutil.h
tbl_dv_tnpp.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_tnpp.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_dv_versacom.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_versacom.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_dv_wnd.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_wnd.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_dyn_paoinfo.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h rwutil.h ctitime.h boost_time.h boostutil.h utility.h \
		queues.h sorted_vector.h logger.h thread.h CtiPCPtrQueue.h \
		tbl_dyn_paoinfo.h ctibase.h ctinexus.h dbmemobject.h \
		pointdefs.h
tbl_dyn_ptalarming.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_dyn_ptalarming.h ctibase.h \
		ctinexus.h dbmemobject.h pointdefs.h rwutil.h boost_time.h \
		boostutil.h
tbl_dyn_pttag.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_dyn_pttag.h ctibase.h ctinexus.h \
		dbmemobject.h pointdefs.h rwutil.h boost_time.h boostutil.h
tbl_gateway_end_device.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h tbl_gateway_end_device.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		dbmemobject.h rwutil.h boost_time.h boostutil.h
tbl_lmg_golay.obj:	yukon.h precompiled.h ctidbgmem.h tbl_lmg_golay.h \
		dlldefs.h dbmemobject.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_lmg_point.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h tbl_lmg_point.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h
tbl_lmg_sa205105.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h configkey.h configval.h logger.h thread.h \
		mutex.h guard.h clrdump.h CtiPCPtrQueue.h tbl_lmg_sa205105.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h
tbl_lmg_sa305.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h tbl_lmg_sa305.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		rwutil.h boost_time.h boostutil.h
tbl_lmg_sasimple.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h tbl_lmg_sasimple.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		prot_sa3rdparty.h cmdparse.h ctitokenizer.h parsevalue.h \
		prot_base.h msg_pdata.h pointdefs.h message.h collectable.h \
		rwutil.h boost_time.h boostutil.h xfer.h dialup.h \
		protocol_sa.h
tbl_lm_controlhist.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_lm_controlhist.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		utility.h ctitime.h queues.h sorted_vector.h logger.h \
		thread.h CtiPCPtrQueue.h rwutil.h boost_time.h boostutil.h \
		ctidate.h
tbl_loadprofile.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_loadprofile.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_metergrp.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		rwutil.h boost_time.h boostutil.h
tbl_meterreadlog.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_meterreadlog.h ctitime.h dlldefs.h pointdefs.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h boostutil.h dbaccess.h dllbase.h dsm2.h \
		mutex.h guard.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h rwutil.h boost_time.h
tbl_pao.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_pao.h dbmemobject.h rwutil.h \
		boost_time.h boostutil.h
tbl_paoexclusion.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_paoexclusion.h rwutil.h \
		boost_time.h boostutil.h
tbl_port_base.obj:	yukon.h precompiled.h ctidbgmem.h tbl_port_base.h \
		dbmemobject.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_port_dialup.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_dialup.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		rwutil.h boost_time.h boostutil.h
tbl_port_serial.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_serial.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h dbmemobject.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_port_settings.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_settings.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_port_statistics.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_statistics.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h sema.h dbmemobject.h \
		ctitime.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_port_tcpip.obj:	yukon.h precompiled.h ctidbgmem.h tbl_port_tcpip.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_port_timing.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_timing.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_ptdispatch.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_ptdispatch.h ctibase.h \
		ctinexus.h dbmemobject.h pointdefs.h rwutil.h boost_time.h \
		boostutil.h ctidate.h
tbl_pthist.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pthist.h \
		dlldefs.h dbmemobject.h ctibase.h ctinexus.h netports.h \
		cticonnect.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h ctitime.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h rwutil.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h
tbl_pt_accum.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_accum.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_pt_accumhistory.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_pt_accumhistory.h dbmemobject.h dlldefs.h dbaccess.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h
tbl_pt_alarm.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_pt_alarm.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		rwutil.h boost_time.h boostutil.h
tbl_pt_analog.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_analog.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_pt_base.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_pt_base.h dllbase.h dsm2.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		desolvers.h pointdefs.h rwutil.h boost_time.h boostutil.h
tbl_pt_calc.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_calc.h
tbl_pt_control.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_control.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_pt_limit.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_limit.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_pt_property.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_pt_property.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_pt_status.obj:	yukon.h precompiled.h ctidbgmem.h pointdefs.h \
		tbl_pt_status.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_pt_trigger.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_trigger.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_pt_unit.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_unit.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h tbl_unitmeasure.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h
tbl_route.obj:	yukon.h precompiled.h ctidbgmem.h tbl_route.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h
tbl_rtcarrier.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtcarrier.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h dbmemobject.h rwutil.h \
		boost_time.h boostutil.h
tbl_rtcomm.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtcomm.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h dbmemobject.h ctibase.h \
		ctinexus.h rwutil.h boost_time.h boostutil.h
tbl_rtmacro.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtmacro.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h ctibase.h \
		ctinexus.h
tbl_rtrepeater.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtrepeater.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_rtroute.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtroute.h \
		dlldefs.h dbmemobject.h ctibase.h ctinexus.h netports.h \
		cticonnect.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h dbaccess.h sema.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h resolvers.h pointtypes.h db_entry_defines.h \
		rwutil.h boost_time.h boostutil.h
tbl_rtversacom.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_rtversacom.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h
tbl_scanrate.obj:	yukon.h precompiled.h ctidbgmem.h tbl_scanrate.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_state.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		tbl_state.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h rwutil.h boost_time.h \
		boostutil.h
tbl_state_grp.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h tbl_state_grp.h tbl_state.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		rwutil.h boost_time.h boostutil.h
tbl_stats.obj:	yukon.h precompiled.h ctidbgmem.h tbl_stats.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		logger.h thread.h CtiPCPtrQueue.h ctidate.h
tbl_tag.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_tag.h ctibase.h ctinexus.h \
		dbmemobject.h rwutil.h boost_time.h boostutil.h
tbl_taglog.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_taglog.h ctibase.h ctinexus.h \
		dbmemobject.h pointdefs.h rwutil.h boost_time.h boostutil.h
tbl_unitmeasure.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_unitmeasure.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
#ENDUPDATE#
