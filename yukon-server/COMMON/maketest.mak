#Build name MUST BE FIRST!!!!

!include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(ACTIVEMQ)\include \
-I$(OPENSSL)\include \

.PATH.H = \
.\include \
;$(COMMON)\include \

COMMON_TEST_OBJS=\
$(PRECOMPILED_OBJ) \
test_main.obj \
test_CtiPCPtrQueue.obj \
test_PointAttribute.obj \
test_cmdparse.obj \
test_compiler_behaviors.obj \
test_ctidate.obj \
test_ctitime.obj \
test_date_utility.obj \
test_database_util.obj \
test_dbaccess.obj \
test_desolvers.obj \
test_encryption.obj \
test_error.obj \
test_logger.obj \
test_multiset.obj \
test_numstr.obj \
test_old_queues.obj \
test_queue.obj \
test_readers_writer_lock.obj \
test_resolvers.obj \
test_timeperiod.obj \
test_utility.obj \
test_words.obj \
test_std_helper.obj \
test_string_formatter.obj \
test_string_util.obj \

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib \
$(OPENSSL_LIBS) \

COMMON_TEST_FULLBUILD = $[Filename,$(OBJ),CommonTestFullBuild,target]


ALL:            test_common.exe cmdparsetestgenerator.exe

$(COMMON_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(COMMON_TEST_OBJS)]

test_common.exe:    $(COMMON_TEST_FULLBUILD) $(COMMON_TEST_OBJS)  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS)  /Fe..\$(BIN)\$(_TargetF) \
        $(COMMON_TEST_OBJS) -link /LARGEADDRESSAWARE /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(OPENSSL_LIBS) advapi32.lib
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
	-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
        -copy $(BIN)\*.pdb $(YUKONDEBUG)
        @%cd $(CWD)
        @echo.

