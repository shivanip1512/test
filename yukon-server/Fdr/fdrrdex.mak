# nmake file YUKON 1.0 FDR Rdex Interface

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
$(COMPILEBASE)\lib\cparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cti_fdr.lib \




ALL:   fdrrdex.dll

fdrrdex.dll: fdrrdex.obj fdrrdex.mak
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrrdex.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\fdrrdex.dll copy bin\fdrrdex.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\fdrrdex.lib copy bin\fdrrdex.lib $(COMPILEBASE)\lib


clean:
                -del fdrrdex.obj fdrrdext.dll fdrrdex.ilk fdrrdex.pdb fdrrdex.lib fdrrdex.exp


deps:
                scandeps -Output fdrrdex.mak fdrrdex.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRRDEX -DWINDOWS -Fo$(OBJ)\ -c $<

#UPDATE#
fdrrdex.obj:    cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
                pointdefs.h message.h msg_signal.h yukon.h dllbase.h \
                os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
                msg_ptreg.h msg_cmd.h msg_reg.h connection.h exchange.h \
                queue.h pointtypes.h logger.h thread.h fdrsocketinterface.h \
                queues.h fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h \
                fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
                fdrpointlist.h fdrsinglesocket.h fdrsocketlayer.h \
                devicetypes.h fdrsocketconnection.h fdrrdex.h
#ENDUPDATE#


