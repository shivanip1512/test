# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(CAPCONTROL)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(PROCLOG)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(SERVER)\include \
-I$(RW)


.PATH.cpp = .
.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CAPCONTROL)\include \
;$(CPARMS)\include \
;$(DATABASE)\include \
;$(SERVICE)\include \
;$(RTDB)\include \
;$(SERVER)\include \
;$(PROCLOG)\include \
;$(MSG)\include \
;$(RW)



LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib

BASEOBJS= \
capcontroller.obj \
cccapbank.obj \
ccclientconn.obj \
ccclientlistener.obj \
ccexecutor.obj \
ccfeeder.obj \
ccmain.obj \
ccmessage.obj \
ccservice.obj \
ccstate.obj \
ccsubstationbus.obj \
ccsubstationbusstore.obj

TARGS = capcontrol.exe

ALL:          $(TARGS)

capcontrol.exe:     $(BASEOBJS) Makefile
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS)
              @echo:
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
              @%cd $(CWD)
              @echo Done building Target ..\$@
              @echo:

copy:       $(TARGS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)

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
capcontroller.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h connection.h \
		exchange.h logger.h thread.h message.h ctidbgmem.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		ccmessage.h ccsubstationbus.h observe.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccstate.h msg_cmd.h \
		msg_pcreturn.h msg_dbchg.h pointtypes.h configparms.h \
		cparms.h capcontroller.h ccsubstationbusstore.h ccid.h \
		ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h netports.h \
		devicetypes.h resolvers.h db_entry_defines.h
cccapbank.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h cccapbank.h \
		observe.h ccid.h pointdefs.h device.h devicetypes.h logger.h \
		thread.h resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h
ccclientconn.obj:	ccclientconn.h ctdpcptrq.h dlldefs.h observe.h \
		types.h ccmessage.h message.h ctidbgmem.h collectable.h \
		ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h sema.h connection.h exchange.h \
		logger.h thread.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccstate.h ccexecutor.h \
		ccsubstationbusstore.h ccid.h ctibase.h ctinexus.h
ccclientlistener.obj:	ccclientlistener.h ccclientconn.h ctdpcptrq.h \
		dlldefs.h observe.h types.h ccstate.h connection.h exchange.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		logger.h thread.h message.h ctidbgmem.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ccmessage.h ccsubstationbus.h \
		dbaccess.h sema.h ccfeeder.h cccapbank.h msg_pcrequest.h \
		ccsubstationbusstore.h ccid.h configparms.h cparms.h \
		ctibase.h ctinexus.h ccexecutor.h
ccexecutor.obj:	ccclientlistener.h ccclientconn.h ctdpcptrq.h \
		dlldefs.h observe.h types.h ccstate.h connection.h exchange.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		logger.h thread.h message.h ctidbgmem.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ccexecutor.h ccmessage.h \
		ccsubstationbus.h dbaccess.h sema.h ccfeeder.h cccapbank.h \
		msg_pcrequest.h ccsubstationbusstore.h ccid.h capcontroller.h \
		msg_cmd.h pointtypes.h configparms.h cparms.h ctibase.h \
		ctinexus.h
ccfeeder.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h ccsubstationbus.h \
		connection.h exchange.h logger.h thread.h message.h \
		ctidbgmem.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h observe.h \
		ccfeeder.h cccapbank.h msg_pcrequest.h ccid.h pointtypes.h \
		capcontroller.h msg_cmd.h configparms.h cparms.h \
		ccsubstationbusstore.h ccexecutor.h ccmessage.h ccstate.h \
		ctdpcptrq.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h
ccmain.obj:	ccsubstationbusstore.h observe.h types.h dlldefs.h \
		ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h sema.h connection.h exchange.h \
		logger.h thread.h message.h ctidbgmem.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ccfeeder.h cccapbank.h \
		msg_pcrequest.h ccid.h capcontroller.h msg_cmd.h pointtypes.h \
		configparms.h cparms.h ccexecutor.h ccmessage.h ccstate.h \
		ctdpcptrq.h ctibase.h ctinexus.h ccservice.h \
		ccclientlistener.h ccclientconn.h cservice.h precomp.h \
		Monitor.h CServiceConfig.h rtdb.h hashkey.h
ccmessage.obj:	ccmessage.h message.h ctidbgmem.h collectable.h \
		dlldefs.h ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h connection.h \
		exchange.h logger.h thread.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h observe.h ccfeeder.h cccapbank.h msg_pcrequest.h \
		ccstate.h ccid.h
ccserver.obj:	ccserver.h ccclientlistener.h ccclientconn.h ctdpcptrq.h \
		dlldefs.h observe.h types.h ccstate.h connection.h exchange.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		logger.h thread.h message.h ctidbgmem.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h ccmessage.h ccsubstationbus.h \
		dbaccess.h sema.h ccfeeder.h cccapbank.h msg_pcrequest.h \
		ctibase.h ctinexus.h configparms.h cparms.h
ccservice.obj:	ccservice.h ccclientlistener.h ccclientconn.h \
		ctdpcptrq.h dlldefs.h observe.h types.h ccstate.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h logger.h thread.h message.h \
		ctidbgmem.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h cservice.h \
		capcontroller.h dbaccess.h sema.h msg_cmd.h pointtypes.h \
		configparms.h cparms.h ccsubstationbusstore.h \
		ccsubstationbus.h ccfeeder.h cccapbank.h msg_pcrequest.h \
		ccid.h ccexecutor.h ccmessage.h ctibase.h ctinexus.h \
		eventlog.h rtdb.h hashkey.h
ccstate.obj:	ccid.h ccstate.h connection.h dlldefs.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h message.h ctidbgmem.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h observe.h
ccsubstationbus.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h ccsubstationbus.h \
		connection.h exchange.h logger.h thread.h message.h \
		ctidbgmem.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h observe.h \
		ccfeeder.h cccapbank.h msg_pcrequest.h ccid.h pointtypes.h \
		capcontroller.h msg_cmd.h configparms.h cparms.h \
		ccsubstationbusstore.h ccexecutor.h ccmessage.h ccstate.h \
		ctdpcptrq.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h mgr_holiday.h
ccsubstationbusstore.obj:	ccsubstationbusstore.h observe.h types.h \
		dlldefs.h ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h connection.h \
		exchange.h logger.h thread.h message.h ctidbgmem.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccid.h ccstate.h desolvers.h \
		db_entry_defines.h pointtypes.h resolvers.h devicetypes.h \
		ctibase.h ctinexus.h configparms.h cparms.h msg_dbchg.h \
		capcontroller.h msg_cmd.h ccexecutor.h ccmessage.h \
		ctdpcptrq.h utility.h
#ENDUPDATE#

