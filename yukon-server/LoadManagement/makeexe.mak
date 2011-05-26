# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(LOADMANAGEMENT)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(SERVER)\include \
-I$(BOOST) \
-I$(RW) \
-I$(SQLAPI)\include \
-I$(ACTIVEMQ) \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(LOADMANAGENT)\include \
;$(CPARMS)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(MSG)\include \
;$(BOOST) \
;$(RW) \
;$(ACTIVEMQ) \
;$(ACTIVEMQ)\cms \
;$(ACTIVEMQ)\activemq\library \


LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctiseasondb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib

BASEOBJS= \
clientconn.obj \
clistener.obj \
executor.obj \
ConstraintViolation.obj \
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
lmgroupdigisep.obj \
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
lmutility.obj \
main.obj \
sepcyclegear.obj \
septempoffsetgear.obj \

TARGS = loadmanagement.exe


LOADMANAGEMENT_FULLBUILD = $[Filename,$(OBJ),LoadManagementFullBuild,target]


ALL:          $(TARGS)


$(LOADMANAGEMENT_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(BASEOBJS)]


loadmanagement.exe: $(LOADMANAGEMENT_FULLBUILD) $(BASEOBJS) Makefile
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(RWCPPINVOKE) $(CFLAGS) -D_DEBUG_MEMORY $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOST_LIBS) $(LINKFLAGS)
              @echo:
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
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
clientconn.obj:	yukon.h precompiled.h types.h ctidbgmem.h clientconn.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h numstr.h ctdpcptrq.h CtiPCPtrQueue.h mutex.h \
		guard.h lmmessage.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h lmcontrolarea.h \
		dbmemobject.h connection.h exchange.h logger.h thread.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h executor.h msg_server_req.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		lmcontrolareastore.h lmid.h loadmanager.h configparms.h \
		ctibase.h ctinexus.h
clistener.obj:	yukon.h precompiled.h types.h ctidbgmem.h clistener.h \
		clientconn.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h numstr.h ctdpcptrq.h \
		CtiPCPtrQueue.h mutex.h guard.h lmmessage.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		lmcontrolarea.h dbmemobject.h connection.h exchange.h \
		logger.h thread.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmcontrolareastore.h lmid.h \
		configparms.h ctibase.h ctinexus.h executor.h \
		msg_server_req.h lmprogramcurtailment.h lmcurtailcustomer.h \
		lmcicustomerbase.h
constraintviolation.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ConstraintViolation.h ctitime.h dlldefs.h lmid.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h
executor.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		msg_server_req.h dlldefs.h message.h ctitime.h collectable.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_server_resp.h msg_signal.h executor.h \
		ctdpcptrq.h CtiPCPtrQueue.h lmmessage.h clientconn.h \
		lmcontrolarea.h dbmemobject.h connection.h exchange.h \
		logger.h thread.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h clistener.h \
		lmcontrolareastore.h lmid.h loadmanager.h configparms.h \
		ctibase.h ctinexus.h lmprogramenergyexchange.h \
		lmenergyexchangeoffer.h lmenergyexchangeofferrevision.h \
		lmenergyexchangehourlyoffer.h lmenergyexchangecustomer.h \
		lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lmprogramcontrolwindow.h \
		devicetypes.h lmconstraint.h lmutility.h
lmcicustomerbase.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmcicustomerbase.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		lmid.h logger.h thread.h CtiPCPtrQueue.h loadmanager.h \
		connection.h exchange.h string_utility.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		configparms.h lmcontrolareastore.h lmcontrolarea.h \
		dbmemobject.h lmprogrambase.h lmgroupbase.h \
		lmcontrolareatrigger.h ctidate.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h ctibase.h ctinexus.h resolvers.h \
		db_entry_defines.h
lmconstraint.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		lmconstraint.h lmprogramdirect.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		mutex.h guard.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		tbl_lmprogramhistory.h lmutility.h lmmessage.h clientconn.h \
		ctdpcptrq.h ConstraintViolation.h lmprogramcontrolwindow.h \
		lmid.h mgr_season.h mgr_holiday.h
