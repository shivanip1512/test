include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(DATABASE)\include \
-I$(COMMON)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \



.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \



DLLOBJS=\
$(PRECOMPILED_OBJ) \
tbl_rawpthistory.obj \
tbl_signal.obj \


DBLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \


CTIPROGS=\
ctidbres.dll


PROGS_VERSION=\
$(CTIPROGS)


ALL:           $(CTIPROGS)

ctidbres.dll:  $(DLLOBJS) Makefile $(OBJ)\ctidbres.res
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(CC) $(INCLPATHS) $(DLLFLAGS) -Fe..\$@ $(DLLOBJS) -link $(BOOST_LIBS) \
$(DBLIBS) ctidbres.res
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	       -if exist ..\bin\$(@B).pdb copy ..\bin\$(@B).pdb $(YUKONDEBUG)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @echo:
                @echo Done building Target $@
                @echo:
                @%cd $(CWD)

copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist bin\*.dll copy bin\*.dll $(YUKONOUTPUT)
	       -@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
               -if exist bin\*.pdb copy bin\*.pdb $(YUKONDEBUG)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\*.lib copy bin\*.lib $(COMPILEBASE)\lib


deps:
                scandeps -Output makedll.mak *.cpp


clean:
        -del \
*.pdb \
$(OBJ)\*.obj \
$(OBJ)\*.res \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.exe \
$(BIN)\*.pdb



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(CC) $(CCOPTS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_SIGNAL -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
dbsigsend.obj:	precompiled.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h \
		tbl_signal.h ctitime.h row_reader.h database_connection.h \
		dbaccess.h guard.h utility.h queues.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h dbmemobject.h
tbl_rawpthistory.obj:	precompiled.h tbl_rawpthistory.h ctitime.h \
		dlldefs.h row_writer.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h critical_section.h
tbl_signal.obj:	precompiled.h tbl_signal.h ctitime.h dlldefs.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h guard.h utility.h queues.h constants.h \
		numstr.h logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h dbmemobject.h \
		database_writer.h row_writer.h database_reader.h \
		database_util.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
