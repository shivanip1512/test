# nmake file YUKON 1.0

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


FDRINTERFACES=\


FDRINTERFACEOBJS=\
fdrdestination.obj  \
fdrpoint.obj \
mgr_fdrpoint.obj \
fdrinterface.obj \
fdrsocketlayer.obj \
fdrsocketconnection.obj \
fdrserverconnection.obj \
fdrclientconnection.obj \
fdrftpinterface.obj \
fdrpointlist.obj \
fdrsocketinterface.obj \
fdrasciiimportbase.obj \
textfileinterfaceparts.obj \
fdrtextfilebase.obj \
fdrsinglesocket.obj

# socketinterface.obj \
#fdrpointidmap.obj \
#fdrprotectedmaplist.obj \

CTIFDRLIBS=\
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\proclog.lib \
$(COMPILEBASE)\lib\cmdline.lib \
wininet.lib



TESTINTERFACEDLLOBJS=\
testinterface.obj \
#  commented out because of bigtime Microsoft compiler problems.  i'm compiling
#    all of the code into one .obj, because it can't handle the code being contained
#    in more than one file.  #include "fdrinterface.cpp" is ugly, but the only way
#    around it for now.
# $(FDRINTERFACEOBJS) \


ALL:            cti_fdr.dll

cti_fdr.dll: $(FDRINTERFACEOBJS) Makedll.mak
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(FDRINTERFACEOBJS) $(INCLPATHS) $(RWLIBS) $(CTIFDRLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\cti_fdr.dll copy bin\cti_fdr.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\cti_fdr.lib copy bin\*.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp *.map


deps:
                scandeps -Output makedll.mak *.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRBASE -DWINDOWS -Fo$(OBJ)\ -c $<


#UPDATE#
fdr.obj:	dlldefs.h ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h cparms.h \
		CServiceConfig.h fdrservice.h cservice.h id_ctibase.h \
		utility.h id_build.h
fdracs.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdracs.h
fdrasciiimportbase.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h fdrasciiimportbase.h \
		fdrinterface.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
fdrclientconnection.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h fdrinterface.h message.h collectable.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h cparms.h \
		fdrdebuglevel.h fdrpointlist.h queues.h fdrsocketlayer.h \
		device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h
fdrcygnet.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h fdrcygnet.h \
		fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		queues.h device.h devicetypes.h
fdrdestination.obj:	fdrdestination.h dlldefs.h fdr.h pointdefs.h
fdrdsm2import.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrdsm2import.h fdrasciiimportbase.h fdrinterface.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h queues.h
fdrftpinterface.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h fdrftpinterface.h \
		fdrinterface.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
fdrinet.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h fdrinet.h
fdrinterface.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h cparms.h dbaccess.h \
		sema.h ctinexus.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_dbchg.h fdrinterface.h \
		connection.h exchange.h logger.h thread.h msg_reg.h queue.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h queues.h
fdrio.obj:	fdrio.h
fdriosocket.obj:	fdriosocket.h fdrio.h
fdrlodestarimport.obj:	cparms.h dlldefs.h msg_cmd.h message.h \
		ctidbgmem.h collectable.h pointtypes.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h TextFileInterfaceParts.h \
		fdrlodestarimport.h
fdrpoint.obj:	fdrpoint.h dlldefs.h pointtypes.h fdrdestination.h fdr.h \
		pointdefs.h types.h logger.h thread.h mutex.h guard.h
fdrpointidmap.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h fdrpointidmap.h \
		pointtypes.h resolvers.h yukon.h ctidbgmem.h \
		db_entry_defines.h logger.h thread.h
fdrpointlist.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h \
		dsm2.h mutex.h guard.h logger.h thread.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		fdrpointlist.h queues.h mgr_fdrpoint.h rtdb.h hashkey.h
fdrprotectedmaplist.obj:	dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		fdrprotectedmaplist.h queues.h
fdrrccs.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h fdrinet.h fdrrccs.h
fdrrdex.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h fdrrdex.h
fdrserverconnection.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h fdrinterface.h message.h collectable.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h cparms.h \
		fdrdebuglevel.h fdrpointlist.h queues.h fdrsocketlayer.h \
		device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h
fdrservice.obj:	cparms.h dlldefs.h logger.h thread.h mutex.h guard.h \
		fdrservice.h cservice.h
fdrsinglesocket.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h device.h \
		devicetypes.h fdrsocketconnection.h fdrserverconnection.h \
		fdrsinglesocket.h
fdrsocketconnection.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		fdrsocketlayer.h queues.h types.h device.h devicetypes.h \
		fdrsocketconnection.h
fdrsocketinterface.obj:	dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrpointlist.h queues.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		fdrsocketconnection.h fdrsocketinterface.h fdrinterface.h \
		message.h ctidbgmem.h collectable.h connection.h exchange.h \
		msg_multi.h msg_pdata.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h fdrdebuglevel.h
fdrsocketlayer.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrinterface.h message.h collectable.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h cparms.h \
		fdrdebuglevel.h fdrpointlist.h queues.h fdrsocketinterface.h \
		fdrclientconnection.h fdrsocketlayer.h device.h devicetypes.h \
		fdrsocketconnection.h fdrserverconnection.h
fdrstec.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h fdrstec.h \
		fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
fdrtelegyr.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h dbaccess.h \
		sema.h hashkey.h resolvers.h db_entry_defines.h fdr.h \
		fdrdebuglevel.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		fdrpoint.h fdrdestination.h fdrpointlist.h queues.h \
		fdrtelegyr.h fdrasciiimportbase.h device.h devicetypes.h \
		telegyrgroup.h telegyrcontrolcenter.h
fdrtextexport.obj:	cparms.h dlldefs.h msg_cmd.h message.h ctidbgmem.h \
		collectable.h pointtypes.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h TextFileInterfaceParts.h \
		fdrtextexport.h
fdrtextfilebase.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h fdrtextfilebase.h \
		fdrinterface.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h TextFileInterfaceParts.h
fdrtextimport.obj:	cparms.h dlldefs.h msg_cmd.h message.h ctidbgmem.h \
		collectable.h pointtypes.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h TextFileInterfaceParts.h \
		fdrtextimport.h
fdrtristate.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrtristate.h fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h
fdrvalmet.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrvalmet.h
inetinterface.obj:	dlldefs.h socketinterface.h fdrinterface.h \
		message.h ctidbgmem.h collectable.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h cparms.h fdrdebuglevel.h \
		fdrpointlist.h queues.h inetinterface.h fdriosocket.h fdrio.h
mgr_fdrpoint.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h hashkey.h \
		resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h fdr.h pointdefs.h fdrdebuglevel.h \
		mgr_fdrpoint.h rtdb.h fdrpoint.h fdrdestination.h logger.h \
		thread.h utility.h
socketinterface.obj:	socketinterface.h dlldefs.h fdrinterface.h \
		message.h ctidbgmem.h collectable.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h cparms.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
telegyrcontrolcenter.obj:	telegyrcontrolcenter.h dlldefs.h fdr.h \
		pointdefs.h telegyrgroup.h fdrpoint.h pointtypes.h \
		fdrdestination.h types.h mutex.h logger.h thread.h guard.h
telegyrgroup.obj:	telegyrgroup.h dlldefs.h fdr.h pointdefs.h \
		fdrpoint.h pointtypes.h fdrdestination.h types.h
testinterface.obj:	dlldefs.h fdrinterface.h message.h ctidbgmem.h \
		collectable.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h cparms.h \
		fdrdebuglevel.h fdrpointlist.h queues.h testinterface.h \
		fdrinterface.cpp dbaccess.h sema.h ctinexus.h resolvers.h \
		db_entry_defines.h msg_cmd.h msg_dbchg.h
textfileinterfaceparts.obj:	cparms.h dlldefs.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h ctidbgmem.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h logger.h thread.h \
		TextFileInterfaceParts.h
#ENDUPDATE#
