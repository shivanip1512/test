include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(PIL)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(BOOST) \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
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
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctipil.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\cticonfig.lib


TESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib

PARSETESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib


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
applist.obj:    yukon.h precompiled.h ctidbgmem.h applist.h con_mgr.h \
                connection.h dlldefs.h exchange.h dllbase.h os2_2w32.h \
                types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
                cticonnect.h netports.h logger.h thread.h ctitime.h \
                CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
                collectable.h rwutil.h boost_time.h msg_multi.h msg_pdata.h \
                pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
                configkey.h configval.h ctibase.h ctinexus.h
exe_pcreq.obj:  yukon.h precompiled.h ctidbgmem.h message.h \
                collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
                pil_conmgr.h exchange.h dllbase.h os2_2w32.h types.h \
                cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
                cticonnect.h netports.h logger.h thread.h CtiPCPtrQueue.h \
                utility.h queues.h sorted_vector.h con_mgr.h connection.h \
                msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
                queue.h cparms.h configkey.h configval.h ctibase.h ctinexus.h \
                pilserver.h server_b.h cmdopts.h argkey.h argval.h \
                critical_Section.h smartmap.h hashkey.h hash_functions.h \
                msg_pcrequest.h mgr_device.h rtdb.h dev_base.h cmdparse.h \
                ctitokenizer.h parsevalue.h counter.h dev_exclusion.h \
                tbl_paoexclusion.h config_device.h config_base.h \
                config_resolvers.h rte_base.h dbmemobject.h tbl_pao.h \
                tbl_rtcomm.h dbaccess.h sema.h resolvers.h pointtypes.h \
                db_entry_defines.h desolvers.h msg_signal.h tbl_base.h \
                tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
                pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h \
                slctdev.h mgr_route.h repeaterrole.h mgr_config.h exe_pcreq.h \
                executor.h
parsetest.obj:  yukon.h precompiled.h ctidbgmem.h queue.h cparms.h \
                rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
                configval.h logger.h thread.h mutex.h guard.h numstr.h \
                clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
                sorted_vector.h cmdparse.h ctitokenizer.h parsevalue.h
pilglob.obj:    yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
                types.h
pilhost.obj:    yukon.h precompiled.h ctidbgmem.h mgr_device.h dlldefs.h \
                rtdb.h hashkey.h hash_functions.h utility.h ctitime.h \
                queues.h types.h numstr.h sorted_vector.h dllbase.h \
                os2_2w32.h cticalls.h dsm2.h mutex.h guard.h clrdump.h \
                cticonnect.h netports.h dev_base.h cmdparse.h ctitokenizer.h \
                parsevalue.h counter.h dev_exclusion.h tbl_paoexclusion.h \
                config_device.h logger.h thread.h CtiPCPtrQueue.h \
                config_base.h config_resolvers.h rte_base.h dbmemobject.h \
                ctibase.h ctinexus.h message.h collectable.h rwutil.h \
                boost_time.h tbl_pao.h tbl_rtcomm.h dbaccess.h sema.h \
                resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
                msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
                tbl_dyn_paoinfo.h pointdefs.h pt_base.h pt_dyn_base.h \
                tbl_pt_base.h tbl_pt_trigger.h slctdev.h smartmap.h \
                mgr_route.h repeaterrole.h pilserver.h server_b.h con_mgr.h \
                connection.h exchange.h msg_multi.h msg_pdata.h msg_ptreg.h \
                msg_reg.h queue.h cparms.h configkey.h configval.h cmdopts.h \
                argkey.h argval.h critical_Section.h msg_pcrequest.h \
                mgr_config.h dlldev.h
pilserver.obj:  yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
                types.h cticalls.h dev_grp_versacom.h dev_base.h dsm2.h \
                mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
                cmdparse.h ctitokenizer.h parsevalue.h counter.h \
                dev_exclusion.h tbl_paoexclusion.h ctitime.h config_device.h \
                logger.h thread.h CtiPCPtrQueue.h utility.h queues.h \
                sorted_vector.h dllbase.h config_base.h config_resolvers.h \
                rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
                collectable.h rwutil.h boost_time.h tbl_pao.h tbl_rtcomm.h \
                dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
                desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
                tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h pt_base.h \
                pt_dyn_base.h tbl_pt_base.h tbl_pt_trigger.h dev_grp.h \
                cparms.h configkey.h configval.h msg_lmcontrolhistory.h \
                msg_pcrequest.h msg_pdata.h msg_multi.h pt_status.h \
                tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
                tbl_unitmeasure.h tbl_pt_limit.h tbl_pt_analog.h \
                tbl_dv_versacom.h vcomdefs.h dev_grp_point.h tbl_lmg_point.h \
                dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h \
                msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
                exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
                dialup.h tbl_route.h tbl_carrier.h prot_emetcon.h \
                tbl_metergrp.h tbl_loadprofile.h tbl_dv_mctiedport.h \
                config_parts.h CtiLocalConnect.h critical_section.h porter.h \
                dsm2err.h devicetypes.h queent.h pil_conmgr.h con_mgr.h \
                pil_exefct.h executorfactory.h executor.h exe_cmd.h exe_reg.h \
                pilserver.h server_b.h cmdopts.h argkey.h argval.h smartmap.h \
                hashkey.h hash_functions.h mgr_device.h rtdb.h slctdev.h \
                mgr_route.h repeaterrole.h mgr_config.h msg_cmd.h pilglob.h \
                rte_ccu.h rte_xcu.h tbl_rtcarrier.h tbl_rtrepeater.h \
                ctistring.h
piltest.obj:    yukon.h precompiled.h ctidbgmem.h queue.h cparms.h \
                rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
                configval.h logger.h thread.h mutex.h guard.h numstr.h \
                clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
                sorted_vector.h exchange.h dllbase.h os2_2w32.h cticalls.h \
                dsm2.h cticonnect.h netports.h message.h collectable.h \
                msg_cmd.h msg_reg.h msg_pcrequest.h msg_pcreturn.h \
                msg_multi.h msg_pdata.h pointdefs.h connection.h msg_ptreg.h \
                cmdparse.h ctitokenizer.h parsevalue.h
pil_conmgr.obj: yukon.h precompiled.h ctidbgmem.h collectable.h \
                pil_conmgr.h exchange.h dlldefs.h dllbase.h os2_2w32.h \
                types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
                cticonnect.h netports.h logger.h thread.h ctitime.h \
                CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
                rwutil.h boost_time.h con_mgr.h connection.h msg_multi.h \
                msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
                cparms.h configkey.h configval.h ctibase.h ctinexus.h \
                pil_exefct.h executorfactory.h executor.h exe_cmd.h exe_reg.h \
                msg_cmd.h
pil_exefct.obj: yukon.h precompiled.h ctidbgmem.h executorfactory.h \
                collectable.h message.h dlldefs.h rwutil.h ctitime.h \
                boost_time.h executor.h exe_cmd.h exe_reg.h pil_exefct.h \
                exe_pcreq.h
precompiled.obj:        yukon.h precompiled.h ctidbgmem.h
#ENDUPDATE#
