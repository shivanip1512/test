!include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(CAPCONTROL)\include \
-I$(COMMON)\include \
-I$(PROT)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(PROCLOG)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(SERVICE)\include \
-I$(SERVER)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


.PATH.cpp = .
.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CAPCONTROL)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
;$(SERVICE)\include \
;$(RTDB)\include \
;$(SERVER)\include \
;$(PROCLOG)\include \
;$(MSG)\include \



LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctipil.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(COMPILEBASE)\lib\rfn-e2e.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \

PIL_TEST_OBJS= \
$(PRECOMPILED_OBJ) \
test_main.obj \
test_mgr_rfn_request.obj \
test_pilserver.obj

PIL_TEST_FULLBUILD = $[Filename,$(OBJ),PilTestFullBuild,target]


ALL:            test_pil.exe

$(PIL_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(PIL_TEST_OBJS)]

test_pil.exe:    $(PIL_TEST_FULLBUILD) $(PIL_TEST_OBJS)  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS)  /Fe..\$(BIN)\$(_TargetF) \
        $(PIL_TEST_OBJS) -link /LARGEADDRESSAWARE /subsystem:console $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(LIBS) $(LINKFLAGS)
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
test_main.obj:	amq_connection.h thread.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h \
		StreamableMessage.h connection_base.h
test_mgr_rfn_request.obj:	mgr_rfn_request.h dlldefs.h dev_rfn.h \
		rfn_identifier.h streamBuffer.h loggable.h cmd_rfn.h \
		cmd_device.h dev_single.h dsm2.h connectionHandle.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h logger.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h config_device.h dllbase.h \
		rte_base.h dbmemobject.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h pointdefs.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h readers_writer_lock.h connection_base.h \
		worker_thread.h concurrentSet.h xfer.h config_exceptions.h \
		exceptions.h rfn_asid.h rfn_e2e_messenger.h \
		RfnE2eDataIndicationMsg.h RfnE2eMsg.h RfnE2eDataConfirmMsg.h \
		NetworkManagerMessaging.h RfnE2eDataRequestMsg.h \
		NetworkManagerRequest.h cmd_rfn_ChannelConfiguration.h \
		ctidate.h boost_test_helpers.h millisecond_timer.h
test_pilserver.obj:	pilserver.h dsm2.h connectionHandle.h dlldefs.h \
		loggable.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		server_b.h con_mgr.h connection_server.h connection.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		concurrentSet.h connection_listener.h dllbase.h smartmap.h \
		msg_pcrequest.h mgr_device.h rtdb.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h config_device.h rte_base.h \
		dbmemobject.h tbl_pao_lite.h tbl_rtcomm.h dbaccess.h \
		resolvers.h db_entry_defines.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h std_helper.h tbl_base.h \
		tbl_scanrate.h database_connection.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h slctdev.h mgr_point.h mgr_route.h \
		repeaterrole.h mgr_config.h devicetypes.h amq_constants.h \
		mgr_rfn_request.h dev_rfn.h rfn_identifier.h cmd_rfn.h \
		cmd_device.h dev_single.h msg_pcreturn.h tbl_dv_scandata.h \
		tbl_dv_wnd.h xfer.h config_exceptions.h exceptions.h \
		rfn_asid.h rfn_e2e_messenger.h RfnE2eDataIndicationMsg.h \
		RfnE2eMsg.h RfnE2eDataConfirmMsg.h NetworkManagerMessaging.h \
		RfnE2eDataRequestMsg.h NetworkManagerRequest.h \
		cmd_rfn_demandFreeze.h
#ENDUPDATE#

