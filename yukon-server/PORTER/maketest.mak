# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(CAPCONTROL)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(PROCLOG)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(SERVICE)\include \
-I$(SERVER)\include \
-I$(RW) \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


.PATH.cpp = .
.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CAPCONTROL)\include \
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
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\portglob.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(OPENSSL_LIBS)

PORTER_TEST_OBJS= \
test_main.obj \
test_LantronixEncryption.obj \
test_gen_reply.obj \
test_PaoStatisticsRecord.obj

PORTERBASEOBJS= \
encryption_lantronix.obj \
PaoStatisticsRecord.obj \
plidlc.obj

PORTER_TEST_FULLBUILD = $[Filename,$(OBJ),PorterTestFullBuild,target]


ALL:            test_porter.exe

$(PORTER_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(PORTER_TEST_OBJS)]

test_porter.exe:    $(PORTER_TEST_FULLBUILD) $(PORTER_TEST_OBJS)  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(_TargetF) \
        $(PORTER_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(PORTERBASEOBJS) $(RWLIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	$(MANIFEST_TOOL) -manifest $(BIN)\$(_TargetF).manifest -outputresource:$(BIN)\$(_TargetF);1
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
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################
#UPDATE#
test_gen_reply.obj:	trx_711.h trx_info.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h porter.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h mutex.h dsm2err.h words.h optional.h macro_offset.h \
		devicetypes.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		dllbase.h message.h collectable.h tbl_pao_lite.h row_reader.h \
		tbl_rtcomm.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h \
		tbl_static_paoinfo.h pointdefs.h database_connection.h \
		rwutil.h database_reader.h boost_time.h encryption.h \
		port_base.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h logManager.h module_util.h tbl_port_base.h \
		xfer.h counter.h cparms.h configkey.h configval.h
test_lantronixencryption.obj:	encryption_lantronix.h EncodingFilter.h
test_paostatistics.obj:	PaoStatistics.h PaoStatisticsRecord.h \
		ctitime.h dlldefs.h yukon.h types.h ctidbgmem.h test_reader.h \
		row_reader.h utility.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h
test_paostatisticsrecord.obj:	PaoStatisticsRecord.h ctitime.h \
		dlldefs.h yukon.h types.h ctidbgmem.h ctidate.h
#ENDUPDATE#

