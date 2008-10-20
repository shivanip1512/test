include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DATABASE)\include \
-I$(COMMON)\include \
-I$(RW) \
-I$(BOOST) \
-I$(R_COMMON)\include \
-I$(R_DATABASE)\include \



.PATH.cpp = .;$(R_DBSIGNAL)

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
;$(PROT)\include \
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(R_CPARMS)\include \
;$(R_DATABASE)\include \
;$(R_PORTER)\include \
;$(R_COMMON)\include \
;$(R_SCANNER)\include \
;$(R_SERVICE)\include \
;$(R_PIL)\include \
;$(R_SERVER)\include \
;$(R_PROT)\include \
;$(R_PROCLOG)\include \
;$(R_DISPATCH)\include \
;$(R_MSG)\include \
;$(TCLINC) \
;$(RW)



DLLOBJS=\
tbl_rawpthistory.obj \
tbl_signal.obj \


DBLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib \


CTIPROGS=\
ctidbres.dll


ALL:           $(CTIPROGS)

ctidbres.dll:  $(DLLOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(DLLOBJS) -link $(RWLIBS) $(BOOSTLIBS) \
$(DBLIBS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @echo:
                @echo Done building Target $@
                @echo:
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\*.dll copy bin\*.dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\*.lib copy bin\*.lib $(COMPILEBASE)\lib


deps:
                scandeps -Output makedll.mak *.cpp


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



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_SIGNAL -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
dbsigsend.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h tbl_signal.h ctitime.h \
		dbmemobject.h pointdefs.h utility.h queues.h sorted_vector.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
tbl_rawpthistory.obj:	yukon.h precompiled.h ctidbgmem.h \
		tbl_rawpthistory.h ctitime.h dlldefs.h pointdefs.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h dbaccess.h dllbase.h dsm2.h mutex.h guard.h \
		clrdump.h cticonnect.h netports.h sema.h logger.h thread.h \
		CtiPCPtrQueue.h rwutil.h boost_time.h boostutil.h
tbl_signal.obj:	yukon.h precompiled.h ctidbgmem.h tbl_signal.h \
		ctitime.h dlldefs.h dbmemobject.h pointdefs.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h dbaccess.h dllbase.h dsm2.h mutex.h guard.h \
		clrdump.h cticonnect.h netports.h sema.h logger.h thread.h \
		CtiPCPtrQueue.h rwutil.h boost_time.h boostutil.h
#ENDUPDATE#
