include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(RW) \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
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
con_mgr.obj:	precompiled.h dlldefs.h collectable.h con_mgr.h \
		connection_server.h connection.h exchange.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h macro_offset.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h connection_listener.h \
		ctibase.h ctinexus.h socket_helper.h msg_server_resp.h \
		msg_cmd.h
ctique.obj:	precompiled.h
dlldbmemmgr.obj:	precompiled.h dlldefs.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h
executor.obj:	precompiled.h executor.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h con_mgr.h connection_server.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		readers_writer_lock.h critical_section.h connection_base.h \
		connection_listener.h ctibase.h ctinexus.h socket_helper.h
executorfactory.obj:	precompiled.h executorfactory.h collectable.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h executor.h \
		exe_cmd.h exe_reg.h
exe_cmd.obj:	precompiled.h dlldefs.h con_mgr.h connection_server.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h connection_listener.h \
		ctibase.h ctinexus.h socket_helper.h server_b.h smartmap.h \
		msg_cmd.h exe_cmd.h executor.h
exe_reg.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h exe_reg.h executor.h con_mgr.h \
		connection_server.h connection.h exchange.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h netports.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h readers_writer_lock.h \
		critical_section.h connection_base.h connection_listener.h \
		ctibase.h ctinexus.h socket_helper.h con_mgr_vg.h \
		vgexe_factory.h exe_ptchg.h executorfactory.h exe_cmd.h \
		msg_cmd.h server_b.h smartmap.h
id_svr.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h id_svr.h
server_b.obj:	precompiled.h server_b.h con_mgr.h connection_server.h \
		connection.h dlldefs.h exchange.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h macro_offset.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		readers_writer_lock.h critical_section.h connection_base.h \
		connection_listener.h ctibase.h ctinexus.h socket_helper.h \
		smartmap.h executor.h msg_cmd.h id_svr.h
#ENDUPDATE#
