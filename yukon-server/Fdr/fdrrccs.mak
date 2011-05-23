# nmake file YUKON 1.0 FDR RCCS Interface
# note:  the fdrinet.lib is linked to this interface because it inherits from it

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
-I$(RW) \


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



FDRINTERFACEOBJS=\


CTIFDRLIBS=\
$(COMPILEBASE)\lib\cparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cti_fdr.lib \
$(COMPILEBASE)\lib\fdrinet.lib




ALL:   fdrrccs.dll

fdrrccs.dll: fdrrccs.obj fdrrccs.mak
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrrccs.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS)  /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\fdrrccs.dll copy bin\fdrrccs.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\fdrrccs.lib copy bin\fdrrccs.lib $(COMPILEBASE)\lib


clean:
                -del fdrrccs.obj fdrrccs.dll fdrrccs.ilk fdrrccs.pdb fdrrccs.lib fdrrccs.exp


deps:
                scandeps -Output fdrrccs.mak fdrrccs.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRRCCS -DWINDOWS -Fo$(OBJ)\ -c $<

#UPDATE#
fdr.obj:        dlldefs.h ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h \
                cticalls.h dsm2.h mutex.h guard.h logger.h thread.h cparms.h \
                CServiceConfig.h fdrservice.h cservice.h
fdracs.obj:     cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
                pointdefs.h message.h msg_signal.h yukon.h dllbase.h \
                os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
                msg_ptreg.h msg_cmd.h msg_reg.h connection.h exchange.h \
                queue.h pointtypes.h logger.h thread.h fdrsocketinterface.h \
                queues.h fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h \
                fdrpoint.h fdrdestination.h fdrdebuglevel.h \
                fdrprotectedmaplist.h fdrpointidmap.h fdrsinglesocket.h \
                fdrsocketlayer.h fdrsocketconnection.h devicetypes.h \
                fdracs.h
fdrclientconnection.obj:        yukon.h dlldefs.h dllbase.h os2_2w32.h \
                types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
                fdrinterface.h message.h collectable.h connection.h \
                exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
                msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
                fdrpoint.h fdrdestination.h cparms.h fdrdebuglevel.h \
                fdrprotectedmaplist.h queues.h fdrpointidmap.h \
                fdrsocketlayer.h fdrsocketconnection.h devicetypes.h \
                fdrclientconnection.h
fdrcygnet.obj:  cparms.h dlldefs.h msg_multi.h collectable.h \
                msg_pdata.h pointdefs.h message.h msg_signal.h yukon.h \
                dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
                guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
                exchange.h queue.h pointtypes.h logger.h thread.h fdrcygnet.h \
                fdrpointidmap.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
                hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
                fdrprotectedmaplist.h queues.h devicetypes.h
fdrdestination.obj:     fdrdestination.h dlldefs.h
fdrftpinterface.obj:    cparms.h dlldefs.h msg_multi.h collectable.h \
                msg_pdata.h pointdefs.h message.h msg_signal.h yukon.h \
                dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
                guard.h logger.h thread.h fdrftpinterface.h fdrinterface.h \
                connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
                mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h fdrdestination.h \
                fdrdebuglevel.h fdrprotectedmaplist.h queues.h \
                fdrpointidmap.h
fdrinet.obj:    cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
                pointdefs.h message.h msg_signal.h yukon.h dllbase.h \
                os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
                msg_ptreg.h msg_cmd.h msg_reg.h connection.h exchange.h \
                queue.h pointtypes.h logger.h thread.h fdrsocketinterface.h \
                queues.h fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h \
                fdrpoint.h fdrdestination.h fdrdebuglevel.h \
                fdrprotectedmaplist.h fdrpointidmap.h fdrserverconnection.h \
                fdrsocketconnection.h fdrclientconnection.h fdrsocketlayer.h \
                devicetypes.h fdrinet.h
fdrinterface.obj:       yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
                cticalls.h dsm2.h mutex.h guard.h cparms.h ctinexus.h \
                msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
                msg_signal.h msg_ptreg.h msg_cmd.h msg_dbchg.h fdrinterface.h \
                connection.h exchange.h msg_reg.h queue.h mgr_fdrpoint.h \
                rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
                fdrprotectedmaplist.h queues.h fdrpointidmap.h logger.h \
                thread.h
fdrio.obj:      fdrio.h
fdriosocket.obj:        fdriosocket.h fdrio.h
fdrpoint.obj:   fdrpoint.h dlldefs.h fdrdestination.h
fdrpointidmap.obj:      dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
                cticalls.h dsm2.h mutex.h guard.h fdrpointidmap.h logger.h \
                thread.h
fdrprotectedmaplist.obj:        dllbase.h os2_2w32.h dlldefs.h types.h \
                cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
                fdrpointidmap.h fdrprotectedmaplist.h queues.h
