include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(SERVICE)\include \
-I$(MSG)\include \
-I$(RW) \
-I$(BOOST) \


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
;$(RW) \
;$(BOOST)


TSTEST=\
tstest.obj

TESTOBJS=\
test.obj

TEST2OBJS=\
test2.obj

SIGTESTOBJS=\
sigtest.obj

PORTERPOKE=\
porterpoker.obj

CBCOBJS=\
cbctest.obj

SIGSRCTESTOBJS=\
sigsrctest.obj

CMDTESTOBJS=\
cmdtest.obj


PCMOBJS=\
pcmtest.obj

VGOBJS= \
dispmain.obj \
dispsvc.obj \
con_mgr_vg.obj \
vgexe_factory.obj \
vangogh.obj


WINLIBS=kernel32.lib user32.lib
SOCKSLIB=wsock32.lib

CTIPROGS=\
dispatch.exe \
cmdtest.exe \
cbctest.exe \
pcmtest.exe \
poker.exe \
sigsinktest.exe \
sigsrctest.exe \
vgsrctest.exe \
vgsinktest.exe

VGLIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cmdline.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\clrdump.lib \



TESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\clrdump.lib \





ALL:            $(CTIPROGS)

dispatch.exe:   $(VGOBJS) makeexe.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(VGOBJS) id_vg.obj -link $(RWLIBS) $(BOOSTLIBS) $(VGLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


pcmtest.exe:    $(PCMOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(PCMOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

vgsrctest.exe:  $(TESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(TESTOBJS) -link $(BOOSTLIBS) $(RWLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

vgsinktest.exe: $(TEST2OBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(TEST2OBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


sigsinktest.exe: $(SIGTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(SIGTESTOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

poker.exe: $(PORTERPOKE) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(PORTERPOKE) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

cbctest.exe: $(CBCOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(CBCOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


sigsrctest.exe: $(SIGSRCTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(SIGSRCTESTOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

cmdtest.exe: $(CMDTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(CMDTESTOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

tstest.exe:     $(TSTEST) makeexe.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(TSTEST) tstest.obj -link $(RWLIBS) $(BOOSTLIBS) $(VGLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
                scandeps -Output makeexe.mak *.cpp



clean:
        -del *.obj
        -del *.pch
        -del *.pdb
        -del *.sdb
        -del *.adb
        -del *.ilk
        -del *.exe

# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_vg.obj

id_vg.obj:    id_vg.cpp include\id_vg.h id_vinfo.h



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################


#UPDATE#
applist.obj:	yukon.h precompiled.h ctidbgmem.h applist.h con_mgr.h \
		connection.h dlldefs.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		collectable.h rwutil.h boost_time.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ctibase.h ctinexus.h
cbctest.obj:	yukon.h precompiled.h ctidbgmem.h exchange.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h message.h collectable.h rwutil.h boost_time.h \
		msg_cmd.h msg_reg.h msg_signal.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_pcreturn.h msg_multi.h
cmdtest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h message.h collectable.h \
		msg_cmd.h msg_reg.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h connection.h
con_mgr_vg.obj:	yukon.h precompiled.h ctidbgmem.h con_mgr_vg.h \
		exchange.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h message.h collectable.h \
		rwutil.h boost_time.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ctibase.h ctinexus.h
ctivangogh.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		counter.h guard.h numstr.h dlldefs.h clrdump.h mutex.h \
		monitor.h cparms.h rwutil.h ctitime.h boost_time.h \
		configkey.h configval.h netports.h queent.h queue.h logger.h \
		thread.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h con_mgr.h connection.h exchange.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h cticonnect.h message.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h ctibase.h ctinexus.h msg_cmd.h msg_pcrequest.h \
		msg_signal.h msg_commerrorhistory.h msg_notif_alarm.h \
		msg_server_req.h msg_server_resp.h ctivangogh.h con_mgr_vg.h \
		vgexe_factory.h executor.h exe_ptchg.h executorfactory.h \
		exe_cmd.h exe_reg.h server_b.h cmdopts.h argkey.h argval.h \
		critical_Section.h smartmap.h hashkey.h hash_functions.h \
		dev_base_lite.h dbaccess.h sema.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h msg_lmcontrolhistory.h \
		msg_tag.h pendingopthread.h pendable.h pending_info.h \
		tbl_lm_controlhist.h pt_numeric.h pt_base.h resolvers.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		tbl_pt_trigger.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h signalmanager.h pt_status.h tbl_pt_status.h \
		pttrigger.h mgr_point.h slctpnt.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		tbl_pao.h tbl_rtcomm.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h tbl_dyn_ptalarming.h \
		tbl_ptdispatch.h tbl_pt_alarm.h thread_monitor.h \
		thread_register_data.h mgr_ptclients.h ptconnect.h \
		pt_dyn_dispatch.h dllvg.h dllyukon.h ctidate.h
dispmain.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		dispsvc.h cservice.h dllvg.h CServiceConfig.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h ctibase.h ctinexus.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h dbghelp.h
dispsvc.obj:	yukon.h precompiled.h ctidbgmem.h dispsvc.h cservice.h \
		dlldefs.h
dllvg.obj:	yukon.h precompiled.h ctidbgmem.h configparms.h dlldefs.h \
		cparms.h rwutil.h ctitime.h boost_time.h configkey.h \
		configval.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dllvg.h logger.h thread.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h
exe_ptchg.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		con_mgr_vg.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h vgexe_factory.h executor.h \
		exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h \
		con_mgr.h connection.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ctibase.h ctinexus.h ctivangogh.h \
		server_b.h cmdopts.h argkey.h argval.h critical_Section.h \
		smartmap.h hashkey.h hash_functions.h dev_base_lite.h \
		dbaccess.h sema.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_commerrorhistory.h msg_lmcontrolhistory.h \
		msg_tag.h pendingopthread.h pendable.h pending_info.h \
		msg_signal.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
		resolvers.h db_entry_defines.h pt_dyn_base.h tbl_pt_base.h \
		desolvers.h tbl_pt_trigger.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h signalmanager.h pt_status.h tbl_pt_status.h \
		pttrigger.h mgr_point.h slctpnt.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h
exe_signal.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		con_mgr_vg.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h vgexe_factory.h executor.h \
		exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h \
		con_mgr.h connection.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ctibase.h ctinexus.h ctivangogh.h \
		server_b.h cmdopts.h argkey.h argval.h critical_Section.h \
		smartmap.h hashkey.h hash_functions.h dev_base_lite.h \
		dbaccess.h sema.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_commerrorhistory.h msg_lmcontrolhistory.h \
		msg_tag.h pendingopthread.h pendable.h pending_info.h \
		msg_signal.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
		resolvers.h db_entry_defines.h pt_dyn_base.h tbl_pt_base.h \
		desolvers.h tbl_pt_trigger.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h signalmanager.h pt_status.h tbl_pt_status.h \
		pttrigger.h mgr_point.h slctpnt.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h exe_signal.h
id_vg.obj:	yukon.h precompiled.h ctidbgmem.h id_vg.h utility.h \
		ctitime.h dlldefs.h queues.h types.h numstr.h sorted_vector.h \
		id_vinfo.h
id_vgdll.obj:	yukon.h precompiled.h ctidbgmem.h id_vgdll.h utility.h \
		ctitime.h dlldefs.h queues.h types.h numstr.h sorted_vector.h \
		id_vinfo.h
mgr_ptclients.obj:	yukon.h precompiled.h ctidbgmem.h dllvg.h dlldefs.h \
		pt_base.h dbmemobject.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dbaccess.h sema.h \
		desolvers.h tbl_pt_trigger.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		mgr_ptclients.h mgr_point.h smartmap.h hashkey.h \
		hash_functions.h slctpnt.h msg_pdata.h message.h \
		collectable.h rwutil.h boost_time.h ptconnect.h con_mgr.h \
		connection.h exchange.h msg_multi.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ctibase.h ctinexus.h \
		server_b.h cmdopts.h argkey.h argval.h critical_Section.h \
		pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h rtdb.h \
		tbl_rawpthistory.h devicetypes.h msg_pcreturn.h msg_signal.h \
		con_mgr_vg.h vgexe_factory.h executor.h exe_ptchg.h \
		executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h
pcmtest.obj:	yukon.h precompiled.h ctidbgmem.h mgr_ptclients.h \
		dlldefs.h mgr_point.h smartmap.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h hashkey.h hash_functions.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h pt_base.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
		tbl_pt_base.h dbaccess.h sema.h desolvers.h tbl_pt_trigger.h \
		slctpnt.h msg_pdata.h message.h collectable.h rwutil.h \
		boost_time.h ptconnect.h con_mgr.h connection.h exchange.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ctibase.h ctinexus.h server_b.h \
		cmdopts.h argkey.h argval.h critical_Section.h \
		pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h rtdb.h \
		tbl_rawpthistory.h
pendingopthread.obj:	yukon.h precompiled.h ctidbgmem.h counter.h \
		guard.h numstr.h dlldefs.h clrdump.h mutex.h cparms.h \
		rwutil.h ctitime.h boost_time.h configkey.h configval.h \
		dllvg.h mgr_ptclients.h mgr_point.h smartmap.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h netports.h \
		hashkey.h hash_functions.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h desolvers.h \
		tbl_pt_trigger.h slctpnt.h msg_pdata.h message.h \
		collectable.h ptconnect.h con_mgr.h connection.h exchange.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h server_b.h cmdopts.h argkey.h argval.h \
		critical_Section.h pt_dyn_dispatch.h tbl_pt_alarm.h \
		tbl_ptdispatch.h rtdb.h tbl_rawpthistory.h msg_cmd.h \
		pendingopthread.h pendable.h pending_info.h msg_signal.h \
		tbl_lm_controlhist.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h signalmanager.h
pending_info.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		pending_info.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		collectable.h rwutil.h boost_time.h msg_signal.h \
		tbl_lm_controlhist.h dbmemobject.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h cticonnect.h netports.h sema.h
porterpoker.obj:	yukon.h precompiled.h ctidbgmem.h queue.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h message.h collectable.h \
		msg_cmd.h msg_reg.h msg_dbchg.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h counter.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
ptconnect.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h msg_pcreturn.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h boost_time.h pt_base.h dbmemobject.h resolvers.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		tbl_pt_trigger.h ptconnect.h hashkey.h hash_functions.h \
		con_mgr.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ctibase.h ctinexus.h \
		server_b.h cmdopts.h argkey.h argval.h critical_Section.h \
		smartmap.h
signalmanager.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h pointdefs.h signalmanager.h \
		msg_signal.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h pointtypes.h tbl_dyn_ptalarming.h \
		ctibase.h ctinexus.h dbmemobject.h tbl_pt_alarm.h resolvers.h \
		db_entry_defines.h desolvers.h
sigsrctest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h message.h collectable.h \
		msg_cmd.h msg_reg.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_signal.h msg_ptreg.h \
		connection.h
sigtest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h message.h collectable.h \
		msg_cmd.h msg_reg.h msg_signal.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_pcreturn.h msg_multi.h
tagmanager.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h pointdefs.h tagmanager.h msg_tag.h \
		message.h collectable.h rwutil.h boost_time.h msg_multi.h \
		msg_pdata.h pointtypes.h queue.h cparms.h configkey.h \
		configval.h tbl_dyn_pttag.h ctibase.h ctinexus.h \
		dbmemobject.h tbl_tag.h tbl_taglog.h
test.obj:	yukon.h precompiled.h ctidbgmem.h thread.h mutex.h dlldefs.h \
		guard.h numstr.h clrdump.h queue.h cparms.h rwutil.h \
		ctitime.h boost_time.h configkey.h configval.h logger.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		exchange.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		cticonnect.h netports.h message.h collectable.h mgr_point.h \
		smartmap.h hashkey.h hash_functions.h pt_base.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h desolvers.h \
		tbl_pt_trigger.h slctpnt.h msg_cmd.h msg_dbchg.h msg_reg.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h msg_pcrequest.h \
		msg_signal.h msg_ptreg.h msg_tag.h msg_commerrorhistory.h \
		msg_lmcontrolhistory.h connection.h counter.h \
		msg_notif_email.h msg_notif_email_attachment.h \
		thread_monitor.h thread_register_data.h ctidate.h
test2.obj:	yukon.h precompiled.h ctidbgmem.h queue.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h msg_cmd.h msg_pcreturn.h
vangogh.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		ctivangogh.h con_mgr.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h con_mgr_vg.h vgexe_factory.h executor.h \
		exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h \
		server_b.h cmdopts.h argkey.h argval.h critical_Section.h \
		smartmap.h hashkey.h hash_functions.h dev_base_lite.h \
		dbaccess.h sema.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_commerrorhistory.h msg_lmcontrolhistory.h \
		msg_tag.h pendingopthread.h pendable.h pending_info.h \
		msg_signal.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
		resolvers.h db_entry_defines.h pt_dyn_base.h tbl_pt_base.h \
		desolvers.h tbl_pt_trigger.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h signalmanager.h pt_status.h tbl_pt_status.h \
		pttrigger.h mgr_point.h slctpnt.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h dllvg.h \
		stdexcepthdlr.h
vgexe_factory.obj:	yukon.h precompiled.h ctidbgmem.h executorfactory.h \
		collectable.h message.h dlldefs.h rwutil.h ctitime.h \
		boost_time.h executor.h exe_cmd.h exe_reg.h vgexe_factory.h \
		exe_ptchg.h ctibase.h ctinexus.h netports.h cticonnect.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h
#ENDUPDATE#
