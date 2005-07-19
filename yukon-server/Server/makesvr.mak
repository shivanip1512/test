# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

#
# ALWAYS place the local drives/direcories before the reference ones.
#
INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(DISPATCH)\include \
-I$(MSG)\include \
-I$(RW) \
-I$(BOOST) \

.PATH.cpp = .;$(R_SERVER)

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
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
;$(RW)



SVRLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\cmdline.lib \


SERVEROBJS=\
con_mgr.obj \
exe_cmd.obj \
exe_reg.obj \
executor.obj \
executorfactory.obj \
server_b.obj

ALL:            ctisvr.dll


ctisvr.dll:     $(SERVEROBJS) makesvr.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo Building  $@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(SERVEROBJS) id_svr.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(SVRLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\ctisvr.dll copy bin\ctisvr.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\ctisvr.lib copy bin\ctisvr.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp


deps:
                scandeps -Output makesvr.mak *.cpp

# The lines below accomplish the ID'ing of the project!
id:
            @cid .\include\id_svr.h id_vinfo.h
            @$(MAKE) -nologo -f $(_InputFile) id_svr.obj

id_svr.obj:    id_svr.cpp include\id_svr.h id_vinfo.h


.cpp.obj :
                @echo:
                @echo Compiling: $< Output: $@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /DCTISVR $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

#######
#UPDATE#
con_mgr.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h collectable.h \
		con_mgr.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		message.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h ctibase.h ctinexus.h netports.h
ctique.obj:	yukon.h precompiled.h ctidbgmem.h
dlldbmemmgr.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h utility.h \
		dsm2.h mutex.h guard.h
executor.obj:	yukon.h precompiled.h ctidbgmem.h executor.h message.h \
		collectable.h dlldefs.h con_mgr.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h netports.h
executorfactory.obj:	yukon.h precompiled.h ctidbgmem.h \
		executorfactory.h collectable.h message.h dlldefs.h \
		executor.h exe_cmd.h exe_reg.h
exe_cmd.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h con_mgr.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h netports.h server_b.h cmdopts.h critical_Section.h \
		msg_cmd.h exe_cmd.h executor.h
exe_reg.obj:	yukon.h precompiled.h ctidbgmem.h message.h collectable.h \
		dlldefs.h exe_reg.h executor.h con_mgr.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		ctibase.h ctinexus.h netports.h con_mgr_vg.h vgexe_factory.h \
		exe_ptchg.h executorfactory.h exe_cmd.h msg_cmd.h server_b.h \
		cmdopts.h critical_Section.h
id_svr.obj:	yukon.h precompiled.h ctidbgmem.h utility.h dsm2.h mutex.h \
		dlldefs.h guard.h id_svr.h id_build.h id_vinfo.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
server_b.obj:	yukon.h precompiled.h ctidbgmem.h executor.h message.h \
		collectable.h dlldefs.h server_b.h con_mgr.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h msg_ptreg.h msg_reg.h queue.h \
		ctibase.h ctinexus.h netports.h cmdopts.h critical_Section.h \
		msg_cmd.h numstr.h utility.h
#ENDUPDATE#
