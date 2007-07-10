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
-I$(BOOST) \
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
;$(BOOST) \
;$(RW)



LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\proclog.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctiseasondb.lib

BASEOBJS= \
clientconn.obj \
clistener.obj \
executor.obj \
lmconstraint.obj \
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
lmfactory.obj \
lmgroupbase.obj \
lmgroupemetcon.obj \
lmgroupexpresscom.obj \
lmgroupmacro.obj \
lmgroupmct.obj \
lmgrouppoint.obj \
lmgroupripple.obj \
lmgroupsa305.obj \
lmgroupsa105.obj \
lmgroupsa205.obj \
lmgroupsadigital.obj \
lmgroupgolay.obj \
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
              $(RWCPPINVOKE) $(CFLAGS) -D_DEBUG_MEMORY $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
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
               $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS)  $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
#UPDATE#
clientconn.obj:	yukon.h precompiled.h ctidbgmem.h clientconn.h \
		ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h mutex.h guard.h \
		numstr.h clrdump.h lmmessage.h ctitime.h message.h \
		collectable.h rwutil.h boost_time.h lmcontrolarea.h \
		dbmemobject.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h logger.h \
		thread.h utility.h queues.h sorted_vector.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h executor.h msg_server_req.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		lmcontrolareastore.h lmid.h loadmanager.h dbaccess.h sema.h \
		pointtypes.h configparms.h ctibase.h ctinexus.h
clistener.obj:	yukon.h precompiled.h ctidbgmem.h clistener.h \
		clientconn.h ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h mutex.h \
		guard.h numstr.h clrdump.h lmmessage.h ctitime.h message.h \
		collectable.h rwutil.h boost_time.h lmcontrolarea.h \
		dbmemobject.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h logger.h \
		thread.h utility.h queues.h sorted_vector.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmcontrolareastore.h lmid.h \
		configparms.h ctibase.h ctinexus.h executor.h \
		msg_server_req.h lmprogramdirect.h lmprogramdirectgear.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h
executor.obj:	yukon.h precompiled.h ctidbgmem.h msg_server_req.h \
		dlldefs.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h msg_server_resp.h msg_signal.h executor.h \
		ctdpcptrq.h CtiPCPtrQueue.h mutex.h guard.h numstr.h \
		clrdump.h lmmessage.h clientconn.h lmcontrolarea.h \
		dbmemobject.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h logger.h \
		thread.h utility.h queues.h sorted_vector.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h clistener.h \
		lmcontrolareastore.h lmid.h loadmanager.h dbaccess.h sema.h \
		pointtypes.h configparms.h ctibase.h ctinexus.h \
		lmprogramenergyexchange.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lmprogramcontrolwindow.h \
		devicetypes.h lmconstraint.h ctidate.h
lmcicustomerbase.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmcicustomerbase.h observe.h utility.h ctitime.h \
		queues.h sorted_vector.h msg_pcrequest.h message.h \
		collectable.h rwutil.h boost_time.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		pointtypes.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h
lmconstraint.obj:	yukon.h precompiled.h ctidbgmem.h lmconstraint.h \
		lmprogramdirect.h lmprogrambase.h dbmemobject.h observe.h \
		types.h dlldefs.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h rwutil.h boost_time.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h lmcontrolareatrigger.h \
		lmprogramdirectgear.h lmcontrolarea.h connection.h exchange.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		clrdump.h cticonnect.h netports.h logger.h thread.h \
		CtiPCPtrQueue.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h dbaccess.h sema.h \
		lmprogramcontrolwindow.h lmid.h mgr_season.h ctidate.h \
		mgr_holiday.h
lmcontrolarea.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h msg_signal.h message.h collectable.h rwutil.h \
		ctitime.h boost_time.h lmcontrolarea.h dbmemobject.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmid.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcontrolwindow.h pointtypes.h \
		devicetypes.h loadmanager.h configparms.h \
		lmcontrolareastore.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h lmconstraint.h ctidate.h
