# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(LOADMANAGEMENT)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(PROCLOG)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(SERVER)\include \
-I$(RW)


.PATH.cpp = .;$(R_LOADMANAGEMENT)
.PATH.H = \
.\include \
;$(COMMON)\include \
;$(LOADMANAGENT)\include \
;$(CPARMS)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
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
$(COMPILEBASE)\lib\proclog.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctiseasondb.lib

BASEOBJS= \
clientconn.obj \
clistener.obj \
executor.obj \
lmcontrolarea.obj \
lmcontrolareastore.obj \
lmcontrolareatrigger.obj \
lmcicustomerbase.obj \
lmcurtailcustomer.obj \
lmenergyexchangecustomer.obj \
lmenergyexchangecustomerreply.obj \
lmenergyexchangehourlycustomer.obj \
lmenergyexchangehourlyoffer.obj \
lmenergyexchangeoffer.obj \
lmenergyexchangeofferrevision.obj \
lmgroupbase.obj \
lmgroupemetcon.obj \
lmgroupexpresscom.obj \
lmgroupmct.obj \
lmgrouppoint.obj \
lmgroupripple.obj \
lmgroupversacom.obj \
lmmessage.obj \
lmprogrambase.obj \
lmprogramcontrolwindow.obj \
lmprogramcurtailment.obj \
lmprogramdirect.obj \
lmprogramdirectgear.obj \
lmprogramenergyexchange.obj \
lmprogramthermostatgear.obj \
lmservice.obj \
loadmanager.obj \
main.obj

TARGS = loadmanagement.exe

ALL:          $(TARGS)

loadmanagement.exe:     $(BASEOBJS) Makefile
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
clientconn.obj:	clientconn.h ctdpcptrq.h dlldefs.h lmmessage.h \
		message.h ctidbgmem.h collectable.h lmcontrolarea.h \
		connection.h exchange.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h executor.h \
		lmcontrolareastore.h lmcontrolareatrigger.h lmid.h ctibase.h \
		ctinexus.h
clistener.obj:	clistener.h clientconn.h ctdpcptrq.h dlldefs.h \
		lmmessage.h message.h ctidbgmem.h collectable.h \
		lmcontrolarea.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h lmcontrolareastore.h \
		lmcontrolareatrigger.h lmid.h configparms.h cparms.h \
		ctibase.h ctinexus.h executor.h
executor.obj:	executor.h lmmessage.h message.h ctidbgmem.h \
		collectable.h dlldefs.h lmcontrolarea.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h observe.h lmprogrambase.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h ctdpcptrq.h clistener.h \
		clientconn.h lmcontrolareastore.h lmcontrolareatrigger.h \
		lmid.h loadmanager.h dbaccess.h sema.h pointtypes.h \
		configparms.h cparms.h ctibase.h ctinexus.h \
		lmprogramcurtailment.h lmprogramenergyexchange.h \
		lmenergyexchangeoffer.h lmenergyexchangeofferrevision.h \
		lmenergyexchangehourlyoffer.h lmenergyexchangecustomer.h \
		lmcicustomerbase.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lmprogramdirect.h \
		lmprogramdirectgear.h devicetypes.h lmcurtailcustomer.h
lmcicustomerbase.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmcicustomerbase.h observe.h msg_pcrequest.h message.h \
		ctidbgmem.h collectable.h lmid.h logger.h thread.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h msg_cmd.h pointtypes.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmgroupbase.h lmcontrolareatrigger.h executor.h lmmessage.h \
		ctdpcptrq.h ctibase.h ctinexus.h device.h devicetypes.h \
		resolvers.h db_entry_defines.h
lmcontrolarea.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h lmcontrolarea.h \
		connection.h exchange.h logger.h thread.h message.h \
		ctidbgmem.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h observe.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmid.h lmprogramdirect.h \
		lmprogramdirectgear.h pointtypes.h devicetypes.h device.h \
		loadmanager.h configparms.h cparms.h lmcontrolareastore.h \
		executor.h lmmessage.h ctdpcptrq.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h