lmcontrolarea.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		msg_signal.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h lmcontrolarea.h dbmemobject.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmid.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		lmprogramcontrolwindow.h devicetypes.h loadmanager.h \
		configparms.h lmcontrolareastore.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h lmconstraint.h lmutility.h \
		database_writer.h row_writer.h
lmcontrolareastore.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		mgr_holiday.h ctidate.h dlldefs.h logger.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h CtiPCPtrQueue.h mgr_season.h msg_signal.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h lmcontrolareastore.h observe.h lmcontrolarea.h \
		dbmemobject.h connection.h exchange.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		lmprogrambase.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h lmid.h lmcurtailcustomer.h \
		lmcicustomerbase.h lmenergyexchangecustomer.h \
		lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangeoffer.h lmprogramcurtailment.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h lmprogramthermostatgear.h \
		lmprogramenergyexchange.h lmgroupversacom.h lmgroupemetcon.h \
		lmgroupexpresscom.h lmgroupmct.h lmgroupripple.h \
		lmgrouppoint.h lmgroupsa105.h lmgroupsa205.h lmgroupsa305.h \
		lmgroupsadigital.h lmgroupgolay.h lmprogramcontrolwindow.h \
		sepcyclegear.h smartgearbase.h septempoffsetgear.h \
		resolvers.h db_entry_defines.h desolvers.h devicetypes.h \
		ctibase.h ctinexus.h configparms.h msg_dbchg.h loadmanager.h \
		executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		clientconn.h ConstraintViolation.h lmfactory.h \
		tbl_paoexclusion.h database_writer.h row_writer.h
lmcontrolareatrigger.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmcontrolareatrigger.h row_reader.h observe.h \
		database_connection.h lmid.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h rwutil.h database_reader.h \
		boost_time.h boostutil.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h ctidate.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h string_utility.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		database_writer.h row_writer.h
lmcurtailcustomer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmcurtailcustomer.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		lmcicustomerbase.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h string_utility.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h msg_cmd.h \
		configparms.h lmcontrolareastore.h lmcontrolarea.h \
		dbmemobject.h lmprogrambase.h lmgroupbase.h \
		lmcontrolareatrigger.h ctidate.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h ctibase.h \
		ctinexus.h resolvers.h db_entry_defines.h database_writer.h \
		row_writer.h
lmenergyexchangecustomer.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h lmenergyexchangecustomer.h observe.h \
		lmcicustomerbase.h msg_pcrequest.h message.h collectable.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h msg_cmd.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h dbmemobject.h \
		lmprogrambase.h lmgroupbase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		clientconn.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h ctibase.h \
		ctinexus.h resolvers.h db_entry_defines.h
lmenergyexchangecustomerreply.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h lmenergyexchangecustomerreply.h observe.h \
		lmenergyexchangehourlycustomer.h row_reader.h lmid.h logger.h \
		thread.h CtiPCPtrQueue.h loadmanager.h connection.h \
		exchange.h string_utility.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h msg_cmd.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
		msg_pcrequest.h lmcontrolareatrigger.h ctidate.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h clientconn.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h resolvers.h db_entry_defines.h \
		database_writer.h row_writer.h
lmenergyexchangehourlycustomer.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h lmenergyexchangehourlycustomer.h observe.h \
		row_reader.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		configparms.h lmcontrolareastore.h lmcontrolarea.h \
		dbmemobject.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h database_writer.h row_writer.h
lmenergyexchangehourlyoffer.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h lmenergyexchangehourlyoffer.h observe.h \
		row_reader.h lmid.h logger.h thread.h CtiPCPtrQueue.h \
		loadmanager.h connection.h exchange.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h boost_time.h boostutil.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		configparms.h lmcontrolareastore.h lmcontrolarea.h \
		dbmemobject.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h database_writer.h row_writer.h
