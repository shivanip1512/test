# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(CAPCONTROL)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(PROCLOG)\include \
-I$(MSG)\include \
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

BASEOBJS= \
capcontroller.obj \
cccapbank.obj \
ccclientconn.obj \
ccclientlistener.obj \
ccexecutor.obj \
ccfeeder.obj \
ccmain.obj \
ccmessage.obj \
ccserver.obj \
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
capbank.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h capbank.h observe.h ccid.h \
		device.h devicetypes.h
capcontrol.obj:	capcontrol.h clistener.h clientconn.h ctdpcptrq.h \
		dlldefs.h observe.h types.h state.h connection.h exchange.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		ccmessage.h ccsubstationbus.h ccfeeder.h cccapbank.h \
		msg_pcrequest.h ccstate.h ctibase.h ctinexus.h logger.h \
		thread.h configparms.h cparms.h
capcontroller.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h connection.h exchange.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		ccmessage.h ccsubstationbus.h observe.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccstate.h msg_cmd.h \
		msg_pcreturn.h msg_dbchg.h pointtypes.h configparms.h \
		cparms.h capcontroller.h ccsubstationbusstore.h ccid.h \
		ccexecutor.h ctdpcptrq.h ctibase.h ctinexus.h logger.h \
		thread.h netports.h resolvers.h db_entry_defines.h
cccapbank.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h cccapbank.h observe.h \
		ccid.h pointdefs.h device.h devicetypes.h logger.h thread.h
ccclientconn.obj:	ccclientconn.h ctdpcptrq.h dlldefs.h observe.h \
		types.h ccmessage.h message.h collectable.h ccsubstationbus.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccstate.h ccexecutor.h \
		ccsubstationbusstore.h ccid.h ccserver.h ccclientlistener.h \
		ctibase.h ctinexus.h logger.h thread.h
ccclientlistener.obj:	ccclientlistener.h ccclientconn.h ctdpcptrq.h \
		dlldefs.h observe.h types.h ccstate.h connection.h exchange.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		ccmessage.h ccsubstationbus.h ccfeeder.h cccapbank.h \
		msg_pcrequest.h ccsubstationbusstore.h ccid.h ccserver.h \
		ctibase.h ctinexus.h ccexecutor.h logger.h thread.h
ccexecutor.obj:	ccexecutor.h ccmessage.h message.h collectable.h \
		dlldefs.h ccsubstationbus.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h observe.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccstate.h ctdpcptrq.h ccserver.h \
		ccclientlistener.h ccclientconn.h ccsubstationbusstore.h \
		ccid.h capcontroller.h dbaccess.h msg_cmd.h pointtypes.h \
		configparms.h cparms.h ctibase.h ctinexus.h logger.h thread.h
ccfeeder.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h ccsubstationbus.h \
		connection.h exchange.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h observe.h ccfeeder.h cccapbank.h \
		msg_pcrequest.h ccid.h pointtypes.h logger.h thread.h \
		capcontroller.h msg_cmd.h configparms.h cparms.h \
		ccsubstationbusstore.h ccexecutor.h ccmessage.h ccstate.h \
		ctdpcptrq.h ctibase.h ctinexus.h
ccmain.obj:	ccserver.h ccclientlistener.h ccclientconn.h ctdpcptrq.h \
		dlldefs.h observe.h types.h ccstate.h connection.h exchange.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		ccmessage.h ccsubstationbus.h ccfeeder.h cccapbank.h \
		msg_pcrequest.h ccsubstationbusstore.h ccid.h capcontroller.h \
		dbaccess.h msg_cmd.h pointtypes.h configparms.h cparms.h \
		ccexecutor.h ctibase.h ctinexus.h logger.h thread.h \
		ccservice.h cservice.h precomp.h Monitor.h CServiceConfig.h \
		rtdb.h hashkey.h
ccmessage.obj:	ccmessage.h message.h collectable.h dlldefs.h \
		ccsubstationbus.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h observe.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccstate.h ccid.h
ccserver.obj:	ccserver.h ccclientlistener.h ccclientconn.h ctdpcptrq.h \
		dlldefs.h observe.h types.h ccstate.h connection.h exchange.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		ccmessage.h ccsubstationbus.h ccfeeder.h cccapbank.h \
		msg_pcrequest.h ctibase.h ctinexus.h logger.h thread.h \
		configparms.h cparms.h
ccservice.obj:	ccserver.h ccclientlistener.h ccclientconn.h \
		ctdpcptrq.h dlldefs.h observe.h types.h ccstate.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h ccmessage.h ccsubstationbus.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccservice.h cservice.h \
		capcontroller.h dbaccess.h msg_cmd.h pointtypes.h \
		configparms.h cparms.h ccsubstationbusstore.h ccid.h \
		ccexecutor.h ctibase.h ctinexus.h logger.h thread.h \
		eventlog.h rtdb.h hashkey.h
