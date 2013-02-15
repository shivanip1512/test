# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(MACS)\include \
-I$(COMMON)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(MSG)\include \
-I$(TCL)\include \
-I$(RW) \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \



.PATH.H = \
.\include \
;$(COMMON)\include \
;$(MACS)\include \
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
;$(DATABASE)\include \
;$(RW)



LIBS=\
advapi32.lib \
$(TCL_LIBS) \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib

BASEOBJS= \
interp.obj \
interp_pool.obj \
mccmd.obj \
wpsc.obj  \
decodetextcmdfile.obj \
xcel.obj \
clientconn.obj \
clistener.obj \
mc_dbthr.obj \
mc_fileint.obj \
mc_main.obj \
mc_msg.obj \
mc_sched.obj \
mc_scheduler.obj \
mc_script.obj \
mc_server.obj \
mc_svc.obj \
mgr_mcsched.obj \
tbl_mcsched.obj \
tbl_mcsimpsched.obj

TARGS = macs.exe


MACS_EXE_FULLBUILD = $[Filename,$(OBJ),MacsExeFullBuild,target]


PROGS_VERSION=\
$(TARGS)


ALL:          $(TARGS)


$(MACS_EXE_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(BASEOBJS)]


macs.exe:     $(MACS_EXE_FULLBUILD) $(BASEOBJS) Makefile $(OBJ)\macs.res
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOST_LIBS) $(LINKFLAGS) macs.res
              @echo:
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
              -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
              -if exist ..\tcl\*.* copy ..\tcl\*.* $(YUKONOUTPUT)
              @%cd $(CWD)
              @echo Done building Target ..\$@
              @echo:

copy:       $(TARGS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)
           -if exist tcl\*.* copy tcl\*.* $(YUKONOUTPUT)


deps:
                scandeps -Output makeexe.mak *.cpp


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


allclean:   clean all


.SUFFIXES:      .cpp .obj

.cpp.obj:
               @echo:
               @echo Compiling: $<
               @echo C-Options: $(CFLAGS)
               @echo Output   : ..\$@
               @echo:
               $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
#UPDATE#
clientconn.obj:	precompiled.h clientconn.h mc.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		observe.h ctibase.h ctinexus.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dllbase.h dsm2.h dsm2err.h words.h \
		optional.h
clistener.obj:	precompiled.h clistener.h mc.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		clientconn.h observe.h msg_multi.h collectable.h msg_pdata.h \
		yukon.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		queue.h cparms.h configkey.h configval.h string_utility.h
decodetextcmdfile.obj:	precompiled.h ctidate.h dlldefs.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		decodeTextCmdFile.h rwutil.h yukon.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h
interp.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h interp.h critical_section.h \
		thread.h logger.h CtiPCPtrQueue.h
interp_pool.obj:	precompiled.h interp_pool.h interp.h \
		critical_section.h dlldefs.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h thread.h \
		mutex.h logger.h CtiPCPtrQueue.h
mccmd.obj:	precompiled.h mccmd.h msg_pcrequest.h dlldefs.h message.h \
		ctitime.h ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h logger.h thread.h CtiPCPtrQueue.h ctdpcptrq.h \
		tbl_meterreadlog.h ctistring.h database_transaction.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h cmdparse.h \
		ctitokenizer.h parsevalue.h msg_requestcancel.h \
		msg_queuedata.h msg_signal.h msg_dbchg.h msg_notif_email.h \
		msg_notif_email_attachment.h tbl_devicereadrequestlog.h \
		ctibase.h ctinexus.h mgr_holiday.h ctidate.h wpsc.h xcel.h \
		decodetextcmdfile.h
mc_dbthr.obj:	precompiled.h mc_dbthr.h mc.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		mgr_mcsched.h rtdb.h hashkey.h hash_functions.h \
		string_utility.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		mc_sched.h row_reader.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		boost_time.h boostutil.h
mc_fileint.obj:	precompiled.h mc_fileint.h fileint.h dlldefs.h queue.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h mgr_mcsched.h mc.h rtdb.h \
		hashkey.h hash_functions.h mc_sched.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h message.h collectable.h \
		mc_msg.h ctibase.h ctinexus.h ctidate.h
