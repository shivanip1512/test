include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(MSG)\include \
-I$(PROT)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \



YUKONDLLOBJS=\
$(PRECOMPILED_OBJ) \
dllyukon.obj \
tbl_alm_ngroup.obj \
tbl_base.obj \
tbl_carrier.obj \
tbl_ci_cust.obj \
tbl_contact_notification.obj \
tbl_dialup.obj \
tbl_direct.obj \
tbl_dyn_lcrComms.obj \
tbl_dyn_ptalarming.obj \
tbl_dyn_pttag.obj \
tbl_dyn_paoinfo.obj \
tbl_dv_address.obj \
tbl_dv_cbc.obj \
tbl_dv_emetcon.obj \
tbl_dv_expresscom.obj \
tbl_dv_idlcremote.obj \
tbl_dv_ied.obj \
tbl_dv_lmvcserial.obj \
tbl_dv_lmgmct.obj \
tbl_dv_lmg_ripple.obj \
tbl_dv_mctiedport.obj \
tbl_dv_pagingreceiver.obj \
tbl_dv_rtc.obj \
tbl_dv_scandata.obj \
tbl_dv_seriesv.obj \
tbl_dv_tappaging.obj \
tbl_dv_tnpp.obj \
tbl_dv_versacom.obj \
tbl_dv_wnd.obj \
tbl_devicereadjoblog.obj \
tbl_devicereadrequestlog.obj \
tbl_lm_controlhist.obj \
tbl_lmprogramhistory.obj \
tbl_lmg_point.obj \
tbl_lmg_sasimple.obj \
tbl_lmg_sa205105.obj \
tbl_lmg_sa305.obj \
tbl_loadprofile.obj \
tbl_metergrp.obj \
tbl_meterreadlog.obj \
tbl_pao.obj \
tbl_pao_lite.obj \
tbl_paoexclusion.obj \
tbl_paoproperty.obj \
tbl_port_base.obj \
tbl_port_dialup.obj \
tbl_port_serial.obj \
tbl_port_settings.obj \
tbl_port_tcpip.obj \
tbl_port_timing.obj \
tbl_pt_accum.obj \
tbl_pt_accumhistory.obj \
tbl_pt_alarm.obj \
tbl_pt_analog.obj \
tbl_pt_control.obj \
tbl_pt_property.obj \
tbl_pt_base.obj \
tbl_pt_limit.obj \
tbl_pt_status.obj \
tbl_pt_status_control.obj \
tbl_pt_unit.obj \
tbl_rfnidentifier.obj \
tbl_route.obj \
tbl_rtcarrier.obj \
tbl_rtcomm.obj \
tbl_rtmacro.obj \
tbl_rtrepeater.obj \
tbl_rtversacom.obj \
tbl_ptdispatch.obj \
tbl_scanrate.obj \
tbl_static_paoinfo.obj \
tbl_state.obj \
tbl_state_grp.obj \
tbl_tag.obj \
tbl_taglog.obj \
tbl_unitmeasure.obj \
InvalidReaderException.obj \


DBLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(SQLAPI_LIB) \



CTIPROGS=\
ctidbsrc.dll \


DATABASE_FULLBUILD = $[Filename,$(OBJ),DatabaseFullBuild,target]

PROGS_VERSION=\
$(CTIPROGS)

ALL:            $(CTIPROGS)


$(DATABASE_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(CC) $(CCOPTS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) /DCTIYUKONDB -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(YUKONDLLOBJS)]



ctidbsrc.dll:   $(DATABASE_FULLBUILD) $(YUKONDLLOBJS) Makefile $(OBJ)\ctidbsrc.res
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(CC) $(INCLPATHS) $(DLLFLAGS) -Fe..\$@ \
$(YUKONDLLOBJS) -link $(BOOST_LIBS) $(DBLIBS) ctidbsrc.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	       -if exist ..\$(@B).pdb copy ..\$(@B).pdb $(YUKONDEBUG)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @echo:
                @echo Done building Target $@
                @echo:
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\*.dll copy bin\*.dll $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
               -if exist bin\*.pdb copy bin\*.pdb $(YUKONDEBUG)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\*.lib copy bin\*.lib $(COMPILEBASE)\lib


deps:
                scandeps -Output makedll.mak *.cpp


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
        $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /DCTIYUKONDB -Fo$(OBJ)\ -c $<


######################################################################################
#UPDATE#
dllyukon.obj:	precompiled.h tbl_route.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		dbmemobject.h row_reader.h ctitime.h tbl_rtcarrier.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h tbl_rtcomm.h tbl_rtmacro.h \
		tbl_rtrepeater.h tbl_rtversacom.h msg_pcrequest.h message.h \
		collectable.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h tbl_state_grp.h tbl_state.h
