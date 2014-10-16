#Build name MUST BE FIRST!!!!

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \
-I$(ACTIVEMQ)\include \
-I$(OPENSSL)\include \

.PATH.H = \
.\include \
;$(COMMON)\include \

COMMON_TEST_OBJS=\
test_main.obj \
test_CtiPCPtrQueue.obj \
test_PointAttribute.obj \
test_cmdparse.obj \
test_compiler_behaviors.obj \
test_ctidate.obj \
test_ctistring.obj \
test_ctitime.obj \
test_date_utility.obj \
test_desolvers.obj \
test_encryption.obj \
test_error.obj \
test_hash.obj \
test_logger.obj \
test_multiset.obj \
test_numstr.obj \
test_old_queues.obj \
test_queue.obj \
test_readers_writer_lock.obj \
test_resolvers.obj \
test_rwutil.obj \
test_string_utility.obj \
test_timeperiod.obj \
test_utility.obj \
test_words.obj \
test_atomic.obj \
test_string_formatter.obj \

SQLAPILIB=$(SQLAPI)\lib\$(SQLAPI_LIB).lib

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib \
$(OPENSSL_LIBS) \

COMMON_TEST_FULLBUILD = $[Filename,$(OBJ),CommonTestFullBuild,target]


ALL:            test_common.exe cmdparsetestgenerator.exe

$(COMMON_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(COMMON_TEST_OBJS)]

test_common.exe:    $(COMMON_TEST_FULLBUILD) $(COMMON_TEST_OBJS)  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(_TargetF) \
        $(COMMON_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LINKFLAGS) $(OPENSSL_LIBS) advapi32.lib
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	$(MANIFEST_TOOL) -manifest $(BIN)\$(_TargetF).manifest -outputresource:$(BIN)\$(_TargetF);1
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
        @%cd $(CWD)
        @echo.

