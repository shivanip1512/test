include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DATABASE)\include \
-I$(PORTER)\include \
-I$(COMMON)\include \
-I$(RW) \
-I$(BOOST) \

.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROTOCOL)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)


LIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\clrdump.lib

TEST_OBJS=\
test_tbl_dv_idlcremote.obj

ALL:            $(TEST_OBJS) Makefile

deps:
                scandeps -Output maketest.mak *.cpp

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


allclean:   clean test

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy bin\*.exe $(YUKONOUTPUT)


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
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe$(BIN)\$(@B).exe \
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.

######################################################################################

#UPDATE#
almtest.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		tbl_pt_alarm.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h
dllyukon.obj:	yukon.h precompiled.h ctidbgmem.h tbl_route.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h tbl_rtcarrier.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		tbl_rtcomm.h ctibase.h ctinexus.h tbl_rtmacro.h tbl_rtroute.h \
		tbl_rtrepeater.h tbl_rtversacom.h msg_pcrequest.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h \
		tbl_state_grp.h tbl_state.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
tbl_alm_ndest.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h tbl_alm_ndest.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_alm_ngroup.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h tbl_alm_ngroup.h tbl_alm_ndest.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		rwutil.h boost_time.h boostutil.h
tbl_base.obj:	yukon.h precompiled.h ctidbgmem.h tbl_base.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h rwutil.h boost_time.h boostutil.h
tbl_carrier.obj:	yukon.h precompiled.h ctidbgmem.h tbl_carrier.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_ci_cust.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_ci_cust.h
tbl_commerrhist.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_commerrhist.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		utility.h ctitime.h queues.h sorted_vector.h logger.h \
		thread.h CtiPCPtrQueue.h rwutil.h boost_time.h boostutil.h
tbl_contact_notification.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h tbl_contact_notification.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_devicereadjoblog.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_devicereadjoblog.h ctitime.h dlldefs.h pointdefs.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h logger.h thread.h mutex.h guard.h clrdump.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		netports.h sema.h rwutil.h boost_time.h boostutil.h
tbl_devicereadrequestlog.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_devicereadrequestlog.h ctitime.h dlldefs.h pointdefs.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h logger.h thread.h mutex.h guard.h clrdump.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		netports.h sema.h rwutil.h boost_time.h boostutil.h
tbl_dialup.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dialup.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_direct.obj:	yukon.h precompiled.h ctidbgmem.h tbl_direct.h \
		dbmemobject.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_dv_address.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_address.h \
		types.h logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h sorted_vector.h dllbase.h dsm2.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h
tbl_dv_cbc.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_cbc.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_dv_emetcon.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_emetcon.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_dv_expresscom.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_dv_expresscom.h vcomdefs.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h rwutil.h boost_time.h boostutil.h
tbl_dv_idlcremote.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h tbl_dv_idlcremote.h \
		dbmemobject.h rwutil.h boost_time.h boostutil.h
tbl_dv_ied.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_ied.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_dv_lmgmct.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_lmgmct.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_dv_lmg_ripple.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h tbl_dv_lmg_ripple.h \
		dllbase.h dsm2.h cticonnect.h netports.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		rwutil.h boost_time.h boostutil.h
tbl_dv_lmvcserial.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_lmvcserial.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h vcomdefs.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_dv_mctiedport.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_mctiedport.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		rwutil.h boost_time.h boostutil.h
tbl_dv_pagingreceiver.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_pagingreceiver.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_dv_rtc.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_dv_rtc.h rwutil.h boost_time.h \
		boostutil.h
tbl_dv_scandata.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_dv_scandata.h dbmemobject.h \
		rwutil.h boost_time.h boostutil.h
tbl_dv_seriesv.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_seriesv.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_dv_tappaging.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_tappaging.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		rwutil.h boost_time.h boostutil.h
tbl_dv_tnpp.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_tnpp.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_dv_versacom.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_dv_versacom.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_dv_wnd.obj:	yukon.h precompiled.h ctidbgmem.h tbl_dv_wnd.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_dyn_paoinfo.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h rwutil.h ctitime.h boost_time.h boostutil.h utility.h \
		queues.h sorted_vector.h logger.h thread.h CtiPCPtrQueue.h \
		tbl_dyn_paoinfo.h ctibase.h ctinexus.h dbmemobject.h \
		pointdefs.h
tbl_dyn_ptalarming.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_dyn_ptalarming.h ctibase.h \
		ctinexus.h dbmemobject.h pointdefs.h rwutil.h boost_time.h \
		boostutil.h
tbl_dyn_pttag.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_dyn_pttag.h ctibase.h ctinexus.h \
		dbmemobject.h pointdefs.h rwutil.h boost_time.h boostutil.h
tbl_gateway_end_device.obj:	yukon.h precompiled.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h tbl_gateway_end_device.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		dbmemobject.h rwutil.h boost_time.h boostutil.h
tbl_lmg_golay.obj:	yukon.h precompiled.h ctidbgmem.h tbl_lmg_golay.h \
		dlldefs.h dbmemobject.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_lmg_point.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h tbl_lmg_point.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h
tbl_lmg_sa205105.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h configkey.h configval.h logger.h thread.h \
		mutex.h guard.h clrdump.h CtiPCPtrQueue.h tbl_lmg_sa205105.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h