invalidreaderexception.obj:	precompiled.h InvalidReaderException.h \
		dlldefs.h database_reader.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h row_reader.h
tbl_alm_ngroup.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h tbl_alm_ngroup.h loggable.h \
		mutex.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h database_connection.h \
		guard.h database_reader.h
tbl_base.obj:	precompiled.h tbl_base.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h dbmemobject.h \
		row_reader.h resolvers.h pointtypes.h db_entry_defines.h
tbl_carrier.obj:	precompiled.h tbl_carrier.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h database_connection.h \
		guard.h database_reader.h database_writer.h row_writer.h
tbl_ci_cust.obj:	precompiled.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h guard.h utility.h ctitime.h \
		queues.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h database_reader.h row_reader.h \
		tbl_ci_cust.h mutex.h
tbl_contact_notification.obj:	precompiled.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h guard.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h database_reader.h row_reader.h \
		tbl_contact_notification.h mutex.h
tbl_devicereadjoblog.obj:	precompiled.h tbl_devicereadjoblog.h \
		ctitime.h dlldefs.h pointdefs.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		critical_section.h dbaccess.h dllbase.h database_connection.h \
		guard.h database_writer.h row_writer.h database_util.h
tbl_devicereadrequestlog.obj:	precompiled.h tbl_devicereadrequestlog.h \
		ctitime.h dlldefs.h pointdefs.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		critical_section.h dbaccess.h dllbase.h database_connection.h \
		guard.h database_writer.h row_writer.h database_util.h
tbl_dialup.obj:	precompiled.h tbl_dialup.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h database_connection.h \
		guard.h database_reader.h database_writer.h row_writer.h
tbl_direct.obj:	precompiled.h tbl_direct.h dbmemobject.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h ctitime.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h database_connection.h guard.h \
		database_reader.h database_writer.h row_writer.h
tbl_dv_address.obj:	precompiled.h tbl_dv_address.h types.h logger.h \
		dlldefs.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h critical_section.h dllbase.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
tbl_dv_cbc.obj:	precompiled.h tbl_dv_cbc.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h database_connection.h \
		guard.h database_writer.h row_writer.h
tbl_dv_emetcon.obj:	precompiled.h tbl_dv_emetcon.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h database_connection.h \
		guard.h database_writer.h row_writer.h
tbl_dv_expresscom.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_dv_expresscom.h vcomdefs.h \
		dbmemobject.h dllbase.h dbaccess.h row_reader.h
tbl_dv_idlcremote.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h dbaccess.h dllbase.h resolvers.h \
		pointtypes.h db_entry_defines.h tbl_dv_idlcremote.h \
		dbmemobject.h row_reader.h
tbl_dv_ied.obj:	precompiled.h tbl_dv_ied.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h database_connection.h \
		guard.h database_writer.h row_writer.h
tbl_dv_lmgmct.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		tbl_dv_lmgmct.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h
tbl_dv_lmg_ripple.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h tbl_dv_lmg_ripple.h dllbase.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h database_connection.h guard.h \
		database_writer.h row_writer.h
tbl_dv_lmvcserial.obj:	precompiled.h tbl_dv_lmvcserial.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		vcomdefs.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h database_connection.h \
		guard.h database_reader.h
tbl_dv_mctiedport.obj:	precompiled.h tbl_dv_mctiedport.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_dv_pagingreceiver.obj:	precompiled.h tbl_dv_pagingreceiver.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		ctitime.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h database_connection.h \
		guard.h database_writer.h row_writer.h
tbl_dv_rtc.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h tbl_dv_rtc.h row_reader.h
tbl_dv_scandata.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h tbl_dv_scandata.h row_reader.h \
		dbmemobject.h database_connection.h dbaccess.h dllbase.h \
		guard.h database_reader.h database_writer.h row_writer.h \
		database_util.h
tbl_dv_seriesv.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		tbl_dv_seriesv.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_dv_tappaging.obj:	precompiled.h tbl_dv_tappaging.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h database_connection.h \
		guard.h database_writer.h row_writer.h
tbl_dv_tnpp.obj:	precompiled.h tbl_dv_tnpp.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h database_connection.h \
		guard.h database_writer.h row_writer.h
tbl_dv_versacom.obj:	precompiled.h tbl_dv_versacom.h yukon.h types.h \
		ctidbgmem.h vcomdefs.h dlldefs.h dbmemobject.h dllbase.h \
		os2_2w32.h cticalls.h critical_section.h dbaccess.h \
		row_reader.h ctitime.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		database_connection.h guard.h database_writer.h row_writer.h \
		database_util.h