cmdparsetestgenerator.exe: cmdparsetestgenerator.obj
        @echo:
        @echo Creating Executable $(BIN)\$(@B).exe
        @echo:
        $(CC) $(CFLAGS) $(INCLPATHS)  /Fe$(BIN)\$(@B).exe \
        $(OBJ)\$(@B).obj -link /LARGEADDRESSAWARE /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(LINKFLAGS)

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
        -copy $(BIN)\$(@B).pdb $(YUKONDEBUG)
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
		-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
                -copy bin\*.pdb $(YUKONDEBUG)


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
        $(CC) $(CCOPTS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

#UPDATE#
attributeservice.obj:	precompiled.h AttributeService.h LitePoint.h \
		dlldefs.h pointtypes.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h resolvers.h db_entry_defines.h numstr.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h critical_section.h \
		database_reader.h database_connection.h guard.h utility.h \
		ctitime.h queues.h constants.h module_util.h version.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h row_reader.h
beatthepeakalertlevel.obj:	precompiled.h BeatThePeakAlertLevel.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h
cmdparse.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h cparms.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h pointdefs.h std_helper.h
cmdparsetestgenerator.obj:	cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h test_cmdparse_input.h
connectionhandle.obj:	precompiled.h connectionHandle.h dlldefs.h \
		loggable.h
counter.obj:	precompiled.h counter.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h mutex.h
cparms.obj:	precompiled.h cparms.h dlldefs.h utility.h ctitime.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h encryption.h \
		logManager.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h \
		std_helper.h
critical_section.obj:	precompiled.h critical_section.h dlldefs.h
ctdpcptrq.obj:	precompiled.h ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		critical_section.h
cticalls.obj:	precompiled.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h dlldefs.h dllbase.h critical_section.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h constants.h numstr.h \
		module_util.h version.h win_helper.h dsm2.h \
		connectionHandle.h streamConnection.h timing_util.h \
		immutable.h guard.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
ctidate.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h
ctitime.obj:	precompiled.h ctidate.h dlldefs.h ctitime.h std_helper.h \
		utility.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		streamBuffer.h loggable.h
ctitokenizer.obj:	precompiled.h ctitokenizer.h dlldefs.h
cti_asmc.obj:	precompiled.h cti_asmc.h dlldefs.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h
database_connection.obj:	precompiled.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h guard.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h database_exceptions.h \
		std_helper.h
database_reader.obj:	precompiled.h database_reader.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h row_reader.h CParms.h
database_transaction.obj:	precompiled.h database_transaction.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h
database_util.obj:	precompiled.h database_exceptions.h database_util.h \
		database_writer.h ctitime.h dlldefs.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h guard.h utility.h queues.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h row_writer.h database_reader.h row_reader.h
database_writer.obj:	precompiled.h database_writer.h ctitime.h \
		dlldefs.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		row_writer.h ctidate.h boost_time.h
date_utility.obj:	precompiled.h date_utility.h ctidate.h dlldefs.h \
		ctitokenizer.h
dbaccess.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h guard.h
debug_timer.obj:	precompiled.h debug_timer.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h
desolvers.obj:	precompiled.h desolvers.h dlldefs.h pointtypes.h dsm2.h \
		connectionHandle.h loggable.h streamConnection.h yukon.h \
		types.h ctidbgmem.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h resolvers.h db_entry_defines.h \
		devicetypes.h std_helper.h
deviceattributelookup.obj:	precompiled.h DeviceAttributeLookup.h \
		devicetypes.h pointtypes.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h
deviceconfigdescription.obj:	precompiled.h DeviceConfigDescription.h \
		devicetypes.h pointtypes.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h std_helper.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h streamBuffer.h loggable.h
dllbase.obj:	precompiled.h dsm2.h connectionHandle.h dlldefs.h \
		loggable.h streamConnection.h yukon.h types.h ctidbgmem.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		dbaccess.h dllbase.h streamSocketConnection.h socket_helper.h \
		win_helper.h encryption.h json.h xml.h devicetypes.h \
		pointtypes.h resource_helper.h PointAttribute.h \
		DeviceConfigDescription.h thread_monitor.h smartmap.h \
		readers_writer_lock.h cparms.h queue.h thread.h \
		thread_register_data.h boost_time.h
elog_cli.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h dsm2.h connectionHandle.h loggable.h \
		streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h elogger.h
encryption.obj:	precompiled.h encryption.h dlldefs.h string_util.h \
		streamBuffer.h loggable.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h logger.h \
		exception_helper.h boostutil.h critical_section.h
error.obj:	precompiled.h dsm2err.h dlldefs.h yukon.h types.h \
		ctidbgmem.h constants.h numstr.h std_helper.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h module_util.h \
		version.h streamBuffer.h loggable.h
exception_helper.obj:	precompiled.h streamBuffer.h dlldefs.h \
		loggable.h exception_helper.h
fileint.obj:	precompiled.h fileint.h dlldefs.h critical_section.h \
		worker_thread.h timing_util.h loggable.h ctitime.h \
		concurrentSet.h dllbase.h os2_2w32.h types.h cticalls.h \
		yukon.h ctidbgmem.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h guard.h
globalsettings.obj:	precompiled.h GlobalSettings.h dlldefs.h \
		critical_section.h utility.h ctitime.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h database_connection.h dbaccess.h \
		dllbase.h guard.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		database_reader.h row_reader.h std_helper.h mutex.h
guard.obj:	precompiled.h readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dllbase.h
id_ctibase.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h id_ctibase.h
json.obj:	precompiled.h MetricIdLookup.h PointAttribute.h yukon.h \
		types.h ctidbgmem.h dlldefs.h resource_helper.h
litepoint.obj:	precompiled.h LitePoint.h dlldefs.h pointtypes.h
logfileappender.obj:	precompiled.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h \
		logFileAppender.h logManager.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		critical_section.h ctidate.h
logger.obj:	precompiled.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h critical_section.h logManager.h
loglayout.obj:	precompiled.h ctitime.h dlldefs.h logLayout.h \
		logManager.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h critical_section.h
logmanager.obj:	precompiled.h logLayout.h dlldefs.h logManager.h \
		module_util.h ctitime.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h critical_section.h \
		logFileAppender.h ctidate.h truncatingConsoleAppender.h \
		timing_util.h cparms.h
macro_offset.obj:	precompiled.h macro_offset.h dlldefs.h
master.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h cti_asmc.h queues.h constants.h dsm2.h \
		connectionHandle.h loggable.h streamConnection.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h master.h
metricidlookup.obj:	precompiled.h MetricIdLookup.h PointAttribute.h \
		yukon.h types.h ctidbgmem.h dlldefs.h
millisecond_timer.obj:	precompiled.h millisecond_timer.h dlldefs.h
module_util.obj:	precompiled.h module_util.h dlldefs.h ctitime.h \
		version.h win_helper.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h critical_section.h dllbase.h guard.h
mutex.obj:	precompiled.h mutex.h dlldefs.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h std_helper.h win_helper.h
numstr.obj:	precompiled.h numstr.h dlldefs.h
observe.obj:	precompiled.h observe.h types.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h
parse.obj:	precompiled.h
pointattribute.obj:	precompiled.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h
queue.obj:	precompiled.h queue.h cparms.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h
queues.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h critical_section.h dllbase.h
readers_writer_lock.obj:	precompiled.h readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h
regression.obj:	precompiled.h cparms.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h regression.h
repeaterrole.obj:	precompiled.h repeaterrole.h dlldefs.h
resolvers.obj:	precompiled.h dsm2.h connectionHandle.h dlldefs.h \
		loggable.h streamConnection.h yukon.h types.h ctidbgmem.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		resolvers.h pointtypes.h db_entry_defines.h devicetypes.h \
		std_helper.h
resource_helper.obj:	precompiled.h resource_helper.h dlldefs.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h critical_section.h
rtdb.obj:	precompiled.h dlldefs.h rtdb.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h dllbase.h critical_section.h
sema.obj:	precompiled.h sema.h dlldefs.h
stdexcepthdlr.obj:	precompiled.h stdexcepthdlr.h dlldefs.h
streambuffer.obj:	precompiled.h string_util.h dlldefs.h streamBuffer.h \
		loggable.h ctidate.h ctitime.h numstr.h
streamlocalconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		millisecond_timer.h win_helper.h streamLocalConnection.h \
		streamConnection.h timing_util.h immutable.h guard.h dsm2.h \
		connectionHandle.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
streamsocketconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h std_helper.h \
		win_helper.h millisecond_timer.h streamSocketConnection.h \
		streamConnection.h timing_util.h immutable.h guard.h \
		socket_helper.h
streamsocketlistener.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h std_helper.h \
		win_helper.h millisecond_timer.h streamSocketListener.h \
		socket_helper.h guard.h timing_util.h \
		streamSocketConnection.h streamConnection.h immutable.h
string_util.obj:	precompiled.h string_util.h dlldefs.h streamBuffer.h \
		loggable.h
test_cmdparse.obj:	cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h \
		test_cmdparse_input.h test_cmdparse_output.h
test_ctidate.obj:	ctidate.h dlldefs.h ctitime.h boost_test_helpers.h \
		millisecond_timer.h
test_ctipcptrqueue.obj:	ctipcptrqueue.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h \
		millisecond_timer.h
test_ctitime.obj:	ctitime.h dlldefs.h ctidate.h boost_test_helpers.h \
		millisecond_timer.h
test_database_util.obj:	database_util.h database_writer.h ctitime.h \
		dlldefs.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		row_writer.h
test_date_utility.obj:	date_utility.h ctidate.h dlldefs.h
test_dbaccess.obj:	dbaccess.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h
test_desolvers.obj:	desolvers.h dlldefs.h pointtypes.h devicetypes.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h
test_encryption.obj:	encryption.h dlldefs.h
test_error.obj:	yukon.h types.h ctidbgmem.h
test_logger.obj:	logManager.h dlldefs.h module_util.h ctitime.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h ctidate.h
test_multiset.obj:	utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h
test_numstr.obj:	numstr.h dlldefs.h
test_old_queues.obj:	dsm2.h connectionHandle.h dlldefs.h loggable.h \
		streamConnection.h yukon.h types.h ctidbgmem.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
test_pointattribute.obj:	PointAttribute.h yukon.h types.h ctidbgmem.h \
		dlldefs.h
test_queue.obj:	queue.h cparms.h dlldefs.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h module_util.h \
		version.h critical_section.h dbaccess.h dllbase.h
test_readers_writer_lock.obj:	readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h
test_resolvers.obj:	dsm2.h connectionHandle.h dlldefs.h loggable.h \
		streamConnection.h yukon.h types.h ctidbgmem.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h resolvers.h pointtypes.h \
		db_entry_defines.h devicetypes.h boost_test_helpers.h \
		millisecond_timer.h ctidate.h
test_std_helper.obj:	std_helper.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h streamBuffer.h \
		loggable.h
test_string_formatter.obj:	string_util.h dlldefs.h streamBuffer.h \
		loggable.h
test_string_util.obj:	string_util.h dlldefs.h streamBuffer.h \
		loggable.h
test_timeperiod.obj:	timeperiod.h dlldefs.h ctitime.h ctidate.h
test_utility.obj:	utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h ctidate.h dsm2.h connectionHandle.h \
		loggable.h streamConnection.h timing_util.h immutable.h \
		critical_section.h guard.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h devicetypes.h
test_words.obj:	boost_test_helpers.h millisecond_timer.h dlldefs.h \
		ctitime.h ctidate.h
thread.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h constants.h \
		numstr.h module_util.h version.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		win_helper.h
threadstatuskeeper.obj:	precompiled.h ThreadStatusKeeper.h ctitime.h \
		dlldefs.h thread_register_data.h boost_time.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h boostutil.h utility.h \
		queues.h constants.h numstr.h module_util.h version.h \
		thread_monitor.h smartmap.h dllbase.h critical_section.h \
		readers_writer_lock.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h cparms.h queue.h \
		thread.h mutex.h
thread_listener.obj:	precompiled.h thread_listener.h thread.h mutex.h \
		dlldefs.h guard.h utility.h ctitime.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		critical_section.h
thread_monitor.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h thread_monitor.h \
		smartmap.h readers_writer_lock.h guard.h cparms.h queue.h \
		thread.h mutex.h thread_register_data.h boost_time.h
thread_register_data.obj:	precompiled.h thread_register_data.h \
		boost_time.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h dlldefs.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h module_util.h version.h
timeperiod.obj:	precompiled.h ctitime.h dlldefs.h timeperiod.h
timing_util.obj:	precompiled.h numstr.h dlldefs.h timing_util.h \
		loggable.h
truncatingconsoleappender.obj:	precompiled.h \
		truncatingConsoleAppender.h dlldefs.h timing_util.h \
		loggable.h streamBuffer.h
ucttime.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h dsm2.h \
		connectionHandle.h loggable.h streamConnection.h \
		timing_util.h immutable.h guard.h utility.h ctitime.h \
		queues.h constants.h numstr.h module_util.h version.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h elogger.h
utility.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		database_reader.h database_connection.h guard.h utility.h \
		ctitime.h queues.h constants.h numstr.h module_util.h \
		version.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h row_reader.h \
		database_transaction.h database_writer.h row_writer.h \
		database_util.h pointdefs.h ctidate.h devicetypes.h \
		desolvers.h pointtypes.h dsm2.h connectionHandle.h \
		streamConnection.h timing_util.h immutable.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h
verification_objects.obj:	precompiled.h verification_objects.h \
		dlldefs.h boost_time.h dsm2.h connectionHandle.h loggable.h \
		streamConnection.h yukon.h types.h ctidbgmem.h timing_util.h \
		immutable.h critical_section.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
win_helper.obj:	precompiled.h win_helper.h numstr.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h module_util.h \
		version.h critical_section.h
words.obj:	precompiled.h words.h dlldefs.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h optional.h cti_asmc.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h constants.h numstr.h \
		module_util.h version.h critical_section.h
worker_thread.obj:	precompiled.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h module_util.h version.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h critical_section.h worker_thread.h timing_util.h \
		concurrentSet.h win_helper.h
xfer.obj:	precompiled.h xfer.h dlldefs.h yukon.h types.h ctidbgmem.h
xml.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h xml.h devicetypes.h pointtypes.h \
		resource_helper.h PointAttribute.h DeviceConfigDescription.h \
		DeviceAttributeLookup.h resolvers.h db_entry_defines.h \
		std_helper.h
#ENDUPDATE#