lmenergyexchangeoffer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmid.h logger.h thread.h CtiPCPtrQueue.h \
		lmenergyexchangeoffer.h observe.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		row_reader.h rwutil.h database_connection.h database_reader.h \
		boost_time.h boostutil.h database_writer.h row_writer.h
lmenergyexchangeofferrevision.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h lmenergyexchangeofferrevision.h \
		lmenergyexchangehourlyoffer.h observe.h row_reader.h lmid.h \
		logger.h thread.h CtiPCPtrQueue.h loadmanager.h connection.h \
		exchange.h string_utility.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h msg_cmd.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h dbmemobject.h lmprogrambase.h lmgroupbase.h \
		msg_pcrequest.h lmcontrolareatrigger.h ctidate.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h clientconn.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h resolvers.h db_entry_defines.h \
		database_writer.h row_writer.h
lmfactory.obj:	yukon.h precompiled.h types.h ctidbgmem.h lmfactory.h \
		lmgroupbase.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h numstr.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h msg_cmd.h \
		logger.h thread.h CtiPCPtrQueue.h resolvers.h pointtypes.h \
		db_entry_defines.h devicetypes.h lmgroupversacom.h \
		lmgroupdigisep.h lmgroupemetcon.h lmgroupexpresscom.h \
		lmgroupmct.h lmgroupripple.h lmgrouppoint.h lmgroupsa105.h \
		lmgroupsa205.h lmgroupsa305.h lmgroupsadigital.h \
		lmgroupgolay.h lmgroupmacro.h
lmgroupbase.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h lmgroupbase.h \
		boostutil.h dbmemobject.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h msg_cmd.h lmid.h \
		logger.h thread.h CtiPCPtrQueue.h loadmanager.h connection.h \
		exchange.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h ctidate.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		resolvers.h db_entry_defines.h database_writer.h row_writer.h
lmgroupdigisep.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		lmgroupdigisep.h lmgroupbase.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		dbmemobject.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h amq_connection.h critical_section.h \
		activemqcpp.h connection.h lmsepcontrolmessage.h \
		lmseprestoremessage.h
lmgroupemetcon.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmgroupemetcon.h lmgroupbase.h boostutil.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		clientconn.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmgroupexpresscom.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmgroupexpresscom.h lmgroupbase.h boostutil.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		clientconn.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h ctistring.h ctitokenizer.h
lmgroupgolay.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		lmgroupgolay.h lmgroupbase.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		dbmemobject.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h
lmgroupmacro.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h lmgroupmacro.h \
		lmgroupbase.h boostutil.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		clientconn.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmgroupmct.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h lmgroupmct.h \
		lmgroupemetcon.h lmgroupbase.h boostutil.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		clientconn.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmgrouppoint.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h lmgrouppoint.h \
		lmgroupbase.h boostutil.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		clientconn.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmgroupripple.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmgroupripple.h lmgroupbase.h boostutil.h dbmemobject.h \
		observe.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h lmprogrambase.h lmcontrolareatrigger.h \
		ctidate.h executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		clientconn.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmgroupsa105.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		lmgroupsa105.h lmgroupbase.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		dbmemobject.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h
lmgroupsa205.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		lmgroupsa205.h lmgroupbase.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		dbmemobject.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h
lmgroupsa205or105.obj:	lmgroupSA205or105.h lmgroupbase.h boostutil.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h dbmemobject.h observe.h msg_pcrequest.h \
		message.h ctidbgmem.h collectable.h rwutil.h yukon.h \
		precompiled.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h mutex.h guard.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h
lmgroupsa305.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		lmgroupsa305.h lmgroupbase.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		dbmemobject.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h
lmgroupsadigital.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		lmgroupsadigital.h lmgroupbase.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		dbmemobject.h observe.h msg_pcrequest.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h
lmgroupsadigitalorgolay.obj:	lmgroupsadigitalorgolay.h lmgroupbase.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h precompiled.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h mutex.h guard.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h msg_cmd.h lmid.h logger.h thread.h \
		CtiPCPtrQueue.h
