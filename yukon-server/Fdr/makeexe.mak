include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(SERVICE)\include \
-I$(RW) \
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
;$(SIGNAL)\include \
;$(RW)



FDROBJS= \
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
        $(RWCPPINVOKE) /Fm $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
        $(FDROBJS) -link $(RWLIBS) $(BOOST_LIBS) $(FDRLIBS) fdr.res
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           $(MANIFEST_TOOL) -manifest ..\$@.manifest -outputresource:..\$@;1
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)



copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


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
    $(RWCPPINVOKE) /Fm $(RWCPPFLAGS) $(CFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################



#UPDATE#
a.obj:	precompiled.h
fdr.obj:	precompiled.h ctitime.h dlldefs.h ctibase.h \
		streamSocketConnection.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		socket_helper.h win_helper.h dllbase.h dsm2.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h logManager.h \
		module_util.h cparms.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h CServiceConfig.h fdrservice.h \
		cservice.h id_fdr.h connection_base.h
fdracs.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h socket_helper.h win_helper.h \
		fdrsinglesocket.h fdrsocketlayer.h fdrsocketconnection.h \
		fdracs.h
fdracsmulti.obj:	precompiled.h ctidate.h dlldefs.h ctitime.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h fdrpointlist.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h fdrpoint.h \
		fdrdestination.h fdr.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h queue.h \
		connection_base.h worker_thread.h fdrdebuglevel.h \
		socket_helper.h win_helper.h fdrscadahelper.h fdracsmulti.h \
		fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h std_helper.h
fdrasciiimportbase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h fdrasciiimportbase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h
fdrbepc.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_cmd.h message.h \
		collectable.h pointtypes.h fdrtextfilebase.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrbepc.h ctitokenizer.h
fdrclientconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h guard.h fdrinterface.h message.h \
		collectable.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h fdrsocketlayer.h \
		fdrsocketconnection.h socket_helper.h win_helper.h \
		fdrclientconnection.h
fdrclientserverconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h guard.h fdrinterface.h message.h \
		collectable.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h socket_helper.h win_helper.h prot_dnp.h \
		prot_base.h xfer.h packet_finder.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h
fdrcygnet.obj:	precompiled.h ctitime.h dlldefs.h cparms.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h fdrcygnet.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrdestination.obj:	precompiled.h fdrpoint.h dlldefs.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h
fdrdnpslave.obj:	precompiled.h ctidate.h dlldefs.h ctitime.h \
		ctistring.h cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h fdrpointlist.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h fdrpoint.h fdrdestination.h \
		fdr.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h connection_base.h \
		worker_thread.h fdrdebuglevel.h socket_helper.h win_helper.h \
		fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h fdrdnpslave.h \
		dnp_object_analoginput.h dnp_objects.h prot_base.h xfer.h \
		dnp_object_time.h prot_dnp.h packet_finder.h \
		dnp_application.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h dnp_object_binaryinput.h \
		dnp_object_counter.h std_helper.h
fdrdsm2filein.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_cmd.h message.h collectable.h \
		pointtypes.h fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrdsm2filein.h ctitokenizer.h
fdrdsm2import.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h fdrdsm2import.h fdrasciiimportbase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrftpinterface.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h fdrftpinterface.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h
fdrinet.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h socket_helper.h win_helper.h \
		fdrserverconnection.h fdrsocketlayer.h fdrsocketconnection.h \
		fdrclientconnection.h fdrinet.h
fdrinterface.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h boost_time.h configkey.h \
		configval.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_ptreg.h msg_cmd.h msg_dbchg.h msg_signal.h fdrinterface.h \
		connection_client.h connection.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h amq_constants.h
fdrio.obj:	precompiled.h fdrio.h
fdriosocket.obj:	precompiled.h fdriosocket.h ctitime.h dlldefs.h \
		fdrio.h
fdrlivedata.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h hashkey.h \
		hash_functions.h resolvers.h db_entry_defines.h fdr.h \
		fdrdebuglevel.h livedatatypes.h fdrlivedata.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h \
		fdrpointlist.h fdrsimplebase.h fdrasciiimportbase.h \
		livedata_rtp_api.h RTP_APIW.H RTP.H
fdrlodestarimport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_cmd.h message.h collectable.h \
		msg_signal.h pointtypes.h fdrtextfilebase.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrlodestarimport.h fdrlodestarinfo.h rtdb.h hashkey.h \
		hash_functions.h
fdrlodestarimport_enh.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_cmd.h message.h collectable.h \
		pointtypes.h fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrlodestarimport.h fdrlodestarinfo.h rtdb.h hashkey.h \
		hash_functions.h fdrlodestarimport_enh.h
fdrlodestarimport_std.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_cmd.h message.h collectable.h \
		pointtypes.h fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrlodestarimport.h fdrlodestarinfo.h rtdb.h hashkey.h \
		hash_functions.h fdrlodestarimport_std.h
fdrlodestarinfo.obj:	precompiled.h fdrlodestarinfo.h dlldefs.h fdr.h \
		pointdefs.h
fdrpibase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h hashkey.h \
		hash_functions.h resolvers.h db_entry_defines.h ctistring.h \
		fdr.h fdrdebuglevel.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h \
		fdrpointlist.h fdrpipoll.h fdrpibase.h fdrsimplebase.h \
		fdrasciiimportbase.h fdrpinotify.h
fdrpinotify.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		fdrpinotify.h fdrpibase.h fdrinterface.h message.h \
		ctidbgmem.h collectable.h loggable.h msg_dbchg.h yukon.h \
		types.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrsimplebase.h fdrasciiimportbase.h
fdrpipoll.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h fdrpipoll.h fdrpibase.h fdrinterface.h \
		message.h collectable.h loggable.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h fdrsimplebase.h \
		fdrasciiimportbase.h
fdrpoint.obj:	precompiled.h ctitime.h dlldefs.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h
fdrpointidmap.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h fdrpointidmap.h pointtypes.h fdr.h pointdefs.h \
		resolvers.h db_entry_defines.h
fdrpointlist.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h fdrpointlist.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h
fdrprotectedmaplist.obj:	precompiled.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h fdrpoint.h pointtypes.h fdrdestination.h fdr.h \
		pointdefs.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h \
		fdrprotectedmaplist.h
fdrrccs.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h socket_helper.h win_helper.h \
		fdrserverconnection.h fdrsocketlayer.h fdrsocketconnection.h \
		fdrclientconnection.h fdrinet.h fdrrccs.h
fdrrdex.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h socket_helper.h win_helper.h \
		fdrsinglesocket.h fdrsocketlayer.h fdrsocketconnection.h \
		fdrserverconnection.h fdrrdex.h
fdrscadahelper.obj:	precompiled.h msg_cmd.h message.h ctitime.h \
		dlldefs.h ctidbgmem.h collectable.h loggable.h \
		fdrdebuglevel.h fdrscadahelper.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h rwutil.h yukon.h types.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h fdracsmulti.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h fdrpoint.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h connection_base.h worker_thread.h socket_helper.h \
		win_helper.h fdrdnpslave.h dnp_object_analoginput.h \
		dnp_objects.h prot_base.h xfer.h dnp_object_time.h prot_dnp.h \
		packet_finder.h dnp_application.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h fdrvalmetmulti.h
fdrscadaserver.obj:	precompiled.h fdrscadaserver.h dlldefs.h \
		fdrsocketserver.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h \
		fdrclientserverconnection.h serverconnection.h loggable.h \
		string_util.h streamBuffer.h fdrinterface.h message.h \
		ctitime.h collectable.h msg_dbchg.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h numstr.h \
		logger.h exception_helper.h boostutil.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h socket_helper.h win_helper.h
fdrserverconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h guard.h fdrinterface.h message.h \
		collectable.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h fdrsocketlayer.h \
		fdrsocketconnection.h socket_helper.h win_helper.h \
		fdrserverconnection.h
fdrservice.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h fdrservice.h cservice.h \
		thread_monitor.h smartmap.h readers_writer_lock.h queue.h \
		thread.h thread_register_data.h connection_client.h \
		connection.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		connection_base.h worker_thread.h msg_cmd.h amq_constants.h \
		module_util.h
fdrsimplebase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h hashkey.h hash_functions.h resolvers.h \
		db_entry_defines.h fdr.h fdrdebuglevel.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h \
		fdrpointlist.h fdrsimplebase.h fdrasciiimportbase.h
fdrsinglesocket.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h socket_helper.h win_helper.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h \
		fdrsinglesocket.h
fdrsocketconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h guard.h fdrsocketlayer.h \
		fdrsocketconnection.h socket_helper.h timing_util.h \
		win_helper.h
fdrsocketinterface.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h fdrsocketconnection.h \
		socket_helper.h win_helper.h fdrsocketinterface.h \
		fdrinterface.h message.h collectable.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h connection_base.h worker_thread.h fdrdebuglevel.h \
		msg_cmd.h
fdrsocketlayer.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h guard.h socket_helper.h \
		timing_util.h win_helper.h fdrinterface.h message.h \
		collectable.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h immutable.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h fdrsocketinterface.h \
		fdrclientconnection.h fdrsocketlayer.h fdrsocketconnection.h \
		fdrserverconnection.h
fdrsocketserver.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h socket_helper.h win_helper.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h ctidate.h
fdrstec.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h fdrstec.h \
		fdrftpinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrtelegyr.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h hashkey.h \
		hash_functions.h resolvers.h db_entry_defines.h fdr.h \
		fdrdebuglevel.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h \
		fdrpointlist.h fdrtelegyr.h fdrasciiimportbase.h \
		telegyrgroup.h telegyrcontrolcenter.h rtdb.h
fdrtextexport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_cmd.h message.h collectable.h \
		pointtypes.h fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrtextexport.h
fdrtextfilebase.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection_client.h connection.h \
		msg_ptreg.h msg_reg.h queue.h readers_writer_lock.h \
		connection_base.h worker_thread.h mgr_fdrpoint.h smartmap.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h msg_cmd.h TextFileInterfaceParts.h
fdrtextimport.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		ctistring.h cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h msg_cmd.h message.h collectable.h \
		pointtypes.h fdrtextfilebase.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrtextimport.h
fdrtristate.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h fdrtristate.h \
		fdrftpinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrtristatesub.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h fdrTriStateSub.h \
		fdrftpinterface.h fdrinterface.h message.h collectable.h \
		msg_dbchg.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h ctidate.h
fdrvalmet.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h socket_helper.h win_helper.h \
		fdrsinglesocket.h fdrsocketlayer.h fdrsocketconnection.h \
		fdrvalmet.h fdrvalmetutil.h
fdrvalmetmulti.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		utility.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h socket_helper.h win_helper.h \
		fdrscadahelper.h fdrvalmetmulti.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h \
		serverconnection.h fdrvalmetutil.h
fdrvalmetutil.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h \
		fdrvalmetutil.h pointdefs.h fdrpointlist.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		mgr_fdrpoint.h smartmap.h boostutil.h utility.h numstr.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		readers_writer_lock.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		cparms.h configkey.h configval.h
fdrwabash.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h fdrwabash.h \
		fdrinterface.h message.h collectable.h msg_dbchg.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h
fdrxa21lm.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		connection_client.h connection.h queue.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h socket_helper.h win_helper.h \
		fdrsinglesocket.h fdrsocketlayer.h fdrsocketconnection.h \
		fdrserverconnection.h fdrxa21lm.h
livedatatypes.obj:	precompiled.h livedatatypes.h pointdefs.h cparms.h \
		dlldefs.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h
livedata_rtp_api.obj:	precompiled.h livedata_rtp_api.h logger.h \
		dlldefs.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h atomic.h guard.h RTP_APIW.H RTP.H
mgr_fdrpoint.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h hashkey.h hash_functions.h resolvers.h \
		pointtypes.h db_entry_defines.h fdr.h pointdefs.h \
		fdrdebuglevel.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h fdrpoint.h fdrdestination.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h FdrException.h
telegyrcontrolcenter.obj:	precompiled.h telegyrcontrolcenter.h \
		dlldefs.h fdr.h pointdefs.h telegyrgroup.h ctitime.h \
		fdrpoint.h pointtypes.h fdrdestination.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h
telegyrgroup.obj:	precompiled.h telegyrgroup.h ctitime.h dlldefs.h \
		fdr.h pointdefs.h fdrpoint.h pointtypes.h fdrdestination.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h
test_fdrdnpslave.obj:	fdrdnpslave.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h boostutil.h \
		utility.h ctitime.h numstr.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h readers_writer_lock.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h fdrinterface.h \
		message.h collectable.h msg_dbchg.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h connection_base.h \
		worker_thread.h fdrdebuglevel.h msg_cmd.h socket_helper.h \
		win_helper.h dnp_object_analoginput.h dnp_objects.h \
		prot_base.h xfer.h dnp_object_time.h prot_dnp.h \
		packet_finder.h dnp_application.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h boost_test_helpers.h \
		millisecond_timer.h
test_fdrtelegyr.obj:	fdrtelegyr.h dlldefs.h fdrinterface.h message.h \
		ctitime.h ctidbgmem.h collectable.h loggable.h msg_dbchg.h \
		yukon.h types.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h mgr_fdrpoint.h smartmap.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrasciiimportbase.h telegyrgroup.h \
		telegyrcontrolcenter.h rtdb.h hashkey.h hash_functions.h
test_fdrtextimport.obj:	fdrtextimport.h dlldefs.h fdrtextfilebase.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h msg_dbchg.h yukon.h types.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		TextFileInterfaceParts.h ctistring.h
test_fdrtristatesub.obj:	fdrTriStateSub.h dlldefs.h fdrftpinterface.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h msg_dbchg.h yukon.h types.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h \
		readers_writer_lock.h connection_base.h worker_thread.h \
		mgr_fdrpoint.h smartmap.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h msg_cmd.h ctidate.h
textfileinterfaceparts.obj:	precompiled.h cparms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		TextFileInterfaceParts.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
