include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \
-I$(ACTIVEMQ) \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROTOCOL)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST_INCLUDE) \
;$(RW) \
;$(ACTIVEMQ) \
;$(ACTIVEMQ)\cms \
;$(ACTIVEMQ)\activemq\library \



OBJS=\
IVVCAnalysisMessage.obj \
DispatchConnection.obj \
DispatchPointDataRequest.obj \
PointDataHandler.obj \
PointDataRequestFactory.obj \
PorterResponseMessage.obj \
LMSepControlMessage.obj \
LMSepRestoreMessage.obj \
RfnBroadcastMessage.obj \
ControlHistoryAssociationResponse.obj \
amq_connection.obj \
connection.obj \
dll_msg.obj \
message.obj \
msg_cmd.obj \
msg_commerrorhistory.obj \
msg_dbchg.obj \
msg_lmcontrolhistory.obj \
msg_multi.obj \
msg_multiwrap.obj \
msg_notif_alarm.obj \
msg_notif_email.obj \
msg_notif_email_attachment.obj \
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


MESSAGE_FULLBUILD = $[Filename,$(OBJ),MessageFullBuild,target]

ACTIVEMQLIB=$(ACTIVEMQ)\lib\activemq-cpp.lib

CTIPROGS=\
ctimsg.dll


ALL:           $(CTIPROGS)


$(MESSAGE_FULLBUILD) :
        @touch $@
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(PARALLEL) $(INCLPATHS) /D_DLL_MESSAGE -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(OBJS)]


ctimsg.dll:    $(MESSAGE_FULLBUILD) $(OBJS) Makefile
                @build -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(OBJS) id_ctimsg.obj -link $(RWLIBS) $(BOOST_LIBS) $(ACTIVEMQLIB) $(COMPILEBASE)\lib\ctibase.lib $(LINKFLAGS)
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
		os2_2w32.h types.h numstr.h critical_section.h activemqcpp.h \
		connection.h StreamableMessage.h connectionfactory.h logger.h \
		CtiPCPtrQueue.h
connection.obj:	precompiled.h collectable.h connection.h dlldefs.h \
		exchange.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h
controlhistoryassociationresponse.obj:	precompiled.h \
		ControlHistoryAssociationResponse.h dlldefs.h
dispatchconnection.obj:	precompiled.h DispatchConnection.h \
		connection.h dlldefs.h exchange.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h MessageListener.h msg_cmd.h
dispatchpointdatarequest.obj:	precompiled.h DispatchPointDataRequest.h \
		yukon.h types.h ctidbgmem.h MessageListener.h message.h \
		ctitime.h dlldefs.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		DispatchConnection.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h PointDataRequest.h \
		msg_signal.h
dll_msg.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		amq_connection.h thread.h critical_section.h activemqcpp.h \
		connection.h
id_ctimsg.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h id_ctimsg.h
ivvcanalysismessage.obj:	precompiled.h CtiTime.h dlldefs.h \
		IVVCAnalysisMessage.h StreamableMessage.h
lmsepcontrolmessage.obj:	precompiled.h LMSepControlMessage.h dlldefs.h \
		msg_pcreturn.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		ctitime.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
lmseprestoremessage.obj:	precompiled.h LMSepRestoreMessage.h dlldefs.h \
		msg_pcreturn.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		ctitime.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
message.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h rwutil.h yukon.h types.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h logger.h thread.h CtiPCPtrQueue.h
msg_cmd.obj:	precompiled.h msg_cmd.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		logger.h thread.h CtiPCPtrQueue.h
msg_commerrorhistory.obj:	precompiled.h collectable.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h msg_commerrorhistory.h message.h ctidbgmem.h \
		rwutil.h yukon.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h
msg_dbchg.obj:	precompiled.h collectable.h msg_dbchg.h message.h \
		ctitime.h dlldefs.h ctidbgmem.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		ctibase.h ctinexus.h logger.h thread.h CtiPCPtrQueue.h