mc_main.obj:	precompiled.h ctitime.h dlldefs.h CServiceConfig.h \
		id_macs.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h mc_svc.h cservice.h mc_server.h mc.h logger.h \
		thread.h mutex.h guard.h CtiPCPtrQueue.h CParms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h message.h collectable.h \
		queue.h string_utility.h interp.h critical_section.h \
		interp_pool.h mgr_mcsched.h rtdb.h hashkey.h hash_functions.h \
		mc_sched.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h mc_dbthr.h mccmd.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h ctdpcptrq.h tbl_meterreadlog.h ctistring.h \
		clistener.h clientconn.h observe.h mc_msg.h mc_script.h \
		mc_scheduler.h mgr_holiday.h ctidate.h mc_fileint.h fileint.h \
		ctibase.h ctinexus.h
mc_msg.obj:	precompiled.h mc_msg.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		mc_sched.h mc.h logger.h thread.h CtiPCPtrQueue.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h
mc_sched.obj:	precompiled.h mc_sched.h row_reader.h ctitime.h \
		dlldefs.h mc.h logger.h thread.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h message.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h boost_time.h boostutil.h ctidate.h
mc_scheduler.obj:	precompiled.h mc_scheduler.h ctitime.h dlldefs.h \
		mc.h logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		mc_sched.h row_reader.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h message.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h boost_time.h boostutil.h mgr_mcsched.h \
		rtdb.h hashkey.h hash_functions.h string_utility.h \
		mgr_holiday.h ctidate.h
mc_script.obj:	precompiled.h mc_script.h mc.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		message.h ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h
mc_server.obj:	precompiled.h mc_server.h mc.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		CParms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h message.h \
		collectable.h queue.h string_utility.h interp.h \
		critical_section.h interp_pool.h mgr_mcsched.h rtdb.h \
		hashkey.h hash_functions.h mc_sched.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h mc_dbthr.h mccmd.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h ctdpcptrq.h tbl_meterreadlog.h \
		ctistring.h clistener.h clientconn.h observe.h mc_msg.h \
		mc_script.h mc_scheduler.h mgr_holiday.h ctidate.h \
		mc_fileint.h fileint.h thread_monitor.h smartmap.h \
		readers_writer_lock.h thread_register_data.h msg_cmd.h \
		msg_reg.h connection.h exchange.h msg_ptreg.h \
		tbl_devicereadjoblog.h
mc_svc.obj:	precompiled.h mc_svc.h cservice.h dlldefs.h mc_server.h \
		mc.h logger.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h CParms.h rwutil.h yukon.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h message.h collectable.h queue.h \
		string_utility.h interp.h critical_section.h interp_pool.h \
		mgr_mcsched.h rtdb.h hashkey.h hash_functions.h mc_sched.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h \
		mc_dbthr.h mccmd.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h ctdpcptrq.h \
		tbl_meterreadlog.h ctistring.h clistener.h clientconn.h \
		observe.h mc_msg.h mc_script.h mc_scheduler.h mgr_holiday.h \
		ctidate.h mc_fileint.h fileint.h thread_monitor.h smartmap.h \
		readers_writer_lock.h thread_register_data.h
mgr_mcsched.obj:	precompiled.h mgr_mcsched.h mc.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h rtdb.h \
		hashkey.h hash_functions.h string_utility.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h mc_sched.h row_reader.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		boost_time.h boostutil.h
tbl_mcsched.obj:	precompiled.h tbl_mcsched.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h logger.h thread.h CtiPCPtrQueue.h row_reader.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h \
		netports.h dsm2err.h words.h optional.h database_connection.h \
		database_writer.h row_writer.h
tbl_mcsimpsched.obj:	precompiled.h tbl_mcsimpsched.h mutex.h dlldefs.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h logger.h thread.h CtiPCPtrQueue.h \
		row_reader.h dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		database_connection.h database_writer.h row_writer.h
test_decodetextcmdfile.obj:	decodetextcmdfile.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		rwutil.h yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h
test_interp.obj:	interp.h critical_section.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h thread.h mutex.h
test_scheduletime.obj:	ctitime.h dlldefs.h mc_scheduler.h mc.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		mc_sched.h row_reader.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h message.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h boost_time.h boostutil.h mgr_mcsched.h \
		rtdb.h hashkey.h hash_functions.h string_utility.h \
		mgr_holiday.h ctidate.h
wpsc.obj:	precompiled.h wpsc.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h
xcel.obj:	precompiled.h xcel.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
