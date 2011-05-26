# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(MACS)\include \
-I$(COMMON)\include \
-I$(MCCMD)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(MSG)\include \
-I$(INTERP)\include \
-I$(TCL)\include \
-I$(RW) \
-I$(BOOST) \
-I$(SQLAPI)\include \

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(MACS)\include \
;$(MCCMD)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROTOCOL)\include \
;$(RTDB)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(DATABASE)\include \
;$(RW)

LIBS=\
advapi32.lib \
$(TCL_LIBS) \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\mccmd.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\interp.lib


MACSTESTOBJS= \
test_scheduletime.obj

MACSBASEOBJS= \
$(OBJ)/clientconn.obj \
$(OBJ)/clistener.obj \
$(OBJ)/mc_dbthr.obj \
$(OBJ)/mc_fileint.obj \
$(OBJ)/mc_msg.obj \
$(OBJ)/mc_sched.obj \
$(OBJ)/mc_scheduler.obj \
$(OBJ)/mc_server.obj \
$(OBJ)/mc_script.obj \
$(OBJ)/mgr_mcsched.obj \
$(OBJ)/tbl_mcsched.obj \
$(OBJ)/tbl_mcsimpsched.obj

ALL:      macs

macs:      $(MACSTESTOBJS) maketest.mak

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
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(MACSBASEOBJS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################
#UPDATE#
test_scheduletime.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctitime.h dlldefs.h mc_scheduler.h mc.h logger.h thread.h \
		mutex.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		numstr.h CtiPCPtrQueue.h mc_sched.h row_reader.h \
		dbmemobject.h tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h boost_time.h \
		boostutil.h mgr_mcsched.h rtdb.h hashkey.h hash_functions.h \
		string_utility.h mgr_holiday.h ctidate.h
#ENDUPDATE#

