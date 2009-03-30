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



CCU_SIMULATOR_OBJS=\
ccu_simulator.obj \
CommInterface.obj \
PortLogger.obj \
PlcInfrastructure.obj \
EmetconWords.obj \
PlcTransmitter.obj \
Ccu710.obj \
Ccu711.obj \
Mct410.obj \

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
                $(RWCPPINVOKE) $(CFLAGS) $(RWLINKFLAGS) $(INCLPATHS) /Fe..\$@ $(CCU_SIMULATOR_OBJS) -link $(CCU_SIMULATOR_LIBS) $(RWLIBS) $(BOOSTLIBS)
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
                -@copy ..\$@ $(YUKONOUTPUT)
                @echo:
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

copy:           $(CTIPROGS)
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
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
ccu710.obj:	yukon.h precompiled.h ctidbgmem.h Ccu710.h portlogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h emetconwords.h \
		PlcTransmitter.h CommInterface.h ctinexus.h netports.h \
		cticonnect.h Simulator.h PlcInfrastructure.h Mct410.h \
		smartmap.h boostutil.h dllbase.h dsm2.h readers_writer_lock.h \
		critical_section.h
ccu711.obj:	yukon.h precompiled.h ctidbgmem.h ccu711.h ccu710.h \
		portlogger.h logger.h dlldefs.h thread.h mutex.h guard.h \
		numstr.h clrdump.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		emetconwords.h PlcTransmitter.h CommInterface.h ctinexus.h \
		netports.h cticonnect.h fifo_multiset.h boostutil.h \
		cti_asmc.h color.h ctidate.h simulator.h PlcInfrastructure.h \
		Mct410.h smartmap.h dllbase.h dsm2.h readers_writer_lock.h \
		critical_section.h
ccu_simulator.obj:	yukon.h precompiled.h ctidbgmem.h \
		PlcInfrastructure.h Mct410.h EmetconWords.h types.h ctitime.h \
		dlldefs.h smartmap.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h dllbase.h \
		dsm2.h mutex.h guard.h clrdump.h cticonnect.h netports.h \
		readers_writer_lock.h critical_section.h Ccu710.h \
		portlogger.h logger.h thread.h CtiPCPtrQueue.h \
		PlcTransmitter.h CommInterface.h ctinexus.h Ccu711.h \
		fifo_multiset.h
comminterface.obj:	yukon.h precompiled.h ctidbgmem.h CommInterface.h \
		types.h ctinexus.h dlldefs.h netports.h cticonnect.h
emetconwords.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWords.h \
		types.h dlldefs.h cti_asmc.h
mct410.obj:	yukon.h precompiled.h ctidbgmem.h Mct410.h EmetconWords.h \
		types.h ctitime.h dlldefs.h logger.h thread.h mutex.h guard.h \
		numstr.h clrdump.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h sorted_vector.h
plcinfrastructure.obj:	yukon.h precompiled.h ctidbgmem.h \
		plcinfrastructure.h Mct410.h EmetconWords.h types.h ctitime.h \
		dlldefs.h smartmap.h boostutil.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h sorted_vector.h dllbase.h \
		dsm2.h mutex.h guard.h clrdump.h cticonnect.h netports.h \
		readers_writer_lock.h critical_section.h
plctransmitter.obj:	yukon.h precompiled.h ctidbgmem.h PlcTransmitter.h
portlogger.obj:	yukon.h precompiled.h ctidbgmem.h portlogger.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
test_ccusim.obj:	ctitime.h dlldefs.h ctidate.h logger.h thread.h \
		mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h \
		CCU711.h ccu710.h portlogger.h emetconwords.h \
		PlcTransmitter.h CommInterface.h ctinexus.h netports.h \
		cticonnect.h yukon.h precompiled.h ctidbgmem.h \
		fifo_multiset.h rwutil.h boost_time.h boostutil.h
#ENDUPDATE#
