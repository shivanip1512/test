include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(PORTER)\include \
-I$(MSG)\include \
-I$(PROT)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include


DYNPAOINFO_OBJS=\
$(PRECOMPILED_OBJ) \
id_dynpaoinfo.obj \
mgr_dyn_paoinfo.obj

DYNPAOINFO_LIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib

CTIPROGS=\
dynpaoinfo.dll

PROGS_VERSION=\
$(CTIPROGS)


ALL:            $(CTIPROGS)


dynpaoinfo.dll:   $(DYNPAOINFO_OBJS) Makefile $(OBJ)\dynpaoinfo.res
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(CC) $(INCLPATHS) $(DLLFLAGS) -Fe..\$@ $(DYNPAOINFO_OBJS) -link $(BOOST_LIBS) $(DYNPAOINFO_LIBS) dynpaoinfo.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @echo:
                @echo Done building Target $@
                @echo:
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\*.dll copy bin\*.dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\*.lib copy bin\*.lib $(COMPILEBASE)\lib


deps:
                scandeps -Output makedynpaoinfo.mak *.cpp


clean:
        -del \
*.pdb \
$(OBJ)\*.obj \
$(OBJ)\*.res \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.exe



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_DYNPAOINFO -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
cmd_device.obj:	precompiled.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h
cmd_lcr3102_demandresponsesummary.obj:	precompiled.h \
		cmd_lcr3102_DemandResponseSummary.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_dlc.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h
cmd_lcr3102_hourlydatalog.obj:	precompiled.h \
		cmd_lcr3102_hourlyDataLog.h cmd_lcr3102.h cmd_dlc.h \
		cmd_device.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h
cmd_lcr3102_tamperread.obj:	precompiled.h cmd_lcr3102_tamperRead.h \
		cmd_lcr3102_ThreePart.h cmd_lcr3102.h cmd_dlc.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h
cmd_lcr3102_threepart.obj:	precompiled.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_dlc.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h
cmd_mct410_disconnectconfiguration.obj:	precompiled.h numstr.h \
		dlldefs.h cmd_mct410_disconnectConfiguration.h cmd_mct410.h \
		cmd_mct4xx.h cmd_dlc.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h cmd_rfn_helper.h cmd_rfn.h rfn_asid.h \
		dev_mct410.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h tbl_route.h tbl_carrier.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h ctidate.h dev_mct410_commands.h \
		cmd_mct410_hourlyRead.h
cmd_mct410_hourlyread.obj:	precompiled.h cmd_mct410_hourlyRead.h \
		cmd_mct410.h cmd_mct4xx.h cmd_dlc.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h ctidate.h dev_mct410.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h tbl_route.h tbl_carrier.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h dev_mct410_commands.h \
		cmd_mct410_disconnectConfiguration.h
cmd_mct420.obj:	precompiled.h cmd_mct420.h cmd_mct410.h cmd_mct4xx.h \
		cmd_dlc.h cmd_device.h dev_single.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h
cmd_mct420_hourlyread.obj:	precompiled.h cmd_mct420_hourlyRead.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h cmd_mct4xx.h cmd_dlc.h \
		cmd_device.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h ctidate.h dev_mct420.h dev_mct410.h \
		dev_mct4xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		tbl_route.h tbl_carrier.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		dev_mct410_commands.h cmd_mct410_disconnectConfiguration.h
cmd_mct420_lcdconfiguration.obj:	precompiled.h \
		cmd_mct420_LcdConfiguration.h cmd_mct420.h cmd_mct410.h \
		cmd_mct4xx.h cmd_dlc.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h
cmd_mct420_meterparameters.obj:	precompiled.h \
		cmd_mct420_MeterParameters.h cmd_mct420.h cmd_mct410.h \
		cmd_mct4xx.h cmd_dlc.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h
cmd_mct420_meterparametersdisplaydigits.obj:	precompiled.h \
		cmd_mct420_MeterParametersDisplayDigits.h \
		cmd_mct420_MeterParameters.h cmd_mct420.h cmd_mct410.h \
		cmd_mct4xx.h cmd_dlc.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h
cmd_mct440_holidays.obj:	precompiled.h cmd_mct440_holidays.h cmd_dlc.h \
		cmd_device.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h ctidate.h
cmd_rfn.obj:	precompiled.h std_helper.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h
cmd_rfn_centronlcdconfiguration.obj:	precompiled.h \
		cmd_rfn_CentronLcdConfiguration.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_helper.h
cmd_rfn_channelconfiguration.obj:	precompiled.h \
		cmd_rfn_ChannelConfiguration.h ctidate.h dlldefs.h cmd_rfn.h \
		cmd_device.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_helper.h
cmd_rfn_demandfreeze.obj:	precompiled.h cmd_rfn_DemandFreeze.h \
		cmd_rfn.h cmd_device.h dev_single.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_helper.h
cmd_rfn_focusallcdconfiguration.obj:	precompiled.h std_helper.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		cmd_rfn_FocusAlLcdConfiguration.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_helper.h
cmd_rfn_helper.obj:	precompiled.h cmd_rfn_helper.h cmd_rfn.h \
		cmd_device.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h
cmd_rfn_loadprofile.obj:	precompiled.h cmd_rfn_LoadProfile.h cmd_rfn.h \
		cmd_device.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h ctidate.h cmd_rfn_helper.h
cmd_rfn_ovuvconfiguration.obj:	precompiled.h \
		cmd_rfn_OvUvConfiguration.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h ctidate.h cmd_rfn_helper.h
cmd_rfn_remotedisconnect.obj:	precompiled.h cmd_rfn_RemoteDisconnect.h \
		cmd_rfn.h cmd_device.h dev_single.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_helper.h
cmd_rfn_temperaturealarm.obj:	precompiled.h cmd_rfn_TemperatureAlarm.h \
		cmd_rfn.h cmd_device.h dev_single.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_helper.h
cmd_rfn_touconfiguration.obj:	precompiled.h std_helper.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		cmd_rfn_TouConfiguration.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h ctidate.h cmd_rfn_helper.h
cmd_rf_da_dnpaddress.obj:	precompiled.h cmd_rf_da_dnpAddress.h \
		cmd_rfn.h cmd_device.h dev_single.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_helper.h
conntest.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		connection_client.h connection.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		connection_server.h connection_listener.h amq_constants.h \
		msg_cmd.h msg_trace.h logManager.h module_util.h
dev_710.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dev_710.h dev_idlc.h types.h os2_2w32.h dsm2.h \
		streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h cti_asmc.h
dev_a1.obj:	precompiled.h porter.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h devicetypes.h dev_alpha.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		dev_a1.h pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_trace.h ctidate.h
dev_alpha.obj:	precompiled.h porter.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h devicetypes.h \
		dev_alpha.h dev_meter.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h
dev_ansi.obj:	precompiled.h porter.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h devicetypes.h dev_ansi.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		prot_ansi.h ansi_application.h ansi_datalink.h \
		ansi_billing_table.h std_ansi_tbl_base.h std_ansi_tbl_00.h \
		std_ansi_tbl_01.h std_ansi_tbl_08.h std_ansi_tbl_10.h \
		std_ansi_tbl_11.h std_ansi_tbl_12.h std_ansi_tbl_13.h \
		std_ansi_tbl_14.h std_ansi_tbl_15.h std_ansi_tbl_16.h \
		std_ansi_tbl_21.h std_ansi_tbl_22.h std_ansi_tbl_23.h \
		std_ansi_tbl_25.h std_ansi_tbl_27.h std_ansi_tbl_28.h \
		std_ansi_tbl_31.h std_ansi_tbl_32.h std_ansi_tbl_33.h \
		std_ansi_tbl_51.h std_ansi_tbl_52.h std_ansi_tbl_61.h \
		std_ansi_tbl_62.h std_ansi_tbl_63.h std_ansi_tbl_64.h \
		dllyukon.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h tbl_pt_control.h \
		pt_status.h tbl_pt_status.h tbl_pt_status_control.h ctidate.h
dev_aplus.obj:	precompiled.h porter.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h devicetypes.h \
		dev_alpha.h dev_meter.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		dev_aplus.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		msg_trace.h ctidate.h
dev_base.obj:	precompiled.h cparms.h dlldefs.h configkey.h configval.h \
		dev_base.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h row_reader.h rte_base.h \
		dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h mgr_route.h \
		repeaterrole.h slctdev.h smartmap.h readers_writer_lock.h \
		mgr_point.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h porter.h devicetypes.h database_writer.h \
		row_writer.h database_util.h mgr_dyn_paoinfo.h
dev_base_lite.obj:	precompiled.h dev_base_lite.h dlldefs.h \
		dbmemobject.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h
dev_carrier.obj:	precompiled.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h
dev_cbc.obj:	precompiled.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h porter.h devicetypes.h pt_base.h dbmemobject.h \
		tbl_pt_base.h row_reader.h dllbase.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h master.h \
		mgr_route.h repeaterrole.h rte_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		slctdev.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h smartmap.h readers_writer_lock.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		dev_cbc.h tbl_dv_cbc.h cparms.h configkey.h configval.h
dev_cbc6510.obj:	precompiled.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h porter.h devicetypes.h pt_base.h dbmemobject.h \
		tbl_pt_base.h row_reader.h dllbase.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h master.h mgr_route.h \
		repeaterrole.h rte_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h slctdev.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h smartmap.h \
		readers_writer_lock.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h dev_cbc6510.h dev_remote.h \
		dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h connection_base.h worker_thread.h xfer.h \
		exceptions.h tbl_dialup.h tbl_direct.h dev_dnp.h prot_dnp.h \
		prot_base.h packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h \
		tbl_dv_address.h tbl_dv_idlcremote.h
dev_cbc7020.obj:	precompiled.h dev_cbc7020.h dev_dnp.h dev_remote.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h prot_dnp.h \
		prot_base.h packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h \
		tbl_dv_address.h pointAttribute.h
