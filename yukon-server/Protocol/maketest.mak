include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(PROT)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RW) \
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
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)


PROTOCOL_TEST_OBJS=\
test_main.obj \
test_prot_klondike.obj \
test_prot_expresscom.obj \
test_prot_dnp.obj \
test_dnp_transport.obj \
test_prot_sa305.obj

PROTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\saprotocol.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib


SQLAPILIB=$(SQLAPI)\lib\$(SQLAPI_LIB).lib

LIBS=\
kernel32.lib user32.lib advapi32.lib wsock32.lib

PROTOCOL_TEST_FULLBUILD = $[Filename,$(OBJ),ProtocolTestFullBuild,target]


ALL:            test_protocol.exe

$(PROTOCOL_TEST_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(PARALLEL) /FI precompiled.h $(PCHFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(PROTOCOL_TEST_OBJS)]

test_protocol.exe:    $(PROTOCOL_TEST_FULLBUILD) $(PROTOCOL_TEST_OBJS)  Makefile
        @echo:
	@echo Creating Executable $(BIN)\$(_TargetF)
        @echo:
	@%cd $(OBJ)
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe..\$(BIN)\$(_TargetF) \
        $(PROTOCOL_TEST_OBJS) -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOST_LIBS) $(BOOST_TEST_LIBS) $(RWLIBS) $(PROTLIBS) $(LINKFLAGS)
	@%cd ..

        -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(_TargetF).manifest -outputresource:$(BIN)\$(_TargetF);1
        -copy $(BIN)\$(_TargetF) $(YUKONOUTPUT)
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


########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

######################################################################################

#UPDATE#
test_dnp_transport.obj:	dnp_transport.h dnp_datalink.h xfer.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		dnp_datalink_packet.h boostutil.h
test_prot_dnp.obj:	prot_dnp.h dlldefs.h pointtypes.h prot_base.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h message.h \
		ctitime.h collectable.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h xfer.h packet_finder.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h dnp_object_internalindications.h \
		boost_test_helpers.h
test_prot_expresscom.obj:	expresscom.h cmdparse.h ctitokenizer.h \
		dlldefs.h parsevalue.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h ctistring.h
test_prot_klondike.obj:	prot_klondike.h prot_wrap.h prot_base.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h dlldefs.h pointdefs.h \
		pointtypes.h message.h ctitime.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		xfer.h prot_idlc.h critical_section.h boost_test_helpers.h
test_prot_sa305.obj:	prot_sa305.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h pointtypes.h devicetypes.h
#ENDUPDATE#

