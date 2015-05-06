#Build name MUST BE FIRST!!!!

DLLBUILDNAME = -DCTIBASE

!include $(COMPILEBASE)\global.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(RESOURCE)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(OPENSSL)\include \
-I$(DBGHELP)\include \
-I$(XERCES)\include \
-I$(CAJUN_INCLUDE) \
-I$(LOG4CXX_INCLUDE) \
-I$(APR_INCLUDE) \

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(RESOURCE)\include \
;$(BOOST_INCLUDE) \
;$(SQLAPI)\include \
;$(XERCES)\include \


BASEOBJS=\
$(PRECOMPILED_OBJ) \
ctidate.obj \
ctitime.obj \
ctitokenizer.obj \
resolvers.obj \
numstr.obj \
cmdparse.obj \
counter.obj \
critical_section.obj \
ctdpcptrq.obj \
cti_asmc.obj \
cticalls.obj \
streamLocalConnection.obj \
streamSocketConnection.obj \
streamSocketListener.obj \
database_writer.obj \
database_connection.obj \
database_transaction.obj \
database_reader.obj \
database_util.obj \
dbaccess.obj \
debug_timer.obj \
desolvers.obj \
dllbase.obj \
elog_cli.obj \
error.obj \
fileint.obj \
guard.obj \
macro_offset.obj \
master.obj \
millisecond_timer.obj \
mutex.obj \
observe.obj \
queue.obj \
queues.obj \
readers_writer_lock.obj \
regression.obj \
repeaterrole.obj \
rtdb.obj \
sema.obj \
stdexcepthdlr.obj \
thread.obj \
ucttime.obj \
date_utility.obj \
utility.obj \
words.obj \
xfer.obj \
verification_objects.obj \
thread_register_data.obj \
thread_monitor.obj \
ThreadStatusKeeper.obj \
timeperiod.obj \
LitePoint.obj \
AttributeService.obj \
PointAttribute.obj \
MetricIdLookup.obj \
configval.obj \
configkey.obj \
cparms.obj \
encryption.obj \
BeatThePeakAlertLevel.obj \
json.obj \
xml.obj \
DeviceAttributeLookup.obj \
DeviceConfigDescription.obj \
timing_util.obj \
worker_thread.obj \
resource_helper.obj \
module_util.obj \
streamBuffer.obj \
string_util.obj \
exception_helper.obj \
win_helper.obj \

LOGGEROBJS=\
logLayout.obj \
logFileAppender.obj \
truncatingConsoleAppender.obj \
logManager.obj \
logger.obj \


CTIPROGS=\
ctibase.dll


WINLIBS=kernel32.lib user32.lib advapi32.lib wsock32.lib winmm.lib shlwapi.lib Ws2_32.lib psapi.lib

COMMON_FULLBUILD = $[Filename,$(OBJ),CommonFullBuild,target]

PROGS_VERSION=\
$(CTIPROGS)

ALL:            $(CTIPROGS)

$(COMMON_FULLBUILD):
        @touch $@
        @echo:
        @echo Compiling logger cpp to obj
        @echo:
        $(CC) $(CCOPTS) $(DLLFLAGS) $(PARALLEL) $(DLLBUILDNAME) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(LOGGEROBJS)] /wd4275 /wd4251
        @echo:
        @echo Compiling cpp to obj
        @echo:
        $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) $(PARALLEL) $(DLLBUILDNAME) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(BASEOBJS)]

ctibase.dll:    $(COMMON_FULLBUILD) $(BASEOBJS) $(LOGGEROBJS) Makefile $(OBJ)\ctibase.res
                @build -nologo -f $(_InputFile) id
                @%cd $(OBJ)
                $(CC) $(BASEOBJS) $(LOGGEROBJS) id_ctibase.obj $(WINLIBS) $(SQLAPI_LIB) $(XERCES_LIB) $(OPENSSL_LIBS) $(DLLFLAGS) $(BOOST_LIBS) $(DBGHELP_LIBS) $(LOG4CXX_LIB) $(APR_LIB) /Fe..\$@ ctibase.res
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy bin\ctibase.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\ctibase.lib copy bin\ctibase.lib $(COMPILEBASE)\lib

deps:
                scandeps -Output makebase.mak *.cpp

clean:
        -del \
*.pdb \
*.idb \
*.obj \
$(OBJ)\*.obj \
$(OBJ)\*.target \
$(OBJ)\*.res \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.map \
$(BIN)\*.manifest \
$(BIN)\*.exe


