include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(BOOST) \
-I$(PROT)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RW) \


.PATH.cpp = .;$(R_PROT)

.PATH.H = \
.\include \
;$(BOOST) \
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


TEST_OBJS=\
test_prot_klondike.obj \
test_prot_xml.obj \


PROTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\saprotocol.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib


ALL:            $(TEST_OBJS) Makefile

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
                mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
                -copy bin\*.exe $(YUKONOUTPUT)


# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_ctiprot.obj

id_ctiprot.obj:    id_ctiprot.cpp include\id_ctiprot.h id_vinfo.h



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj :
        @echo:
        @echo Compiling $< to
        @echo           $(OBJ)\$(@B).obj
        @echo:
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(CFLAGS) $(INCLPATHS) -Fo$(OBJ)\ -c $<

	@echo:
	@echo Creating Executable $(OBJ)\$(@B).exe
        @echo:
	$(CC) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS)  /Fe$(BIN)\$(@B).exe \
	.\obj\$(@B).obj -link /subsystem:console $(COMPILEBASE)\lib\ctibase.lib $(BOOSTLIBS) $(BOOSTTESTLIBS) $(RWLIBS) $(PROTLIBS) $(LINKFLAGS)

	-@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
	mt.exe -manifest $(BIN)\$(@B).exe.manifest -outputresource:$(BIN)\$(@B).exe;1
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.

######################################################################################

#UPDATE#
ansi_application.obj:	yukon.h precompiled.h ctidbgmem.h guard.h \
		numstr.h dlldefs.h clrdump.h logger.h thread.h mutex.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h configparms.h cparms.h \
		rwutil.h boost_time.h boostutil.h configkey.h configval.h \
		ansi_application.h dllbase.h dsm2.h cticonnect.h netports.h \
		ansi_datalink.h xfer.h
ansi_billing_table.obj:	yukon.h precompiled.h ctidbgmem.h \
		ansi_billing_table.h dlldefs.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h types.h \
		std_ansi_tbl_base.h
ansi_datalink.obj:	yukon.h precompiled.h ctidbgmem.h ansi_datalink.h \
		dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		xfer.h ansi_application.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
ansi_kv2_mtable_000.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h ansi_kv2_mtable_000.h std_ansi_tbl_base.h types.h \
		logger.h thread.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h sorted_vector.h
ansi_kv2_mtable_070.obj:	yukon.h precompiled.h ctidbgmem.h dsm2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h \
		sorted_vector.h ansi_kv2_mtable_070.h std_ansi_tbl_base.h
ansi_kv2_mtable_110.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ansi_kv2_mtable_110.h \
		std_ansi_tbl_base.h
dll_prot.obj:	yukon.h precompiled.h ctidbgmem.h dll_prot.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h
dnp_application.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h prot_dnp.h pointtypes.h prot_base.h \
		msg_pdata.h pointdefs.h message.h collectable.h rwutil.h \
		boost_time.h boostutil.h xfer.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h
dnp_datalink.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h porter.h dsm2err.h devicetypes.h prot_dnp.h \
		pointtypes.h prot_base.h msg_pdata.h pointdefs.h message.h \
		collectable.h rwutil.h boost_time.h boostutil.h xfer.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h
dnp_objects.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h dnp_objects.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h prot_base.h xfer.h dnp_object_analoginput.h \
		dnp_object_time.h dnp_object_analogoutput.h \
		dnp_object_binaryinput.h dnp_object_binaryoutput.h \
		dnp_object_class.h dnp_object_counter.h
dnp_object_analoginput.obj:	yukon.h precompiled.h ctidbgmem.h \
		dnp_object_analoginput.h dnp_objects.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		prot_base.h xfer.h dnp_object_time.h logger.h thread.h \
		CtiPCPtrQueue.h
dnp_object_analogoutput.obj:	yukon.h precompiled.h ctidbgmem.h \
		dnp_object_analogoutput.h dnp_objects.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		prot_base.h xfer.h logger.h thread.h CtiPCPtrQueue.h
dnp_object_binaryinput.obj:	yukon.h precompiled.h ctidbgmem.h \
		dnp_object_binaryinput.h dnp_objects.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		prot_base.h xfer.h dnp_object_time.h logger.h thread.h \
		CtiPCPtrQueue.h
