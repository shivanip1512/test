include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(SERVER)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
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
ccu710.obj \
ccu711.obj \

CCU_SIMULATOR_LIBS=\
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctiprtdb.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\clrdump.lib


CTIPROGS=\
ccu_simulator.exe


ALL:            $(CTIPROGS)

ccu_simulator.exe:      $(CCU_SIMULATOR_OBJS) makeexe.mak
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(RWLINKFLAGS) $(INCLPATHS) -o ..\$@ $(CCU_SIMULATOR_OBJS) -link $(CCU_SIMULATOR_LIBS) $(RWLIBS) $(BOOSTLIBS) $(LINKFLAGS)
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
		ctinexus.h dlldefs.h netports.h cticonnect.h numstr.h \
		cticalls.h os2_2w32.h types.h color.h
ccu711.obj:	yukon.h precompiled.h ctidbgmem.h CCU711.h CCU710.h \
		EmetconWord.h ctiTime.h dlldefs.h mctStruct.h ctinexus.h \
		netports.h cticonnect.h numstr.h cticalls.h os2_2w32.h \
		types.h cti_asmc.h color.h
ccusimulator.obj:	yukon.h precompiled.h ctidbgmem.h cticalls.h \
		os2_2w32.h dlldefs.h types.h ctinexus.h netports.h \
		cticonnect.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		color.h ctiTime.h dbaccess.h dllbase.h sema.h mctStruct.h \
		CCU710.h EmetconWord.h CCU711.h
clientnexus.obj:	yukon.h precompiled.h ctidbgmem.h clientNexus.h \
		ctinexus.h dlldefs.h netports.h cticonnect.h numstr.h \
		cticalls.h os2_2w32.h types.h
emetconword.obj:	yukon.h precompiled.h ctidbgmem.h EmetconWord.h \
		cticalls.h os2_2w32.h dlldefs.h types.h cti_asmc.h numstr.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
test_ccusim.obj:	yukon.h precompiled.h ctidbgmem.h ctitime.h dlldefs.h \
		ctidate.h logger.h thread.h mutex.h guard.h numstr.h \
		clrdump.h CtiPCPtrQueue.h utility.h queues.h types.h \
		sorted_vector.h ctistring.h rwutil.h boost_time.h CCU711.h \
		CCU710.h EmetconWord.h mctStruct.h
#ENDUPDATE#
