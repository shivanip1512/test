#Build name MUST BE FIRST!!!!

.include global.inc
.include rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(BOOST) \
-I$(SQLAPI)\include \
-I$(RW) \
-I$(ACTIVEMQ)

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
;$(BOOST) \
;$(RW) \
;$(ACTIVEMQ)

TESTOBJS=\
cmdparsetestgenerator.obj \
test_cmdparse.obj \
test_ctidate.obj \
test_CtiPCPtrQueue.obj \
test_ctistring.obj \
test_ctitime.obj \
test_desolvers.obj \
test_fifo_multiset.obj \
test_hash.obj \
test_logger.obj \
test_numstr.obj \
test_old_queues.obj \
test_queue.obj \
test_resolvers.obj \
test_rwutil.obj \
test_readers_writer_lock.obj \
test_timeperiod.obj \
test_date_utility.obj \
test_utility.obj \
test_string_utility.obj \
test_PointAttribute.obj \
test_compiler_behaviors.obj \

SQLAPILIB=$(SQLAPI)\lib\$(SQLAPI_LIB).lib

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib


CMTEST=\
cmdparsetestgenerator.exe


ALL:            ctibasetest

ctibasetest:    $(TESTOBJS)  Makefile

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
        .\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\cticparms.lib $(SQLAPILIB) $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LINKFLAGS)

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
        -copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
        -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
        -if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
        @%cd $(CWD)
        @echo.

######################################################################################
ctidate.obj:    yukon.h ctidate.h
ctitime.obj:    yukon.h ctitime.h
cmdparse.obj:   yukon.h precompiled.h ctidbgmem.h cmdparse.h dlldefs.h \
                parsevalue.h cparms.h devicetypes.h logger.h thread.h mutex.h \
                guard.h numstr.h pointdefs.h utility.h dsm2.h
test_cmdparse.obj:      cmdparse.h test_cmdparse_input.h test_cmdparse_output.h
cmdparsetestgenerator.obj:  cmdparse.h test_cmdparse_input.h
test_ctidate.obj:  ctidate.h
test_ctitime.obj:  ctitime.h

#UPDATE#
attributeservice.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		AttributeService.h LitePoint.h dlldefs.h pointtypes.h \
		PointAttribute.h resolvers.h db_entry_defines.h numstr.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h database_connection.h row_reader.h
bfexec.obj:	yukon.h precompiled.h types.h ctidbgmem.h bfexec.h
cmdparse.obj:	yukon.h precompiled.h types.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h pointdefs.h ctistring.h
cmdparsetestgenerator.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h numstr.h \
		test_cmdparse_input.h
counter.obj:	yukon.h precompiled.h types.h ctidbgmem.h counter.h \
		guard.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h numstr.h sorted_vector.h mutex.h
critical_section.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		critical_section.h dlldefs.h
ctdpcptrq.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctdpcptrq.h \
		dlldefs.h CtiPCPtrQueue.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h sorted_vector.h
cticalls.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h dllbase.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h
ctidate.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h CtiPCPtrQueue.h
ctilocalconnect.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		cparms.h dlldefs.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h \
		ctilocalconnect.h critical_section.h fifo_multiset.h
ctinexus.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h ctinexus.h netports.h cticonnect.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h numstr.h sorted_vector.h CtiPCPtrQueue.h dsm2.h \
		dsm2err.h words.h optional.h millisecond_timer.h
ctistring.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctistring.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h sorted_vector.h
ctitime.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctidate.h \
		dlldefs.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h CtiPCPtrQueue.h
ctitokenizer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctitokenizer.h dlldefs.h
cti_asmc.obj:	yukon.h precompiled.h types.h ctidbgmem.h cti_asmc.h \
		dlldefs.h cticalls.h os2_2w32.h
c_port_interface.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dlldefs.h os2_2w32.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h porter.h \
		devicetypes.h c_port_interface.h elogger.h
database_connection.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h logger.h \
		thread.h CtiPCPtrQueue.h
database_reader.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		database_reader.h ctitime.h dlldefs.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		row_reader.h logger.h thread.h CtiPCPtrQueue.h
database_writer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		database_writer.h ctitime.h dlldefs.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		row_writer.h ctidate.h logger.h thread.h CtiPCPtrQueue.h \
		boost_time.h
