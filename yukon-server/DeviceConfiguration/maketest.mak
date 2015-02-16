# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(PIL)\include \
-I$(SERVER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIG)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \


.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(DEVICECONFIG)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST_INCLUDE) \

LIBS=\
$(COMPILEBASE)\lib\cticonfig.lib \

DEVICECONFIG_TEST_OBJS=\
$(PRECOMPILED_OBJ) \
test_main.obj \
test_device_config.obj \


DEVICECONFIG_TEST_FULLBUILD = $[Filename,$(OBJ),DeviceConfigTestFullBuild,target]


ALL:            test_deviceconfig.exe

$(DEVICECONFIG_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DEVICECONFIG_TEST_OBJS)]

test_deviceconfig.exe:	$(DEVICECONFIG_TEST_FULLBUILD) $(DEVICECONFIG_TEST_OBJS) Makefile
	@echo:
	@echo Creating Executable $(BIN)\$(@B).exe
        @echo:
	@%cd obj
	$(CC) $(CFLAGS) $(INCLPATHS)  /Fe..\$(BIN)\$(@B).exe \
	$(DEVICECONFIG_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..
	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.


deps:
                scandeps -Output maketest.mak *.cpp

clean:
        -del \
test*.pdb \
$(OBJ)\test*.obj \
$(BIN)\test*.pdb \
$(BIN)\test*.pch \
$(BIN)\test*.ilk \
$(BIN)\test*.exp \
$(BIN)\test*.lib \
$(BIN)\test*.dll \
$(BIN)\test*.exe


allclean:   clean test

copy:
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -copy bin\*.exe $(YUKONOUTPUT)


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
	$(CC) $(CCOPTS) $(CFLAGS) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################

#UPDATE#
config_device.obj:	precompiled.h config_device.h yukon.h types.h \
		ctidbgmem.h dllbase.h dsm2.h streamConnection.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h \
		DeviceConfigDescription.h devicetypes.h pointtypes.h \
		PointAttribute.h std_helper.h
config_strings.obj:	precompiled.h config_data_mct.h yukon.h types.h \
		ctidbgmem.h dllbase.h dsm2.h streamConnection.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h config_data_dnp.h \
		config_data_rfn.h
da_lp_deviceconfig.obj:	precompiled.h da_lp_deviceconfig.h yukon.h \
		types.h ctidbgmem.h da_load_profile.h config_device.h \
		dllbase.h dsm2.h streamConnection.h dlldefs.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h config_data_mct.h
id_dcdll.obj:	precompiled.h id_dcdll.h module_util.h dlldefs.h \
		ctitime.h
mgr_config.obj:	precompiled.h mgr_config.h dllbase.h dsm2.h \
		streamConnection.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h timing_util.h immutable.h critical_section.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		constants.h numstr.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h boostutil.h mutex.h \
		dsm2err.h words.h optional.h macro_offset.h config_device.h \
		devicetypes.h readers_writer_lock.h dbaccess.h \
		database_connection.h database_reader.h row_reader.h \
		DeviceConfigDescription.h pointtypes.h PointAttribute.h \
		debug_timer.h std_helper.h
test_device_config.obj:	boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h yukon.h types.h ctidbgmem.h os2_2w32.h \
		constants.h numstr.h config_device.h dllbase.h dsm2.h \
		streamConnection.h netports.h timing_util.h immutable.h \
		critical_section.h guard.h logger.h streamBuffer.h loggable.h \
		string_util.h exception_helper.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h
#ENDUPDATE#
