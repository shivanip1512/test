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
               $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
#UPDATE#
clientconn.obj:	yukon.h precompiled.h ctidbgmem.h clientconn.h \
		ctitime.h dlldefs.h mc.h logger.h thread.h mutex.h guard.h \
		observe.h types.h ctibase.h ctinexus.h netports.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h
clistener.obj:	yukon.h precompiled.h ctidbgmem.h clistener.h mc.h \
		logger.h dlldefs.h thread.h mutex.h guard.h ctitime.h \
		clientconn.h observe.h types.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h rwutil.h boost_time.h \
		queue.h dllbase.h os2_2w32.h cticalls.h dsm2.h
mc_dbthr.obj:	yukon.h precompiled.h ctidbgmem.h mc_dbthr.h mc.h \
		logger.h dlldefs.h thread.h mutex.h guard.h ctitime.h \
		mgr_mcsched.h rtdb.h hashkey.h hash_functions.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mc_sched.h dbmemobject.h \
		tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h message.h \
		collectable.h rwutil.h boost_time.h
mc_fileint.obj:	yukon.h precompiled.h ctidbgmem.h mc_fileint.h \
		fileint.h dlldefs.h queue.h logger.h thread.h mutex.h guard.h \
		ctitime.h mgr_mcsched.h mc.h rtdb.h hashkey.h \
		hash_functions.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mc_sched.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h message.h collectable.h rwutil.h \
		boost_time.h mc_msg.h ctibase.h ctinexus.h netports.h \
		ctidate.h
mc_main.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		CServiceConfig.h mc_svc.h cservice.h mc_server.h ctidate.h \
		logger.h thread.h mutex.h guard.h mc.h CParms.h rwutil.h \
		boost_time.h message.h collectable.h queue.h mgr_mcsched.h \
		rtdb.h hashkey.h hash_functions.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mc_sched.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h mc_dbthr.h mccmd.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h ctdpcptrq.h clistener.h clientconn.h observe.h \
		mc_msg.h mc_script.h mc_scheduler.h mgr_holiday.h \
		mc_fileint.h fileint.h ctibase.h ctinexus.h netports.h
mc_msg.obj:	yukon.h precompiled.h ctidbgmem.h mc_msg.h message.h \
		collectable.h dlldefs.h rwutil.h ctitime.h boost_time.h \
		mc_sched.h mc.h logger.h thread.h mutex.h guard.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h
mc_sched.obj:	yukon.h precompiled.h ctidbgmem.h mc_sched.h ctitime.h \
		dlldefs.h mc.h logger.h thread.h mutex.h guard.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h \
		message.h collectable.h rwutil.h boost_time.h ctidate.h
mc_scheduler.obj:	yukon.h precompiled.h ctidbgmem.h mc_scheduler.h \
		ctitime.h dlldefs.h ctidate.h logger.h thread.h mutex.h \
		guard.h mc.h mc_sched.h dbmemobject.h tbl_pao.h tbl_mcsched.h \
		tbl_mcsimpsched.h message.h collectable.h rwutil.h \
		boost_time.h mgr_mcsched.h rtdb.h hashkey.h hash_functions.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mgr_holiday.h
mc_script.obj:	yukon.h precompiled.h ctidbgmem.h mc_script.h mc.h \
		logger.h dlldefs.h thread.h mutex.h guard.h ctitime.h \
		message.h collectable.h rwutil.h boost_time.h
mc_server.obj:	yukon.h precompiled.h ctidbgmem.h mc_server.h ctidate.h \
		dlldefs.h logger.h thread.h mutex.h guard.h ctitime.h mc.h \
		CParms.h rwutil.h boost_time.h message.h collectable.h \
		queue.h mgr_mcsched.h rtdb.h hashkey.h hash_functions.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mc_sched.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h \
		mc_dbthr.h mccmd.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h ctdpcptrq.h clistener.h clientconn.h \
		observe.h mc_msg.h mc_script.h mc_scheduler.h mgr_holiday.h \
		mc_fileint.h fileint.h numstr.h
mc_svc.obj:	yukon.h precompiled.h ctidbgmem.h mc_svc.h cservice.h \
		dlldefs.h mc_server.h ctidate.h logger.h thread.h mutex.h \
		guard.h ctitime.h mc.h CParms.h rwutil.h boost_time.h \
		message.h collectable.h queue.h mgr_mcsched.h rtdb.h \
		hashkey.h hash_functions.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mc_sched.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h mc_dbthr.h mccmd.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		pointdefs.h ctdpcptrq.h clistener.h clientconn.h observe.h \
		mc_msg.h mc_script.h mc_scheduler.h mgr_holiday.h \
		mc_fileint.h fileint.h
mgr_mcsched.obj:	yukon.h precompiled.h ctidbgmem.h mgr_mcsched.h mc.h \
		logger.h dlldefs.h thread.h mutex.h guard.h ctitime.h rtdb.h \
		hashkey.h hash_functions.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mc_sched.h dbmemobject.h tbl_pao.h \
		tbl_mcsched.h tbl_mcsimpsched.h message.h collectable.h \
		rwutil.h boost_time.h dbaccess.h sema.h utility.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
tbl_mcsched.obj:	yukon.h precompiled.h ctidbgmem.h tbl_mcsched.h \
		mutex.h dlldefs.h guard.h logger.h thread.h ctitime.h \
		dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		sema.h rwutil.h boost_time.h
tbl_mcsimpsched.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_mcsimpsched.h mutex.h dlldefs.h guard.h logger.h thread.h \
		ctitime.h dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h sema.h rwutil.h boost_time.h
#ENDUPDATE#