dev_cbc8020.obj:	precompiled.h dev_cbc8020.h dev_cbc7020.h dev_dnp.h \
		dev_remote.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h prot_dnp.h \
		prot_base.h packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h \
		tbl_dv_address.h pointAttribute.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h
dev_ccu.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h porter.h devicetypes.h pt_base.h dbmemobject.h \
		tbl_pt_base.h row_reader.h dllbase.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h master.h \
		dev_ccu.h dev_idlc.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h xfer.h exceptions.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h \
		dev_ccu_queue_interface.h device_queue_interface.h prot_711.h
dev_ccu721.obj:	precompiled.h dev_ccu721.h dev_remote.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		tbl_dv_address.h device_queue_interface.h prot_klondike.h \
		prot_wrap.h prot_base.h prot_idlc.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_device.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h cmd_mct4xx.h ctidate.h mgr_route.h \
		repeaterrole.h slctdev.h smartmap.h
dev_ccu_queue_interface.obj:	precompiled.h dev_ccu_queue_interface.h \
		device_queue_interface.h dlldefs.h trx_711.h trx_info.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h porter.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		devicetypes.h
dev_davis.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h connection.h message.h ctitime.h ctidbgmem.h \
		collectable.h loggable.h msg_multi.h msg_pdata.h yukon.h \
		types.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h configkey.h configval.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h dev_davis.h \
		dev_ied.h dsm2.h streamConnection.h netports.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		row_reader.h rte_base.h dbmemobject.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dllbase.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h resolvers.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h msg_pcrequest.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h tbl_dv_ied.h \
		msg_cmd.h msg_lmcontrolhistory.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h
dev_dct501.obj:	precompiled.h devicetypes.h tbl_ptdispatch.h ctibase.h \
		streamSocketConnection.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h socket_helper.h win_helper.h dllbase.h dsm2.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbmemobject.h pointdefs.h row_reader.h database_connection.h \
		dbaccess.h dev_dct501.h dev_mct24x.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h porter.h
dev_dlcbase.obj:	precompiled.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h dev_mct.h dev_carrier.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h devicetypes.h msg_cmd.h porter.h
dev_dnp.obj:	precompiled.h dev_dnp.h dev_remote.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h prot_dnp.h \
		prot_base.h packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h \
		tbl_dv_address.h dnp_object_analogoutput.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_analog.h \
		tbl_pt_analog.h dllyukon.h msg_cmd.h msg_lmcontrolhistory.h
dev_dr87.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h porter.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h devicetypes.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_dr87.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_exclusion.obj:	precompiled.h dlldefs.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h cparms.h \
		configkey.h configval.h dev_exclusion.h tbl_paoexclusion.h \
		row_reader.h
dev_focus.obj:	precompiled.h porter.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h devicetypes.h \
		dev_focus.h dev_meter.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		dev_ansi.h prot_ansi.h ansi_application.h ansi_datalink.h \
		ansi_billing_table.h std_ansi_tbl_base.h std_ansi_tbl_00.h \
		std_ansi_tbl_01.h std_ansi_tbl_08.h std_ansi_tbl_10.h \
		std_ansi_tbl_11.h std_ansi_tbl_12.h std_ansi_tbl_13.h \
		std_ansi_tbl_14.h std_ansi_tbl_15.h std_ansi_tbl_16.h \
		std_ansi_tbl_21.h std_ansi_tbl_22.h std_ansi_tbl_23.h \
		std_ansi_tbl_25.h std_ansi_tbl_27.h std_ansi_tbl_28.h \
		std_ansi_tbl_31.h std_ansi_tbl_32.h std_ansi_tbl_33.h \
		std_ansi_tbl_51.h std_ansi_tbl_52.h std_ansi_tbl_61.h \
		std_ansi_tbl_62.h std_ansi_tbl_63.h std_ansi_tbl_64.h \
		dllyukon.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h tbl_pt_control.h \
		pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		prot_ansi_focus.h ansi_focus_mtable_004.h \
		ansi_focus_mtable_013.h ansi_focus_mtable_024.h \
		prot_ansi_sentinel.h ctidate.h
dev_fulcrum.obj:	precompiled.h ctidate.h dlldefs.h ctitime.h porter.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h devicetypes.h dev_schlum.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		dev_fulcrum.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_gridadvisor.obj:	precompiled.h porter.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h devicetypes.h \
		tbl_ptdispatch.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h dbmemobject.h \
		pointdefs.h row_reader.h database_connection.h dbaccess.h \
		dev_gridadvisor.h dev_dnp.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h prot_dnp.h \
		prot_base.h packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h \
		tbl_dv_address.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		msg_lmcontrolhistory.h dllyukon.h
dev_grp_emetcon.obj:	precompiled.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h porter.h devicetypes.h pt_base.h dbmemobject.h \
		tbl_pt_base.h row_reader.h dllbase.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		master.h connection.h message.h collectable.h msg_multi.h \
		msg_pdata.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h mgr_route.h repeaterrole.h \
		rte_base.h cmdparse.h ctitokenizer.h parsevalue.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		tbl_pao_lite.h tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h slctdev.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h smartmap.h \
		msg_pcrequest.h msg_pcreturn.h dev_grp_emetcon.h dev_grp.h \
		msg_lmcontrolhistory.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h tbl_dv_emetcon.h
dev_grp_expresscom.obj:	precompiled.h cmdparse.h ctitokenizer.h \
		dlldefs.h parsevalue.h dev_grp_expresscom.h dev_base.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h dev_grp.h cparms.h configkey.h \
		configval.h msg_lmcontrolhistory.h msg_pcrequest.h \
		msg_pdata.h msg_multi.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_dv_expresscom.h vcomdefs.h expresscom.h mgr_route.h \
		repeaterrole.h slctdev.h smartmap.h readers_writer_lock.h \
		msg_pcreturn.h devicetypes.h
dev_grp_golay.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dev_grp_golay.h dev_base.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h dev_grp.h cparms.h configkey.h \
		configval.h msg_lmcontrolhistory.h msg_pcrequest.h \
		msg_pdata.h msg_multi.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_lmg_sasimple.h expresscom.h mgr_route.h repeaterrole.h \
		slctdev.h smartmap.h readers_writer_lock.h msg_pcreturn.h
dev_grp_mct.obj:	precompiled.h dev_grp_mct.h dev_grp.h cparms.h \
		dlldefs.h configkey.h configval.h msg_lmcontrolhistory.h \
		pointdefs.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h yukon.h types.h msg_pcrequest.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_signal.h \
		msg_pdata.h pointtypes.h msg_multi.h pt_status.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h dbaccess.h \
		resolvers.h db_entry_defines.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		tbl_pao_lite.h tbl_rtcomm.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h tbl_dv_lmgmct.h msg_pcreturn.h porter.h \
		devicetypes.h prot_emetcon.h
dev_grp_point.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h cparms.h configkey.h configval.h dev_grp_point.h \
		dev_base.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_exclusion.h tbl_paoexclusion.h \
		row_reader.h rte_base.h dbmemobject.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dllbase.h message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		dev_grp.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_lmg_point.h mgr_route.h repeaterrole.h slctdev.h \
		smartmap.h readers_writer_lock.h msg_pcreturn.h
dev_grp_rfn_expresscom.obj:	precompiled.h cmdparse.h ctitokenizer.h \
		dlldefs.h parsevalue.h dev_grp_rfn_expresscom.h \
		dev_grp_expresscom.h dev_base.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_exclusion.h tbl_paoexclusion.h \
		row_reader.h rte_base.h dbmemobject.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dllbase.h message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		dev_grp.h cparms.h configkey.h configval.h \
		msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_dv_expresscom.h vcomdefs.h expresscom.h msg_pcreturn.h \
		devicetypes.h amq_connection.h thread.h StreamableMessage.h \
		connection_base.h RfnBroadcastReplyMessage.h \
		RfnBroadcastMessage.h
dev_grp_ripple.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h dev_grp_ripple.h dev_base.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h dev_grp.h msg_lmcontrolhistory.h \
		msg_pcrequest.h msg_pdata.h msg_multi.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h tbl_dv_lmg_ripple.h mgr_route.h \
		repeaterrole.h slctdev.h smartmap.h readers_writer_lock.h \
		msg_pcreturn.h msg_cmd.h porter.h devicetypes.h
dev_grp_sa105.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h cparms.h configkey.h configval.h dev_grp_sa105.h \
		dev_base.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_exclusion.h tbl_paoexclusion.h \
		row_reader.h rte_base.h dbmemobject.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dllbase.h message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		dev_grp.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_lmg_sa205105.h expresscom.h mgr_route.h repeaterrole.h \
		slctdev.h smartmap.h readers_writer_lock.h msg_pcreturn.h
dev_grp_sa205.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_grp_sa205.h dev_base.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_exclusion.h tbl_paoexclusion.h \
		row_reader.h rte_base.h dbmemobject.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dllbase.h message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		dev_grp.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_lmg_sa205105.h expresscom.h mgr_route.h repeaterrole.h \
		slctdev.h smartmap.h readers_writer_lock.h msg_pcreturn.h \
		prot_sa3rdparty.h prot_base.h xfer.h protocol_sa.h
dev_grp_sa305.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_grp_sa305.h dev_base.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_exclusion.h tbl_paoexclusion.h \
		row_reader.h rte_base.h dbmemobject.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dllbase.h message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		dev_grp.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		prot_sa305.h tbl_lmg_sa305.h expresscom.h mgr_route.h \
		repeaterrole.h slctdev.h smartmap.h readers_writer_lock.h \
		msg_pcreturn.h