ccstate.obj:	ccid.h ccstate.h connection.h dlldefs.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h observe.h logger.h thread.h
ccsubstationbus.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h ccsubstationbus.h \
		connection.h exchange.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h observe.h ccfeeder.h cccapbank.h \
		msg_pcrequest.h ccid.h pointtypes.h logger.h thread.h \
		capcontroller.h msg_cmd.h configparms.h cparms.h \
		ccsubstationbusstore.h ccexecutor.h ccmessage.h ccstate.h \
		ctdpcptrq.h ctibase.h ctinexus.h
ccsubstationbusstore.obj:	ccsubstationbusstore.h observe.h types.h \
		dlldefs.h ccsubstationbus.h connection.h exchange.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccid.h ccstate.h desolvers.h \
		db_entry_defines.h pointtypes.h resolvers.h devicetypes.h \
		dbaccess.h ctibase.h ctinexus.h logger.h thread.h \
		configparms.h cparms.h msg_dbchg.h capcontroller.h msg_cmd.h \
		ccexecutor.h ccmessage.h ctdpcptrq.h
clientconn.obj:	clientconn.h ctdpcptrq.h dlldefs.h observe.h types.h \
		ccmessage.h message.h collectable.h ccsubstationbus.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccstate.h executor.h \
		strategylist.h strategy.h capbank.h ccid.h strategystore.h \
		state.h capcontrol.h clistener.h ctibase.h ctinexus.h \
		logger.h thread.h
clistener.obj:	clistener.h clientconn.h ctdpcptrq.h dlldefs.h \
		observe.h types.h state.h connection.h exchange.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		ccmessage.h ccsubstationbus.h ccfeeder.h cccapbank.h \
		msg_pcrequest.h ccstate.h strategystore.h strategy.h \
		capbank.h ccid.h strategylist.h capcontrol.h ctibase.h \
		ctinexus.h executor.h logger.h thread.h
controller.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h connection.h exchange.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h msg_cmd.h \
		msg_pcrequest.h msg_pcreturn.h msg_dbchg.h pointtypes.h \
		configparms.h cparms.h controller.h strategystore.h observe.h \
		strategy.h capbank.h ccid.h strategylist.h state.h executor.h \
		ccmessage.h ccsubstationbus.h ccfeeder.h cccapbank.h \
		ccstate.h ctdpcptrq.h ctibase.h ctinexus.h logger.h thread.h \
		netports.h
executor.obj:	executor.h ccmessage.h message.h collectable.h dlldefs.h \
		ccsubstationbus.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h observe.h ccfeeder.h \
		cccapbank.h msg_pcrequest.h ccstate.h ctdpcptrq.h \
		strategylist.h strategy.h capbank.h ccid.h capcontrol.h \
		clistener.h clientconn.h state.h strategystore.h controller.h \
		dbaccess.h msg_cmd.h pointtypes.h configparms.h cparms.h \
		ctibase.h ctinexus.h logger.h thread.h
main.obj:	capcontrol.h clistener.h clientconn.h ctdpcptrq.h dlldefs.h \
		observe.h types.h state.h connection.h exchange.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		ccmessage.h ccsubstationbus.h ccfeeder.h cccapbank.h \
		msg_pcrequest.h ccstate.h strategystore.h strategy.h \
		capbank.h ccid.h strategylist.h controller.h dbaccess.h \
		msg_cmd.h pointtypes.h configparms.h cparms.h executor.h \
		ctibase.h ctinexus.h logger.h thread.h ccservice.h cservice.h \
		capcontroller.h ccsubstationbusstore.h ccexecutor.h \
		ccserver.h ccclientlistener.h ccclientconn.h precomp.h \
		Monitor.h CServiceConfig.h rtdb.h hashkey.h
state.obj:	ccid.h state.h connection.h dlldefs.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h observe.h \
		logger.h thread.h
strategy.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h strategy.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h observe.h capbank.h msg_pcrequest.h ccid.h \
		pointtypes.h logger.h thread.h controller.h msg_cmd.h \
		configparms.h cparms.h strategystore.h strategylist.h state.h \
		executor.h ccmessage.h ccsubstationbus.h ccfeeder.h \
		cccapbank.h ccstate.h ctdpcptrq.h ctibase.h ctinexus.h
strategylist.obj:	strategylist.h strategy.h connection.h dlldefs.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h observe.h capbank.h msg_pcrequest.h ccid.h \
		ctibase.h ctinexus.h logger.h thread.h
strategystore.obj:	strategylist.h strategy.h connection.h dlldefs.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h observe.h capbank.h msg_pcrequest.h ccid.h \
		strategystore.h state.h dbaccess.h ctibase.h ctinexus.h \
		logger.h thread.h configparms.h cparms.h
#ENDUPDATE#