lmcontrolareastore.obj:	lmcontrolareastore.h observe.h types.h \
		dlldefs.h lmcontrolarea.h connection.h exchange.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h message.h ctidbgmem.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h lmprogrambase.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h lmcontrolareatrigger.h lmid.h \
		lmcurtailcustomer.h lmcicustomerbase.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangeofferrevision.h lmenergyexchangeoffer.h \
		lmenergyexchangehourlyoffer.h \
		lmenergyexchangehourlycustomer.h lmprogramcurtailment.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		lmprogramthermostatgear.h lmprogramenergyexchange.h \
		lmgroupversacom.h lmgroupemetcon.h lmgroupexpresscom.h \
		lmgroupmct.h lmgroupripple.h lmgrouppoint.h \
		lmprogramcontrolwindow.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h devicetypes.h dbaccess.h \
		sema.h ctibase.h ctinexus.h configparms.h cparms.h \
		msg_dbchg.h loadmanager.h executor.h lmmessage.h ctdpcptrq.h \
		utility.h
lmcontrolareatrigger.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmcontrolareatrigger.h observe.h lmid.h lmprogrambase.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		ctidbgmem.h msg_signal.h yukon.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h pointtypes.h logger.h thread.h \
		loadmanager.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h configparms.h cparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h lmmessage.h ctdpcptrq.h ctibase.h \
		ctinexus.h
lmcurtailcustomer.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmcurtailcustomer.h observe.h msg_pcrequest.h message.h \
		ctidbgmem.h collectable.h lmcicustomerbase.h lmid.h logger.h \
		thread.h loadmanager.h connection.h exchange.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h msg_cmd.h pointtypes.h configparms.h \
		cparms.h lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmgroupbase.h lmcontrolareatrigger.h executor.h lmmessage.h \
		ctdpcptrq.h ctibase.h ctinexus.h device.h devicetypes.h \
		resolvers.h db_entry_defines.h
lmenergyexchangecustomer.obj:	dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmenergyexchangecustomer.h observe.h lmcicustomerbase.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h \
		lmenergyexchangecustomerreply.h lmid.h logger.h thread.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h msg_cmd.h pointtypes.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmgroupbase.h lmcontrolareatrigger.h executor.h lmmessage.h \
		ctdpcptrq.h ctibase.h ctinexus.h device.h devicetypes.h \
		resolvers.h db_entry_defines.h
lmenergyexchangecustomerreply.obj:	dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmenergyexchangecustomerreply.h observe.h \
		lmenergyexchangehourlycustomer.h lmid.h logger.h thread.h \
		loadmanager.h connection.h exchange.h message.h ctidbgmem.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h msg_cmd.h \
		pointtypes.h configparms.h cparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h device.h devicetypes.h resolvers.h \
		db_entry_defines.h
lmenergyexchangehourlycustomer.obj:	dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmenergyexchangehourlycustomer.h observe.h lmid.h logger.h \
		thread.h loadmanager.h connection.h exchange.h message.h \
		ctidbgmem.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h msg_cmd.h \
		pointtypes.h configparms.h cparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h device.h devicetypes.h resolvers.h \
		db_entry_defines.h
lmenergyexchangehourlyoffer.obj:	dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmenergyexchangehourlyoffer.h observe.h lmid.h logger.h \
		thread.h loadmanager.h connection.h exchange.h message.h \
		ctidbgmem.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h msg_cmd.h \
		pointtypes.h configparms.h cparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h device.h devicetypes.h resolvers.h \
		db_entry_defines.h
lmenergyexchangeoffer.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h lmid.h \
		logger.h thread.h lmenergyexchangeoffer.h observe.h \
		lmenergyexchangeofferrevision.h
