#Build name MUST BE FIRST!!!!

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(RTDB)\include \
-I$(PROT)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(MSG)\include \
-I$(BOOST) \
-I$(SQLAPI)\include \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CPARMS)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)


TESTOBJS=\
test_ccusim.obj \
test_behavior_collection.obj \
test_delay_behavior.obj \
test_bch_behavior.obj \
test_mct410_sim.obj \


CCU_SIMULATOR_BASE_OBJS=\
$(OBJ)\EmetconWords.obj \
$(OBJ)\Mct410.obj \
$(OBJ)\DelayBehavior.obj \
$(OBJ)\BchBehavior.obj \

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib

ALL:            ctibasetest

ctibasetest:    $(TESTOBJS) Makefile

deps:
                scandeps -Output maketest.mak *.cpp

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
                -copy bin\*.exe $(YUKONOUTPUT)

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
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(CCU_SIMULATOR_BASE_OBJS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################
#UPDATE#
bchbehavior.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		BchBehavior.h PlcBehavior.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h CtiPCPtrQueue.h
ccu710.obj:	yukon.h precompiled.h types.h ctidbgmem.h Ccu710.h \
		portlogger.h logger.h dlldefs.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h emetconwords.h PlcTransmitter.h \
		CommInterface.h ctinexus.h netports.h cticonnect.h \
		BehaviorCollection.h CommsBehavior.h Simulator.h \
		PlcInfrastructure.h Mct410.h smartmap.h boostutil.h dllbase.h \
		dsm2.h dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h
ccu711.obj:	yukon.h precompiled.h types.h ctidbgmem.h ccu711.h \
		ccu710.h portlogger.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h CtiPCPtrQueue.h emetconwords.h PlcTransmitter.h \
		CommInterface.h ctinexus.h netports.h cticonnect.h \
		BehaviorCollection.h CommsBehavior.h CcuIDLC.h boostutil.h \
		cti_asmc.h color.h ctidate.h simulator.h PlcInfrastructure.h \
		Mct410.h smartmap.h dllbase.h dsm2.h dsm2err.h words.h \
		optional.h readers_writer_lock.h critical_section.h \
		PlcBehavior.h
ccu721.obj:	yukon.h precompiled.h types.h ctidbgmem.h Ccu721.h \
		CcuIDLC.h PlcTransmitter.h CommInterface.h ctinexus.h \
		dlldefs.h netports.h cticonnect.h BehaviorCollection.h \
		CommsBehavior.h portlogger.h logger.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h CtiPCPtrQueue.h EmetconWords.h ctidate.h simulator.h \
		PlcInfrastructure.h Mct410.h smartmap.h boostutil.h dllbase.h \
		dsm2.h dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h PlcBehavior.h cti_asmc.h
ccuidlc.obj:	Yukon.h precompiled.h types.h ctidbgmem.h CcuIDLC.h \
		PlcTransmitter.h CommInterface.h ctinexus.h dlldefs.h \
		netports.h cticonnect.h BehaviorCollection.h CommsBehavior.h \
		portlogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
ccusimsvc.obj:	yukon.h precompiled.h types.h ctidbgmem.h ctitime.h \
		dlldefs.h ccusimsvc.h cservice.h ctibase.h ctinexus.h \
		netports.h cticonnect.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h queues.h numstr.h dsm2err.h \
		words.h optional.h
ccu_simulator.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PlcInfrastructure.h Mct410.h EmetconWords.h ctitime.h \
		dlldefs.h smartmap.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dllbase.h dsm2.h mutex.h \
		guard.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h \
		BehaviorCollection.h Ccu710.h portlogger.h logger.h thread.h \
		CtiPCPtrQueue.h PlcTransmitter.h CommInterface.h ctinexus.h \
		CommsBehavior.h Ccu711.h CcuIDLC.h Ccu721.h ctidate.h \
		DelayBehavior.h BchBehavior.h cparms.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h configkey.h configval.h
comminterface.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		CommInterface.h ctinexus.h dlldefs.h netports.h cticonnect.h \
		BehaviorCollection.h CommsBehavior.h
delaybehavior.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		DelayBehavior.h CommsBehavior.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h CtiPCPtrQueue.h
emetconwords.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		EmetconWords.h dlldefs.h cti_asmc.h cticalls.h os2_2w32.h
mct410.obj:	yukon.h precompiled.h types.h ctidbgmem.h Mct410.h \
		EmetconWords.h ctitime.h dlldefs.h logger.h thread.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h
plcinfrastructure.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		plcinfrastructure.h Mct410.h EmetconWords.h ctitime.h \
		dlldefs.h smartmap.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dllbase.h dsm2.h mutex.h \
		guard.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		readers_writer_lock.h critical_section.h PlcBehavior.h \
		BehaviorCollection.h
plctransmitter.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		PlcTransmitter.h CommInterface.h ctinexus.h dlldefs.h \
		netports.h cticonnect.h BehaviorCollection.h CommsBehavior.h \
		portlogger.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
portlogger.obj:	yukon.h precompiled.h types.h ctidbgmem.h portlogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
simulator_main.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		ctitime.h dlldefs.h ccusimsvc.h cservice.h CServiceConfig.h \
		ctibase.h ctinexus.h netports.h cticonnect.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h
test_bch_behavior.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h numstr.h BehaviorCollection.h BchBehavior.h \
		PlcBehavior.h logger.h thread.h mutex.h guard.h \
		CtiPCPtrQueue.h
test_behavior_collection.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h numstr.h BehaviorCollection.h
test_ccusim.obj:	ctitime.h dlldefs.h ctidate.h logger.h thread.h \
		mutex.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h CCU711.h ccu710.h \
		portlogger.h emetconwords.h PlcTransmitter.h CommInterface.h \
		ctinexus.h netports.h cticonnect.h yukon.h precompiled.h \
		ctidbgmem.h BehaviorCollection.h CommsBehavior.h CcuIDLC.h \
		Ccu721.h Mct410.h
test_delay_behavior.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h numstr.h BehaviorCollection.h DelayBehavior.h \
		CommsBehavior.h logger.h thread.h mutex.h guard.h \
		CtiPCPtrQueue.h
test_mct410_sim.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h numstr.h Mct410.h EmetconWords.h
#ENDUPDATE#

