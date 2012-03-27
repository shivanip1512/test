# nmake file YUKON 1.0 FDR Valmet Interface

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

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
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cti_fdr.lib \




ALL:   fdrvalmet.dll

fdrvalmet.dll: fdrvalmet.obj fdrvalmet.mak
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrvalmet.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\fdrvalmet.dll copy bin\fdrvalmet.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\fdrvalmet.lib copy bin\fdrvalmet.lib $(COMPILEBASE)\lib


clean:
                -del fdrvalmet.obj fdrvalmett.dll fdrvalmet.ilk fdrvalmet.pdb fdrvalmet.lib fdrvalmet.exp


deps:
                scandeps -Output fdrvalmet.mak fdrvalmet.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRVALMET -DWINDOWS -Fo$(OBJ)\ -c $<

#UPDATE#
fdrvalmet.obj:  cparms.h dlldefs.h msg_multi.h collectable.h \
                msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
                yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
                mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
                exchange.h logger.h thread.h queue.h pointtypes.h \
                fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
                rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
                fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
                fdrsocketlayer.h devicetypes.h fdrsocketconnection.h \
                fdrvalmet.h
#ENDUPDATE#



