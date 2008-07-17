# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(CAPCONTROL)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(PROCLOG)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(SERVER)\include \
-I$(RW) \
-I$(BOOST) \


.PATH.cpp = .
.PATH.H = \
.\include \
;$(COMMON)\include \
;$(CAPCONTROL)\include \
;$(CPARMS)\include \
;$(DATABASE)\include \
;$(SERVICE)\include \
;$(RTDB)\include \
;$(SERVER)\include \
;$(PROCLOG)\include \
;$(MSG)\include \
;$(RW)



LIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib

BASEOBJS= \
capcontroller.obj \
cccapbank.obj \
ccclientconn.obj \
ccclientlistener.obj \
ccexecutor.obj \
ccfeeder.obj \
ccmain.obj \
ccmessage.obj \
ccservice.obj \
ccstate.obj \
ccsubstationbus.obj \
ccsubstationbusstore.obj \
pao_schedule.obj \
pao_event.obj \
mgr_paosched.obj \
ccstrategy.obj \
ccmonitorpoint.obj \
ccpointresponse.obj \
cctwowaycbcpoints.obj \
ccarea.obj \
ccsparea.obj \
ccsubstation.obj \
ccoperationstats.obj \
ccconfirmationstats.obj 


TARGS = capcontrol.exe

ALL:          $(TARGS)

capcontrol.exe:     $(BASEOBJS) Makefile
              @echo:
              @echo Compiling $@
              @%cd $(OBJ)
              $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOSTLIBS) $(LINKFLAGS)
              @echo:
              -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
              -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
              @%cd $(CWD)
              @echo Done building Target ..\$@
              @echo:

copy:       $(TARGS)
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)

deps:
                scandeps -Output makeexe.mak *.cpp


clean:
        -del \
*.pdb \
$(OBJ)\*.obj \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.exe


allclean:   clean all


.SUFFIXES:      .cpp .obj

.cpp.obj:
               @echo:
               @echo Compiling: $<
               @echo C-Options: $(CFLAGS)
               @echo Output   : ..\$@
               @echo:
               $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<
#UPDATE#
capcontroller.obj:	ccmessage.h ccsubstation.h ccfeeder.h \
		ccmonitorpoint.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		ccstrategy.h ccsubstationbus.h ccarea.h ccsparea.h ccstate.h \
		capcontroller.h ccsubstationbusstore.h ccid.h ccexecutor.h \
		mgr_paosched.h pao_schedule.h pao_event.h ccclientconn.h \
		ccclientlistener.h
ccarea.obj:	ccarea.h ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h ccstrategy.h ccid.h \
		capcontroller.h ccsubstationbusstore.h ccsparea.h ccstate.h \
		ccmessage.h ccsubstation.h ccexecutor.h
cccapbank.obj:	cccapbank.h ccpointresponse.h ccmonitorpoint.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		ccid.h
ccclientconn.obj:	ccclientconn.h ccmessage.h ccsubstation.h ccfeeder.h \
		ccmonitorpoint.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		ccstrategy.h ccsubstationbus.h ccarea.h ccsparea.h ccstate.h \
		ccexecutor.h ccsubstationbusstore.h ccid.h capcontroller.h
ccclientlistener.obj:	ccclientlistener.h ccclientconn.h ccstate.h \
		ccmessage.h ccsubstation.h ccfeeder.h ccmonitorpoint.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h ccstrategy.h \
		ccsubstationbus.h ccarea.h ccsparea.h ccsubstationbusstore.h \
		ccid.h ccexecutor.h
ccconfirmationstats.obj:	ccid.h ccconfirmationstats.h
ccexecutor.obj:	ccclientlistener.h ccclientconn.h ccstate.h \
		ccexecutor.h ccmessage.h ccsubstation.h ccfeeder.h \
		ccmonitorpoint.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		ccstrategy.h ccsubstationbus.h ccarea.h ccsparea.h \
		ccsubstationbusstore.h ccid.h capcontroller.h
ccfeeder.obj:	ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h ccstrategy.h ccid.h \
		capcontroller.h ccsubstationbusstore.h ccarea.h ccsparea.h \
		ccstate.h ccmessage.h ccsubstation.h ccexecutor.h