msg_lmcontrolhistory.obj:	precompiled.h collectable.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h msg_lmcontrolhistory.h pointdefs.h message.h \
		ctidbgmem.h rwutil.h yukon.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
msg_multi.obj:	precompiled.h collectable.h msg_multi.h dlldefs.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h ctitime.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		ctibase.h ctinexus.h logger.h thread.h CtiPCPtrQueue.h
msg_multiwrap.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h msg_multiwrap.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h
msg_notif_alarm.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h msg_notif_alarm.h message.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h
msg_notif_email.obj:	precompiled.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		msg_notif_email.h collectable.h logger.h thread.h mutex.h \
		guard.h CtiPCPtrQueue.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h message.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h \
		msg_notif_email_attachment.h
msg_notif_email_attachment.obj:	precompiled.h \
		msg_notif_email_attachment.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h
msg_notif_lmcontrol.obj:	precompiled.h msg_notif_lmcontrol.h dlldefs.h \
		message.h ctitime.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h types.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
msg_pcrequest.obj:	precompiled.h msg_pcrequest.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		logger.h thread.h CtiPCPtrQueue.h
msg_pcreturn.obj:	precompiled.h msg_pcreturn.h dlldefs.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h types.h ctidbgmem.h \
		pointdefs.h pointtypes.h message.h ctitime.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		logger.h thread.h CtiPCPtrQueue.h
msg_pdata.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h rwutil.h yukon.h types.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h logger.h thread.h CtiPCPtrQueue.h \
		pointtypes.h msg_pdata.h pointdefs.h
msg_ptreg.obj:	precompiled.h collectable.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h msg_ptreg.h \
		message.h ctidbgmem.h rwutil.h yukon.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h
msg_queuedata.obj:	precompiled.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		logger.h thread.h CtiPCPtrQueue.h msg_queuedata.h
msg_reg.obj:	precompiled.h collectable.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h msg_reg.h \
		message.h ctidbgmem.h rwutil.h yukon.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h
msg_requestcancel.obj:	precompiled.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		logger.h thread.h CtiPCPtrQueue.h msg_requestcancel.h
msg_server_req.obj:	precompiled.h msg_server_req.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		logger.h thread.h CtiPCPtrQueue.h
msg_server_resp.obj:	precompiled.h msg_server_resp.h dlldefs.h \
		message.h ctitime.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h types.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h logger.h thread.h CtiPCPtrQueue.h
msg_signal.obj:	precompiled.h collectable.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		msg_signal.h message.h ctidbgmem.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_pdata.h pointdefs.h pointtypes.h
msg_tag.obj:	precompiled.h collectable.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h msg_tag.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		dsm2err.h words.h optional.h message.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h
msg_trace.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h collectable.h logger.h thread.h \
		CtiPCPtrQueue.h msg_trace.h message.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h
pointdatahandler.obj:	precompiled.h PointDataHandler.h yukon.h types.h \
		ctidbgmem.h PointDataListener.h msg_pdata.h dlldefs.h \
		pointdefs.h pointtypes.h message.h ctitime.h collectable.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		MessageListener.h msg_ptreg.h
pointdatarequestfactory.obj:	precompiled.h PointDataRequestFactory.h \
		PointDataRequest.h yukon.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h DispatchConnection.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h MessageListener.h \
		DispatchPointDataRequest.h
porterresponsemessage.obj:	precompiled.h PorterResponseMessage.h \
		dlldefs.h msg_pcreturn.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h ctitime.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h
rfnbroadcastmessage.obj:	precompiled.h RfnBroadcastMessage.h dlldefs.h \
		msg_pcreturn.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		ctitime.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
test_message.obj:	message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h rwutil.h yukon.h types.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h queue.h cparms.h configkey.h \
		configval.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h
test_pointdatarequest.obj:	PointDataRequestFactory.h \
		PointDataRequest.h yukon.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h DispatchConnection.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h MessageListener.h \
		DispatchPointdataRequest.h
#ENDUPDATE#
