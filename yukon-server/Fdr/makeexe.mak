include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(SERVICE)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include


FDROBJS= \
$(PRECOMPILED_OBJ) \
fdrservice.obj \
fdr.obj


WINLIBS=kernel32.lib user32.lib
SOCKSLIB=wsock32.lib


CTIPROGS=fdr.exe

FDRLIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib


PROGS_VERSION=\
$(CTIPROGS)


ALL:            $(CTIPROGS)

fdr.exe:    $(FDROBJS) makeexe.mak $(OBJ)\fdr.res
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(CC) /Fm $(CFLAGS) $(INCLPATHS) /Fe..\$@ \
        $(FDROBJS) -link /LARGEADDRESSAWARE $(BOOST_LIBS) $(FDRLIBS) fdr.res
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
	   -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	   -copy ..\bin\$(@B).pdb $(YUKONDEBUG)
        @%cd $(CWD)



copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)
	   -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
           -@if exist bin\*.pdb copy bin\*.pdb $(YUKONDEBUG)


deps:
        scandeps -Output makeexe.mak *.cpp



clean:
    -del *.obj
    -del *.pch
    -del *.pdb
    -del *.sdb
    -del *.adb
    -del *.ilk
    -del *.exe


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
    @echo:
    @echo Compiling cpp to obj
    $(CC) /Fm $(CCOPTS) $(CFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################



#UPDATE#
fdr.obj:	precompiled.h ctitime.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logManager.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h guard.h cparms.h \
		CServiceConfig.h fdrservice.h cservice.h id_fdr.h \
		connection_base.h
fdracs.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h types.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h socket_helper.h \
		win_helper.h fdrsinglesocket.h fdrsocketlayer.h \
		fdrsocketconnection.h fdracs.h
fdracsmulti.obj:	precompiled.h ctidate.h dlldefs.h ctitime.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h types.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrpointlist.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h mutex.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		queue.h connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h fdrdebuglevel.h socket_helper.h win_helper.h \
		fdrscadahelper.h fdracsmulti.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h std_helper.h
fdrasciiimportbase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		loggable.h connectionHandle.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrasciiimportbase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h
fdrbepc.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		msg_cmd.h message.h ctidbgmem.h collectable.h loggable.h \
		connectionHandle.h pointtypes.h numstr.h dllbase.h os2_2w32.h \
		types.h cticalls.h yukon.h critical_section.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h module_util.h version.h \
		guard.h fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h concurrentSet.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h fdrbepc.h \
		ctitokenizer.h
fdrclientconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h guard.h \
		fdrinterface.h message.h collectable.h connectionHandle.h \
		msg_dbchg.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h readers_writer_lock.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h mgr_fdrpoint.h smartmap.h dllbase.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h fdrsocketlayer.h \
		fdrsocketconnection.h socket_helper.h win_helper.h \
		fdrclientconnection.h
fdrclientserverconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h guard.h \
		fdrinterface.h message.h collectable.h connectionHandle.h \
		msg_dbchg.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h readers_writer_lock.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h mgr_fdrpoint.h smartmap.h dllbase.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h \
		socket_helper.h win_helper.h prot_dnp.h prot_base.h xfer.h \
		packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h
fdrcygnet.obj:	precompiled.h ctitime.h dlldefs.h cparms.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h types.h ctidbgmem.h \
		pointdefs.h pointtypes.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrcygnet.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h
fdrdestination.obj:	precompiled.h fdrpoint.h dlldefs.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h utility.h ctitime.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h loggable.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h boostutil.h \
		dllbase.h critical_section.h readers_writer_lock.h guard.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h mutex.h
fdrdnpslave.obj:	precompiled.h fdrdnpslave.h dlldefs.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h boostutil.h \
		utility.h ctitime.h numstr.h module_util.h version.h \
		dllbase.h critical_section.h readers_writer_lock.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h pointdefs.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h mutex.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h worker_thread.h timing_util.h \
		concurrentSet.h fdrinterface.h message.h collectable.h \
		connectionHandle.h msg_dbchg.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h connection_base.h fdrdebuglevel.h msg_cmd.h \
		socket_helper.h win_helper.h dnp_object_analoginput.h \
		dnp_objects.h prot_base.h xfer.h dnp_object_time.h \
		prot_dnpSlave.h dnp_application.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_analogoutput.h dnp_object_binaryoutput.h \
		random_generator.h AttributeService.h LitePoint.h \
		PointAttribute.h prot_dnp.h packet_finder.h amq_constants.h \
		resolvers.h db_entry_defines.h desolvers.h msg_pcrequest.h \
		dsm2.h streamConnection.h netports.h immutable.h dsm2err.h \
		words.h optional.h macro_offset.h msg_pcreturn.h \
		millisecond_timer.h std_helper.h
fdrdsm2filein.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_cmd.h message.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h pointtypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h guard.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrdsm2filein.h ctitokenizer.h
fdrdsm2import.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		loggable.h connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrdsm2import.h \
		fdrasciiimportbase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h concurrentSet.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h
fdrftpinterface.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		loggable.h connectionHandle.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrftpinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h
fdrinet.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h connectionHandle.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h dllbase.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h guard.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h socket_helper.h \
		win_helper.h fdrserverconnection.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrclientconnection.h dsm2.h \
		streamConnection.h netports.h immutable.h dsm2err.h words.h \
		optional.h macro_offset.h fdrutility.h fdrinet.h
fdrinterface.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		cparms.h dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h resolvers.h \
		pointtypes.h db_entry_defines.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_dbchg.h \
		msg_signal.h fdrinterface.h connection_client.h connection.h \
		msg_reg.h mutex.h queue.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h database_reader.h fdrdebuglevel.h \
		fdrpointlist.h amq_constants.h
fdrlivedata.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h types.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h dbaccess.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		livedatatypes.h fdrlivedata.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h concurrentSet.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h database_connection.h \
		database_reader.h row_reader.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h livedata_rtp_api.h RTP_APIW.H RTP.H
fdrlodestarimport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_cmd.h message.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h msg_signal.h yukon.h types.h \
		pointtypes.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h rtdb.h
fdrlodestarimport_enh.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_cmd.h message.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h pointtypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h guard.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h rtdb.h fdrlodestarimport_enh.h
fdrlodestarimport_std.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_cmd.h message.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h pointtypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h guard.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h rtdb.h fdrlodestarimport_std.h
fdrlodestarinfo.obj:	precompiled.h fdrlodestarinfo.h dlldefs.h fdr.h \
		pointdefs.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h
fdrpibase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h types.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h dbaccess.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h database_connection.h \
		database_reader.h row_reader.h fdrpointlist.h fdrpipoll.h \
		fdrpibase.h fdrsimplebase.h fdrasciiimportbase.h \
		fdrpinotify.h
fdrpinotify.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		fdrpinotify.h fdrpibase.h fdrinterface.h message.h \
		ctidbgmem.h collectable.h loggable.h connectionHandle.h \
		msg_dbchg.h yukon.h types.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h dllbase.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		fdrsimplebase.h fdrasciiimportbase.h
fdrpipoll.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h fdrpipoll.h \
		fdrpibase.h fdrinterface.h message.h collectable.h loggable.h \
		connectionHandle.h msg_dbchg.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h mgr_fdrpoint.h smartmap.h dllbase.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h fdrsimplebase.h fdrasciiimportbase.h
fdrpoint.obj:	precompiled.h ctitime.h dlldefs.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h loggable.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h critical_section.h
fdrpointidmap.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h fdrpointidmap.h ctitime.h pointtypes.h \
		fdr.h pointdefs.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h resolvers.h db_entry_defines.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h guard.h
fdrpointlist.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h constants.h numstr.h \
		module_util.h version.h guard.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h fdrpointlist.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h mutex.h
fdrrccs.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h connectionHandle.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h dllbase.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h guard.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h socket_helper.h \
		win_helper.h fdrserverconnection.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrclientconnection.h fdrinet.h dsm2.h \
		streamConnection.h netports.h immutable.h dsm2err.h words.h \
		optional.h macro_offset.h fdrrccs.h
fdrrdex.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h types.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h socket_helper.h \
		win_helper.h fdrsinglesocket.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrrdex.h
fdrscadahelper.obj:	precompiled.h fdrscadahelper.h dlldefs.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		loggable.h msg_cmd.h message.h collectable.h \
		connectionHandle.h fdrdebuglevel.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h \
		critical_section.h fdracsmulti.h fdrpointlist.h \
		mgr_fdrpoint.h smartmap.h dllbase.h readers_writer_lock.h \
		guard.h fdrpoint.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h mutex.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h worker_thread.h timing_util.h \
		concurrentSet.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h connection_base.h \
		socket_helper.h win_helper.h fdrvalmetmulti.h
fdrscadaserver.obj:	precompiled.h fdrscadaserver.h dlldefs.h \
		fdrsocketserver.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h \
		fdrclientserverconnection.h serverconnection.h loggable.h \
		string_util.h streamBuffer.h worker_thread.h timing_util.h \
		concurrentSet.h utility.h ctitime.h numstr.h module_util.h \
		version.h fdrinterface.h message.h collectable.h \
		connectionHandle.h msg_dbchg.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h logger.h \
		exception_helper.h boostutil.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		mgr_fdrpoint.h smartmap.h dllbase.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h socket_helper.h win_helper.h
fdrserverconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h guard.h \
		fdrinterface.h message.h collectable.h connectionHandle.h \
		msg_dbchg.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h readers_writer_lock.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h mgr_fdrpoint.h smartmap.h dllbase.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h fdrsocketlayer.h \
		fdrsocketconnection.h socket_helper.h win_helper.h \
		fdrserverconnection.h
fdrservice.obj:	precompiled.h cparms.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h guard.h \
		fdrservice.h cservice.h thread_monitor.h smartmap.h dllbase.h \
		readers_writer_lock.h queue.h thread.h mutex.h \
		thread_register_data.h boost_time.h connection_client.h \
		connection.h message.h collectable.h connectionHandle.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h msg_cmd.h amq_constants.h win_helper.h
fdrsimplebase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		loggable.h connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h dbaccess.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h database_connection.h \
		database_reader.h row_reader.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h
fdrsinglesocket.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		loggable.h connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h socket_helper.h \
		win_helper.h fdrsocketlayer.h fdrsocketconnection.h \
		fdrserverconnection.h fdrsinglesocket.h
fdrsocketconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h guard.h \
		fdrsocketlayer.h fdrsocketconnection.h socket_helper.h \
		timing_util.h win_helper.h worker_thread.h concurrentSet.h
fdrsocketinterface.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		dllbase.h os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h guard.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h mutex.h \
		fdrsocketconnection.h socket_helper.h timing_util.h \
		win_helper.h fdrsocketinterface.h fdrinterface.h message.h \
		collectable.h connectionHandle.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h connection_base.h \
		worker_thread.h concurrentSet.h fdrdebuglevel.h msg_cmd.h
fdrsocketlayer.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h guard.h socket_helper.h \
		timing_util.h win_helper.h fdrinterface.h message.h \
		collectable.h connectionHandle.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h readers_writer_lock.h connection_base.h \
		worker_thread.h concurrentSet.h mgr_fdrpoint.h smartmap.h \
		dllbase.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		fdrsocketinterface.h fdrclientconnection.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h
fdrsocketserver.obj:	precompiled.h cparms.h dlldefs.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h types.h ctidbgmem.h \
		pointdefs.h pointtypes.h message.h ctitime.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h socket_helper.h \
		win_helper.h fdrsocketlayer.h fdrsocketconnection.h \
		fdrserverconnection.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h ctidate.h
fdrstec.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h types.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrstec.h \
		fdrftpinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h concurrentSet.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h
fdrtelegyr.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h types.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h dbaccess.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h database_connection.h \
		database_reader.h row_reader.h fdrpointlist.h fdrtelegyr.h \
		fdrasciiimportbase.h telegyrgroup.h telegyrcontrolcenter.h \
		rtdb.h
fdrtextexport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_cmd.h message.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h pointtypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h guard.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrtextexport.h
fdrtextfilebase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_multi.h collectable.h msg_pdata.h yukon.h \
		types.h ctidbgmem.h pointdefs.h pointtypes.h message.h \
		loggable.h connectionHandle.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h TextFileInterfaceParts.h
fdrtextimport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h msg_cmd.h message.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h pointtypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h critical_section.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h \
		module_util.h version.h guard.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrtextimport.h
fdrtristate.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h types.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrtristate.h \
		fdrftpinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h mutex.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		timing_util.h concurrentSet.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h
fdrtristatesub.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		fdrTriStateSub.h fdrftpinterface.h fdrinterface.h message.h \
		ctitime.h collectable.h loggable.h connectionHandle.h \
		msg_dbchg.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		ctidate.h
fdrutility.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h fdrutility.h std_helper.h
fdrvalmet.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h connectionHandle.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h dllbase.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h guard.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h socket_helper.h \
		win_helper.h fdrsinglesocket.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrvalmet.h fdrvalmetutil.h
fdrvalmetmulti.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		utility.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		cparms.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h connectionHandle.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h dllbase.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h guard.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h socket_helper.h \
		win_helper.h fdrscadahelper.h fdrvalmetmulti.h \
		fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h \
		fdrvalmetutil.h
fdrvalmetutil.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		fdrvalmetutil.h pointdefs.h fdrpointlist.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		mgr_fdrpoint.h smartmap.h boostutil.h utility.h numstr.h \
		module_util.h version.h dllbase.h critical_section.h \
		readers_writer_lock.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h mutex.h cparms.h
fdrwabash.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h fdrwabash.h \
		fdrinterface.h message.h ctitime.h collectable.h loggable.h \
		connectionHandle.h msg_dbchg.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h constants.h numstr.h module_util.h \
		version.h readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h
fdrxa21lm.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h types.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h loggable.h \
		connectionHandle.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h fdrsocketinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		mutex.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h timing_util.h concurrentSet.h mgr_fdrpoint.h \
		smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h socket_helper.h \
		win_helper.h fdrsinglesocket.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrxa21lm.h
livedatatypes.obj:	precompiled.h livedatatypes.h pointdefs.h cparms.h \
		dlldefs.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h critical_section.h guard.h
livedata_rtp_api.obj:	precompiled.h livedata_rtp_api.h logger.h \
		dlldefs.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h critical_section.h guard.h \
		RTP_APIW.H RTP.H
mgr_fdrpoint.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h resolvers.h pointtypes.h \
		db_entry_defines.h fdr.h pointdefs.h utility.h ctitime.h \
		queues.h constants.h numstr.h module_util.h version.h \
		fdrdebuglevel.h mgr_fdrpoint.h smartmap.h boostutil.h \
		readers_writer_lock.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h fdrpoint.h \
		fdrdestination.h database_connection.h database_reader.h \
		row_reader.h FdrException.h
telegyrcontrolcenter.obj:	precompiled.h telegyrcontrolcenter.h \
		dlldefs.h fdr.h pointdefs.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h telegyrgroup.h fdrpoint.h \
		pointtypes.h fdrdestination.h loggable.h mutex.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		critical_section.h
telegyrgroup.obj:	precompiled.h telegyrgroup.h ctitime.h dlldefs.h \
		fdr.h pointdefs.h utility.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h fdrpoint.h pointtypes.h \
		fdrdestination.h loggable.h
test_fdrdnpslave.obj:	fdrdnpslave.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h boostutil.h \
		utility.h ctitime.h numstr.h module_util.h version.h \
		dllbase.h critical_section.h readers_writer_lock.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h pointdefs.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h mutex.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h worker_thread.h timing_util.h \
		concurrentSet.h fdrinterface.h message.h collectable.h \
		connectionHandle.h msg_dbchg.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h connection_base.h fdrdebuglevel.h msg_cmd.h \
		socket_helper.h win_helper.h dnp_object_analoginput.h \
		dnp_objects.h prot_base.h xfer.h dnp_object_time.h \
		prot_dnpSlave.h dnp_application.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_analogoutput.h dnp_object_binaryoutput.h \
		random_generator.h AttributeService.h LitePoint.h \
		PointAttribute.h desolvers.h msg_pcrequest.h dsm2.h \
		streamConnection.h netports.h immutable.h dsm2err.h words.h \
		optional.h macro_offset.h msg_pcreturn.h boost_test_helpers.h \
		millisecond_timer.h ctidate.h
test_fdrtelegyr.obj:	fdrtelegyr.h dlldefs.h fdrinterface.h message.h \
		ctitime.h ctidbgmem.h collectable.h loggable.h \
		connectionHandle.h msg_dbchg.h yukon.h types.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h mgr_fdrpoint.h smartmap.h dllbase.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h fdrasciiimportbase.h telegyrgroup.h \
		telegyrcontrolcenter.h rtdb.h
test_fdrtextimport.obj:	fdrtextimport.h dlldefs.h fdrtextfilebase.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h msg_dbchg.h yukon.h types.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h mgr_fdrpoint.h smartmap.h dllbase.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h TextFileInterfaceParts.h
test_fdrtristatesub.obj:	fdrTriStateSub.h dlldefs.h fdrftpinterface.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h connectionHandle.h msg_dbchg.h yukon.h types.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		concurrentSet.h mgr_fdrpoint.h smartmap.h dllbase.h \
		fdrpoint.h fdrdestination.h fdr.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h ctidate.h
test_fdrutility.obj:	fdrutility.h dlldefs.h utility.h ctitime.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h \
		boost_test_helpers.h millisecond_timer.h ctidate.h
textfileinterfaceparts.obj:	precompiled.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h yukon.h types.h \
		ctidbgmem.h pointdefs.h pointtypes.h message.h ctitime.h \
		loggable.h connectionHandle.h dllbase.h os2_2w32.h cticalls.h \
		critical_section.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h \
		TextFileInterfaceParts.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
