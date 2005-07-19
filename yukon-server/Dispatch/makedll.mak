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
-I$(SERVICE)\include \
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
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\service.lib


DLLOBJS=\
ctivangogh.obj \
con_mgr_vg.obj \
dllvg.obj \
exe_ptchg.obj \
pending_info.obj \
pendingopthread.obj \
ptconnect.obj \
mgr_ptclients.obj \
signalmanager.obj \
tagmanager.obj \
vgexe_factory.obj \



ALL:            ctivg.dll

ctivg.dll:      $(DLLOBJS) Makedll.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(DLLOBJS) id_vgdll.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIVGLIBS) /Fe..\$@
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
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /DCTIVANGOGH $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<


# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_vgdll.obj

id_vgdll.obj:    id_vgdll.cpp include\id_vgdll.h id_vinfo.h


#UPDATE#
applist.obj:	yukon.h precompiled.h ctidbgmem.h applist.h con_mgr.h \
		connection.h dlldefs.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h netports.h
cbctest.obj:	yukon.h precompiled.h ctidbgmem.h exchange.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h message.h collectable.h msg_cmd.h \
		msg_reg.h msg_signal.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_pcreturn.h msg_multi.h
cmdtest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h netports.h message.h \
		collectable.h msg_cmd.h msg_reg.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h connection.h \
		pointtypes.h
con_mgr_vg.obj:	yukon.h precompiled.h ctidbgmem.h con_mgr_vg.h \
		exchange.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h logger.h thread.h message.h \
		collectable.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h netports.h \
		pointtypes.h
ctivangogh.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		counter.h guard.h dlldefs.h mutex.h monitor.h cparms.h \
		netports.h queent.h queue.h logger.h thread.h con_mgr.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h message.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h ctibase.h \
		ctinexus.h msg_cmd.h msg_pcrequest.h msg_commerrorhistory.h \
		ctivangogh.h con_mgr_vg.h vgexe_factory.h executor.h \
		exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h server_b.h \
		cmdopts.h critical_Section.h dev_base_lite.h dbaccess.h \
		sema.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_lmcontrolhistory.h msg_tag.h \
		pendingopthread.h pendable.h pending_info.h \
		tbl_lm_controlhist.h utility.h pt_numeric.h pt_base.h \
		resolvers.h pointtypes.h db_entry_defines.h pt_dyn_base.h \
		tbl_pt_base.h desolvers.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h signalmanager.h tagmanager.h tbl_dyn_pttag.h \
		tbl_tag.h tbl_taglog.h tbl_state_grp.h tbl_state.h \
		tbl_alm_ngroup.h tbl_alm_ndest.h tbl_commerrhist.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h pt_status.h tbl_pt_status.h dev_base.h \
		cmdparse.h parsevalue.h dev_exclusion.h tbl_paoexclusion.h \
		rte_base.h tbl_pao.h tbl_rtcomm.h tbl_base.h tbl_2way.h \
		tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h queues.h \
		tbl_dyn_ptalarming.h tbl_ptdispatch.h tbl_pt_alarm.h \
		thread_monitor.h thread_register_data.h boost_time.h \
		mgr_ptclients.h mgr_point.h slctpnt.h ptconnect.h \
		pt_dyn_dispatch.h numstr.h device.h devicetypes.h dllvg.h \
		dllyukon.h
dispmain.obj:	yukon.h precompiled.h ctidbgmem.h dispsvc.h cservice.h \
		dlldefs.h dllvg.h CServiceConfig.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h ctibase.h \
		ctinexus.h netports.h logger.h thread.h utility.h
dispsvc.obj:	yukon.h precompiled.h ctidbgmem.h dispsvc.h cservice.h \
		dlldefs.h
dllvg.obj:	yukon.h precompiled.h ctidbgmem.h configparms.h dlldefs.h \
		cparms.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h dllvg.h logger.h thread.h utility.h
exe_ptchg.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h con_mgr_vg.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h netports.h \
		ctivangogh.h server_b.h cmdopts.h critical_Section.h \
		dev_base_lite.h dbaccess.h sema.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h msg_commerrorhistory.h \
		msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
		pending_info.h tbl_lm_controlhist.h utility.h pt_numeric.h \
		pt_base.h resolvers.h pointtypes.h db_entry_defines.h \
		pt_dyn_base.h tbl_pt_base.h desolvers.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h signalmanager.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h
exe_signal.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h con_mgr_vg.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h netports.h \
		ctivangogh.h server_b.h cmdopts.h critical_Section.h \
		dev_base_lite.h dbaccess.h sema.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h msg_commerrorhistory.h \
		msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
		pending_info.h tbl_lm_controlhist.h utility.h pt_numeric.h \
		pt_base.h resolvers.h pointtypes.h db_entry_defines.h \
		pt_dyn_base.h tbl_pt_base.h desolvers.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h signalmanager.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h \
		exe_signal.h
id_vg.obj:	yukon.h precompiled.h ctidbgmem.h id_vg.h utility.h dsm2.h \
		mutex.h dlldefs.h guard.h id_build.h id_vinfo.h
id_vgdll.obj:	yukon.h precompiled.h ctidbgmem.h id_vgdll.h utility.h \
		dsm2.h mutex.h dlldefs.h guard.h id_build.h id_vinfo.h
