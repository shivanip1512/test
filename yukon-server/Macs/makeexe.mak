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

BASEOBJS= \
interp.obj \
interp_pool.obj \
mccmd.obj \
wpsc.obj  \
decodetextcmdfile.obj \
xcel.obj \
clientconn.obj \
clistener.obj \
mc_dbthr.obj \
mc_fileint.obj \
mc_main.obj \
mc_msg.obj \
mc_sched.obj \
mc_scheduler.obj \
mc_script.obj \
mc_server.obj \
mc_svc.obj \
mgr_mcsched.obj \
tbl_mcsched.obj \
tbl_mcsimpsched.obj \
mc_message_serialization.obj

TARGS = macs.exe


MACS_EXE_FULLBUILD = $[Filename,$(OBJ),MacsExeFullBuild,target]


PROGS_VERSION=\
$(TARGS)


ALL:          $(TARGS)


$(MACS_EXE_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(BASEOBJS)]


macs.exe:     $(MACS_EXE_FULLBUILD) $(BASEOBJS) Makefile $(OBJ)\macs.res
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOST_LIBS) $(LINKFLAGS) macs.res
              @echo:
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              $(MANIFEST_TOOL) -manifest ..\$@.manifest -outputresource:..\$@;1
              -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
              -if exist ..\tcl\*.* copy ..\tcl\*.* $(YUKONOUTPUT)
              @%cd $(CWD)
              @echo Done building Target ..\$@
              @echo:

copy:       $(TARGS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)
           -if exist tcl\*.* copy tcl\*.* $(YUKONOUTPUT)


deps:
                scandeps -Output makeexe.mak *.cpp


clean:
        -del \
*.pdb \
$(OBJ)\*.obj \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.exe


allclean:   clean all


.SUFFIXES:      .cpp .obj

.cpp.obj:
               @echo:
               @echo Compiling: $<
               @echo C-Options: $(CFLAGS)
               @echo Output   : ..\$@
               @echo:
               $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
#UPDATE#
clientconn.obj:	precompiled.h clientconn.h connection_server.h \
		connection.h dlldefs.h message.h ctitime.h ctidbgmem.h \
		collectable.h loggable.h msg_multi.h msg_pdata.h yukon.h \
		types.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h connection_listener.h
clistener.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h amq_constants.h \
		mc.h std_helper.h clistener.h thread.h connection_listener.h \
		connection.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h clientconn.h connection_server.h
decodetextcmdfile.obj:	precompiled.h ctidate.h dlldefs.h \
		decodeTextCmdFile.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h critical_section.h atomic.h \
		guard.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h
interp.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h interp.h thread.h
interp_pool.obj:	precompiled.h interp_pool.h interp.h \
		critical_section.h dlldefs.h guard.h utility.h ctitime.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h atomic.h \
		thread.h mutex.h
mccmd.obj:	precompiled.h mccmd.h msg_pcrequest.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h loggable.h dsm2.h \
		streamConnection.h yukon.h types.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h ctdpcptrq.h CtiPCPtrQueue.h dllBase.h \
		tbl_meterreadlog.h row_reader.h database_connection.h \
		dbaccess.h ctistring.h database_reader.h \
		database_transaction.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h amq_constants.h cmdparse.h \
		ctitokenizer.h parsevalue.h msg_requestcancel.h \
		msg_queuedata.h msg_signal.h msg_dbchg.h msg_notif_email.h \
		msg_cmd.h tbl_devicereadrequestlog.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		mgr_holiday.h ctidate.h wpsc.h xcel.h decodetextcmdfile.h \
		smartmap.h
mc_dbthr.obj:	precompiled.h mc_dbthr.h mc.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h thread.h mutex.h guard.h \
		mgr_mcsched.h rtdb.h hashkey.h hash_functions.h dllbase.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h dsm2err.h words.h optional.h macro_offset.h \
		mc_sched.h row_reader.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h message.h collectable.h
