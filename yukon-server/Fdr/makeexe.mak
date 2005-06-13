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
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib


# Debug testing $(COMPILEBASE)\lib\cticparms.lib \
#        $(FDROBJS) -link $(RWLIBS) $(FDRLIBS) /PROFILE /DEBUGTYPE:CV /PDB:NONE


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
fdr.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h ctibase.h \
		ctinexus.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h cparms.h CServiceConfig.h \
		fdrservice.h cservice.h id_ctibase.h utility.h id_build.h
fdracs.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdracs.h
fdrasciiimportbase.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_signal.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrasciiimportbase.h fdrinterface.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h
fdrclientconnection.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h fdrinterface.h message.h \
		collectable.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h cparms.h fdrdebuglevel.h fdrpointlist.h queues.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h
fdrcygnet.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		fdrcygnet.h fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h device.h devicetypes.h
fdrdestination.obj:	yukon.h precompiled.h ctidbgmem.h fdrdestination.h \
		dlldefs.h fdr.h pointdefs.h
fdrdsm2filein.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_cmd.h message.h collectable.h pointtypes.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h fdrtextfilebase.h fdrinterface.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h \
		TextFileInterfaceParts.h fdrdsm2filein.h
fdrdsm2import.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h queue.h \
		pointtypes.h fdrdsm2import.h fdrasciiimportbase.h \
		fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		queues.h
fdrftpinterface.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_signal.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrftpinterface.h fdrinterface.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h
fdrinet.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h fdrinet.h
fdrinterface.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h ctinexus.h resolvers.h pointtypes.h \
		db_entry_defines.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_signal.h msg_ptreg.h msg_cmd.h \
		msg_dbchg.h fdrinterface.h connection.h exchange.h logger.h \
		thread.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
fdrio.obj:	yukon.h precompiled.h ctidbgmem.h fdrio.h
fdriosocket.obj:	yukon.h precompiled.h ctidbgmem.h fdriosocket.h \
		fdrio.h
fdrlivedata.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		dbaccess.h sema.h hashkey.h resolvers.h db_entry_defines.h \
		fdr.h fdrdebuglevel.h livedatatypes.h fdrlivedata.h \
		fdrinterface.h mgr_fdrpoint.h rtdb.h fdrpoint.h \
		fdrdestination.h fdrpointlist.h queues.h fdrsimplebase.h \
		fdrasciiimportbase.h device.h devicetypes.h \
		livedata_rtp_api.h RTP_APIW.H RTP.H
fdrlodestarimport.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_cmd.h message.h collectable.h pointtypes.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h fdrtextfilebase.h fdrinterface.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h
fdrlodestarimport_enh.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_cmd.h message.h collectable.h pointtypes.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h fdrtextfilebase.h fdrinterface.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h fdrlodestarimport_enh.h
fdrlodestarimport_std.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_cmd.h message.h collectable.h pointtypes.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h fdrtextfilebase.h fdrinterface.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h \
		TextFileInterfaceParts.h fdrlodestarimport.h \
		fdrlodestarinfo.h fdrlodestarimport_std.h
fdrlodestarinfo.obj:	yukon.h precompiled.h ctidbgmem.h \
		fdrlodestarinfo.h dlldefs.h fdr.h pointdefs.h
fdrpibase.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		dbaccess.h sema.h hashkey.h resolvers.h db_entry_defines.h \
		fdr.h fdrdebuglevel.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		fdrpoint.h fdrdestination.h fdrpointlist.h queues.h \
		fdrpipoll.h fdrpibase.h fdrsimplebase.h fdrasciiimportbase.h \
		device.h devicetypes.h fdrpinotify.h
fdrpinotify.obj:	yukon.h precompiled.h ctidbgmem.h fdrpinotify.h \
		dlldefs.h fdrpibase.h fdrinterface.h message.h collectable.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h cparms.h fdrdebuglevel.h \
		fdrpointlist.h queues.h fdrsimplebase.h fdrasciiimportbase.h \
		device.h devicetypes.h
fdrpipoll.obj:	yukon.h precompiled.h ctidbgmem.h fdrpipoll.h dlldefs.h \
		fdrpibase.h fdrinterface.h message.h collectable.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h cparms.h fdrdebuglevel.h \
		fdrpointlist.h queues.h fdrsimplebase.h fdrasciiimportbase.h \
		device.h devicetypes.h
fdrpoint.obj:	yukon.h precompiled.h ctidbgmem.h fdrpoint.h dlldefs.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h types.h \
		logger.h thread.h mutex.h guard.h
fdrpointidmap.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h fdrpointidmap.h pointtypes.h \
		resolvers.h db_entry_defines.h logger.h thread.h
fdrpointlist.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h fdrpointlist.h queues.h \
		mgr_fdrpoint.h rtdb.h hashkey.h
fdrprotectedmaplist.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h fdrprotectedmaplist.h \
		queues.h