mgr_ptclients.obj:	yukon.h precompiled.h ctidbgmem.h dllvg.h dlldefs.h \
		pt_base.h dbmemobject.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		dbaccess.h sema.h desolvers.h logger.h thread.h \
		mgr_ptclients.h mgr_point.h rtdb.h hashkey.h slctpnt.h \
		msg_pdata.h message.h collectable.h msg_signal.h ptconnect.h \
		con_mgr.h connection.h exchange.h msg_multi.h msg_ptreg.h \
		msg_reg.h queue.h ctibase.h ctinexus.h netports.h \
		pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		tbl_rawpthistory.h utility.h devicetypes.h msg_pcreturn.h \
		con_mgr_vg.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h
pcmtest.obj:	yukon.h precompiled.h ctidbgmem.h mgr_ptclients.h \
		dlldefs.h mgr_point.h rtdb.h hashkey.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h pt_base.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h \
		desolvers.h slctpnt.h msg_pdata.h message.h collectable.h \
		msg_signal.h ptconnect.h con_mgr.h connection.h exchange.h \
		logger.h thread.h msg_multi.h msg_ptreg.h msg_reg.h queue.h \
		ctibase.h ctinexus.h netports.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h tbl_rawpthistory.h utility.h
pendingopthread.obj:	yukon.h precompiled.h ctidbgmem.h counter.h \
		guard.h dlldefs.h mutex.h cparms.h dllvg.h mgr_ptclients.h \
		mgr_point.h rtdb.h hashkey.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h pt_base.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h dbaccess.h sema.h desolvers.h slctpnt.h \
		msg_pdata.h message.h collectable.h msg_signal.h ptconnect.h \
		con_mgr.h connection.h exchange.h logger.h thread.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h netports.h pt_dyn_dispatch.h tbl_pt_alarm.h \
		tbl_ptdispatch.h tbl_rawpthistory.h utility.h msg_cmd.h \
		pendingopthread.h pendable.h pending_info.h \
		tbl_lm_controlhist.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h
pending_info.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h pending_info.h msg_pdata.h \
		pointdefs.h message.h collectable.h msg_signal.h \
		tbl_lm_controlhist.h dbmemobject.h dbaccess.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h sema.h utility.h
porterpoker.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h netports.h message.h \
		collectable.h msg_cmd.h msg_reg.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h counter.h \
		pointtypes.h numstr.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
ptconnect.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h logger.h thread.h msg_pcreturn.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h msg_signal.h \
		pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		ptconnect.h hashkey.h con_mgr.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h netports.h
signalmanager.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h logger.h thread.h pointdefs.h \
		signalmanager.h msg_signal.h message.h collectable.h \
		msg_multi.h msg_pdata.h tbl_dyn_ptalarming.h ctibase.h \
		ctinexus.h netports.h dbmemobject.h
sigsrctest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h netports.h message.h \
		collectable.h msg_cmd.h msg_reg.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h connection.h \
		pointtypes.h
sigtest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h netports.h message.h \
		collectable.h msg_cmd.h msg_reg.h msg_signal.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_pcreturn.h msg_multi.h
tagmanager.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h logger.h thread.h numstr.h pointdefs.h \
		tagmanager.h msg_tag.h message.h collectable.h msg_multi.h \
		msg_pdata.h msg_signal.h queue.h tbl_dyn_pttag.h ctibase.h \
		ctinexus.h netports.h dbmemobject.h tbl_tag.h tbl_taglog.h
test.obj:	yukon.h precompiled.h ctidbgmem.h thread.h mutex.h dlldefs.h \
		guard.h queue.h logger.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h netports.h message.h collectable.h \
		mgr_point.h rtdb.h hashkey.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h desolvers.h \
		slctpnt.h msg_cmd.h msg_dbchg.h msg_reg.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h msg_signal.h msg_ptreg.h msg_tag.h \
		msg_commerrorhistory.h msg_lmcontrolhistory.h connection.h \
		counter.h msg_notif_email.h msg_notif_email_attachment.h \
		thread_monitor.h thread_register_data.h boost_time.h
test2.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h netports.h msg_cmd.h \
		msg_pcreturn.h
vangogh.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		ctivangogh.h con_mgr.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		ctibase.h ctinexus.h netports.h con_mgr_vg.h vgexe_factory.h \
		executor.h exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h \
		msg_cmd.h server_b.h cmdopts.h critical_Section.h \
		dev_base_lite.h dbaccess.h sema.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h msg_commerrorhistory.h \
		msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
		pending_info.h tbl_lm_controlhist.h utility.h pt_numeric.h \
		pt_base.h resolvers.h pointtypes.h db_entry_defines.h \
		pt_dyn_base.h tbl_pt_base.h desolvers.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h signalmanager.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h \
		dllvg.h stdexcepthdlr.h
vgexe_factory.obj:	yukon.h precompiled.h ctidbgmem.h executorfactory.h \
		collectable.h message.h dlldefs.h executor.h exe_cmd.h \
		exe_reg.h vgexe_factory.h exe_ptchg.h ctibase.h ctinexus.h \
		netports.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h
#ENDUPDATE#
