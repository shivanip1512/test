# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(SIGNAL)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(CPARMS) \
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
$(COMPILEBASE)\lib\clrdump.lib \
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
applist.obj:    yukon.h precompiled.h ctidbgmem.h applist.h con_mgr.h \
                connection.h dlldefs.h exchange.h dllbase.h os2_2w32.h \
                types.h cticalls.h dsm2.h mutex.h guard.h cticonnect.h \
                netports.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
                message.h collectable.h rwutil.h boost_time.h msg_multi.h \
                msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
                utility.h sorted_vector.h ctibase.h ctinexus.h
cbctest.obj:    yukon.h precompiled.h ctidbgmem.h exchange.h dlldefs.h \
                dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
                guard.h cticonnect.h netports.h logger.h thread.h ctitime.h \
                CtiPCPtrQueue.h message.h collectable.h rwutil.h boost_time.h \
                msg_cmd.h msg_reg.h msg_signal.h msg_pdata.h pointdefs.h \
                msg_ptreg.h msg_pcreturn.h msg_multi.h
cmdtest.obj:    yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
                logger.h thread.h mutex.h guard.h ctitime.h CtiPCPtrQueue.h \
                utility.h dsm2.h cticonnect.h netports.h sorted_vector.h \
                exchange.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
                collectable.h rwutil.h boost_time.h msg_cmd.h msg_reg.h \
                msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
                msg_ptreg.h connection.h pointtypes.h
con_mgr_vg.obj: yukon.h precompiled.h ctidbgmem.h con_mgr_vg.h \
                exchange.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
                dsm2.h mutex.h guard.h cticonnect.h netports.h logger.h \
                thread.h ctitime.h CtiPCPtrQueue.h message.h collectable.h \
                rwutil.h boost_time.h vgexe_factory.h executor.h exe_ptchg.h \
                executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h con_mgr.h \
                connection.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
                msg_reg.h queue.h utility.h sorted_vector.h ctibase.h \
                ctinexus.h pointtypes.h
ctivangogh.obj: yukon.h precompiled.h ctidbgmem.h collectable.h \
                counter.h guard.h dlldefs.h mutex.h monitor.h cparms.h \
                rwutil.h ctitime.h boost_time.h netports.h queent.h queue.h \
                logger.h thread.h CtiPCPtrQueue.h utility.h dsm2.h \
                cticonnect.h sorted_vector.h con_mgr.h connection.h \
                exchange.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
                msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
                ctibase.h ctinexus.h msg_cmd.h msg_pcrequest.h msg_signal.h \
                msg_commerrorhistory.h msg_notif_alarm.h ctivangogh.h \
                con_mgr_vg.h vgexe_factory.h executor.h exe_ptchg.h \
                executorfactory.h exe_cmd.h exe_reg.h server_b.h cmdopts.h \
                critical_Section.h dev_base_lite.h dbaccess.h sema.h \
                dbmemobject.h msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
                msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
                pending_info.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
                resolvers.h pointtypes.h db_entry_defines.h pt_dyn_base.h \
                tbl_pt_base.h desolvers.h tbl_pt_unit.h tbl_unitmeasure.h \
                tbl_pt_limit.h pt_status.h tbl_pt_status.h signalmanager.h \
                tagmanager.h tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h \
                tbl_state_grp.h tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
                tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
                tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h \
                hash_functions.h numstr.h pt_accum.h tbl_pt_accum.h \
                tbl_pt_accumhistory.h pt_analog.h tbl_pt_analog.h dev_base.h \
                cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
                tbl_paoexclusion.h config_device.h config_base.h \
                config_resolvers.h rte_base.h tbl_pao.h tbl_rtcomm.h \
                tbl_base.h tbl_2way.h tbl_stats.h tbl_scanrate.h \
                tbl_dyn_paoinfo.h queues.h tbl_dyn_ptalarming.h \
                tbl_ptdispatch.h tbl_pt_alarm.h thread_monitor.h smartmap.h \
                thread_register_data.h mgr_ptclients.h mgr_point.h slctpnt.h \
                ptconnect.h pt_dyn_dispatch.h device.h devicetypes.h dllvg.h \
                dllyukon.h ctidate.h
dispmain.obj:   yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
                dispsvc.h cservice.h dllvg.h CServiceConfig.h dllbase.h \
                os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
                cticonnect.h netports.h ctibase.h ctinexus.h logger.h \
                thread.h CtiPCPtrQueue.h utility.h sorted_vector.h
dispsvc.obj:    yukon.h precompiled.h ctidbgmem.h dispsvc.h cservice.h \
                dlldefs.h