lmgroupversacom.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmgroupbase.h boostutil.h dbmemobject.h observe.h \
		msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h msg_cmd.h lmgroupversacom.h lmid.h logger.h \
		thread.h CtiPCPtrQueue.h loadmanager.h connection.h \
		exchange.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h configparms.h \
		lmcontrolareastore.h lmcontrolarea.h lmprogrambase.h \
		lmcontrolareatrigger.h ctidate.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h
lmmessage.obj:	yukon.h precompiled.h types.h ctidbgmem.h lmmessage.h \
		ctitime.h dlldefs.h clientconn.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h ctdpcptrq.h \
		CtiPCPtrQueue.h mutex.h guard.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h lmcontrolarea.h \
		dbmemobject.h connection.h exchange.h logger.h thread.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmid.h
lmprogrambase.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmid.h lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramcontrolwindow.h loadmanager.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		configparms.h lmcontrolareastore.h lmcontrolarea.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h clientconn.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h resolvers.h db_entry_defines.h \
		mgr_holiday.h mgr_season.h lmutility.h database_writer.h \
		row_writer.h
lmprogramcontrolwindow.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmid.h lmprogramcontrolwindow.h observe.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h row_reader.h pointdefs.h \
		pointtypes.h loadmanager.h connection.h exchange.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h msg_cmd.h \
		configparms.h lmcontrolareastore.h lmcontrolarea.h \
		dbmemobject.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		lmutility.h
lmprogramcurtailment.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmid.h lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		msg_notif_email.h msg_notif_email_attachment.h \
		database_writer.h row_writer.h
lmprogramdirect.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmprogramdirect.h boostutil.h lmprogrambase.h dbmemobject.h \
		observe.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h lmgroupbase.h \
		msg_pcrequest.h msg_cmd.h lmcontrolareatrigger.h ctidate.h \
		logger.h thread.h CtiPCPtrQueue.h lmprogramdirectgear.h \
		lmcontrolarea.h connection.h exchange.h string_utility.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h tbl_lmprogramhistory.h lmgrouppoint.h \
		devicetypes.h lmid.h desolvers.h loadmanager.h configparms.h \
		lmcontrolareastore.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h msg_signal.h msg_notif_lmcontrol.h \
		lmprogramthermostatgear.h lmprogramcontrolwindow.h \
		lmconstraint.h lmutility.h database_writer.h row_writer.h \
		smartgearbase.h
lmprogramdirectgear.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmprogramdirectgear.h observe.h row_reader.h lmid.h \
		lmprogrambase.h dbmemobject.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramdirect.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmprogramenergyexchange.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmid.h lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		lmprogramenergyexchange.h lmenergyexchangeoffer.h \
		lmenergyexchangeofferrevision.h lmenergyexchangehourlyoffer.h \
		lmenergyexchangecustomer.h lmenergyexchangecustomerreply.h \
		lmenergyexchangehourlycustomer.h msg_notif_email.h \
		msg_notif_email_attachment.h
lmprogramthermostatgear.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		lmprogramthermostatgear.h observe.h lmprogramdirectgear.h \
		row_reader.h lmid.h lmprogrambase.h dbmemobject.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h database_reader.h boost_time.h \
		boostutil.h lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h loadmanager.h connection.h exchange.h \
		string_utility.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h configparms.h lmcontrolareastore.h \
		lmcontrolarea.h executor.h ctdpcptrq.h msg_server_req.h \
		lmmessage.h clientconn.h lmprogramdirect.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h
