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
tbl_dv_cbc.obj \
tbl_dv_dnp.obj \
tbl_dv_emetcon.obj \
tbl_dv_expresscom.obj \
tbl_dv_idlcremote.obj \
tbl_dv_ied.obj \
tbl_dv_lmvcserial.obj \
tbl_dv_lmgmct.obj \
tbl_dv_lmg_ripple.obj \
tbl_dv_mctiedport.obj \
tbl_dv_rtc.obj \
tbl_dv_scandata.obj \
tbl_dv_tappaging.obj \
tbl_dv_versacom.obj \
tbl_dv_wnd.obj \
tbl_gateway_end_device.obj \
tbl_lm_controlhist.obj \
tbl_lmg_sasimple.obj \
tbl_lmg_sa205105.obj \
tbl_lmg_sa305.obj \
tbl_loadprofile.obj \
tbl_metergrp.obj \
tbl_pao.obj \
tbl_paoexclusion.obj \
tbl_pt_accum.obj \
tbl_port_base.obj \
tbl_port_dialup.obj \
tbl_port_serial.obj \
tbl_port_settings.obj \
tbl_port_statistics.obj \
tbl_port_tcpip.obj \
tbl_port_timing.obj \
tbl_pt_accumhistory.obj \
tbl_pt_alarm.obj \
tbl_pt_analog.obj \
tbl_pt_base.obj \
tbl_pt_limit.obj \
tbl_pt_status.obj \
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



CTIPROGS=\
ctidbsrc.dll \


ALL:            $(CTIPROGS)

ctidbsrc.dll:   $(YUKONDLLOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ \
$(YUKONDLLOBJS) -link $(RWLIBS) $(DBLIBS)
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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) /DCTIYUKONDB -Fo$(OBJ)\ -c $<


######################################################################################
#UPDATE#
almtest.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h tbl_pt_alarm.h \
		dbmemobject.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h desolvers.h
dllyukon.obj:	tbl_route.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		tbl_rtcarrier.h dbaccess.h sema.h resolvers.h pointtypes.h \
		yukon.h ctidbgmem.h db_entry_defines.h desolvers.h logger.h \
		thread.h tbl_rtcomm.h ctibase.h ctinexus.h tbl_rtmacro.h \
		tbl_rtroute.h tbl_rtrepeater.h tbl_rtversacom.h \
		msg_pcrequest.h message.h collectable.h tbl_state_grp.h \
		tbl_state.h
tabletest.obj:	tbl_pthist.h yukon.h ctidbgmem.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		ctibase.h ctinexus.h tbl_rtroute.h rtdb.h hashkey.h
tbl.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h sema.h
tbl_2way.obj:	tbl_2way.h dbmemobject.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h logger.h thread.h
tbl_alm_ndest.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h tbl_alm_ndest.h \
		logger.h thread.h
tbl_alm_ngroup.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h tbl_alm_ngroup.h \
		tbl_alm_ndest.h yukon.h ctidbgmem.h logger.h thread.h
tbl_alm_nloc.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h tbl_alm_nloc.h \
		logger.h thread.h
tbl_base.obj:	tbl_base.h resolvers.h types.h pointtypes.h dlldefs.h \
		yukon.h ctidbgmem.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h
tbl_carrier.obj:	tbl_carrier.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h logger.h thread.h
tbl_ci_cust.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		tbl_ci_cust.h yukon.h ctidbgmem.h
tbl_commerrhist.obj:	tbl_commerrhist.h dbmemobject.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h yukon.h ctidbgmem.h utility.h logger.h \
		thread.h
tbl_contact_notification.obj:	dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		tbl_contact_notification.h logger.h thread.h
tbl_dialup.obj:	tbl_dialup.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h logger.h thread.h
tbl_direct.obj:	tbl_direct.h dbmemobject.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h logger.h thread.h
tbl_dv_address.obj:	tbl_dv_address.h types.h logger.h thread.h mutex.h \
		dlldefs.h guard.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		yukon.h ctidbgmem.h db_entry_defines.h desolvers.h
