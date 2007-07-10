include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(SERVICE)\include \
-I$(CPARMS)\include \
-I$(RW) \
-I$(BOOST) \


.PATH.cpp = .;$(R_DISPATCH)

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
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
;$(SIGNAL)\include \
;$(TCLINC) \
;$(RW)



CALCTESTOBJS= \
test_calc.obj

CALCLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctiholidaydb.lib \
$(COMPILEBASE)\lib\clrdump.lib \
advapi32.lib

CALCOBJS= \
$(OBJ)\calc.obj \
$(OBJ)\calccomponent.obj \
$(OBJ)\pointstore.obj \
$(OBJ)\calcthread.obj \
$(OBJ)\calclogicsvc.obj \


ALL:      calctest

calctest:  $(CALCTESTOBJS) makeexe.mak

copy:
           -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
           -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


deps:
        scandeps -Output maketest.mak test_*.cpp



clean:
    -del *.obj
    -del *.pch
    -del *.pdb
    -del *.sdb
    -del *.adb
    -del *.ilk
    -del *.exe


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
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(CALCOBJS) $(BOOSTTESTLIBS) $(RWLIBS) $(CALCLIBS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


######################################################################################



#UPDATE#
test_calc.obj:	yukon.h precompiled.h ctidbgmem.h calc.h ctiqueues.h \
		calccomponent.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h numstr.h clrdump.h CtiPCPtrQueue.h \
		utility.h queues.h types.h sorted_vector.h pointstore.h \
		hashkey.h hash_functions.h rtdb.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h cticonnect.h netports.h pointdefs.h \
		regression.h tbl_pt_limit.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h
#ENDUPDATE#
