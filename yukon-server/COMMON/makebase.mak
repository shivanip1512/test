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
sema.obj \
statistics.obj \
stdexcepthdlr.obj \
thread.obj \
ucttime.obj \
utility.obj \
words.obj \
xfer.obj \


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
                $(CC) $(BASEOBJS) id_ctibase.obj $(WINLIBS) $(DLLFLAGS) $(RWLIBS) $(COMPILEBASE)\lib\cticparms.lib /Fe..\$@
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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(DLLBUILDNAME) $(INCLPATHS) -Fo$(OBJ)\ -c $<

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
		utility.h
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
repeaterrole.obj:	repeaterrole.h dlldefs.h
resolvers.obj:	dsm2.h mutex.h dlldefs.h guard.h resolvers.h types.h \
		pointtypes.h yukon.h ctidbgmem.h dllbase.h os2_2w32.h \
		cticalls.h db_entry_defines.h devicetypes.h logger.h thread.h \
		numstr.h
rtdb.obj:	dlldefs.h rtdb.h hashkey.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h
sema.obj:	sema.h dlldefs.h
statistics.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h dsm2err.h logger.h \
		thread.h statistics.h counter.h yukon.h ctidbgmem.h
stdexcepthdlr.obj:	stdexcepthdlr.h dlldefs.h
tfexec.obj:	tfexec.h
thread.obj:	thread.h mutex.h dlldefs.h guard.h
ucttime.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h \
		mutex.h guard.h elogger.h logger.h thread.h
utility.obj:	ctinexus.h dlldefs.h dbaccess.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h sema.h porter.h \
		dsm2err.h devicetypes.h queues.h logger.h thread.h numstr.h \
		pointdefs.h utility.h yukon.h ctidbgmem.h
wordbuilder.obj:	wordbuilder.h
words.obj:	os2_2w32.h dlldefs.h types.h cticalls.h cti_asmc.h queues.h \
		dsm2.h mutex.h guard.h dsm2err.h device.h devicetypes.h \
		routes.h porter.h logger.h thread.h
xfer.obj:	xfer.h dsm2.h mutex.h dlldefs.h guard.h dialup.h yukon.h \
		ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h
#ENDUPDATE#
