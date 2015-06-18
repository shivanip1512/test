# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(FDR)\Telegyr\inc \
-I$(FDR)\OSIPI\inc \
-I$(FDR)\LiveData \
-I$(PROT)\include \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include

FDR_TEST_OBJS=\
$(PRECOMPILED_OBJ) \
test_main.obj \
test_fdrDnpSlave.obj \
test_fdrTextImport.obj \
test_fdrTristateSub.obj \
test_fdrTelegyr.obj \
test_fdrutility.obj


CTIFDRLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\cti_fdr.lib \
wininet.lib


FDRTELEGYRLIBS=\
$(COMPILEBASE)\lib\apiclilib.lib \
$(COMPILEBASE)\lib\pllib.lib \
$(COMPILEBASE)\lib\psapi.lib

CTIFDRDLL=\
..\$(BIN)\fdrbepc.lib \
..\$(BIN)\fdrtextexport.lib \
..\$(BIN)\fdrtextimport.lib \
..\$(BIN)\fdrdsm2import.lib \
..\$(BIN)\fdrdsm2filein.lib \
..\$(BIN)\fdrrdex.lib \
..\$(BIN)\fdrcygnet.lib \
..\$(BIN)\fdracs.lib \
..\$(BIN)\fdracsmulti.lib \
..\$(BIN)\fdrvalmet.lib \
..\$(BIN)\fdrinet.lib \
..\$(BIN)\fdrstec.lib \
..\$(BIN)\fdrtristate.lib \
..\$(BIN)\fdrrccs.lib \
..\$(BIN)\fdrlodestarimport_enh.lib \
..\$(BIN)\fdrlodestarimport_std.lib \
..\$(BIN)\fdrtelegyr.lib \
..\$(BIN)\fdrxa21lm.lib \
..\$(BIN)\fdrlivedata.lib \
..\$(BIN)\fdrwabash.lib \
..\$(BIN)\fdrtristatesub.lib \
..\$(BIN)\cti_fdr.lib \
..\$(BIN)\fdrdnpslave.lib \


FDR_TEST_FULLBUILD = $[Filename,$(OBJ),FdrTestFullBuild,target]


ALL:      test_fdr.exe

$(FDR_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(FDR_TEST_OBJS)]

test_fdr.exe:  $(FDR_TEST_FULLBUILD) $(FDR_TEST_OBJS) Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) /Fe..\$(BIN)\$(_TargetF) \
        $(FDR_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(CTIFDRDLL) $(FDRTELEGYRLIBS) $(CTIFDRLIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
        @%cd $(CWD)
        @echo.


deps:
                scandeps -Output maketest.mak test_*.cpp

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
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################

#UPDATE#
test_fdrdnpslave.obj:	fdrdnpslave.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		fdrpointlist.h mgr_fdrpoint.h smartmap.h boostutil.h \
		utility.h ctitime.h numstr.h dllbase.h critical_section.h \
		readers_writer_lock.h guard.h logger.h streamBuffer.h \
		loggable.h string_util.h exception_helper.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h mutex.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h \
		worker_thread.h timing_util.h fdrinterface.h message.h \
		collectable.h msg_dbchg.h connection_client.h connection.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h connection_base.h \
		fdrdebuglevel.h msg_cmd.h socket_helper.h win_helper.h \
		dnp_object_analoginput.h dnp_objects.h prot_base.h xfer.h \
		dnp_object_time.h prot_dnpSlave.h dnp_application.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h dnp_object_binaryoutput.h desolvers.h \
		boost_test_helpers.h millisecond_timer.h
test_fdrtelegyr.obj:	fdrtelegyr.h dlldefs.h fdrinterface.h message.h \
		ctitime.h ctidbgmem.h collectable.h loggable.h msg_dbchg.h \
		yukon.h types.h connection_client.h connection.h msg_multi.h \
		msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h \
		mutex.h queue.h cparms.h configkey.h configval.h logger.h \
		streamBuffer.h string_util.h exception_helper.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		critical_section.h readers_writer_lock.h guard.h \
		connection_base.h worker_thread.h timing_util.h \
		mgr_fdrpoint.h smartmap.h dllbase.h fdrpoint.h \
		fdrdestination.h fdr.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h fdrdebuglevel.h fdrpointlist.h \
		msg_cmd.h fdrasciiimportbase.h telegyrgroup.h \
		telegyrcontrolcenter.h rtdb.h
test_fdrtextimport.obj:	fdrtextimport.h dlldefs.h fdrtextfilebase.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h msg_dbchg.h yukon.h types.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h configkey.h \
		configval.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h mgr_fdrpoint.h smartmap.h \
		dllbase.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		TextFileInterfaceParts.h
test_fdrtristatesub.obj:	fdrTriStateSub.h dlldefs.h fdrftpinterface.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		loggable.h msg_dbchg.h yukon.h types.h connection_client.h \
		connection.h msg_multi.h msg_pdata.h pointdefs.h pointtypes.h \
		msg_ptreg.h msg_reg.h mutex.h queue.h cparms.h configkey.h \
		configval.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h critical_section.h \
		readers_writer_lock.h guard.h connection_base.h \
		worker_thread.h timing_util.h mgr_fdrpoint.h smartmap.h \
		dllbase.h fdrpoint.h fdrdestination.h fdr.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h fdrdebuglevel.h fdrpointlist.h msg_cmd.h \
		ctidate.h
#ENDUPDATE#