tbl_dv_wnd.obj:	precompiled.h tbl_dv_wnd.h row_reader.h ctitime.h \
		dlldefs.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		database_reader.h desolvers.h
tbl_dyn_lcrcomms.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h database_connection.h dbaccess.h \
		dllbase.h guard.h database_reader.h row_reader.h \
		database_writer.h row_writer.h database_util.h \
		tbl_dyn_lcrComms.h
tbl_dyn_paoinfo.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h tbl_dyn_paoinfo.h dbmemobject.h \
		database_connection.h guard.h row_reader.h database_reader.h \
		database_writer.h row_writer.h database_util.h std_helper.h
tbl_dyn_ptalarming.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h tbl_dyn_ptalarming.h dbmemobject.h pointdefs.h \
		row_reader.h database_connection.h guard.h database_reader.h \
		database_writer.h row_writer.h database_util.h
tbl_dyn_pttag.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h tbl_dyn_pttag.h dbmemobject.h pointdefs.h \
		row_reader.h database_connection.h guard.h database_reader.h \
		database_writer.h row_writer.h database_util.h
tbl_lmg_point.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h tbl_lmg_point.h dbmemobject.h \
		dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
tbl_lmg_sa205105.obj:	precompiled.h cparms.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h tbl_lmg_sa205105.h \
		dbmemobject.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
tbl_lmg_sa305.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h tbl_lmg_sa305.h dbmemobject.h \
		dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
tbl_lmg_sasimple.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h tbl_lmg_sasimple.h dbmemobject.h \
		dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h prot_sa3rdparty.h cmdparse.h \
		ctitokenizer.h parsevalue.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h prot_base.h \
		msg_pdata.h pointdefs.h message.h collectable.h xfer.h \
		protocol_sa.h
tbl_lmprogramhistory.obj:	precompiled.h row_reader.h ctitime.h \
		dlldefs.h tbl_lmprogramhistory.h dbmemobject.h dbaccess.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		database_connection.h guard.h database_reader.h \
		database_writer.h row_writer.h database_util.h
tbl_lm_controlhist.obj:	precompiled.h tbl_lm_controlhist.h \
		row_reader.h ctitime.h dlldefs.h dbmemobject.h dbaccess.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h database_connection.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h ctidate.h database_reader.h \
		database_writer.h row_writer.h database_util.h mutex.h
tbl_loadprofile.obj:	precompiled.h tbl_loadprofile.h yukon.h types.h \
		ctidbgmem.h vcomdefs.h dlldefs.h dbmemobject.h dllbase.h \
		os2_2w32.h cticalls.h critical_section.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h da_load_profile.h \
		row_reader.h ctitime.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		database_connection.h guard.h database_reader.h
tbl_metergrp.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h database_connection.h guard.h \
		database_writer.h row_writer.h
tbl_meterreadlog.obj:	precompiled.h tbl_meterreadlog.h ctitime.h \
		dlldefs.h pointdefs.h utility.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h row_reader.h database_connection.h \
		dbaccess.h dllbase.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h database_writer.h row_writer.h
tbl_pao.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h resolvers.h \
		pointtypes.h db_entry_defines.h tbl_pao.h row_reader.h \
		dbmemobject.h database_connection.h guard.h database_reader.h \
		database_writer.h row_writer.h database_util.h
tbl_paoexclusion.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h tbl_paoexclusion.h row_reader.h \
		database_connection.h guard.h database_reader.h
tbl_paoproperty.obj:	precompiled.h tbl_paoproperty.h dlldefs.h \
		row_reader.h ctitime.h dbaccess.h dllbase.h os2_2w32.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h
tbl_pao_lite.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h resolvers.h pointtypes.h db_entry_defines.h \
		tbl_pao_lite.h dbmemobject.h row_reader.h \
		database_connection.h guard.h database_reader.h
tbl_port_base.obj:	precompiled.h tbl_port_base.h dbmemobject.h \
		dbaccess.h dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h ctitime.h \
		loggable.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_port_dialup.obj:	precompiled.h tbl_port_dialup.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_port_serial.obj:	precompiled.h tbl_port_serial.h dbaccess.h \
		dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h dbmemobject.h row_reader.h \
		ctitime.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_port_settings.obj:	precompiled.h tbl_port_settings.h dbmemobject.h \
		dbaccess.h dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h row_reader.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_port_tcpip.obj:	precompiled.h tbl_port_tcpip.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_port_timing.obj:	precompiled.h tbl_port_timing.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_ptdispatch.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h tbl_ptdispatch.h dbmemobject.h pointdefs.h \
		row_reader.h database_connection.h guard.h database_reader.h \
		database_writer.h row_writer.h database_util.h \
		database_exceptions.h ctidate.h