cmdparsetestgenerator.exe: cmdparsetestgenerator.obj
        @echo:
        @echo Creating Executable $(BIN)\$(@B).exe
        @echo:
        $(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe$(BIN)\$(@B).exe \
        $(OBJ)\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(LINKFLAGS)

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	$(MANIFEST_TOOL) -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
        -copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
        -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
        -if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
        @%cd $(CWD)
        @echo.


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

#UPDATE#
attributeservice.obj:	precompiled.h AttributeService.h LitePoint.h \
		dlldefs.h pointtypes.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h resolvers.h db_entry_defines.h numstr.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h database_reader.h \
		database_connection.h row_reader.h
beatthepeakalertlevel.obj:	precompiled.h BeatThePeakAlertLevel.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h constants.h numstr.h
bfexec.obj:	precompiled.h bfexec.h
cmdparse.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h streamBuffer.h string_util.h \
		exception_helper.h pointdefs.h ctistring.h std_helper.h
cmdparsetestgenerator.obj:	cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h test_cmdparse_input.h
configkey.obj:	precompiled.h configkey.h
configval.obj:	precompiled.h configval.h
counter.obj:	precompiled.h counter.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h constants.h \
		numstr.h mutex.h
cparms.obj:	precompiled.h ctistring.h dlldefs.h cparms.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h encryption.h
critical_section.obj:	precompiled.h critical_section.h dlldefs.h
ctdpcptrq.obj:	precompiled.h ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h constants.h numstr.h
cticalls.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		dllbase.h dsm2.h streamConnection.h yukon.h ctidbgmem.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h logger.h streamBuffer.h string_util.h \
		exception_helper.h
ctidate.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h exception_helper.h
ctistring.obj:	precompiled.h ctistring.h dlldefs.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h constants.h numstr.h
ctitime.obj:	precompiled.h ctidate.h dlldefs.h ctitime.h
ctitokenizer.obj:	precompiled.h ctitokenizer.h dlldefs.h
cti_asmc.obj:	precompiled.h cti_asmc.h dlldefs.h cticalls.h os2_2w32.h \
		types.h
database_connection.obj:	precompiled.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_exceptions.h logger.h streamBuffer.h \
		string_util.h exception_helper.h std_helper.h
database_reader.obj:	precompiled.h database_reader.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h row_reader.h logger.h \
		streamBuffer.h string_util.h exception_helper.h CParms.h \
		rwutil.h boost_time.h boostutil.h configkey.h configval.h
database_transaction.obj:	precompiled.h database_transaction.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h
database_util.obj:	precompiled.h database_exceptions.h database_util.h \
		database_writer.h ctitime.h dlldefs.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h row_writer.h \
		database_reader.h row_reader.h logger.h streamBuffer.h \
		string_util.h exception_helper.h
database_writer.obj:	precompiled.h database_writer.h ctitime.h \
		dlldefs.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h row_writer.h ctidate.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boost_time.h
date_utility.obj:	precompiled.h date_utility.h ctidate.h dlldefs.h \
		ctitokenizer.h
dbaccess.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h logger.h streamBuffer.h \
		string_util.h exception_helper.h
debug_timer.obj:	precompiled.h debug_timer.h dlldefs.h logger.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h exception_helper.h
desolvers.obj:	precompiled.h desolvers.h dlldefs.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h resolvers.h pointtypes.h \
		db_entry_defines.h devicetypes.h logger.h streamBuffer.h \
		string_util.h dllbase.h exception_helper.h
deviceattributelookup.obj:	precompiled.h DeviceAttributeLookup.h \
		devicetypes.h pointtypes.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h
deviceconfigdescription.obj:	precompiled.h DeviceConfigDescription.h \
		devicetypes.h pointtypes.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h std_helper.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h
dllbase.obj:	precompiled.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbaccess.h dllbase.h streamSocketConnection.h socket_helper.h \
		win_helper.h logger.h streamBuffer.h string_util.h \
		exception_helper.h encryption.h json.h xml.h devicetypes.h \
		pointtypes.h resource_helper.h PointAttribute.h \
		DeviceConfigDescription.h thread_monitor.h smartmap.h \
		boostutil.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h queue.h string_utility.h thread.h \
		thread_register_data.h
elog_cli.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		dsm2.h streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h elogger.h logger.h streamBuffer.h \
		string_util.h dllbase.h exception_helper.h
encryption.obj:	precompiled.h encryption.h dlldefs.h ctistring.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h constants.h numstr.h
error.obj:	precompiled.h dsm2err.h dlldefs.h yukon.h types.h \
		ctidbgmem.h constants.h
exception_helper.obj:	precompiled.h streamBuffer.h dlldefs.h \
		exception_helper.h
fileint.obj:	precompiled.h fileint.h dlldefs.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h
guard.obj:	precompiled.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h constants.h numstr.h
hash_functions.obj:	precompiled.h hash_functions.h dlldefs.h
id_ctibase.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h \
		id_ctibase.h
json.obj:	precompiled.h MetricIdLookup.h PointAttribute.h yukon.h \
		types.h ctidbgmem.h dlldefs.h resource_helper.h
litepoint.obj:	precompiled.h LitePoint.h dlldefs.h pointtypes.h
logfileappender.obj:	precompiled.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h constants.h numstr.h \
		logFileAppender.h logManager.h logger.h streamBuffer.h \
		string_util.h dllbase.h dsm2.h streamConnection.h yukon.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h ctidate.h
logger.obj:	precompiled.h logManager.h dlldefs.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h exception_helper.h
loglayout.obj:	precompiled.h ctitime.h dlldefs.h logLayout.h
logmanager.obj:	precompiled.h ctistring.h dlldefs.h logLayout.h \
		logFileAppender.h logManager.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h exception_helper.h ctidate.h
macro_offset.obj:	precompiled.h macro_offset.h dlldefs.h
master.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		cti_asmc.h queues.h constants.h dsm2.h streamConnection.h \
		yukon.h ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h master.h
metricidlookup.obj:	precompiled.h MetricIdLookup.h PointAttribute.h \
		yukon.h types.h ctidbgmem.h dlldefs.h
millisecond_timer.obj:	precompiled.h millisecond_timer.h dlldefs.h
mutex.obj:	precompiled.h mutex.h dlldefs.h
numstr.obj:	precompiled.h numstr.h dlldefs.h
observe.obj:	precompiled.h observe.h types.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h
parse.obj:	precompiled.h
pexec.obj:	precompiled.h porter.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h devicetypes.h logger.h streamBuffer.h \
		string_util.h dllbase.h exception_helper.h \
		streamSocketConnection.h socket_helper.h win_helper.h
pointattribute.obj:	precompiled.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h
queue.obj:	precompiled.h queue.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h logger.h streamBuffer.h \
		string_util.h exception_helper.h string_utility.h
queues.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		logger.h streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h exception_helper.h
readers_writer_lock.obj:	precompiled.h readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h
regression.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h logger.h streamBuffer.h \
		string_util.h exception_helper.h regression.h
repeaterrole.obj:	precompiled.h repeaterrole.h dlldefs.h
resolvers.obj:	precompiled.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		resolvers.h pointtypes.h db_entry_defines.h devicetypes.h \
		logger.h streamBuffer.h string_util.h dllbase.h \
		exception_helper.h std_helper.h
resource_helper.obj:	precompiled.h resource_helper.h dlldefs.h \
		logger.h streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h exception_helper.h
rtdb.obj:	precompiled.h dlldefs.h rtdb.h hashkey.h hash_functions.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		constants.h numstr.h string_utility.h dllbase.h dsm2.h \
		streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
sema.obj:	precompiled.h sema.h dlldefs.h
stdexcepthdlr.obj:	precompiled.h stdexcepthdlr.h dlldefs.h
streambuffer.obj:	precompiled.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h streamBuffer.h ctidate.h
streamlocalconnection.obj:	precompiled.h cparms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h logger.h streamBuffer.h \
		string_util.h exception_helper.h millisecond_timer.h \
		win_helper.h streamLocalConnection.h
streamsocketconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h exception_helper.h \
		std_helper.h win_helper.h millisecond_timer.h \
		streamSocketConnection.h socket_helper.h
streamsocketlistener.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h exception_helper.h \
		std_helper.h win_helper.h millisecond_timer.h \
		streamSocketListener.h socket_helper.h \
		streamSocketConnection.h
string_util.obj:	precompiled.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h streamBuffer.h
string_utility.obj:	precompiled.h string_utility.h dlldefs.h
test_cmdparse.obj:	cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h \
		test_cmdparse_input.h test_cmdparse_output.h
test_ctidate.obj:	ctidate.h dlldefs.h ctitime.h
test_ctipcptrqueue.obj:	ctipcptrqueue.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		constants.h numstr.h
test_ctistring.obj:	ctistring.h dlldefs.h
test_ctitime.obj:	ctitime.h dlldefs.h ctidate.h boost_test_helpers.h \
		millisecond_timer.h
test_date_utility.obj:	date_utility.h ctidate.h dlldefs.h
test_desolvers.obj:	desolvers.h dlldefs.h devicetypes.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		constants.h numstr.h
test_encryption.obj:	encryption.h dlldefs.h
test_hash.obj:	hashkey.h hash_functions.h dlldefs.h
test_logger.obj:	logManager.h dlldefs.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h logger.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h exception_helper.h ctidate.h
test_multiset.obj:	utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h constants.h numstr.h
test_numstr.obj:	numstr.h dlldefs.h
test_old_queues.obj:	dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h
test_pointattribute.obj:	PointAttribute.h yukon.h types.h ctidbgmem.h \
		dlldefs.h
test_queue.obj:	queue.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h streamBuffer.h string_util.h \
		exception_helper.h string_utility.h
test_readers_writer_lock.obj:	readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h
test_resolvers.obj:	dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		resolvers.h pointtypes.h db_entry_defines.h devicetypes.h \
		logger.h streamBuffer.h string_util.h dllbase.h \
		exception_helper.h
test_rwutil.obj:	rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h dlldefs.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h database_reader.h row_reader.h boost_time.h \
		boostutil.h ctidate.h
test_string_formatter.obj:	string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h streamBuffer.h
test_string_utility.obj:	string_utility.h dlldefs.h
test_timeperiod.obj:	timeperiod.h dlldefs.h ctitime.h ctidate.h
test_utility.obj:	utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h constants.h numstr.h ctidate.h dsm2.h \
		streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h devicetypes.h
tfexec.obj:	precompiled.h tfexec.h
thread.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h thread.h
threadstatuskeeper.obj:	precompiled.h ThreadStatusKeeper.h ctitime.h \
		dlldefs.h thread_register_data.h boost_time.h cticalls.h \
		os2_2w32.h types.h boostutil.h utility.h queues.h constants.h \
		numstr.h thread_monitor.h smartmap.h dllbase.h dsm2.h \
		streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h configkey.h configval.h queue.h logger.h \
		streamBuffer.h string_util.h exception_helper.h \
		string_utility.h thread.h
thread_listener.obj:	precompiled.h thread_listener.h thread.h mutex.h \
		dlldefs.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h constants.h numstr.h
thread_monitor.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h logger.h streamBuffer.h \
		string_util.h exception_helper.h thread_monitor.h smartmap.h \
		boostutil.h cparms.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		configkey.h configval.h queue.h string_utility.h thread.h \
		thread_register_data.h
thread_register_data.obj:	precompiled.h thread_register_data.h \
		boost_time.h cticalls.h os2_2w32.h dlldefs.h types.h \
		boostutil.h utility.h ctitime.h queues.h constants.h numstr.h
thread_timer.obj:	precompiled.h thread_timer.h thread.h mutex.h \
		dlldefs.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h constants.h numstr.h \
		thread_register_data.h boost_time.h boostutil.h
timeperiod.obj:	precompiled.h ctitime.h dlldefs.h timeperiod.h
timing_util.obj:	precompiled.h numstr.h dlldefs.h timing_util.h
ucttime.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h elogger.h
utility.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h database_reader.h \
		database_connection.h row_reader.h database_transaction.h \
		database_writer.h row_writer.h database_util.h logger.h \
		streamBuffer.h string_util.h exception_helper.h pointdefs.h \
		ctidate.h devicetypes.h
verification_objects.obj:	precompiled.h verification_objects.h \
		dlldefs.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		boost_time.h
words.obj:	precompiled.h words.h dlldefs.h cticalls.h os2_2w32.h \
		types.h optional.h cti_asmc.h yukon.h ctidbgmem.h logger.h \
		streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h constants.h numstr.h mutex.h dsm2err.h \
		macro_offset.h exception_helper.h
worker_thread.obj:	precompiled.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h constants.h numstr.h \
		logger.h streamBuffer.h string_util.h dllbase.h dsm2.h \
		streamConnection.h yukon.h ctidbgmem.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h exception_helper.h worker_thread.h
xfer.obj:	precompiled.h xfer.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
xml.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h string_util.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h xml.h devicetypes.h pointtypes.h \
		resource_helper.h PointAttribute.h DeviceConfigDescription.h \
		DeviceAttributeLookup.h resolvers.h db_entry_defines.h \
		std_helper.h
#ENDUPDATE#
