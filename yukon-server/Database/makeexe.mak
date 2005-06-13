
# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DATABASE)\include \
-I$(PORTER)\include \
-I$(COMMON)\include \
-I$(RW) \
-I$(BOOST) \

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
;$(PROTOCOL)\include \
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)


BASEOBJS=\
tabletest.obj \
tbl_rtroute.obj \
tbl_pthist.obj

TABLETESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib

EXECS=\
tabletest.exe \
almtest.exe


ALL:            $(EXECS)

tabletest.exe:  $(BASEOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOSTLIBS) $(TABLETESTLIBS)
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target ..\$@
                @%cd $(CWD)

almtest.exe:    almtest.obj Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
almtest.obj -link $(LIBS) $(RWLIBS) $(BOOSTLIBS) $(TABLETESTLIBS) $(COMPILEBASE)\lib\ctidbsrc.lib
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target ..\$@
                @%cd $(CWD)

copy:           $(EXECS)
               -if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist *.exe copy *.exe $(YUKONOUTPUT)


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


#UPDATE#
almtest.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h tbl_pt_alarm.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h
dllyukon.obj:	yukon.h precompiled.h ctidbgmem.h tbl_route.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbmemobject.h tbl_rtcarrier.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h tbl_rtcomm.h ctibase.h ctinexus.h \
		tbl_rtmacro.h tbl_rtroute.h tbl_rtrepeater.h tbl_rtversacom.h \
		msg_pcrequest.h message.h collectable.h tbl_state_grp.h \
		tbl_state.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
tabletest.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pthist.h \
		dlldefs.h dbmemobject.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h tbl_rtroute.h rtdb.h \
		hashkey.h
tbl.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h
tbl_2way.obj:	yukon.h precompiled.h ctidbgmem.h tbl_2way.h \
		dbmemobject.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h
tbl_alm_ndest.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h tbl_alm_ndest.h logger.h thread.h
tbl_alm_ngroup.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		logger.h thread.h
tbl_alm_nloc.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h tbl_alm_nloc.h logger.h thread.h
tbl_base.obj:	yukon.h precompiled.h ctidbgmem.h tbl_base.h resolvers.h \
		types.h pointtypes.h dlldefs.h db_entry_defines.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h dbmemobject.h
tbl_carrier.obj:	yukon.h precompiled.h ctidbgmem.h tbl_carrier.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h
tbl_ci_cust.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h logger.h thread.h tbl_ci_cust.h
tbl_commerrhist.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_commerrhist.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h utility.h logger.h thread.h
tbl_contact_notification.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h sema.h tbl_contact_notification.h \
		logger.h thread.h
tbl_dialup.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dialup.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h
tbl_direct.obj:	yukon.h precompiled.h ctidbgmem.h tbl_direct.h \
		dbmemobject.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h
tbl_dv_address.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_address.h \
		types.h logger.h dlldefs.h thread.h mutex.h guard.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h
tbl_dv_cbc.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_cbc.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h
tbl_dv_cicust.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h logger.h thread.h tbl_dv_cicust.h
tbl_dv_emetcon.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_emetcon.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h
tbl_dv_expresscom.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h resolvers.h types.h \
		pointtypes.h db_entry_defines.h tbl_dv_expresscom.h \
		vcomdefs.h dbmemobject.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h dbaccess.h sema.h
tbl_dv_idlcremote.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_idlcremote.h types.h logger.h dlldefs.h thread.h \
		mutex.h guard.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h
tbl_dv_ied.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_ied.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h
tbl_dv_lmgmct.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_lmgmct.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h
tbl_dv_lmg_ripple.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h tbl_dv_lmg_ripple.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h
tbl_dv_lmvcserial.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_lmvcserial.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h vcomdefs.h \
		logger.h thread.h
tbl_dv_mctiedport.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_mctiedport.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h logger.h \
		thread.h
tbl_dv_rtc.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h logger.h thread.h tbl_dv_rtc.h
tbl_dv_scandata.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h logger.h thread.h tbl_dv_scandata.h \
		dbmemobject.h
tbl_dv_tappaging.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_tappaging.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h logger.h \
		thread.h
tbl_dv_versacom.obj:	yukon.h precompiled.h ctidbgmem.h resolvers.h \
		types.h pointtypes.h dlldefs.h db_entry_defines.h \
		tbl_dv_versacom.h vcomdefs.h dbmemobject.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h dbaccess.h \
		sema.h logger.h thread.h
tbl_dv_wnd.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_wnd.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_dyn_paoinfo.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h rwutil.h boost_time.h logger.h \
		thread.h numstr.h tbl_dyn_paoinfo.h ctibase.h ctinexus.h \
		dbmemobject.h pointdefs.h
