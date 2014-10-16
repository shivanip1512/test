include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \
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
IVVCAnalysisMessage.obj \
CapControlOperationMessage.obj \
DispatchConnection.obj \
DispatchPointDataRequest.obj \
PointDataHandler.obj \
PointDataRequestFactory.obj \
PorterResponseMessage.obj \
LMSepControlMessage.obj \
LMSepRestoreMessage.obj \
LMEcobeeMessages.obj \
RfnBroadcastMessage.obj \
RfnBroadcastReplyMessage.obj \
ControlHistoryAssociationResponse.obj \
amq_connection.obj \
amq_util.obj \
connection.obj \
connection_base.obj \
connection_client.obj \
connection_Server.obj \
connection_listener.obj \
dll_msg.obj \
message.obj \
msg_cmd.obj \
msg_dbchg.obj \
msg_lmcontrolhistory.obj \
msg_multi.obj \
msg_multiwrap.obj \
msg_notif_alarm.obj \
msg_notif_email.obj \
msg_notif_lmcontrol.obj \
msg_pcrequest.obj \
msg_pcreturn.obj \
msg_pdata.obj \
msg_ptreg.obj \
msg_queuedata.obj \
msg_reg.obj \
msg_requestcancel.obj \
msg_server_req.obj \
msg_server_resp.obj \
msg_signal.obj \
msg_tag.obj \
msg_trace.obj \
rfn_asid.obj \


SERIALIZATION_OBJS=\
message_factory.obj \
message_serialization.obj

SERIALIZATION_DIR=\
Serialization

SERIALIZATION_CPP=\
$(SERIALIZATION_DIR)\$[Separators," $(SERIALIZATION_DIR)\\",$[StrReplace,.obj,.cpp,$(SERIALIZATION_OBJS)]]


LIBS = \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctithriftmsg.lib \
$(RWLIBS) \
$(BOOST_LIBS) \
$(ACTIVEMQ_LIB) \
$(THRIFT_LIB) \


MESSAGE_FULLBUILD = $[Filename,$(OBJ),MessageFullBuild,target]


CTIPROGS=\
ctimsg.dll


PROGS_VERSION=\
$(CTIPROGS)


ALL:           $(CTIPROGS)


$(MESSAGE_FULLBUILD) :
        @touch $@
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(PARALLEL) $(INCLPATHS) /D_DLL_MESSAGE -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(OBJS)] $(SERIALIZATION_CPP)


ctimsg.dll:    $(MESSAGE_FULLBUILD) $(OBJS) $(SERIALIZATION_OBJS) Makefile $(OBJ)\ctimsg.res
                @build -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(OBJS) $(SERIALIZATION_OBJS) id_ctimsg.obj -link $(LIBS) $(LINKFLAGS) ctimsg.res
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

# The lines below accomplish the ID'ing of the project!
id:
            @build -nologo -f $(_InputFile) id_ctimsg.obj

id_ctimsg.obj:    id_ctimsg.cpp include\id_ctimsg.h




########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_MESSAGE -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
amq_connection.obj:	precompiled.h amq_connection.h thread.h mutex.h \
		dlldefs.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h constants.h numstr.h critical_section.h \
		StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h logger.h streamBuffer.h \
		string_util.h dllbase.h dsm2.h streamConnection.h yukon.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h dsm2err.h words.h optional.h \
		macro_offset.h exception_helper.h amq_util.h std_helper.h
amq_util.obj:	precompiled.h amq_util.h dlldefs.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h exception_helper.h
capcontroloperationmessage.obj:	precompiled.h CtiTime.h dlldefs.h \
		CapControlOperationMessage.h StreamableMessage.h
connection.obj:	precompiled.h collectable.h connection.h dlldefs.h \
		message.h ctitime.h ctidbgmem.h msg_multi.h msg_pdata.h \
		yukon.h types.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h amq_util.h \
		millisecond_timer.h
connection_base.obj:	precompiled.h mutex.h dlldefs.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h constants.h \
		numstr.h connection_base.h
connection_client.obj:	precompiled.h connection_client.h connection.h \
		dlldefs.h message.h ctitime.h ctidbgmem.h collectable.h \
		msg_multi.h msg_pdata.h yukon.h types.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		amq_constants.h amq_util.h
connection_listener.obj:	precompiled.h connection_listener.h dlldefs.h \
		critical_section.h connection.h message.h ctitime.h \
		ctidbgmem.h collectable.h msg_multi.h msg_pdata.h yukon.h \
		types.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		amq_constants.h amq_util.h
connection_server.obj:	precompiled.h connection_server.h connection.h \
		dlldefs.h message.h ctitime.h ctidbgmem.h collectable.h \
		msg_multi.h msg_pdata.h yukon.h types.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		connection_listener.h amq_constants.h amq_util.h