dnp_object_binaryoutput.obj:	yukon.h precompiled.h ctidbgmem.h \
		dnp_object_binaryoutput.h dnp_objects.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		prot_base.h xfer.h logger.h thread.h CtiPCPtrQueue.h
dnp_object_class.obj:	yukon.h precompiled.h ctidbgmem.h \
		dnp_object_class.h dnp_objects.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		prot_base.h xfer.h logger.h thread.h CtiPCPtrQueue.h
dnp_object_counter.obj:	yukon.h precompiled.h ctidbgmem.h \
		dnp_object_counter.h dnp_objects.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		prot_base.h xfer.h dnp_object_time.h logger.h thread.h \
		CtiPCPtrQueue.h
dnp_object_time.obj:	yukon.h precompiled.h ctidbgmem.h \
		dnp_object_time.h dnp_objects.h dllbase.h os2_2w32.h \
		dlldefs.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h sorted_vector.h \
		prot_base.h xfer.h logger.h thread.h CtiPCPtrQueue.h cparms.h \
		configkey.h configval.h
dnp_transport.obj:	yukon.h precompiled.h ctidbgmem.h dllbase.h \
		os2_2w32.h dlldefs.h types.h cticalls.h dsm2.h mutex.h \
		guard.h numstr.h clrdump.h cticonnect.h netports.h logger.h \
		thread.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		sorted_vector.h dnp_transport.h dnp_datalink.h xfer.h \
		dnp_datalink_packet.h
expresscom.obj:	yukon.h precompiled.h ctidbgmem.h expresscom.h \
		cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h ctitime.h ctistring.h \
		rwutil.h boost_time.h boostutil.h utility.h queues.h \
		sorted_vector.h logger.h thread.h CtiPCPtrQueue.h cparms.h \
		configkey.h configval.h ctidate.h
fmu_application.obj:	yukon.h precompiled.h ctidbgmem.h guard.h \
		numstr.h dlldefs.h clrdump.h logger.h thread.h mutex.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h configparms.h cparms.h \
		rwutil.h boost_time.h boostutil.h configkey.h configval.h
fmu_datalink.obj:	yukon.h precompiled.h ctidbgmem.h guard.h numstr.h \
		dlldefs.h clrdump.h logger.h thread.h mutex.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h
id_ctiprot.obj:	yukon.h precompiled.h ctidbgmem.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h id_ctiprot.h id_vinfo.h
ion_datastream.obj:	yukon.h precompiled.h ctidbgmem.h guard.h numstr.h \
		dlldefs.h clrdump.h logger.h thread.h mutex.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h ion_datastream.h ion_value.h \
		ion_serializable.h ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable.h ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h
ion_net_application.obj:	yukon.h precompiled.h ctidbgmem.h guard.h \
		numstr.h dlldefs.h clrdump.h logger.h thread.h mutex.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ion_net_application.h \
		dllbase.h dsm2.h cticonnect.h netports.h xfer.h \
		ion_net_network.h ion_net_datalink.h ion_serializable.h
ion_net_datalink.obj:	yukon.h precompiled.h ctidbgmem.h guard.h \
		numstr.h dlldefs.h clrdump.h logger.h thread.h mutex.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ion_net_datalink.h \
		dllbase.h dsm2.h cticonnect.h netports.h xfer.h \
		ion_serializable.h
ion_net_network.obj:	yukon.h precompiled.h ctidbgmem.h guard.h \
		numstr.h dlldefs.h clrdump.h logger.h thread.h mutex.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ion_net_network.h \
		dllbase.h dsm2.h cticonnect.h netports.h xfer.h \
		ion_net_datalink.h ion_serializable.h
ion_value.obj:	yukon.h precompiled.h ctidbgmem.h guard.h numstr.h \
		dlldefs.h clrdump.h logger.h thread.h mutex.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h ion_value.h ion_serializable.h \
		ion_value_fixed.h ion_value_numeric.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_intunsigned.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_fixed.obj:	yukon.h precompiled.h ctidbgmem.h guard.h \
		numstr.h dlldefs.h clrdump.h logger.h thread.h mutex.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ion_value_fixed.h \
		ion_value.h ion_serializable.h ion_value_numeric.h \
		ion_value_fixed_char.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_fixed_time.h
