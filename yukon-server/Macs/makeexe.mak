# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(MACS)\include \
-I$(COMMON)\include \
-I$(MCCMD)\include \
-I$(PROCLOG)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(MSG)\include \
-I$(INTERP)\include \
-I$(TCLINC) \
-I$(RW) \
-I$(BOOST)



.PATH.cpp = .;$(R_MACS)
.PATH.H = \
.\include \
;$(COMMON)\include \
;$(MACS)\include \
;$(MCCMD)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROTOCOL)\include \
;$(PROCLOG)\include \
;$(RTDB)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(DATABASE)\include \
;$(RW)



LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\$(TCL_LIB).lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\proclog.lib \
$(COMPILEBASE)\lib\mccmd.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\interp.lib


BASEOBJS= \
clientconn.obj \
clistener.obj \
mc_dbthr.obj \
mc_fileint.obj \
mc_main.obj \
mc_msg.obj \
mc_sched.obj \
mc_scheduler.obj \
mc_script.obj \
mc_server.obj \
mc_svc.obj \
mgr_mcsched.obj \
tbl_mcsched.obj \
tbl_mcsimpsched.obj

TARGS = macs.exe

ALL:          $(TARGS)

macs.exe:     $(BASEOBJS) Makefile
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOSTLIBS) 
              @echo:
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
              -if exist ..\tcl\*.* copy ..\tcl\*.* $(YUKONOUTPUT)
              @%cd $(CWD)
              @echo Done building Target ..\$@
              @echo:

copy:       $(TARGS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)
           -if exist tcl\*.* copy tcl\*.* $(YUKONOUTPUT)


deps:
                scandeps -Output makeexe.mak *.cpp


clean:
        -del \
*.pdb \
$(OBJ)\*.obj \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.exe


allclean:   clean all


.SUFFIXES:      .cpp .obj

.cpp.obj:
               @echo:
               @echo Compiling: $<
               @echo C-Options: $(CFLAGS)
               @echo Output   : ..\$@
               @echo:
               $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
#UPDATE#
clientconn.obj:	clientconn.h mc.h logger.h thread.h mutex.h dlldefs.h \
		guard.h observe.h types.h ctibase.h ctinexus.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h
clistener.obj:	clistener.h mc.h logger.h thread.h mutex.h dlldefs.h \
		guard.h clientconn.h observe.h types.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h ctidbgmem.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		queue.h
mc_dbthr.obj:	mc_dbthr.h mc.h logger.h thread.h mutex.h dlldefs.h \
		guard.h mgr_mcsched.h rtdb.h hashkey.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mc_sched.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h message.h ctidbgmem.h \
		collectable.h
mc_fileint.obj:	mc_fileint.h fileint.h dlldefs.h queue.h logger.h \
		thread.h mutex.h guard.h mgr_mcsched.h mc.h rtdb.h hashkey.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mc_sched.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h \
		message.h ctidbgmem.h collectable.h mc_msg.h ctibase.h \
		ctinexus.h
mc_main.obj:	CServiceConfig.h dlldefs.h mc_svc.h cservice.h \
		mc_server.h mc.h logger.h thread.h mutex.h guard.h CParms.h \
		message.h ctidbgmem.h collectable.h queue.h mgr_mcsched.h \
		rtdb.h hashkey.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mc_sched.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h mc_dbthr.h mccmd.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h ctdpcptrq.h clistener.h clientconn.h \
		observe.h mc_msg.h mc_script.h mc_scheduler.h mgr_holiday.h \
		mc_fileint.h fileint.h ctibase.h ctinexus.h
mc_msg.obj:	mc_msg.h message.h ctidbgmem.h collectable.h dlldefs.h \
		mc_sched.h mc.h logger.h thread.h mutex.h guard.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h
mc_sched.obj:	mc_sched.h mc.h logger.h thread.h mutex.h dlldefs.h \
		guard.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h message.h ctidbgmem.h collectable.h
mc_scheduler.obj:	mc_scheduler.h mc.h logger.h thread.h mutex.h \
		dlldefs.h guard.h mc_sched.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h message.h ctidbgmem.h \
		collectable.h mgr_mcsched.h rtdb.h hashkey.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mgr_holiday.h
mc_script.obj:	mc_script.h mc.h logger.h thread.h mutex.h dlldefs.h \
		guard.h message.h ctidbgmem.h collectable.h
mc_server.obj:	mc_server.h mc.h logger.h thread.h mutex.h dlldefs.h \
		guard.h CParms.h message.h ctidbgmem.h collectable.h queue.h \
		mgr_mcsched.h rtdb.h hashkey.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mc_sched.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h mc_dbthr.h mccmd.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h ctdpcptrq.h clistener.h \
		clientconn.h observe.h mc_msg.h mc_script.h mc_scheduler.h \
		mgr_holiday.h mc_fileint.h fileint.h numstr.h
mc_svc.obj:	mc_svc.h cservice.h dlldefs.h mc_server.h mc.h logger.h \
		thread.h mutex.h guard.h CParms.h message.h ctidbgmem.h \
		collectable.h queue.h mgr_mcsched.h rtdb.h hashkey.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mc_sched.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h \
		mc_dbthr.h mccmd.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h ctdpcptrq.h \
		clistener.h clientconn.h observe.h mc_msg.h mc_script.h \
		mc_scheduler.h mgr_holiday.h mc_fileint.h fileint.h
mgr_mcsched.obj:	mgr_mcsched.h mc.h logger.h thread.h mutex.h \
		dlldefs.h guard.h rtdb.h hashkey.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mc_sched.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h message.h ctidbgmem.h \
		collectable.h dbaccess.h sema.h utility.h
tbl_mcsched.obj:	tbl_mcsched.h mutex.h dlldefs.h guard.h logger.h \
		thread.h dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h sema.h
tbl_mcsimpsched.obj:	tbl_mcsimpsched.h mutex.h dlldefs.h guard.h \
		logger.h thread.h dbaccess.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h sema.h
#ENDUPDATE#
