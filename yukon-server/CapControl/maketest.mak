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

CAPCTRLTESTOBJS= \
test_ccsubstationbus.obj

CAPCTRLBASEOBJS= \
$(OBJ)\ccservice.obj \
$(OBJ)\capcontroller.obj \
$(OBJ)\cccapbank.obj \
$(OBJ)\ccclientconn.obj \
$(OBJ)\ccclientlistener.obj \
$(OBJ)\ccexecutor.obj \
$(OBJ)\ccfeeder.obj \
$(OBJ)\ccmessage.obj \
$(OBJ)\ccstate.obj \
$(OBJ)\ccsubstationbus.obj \
$(OBJ)\ccsubstationbusstore.obj \
$(OBJ)\pao_schedule.obj \
$(OBJ)\pao_event.obj \
$(OBJ)\mgr_paosched.obj \
$(OBJ)\ccstrategy.obj \
$(OBJ)\ccmonitorpoint.obj \
$(OBJ)\ccpointresponse.obj \
$(OBJ)\cctwowaycbcpoints.obj \
$(OBJ)\ccarea.obj \
$(OBJ)\ccsparea.obj \
$(OBJ)\ccsubstation.obj \
$(OBJ)\ccoperationstats.obj \
$(OBJ)\ccconfirmationstats.obj 

TARGS = capcontrol.exe

ALL:      capcontrol

capcontrol:  $(CAPCTRLTESTOBJS) makeexe.mak

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
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(CAPCTRLBASEOBJS) $(BOOSTTESTLIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################
#UPDATE#
test_ccsubstationbus.obj:	ccsubstationbus.h ccfeeder.h \
		ccmonitorpoint.h cccapbank.h ccpointresponse.h \
		cctwowaycbcpoints.h ccoperationstats.h ccConfirmationStats.h \
		ccstrategy.h
#ENDUPDATE#

