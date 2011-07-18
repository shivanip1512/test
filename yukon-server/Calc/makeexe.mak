include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(RW) \
-I$(BOOST) \
-I$(SQLAPI)\include \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \
;$(RW)



CALCOBJS= \
calc.obj \
calccomponent.obj \
calc_logic.obj \
pointstore.obj \
calcthread.obj \
calclogicsvc.obj \



LURKOBJS= \
lurk.obj \

LOGOBJS= \
log.obj \

NEWVALOBJS= \
newval.obj \

NEWVALRNGOBJS= \
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
$(COMPILEBASE)\lib\cticparms.lib


TESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib


CALCLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
advapi32.lib


CALC_FULLBUILD = $[Filename,$(OBJ),CalcFullBuild,target]


ALL:            $(CTIPROGS)


$(CALC_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	$(RWCPPINVOKE) /Fm $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) $(PCHFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(CALCOBJS)]


calc_logic.exe:  $(CALC_FULLBUILD) $(CALCOBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) /Fm $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ $(CALCOBJS) -link $(RWLIBS) $(BOOST_LIBS) $(CALCLIBS) $(LINKFLAGS)
	   -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)
#--  START TEST APPLICATIONS
lurk.exe:       $(LURKOBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ $(LURKOBJS) -link $(RWLIBS) $(BOOST_LIBS) $(TESTLIBS)
	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)

log.exe:        $(LOGOBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ $(LOGOBJS) -link $(RWLIBS) $(BOOST_LIBS) $(TESTLIBS)
	   -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)

newval.exe:     $(NEWVALOBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ $(NEWVALOBJS) -link $(RWLIBS) $(BOOST_LIBS) $(TESTLIBS)
	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
           -copy ..\$@ $(YUKONOUTPUT)
        @%cd $(CWD)

newvalrng.exe:     $(NEWVALRNGOBJS) makeexe.mak
        @echo:
        @echo Compiling ..\$@
        @%cd $(OBJ)
        $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ $(NEWVALRNGOBJS) -link $(RWLIBS) $(BOOST_LIBS) $(TESTLIBS)
	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
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
    $(RWCPPINVOKE) /Fm $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) -DIMPORT $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################



#UPDATE#
calc.obj:	precompiled.h calc.h ctiqueues.h calccomponent.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h pointstore.h hashkey.h hash_functions.h \
		rtdb.h string_utility.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		pointdefs.h regression.h tbl_pt_limit.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h row_reader.h
calccomponent.obj:	precompiled.h calccomponent.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h pointstore.h hashkey.h hash_functions.h \
		rtdb.h string_utility.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		pointdefs.h regression.h tbl_pt_limit.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h row_reader.h calc.h ctiqueues.h \
		database_connection.h database_reader.h
calclogicsvc.obj:	precompiled.h id_calc.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h \
		netports.h mutex.h guard.h dsm2err.h words.h optional.h \
		sema.h ctinexus.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_cmd.h msg_reg.h msg_signal.h msg_ptreg.h \
		msg_dbchg.h configparms.h cparms.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h ThreadStatusKeeper.h \
		thread_register_data.h thread_monitor.h smartmap.h \
		readers_writer_lock.h critical_section.h queue.h \
		string_utility.h calclogicsvc.h connection.h exchange.h \
		cservice.h calcthread.h hashkey.h hash_functions.h calc.h \
		ctiqueues.h calccomponent.h ctidate.h pointstore.h rtdb.h \
		regression.h tbl_pt_limit.h dbmemobject.h resolvers.h \
		db_entry_defines.h desolvers.h
calcthread.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h sema.h ctibase.h \
		ctinexus.h pointtypes.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h msg_multi.h msg_pdata.h pointdefs.h \
		logger.h thread.h CtiPCPtrQueue.h cparms.h configkey.h \
		configval.h mgr_holiday.h ctidate.h ThreadStatusKeeper.h \
		thread_register_data.h thread_monitor.h smartmap.h \
		readers_writer_lock.h critical_section.h queue.h \
		string_utility.h calcthread.h hashkey.h hash_functions.h \
		calc.h ctiqueues.h calccomponent.h pointstore.h rtdb.h \
		regression.h tbl_pt_limit.h dbmemobject.h resolvers.h \
		db_entry_defines.h desolvers.h database_writer.h row_writer.h
calc_logic.obj:	precompiled.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h sema.h CServiceConfig.h \
		rtdb.h hashkey.h hash_functions.h string_utility.h ctibase.h \
		ctinexus.h cparms.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h configparms.h calclogicsvc.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		message.h collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cservice.h \
		calcthread.h calc.h ctiqueues.h calccomponent.h ctidate.h \
		pointstore.h regression.h tbl_pt_limit.h dbmemobject.h \
		resolvers.h db_entry_defines.h desolvers.h thread_monitor.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		thread_register_data.h msg_dbchg.h
log.obj:	precompiled.h connection.h dlldefs.h exchange.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h ctinexus.h msg_cmd.h
lurk.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h message.h collectable.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h ctinexus.h \
		msg_cmd.h msg_dbchg.h
newval.obj:	precompiled.h queue.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h exchange.h \
		message.h collectable.h msg_cmd.h msg_reg.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		connection.h
newvalrng.obj:	precompiled.h queue.h cparms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h exchange.h \
		message.h collectable.h msg_cmd.h msg_reg.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		connection.h
pointstore.obj:	precompiled.h pointstore.h hashkey.h hash_functions.h \
		dlldefs.h rtdb.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h string_utility.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h guard.h \
		dsm2err.h words.h optional.h pointdefs.h regression.h \
		tbl_pt_limit.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h row_reader.h \
		ctidate.h logger.h thread.h CtiPCPtrQueue.h
test_calc.obj:	precompiled.h calc.h ctiqueues.h calccomponent.h \
		ctitime.h dlldefs.h ctidate.h logger.h thread.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h CtiPCPtrQueue.h pointstore.h hashkey.h \
		hash_functions.h rtdb.h string_utility.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h pointdefs.h regression.h tbl_pt_limit.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h row_reader.h
#ENDUPDATE#