ion_value_fixed_char.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ion_value_fixed_char.h \
		ion_value_fixed.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h
ion_value_fixed_float.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ion_value_fixed_float.h \
		ion_value_fixed.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h
ion_value_fixed_intsigned.obj:	yukon.h precompiled.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h \
		ion_value_fixed_intsigned.h ion_value_fixed.h ion_value.h \
		ion_serializable.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h
ion_value_fixed_intunsigned.obj:	yukon.h precompiled.h ctidbgmem.h \
		ion_value_fixed_intunsigned.h ion_value_fixed.h ion_value.h \
		dlldefs.h ion_serializable.h numstr.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h logger.h thread.h \
		mutex.h guard.h clrdump.h ctitime.h CtiPCPtrQueue.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h sorted_vector.h
ion_value_fixed_time.obj:	yukon.h precompiled.h ctidbgmem.h \
		ion_value_fixed_time.h ion_value_fixed.h ion_value.h \
		dlldefs.h ion_serializable.h numstr.h ion_value_numeric.h \
		logger.h thread.h mutex.h guard.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h
ion_value_struct.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ion_value_struct.h \
		ion_value.h ion_serializable.h ion_value_struct_types.h \
		ion_value_fixed_intunsigned.h ion_value_fixed.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_structarray.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ion_value_structarray.h \
		ion_value.h ion_serializable.h ion_value_struct_types.h \
		ion_value_struct.h ion_value_fixed_intunsigned.h \
		ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_struct_types.obj:	yukon.h precompiled.h ctidbgmem.h \
		ion_value_struct_types.h ion_value_struct.h ion_value.h \
		dlldefs.h ion_serializable.h numstr.h \
		ion_value_fixed_intunsigned.h ion_value_fixed.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_variable.obj:	yukon.h precompiled.h ctidbgmem.h guard.h \
		numstr.h dlldefs.h clrdump.h logger.h thread.h mutex.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h ion_value_variable.h \
		ion_value.h ion_serializable.h ion_value_numeric.h \
		ion_value_variable_boolean.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_variable_program.h ion_value_variable_fixedarray.h \
		ion_value_fixed_char.h ion_value_fixed.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h
ion_value_variable_boolean.obj:	yukon.h precompiled.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h \
		ion_value_variable_boolean.h ion_value_variable.h ion_value.h \
		ion_serializable.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h
ion_value_variable_fixedarray.obj:	yukon.h precompiled.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h dllbase.h \
		dsm2.h cticonnect.h netports.h \
		ion_value_variable_fixedarray.h ion_value_variable.h \
		ion_value.h ion_serializable.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_fixed.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h ion_value_fixed_intunsigned.h
ion_value_variable_program.obj:	yukon.h precompiled.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h sorted_vector.h \
		ion_value_variable_program.h ion_value_variable.h ion_value.h \
		ion_serializable.h ion_value_numeric.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
prot_711.obj:	yukon.h precompiled.h ctidbgmem.h cticalls.h os2_2w32.h \
		dlldefs.h types.h prot_711.h porter.h dsm2.h mutex.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h dsm2err.h \
		devicetypes.h queues.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h sorted_vector.h dllbase.h
prot_ansi.obj:	yukon.h precompiled.h ctidbgmem.h guard.h numstr.h \
		dlldefs.h clrdump.h configparms.h cparms.h rwutil.h ctitime.h \
		boost_time.h boostutil.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h configkey.h configval.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h pointdefs.h \
		prot_ansi.h dllbase.h dsm2.h cticonnect.h netports.h \
		ansi_application.h ansi_datalink.h xfer.h \
		ansi_billing_table.h std_ansi_tbl_base.h std_ansi_tbl_00.h \
		std_ansi_tbl_01.h std_ansi_tbl_08.h std_ansi_tbl_10.h \
		std_ansi_tbl_11.h std_ansi_tbl_12.h std_ansi_tbl_13.h \
		std_ansi_tbl_14.h std_ansi_tbl_15.h std_ansi_tbl_16.h \
		std_ansi_tbl_21.h std_ansi_tbl_22.h std_ansi_tbl_23.h \
		std_ansi_tbl_25.h std_ansi_tbl_27.h std_ansi_tbl_28.h \
		std_ansi_tbl_31.h std_ansi_tbl_32.h std_ansi_tbl_33.h \
		std_ansi_tbl_51.h std_ansi_tbl_52.h std_ansi_tbl_61.h \
		std_ansi_tbl_62.h std_ansi_tbl_63.h std_ansi_tbl_64.h \
		ctidate.h