dev_grp_sadigital.obj:	precompiled.h cmdparse.h ctitokenizer.h \
		dlldefs.h parsevalue.h dev_grp_sadigital.h dev_base.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h dev_grp.h cparms.h configkey.h \
		configval.h msg_lmcontrolhistory.h msg_pcrequest.h \
		msg_pdata.h msg_multi.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_lmg_sasimple.h expresscom.h mgr_route.h repeaterrole.h \
		slctdev.h smartmap.h readers_writer_lock.h msg_pcreturn.h
dev_grp_versacom.obj:	precompiled.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h porter.h devicetypes.h pt_base.h dbmemobject.h \
		tbl_pt_base.h row_reader.h dllbase.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		master.h connection.h message.h collectable.h msg_multi.h \
		msg_pdata.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h mgr_route.h repeaterrole.h \
		rte_base.h cmdparse.h ctitokenizer.h parsevalue.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		tbl_pao_lite.h tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h slctdev.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h smartmap.h \
		msg_pcrequest.h msg_pcreturn.h dev_grp_versacom.h dev_grp.h \
		msg_lmcontrolhistory.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h tbl_dv_versacom.h \
		vcomdefs.h
dev_ilex.obj:	precompiled.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h porter.h devicetypes.h dev_ilex.h dev_idlc.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_lmcontrolhistory.h
dev_ion.obj:	precompiled.h porter.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h devicetypes.h tbl_ptdispatch.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dllbase.h dbmemobject.h pointdefs.h row_reader.h \
		database_connection.h dbaccess.h dev_ion.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h resolvers.h pointtypes.h \
		db_entry_defines.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		tbl_dv_address.h prot_ion.h prot_base.h ion_datastream.h \
		ion_value.h ion_serializable.h ion_value_fixed.h \
		ion_value_numeric.h ion_value_variable.h \
		ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_lmcontrolhistory.h \
		dllyukon.h
dev_ipc410al.obj:	precompiled.h dev_ipc410al.h dlldefs.h dev_focus.h \
		dev_meter.h tbl_metergrp.h yukon.h types.h ctidbgmem.h \
		vcomdefs.h dbmemobject.h dllbase.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h porter.h \
		devicetypes.h tbl_dv_ied.h dev_ansi.h prot_ansi.h \
		ansi_application.h ansi_datalink.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_00.h std_ansi_tbl_01.h \
		std_ansi_tbl_08.h std_ansi_tbl_10.h std_ansi_tbl_11.h \
		std_ansi_tbl_12.h std_ansi_tbl_13.h std_ansi_tbl_14.h \
		std_ansi_tbl_15.h std_ansi_tbl_16.h std_ansi_tbl_21.h \
		std_ansi_tbl_22.h std_ansi_tbl_23.h std_ansi_tbl_25.h \
		std_ansi_tbl_27.h std_ansi_tbl_28.h std_ansi_tbl_31.h \
		std_ansi_tbl_32.h std_ansi_tbl_33.h std_ansi_tbl_51.h \
		std_ansi_tbl_52.h std_ansi_tbl_61.h std_ansi_tbl_62.h \
		std_ansi_tbl_63.h std_ansi_tbl_64.h dllyukon.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_pt_control.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h prot_ansi_focus.h \
		ansi_focus_mtable_004.h ansi_focus_mtable_013.h \
		ansi_focus_mtable_024.h prot_ansi_sentinel.h
dev_ipc420ad.obj:	precompiled.h dev_ipc420ad.h dlldefs.h dev_focus.h \
		dev_meter.h tbl_metergrp.h yukon.h types.h ctidbgmem.h \
		vcomdefs.h dbmemobject.h dllbase.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h porter.h \
		devicetypes.h tbl_dv_ied.h dev_ansi.h prot_ansi.h \
		ansi_application.h ansi_datalink.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_00.h std_ansi_tbl_01.h \
		std_ansi_tbl_08.h std_ansi_tbl_10.h std_ansi_tbl_11.h \
		std_ansi_tbl_12.h std_ansi_tbl_13.h std_ansi_tbl_14.h \
		std_ansi_tbl_15.h std_ansi_tbl_16.h std_ansi_tbl_21.h \
		std_ansi_tbl_22.h std_ansi_tbl_23.h std_ansi_tbl_25.h \
		std_ansi_tbl_27.h std_ansi_tbl_28.h std_ansi_tbl_31.h \
		std_ansi_tbl_32.h std_ansi_tbl_33.h std_ansi_tbl_51.h \
		std_ansi_tbl_52.h std_ansi_tbl_61.h std_ansi_tbl_62.h \
		std_ansi_tbl_63.h std_ansi_tbl_64.h dllyukon.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_pt_control.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h prot_ansi_focus.h \
		ansi_focus_mtable_004.h ansi_focus_mtable_013.h \
		ansi_focus_mtable_024.h prot_ansi_sentinel.h
dev_kv2.obj:	precompiled.h porter.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h devicetypes.h dev_kv2.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		prot_ansi_kv2.h prot_ansi.h ansi_application.h \
		ansi_datalink.h ansi_billing_table.h std_ansi_tbl_base.h \
		std_ansi_tbl_00.h std_ansi_tbl_01.h std_ansi_tbl_08.h \
		std_ansi_tbl_10.h std_ansi_tbl_11.h std_ansi_tbl_12.h \
		std_ansi_tbl_13.h std_ansi_tbl_14.h std_ansi_tbl_15.h \
		std_ansi_tbl_16.h std_ansi_tbl_21.h std_ansi_tbl_22.h \
		std_ansi_tbl_23.h std_ansi_tbl_25.h std_ansi_tbl_27.h \
		std_ansi_tbl_28.h std_ansi_tbl_31.h std_ansi_tbl_32.h \
		std_ansi_tbl_33.h std_ansi_tbl_51.h std_ansi_tbl_52.h \
		std_ansi_tbl_61.h std_ansi_tbl_62.h std_ansi_tbl_63.h \
		std_ansi_tbl_64.h ansi_kv2_mtable_000.h ansi_kv2_mtable_070.h \
		ansi_kv2_mtable_110.h dllyukon.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_pt_control.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h
dev_lcr3102.obj:	precompiled.h dev_lcr3102.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h dev_lcr3102_commands.h \
		cmd_lcr3102_tamperRead.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_lcr3102_DemandResponseSummary.h \
		cmd_lcr3102_hourlyDataLog.h tbl_dyn_lcrComms.h ctidate.h \
		date_utility.h
dev_lcu.obj:	precompiled.h cparms.h dlldefs.h configkey.h configval.h \
		connection.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h msg_multi.h msg_pdata.h yukon.h types.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_lcu.h dsm2.h streamConnection.h netports.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		dev_idlc.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h row_reader.h rte_base.h \
		dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h xfer.h \
		exceptions.h tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h master.h pt_accum.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h
dev_lgs4.obj:	precompiled.h porter.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h devicetypes.h dev_lgs4.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h ctidate.h
dev_lmi.obj:	precompiled.h dev_lmi.h dev_remote.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		tbl_dv_address.h tbl_dv_seriesv.h prot_lmi.h prot_seriesv.h \
		prot_base.h verification_objects.h boost_time.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h dllyukon.h
dev_macro.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h devicetypes.h message.h \
		collectable.h msg_pcrequest.h msg_pdata.h pointdefs.h \
		pointtypes.h dllbase.h dev_macro.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		tbl_pao_lite.h tbl_rtcomm.h dbaccess.h resolvers.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h dev_grp.h cparms.h configkey.h configval.h \
		msg_lmcontrolhistory.h msg_multi.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h dev_grp_ripple.h tbl_dv_lmg_ripple.h
dev_mark_v.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h porter.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h devicetypes.h dev_mark_v.h prot_transdata.h \
		transdata_application.h xfer.h transdata_tracker.h \
		transdata_datalink.h prot_ymodem.h dllbase.h ctidate.h \
		transdata_data.h dev_meter.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h exceptions.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		msg_cmd.h
dev_mct.obj:	precompiled.h numstr.h dlldefs.h devicetypes.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h dev_mct210.h dev_mct2xx.h \
		dev_mct31x.h dev_mct310.h dev_mct410.h dev_mct4xx.h \
		cmd_mct4xx.h ctidate.h dev_mct410_commands.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h dev_mct470.h \
		dev_mct_lmt2.h dev_mct22x.h msg_cmd.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		porter.h dllyukon.h desolvers.h
dev_mct210.obj:	precompiled.h devicetypes.h dev_mct210.h dev_mct2xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h porter.h
dev_mct22x.obj:	precompiled.h devicetypes.h dev_mct22X.h dev_mct2xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h porter.h
dev_mct24x.obj:	precompiled.h devicetypes.h dev_mct24X.h dev_mct2xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h porter.h dllyukon.h
dev_mct2xx.obj:	precompiled.h devicetypes.h dev_mct2XX.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h porter.h
dev_mct310.obj:	precompiled.h devicetypes.h dev_mct310.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h porter.h dllyukon.h
dev_mct31x.obj:	precompiled.h devicetypes.h tbl_ptdispatch.h ctibase.h \
		streamSocketConnection.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h socket_helper.h win_helper.h dllbase.h dsm2.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbmemobject.h pointdefs.h row_reader.h database_connection.h \
		dbaccess.h dev_mct31X.h dev_mct310.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h dllyukon.h ctidate.h
dev_mct410.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h dllyukon.h date_utility.h ctidate.h \
		config_helpers.h config_exceptions.h dev_mct410.h \
		dev_mct4xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h tbl_ptdispatch.h \
		pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h portglob.h streamSocketListener.h porter.h \
		devicetypes.h
dev_mct420.obj:	precompiled.h dev_mct420.h dev_mct410.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h dev_mct420_commands.h \
		cmd_mct420_LcdConfiguration.h cmd_mct420.h \
		cmd_mct420_hourlyRead.h cmd_mct420_MeterParameters.h \
		cmd_mct420_MeterParametersDisplayDigits.h devicetypes.h
dev_mct440_2131b.obj:	precompiled.h dev_mct440_2131b.h \
		dev_mct440_213xb.h dev_mct420.h dev_mct410.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h
