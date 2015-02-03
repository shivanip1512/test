# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(LOADMANAGEMENT)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(SERVICE)\include \
-I$(SERVER)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(THRIFT_INCLUDE) \
-I$(MSG)\Serialization \

.PATH.cpp = .;$(R_LOADMANAGEMENT)
.PATH.H = \
.\include \
;$(COMMON)\include \
;$(LOADMANAGENT)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(MSG)\include \
;$(BOOST_INCLUDE)

LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctiseasondb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctithriftmsg.lib \
$(THRIFT_LIB)

LOADMANAGEMENT_TEST_OBJS= \
test_main.obj \
test_lmobjects.obj \
test_lmprogram.obj \
test_lm_constraintviolations.obj \
test_lmthermostatgear.obj

LOADMANAGEMENTBASEOBJS= \
$(PRECOMPILED_OBJ) \
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
lmgroupecobee.obj \
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
lmgroupdigisep.obj \
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
lmutility.obj \
sepcyclegear.obj \
septempoffsetgear.obj \
ecobeeCycleGear.obj \
loadmanager.obj \
lmprogrambeatthepeakgear.obj \
lm_message_serialization.obj \
lm_program_serialization.obj \
lm_group_serialization.obj

LOADMANAGEMENT_TEST_FULLBUILD = $[Filename,$(OBJ),LoadManagementTestFullBuild,target]


ALL:            test_loadmanagement.exe \
                lm_server_client_serialization_test.exe


$(LOADMANAGEMENT_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(LOADMANAGEMENT_TEST_OBJS)]

test_loadmanagement.exe:    $(LOADMANAGEMENT_TEST_FULLBUILD) $(LOADMANAGEMENT_TEST_OBJS)  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$(BIN)\$(_TargetF) \
        $(LOADMANAGEMENT_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(LOADMANAGEMENTBASEOBJS) $(LIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
        @%cd $(CWD)
        @echo.

lm_server_client_serialization_test.exe:    $(LOADMANAGEMENT_TEST_FULLBUILD) lm_server_client_serialization_test.obj  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$(BIN)\$(_TargetF) \
        lm_server_client_serialization_test.obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(LOADMANAGEMENTBASEOBJS) $(LIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
        @%cd $(CWD)
        @echo.

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
	$(CC) $(CCOPTS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################
#UPDATE#
test_lmobjects.obj:	devicetypes.h lmutility.h CtiTime.h dlldefs.h \
		ctidate.h lmprogrambase.h dbmemobject.h msg_multi.h \
		collectable.h msg_pdata.h yukon.h types.h ctidbgmem.h \
		pointdefs.h pointtypes.h message.h loggable.h lmgroupbase.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h msg_pcrequest.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h logger.h streamBuffer.h \
		string_util.h exception_helper.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h msg_cmd.h row_reader.h \
		database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h lmcontrolarea.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h test_reader.h lmgroupecobee.h \
		GroupControlInterface.h ecobeeControlInterface.h \
		ecobeeCycleGear.h lmprogramdirect.h lmprogramdirectgear.h \
		tbl_lmprogramhistory.h smartgearbase.h
test_lmprogram.obj:	lmprogramdirect.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h lmprogrambase.h dbmemobject.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h loggable.h lmgroupbase.h \
		msg_pcrequest.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h ctidate.h lmprogramdirectgear.h \
		lmcontrolarea.h connection.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h readers_writer_lock.h \
		connection_base.h worker_thread.h tbl_lmprogramhistory.h \
		lmprogramcontrolwindow.h lmutility.h lmconstraint.h \
		lmmessage.h ConstraintViolation.h executor.h msg_server_req.h \
		lmprogramcurtailment.h lmcurtailcustomer.h lmcicustomerbase.h \
		test_reader.h
test_lmthermostatgear.obj:	lmutility.h CtiTime.h dlldefs.h ctidate.h \
		lmprogrambase.h dbmemobject.h msg_multi.h collectable.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h loggable.h lmgroupbase.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		msg_pcrequest.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h msg_cmd.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		lmcontrolareatrigger.h lmcontrolarea.h connection.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h readers_writer_lock.h connection_base.h \
		worker_thread.h test_reader.h lmProgramThermostatGear.h \
		lmprogramdirectgear.h lmGroupExpresscom.h \
		BeatThePeakControlInterface.h BeatThePeakAlertLevel.h
test_lm_constraintviolations.obj:	ConstraintViolation.h ctitime.h \
		dlldefs.h collectable.h ctidate.h
#ENDUPDATE#