mc_fileint.obj:	precompiled.h mc_fileint.h fileint.h dlldefs.h queue.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h mgr_mcsched.h mc.h rtdb.h hashkey.h \
		hash_functions.h mc_sched.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h message.h collectable.h \
		mc_msg.h ctibase.h streamSocketConnection.h socket_helper.h \
		win_helper.h ctidate.h
mc_main.obj:	precompiled.h ctitime.h dlldefs.h CServiceConfig.h \
		id_macs.h module_util.h mc_svc.h cservice.h mc_server.h mc.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h CParms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h thread.h message.h collectable.h queue.h interp.h \
		interp_pool.h mgr_mcsched.h rtdb.h hashkey.h hash_functions.h \
		mc_sched.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h mc_dbthr.h mccmd.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h ctdpcptrq.h CtiPCPtrQueue.h tbl_meterreadlog.h \
		ctistring.h clistener.h connection_listener.h connection.h \
		msg_ptreg.h msg_reg.h readers_writer_lock.h connection_base.h \
		worker_thread.h clientconn.h connection_server.h mc_msg.h \
		mc_script.h mc_scheduler.h mgr_holiday.h ctidate.h \
		mc_fileint.h fileint.h connection_client.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		logManager.h
mc_message_serialization.obj:	precompiled.h mc_message_serialization.h \
		mc_msg.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h loggable.h mc_sched.h row_reader.h mc.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h yukon.h types.h \
		os2_2w32.h constants.h numstr.h critical_section.h atomic.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h mutex.h guard.h \
		tbl_mcsimpsched.h mc_script.h
mc_msg.obj:	precompiled.h mc_msg.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h loggable.h mc_sched.h row_reader.h \
		mc.h logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h yukon.h types.h \
		os2_2w32.h constants.h numstr.h critical_section.h atomic.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h mutex.h guard.h \
		tbl_mcsimpsched.h
mc_sched.obj:	precompiled.h mc_sched.h row_reader.h ctitime.h \
		dlldefs.h mc.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h critical_section.h atomic.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h mutex.h guard.h \
		tbl_mcsimpsched.h message.h collectable.h ctidate.h
mc_scheduler.obj:	precompiled.h mc_scheduler.h ctitime.h dlldefs.h \
		mc.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h mc_sched.h row_reader.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h mutex.h guard.h \
		tbl_mcsimpsched.h message.h collectable.h mgr_mcsched.h \
		rtdb.h hashkey.h hash_functions.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_holiday.h \
		ctidate.h
mc_script.obj:	precompiled.h mc_script.h mc.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h message.h collectable.h
mc_server.obj:	precompiled.h mc_server.h mc.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h CParms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h thread.h message.h collectable.h queue.h interp.h \
		interp_pool.h mgr_mcsched.h rtdb.h hashkey.h hash_functions.h \
		mc_sched.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h mc_dbthr.h mccmd.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h ctdpcptrq.h CtiPCPtrQueue.h tbl_meterreadlog.h \
		ctistring.h clistener.h connection_listener.h connection.h \
		msg_ptreg.h msg_reg.h readers_writer_lock.h connection_base.h \
		worker_thread.h clientconn.h connection_server.h mc_msg.h \
		mc_script.h mc_scheduler.h mgr_holiday.h ctidate.h \
		mc_fileint.h fileint.h connection_client.h thread_monitor.h \
		smartmap.h thread_register_data.h msg_cmd.h amq_constants.h \
		module_util.h tbl_devicereadjoblog.h
mc_server_client_serialization_test.obj:	precompiled.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h collectable.h loggable.h \
		msg_cmd.h msg_dbchg.h yukon.h types.h msg_lmcontrolhistory.h \
		pointdefs.h msg_multi.h msg_pdata.h pointtypes.h \
		msg_notif_alarm.h msg_notif_email.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		msg_notif_lmcontrol.h msg_pcrequest.h msg_pcreturn.h \
		msg_ptreg.h msg_queuedata.h Msg_reg.h msg_requestcancel.h \
		msg_server_req.h msg_server_resp.h msg_signal.h msg_tag.h \
		msg_trace.h mc_msg.h mc_sched.h row_reader.h mc.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h \
		mc_script.h test_mc_serialization.h test_serialization.h \
		test_serialization_helper.h connection_server.h connection.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		connection_listener.h logManager.h module_util.h
