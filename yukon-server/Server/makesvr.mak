# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

#
# ALWAYS place the local drives/direcories before the reference ones.
#
INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(DISPATCH)\include \
-I$(MSG)\include \
-I$(RW) \


.PATH.cpp = .;$(R_SERVER)

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)



SVRLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\cmdline.lib \


SERVEROBJS=\
con_mgr.obj \
exe_cmd.obj \
exe_reg.obj \
executor.obj \
executorfactory.obj \
server_b.obj

ALL:            ctisvr.dll


ctisvr.dll:     $(SERVEROBJS) makesvr.mak
                @echo Building  $@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(SERVEROBJS) $(INCLPATHS) $(RWLIBS) \
$(SVRLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\ctisvr.dll copy bin\ctisvr.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\ctisvr.lib copy bin\ctisvr.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp


deps:
                scandeps -Output makesvr.mak *.cpp


.cpp.obj :
                @echo:
                @echo Compiling: $< Output: $@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) /DCTISVR $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

#######
#UPDATE#
con_mgr.obj:	dlldefs.h collectable.h con_mgr.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h message.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h ctibase.h ctinexus.h
dlldbmemmgr.obj:	dlldefs.h utility.h dsm2.h mutex.h guard.h
executor.obj:	executor.h message.h collectable.h dlldefs.h con_mgr.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h
executorfactory.obj:	executorfactory.h collectable.h message.h \
		dlldefs.h executor.h exe_cmd.h exe_reg.h
exe_cmd.obj:	dlldefs.h con_mgr.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h ctibase.h ctinexus.h server_b.h cmdopts.h msg_cmd.h \
		exe_cmd.h executor.h
exe_reg.obj:	message.h collectable.h dlldefs.h exe_reg.h executor.h \
		con_mgr.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h \
		con_mgr_vg.h vgexe_factory.h exe_ptchg.h exe_email.h \
		executorfactory.h exe_cmd.h msg_cmd.h server_b.h cmdopts.h
server_b.obj:	executor.h message.h collectable.h dlldefs.h server_b.h \
		con_mgr.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h cmdopts.h \
		msg_cmd.h
#ENDUPDATE#
