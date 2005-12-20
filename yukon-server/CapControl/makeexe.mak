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
-I$(RW) \
-I$(BOOST) \


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
ccsubstationbusstore.obj \
pao_schedule.obj \
pao_event.obj \
mgr_paosched.obj \
ccstrategy.obj

TARGS = capcontrol.exe

ALL:          $(TARGS)

capcontrol.exe:     $(BASEOBJS) Makefile
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOSTLIBS)
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
               $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
#UPDATE#
capcontroller.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h connection.h exchange.h logger.h \
		thread.h ctitime.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h ccmessage.h ccsubstationbus.h observe.h \
		ccfeeder.h cccapbank.h ctidate.h msg_pcrequest.h ccstrategy.h \
		ccstate.h msg_cmd.h msg_signal.h msg_pcreturn.h msg_dbchg.h \
		pointtypes.h configparms.h cparms.h capcontroller.h \
		ccsubstationbusstore.h ccid.h ccexecutor.h ctdpcptrq.h \
		ctibase.h ctinexus.h netports.h devicetypes.h resolvers.h \
		db_entry_defines.h mgr_paosched.h pao_schedule.h pao_event.h \
		dbmemobject.h ccclientconn.h ccclientlistener.h
cccapbank.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h cccapbank.h observe.h ctitime.h ctidate.h \
		logger.h thread.h ccid.h pointdefs.h device.h devicetypes.h \
		resolvers.h pointtypes.h db_entry_defines.h rwutil.h \
		boost_time.h
ccclientconn.obj:	yukon.h precompiled.h ctidbgmem.h ccclientconn.h \
		ctdpcptrq.h dlldefs.h observe.h types.h ccmessage.h ctitime.h \
		message.h collectable.h rwutil.h boost_time.h \
		ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h sema.h connection.h exchange.h \
		logger.h thread.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h ccfeeder.h cccapbank.h \
		ctidate.h msg_pcrequest.h ccstrategy.h ccstate.h ccexecutor.h \
		ccsubstationbusstore.h ccid.h ctibase.h ctinexus.h netports.h \
		capcontroller.h msg_cmd.h pointtypes.h configparms.h cparms.h
ccclientlistener.obj:	yukon.h precompiled.h ctidbgmem.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		observe.h types.h ccstate.h connection.h exchange.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h ctitime.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h ccmessage.h ccsubstationbus.h dbaccess.h \
		sema.h ccfeeder.h cccapbank.h ctidate.h msg_pcrequest.h \
		ccstrategy.h ccsubstationbusstore.h ccid.h configparms.h \
		cparms.h ctibase.h ctinexus.h netports.h ccexecutor.h
ccexecutor.obj:	yukon.h precompiled.h ctidbgmem.h msg_signal.h \
		message.h collectable.h dlldefs.h rwutil.h ctitime.h \
		boost_time.h ccclientlistener.h ccclientconn.h ctdpcptrq.h \
		observe.h types.h ccstate.h connection.h exchange.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h ccexecutor.h ccmessage.h ccsubstationbus.h \
		dbaccess.h sema.h ccfeeder.h cccapbank.h ctidate.h \
		msg_pcrequest.h ccstrategy.h ccsubstationbusstore.h ccid.h \
		capcontroller.h msg_cmd.h pointtypes.h configparms.h cparms.h \
		ctibase.h ctinexus.h netports.h
ccfeeder.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h msg_signal.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h ccsubstationbus.h connection.h \
		exchange.h logger.h thread.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h observe.h \
		ccfeeder.h cccapbank.h ctidate.h msg_pcrequest.h ccstrategy.h \
		ccid.h pointtypes.h capcontroller.h msg_cmd.h configparms.h \
		cparms.h ccsubstationbusstore.h ccexecutor.h ccmessage.h \
		ccstate.h ctdpcptrq.h ctibase.h ctinexus.h netports.h \
		resolvers.h db_entry_defines.h numstr.h
ccmain.obj:	yukon.h precompiled.h ctidbgmem.h ccsubstationbusstore.h \
		observe.h types.h dlldefs.h ccsubstationbus.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		connection.h exchange.h logger.h thread.h ctitime.h message.h \
		collectable.h rwutil.h boost_time.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h ccfeeder.h \
		cccapbank.h ctidate.h msg_pcrequest.h ccstrategy.h ccid.h \
		capcontroller.h msg_cmd.h pointtypes.h configparms.h cparms.h \
		ccexecutor.h ccmessage.h ccstate.h ctdpcptrq.h ctibase.h \
		ctinexus.h netports.h ccservice.h ccclientlistener.h \
		ccclientconn.h cservice.h precomp.h Monitor.h \
		CServiceConfig.h rtdb.h hashkey.h hash_functions.h
ccmessage.obj:	yukon.h precompiled.h ctidbgmem.h ccmessage.h ctitime.h \
		dlldefs.h message.h collectable.h rwutil.h boost_time.h \
		ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h connection.h \
		exchange.h logger.h thread.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h observe.h \
		ccfeeder.h cccapbank.h ctidate.h msg_pcrequest.h ccstrategy.h \
		ccstate.h ccid.h
