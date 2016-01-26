# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc

#
# ALWAYS place the local drives/direcories before the reference ones.
#
INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(DISPATCH)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include

.PATH.cpp = .;$(R_SERVER)

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



SVRLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \


SERVEROBJS=\
$(PRECOMPILED_OBJ) \
con_mgr.obj \
exe_cmd.obj \
exe_reg.obj \
executor.obj \
executorfactory.obj \
server_b.obj


CTIPROGS=\
ctisvr.dll


SERVER_FULLBUILD = $[Filename,$(OBJ),ServerFullBuild,target]


PROGS_VERSION=\
$(CTIPROGS)


ALL:            $(CTIPROGS)


$(SERVER_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	@echo:
	$(CC) $(CCOPTS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) /DCTISVR $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(SERVEROBJS)]


ctisvr.dll:     $(SERVER_FULLBUILD) $(SERVEROBJS) makesvr.mak $(OBJ)\ctisvr.res
                @build -nologo -f $(_InputFile) id
                @echo Building  $@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(SERVEROBJS) $(INCLPATHS) $(BOOST_LIBS) $(SVRLIBS) /Fe..\$@ ctisvr.res
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
		-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
		-if exist ..\bin\$(@B).pdb copy ..\bin\$(@B).pdb $(YUKONDEBUG)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\ctisvr.dll copy bin\ctisvr.dll $(YUKONOUTPUT)
		-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
                -if exist bin\ctisvr.pdb copy bin\ctisvr.pdb $(YUKONDEBUG)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist bin\ctisvr.lib copy bin\ctisvr.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp


deps:
                scandeps -Output makesvr.mak *.cpp

# The lines below accomplish the ID'ing of the project!
id:
#            @cid .\include\id_svr.h
#            @build -nologo -f $(_InputFile) id_svr.obj
#
#id_svr.obj:    id_svr.cpp include\id_svr.h


.cpp.obj :
                @echo:
                @echo Compiling: $< Output: $@
                @echo:
                $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) /DCTISVR $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

#######
#UPDATE#
con_mgr.obj:	precompiled.h dlldefs.h collectable.h con_mgr.h \
		connection_server.h connection.h message.h ctitime.h \
		ctidbgmem.h loggable.h msg_multi.h msg_pdata.h yukon.h \
		types.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h connection_listener.h dllbase.h \
		msg_server_resp.h msg_cmd.h
ctique.obj:	precompiled.h
dlldbmemmgr.obj:	precompiled.h dlldefs.h module_util.h ctitime.h \
		version.h
executor.obj:	precompiled.h executor.h dlldefs.h yukon.h types.h \
		ctidbgmem.h con_mgr.h connection_server.h connection.h \
		message.h ctitime.h collectable.h loggable.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h connection_listener.h dllbase.h
executorfactory.obj:	precompiled.h executorfactory.h dlldefs.h \
		message.h ctitime.h ctidbgmem.h collectable.h loggable.h \
		exe_cmd.h executor.h yukon.h types.h exe_reg.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h
exe_cmd.obj:	precompiled.h dlldefs.h con_mgr.h connection_server.h \
		connection.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h msg_multi.h msg_pdata.h yukon.h types.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		connection_listener.h dllbase.h server_b.h smartmap.h \
		msg_cmd.h exe_cmd.h executor.h
exe_reg.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h loggable.h exe_reg.h executor.h yukon.h types.h \
		con_mgr.h connection_server.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h connection_listener.h dllbase.h \
		con_mgr_vg.h vgexe_factory.h executorfactory.h exe_cmd.h \
		msg_cmd.h server_b.h smartmap.h
id_svr.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h module_util.h version.h id_svr.h
server_b.obj:	precompiled.h server_b.h con_mgr.h connection_server.h \
		connection.h dlldefs.h message.h ctitime.h ctidbgmem.h \
		collectable.h loggable.h msg_multi.h msg_pdata.h yukon.h \
		types.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h logger.h streamBuffer.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h connection_listener.h dllbase.h \
		smartmap.h msg_cmd.h id_svr.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
