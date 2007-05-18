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
-I$(PROCLOG)\include \
-I$(RW) \
-I$(BOOST) \
-I$(FDR)\Telegyr\inc \
-I$(FDR)\OSIPI\inc \
-I$(FDR)\LiveData


.PATH.cpp = .;$(R_FDR)

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
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(SIGNAL)\include \
;$(TCLINC) \
;$(RW)

FDRTESTOBJS=\
test_fdrTextImport.obj \
test_fdrTristateSub.obj 


CTIFDRLIBS=\
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\proclog.lib \
$(COMPILEBASE)\lib\cmdline.lib \
wininet.lib


FRDLIBS=\
$(COMPILEBASE)\lib\apiclilib.lib \
$(COMPILEBASE)\lib\pllib.lib \
$(COMPILEBASE)\lib\psapi.lib \
$(COMPILEBASE)\Fdr\OSIPI\lib\piapi32.lib \
$(COMPILEBASE)\Fdr\OSIPI\lib\pilog32.lib


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
$(BIN)\fdrtristatesub.lib 


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
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(CTIFDRDLL) $(BOOSTTESTLIBS) $(RWLIBS) $(CTIFDRLIBS) $(FDRLIBS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################

#UPDATE#
test_fdrtextimport.obj:	yukon.h precompiled.h ctidbgmem.h ctidate.h \
		dlldefs.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h ctistring.h rwutil.h \
		boost_time.h fdrtextimport.h fdrtextfilebase.h fdrinterface.h \
		message.h collectable.h connection.h exchange.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h cticonnect.h netports.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h utility.h queues.h \
		sorted_vector.h mgr_fdrpoint.h rtdb.h hashkey.h \
		hash_functions.h fdrpoint.h pointtypes.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h TextFileInterfaceParts.h
test_fdrtristatesub.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h ctistring.h rwutil.h \
		boost_time.h fdrTriStateSub.h fdrftpinterface.h \
		fdrinterface.h message.h collectable.h connection.h \
		exchange.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		cticonnect.h netports.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h utility.h queues.h sorted_vector.h mgr_fdrpoint.h \
		rtdb.h hashkey.h hash_functions.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h
#ENDUPDATE#