ccserver.obj:	yukon.h precompiled.h ctidbgmem.h ccserver.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		observe.h types.h ccstate.h connection.h exchange.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h ctitime.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h ccmessage.h ccsubstationbus.h dbaccess.h \
		sema.h ccfeeder.h cccapbank.h ctidate.h msg_pcrequest.h \
		ccstrategy.h ctibase.h ctinexus.h netports.h configparms.h \
		cparms.h
ccservice.obj:	yukon.h precompiled.h ctidbgmem.h ccservice.h \
		ccclientlistener.h ccclientconn.h ctdpcptrq.h dlldefs.h \
		observe.h types.h ccstate.h connection.h exchange.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h ctitime.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cservice.h capcontroller.h dbaccess.h \
		sema.h msg_cmd.h pointtypes.h configparms.h cparms.h \
		ccsubstationbusstore.h ccsubstationbus.h ccfeeder.h \
		cccapbank.h ctidate.h msg_pcrequest.h ccstrategy.h ccid.h \
		ccexecutor.h ccmessage.h ctibase.h ctinexus.h netports.h \
		eventlog.h rtdb.h hashkey.h hash_functions.h
ccstate.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h ccid.h ccstate.h connection.h exchange.h \
		logger.h thread.h ctitime.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h observe.h
ccstrategy.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h msg_signal.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h ccstrategy.h connection.h exchange.h \
		logger.h thread.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h observe.h msg_pcrequest.h \
		ccid.h pointtypes.h capcontroller.h msg_cmd.h configparms.h \
		cparms.h ccsubstationbusstore.h ccsubstationbus.h ccfeeder.h \
		cccapbank.h ctidate.h ccexecutor.h ccmessage.h ccstate.h \
		ctdpcptrq.h ctibase.h ctinexus.h netports.h resolvers.h \
		db_entry_defines.h mgr_holiday.h
ccsubstationbus.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h msg_signal.h message.h collectable.h \
		rwutil.h ctitime.h boost_time.h ccsubstationbus.h \
		connection.h exchange.h logger.h thread.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		observe.h ccfeeder.h cccapbank.h ctidate.h msg_pcrequest.h \
		ccstrategy.h ccid.h pointtypes.h capcontroller.h msg_cmd.h \
		configparms.h cparms.h ccsubstationbusstore.h ccexecutor.h \
		ccmessage.h ccstate.h ctdpcptrq.h ctibase.h ctinexus.h \
		netports.h resolvers.h db_entry_defines.h mgr_holiday.h \
		mgr_paosched.h pao_schedule.h pao_event.h dbmemobject.h
ccsubstationbusstore.obj:	yukon.h precompiled.h ctidbgmem.h \
		ccsubstationbusstore.h observe.h types.h dlldefs.h \
		ccsubstationbus.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h sema.h connection.h exchange.h \
		logger.h thread.h ctitime.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h ccfeeder.h cccapbank.h ctidate.h \
		msg_pcrequest.h ccstrategy.h ccid.h ccstate.h desolvers.h \
		db_entry_defines.h pointtypes.h resolvers.h devicetypes.h \
		ctibase.h ctinexus.h netports.h configparms.h cparms.h \
		msg_dbchg.h msg_signal.h capcontroller.h msg_cmd.h \
		ccexecutor.h ccmessage.h ctdpcptrq.h utility.h
mgr_paosched.obj:	yukon.h precompiled.h ctidbgmem.h mgr_paosched.h \
		pao_schedule.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h connection.h \
		exchange.h logger.h thread.h ctitime.h message.h \
		collectable.h rwutil.h boost_time.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h ctibase.h \
		ctinexus.h netports.h pao_event.h dbmemobject.h \
		capcontroller.h msg_cmd.h pointtypes.h configparms.h cparms.h \
		ccsubstationbusstore.h observe.h ccsubstationbus.h ccfeeder.h \
		cccapbank.h ctidate.h msg_pcrequest.h ccstrategy.h ccid.h \
		ccexecutor.h ccmessage.h ccstate.h ctdpcptrq.h ctitokenizer.h
pao_event.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h connection.h exchange.h logger.h thread.h \
		ctitime.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h msg_dbchg.h capcontroller.h msg_cmd.h pointtypes.h \
		configparms.h cparms.h ccsubstationbusstore.h observe.h \
		ccsubstationbus.h ccfeeder.h cccapbank.h ctidate.h \
		msg_pcrequest.h ccstrategy.h ccid.h ccexecutor.h ccmessage.h \
		ccstate.h ctdpcptrq.h ctibase.h ctinexus.h netports.h \
		pao_schedule.h pao_event.h dbmemobject.h
pao_schedule.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h connection.h exchange.h logger.h \
		thread.h ctitime.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h msg_dbchg.h capcontroller.h msg_cmd.h \
		pointtypes.h configparms.h cparms.h ccsubstationbusstore.h \
		observe.h ccsubstationbus.h ccfeeder.h cccapbank.h ctidate.h \
		msg_pcrequest.h ccstrategy.h ccid.h ccexecutor.h ccmessage.h \
		ccstate.h ctdpcptrq.h ctibase.h ctinexus.h netports.h \
		pao_schedule.h pao_event.h dbmemobject.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
#ENDUPDATE#

