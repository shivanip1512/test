include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(SERVICE)\include \
-I$(RW) \
-I$(BOOST) \


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
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib


ALL:            $(CTIPROGS)

fdr.exe:    $(FDROBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) /Fm $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
        $(FDROBJS) -link $(RWLIBS) $(BOOST_LIBS) $(FDRLIBS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
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
a.obj:	yukon.h precompiled.h ctidbgmem.h
fdr.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctibase.h ctinexus.h netports.h cticonnect.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		dsm2err.h words.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h CServiceConfig.h \
		fdrservice.h cservice.h id_fdr.h
fdracs.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdracs.h
fdracsmulti.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h fdrpointlist.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h fdrdebuglevel.h fdrscadahelper.h fdracsmulti.h \
		fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h
fdrasciiimportbase.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		fdrasciiimportbase.h fdrinterface.h msg_dbchg.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h
fdrbepc.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_cmd.h message.h \
		collectable.h pointtypes.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h fdrtextfilebase.h fdrinterface.h \
		msg_dbchg.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h fdrbepc.h \
		ctitokenizer.h
fdrclientconnection.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h fdrinterface.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_dbchg.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrclientconnection.h
fdrclientserverconnection.obj:	yukon.h precompiled.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h fdrinterface.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_dbchg.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h prot_dnp.h prot_base.h xfer.h \
		packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h
fdrcygnet.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		cparms.h rwutil.h boost_time.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h msg_ptreg.h msg_cmd.h \
		msg_reg.h msg_signal.h connection.h exchange.h dllbase.h \
		dsm2.h mutex.h guard.h cticonnect.h netports.h dsm2err.h \
		words.h logger.h thread.h CtiPCPtrQueue.h queue.h fdrcygnet.h \
		fdrinterface.h msg_dbchg.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrdestination.obj:	yukon.h precompiled.h ctidbgmem.h fdrpoint.h \
		dlldefs.h pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		types.h ctitime.h rwutil.h boost_time.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h sorted_vector.h \
		logger.h thread.h mutex.h guard.h CtiPCPtrQueue.h
fdrdnphelper.obj:	yukon.h precompiled.h ctidbgmem.h msg_cmd.h \
		dlldefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h fdrdebuglevel.h \
		fdrdnphelper.h pointtypes.h fdrdestination.h fdr.h \
		pointdefs.h fdrdnpslave.h fdrpointlist.h mgr_fdrpoint.h \
		smartmap.h dllbase.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h readers_writer_lock.h \
		critical_section.h fdrpoint.h logger.h thread.h \
		CtiPCPtrQueue.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h fdrinterface.h msg_dbchg.h \
		connection.h exchange.h msg_multi.h msg_pdata.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		dnp_object_analoginput.h dnp_objects.h prot_base.h xfer.h \
		dnp_object_time.h prot_dnp.h packet_finder.h \
		dnp_application.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h
fdrdnpslave.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h ctistring.h rwutil.h boost_time.h \
		boostutil.h cparms.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h queue.h fdrpointlist.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h fdrdebuglevel.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h fdrdnphelper.h \
		fdrdnpslave.h dnp_object_analoginput.h dnp_objects.h \
		prot_base.h xfer.h dnp_object_time.h prot_dnp.h \
		packet_finder.h dnp_application.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h dnp_object_binaryinput.h \
		dnp_object_counter.h
fdrdsm2filein.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h pointtypes.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrdsm2filein.h ctitokenizer.h
fdrdsm2import.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h queue.h fdrdsm2import.h fdrasciiimportbase.h \
		fdrinterface.h msg_dbchg.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrftpinterface.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		fdrftpinterface.h fdrinterface.h msg_dbchg.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h
fdrinet.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrclientconnection.h \
		fdrinet.h
fdrinterface.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h configkey.h configval.h dbaccess.h dllbase.h \
		dsm2.h mutex.h guard.h cticonnect.h netports.h dsm2err.h \
		words.h sema.h ctinexus.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_dbchg.h \
		msg_signal.h fdrinterface.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h msg_reg.h queue.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h
fdrio.obj:	yukon.h precompiled.h ctidbgmem.h fdrio.h
fdriosocket.obj:	yukon.h precompiled.h ctidbgmem.h fdriosocket.h \
		ctitime.h dlldefs.h fdrio.h
fdrlivedata.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h dbaccess.h sema.h hashkey.h hash_functions.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		livedatatypes.h fdrlivedata.h fdrinterface.h msg_dbchg.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdrpointlist.h \
		fdrsimplebase.h fdrasciiimportbase.h livedata_rtp_api.h \
		RTP_APIW.H RTP.H
fdrlodestarimport.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h msg_signal.h pointtypes.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h rtdb.h hashkey.h hash_functions.h
fdrlodestarimport_enh.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h pointtypes.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrlodestarimport.h fdrlodestarinfo.h rtdb.h hashkey.h \
		hash_functions.h fdrlodestarimport_enh.h
fdrlodestarimport_std.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h pointtypes.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrlodestarimport.h fdrlodestarinfo.h rtdb.h hashkey.h \
		hash_functions.h fdrlodestarimport_std.h
fdrlodestarinfo.obj:	yukon.h precompiled.h ctidbgmem.h \
		fdrlodestarinfo.h dlldefs.h fdr.h pointdefs.h
fdrpibase.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h dbaccess.h sema.h hashkey.h hash_functions.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		fdrinterface.h msg_dbchg.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdrpointlist.h fdrpipoll.h fdrpibase.h \
		fdrsimplebase.h fdrasciiimportbase.h fdrpinotify.h
fdrpinotify.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h fdrpinotify.h fdrpibase.h \
		fdrinterface.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h msg_dbchg.h connection.h exchange.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h fdrsimplebase.h fdrasciiimportbase.h
fdrpipoll.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h fdrpipoll.h fdrpibase.h \
		fdrinterface.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h msg_dbchg.h connection.h exchange.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h fdrsimplebase.h fdrasciiimportbase.h
fdrpoint.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		types.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		sorted_vector.h rwutil.h boost_time.h boostutil.h
fdrpointidmap.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h sema.h fdrpointidmap.h ctitime.h pointtypes.h fdr.h \
		pointdefs.h resolvers.h db_entry_defines.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
fdrpointlist.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h fdrpointlist.h \
		mgr_fdrpoint.h smartmap.h boostutil.h readers_writer_lock.h \
		critical_section.h
fdrprotectedmaplist.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h fdrprotectedmaplist.h
fdrrccs.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrclientconnection.h \
		fdrinet.h fdrrccs.h
fdrrdex.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h \
		fdrrdex.h
fdrscadahelper.obj:	yukon.h precompiled.h ctidbgmem.h msg_cmd.h \
		dlldefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h fdrdebuglevel.h \
		fdrscadahelper.h pointtypes.h fdrdestination.h fdr.h \
		pointdefs.h fdracsmulti.h fdrpointlist.h mgr_fdrpoint.h \
		smartmap.h dllbase.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h readers_writer_lock.h \
		critical_section.h fdrpoint.h logger.h thread.h \
		CtiPCPtrQueue.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h fdrinterface.h msg_dbchg.h \
		connection.h exchange.h msg_multi.h msg_pdata.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		fdrdnpslave.h fdrdnphelper.h dnp_object_analoginput.h \
		dnp_objects.h prot_base.h xfer.h dnp_object_time.h prot_dnp.h \
		packet_finder.h dnp_application.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h
fdrscadaserver.obj:	yukon.h precompiled.h ctidbgmem.h fdrscadaserver.h \
		dlldefs.h fdrsocketserver.h queues.h cticalls.h os2_2w32.h \
		types.h fdrclientserverconnection.h fdrinterface.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h boostutil.h \
		utility.h numstr.h sorted_vector.h msg_dbchg.h connection.h \
		exchange.h dllbase.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrserverconnection.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h fdrinterface.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_dbchg.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h
fdrservice.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h configkey.h configval.h logger.h thread.h \
		mutex.h guard.h CtiPCPtrQueue.h fdrservice.h cservice.h \
		thread_monitor.h smartmap.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h readers_writer_lock.h \
		critical_section.h queue.h thread_register_data.h \
		connection.h exchange.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h
fdrsimplebase.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h queue.h dbaccess.h sema.h hashkey.h hash_functions.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		fdrinterface.h msg_dbchg.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h
fdrsinglesocket.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h queue.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrsinglesocket.h
fdrsocketconnection.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h fdrsocketlayer.h \
		fdrsocketconnection.h
fdrsocketinterface.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h fdrpointlist.h \
		mgr_fdrpoint.h smartmap.h boostutil.h readers_writer_lock.h \
		critical_section.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h pointdefs.h fdrsocketconnection.h fdrsocketinterface.h \
		fdrinterface.h message.h collectable.h rwutil.h boost_time.h \
		msg_dbchg.h connection.h exchange.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h fdrdebuglevel.h
fdrsocketlayer.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h fdrinterface.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h msg_dbchg.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		fdrsocketinterface.h fdrclientconnection.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h
fdrsocketserver.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h dllbase.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h logger.h thread.h \
		CtiPCPtrQueue.h queue.h fdrsocketinterface.h fdrinterface.h \
		msg_dbchg.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h \
		fdrsocketconnection.h fdrserverconnection.h fdrsocketserver.h \
		fdrclientserverconnection.h ctidate.h
fdrstec.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h fdrstec.h fdrftpinterface.h fdrinterface.h \
		msg_dbchg.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrtelegyr.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h dbaccess.h sema.h hashkey.h hash_functions.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		fdrinterface.h msg_dbchg.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdrpointlist.h fdrtelegyr.h \
		fdrasciiimportbase.h telegyrgroup.h telegyrcontrolcenter.h \
		rtdb.h
fdrtextexport.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h pointtypes.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h fdrtextfilebase.h \
		fdrinterface.h msg_dbchg.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrtextexport.h
fdrtextfilebase.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h
fdrtextimport.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ctistring.h rwutil.h \
		boost_time.h boostutil.h cparms.h configkey.h configval.h \
		msg_cmd.h message.h collectable.h pointtypes.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		fdrtextfilebase.h fdrinterface.h msg_dbchg.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h fdrtextimport.h
fdrtristate.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h fdrtristate.h fdrftpinterface.h fdrinterface.h \
		msg_dbchg.h mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrtristatesub.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		fdrTriStateSub.h fdrftpinterface.h fdrinterface.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h boostutil.h \
		utility.h queues.h sorted_vector.h msg_dbchg.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h ctidate.h
fdrvalmet.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrvalmet.h
fdrwabash.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h fdrwabash.h \
		fdrinterface.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		msg_dbchg.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrxa21lm.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h cparms.h rwutil.h boost_time.h \
		boostutil.h configkey.h configval.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		queue.h fdrsocketinterface.h fdrinterface.h msg_dbchg.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h fdrserverconnection.h \
		fdrxa21lm.h string_util.h
livedatatypes.obj:	yukon.h precompiled.h ctidbgmem.h livedatatypes.h \
		pointdefs.h cparms.h rwutil.h ctitime.h dlldefs.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h configkey.h \
		configval.h logger.h thread.h mutex.h guard.h CtiPCPtrQueue.h
livedata_rtp_api.obj:	yukon.h precompiled.h ctidbgmem.h \
		livedata_rtp_api.h logger.h dlldefs.h thread.h mutex.h \
		guard.h numstr.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h RTP_APIW.H \
		RTP.H
mgr_fdrpoint.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h sema.h hashkey.h hash_functions.h resolvers.h \
		pointtypes.h db_entry_defines.h fdr.h pointdefs.h \
		fdrdebuglevel.h mgr_fdrpoint.h smartmap.h boostutil.h \
		utility.h ctitime.h queues.h sorted_vector.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h logger.h thread.h CtiPCPtrQueue.h \
		FdrException.h rwutil.h boost_time.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
telegyrcontrolcenter.obj:	yukon.h precompiled.h ctidbgmem.h \
		telegyrcontrolcenter.h dlldefs.h fdr.h pointdefs.h \
		telegyrgroup.h ctitime.h fdrpoint.h pointtypes.h \
		fdrdestination.h types.h mutex.h logger.h thread.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h sorted_vector.h
telegyrgroup.obj:	yukon.h precompiled.h ctidbgmem.h telegyrgroup.h \
		ctitime.h dlldefs.h fdr.h pointdefs.h fdrpoint.h pointtypes.h \
		fdrdestination.h types.h
test_fdrtextimport.obj:	fdrtextimport.h dlldefs.h fdrtextfilebase.h \
		fdrinterface.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h precompiled.h ctitime.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h msg_dbchg.h connection.h exchange.h dllbase.h \
		dsm2.h mutex.h guard.h cticonnect.h netports.h dsm2err.h \
		words.h logger.h thread.h CtiPCPtrQueue.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h mgr_fdrpoint.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h TextFileInterfaceParts.h ctistring.h ctidate.h
test_fdrtristatesub.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ctistring.h rwutil.h \
		boost_time.h boostutil.h fdrTriStateSub.h fdrftpinterface.h \
		fdrinterface.h message.h collectable.h msg_dbchg.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
textfileinterfaceparts.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		dllbase.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
		dsm2err.h words.h logger.h thread.h CtiPCPtrQueue.h \
		TextFileInterfaceParts.h
#ENDUPDATE#
