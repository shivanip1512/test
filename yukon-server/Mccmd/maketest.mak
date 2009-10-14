
# nmake file YUKON 1.0

!include ..\common\global.inc
!include ..\common\rwglobal.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(MCCMD)\include \
-I$(CPARMS)\include \
-I$(DATABASE)\include \
-I$(MSG)\include \
-I$(RTDB)\include \
-I$(TCLINC) \
-I$(RW) \
-I$(BOOST) \



.PATH.cpp = .;$(R_MCCMD)

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
;$(PROTOCOL)\include \
;$(RTDB)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)

MCCMDTESTOBJS=\
test_decodeTextCmdFile.obj

DLLOBJS=\
$(OBJ)\decodeTextCmdFile.obj

LIBS=\
$(COMPILEBASE)\lib\$(TCL_LIB).lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib

ALL: mccmd

mccmd:  $(MCCMDTESTOBJS) maketest.mak


copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -if exist bin\mccmd.dll copy bin\test_*.exe $(YUKONOUTPUT)

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
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS) $(DLLOBJS) $(LIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################

#UPDATE#
test_decodetextcmdfile.obj:	decodetextcmdfile.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h
#ENDUPDATE#