mc_svc.obj:	precompiled.h mc_svc.h cservice.h dlldefs.h mc_server.h \
		mc.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h atomic.h CParms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h thread.h message.h collectable.h queue.h interp.h \
		interp_pool.h mgr_mcsched.h rtdb.h hashkey.h hash_functions.h \
		mc_sched.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h mc_dbthr.h mccmd.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h ctdpcptrq.h CtiPCPtrQueue.h tbl_meterreadlog.h \
		ctistring.h clistener.h connection_listener.h connection.h \
		msg_ptreg.h msg_reg.h readers_writer_lock.h connection_base.h \
		worker_thread.h clientconn.h connection_server.h mc_msg.h \
		mc_script.h mc_scheduler.h mgr_holiday.h ctidate.h \
		mc_fileint.h fileint.h connection_client.h thread_monitor.h \
		smartmap.h thread_register_data.h
mgr_mcsched.obj:	precompiled.h mgr_mcsched.h mc.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h rtdb.h hashkey.h hash_functions.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h mc_sched.h row_reader.h dbmemobject.h \
		tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h message.h \
		collectable.h dbaccess.h database_connection.h \
		database_reader.h
tbl_mcsched.obj:	precompiled.h tbl_mcsched.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h critical_section.h atomic.h row_reader.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h dsm2err.h words.h optional.h \
		macro_offset.h database_connection.h database_writer.h \
		row_writer.h database_util.h
tbl_mcsimpsched.obj:	precompiled.h tbl_mcsimpsched.h mutex.h dlldefs.h \
		guard.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h critical_section.h atomic.h row_reader.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h dsm2err.h words.h optional.h \
		macro_offset.h database_connection.h database_writer.h \
		row_writer.h database_util.h
test_decodetextcmdfile.obj:	decodetextcmdfile.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h guard.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h
test_interp.obj:	interp.h critical_section.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h atomic.h thread.h mutex.h
test_mccmd.obj:	mccmd.h msg_pcrequest.h dlldefs.h message.h ctitime.h \
		ctidbgmem.h collectable.h loggable.h dsm2.h \
		streamConnection.h yukon.h types.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h ctdpcptrq.h CtiPCPtrQueue.h dllBase.h \
		tbl_meterreadlog.h row_reader.h database_connection.h \
		dbaccess.h ctistring.h
test_mc_serialization.obj:	message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h loggable.h msg_cmd.h msg_dbchg.h yukon.h \
		types.h msg_lmcontrolhistory.h pointdefs.h msg_multi.h \
		msg_pdata.h pointtypes.h msg_notif_alarm.h msg_notif_email.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h critical_section.h atomic.h dllbase.h \
		dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h msg_notif_lmcontrol.h msg_pcrequest.h \
		msg_pcreturn.h msg_ptreg.h msg_queuedata.h Msg_reg.h \
		msg_requestcancel.h msg_server_req.h msg_server_resp.h \
		msg_signal.h msg_tag.h msg_trace.h mc_msg.h mc_sched.h \
		row_reader.h mc.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h mc_script.h test_mc_serialization.h \
		test_serialization.h test_serialization_helper.h \
		connection_server.h connection.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		boost_time.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h connection_listener.h
test_scheduletime.obj:	ctitime.h dlldefs.h mc_scheduler.h mc.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h mc_sched.h row_reader.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h mutex.h guard.h \
		tbl_mcsimpsched.h message.h collectable.h mgr_mcsched.h \
		rtdb.h hashkey.h hash_functions.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h mgr_holiday.h \
		ctidate.h
wpsc.obj:	precompiled.h wpsc.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h guard.h
xcel.obj:	precompiled.h xcel.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h guard.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
