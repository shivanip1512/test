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
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cmdline.lib \
$(COMPILEBASE)\lib\service.lib \



TESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \





ALL:            $(CTIPROGS)

dispatch.exe:   $(VGOBJS) makeexe.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(VGOBJS) id_vg.obj -link $(RWLIBS) $(VGLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


pcmtest.exe:    $(PCMOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(PCMOBJS) -link $(RWLIBS) $(TESTLIBS)
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
$(TEST2OBJS) -link $(RWLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


sigsinktest.exe: $(SIGTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(SIGTESTOBJS) -link $(RWLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

poker.exe: $(PORTERPOKE) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(PORTERPOKE) -link $(RWLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

cbctest.exe: $(CBCOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(CBCOBJS) -link $(RWLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)


sigsrctest.exe: $(SIGSRCTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(SIGSRCTESTOBJS) -link $(RWLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

cmdtest.exe: $(CMDTESTOBJS) makeexe.mak
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(CMDTESTOBJS) -link $(RWLIBS) $(TESTLIBS)
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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################


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
ctivangogh.obj:	collectable.h monitor.h cparms.h dlldefs.h guard.h \
		netports.h queent.h queue.h logger.h thread.h mutex.h \
		con_mgr.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h message.h ctidbgmem.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h ctibase.h ctinexus.h msg_cmd.h msg_pcrequest.h \
		msg_commerrorhistory.h ctivangogh.h con_mgr_vg.h \
		vgexe_factory.h executor.h exe_ptchg.h exe_email.h \
		executorfactory.h exe_cmd.h exe_reg.h server_b.h cmdopts.h \
		dev_base_lite.h dbaccess.h sema.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h msg_email.h \
		msg_lmcontrolhistory.h msg_tag.h pending_info.h \
		tbl_lm_controlhist.h utility.h signalmanager.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h \
		pt_base.h resolvers.h pointtypes.h db_entry_defines.h \
		pt_dyn_base.h tbl_pt_base.h desolvers.h pt_accum.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h pt_status.h tbl_pt_status.h dev_base.h \
		cmdparse.h parsevalue.h rte_base.h tbl_pao.h tbl_rtcomm.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_paoexclusion.h queues.h tbl_dyn_ptalarming.h \
		tbl_ptdispatch.h tbl_pt_alarm.h mgr_ptclients.h mgr_point.h \
		slctpnt.h ptconnect.h pt_dyn_dispatch.h numstr.h device.h \
		devicetypes.h dllvg.h dllyukon.h
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
		msg_lmcontrolhistory.h msg_tag.h pending_info.h \
		tbl_lm_controlhist.h utility.h signalmanager.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h
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
		msg_lmcontrolhistory.h msg_tag.h pending_info.h \
		tbl_lm_controlhist.h utility.h signalmanager.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h
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
		msg_lmcontrolhistory.h msg_tag.h pending_info.h \
		tbl_lm_controlhist.h utility.h signalmanager.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h \
		exe_signal.h
exe_tag.obj:	con_mgr_vg.h exchange.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		message.h ctidbgmem.h collectable.h vgexe_factory.h \
		executor.h exe_ptchg.h exe_email.h executorfactory.h \
		exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h \
		ctivangogh.h server_b.h cmdopts.h dev_base_lite.h dbaccess.h \
		sema.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
		msg_pcreturn.h msg_email.h msg_commerrorhistory.h \
		msg_lmcontrolhistory.h msg_tag.h pending_info.h \
		tbl_lm_controlhist.h utility.h signalmanager.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h \
		exe_tag.h
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
porterpoker.obj:	queue.h dlldefs.h logger.h thread.h mutex.h guard.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		netports.h message.h ctidbgmem.h collectable.h msg_cmd.h \
		msg_reg.h connection.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h counter.h pointtypes.h \
		numstr.h
ptconnect.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		msg_pcreturn.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		pt_base.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
		ptconnect.h hashkey.h con_mgr.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h
signalmanager.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		pointdefs.h signalmanager.h msg_signal.h message.h \
		ctidbgmem.h collectable.h yukon.h msg_multi.h msg_pdata.h \
		tbl_dyn_ptalarming.h ctibase.h ctinexus.h dbmemobject.h
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
tagmanager.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h logger.h thread.h \
		numstr.h pointdefs.h tagmanager.h msg_tag.h message.h \
		ctidbgmem.h collectable.h yukon.h msg_multi.h msg_pdata.h \
		msg_signal.h queue.h tbl_dyn_pttag.h ctibase.h ctinexus.h \
		dbmemobject.h tbl_tag.h tbl_taglog.h
test.obj:	queue.h dlldefs.h logger.h thread.h mutex.h guard.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		netports.h message.h ctidbgmem.h collectable.h mgr_point.h \
		rtdb.h hashkey.h pt_base.h dbmemobject.h resolvers.h \
		pointtypes.h yukon.h db_entry_defines.h pointdefs.h \
		pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h desolvers.h \
		slctpnt.h msg_cmd.h msg_reg.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h msg_signal.h msg_ptreg.h msg_email.h msg_tag.h \
		msg_commerrorhistory.h msg_lmcontrolhistory.h connection.h \
		counter.h
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
		msg_lmcontrolhistory.h msg_tag.h pending_info.h \
		tbl_lm_controlhist.h utility.h signalmanager.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
		tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
		tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h \
		stdexcepthdlr.h
vgexe_factory.obj:	executorfactory.h collectable.h message.h \
		ctidbgmem.h dlldefs.h executor.h exe_cmd.h exe_reg.h \
		vgexe_factory.h exe_ptchg.h exe_email.h ctibase.h ctinexus.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h
#ENDUPDATE#