dllvg.obj:      yukon.h precompiled.h ctidbgmem.h configparms.h dlldefs.h \
                cparms.h rwutil.h ctitime.h boost_time.h dllbase.h os2_2w32.h \
                types.h cticalls.h dsm2.h mutex.h guard.h cticonnect.h \
                netports.h dllvg.h logger.h thread.h CtiPCPtrQueue.h \
                utility.h sorted_vector.h
exe_ptchg.obj:  yukon.h precompiled.h ctidbgmem.h message.h \
                collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
                con_mgr_vg.h exchange.h dllbase.h os2_2w32.h types.h \
                cticalls.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
                logger.h thread.h CtiPCPtrQueue.h vgexe_factory.h executor.h \
                exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h \
                con_mgr.h connection.h msg_multi.h msg_pdata.h pointdefs.h \
                msg_ptreg.h msg_reg.h queue.h utility.h sorted_vector.h \
                ctibase.h ctinexus.h ctivangogh.h server_b.h cmdopts.h \
                critical_Section.h dev_base_lite.h dbaccess.h sema.h \
                dbmemobject.h msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
                msg_commerrorhistory.h msg_lmcontrolhistory.h msg_tag.h \
                pendingopthread.h pendable.h pending_info.h msg_signal.h \
                tbl_lm_controlhist.h pt_numeric.h pt_base.h resolvers.h \
                pointtypes.h db_entry_defines.h pt_dyn_base.h tbl_pt_base.h \
                desolvers.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
                pt_status.h tbl_pt_status.h signalmanager.h tagmanager.h \
                tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
                tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
                tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
                tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h \
                hash_functions.h numstr.h
exe_signal.obj: yukon.h precompiled.h ctidbgmem.h message.h \
                collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
                con_mgr_vg.h exchange.h dllbase.h os2_2w32.h types.h \
                cticalls.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
                logger.h thread.h CtiPCPtrQueue.h vgexe_factory.h executor.h \
                exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h \
                con_mgr.h connection.h msg_multi.h msg_pdata.h pointdefs.h \
                msg_ptreg.h msg_reg.h queue.h utility.h sorted_vector.h \
                ctibase.h ctinexus.h ctivangogh.h server_b.h cmdopts.h \
                critical_Section.h dev_base_lite.h dbaccess.h sema.h \
                dbmemobject.h msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
                msg_commerrorhistory.h msg_lmcontrolhistory.h msg_tag.h \
                pendingopthread.h pendable.h pending_info.h msg_signal.h \
                tbl_lm_controlhist.h pt_numeric.h pt_base.h resolvers.h \
                pointtypes.h db_entry_defines.h pt_dyn_base.h tbl_pt_base.h \
                desolvers.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h \
                pt_status.h tbl_pt_status.h signalmanager.h tagmanager.h \
                tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
                tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
                tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
                tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h \
                hash_functions.h numstr.h exe_signal.h
id_vg.obj:      yukon.h precompiled.h ctidbgmem.h id_vg.h utility.h \
                ctitime.h dlldefs.h dsm2.h mutex.h guard.h cticonnect.h \
                netports.h sorted_vector.h id_build.h id_vinfo.h
id_vgdll.obj:   yukon.h precompiled.h ctidbgmem.h id_vgdll.h utility.h \
                ctitime.h dlldefs.h dsm2.h mutex.h guard.h cticonnect.h \
                netports.h sorted_vector.h id_build.h id_vinfo.h
mgr_ptclients.obj:      yukon.h precompiled.h ctidbgmem.h dllvg.h dlldefs.h \
                pt_base.h dbmemobject.h resolvers.h types.h pointtypes.h \
                db_entry_defines.h pointdefs.h pt_dyn_base.h tbl_pt_base.h \
                dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
                cticonnect.h netports.h dbaccess.h sema.h desolvers.h \
                logger.h thread.h ctitime.h CtiPCPtrQueue.h mgr_ptclients.h \
                mgr_point.h smartmap.h hashkey.h hash_functions.h slctpnt.h \
                msg_pdata.h message.h collectable.h rwutil.h boost_time.h \
                ptconnect.h con_mgr.h connection.h exchange.h msg_multi.h \
                msg_ptreg.h msg_reg.h queue.h utility.h sorted_vector.h \
                ctibase.h ctinexus.h pt_dyn_dispatch.h tbl_pt_alarm.h \
                tbl_ptdispatch.h rtdb.h tbl_rawpthistory.h devicetypes.h \
                msg_pcreturn.h msg_signal.h con_mgr_vg.h vgexe_factory.h \
                executor.h exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h \
                msg_cmd.h
