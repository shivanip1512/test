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



OBJS=\
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




CTIPROGS=\
ctimsg.dll


ALL:           $(CTIPROGS)

ctimsg.dll:    $(OBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(OBJS) id_ctimsg.obj -link $(RWLIBS) $(BOOSTLIBS) $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\cticparms.lib
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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_MESSAGE -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
connection.obj:	connection.h message.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h
dll_msg.obj:	connection.h message.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h
id_ctimsg.obj:	id_ctimsg.h
message.obj:	message.h
msg_cmd.obj:	msg_cmd.h message.h
msg_commerrorhistory.obj:	msg_commerrorhistory.h message.h
msg_dbchg.obj:	msg_dbchg.h message.h
msg_lmcontrolhistory.obj:	msg_lmcontrolhistory.h message.h
msg_multi.obj:	msg_multi.h msg_pdata.h message.h
msg_multiwrap.obj:	msg_multiwrap.h msg_multi.h msg_pdata.h message.h
msg_notif_alarm.obj:	msg_notif_alarm.h message.h
msg_notif_email.obj:	msg_notif_email.h message.h \
		msg_notif_email_attachment.h
msg_notif_email_attachment.obj:	msg_notif_email_attachment.h message.h
msg_notif_lmcontrol.obj:	msg_notif_lmcontrol.h message.h
msg_pcrequest.obj:	msg_pcrequest.h message.h
msg_pcreturn.obj:	msg_pcreturn.h msg_multi.h msg_pdata.h message.h
msg_pdata.obj:	message.h msg_pdata.h
msg_ptreg.obj:	msg_ptreg.h message.h
msg_queuedata.obj:	message.h msg_queuedata.h
msg_reg.obj:	msg_reg.h message.h
msg_requestcancel.obj:	message.h msg_queuedata.h
msg_server_req.obj:	msg_server_req.h message.h
msg_server_resp.obj:	msg_server_resp.h message.h
msg_signal.obj:	msg_signal.h message.h msg_pdata.h
msg_tag.obj:	msg_tag.h message.h
msg_trace.obj:	msg_trace.h message.h
#ENDUPDATE#