prot_ansi_kv2.obj:	yukon.h precompiled.h ctidbgmem.h guard.h numstr.h \
		dlldefs.h clrdump.h logger.h thread.h mutex.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h prot_ansi_kv2.h prot_ansi.h dllbase.h \
		dsm2.h cticonnect.h netports.h ansi_application.h \
		ansi_datalink.h xfer.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_00.h std_ansi_tbl_01.h \
		std_ansi_tbl_08.h std_ansi_tbl_10.h std_ansi_tbl_11.h \
		std_ansi_tbl_12.h std_ansi_tbl_13.h std_ansi_tbl_14.h \
		std_ansi_tbl_15.h std_ansi_tbl_16.h std_ansi_tbl_21.h \
		std_ansi_tbl_22.h std_ansi_tbl_23.h std_ansi_tbl_25.h \
		std_ansi_tbl_27.h std_ansi_tbl_28.h std_ansi_tbl_31.h \
		std_ansi_tbl_32.h std_ansi_tbl_33.h std_ansi_tbl_51.h \
		std_ansi_tbl_52.h std_ansi_tbl_61.h std_ansi_tbl_62.h \
		std_ansi_tbl_63.h std_ansi_tbl_64.h pointdefs.h \
		ansi_kv2_mtable_000.h ansi_kv2_mtable_070.h \
		ansi_kv2_mtable_110.h
prot_ansi_sentinel.obj:	yukon.h precompiled.h ctidbgmem.h guard.h \
		numstr.h dlldefs.h clrdump.h logger.h thread.h mutex.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h prot_ansi_sentinel.h \
		prot_ansi.h dllbase.h dsm2.h cticonnect.h netports.h \
		ansi_application.h ansi_datalink.h xfer.h \
		ansi_billing_table.h std_ansi_tbl_base.h std_ansi_tbl_00.h \
		std_ansi_tbl_01.h std_ansi_tbl_08.h std_ansi_tbl_10.h \
		std_ansi_tbl_11.h std_ansi_tbl_12.h std_ansi_tbl_13.h \
		std_ansi_tbl_14.h std_ansi_tbl_15.h std_ansi_tbl_16.h \
		std_ansi_tbl_21.h std_ansi_tbl_22.h std_ansi_tbl_23.h \
		std_ansi_tbl_25.h std_ansi_tbl_27.h std_ansi_tbl_28.h \
		std_ansi_tbl_31.h std_ansi_tbl_32.h std_ansi_tbl_33.h \
		std_ansi_tbl_51.h std_ansi_tbl_52.h std_ansi_tbl_61.h \
		std_ansi_tbl_62.h std_ansi_tbl_63.h std_ansi_tbl_64.h \
		ctidate.h
prot_base.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h porter.h dsm2.h cticonnect.h \
		netports.h dsm2err.h devicetypes.h prot_base.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h collectable.h rwutil.h \
		boost_time.h boostutil.h xfer.h
prot_dnp.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h prot_dnp.h pointtypes.h prot_base.h \
		msg_pdata.h pointdefs.h message.h collectable.h rwutil.h \
		boost_time.h boostutil.h xfer.h dsm2.h cticonnect.h \
		netports.h dnp_application.h dnp_objects.h dllbase.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h dnp_object_class.h \
		dnp_object_binaryinput.h dnp_object_time.h \
		dnp_object_analoginput.h dnp_object_analogoutput.h \
		dnp_object_counter.h
