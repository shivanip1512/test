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
-I$(CPARMS)\include \
-I$(SERVER)\include \
-I$(RW) \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


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
	mt.exe -manifest $(BIN)\$(_TargetF).manifest -outputresource:$(BIN)\$(_TargetF);1
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
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################
#UPDATE#
test_gen_reply.obj:	trx_711.h trx_info.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		critical_section.h porter.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		devicetypes.h string_utility.h portdecl.h rte_base.h \
		boostutil.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h dllbase.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_static_paoinfo.h pointdefs.h \
		port_base.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h tbl_port_base.h xfer.h counter.h cparms.h \
		configkey.h configval.h
test_lantronixencryption.obj:	encryption_lantronix.h EncodingFilter.h
test_paostatisticsrecord.obj:	PaoStatisticsRecord.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
#ENDUPDATE#