lmenergyexchangeofferrevision.obj:	dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmenergyexchangeofferrevision.h observe.h \
		lmenergyexchangehourlyoffer.h lmid.h logger.h thread.h \
		loadmanager.h connection.h exchange.h message.h ctidbgmem.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h msg_cmd.h \
		pointtypes.h configparms.h cparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h device.h devicetypes.h resolvers.h \
		db_entry_defines.h
lmgroupbase.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h lmgroupbase.h \
		observe.h msg_pcrequest.h message.h ctidbgmem.h collectable.h \
		msg_cmd.h lmid.h logger.h thread.h loadmanager.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		yukon.h msg_ptreg.h msg_reg.h queue.h pointtypes.h \
		configparms.h cparms.h lmcontrolareastore.h lmcontrolarea.h \
		lmprogrambase.h lmcontrolareatrigger.h executor.h lmmessage.h \
		ctdpcptrq.h ctibase.h ctinexus.h device.h devicetypes.h \
		resolvers.h db_entry_defines.h
lmgroupemetcon.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h lmgroupemetcon.h \
		lmgroupbase.h observe.h msg_pcrequest.h message.h ctidbgmem.h \
		collectable.h msg_cmd.h lmid.h logger.h thread.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h pointtypes.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h
lmgroupexpresscom.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmgroupexpresscom.h lmgroupbase.h observe.h msg_pcrequest.h \
		message.h ctidbgmem.h collectable.h msg_cmd.h lmid.h logger.h \
		thread.h loadmanager.h connection.h exchange.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h pointtypes.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h
lmgroupmct.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h lmgroupmct.h \
		lmgroupemetcon.h lmgroupbase.h observe.h msg_pcrequest.h \
		message.h ctidbgmem.h collectable.h msg_cmd.h lmid.h logger.h \
		thread.h loadmanager.h connection.h exchange.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h pointtypes.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h
lmgrouppoint.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h lmgrouppoint.h \
		lmgroupbase.h observe.h msg_pcrequest.h message.h ctidbgmem.h \
		collectable.h msg_cmd.h lmid.h logger.h thread.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h pointtypes.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h
lmgroupripple.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h lmgroupripple.h \
		lmgroupbase.h observe.h msg_pcrequest.h message.h ctidbgmem.h \
		collectable.h msg_cmd.h lmid.h logger.h thread.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h pointtypes.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h
lmgroupversacom.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h lmgroupbase.h \
		observe.h msg_pcrequest.h message.h ctidbgmem.h collectable.h \
		msg_cmd.h lmgroupversacom.h lmid.h logger.h thread.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_signal.h yukon.h msg_ptreg.h msg_reg.h \
		queue.h pointtypes.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h
lmmessage.obj:	lmmessage.h message.h ctidbgmem.h collectable.h \
		dlldefs.h lmcontrolarea.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		yukon.h msg_ptreg.h msg_reg.h queue.h observe.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmid.h
lmprogrambase.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h lmid.h \
		lmprogrambase.h observe.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmprogramcontrolwindow.h pointtypes.h logger.h thread.h \
		device.h devicetypes.h loadmanager.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmcontrolareatrigger.h \
		executor.h lmmessage.h ctdpcptrq.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h mgr_holiday.h mgr_season.h
lmprogramcontrolwindow.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h lmid.h \
		lmprogramcontrolwindow.h observe.h pointdefs.h pointtypes.h \
		logger.h thread.h loadmanager.h connection.h exchange.h \
		message.h ctidbgmem.h collectable.h msg_multi.h msg_pdata.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h msg_cmd.h \
		configparms.h cparms.h lmcontrolareastore.h lmcontrolarea.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h lmmessage.h ctdpcptrq.h \
		ctibase.h ctinexus.h
lmprogramcurtailment.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h lmid.h \
		lmprogrambase.h observe.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h lmgroupbase.h msg_pcrequest.h msg_cmd.h pointtypes.h \
		logger.h thread.h loadmanager.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmcontrolareatrigger.h \
		executor.h lmmessage.h ctdpcptrq.h ctibase.h ctinexus.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		msg_email.h