allclean:   clean all


# The lines below accomplish the ID'ing of the project!
id:
            # @cid .\include\id_ctibase.h
            @build -nologo -f $(_InputFile) id_ctibase.obj

id_ctibase.obj:    id_ctibase.cpp include\id_ctibase.h



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
        $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) $(DLLBUILDNAME) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################



#UPDATE#
attributeservice.obj:	precompiled.h AttributeService.h LitePoint.h \
		dlldefs.h pointtypes.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h resolvers.h db_entry_defines.h numstr.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h critical_section.h \
		database_reader.h database_connection.h guard.h utility.h \
		ctitime.h queues.h constants.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		row_reader.h
beatthepeakalertlevel.obj:	precompiled.h BeatThePeakAlertLevel.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h
cmdparse.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h cparms.h configkey.h configval.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h pointdefs.h std_helper.h
cmdparsetestgenerator.obj:	cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h test_cmdparse_input.h
configkey.obj:	precompiled.h configkey.h
configval.obj:	precompiled.h configval.h
counter.obj:	precompiled.h counter.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		critical_section.h mutex.h
cparms.obj:	precompiled.h cparms.h dlldefs.h configkey.h configval.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h encryption.h
critical_section.obj:	precompiled.h critical_section.h dlldefs.h
ctdpcptrq.obj:	precompiled.h ctdpcptrq.h dlldefs.h CtiPCPtrQueue.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h
cticalls.obj:	precompiled.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h dlldefs.h dllbase.h critical_section.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h constants.h numstr.h \
		win_helper.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
ctidate.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h
ctitime.obj:	precompiled.h ctidate.h dlldefs.h ctitime.h
ctitokenizer.obj:	precompiled.h ctitokenizer.h dlldefs.h
cti_asmc.obj:	precompiled.h cti_asmc.h dlldefs.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h
database_connection.obj:	precompiled.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h critical_section.h guard.h utility.h \
		ctitime.h queues.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h database_exceptions.h std_helper.h
database_reader.obj:	precompiled.h database_reader.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h row_reader.h \
		CParms.h configkey.h configval.h
database_transaction.obj:	precompiled.h database_transaction.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h
database_util.obj:	precompiled.h database_exceptions.h database_util.h \
		database_writer.h ctitime.h dlldefs.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h guard.h utility.h queues.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h row_writer.h \
		database_reader.h row_reader.h
database_writer.obj:	precompiled.h database_writer.h ctitime.h \
		dlldefs.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h queues.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h row_writer.h ctidate.h \
		boost_time.h
date_utility.obj:	precompiled.h date_utility.h ctidate.h dlldefs.h \
		ctitokenizer.h
dbaccess.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h guard.h
debug_timer.obj:	precompiled.h debug_timer.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h
desolvers.obj:	precompiled.h desolvers.h dlldefs.h pointtypes.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h resolvers.h \
		db_entry_defines.h devicetypes.h std_helper.h
deviceattributelookup.obj:	precompiled.h DeviceAttributeLookup.h \
		devicetypes.h pointtypes.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h
deviceconfigdescription.obj:	precompiled.h DeviceConfigDescription.h \
		devicetypes.h pointtypes.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h std_helper.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h
dllbase.obj:	precompiled.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h dbaccess.h dllbase.h streamSocketConnection.h \
		socket_helper.h win_helper.h encryption.h json.h xml.h \
		devicetypes.h pointtypes.h resource_helper.h PointAttribute.h \
		DeviceConfigDescription.h thread_monitor.h smartmap.h \
		readers_writer_lock.h cparms.h configkey.h configval.h \
		queue.h thread.h thread_register_data.h boost_time.h \
		module_util.h
elog_cli.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h elogger.h
encryption.obj:	precompiled.h encryption.h dlldefs.h string_util.h \
		streamBuffer.h loggable.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h logger.h exception_helper.h boostutil.h \
		critical_section.h
error.obj:	precompiled.h dsm2err.h dlldefs.h yukon.h types.h \
		ctidbgmem.h constants.h numstr.h std_helper.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h
exception_helper.obj:	precompiled.h streamBuffer.h dlldefs.h \
		loggable.h exception_helper.h
fileint.obj:	precompiled.h fileint.h dlldefs.h critical_section.h \
		worker_thread.h timing_util.h dllbase.h os2_2w32.h types.h \
		cticalls.h yukon.h ctidbgmem.h ctitime.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h queues.h constants.h numstr.h guard.h
