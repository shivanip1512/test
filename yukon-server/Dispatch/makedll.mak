# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(SIGNAL)\include \
-I$(CPARMS)\include \
-I$(BOOST) \
-I$(RW) \


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
;$(SIGNAL)\include \
;$(BOOST) \
;$(RW)



CTIVGLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\cmdline.lib \
$(COMPILEBASE)\lib\ctidbres.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctisvr.lib


DLLOBJS=\
ctivangogh.obj \
con_mgr_vg.obj \
dllvg.obj \
exe_ptchg.obj \
exe_email.obj \
pending_info.obj \
ptconnect.obj \
mgr_ptclients.obj \
vgexe_factory.obj \



ALL:            ctivg.dll

ctivg.dll:      $(DLLOBJS) Makedll.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(DLLOBJS) id_vgdll.obj $(INCLPATHS) $(RWLIBS) $(CTIVGLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\ctivg.dll copy bin\ctivg.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\ctivg.lib copy bin\ctivg.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp


deps:
                scandeps -Output makedll.mak *.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) /DCTIVANGOGH $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<


# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_vgdll.obj

id_vgdll.obj:    id_vgdll.cpp include\id_vgdll.h



#UPDATE#
applist.obj:	applist.h con_mgr.h connection.h dlldefs.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h message.h ctidbgmem.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h
cbctest.obj:	exchange.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h message.h \
		ctidbgmem.h collectable.h msg_cmd.h msg_reg.h msg_signal.h \
		yukon.h msg_pdata.h pointdefs.h msg_ptreg.h msg_pcreturn.h \
		msg_multi.h
cmdtest.obj:	queue.h dlldefs.h logger.h thread.h mutex.h guard.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		netports.h message.h ctidbgmem.h collectable.h msg_cmd.h \
		msg_reg.h msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h connection.h pointtypes.h
con_mgr_vg.obj:	con_mgr_vg.h exchange.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		message.h ctidbgmem.h collectable.h vgexe_factory.h \
		executor.h exe_ptchg.h exe_email.h executorfactory.h \
		exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h \
		pointtypes.h
ctivangogh.obj:	collectable.h cparms.h dlldefs.h guard.h netports.h \
		queent.h queue.h logger.h thread.h mutex.h con_mgr.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h message.h ctidbgmem.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h ctibase.h ctinexus.h msg_cmd.h msg_pcrequest.h \
		msg_commerrorhistory.h ctivangogh.h con_mgr_vg.h \
		vgexe_factory.h executor.h exe_ptchg.h exe_email.h \
		executorfactory.h exe_cmd.h exe_reg.h server_b.h cmdopts.h \
		dev_base_lite.h dbaccess.h sema.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h msg_email.h \
		msg_lmcontrolhistory.h pending_info.h tbl_lm_controlhist.h \
		utility.h tbl_state_grp.h tbl_state.h tbl_alm_ngroup.h \
		tbl_alm_ndest.h tbl_alm_nloc.h tbl_commerrhist.h \
		tbl_rawpthistory.h tbl_signal.h tbl_dv_cicust.h rtdb.h \
		hashkey.h pt_base.h resolvers.h pointtypes.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		pt_analog.h tbl_pt_analog.h pt_status.h tbl_pt_status.h \
		dev_base.h cmdparse.h parsevalue.h rte_base.h tbl_pao.h \
		tbl_rtcomm.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		queues.h tbl_ptdispatch.h tbl_pt_alarm.h mgr_ptclients.h \
		mgr_point.h slctpnt.h ptconnect.h pt_dyn_dispatch.h numstr.h \
		device.h devicetypes.h dllvg.h dllyukon.h
dispmain.obj:	dispsvc.h cservice.h dlldefs.h dllvg.h CServiceConfig.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h ctibase.h ctinexus.h logger.h thread.h utility.h
dispsvc.obj:	dispsvc.h cservice.h dlldefs.h
dllvg.obj:	configparms.h dlldefs.h cparms.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dllvg.h logger.h \
		thread.h utility.h
exe_email.obj:	message.h ctidbgmem.h collectable.h dlldefs.h \
		con_mgr_vg.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		vgexe_factory.h executor.h exe_ptchg.h exe_email.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		yukon.h msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h \
		ctivangogh.h server_b.h cmdopts.h dev_base_lite.h dbaccess.h \
		sema.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_email.h msg_commerrorhistory.h \
		msg_lmcontrolhistory.h pending_info.h tbl_lm_controlhist.h \
		utility.h tbl_state_grp.h tbl_state.h tbl_alm_ngroup.h \
		tbl_alm_ndest.h tbl_alm_nloc.h tbl_commerrhist.h \
		tbl_rawpthistory.h tbl_signal.h tbl_dv_cicust.h rtdb.h \
		hashkey.h
exe_ptchg.obj:	message.h ctidbgmem.h collectable.h dlldefs.h \
		con_mgr_vg.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		vgexe_factory.h executor.h exe_ptchg.h exe_email.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		yukon.h msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h \
		ctivangogh.h server_b.h cmdopts.h dev_base_lite.h dbaccess.h \
		sema.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_email.h msg_commerrorhistory.h \
		msg_lmcontrolhistory.h pending_info.h tbl_lm_controlhist.h \
		utility.h tbl_state_grp.h tbl_state.h tbl_alm_ngroup.h \
		tbl_alm_ndest.h tbl_alm_nloc.h tbl_commerrhist.h \
		tbl_rawpthistory.h tbl_signal.h tbl_dv_cicust.h rtdb.h \
		hashkey.h