lmserver.obj:	lmserver.h clistener.h clientconn.h boostutil.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h ctdpcptrq.h CtiPCPtrQueue.h mutex.h guard.h \
		lmmessage.h message.h ctidbgmem.h collectable.h rwutil.h \
		yukon.h precompiled.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		lmcontrolarea.h dbmemobject.h connection.h exchange.h \
		logger.h thread.h string_utility.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h observe.h lmprogrambase.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h ctibase.h ctinexus.h configparms.h
lmservice.obj:	yukon.h precompiled.h types.h ctidbgmem.h lmservice.h \
		cservice.h dlldefs.h loadmanager.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h msg_cmd.h \
		configparms.h lmcontrolareastore.h observe.h lmcontrolarea.h \
		dbmemobject.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		lmcontrolareatrigger.h ctidate.h lmid.h executor.h \
		ctdpcptrq.h msg_server_req.h lmmessage.h clientconn.h \
		lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h clistener.h id_loadmanagement.h \
		eventlog.h rtdb.h hashkey.h hash_functions.h
lmutility.obj:	yukon.h precompiled.h types.h ctidbgmem.h timeperiod.h \
		dlldefs.h lmutility.h CtiTime.h ctidate.h logger.h thread.h \
		mutex.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		numstr.h CtiPCPtrQueue.h lmprogrambase.h dbmemobject.h \
		observe.h msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h lmcontrolarea.h connection.h \
		exchange.h string_utility.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h lmprogramcontrolwindow.h
loadmanager.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		msg_signal.h msg_cmd.h msg_pcrequest.h msg_pcreturn.h \
		msg_dbchg.h configparms.h loadmanager.h lmcontrolareastore.h \
		observe.h lmcontrolarea.h dbmemobject.h lmprogrambase.h \
		lmgroupbase.h lmcontrolareatrigger.h ctidate.h lmid.h \
		executor.h ctdpcptrq.h msg_server_req.h lmmessage.h \
		clientconn.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h ConstraintViolation.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		ctibase.h ctinexus.h resolvers.h db_entry_defines.h \
		devicetypes.h clistener.h debug_timer.h
main.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		lmcontrolareastore.h observe.h dlldefs.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h lmcontrolarea.h \
		dbmemobject.h connection.h exchange.h dllbase.h dsm2.h \
		mutex.h guard.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h lmprogrambase.h lmgroupbase.h msg_pcrequest.h \
		msg_cmd.h lmcontrolareatrigger.h ctidate.h lmid.h \
		loadmanager.h configparms.h executor.h ctdpcptrq.h \
		msg_server_req.h lmmessage.h clientconn.h lmprogramdirect.h \
		lmprogramdirectgear.h tbl_lmprogramhistory.h \
		ConstraintViolation.h lmprogramcurtailment.h \
		lmcurtailcustomer.h lmcicustomerbase.h ctibase.h ctinexus.h \
		lmservice.h cservice.h clistener.h precomp.h Monitor.h \
		CServiceConfig.h rtdb.h hashkey.h hash_functions.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
sepcyclegear.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		sepcyclegear.h lmprogramdirect.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		mutex.h guard.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		tbl_lmprogramhistory.h smartgearbase.h
septempoffsetgear.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		septempoffsetgear.h lmprogramdirect.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		lmprogrambase.h dbmemobject.h observe.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		mutex.h guard.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		tbl_lmprogramhistory.h lmprogramthermostatgear.h \
		smartgearbase.h ctistring.h
test_lmprogram.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		lmprogramdirect.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h numstr.h lmprogrambase.h \
		dbmemobject.h observe.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		lmgroupbase.h msg_pcrequest.h msg_cmd.h \
		lmcontrolareatrigger.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h lmprogramdirectgear.h lmcontrolarea.h \
		connection.h exchange.h string_utility.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		tbl_lmprogramhistory.h lmprogramcontrolwindow.h lmutility.h \
		lmconstraint.h lmmessage.h clientconn.h ctdpcptrq.h \
		ConstraintViolation.h executor.h msg_server_req.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h
test_lm_constraintviolations.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h ConstraintViolation.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h
#ENDUPDATE#
