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
        $(FDROBJS) -link $(RWLIBS) $(FDRLIBS)
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
fdr.obj:	dlldefs.h ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h cparms.h \
		CServiceConfig.h fdrservice.h cservice.h
fdracs.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_signal.h yukon.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		msg_ptreg.h msg_cmd.h msg_reg.h connection.h exchange.h \
		queue.h pointtypes.h logger.h thread.h fdrsocketinterface.h \
		queues.h fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdrdebuglevel.h \
		fdrprotectedmaplist.h fdrpointidmap.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h device.h devicetypes.h \
		fdracs.h
fdrclientconnection.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrinterface.h message.h collectable.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h cparms.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h \
		fdrsocketlayer.h fdrsocketconnection.h device.h devicetypes.h \
		fdrclientconnection.h
fdrcygnet.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h queue.h pointtypes.h logger.h thread.h fdrcygnet.h \
		fdrpointidmap.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h device.h devicetypes.h
fdrdestination.obj:	fdrinterface.h dlldefs.h message.h collectable.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		fdrdestination.h cparms.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h
fdrftpinterface.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h fdrftpinterface.h fdrinterface.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h fdrdestination.h \
		fdrdebuglevel.h fdrprotectedmaplist.h queues.h \
		fdrpointidmap.h
fdrinet.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_signal.h yukon.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		msg_ptreg.h msg_cmd.h msg_reg.h connection.h exchange.h \
		queue.h pointtypes.h logger.h thread.h fdrsocketinterface.h \
		queues.h fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdrdebuglevel.h \
		fdrprotectedmaplist.h fdrpointidmap.h fdrserverconnection.h \
		fdrsocketconnection.h fdrclientconnection.h fdrsocketlayer.h \
		device.h devicetypes.h fdrinet.h
fdrinterface.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h cparms.h ctinexus.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_dbchg.h fdrinterface.h \
		connection.h exchange.h msg_reg.h queue.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h logger.h \
		thread.h
fdrio.obj:	fdrio.h
fdriosocket.obj:	fdriosocket.h fdrio.h
fdrpoint.obj:	fdrpoint.h dlldefs.h fdrdestination.h
fdrpointidmap.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h fdrpointidmap.h logger.h \
		thread.h
fdrprotectedmaplist.obj:	dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrpointidmap.h fdrprotectedmaplist.h queues.h
fdrserverconnection.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrinterface.h message.h collectable.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h cparms.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h \
		fdrsocketlayer.h fdrsocketconnection.h device.h devicetypes.h \
		fdrserverconnection.h
fdrservice.obj:	cparms.h dlldefs.h logger.h thread.h mutex.h guard.h \
		fdrservice.h cservice.h
fdrsinglesocket.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h queue.h pointtypes.h logger.h thread.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
		fdrprotectedmaplist.h fdrpointidmap.h fdrsocketlayer.h \
		fdrsocketconnection.h device.h devicetypes.h \
		fdrsinglesocket.h
fdrsocketconnection.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		fdrsocketconnection.h queues.h types.h
fdrsocketinterface.obj:	dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h \
		fdrsocketinterface.h fdrinterface.h message.h collectable.h \
		connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h fdrdestination.h \
		cparms.h fdrdebuglevel.h
fdrsocketlayer.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrinterface.h message.h collectable.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h cparms.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h \
		fdrsocketinterface.h fdrclientconnection.h \
		fdrsocketconnection.h fdrserverconnection.h fdrsocketlayer.h \
		device.h devicetypes.h
fdrstec.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h msg_signal.h yukon.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		msg_ptreg.h msg_cmd.h msg_reg.h connection.h exchange.h \
		queue.h pointtypes.h logger.h thread.h fdrstec.h \
		fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h
fdrtristate.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h queue.h pointtypes.h logger.h thread.h \
		fdrtristate.h fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h
fdrvalmet.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h queue.h pointtypes.h logger.h thread.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
		fdrprotectedmaplist.h fdrpointidmap.h fdrsinglesocket.h \
		fdrsocketlayer.h fdrsocketconnection.h device.h devicetypes.h \
		fdrvalmet.h
inetinterface.obj:	dlldefs.h socketinterface.h fdrinterface.h \
		message.h collectable.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h cparms.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h \
		inetinterface.h fdriosocket.h fdrio.h
mgr_fdrpoint.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h hashkey.h resolvers.h \
		pointtypes.h yukon.h mgr_fdrpoint.h rtdb.h fdrpoint.h \
		fdrdestination.h logger.h thread.h
socketinterface.obj:	socketinterface.h dlldefs.h fdrinterface.h \
		message.h collectable.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h cparms.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h
testinterface.obj:	dlldefs.h fdrinterface.h message.h collectable.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		fdrdestination.h cparms.h fdrdebuglevel.h \
		fdrprotectedmaplist.h queues.h fdrpointidmap.h \
		testinterface.h fdrinterface.cpp ctinexus.h msg_cmd.h \
		msg_dbchg.h logger.h thread.h
#ENDUPDATE#
