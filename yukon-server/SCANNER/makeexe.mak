# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SCANNER)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(MSG)\include \
-I$(CPARMS)\include \
-I$(SERVICE)\include \
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


BASEOBJS=\
scanner.obj \
scanmain.obj \
scansvc.obj \


SCANNERLIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\scansup.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib


ALL:            scanner.exe

scanner.exe:    $(BASEOBJS) makeexe.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(BASEOBJS) id_scanner.obj $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ -link $(LIBS) $(RWLIBS) $(SCANNERLIBS)
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

killscan.exe:   poker.obj makeexe.mak
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) poker.obj $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ -link $(LIBS) $(RWLIBS) $(SCANNERLIBS)
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

copy:           scanner.exe
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\scanner.exe copy bin\scanner.exe $(YUKONOUTPUT)


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp


deps:
                scandeps -Output makeexe.mak *.cpp

.cpp.obj:
                @echo:
                @echo Compiling: $<
                @echo C-Options: $(CFLAGS)
                @echo Output   : ..\$@
                @echo:
                $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<


# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_scanner.obj

id_scanner.obj:    id_scanner.cpp include\id_scanner.h


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
		tbl_2way.h tbl_stats.h tbl_scanrate.h slctdev.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h tbl_dv_scandata.h tbl_dv_wnd.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h mgr_route.h repeaterrole.h smartmap.h \
		prot_emetcon.h msg_cmd.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h tbl_dv_mctiedport.h pt_numeric.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_limit.h exchange.h msg_reg.h msg_dbchg.h \
		c_port_interface.h group.h cparms.h configparms.h \
		connection.h msg_ptreg.h queue.h utility.h dllyukon.h
scansup.obj:	os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h scanglob.h scansup.h
scansvc.obj:	scanglob.h dlldefs.h scansvc.h cservice.h
#ENDUPDATE#
