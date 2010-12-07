include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DATABASE)\include \
-I$(COMMON)\include \
-I$(RW) \
-I$(BOOST) \
-I$(SQLAPI)\include \



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
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)



DLLOBJS=\
tbl_rawpthistory.obj \
tbl_signal.obj \


DBLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \


CTIPROGS=\
ctidbres.dll


ALL:           $(CTIPROGS)

ctidbres.dll:  $(DLLOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(DLLOBJS) -link $(RWLIBS) $(BOOST_LIBS) \
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
dbsigsend.obj:	yukon.h precompiled.h types.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h tbl_signal.h \
		row_reader.h database_connection.h dbaccess.h sema.h \
		dbmemobject.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
tbl_rawpthistory.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_rawpthistory.h ctitime.h dlldefs.h pointdefs.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h sorted_vector.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h mutex.h guard.h cticonnect.h netports.h dsm2err.h \
		words.h sema.h logger.h thread.h CtiPCPtrQueue.h \
		database_writer.h row_writer.h database_reader.h
tbl_signal.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_signal.h \
		ctitime.h dlldefs.h row_reader.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h sorted_vector.h \
		cticonnect.h netports.h dsm2err.h words.h sema.h \
		dbmemobject.h logger.h thread.h CtiPCPtrQueue.h \
		database_writer.h row_writer.h database_reader.h
#ENDUPDATE#
