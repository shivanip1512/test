include global.inc
include rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(RW) \
-I$(BOOST) \
-I$(R_COMMON)\include \
-I$(R_CPARMS)\include \

.PATH.cpp = .;$(R_COMMON)

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(RW) \
;$(BOOST) \
;$(R_COMMON)\include \
;$(CPARMS)\include \
;$(R_CPARMS)\include \


CMDOBJS=\
cmdopts.obj \
argval.obj \
argkey.obj

CTIPROGS=\
cmdline.dll

WINLIBS=kernel32.lib user32.lib wsock32.lib


ALL:            $(CTIPROGS)


cmdline.dll:    $(CMDOBJS) Makefile
                @echo:
                @echo Compiling $@
                %cd $(OBJ)
                $(CC) $(CMDOBJS) $(DLLFLAGS) $(RWLIBS) $(BOOSTLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                %cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy bin\cmdline.dll $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\cmdline.dll.lib copy bin\cmdline.dll.lib $(COMPILEBASE)\lib


clean:
        -del $(OBJ)\*.obj *.pch *.pdb *.sdb *.adb *.ilk $(BIN)\*.exe

allclean:   clean all

deps:
                scandeps -Output makecmdline.mak *.cpp



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling $< to $(@B).obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) /D_DLL_CMDLINE $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
alarmtst.obj:	os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dsm2err.h elogger.h device.h devicetypes.h alarmlog.h
argkey.obj:	argkey.h
argval.obj:	argval.h
bfexec.obj:	bfexec.h
cmdopts.obj:	dlldefs.h argkey.h argval.h cmdopts.h yukon.h ctidbgmem.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h
cmdparse.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h cmdparse.h \
		parsevalue.h cparms.h devicetypes.h logger.h thread.h \
		numstr.h pointdefs.h utility.h
counter.obj:	counter.h guard.h dlldefs.h mutex.h
critical_section.obj:	critical_section.h dlldefs.h
ctdpcptrq.obj:	ctdpcptrq.h dlldefs.h
cticalls.obj:	os2_2w32.h dlldefs.h types.h cticalls.h dllbase.h dsm2.h \
		mutex.h guard.h logger.h thread.h
ctinexus.obj:	os2_2w32.h dlldefs.h types.h ctinexus.h logger.h \
		thread.h mutex.h guard.h yukon.h ctidbgmem.h dllbase.h \
		cticalls.h dsm2.h
cti_asmc.obj:	cticalls.h os2_2w32.h dlldefs.h types.h cti_asmc.h
c_port_interface.obj:	dlldefs.h os2_2w32.h types.h dsm2.h mutex.h \
		guard.h queues.h porter.h dsm2err.h devicetypes.h \
		c_port_interface.h group.h elogger.h alarmlog.h
dbaccess.obj:	ctidbgmem.h types.h dlldefs.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h sema.h logger.h \
		thread.h
desolvers.obj:	desolvers.h db_entry_defines.h yukon.h ctidbgmem.h \
		dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h pointtypes.h resolvers.h devicetypes.h \
		logger.h thread.h
dllbase.obj:	dsm2.h mutex.h dlldefs.h guard.h yukon.h ctidbgmem.h \
		dllbase.h os2_2w32.h types.h cticalls.h configparms.h \
		cparms.h dbaccess.h sema.h ctinexus.h logger.h thread.h \
		utility.h thread_monitor.h queue.h thread_register_data.h \
		boost_time.h
drpint.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h drp.h
elog_cli.obj:	os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h elogger.h logger.h thread.h
error.obj:	os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h dsm2err.h dllbase.h logger.h thread.h yukon.h \
		ctidbgmem.h
exchange.obj:	exchange.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h
fileint.obj:	fileint.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h
guard.obj:	guard.h dlldefs.h
id_ctibase.obj:	utility.h dsm2.h mutex.h dlldefs.h guard.h \
		id_ctibase.h id_build.h id_vinfo.h
ilexprot.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		porter.h ilex.h
logger.obj:	cparms.h dlldefs.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h logger.h thread.h numstr.h
master.obj:	os2_2w32.h dlldefs.h types.h cticalls.h cti_asmc.h \
		queues.h dsm2.h mutex.h guard.h dsm2err.h master.h
