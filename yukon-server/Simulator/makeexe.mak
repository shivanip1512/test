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
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)



CCU_SIMULATOR_OBJS=\
CCUSimulator.obj \
EmetconWord.obj \
SimulatedCCU.obj \
CCU711Manager.obj \
CCU710Manager.obj \
ccu710.obj \
ccu711.obj \
EmetconWordBase.obj \
EmetconWordD1.obj \
EmetconWordB.obj \
EmetconWordC.obj \
EmetconWordDn.obj \
EmetconWordFactory.obj \
SharedFunctions.obj \
Mct410Sim.obj \

CCU_SIMULATOR_LIBS=\
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiprtdb.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib 


CTIPROGS=\
ccu_simulator.exe


ALL:            $(CTIPROGS)

ccu_simulator.exe:      $(CCU_SIMULATOR_OBJS) makeexe.mak
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(RWLINKFLAGS) $(INCLPATHS) -o ..\$@ $(CCU_SIMULATOR_OBJS) -link $(CCU_SIMULATOR_LIBS) $(RWLIBS) $(BOOSTLIBS) 
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -@copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

copy:           $(CTIPROGS)
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
                scandeps -Output makeexe.mak *.cpp



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
ccu710.obj:	yukon.h precompiled.h ctidbgmem.h CCU710.h EmetconWord.h \
		ctitime.h dlldefs.h ctinexus.h netports.h cticonnect.h \
		numstr.h cticalls.h os2_2w32.h types.h color.h logger.h \
		thread.h mutex.h guard.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h sorted_vector.h SharedFunctions.h
ccu710manager.obj:	yukon.h precompiled.h ctidbgmem.h CCU710Manager.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h CCU710.h \
		EmetconWord.h ctitime.h SimulatedCCU.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h
ccu711.obj:	yukon.h precompiled.h ctidbgmem.h CCU711.h CCU710.h \
		EmetconWord.h ctitime.h dlldefs.h ctiDate.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		mctStruct.h EmetconWordB.h EmetconWordBase.h Mct410Sim.h \
		cti_asmc.h ctinexus.h netports.h cticonnect.h color.h \
		SharedFunctions.h EmetconWordC.h EmetconWordFactory.h
ccu711manager.obj:	yukon.h precompiled.h ctidbgmem.h CCU711Manager.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h SimulatedCCU.h \
		ctiTime.h CCU711.h CCU710.h EmetconWord.h ctiDate.h logger.h \
		thread.h mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h \
		sorted_vector.h mctStruct.h EmetconWordB.h EmetconWordBase.h \
		Mct410Sim.h cti_asmc.h
ccusimulator.obj:	yukon.h precompiled.h ctidbgmem.h cticalls.h \
		os2_2w32.h dlldefs.h types.h ctinexus.h netports.h \
		cticonnect.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		color.h ctiTime.h dbaccess.h dllbase.h sema.h mctStruct.h \
		SimulatedCCU.h CCU711Manager.h CCU711.h CCU710.h \
		EmetconWord.h ctiDate.h logger.h thread.h CtiPCPtrQueue.h \
		utility.h queues.h sorted_vector.h EmetconWordB.h \
		EmetconWordBase.h Mct410Sim.h cti_asmc.h CCU710Manager.h
clientnexus.obj:	yukon.h precompiled.h ctidbgmem.h clientNexus.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h numstr.h \
		cticalls.h os2_2w32.h types.h
emetconword.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWord.h \
		ctitime.h dlldefs.h cticalls.h os2_2w32.h types.h cti_asmc.h \
		numstr.h SharedFunctions.h
emetconwordb.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWordB.h \
		EmetconWordBase.h SharedFunctions.h ctitime.h dlldefs.h
emetconwordbase.obj:	yukon.h precompiled.h ctidbgmem.h \
		EmetconWordBase.h
emetconwordc.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWordC.h \
		EmetconWordBase.h
emetconwordd1.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWordD1.h \
		EmetconWordBase.h dlldefs.h cti_asmc.h
emetconworddn.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWordDn.h \
		EmetconWordBase.h dlldefs.h cti_asmc.h
emetconwordfactory.obj:	yukon.h precompiled.h ctidbgmem.h \
		EmetconWordFactory.h EmetconWordBase.h EmetconWordD1.h \
		EmetconWordB.h EmetconWordC.h
mct410sim.obj:	yukon.h precompiled.h ctidbgmem.h Mct410Sim.h ctitime.h \
		dlldefs.h cti_asmc.h SharedFunctions.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		EmetconWordDn.h EmetconWordBase.h EmetconWordD1.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
sharedfunctions.obj:	yukon.h precompiled.h ctidbgmem.h \
		SharedFunctions.h ctitime.h dlldefs.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h
simulatedccu.obj:	yukon.h precompiled.h ctidbgmem.h SimulatedCCU.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h ctiTime.h
test_ccusim.obj:	ctitime.h dlldefs.h ctidate.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		CCU711.h CCU710.h EmetconWord.h mctStruct.h EmetconWordB.h \
		EmetconWordBase.h Mct410Sim.h cti_asmc.h yukon.h \
		precompiled.h ctidbgmem.h rwutil.h boost_time.h boostutil.h \
		SharedFunctions.h
#ENDUPDATE#
