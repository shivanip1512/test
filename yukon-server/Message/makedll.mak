include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
-I$(BOOST) \
-I$(RW) \


.PATH.cpp = .

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
;$(BOOST) \
;$(RW)



OBJS=\
connection.obj \
PointDataHandler.obj \
DispatchConnection.obj \
GroupPointDataRequest.obj \
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
msg_signal.obj \
msg_pdata.obj \
msg_queuedata.obj \
msg_requestcancel.obj \
msg_reg.obj \
msg_ptreg.obj \
msg_server_req.obj \
msg_server_resp.obj \
msg_tag.obj \
msg_trace.obj \


MESSAGE_FULLBUILD = $[Filename,$(OBJ),MessageFullBuild,target]


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
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(OBJS) id_ctimsg.obj -link $(RWLIBS) $(BOOSTLIBS) $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\cticparms.lib $(COMPILEBASE)\lib\clrdump.lib $(LINKFLAGS)
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

id_ctimsg.obj:    id_ctimsg.cpp include\id_ctimsg.h id_vinfo.h




########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_MESSAGE -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
connection.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		connection.h dlldefs.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		rwutil.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h
dispatchconnection.obj:	yukon.h precompiled.h ctidbgmem.h \
		DispatchConnection.h connection.h dlldefs.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h
dll_msg.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		os2_2w32.h types.h cticalls.h connection.h exchange.h \
		dllbase.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h message.h collectable.h \
		rwutil.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h
id_ctimsg.obj:	yukon.h precompiled.h ctidbgmem.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h id_ctimsg.h id_vinfo.h
message.obj:	yukon.h precompiled.h ctidbgmem.h message.h collectable.h \
		dlldefs.h rwutil.h ctitime.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h dllbase.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h CtiPCPtrQueue.h
msg_cmd.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h collectable.h msg_cmd.h \
		message.h rwutil.h ctitime.h boost_time.h boostutil.h \
		utility.h queues.h sorted_vector.h logger.h thread.h \
		CtiPCPtrQueue.h
msg_commerrorhistory.obj:	yukon.h precompiled.h ctidbgmem.h \
		collectable.h logger.h dlldefs.h thread.h mutex.h guard.h \
		numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		msg_commerrorhistory.h message.h rwutil.h boost_time.h \
		boostutil.h
msg_dbchg.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		msg_dbchg.h message.h dlldefs.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h ctibase.h \
		ctinexus.h netports.h cticonnect.h dllbase.h dsm2.h mutex.h \
		guard.h clrdump.h logger.h thread.h CtiPCPtrQueue.h
msg_lmcontrolhistory.obj:	yukon.h precompiled.h ctidbgmem.h \
		collectable.h logger.h dlldefs.h thread.h mutex.h guard.h \
		numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		msg_lmcontrolhistory.h pointdefs.h message.h rwutil.h \
		boost_time.h boostutil.h
msg_multi.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		msg_multi.h dlldefs.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h rwutil.h ctitime.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h ctibase.h ctinexus.h netports.h cticonnect.h \
		dllbase.h dsm2.h mutex.h guard.h clrdump.h logger.h thread.h \
		CtiPCPtrQueue.h
msg_multiwrap.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h msg_multiwrap.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h rwutil.h boost_time.h boostutil.h
msg_notif_alarm.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h msg_notif_alarm.h \
		message.h collectable.h rwutil.h boost_time.h boostutil.h
msg_notif_email.obj:	yukon.h precompiled.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h msg_notif_email.h collectable.h \
		logger.h thread.h mutex.h guard.h clrdump.h CtiPCPtrQueue.h \
		dllbase.h dsm2.h cticonnect.h netports.h message.h rwutil.h \
		boost_time.h boostutil.h msg_notif_email_attachment.h
msg_notif_email_attachment.obj:	yukon.h precompiled.h ctidbgmem.h \
		msg_notif_email_attachment.h logger.h dlldefs.h thread.h \
		mutex.h guard.h numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h \
		sorted_vector.h dllbase.h dsm2.h cticonnect.h netports.h \
		message.h collectable.h rwutil.h boost_time.h boostutil.h
msg_notif_lmcontrol.obj:	yukon.h precompiled.h ctidbgmem.h \
		msg_notif_lmcontrol.h dlldefs.h message.h collectable.h \
		rwutil.h ctitime.h boost_time.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h
msg_pcrequest.obj:	yukon.h precompiled.h ctidbgmem.h msg_pcrequest.h \
		dlldefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h logger.h thread.h \
		mutex.h guard.h clrdump.h CtiPCPtrQueue.h dllbase.h dsm2.h \
		cticonnect.h netports.h
msg_pcreturn.obj:	yukon.h precompiled.h ctidbgmem.h msg_pcreturn.h \
		dlldefs.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h rwutil.h ctitime.h boost_time.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h logger.h thread.h mutex.h guard.h \
		clrdump.h CtiPCPtrQueue.h
msg_pdata.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h logger.h thread.h mutex.h guard.h \
		clrdump.h CtiPCPtrQueue.h pointtypes.h msg_pdata.h \
		pointdefs.h
msg_ptreg.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h msg_ptreg.h \
		message.h rwutil.h boost_time.h boostutil.h dllbase.h dsm2.h \
		cticonnect.h netports.h
msg_queuedata.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h logger.h thread.h mutex.h guard.h \
		clrdump.h CtiPCPtrQueue.h msg_queuedata.h
msg_reg.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h msg_reg.h message.h \
		rwutil.h boost_time.h boostutil.h dllbase.h dsm2.h \
		cticonnect.h netports.h
msg_requestcancel.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h logger.h thread.h mutex.h guard.h \
		clrdump.h CtiPCPtrQueue.h msg_requestcancel.h
msg_server_req.obj:	yukon.h precompiled.h ctidbgmem.h msg_server_req.h \
		dlldefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h logger.h thread.h \
		mutex.h guard.h clrdump.h CtiPCPtrQueue.h
msg_server_resp.obj:	yukon.h precompiled.h ctidbgmem.h \
		msg_server_resp.h dlldefs.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		logger.h thread.h mutex.h guard.h clrdump.h CtiPCPtrQueue.h
msg_signal.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h msg_signal.h \
		message.h rwutil.h boost_time.h boostutil.h msg_pdata.h \
		pointdefs.h pointtypes.h
msg_tag.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h msg_tag.h dllbase.h dsm2.h \
		cticonnect.h netports.h message.h rwutil.h boost_time.h \
		boostutil.h
msg_trace.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h collectable.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h msg_trace.h message.h rwutil.h boost_time.h \
		boostutil.h
pointdatahandler.obj:	yukon.h precompiled.h ctidbgmem.h \
		PointDataHandler.h PointDataListener.h msg_pdata.h dlldefs.h \
		pointdefs.h pointtypes.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		msg_ptreg.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
test_message.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h queue.h cparms.h configkey.h \
		configval.h logger.h thread.h mutex.h guard.h clrdump.h \
		CtiPCPtrQueue.h
test_pointdatahandler.obj:	yukon.h precompiled.h ctidbgmem.h
#ENDUPDATE#
