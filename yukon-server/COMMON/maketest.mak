#Build name MUST BE FIRST!!!!

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \
-I$(ACTIVEMQ)

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
;$(BOOST_INCLUDE) \
;$(RW) \
;$(ACTIVEMQ)

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

SQLAPILIB=$(SQLAPI)\lib\$(SQLAPI_LIB).lib

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib

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
        $(COMMON_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(_TargetF).manifest -outputresource:$(BIN)\$(_TargetF);1
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
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
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
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		dsm2err.h words.h optional.h database_reader.h \
		database_connection.h row_reader.h
bfexec.obj:	precompiled.h bfexec.h
cmdparse.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h logger.h thread.h \
		CtiPCPtrQueue.h pointdefs.h ctistring.h
cmdparsetestgenerator.obj:	cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h test_cmdparse_input.h
configkey.obj:	precompiled.h configkey.h
configval.obj:	precompiled.h configval.h
counter.obj:	precompiled.h counter.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		mutex.h
cparms.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h
critical_section.obj:	precompiled.h critical_section.h dlldefs.h
ctdpcptrq.obj:	precompiled.h ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h
cticalls.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h
ctidate.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h
ctilocalconnect.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h ctilocalconnect.h critical_section.h
ctinexus.obj:	precompiled.h os2_2w32.h dlldefs.h types.h ctinexus.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		numstr.h CtiPCPtrQueue.h dsm2.h dsm2err.h words.h optional.h \
		millisecond_timer.h
ctistring.obj:	precompiled.h ctistring.h dlldefs.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h
ctitime.obj:	precompiled.h ctidate.h dlldefs.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h
ctitokenizer.obj:	precompiled.h ctitokenizer.h dlldefs.h
cti_asmc.obj:	precompiled.h cti_asmc.h dlldefs.h cticalls.h os2_2w32.h \
		types.h
c_port_interface.obj:	precompiled.h dlldefs.h os2_2w32.h types.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h numstr.h \
		dsm2err.h words.h optional.h porter.h devicetypes.h \
		c_port_interface.h elogger.h
database_connection.obj:	precompiled.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h logger.h thread.h CtiPCPtrQueue.h
database_reader.obj:	precompiled.h database_reader.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h row_reader.h \
		logger.h thread.h CtiPCPtrQueue.h
database_writer.obj:	precompiled.h database_writer.h ctitime.h \
		dlldefs.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h row_writer.h ctidate.h logger.h \
		thread.h CtiPCPtrQueue.h boost_time.h
date_utility.obj:	precompiled.h date_utility.h ctidate.h dlldefs.h \
		logger.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h ctitokenizer.h
dbaccess.obj:	precompiled.h ctidbgmem.h types.h dlldefs.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h yukon.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h ctistring.h database_writer.h \
		row_writer.h
debug_timer.obj:	precompiled.h debug_timer.h dlldefs.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h
desolvers.obj:	precompiled.h desolvers.h dlldefs.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h resolvers.h pointtypes.h \
		db_entry_defines.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h
dllbase.obj:	precompiled.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h dbaccess.h dllbase.h ctinexus.h logger.h \
		thread.h CtiPCPtrQueue.h thread_monitor.h smartmap.h \
		boostutil.h readers_writer_lock.h critical_section.h cparms.h \
		rwutil.h database_connection.h database_reader.h row_reader.h \
		boost_time.h configkey.h configval.h queue.h string_utility.h \
		thread_register_data.h
elog_cli.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h dsm2err.h \
		words.h optional.h elogger.h logger.h thread.h \
		CtiPCPtrQueue.h
encryption_key_file.obj:	precompiled.h
error.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h dsm2err.h \
		words.h optional.h dllbase.h logger.h thread.h \
		CtiPCPtrQueue.h
exchange.obj:	precompiled.h exchange.h dlldefs.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h
fileint.obj:	precompiled.h fileint.h dlldefs.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h
guard.obj:	precompiled.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h
hash_functions.obj:	precompiled.h hash_functions.h dlldefs.h
id_ctibase.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h id_ctibase.h
litepoint.obj:	precompiled.h LitePoint.h dlldefs.h pointtypes.h
logger.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h cparms.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h
master.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		cti_asmc.h queues.h dsm2.h cticonnect.h yukon.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h numstr.h \
		dsm2err.h words.h optional.h master.h
millisecond_timer.obj:	precompiled.h millisecond_timer.h dlldefs.h
mutex.obj:	precompiled.h mutex.h dlldefs.h
numstr.obj:	precompiled.h numstr.h dlldefs.h
observe.obj:	precompiled.h observe.h types.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h
parse.obj:	precompiled.h
pexec.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h ctinexus.h
pointattribute.obj:	precompiled.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h
point_change.obj:	precompiled.h point_change.h yukon.h types.h \
		ctidbgmem.h dlldefs.h
queent.obj:	precompiled.h queent.h dlldefs.h
queue.obj:	precompiled.h queue.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h
queues.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		logger.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h CtiPCPtrQueue.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h
readers_writer_lock.obj:	precompiled.h readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h
regression.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h regression.h
repeaterrole.obj:	precompiled.h repeaterrole.h dlldefs.h
resolvers.obj:	precompiled.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h resolvers.h pointtypes.h \
		db_entry_defines.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h
rtdb.obj:	precompiled.h dlldefs.h rtdb.h hashkey.h hash_functions.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h string_utility.h dllbase.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h mutex.h guard.h dsm2err.h \
		words.h optional.h
sema.obj:	precompiled.h sema.h dlldefs.h
statistics.obj:	yukon.h types.h ctidbgmem.h row_reader.h ctitime.h \
		dlldefs.h dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h \
		mutex.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h ctidate.h cparms.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h configkey.h configval.h database_writer.h \
		row_writer.h
stdexcepthdlr.obj:	precompiled.h stdexcepthdlr.h dlldefs.h
string_utility.obj:	precompiled.h string_utility.h dlldefs.h
test_cmdparse.obj:	cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h \
		test_cmdparse_input.h test_cmdparse_output.h
test_ctidate.obj:	ctidate.h dlldefs.h logger.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h
test_ctipcptrqueue.obj:	ctipcptrqueue.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h
test_ctistring.obj:	ctistring.h dlldefs.h
test_ctitime.obj:	ctitime.h dlldefs.h ctidate.h logger.h thread.h \
		mutex.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h
test_date_utility.obj:	date_utility.h ctidate.h dlldefs.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h
test_desolvers.obj:	desolvers.h dlldefs.h devicetypes.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h
test_hash.obj:	hashkey.h hash_functions.h dlldefs.h
test_logger.obj:	logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
test_multiset.obj:	utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h
test_numstr.obj:	numstr.h dlldefs.h
test_old_queues.obj:	dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h
test_pointattribute.obj:	PointAttribute.h yukon.h types.h ctidbgmem.h \
		dlldefs.h
test_queue.obj:	queue.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h
test_readers_writer_lock.obj:	readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h
test_resolvers.obj:	dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h resolvers.h pointtypes.h db_entry_defines.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h
test_rwutil.obj:	rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h
test_string_utility.obj:	string_utility.h dlldefs.h
test_timeperiod.obj:	timeperiod.h dlldefs.h ctitime.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h
test_utility.obj:	utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h ctidate.h logger.h thread.h \
		mutex.h guard.h CtiPCPtrQueue.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		devicetypes.h
tfexec.obj:	precompiled.h tfexec.h
thread.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h thread.h
threadstatuskeeper.obj:	precompiled.h ThreadStatusKeeper.h ctitime.h \
		dlldefs.h thread_register_data.h boost_time.h cticalls.h \
		os2_2w32.h types.h boostutil.h utility.h queues.h numstr.h \
		thread_monitor.h smartmap.h dllbase.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h mutex.h guard.h dsm2err.h \
		words.h optional.h readers_writer_lock.h critical_section.h \
		cparms.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h configkey.h configval.h \
		queue.h logger.h thread.h CtiPCPtrQueue.h string_utility.h
thread_listener.obj:	precompiled.h thread_listener.h thread.h mutex.h \
		dlldefs.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h
thread_monitor.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h thread_monitor.h smartmap.h boostutil.h \
		readers_writer_lock.h critical_section.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h queue.h \
		string_utility.h thread_register_data.h
thread_register_data.obj:	precompiled.h thread_register_data.h \
		boost_time.h cticalls.h os2_2w32.h dlldefs.h types.h \
		boostutil.h utility.h ctitime.h queues.h numstr.h
thread_timer.obj:	precompiled.h thread_timer.h thread.h mutex.h \
		dlldefs.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h thread_register_data.h \
		boost_time.h boostutil.h
timeperiod.obj:	precompiled.h ctitime.h dlldefs.h timeperiod.h
ucttime.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h elogger.h logger.h thread.h \
		CtiPCPtrQueue.h
utility.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h database_reader.h \
		database_connection.h row_reader.h database_writer.h \
		row_writer.h logger.h thread.h CtiPCPtrQueue.h pointdefs.h \
		ctidate.h devicetypes.h
verification_objects.obj:	precompiled.h verification_objects.h \
		dlldefs.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		boost_time.h
words.obj:	precompiled.h words.h dlldefs.h cticalls.h os2_2w32.h \
		types.h optional.h cti_asmc.h dsm2err.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		CtiPCPtrQueue.h
xfer.obj:	precompiled.h xfer.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h
#ENDUPDATE#
