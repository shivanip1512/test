include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(DATABASE)\include \
-I$(PORTER)\include \
-I$(COMMON)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RTDB)\include \

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(RTDB)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROTOCOL)\include \
;$(DISPATCH)\include \
;$(MSG)\include \


LIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \

DATABASE_TEST_OBJS=\
$(PRECOMPILED_OBJ) \
test_main.obj \
test_tbl_dyn_paoinfo.obj \
test_tbl_dv_idlcremote.obj


DATABASE_TEST_FULLBUILD = $[Filename,$(OBJ),DatabaseTestFullBuild,target]


ALL:            test_database.exe

$(DATABASE_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DATABASE_TEST_OBJS)]

test_database.exe:    $(DATABASE_TEST_FULLBUILD) $(DATABASE_TEST_OBJS)  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS)  /Fe..\$(BIN)\$(_TargetF) \
        $(DATABASE_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
        @%cd $(CWD)
        @echo.

deps:
                scandeps -Output maketest.mak *.cpp

clean:
        -del \
test*.pdb \
$(OBJ)\test*.obj \
$(BIN)\test*.pdb \
$(BIN)\test*.pch \
$(BIN)\test*.ilk \
$(BIN)\test*.exp \
$(BIN)\test*.lib \
$(BIN)\test*.dll \
$(BIN)\test*.exe


allclean:   clean test

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy bin\*.exe $(YUKONOUTPUT)


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
	$(CC) $(CCOPTS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################

#UPDATE#
dllyukon.obj:	precompiled.h tbl_route.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h row_reader.h tbl_rtcarrier.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		tbl_rtcomm.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h tbl_rtmacro.h tbl_rtrepeater.h \
		tbl_rtversacom.h msg_pcrequest.h message.h collectable.h \
		pt_base.h tbl_pt_base.h pointdefs.h tbl_state_grp.h \
		tbl_state.h
invalidreaderexception.obj:	precompiled.h InvalidReaderException.h \
		dlldefs.h database_reader.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h row_reader.h
tbl_alm_ngroup.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_alm_ngroup.h database_connection.h \
		database_reader.h
tbl_base.obj:	precompiled.h tbl_base.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h row_reader.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_carrier.obj:	precompiled.h tbl_carrier.h dlldefs.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h \
		database_connection.h database_reader.h database_writer.h \
		row_writer.h
tbl_ci_cust.obj:	precompiled.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h tbl_ci_cust.h
tbl_contact_notification.obj:	precompiled.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		tbl_contact_notification.h
tbl_devicereadjoblog.obj:	precompiled.h tbl_devicereadjoblog.h \
		ctitime.h dlldefs.h pointdefs.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h atomic.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_connection.h \
		database_writer.h row_writer.h database_util.h
tbl_devicereadrequestlog.obj:	precompiled.h tbl_devicereadrequestlog.h \
		ctitime.h dlldefs.h pointdefs.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h atomic.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_connection.h \
		database_writer.h row_writer.h database_util.h
tbl_dialup.obj:	precompiled.h tbl_dialup.h dlldefs.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h database_connection.h database_reader.h \
		database_writer.h row_writer.h
tbl_direct.obj:	precompiled.h tbl_direct.h dbmemobject.h dlldefs.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h database_connection.h \
		database_reader.h database_writer.h row_writer.h
tbl_dv_address.obj:	precompiled.h tbl_dv_address.h types.h logger.h \
		dlldefs.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h atomic.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
tbl_dv_cbc.obj:	precompiled.h tbl_dv_cbc.h dlldefs.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h database_connection.h database_writer.h \
		row_writer.h
tbl_dv_emetcon.obj:	precompiled.h tbl_dv_emetcon.h dlldefs.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h \
		database_connection.h database_writer.h row_writer.h
tbl_dv_expresscom.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_dv_expresscom.h vcomdefs.h \
		dbmemobject.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbaccess.h row_reader.h
tbl_dv_idlcremote.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		resolvers.h pointtypes.h db_entry_defines.h \
		tbl_dv_idlcremote.h dbmemobject.h row_reader.h
tbl_dv_ied.obj:	precompiled.h tbl_dv_ied.h dlldefs.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h database_connection.h database_writer.h \
		row_writer.h
tbl_dv_lmgmct.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		tbl_dv_lmgmct.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
tbl_dv_lmg_ripple.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h tbl_dv_lmg_ripple.h dllbase.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h \
		database_connection.h database_writer.h row_writer.h
tbl_dv_lmvcserial.obj:	precompiled.h tbl_dv_lmvcserial.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h vcomdefs.h \
		database_connection.h database_reader.h
tbl_dv_mctiedport.obj:	precompiled.h tbl_dv_mctiedport.h dlldefs.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h
tbl_dv_pagingreceiver.obj:	precompiled.h tbl_dv_pagingreceiver.h \
		dlldefs.h dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h \
		database_connection.h database_writer.h row_writer.h
tbl_dv_rtc.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_dv_rtc.h row_reader.h
tbl_dv_scandata.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h tbl_dv_scandata.h row_reader.h \
		dbmemobject.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h database_writer.h \
		row_writer.h database_util.h
tbl_dv_seriesv.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		tbl_dv_seriesv.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_dv_tappaging.obj:	precompiled.h tbl_dv_tappaging.h dlldefs.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h \
		database_connection.h database_writer.h row_writer.h
tbl_dv_tnpp.obj:	precompiled.h tbl_dv_tnpp.h dlldefs.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h \
		database_connection.h database_writer.h row_writer.h
tbl_dv_versacom.obj:	precompiled.h tbl_dv_versacom.h yukon.h types.h \
		ctidbgmem.h vcomdefs.h dlldefs.h dbmemobject.h dllbase.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbaccess.h row_reader.h resolvers.h \
		pointtypes.h db_entry_defines.h database_connection.h \
		database_writer.h row_writer.h database_util.h
tbl_dv_wnd.obj:	precompiled.h tbl_dv_wnd.h row_reader.h ctitime.h \
		dlldefs.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h database_reader.h desolvers.h
tbl_dyn_lcrcomms.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h \
		database_writer.h row_writer.h database_util.h \
		tbl_dyn_lcrComms.h
tbl_dyn_paoinfo.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_dyn_paoinfo.h dbmemobject.h \
		database_connection.h row_reader.h database_reader.h \
		database_writer.h row_writer.h database_util.h std_helper.h
tbl_dyn_ptalarming.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_dyn_ptalarming.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dbmemobject.h pointdefs.h row_reader.h database_connection.h \
		database_reader.h database_writer.h row_writer.h \
		database_util.h
tbl_dyn_pttag.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_dyn_pttag.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dbmemobject.h pointdefs.h row_reader.h database_connection.h \
		database_reader.h database_writer.h row_writer.h \
		database_util.h
tbl_lmg_point.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h tbl_lmg_point.h dbmemobject.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
tbl_lmg_sa205105.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h atomic.h tbl_lmg_sa205105.h \
		dbmemobject.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
tbl_lmg_sa305.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h tbl_lmg_sa305.h dbmemobject.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
tbl_lmg_sasimple.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h tbl_lmg_sasimple.h dbmemobject.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
tbl_lmprogramhistory.obj:	precompiled.h row_reader.h ctitime.h \
		dlldefs.h tbl_lmprogramhistory.h dbmemobject.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_connection.h database_reader.h database_writer.h \
		row_writer.h database_util.h
tbl_lm_controlhist.obj:	precompiled.h tbl_lm_controlhist.h \
		row_reader.h ctitime.h dlldefs.h dbmemobject.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_connection.h ctidate.h database_reader.h \
		database_writer.h row_writer.h database_util.h
tbl_loadprofile.obj:	precompiled.h tbl_loadprofile.h yukon.h types.h \
		ctidbgmem.h vcomdefs.h dlldefs.h dbmemobject.h dllbase.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h da_load_profile.h row_reader.h \
		database_connection.h database_reader.h
tbl_metergrp.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h database_connection.h \
		database_writer.h row_writer.h
tbl_meterreadlog.obj:	precompiled.h tbl_meterreadlog.h ctitime.h \
		dlldefs.h pointdefs.h utility.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_writer.h row_writer.h
tbl_pao.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h resolvers.h pointtypes.h db_entry_defines.h \
		tbl_pao.h row_reader.h dbmemobject.h database_connection.h \
		database_reader.h database_writer.h row_writer.h \
		database_util.h
tbl_paoexclusion.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_paoexclusion.h row_reader.h \
		database_connection.h database_reader.h
tbl_paoproperty.obj:	precompiled.h tbl_paoproperty.h dlldefs.h \
		row_reader.h ctitime.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
tbl_pao_lite.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h resolvers.h pointtypes.h db_entry_defines.h \
		tbl_pao_lite.h dbmemobject.h row_reader.h \
		database_connection.h database_reader.h
tbl_port_base.obj:	precompiled.h tbl_port_base.h dbmemobject.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
tbl_port_dialup.obj:	precompiled.h tbl_port_dialup.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_port_serial.obj:	precompiled.h tbl_port_serial.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h row_reader.h
tbl_port_settings.obj:	precompiled.h tbl_port_settings.h dbmemobject.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h row_reader.h
tbl_port_tcpip.obj:	precompiled.h tbl_port_tcpip.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_port_timing.obj:	precompiled.h tbl_port_timing.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_ptdispatch.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_ptdispatch.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dbmemobject.h pointdefs.h row_reader.h database_connection.h \
		database_reader.h database_writer.h row_writer.h \
		database_util.h database_exceptions.h ctidate.h
tbl_pt_accum.obj:	precompiled.h tbl_pt_accum.h row_reader.h ctitime.h \
		dlldefs.h dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h
tbl_pt_accumhistory.obj:	precompiled.h tbl_pt_accumhistory.h yukon.h \
		types.h ctidbgmem.h dbmemobject.h row_reader.h ctitime.h \
		dlldefs.h dbaccess.h dllbase.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_connection.h database_reader.h database_writer.h \
		row_writer.h database_util.h
tbl_pt_alarm.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_pt_alarm.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_pt_analog.obj:	precompiled.h tbl_pt_analog.h row_reader.h \
		ctitime.h dlldefs.h dbmemobject.h
tbl_pt_base.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_pt_base.h row_reader.h dllbase.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h pointdefs.h
tbl_pt_control.obj:	precompiled.h tbl_pt_control.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h
tbl_pt_limit.obj:	precompiled.h tbl_pt_limit.h dlldefs.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h
tbl_pt_property.obj:	precompiled.h tbl_pt_property.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h
tbl_pt_status.obj:	precompiled.h tbl_pt_status.h dlldefs.h \
		dbmemobject.h row_reader.h ctitime.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
tbl_pt_status_control.obj:	precompiled.h tbl_pt_status_control.h \
		tbl_pt_control.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbmemobject.h pointtypes.h resolvers.h db_entry_defines.h
tbl_pt_unit.obj:	precompiled.h tbl_pt_unit.h row_reader.h ctitime.h \
		dlldefs.h dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_unitmeasure.h
tbl_rfnidentifier.obj:	precompiled.h tbl_rfnidentifier.h \
		rfn_identifier.h streamBuffer.h dlldefs.h loggable.h \
		row_reader.h ctitime.h
tbl_route.obj:	precompiled.h tbl_route.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h row_reader.h
tbl_rtcarrier.obj:	precompiled.h tbl_rtcarrier.h row_reader.h \
		ctitime.h dlldefs.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h resolvers.h pointtypes.h \
		db_entry_defines.h dbmemobject.h database_connection.h \
		database_reader.h
tbl_rtcomm.obj:	precompiled.h tbl_rtcomm.h row_reader.h ctitime.h \
		dlldefs.h dbaccess.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h resolvers.h pointtypes.h db_entry_defines.h \
		dbmemobject.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h database_connection.h \
		database_reader.h
tbl_rtmacro.obj:	precompiled.h tbl_rtmacro.h dlldefs.h dbmemobject.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h atomic.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h
tbl_rtrepeater.obj:	precompiled.h tbl_rtrepeater.h dlldefs.h \
		loggable.h row_reader.h ctitime.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
tbl_rtversacom.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_rtversacom.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h \
		database_connection.h database_reader.h
tbl_scanrate.obj:	precompiled.h tbl_scanrate.h row_reader.h ctitime.h \
		dlldefs.h dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h database_connection.h database_reader.h \
		desolvers.h
tbl_state.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_state.h row_reader.h database_connection.h \
		database_reader.h
tbl_state_grp.obj:	precompiled.h tbl_state_grp.h dlldefs.h mutex.h \
		tbl_state.h row_reader.h ctitime.h loggable.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h
tbl_static_paoinfo.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_static_paoinfo.h dbmemobject.h \
		database_reader.h database_connection.h row_reader.h \
		database_writer.h row_writer.h
tbl_tag.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_tag.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h dbmemobject.h row_reader.h
tbl_taglog.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h tbl_taglog.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dbmemobject.h pointdefs.h row_reader.h database_connection.h \
		database_reader.h database_writer.h row_writer.h \
		database_util.h
tbl_unitmeasure.obj:	precompiled.h tbl_unitmeasure.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h
test_tbl_dv_idlcremote.obj:	tbl_dv_idlcremote.h types.h dlldefs.h \
		dllbase.h dsm2.h streamConnection.h yukon.h ctidbgmem.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbmemobject.h row_reader.h
test_tbl_dyn_paoinfo.obj:	tbl_dyn_paoinfo.h dlldefs.h dbmemobject.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h row_reader.h
#ENDUPDATE#