dev_mct440_2132b.obj:	precompiled.h dev_mct440_2132b.h \
		dev_mct440_213xb.h dev_mct420.h dev_mct410.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h
dev_mct440_2133b.obj:	precompiled.h dev_mct440_2133b.h \
		dev_mct440_213xb.h dev_mct420.h dev_mct410.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h
dev_mct440_213xb.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h dev_mct440_213xb.h dev_mct420.h \
		dev_mct410.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h date_utility.h \
		tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h portglob.h \
		streamSocketListener.h porter.h devicetypes.h dllyukon.h \
		eventlog_mct440_213xb.h cmd_mct440_holidays.h
dev_mct470.obj:	precompiled.h devicetypes.h tbl_ptdispatch.h ctibase.h \
		streamSocketConnection.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h socket_helper.h win_helper.h dllbase.h dsm2.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbmemobject.h pointdefs.h row_reader.h database_connection.h \
		dbaccess.h dev_mct470.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		dllyukon.h config_exceptions.h
dev_mct4xx.obj:	precompiled.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct470.h dev_mct410.h dev_mct410_commands.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h devicetypes.h \
		date_utility.h
dev_mct_broadcast.obj:	precompiled.h dev_mct_broadcast.h dev_dlcbase.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h porter.h devicetypes.h dev_mct.h dev_carrier.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h dev_mct31x.h dev_mct310.h dev_mct4xx.h \
		cmd_mct4xx.h ctidate.h
dev_mct_lmt2.obj:	precompiled.h devicetypes.h dev_mct_lmt2.h \
		dev_mct22x.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h
dev_meter.obj:	precompiled.h dev_meter.h tbl_metergrp.h yukon.h \
		types.h ctidbgmem.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h porter.h \
		devicetypes.h tbl_dv_ied.h
dev_modbus.obj:	precompiled.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h porter.h devicetypes.h pt_base.h dbmemobject.h \
		tbl_pt_base.h row_reader.h dllbase.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h pointdefs.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h master.h dllyukon.h mgr_route.h \
		repeaterrole.h rte_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h slctdev.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h smartmap.h \
		readers_writer_lock.h mgr_point.h msg_cmd.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h msg_lmcontrolhistory.h \
		dev_modbus.h dev_remote.h dev_single.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h connection_base.h \
		worker_thread.h xfer.h exceptions.h tbl_dialup.h tbl_direct.h \
		prot_modbus.h prot_base.h tbl_dv_address.h
dev_paging.obj:	precompiled.h dev_paging.h tbl_dv_tappaging.h \
		dlldefs.h dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h porter.h \
		devicetypes.h tbl_dv_ied.h
dev_pagingreceiver.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_rtm.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h row_reader.h rte_base.h \
		dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h porter.h \
		devicetypes.h tbl_dv_ied.h verification_objects.h \
		boost_time.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		port_base.h logManager.h module_util.h tbl_port_base.h \
		mgr_route.h repeaterrole.h slctdev.h smartmap.h msg_trace.h \
		dev_pagingreceiver.h tbl_dv_pagingreceiver.h
dev_quantum.obj:	precompiled.h ctidate.h dlldefs.h ctitime.h numstr.h \
		porter.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h devicetypes.h \
		dev_schlum.h dev_meter.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		dev_quantum.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_rds.obj:	precompiled.h dev_rds.h dev_remote.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		encryption_oneway_message.h
dev_repeater.obj:	precompiled.h devicetypes.h dev_repeater.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h porter.h
dev_repeater800.obj:	precompiled.h devicetypes.h dev_repeater800.h \
		dev_repeater.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		porter.h
dev_repeater850.obj:	precompiled.h devicetypes.h dev_repeater850.h \
		dev_repeater800.h dev_repeater.h dev_dlcbase.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		porter.h
dev_rfn.obj:	precompiled.h dev_rfn.h rfn_identifier.h streamBuffer.h \
		dlldefs.h loggable.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h tbl_rfnidentifier.h
dev_rfn420centron.obj:	precompiled.h dev_rfn420centron.h \
		dev_rfnResidential.h dev_rfnMeter.h dev_rfn.h \
		rfn_identifier.h streamBuffer.h dlldefs.h loggable.h \
		cmd_rfn.h cmd_device.h dev_single.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_LoadProfile.h ctidate.h \
		cmd_rfn_DemandFreeze.h cmd_rfn_TouConfiguration.h \
		cmd_rfn_OvUvConfiguration.h cmd_rfn_RemoteDisconnect.h \
		cmd_rfn_CentronLcdConfiguration.h config_helpers.h \
		config_exceptions.h
dev_rfn420focus_al.obj:	precompiled.h config_helpers.h \
		config_exceptions.h dev_rfn420focus_al.h dev_rfnResidential.h \
		dev_rfnMeter.h dev_rfn.h rfn_identifier.h streamBuffer.h \
		dlldefs.h loggable.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_LoadProfile.h ctidate.h \
		cmd_rfn_DemandFreeze.h cmd_rfn_TouConfiguration.h \
		cmd_rfn_OvUvConfiguration.h cmd_rfn_RemoteDisconnect.h \
		cmd_rfn_FocusAlLcdConfiguration.h
dev_rfncommercial.obj:	precompiled.h std_helper.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h config_exceptions.h \
		dev_rfnCommercial.h dev_rfnMeter.h dev_rfn.h rfn_identifier.h \
		streamBuffer.h loggable.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h logger.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h rfn_asid.h cmd_rfn_LoadProfile.h \
		ctidate.h cmd_rfn_DemandFreeze.h cmd_rfn_TouConfiguration.h \
		cmd_rfn_OvUvConfiguration.h devicetypes.h
dev_rfnmeter.obj:	precompiled.h dev_rfnMeter.h dev_rfn.h \
		rfn_identifier.h streamBuffer.h dlldefs.h loggable.h \
		cmd_rfn.h cmd_device.h dev_single.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h tbl_rfnidentifier.h config_helpers.h \
		config_exceptions.h cmd_rfn_TemperatureAlarm.h \
		cmd_rfn_ChannelConfiguration.h ctidate.h PointAttribute.h \
		MetricIdLookup.h
dev_rfnresidential.obj:	precompiled.h std_helper.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h config_helpers.h \
		config_exceptions.h dev_rfnResidential.h dev_rfnMeter.h \
		dev_rfn.h rfn_identifier.h streamBuffer.h loggable.h \
		cmd_rfn.h cmd_device.h dev_single.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h logger.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h row_reader.h rte_base.h \
		dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_LoadProfile.h ctidate.h \
		cmd_rfn_DemandFreeze.h cmd_rfn_TouConfiguration.h \
		cmd_rfn_OvUvConfiguration.h cmd_rfn_RemoteDisconnect.h \
		devicetypes.h
dev_rf_da.obj:	precompiled.h dev_rf_da.h dev_rfn.h rfn_identifier.h \
		streamBuffer.h dlldefs.h loggable.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rf_da_dnpAddress.h
dev_rtc.obj:	precompiled.h cparms.h dlldefs.h configkey.h configval.h \
		dsm2err.h yukon.h types.h ctidbgmem.h dev_rtc.h dev_remote.h \
		dev_single.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h porter.h \
		devicetypes.h tbl_dv_rtc.h msg_cmd.h msg_lmcontrolhistory.h \
		protocol_sa.h prot_sa305.h prot_sa3rdparty.h prot_base.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		verification_objects.h boost_time.h
dev_rtm.obj:	precompiled.h cparms.h dlldefs.h configkey.h configval.h \
		dsm2err.h yukon.h types.h ctidbgmem.h dev_rtm.h dev_ied.h \
		os2_2w32.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h words.h optional.h macro_offset.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h porter.h \
		devicetypes.h tbl_dv_ied.h verification_objects.h \
		boost_time.h msg_cmd.h protocol_sa.h prot_sa3rdparty.h \
		prot_base.h prot_sa305.h
dev_schlum.obj:	precompiled.h porter.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h devicetypes.h \
		dev_schlum.h dev_meter.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h
dev_sentinel.obj:	precompiled.h porter.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h devicetypes.h \
		dev_sentinel.h dev_meter.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		dev_ansi.h prot_ansi.h ansi_application.h ansi_datalink.h \
		ansi_billing_table.h std_ansi_tbl_base.h std_ansi_tbl_00.h \
		std_ansi_tbl_01.h std_ansi_tbl_08.h std_ansi_tbl_10.h \
		std_ansi_tbl_11.h std_ansi_tbl_12.h std_ansi_tbl_13.h \
		std_ansi_tbl_14.h std_ansi_tbl_15.h std_ansi_tbl_16.h \
		std_ansi_tbl_21.h std_ansi_tbl_22.h std_ansi_tbl_23.h \
		std_ansi_tbl_25.h std_ansi_tbl_27.h std_ansi_tbl_28.h \
		std_ansi_tbl_31.h std_ansi_tbl_32.h std_ansi_tbl_33.h \
		std_ansi_tbl_51.h std_ansi_tbl_52.h std_ansi_tbl_61.h \
		std_ansi_tbl_62.h std_ansi_tbl_63.h std_ansi_tbl_64.h \
		dllyukon.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h tbl_pt_control.h \
		pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		prot_ansi_sentinel.h ctidate.h
dev_seriesv.obj:	precompiled.h dev_seriesv.h dev_ied.h types.h \
		os2_2w32.h dlldefs.h dsm2.h streamConnection.h yukon.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h tbl_dv_ied.h \
		prot_seriesv.h prot_base.h
dev_single.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h porter.h devicetypes.h tbl_ptdispatch.h \
		ctidate.h scanglob.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h dllyukon.h \
		prot_base.h desolvers.h