tbl_dv_cbc.obj:	tbl_dv_cbc.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h logger.h thread.h
tbl_dv_cicust.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		tbl_dv_cicust.h yukon.h ctidbgmem.h
tbl_dv_dnp.obj:	tbl_dv_address.h types.h logger.h thread.h mutex.h \
		dlldefs.h guard.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		yukon.h ctidbgmem.h db_entry_defines.h desolvers.h
tbl_dv_emetcon.obj:	tbl_dv_emetcon.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h logger.h thread.h
tbl_dv_expresscom.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		resolvers.h types.h pointtypes.h yukon.h ctidbgmem.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h db_entry_defines.h \
		tbl_dv_expresscom.h vcomdefs.h dbmemobject.h dbaccess.h \
		sema.h
tbl_dv_idlcremote.obj:	tbl_dv_idlcremote.h types.h logger.h thread.h \
		mutex.h dlldefs.h guard.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h yukon.h ctidbgmem.h db_entry_defines.h \
		desolvers.h
tbl_dv_ied.obj:	tbl_dv_ied.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h logger.h thread.h
tbl_dv_lmgmct.obj:	tbl_dv_lmgmct.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h
tbl_dv_lmg_ripple.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		tbl_dv_lmg_ripple.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h yukon.h ctidbgmem.h db_entry_defines.h
tbl_dv_lmvcserial.obj:	tbl_dv_lmvcserial.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		yukon.h ctidbgmem.h db_entry_defines.h vcomdefs.h logger.h \
		thread.h
tbl_dv_mctiedport.obj:	tbl_dv_mctiedport.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		yukon.h ctidbgmem.h db_entry_defines.h logger.h thread.h
tbl_dv_rtc.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		tbl_dv_rtc.h yukon.h ctidbgmem.h
tbl_dv_scandata.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		tbl_dv_scandata.h dbmemobject.h yukon.h ctidbgmem.h
tbl_dv_tappaging.obj:	tbl_dv_tappaging.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		yukon.h ctidbgmem.h db_entry_defines.h logger.h thread.h
tbl_dv_versacom.obj:	resolvers.h types.h pointtypes.h dlldefs.h \
		yukon.h ctidbgmem.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h db_entry_defines.h tbl_dv_versacom.h \
		vcomdefs.h dbmemobject.h dbaccess.h sema.h logger.h thread.h
tbl_dv_wnd.obj:	tbl_dv_wnd.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h desolvers.h logger.h thread.h
tbl_dyn_ptalarming.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h logger.h \
		thread.h numstr.h tbl_dyn_ptalarming.h ctibase.h ctinexus.h \
		dbmemobject.h pointdefs.h yukon.h ctidbgmem.h
tbl_dyn_pttag.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		numstr.h tbl_dyn_pttag.h ctibase.h ctinexus.h dbmemobject.h \
		pointdefs.h yukon.h ctidbgmem.h
tbl_gateway_end_device.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h numstr.h \
		tbl_gateway_end_device.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h logger.h thread.h \
		dbmemobject.h
tbl_lmg_golay.obj:	tbl_lmg_golay.h yukon.h ctidbgmem.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_lmg_sa205.obj:	tbl_lmg_sa205.h yukon.h ctidbgmem.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_lmg_sa205105.obj:	tbl_lmg_sa205105.h yukon.h ctidbgmem.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_lmg_sa305.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		tbl_lmg_sa305.h yukon.h ctidbgmem.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h
tbl_lmg_sadigital.obj:	tbl_lmg_sadigital.h yukon.h ctidbgmem.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_lmg_sasimple.obj:	tbl_lmg_sasimple.h yukon.h ctidbgmem.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_lmg_sa_205105.obj:	tbl_lmg_sa_205105.h yukon.h ctidbgmem.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_lmg_sa_simple.obj:	tbl_lmg_sa_simple.h yukon.h ctidbgmem.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_lm_controlhist.obj:	tbl_lm_controlhist.h dbmemobject.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h yukon.h ctidbgmem.h utility.h logger.h \
		thread.h numstr.h
tbl_loadprofile.obj:	tbl_loadprofile.h yukon.h ctidbgmem.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h vcomdefs.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h
tbl_metergrp.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		tbl_metergrp.h yukon.h ctidbgmem.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h vcomdefs.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h
tbl_pao.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h tbl_pao.h dbmemobject.h
tbl_paoexclusion.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h logger.h \
		thread.h tbl_paoexclusion.h utility.h yukon.h ctidbgmem.h