tbl_lmg_sa305.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h tbl_lmg_sa305.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		rwutil.h boost_time.h boostutil.h
tbl_lmg_sasimple.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h tbl_lmg_sasimple.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		rwutil.h boost_time.h boostutil.h
tbl_lmprogramhistory.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_lmprogramhistory.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		CtiTime.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_lm_controlhist.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_lm_controlhist.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		utility.h ctitime.h queues.h sorted_vector.h logger.h \
		thread.h CtiPCPtrQueue.h rwutil.h boost_time.h boostutil.h \
		ctidate.h
tbl_loadprofile.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_loadprofile.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h \
		da_load_profile.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h rwutil.h boost_time.h \
		boostutil.h
tbl_metergrp.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h tbl_metergrp.h vcomdefs.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		rwutil.h boost_time.h boostutil.h
tbl_meterreadlog.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_meterreadlog.h ctitime.h dlldefs.h pointdefs.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h boostutil.h dbaccess.h dllbase.h dsm2.h \
		mutex.h guard.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h rwutil.h boost_time.h
tbl_pao.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_pao.h dbmemobject.h rwutil.h \
		boost_time.h boostutil.h
tbl_paoexclusion.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_paoexclusion.h rwutil.h \
		boost_time.h boostutil.h
tbl_paoproperty.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_paoproperty.h dlldefs.h dbaccess.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h
tbl_pao_lite.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_pao_lite.h dbmemobject.h rwutil.h \
		boost_time.h boostutil.h
tbl_port_base.obj:	yukon.h precompiled.h ctidbgmem.h tbl_port_base.h \
		dbmemobject.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_port_dialup.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_dialup.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		rwutil.h boost_time.h boostutil.h
tbl_port_serial.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_serial.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h sema.h dbmemobject.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_port_settings.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_settings.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_port_statistics.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_statistics.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h sema.h dbmemobject.h \
		ctitime.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_port_tcpip.obj:	yukon.h precompiled.h ctidbgmem.h tbl_port_tcpip.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_port_timing.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_port_timing.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_ptdispatch.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_ptdispatch.h ctibase.h \
		ctinexus.h dbmemobject.h pointdefs.h rwutil.h boost_time.h \
		boostutil.h ctidate.h
tbl_pthist.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pthist.h \
		dlldefs.h dbmemobject.h ctibase.h ctinexus.h netports.h \
		cticonnect.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h ctitime.h dbaccess.h \
		sema.h resolvers.h pointtypes.h db_entry_defines.h rwutil.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h
tbl_pt_accum.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_accum.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_pt_accumhistory.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_pt_accumhistory.h dbmemobject.h dlldefs.h dbaccess.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h
tbl_pt_alarm.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_pt_alarm.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		rwutil.h boost_time.h boostutil.h
tbl_pt_analog.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_analog.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_pt_base.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_pt_base.h dllbase.h dsm2.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		desolvers.h pointdefs.h rwutil.h boost_time.h boostutil.h
tbl_pt_calc.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_calc.h
tbl_pt_control.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_control.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_pt_limit.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_limit.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
tbl_pt_property.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_pt_property.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_pt_status.obj:	yukon.h precompiled.h ctidbgmem.h pointdefs.h \
		tbl_pt_status.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
tbl_pt_trigger.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_trigger.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_pt_unit.obj:	yukon.h precompiled.h ctidbgmem.h tbl_pt_unit.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h tbl_unitmeasure.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h
tbl_route.obj:	yukon.h precompiled.h ctidbgmem.h tbl_route.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h
tbl_rtcarrier.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtcarrier.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h dbmemobject.h rwutil.h \
		boost_time.h boostutil.h
tbl_rtcomm.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtcomm.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h dbmemobject.h ctibase.h \
		ctinexus.h rwutil.h boost_time.h boostutil.h
tbl_rtmacro.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtmacro.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h ctibase.h \
		ctinexus.h
tbl_rtrepeater.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtrepeater.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_rtroute.obj:	yukon.h precompiled.h ctidbgmem.h tbl_rtroute.h \
		dlldefs.h dbmemobject.h ctibase.h ctinexus.h netports.h \
		cticonnect.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h dbaccess.h sema.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h resolvers.h pointtypes.h db_entry_defines.h \
		rwutil.h boost_time.h boostutil.h
tbl_rtversacom.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_rtversacom.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h
tbl_scanrate.obj:	yukon.h precompiled.h ctidbgmem.h tbl_scanrate.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h rwutil.h \
		boost_time.h boostutil.h
tbl_state.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		tbl_state.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h rwutil.h boost_time.h \
		boostutil.h
tbl_state_grp.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		sema.h tbl_state_grp.h tbl_state.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		rwutil.h boost_time.h boostutil.h
tbl_stats.obj:	yukon.h precompiled.h ctidbgmem.h tbl_stats.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		logger.h thread.h CtiPCPtrQueue.h ctidate.h
tbl_tag.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_tag.h ctibase.h ctinexus.h \
		dbmemobject.h rwutil.h boost_time.h boostutil.h
tbl_taglog.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h tbl_taglog.h ctibase.h ctinexus.h \
		dbmemobject.h pointdefs.h rwutil.h boost_time.h boostutil.h
tbl_unitmeasure.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_unitmeasure.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h rwutil.h boost_time.h boostutil.h
test_tbl_dv_idlcremote.obj:	tbl_dv_idlcremote.h types.h dlldefs.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h yukon.h precompiled.h \
		ctidbgmem.h netports.h dbmemobject.h
#ENDUPDATE#
