# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SCANNER)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(PROCLOG)\include \
-I$(PROT)\include \
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
;$(BOOST) \
;$(RW)

DLLOBJS=\
scanglob.obj \


#scan_dlc.obj


ALL:            scansup.dll


scansup.dll:   $(DLLOBJS) Makedll.mak
               @$(MAKE) -nologo -f $(_InputFile) id
               @echo Building  ..\$@
               @%cd $(OBJ)
               $(CC) $(DLLFLAGS) $(DLLOBJS) id_sgdll.obj $(INCLPATHS) $(RWLIBS) $(COMPILEBASE)\lib\ctibase.lib /Fe..\$@
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
               @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\scansup.dll copy bin\scansup.dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\scansup.lib copy bin\scansup.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp

deps:
                scandeps -Output makedll.mak *.cpp


.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) /D_DLL_SCANSUP $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_sgdll.obj

id_sgdll.obj:    id_sgdll.cpp include\id_sgdll.h id_vinfo.h


#UPDATE#
id_scanner.obj:	utility.h dsm2.h mutex.h dlldefs.h guard.h \
		id_scanner.h id_build.h id_vinfo.h
id_sgdll.obj:	utility.h dsm2.h mutex.h dlldefs.h guard.h id_sgdll.h \
		id_build.h id_vinfo.h
scanglob.obj:	os2_2w32.h dlldefs.h types.h scanner.h dllbase.h \
		cticalls.h dsm2.h mutex.h guard.h scanglob.h utility.h
scanmain.obj:	scansvc.h cservice.h dlldefs.h CServiceConfig.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h ctibase.h ctinexus.h logger.h thread.h
scanner.obj:	os2_2w32.h dlldefs.h types.h cticalls.h dbaccess.h \
		dllbase.h dsm2.h mutex.h guard.h sema.h device.h \
		devicetypes.h drp.h elogger.h dsm2err.h alarmlog.h routes.h \
		queues.h porter.h lm_auto.h perform.h scanner.h ilex.h \
		master.h scanglob.h scansup.h rtdb.h hashkey.h mgr_device.h \
		dev_base.h cmdparse.h parsevalue.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h message.h ctidbgmem.h collectable.h \
		tbl_pao.h tbl_rtcomm.h resolvers.h pointtypes.h yukon.h \
		db_entry_defines.h desolvers.h logger.h thread.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_paoexclusion.h \
		utility.h slctdev.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h tbl_route.h tbl_carrier.h \
		mgr_route.h repeaterrole.h smartmap.h prot_emetcon.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		tbl_dv_mctiedport.h pt_numeric.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
		dev_welco.h ctitypes.h dev_idlc.h dev_remote.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		mgr_point.h slctpnt.h prot_welco.h exchange.h msg_cmd.h \
		msg_reg.h msg_dbchg.h c_port_interface.h group.h cparms.h \
		configparms.h connection.h msg_ptreg.h queue.h dllyukon.h
scansup.obj:	os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h scanglob.h scansup.h
scansvc.obj:	scanglob.h dlldefs.h scansvc.h cservice.h
#ENDUPDATE#
