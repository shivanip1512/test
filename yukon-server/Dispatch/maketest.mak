
# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc

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
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


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
;$(SIGNAL)\include \
;$(BOOST_INCLUDE) \



LIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\ctidbres.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctivg.lib


DISPATCH_TEST_OBJS= \
$(PRECOMPILED_OBJ) \
test_main.obj \
test_signalmanager.obj \
test_mgr_ptclients.obj \
test_vangogh.obj

DISPATCH_TEST_FULLBUILD = $[Filename,$(OBJ),DispatchTestFullBuild,target]


ALL:      test_dispatch.exe


$(DISPATCH_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DISPATCH_TEST_OBJS)]


test_dispatch.exe:  $(DISPATCH_TEST_FULLBUILD) $(DISPATCH_TEST_OBJS) Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS)  /Fe..\$(BIN)\$(_TargetF) \
        $(DISPATCH_TEST_OBJS) -link /LARGEADDRESSAWARE /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
	-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	-copy $(BIN)\*.pdb $(YUKONDEBUG)
        @%cd $(CWD)
        @echo.

copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)
	   -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
           -@if exist bin\*.pdb copy bin\*.pdb $(YUKONDEBUG)


deps:
        scandeps -Output maketest.mak test_*.cpp

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


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################
#UPDATE#
test_mgr_ptclients.obj:	mgr_ptclients.h dlldefs.h mgr_point.h \
		pt_base.h dbmemobject.h tbl_pt_base.h row_reader.h ctitime.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h pointdefs.h smartmap.h boostutil.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h readers_writer_lock.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		msg_pdata.h message.h collectable.h connectionHandle.h \
		ptconnect.h con_mgr.h connection_server.h connection.h \
		msg_multi.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h connection_listener.h server_b.h \
		pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		database_connection.h tbl_pt_limit.h rtdb.h \
		tbl_rawpthistory.h row_writer.h tbl_pt_property.h pt_status.h \
		tbl_pt_status.h tbl_pt_status_control.h tbl_pt_control.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h rtdb_test_helpers.h mgr_config.h \
		config_device.h devicetypes.h mgr_dyn_paoinfo.h \
		tbl_dyn_paoinfo.h desolvers.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h dev_single.h dsm2.h streamConnection.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h tbl_pao_lite.h \
		tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		std_helper.h tbl_base.h tbl_scanrate.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h xfer.h \
		config_exceptions.h exceptions.h test_reader.h \
		boost_test_helpers.h millisecond_timer.h ctidate.h
test_signalmanager.obj:	tbl_pt_alarm.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h ctitime.h signalmanager.h \
		msg_signal.h message.h collectable.h loggable.h \
		connectionHandle.h msg_multi.h msg_pdata.h pointdefs.h \
		mutex.h
test_vangogh.obj:	ctivangogh.h con_mgr.h connection_server.h \
		connection.h dlldefs.h message.h ctitime.h ctidbgmem.h \
		collectable.h loggable.h connectionHandle.h msg_multi.h \
		msg_pdata.h yukon.h types.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h \
		connection_listener.h dllbase.h con_mgr_vg.h vgexe_factory.h \
		executorfactory.h exe_cmd.h executor.h msg_cmd.h server_b.h \
		smartmap.h dev_base_lite.h dbmemobject.h msg_dbchg.h \
		msg_multiwrap.h msg_pcreturn.h dsm2.h streamConnection.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		msg_lmcontrolhistory.h msg_tag.h pendingopthread.h pendable.h \
		pending_info.h msg_signal.h tbl_lm_controlhist.h row_reader.h \
		dbaccess.h database_connection.h pt_numeric.h pt_base.h \
		tbl_pt_base.h resolvers.h db_entry_defines.h tbl_pt_unit.h \
		tbl_unitmeasure.h thread.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h tbl_pt_control.h tagmanager.h \
		tbl_dyn_pttag.h tbl_tag.h tbl_taglog.h tbl_state_grp.h \
		tbl_state.h tbl_alm_ngroup.h tbl_pt_limit.h \
		tbl_rawpthistory.h row_writer.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h connection_client.h \
		ctidate.h
#ENDUPDATE#