lmcontrolareastore.obj:	yukon.h precompiled.h ctidbgmem.h \
		mgr_holiday.h ctidate.h dlldefs.h logger.h thread.h mutex.h \
		guard.h numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h types.h sorted_vector.h mgr_season.h \
		msg_signal.h message.h collectable.h rwutil.h boost_time.h \
		lmcontrolareastore.h observe.h lmcontrolarea.h dbmemobject.h \
		connection.h exchange.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h lmprogrambase.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h lmcontrolareatrigger.h lmid.h \
		lmcurtailcustomer.h lmcicustomerbase.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangeoffer.h lmprogramcurtailment.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		lmprogramthermostatgear.h lmprogramenergyexchange.h \
		lmgroupversacom.h lmgroupemetcon.h lmgroupexpresscom.h \
		lmgroupmct.h lmgroupripple.h lmgrouppoint.h lmgroupsa105.h \
		lmgroupsa205.h lmgroupsa305.h lmgroupsadigital.h \
		lmgroupgolay.h lmprogramcontrolwindow.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h devicetypes.h \
		dbaccess.h sema.h ctibase.h ctinexus.h configparms.h \
		msg_dbchg.h loadmanager.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmfactory.h \
		tbl_paoexclusion.h
lmcontrolareatrigger.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmcontrolareatrigger.h ctitime.h observe.h utility.h \
		queues.h sorted_vector.h lmid.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		rwutil.h boost_time.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		pointtypes.h logger.h thread.h CtiPCPtrQueue.h loadmanager.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h
lmcurtailcustomer.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmcurtailcustomer.h observe.h utility.h ctitime.h \
		queues.h sorted_vector.h msg_pcrequest.h message.h \
		collectable.h rwutil.h boost_time.h lmcicustomerbase.h lmid.h \
		logger.h thread.h CtiPCPtrQueue.h loadmanager.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h msg_cmd.h \
		pointtypes.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h ctibase.h \
		ctinexus.h resolvers.h db_entry_defines.h
lmenergyexchangecustomer.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h lmenergyexchangecustomer.h observe.h \
		utility.h ctitime.h queues.h sorted_vector.h \
		lmcicustomerbase.h msg_pcrequest.h message.h collectable.h \
		rwutil.h boost_time.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		pointtypes.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h
lmenergyexchangecustomerreply.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h lmenergyexchangecustomerreply.h ctitime.h \
		observe.h utility.h queues.h sorted_vector.h \
		lmenergyexchangehourlycustomer.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		message.h collectable.h rwutil.h boost_time.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h msg_cmd.h pointtypes.h \
		configparms.h lmcontrolareastore.h lmcontrolarea.h \
		dbmemobject.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h
lmenergyexchangehourlycustomer.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h lmenergyexchangehourlycustomer.h observe.h \
		utility.h ctitime.h queues.h sorted_vector.h lmid.h logger.h \
		thread.h CtiPCPtrQueue.h loadmanager.h connection.h \
		exchange.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		pointtypes.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
		msg_pcrequest.h lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h
lmenergyexchangehourlyoffer.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h lmenergyexchangehourlyoffer.h observe.h \
		utility.h ctitime.h queues.h sorted_vector.h lmid.h logger.h \
		thread.h CtiPCPtrQueue.h loadmanager.h connection.h \
		exchange.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		pointtypes.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
		msg_pcrequest.h lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h
lmenergyexchangeoffer.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h lmid.h ctitime.h logger.h thread.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		lmenergyexchangeoffer.h observe.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		rwutil.h boost_time.h
lmenergyexchangeofferrevision.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h lmenergyexchangeofferrevision.h ctitime.h \
		lmenergyexchangehourlyoffer.h observe.h utility.h queues.h \
		sorted_vector.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h message.h collectable.h \
		rwutil.h boost_time.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h msg_cmd.h pointtypes.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h
