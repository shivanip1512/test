#Build name MUST BE FIRST!!!!

DLLBUILDNAME = -DCTIBASE

.include global.inc
.include rwglobal.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(BOOST) \
-I$(SQLAPI)\include \
-I$(RW) \
-I$(DBGHELP)\include

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
;$(BOOST) \
;$(RW) \
;$(SQLAPI)\include \


BASEOBJS=\
ctidate.obj \
ctitime.obj \
ctistring.obj \
ctitokenizer.obj \
resolvers.obj \
numstr.obj \
bfexec.obj \
c_port_interface.obj \
cmdparse.obj \
counter.obj \
critical_section.obj \
ctdpcptrq.obj \
cti_asmc.obj \
cticalls.obj \
ctilocalconnect.obj \
ctinexus.obj \
database_writer.obj \
database_connection.obj \
database_reader.obj \
dbaccess.obj \
debug_timer.obj \
desolvers.obj \
dllbase.obj \
exchange.obj \
elog_cli.obj \
error.obj \
fileint.obj \
guard.obj \
hash_functions.obj \
logger.obj \
master.obj \
millisecond_timer.obj \
mutex.obj \
observe.obj \
pexec.obj \
queue.obj \
queues.obj \
readers_writer_lock.obj \
regression.obj \
repeaterrole.obj \
rtdb.obj \
sema.obj \
stdexcepthdlr.obj \
string_utility.obj \
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


CTIPROGS=\
ctibase.dll


WINLIBS=kernel32.lib user32.lib advapi32.lib wsock32.lib winmm.lib

SQLAPILIB=$(SQLAPI)\lib\$(SQLAPI_LIB).lib


COMMON_FULLBUILD = $[Filename,$(OBJ),CommonFullBuild,target]


ALL:            $(CTIPROGS)
                -@if exist $(BOOST)\stage\lib\boost_thread-vc6-mt-gd-1_31.dll copy $(BOOST)\stage\lib\boost_thread-vc6-mt-gd-1_31.dll $(YUKONOUTPUT)

$(COMMON_FULLBUILD):
        @touch $@
        @echo:
        @echo Compiling cpp to obj
        @echo:
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(PARALLEL) $(DLLBUILDNAME) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(BASEOBJS)]


ctibase.dll:    $(COMMON_FULLBUILD) $(BASEOBJS) Makefile
                @build -nologo -f $(_InputFile) id
                @%cd $(OBJ)
                $(CC) $(BASEOBJS) id_ctibase.obj $(WINLIBS) $(SQLAPILIB) $(DLLFLAGS) $(RWLIBS) $(BOOST_LIBS) $(COMPILEBASE)\lib\cticparms.lib $(DBGHELP_LIBS) /Fe..\$@ $(LINKFLAGS)
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
                -@if exist $(BOOST)\stage\lib\boost_thread-vc6-mt-gd-1_31.dll copy $(BOOST)\stage\lib\boost_thread-vc6-mt-gd-1_31.dll $(YUKONOUTPUT)

deps:
                scandeps -Output makebase.mak *.cpp

clean:
        -del \
*.pdb \
*.idb \
*.obj \
$(OBJ)\*.obj \
$(OBJ)\*.target \
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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(DLLBUILDNAME) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################



#UPDATE#
attributeservice.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		AttributeService.h LitePoint.h dlldefs.h pointtypes.h \
		PointAttribute.h resolvers.h db_entry_defines.h numstr.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		database_connection.h row_reader.h
bfexec.obj:	yukon.h precompiled.h types.h ctidbgmem.h bfexec.h
cmdparse.obj:	yukon.h precompiled.h types.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h logger.h thread.h \
		CtiPCPtrQueue.h pointdefs.h ctistring.h
cmdparsetestgenerator.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h numstr.h \
		test_cmdparse_input.h
counter.obj:	yukon.h precompiled.h types.h ctidbgmem.h counter.h \
		guard.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h numstr.h mutex.h
critical_section.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		critical_section.h dlldefs.h
ctdpcptrq.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctdpcptrq.h \
		dlldefs.h CtiPCPtrQueue.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h
cticalls.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h dllbase.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h
ctidate.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
ctilocalconnect.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		cparms.h dlldefs.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h ctilocalconnect.h \
		critical_section.h
ctinexus.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h ctinexus.h netports.h cticonnect.h logger.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h numstr.h CtiPCPtrQueue.h dsm2.h dsm2err.h words.h \
		optional.h millisecond_timer.h
