#Build name MUST BE FIRST!!!!

DLLBUILDNAME = -DCTIBASE

.include global.inc
.include rwglobal.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(BOOST) \
-I$(RW) \

.PATH.cpp = .;$(R_COMMON)

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
;$(BOOST) \
;$(RW) \



CMDOBJS=\
cmdopts.obj \
argval.obj \
argkey.obj


BASEOBJS=\
bfexec.obj \
c_port_interface.obj \
cmdparse.obj \
counter.obj \
critical_section.obj \
ctdpcptrq.obj \
cti_asmc.obj \
cticalls.obj \
ctinexus.obj \
dbaccess.obj \
desolvers.obj \
dllbase.obj \
exchange.obj \
elog_cli.obj \
error.obj \
fileint.obj \
guard.obj \
ilexprot.obj \
logger.obj \
master.obj \
mutex.obj \
numstr.obj \
observe.obj \
pending_stat_operation.obj \
pexec.obj \
psup.obj \
queue.obj \
queues.obj \
repeaterrole.obj \
resolvers.obj \
rtdb.obj \
rwutil.obj \
sema.obj \
statistics.obj \
stdexcepthdlr.obj \
thread.obj \
ucttime.obj \
utility.obj \
words.obj \
xfer.obj \
verification_objects.obj \
thread_register_data.obj \
thread_monitor.obj \


# portsup.obj \
# queent.obj \
# drpint.obj \
# perform.obj \


CTIPROGS=\
ctibase.dll

# mailtest.exe

WINLIBS=kernel32.lib user32.lib advapi32.lib wsock32.lib


ALL:            $(CTIPROGS)


