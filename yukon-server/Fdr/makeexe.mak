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
-I$(PROCLOG)\include \
-I$(SERVICE)\include \
-I$(RW) \
-I$(BOOST) \


.PATH.cpp = .;$(R_FDR)

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
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \
;$(TCLINC) \
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
$(COMPILEBASE)\lib\proclog.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib


# Debug testing $(COMPILEBASE)\lib\cticparms.lib \
#        $(FDROBJS) -link $(RWLIBS) $(BOOSTLIBS) $(FDRLIBS) /PROFILE /DEBUGTYPE:CV /PDB:NONE


ALL:            $(CTIPROGS)

fdr.exe:    $(FDROBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) /Fm $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) -o ..\$@ \
        $(FDROBJS) -link $(RWLIBS) $(BOOSTLIBS) $(FDRLIBS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
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
		clrdump.h logger.h thread.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h CServiceConfig.h \
		fdrservice.h cservice.h id_ctibase.h utility.h queues.h \
		sorted_vector.h id_build.h
fdracs.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h queue.h \
		utility.h queues.h sorted_vector.h pointtypes.h \
		fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdracs.h
fdracsmulti.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h queue.h \
		utility.h queues.h sorted_vector.h pointtypes.h \
		fdrpointlist.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrsocketinterface.h fdrinterface.h fdrdebuglevel.h \
		fdrscadahelper.h fdracsmulti.h device.h devicetypes.h \
		fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h
fdrareva.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h fdrareva.h fdrinterface.h \
		message.h collectable.h rwutil.h ctitime.h boost_time.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h
fdrasciiimportbase.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h netports.h \
		fdrasciiimportbase.h fdrinterface.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h
fdrbepc.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h msg_cmd.h message.h collectable.h \
		pointtypes.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		cticonnect.h netports.h fdrtextfilebase.h fdrinterface.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrbepc.h ctitokenizer.h
fdrclientconnection.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h fdrinterface.h message.h \
		collectable.h rwutil.h boost_time.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h \
		netports.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h utility.h \
		queues.h sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h \
		device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h
fdrclientserverconnection.obj:	yukon.h precompiled.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h fdrinterface.h message.h \
		collectable.h rwutil.h boost_time.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h \
		netports.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h utility.h \
		queues.h sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h fdrscadaserver.h \
		fdrsocketserver.h fdrclientserverconnection.h
fdrcygnet.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		cparms.h rwutil.h boost_time.h configkey.h configval.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		logger.h thread.h CtiPCPtrQueue.h queue.h utility.h queues.h \
		sorted_vector.h pointtypes.h fdrcygnet.h fdrinterface.h \
		mgr_fdrpoint.h rtdb.h hashkey.h hash_functions.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		device.h devicetypes.h
fdrdestination.obj:	yukon.h precompiled.h ctidbgmem.h fdrpoint.h \
		dlldefs.h pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		types.h ctitime.h rwutil.h boost_time.h
fdrdsm2filein.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_cmd.h message.h \
		collectable.h pointtypes.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h cticonnect.h netports.h fdrtextfilebase.h \
		fdrinterface.h connection.h exchange.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		utility.h queues.h sorted_vector.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrdsm2filein.h ctitokenizer.h
fdrdsm2import.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h \
		netports.h queue.h utility.h queues.h sorted_vector.h \
		pointtypes.h fdrdsm2import.h fdrasciiimportbase.h \
		fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrftpinterface.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h netports.h \
		fdrftpinterface.h fdrinterface.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h
fdrinet.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h cparms.h rwutil.h boost_time.h configkey.h \
		configval.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h queue.h pointtypes.h \
		fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h fdrinet.h
fdrinterface.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h ctinexus.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_ptreg.h msg_cmd.h msg_dbchg.h msg_signal.h fdrinterface.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		msg_reg.h queue.h utility.h queues.h sorted_vector.h \
		mgr_fdrpoint.h rtdb.h hashkey.h hash_functions.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrio.obj:	yukon.h precompiled.h ctidbgmem.h fdrio.h
fdriosocket.obj:	yukon.h precompiled.h ctidbgmem.h fdriosocket.h \
		ctitime.h dlldefs.h fdrio.h
fdrlivedata.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h queue.h \
		utility.h queues.h sorted_vector.h pointtypes.h dbaccess.h \
		sema.h hashkey.h hash_functions.h resolvers.h \
		db_entry_defines.h fdr.h fdrdebuglevel.h livedatatypes.h \
		fdrlivedata.h fdrinterface.h mgr_fdrpoint.h rtdb.h fdrpoint.h \
		fdrdestination.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h device.h devicetypes.h \
		livedata_rtp_api.h RTP_APIW.H RTP.H
fdrlodestarimport.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_cmd.h message.h \
		collectable.h msg_signal.h pointtypes.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h utility.h queues.h sorted_vector.h mgr_fdrpoint.h \
		rtdb.h hashkey.h hash_functions.h fdrpoint.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrlodestarimport.h fdrlodestarinfo.h
fdrlodestarimport_enh.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_cmd.h message.h \
		collectable.h pointtypes.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h cticonnect.h netports.h fdrtextfilebase.h \
		fdrinterface.h connection.h exchange.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		utility.h queues.h sorted_vector.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrlodestarimport.h fdrlodestarinfo.h fdrlodestarimport_enh.h
fdrlodestarimport_std.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_cmd.h message.h \
		collectable.h pointtypes.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h cticonnect.h netports.h fdrtextfilebase.h \
		fdrinterface.h connection.h exchange.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		utility.h queues.h sorted_vector.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrlodestarimport.h fdrlodestarinfo.h fdrlodestarimport_std.h
fdrlodestarinfo.obj:	yukon.h precompiled.h ctidbgmem.h \
		fdrlodestarinfo.h dlldefs.h fdr.h pointdefs.h
fdrpibase.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h queue.h \
		utility.h queues.h sorted_vector.h pointtypes.h dbaccess.h \
		sema.h hashkey.h hash_functions.h resolvers.h \
		db_entry_defines.h fdr.h fdrdebuglevel.h fdrinterface.h \
		mgr_fdrpoint.h rtdb.h fdrpoint.h fdrdestination.h \
		fdrpointlist.h fdrpipoll.h fdrpibase.h fdrsimplebase.h \
		fdrasciiimportbase.h device.h devicetypes.h fdrpinotify.h
fdrpinotify.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h fdrpinotify.h fdrpibase.h \
		fdrinterface.h message.h collectable.h rwutil.h boost_time.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h cticonnect.h netports.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h device.h devicetypes.h
fdrpipoll.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h fdrpipoll.h fdrpibase.h fdrinterface.h \
		message.h collectable.h rwutil.h boost_time.h connection.h \
		exchange.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		cticonnect.h netports.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h mgr_fdrpoint.h rtdb.h hashkey.h hash_functions.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h device.h devicetypes.h
fdrpoint.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		types.h logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		CtiPCPtrQueue.h rwutil.h boost_time.h utility.h queues.h \
		sorted_vector.h
fdrpointidmap.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h fdrpointidmap.h ctitime.h pointtypes.h resolvers.h \
		db_entry_defines.h logger.h thread.h CtiPCPtrQueue.h
fdrpointlist.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h fdrpointlist.h queues.h \
		mgr_fdrpoint.h rtdb.h hashkey.h hash_functions.h utility.h \
		sorted_vector.h
fdrprotectedmaplist.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h fdrprotectedmaplist.h \
		queues.h
fdrrccs.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h cparms.h rwutil.h boost_time.h configkey.h \
		configval.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h queue.h pointtypes.h \
		fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h fdrinet.h fdrrccs.h
fdrrdex.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h queue.h \
		utility.h queues.h sorted_vector.h pointtypes.h \
		fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h fdrrdex.h
fdrscadahelper.obj:	yukon.h precompiled.h ctidbgmem.h msg_cmd.h \
		dlldefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h fdrdebuglevel.h fdrscadahelper.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h fdracsmulti.h queues.h \
		types.h fdrpointlist.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h utility.h numstr.h sorted_vector.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h fdrpoint.h device.h devicetypes.h \
		fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h fdrinterface.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h msg_multi.h \
		msg_pdata.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h
fdrscadaserver.obj:	yukon.h precompiled.h ctidbgmem.h fdrscadaserver.h \
		dlldefs.h fdrsocketserver.h queues.h types.h \
		fdrclientserverconnection.h fdrinterface.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h connection.h \
		exchange.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h utility.h sorted_vector.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
fdrserverconnection.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h fdrinterface.h message.h \
		collectable.h rwutil.h boost_time.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h \
		netports.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h utility.h \
		queues.h sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h \
		device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h
fdrservice.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h configkey.h configval.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		CtiPCPtrQueue.h fdrservice.h cservice.h
fdrsimplebase.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h \
		netports.h queue.h utility.h queues.h sorted_vector.h \
		pointtypes.h dbaccess.h sema.h hashkey.h hash_functions.h \
		resolvers.h db_entry_defines.h fdr.h fdrdebuglevel.h \
		fdrinterface.h mgr_fdrpoint.h rtdb.h fdrpoint.h \
		fdrdestination.h fdrpointlist.h fdrsimplebase.h \
		fdrasciiimportbase.h device.h devicetypes.h
fdrsinglesocket.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h msg_ptreg.h \
		msg_cmd.h msg_reg.h msg_signal.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h \
		netports.h queue.h utility.h queues.h sorted_vector.h \
		pointtypes.h fdrsocketinterface.h fdrinterface.h \
		mgr_fdrpoint.h rtdb.h hashkey.h hash_functions.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h fdrsinglesocket.h
fdrsocketconnection.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h fdrsocketlayer.h queues.h types.h \
		device.h devicetypes.h fdrsocketconnection.h
fdrsocketinterface.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h \
		fdrpointlist.h queues.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h utility.h sorted_vector.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		fdrsocketconnection.h fdrsocketinterface.h fdrinterface.h \
		message.h collectable.h rwutil.h boost_time.h connection.h \
		exchange.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h fdrdebuglevel.h
fdrsocketlayer.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h fdrinterface.h message.h \
		collectable.h rwutil.h boost_time.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h \
		netports.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h utility.h \
		queues.h sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h fdrsocketinterface.h \
		fdrclientconnection.h fdrsocketlayer.h device.h devicetypes.h \
		fdrsocketconnection.h fdrserverconnection.h
fdrsocketserver.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h CtiPCPtrQueue.h \
		queue.h utility.h queues.h sorted_vector.h pointtypes.h \
		fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h device.h \
		devicetypes.h fdrsocketconnection.h fdrserverconnection.h \
		fdrsocketserver.h fdrclientserverconnection.h ctidate.h
fdrstec.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h queue.h \
		utility.h queues.h sorted_vector.h pointtypes.h fdrstec.h \
		fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrtelegyr.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h queue.h \
		utility.h queues.h sorted_vector.h pointtypes.h dbaccess.h \
		sema.h hashkey.h hash_functions.h resolvers.h \
		db_entry_defines.h fdr.h fdrdebuglevel.h fdrinterface.h \
		mgr_fdrpoint.h rtdb.h fdrpoint.h fdrdestination.h \
		fdrpointlist.h fdrtelegyr.h fdrasciiimportbase.h device.h \
		devicetypes.h telegyrgroup.h telegyrcontrolcenter.h
fdrtextexport.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_cmd.h message.h \
		collectable.h pointtypes.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h cticonnect.h netports.h fdrtextfilebase.h \
		fdrinterface.h connection.h exchange.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		utility.h queues.h sorted_vector.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrtextexport.h
fdrtextfilebase.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h \
		boost_time.h configkey.h configval.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h netports.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h
fdrtextimport.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h ctistring.h rwutil.h \
		boost_time.h cparms.h configkey.h configval.h msg_cmd.h \
		message.h collectable.h pointtypes.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h utility.h queues.h sorted_vector.h mgr_fdrpoint.h \
		rtdb.h hashkey.h hash_functions.h fdrpoint.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h \
		fdrtextimport.h
fdrtristate.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h queue.h \
		utility.h queues.h sorted_vector.h pointtypes.h fdrtristate.h \
		fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h
fdrtristatesub.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		fdrTriStateSub.h fdrftpinterface.h fdrinterface.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h ctidate.h
fdrvalmet.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h cparms.h rwutil.h boost_time.h configkey.h \
		configval.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_ptreg.h msg_cmd.h msg_reg.h msg_signal.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h queue.h pointtypes.h \
		fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrvalmet.h
fdrwabash.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h fdrwabash.h fdrinterface.h \
		message.h collectable.h rwutil.h ctitime.h boost_time.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h
fdrxa21lm.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h cparms.h rwutil.h boost_time.h \
		configkey.h configval.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_ptreg.h msg_cmd.h msg_reg.h \
		msg_signal.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h queue.h \
		utility.h queues.h sorted_vector.h pointtypes.h \
		fdrsocketinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h hash_functions.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h fdrxa21lm.h critical_section.h \
		string_util.h
inetinterface.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h \
		inetinterface.h ctitime.h fdriosocket.h fdrio.h
livedatatypes.obj:	yukon.h precompiled.h ctidbgmem.h livedatatypes.h \
		pointdefs.h cparms.h rwutil.h ctitime.h dlldefs.h \
		boost_time.h configkey.h configval.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h
livedata_rtp_api.obj:	yukon.h precompiled.h ctidbgmem.h \
		livedata_rtp_api.h logger.h dlldefs.h thread.h mutex.h \
		guard.h numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h \
		RTP_APIW.H RTP.H
mgr_fdrpoint.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h hashkey.h hash_functions.h resolvers.h pointtypes.h \
		db_entry_defines.h fdr.h pointdefs.h fdrdebuglevel.h \
		mgr_fdrpoint.h rtdb.h utility.h ctitime.h queues.h \
		sorted_vector.h fdrpoint.h fdrdestination.h logger.h thread.h \
		CtiPCPtrQueue.h rwutil.h boost_time.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
telegyrcontrolcenter.obj:	yukon.h precompiled.h ctidbgmem.h \
		telegyrcontrolcenter.h dlldefs.h fdr.h pointdefs.h \
		telegyrgroup.h ctitime.h fdrpoint.h pointtypes.h \
		fdrdestination.h types.h mutex.h logger.h thread.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h
telegyrgroup.obj:	yukon.h precompiled.h ctidbgmem.h telegyrgroup.h \
		ctitime.h dlldefs.h fdr.h pointdefs.h fdrpoint.h pointtypes.h \
		fdrdestination.h types.h
testinterface.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h \
		fdrinterface.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h CtiPCPtrQueue.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h testinterface.h \
		fdrinterface.cpp dbaccess.h sema.h ctinexus.h resolvers.h \
		db_entry_defines.h msg_cmd.h msg_dbchg.h msg_signal.h
test_fdrtextimport.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h \
		dlldefs.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h ctistring.h rwutil.h \
		boost_time.h fdrtextimport.h fdrtextfilebase.h fdrinterface.h \
		message.h collectable.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h netports.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h
test_fdrtristatesub.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h ctistring.h rwutil.h \
		boost_time.h fdrTriStateSub.h fdrftpinterface.h \
		fdrinterface.h message.h collectable.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		cticonnect.h netports.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h utility.h queues.h sorted_vector.h mgr_fdrpoint.h \
		rtdb.h hashkey.h hash_functions.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
textfileinterfaceparts.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h configkey.h \
		configval.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		logger.h thread.h CtiPCPtrQueue.h TextFileInterfaceParts.h
#ENDUPDATE#