date_utility.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		date_utility.h ctidate.h dlldefs.h logger.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h sorted_vector.h CtiPCPtrQueue.h ctitokenizer.h
dbaccess.obj:	yukon.h precompiled.h types.h ctidbgmem.h dlldefs.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h cparms.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h ctistring.h \
		database_writer.h row_writer.h
debug_timer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		debug_timer.h dlldefs.h logger.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h CtiPCPtrQueue.h
desolvers.obj:	yukon.h precompiled.h types.h ctidbgmem.h desolvers.h \
		dlldefs.h dsm2.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h resolvers.h \
		pointtypes.h db_entry_defines.h devicetypes.h logger.h \
		thread.h CtiPCPtrQueue.h
dllbase.obj:	yukon.h precompiled.h types.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h sorted_vector.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h configparms.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h ctinexus.h logger.h thread.h \
		CtiPCPtrQueue.h thread_monitor.h smartmap.h \
		readers_writer_lock.h critical_section.h queue.h \
		string_utility.h thread_register_data.h
elog_cli.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h elogger.h logger.h \
		thread.h CtiPCPtrQueue.h
error.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dllbase.h logger.h \
		thread.h CtiPCPtrQueue.h
exchange.obj:	yukon.h precompiled.h types.h ctidbgmem.h exchange.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h
fileint.obj:	yukon.h precompiled.h types.h ctidbgmem.h fileint.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h
guard.obj:	yukon.h precompiled.h types.h ctidbgmem.h guard.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h
hash_functions.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		hash_functions.h dlldefs.h
id_ctibase.obj:	yukon.h precompiled.h types.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h id_ctibase.h
litepoint.obj:	yukon.h precompiled.h types.h ctidbgmem.h LitePoint.h \
		dlldefs.h pointtypes.h
logger.obj:	yukon.h precompiled.h types.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h
master.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h cti_asmc.h queues.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h master.h
millisecond_timer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		millisecond_timer.h dlldefs.h
mutex.obj:	yukon.h precompiled.h types.h ctidbgmem.h mutex.h dlldefs.h
numstr.obj:	yukon.h precompiled.h types.h ctidbgmem.h numstr.h \
		dlldefs.h
observe.obj:	yukon.h precompiled.h types.h ctidbgmem.h observe.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h sorted_vector.h
parse.obj:	yukon.h precompiled.h types.h ctidbgmem.h
pexec.obj:	yukon.h precompiled.h types.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h devicetypes.h \
		logger.h thread.h CtiPCPtrQueue.h ctinexus.h
pointattribute.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PointAttribute.h dlldefs.h
point_change.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		point_change.h dlldefs.h
portsup.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h queues.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dllbase.h color.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
queent.obj:	yukon.h precompiled.h types.h ctidbgmem.h queent.h \
		dlldefs.h
queue.obj:	yukon.h precompiled.h types.h ctidbgmem.h queue.h cparms.h \
		dlldefs.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h
queues.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h logger.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		CtiPCPtrQueue.h dllbase.h dsm2.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h
readers_writer_lock.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		readers_writer_lock.h dlldefs.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h
regression.obj:	yukon.h precompiled.h types.h ctidbgmem.h cparms.h \
		dlldefs.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h \
		regression.h
repeaterrole.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		repeaterrole.h dlldefs.h
resolvers.obj:	yukon.h precompiled.h types.h ctidbgmem.h dsm2.h \
		mutex.h dlldefs.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h resolvers.h \
		pointtypes.h db_entry_defines.h devicetypes.h logger.h \
		thread.h CtiPCPtrQueue.h
rtdb.obj:	yukon.h precompiled.h types.h ctidbgmem.h dlldefs.h rtdb.h \
		hashkey.h hash_functions.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h \
		string_utility.h dllbase.h dsm2.h mutex.h guard.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h
