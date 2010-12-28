# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(RW) \
-I$(BOOST) \
-I$(SQLAPI)\include \
-I$(FDR)\Telegyr\inc \
-I$(FDR)\OSIPI\inc \
-I$(FDR)\LiveData \
-I$(PROT)\include


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
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

FDRTESTOBJS=\
test_fdrDnpSlave.obj \
test_fdrTextImport.obj \
test_fdrTristateSub.obj \
test_fdrTelegyr.obj

CTIFDRLIBS=\
$(COMPILEBASE)\lib\cticparms.lib \
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
$(BIN)\fdrbepc.lib \
$(BIN)\fdrtextexport.lib \
$(BIN)\fdrtextimport.lib \
$(BIN)\fdrdsm2import.lib \
$(BIN)\fdrdsm2filein.lib \
$(BIN)\fdrrdex.lib \
$(BIN)\fdrcygnet.lib \
$(BIN)\fdracs.lib \
$(BIN)\fdracsmulti.lib \
$(BIN)\fdrvalmet.lib \
$(BIN)\fdrinet.lib \
$(BIN)\fdrstec.lib \
$(BIN)\fdrtristate.lib \
$(BIN)\fdrrccs.lib \
$(BIN)\fdrlodestarimport_enh.lib \
$(BIN)\fdrlodestarimport_std.lib \
$(BIN)\fdrtelegyr.lib \
$(BIN)\fdrxa21lm.lib \
$(BIN)\fdrlivedata.lib \
$(BIN)\fdrwabash.lib \
$(BIN)\fdrtristatesub.lib \
$(BIN)\cti_fdr.lib \
$(BIN)\fdrdnpslave.lib \


ALL:      fdrtest

fdrtest:  $(FDRTESTOBJS) makeexe.mak

copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
        scandeps -Output maketest.mak test_*.cpp


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
	$(CC) $(CFLAGS) $(INCLPATHS) $(PCHFLAGS) $(RWCPPFLAGS) $(RWLINKFLAGS)  /Fe$(BIN)\$(@B).exe \
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(CTIFDRDLL) $(FDRTELEGYRLIBS) $(BOOST_TEST_LIBS) $(RWLIBS) $(CTIFDRLIBS) $(FDRLIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)

	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1

	#@set  # this would run the shell command-line instruction "set" showing the environment variabless.

	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################

#UPDATE#
test_fdrdnpslave.obj:	boost_test_helpers.h fdrdnpslave.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h fdrpointlist.h \
		mgr_fdrpoint.h smartmap.h boostutil.h utility.h ctitime.h \
		numstr.h sorted_vector.h dllbase.h dsm2.h mutex.h guard.h \
		cticonnect.h yukon.h precompiled.h ctidbgmem.h netports.h \
		dsm2err.h words.h readers_writer_lock.h critical_section.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		rwutil.h database_connection.h dbaccess.h sema.h \
		database_reader.h row_reader.h boost_time.h logger.h thread.h \
		CtiPCPtrQueue.h fdrscadaserver.h fdrsocketserver.h \
		fdrclientserverconnection.h serverconnection.h fdrinterface.h \
		message.h collectable.h msg_dbchg.h connection.h exchange.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h fdrdebuglevel.h \
		fdrdnphelper.h dnp_object_analoginput.h dnp_objects.h \
		prot_base.h xfer.h dnp_object_time.h prot_dnp.h \
		packet_finder.h dnp_application.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h
test_fdrtelegyr.obj:	fdrtelegyr.h dlldefs.h fdrinterface.h message.h \
		ctitime.h ctidbgmem.h collectable.h rwutil.h yukon.h \
		precompiled.h types.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h sorted_vector.h cticonnect.h \
		netports.h dsm2err.h words.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_dbchg.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_fdrpoint.h smartmap.h readers_writer_lock.h \
		critical_section.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrasciiimportbase.h \
		telegyrgroup.h telegyrcontrolcenter.h rtdb.h hashkey.h \
		hash_functions.h
test_fdrtextimport.obj:	fdrtextimport.h dlldefs.h fdrtextfilebase.h \
		fdrinterface.h message.h ctitime.h ctidbgmem.h collectable.h \
		rwutil.h yukon.h precompiled.h types.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_dbchg.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cparms.h \
		configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		TextFileInterfaceParts.h ctistring.h ctidate.h
test_fdrtristatesub.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctitime.h dlldefs.h ctidate.h logger.h thread.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		sorted_vector.h CtiPCPtrQueue.h ctistring.h fdrTriStateSub.h \
		fdrftpinterface.h fdrinterface.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_dbchg.h connection.h exchange.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_fdrpoint.h smartmap.h \
		readers_writer_lock.h critical_section.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
#ENDUPDATE#