lmfactory.obj:	yukon.h precompiled.h ctidbgmem.h lmfactory.h \
		lmgroupbase.h rwutil.h ctitime.h dlldefs.h boost_time.h \
		dbmemobject.h observe.h types.h utility.h queues.h numstr.h \
		sorted_vector.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h guard.h clrdump.h logger.h thread.h mutex.h \
		CtiPCPtrQueue.h resolvers.h pointtypes.h db_entry_defines.h \
		devicetypes.h lmgroupversacom.h lmgroupemetcon.h \
		lmgroupexpresscom.h lmgroupmct.h lmgroupripple.h \
		lmgrouppoint.h lmgroupsa105.h lmgroupsa205.h lmgroupsa305.h \
		lmgroupsadigital.h lmgroupgolay.h lmgroupmacro.h
lmgroupbase.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmgroupbase.h rwutil.h ctitime.h boost_time.h \
		dbmemobject.h observe.h utility.h queues.h sorted_vector.h \
		msg_pcrequest.h message.h collectable.h msg_cmd.h lmid.h \
		logger.h thread.h CtiPCPtrQueue.h loadmanager.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		pointtypes.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		clientconn.h lmprogramdirect.h lmprogramdirectgear.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h resolvers.h db_entry_defines.h ctidate.h
lmgroupemetcon.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmgroupemetcon.h lmgroupbase.h rwutil.h ctitime.h \
		boost_time.h dbmemobject.h observe.h utility.h queues.h \
		sorted_vector.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h pointtypes.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h
lmgroupexpresscom.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmgroupexpresscom.h lmgroupbase.h rwutil.h ctitime.h \
		boost_time.h dbmemobject.h observe.h utility.h queues.h \
		sorted_vector.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h pointtypes.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		ctistring.h ctitokenizer.h
lmgroupgolay.obj:	yukon.h precompiled.h ctidbgmem.h lmgroupgolay.h \
		lmgroupbase.h rwutil.h ctitime.h dlldefs.h boost_time.h \
		dbmemobject.h observe.h types.h utility.h queues.h numstr.h \
		sorted_vector.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h lmid.h logger.h thread.h mutex.h guard.h clrdump.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h sema.h
lmgroupmacro.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmgroupmacro.h lmgroupbase.h rwutil.h ctitime.h \
		boost_time.h dbmemobject.h observe.h utility.h queues.h \
		sorted_vector.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h pointtypes.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h
lmgroupmct.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		lmgroupmct.h lmgroupemetcon.h lmgroupbase.h rwutil.h \
		ctitime.h boost_time.h dbmemobject.h observe.h utility.h \
		queues.h sorted_vector.h msg_pcrequest.h message.h \
		collectable.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h pointtypes.h \
		configparms.h lmcontrolareastore.h lmcontrolarea.h \
		lmprogrambase.h lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h
lmgrouppoint.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmgrouppoint.h lmgroupbase.h rwutil.h ctitime.h \
		boost_time.h dbmemobject.h observe.h utility.h queues.h \
		sorted_vector.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h pointtypes.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h
lmgroupripple.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmgroupripple.h lmgroupbase.h rwutil.h ctitime.h \
		boost_time.h dbmemobject.h observe.h utility.h queues.h \
		sorted_vector.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h pointtypes.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h
lmgroupsa105.obj:	yukon.h precompiled.h ctidbgmem.h lmgroupsa105.h \
		lmgroupbase.h rwutil.h ctitime.h dlldefs.h boost_time.h \
		dbmemobject.h observe.h types.h utility.h queues.h numstr.h \
		sorted_vector.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h lmid.h logger.h thread.h mutex.h guard.h clrdump.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h sema.h
