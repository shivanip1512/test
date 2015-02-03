include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(SERVICE)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \



CALCOBJS= \
$(PRECOMPILED_OBJ) \
calc.obj \
calccomponent.obj \
calc_logic.obj \
pointstore.obj \
calcthread.obj \
calclogicsvc.obj \



LURKOBJS= \
$(PRECOMPILED_OBJ) \
lurk.obj \

LOGOBJS= \
$(PRECOMPILED_OBJ) \
log.obj \

NEWVALOBJS= \
$(PRECOMPILED_OBJ) \
newval.obj \

NEWVALRNGOBJS= \
$(PRECOMPILED_OBJ) \
newvalrng.obj \

WINLIBS=kernel32.lib user32.lib
SOCKSLIB=wsock32.lib

CTIPROGS=\
calc_logic.exe \
lurk.exe \
newval.exe \
newvalrng.exe \
log.exe


VGLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \


TESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \


CALCLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
advapi32.lib


CALC_FULLBUILD = $[Filename,$(OBJ),CalcFullBuild,target]

PROGS_VERSION=\
$(CTIPROGS)

ALL:            $(CTIPROGS)


$(CALC_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	$(CC) /Fm $(CCOPTS) $(CFLAGS) $(PARALLEL) $(PCHFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(CALCOBJS)]


calc_logic.exe:  $(CALC_FULLBUILD) $(CALCOBJS) makeexe.mak $(OBJ)\calc_logic.res
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(CC) /Fm $(CFLAGS) $(INCLPATHS) /Fe..\$@ $(CALCOBJS) -link $(BOOST_LIBS) $(CALCLIBS) calc_logic.res
	   -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)
#--  START TEST APPLICATIONS
lurk.exe:       $(LURKOBJS) makeexe.mak $(OBJ)\lurk.res
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$@ $(LURKOBJS) -link $(BOOST_LIBS) $(TESTLIBS) lurk.res
	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)

log.exe:        $(LOGOBJS) makeexe.mak $(OBJ)\log.res
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$@ $(LOGOBJS) -link $(BOOST_LIBS) $(TESTLIBS) log.res
	   -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)

newval.exe:     $(NEWVALOBJS) makeexe.mak $(OBJ)\newval.res
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$@ $(NEWVALOBJS) -link $(BOOST_LIBS) $(TESTLIBS) newval.res
	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)

newvalrng.exe:     $(NEWVALRNGOBJS) makeexe.mak $(OBJ)\newvalrng.res
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$@ $(NEWVALRNGOBJS) -link $(BOOST_LIBS) $(TESTLIBS) newvalrng.res
	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)
#--  END TEST APPLICATIONS

copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
        scandeps -Output makeexe.mak *.cpp



