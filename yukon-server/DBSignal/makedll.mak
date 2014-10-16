include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(DATABASE)\include \
-I$(COMMON)\include \
-I$(RW) \
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
;$(RW)



DLLOBJS=\
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
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(DLLOBJS) -link $(RWLIBS) $(BOOST_LIBS) \
$(DBLIBS) ctidbres.res
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
$(OBJ)\*.res \
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
dbsigsend.obj:	precompiled.h dllbase.h dsm2.h streamConnection.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h tbl_signal.h row_reader.h \
		database_connection.h dbaccess.h dbmemobject.h
tbl_rawpthistory.obj:	precompiled.h tbl_rawpthistory.h ctitime.h \
		dlldefs.h row_writer.h logger.h streamBuffer.h string_util.h \
		dllbase.h dsm2.h streamConnection.h yukon.h types.h \
		ctidbgmem.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h constants.h numstr.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		exception_helper.h
tbl_signal.obj:	precompiled.h tbl_signal.h ctitime.h dlldefs.h \
		row_reader.h database_connection.h dbaccess.h dllbase.h \
		dsm2.h streamConnection.h yukon.h types.h ctidbgmem.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h dbmemobject.h logger.h \
		streamBuffer.h string_util.h exception_helper.h \
		database_writer.h row_writer.h database_reader.h \
		database_util.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc
