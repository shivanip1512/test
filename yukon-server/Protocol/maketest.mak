include $(COMPILEBASE)\global.inc

INCLPATHS+= \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(PROT)\include \
-I$(COMMON)\include \
-I$(MSG)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(SQLAPI)\include \


.PATH.cpp = .;$(R_PROT)

.PATH.H = \
.\include \
;$(BOOST_INCLUDE) \
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


PROTOCOL_TEST_OBJS=\
$(PRECOMPILED_OBJ) \
test_main.obj \
test_prot_klondike.obj \
test_prot_e2eDataTransfer.obj \
test_prot_expresscom.obj \
test_prot_dnp.obj \
test_dnp_transport.obj \
test_dnp_objects.obj \
test_dnp_datalink.obj \
test_dnp_application.obj \
test_prot_sa305.obj

PROTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\saprotocol.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib


LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib

PROTOCOL_TEST_FULLBUILD = $[Filename,$(OBJ),ProtocolTestFullBuild,target]


ALL:            test_protocol.exe

$(PROTOCOL_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(CC) $(CCOPTS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(PROTOCOL_TEST_OBJS)]

test_protocol.exe:    $(PROTOCOL_TEST_FULLBUILD) $(PROTOCOL_TEST_OBJS)  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS)  /Fe..\$(BIN)\$(_TargetF) \
        $(PROTOCOL_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(PROTLIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
	-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
	-copy $(BIN)\*.pdb $(YUKONDEBUG)
        @%cd $(CWD)
        @echo.


deps:
                scandeps -Output maketest.mak test_*.cpp

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
		-@if not exist $(YUKONDEBUG) md $(YUKONDEBUG)
                -copy bin\*.pdb $(YUKONDEBUG)


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
	$(CC) $(CCOPTS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################

#UPDATE#
test_dnp_application.obj:	dnp_application.h dnp_objects.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h yukon.h ctidbgmem.h \
		critical_section.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h ctitime.h collectable.h loggable.h prot_base.h \
		xfer.h dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_configuration.h boost_test_helpers.h millisecond_timer.h \
		ctidate.h
test_dnp_datalink.obj:	dnp_datalink_packet.h dlldefs.h \
		boost_test_helpers.h millisecond_timer.h ctitime.h ctidate.h
test_dnp_objects.obj:	dnp_object_internalindications.h dnp_objects.h \
		dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h ctitime.h collectable.h loggable.h \
		prot_base.h xfer.h dnp_object_analoginput.h dnp_object_time.h \
		dnp_object_analogoutput.h dnp_object_binaryinput.h \
		dnp_object_binaryoutput.h dnp_object_counter.h
test_dnp_transport.obj:	dnp_transport.h dnp_datalink.h xfer.h \
		dlldefs.h yukon.h types.h ctidbgmem.h dnp_datalink_packet.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h constants.h numstr.h module_util.h version.h
test_prot_dnp.obj:	prot_dnp.h dlldefs.h pointtypes.h prot_base.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h message.h \
		ctitime.h collectable.h loggable.h xfer.h packet_finder.h \
		dnp_application.h dnp_objects.h dllbase.h os2_2w32.h \
		cticalls.h critical_section.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h utility.h queues.h constants.h \
		numstr.h module_util.h version.h boost_test_helpers.h \
		millisecond_timer.h ctidate.h
test_prot_e2edatatransfer.obj:	prot_e2eDataTransfer.h dlldefs.h \
		boost_test_helpers.h millisecond_timer.h ctitime.h ctidate.h
test_prot_expresscom.obj:	expresscom.h cmdparse.h ctitokenizer.h \
		dlldefs.h parsevalue.h dllbase.h os2_2w32.h types.h \
		cticalls.h yukon.h ctidbgmem.h critical_section.h ctitime.h
test_prot_klondike.obj:	prot_klondike.h prot_wrap.h prot_base.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h dlldefs.h pointdefs.h \
		pointtypes.h message.h ctitime.h collectable.h loggable.h \
		xfer.h prot_idlc.h dsm2.h streamConnection.h netports.h \
		timing_util.h immutable.h critical_section.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h boost_test_helpers.h \
		millisecond_timer.h ctidate.h
test_prot_sa305.obj:	prot_sa305.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dllbase.h os2_2w32.h types.h cticalls.h yukon.h \
		ctidbgmem.h critical_section.h dsm2.h streamConnection.h \
		netports.h timing_util.h loggable.h immutable.h guard.h \
		utility.h ctitime.h queues.h constants.h numstr.h \
		module_util.h version.h logger.h streamBuffer.h string_util.h \
		exception_helper.h boostutil.h mutex.h dsm2err.h words.h \
		optional.h macro_offset.h pointtypes.h devicetypes.h
#ENDUPDATE#