tbl_port_base.obj:	tbl_port_base.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h logger.h thread.h
tbl_port_dialup.obj:	tbl_port_dialup.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h logger.h thread.h
tbl_port_serial.obj:	tbl_port_serial.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		dbmemobject.h logger.h thread.h
tbl_port_settings.obj:	tbl_port_settings.h dbmemobject.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h yukon.h ctidbgmem.h logger.h thread.h
tbl_port_statistics.obj:	tbl_port_statistics.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h dbmemobject.h logger.h thread.h
tbl_port_tcpip.obj:	tbl_port_tcpip.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h logger.h thread.h
tbl_port_timing.obj:	tbl_port_timing.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h logger.h thread.h
tbl_ptdispatch.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		tbl_ptdispatch.h ctibase.h ctinexus.h dbmemobject.h \
		pointdefs.h yukon.h ctidbgmem.h
tbl_pthist.obj:	tbl_pthist.h yukon.h ctidbgmem.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		ctibase.h ctinexus.h dbaccess.h sema.h
tbl_pt_accum.obj:	tbl_pt_accum.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_pt_accumhistory.obj:	tbl_pt_accumhistory.h dbmemobject.h yukon.h \
		ctidbgmem.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h dbaccess.h sema.h
tbl_pt_alarm.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		tbl_pt_alarm.h dbmemobject.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h desolvers.h
tbl_pt_analog.obj:	tbl_pt_analog.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_pt_base.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		resolvers.h types.h pointtypes.h yukon.h ctidbgmem.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h db_entry_defines.h \
		tbl_pt_base.h dbmemobject.h dbaccess.h sema.h desolvers.h \
		pointdefs.h
tbl_pt_calc.obj:	tbl_pt_calc.h
tbl_pt_control.obj:	tbl_pt_control.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_pt_limit.obj:	tbl_pt_limit.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_pt_status.obj:	pointdefs.h tbl_pt_status.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		yukon.h ctidbgmem.h db_entry_defines.h desolvers.h logger.h \
		thread.h
tbl_pt_unit.obj:	tbl_pt_unit.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h desolvers.h tbl_unitmeasure.h logger.h \
		thread.h
tbl_route.obj:	tbl_route.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h logger.h \
		thread.h
tbl_rtcarrier.obj:	tbl_rtcarrier.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		dbmemobject.h
tbl_rtcomm.obj:	tbl_rtcomm.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h resolvers.h \
		pointtypes.h yukon.h ctidbgmem.h db_entry_defines.h \
		desolvers.h logger.h thread.h dbmemobject.h ctibase.h \
		ctinexus.h
tbl_rtmacro.obj:	tbl_rtmacro.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctibase.h \
		ctinexus.h
tbl_rtrepeater.obj:	tbl_rtrepeater.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_rtroute.obj:	tbl_rtroute.h yukon.h ctidbgmem.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		ctibase.h ctinexus.h dbaccess.h sema.h logger.h thread.h
tbl_rtversacom.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		tbl_rtversacom.h dbmemobject.h resolvers.h pointtypes.h \
		yukon.h ctidbgmem.h db_entry_defines.h
tbl_scanrate.obj:	tbl_scanrate.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_state.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h tbl_state.h logger.h \
		thread.h
tbl_state_grp.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h tbl_state_grp.h \
		tbl_state.h logger.h thread.h
tbl_stats.obj:	tbl_stats.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h desolvers.h logger.h thread.h
tbl_tag.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		numstr.h tbl_tag.h ctibase.h ctinexus.h dbmemobject.h yukon.h \
		ctidbgmem.h
tbl_taglog.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		numstr.h tbl_taglog.h ctibase.h ctinexus.h dbmemobject.h \
		pointdefs.h yukon.h ctidbgmem.h
tbl_unitmeasure.obj:	tbl_unitmeasure.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h yukon.h \
		ctidbgmem.h db_entry_defines.h desolvers.h logger.h thread.h
#ENDUPDATE#