clean:
    -del *.obj
    -del *.pch
    -del *.pdb
    -del *.sdb
    -del *.adb
    -del *.ilk
    -del *.exe


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
    @echo:
    @echo Compiling cpp to obj
    $(CC) /Fm $(CCOPTS) $(CFLAGS) $(PCHFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################



#UPDATE#
calc.obj:	precompiled.h calc.h ctiqueues.h calccomponent.h ctitime.h \
		dlldefs.h ctidate.h pointstore.h rtdb.h utility.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h dllbase.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h atomic.h critical_section.h guard.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h pointdefs.h regression.h \
		tbl_pt_limit.h dbmemobject.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h
calccomponent.obj:	precompiled.h calccomponent.h ctitime.h dlldefs.h \
		ctidate.h pointstore.h rtdb.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h pointdefs.h regression.h tbl_pt_limit.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h calc.h ctiqueues.h \
		database_connection.h database_reader.h
calclogicsvc.obj:	precompiled.h id_calc.h module_util.h dlldefs.h \
		ctitime.h dbaccess.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h message.h collectable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_cmd.h msg_reg.h \
		msg_signal.h msg_ptreg.h msg_dbchg.h logManager.h cparms.h \
		configkey.h configval.h database_connection.h \
		database_reader.h row_reader.h ThreadStatusKeeper.h \
		thread_register_data.h boost_time.h thread_monitor.h \
		smartmap.h readers_writer_lock.h queue.h thread.h \
		calclogicsvc.h connection_client.h connection.h \
		connection_base.h worker_thread.h cservice.h calcthread.h \
		calc.h ctiqueues.h calccomponent.h ctidate.h pointstore.h \
		rtdb.h regression.h tbl_pt_limit.h dbmemobject.h resolvers.h \
		db_entry_defines.h amq_constants.h
calcthread.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h pointtypes.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h cparms.h \
		configkey.h configval.h mgr_holiday.h ctidate.h \
		ThreadStatusKeeper.h thread_register_data.h boost_time.h \
		thread_monitor.h smartmap.h readers_writer_lock.h queue.h \
		thread.h calcthread.h calc.h ctiqueues.h calccomponent.h \
		pointstore.h rtdb.h regression.h tbl_pt_limit.h dbmemobject.h \
		resolvers.h db_entry_defines.h row_reader.h worker_thread.h \
		database_writer.h database_connection.h row_writer.h \
		database_reader.h database_util.h
calc_logic.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h atomic.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h CServiceConfig.h rtdb.h ctibase.h \
		streamSocketConnection.h socket_helper.h win_helper.h \
		cparms.h configkey.h configval.h calclogicsvc.h \
		connection_client.h connection.h message.h collectable.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h readers_writer_lock.h connection_base.h \
		worker_thread.h cservice.h calcthread.h calc.h ctiqueues.h \
		calccomponent.h ctidate.h pointstore.h regression.h \
		tbl_pt_limit.h dbmemobject.h resolvers.h db_entry_defines.h \
		row_reader.h thread_monitor.h smartmap.h thread.h \
		thread_register_data.h boost_time.h msg_dbchg.h logManager.h \
		module_util.h
log.obj:	precompiled.h connection_client.h connection.h dlldefs.h \
		message.h ctitime.h ctidbgmem.h collectable.h loggable.h \
		msg_multi.h msg_pdata.h yukon.h types.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		configkey.h configval.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h critical_section.h atomic.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h amq_constants.h msg_cmd.h
lurk.obj:	precompiled.h cparms.h dlldefs.h configkey.h configval.h \
		message.h ctitime.h ctidbgmem.h collectable.h loggable.h \
		connection_client.h connection.h msg_multi.h msg_pdata.h \
		yukon.h types.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		critical_section.h atomic.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		amq_constants.h msg_cmd.h msg_dbchg.h
newval.obj:	precompiled.h queue.h cparms.h dlldefs.h configkey.h \
		configval.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h atomic.h netports.h message.h \
		collectable.h msg_cmd.h msg_reg.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h dsm2.h \
		streamConnection.h timing_util.h immutable.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_ptreg.h \
		connection_client.h connection.h readers_writer_lock.h \
		connection_base.h worker_thread.h amq_constants.h
newvalrng.obj:	precompiled.h queue.h cparms.h dlldefs.h configkey.h \
		configval.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h atomic.h netports.h message.h \
		collectable.h msg_cmd.h msg_reg.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h dsm2.h \
		streamConnection.h timing_util.h immutable.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h msg_ptreg.h \
		connection_client.h connection.h readers_writer_lock.h \
		connection_base.h worker_thread.h amq_constants.h
pointstore.obj:	precompiled.h pointstore.h rtdb.h dlldefs.h utility.h \
		ctitime.h queues.h cticalls.h yukon.h types.h ctidbgmem.h \
		os2_2w32.h constants.h numstr.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		atomic.h critical_section.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h boostutil.h \
		mutex.h dsm2err.h words.h optional.h macro_offset.h \
		pointdefs.h regression.h tbl_pt_limit.h dbmemobject.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h ctidate.h std_helper.h
test_calc.obj:	calc.h ctiqueues.h calccomponent.h ctitime.h dlldefs.h \
		ctidate.h pointstore.h rtdb.h utility.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h atomic.h critical_section.h guard.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h mutex.h dsm2err.h words.h optional.h \
		macro_offset.h pointdefs.h regression.h tbl_pt_limit.h \
		dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
