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
test_mct410_sim.obj \


CCU_SIMULATOR_BASE_OBJS=\
$(OBJ)\EmetconWords.obj \
$(OBJ)\Mct410.obj \
$(OBJ)\DelayBehavior.obj \

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib

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
                mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
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
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\clrdump.lib $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(CCU_SIMULATOR_BASE_OBJS) $(BOOSTTESTLIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################
#UPDATE#
ccu710.obj:	yukon.h precompiled.h ctidbgmem.h Ccu710.h portlogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h emetconwords.h \
		PlcTransmitter.h CommInterface.h ctinexus.h netports.h \
		cticonnect.h BehaviorCollection.h CommsBehavior.h Simulator.h \
		PlcInfrastructure.h Mct410.h smartmap.h boostutil.h dllbase.h \
		dsm2.h dsm2err.h words.h readers_writer_lock.h \
		critical_section.h
ccu711.obj:	yukon.h precompiled.h ctidbgmem.h ccu711.h ccu710.h \
		portlogger.h logger.h dlldefs.h thread.h mutex.h guard.h \
		numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		emetconwords.h PlcTransmitter.h CommInterface.h ctinexus.h \
		netports.h cticonnect.h BehaviorCollection.h CommsBehavior.h \
		fifo_multiset.h boostutil.h cti_asmc.h color.h ctidate.h \
		simulator.h PlcInfrastructure.h Mct410.h smartmap.h dllbase.h \
		dsm2.h dsm2err.h words.h readers_writer_lock.h \
		critical_section.h
ccusimsvc.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ccusimsvc.h cservice.h ctibase.h ctinexus.h netports.h \
		cticonnect.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h dsm2err.h words.h \
		utility.h queues.h sorted_vector.h
ccu_simulator.obj:	yukon.h precompiled.h ctidbgmem.h \
		PlcInfrastructure.h Mct410.h EmetconWords.h types.h ctitime.h \
		dlldefs.h smartmap.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h dllbase.h \
		dsm2.h mutex.h guard.h clrdump.h cticonnect.h netports.h \
		dsm2err.h words.h readers_writer_lock.h critical_section.h \
		Ccu710.h portlogger.h logger.h thread.h CtiPCPtrQueue.h \
		PlcTransmitter.h CommInterface.h ctinexus.h \
		BehaviorCollection.h CommsBehavior.h Ccu711.h fifo_multiset.h \
		DelayBehavior.h cparms.h rwutil.h boost_time.h configkey.h \
		configval.h
comminterface.obj:	yukon.h precompiled.h ctidbgmem.h CommInterface.h \
		types.h ctinexus.h dlldefs.h netports.h cticonnect.h \
		BehaviorCollection.h CommsBehavior.h
delaybehavior.obj:	yukon.h precompiled.h ctidbgmem.h DelayBehavior.h \
		CommsBehavior.h types.h logger.h dlldefs.h thread.h mutex.h \
		guard.h numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h os2_2w32.h sorted_vector.h
emetconwords.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWords.h \
		types.h dlldefs.h cti_asmc.h cticalls.h os2_2w32.h
mct410.obj:	yukon.h precompiled.h ctidbgmem.h Mct410.h EmetconWords.h \
		types.h ctitime.h dlldefs.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h sorted_vector.h
plcinfrastructure.obj:	yukon.h precompiled.h ctidbgmem.h \
		plcinfrastructure.h Mct410.h EmetconWords.h types.h ctitime.h \
		dlldefs.h smartmap.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h dllbase.h \
		dsm2.h mutex.h guard.h clrdump.h cticonnect.h netports.h \
		dsm2err.h words.h readers_writer_lock.h critical_section.h
plctransmitter.obj:	yukon.h precompiled.h ctidbgmem.h PlcTransmitter.h
portlogger.obj:	yukon.h precompiled.h ctidbgmem.h portlogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
simulator_main.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h \
		dlldefs.h ccusimsvc.h cservice.h CServiceConfig.h ctibase.h \
		ctinexus.h netports.h cticonnect.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		dsm2err.h words.h logger.h thread.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h
test_behavior_collection.obj:	yukon.h precompiled.h ctidbgmem.h \
		boostutil.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h sorted_vector.h \
		BehaviorCollection.h CommsBehavior.h
test_ccusim.obj:	ctitime.h dlldefs.h ctidate.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		CCU711.h ccu710.h portlogger.h emetconwords.h \
		PlcTransmitter.h CommInterface.h ctinexus.h netports.h \
		cticonnect.h yukon.h precompiled.h ctidbgmem.h \
		BehaviorCollection.h CommsBehavior.h fifo_multiset.h rwutil.h \
		boost_time.h boostutil.h Mct410.h
test_delay_behavior.obj:	yukon.h precompiled.h ctidbgmem.h boostutil.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h sorted_vector.h BehaviorCollection.h \
		CommsBehavior.h DelayBehavior.h logger.h thread.h mutex.h \
		guard.h clrdump.h CtiPCPtrQueue.h
test_mct410_sim.obj:	yukon.h precompiled.h ctidbgmem.h boostutil.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h sorted_vector.h Mct410.h EmetconWords.h
#ENDUPDATE#