tbl_pt_accum.obj:	precompiled.h tbl_pt_accum.h row_reader.h ctitime.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h loggable.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h
tbl_pt_accumhistory.obj:	precompiled.h tbl_pt_accumhistory.h yukon.h \
		types.h ctidbgmem.h dbmemobject.h row_reader.h ctitime.h \
		dlldefs.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h database_connection.h guard.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h database_reader.h \
		database_writer.h row_writer.h database_util.h
tbl_pt_alarm.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h tbl_pt_alarm.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_pt_analog.obj:	precompiled.h tbl_pt_analog.h row_reader.h \
		ctitime.h dlldefs.h dbmemobject.h
tbl_pt_base.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_pt_base.h row_reader.h dllbase.h \
		dbmemobject.h dbaccess.h pointdefs.h
tbl_pt_control.obj:	precompiled.h tbl_pt_control.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h dbmemobject.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h
tbl_pt_limit.obj:	precompiled.h tbl_pt_limit.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h ctitime.h \
		loggable.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_pt_property.obj:	precompiled.h tbl_pt_property.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h dbmemobject.h \
		dbaccess.h loggable.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_pt_status.obj:	precompiled.h tbl_pt_status.h dlldefs.h \
		dbmemobject.h row_reader.h ctitime.h dllbase.h os2_2w32.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_pt_status_control.obj:	precompiled.h tbl_pt_status_control.h \
		tbl_pt_control.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h pointtypes.h resolvers.h \
		db_entry_defines.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h
tbl_pt_unit.obj:	precompiled.h tbl_pt_unit.h row_reader.h ctitime.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h loggable.h \
		tbl_unitmeasure.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_rfnidentifier.obj:	precompiled.h tbl_rfnidentifier.h \
		rfn_identifier.h streamBuffer.h dlldefs.h loggable.h \
		row_reader.h ctitime.h
tbl_route.obj:	precompiled.h tbl_route.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h row_reader.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
tbl_rtcarrier.obj:	precompiled.h tbl_rtcarrier.h row_reader.h \
		ctitime.h dlldefs.h dbaccess.h dllbase.h os2_2w32.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h dbmemobject.h database_connection.h guard.h \
		database_reader.h
tbl_rtcomm.obj:	precompiled.h tbl_rtcomm.h row_reader.h ctitime.h \
		dlldefs.h dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h dbmemobject.h database_connection.h guard.h \
		database_reader.h
tbl_rtmacro.obj:	precompiled.h tbl_rtmacro.h dlldefs.h dbmemobject.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h critical_section.h \
		database_connection.h dbaccess.h dllbase.h guard.h \
		database_reader.h row_reader.h
tbl_rtrepeater.obj:	precompiled.h tbl_rtrepeater.h dlldefs.h \
		loggable.h row_reader.h ctitime.h dllbase.h os2_2w32.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h
tbl_rtversacom.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h tbl_rtversacom.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h \
		database_connection.h guard.h database_reader.h
tbl_scanrate.obj:	precompiled.h tbl_scanrate.h row_reader.h ctitime.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h dbmemobject.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h database_connection.h guard.h \
		database_reader.h desolvers.h
tbl_state.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		tbl_state.h row_reader.h ctitime.h loggable.h mutex.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h database_connection.h guard.h \
		database_reader.h
tbl_state_grp.obj:	precompiled.h tbl_state_grp.h dlldefs.h mutex.h \
		tbl_state.h row_reader.h ctitime.h loggable.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h guard.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h database_reader.h
tbl_static_paoinfo.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h tbl_static_paoinfo.h dbmemobject.h \
		database_reader.h database_connection.h guard.h row_reader.h \
		database_writer.h row_writer.h
tbl_tag.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h tbl_tag.h \
		dbmemobject.h row_reader.h
tbl_taglog.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h tbl_taglog.h dbmemobject.h pointdefs.h row_reader.h \
		database_connection.h guard.h database_reader.h \
		database_writer.h row_writer.h database_util.h
tbl_unitmeasure.obj:	precompiled.h tbl_unitmeasure.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		loggable.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h
test_tbl_dv_idlcremote.obj:	tbl_dv_idlcremote.h types.h dlldefs.h \
		dllbase.h os2_2w32.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbmemobject.h row_reader.h ctitime.h
test_tbl_dyn_paoinfo.obj:	tbl_dyn_paoinfo.h dlldefs.h dbmemobject.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h guard.h \
		utility.h ctitime.h queues.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h row_reader.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