lmprogramdirect.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h lmprogramdirect.h \
		lmprogrambase.h observe.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmprogramdirectgear.h lmgrouppoint.h devicetypes.h lmid.h \
		desolvers.h db_entry_defines.h pointtypes.h logger.h thread.h \
		loadmanager.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h configparms.h cparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmcontrolareatrigger.h executor.h lmmessage.h \
		ctdpcptrq.h ctibase.h ctinexus.h lmprogramthermostatgear.h
lmprogramdirectgear.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmprogramdirectgear.h observe.h lmid.h lmprogrambase.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		ctidbgmem.h msg_signal.h yukon.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h pointtypes.h logger.h thread.h \
		loadmanager.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h configparms.h cparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmcontrolareatrigger.h executor.h lmmessage.h \
		ctdpcptrq.h ctibase.h ctinexus.h
lmprogramenergyexchange.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h lmid.h \
		lmprogrambase.h observe.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h lmgroupbase.h msg_pcrequest.h msg_cmd.h pointtypes.h \
		logger.h thread.h loadmanager.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h configparms.h cparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmcontrolareatrigger.h \
		executor.h lmmessage.h ctdpcptrq.h ctibase.h ctinexus.h \
		lmprogramenergyexchange.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangecustomer.h \
		lmcicustomerbase.h lmenergyexchangecustomerreply.h \
		msg_email.h
lmprogramthermostatgear.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h \
		lmprogramthermostatgear.h observe.h lmprogramdirectgear.h \
		lmid.h lmprogrambase.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h pointtypes.h logger.h \
		thread.h loadmanager.h connection.h exchange.h msg_ptreg.h \
		msg_reg.h queue.h configparms.h cparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmcontrolareatrigger.h executor.h lmmessage.h \
		ctdpcptrq.h ctibase.h ctinexus.h
lmserver.obj:	lmserver.h clistener.h clientconn.h ctdpcptrq.h \
		dlldefs.h lmmessage.h message.h ctidbgmem.h collectable.h \
		lmcontrolarea.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h ctibase.h ctinexus.h \
		configparms.h cparms.h
lmservice.obj:	lmservice.h cservice.h dlldefs.h loadmanager.h \
		dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h connection.h exchange.h logger.h \
		thread.h message.h ctidbgmem.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h msg_cmd.h pointtypes.h configparms.h \
		cparms.h lmcontrolareastore.h observe.h lmcontrolarea.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h lmid.h executor.h lmmessage.h \
		ctdpcptrq.h ctibase.h ctinexus.h clistener.h clientconn.h \
		eventlog.h rtdb.h hashkey.h
loadmanager.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h connection.h \
		exchange.h logger.h thread.h message.h ctidbgmem.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h msg_cmd.h \
		msg_pcrequest.h msg_pcreturn.h msg_dbchg.h pointtypes.h \
		configparms.h cparms.h loadmanager.h lmcontrolareastore.h \
		observe.h lmcontrolarea.h lmprogrambase.h lmgroupbase.h \
		lmcontrolareatrigger.h lmid.h executor.h lmmessage.h \
		ctdpcptrq.h ctibase.h ctinexus.h netports.h resolvers.h \
		db_entry_defines.h devicetypes.h lmprogramdirect.h \
		lmprogramdirectgear.h
main.obj:	lmcontrolareastore.h observe.h types.h dlldefs.h \
		lmcontrolarea.h connection.h exchange.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h message.h \
		ctidbgmem.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmid.h loadmanager.h dbaccess.h sema.h \
		pointtypes.h configparms.h cparms.h executor.h lmmessage.h \
		ctdpcptrq.h ctibase.h ctinexus.h lmservice.h cservice.h \
		clistener.h clientconn.h precomp.h Monitor.h CServiceConfig.h \
		rtdb.h hashkey.h
#ENDUPDATE#