dev_sixnet.obj:	precompiled.h dev_sixnet.h dev_meter.h tbl_metergrp.h \
		yukon.h types.h ctidbgmem.h vcomdefs.h dlldefs.h \
		dbmemobject.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h porter.h \
		devicetypes.h tbl_dv_ied.h prot_sixnet.h msg_cmd.h \
		pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h
dev_snpp.obj:	precompiled.h cparms.h dlldefs.h configkey.h configval.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h cmdparse.h ctitokenizer.h parsevalue.h \
		pt_base.h dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		port_base.h logManager.h module_util.h tbl_port_base.h \
		tbl_pao_lite.h tbl_paoexclusion.h xfer.h dev_base.h \
		dev_exclusion.h rte_base.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h connection.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h mgr_route.h repeaterrole.h \
		slctdev.h smartmap.h msg_pcrequest.h msg_pcreturn.h \
		msg_trace.h verification_objects.h boost_time.h dev_snpp.h \
		dev_paging.h tbl_dv_tappaging.h dev_ied.h dev_remote.h \
		dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h exceptions.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		encryption_oneway_message.h
dev_system.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h dev_system.h dev_base.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h row_reader.h rte_base.h \
		dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h connection.h \
		msg_ptreg.h msg_reg.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h prot_versacom.h rte_xcu.h \
		smartmap.h porter.h devicetypes.h mgr_route.h repeaterrole.h \
		slctdev.h dev_mct410.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h \
		xfer.h exceptions.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		cmd_dlc.h cmd_device.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h \
		ctidate.h dev_mct410_commands.h cmd_mct410_hourlyRead.h \
		cmd_mct410.h cmd_mct410_disconnectConfiguration.h
dev_tap.obj:	precompiled.h cparms.h dlldefs.h configkey.h configval.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h cmdparse.h ctitokenizer.h parsevalue.h \
		pt_base.h dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		connection.h message.h collectable.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h mgr_route.h repeaterrole.h \
		rte_base.h ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h tbl_pao_lite.h tbl_rtcomm.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h slctdev.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		smartmap.h msg_pcrequest.h msg_pcreturn.h msg_trace.h \
		verification_objects.h boost_time.h dev_tap.h dev_paging.h \
		tbl_dv_tappaging.h dev_ied.h dev_remote.h dev_single.h \
		tbl_dv_scandata.h tbl_dv_wnd.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		encryption_oneway_message.h
dev_tcu.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h cparms.h configkey.h configval.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h pt_base.h dbmemobject.h tbl_pt_base.h \
		row_reader.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h master.h dev_tcu.h dev_idlc.h \
		dev_remote.h dev_single.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		msg_ptreg.h msg_reg.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h
dev_tnpp.obj:	precompiled.h cparms.h dlldefs.h configkey.h configval.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_rtm.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h porter.h \
		devicetypes.h tbl_dv_ied.h verification_objects.h \
		boost_time.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		port_base.h logManager.h module_util.h tbl_port_base.h \
		mgr_route.h repeaterrole.h slctdev.h smartmap.h msg_trace.h \
		dev_tnpp.h tbl_dv_tnpp.h encryption_oneway_message.h
dev_vectron.obj:	precompiled.h ctidate.h dlldefs.h ctitime.h porter.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h devicetypes.h dev_schlum.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		dev_vectron.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_wctp.obj:	precompiled.h cparms.h dlldefs.h configkey.h configval.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h porter.h \
		devicetypes.h pt_base.h dbmemobject.h tbl_pt_base.h \
		row_reader.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h pt_accum.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h connection.h message.h collectable.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_route.h repeaterrole.h rte_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		tbl_pao_lite.h tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h slctdev.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h smartmap.h \
		msg_pcrequest.h msg_pcreturn.h msg_trace.h dev_wctp.h \
		dev_paging.h tbl_dv_tappaging.h dev_ied.h dev_remote.h \
		dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h xfer.h \
		exceptions.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		encryption_oneway_message.h verification_objects.h \
		boost_time.h ctidate.h
dev_welco.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dllyukon.h porter.h devicetypes.h dev_welco.h \
		dev_idlc.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h prot_welco.h \
		pt_status.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_lmcontrolhistory.h
dlldev.obj:	precompiled.h module_util.h dlldefs.h ctitime.h
encryption_cbcrbt.obj:	precompiled.h encryption_cbcrbt.h dlldefs.h
encryption_cmac.obj:	precompiled.h encryption_cmac.h dlldefs.h
encryption_oneway.obj:	precompiled.h encryption_oneway.h dlldefs.h \
		encryption_cbcrbt.h encryption_cmac.h CtiTime.h CtiDate.h
encryption_oneway_message.obj:	precompiled.h encryption_cbcrbt.h \
		dlldefs.h encryption_cmac.h encryption_oneway.h \
		encryption_oneway_message.h CtiTime.h mutex.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h guard.h CParms.h configkey.h configval.h
eventlog_mct440_213xb.obj:	precompiled.h numstr.h dlldefs.h \
		eventlog_mct440_213xb.h
id_devdll.obj:	precompiled.h id_devdll.h module_util.h dlldefs.h \
		ctitime.h
id_dynpaoinfo.obj:	precompiled.h id_dynpaoinfo.h module_util.h \
		dlldefs.h ctitime.h
id_pntdll.obj:	precompiled.h id_pntdll.h module_util.h dlldefs.h \
		ctitime.h
id_prtdll.obj:	precompiled.h id_prtdll.h module_util.h dlldefs.h \
		ctitime.h
key_password_encryptor.obj:	precompiled.h encryption_cbcrbt.h \
		dlldefs.h encryption_cmac.h encryption_oneway.h
memtest.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
mgr_device.obj:	precompiled.h mgr_device.h dlldefs.h rtdb.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h slctdev.h smartmap.h \
		readers_writer_lock.h debug_timer.h cparms.h configkey.h \
		configval.h database_reader.h database_util.h \
		database_writer.h row_writer.h dev_macro.h dev_grp.h \
		msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		dev_cbc.h tbl_dv_cbc.h dev_dnp.h dev_remote.h dev_single.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		msg_ptreg.h msg_reg.h queue.h connection_base.h \
		worker_thread.h xfer.h exceptions.h tbl_dialup.h tbl_direct.h \
		porter.h devicetypes.h prot_dnp.h prot_base.h packet_finder.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h tbl_dv_address.h dev_ion.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dev_ied.h tbl_dv_ied.h \
		prot_ion.h ion_datastream.h ion_value.h ion_serializable.h \
		ion_value_fixed.h ion_value_numeric.h ion_value_variable.h \
		ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h dev_gridadvisor.h \
		dev_idlc.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		dev_ccu721.h device_queue_interface.h prot_klondike.h \
		prot_wrap.h prot_idlc.h dev_carrier.h dev_dlcbase.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h dev_lmi.h tbl_dv_seriesv.h prot_lmi.h \
		prot_seriesv.h verification_objects.h boost_time.h dev_mct.h \
		dev_mct410.h dev_mct4xx.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h dev_modbus.h \
		prot_modbus.h dev_repeater.h dev_rfn.h rfn_identifier.h \
		cmd_rfn.h rfn_asid.h dev_rtc.h tbl_dv_rtc.h dev_rtm.h \
		dev_tap.h dev_paging.h tbl_dv_tappaging.h \
		encryption_oneway_message.h dev_snpp.h dev_tnpp.h \
		tbl_dv_tnpp.h dev_pagingreceiver.h tbl_dv_pagingreceiver.h \
		dev_grp_emetcon.h tbl_dv_emetcon.h dev_grp_expresscom.h \
		tbl_dv_expresscom.h dev_grp_rfn_expresscom.h dev_grp_golay.h \
		tbl_lmg_sasimple.h dev_grp_point.h tbl_lmg_point.h \
		dev_grp_ripple.h tbl_dv_lmg_ripple.h dev_grp_sa105.h \
		tbl_lmg_sa205105.h dev_grp_sa305.h prot_sa305.h \
		tbl_lmg_sa305.h dev_grp_sa205.h dev_grp_sadigital.h \
		dev_grp_versacom.h tbl_dv_versacom.h dev_grp_mct.h \
		tbl_dv_lmgmct.h dev_mct_broadcast.h dev_rds.h dev_lcr3102.h \
		dev_lcr3102_commands.h cmd_lcr3102_tamperRead.h \
		cmd_lcr3102_ThreePart.h cmd_lcr3102.h \
		cmd_lcr3102_DemandResponseSummary.h \
		cmd_lcr3102_hourlyDataLog.h tbl_dyn_lcrComms.h desolvers.h
mgr_dyn_paoinfo.obj:	precompiled.h mgr_dyn_paoinfo.h dlldefs.h \
		tbl_dyn_paoinfo.h dbmemobject.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h row_reader.h readers_writer_lock.h \
		database_reader.h database_util.h database_writer.h \
		row_writer.h std_helper.h
mgr_holiday.obj:	precompiled.h ctidbgmem.h mgr_holiday.h ctidate.h \
		dlldefs.h ctitime.h mutex.h guard.h utility.h queues.h \
		cticalls.h yukon.h types.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h database_connection.h row_reader.h
mgr_point.obj:	precompiled.h ctidbgmem.h pt_base.h dbmemobject.h \
		tbl_pt_base.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h mgr_point.h smartmap.h \
		readers_writer_lock.h devicetypes.h pt_accum.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h pt_analog.h tbl_pt_analog.h \
		tbl_pt_control.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h desolvers.h module_util.h cparms.h \
		configkey.h configval.h database_reader.h \
		database_connection.h database_util.h database_writer.h \
		row_writer.h
