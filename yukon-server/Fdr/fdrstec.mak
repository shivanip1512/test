# nmake file YUKON 1.0 FDR STEC Interface

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




ALL:   fdrstec.dll

fdrstec.dll: fdrstec.obj fdrstec.mak
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrstec.obj $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(CTIFDRLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\fdrstec.dll copy bin\fdrstec.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\fdrstec.lib copy bin\fdrstec.lib $(COMPILEBASE)\lib


clean:
                -del fdrstec.obj fdrstec.dll fdrstec.ilk fdrstec.pdb fdrstec.lib fdrstec.exp


deps:
                scandeps -Output fdrstec.mak fdrstec.cpp



.cpp.obj :
                @echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRSTEC -DWINDOWS -Fo$(OBJ)\ -c $<

#UPDATE#
fdrstec.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h fdrstec.h \
		fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
#ENDUPDATE#




