
# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DATABASE)\include \
-I$(PORTER)\include \
-I$(COMMON)\include \
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
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(TABLETESTLIBS)
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
almtest.obj -link $(LIBS) $(RWLIBS) $(TABLETESTLIBS) $(COMPILEBASE)\lib\ctidbsrc.lib
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
                $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

deps:
                scandeps -Output makeexe.mak *.cpp


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
tbl_dv_cbc.obj:	tbl_dv_cbc.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dbmemobject.h dbaccess.h \
		sema.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h logger.h thread.h
tbl_dv_cicust.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		tbl_dv_cicust.h yukon.h ctidbgmem.h
tbl_dv_dnp.obj:	tbl_dv_dnp.h types.h logger.h thread.h mutex.h \
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
tbl_dv_filler.obj:	tbl_dv_filler.h yukon.h ctidbgmem.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h
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
