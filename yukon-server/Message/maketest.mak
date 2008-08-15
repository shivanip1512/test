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
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST) \
;$(RW)

LIBS=\
kernel32.lib user32.lib advapi32.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\clrdump.lib \

ALL:            ctimsgtest

ctimsgtest:    test_message.obj Makefile

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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

        @echo:
        @echo Creating Executable $(OBJ)\$(@B).exe
        @echo:
        $(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe$(BIN)\$(@B).exe \
        .\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
        -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
        -if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
        @%cd $(CWD)
        @echo.

######################################################################################

#UPDATE#
connection.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		connection.h dlldefs.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		rwutil.h boost_time.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h
dll_msg.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		os2_2w32.h types.h cticalls.h connection.h exchange.h \
		dllbase.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h message.h collectable.h \
		rwutil.h boost_time.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h
id_ctimsg.obj:	yukon.h precompiled.h ctidbgmem.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h id_ctimsg.h id_vinfo.h
message.obj:	yukon.h precompiled.h ctidbgmem.h message.h collectable.h \
		dlldefs.h rwutil.h ctitime.h boost_time.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
msg_cmd.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h collectable.h msg_cmd.h \
		message.h rwutil.h ctitime.h boost_time.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
msg_commerrorhistory.obj:	yukon.h precompiled.h ctidbgmem.h \
		collectable.h logger.h dlldefs.h thread.h mutex.h guard.h \
		numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		msg_commerrorhistory.h message.h rwutil.h boost_time.h
msg_dbchg.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		msg_dbchg.h message.h dlldefs.h rwutil.h ctitime.h \
		boost_time.h ctibase.h ctinexus.h netports.h cticonnect.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h
msg_lmcontrolhistory.obj:	yukon.h precompiled.h ctidbgmem.h \
		collectable.h logger.h dlldefs.h thread.h mutex.h guard.h \
		numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		msg_lmcontrolhistory.h pointdefs.h message.h rwutil.h \
		boost_time.h
msg_multi.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		msg_multi.h dlldefs.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h rwutil.h ctitime.h boost_time.h ctibase.h \
		ctinexus.h netports.h cticonnect.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		logger.h thread.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h
msg_multiwrap.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h msg_multiwrap.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h rwutil.h boost_time.h
msg_notif_alarm.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h msg_notif_alarm.h \
		message.h collectable.h rwutil.h boost_time.h
msg_notif_email.obj:	yukon.h precompiled.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h msg_notif_email.h collectable.h \
		logger.h thread.h mutex.h guard.h clrdump.h CtiPCPtrQueue.h \
		dllbase.h dsm2.h cticonnect.h netports.h message.h rwutil.h \
		boost_time.h msg_notif_email_attachment.h
msg_notif_email_attachment.obj:	yukon.h precompiled.h ctidbgmem.h \
		msg_notif_email_attachment.h logger.h dlldefs.h thread.h \
		mutex.h guard.h numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h \
		sorted_vector.h dllbase.h dsm2.h cticonnect.h netports.h \
		message.h collectable.h rwutil.h boost_time.h
msg_notif_lmcontrol.obj:	yukon.h precompiled.h ctidbgmem.h \
		msg_notif_lmcontrol.h dlldefs.h message.h collectable.h \
		rwutil.h ctitime.h boost_time.h
msg_pcrequest.obj:	yukon.h precompiled.h ctidbgmem.h msg_pcrequest.h \
		dlldefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h dllbase.h dsm2.h \
		cticonnect.h netports.h
msg_pcreturn.obj:	yukon.h precompiled.h ctidbgmem.h msg_pcreturn.h \
		dlldefs.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h rwutil.h ctitime.h boost_time.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h
msg_pdata.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h pointtypes.h msg_pdata.h pointdefs.h
msg_ptreg.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h msg_ptreg.h \
		message.h rwutil.h boost_time.h dllbase.h dsm2.h cticonnect.h \
		netports.h
msg_queuedata.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h msg_queuedata.h
msg_reg.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h msg_reg.h message.h \
		rwutil.h boost_time.h dllbase.h dsm2.h cticonnect.h \
		netports.h
msg_requestcancel.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h msg_requestcancel.h
msg_server_req.obj:	yukon.h precompiled.h ctidbgmem.h msg_server_req.h \
		dlldefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h
msg_server_resp.obj:	yukon.h precompiled.h ctidbgmem.h \
		msg_server_resp.h dlldefs.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h
msg_signal.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h msg_signal.h \
		message.h rwutil.h boost_time.h msg_pdata.h pointdefs.h \
		pointtypes.h
msg_tag.obj:	yukon.h precompiled.h ctidbgmem.h collectable.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h msg_tag.h dllbase.h dsm2.h \
		cticonnect.h netports.h message.h rwutil.h boost_time.h
msg_trace.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h collectable.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h msg_trace.h message.h rwutil.h boost_time.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
test_message.obj:	yukon.h precompiled.h ctidbgmem.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		queue.h cparms.h configkey.h configval.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h
#ENDUPDATE#
