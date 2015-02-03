include $(COMPILEBASE)\global.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(ACTIVEMQ_INCLUDE) \
-I$(APR_INCLUDE) \
-I$(THRIFT_INCLUDE) \
-I$(MSG)\Serialization


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROTOCOL)\include \
;$(DISPATCH)\include \
;$(MSG)\include


OBJS=\
$(PRECOMPILED_OBJ) \
rfn_e2e_messenger.obj \
RfnE2eMsgSerialization.obj \
dll_rfn_e2e.obj \
id_rfn_e2e.obj \


LIBS = \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctithriftmsg.lib \
$(BOOST_LIBS) \
$(ACTIVEMQ_LIB) \
$(THRIFT_LIB) \


MESSAGE_FULLBUILD = $[Filename,$(OBJ),MessageFullBuild,target]


CTIPROGS=\
rfn-e2e.dll


PROGS_VERSION=\
$(CTIPROGS)


ALL:           $(CTIPROGS)


$(MESSAGE_FULLBUILD) :
        @touch $@
        @echo:
        @echo Compiling cpp to obj
        $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) $(PARALLEL) $(INCLPATHS) /D_DLL_MESSAGE -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(OBJS)] $(SERIALIZATION_CPP)


rfn-e2e.dll:    $(MESSAGE_FULLBUILD) $(OBJS) $(SERIALIZATION_OBJS) Makefile $(OBJ)\rfn-e2e.res
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(CC) $(INCLPATHS) $(DLLFLAGS) -Fe..\$@ $(OBJS) -link $(LIBS) rfn-e2e.res
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
                scandeps -Output makedll.mak *.cpp



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


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_RFN_E2E -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
amq_connection.obj:	precompiled.h amq_connection.h thread.h mutex.h \
		dlldefs.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h critical_section.h \
		StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h logger.h CtiPCPtrQueue.h dllbase.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h \
		words.h optional.h macro_offset.h amq_util.h \
		readers_writer_lock.h std_helper.h
amq_util.obj:	precompiled.h amq_util.h dlldefs.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h logger.h thread.h \
		mutex.h CtiPCPtrQueue.h
capcontroloperationmessage.obj:	precompiled.h CtiTime.h dlldefs.h \
		CapControlOperationMessage.h StreamableMessage.h
connection.obj:	precompiled.h collectable.h connection.h dlldefs.h \
		exchange.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h macro_offset.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h amq_util.h
connection_base.obj:	precompiled.h mutex.h dlldefs.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		connection_base.h
connection_client.obj:	precompiled.h connection_client.h connection.h \
		dlldefs.h exchange.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h amq_constants.h \
		amq_util.h
connection_listener.obj:	precompiled.h connection_listener.h dlldefs.h \
		critical_section.h connection.h exchange.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h macro_offset.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h amq_constants.h \
		amq_util.h
connection_server.obj:	precompiled.h connection_server.h connection.h \
		dlldefs.h exchange.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h connection_listener.h \
		amq_constants.h amq_util.h
controlhistoryassociationresponse.obj:	precompiled.h \
		ControlHistoryAssociationResponse.h dlldefs.h
dispatchconnection.obj:	precompiled.h DispatchConnection.h \
		connection_client.h connection.h dlldefs.h exchange.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h MessageListener.h \
		msg_cmd.h amq_constants.h
dispatchpointdatarequest.obj:	precompiled.h DispatchPointDataRequest.h \
		yukon.h types.h ctidbgmem.h MessageListener.h message.h \
		ctitime.h dlldefs.h collectable.h DispatchConnection.h \
		connection_client.h connection.h exchange.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		readers_writer_lock.h critical_section.h connection_base.h \
		PointDataRequest.h msg_signal.h
dll_msg.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h amq_constants.h \
		amq_connection.h thread.h mutex.h guard.h critical_section.h \
		StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h
id_ctimsg.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h id_ctimsg.h
ivvcanalysismessage.obj:	precompiled.h CtiTime.h dlldefs.h \
		IVVCAnalysisMessage.h StreamableMessage.h
lmsepcontrolmessage.obj:	precompiled.h LMSepControlMessage.h dlldefs.h \
		msg_pcreturn.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		ctitime.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h
lmseprestoremessage.obj:	precompiled.h LMSepRestoreMessage.h dlldefs.h \
		msg_pcreturn.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		ctitime.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h
message.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		netports.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h
msg_cmd.obj:	precompiled.h msg_cmd.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h
msg_dbchg.obj:	precompiled.h collectable.h msg_dbchg.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h yukon.h types.h ctibase.h \
		ctinexus.h cticonnect.h netports.h socket_helper.h numstr.h \
		dllbase.h dsm2.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h
msg_lmcontrolhistory.obj:	precompiled.h collectable.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h msg_lmcontrolhistory.h pointdefs.h message.h \
		ctidbgmem.h yukon.h
msg_multi.obj:	precompiled.h collectable.h msg_multi.h dlldefs.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h ctitime.h ctibase.h ctinexus.h \
		cticonnect.h netports.h socket_helper.h numstr.h dllbase.h \
		dsm2.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h dsm2err.h words.h optional.h macro_offset.h \
		logger.h thread.h CtiPCPtrQueue.h
