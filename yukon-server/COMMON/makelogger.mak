#Build name MUST BE FIRST!!!!

DLLBUILDNAME = -DCTIBASE

!include $(COMPILEBASE)\global.inc
!include $(COMPILEBASE)\rwglobal.inc


INCLPATHS+= \
-I$(COMMON)\include \
-I$(RESOURCE)\include \
-I$(BOOST_INCLUDE) \
-I$(LOG4CXX_INCLUDE) \

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(RESOURCE)\include \
;$(BOOST_INCLUDE) \


LOGGEROBJS=\
logLayout.obj \
logFileAppender.obj \
logManager.obj \
logger.obj \


CTIPROGS=\
logger.lib


COMMON_LOGGERBUILD = $[Filename,$(OBJ),CommonLoggerFullBuild,target]

ALL:            $(CTIPROGS)

$(COMMON_LOGGERBUILD):
        @touch $@
        @echo:
        @echo Compiling logger cpp to obj
        @echo:
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(DLLBUILDNAME) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(LOGGEROBJS)] /wd4275 /wd4251

logger.lib:    $(COMMON_LOGGERBUILD) $(LOGGEROBJS) Makefile
                @%cd $(OBJ)
                lib /OUT:$@ $(LOGGEROBJS)
                @%cd $(CWD)

deps:
                scandeps -Output makelogger.mak *.cpp

clean:
        -del \
*.pdb \
*.idb \
*.obj \
$(OBJ)\*.obj \
$(OBJ)\*.target \
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.map \


allclean:   clean all


# The lines below accomplish the ID'ing of the project!
id:
            # @cid .\include\id_ctibase.h
            @build -nologo -f $(_InputFile) id_ctibase.obj

id_ctibase.obj:    id_ctibase.cpp include\id_ctibase.h


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(DLLBUILDNAME) $(INCLPATHS) -Fo$(OBJ)\ -c $<  /wd4275 /wd4251

######################################################################################



#UPDATE#
logfileappender.obj:	utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		logFileAppender.h logManager.h module_util.h logger.h \
		streamBuffer.h loggable.h string_util.h exception_helper.h \
		boostutil.h critical_section.h atomic.h ctidate.h
logger.obj:	guard.h utility.h ctitime.h dlldefs.h queues.h cticalls.h \
		yukon.h types.h ctidbgmem.h os2_2w32.h constants.h numstr.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h critical_section.h atomic.h \
		logManager.h module_util.h
loglayout.obj:	ctitime.h dlldefs.h logLayout.h logManager.h \
		module_util.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h utility.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h critical_section.h atomic.h
logmanager.obj:	logLayout.h dlldefs.h logManager.h module_util.h \
		logger.h streamBuffer.h loggable.h string_util.h \
		exception_helper.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h constants.h \
		numstr.h critical_section.h atomic.h logFileAppender.h \
		ctidate.h ctistring.h
#ENDUPDATE#