mgr_port.obj:	precompiled.h mgr_port.h dlldefs.h smartmap.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h dllbase.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		readers_writer_lock.h port_base.h logManager.h module_util.h \
		tbl_port_base.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h tbl_pao_lite.h \
		tbl_paoexclusion.h xfer.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h port_direct.h port_serial.h tbl_port_settings.h \
		tbl_port_timing.h port_dialable.h port_modem.h \
		tbl_port_dialup.h tbl_port_serial.h port_dialin.h \
		port_dialout.h port_pool_out.h port_rf_da.h rfn_identifier.h \
		port_tcpipdirect.h tbl_port_tcpip.h port_tcp.h port_udp.h \
		database_reader.h database_util.h database_writer.h \
		row_writer.h
mgr_route.obj:	precompiled.h mgr_route.h repeaterrole.h dlldefs.h \
		rte_base.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbmemobject.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		row_reader.h tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h slctdev.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h smartmap.h readers_writer_lock.h \
		rte_xcu.h msg_pcrequest.h rte_ccu.h tbl_rtcarrier.h \
		tbl_rtrepeater.h rte_versacom.h tbl_rtversacom.h master.h \
		dev_remote.h dev_single.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h connection_base.h worker_thread.h xfer.h \
		exceptions.h tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		rte_expresscom.h rte_macro.h tbl_rtmacro.h database_reader.h \
		database_util.h database_writer.h row_writer.h
mgr_season.obj:	precompiled.h ctidbgmem.h mgr_season.h ctidate.h \
		dlldefs.h ctitime.h mutex.h guard.h utility.h queues.h \
		cticalls.h yukon.h types.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h database_connection.h row_reader.h
points.obj:	precompiled.h pt_accum.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h \
		critical_section.h pt_numeric.h row_reader.h pt_base.h \
		dbmemobject.h tbl_pt_base.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h pt_analog.h tbl_pt_analog.h \
		tbl_pt_control.h
port_base.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h port_base.h logManager.h module_util.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h tbl_port_base.h dbmemobject.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h tbl_pao_lite.h tbl_paoexclusion.h xfer.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h rte_base.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h prot_emetcon.h color.h porter.h msg_trace.h
port_dialable.obj:	precompiled.h port_dialable.h port_base.h \
		logManager.h dlldefs.h module_util.h ctitime.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h tbl_port_base.h dbmemobject.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h tbl_pao_lite.h tbl_paoexclusion.h xfer.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h rte_base.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h port_modem.h tbl_port_dialup.h
port_dialin.obj:	precompiled.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h port_dialin.h port_base.h logManager.h \
		module_util.h tbl_port_base.h dbmemobject.h dbaccess.h \
		dllbase.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h tbl_pao_lite.h tbl_paoexclusion.h xfer.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h rte_base.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h port_dialable.h port_modem.h tbl_port_dialup.h
port_dialout.obj:	precompiled.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h port_dialout.h port_base.h logManager.h \
		module_util.h tbl_port_base.h dbmemobject.h dbaccess.h \
		dllbase.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h tbl_pao_lite.h tbl_paoexclusion.h xfer.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h rte_base.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h port_dialable.h port_modem.h tbl_port_dialup.h
port_direct.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h port_direct.h port_serial.h port_base.h \
		logManager.h module_util.h tbl_port_base.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h tbl_pao_lite.h tbl_paoexclusion.h xfer.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h rte_base.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h tbl_port_settings.h tbl_port_timing.h \
		port_dialable.h port_modem.h tbl_port_dialup.h \
		tbl_port_serial.h
port_modem.obj:	precompiled.h numstr.h dlldefs.h port_modem.h \
		port_base.h logManager.h module_util.h ctitime.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h critical_section.h \
		tbl_port_base.h dbmemobject.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		tbl_pao_lite.h tbl_paoexclusion.h xfer.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		rte_base.h ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h
port_pool_out.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h port_pool_out.h port_base.h logManager.h \
		module_util.h ctitime.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h critical_section.h tbl_port_base.h \
		dbmemobject.h dbaccess.h dllbase.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h tbl_pao_lite.h \
		tbl_paoexclusion.h xfer.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h
port_rf_da.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h port_rf_da.h port_base.h logManager.h \
		module_util.h tbl_port_base.h dbmemobject.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h tbl_pao_lite.h tbl_paoexclusion.h xfer.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h rte_base.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h message.h collectable.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h rfn_identifier.h tbl_rfnidentifier.h
port_serial.obj:	precompiled.h port_serial.h port_base.h logManager.h \
		dlldefs.h module_util.h ctitime.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h critical_section.h \
		tbl_port_base.h dbmemobject.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		tbl_pao_lite.h tbl_paoexclusion.h xfer.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		rte_base.h ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h tbl_port_settings.h tbl_port_timing.h
port_tcp.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h port_tcp.h port_serial.h port_base.h \
		logManager.h module_util.h tbl_port_base.h dbmemobject.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h tbl_pao_lite.h \
		tbl_paoexclusion.h xfer.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h tbl_port_settings.h tbl_port_timing.h
port_tcpipdirect.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h port_tcpipdirect.h port_serial.h \
		port_base.h logManager.h module_util.h tbl_port_base.h \
		dbmemobject.h dbaccess.h dllbase.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h tbl_pao_lite.h \
		tbl_paoexclusion.h xfer.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h tbl_port_settings.h tbl_port_timing.h \
		port_dialable.h port_modem.h tbl_port_dialup.h \
		tbl_port_tcpip.h
port_udp.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h port_udp.h port_serial.h port_base.h \
		logManager.h module_util.h tbl_port_base.h dbmemobject.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h tbl_pao_lite.h \
		tbl_paoexclusion.h xfer.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h rte_base.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_rtcomm.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		devicetypes.h tbl_port_settings.h tbl_port_timing.h \
		tbl_port_tcpip.h
pt_base.obj:	precompiled.h pt_base.h dbmemobject.h tbl_pt_base.h \
		row_reader.h ctitime.h dlldefs.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h tbl_pt_alarm.h
pt_dyn_dispatch.obj:	precompiled.h pt_dyn_dispatch.h tbl_pt_alarm.h \
		dlldefs.h dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h tbl_ptdispatch.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h pointdefs.h database_connection.h
pt_numeric.obj:	precompiled.h pt_numeric.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h row_reader.h \
		pt_base.h dbmemobject.h tbl_pt_base.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_alarm.h
pt_status.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h pt_status.h pt_base.h dbmemobject.h \
		tbl_pt_base.h row_reader.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h tbl_pt_alarm.h
routetest.obj:	precompiled.h logManager.h dlldefs.h module_util.h \
		ctitime.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h mgr_route.h repeaterrole.h rte_base.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h row_reader.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		slctdev.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		smartmap.h readers_writer_lock.h rtdb.h
rte_ccu.obj:	precompiled.h rte_ccu.h rte_xcu.h dev_base.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h smartmap.h \
		readers_writer_lock.h tbl_rtcarrier.h tbl_rtrepeater.h \
		dev_ccu.h dev_idlc.h dev_remote.h dev_single.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h connection_base.h worker_thread.h \
		xfer.h exceptions.h tbl_dialup.h tbl_direct.h porter.h \
		devicetypes.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		dev_ccu_queue_interface.h device_queue_interface.h \
		prot_versacom.h prot_emetcon.h expresscom.h
rte_expresscom.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h porter.h devicetypes.h rte_expresscom.h \
		rte_xcu.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		row_reader.h rte_base.h dbmemobject.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dllbase.h message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h smartmap.h readers_writer_lock.h \
		tbl_rtversacom.h master.h dev_remote.h dev_single.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h connection_base.h \
		worker_thread.h xfer.h exceptions.h tbl_dialup.h tbl_direct.h \
		expresscom.h
rte_macro.obj:	precompiled.h rte_macro.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h tbl_rtmacro.h \
		dbmemobject.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h critical_section.h \
		rte_base.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		row_reader.h tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h pointdefs.h porter.h devicetypes.h
rte_versacom.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h porter.h devicetypes.h rte_versacom.h \
		rte_xcu.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		row_reader.h rte_base.h dbmemobject.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dllbase.h message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h std_helper.h \
		tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h pointdefs.h \
		msg_pcrequest.h smartmap.h readers_writer_lock.h \
		tbl_rtversacom.h master.h dev_remote.h dev_single.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h connection_base.h \
		worker_thread.h xfer.h exceptions.h tbl_dialup.h tbl_direct.h \
		prot_versacom.h
rte_xcu.obj:	precompiled.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h expresscom.h cmdparse.h ctitokenizer.h \
		parsevalue.h dllbase.h rte_xcu.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h smartmap.h \
		readers_writer_lock.h master.h dev_remote.h dev_single.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h connection_base.h \
		worker_thread.h xfer.h exceptions.h tbl_dialup.h tbl_direct.h \
		porter.h devicetypes.h dev_tap.h dev_paging.h \
		tbl_dv_tappaging.h dev_ied.h tbl_dv_ied.h \
		encryption_oneway_message.h dev_snpp.h dev_tnpp.h \
		tbl_dv_tnpp.h dev_pagingreceiver.h tbl_dv_pagingreceiver.h \
		boost_time.h dev_lcu.h dev_idlc.h tbl_dv_idlcremote.h \
		trx_info.h trx_711.h dev_wctp.h prot_versacom.h prot_fpcbc.h \
		prot_sa305.h prot_sa3rdparty.h prot_base.h protocol_sa.h \
		prot_lmi.h prot_seriesv.h verification_objects.h
