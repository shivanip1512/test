include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(RW) \
-I$(R_SERVER)\include \
-I$(R_COMMON)\include \
-I$(R_DATABASE)\include \
-I$(R_RTDB)\include \


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
;$(R_CPARMS)\include \
;$(R_DATABASE)\include \
;$(R_RTDB)\include \
;$(R_PORTER)\include \
;$(R_COMMON)\include \
;$(R_SCANNER)\include \
;$(R_SERVICE)\include \
;$(R_PIL)\include \
;$(R_SERVER)\include \
;$(R_PROT)\include \
;$(R_PROCLOG)\include \
;$(R_DISPATCH)\include \
;$(R_MSG)\include \
;$(TCLINC) \
;$(RW)




CTIPROGS=


ALL:            $(CTIPROGS)


copy:           $(CTIPROGS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
                scandeps -Output makeexe.mak *.cpp



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
con_mgr.obj:	dlldefs.h collectable.h con_mgr.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h message.h ctidbgmem.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h ctibase.h ctinexus.h
dlldbmemmgr.obj:	dlldefs.h utility.h dsm2.h mutex.h guard.h
executor.obj:	executor.h message.h ctidbgmem.h collectable.h dlldefs.h \
		con_mgr.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h
executorfactory.obj:	executorfactory.h collectable.h message.h \
		ctidbgmem.h dlldefs.h executor.h exe_cmd.h exe_reg.h
exe_cmd.obj:	dlldefs.h con_mgr.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h message.h ctidbgmem.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h ctibase.h ctinexus.h server_b.h cmdopts.h \
		msg_cmd.h exe_cmd.h executor.h
exe_reg.obj:	message.h ctidbgmem.h collectable.h dlldefs.h exe_reg.h \
		executor.h con_mgr.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		yukon.h msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h \
		con_mgr_vg.h vgexe_factory.h exe_ptchg.h exe_email.h \
		executorfactory.h exe_cmd.h msg_cmd.h server_b.h cmdopts.h
server_b.obj:	executor.h message.h ctidbgmem.h collectable.h dlldefs.h \
		server_b.h con_mgr.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		yukon.h msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h \
		cmdopts.h msg_cmd.h
#ENDUPDATE#