ctistring.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctistring.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h
ctitime.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctidate.h \
		dlldefs.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
ctitokenizer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctitokenizer.h dlldefs.h
cti_asmc.obj:	yukon.h precompiled.h types.h ctidbgmem.h cti_asmc.h \
		dlldefs.h cticalls.h os2_2w32.h
c_port_interface.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dlldefs.h os2_2w32.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h porter.h \
		devicetypes.h c_port_interface.h elogger.h
database_connection.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h logger.h thread.h CtiPCPtrQueue.h
database_reader.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		database_reader.h ctitime.h dlldefs.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h row_reader.h logger.h \
		thread.h CtiPCPtrQueue.h
database_writer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		database_writer.h ctitime.h dlldefs.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h row_writer.h ctidate.h \
		logger.h thread.h CtiPCPtrQueue.h boost_time.h
date_utility.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		date_utility.h ctidate.h dlldefs.h logger.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h CtiPCPtrQueue.h ctitokenizer.h
dbaccess.obj:	yukon.h precompiled.h types.h ctidbgmem.h dlldefs.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h logger.h \
		thread.h CtiPCPtrQueue.h cparms.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h ctistring.h \
		database_writer.h row_writer.h
debug_timer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		debug_timer.h dlldefs.h logger.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
desolvers.obj:	yukon.h precompiled.h types.h ctidbgmem.h desolvers.h \
		dlldefs.h dsm2.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h resolvers.h pointtypes.h \
		db_entry_defines.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h
dllbase.obj:	yukon.h precompiled.h types.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h configparms.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h ctinexus.h logger.h thread.h \
		CtiPCPtrQueue.h thread_monitor.h smartmap.h \
		readers_writer_lock.h critical_section.h queue.h \
		string_utility.h thread_register_data.h
elog_cli.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h elogger.h logger.h thread.h \
		CtiPCPtrQueue.h
error.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h dllbase.h logger.h thread.h \
		CtiPCPtrQueue.h
exchange.obj:	yukon.h precompiled.h types.h ctidbgmem.h exchange.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h
fileint.obj:	yukon.h precompiled.h types.h ctidbgmem.h fileint.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h
guard.obj:	yukon.h precompiled.h types.h ctidbgmem.h guard.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h
hash_functions.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		hash_functions.h dlldefs.h
id_ctibase.obj:	yukon.h precompiled.h types.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		id_ctibase.h
litepoint.obj:	yukon.h precompiled.h types.h ctidbgmem.h LitePoint.h \
		dlldefs.h pointtypes.h
logger.obj:	yukon.h precompiled.h types.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		cparms.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h mutex.h guard.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h
master.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h cti_asmc.h queues.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h master.h
millisecond_timer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		millisecond_timer.h dlldefs.h
mutex.obj:	yukon.h precompiled.h types.h ctidbgmem.h mutex.h dlldefs.h
numstr.obj:	yukon.h precompiled.h types.h ctidbgmem.h numstr.h \
		dlldefs.h
observe.obj:	yukon.h precompiled.h types.h ctidbgmem.h observe.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h
parse.obj:	yukon.h precompiled.h types.h ctidbgmem.h
pexec.obj:	yukon.h precompiled.h types.h ctidbgmem.h porter.h dsm2.h \
		mutex.h dlldefs.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h ctinexus.h
pointattribute.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PointAttribute.h dlldefs.h
point_change.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		point_change.h dlldefs.h
portsup.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h queues.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h dllbase.h color.h devicetypes.h \
		logger.h thread.h CtiPCPtrQueue.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
queent.obj:	yukon.h precompiled.h types.h ctidbgmem.h queent.h \
		dlldefs.h
queue.obj:	yukon.h precompiled.h types.h ctidbgmem.h queue.h cparms.h \
		dlldefs.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h
queues.obj:	yukon.h precompiled.h types.h ctidbgmem.h os2_2w32.h \
		dlldefs.h cticalls.h logger.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h CtiPCPtrQueue.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h
readers_writer_lock.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		readers_writer_lock.h dlldefs.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h
regression.obj:	yukon.h precompiled.h types.h ctidbgmem.h cparms.h \
		dlldefs.h rwutil.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h regression.h