slctdev.obj:	precompiled.h dev_710.h dev_idlc.h types.h os2_2w32.h \
		dlldefs.h dsm2.h streamConnection.h yukon.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h row_reader.h rte_base.h \
		dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h dev_macro.h \
		dev_grp.h msg_lmcontrolhistory.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		dev_cbc6510.h dev_dnp.h prot_dnp.h prot_base.h \
		packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h \
		tbl_dv_address.h dev_cbc.h tbl_dv_cbc.h dev_cbc7020.h \
		pointAttribute.h dev_cbc8020.h dev_ccu.h \
		dev_ccu_queue_interface.h device_queue_interface.h \
		dev_ccu721.h prot_klondike.h prot_wrap.h prot_idlc.h \
		dev_welco.h prot_welco.h dev_ilex.h dev_seriesv.h dev_ied.h \
		tbl_dv_ied.h prot_seriesv.h dev_lmi.h tbl_dv_seriesv.h \
		prot_lmi.h verification_objects.h boost_time.h dev_tcu.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dev_gridadvisor.h \
		dev_modbus.h prot_modbus.h dev_schlum.h dev_fulcrum.h \
		dev_ion.h prot_ion.h ion_datastream.h ion_value.h \
		ion_serializable.h ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable.h ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h dev_lcu.h dev_quantum.h \
		dev_vectron.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_device.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		dev_rtm.h dev_tap.h dev_paging.h tbl_dv_tappaging.h \
		encryption_oneway_message.h dev_snpp.h dev_pagingreceiver.h \
		tbl_dv_pagingreceiver.h dev_tnpp.h tbl_dv_tnpp.h dev_wctp.h \
		dev_grp_emetcon.h tbl_dv_emetcon.h dev_grp_expresscom.h \
		tbl_dv_expresscom.h dev_grp_rfn_expresscom.h dev_grp_golay.h \
		tbl_lmg_sasimple.h dev_grp_mct.h tbl_dv_lmgmct.h \
		dev_grp_point.h tbl_lmg_point.h dev_grp_ripple.h \
		tbl_dv_lmg_ripple.h dev_grp_sa105.h tbl_lmg_sa205105.h \
		dev_grp_sa205.h dev_grp_sa305.h prot_sa305.h tbl_lmg_sa305.h \
		dev_grp_sadigital.h dev_grp_versacom.h tbl_dv_versacom.h \
		dev_davis.h dev_system.h dev_aplus.h dev_alpha.h dev_a1.h \
		dev_lgs4.h dev_lcr3102.h dev_lcr3102_commands.h \
		cmd_lcr3102_tamperRead.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_lcr3102_DemandResponseSummary.h \
		cmd_lcr3102_hourlyDataLog.h tbl_dyn_lcrComms.h dev_dr87.h \
		dev_dct501.h dev_mct24x.h dev_mct2xx.h dev_mct.h dev_mct210.h \
		dev_mct22X.h dev_mct310.h dev_mct31X.h dev_mct410.h \
		dev_mct4xx.h cmd_mct4xx.h ctidate.h dev_mct410_commands.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h dev_mct420.h \
		dev_mct470.h dev_mct440_2131b.h dev_mct440_213xb.h \
		dev_mct440_2132b.h dev_mct440_2133b.h dev_mct_lmt2.h \
		dev_mct_broadcast.h dev_kv2.h prot_ansi_kv2.h prot_ansi.h \
		ansi_application.h ansi_datalink.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_00.h std_ansi_tbl_01.h \
		std_ansi_tbl_08.h std_ansi_tbl_10.h std_ansi_tbl_11.h \
		std_ansi_tbl_12.h std_ansi_tbl_13.h std_ansi_tbl_14.h \
		std_ansi_tbl_15.h std_ansi_tbl_16.h std_ansi_tbl_21.h \
		std_ansi_tbl_22.h std_ansi_tbl_23.h std_ansi_tbl_25.h \
		std_ansi_tbl_27.h std_ansi_tbl_28.h std_ansi_tbl_31.h \
		std_ansi_tbl_32.h std_ansi_tbl_33.h std_ansi_tbl_51.h \
		std_ansi_tbl_52.h std_ansi_tbl_61.h std_ansi_tbl_62.h \
		std_ansi_tbl_63.h std_ansi_tbl_64.h ansi_kv2_mtable_000.h \
		ansi_kv2_mtable_070.h ansi_kv2_mtable_110.h dllyukon.h \
		dev_sentinel.h dev_ansi.h prot_ansi_sentinel.h dev_focus.h \
		prot_ansi_focus.h ansi_focus_mtable_004.h \
		ansi_focus_mtable_013.h ansi_focus_mtable_024.h \
		dev_ipc410al.h dev_ipc420ad.h dev_mark_v.h prot_transdata.h \
		transdata_application.h transdata_tracker.h \
		transdata_datalink.h prot_ymodem.h transdata_data.h msg_cmd.h \
		dev_rds.h dev_repeater800.h dev_repeater.h dev_repeater850.h \
		dev_rfn.h rfn_identifier.h cmd_rfn.h rfn_asid.h dev_rf_da.h \
		dev_rfn420centron.h dev_rfnResidential.h dev_rfnMeter.h \
		cmd_rfn_LoadProfile.h cmd_rfn_DemandFreeze.h \
		cmd_rfn_TouConfiguration.h cmd_rfn_OvUvConfiguration.h \
		cmd_rfn_RemoteDisconnect.h cmd_rfn_CentronLcdConfiguration.h \
		dev_rfn420focus_al.h cmd_rfn_FocusAlLcdConfiguration.h \
		dev_rfnCommercial.h dev_rtc.h tbl_dv_rtc.h dev_sixnet.h \
		prot_sixnet.h rte_macro.h tbl_rtmacro.h rte_ccu.h rte_xcu.h \
		smartmap.h tbl_rtcarrier.h tbl_rtrepeater.h rte_versacom.h \
		tbl_rtversacom.h master.h rte_expresscom.h rtdb.h slctdev.h
test_cmd_device.obj:	cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h
test_cmd_lcr3102_demandresponsesummary.obj:	\
		cmd_lcr3102_DemandResponseSummary.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_dlc.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h ctidate.h
test_cmd_lcr3102_hourlydatalog.obj:	cmd_lcr3102_hourlyDataLog.h \
		cmd_lcr3102.h cmd_dlc.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h ctidate.h
test_cmd_lcr3102_tamperread.obj:	cmd_lcr3102_tamperRead.h \
		cmd_lcr3102_ThreePart.h cmd_lcr3102.h cmd_dlc.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h ctidate.h
test_cmd_mct410_disconnectconfiguration.obj:	\
		cmd_mct410_disconnectConfiguration.h cmd_mct410.h \
		cmd_mct4xx.h cmd_dlc.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h ctidate.h boost_test_helpers.h \
		millisecond_timer.h
test_cmd_mct410_hourlyread.obj:	cmd_mct410_hourlyread.h cmd_mct410.h \
		cmd_mct4xx.h cmd_dlc.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h ctidate.h
test_cmd_mct420_hourlyread.obj:	cmd_mct420_hourlyread.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h cmd_mct4xx.h cmd_dlc.h \
		cmd_device.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h ctidate.h
test_cmd_mct420_lcdconfiguration.obj:	cmd_mct420_LcdConfiguration.h \
		cmd_mct420.h cmd_mct410.h cmd_mct4xx.h cmd_dlc.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		prot_emetcon.h ctidate.h
test_cmd_rfn.obj:	cmd_rfn.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h
test_cmd_rfn_centronlcdconfiguration.obj:	ctidate.h dlldefs.h \
		cmd_rfn_CentronLcdConfiguration.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h
test_cmd_rfn_channelconfiguration.obj:	cmd_rfn_ChannelConfiguration.h \
		ctidate.h dlldefs.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h boost_test_helpers.h millisecond_timer.h
test_cmd_rfn_demandfreeze.obj:	ctidate.h dlldefs.h \
		cmd_rfn_DemandFreeze.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h boost_test_helpers.h millisecond_timer.h
test_cmd_rfn_focusallcdconfiguration.obj:	ctidate.h dlldefs.h \
		cmd_rfn_FocusAlLcdConfiguration.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h boost_test_helpers.h millisecond_timer.h
test_cmd_rfn_loadprofile.obj:	ctidate.h dlldefs.h \
		cmd_rfn_LoadProfile.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h boost_test_helpers.h millisecond_timer.h
test_cmd_rfn_ovuvconfiguration.obj:	ctidate.h dlldefs.h \
		cmd_rfn_OvUvConfiguration.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h boost_test_helpers.h millisecond_timer.h
test_cmd_rfn_remotedisconnect.obj:	ctidate.h dlldefs.h \
		cmd_rfn_RemoteDisconnect.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h
test_cmd_rfn_temperaturealarm.obj:	ctidate.h dlldefs.h \
		cmd_rfn_TemperatureAlarm.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h boost_test_helpers.h millisecond_timer.h
test_cmd_rfn_touconfiguration.obj:	ctidate.h dlldefs.h std_helper.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		boost_test_helpers.h millisecond_timer.h \
		cmd_rfn_TouConfiguration.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h
test_cmd_rf_da_dnpaddress.obj:	ctidate.h dlldefs.h \
		cmd_rf_da_dnpAddress.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h
test_dev_cbc7020.obj:	dev_cbc7020.h dev_dnp.h dev_remote.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h prot_dnp.h \
		prot_base.h packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h \
		tbl_dv_address.h pointAttribute.h
test_dev_cbc8020.obj:	dev_cbc8020.h dev_cbc7020.h dev_dnp.h \
		dev_remote.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h prot_dnp.h \
		prot_base.h packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h \
		tbl_dv_address.h pointAttribute.h ctidate.h
test_dev_ccu721.obj:	dev_ccu721.h dev_remote.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		tbl_dv_address.h device_queue_interface.h prot_klondike.h \
		prot_wrap.h prot_base.h prot_idlc.h prot_emetcon.h
test_dev_dct501.obj:	dev_dct501.h dev_mct24x.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h
test_dev_dlcbase.obj:	dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h dev_mct.h dev_carrier.h tbl_metergrp.h \
		vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h
