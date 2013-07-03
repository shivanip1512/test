# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(SCANNER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(COMMON)\include \
-I$(PROT)\include \
-I$(BOOST_INCLUDE) \
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
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST_INCLUDE) \
;$(RW)

DLLOBJS=\
scanglob.obj \


#scan_dlc.obj

CTIPROGS=\
scansup.dll


PROGS_VERSION=\
$(CTIPROGS)


ALL:            $(CTIPROGS)



scansup.dll:   $(DLLOBJS) Makedll.mak $(OBJ)\scansup.res
               @build -nologo -f $(_InputFile) id
               @echo Building  ..\$@
               @%cd $(OBJ)
               $(CC) $(DLLFLAGS) $(DLLOBJS) id_sgdll.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(COMPILEBASE)\lib\ctibase.lib /Fe..\$@ scansup.res
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
id_scanner.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h id_scanner.h
id_sgdll.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h id_sgdll.h
mgr_device_scannable.obj:	precompiled.h mgr_device_scannable.h \
		mgr_device.h dlldefs.h rtdb.h hashkey.h hash_functions.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h string_utility.h dllbase.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h mutex.h guard.h dsm2err.h \
		words.h optional.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h boost_time.h config_device.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h msg_signal.h tbl_static_paoinfo.h pointdefs.h \
		encryption.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h slctdev.h smartmap.h \
		readers_writer_lock.h critical_section.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h dev_carrier.h dev_dlcbase.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h debug_timer.h
scanglob.obj:	precompiled.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h scanglob.h
scanmain.obj:	precompiled.h ctitime.h dlldefs.h scansvc.h cservice.h \
		CServiceConfig.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h netports.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h ctibase.h ctinexus.h logger.h thread.h \
		CtiPCPtrQueue.h thread_monitor.h smartmap.h boostutil.h \
		readers_writer_lock.h critical_section.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h queue.h \
		string_utility.h thread_register_data.h
scanner.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h porter.h devicetypes.h \
		scanner.h master.h dlldev.h scanglob.h rtdb.h hashkey.h \
		hash_functions.h string_utility.h mgr_device_scannable.h \
		mgr_device.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h database_reader.h \
		boost_time.h config_device.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h CtiPCPtrQueue.h msg_signal.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		slctdev.h smartmap.h readers_writer_lock.h critical_section.h \
		mgr_point.h mgr_config.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_device.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h dev_welco.h dev_idlc.h dev_remote.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h prot_welco.h msg_cmd.h msg_dbchg.h \
		c_port_interface.h elogger.h dllyukon.h thread_monitor.h \
		thread_register_data.h ThreadStatusKeeper.h \
		database_transaction.h millisecond_timer.h
scansvc.obj:	precompiled.h scanglob.h dlldefs.h scansvc.h cservice.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