mutex.obj:	mutex.h dlldefs.h
numstr.obj:	numstr.h dlldefs.h
observe.obj:	observe.h types.h dlldefs.h
pending_stat_operation.obj:	ctidbgmem.h logger.h thread.h mutex.h \
		dlldefs.h guard.h pending_stat_operation.h ctitypes.h dsm2.h
perform.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dsm2err.h device.h devicetypes.h routes.h \
		drp.h elogger.h alarmlog.h porter.h perform.h
pexec.obj:	os2_2w32.h dlldefs.h types.h cticalls.h ctinexus.h queues.h \
		dsm2.h mutex.h guard.h dsm2err.h dllbase.h routes.h porter.h \
		devicetypes.h master.h lm_auto.h perform.h dialup.h \
		c_port_interface.h group.h elogger.h alarmlog.h logger.h \
		thread.h
point_change.obj:	point_change.h yukon.h ctidbgmem.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h
portsup.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dsm2.h \
		mutex.h guard.h dllbase.h color.h dupreq.h devicetypes.h \
		logger.h thread.h
psup.obj:	os2_2w32.h dlldefs.h types.h cticalls.h queues.h dllbase.h \
		dsm2.h mutex.h guard.h dsm2err.h drp.h device.h devicetypes.h \
		elogger.h alarmlog.h lm_auto.h group.h routes.h porter.h \
		master.h logger.h thread.h c_port_interface.h
queent.obj:	queent.h dlldefs.h
quetest.obj:	queue.h dlldefs.h logger.h thread.h mutex.h guard.h
queue.obj:	queue.h dlldefs.h logger.h thread.h mutex.h guard.h
queues.obj:	os2_2w32.h dlldefs.h types.h cticalls.h logger.h thread.h \
		mutex.h guard.h queues.h dllbase.h dsm2.h
queues_test.obj:	os2_2w32.h dlldefs.h types.h cticalls.h logger.h \
		thread.h mutex.h guard.h numstr.h queues.h dllbase.h dsm2.h
repeaterrole.obj:	repeaterrole.h dlldefs.h
resolvers.obj:	dsm2.h mutex.h dlldefs.h guard.h resolvers.h types.h \
		pointtypes.h yukon.h ctidbgmem.h dllbase.h os2_2w32.h \
		cticalls.h db_entry_defines.h devicetypes.h logger.h thread.h \
		numstr.h
rtdb.obj:	dlldefs.h rtdb.h hashkey.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h
rwutil.obj:	rwutil.h boost_time.h dlldefs.h
sema.obj:	sema.h dlldefs.h
statistics.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h dsm2err.h logger.h \
		thread.h statistics.h counter.h yukon.h ctidbgmem.h
stdexcepthdlr.obj:	stdexcepthdlr.h dlldefs.h
tfexec.obj:	tfexec.h
thread.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h \
		mutex.h guard.h thread.h
thread_listener.obj:	thread_listener.h thread.h mutex.h dlldefs.h \
		guard.h
thread_monitor.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h \
		dsm2.h mutex.h guard.h logger.h thread.h thread_monitor.h \
		queue.h thread_register_data.h boost_time.h
thread_register_data.obj:	thread_register_data.h boost_time.h \
		cticalls.h os2_2w32.h dlldefs.h types.h
thread_timer.obj:	thread_timer.h thread.h mutex.h dlldefs.h guard.h \
		thread_register_data.h boost_time.h cticalls.h os2_2w32.h \
		types.h
ucttime.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h \
		mutex.h guard.h elogger.h logger.h thread.h
utility.obj:	ctinexus.h dlldefs.h dbaccess.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h porter.h \
		dsm2err.h devicetypes.h queues.h logger.h thread.h numstr.h \
		pointdefs.h utility.h yukon.h ctidbgmem.h
verification_objects.obj:	verification_objects.h dlldefs.h dsm2.h \
		mutex.h guard.h boost_time.h ctidbgmem.h
wordbuilder.obj:	wordbuilder.h
words.obj:	os2_2w32.h dlldefs.h types.h cticalls.h cti_asmc.h queues.h \
		dsm2.h mutex.h guard.h dsm2err.h device.h devicetypes.h \
		routes.h porter.h logger.h thread.h
xfer.obj:	xfer.h dsm2.h mutex.h dlldefs.h guard.h dialup.h yukon.h \
		ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h
#ENDUPDATE#