fdrrccs.obj:    cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
                pointdefs.h message.h msg_signal.h yukon.h dllbase.h \
                os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
                msg_ptreg.h msg_cmd.h msg_reg.h connection.h exchange.h \
                queue.h pointtypes.h logger.h thread.h fdrsocketinterface.h \
                queues.h fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h \
                fdrpoint.h fdrdestination.h fdrdebuglevel.h \
                fdrprotectedmaplist.h fdrpointidmap.h fdrserverconnection.h \
                fdrsocketconnection.h fdrclientconnection.h fdrsocketlayer.h \
                devicetypes.h fdrinet.h fdrrccs.h
fdrserverconnection.obj:        yukon.h dlldefs.h dllbase.h os2_2w32.h \
                types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
                fdrinterface.h message.h collectable.h connection.h \
                exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
                msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
                fdrpoint.h fdrdestination.h cparms.h fdrdebuglevel.h \
                fdrprotectedmaplist.h queues.h fdrpointidmap.h \
                fdrsocketlayer.h fdrsocketconnection.h devicetypes.h \
                fdrserverconnection.h
fdrservice.obj: cparms.h dlldefs.h logger.h thread.h mutex.h guard.h \
                fdrservice.h cservice.h
fdrsinglesocket.obj:    cparms.h dlldefs.h msg_multi.h collectable.h \
                msg_pdata.h pointdefs.h message.h msg_signal.h yukon.h \
                dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
                guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
                exchange.h queue.h pointtypes.h logger.h thread.h \
                fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
                rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
                fdrprotectedmaplist.h fdrpointidmap.h fdrsocketlayer.h \
                fdrsocketconnection.h devicetypes.h \
                fdrsinglesocket.h
fdrsocketconnection.obj:        logger.h thread.h mutex.h dlldefs.h guard.h \
                fdrsocketconnection.h queues.h types.h
fdrsocketinterface.obj: dllbase.h os2_2w32.h dlldefs.h types.h \
                cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
                fdrprotectedmaplist.h queues.h fdrpointidmap.h \
                fdrsocketinterface.h fdrinterface.h message.h collectable.h \
                connection.h exchange.h msg_multi.h msg_pdata.h pointdefs.h \
                msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
                mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h fdrdestination.h \
                cparms.h fdrdebuglevel.h
fdrsocketlayer.obj:     yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
                cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
                fdrinterface.h message.h collectable.h connection.h \
                exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
                msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
                fdrpoint.h fdrdestination.h cparms.h fdrdebuglevel.h \
                fdrprotectedmaplist.h queues.h fdrpointidmap.h \
                fdrsocketinterface.h fdrclientconnection.h \
                fdrsocketconnection.h fdrserverconnection.h fdrsocketlayer.h \
                devicetypes.h
fdrstec.obj:    cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
                pointdefs.h message.h msg_signal.h yukon.h dllbase.h \
                os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
                msg_ptreg.h msg_cmd.h msg_reg.h connection.h exchange.h \
                queue.h pointtypes.h logger.h thread.h fdrstec.h \
                fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
                hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
                fdrprotectedmaplist.h queues.h fdrpointidmap.h
fdrtristate.obj:        cparms.h dlldefs.h msg_multi.h collectable.h \
                msg_pdata.h pointdefs.h message.h msg_signal.h yukon.h \
                dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
                guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
                exchange.h queue.h pointtypes.h logger.h thread.h \
                fdrtristate.h fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h \
                rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
                fdrprotectedmaplist.h queues.h fdrpointidmap.h
fdrvalmet.obj:  cparms.h dlldefs.h msg_multi.h collectable.h \
                msg_pdata.h pointdefs.h message.h msg_signal.h yukon.h \
                dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
                guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
                exchange.h queue.h pointtypes.h logger.h thread.h \
                fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
                rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdrdebuglevel.h \
                fdrprotectedmaplist.h fdrpointidmap.h fdrsinglesocket.h \
                fdrsocketlayer.h fdrsocketconnection.h devicetypes.h \
                fdrvalmet.h
inetinterface.obj:      dlldefs.h socketinterface.h fdrinterface.h \
                message.h collectable.h connection.h exchange.h dllbase.h \
                os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
                msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
                msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
                fdrpoint.h fdrdestination.h cparms.h fdrdebuglevel.h \
                fdrprotectedmaplist.h queues.h fdrpointidmap.h \
                inetinterface.h fdriosocket.h fdrio.h
mgr_fdrpoint.obj:       dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
                cticalls.h dsm2.h mutex.h guard.h hashkey.h resolvers.h \
                pointtypes.h yukon.h mgr_fdrpoint.h rtdb.h fdrpoint.h \
                fdrdestination.h logger.h thread.h
socketinterface.obj:    socketinterface.h dlldefs.h fdrinterface.h \
                message.h collectable.h connection.h exchange.h dllbase.h \
                os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
                msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
                msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
                fdrpoint.h fdrdestination.h cparms.h fdrdebuglevel.h \
                fdrprotectedmaplist.h queues.h fdrpointidmap.h
#ENDUPDATE#