sema.obj:	yukon.h precompiled.h types.h ctidbgmem.h sema.h dlldefs.h
stdexcepthdlr.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		stdexcepthdlr.h dlldefs.h
string_utility.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		string_utility.h dlldefs.h
test_cmdparse.obj:	boostutil.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		test_cmdparse_input.h test_cmdparse_output.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctistring.h
test_ctidate.obj:	boostutil.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		ctidate.h logger.h thread.h mutex.h guard.h CtiPCPtrQueue.h
test_ctipcptrqueue.obj:	ctipcptrqueue.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h boostutil.h
test_ctistring.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctistring.h dlldefs.h
test_ctitime.obj:	ctitime.h dlldefs.h ctidate.h logger.h thread.h \
		mutex.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h sorted_vector.h CtiPCPtrQueue.h
test_date_utility.obj:	date_utility.h ctidate.h dlldefs.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		CtiPCPtrQueue.h
test_desolvers.obj:	desolvers.h dlldefs.h devicetypes.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h
test_fifo_multiset.obj:	fifo_multiset.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h
test_hash.obj:	hashkey.h hash_functions.h dlldefs.h
test_logger.obj:	yukon.h precompiled.h types.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h sorted_vector.h \
		CtiPCPtrQueue.h
test_numstr.obj:	yukon.h precompiled.h types.h ctidbgmem.h numstr.h \
		dlldefs.h
test_old_queues.obj:	dsm2.h mutex.h dlldefs.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h cticonnect.h yukon.h precompiled.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h
test_pointattribute.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PointAttribute.h dlldefs.h
test_queue.obj:	queue.h cparms.h dlldefs.h rwutil.h yukon.h \
		precompiled.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h
test_readers_writer_lock.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h
test_resolvers.obj:	yukon.h precompiled.h types.h ctidbgmem.h dsm2.h \
		mutex.h dlldefs.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h resolvers.h \
		pointtypes.h db_entry_defines.h devicetypes.h logger.h \
		thread.h CtiPCPtrQueue.h
test_rwutil.obj:	rwutil.h yukon.h precompiled.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		ctidate.h logger.h thread.h CtiPCPtrQueue.h
test_string_utility.obj:	string_utility.h dlldefs.h
test_timeperiod.obj:	timeperiod.h dlldefs.h ctitime.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		CtiPCPtrQueue.h
test_utility.obj:	yukon.h precompiled.h types.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h devicetypes.h
tfexec.obj:	yukon.h precompiled.h types.h ctidbgmem.h tfexec.h
thread.obj:	yukon.h precompiled.h types.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h thread.h
threadstatuskeeper.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ThreadStatusKeeper.h ctitime.h dlldefs.h \
		thread_register_data.h boost_time.h cticalls.h os2_2w32.h \
		boostutil.h utility.h queues.h numstr.h sorted_vector.h \
		thread_monitor.h smartmap.h dllbase.h dsm2.h mutex.h guard.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h cparms.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h configkey.h configval.h queue.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h
thread_listener.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		thread_listener.h thread.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h
thread_monitor.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dllbase.h os2_2w32.h dlldefs.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h thread_monitor.h smartmap.h \
		boostutil.h readers_writer_lock.h critical_section.h cparms.h \
		rwutil.h database_connection.h dbaccess.h sema.h \
		database_reader.h row_reader.h boost_time.h configkey.h \
		configval.h queue.h string_utility.h thread_register_data.h
thread_register_data.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		thread_register_data.h boost_time.h cticalls.h os2_2w32.h \
		dlldefs.h boostutil.h utility.h ctitime.h queues.h numstr.h \
		sorted_vector.h
thread_timer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		thread_timer.h thread.h mutex.h dlldefs.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h thread_register_data.h boost_time.h \
		boostutil.h
timeperiod.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h timeperiod.h
ucttime.obj:	yukon.h precompiled.h types.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		elogger.h logger.h thread.h CtiPCPtrQueue.h
utility.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h database_connection.h row_reader.h \
		database_writer.h row_writer.h logger.h thread.h \
		CtiPCPtrQueue.h pointdefs.h ctidate.h devicetypes.h
verification_objects.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		verification_objects.h dlldefs.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h boost_time.h
words.obj:	yukon.h precompiled.h types.h ctidbgmem.h words.h dlldefs.h \
		cticalls.h os2_2w32.h optional.h cti_asmc.h dsm2err.h \
		logger.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h sorted_vector.h CtiPCPtrQueue.h
xfer.obj:	yukon.h precompiled.h types.h ctidbgmem.h xfer.h dsm2.h \
		mutex.h dlldefs.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h
#ENDUPDATE#
