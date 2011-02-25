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
;$(MSG)\include \
;$(BOOST) \
;$(RW)

LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctiseasondb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib

LOADMANAGEMENTTESTOBJS= \
test_lmprogram.obj \
test_lm_constraintviolations.obj

LOADMANAGEMENTBASEOBJS= \
$(OBJ)\clientconn.obj \
$(OBJ)\clistener.obj \
$(OBJ)\executor.obj \
$(OBJ)\ConstraintViolation.obj \
$(OBJ)\lmconstraint.obj \
$(OBJ)\lmcontrolarea.obj \
$(OBJ)\lmcontrolareastore.obj \
$(OBJ)\lmcontrolareatrigger.obj \
$(OBJ)\lmcicustomerbase.obj \
$(OBJ)\lmcurtailcustomer.obj \
$(OBJ)\lmenergyexchangecustomer.obj \
$(OBJ)\lmenergyexchangecustomerreply.obj \
$(OBJ)\lmenergyexchangehourlycustomer.obj \
$(OBJ)\lmenergyexchangehourlyoffer.obj \
$(OBJ)\lmenergyexchangeoffer.obj \
$(OBJ)\lmenergyexchangeofferrevision.obj \
$(OBJ)\lmfactory.obj \
$(OBJ)\lmgroupbase.obj \
$(OBJ)\lmgroupemetcon.obj \
$(OBJ)\lmgroupexpresscom.obj \
$(OBJ)\lmgroupmacro.obj \
$(OBJ)\lmgroupmct.obj \
$(OBJ)\lmgrouppoint.obj \
$(OBJ)\lmgroupripple.obj \
$(OBJ)\lmgroupsa305.obj \
$(OBJ)\lmgroupsa105.obj \
$(OBJ)\lmgroupsa205.obj \
$(OBJ)\lmgroupsadigital.obj \
$(OBJ)\lmgroupdigisep.obj \
$(OBJ)\lmgroupgolay.obj \
$(OBJ)\lmgroupversacom.obj \
$(OBJ)\lmmessage.obj \
$(OBJ)\lmprogrambase.obj \
$(OBJ)\lmprogramcontrolwindow.obj \
$(OBJ)\lmprogramcurtailment.obj \
$(OBJ)\lmprogramdirect.obj \
$(OBJ)\lmprogramdirectgear.obj \
$(OBJ)\lmprogramenergyexchange.obj \
$(OBJ)\lmprogramthermostatgear.obj \
$(OBJ)\lmservice.obj \
$(OBJ)\lmutility.obj \
$(OBJ)\sepcyclegear.obj \
$(OBJ)\loadmanager.obj

ALL: loadmanagement

loadmanagement:  $(LOADMANAGEMENTTESTOBJS) maketest.mak

copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
        scandeps -Output maketest.mak test_*.cpp


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

	@echo:
	@echo Creating Executable $(OBJ)\$(@B).exe
        @echo:
	$(CC) $(CFLAGS) $(INCLPATHS) $(PCHFLAGS) $(RWCPPFLAGS) $(RWLINKFLAGS)  /Fe$(BIN)\$(@B).exe \
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(LOADMANAGEMENTBASEOBJS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################
#UPDATE#
test_lmprogram.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		lmprogramdirect.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h numstr.h sorted_vector.h \
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
		tbl_lmprogramhistory.h lmprogramcontrolwindow.h lmutility.h \
		lmconstraint.h lmmessage.h clientconn.h ctdpcptrq.h \
		ConstraintViolation.h executor.h msg_server_req.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h
test_lm_constraintviolations.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h ConstraintViolation.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h sorted_vector.h \
		CtiPCPtrQueue.h
#ENDUPDATE#

