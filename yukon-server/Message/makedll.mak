include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
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



OBJS=\
connection.obj \
dll_msg.obj \
message.obj \
msg_cmd.obj \
msg_commerrorhistory.obj \
msg_dbchg.obj \
msg_email.obj \
msg_lmcontrolhistory.obj \
msg_multi.obj \
msg_multiwrap.obj \
msg_notif_email.obj \
msg_notif_email_attachment.obj \
msg_pcrequest.obj \
msg_pcreturn.obj \
msg_signal.obj \
msg_pdata.obj \
msg_reg.obj \
msg_ptreg.obj \
msg_server_req.obj \
msg_server_resp.obj \
msg_tag.obj \
msg_trace.obj \




CTIPROGS=\
ctimsg.dll


ALL:           $(CTIPROGS)

ctimsg.dll:    $(OBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(OBJS) id_ctimsg.obj -link $(RWLIBS) $(BOOSTLIBS) $(COMPILEBASE)\lib\ctibase.lib
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
            @$(MAKE) -nologo -f $(_InputFile) id_ctimsg.obj

id_ctimsg.obj:    id_ctimsg.cpp include\id_ctimsg.h id_vinfo.h




########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) /D_DLL_MESSAGE -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
connection.obj:	collectable.h connection.h dlldefs.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h message.h ctidbgmem.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h numstr.h
dll_msg.obj:	dsm2.h mutex.h dlldefs.h guard.h yukon.h ctidbgmem.h \
		dllbase.h os2_2w32.h types.h cticalls.h connection.h \
		exchange.h logger.h thread.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h utility.h
id_ctimsg.obj:	utility.h dsm2.h mutex.h dlldefs.h guard.h id_ctimsg.h \
		id_build.h id_vinfo.h
message.obj:	message.h ctidbgmem.h collectable.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h
msg_cmd.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h \
		mutex.h guard.h collectable.h msg_cmd.h message.h ctidbgmem.h \
		logger.h thread.h
msg_commerrorhistory.obj:	collectable.h logger.h thread.h mutex.h \
		dlldefs.h guard.h msg_commerrorhistory.h message.h \
		ctidbgmem.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h
msg_dbchg.obj:	collectable.h msg_dbchg.h message.h ctidbgmem.h \
		dlldefs.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h ctibase.h ctinexus.h logger.h thread.h
msg_email.obj:	collectable.h logger.h thread.h mutex.h dlldefs.h \
		guard.h msg_email.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h message.h ctidbgmem.h yukon.h
msg_lmcontrolhistory.obj:	collectable.h logger.h thread.h mutex.h \
		dlldefs.h guard.h msg_lmcontrolhistory.h pointdefs.h \
		message.h ctidbgmem.h yukon.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h
msg_multi.obj:	collectable.h msg_multi.h dlldefs.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h ctibase.h ctinexus.h logger.h thread.h
msg_multiwrap.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		msg_multiwrap.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h
msg_pcrequest.obj:	msg_pcrequest.h dlldefs.h message.h ctidbgmem.h \
		collectable.h logger.h thread.h mutex.h guard.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h
msg_pcreturn.obj:	msg_pcreturn.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h
msg_pdata.obj:	message.h ctidbgmem.h collectable.h dlldefs.h logger.h \
		thread.h mutex.h guard.h pointtypes.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h
msg_ptchg.obj:	collectable.h logger.h thread.h mutex.h dlldefs.h \
		guard.h msg_ptchg.h msg_pdata.h pointdefs.h message.h \
		ctidbgmem.h msg_signal.h yukon.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h ctibase.h ctinexus.h
msg_ptreg.obj:	collectable.h logger.h thread.h mutex.h dlldefs.h \
		guard.h msg_ptreg.h message.h ctidbgmem.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h
msg_reg.obj:	collectable.h logger.h thread.h mutex.h dlldefs.h guard.h \
		msg_reg.h message.h ctidbgmem.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h
msg_signal.obj:	collectable.h logger.h thread.h mutex.h dlldefs.h \
		guard.h msg_signal.h message.h ctidbgmem.h yukon.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h utility.h
msg_tag.obj:	collectable.h logger.h thread.h mutex.h dlldefs.h guard.h \
		msg_tag.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		message.h ctidbgmem.h yukon.h
msg_trace.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h \
		dsm2.h mutex.h guard.h collectable.h logger.h thread.h \
		msg_trace.h message.h ctidbgmem.h
#ENDUPDATE#
