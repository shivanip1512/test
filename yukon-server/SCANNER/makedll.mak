# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SCANNER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(PROT)\include \
-I$(BOOST) \
-I$(SQLAPI)\include \
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
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST) \
;$(RW)

DLLOBJS=\
scanglob.obj \


#scan_dlc.obj


ALL:            scansup.dll


scansup.dll:   $(DLLOBJS) Makedll.mak
               @build -nologo -f $(_InputFile) id
               @echo Building  ..\$@
               @%cd $(OBJ)
               $(CC) $(DLLFLAGS) $(DLLOBJS) id_sgdll.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(COMPILEBASE)\lib\ctibase.lib /Fe..\$@
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
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /D_DLL_SCANSUP $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

# The lines below accomplish the ID'ing of the project!
id:
            @build -nologo -f $(_InputFile) id_sgdll.obj

id_sgdll.obj:    id_sgdll.cpp include\id_sgdll.h


#UPDATE#
id_scanner.obj:	yukon.h precompiled.h types.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h id_scanner.h
id_sgdll.obj:	yukon.h precompiled.h types.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h id_sgdll.h
mgr_device_scannable.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		mgr_device_scannable.h mgr_device.h dlldefs.h rtdb.h \
		hashkey.h hash_functions.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h dllbase.h \
		dsm2.h mutex.h guard.h cticonnect.h netports.h dsm2err.h \
		words.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h sema.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h tbl_static_paoinfo.h \
		pointdefs.h pt_base.h pt_dyn_base.h tbl_pt_base.h \
		tbl_pt_property.h tbl_pt_trigger.h slctdev.h smartmap.h \
		readers_writer_lock.h critical_section.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h debug_timer.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
scanglob.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h scanner.h ctitime.h dllbase.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h scanglob.h
scanmain.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h scansvc.h cservice.h CServiceConfig.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h ctibase.h ctinexus.h logger.h thread.h \
		CtiPCPtrQueue.h thread_monitor.h smartmap.h boostutil.h \
		readers_writer_lock.h critical_section.h cparms.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h queue.h \
		thread_register_data.h
scanner.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h elogger.h \
		porter.h devicetypes.h scanner.h master.h dlldev.h scanglob.h \
		rtdb.h hashkey.h hash_functions.h mgr_device_scannable.h \
		mgr_device.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h rte_base.h dbmemobject.h ctibase.h ctinexus.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_base.h tbl_stats.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h tbl_static_paoinfo.h pointdefs.h pt_base.h \
		pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h slctdev.h smartmap.h readers_writer_lock.h \
		critical_section.h mgr_point.h fifo_multiset.h mgr_config.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h dev_welco.h \
		dev_idlc.h dev_remote.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h prot_welco.h \
		msg_cmd.h msg_dbchg.h c_port_interface.h configparms.h \
		dllyukon.h thread_monitor.h thread_register_data.h \
		ThreadStatusKeeper.h millisecond_timer.h
scansvc.obj:	yukon.h precompiled.h types.h ctidbgmem.h scanglob.h \
		dlldefs.h scansvc.h cservice.h
#ENDUPDATE#