fdrrccs.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h fdrinet.h fdrrccs.h
fdrrdex.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h fdrrdex.h
fdrserverconnection.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h fdrinterface.h message.h \
		collectable.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h cparms.h fdrdebuglevel.h fdrpointlist.h queues.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h
fdrservice.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		logger.h thread.h mutex.h guard.h fdrservice.h cservice.h
fdrsimplebase.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h queue.h \
		pointtypes.h dbaccess.h sema.h hashkey.h resolvers.h \
		db_entry_defines.h fdr.h fdrdebuglevel.h fdrinterface.h \
		mgr_fdrpoint.h rtdb.h fdrpoint.h fdrdestination.h \
		fdrpointlist.h queues.h fdrsimplebase.h fdrasciiimportbase.h \
		device.h devicetypes.h
fdrsinglesocket.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h queue.h \
		pointtypes.h fdrsocketinterface.h queues.h fdrinterface.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h \
		device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h fdrsinglesocket.h
fdrsocketconnection.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h fdrsocketlayer.h queues.h \
		types.h device.h devicetypes.h fdrsocketconnection.h
fdrsocketinterface.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h fdrpointlist.h queues.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h pointdefs.h fdrsocketconnection.h \
		fdrsocketinterface.h fdrinterface.h message.h collectable.h \
		connection.h exchange.h msg_multi.h msg_pdata.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h fdrdebuglevel.h
fdrsocketlayer.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h fdrinterface.h message.h \
		collectable.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h cparms.h fdrdebuglevel.h fdrpointlist.h queues.h \
		fdrsocketinterface.h fdrclientconnection.h fdrsocketlayer.h \
		device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h
fdrstec.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		fdrstec.h fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h
fdrtelegyr.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		dbaccess.h sema.h hashkey.h resolvers.h db_entry_defines.h \
		fdr.h fdrdebuglevel.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		fdrpoint.h fdrdestination.h fdrpointlist.h queues.h \
		fdrtelegyr.h fdrasciiimportbase.h device.h devicetypes.h \
		telegyrgroup.h telegyrcontrolcenter.h
fdrtextexport.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_cmd.h message.h collectable.h pointtypes.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h fdrtextfilebase.h fdrinterface.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h \
		TextFileInterfaceParts.h fdrtextexport.h
fdrtextfilebase.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_signal.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h \
		TextFileInterfaceParts.h
fdrtextimport.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_cmd.h message.h collectable.h pointtypes.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h fdrtextfilebase.h fdrinterface.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h \
		TextFileInterfaceParts.h fdrtextimport.h
fdrtristate.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		fdrtristate.h fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h
fdrvalmet.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrvalmet.h
fdrxa21lm.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h queue.h pointtypes.h \
		numstr.h fdrsocketinterface.h queues.h fdrinterface.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h fdrxa21lm.h critical_section.h \
		string_util.h
inetinterface.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h \
		socketinterface.h fdrinterface.h message.h collectable.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h cparms.h fdrdebuglevel.h \
		fdrpointlist.h queues.h inetinterface.h fdriosocket.h fdrio.h
livedatatypes.obj:	yukon.h precompiled.h ctidbgmem.h livedatatypes.h \
		pointdefs.h cparms.h dlldefs.h logger.h thread.h mutex.h \
		guard.h
livedata_rtp_api.obj:	yukon.h precompiled.h ctidbgmem.h \
		livedata_rtp_api.h logger.h dlldefs.h thread.h mutex.h \
		guard.h RTP_APIW.H RTP.H
mgr_fdrpoint.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h hashkey.h resolvers.h pointtypes.h \
		db_entry_defines.h fdr.h pointdefs.h fdrdebuglevel.h \
		mgr_fdrpoint.h rtdb.h fdrpoint.h fdrdestination.h logger.h \
		thread.h utility.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
socketinterface.obj:	yukon.h precompiled.h ctidbgmem.h \
		socketinterface.h dlldefs.h fdrinterface.h message.h \
		collectable.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h msg_ptreg.h \
		msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h cparms.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
telegyrcontrolcenter.obj:	yukon.h precompiled.h ctidbgmem.h \
		telegyrcontrolcenter.h dlldefs.h fdr.h pointdefs.h \
		telegyrgroup.h fdrpoint.h pointtypes.h fdrdestination.h \
		types.h mutex.h logger.h thread.h guard.h
telegyrgroup.obj:	yukon.h precompiled.h ctidbgmem.h telegyrgroup.h \
		dlldefs.h fdr.h pointdefs.h fdrpoint.h pointtypes.h \
		fdrdestination.h types.h
testinterface.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h \
		fdrinterface.h message.h collectable.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h cparms.h fdrdebuglevel.h \
		fdrpointlist.h queues.h testinterface.h fdrinterface.cpp \
		dbaccess.h sema.h ctinexus.h resolvers.h db_entry_defines.h \
		msg_cmd.h msg_dbchg.h
textfileinterfaceparts.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		dlldefs.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		message.h msg_signal.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		TextFileInterfaceParts.h
#ENDUPDATE#