guard.obj:	precompiled.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		critical_section.h
id_ctibase.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h id_ctibase.h module_util.h
json.obj:	precompiled.h MetricIdLookup.h PointAttribute.h yukon.h \
		types.h ctidbgmem.h dlldefs.h resource_helper.h
litepoint.obj:	precompiled.h LitePoint.h dlldefs.h pointtypes.h
logfileappender.obj:	precompiled.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h logFileAppender.h logManager.h \
		module_util.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		critical_section.h ctidate.h
logger.obj:	precompiled.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		critical_section.h logManager.h module_util.h
loglayout.obj:	precompiled.h ctitime.h dlldefs.h logLayout.h \
		logManager.h module_util.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h critical_section.h
logmanager.obj:	precompiled.h logLayout.h dlldefs.h logManager.h \
		module_util.h ctitime.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h critical_section.h logFileAppender.h \
		ctidate.h truncatingConsoleAppender.h timing_util.h cparms.h \
		configkey.h configval.h
macro_offset.obj:	precompiled.h macro_offset.h dlldefs.h
master.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h cti_asmc.h queues.h constants.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h master.h
metricidlookup.obj:	precompiled.h MetricIdLookup.h PointAttribute.h \
		yukon.h types.h ctidbgmem.h dlldefs.h
millisecond_timer.obj:	precompiled.h millisecond_timer.h dlldefs.h
module_util.obj:	precompiled.h module_util.h dlldefs.h ctitime.h \
		win_helper.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h critical_section.h dllbase.h guard.h
mutex.obj:	precompiled.h mutex.h dlldefs.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h std_helper.h win_helper.h
numstr.obj:	precompiled.h numstr.h dlldefs.h
observe.obj:	precompiled.h observe.h types.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h
parse.obj:	precompiled.h
pointattribute.obj:	precompiled.h PointAttribute.h yukon.h types.h \
		ctidbgmem.h dlldefs.h
queue.obj:	precompiled.h queue.h cparms.h dlldefs.h configkey.h \
		configval.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h
queues.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		yukon.h ctidbgmem.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h constants.h numstr.h critical_section.h \
		dllbase.h
readers_writer_lock.obj:	precompiled.h readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h
regression.obj:	precompiled.h cparms.h dlldefs.h configkey.h \
		configval.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h regression.h
repeaterrole.obj:	precompiled.h repeaterrole.h dlldefs.h
resolvers.obj:	precompiled.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h resolvers.h pointtypes.h db_entry_defines.h \
		devicetypes.h std_helper.h
resource_helper.obj:	precompiled.h resource_helper.h dlldefs.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h
rtdb.obj:	precompiled.h dlldefs.h rtdb.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h dllbase.h critical_section.h
sema.obj:	precompiled.h sema.h dlldefs.h
stdexcepthdlr.obj:	precompiled.h stdexcepthdlr.h dlldefs.h
streambuffer.obj:	precompiled.h string_util.h dlldefs.h streamBuffer.h \
		loggable.h ctidate.h ctitime.h numstr.h
streamlocalconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h millisecond_timer.h win_helper.h \
		streamLocalConnection.h streamConnection.h netports.h \
		timing_util.h immutable.h guard.h dsm2.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h
streamsocketconnection.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h std_helper.h win_helper.h \
		millisecond_timer.h streamSocketConnection.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h socket_helper.h
streamsocketlistener.obj:	precompiled.h logger.h dlldefs.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h std_helper.h win_helper.h \
		millisecond_timer.h streamSocketListener.h socket_helper.h \
		guard.h timing_util.h streamSocketConnection.h \
		streamConnection.h netports.h immutable.h
string_util.obj:	precompiled.h string_util.h dlldefs.h streamBuffer.h \
		loggable.h
test_cmdparse.obj:	cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h \
		test_cmdparse_input.h test_cmdparse_output.h
test_ctidate.obj:	ctidate.h dlldefs.h ctitime.h
test_ctipcptrqueue.obj:	ctipcptrqueue.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h critical_section.h millisecond_timer.h
test_ctitime.obj:	ctitime.h dlldefs.h ctidate.h boost_test_helpers.h \
		millisecond_timer.h
test_database_util.obj:	database_util.h database_writer.h ctitime.h \
		dlldefs.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h queues.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h row_writer.h
