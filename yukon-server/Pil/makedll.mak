# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(PIL)\include \
-I$(SERVER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
-I$(BOOST) \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
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
;$(BOOST) \
;$(RW)


DLLLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cmdline.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib \


DLLOBJS=\
exe_pcreq.obj \
pil_conmgr.obj \
pil_exefct.obj \
pilglob.obj \
pilserver.obj



ALL:            ctipil.dll

ctipil.dll:     $(DLLOBJS) makedll.mak
                @echo Building  $@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(DLLOBJS) $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(DLLLIBS) -Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\ctipil.dll copy bin\ctipil.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\ctipil.lib copy bin\ctipil.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp

deps:
                scandeps -Output makedll.mak *.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: $@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_CTIPIL $(INCLPATHS) \
-DWINDOWS -Fo$(OBJ)\ -c $<


#UPDATE#
applist.obj:	yukon.h precompiled.h ctidbgmem.h applist.h con_mgr.h \
		connection.h dlldefs.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h netports.h
exe_pcreq.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h pil_conmgr.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h con_mgr.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h netports.h pilserver.h server_b.h cmdopts.h \
		critical_Section.h msg_pcrequest.h mgr_device.h rtdb.h \
		hashkey.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h \
		config_base.h rte_base.h dbmemobject.h tbl_pao.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h queues.h utility.h slctdev.h \
		smartmap.h mgr_route.h repeaterrole.h exe_pcreq.h executor.h
parsetest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h cmdparse.h parsevalue.h
pilglob.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h
pilhost.obj:	yukon.h precompiled.h ctidbgmem.h mgr_device.h dlldefs.h \
		rtdb.h hashkey.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		logger.h thread.h config_base.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h netports.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h queues.h utility.h slctdev.h \
		smartmap.h mgr_route.h repeaterrole.h mgr_config.h \
		pilserver.h server_b.h con_mgr.h connection.h exchange.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cmdopts.h critical_Section.h msg_pcrequest.h dlldev.h
pilserver.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dev_grp_versacom.h dev_base.h dsm2.h \
		mutex.h guard.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h dllbase.h config_base.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h netports.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pointdefs.h queues.h utility.h dev_grp.h \
		cparms.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		tbl_dv_versacom.h vcomdefs.h porter.h dsm2err.h devicetypes.h \
		queent.h pil_conmgr.h exchange.h con_mgr.h connection.h \
		msg_ptreg.h msg_reg.h queue.h pil_exefct.h executorfactory.h \
		executor.h exe_cmd.h exe_reg.h pilserver.h server_b.h \
		cmdopts.h critical_Section.h mgr_device.h rtdb.h hashkey.h \
		slctdev.h smartmap.h mgr_route.h repeaterrole.h \
		msg_pcreturn.h msg_cmd.h numstr.h pilglob.h rte_ccu.h \
		rte_xcu.h tbl_rtcarrier.h tbl_rtrepeater.h
piltest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h netports.h message.h \
		collectable.h msg_cmd.h msg_reg.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		connection.h msg_ptreg.h cmdparse.h parsevalue.h
pil_conmgr.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		pil_conmgr.h exchange.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		message.h con_mgr.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h netports.h pil_exefct.h executorfactory.h \
		executor.h exe_cmd.h exe_reg.h msg_cmd.h
pil_exefct.obj:	yukon.h precompiled.h ctidbgmem.h executorfactory.h \
		collectable.h message.h dlldefs.h executor.h exe_cmd.h \
		exe_reg.h pil_exefct.h exe_pcreq.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
#ENDUPDATE#
