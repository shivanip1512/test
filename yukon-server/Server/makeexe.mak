include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(RW) \
-I$(BOOST) \
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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
con_mgr.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h collectable.h \
		con_mgr.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		rwutil.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ctibase.h ctinexus.h \
		msg_server_resp.h msg_cmd.h
ctique.obj:	yukon.h precompiled.h ctidbgmem.h
dlldbmemmgr.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h
executor.obj:	yukon.h precompiled.h ctidbgmem.h executor.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h con_mgr.h connection.h exchange.h \
		dllbase.h dsm2.h mutex.h guard.h clrdump.h cticonnect.h \
		netports.h logger.h thread.h CtiPCPtrQueue.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ctibase.h ctinexus.h
executorfactory.obj:	yukon.h precompiled.h ctidbgmem.h \
		executorfactory.h collectable.h message.h dlldefs.h rwutil.h \
		ctitime.h boost_time.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		executor.h exe_cmd.h exe_reg.h
exe_cmd.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h con_mgr.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ctibase.h ctinexus.h \
		server_b.h cmdopts.h argkey.h argval.h critical_Section.h \
		smartmap.h readers_writer_lock.h msg_cmd.h exe_cmd.h \
		executor.h
exe_reg.obj:	yukon.h precompiled.h ctidbgmem.h message.h collectable.h \
		dlldefs.h rwutil.h ctitime.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h exe_reg.h executor.h con_mgr.h connection.h \
		exchange.h dllbase.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h CtiPCPtrQueue.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h ctibase.h \
		ctinexus.h con_mgr_vg.h vgexe_factory.h exe_ptchg.h \
		executorfactory.h exe_cmd.h msg_cmd.h server_b.h cmdopts.h \
		argkey.h argval.h critical_Section.h smartmap.h \
		readers_writer_lock.h
id_svr.obj:	yukon.h precompiled.h ctidbgmem.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h id_svr.h id_vinfo.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
server_b.obj:	yukon.h precompiled.h ctidbgmem.h server_b.h con_mgr.h \
		connection.h dlldefs.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h ctibase.h ctinexus.h \
		cmdopts.h argkey.h argval.h critical_Section.h smartmap.h \
		readers_writer_lock.h executor.h msg_cmd.h
#ENDUPDATE#
