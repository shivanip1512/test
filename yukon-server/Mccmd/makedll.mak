# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(MCCMD)\include \
-I$(CPARMS)\include \
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
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
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
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -DWINDOWS -c $<

#UPDATE#
decodetextcmdfile.obj:	decodeTextCmdFile.h logger.h thread.h mutex.h \
		dlldefs.h guard.h types.h
mccmd.obj:	mccmd.h msg_pcrequest.h dlldefs.h message.h ctidbgmem.h \
		collectable.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		ctdpcptrq.h dbaccess.h sema.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configparms.h \
		netports.h msg_email.h ctibase.h ctinexus.h pointtypes.h \
		numstr.h mgr_holiday.h dsm2err.h wpsc.h xcel.h \
		decodetextcmdfile.h
mcs8100test.obj:	wpsc.h logger.h thread.h mutex.h dlldefs.h guard.h \
		types.h
mcsh.obj:	mcsh.h logger.h thread.h mutex.h dlldefs.h guard.h mccmd.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h ctdpcptrq.h
wpsc.obj:	wpsc.h logger.h thread.h mutex.h dlldefs.h guard.h types.h \
		numstr.h
xcel.obj:	xcel.h logger.h thread.h mutex.h dlldefs.h guard.h types.h
#ENDUPDATE#