lmgroupsa205.obj:	yukon.h precompiled.h ctidbgmem.h lmgroupsa205.h \
		lmgroupbase.h rwutil.h ctitime.h dlldefs.h boost_time.h \
		dbmemobject.h observe.h types.h utility.h queues.h numstr.h \
		sorted_vector.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h lmid.h logger.h thread.h mutex.h guard.h clrdump.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h sema.h
lmgroupsa205or105.obj:	lmgroupSA205or105.h lmgroupbase.h rwutil.h \
		yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		boost_time.h dbmemobject.h observe.h types.h utility.h \
		queues.h numstr.h sorted_vector.h msg_pcrequest.h message.h \
		collectable.h msg_cmd.h lmid.h logger.h thread.h mutex.h \
		guard.h clrdump.h CtiPCPtrQueue.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h cticonnect.h netports.h sema.h
lmgroupsa305.obj:	yukon.h precompiled.h ctidbgmem.h lmgroupsa305.h \
		lmgroupbase.h rwutil.h ctitime.h dlldefs.h boost_time.h \
		dbmemobject.h observe.h types.h utility.h queues.h numstr.h \
		sorted_vector.h msg_pcrequest.h message.h collectable.h \
		msg_cmd.h lmid.h logger.h thread.h mutex.h guard.h clrdump.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h sema.h
lmgroupsadigital.obj:	yukon.h precompiled.h ctidbgmem.h \
		lmgroupsadigital.h lmgroupbase.h rwutil.h ctitime.h dlldefs.h \
		boost_time.h dbmemobject.h observe.h types.h utility.h \
		queues.h numstr.h sorted_vector.h msg_pcrequest.h message.h \
		collectable.h msg_cmd.h lmid.h logger.h thread.h mutex.h \
		guard.h clrdump.h CtiPCPtrQueue.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h cticonnect.h netports.h sema.h
lmgroupsadigitalorgolay.obj:	lmgroupsadigitalorgolay.h lmgroupbase.h \
		rwutil.h yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h boost_time.h dbmemobject.h observe.h types.h \
		utility.h queues.h numstr.h sorted_vector.h msg_pcrequest.h \
		message.h collectable.h msg_cmd.h lmid.h logger.h thread.h \
		mutex.h guard.h clrdump.h CtiPCPtrQueue.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h cticonnect.h \
		netports.h sema.h
lmgroupversacom.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmgroupbase.h rwutil.h ctitime.h boost_time.h \
		dbmemobject.h observe.h utility.h queues.h sorted_vector.h \
		msg_pcrequest.h message.h collectable.h msg_cmd.h \
		lmgroupversacom.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h pointtypes.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h
lmmessage.obj:	yukon.h precompiled.h ctidbgmem.h lmmessage.h ctitime.h \
		dlldefs.h clientconn.h ctdpcptrq.h CtiPCPtrQueue.h mutex.h \
		guard.h numstr.h clrdump.h message.h collectable.h rwutil.h \
		boost_time.h lmcontrolarea.h dbmemobject.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		cticonnect.h netports.h logger.h thread.h utility.h queues.h \
		sorted_vector.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h observe.h lmprogrambase.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h lmcontrolareatrigger.h lmid.h
lmprogrambase.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmid.h ctitime.h lmprogrambase.h dbmemobject.h \
		observe.h utility.h queues.h sorted_vector.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h rwutil.h \
		boost_time.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmprogramcontrolwindow.h pointtypes.h \
		logger.h thread.h CtiPCPtrQueue.h loadmanager.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h mgr_holiday.h ctidate.h \
		mgr_season.h
lmprogramcontrolwindow.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h lmid.h ctitime.h lmprogramcontrolwindow.h \
		observe.h utility.h queues.h sorted_vector.h pointdefs.h \
		pointtypes.h logger.h thread.h CtiPCPtrQueue.h loadmanager.h \
		connection.h exchange.h message.h collectable.h rwutil.h \
		boost_time.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		configparms.h lmcontrolareastore.h lmcontrolarea.h \
		dbmemobject.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h