ctibase.dll:    $(BASEOBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @%cd $(OBJ)
                $(CC) $(BASEOBJS) id_ctibase.obj $(WINLIBS) $(DLLFLAGS) $(RWLIBS) $(BOOSTLIBS) $(COMPILEBASE)\lib\cticparms.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

mailtest.exe:   mailtest.cpp
                cl.exe /W3 /GA  /GX /DNDEBUG $(INCLPATHS) -Fo$(OBJ)\ /c mailtest.cpp
                link.exe kernel32.lib user32.lib bin\ctibase.lib /subsystem:console /machine:I386 /out:mailtest.exe $(OBJ)\mailtest.obj
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy $@ $(YUKONOUTPUT)


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
$(OBJ)\*.obj \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.exe


allclean:   clean all


# The lines below accomplish the ID'ing of the project!
id:
            @cid .\include\id_ctibase.h id_vinfo.h
            @$(MAKE) -nologo -f $(_InputFile) id_ctibase.obj

id_ctibase.obj:    id_ctibase.cpp include\id_ctibase.h id_vinfo.h



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
alarmtst.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dsm2err.h elogger.h \
		device.h devicetypes.h alarmlog.h
argkey.obj:	yukon.h precompiled.h ctidbgmem.h argkey.h
argval.obj:	yukon.h precompiled.h ctidbgmem.h argval.h
bfexec.obj:	yukon.h precompiled.h ctidbgmem.h bfexec.h
cmdopts.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h argkey.h \
		argval.h cmdopts.h
cmdparse.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h dlldefs.h \
		parsevalue.h cparms.h devicetypes.h logger.h thread.h mutex.h \
		guard.h numstr.h pointdefs.h utility.h dsm2.h
counter.obj:	yukon.h precompiled.h ctidbgmem.h counter.h guard.h \
		dlldefs.h mutex.h
critical_section.obj:	yukon.h precompiled.h ctidbgmem.h \
		critical_section.h dlldefs.h
ctdpcptrq.obj:	yukon.h precompiled.h ctidbgmem.h ctdpcptrq.h dlldefs.h
cticalls.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dllbase.h dsm2.h mutex.h guard.h logger.h \
		thread.h
ctinexus.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h ctinexus.h logger.h thread.h mutex.h guard.h \
		cticalls.h dsm2.h
cti_asmc.obj:	yukon.h precompiled.h ctidbgmem.h cticalls.h os2_2w32.h \
		dlldefs.h types.h cti_asmc.h
c_port_interface.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h \
		os2_2w32.h types.h dsm2.h mutex.h guard.h queues.h porter.h \
		dsm2err.h devicetypes.h c_port_interface.h group.h elogger.h \
		alarmlog.h
dbaccess.obj:	yukon.h precompiled.h ctidbgmem.h types.h dlldefs.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h logger.h thread.h
desolvers.obj:	yukon.h precompiled.h ctidbgmem.h desolvers.h \
		db_entry_defines.h dlldefs.h types.h pointtypes.h dsm2.h \
		mutex.h guard.h resolvers.h devicetypes.h logger.h thread.h
dllbase.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h os2_2w32.h types.h cticalls.h configparms.h \
		cparms.h dbaccess.h dllbase.h sema.h ctinexus.h logger.h \
		thread.h utility.h thread_monitor.h queue.h \
		thread_register_data.h boost_time.h
drpint.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h drp.h
elog_cli.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dsm2.h mutex.h guard.h elogger.h logger.h \
		thread.h
error.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dsm2err.h dllbase.h \
		logger.h thread.h
exchange.obj:	yukon.h precompiled.h ctidbgmem.h exchange.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h
fileint.obj:	yukon.h precompiled.h ctidbgmem.h fileint.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h
guard.obj:	yukon.h precompiled.h ctidbgmem.h guard.h dlldefs.h
id_ctibase.obj:	yukon.h precompiled.h ctidbgmem.h utility.h dsm2.h \
		mutex.h dlldefs.h guard.h id_ctibase.h id_build.h id_vinfo.h
ilexprot.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h porter.h ilex.h
logger.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h numstr.h
master.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cti_asmc.h queues.h dsm2.h mutex.h guard.h \
		dsm2err.h master.h
mutex.obj:	yukon.h precompiled.h ctidbgmem.h mutex.h dlldefs.h
numstr.obj:	yukon.h precompiled.h ctidbgmem.h numstr.h dlldefs.h
observe.obj:	yukon.h precompiled.h ctidbgmem.h observe.h types.h \
		dlldefs.h
parse.obj:	yukon.h precompiled.h ctidbgmem.h
pending_stat_operation.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		thread.h mutex.h dlldefs.h guard.h pending_stat_operation.h \
		ctitypes.h dsm2.h
perform.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dsm2err.h \
		device.h devicetypes.h routes.h drp.h elogger.h alarmlog.h \
		porter.h perform.h
pexec.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h ctinexus.h queues.h dsm2.h mutex.h guard.h \
		dsm2err.h dllbase.h routes.h porter.h devicetypes.h master.h \
		lm_auto.h perform.h dialup.h c_port_interface.h group.h \
		elogger.h alarmlog.h logger.h thread.h
point_change.obj:	yukon.h precompiled.h ctidbgmem.h point_change.h \
		dlldefs.h
portsup.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dsm2.h mutex.h guard.h dllbase.h \
		color.h dupreq.h devicetypes.h logger.h thread.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
psup.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h queues.h dllbase.h dsm2.h mutex.h guard.h \
		dsm2err.h drp.h device.h devicetypes.h elogger.h alarmlog.h \
		lm_auto.h group.h routes.h porter.h master.h logger.h \
		thread.h c_port_interface.h
queent.obj:	yukon.h precompiled.h ctidbgmem.h queent.h dlldefs.h
quetest.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h
queue.obj:	yukon.h precompiled.h ctidbgmem.h queue.h dlldefs.h \
		logger.h thread.h mutex.h guard.h
queues.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h logger.h thread.h mutex.h guard.h queues.h \
		dllbase.h dsm2.h
repeaterrole.obj:	yukon.h precompiled.h ctidbgmem.h repeaterrole.h \
		dlldefs.h
resolvers.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h mutex.h \
		dlldefs.h guard.h resolvers.h types.h pointtypes.h \
		db_entry_defines.h devicetypes.h logger.h thread.h numstr.h
rtdb.obj:	yukon.h precompiled.h ctidbgmem.h dlldefs.h rtdb.h hashkey.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h
rwutil.obj:	yukon.h precompiled.h ctidbgmem.h rwutil.h boost_time.h \
		dlldefs.h
sema.obj:	yukon.h precompiled.h ctidbgmem.h sema.h dlldefs.h
statistics.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h dsm2err.h logger.h thread.h statistics.h \
		counter.h
stdexcepthdlr.obj:	yukon.h precompiled.h ctidbgmem.h stdexcepthdlr.h \
		dlldefs.h
tfexec.obj:	yukon.h precompiled.h ctidbgmem.h tfexec.h
thread.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h thread.h
thread_listener.obj:	yukon.h precompiled.h ctidbgmem.h \
		thread_listener.h thread.h mutex.h dlldefs.h guard.h
thread_monitor.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h thread_monitor.h queue.h \
		thread_register_data.h boost_time.h
thread_register_data.obj:	yukon.h precompiled.h ctidbgmem.h \
		thread_register_data.h boost_time.h cticalls.h os2_2w32.h \
		dlldefs.h types.h
thread_timer.obj:	yukon.h precompiled.h ctidbgmem.h thread_timer.h \
		thread.h mutex.h dlldefs.h guard.h thread_register_data.h \
		boost_time.h cticalls.h os2_2w32.h types.h
ucttime.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h elogger.h \
		logger.h thread.h
utility.obj:	yukon.h precompiled.h ctidbgmem.h ctinexus.h dlldefs.h \
		dbaccess.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h sema.h porter.h dsm2err.h devicetypes.h \
		queues.h logger.h thread.h numstr.h pointdefs.h utility.h
verification_objects.obj:	yukon.h precompiled.h ctidbgmem.h \
		verification_objects.h dlldefs.h dsm2.h mutex.h guard.h \
		boost_time.h
wordbuilder.obj:	yukon.h precompiled.h ctidbgmem.h wordbuilder.h
words.obj:	yukon.h precompiled.h ctidbgmem.h os2_2w32.h dlldefs.h \
		types.h cticalls.h cti_asmc.h queues.h dsm2.h mutex.h guard.h \
		dsm2err.h device.h devicetypes.h routes.h porter.h logger.h \
		thread.h
xfer.obj:	yukon.h precompiled.h ctidbgmem.h xfer.h dsm2.h mutex.h \
		dlldefs.h guard.h dialup.h
#ENDUPDATE#
