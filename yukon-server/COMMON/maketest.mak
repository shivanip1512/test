#Build name MUST BE FIRST!!!!

.include global.inc
.include rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(BOOST) \
-I$(RW)

.PATH.cpp = .;$(R_COMMON)

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
;$(BOOST) \
;$(RW)

TESTOBJS=\
cmdparsetestgenerator.obj \
test_cmdparse.obj \
test_ctidate.obj \
test_CtiPCPtrQueue.obj \
test_ctistring.obj \
test_ctitime.obj \
test_fifo_multiset.obj \
test_hash.obj \
test_logger.obj \
test_numstr.obj \
test_queue.obj \
test_resolvers.obj \
test_rwutil.obj \
test_utility.obj \


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
        .\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\cticparms.lib $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\clrdump.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS) $(LINKFLAGS)

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
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
argkey.obj:	yukon.h precompiled.h ctidbgmem.h argkey.h
argval.obj:	yukon.h precompiled.h ctidbgmem.h argval.h
bfexec.obj:	yukon.h precompiled.h ctidbgmem.h bfexec.h
cmdopts.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h argkey.h \
		argval.h cmdopts.h
cmdparse.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h devicetypes.h logger.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h pointdefs.h ctistring.h rwutil.h \
		boost_time.h boostutil.h
cmdparsetestgenerator.obj:	yukon.h precompiled.h ctidbgmem.h \
		cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h numstr.h \
		test_cmdparse_input.h
counter.obj:	yukon.h precompiled.h ctidbgmem.h counter.h guard.h \
		numstr.h dlldefs.h clrdump.h mutex.h
critical_section.obj:	yukon.h precompiled.h ctidbgmem.h \
		critical_section.h dlldefs.h
ctdpcptrq.obj:	yukon.h precompiled.h ctidbgmem.h ctdpcptrq.h dlldefs.h \
		CtiPCPtrQueue.h mutex.h guard.h numstr.h clrdump.h
cticalls.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dllbase.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
ctidate.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h
ctilocalconnect.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ctilocalconnect.h \
		critical_section.h netports.h cticonnect.h fifo_multiset.h \
		dsm2.h
ctinexus.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h ctinexus.h netports.h cticonnect.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h sorted_vector.h dsm2.h
ctistring.obj:	yukon.h precompiled.h ctidbgmem.h ctistring.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h
ctitime.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h
ctitokenizer.obj:	yukon.h precompiled.h ctidbgmem.h ctitokenizer.h \
		dlldefs.h
cti_asmc.obj:	yukon.h precompiled.h ctidbgmem.h cticalls.h os2_2w32.h \
		dlldefs.h types.h cti_asmc.h
c_port_interface.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h \
		os2_2w32.h types.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h queues.h cticalls.h porter.h \
		dsm2err.h devicetypes.h c_port_interface.h elogger.h
dbaccess.obj:	yukon.h precompiled.h ctidbgmem.h types.h dlldefs.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h
debug_timer.obj:	yukon.h precompiled.h ctidbgmem.h debug_timer.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h
desolvers.obj:	yukon.h precompiled.h ctidbgmem.h desolvers.h \
		db_entry_defines.h dlldefs.h types.h pointtypes.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		resolvers.h devicetypes.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		sorted_vector.h
dllbase.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		os2_2w32.h types.h cticalls.h dbaccess.h dllbase.h sema.h \
		ctinexus.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h thread_monitor.h \
		smartmap.h boostutil.h readers_writer_lock.h \
		critical_section.h queue.h thread_register_data.h \
		boost_time.h
elog_cli.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h elogger.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
error.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h dsm2err.h dllbase.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
exchange.obj:	yukon.h precompiled.h ctidbgmem.h exchange.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h
fileint.obj:	yukon.h precompiled.h ctidbgmem.h fileint.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h ctitime.h
guard.obj:	yukon.h precompiled.h ctidbgmem.h guard.h numstr.h \
		dlldefs.h clrdump.h
hash_functions.obj:	yukon.h precompiled.h ctidbgmem.h hash_functions.h \
		dlldefs.h
id_ctibase.obj:	yukon.h precompiled.h ctidbgmem.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h id_ctibase.h id_vinfo.h
logger.obj:	yukon.h precompiled.h ctidbgmem.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h dllbase.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h logger.h thread.h CtiPCPtrQueue.h
master.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cti_asmc.h queues.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dsm2err.h master.h
mutex.obj:	yukon.h precompiled.h ctidbgmem.h mutex.h dlldefs.h
numstr.obj:	yukon.h precompiled.h ctidbgmem.h numstr.h dlldefs.h
observe.obj:	yukon.h precompiled.h ctidbgmem.h observe.h types.h \
		dlldefs.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h sorted_vector.h
parse.obj:	yukon.h precompiled.h ctidbgmem.h
pending_stat_operation.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h pending_stat_operation.h \
		ctitypes.h dsm2.h cticonnect.h netports.h
pexec.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h ctinexus.h netports.h cticonnect.h \
		queues.h dsm2.h mutex.h guard.h numstr.h clrdump.h dsm2err.h \
		dllbase.h porter.h devicetypes.h master.h c_port_interface.h \
		elogger.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h sorted_vector.h
