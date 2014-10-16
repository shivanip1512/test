# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(PIL)\include \
-I$(SERVER)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(RTDB)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST_INCLUDE) \
;$(RW)

LIBS=\
$(COMPILEBASE)\lib\ctibase.lib \

DLLOBJS = \
config_device.obj \
config_strings.obj \
da_lp_deviceconfig.obj \
mgr_config.obj \


CTIPROGS=\
cticonfig.dll


DEVCONF_FULLBUILD = $[Filename,$(OBJ),DevConfFullBuild,target]


PROGS_VERSION=\
$(CTIPROGS)


ALL:            $(CTIPROGS)


$(DEVCONF_FULLBUILD) :
        @touch $@
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) /D_DLL_CONFIG -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DLLOBJS)]


cticonfig.dll:  $(DEVCONF_FULLBUILD) $(DLLOBJS) Makedll.mak $(OBJ)\cticonfig.res
                @build -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(CC) $(RWCPPFLAGS) $(DLLFLAGS) $(DLLOBJS) id_dcdll.obj $(INCLPATHS) /Fe..\$@ -link $(LIBS) $(RWLIBS) $(BOOST_LIBS) $(LINKFLAGS) cticonfig.res
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


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp

deps:
                scandeps -Output makedll.mak *.cpp


# The lines below accomplish the ID'ing of the project!
id:
            @build -nologo -f $(_InputFile) id_dcdll.obj

id_dcdll.obj:    id_dcdll.cpp include\id_dcdll.h

.SUFFIXES:      .cpp .obj

########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_CONFIG -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
config_device.obj:	precompiled.h config_device.h yukon.h types.h \
		ctidbgmem.h dllbase.h dsm2.h streamConnection.h dlldefs.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h hashkey.h hash_functions.h \
		DeviceConfigDescription.h devicetypes.h pointtypes.h \
		PointAttribute.h std_helper.h
config_strings.obj:	precompiled.h config_data_mct.h yukon.h types.h \
		ctidbgmem.h dllbase.h dsm2.h streamConnection.h dlldefs.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h config_data_dnp.h \
		config_data_rfn.h
da_lp_deviceconfig.obj:	precompiled.h da_lp_deviceconfig.h yukon.h \
		types.h ctidbgmem.h da_load_profile.h config_device.h \
		dllbase.h dsm2.h streamConnection.h dlldefs.h netports.h \
		timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h hashkey.h hash_functions.h \
		config_data_mct.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h
id_dcdll.obj:	precompiled.h id_dcdll.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h constants.h numstr.h
mgr_config.obj:	precompiled.h mgr_config.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h readers_writer_lock.h \
		critical_section.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h constants.h numstr.h mutex.h dsm2err.h \
		words.h optional.h macro_offset.h config_device.h hashkey.h \
		hash_functions.h devicetypes.h dbaccess.h \
		database_connection.h database_reader.h row_reader.h \
		DeviceConfigDescription.h pointtypes.h PointAttribute.h \
		debug_timer.h logger.h streamBuffer.h string_util.h \
		exception_helper.h std_helper.h
test_device_config.obj:	boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h constants.h numstr.h \
		config_device.h yukon.h ctidbgmem.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		readers_writer_lock.h critical_section.h guard.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h hashkey.h \
		hash_functions.h
#ENDUPDATE#

include $(COMPILEBASE)\versioninfo.inc