controlhistoryassociationresponse.obj:	precompiled.h \
		ControlHistoryAssociationResponse.h dlldefs.h
dispatchconnection.obj:	precompiled.h DispatchConnection.h \
		connection_client.h connection.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h msg_multi.h msg_pdata.h \
		yukon.h types.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		MessageListener.h msg_cmd.h amq_constants.h
dispatchpointdatarequest.obj:	precompiled.h DispatchPointDataRequest.h \
		yukon.h types.h ctidbgmem.h MessageListener.h message.h \
		ctitime.h dlldefs.h collectable.h DispatchConnection.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		PointDataRequest.h msg_signal.h
dll_msg.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h \
		amq_constants.h amq_connection.h thread.h mutex.h guard.h \
		critical_section.h StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h
dll_rfn_e2e.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h \
		rfn_e2e_messenger.h rfn_asid.h rfn_identifier.h \
		RfnE2eDataIndicationMsg.h RfnE2eMsg.h RfnE2eDataConfirmMsg.h \
		RfnE2eDataRequestMsg.h readers_writer_lock.h \
		critical_section.h guard.h yukon.h ctidbgmem.h
id_ctimsg.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h \
		id_ctimsg.h
id_rfn_e2e.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h \
		id_rfn_e2e.h
ivvcanalysismessage.obj:	precompiled.h CtiTime.h dlldefs.h \
		IVVCAnalysisMessage.h StreamableMessage.h
lmecobeemessages.obj:	precompiled.h LMEcobeeMessages.h dlldefs.h \
		msg_pcreturn.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		ctitime.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h
lmsepcontrolmessage.obj:	precompiled.h LMSepControlMessage.h dlldefs.h \
		msg_pcreturn.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		ctitime.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h
lmseprestoremessage.obj:	precompiled.h LMSepRestoreMessage.h dlldefs.h \
		msg_pcreturn.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		ctitime.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h
message.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h logger.h \
		streamBuffer.h string_util.h exception_helper.h
msg_cmd.obj:	precompiled.h msg_cmd.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h logger.h \
		streamBuffer.h string_util.h exception_helper.h
msg_dbchg.obj:	precompiled.h collectable.h msg_dbchg.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h yukon.h types.h ctibase.h \
		streamSocketConnection.h streamConnection.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h socket_helper.h win_helper.h \
		dllbase.h dsm2.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h streamBuffer.h string_util.h \
		exception_helper.h
msg_lmcontrolhistory.obj:	precompiled.h collectable.h logger.h \
		dlldefs.h streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h exception_helper.h \
		msg_lmcontrolhistory.h pointdefs.h message.h
msg_multi.obj:	precompiled.h collectable.h msg_multi.h dlldefs.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h ctitime.h ctibase.h \
		streamSocketConnection.h streamConnection.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h socket_helper.h win_helper.h \
		dllbase.h dsm2.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h streamBuffer.h string_util.h \
		exception_helper.h
msg_multiwrap.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		string_util.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h msg_multiwrap.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h
msg_notif_alarm.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		string_util.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h msg_notif_alarm.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h
msg_notif_email.obj:	precompiled.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h constants.h numstr.h \
		msg_notif_email.h collectable.h logger.h streamBuffer.h \
		string_util.h dllbase.h dsm2.h streamConnection.h yukon.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h message.h
msg_notif_lmcontrol.obj:	precompiled.h msg_notif_lmcontrol.h dlldefs.h \
		message.h ctitime.h ctidbgmem.h collectable.h
msg_pcrequest.obj:	precompiled.h msg_pcrequest.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h dsm2.h streamConnection.h \
		yukon.h types.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h logger.h \
		streamBuffer.h string_util.h dllbase.h exception_helper.h
msg_pcreturn.obj:	precompiled.h msg_pcreturn.h dlldefs.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h types.h ctidbgmem.h \
		pointdefs.h pointtypes.h message.h ctitime.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h logger.h \
		streamBuffer.h string_util.h dllbase.h exception_helper.h
msg_pdata.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h logger.h streamBuffer.h string_util.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h exception_helper.h pointtypes.h \
		msg_pdata.h pointdefs.h
msg_ptreg.obj:	precompiled.h collectable.h logger.h dlldefs.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h exception_helper.h \
		msg_ptreg.h message.h
msg_queuedata.obj:	precompiled.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h logger.h streamBuffer.h \
		string_util.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h msg_queuedata.h
msg_reg.obj:	precompiled.h collectable.h logger.h dlldefs.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h exception_helper.h \
		msg_reg.h message.h