point_change.obj:	yukon.h precompiled.h ctidbgmem.h point_change.h \
		dlldefs.h
portsup.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h dllbase.h color.h \
		devicetypes.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h sorted_vector.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
queent.obj:	yukon.h precompiled.h ctidbgmem.h queent.h dlldefs.h
quetest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h
queue.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h
queues.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h dllbase.h dsm2.h cticonnect.h netports.h
readers_writer_lock.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h readers_writer_lock.h \
		critical_section.h
regression.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h regression.h
repeaterrole.obj:	yukon.h precompiled.h ctidbgmem.h repeaterrole.h \
		dlldefs.h
resolvers.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		resolvers.h types.h pointtypes.h db_entry_defines.h \
		devicetypes.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h os2_2w32.h sorted_vector.h
rtdb.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h rtdb.h hashkey.h \
		hash_functions.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h dllbase.h dsm2.h \
		mutex.h guard.h clrdump.h cticonnect.h netports.h
sema.obj:	yukon.h precompiled.h ctidbgmem.h sema.h dlldefs.h
statistics.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h sema.h \
		dsm2err.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h statistics.h counter.h \
		ctidate.h rwutil.h boost_time.h boostutil.h
stdexcepthdlr.obj:	yukon.h precompiled.h ctidbgmem.h stdexcepthdlr.h \
		dlldefs.h
test_cmdparse.obj:	test_cmdparse_input.h test_cmdparse_output.h \
		cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h
test_ctidate.obj:	ctidate.h dlldefs.h logger.h thread.h mutex.h \
		guard.h numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h \
		sorted_vector.h
test_ctipcptrqueue.obj:	ctipcptrqueue.h mutex.h dlldefs.h guard.h \
		numstr.h clrdump.h
test_ctistring.obj:	yukon.h precompiled.h ctidbgmem.h ctistring.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h
test_ctitime.obj:	ctitime.h dlldefs.h ctidate.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h
test_fifo_multiset.obj:	fifo_multiset.h
test_hash.obj:	hashkey.h hash_functions.h dlldefs.h
test_logger.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h
test_numstr.obj:	yukon.h precompiled.h ctidbgmem.h numstr.h dlldefs.h
test_queue.obj:	queue.h dlldefs.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h
test_resolvers.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		resolvers.h types.h pointtypes.h db_entry_defines.h \
		devicetypes.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h os2_2w32.h sorted_vector.h
test_rwutil.obj:	rwutil.h yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h boost_time.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h sorted_vector.h \
		ctidate.h logger.h thread.h mutex.h guard.h clrdump.h \
		CtiPCPtrQueue.h
test_utility.obj:	yukon.h precompiled.h ctidbgmem.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h dsm2.h mutex.h guard.h clrdump.h \
		cticonnect.h netports.h
test_xmlobject.obj:	yukon.h precompiled.h ctidbgmem.h xml_object.h \
		dlldefs.h
tfexec.obj:	yukon.h precompiled.h ctidbgmem.h tfexec.h
thread.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h thread.h
thread_listener.obj:	yukon.h precompiled.h ctidbgmem.h \
		thread_listener.h thread.h mutex.h dlldefs.h guard.h numstr.h \
		clrdump.h
thread_monitor.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h thread_monitor.h smartmap.h boostutil.h \
		readers_writer_lock.h critical_section.h queue.h \
		thread_register_data.h boost_time.h
thread_register_data.obj:	yukon.h precompiled.h ctidbgmem.h \
		thread_register_data.h boost_time.h cticalls.h os2_2w32.h \
		dlldefs.h types.h boostutil.h utility.h ctitime.h queues.h \
		numstr.h sorted_vector.h
thread_timer.obj:	yukon.h precompiled.h ctidbgmem.h thread_timer.h \
		thread.h mutex.h dlldefs.h guard.h numstr.h clrdump.h \
		thread_register_data.h boost_time.h cticalls.h os2_2w32.h \
		types.h boostutil.h utility.h ctitime.h queues.h \
		sorted_vector.h
ucttime.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h elogger.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
utility.obj:	yukon.h precompiled.h ctidbgmem.h ctinexus.h dlldefs.h \
		netports.h cticonnect.h dbaccess.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		sema.h porter.h dsm2err.h devicetypes.h queues.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h sorted_vector.h \
		pointdefs.h rwutil.h boost_time.h boostutil.h ctidate.h
verification_objects.obj:	yukon.h precompiled.h ctidbgmem.h \
		verification_objects.h dlldefs.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h boost_time.h
wordbuilder.obj:	yukon.h precompiled.h ctidbgmem.h wordbuilder.h
words.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cti_asmc.h queues.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dsm2err.h porter.h \
		devicetypes.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h sorted_vector.h
xfer.obj:	yukon.h precompiled.h ctidbgmem.h xfer.h dsm2.h mutex.h \
		dlldefs.h guard.h numstr.h clrdump.h cticonnect.h netports.h
xml_object.obj:	yukon.h precompiled.h ctidbgmem.h xml_object.h \
		dlldefs.h
#ENDUPDATE#