prot_emetcon.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h devicetypes.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h numstr.h \
		clrdump.h cticonnect.h netports.h porter.h dsm2err.h queues.h \
		prot_emetcon.h logger.h thread.h ctitime.h CtiPCPtrQueue.h \
		utility.h sorted_vector.h
prot_fpcbc.obj:	yukon.h precompiled.h ctidbgmem.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h devicetypes.h logger.h thread.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h sorted_vector.h \
		master.h msg_pcrequest.h message.h collectable.h rwutil.h \
		boost_time.h boostutil.h prot_fpcbc.h
prot_idlc.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h prot_idlc.h dsm2.h cticonnect.h \
		netports.h xfer.h prot_wrap.h prot_base.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h collectable.h rwutil.h \
		boost_time.h boostutil.h cti_asmc.h
prot_ion.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h porter.h dsm2.h cticonnect.h \
		netports.h dsm2err.h devicetypes.h cparms.h rwutil.h \
		boost_time.h boostutil.h configkey.h configval.h msg_signal.h \
		message.h collectable.h prot_ion.h dllbase.h pointtypes.h \
		prot_base.h msg_pdata.h pointdefs.h xfer.h ion_datastream.h \
		ion_value.h ion_serializable.h ion_value_fixed.h \
		ion_value_numeric.h ion_value_variable.h \
		ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h \
		ion_value_variable_program.h
prot_klondike.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h prot_klondike.h \
		prot_wrap.h prot_base.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h collectable.h rwutil.h boost_time.h boostutil.h \
		xfer.h dsm2.h cticonnect.h netports.h prot_idlc.h rte_ccu.h \
		rte_xcu.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		counter.h dev_exclusion.h tbl_paoexclusion.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h dllbase.h tbl_pao_lite.h \
		tbl_rtcomm.h dbaccess.h sema.h resolvers.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h pt_dyn_base.h \
		tbl_pt_base.h tbl_pt_property.h tbl_pt_trigger.h \
		msg_pcrequest.h smartmap.h readers_writer_lock.h \
		critical_section.h tbl_rtcarrier.h tbl_rtrepeater.h \
		fifo_multiset.h
prot_lmi.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h porter.h dsm2.h cticonnect.h \
		netports.h dsm2err.h devicetypes.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h boost_time.h \
		boostutil.h prot_lmi.h dllbase.h prot_seriesv.h prot_base.h \
		xfer.h verification_objects.h prot_sa3rdparty.h cmdparse.h \
		ctitokenizer.h parsevalue.h protocol_sa.h cparms.h \
		configkey.h configval.h ctidate.h
prot_modbus.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h prot_modbus.h pointtypes.h \
		prot_base.h msg_pdata.h pointdefs.h message.h collectable.h \
		rwutil.h boost_time.h boostutil.h xfer.h dsm2.h cticonnect.h \
		netports.h
prot_sa105.obj:	yukon.h precompiled.h ctidbgmem.h prot_sa105.h \
		cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		pointtypes.h prot_base.h msg_pdata.h pointdefs.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h \
		sorted_vector.h xfer.h
prot_sa205.obj:	yukon.h precompiled.h ctidbgmem.h prot_sa205.h \
		cmdparse.h ctitokenizer.h dlldefs.h parsevalue.h dsm2.h \
		mutex.h guard.h numstr.h clrdump.h cticonnect.h netports.h \
		pointtypes.h prot_base.h msg_pdata.h pointdefs.h message.h \
		collectable.h rwutil.h ctitime.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h \
		sorted_vector.h xfer.h
prot_sa305.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h configkey.h configval.h devicetypes.h \
		logger.h thread.h mutex.h guard.h clrdump.h CtiPCPtrQueue.h \
		prot_sa305.h cmdparse.h ctitokenizer.h parsevalue.h dllbase.h \
		dsm2.h cticonnect.h netports.h pointtypes.h
prot_sa3rdparty.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h \
		rwutil.h ctitime.h dlldefs.h boost_time.h boostutil.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h configkey.h configval.h logger.h thread.h \
		mutex.h guard.h clrdump.h CtiPCPtrQueue.h prot_sa3rdparty.h \
		cmdparse.h ctitokenizer.h parsevalue.h dllbase.h dsm2.h \
		cticonnect.h netports.h pointtypes.h prot_base.h msg_pdata.h \
		pointdefs.h message.h collectable.h xfer.h protocol_sa.h
