# nmake file YUKON 1.0 FDR dsm2 import Interface

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



FDRINTERFACEOBJS=\


CTIFDRLIBS=\
$(COMPILEBASE)\lib\cparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cti_fdr.lib \
$(COMPILEBASE)\lib\proclog.lib \
$(COMPILEBASE)\lib\cmdline.lib




ALL:   fdrdsm2filein.dll

fdrdsm2filein.dll: fdrdsm2filein.obj fdrdsm2filein.mak
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrdsm2filein.obj $(INCLPATHS) $(RWLIBS) $(CTIFDRLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\fdrdsm2filein.dll copy bin\fdrdsm2filein.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\fdrdsm2filein.lib copy bin\fdrdsm2filein.lib $(COMPILEBASE)\lib


clean:
                -del fdrdsm2filein.obj fdrdsm2filein.dll fdrdsm2filein.ilk fdrdsm2filein.pdb fdrdsm2filein.lib fdrdsm2filein.exp


deps:
                scandeps -Output fdrdsm2filein.mak fdrdsm2filein.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRDSM2FILEIN -DWINDOWS -Fo$(OBJ)\ -c $<

#UPDATE#
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
#ENDUPDATE#


