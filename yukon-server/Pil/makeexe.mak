include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(PIL)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
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



PARSETESTOBJS=\
parsetest.obj

TESTOBJS=\
piltest.obj

PILOBJS= \
pilhost.obj


WINLIBS=kernel32.lib user32.lib
SOCKSLIB=wsock32.lib

CTIPROGS=\
pilhost.exe \
parsetest.exe \
piltest.exe

VGLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctipil.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidevdb.lib


TESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib

PARSETESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib


ALL:            $(CTIPROGS)

pilhost.exe:    $(PILOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(PILOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(VGLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
               @%cd $(CWD)


piltest.exe:    $(TESTOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(TESTOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(TESTLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

parsetest.exe:    $(PARSETESTOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
$(PARSETESTOBJS) -link $(RWLIBS) $(BOOSTLIBS) $(PARSETESTLIBS)
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
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h
exe_pcreq.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h pil_conmgr.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h con_mgr.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		ctibase.h ctinexus.h pilserver.h server_b.h cmdopts.h \
		critical_Section.h msg_pcrequest.h mgr_device.h rtdb.h \
		hashkey.h dev_base.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h utility.h \
		slctdev.h smartmap.h mgr_route.h repeaterrole.h exe_pcreq.h \
		executor.h
parsetest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h cmdparse.h parsevalue.h
pilglob.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h
pilhost.obj:	yukon.h precompiled.h ctidbgmem.h mgr_device.h dlldefs.h \
		rtdb.h hashkey.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h dev_base.h cmdparse.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h slctdev.h smartmap.h mgr_route.h repeaterrole.h \
		pilserver.h server_b.h con_mgr.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h cmdopts.h critical_Section.h \
		msg_pcrequest.h dlldev.h
pilserver.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dev_grp_versacom.h dev_base.h dsm2.h \
		mutex.h guard.h cmdparse.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h dllbase.h message.h collectable.h \
		tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h queues.h \
		utility.h dev_grp.h cparms.h msg_lmcontrolhistory.h \
		pointdefs.h msg_pcrequest.h msg_pdata.h msg_signal.h \
		msg_multi.h pt_status.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
		tbl_dv_versacom.h vcomdefs.h porter.h dsm2err.h devicetypes.h \
		netports.h queent.h pil_conmgr.h exchange.h con_mgr.h \
		connection.h msg_ptreg.h msg_reg.h queue.h pil_exefct.h \
		executorfactory.h executor.h exe_cmd.h exe_reg.h pilserver.h \
		server_b.h cmdopts.h critical_Section.h mgr_device.h rtdb.h \
		hashkey.h slctdev.h smartmap.h mgr_route.h repeaterrole.h \
		msg_pcreturn.h msg_cmd.h numstr.h pilglob.h rte_ccu.h \
		rte_xcu.h tbl_rtcarrier.h tbl_rtrepeater.h
piltest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h netports.h message.h \
		collectable.h msg_cmd.h msg_reg.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h connection.h msg_ptreg.h cmdparse.h parsevalue.h
pil_conmgr.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		pil_conmgr.h exchange.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		message.h con_mgr.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		ctibase.h ctinexus.h pil_exefct.h executorfactory.h \
		executor.h exe_cmd.h exe_reg.h msg_cmd.h
pil_exefct.obj:	yukon.h precompiled.h ctidbgmem.h executorfactory.h \
		collectable.h message.h dlldefs.h executor.h exe_cmd.h \
		exe_reg.h pil_exefct.h exe_pcreq.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
#ENDUPDATE#
