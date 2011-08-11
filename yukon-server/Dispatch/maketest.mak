
# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(SIGNAL)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(BOOST) \
-I$(SQLAPI)\include \
-I$(RW) \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
;$(RTDB)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \
;$(BOOST) \
;$(RW)



LIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\ctidbres.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctivg.lib


DISPATCHTESTOBJS= \
test_signalmanager.obj \
test_mgr_ptclients.obj \
test_vangogh.obj

TARGS = dispatch.exe

ALL:      dispatch

dispatch:  $(DISPATCHTESTOBJS) Makefile

copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
        scandeps -Output maketest.mak test_*.cpp

clean:
	-del \
	test*.pdb \
	$(OBJ)\test*.obj \
	$(BIN)\test*.pdb \
	$(BIN)\test*.pch \
	$(BIN)\test*.ilk \
	$(BIN)\test*.exp \
	$(BIN)\test*.lib \
	$(BIN)\test*.dll \
	$(BIN)\test*.exe


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
	.\obj\$(@B).obj -link /subsystem:console $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################
#UPDATE#
test_mgr_ptclients.obj:	mgr_ptclients.h dlldefs.h mgr_point.h \
		pt_base.h yukon.h types.h ctidbgmem.h row_reader.h ctitime.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		pointdefs.h tbl_pt_base.h dllbase.h dsm2.h cticonnect.h \
		netports.h mutex.h guard.h dsm2err.h words.h optional.h \
		dbaccess.h sema.h desolvers.h tbl_pt_property.h \
		tbl_pt_trigger.h smartmap.h readers_writer_lock.h \
		critical_section.h msg_pdata.h message.h collectable.h \
		rwutil.h database_connection.h database_reader.h boost_time.h \
		ptconnect.h hashkey.h hash_functions.h con_mgr.h connection.h \
		exchange.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		msg_multi.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h ctibase.h ctinexus.h server_b.h \
		pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h \
		tbl_pt_limit.h rtdb.h tbl_rawpthistory.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h
test_signalmanager.obj:	precompiled.h tbl_pt_alarm.h dlldefs.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h row_reader.h signalmanager.h \
		msg_signal.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h
test_vangogh.obj:	ctivangogh.h con_mgr.h connection.h dlldefs.h \
		exchange.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h logger.h thread.h CtiPCPtrQueue.h string_utility.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ctibase.h ctinexus.h con_mgr_vg.h vgexe_factory.h \
		executor.h exe_ptchg.h executorfactory.h exe_cmd.h exe_reg.h \
		msg_cmd.h server_b.h critical_Section.h smartmap.h \
		readers_writer_lock.h dev_base_lite.h dbmemobject.h \
		msg_dbchg.h msg_multiwrap.h msg_pcreturn.h \
		msg_commerrorhistory.h msg_lmcontrolhistory.h msg_tag.h \
		pendingopthread.h pendable.h pending_info.h msg_signal.h \
		tbl_lm_controlhist.h pt_numeric.h pt_base.h resolvers.h \
		db_entry_defines.h tbl_pt_base.h desolvers.h \
		tbl_pt_property.h tbl_pt_trigger.h tbl_pt_unit.h \
		tbl_unitmeasure.h signalmanager.h pt_dyn_dispatch.h \
		tbl_pt_alarm.h tbl_ptdispatch.h pt_status.h tbl_pt_status.h \
		pttrigger.h mgr_point.h tagmanager.h tbl_dyn_pttag.h \
		tbl_tag.h tbl_taglog.h tbl_state_grp.h tbl_state.h \
		tbl_alm_ngroup.h tbl_commerrhist.h tbl_pt_limit.h \
		tbl_rawpthistory.h tbl_signal.h tbl_ci_cust.h \
		tbl_contact_notification.h rtdb.h hashkey.h hash_functions.h \
		ctidate.h
#ENDUPDATE#

