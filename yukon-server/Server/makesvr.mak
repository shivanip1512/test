# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

#
# ALWAYS place the local drives/direcories before the reference ones.
#
INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(DISPATCH)\include \
-I$(MSG)\include \
-I$(RW) \
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
;$(RW)



SVRLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \


SERVEROBJS=\
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
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) /DCTISVR $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(SERVEROBJS)]


ctisvr.dll:     $(SERVER_FULLBUILD) $(SERVEROBJS) makesvr.mak $(OBJ)\ctisvr.res
                @build -nologo -f $(_InputFile) id
                @echo Building  $@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(SERVEROBJS) $(INCLPATHS) $(RWLIBS) $(BOOST_LIBS) $(SVRLIBS) /Fe..\$@ ctisvr.res
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist bin\ctisvr.dll copy bin\ctisvr.dll $(YUKONOUTPUT)
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
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) /DCTISVR $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

#######
#UPDATE#
con_mgr.obj:	precompiled.h dlldefs.h collectable.h con_mgr.h \
		connection_server.h connection.h message.h ctitime.h \
		ctidbgmem.h msg_multi.h msg_pdata.h yukon.h types.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h msg_server_resp.h msg_cmd.h
ctique.obj:	precompiled.h
dlldbmemmgr.obj:	precompiled.h dlldefs.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h
executor.obj:	precompiled.h executor.h message.h ctitime.h dlldefs.h \
		ctidbgmem.h collectable.h con_mgr.h connection_server.h \
		connection.h msg_multi.h msg_pdata.h yukon.h types.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h
executorfactory.obj:	precompiled.h executorfactory.h collectable.h \
		message.h ctitime.h dlldefs.h ctidbgmem.h executor.h \
		exe_cmd.h exe_reg.h
exe_cmd.obj:	precompiled.h dlldefs.h con_mgr.h connection_server.h \
		connection.h message.h ctitime.h ctidbgmem.h collectable.h \
		msg_multi.h msg_pdata.h yukon.h types.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h server_b.h smartmap.h msg_cmd.h \
		exe_cmd.h executor.h
exe_reg.obj:	precompiled.h message.h ctitime.h dlldefs.h ctidbgmem.h \
		collectable.h exe_reg.h executor.h con_mgr.h \
		connection_server.h connection.h msg_multi.h msg_pdata.h \
		yukon.h types.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h mutex.h queue.h cparms.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h dsm2err.h \
		words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h con_mgr_vg.h vgexe_factory.h \
		exe_ptchg.h executorfactory.h exe_cmd.h msg_cmd.h server_b.h \
		smartmap.h
id_svr.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h constants.h numstr.h id_svr.h
server_b.obj:	precompiled.h server_b.h con_mgr.h connection_server.h \
		connection.h dlldefs.h message.h ctitime.h ctidbgmem.h \
		collectable.h msg_multi.h msg_pdata.h yukon.h types.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h mutex.h \
		queue.h cparms.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h streamConnection.h netports.h timing_util.h \
		immutable.h readers_writer_lock.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		dsm2err.h words.h optional.h macro_offset.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h streamBuffer.h string_util.h exception_helper.h \
		string_utility.h connection_base.h worker_thread.h \
		connection_listener.h ctibase.h streamSocketConnection.h \
		socket_helper.h win_helper.h smartmap.h executor.h msg_cmd.h \
		id_svr.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