ccmain.obj:	ccsubstationbusstore.h ccarea.h ccsubstationbus.h \
		ccfeeder.h ccmonitorpoint.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		ccstrategy.h ccsparea.h ccid.h ccstate.h ccmessage.h \
		ccsubstation.h capcontroller.h ccexecutor.h ccservice.h \
		ccclientlistener.h ccclientconn.h
ccmessage.obj:	ccmessage.h ccsubstation.h ccfeeder.h ccmonitorpoint.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h ccstrategy.h \
		ccsubstationbus.h ccarea.h ccsparea.h ccstate.h ccid.h
ccmonitorpoint.obj:	ccid.h ccmonitorpoint.h
ccoperationstats.obj:	ccid.h ccoperationstats.h
ccpointresponse.obj:	ccpointresponse.h ccid.h
ccserver.obj:	ccserver.h ccclientlistener.h ccclientconn.h ccstate.h \
		ccmessage.h ccsubstation.h ccfeeder.h ccmonitorpoint.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h ccstrategy.h \
		ccsubstationbus.h ccarea.h ccsparea.h
ccservice.obj:	id_capcontrol.h ccservice.h ccclientlistener.h \
		ccclientconn.h ccstate.h capcontroller.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		ccstrategy.h ccsparea.h ccid.h ccmessage.h ccsubstation.h \
		ccexecutor.h
ccsparea.obj:	ccsparea.h ccstrategy.h ccOperationStats.h \
		ccConfirmationStats.h ccid.h cccapbank.h ccpointresponse.h \
		ccmonitorpoint.h cctwowaycbcpoints.h capcontroller.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h ccfeeder.h \
		ccstate.h ccmessage.h ccsubstation.h ccexecutor.h
ccstate.obj:	ccid.h ccstate.h
ccstrategy.obj:	ccstrategy.h ccid.h capcontroller.h \
		ccsubstationbusstore.h ccarea.h ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		ccsparea.h ccstate.h ccmessage.h ccsubstation.h ccexecutor.h
ccsubstation.obj:	ccsubstation.h ccfeeder.h ccmonitorpoint.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h ccstrategy.h \
		ccsubstationbus.h ccid.h capcontroller.h \
		ccsubstationbusstore.h ccarea.h ccsparea.h ccstate.h \
		ccmessage.h ccexecutor.h
ccsubstationbus.obj:	ccsubstationbus.h ccfeeder.h ccmonitorpoint.h \
		cccapbank.h ccpointresponse.h cctwowaycbcpoints.h \
		ccoperationstats.h ccConfirmationStats.h ccstrategy.h ccid.h \
		capcontroller.h ccsubstationbusstore.h ccarea.h ccsparea.h \
		ccstate.h ccmessage.h ccsubstation.h ccexecutor.h \
		mgr_paosched.h pao_schedule.h pao_event.h
ccsubstationbusstore.obj:	ccsubstationbusstore.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h ccstrategy.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h ccsubstation.h capcontroller.h \
		ccexecutor.h
cctwowaycbcpoints.obj:	cctwowaycbcpoints.h cccapbank.h \
		ccpointresponse.h ccmonitorpoint.h ccoperationstats.h \
		ccConfirmationStats.h ccid.h
mgr_paosched.obj:	mgr_paosched.h pao_schedule.h pao_event.h \
		capcontroller.h ccsubstationbusstore.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h ccstrategy.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h ccsubstation.h ccexecutor.h
pao_event.obj:	capcontroller.h ccsubstationbusstore.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h ccstrategy.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h ccsubstation.h ccexecutor.h \
		pao_schedule.h pao_event.h
pao_schedule.obj:	capcontroller.h ccsubstationbusstore.h ccarea.h \
		ccsubstationbus.h ccfeeder.h ccmonitorpoint.h cccapbank.h \
		ccpointresponse.h cctwowaycbcpoints.h ccoperationstats.h \
		ccConfirmationStats.h ccstrategy.h ccsparea.h ccid.h \
		ccstate.h ccmessage.h ccsubstation.h ccexecutor.h \
		pao_schedule.h pao_event.h
test_ccsubstationbus.obj:	ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		ccstrategy.h
#ENDUPDATE#

