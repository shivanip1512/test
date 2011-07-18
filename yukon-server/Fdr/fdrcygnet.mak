# nmake file YUKON 1.0 FDR CYGNET Interface

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
$(COMPILEBASE)\lib\dclnd.lib \




ALL:   fdrcygnet.dll

fdrcygnet.dll: fdrcygnet.obj fdrcygnet.mak
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\cygnet\dclnd.lib copy ..\cygnet\dclnd.lib $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrcygnet.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)


dclnd.lib:
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist cygnet\dclnd.lib copy cygnet\dclnd.lib $(COMPILEBASE)\lib

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\fdrcygnet.dll copy bin\fdrcygnet.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\fdrcygnet.lib copy bin\fdrcygnet.lib $(COMPILEBASE)\lib


clean:
                -del fdrcygnet.obj fdrcygnet.dll fdrcygnet.ilk fdrcygnet.pdb fdrcygnet.lib fdrcygnet.exp


deps:
                scandeps -Output fdrcygnet.mak fdrcygnet.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRCYGNET -DWINDOWS -Fo$(OBJ)\ -c $<

#UPDATE#
fdrcygnet.obj:  cparms.h dlldefs.h msg_multi.h collectable.h \
                msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
                yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
                mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
                exchange.h logger.h thread.h queue.h pointtypes.h fdrcygnet.h \
                fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
                fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
                queues.h devicetypes.h
#ENDUPDATE#