prot_sasimple.obj:	yukon.h precompiled.h ctidbgmem.h
prot_seriesv.obj:	yukon.h precompiled.h ctidbgmem.h prot_seriesv.h \
		dlldefs.h prot_base.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h collectable.h rwutil.h ctitime.h boost_time.h \
		boostutil.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h sorted_vector.h xfer.h dsm2.h mutex.h guard.h \
		clrdump.h cticonnect.h netports.h logger.h thread.h \
		CtiPCPtrQueue.h porter.h dsm2err.h devicetypes.h
prot_sixnet.obj:	yukon.h precompiled.h ctidbgmem.h guard.h numstr.h \
		dlldefs.h clrdump.h mutex.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h prot_sixnet.h cmdparse.h \
		ctitokenizer.h parsevalue.h dllbase.h dsm2.h cticonnect.h \
		netports.h
prot_transdata.obj:	yukon.h precompiled.h ctidbgmem.h guard.h numstr.h \
		dlldefs.h clrdump.h logger.h thread.h mutex.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h prot_transdata.h \
		transdata_application.h xfer.h dsm2.h cticonnect.h netports.h \
		transdata_tracker.h transdata_datalink.h prot_ymodem.h \
		dllbase.h ctidate.h transdata_data.h
prot_versacom.obj:	yukon.h precompiled.h ctidbgmem.h cparms.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h boostutil.h utility.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h configkey.h configval.h cmdparse.h \
		ctitokenizer.h parsevalue.h prot_versacom.h dsm2.h mutex.h \
		guard.h clrdump.h cticonnect.h netports.h dllbase.h master.h \
		msg_pcrequest.h message.h collectable.h devicetypes.h \
		logger.h thread.h CtiPCPtrQueue.h
prot_xml.obj:	yukon.h precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h numstr.h clrdump.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h porter.h dsm2.h cticonnect.h \
		netports.h dsm2err.h devicetypes.h prot_xml.h xfer.h \
		xml_object.h boostutil.h prot_base.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h boost_time.h \
		cmdparse.h ctitokenizer.h parsevalue.h
prot_ymodem.obj:	yukon.h precompiled.h ctidbgmem.h guard.h numstr.h \
		dlldefs.h clrdump.h logger.h thread.h mutex.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h sorted_vector.h prot_ymodem.h xfer.h dsm2.h \
		cticonnect.h netports.h dllbase.h
std_ansi_tbl_00.obj:	yukon.h precompiled.h ctidbgmem.h \
		std_ansi_tbl_00.h dlldefs.h types.h std_ansi_tbl_base.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h sorted_vector.h
std_ansi_tbl_01.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_01.h \
		std_ansi_tbl_base.h
std_ansi_tbl_08.obj:	yukon.h precompiled.h ctidbgmem.h \
		std_ansi_tbl_08.h dlldefs.h types.h std_ansi_tbl_base.h \
		logger.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h sorted_vector.h
std_ansi_tbl_10.obj:	yukon.h precompiled.h ctidbgmem.h \
		std_ansi_tbl_10.h dlldefs.h types.h std_ansi_tbl_base.h
std_ansi_tbl_11.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_11.h \
		std_ansi_tbl_base.h
std_ansi_tbl_12.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_12.h \
		std_ansi_tbl_base.h
std_ansi_tbl_13.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_13.h \
		std_ansi_tbl_base.h
std_ansi_tbl_14.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_14.h \
		std_ansi_tbl_base.h
std_ansi_tbl_15.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_15.h \
		std_ansi_tbl_base.h
std_ansi_tbl_16.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_16.h \
		std_ansi_tbl_base.h
std_ansi_tbl_21.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_21.h \
		std_ansi_tbl_base.h
std_ansi_tbl_22.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_22.h \
		std_ansi_tbl_base.h
std_ansi_tbl_23.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_23.h \
		std_ansi_tbl_base.h
std_ansi_tbl_25.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_25.h \
		std_ansi_tbl_base.h std_ansi_tbl_23.h