test_date_utility.obj:	date_utility.h ctidate.h dlldefs.h
test_dbaccess.obj:	dbaccess.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h
test_desolvers.obj:	desolvers.h dlldefs.h pointtypes.h devicetypes.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h numstr.h
test_encryption.obj:	encryption.h dlldefs.h
test_error.obj:	yukon.h types.h ctidbgmem.h
test_logger.obj:	logManager.h dlldefs.h module_util.h ctitime.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		critical_section.h ctidate.h
test_multiset.obj:	utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h
test_numstr.obj:	numstr.h dlldefs.h
test_old_queues.obj:	dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
test_pointattribute.obj:	PointAttribute.h yukon.h types.h ctidbgmem.h \
		dlldefs.h
test_queue.obj:	queue.h cparms.h dlldefs.h configkey.h configval.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h dbaccess.h dllbase.h
test_readers_writer_lock.obj:	readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h
test_resolvers.obj:	dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h resolvers.h pointtypes.h db_entry_defines.h \
		devicetypes.h boost_test_helpers.h millisecond_timer.h
test_std_helper.obj:	std_helper.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h
test_string_formatter.obj:	string_util.h dlldefs.h streamBuffer.h \
		loggable.h
test_string_util.obj:	string_util.h dlldefs.h streamBuffer.h \
		loggable.h
test_timeperiod.obj:	timeperiod.h dlldefs.h ctitime.h ctidate.h
test_utility.obj:	utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		ctidate.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h devicetypes.h
test_words.obj:	boost_test_helpers.h millisecond_timer.h dlldefs.h \
		ctitime.h
thread.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h win_helper.h
threadstatuskeeper.obj:	precompiled.h ThreadStatusKeeper.h ctitime.h \
		dlldefs.h thread_register_data.h boost_time.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h boostutil.h utility.h \
		queues.h constants.h numstr.h thread_monitor.h smartmap.h \
		dllbase.h critical_section.h readers_writer_lock.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h cparms.h configkey.h configval.h queue.h \
		thread.h mutex.h
thread_listener.obj:	precompiled.h thread_listener.h thread.h mutex.h \
		dlldefs.h guard.h utility.h ctitime.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h
thread_monitor.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h thread_monitor.h smartmap.h \
		readers_writer_lock.h guard.h cparms.h configkey.h \
		configval.h queue.h thread.h mutex.h thread_register_data.h \
		boost_time.h
thread_register_data.obj:	precompiled.h thread_register_data.h \
		boost_time.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h dlldefs.h boostutil.h utility.h ctitime.h queues.h \
		constants.h numstr.h
timeperiod.obj:	precompiled.h ctitime.h dlldefs.h timeperiod.h
timing_util.obj:	precompiled.h numstr.h dlldefs.h timing_util.h
truncatingconsoleappender.obj:	precompiled.h \
		truncatingConsoleAppender.h dlldefs.h timing_util.h \
		streamBuffer.h loggable.h
ucttime.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		guard.h utility.h ctitime.h queues.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h elogger.h
utility.obj:	precompiled.h dbaccess.h dllbase.h os2_2w32.h dlldefs.h \
		types.h cticalls.h yukon.h ctidbgmem.h critical_section.h \
		database_reader.h database_connection.h guard.h utility.h \
		ctitime.h queues.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h row_reader.h database_transaction.h \
		database_writer.h row_writer.h database_util.h pointdefs.h \
		ctidate.h devicetypes.h desolvers.h pointtypes.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h
verification_objects.obj:	precompiled.h verification_objects.h \
		dlldefs.h boost_time.h dsm2.h streamConnection.h yukon.h \
		types.h ctidbgmem.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h
win_helper.obj:	precompiled.h win_helper.h numstr.h dlldefs.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h yukon.h \
		types.h ctidbgmem.h os2_2w32.h constants.h critical_section.h
words.obj:	precompiled.h words.h dlldefs.h cticalls.h yukon.h types.h \
		ctidbgmem.h os2_2w32.h optional.h cti_asmc.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h utility.h ctitime.h queues.h constants.h numstr.h \
		critical_section.h
worker_thread.obj:	precompiled.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h \
		critical_section.h worker_thread.h timing_util.h
xfer.obj:	precompiled.h xfer.h dlldefs.h yukon.h types.h ctidbgmem.h
xml.obj:	precompiled.h logger.h dlldefs.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h critical_section.h xml.h \
		devicetypes.h pointtypes.h resource_helper.h PointAttribute.h \
		DeviceConfigDescription.h DeviceAttributeLookup.h resolvers.h \
		db_entry_defines.h std_helper.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc

