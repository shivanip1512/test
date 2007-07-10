# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(MCCMD)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(TCLINC) \
-I$(RW) \
-I$(BOOST) \



.PATH.cpp = .;$(R_MCCMD)

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
$(COMPILEBASE)\lib\$(TCL_LIB).lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib

ALL:            mccmd.dll


mccmd.dll:  $(DLLOBJS) Makedll.mak
            @echo Building  $@
            @%cd $(OBJ)
            $(CC) $(DLLFLAGS) $(DLLOBJS) $(INCLPATHS) $(LIBS) $(RWLIBS) $(BOOSTLIBS) /Fe..\$@ -link /def:$(DLLDEF)
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
decodetextcmdfile.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h \
		dlldefs.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		types.h sorted_vector.h decodeTextCmdFile.h rwutil.h \
		boost_time.h
mccmd.obj:	yukon.h precompiled.h ctidbgmem.h mccmd.h msg_pcrequest.h \
		dlldefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h ctdpcptrq.h dllBase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h tbl_meterreadlog.h dbaccess.h \
		sema.h connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h configparms.h \
		msg_queuedata.h msg_signal.h msg_dbchg.h msg_notif_email.h \
		msg_notif_email_attachment.h tbl_devicereadrequestlog.h \
		ctibase.h ctinexus.h pointtypes.h ctistring.h mgr_holiday.h \
		ctidate.h dsm2err.h wpsc.h xcel.h decodetextcmdfile.h
mcs8100test.obj:	yukon.h precompiled.h ctidbgmem.h wpsc.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h
mcsh.obj:	yukon.h precompiled.h ctidbgmem.h mcsh.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h \
		mccmd.h msg_pcrequest.h message.h collectable.h rwutil.h \
		boost_time.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h ctdpcptrq.h dllBase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h tbl_meterreadlog.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
wpsc.obj:	yukon.h precompiled.h ctidbgmem.h wpsc.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h
xcel.obj:	yukon.h precompiled.h ctidbgmem.h xcel.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h types.h sorted_vector.h
#ENDUPDATE#
