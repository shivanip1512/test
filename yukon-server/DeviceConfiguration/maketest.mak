# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(PIL)\include \
-I$(SERVER)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIG)\include \
-I$(MSG)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \


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
;$(RW)

LIBS=\
$(COMPILEBASE)\lib\cticonfig.lib \

DEVICECONFIG_TEST_OBJS=\
test_main.obj \
test_device_config.obj \


DEVICECONFIG_TEST_FULLBUILD = $[Filename,$(OBJ),DeviceConfigTestFullBuild,target]


ALL:            test_deviceconfig.exe

$(DEVICECONFIG_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DEVICECONFIG_TEST_OBJS)]

test_deviceconfig.exe:	$(DEVICECONFIG_TEST_FULLBUILD) $(DEVICECONFIG_TEST_OBJS) Makefile
	@echo:
	@echo Creating Executable $(BIN)\$(@B).exe
        @echo:
	@%cd obj
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(@B).exe \
	$(DEVICECONFIG_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(RWLIBS) $(LIBS) $(LINKFLAGS)
	@%cd ..
	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
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
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################

#UPDATE#

#ENDUPDATE#