std_ansi_tbl_27.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_27.h \
		std_ansi_tbl_base.h
std_ansi_tbl_28.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_28.h \
		std_ansi_tbl_base.h
std_ansi_tbl_31.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_31.h \
		std_ansi_tbl_base.h
std_ansi_tbl_32.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_32.h \
		std_ansi_tbl_base.h
std_ansi_tbl_33.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_33.h \
		std_ansi_tbl_base.h
std_ansi_tbl_51.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_51.h \
		std_ansi_tbl_base.h
std_ansi_tbl_52.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_52.h \
		std_ansi_tbl_base.h ctidate.h
std_ansi_tbl_55.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h
std_ansi_tbl_61.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_61.h \
		std_ansi_tbl_base.h
std_ansi_tbl_62.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_62.h \
		std_ansi_tbl_base.h std_ansi_tbl_61.h
std_ansi_tbl_63.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_63.h \
		std_ansi_tbl_base.h
std_ansi_tbl_64.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_64.h \
		std_ansi_tbl_base.h
std_ansi_tbl_91.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_91.h \
		std_ansi_tbl_base.h
std_ansi_tbl_92.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_92.h \
		std_ansi_tbl_base.h
std_ansi_tbl_base.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h std_ansi_tbl_base.h \
		ctidate.h
test_prot_klondike.obj:	boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		sorted_vector.h prot_klondike.h prot_wrap.h prot_base.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h ctidbgmem.h \
		collectable.h rwutil.h yukon.h precompiled.h boost_time.h \
		xfer.h dsm2.h mutex.h guard.h clrdump.h cticonnect.h \
		netports.h prot_idlc.h rte_ccu.h rte_xcu.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h counter.h \
		dev_exclusion.h tbl_paoexclusion.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h dllbase.h tbl_pao_lite.h tbl_rtcomm.h \
		dbaccess.h sema.h resolvers.h db_entry_defines.h desolvers.h \
		logger.h thread.h CtiPCPtrQueue.h msg_signal.h tbl_base.h \
		tbl_2way.h tbl_stats.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h pt_dyn_base.h tbl_pt_base.h tbl_pt_property.h \
		tbl_pt_trigger.h msg_pcrequest.h smartmap.h \
		readers_writer_lock.h critical_section.h tbl_rtcarrier.h \
		tbl_rtrepeater.h fifo_multiset.h dev_ccu721.h dev_remote.h \
		dev_single.h msg_pcreturn.h msg_multi.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h tbl_dialup.h \
		tbl_direct.h tbl_dv_address.h
test_prot_xml.obj:	yukon.h precompiled.h ctidbgmem.h prot_xml.h DSM2.h \
		mutex.h dlldefs.h guard.h numstr.h clrdump.h cticonnect.h \
		netports.h xfer.h xml_object.h boostutil.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		sorted_vector.h prot_base.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h boost_time.h
transdata_application.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h transdata_application.h \
		xfer.h dsm2.h cticonnect.h netports.h transdata_tracker.h \
		transdata_datalink.h prot_ymodem.h dllbase.h ctidate.h \
		transdata_data.h
transdata_data.obj:	yukon.h precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h numstr.h clrdump.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h transdata_data.h xfer.h \
		dsm2.h cticonnect.h netports.h ctidate.h
transdata_datalink.obj:	yukon.h precompiled.h ctidbgmem.h guard.h \
		numstr.h dlldefs.h clrdump.h logger.h thread.h mutex.h \
		ctitime.h CtiPCPtrQueue.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h sorted_vector.h transdata_datalink.h \
		xfer.h dsm2.h cticonnect.h netports.h rwutil.h boost_time.h \
		boostutil.h
transdata_tracker.obj:	yukon.h precompiled.h ctidbgmem.h \
		transdata_tracker.h xfer.h dsm2.h mutex.h dlldefs.h guard.h \
		numstr.h clrdump.h cticonnect.h netports.h \
		transdata_datalink.h prot_ymodem.h dllbase.h os2_2w32.h \
		types.h cticalls.h ctidate.h logger.h thread.h ctitime.h \
		CtiPCPtrQueue.h utility.h queues.h sorted_vector.h
#ENDUPDATE#