repeaterrole.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		repeaterrole.h dlldefs.h
resolvers.obj:	yukon.h precompiled.h types.h ctidbgmem.h dsm2.h \
		mutex.h dlldefs.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h resolvers.h pointtypes.h \
		db_entry_defines.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h
rtdb.obj:	yukon.h precompiled.h types.h ctidbgmem.h dlldefs.h rtdb.h \
		hashkey.h hash_functions.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h string_utility.h dllbase.h \
		dsm2.h mutex.h guard.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h
sema.obj:	yukon.h precompiled.h types.h ctidbgmem.h sema.h dlldefs.h
stdexcepthdlr.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		stdexcepthdlr.h dlldefs.h
string_utility.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		string_utility.h dlldefs.h
test_cmdparse.obj:	boostutil.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h test_cmdparse_input.h \
		test_cmdparse_output.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctistring.h
test_ctidate.obj:	boostutil.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h ctidate.h logger.h \
		thread.h mutex.h guard.h CtiPCPtrQueue.h
test_ctipcptrqueue.obj:	ctipcptrqueue.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h boostutil.h
test_ctistring.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctistring.h dlldefs.h
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
test_logger.obj:	yukon.h precompiled.h types.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h
test_multiset.obj:	utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h
test_numstr.obj:	yukon.h precompiled.h types.h ctidbgmem.h numstr.h \
		dlldefs.h
test_old_queues.obj:	dsm2.h mutex.h dlldefs.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		cticonnect.h yukon.h precompiled.h ctidbgmem.h netports.h \
		dsm2err.h words.h optional.h
test_pointattribute.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PointAttribute.h dlldefs.h
test_queue.obj:	queue.h cparms.h dlldefs.h rwutil.h yukon.h \
		precompiled.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h
test_readers_writer_lock.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h readers_writer_lock.h dlldefs.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h
test_resolvers.obj:	yukon.h precompiled.h types.h ctidbgmem.h dsm2.h \
		mutex.h dlldefs.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h resolvers.h pointtypes.h \
		db_entry_defines.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h
test_rwutil.obj:	rwutil.h yukon.h precompiled.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h
test_string_utility.obj:	string_utility.h dlldefs.h
test_timeperiod.obj:	timeperiod.h dlldefs.h ctitime.h ctidate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h
test_utility.obj:	yukon.h precompiled.h types.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2.h mutex.h guard.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h devicetypes.h
tfexec.obj:	yukon.h precompiled.h types.h ctidbgmem.h tfexec.h
thread.obj:	yukon.h precompiled.h types.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h thread.h
threadstatuskeeper.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ThreadStatusKeeper.h ctitime.h dlldefs.h \
		thread_register_data.h boost_time.h cticalls.h os2_2w32.h \
		boostutil.h utility.h queues.h numstr.h thread_monitor.h \
		smartmap.h dllbase.h dsm2.h mutex.h guard.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h cparms.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h configkey.h \
		configval.h queue.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h
thread_listener.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		thread_listener.h thread.h mutex.h dlldefs.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h
thread_monitor.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dllbase.h os2_2w32.h dlldefs.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h thread_monitor.h smartmap.h boostutil.h \
		readers_writer_lock.h critical_section.h cparms.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h queue.h \
		string_utility.h thread_register_data.h
thread_register_data.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		thread_register_data.h boost_time.h cticalls.h os2_2w32.h \
		dlldefs.h boostutil.h utility.h ctitime.h queues.h numstr.h
thread_timer.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		thread_timer.h thread.h mutex.h dlldefs.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		thread_register_data.h boost_time.h boostutil.h
timeperiod.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h timeperiod.h
ucttime.obj:	yukon.h precompiled.h types.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h elogger.h logger.h thread.h \
		CtiPCPtrQueue.h
utility.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h database_connection.h row_reader.h \
		database_writer.h row_writer.h logger.h thread.h \
		CtiPCPtrQueue.h pointdefs.h ctidate.h devicetypes.h
verification_objects.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		verification_objects.h dlldefs.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		boost_time.h
words.obj:	yukon.h precompiled.h types.h ctidbgmem.h words.h dlldefs.h \
		cticalls.h os2_2w32.h optional.h cti_asmc.h dsm2err.h \
		logger.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h CtiPCPtrQueue.h
xfer.obj:	yukon.h precompiled.h types.h ctidbgmem.h xfer.h dsm2.h \
		mutex.h dlldefs.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h
#ENDUPDATE#