pcmtest.obj:    yukon.h precompiled.h ctidbgmem.h mgr_ptclients.h \
                dlldefs.h mgr_point.h smartmap.h dllbase.h os2_2w32.h types.h \
                cticalls.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
                hashkey.h hash_functions.h logger.h thread.h ctitime.h \
                CtiPCPtrQueue.h pt_base.h dbmemobject.h resolvers.h \
                pointtypes.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
                tbl_pt_base.h dbaccess.h sema.h desolvers.h slctpnt.h \
                msg_pdata.h message.h collectable.h rwutil.h boost_time.h \
                ptconnect.h con_mgr.h connection.h exchange.h msg_multi.h \
                msg_ptreg.h msg_reg.h queue.h utility.h sorted_vector.h \
                ctibase.h ctinexus.h pt_dyn_dispatch.h tbl_pt_alarm.h \
                tbl_ptdispatch.h rtdb.h tbl_rawpthistory.h
pendingopthread.obj:    yukon.h precompiled.h ctidbgmem.h counter.h \
                guard.h dlldefs.h mutex.h cparms.h rwutil.h ctitime.h \
                boost_time.h dllvg.h mgr_ptclients.h mgr_point.h smartmap.h \
                dllbase.h os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h \
                netports.h hashkey.h hash_functions.h logger.h thread.h \
                CtiPCPtrQueue.h pt_base.h dbmemobject.h resolvers.h \
                pointtypes.h db_entry_defines.h pointdefs.h pt_dyn_base.h \
                tbl_pt_base.h dbaccess.h sema.h desolvers.h slctpnt.h \
                msg_pdata.h message.h collectable.h ptconnect.h con_mgr.h \
                connection.h exchange.h msg_multi.h msg_ptreg.h msg_reg.h \
                queue.h utility.h sorted_vector.h ctibase.h ctinexus.h \
                pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h rtdb.h \
                tbl_rawpthistory.h msg_cmd.h pendingopthread.h pendable.h \
                pending_info.h msg_signal.h tbl_lm_controlhist.h pt_numeric.h \
                tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_limit.h
pending_info.obj:       yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
                thread.h mutex.h guard.h ctitime.h CtiPCPtrQueue.h \
                pending_info.h msg_pdata.h pointdefs.h message.h \
                collectable.h rwutil.h boost_time.h msg_signal.h \
                tbl_lm_controlhist.h dbmemobject.h dbaccess.h dllbase.h \
                os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h netports.h \
                sema.h utility.h sorted_vector.h
porterpoker.obj:        yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
                logger.h thread.h mutex.h guard.h ctitime.h CtiPCPtrQueue.h \
                utility.h dsm2.h cticonnect.h netports.h sorted_vector.h \
                exchange.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
                collectable.h rwutil.h boost_time.h msg_cmd.h msg_reg.h \
                connection.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
                counter.h pointtypes.h numstr.h
precompiled.obj:        yukon.h precompiled.h ctidbgmem.h
ptconnect.obj:  yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
                dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
                guard.h cticonnect.h netports.h sema.h logger.h thread.h \
                ctitime.h CtiPCPtrQueue.h msg_pcreturn.h msg_multi.h \
                collectable.h msg_pdata.h pointdefs.h message.h rwutil.h \
                boost_time.h pt_base.h dbmemobject.h resolvers.h pointtypes.h \
                db_entry_defines.h pt_dyn_base.h tbl_pt_base.h desolvers.h \
                ptconnect.h hashkey.h hash_functions.h con_mgr.h connection.h \
                exchange.h msg_ptreg.h msg_reg.h queue.h utility.h \
                sorted_vector.h ctibase.h ctinexus.h
signalmanager.obj:      yukon.h precompiled.h ctidbgmem.h dbaccess.h \
                dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
                mutex.h guard.h cticonnect.h netports.h sema.h logger.h \
                thread.h ctitime.h CtiPCPtrQueue.h pointdefs.h \
                signalmanager.h msg_signal.h message.h collectable.h rwutil.h \
                boost_time.h msg_multi.h msg_pdata.h tbl_dyn_ptalarming.h \
                ctibase.h ctinexus.h dbmemobject.h
sigsrctest.obj: yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
                logger.h thread.h mutex.h guard.h ctitime.h CtiPCPtrQueue.h \
                utility.h dsm2.h cticonnect.h netports.h sorted_vector.h \
                exchange.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
                collectable.h rwutil.h boost_time.h msg_cmd.h msg_reg.h \
                msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
                msg_signal.h msg_ptreg.h connection.h pointtypes.h
sigtest.obj:    yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
                logger.h thread.h mutex.h guard.h ctitime.h CtiPCPtrQueue.h \
                utility.h dsm2.h cticonnect.h netports.h sorted_vector.h \
                exchange.h dllbase.h os2_2w32.h types.h cticalls.h message.h \
                collectable.h rwutil.h boost_time.h msg_cmd.h msg_reg.h \
                msg_signal.h msg_pdata.h pointdefs.h msg_ptreg.h \
                msg_pcreturn.h msg_multi.h
tagmanager.obj: yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
                dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
                guard.h cticonnect.h netports.h sema.h logger.h thread.h \
                ctitime.h CtiPCPtrQueue.h numstr.h pointdefs.h tagmanager.h \
                msg_tag.h message.h collectable.h rwutil.h boost_time.h \
                msg_multi.h msg_pdata.h queue.h utility.h sorted_vector.h \
                tbl_dyn_pttag.h ctibase.h ctinexus.h dbmemobject.h tbl_tag.h \
                tbl_taglog.h
test.obj:       yukon.h precompiled.h ctidbgmem.h thread.h mutex.h dlldefs.h \
                guard.h queue.h logger.h ctitime.h CtiPCPtrQueue.h utility.h \
                dsm2.h cticonnect.h netports.h sorted_vector.h exchange.h \
                dllbase.h os2_2w32.h types.h cticalls.h numstr.h message.h \
                collectable.h rwutil.h boost_time.h mgr_point.h smartmap.h \
                hashkey.h hash_functions.h pt_base.h dbmemobject.h \
                resolvers.h pointtypes.h db_entry_defines.h pointdefs.h \
                pt_dyn_base.h tbl_pt_base.h dbaccess.h sema.h desolvers.h \
                slctpnt.h msg_cmd.h msg_dbchg.h msg_reg.h msg_pcreturn.h \
                msg_multi.h msg_pdata.h msg_signal.h msg_ptreg.h msg_tag.h \
                msg_commerrorhistory.h msg_lmcontrolhistory.h connection.h \
                counter.h msg_notif_email.h msg_notif_email_attachment.h \
                thread_monitor.h thread_register_data.h ctidate.h
test2.obj:      yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
                logger.h thread.h mutex.h guard.h ctitime.h CtiPCPtrQueue.h \
                utility.h dsm2.h cticonnect.h netports.h sorted_vector.h \
                connection.h exchange.h dllbase.h os2_2w32.h types.h \
                cticalls.h message.h collectable.h rwutil.h boost_time.h \
                msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
                msg_cmd.h msg_pcreturn.h
vangogh.obj:    yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
                ctitime.h dlldefs.h boost_time.h ctivangogh.h con_mgr.h \
                connection.h exchange.h dllbase.h os2_2w32.h types.h \
                cticalls.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
                logger.h thread.h CtiPCPtrQueue.h message.h collectable.h \
                msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
                queue.h utility.h sorted_vector.h ctibase.h ctinexus.h \
                con_mgr_vg.h vgexe_factory.h executor.h exe_ptchg.h \
                executorfactory.h exe_cmd.h exe_reg.h msg_cmd.h server_b.h \
                cmdopts.h critical_Section.h dev_base_lite.h dbaccess.h \
                sema.h dbmemobject.h msg_dbchg.h msg_multiwrap.h \
                msg_pcreturn.h msg_commerrorhistory.h msg_lmcontrolhistory.h \
                msg_tag.h pendingopthread.h pendable.h pending_info.h \
                msg_signal.h tbl_lm_controlhist.h pt_numeric.h pt_base.h \
                resolvers.h pointtypes.h db_entry_defines.h pt_dyn_base.h \
                tbl_pt_base.h desolvers.h tbl_pt_unit.h tbl_unitmeasure.h \
                tbl_pt_limit.h pt_status.h tbl_pt_status.h signalmanager.h \
                tagmanager.h tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h \
                tbl_state_grp.h tbl_state.h tbl_alm_ngroup.h tbl_alm_ndest.h \
                tbl_commerrhist.h tbl_rawpthistory.h tbl_signal.h \
                tbl_ci_cust.h tbl_contact_notification.h rtdb.h hashkey.h \
                hash_functions.h numstr.h dllvg.h stdexcepthdlr.h
vgexe_factory.obj:      yukon.h precompiled.h ctidbgmem.h executorfactory.h \
                collectable.h message.h dlldefs.h rwutil.h ctitime.h \
                boost_time.h executor.h exe_cmd.h exe_reg.h vgexe_factory.h \
                exe_ptchg.h ctibase.h ctinexus.h netports.h cticonnect.h \
                dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
                guard.h
#ENDUPDATE#