exe_signal.obj:	message.h ctidbgmem.h collectable.h dlldefs.h \
		con_mgr_vg.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		vgexe_factory.h executor.h exe_ptchg.h exe_email.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		yukon.h msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h \
		ctivangogh.h server_b.h cmdopts.h dev_base_lite.h dbaccess.h \
		sema.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_email.h msg_commerrorhistory.h \
		msg_lmcontrolhistory.h pending_info.h tbl_lm_controlhist.h \
		utility.h tbl_state_grp.h tbl_state.h tbl_alm_ngroup.h \
		tbl_alm_ndest.h tbl_alm_nloc.h tbl_commerrhist.h \
		tbl_rawpthistory.h tbl_signal.h tbl_dv_cicust.h rtdb.h \
		hashkey.h exe_signal.h
id_vg.obj:	id_vg.h utility.h dsm2.h mutex.h dlldefs.h guard.h \
		id_build.h id_vinfo.h
id_vgdll.obj:	id_vgdll.h utility.h dsm2.h mutex.h dlldefs.h guard.h \
		id_build.h id_vinfo.h
mgr_ptclients.obj:	dllvg.h dlldefs.h pt_base.h dbmemobject.h \
		resolvers.h types.h pointtypes.h yukon.h ctidbgmem.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dbaccess.h sema.h desolvers.h logger.h thread.h \
		mgr_ptclients.h mgr_point.h rtdb.h hashkey.h slctpnt.h \
		msg_pdata.h message.h collectable.h msg_signal.h ptconnect.h \
		con_mgr.h connection.h exchange.h msg_multi.h msg_ptreg.h \
		msg_reg.h queue.h ctibase.h ctinexus.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h tbl_rawpthistory.h utility.h \
		devicetypes.h msg_pcreturn.h con_mgr_vg.h vgexe_factory.h \
		executor.h exe_ptchg.h exe_email.h executorfactory.h \
		exe_cmd.h exe_reg.h msg_cmd.h
pcmtest.obj:	mgr_ptclients.h dlldefs.h mgr_point.h rtdb.h hashkey.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		yukon.h ctidbgmem.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h desolvers.h \
		slctpnt.h msg_pdata.h message.h collectable.h msg_signal.h \
		ptconnect.h con_mgr.h connection.h exchange.h logger.h \
		thread.h msg_multi.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		tbl_rawpthistory.h utility.h
pending_info.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		pending_info.h msg_pdata.h pointdefs.h message.h ctidbgmem.h \
		collectable.h msg_signal.h yukon.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h tbl_lm_controlhist.h dbmemobject.h \
		dbaccess.h sema.h utility.h
ptconnect.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		msg_pcreturn.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		ptconnect.h hashkey.h con_mgr.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h
sigsrctest.obj:	queue.h dlldefs.h logger.h thread.h mutex.h guard.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		netports.h message.h ctidbgmem.h collectable.h msg_cmd.h \
		msg_reg.h msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_email.h connection.h \
		pointtypes.h
sigtest.obj:	queue.h dlldefs.h logger.h thread.h mutex.h guard.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		netports.h message.h ctidbgmem.h collectable.h msg_cmd.h \
		msg_reg.h msg_signal.h yukon.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_pcreturn.h msg_multi.h
test.obj:	queue.h dlldefs.h logger.h thread.h mutex.h guard.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		netports.h message.h ctidbgmem.h collectable.h msg_cmd.h \
		msg_reg.h msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_email.h connection.h \
		counter.h pointtypes.h
test2.obj:	queue.h dlldefs.h logger.h thread.h mutex.h guard.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h message.h ctidbgmem.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h netports.h msg_cmd.h msg_pcreturn.h
vangogh.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dllvg.h ctivangogh.h con_mgr.h connection.h \
		exchange.h logger.h thread.h message.h ctidbgmem.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h con_mgr_vg.h vgexe_factory.h executor.h \
		exe_ptchg.h exe_email.h executorfactory.h exe_cmd.h exe_reg.h \
		msg_cmd.h server_b.h cmdopts.h dev_base_lite.h dbaccess.h \
		sema.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_email.h msg_commerrorhistory.h \
		msg_lmcontrolhistory.h pending_info.h tbl_lm_controlhist.h \
		utility.h tbl_state_grp.h tbl_state.h tbl_alm_ngroup.h \
		tbl_alm_ndest.h tbl_alm_nloc.h tbl_commerrhist.h \
		tbl_rawpthistory.h tbl_signal.h tbl_dv_cicust.h rtdb.h \
		hashkey.h stdexcepthdlr.h
vgexe_factory.obj:	executorfactory.h collectable.h message.h \
		ctidbgmem.h dlldefs.h executor.h exe_cmd.h exe_reg.h \
		vgexe_factory.h exe_ptchg.h exe_email.h ctibase.h ctinexus.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h
#ENDUPDATE#
