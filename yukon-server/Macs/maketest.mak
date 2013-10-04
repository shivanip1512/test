# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(MACS)\include \
-I$(COMMON)\include \
-I$(SERVICE)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(MSG)\include \
-I$(TCL)\include \
-I$(RW) \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(THRIFT_INCLUDE) \
-I$(MSG)\Serialization

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(MACS)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROTOCOL)\include \
;$(RTDB)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(DATABASE)\include \
;$(RW)

LIBS=\
advapi32.lib \
$(TCL_LIBS) \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctithriftmsg.lib \
$(THRIFT_LIB)

MACS_TEST_OBJS= \
test_main.obj \
test_interp.obj \
test_scheduletime.obj \
test_decodeTextCmdFile.obj \
test_mccmd.obj \
test_mc_serialization.obj \

MACSBASEOBJS= \
clientconn.obj \
clistener.obj \
mc_dbthr.obj \
mc_fileint.obj \
mc_msg.obj \
mc_sched.obj \
mc_scheduler.obj \
mc_server.obj \
mc_script.obj \
mgr_mcsched.obj \
tbl_mcsched.obj \
tbl_mcsimpsched.obj \
decodeTextCmdFile.obj \
mccmd.obj \
interp.obj \
interp_pool.obj \
wpsc.obj \
xcel.obj \
mc_message_serialization.obj


MACS_TEST_FULLBUILD = $[Filename,$(OBJ),MacsTestFullBuild,target]


ALL:            test_macs.exe \
                mc_server_client_serialization_test.exe


$(MACS_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(MACS_TEST_OBJS)]

test_macs.exe:    $(MACS_TEST_FULLBUILD) $(MACS_TEST_OBJS)  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(_TargetF) \
        $(MACS_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(MACSBASEOBJS) $(RWLIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	$(MANIFEST_TOOL) -manifest $(BIN)\$(_TargetF).manifest -outputresource:$(BIN)\$(_TargetF);1
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
        @%cd $(CWD)
        @echo.

mc_server_client_serialization_test.exe:    $(MACS_TEST_FULLBUILD) mc_server_client_serialization_test.obj  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(_TargetF) \
        mc_server_client_serialization_test.obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(MACSBASEOBJS) $(RWLIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	$(MANIFEST_TOOL) -manifest $(BIN)\$(_TargetF).manifest -outputresource:$(BIN)\$(_TargetF);1
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
        @%cd $(CWD)
        @echo.

copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
        scandeps -Output maketest.mak test_*.cpp


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################
#UPDATE#
test_decodetextcmdfile.obj:	decodetextcmdfile.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		rwutil.h yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
test_interp.obj:	interp.h critical_section.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h thread.h mutex.h
test_mccmd.obj:	mccmd.h msg_pcrequest.h dlldefs.h message.h ctitime.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h logger.h thread.h CtiPCPtrQueue.h ctdpcptrq.h \
		tbl_meterreadlog.h ctistring.h
test_mc_serialization.obj:	message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h rwutil.h yukon.h types.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_cmd.h msg_dbchg.h \
		msg_lmcontrolhistory.h pointdefs.h msg_multi.h msg_pdata.h \
		pointtypes.h msg_notif_alarm.h msg_notif_email.h logger.h \
		thread.h CtiPCPtrQueue.h msg_notif_lmcontrol.h \
		msg_pcrequest.h msg_pcreturn.h msg_ptreg.h msg_queuedata.h \
		Msg_reg.h msg_requestcancel.h msg_server_req.h \
		msg_server_resp.h msg_signal.h msg_tag.h msg_trace.h mc_msg.h \
		mc_sched.h mc.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h mc_script.h test_mc_serialization.h \
		test_serialization.h test_serialization_helper.h \
		connection_server.h connection.h exchange.h string_utility.h \
		queue.h cparms.h configkey.h configval.h \
		readers_writer_lock.h critical_section.h \
		connection_listener.h
test_scheduletime.obj:	ctitime.h dlldefs.h mc_scheduler.h mc.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		mc_sched.h row_reader.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h message.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h boost_time.h boostutil.h mgr_mcsched.h \
		rtdb.h hashkey.h hash_functions.h string_utility.h \
		mgr_holiday.h ctidate.h
#ENDUPDATE#