test_dev_grp.obj:	dev_grp.h cparms.h dlldefs.h configkey.h configval.h \
		msg_lmcontrolhistory.h pointdefs.h message.h ctitime.h \
		ctidbgmem.h collectable.h loggable.h yukon.h types.h \
		msg_pcrequest.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h msg_signal.h msg_pdata.h pointtypes.h \
		msg_multi.h pt_status.h pt_base.h dbmemobject.h tbl_pt_base.h \
		row_reader.h dllbase.h dbaccess.h resolvers.h \
		db_entry_defines.h tbl_pt_status.h tbl_pt_status_control.h \
		tbl_pt_control.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		tbl_pao_lite.h tbl_rtcomm.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h database_connection.h \
		tbl_dyn_paoinfo.h dev_grp_expresscom.h tbl_dv_expresscom.h \
		vcomdefs.h devicetypes.h mgr_point.h smartmap.h \
		readers_writer_lock.h expresscom.h
test_dev_mct.obj:	dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h dev_ccu.h dev_idlc.h \
		dev_remote.h tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h \
		dev_ccu_queue_interface.h device_queue_interface.h rte_ccu.h \
		rte_xcu.h smartmap.h tbl_rtcarrier.h tbl_rtrepeater.h \
		ctidate.h
test_dev_mct210.obj:	dev_mct210.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h
test_dev_mct22x.obj:	dev_mct22x.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h
test_dev_mct24x.obj:	dev_mct24x.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h
test_dev_mct2xx.obj:	dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h
test_dev_mct310.obj:	dev_mct310.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h devicetypes.h
test_dev_mct31x.obj:	dev_mct31x.h dev_mct310.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h devicetypes.h
test_dev_mct410.obj:	dev_mct410.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h dev_ccu.h dev_idlc.h \
		dev_remote.h tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h \
		dev_ccu_queue_interface.h device_queue_interface.h rte_ccu.h \
		rte_xcu.h smartmap.h tbl_rtcarrier.h tbl_rtrepeater.h \
		pt_analog.h tbl_pt_analog.h tbl_pt_control.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h desolvers.h \
		rtdb_test_helpers.h mgr_dyn_paoinfo.h boost_test_helpers.h \
		millisecond_timer.h
test_dev_mct420.obj:	dev_mct420.h dev_mct410.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h devicetypes.h \
		pt_analog.h tbl_pt_analog.h tbl_pt_control.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h boost_test_helpers.h \
		millisecond_timer.h
test_dev_mct440_2131b.obj:	dev_mct440_2131b.h dev_mct440_213xb.h \
		dev_mct420.h dev_mct410.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h devicetypes.h \
		pt_analog.h tbl_pt_analog.h tbl_pt_control.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h boost_test_helpers.h \
		millisecond_timer.h
test_dev_mct440_2132b.obj:	dev_mct440_2132b.h dev_mct440_213xb.h \
		dev_mct420.h dev_mct410.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h devicetypes.h \
		pt_analog.h tbl_pt_analog.h tbl_pt_control.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h boost_test_helpers.h \
		millisecond_timer.h
test_dev_mct440_2133b.obj:	dev_mct440_2133b.h dev_mct440_213xb.h \
		dev_mct420.h dev_mct410.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h devicetypes.h \
		pt_analog.h tbl_pt_analog.h tbl_pt_control.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h boost_test_helpers.h \
		millisecond_timer.h
test_dev_mct440_213xb.obj:	dev_mct440_213xb.h dev_mct420.h \
		dev_mct410.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		cmd_mct410_disconnectConfiguration.h devicetypes.h \
		pt_analog.h tbl_pt_analog.h tbl_pt_control.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h rtdb_test_helpers.h \
		mgr_dyn_paoinfo.h boost_test_helpers.h millisecond_timer.h \
		dev_ccu.h dev_idlc.h dev_remote.h tbl_dialup.h tbl_direct.h \
		porter.h tbl_dv_idlcremote.h trx_info.h trx_711.h \
		dev_ccu_queue_interface.h device_queue_interface.h rte_ccu.h \
		rte_xcu.h smartmap.h tbl_rtcarrier.h tbl_rtrepeater.h \
		connection_client.h
test_dev_mct470.obj:	dev_mct470.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h \
		dev_ccu.h dev_idlc.h dev_remote.h tbl_dialup.h tbl_direct.h \
		porter.h devicetypes.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h dev_ccu_queue_interface.h device_queue_interface.h \
		rte_ccu.h rte_xcu.h smartmap.h tbl_rtcarrier.h \
		tbl_rtrepeater.h rtdb_test_helpers.h mgr_dyn_paoinfo.h \
		boost_test_helpers.h millisecond_timer.h
test_dev_mct4xx.obj:	dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_mct4xx.h ctidate.h
test_dev_mct_lmt2.obj:	dev_mct_lmt2.h dev_mct22x.h dev_mct2xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h
test_dev_rds.obj:	dev_rds.h dev_remote.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h \
		encryption_oneway_message.h test_reader.h
test_dev_rfn420centron.obj:	dev_rfn420centron.h dev_rfnResidential.h \
		dev_rfnMeter.h dev_rfn.h rfn_identifier.h streamBuffer.h \
		dlldefs.h loggable.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_LoadProfile.h ctidate.h \
		cmd_rfn_DemandFreeze.h cmd_rfn_TouConfiguration.h \
		cmd_rfn_OvUvConfiguration.h cmd_rfn_RemoteDisconnect.h \
		cmd_rfn_CentronLcdConfiguration.h rtdb_test_helpers.h \
		mgr_dyn_paoinfo.h boost_test_helpers.h millisecond_timer.h
test_dev_rfn420focus_al.obj:	dev_rfn420focus_al.h dev_rfnResidential.h \
		dev_rfnMeter.h dev_rfn.h rfn_identifier.h streamBuffer.h \
		dlldefs.h loggable.h cmd_rfn.h cmd_device.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_LoadProfile.h ctidate.h \
		cmd_rfn_DemandFreeze.h cmd_rfn_TouConfiguration.h \
		cmd_rfn_OvUvConfiguration.h cmd_rfn_RemoteDisconnect.h \
		cmd_rfn_FocusAlLcdConfiguration.h rtdb_test_helpers.h \
		mgr_dyn_paoinfo.h boost_test_helpers.h millisecond_timer.h
test_dev_rfncommercial.obj:	dev_rfnCommercial.h dev_rfnMeter.h \
		dev_rfn.h rfn_identifier.h streamBuffer.h dlldefs.h \
		loggable.h cmd_rfn.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_LoadProfile.h ctidate.h \
		cmd_rfn_DemandFreeze.h cmd_rfn_TouConfiguration.h \
		cmd_rfn_OvUvConfiguration.h rtdb_test_helpers.h \
		mgr_dyn_paoinfo.h boost_test_helpers.h millisecond_timer.h
test_dev_rfnresidential.obj:	dev_rfnResidential.h dev_rfnMeter.h \
		dev_rfn.h rfn_identifier.h streamBuffer.h dlldefs.h \
		loggable.h cmd_rfn.h cmd_device.h dev_single.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h cmd_rfn_LoadProfile.h ctidate.h \
		cmd_rfn_DemandFreeze.h cmd_rfn_TouConfiguration.h \
		cmd_rfn_OvUvConfiguration.h cmd_rfn_RemoteDisconnect.h \
		rtdb_test_helpers.h mgr_dyn_paoinfo.h boost_test_helpers.h \
		millisecond_timer.h
test_dev_rf_da.obj:	dev_rf_da.h dev_rfn.h rfn_identifier.h \
		streamBuffer.h dlldefs.h loggable.h cmd_rfn.h cmd_device.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		rfn_asid.h rtdb_test_helpers.h mgr_dyn_paoinfo.h \
		boost_test_helpers.h millisecond_timer.h ctidate.h
test_dev_rtm.obj:	dev_rtm.h dev_ied.h types.h os2_2w32.h dlldefs.h \
		dsm2.h streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h row_reader.h rte_base.h \
		dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h devicetypes.h tbl_dv_ied.h \
		verification_objects.h boost_time.h
test_dev_single.obj:	devicetypes.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h
test_encryption_cbcrbt.obj:	encryption_cbcrbt.h dlldefs.h
test_encryption_cmac.obj:	encryption_cmac.h dlldefs.h
test_encryption_oneway.obj:	encryption_oneway.h dlldefs.h CtiDate.h \
		CtiTime.h
test_encryption_oneway_message.obj:	encryption_oneway_message.h \
		dlldefs.h CtiTime.h CtiDate.h
test_lcr3102.obj:	dev_lcr3102.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h dev_lcr3102_commands.h \
		cmd_lcr3102_tamperRead.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_lcr3102_DemandResponseSummary.h \
		cmd_lcr3102_hourlyDataLog.h tbl_dyn_lcrComms.h
test_main.obj:	dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rte_base.h dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dllbase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h \
		cmd_device.h tbl_metergrp.h vcomdefs.h tbl_loadprofile.h \
		da_load_profile.h tbl_dv_mctiedport.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h cmd_rfn.h rfn_asid.h
test_mgr_point.obj:	mgr_point.h pt_base.h dbmemobject.h tbl_pt_base.h \
		row_reader.h ctitime.h dlldefs.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h smartmap.h \
		readers_writer_lock.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h
test_pt_base.obj:	pt_base.h dbmemobject.h tbl_pt_base.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h
test_rte_ccu.obj:	rte_ccu.h rte_xcu.h dev_base.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h msg_pcrequest.h smartmap.h \
		readers_writer_lock.h tbl_rtcarrier.h tbl_rtrepeater.h \
		devicetypes.h
test_slctdev.obj:	slctdev.h dev_base.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rte_base.h dbmemobject.h \
		ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h dllbase.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_signal.h tbl_static_paoinfo.h \
		encryption.h std_helper.h tbl_base.h tbl_scanrate.h \
		database_connection.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h pointdefs.h devicetypes.h dev_lcu.h dev_idlc.h \
		dev_remote.h dev_single.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h xfer.h exceptions.h \
		tbl_dialup.h tbl_direct.h porter.h tbl_dv_idlcremote.h \
		trx_info.h trx_711.h test_reader.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc

