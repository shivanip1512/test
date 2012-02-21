# nmake file YUKON 1.0

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc

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


MACS_TEST_OBJS= \
test_main.obj \
test_scheduletime.obj

MACSBASEOBJS= \
clientconn.obj \
clistener.obj \
mc_dbthr.obj \
mc_fileint.obj \
mc_msg.obj \
mc_sched.obj \
mc_scheduler.obj \
mc_server.obj \
mc_script.obj \
mgr_mcsched.obj \
tbl_mcsched.obj \
tbl_mcsimpsched.obj

MACS_TEST_FULLBUILD = $[Filename,$(OBJ),MacsTestFullBuild,target]


ALL:            test_macs.exe

$(MACS_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(MACS_TEST_OBJS)]

test_macs.exe:    $(MACS_TEST_FULLBUILD) $(MACS_TEST_OBJS)  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(_TargetF) \
        $(MACS_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(MACSBASEOBJS) $(RWLIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(_TargetF).manifest -outputresource:$(BIN)\$(_TargetF);1
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
        @%cd $(CWD)
        @echo.

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

######################################################################################
#UPDATE#
test_scheduletime.obj:	precompiled.h ctitime.h dlldefs.h \
		mc_scheduler.h mc.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h mc_sched.h row_reader.h dbmemobject.h \
		tbl_pao.h tbl_mcsched.h tbl_mcsimpsched.h message.h \
		ctidbgmem.h collectable.h rwutil.h yukon.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h boost_time.h boostutil.h mgr_mcsched.h \
		rtdb.h hashkey.h hash_functions.h string_utility.h \
		mgr_holiday.h ctidate.h
#ENDUPDATE#