msg_multiwrap.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h msg_multiwrap.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h
msg_notif_alarm.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h msg_notif_alarm.h message.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
msg_notif_email.obj:	precompiled.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		msg_notif_email.h collectable.h logger.h thread.h mutex.h \
		guard.h CtiPCPtrQueue.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		macro_offset.h message.h
msg_notif_lmcontrol.obj:	precompiled.h msg_notif_lmcontrol.h dlldefs.h \
		message.h ctitime.h ctidbgmem.h collectable.h
msg_pcrequest.obj:	precompiled.h msg_pcrequest.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h dsm2.h cticonnect.h \
		yukon.h types.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h dllbase.h
msg_pcreturn.obj:	precompiled.h msg_pcreturn.h dlldefs.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h types.h ctidbgmem.h \
		pointdefs.h pointtypes.h message.h ctitime.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h
msg_pdata.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h logger.h thread.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h pointtypes.h msg_pdata.h yukon.h pointdefs.h
msg_ptreg.obj:	precompiled.h collectable.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h msg_ptreg.h \
		message.h ctidbgmem.h dllbase.h dsm2.h cticonnect.h yukon.h \
		netports.h dsm2err.h words.h optional.h macro_offset.h
msg_queuedata.obj:	precompiled.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h msg_queuedata.h yukon.h
msg_reg.obj:	precompiled.h collectable.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h msg_reg.h \
		message.h ctidbgmem.h dllbase.h dsm2.h cticonnect.h yukon.h \
		netports.h dsm2err.h words.h optional.h macro_offset.h
msg_requestcancel.obj:	precompiled.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h msg_requestcancel.h yukon.h
msg_server_req.obj:	precompiled.h msg_server_req.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h logger.h thread.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h CtiPCPtrQueue.h
msg_server_resp.obj:	precompiled.h msg_server_resp.h dlldefs.h \
		message.h ctitime.h ctidbgmem.h collectable.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h
msg_signal.obj:	precompiled.h collectable.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		msg_signal.h message.h ctidbgmem.h yukon.h msg_pdata.h \
		pointdefs.h pointtypes.h
msg_tag.obj:	precompiled.h collectable.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h msg_tag.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		dsm2err.h words.h optional.h macro_offset.h message.h
msg_trace.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h macro_offset.h collectable.h \
		logger.h thread.h CtiPCPtrQueue.h msg_trace.h message.h
pointdatahandler.obj:	precompiled.h PointDataHandler.h yukon.h types.h \
		ctidbgmem.h PointDataListener.h msg_pdata.h dlldefs.h \
		pointdefs.h pointtypes.h message.h ctitime.h collectable.h \
		MessageListener.h msg_ptreg.h
pointdatarequestfactory.obj:	precompiled.h PointDataRequestFactory.h \
		PointDataRequest.h yukon.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h DispatchConnection.h connection_client.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h \
		netports.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h MessageListener.h \
		DispatchPointDataRequest.h
porterresponsemessage.obj:	precompiled.h PorterResponseMessage.h \
		dlldefs.h msg_pcreturn.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h ctitime.h dsm2.h cticonnect.h \
		netports.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h
rfnbroadcastmessage.obj:	precompiled.h RfnBroadcastMessage.h dlldefs.h \
		ctitime.h msg_pcreturn.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h macro_offset.h
rfnbroadcastreplymessage.obj:	precompiled.h RfnBroadcastReplyMessage.h \
		dlldefs.h
server_client_serialization_test.obj:	precompiled.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h collectable.h msg_cmd.h \
		msg_dbchg.h yukon.h types.h msg_lmcontrolhistory.h \
		pointdefs.h msg_multi.h msg_pdata.h pointtypes.h \
		msg_notif_alarm.h msg_notif_email.h logger.h thread.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h dllbase.h dsm2.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h macro_offset.h \
		msg_notif_lmcontrol.h msg_pcrequest.h msg_pcreturn.h \
		msg_ptreg.h msg_queuedata.h Msg_reg.h msg_requestcancel.h \
		msg_server_req.h msg_server_resp.h msg_signal.h msg_tag.h \
		msg_trace.h test_serialization.h test_serialization_helper.h \
		connection_server.h connection.h exchange.h string_utility.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h connection_listener.h \
		std_helper.h
test_message.obj:	message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h queue.h cparms.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h
test_multi_msg.obj:	msg_multi.h collectable.h dlldefs.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h ctitime.h
test_pointdatarequest.obj:	PointDataRequestFactory.h \
		PointDataRequest.h yukon.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h DispatchConnection.h connection_client.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h \
		netports.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h MessageListener.h \
		DispatchPointdataRequest.h amq_constants.h
test_serialization.obj:	message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h msg_cmd.h msg_dbchg.h yukon.h types.h \
		msg_lmcontrolhistory.h pointdefs.h msg_multi.h msg_pdata.h \
		pointtypes.h msg_notif_alarm.h msg_notif_email.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h numstr.h CtiPCPtrQueue.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		macro_offset.h msg_notif_lmcontrol.h msg_pcrequest.h \
		msg_pcreturn.h msg_ptreg.h msg_queuedata.h Msg_reg.h \
		msg_requestcancel.h msg_server_req.h msg_server_resp.h \
		msg_signal.h msg_tag.h msg_trace.h test_serialization.h \
		test_serialization_helper.h connection_server.h connection.h \
		exchange.h string_utility.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		readers_writer_lock.h critical_section.h connection_base.h \
		connection_listener.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
