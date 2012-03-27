# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(MCCMD)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(TCL)\include \
-I$(RW) \
-I$(BOOST) \
-I$(SQLAPI)\include \



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
;$(RTDB)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)




DLLOBJS=\
mccmd.obj \
wpsc.obj \
decodeTextCmdFile.obj \
xcel.obj

DLLDEF=..\include\mccmd.def

LIBS=\
$(TCL_LIBS) \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib


MCCMD_FULLBUILD = $[Filename,$(OBJ),MccmdFullBuild,target]



ALL:            mccmd.dll


$(MCCMD_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -DWINDOWS -c $[StrReplace,.obj,.cpp,$(DLLOBJS)]


mccmd.dll:  $(MCCMD_FULLBUILD) $(DLLOBJS) Makedll.mak
            @echo Building  $@
            @%cd $(OBJ)
            $(CC) $(DLLFLAGS) $(DLLOBJS) $(INCLPATHS) $(LIBS) $(RWLIBS) $(BOOST_LIBS) /Fe..\$@ -link /def:$(DLLDEF) $(LINKFLAGS)
            -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
            -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
            -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
            -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
            @%cd $(CWD)


copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\mccmd.dll copy bin\mccmd.dll $(YUKONOUTPUT)
           -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
           -if exist bin\mccmd.lib copy bin\mccmd.lib $(COMPILEBASE)\lib

deps:
                scandeps -Output makedll.mak *.cpp



.cpp.obj :
                @echo Compiling: $<
                @echo C-Options: $(DLLFLAGS)
                @echo Output   : $@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -DWINDOWS -c $<

#UPDATE#
decodetextcmdfile.obj:	precompiled.h ctidate.h dlldefs.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		decodeTextCmdFile.h rwutil.h yukon.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h
mccmd.obj:	precompiled.h mccmd.h msg_pcrequest.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h logger.h thread.h CtiPCPtrQueue.h ctdpcptrq.h \
		tbl_meterreadlog.h connection.h exchange.h string_utility.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h cmdparse.h ctitokenizer.h parsevalue.h \
		msg_requestcancel.h msg_queuedata.h msg_signal.h msg_dbchg.h \
		msg_notif_email.h msg_notif_email_attachment.h \
		tbl_devicereadrequestlog.h ctibase.h ctinexus.h ctistring.h \
		mgr_holiday.h ctidate.h wpsc.h xcel.h decodetextcmdfile.h
mcsh.obj:	precompiled.h mcsh.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h mccmd.h msg_pcrequest.h \
		message.h ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h ctdpcptrq.h tbl_meterreadlog.h
test_decodetextcmdfile.obj:	decodetextcmdfile.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		rwutil.h yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
wpsc.obj:	precompiled.h wpsc.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h
xcel.obj:	precompiled.h xcel.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h
#ENDUPDATE#