msg_requestcancel.obj:	precompiled.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h logger.h streamBuffer.h \
		string_util.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h msg_requestcancel.h
msg_server_req.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		string_util.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h msg_server_req.h message.h collectable.h
msg_server_resp.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		string_util.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h msg_server_resp.h message.h collectable.h
msg_signal.obj:	precompiled.h collectable.h logger.h dlldefs.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h exception_helper.h \
		msg_signal.h message.h msg_pdata.h pointdefs.h pointtypes.h
msg_tag.obj:	precompiled.h collectable.h logger.h dlldefs.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h exception_helper.h \
		msg_tag.h message.h
msg_trace.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h collectable.h logger.h \
		streamBuffer.h string_util.h exception_helper.h msg_trace.h \
		message.h
pointdatahandler.obj:	precompiled.h PointDataHandler.h yukon.h types.h \
		ctidbgmem.h PointDataListener.h msg_pdata.h dlldefs.h \
		pointdefs.h pointtypes.h message.h ctitime.h collectable.h \
		MessageListener.h msg_ptreg.h
pointdatarequestfactory.obj:	precompiled.h PointDataRequestFactory.h \
		PointDataRequest.h yukon.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h DispatchConnection.h connection_client.h \
		connection.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		MessageListener.h DispatchPointDataRequest.h
porterresponsemessage.obj:	precompiled.h PorterResponseMessage.h \
		dlldefs.h msg_pcreturn.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h ctitime.h dsm2.h streamConnection.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
rfnbroadcastmessage.obj:	precompiled.h RfnBroadcastMessage.h dlldefs.h \
		ctitime.h msg_pcreturn.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
rfnbroadcastreplymessage.obj:	precompiled.h RfnBroadcastReplyMessage.h \
		dlldefs.h
rfne2emsgserialization.obj:	precompiled.h RfnE2eDataRequestMsg.h \
		rfn_identifier.h RfnE2eMsg.h RfnE2eDataConfirmMsg.h \
		RfnE2eDataIndicationMsg.h
rfn_asid.obj:	precompiled.h rfn_asid.h dlldefs.h
rfn_e2e_messenger.obj:	precompiled.h rfn_e2e_messenger.h rfn_asid.h \
		dlldefs.h rfn_identifier.h RfnE2eDataIndicationMsg.h \
		RfnE2eMsg.h RfnE2eDataConfirmMsg.h RfnE2eDataRequestMsg.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h constants.h \
		numstr.h yukon.h ctidbgmem.h amq_connection.h thread.h \
		mutex.h StreamableMessage.h connection_base.h \
		RfnBroadcastReplyMessage.h std_helper.h boostutil.h
server_client_serialization_test.obj:	precompiled.h logManager.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h constants.h numstr.h logger.h streamBuffer.h \
		string_util.h dllbase.h dsm2.h streamConnection.h yukon.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h message.h collectable.h msg_cmd.h \
		msg_dbchg.h msg_lmcontrolhistory.h pointdefs.h msg_multi.h \
		msg_pdata.h pointtypes.h msg_notif_alarm.h msg_notif_email.h \
		msg_notif_lmcontrol.h msg_pcrequest.h msg_pcreturn.h \
		msg_ptreg.h msg_queuedata.h Msg_reg.h msg_requestcancel.h \
		msg_server_req.h msg_server_resp.h msg_signal.h msg_tag.h \
		msg_trace.h test_serialization.h test_serialization_helper.h \
		connection_server.h connection.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		string_utility.h connection_base.h worker_thread.h \
		connection_listener.h std_helper.h
test_message.obj:	message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h queue.h cparms.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h
test_multi_msg.obj:	msg_multi.h collectable.h dlldefs.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h ctitime.h
test_pointdatarequest.obj:	PointDataRequestFactory.h \
		PointDataRequest.h yukon.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h DispatchConnection.h connection_client.h \
		connection.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		MessageListener.h DispatchPointdataRequest.h amq_constants.h
test_serialization.obj:	message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h msg_cmd.h msg_dbchg.h yukon.h types.h \
		msg_lmcontrolhistory.h pointdefs.h msg_multi.h msg_pdata.h \
		pointtypes.h msg_notif_alarm.h msg_notif_email.h logger.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h msg_notif_lmcontrol.h msg_pcrequest.h \
		msg_pcreturn.h msg_ptreg.h msg_queuedata.h Msg_reg.h \
		msg_requestcancel.h msg_server_req.h msg_server_resp.h \
		msg_signal.h msg_tag.h msg_trace.h test_serialization.h \
		test_serialization_helper.h connection_server.h connection.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h string_utility.h connection_base.h \
		worker_thread.h connection_listener.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