tbl_dyn_ptalarming.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h logger.h thread.h numstr.h \
		tbl_dyn_ptalarming.h ctibase.h ctinexus.h dbmemobject.h \
		pointdefs.h
tbl_dyn_pttag.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h logger.h thread.h numstr.h \
		tbl_dyn_pttag.h ctibase.h ctinexus.h dbmemobject.h \
		pointdefs.h
tbl_gateway_end_device.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h sema.h numstr.h \
		tbl_gateway_end_device.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h dbmemobject.h
tbl_lmg_golay.obj:	yukon.h precompiled.h ctidbgmem.h tbl_lmg_golay.h \
		dlldefs.h dbmemobject.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h
tbl_lmg_sa205105.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h logger.h thread.h mutex.h guard.h \
		tbl_lmg_sa205105.h dbmemobject.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h
tbl_lmg_sa305.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h tbl_lmg_sa305.h \
		dbmemobject.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h
tbl_lmg_sasimple.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h tbl_lmg_sasimple.h \
		dbmemobject.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h
tbl_lm_controlhist.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_lm_controlhist.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h utility.h logger.h thread.h numstr.h
tbl_loadprofile.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_loadprofile.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h
tbl_metergrp.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h
tbl_pao.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h logger.h thread.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_pao.h dbmemobject.h
tbl_paoexclusion.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h logger.h thread.h tbl_paoexclusion.h \
		utility.h
tbl_port_base.obj:	yukon.h precompiled.h ctidbgmem.h tbl_port_base.h \
		dbmemobject.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h
tbl_port_dialup.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_dialup.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h logger.h \
		thread.h
tbl_port_serial.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_serial.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		dbmemobject.h logger.h thread.h
tbl_port_settings.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_settings.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h logger.h thread.h
tbl_port_statistics.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_statistics.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		dbmemobject.h logger.h thread.h
tbl_port_tcpip.obj:	yukon.h precompiled.h ctidbgmem.h tbl_port_tcpip.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h
tbl_port_timing.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_timing.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h logger.h \
		thread.h
tbl_ptdispatch.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h logger.h thread.h tbl_ptdispatch.h \
		ctibase.h ctinexus.h dbmemobject.h pointdefs.h
tbl_pthist.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pthist.h \
		dlldefs.h dbmemobject.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h dbaccess.h sema.h
tbl_pt_accum.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_accum.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_pt_accumhistory.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_pt_accumhistory.h dbmemobject.h dlldefs.h dbaccess.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h
tbl_pt_alarm.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h logger.h thread.h tbl_pt_alarm.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h
tbl_pt_analog.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_analog.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_pt_base.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h tbl_pt_base.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h dbmemobject.h dbaccess.h sema.h desolvers.h \
		pointdefs.h
tbl_pt_calc.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_calc.h
tbl_pt_control.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_control.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_pt_limit.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_limit.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_pt_status.obj:	yukon.h precompiled.h ctidbgmem.h pointdefs.h \
		tbl_pt_status.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h
tbl_pt_unit.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_unit.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h tbl_unitmeasure.h \
		logger.h thread.h
tbl_route.obj:	yukon.h precompiled.h ctidbgmem.h tbl_route.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbmemobject.h logger.h thread.h
tbl_rtcarrier.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtcarrier.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		dbmemobject.h
tbl_rtcomm.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtcomm.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		dbmemobject.h ctibase.h ctinexus.h
tbl_rtmacro.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtmacro.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		ctibase.h ctinexus.h
tbl_rtrepeater.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtrepeater.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_rtroute.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtroute.h \
		dlldefs.h dbmemobject.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h ctibase.h ctinexus.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h dbaccess.h sema.h logger.h \
		thread.h
tbl_rtversacom.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h logger.h thread.h tbl_rtversacom.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h
tbl_scanrate.obj:	yukon.h precompiled.h ctidbgmem.h tbl_scanrate.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_state.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h tbl_state.h logger.h thread.h
tbl_state_grp.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h tbl_state_grp.h tbl_state.h logger.h \
		thread.h
tbl_stats.obj:	yukon.h precompiled.h ctidbgmem.h tbl_stats.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h
tbl_tag.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h logger.h thread.h numstr.h tbl_tag.h ctibase.h \
		ctinexus.h dbmemobject.h
tbl_taglog.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h logger.h thread.h numstr.h tbl_taglog.h \
		ctibase.h ctinexus.h dbmemobject.h pointdefs.h
tbl_unitmeasure.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_unitmeasure.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h
#ENDUPDATE#