lmprogramcurtailment.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmid.h ctitime.h lmprogrambase.h dbmemobject.h \
		observe.h utility.h queues.h sorted_vector.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h rwutil.h \
		boost_time.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h pointtypes.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		msg_notif_email.h msg_notif_email_attachment.h
lmprogramdirect.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h rwutil.h ctitime.h boost_time.h lmprogramdirect.h \
		lmprogrambase.h dbmemobject.h observe.h utility.h queues.h \
		sorted_vector.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h lmgrouppoint.h devicetypes.h lmid.h desolvers.h \
		db_entry_defines.h pointtypes.h loadmanager.h configparms.h \
		lmcontrolareastore.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		msg_signal.h msg_notif_lmcontrol.h lmprogramthermostatgear.h \
		lmprogramcontrolwindow.h lmconstraint.h ctidate.h
lmprogramdirectgear.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h lmprogramdirectgear.h observe.h utility.h ctitime.h \
		queues.h sorted_vector.h lmid.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		rwutil.h boost_time.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h pointtypes.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmprogramenergyexchange.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h lmid.h ctitime.h lmprogrambase.h \
		dbmemobject.h observe.h utility.h queues.h sorted_vector.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		rwutil.h boost_time.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h pointtypes.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		lmprogramenergyexchange.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h msg_notif_email.h \
		msg_notif_email_attachment.h ctidate.h
lmprogramthermostatgear.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h lmprogramthermostatgear.h observe.h \
		utility.h ctitime.h queues.h sorted_vector.h \
		lmprogramdirectgear.h lmid.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		rwutil.h boost_time.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h pointtypes.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmserver.obj:	lmserver.h clistener.h clientconn.h ctdpcptrq.h \
		dlldefs.h CtiPCPtrQueue.h mutex.h guard.h numstr.h clrdump.h \
		lmmessage.h ctitime.h message.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h precompiled.h boost_time.h lmcontrolarea.h \
		dbmemobject.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h cticonnect.h netports.h logger.h \
		thread.h utility.h queues.h sorted_vector.h msg_multi.h \
		msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctibase.h ctinexus.h configparms.h
lmservice.obj:	yukon.h precompiled.h ctidbgmem.h lmservice.h \
		cservice.h dlldefs.h loadmanager.h dbaccess.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h sema.h connection.h \
		exchange.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h message.h collectable.h \
		rwutil.h boost_time.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h msg_cmd.h pointtypes.h configparms.h \
		lmcontrolareastore.h observe.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h lmid.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		clistener.h eventlog.h rtdb.h hashkey.h hash_functions.h \
		ctidate.h
loadmanager.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h connection.h exchange.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h message.h \
		collectable.h rwutil.h boost_time.h msg_multi.h msg_pdata.h \
		pointdefs.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h msg_signal.h msg_cmd.h \
		msg_pcrequest.h msg_pcreturn.h msg_dbchg.h pointtypes.h \
		configparms.h loadmanager.h lmcontrolareastore.h observe.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
		lmcontrolareatrigger.h lmid.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h devicetypes.h clistener.h
main.obj:	yukon.h precompiled.h ctidbgmem.h id_loadmanagement.h \
		utility.h ctitime.h dlldefs.h queues.h types.h numstr.h \
		sorted_vector.h lmcontrolareastore.h observe.h \
		lmcontrolarea.h dbmemobject.h connection.h exchange.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		clrdump.h cticonnect.h netports.h logger.h thread.h \
		CtiPCPtrQueue.h message.h collectable.h rwutil.h boost_time.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmid.h loadmanager.h dbaccess.h sema.h \
		pointtypes.h configparms.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		lmservice.h cservice.h clistener.h precomp.h Monitor.h \
		CServiceConfig.h rtdb.h hashkey.h hash_functions.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
#ENDUPDATE#
