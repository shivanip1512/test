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
	-copy $(BIN)\$(@B).exe $(YUKONOUTPUT)
	-@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
	-if exist $(BIN)\$(@B).lib copy $(BIN)\$(@B).lib $(COMPILEBASE)\lib
	@%cd $(CWD)
	@echo.

######################################################################################

#UPDATE#
ansi_application.obj:	ansi_application.h ansi_datalink.h
ansi_billing_table.obj:	ansi_billing_table.h std_ansi_tbl_base.h
ansi_datalink.obj:	ansi_datalink.h ansi_application.h
ansi_kv2_mtable_000.obj:	ansi_kv2_mtable_000.h std_ansi_tbl_base.h
ansi_kv2_mtable_070.obj:	ansi_kv2_mtable_070.h std_ansi_tbl_base.h
ansi_kv2_mtable_110.obj:	ansi_kv2_mtable_110.h std_ansi_tbl_base.h
dll_prot.obj:	dll_prot.h
dnp_application.obj:	dnp_application.h dnp_objects.h prot_base.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h
dnp_datalink.obj:	prot_dnp.h prot_base.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h
dnp_objects.obj:	dnp_objects.h prot_base.h dnp_object_analoginput.h \
		dnp_object_time.h dnp_object_analogoutput.h \
		dnp_object_binaryinput.h dnp_object_binaryoutput.h \
		dnp_object_class.h dnp_object_counter.h
dnp_object_analoginput.obj:	dnp_object_analoginput.h dnp_objects.h \
		prot_base.h dnp_object_time.h
dnp_object_analogoutput.obj:	dnp_object_analogoutput.h dnp_objects.h \
		prot_base.h
dnp_object_binaryinput.obj:	dnp_object_binaryinput.h dnp_objects.h \
		prot_base.h dnp_object_time.h
dnp_object_binaryoutput.obj:	dnp_object_binaryoutput.h dnp_objects.h \
		prot_base.h
dnp_object_class.obj:	dnp_object_class.h dnp_objects.h prot_base.h
dnp_object_counter.obj:	dnp_object_counter.h dnp_objects.h prot_base.h \
		dnp_object_time.h
dnp_object_time.obj:	dnp_object_time.h dnp_objects.h prot_base.h
dnp_transport.obj:	dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h
expresscom.obj:	expresscom.h
id_ctiprot.obj:	id_ctiprot.h
ion_datastream.obj:	ion_datastream.h ion_value.h ion_serializable.h \
		ion_value_fixed.h ion_value_numeric.h ion_value_variable.h \
		ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h
ion_net_application.obj:	ion_net_application.h ion_net_network.h \
		ion_net_datalink.h ion_serializable.h
ion_net_datalink.obj:	ion_net_datalink.h ion_serializable.h
ion_net_network.obj:	ion_net_network.h ion_net_datalink.h \
		ion_serializable.h
ion_value.obj:	ion_value.h ion_serializable.h ion_value_fixed.h \
		ion_value_numeric.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_intunsigned.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_fixed.obj:	ion_value_fixed.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_fixed_char.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_fixed_time.h
ion_value_fixed_char.obj:	ion_value_fixed_char.h ion_value_fixed.h \
		ion_value.h ion_serializable.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h
ion_value_fixed_float.obj:	ion_value_fixed_float.h ion_value_fixed.h \
		ion_value.h ion_serializable.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h
ion_value_fixed_intsigned.obj:	ion_value_fixed_intsigned.h \
		ion_value_fixed.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h
ion_value_fixed_intunsigned.obj:	ion_value_fixed_intunsigned.h \
		ion_value_fixed.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h
ion_value_fixed_time.obj:	ion_value_fixed_time.h ion_value_fixed.h \
		ion_value.h ion_serializable.h ion_value_numeric.h
ion_value_struct.obj:	ion_value_struct.h ion_value.h \
		ion_serializable.h ion_value_struct_types.h \
		ion_value_fixed_intunsigned.h ion_value_fixed.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_structarray.obj:	ion_value_structarray.h ion_value.h \
		ion_serializable.h ion_value_struct_types.h \
		ion_value_struct.h ion_value_fixed_intunsigned.h \
		ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_struct_types.obj:	ion_value_struct_types.h \
		ion_value_struct.h ion_value.h ion_serializable.h \
		ion_value_fixed_intunsigned.h ion_value_fixed.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_variable.obj:	ion_value_variable.h ion_value.h \
		ion_serializable.h ion_value_numeric.h \
		ion_value_variable_boolean.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_variable_program.h ion_value_variable_fixedarray.h \
		ion_value_fixed_char.h ion_value_fixed.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h
ion_value_variable_boolean.obj:	ion_value_variable_boolean.h \
		ion_value_variable.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h
ion_value_variable_fixedarray.obj:	ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_fixed.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h ion_value_fixed_intunsigned.h
ion_value_variable_program.obj:	ion_value_variable_program.h \
		ion_value_variable.h ion_value.h ion_serializable.h \
		ion_value_numeric.h
prot_711.obj:	prot_711.h
prot_ansi.obj:	prot_ansi.h ansi_application.h ansi_datalink.h \
		ansi_billing_table.h std_ansi_tbl_base.h std_ansi_tbl_00.h \
		std_ansi_tbl_01.h std_ansi_tbl_08.h std_ansi_tbl_10.h \
		std_ansi_tbl_11.h std_ansi_tbl_12.h std_ansi_tbl_13.h \
		std_ansi_tbl_14.h std_ansi_tbl_15.h std_ansi_tbl_16.h \
		std_ansi_tbl_21.h std_ansi_tbl_22.h std_ansi_tbl_23.h \
		std_ansi_tbl_25.h std_ansi_tbl_27.h std_ansi_tbl_28.h \
		std_ansi_tbl_31.h std_ansi_tbl_32.h std_ansi_tbl_33.h \
		std_ansi_tbl_51.h std_ansi_tbl_52.h std_ansi_tbl_61.h \
		std_ansi_tbl_62.h std_ansi_tbl_63.h std_ansi_tbl_64.h
prot_ansi_kv2.obj:	prot_ansi_kv2.h prot_ansi.h ansi_application.h \
		ansi_datalink.h ansi_billing_table.h std_ansi_tbl_base.h \
		std_ansi_tbl_00.h std_ansi_tbl_01.h std_ansi_tbl_08.h \
		std_ansi_tbl_10.h std_ansi_tbl_11.h std_ansi_tbl_12.h \
		std_ansi_tbl_13.h std_ansi_tbl_14.h std_ansi_tbl_15.h \
		std_ansi_tbl_16.h std_ansi_tbl_21.h std_ansi_tbl_22.h \
		std_ansi_tbl_23.h std_ansi_tbl_25.h std_ansi_tbl_27.h \
		std_ansi_tbl_28.h std_ansi_tbl_31.h std_ansi_tbl_32.h \
		std_ansi_tbl_33.h std_ansi_tbl_51.h std_ansi_tbl_52.h \
		std_ansi_tbl_61.h std_ansi_tbl_62.h std_ansi_tbl_63.h \
		std_ansi_tbl_64.h ansi_kv2_mtable_000.h ansi_kv2_mtable_070.h \
		ansi_kv2_mtable_110.h
prot_ansi_sentinel.obj:	prot_ansi_sentinel.h prot_ansi.h \
		ansi_application.h ansi_datalink.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_00.h std_ansi_tbl_01.h \
		std_ansi_tbl_08.h std_ansi_tbl_10.h std_ansi_tbl_11.h \
		std_ansi_tbl_12.h std_ansi_tbl_13.h std_ansi_tbl_14.h \
		std_ansi_tbl_15.h std_ansi_tbl_16.h std_ansi_tbl_21.h \
		std_ansi_tbl_22.h std_ansi_tbl_23.h std_ansi_tbl_25.h \
		std_ansi_tbl_27.h std_ansi_tbl_28.h std_ansi_tbl_31.h \
		std_ansi_tbl_32.h std_ansi_tbl_33.h std_ansi_tbl_51.h \
		std_ansi_tbl_52.h std_ansi_tbl_61.h std_ansi_tbl_62.h \
		std_ansi_tbl_63.h std_ansi_tbl_64.h
prot_base.obj:	prot_base.h
prot_dnp.obj:	prot_dnp.h prot_base.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h dnp_object_class.h \
		dnp_object_binaryinput.h dnp_object_time.h \
		dnp_object_analoginput.h dnp_object_analogoutput.h \
		dnp_object_counter.h
prot_emetcon.obj:	prot_emetcon.h
prot_fpcbc.obj:	prot_fpcbc.h
prot_idlc.obj:	prot_idlc.h prot_wrap.h prot_base.h
prot_ion.obj:	prot_ion.h prot_base.h ion_datastream.h ion_value.h \
		ion_serializable.h ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable.h ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h \
		ion_value_variable_program.h
prot_klondike.obj:	prot_klondike.h prot_wrap.h prot_base.h prot_idlc.h
prot_lmi.obj:	prot_lmi.h prot_seriesv.h prot_base.h prot_sa3rdparty.h \
		protocol_sa.h
prot_modbus.obj:	prot_modbus.h prot_base.h
prot_sa105.obj:	prot_sa105.h prot_base.h
prot_sa205.obj:	prot_sa205.h prot_base.h
prot_sa305.obj:	prot_sa305.h
prot_sa3rdparty.obj:	prot_sa3rdparty.h prot_base.h protocol_sa.h
prot_seriesv.obj:	prot_seriesv.h prot_base.h
prot_sixnet.obj:	prot_sixnet.h
prot_transdata.obj:	prot_transdata.h transdata_application.h \
		transdata_tracker.h transdata_datalink.h prot_ymodem.h \
		transdata_data.h
prot_versacom.obj:	prot_versacom.h
prot_ymodem.obj:	prot_ymodem.h
std_ansi_tbl_00.obj:	std_ansi_tbl_00.h std_ansi_tbl_base.h
std_ansi_tbl_01.obj:	std_ansi_tbl_01.h std_ansi_tbl_base.h
std_ansi_tbl_08.obj:	std_ansi_tbl_08.h std_ansi_tbl_base.h
std_ansi_tbl_10.obj:	std_ansi_tbl_10.h std_ansi_tbl_base.h
std_ansi_tbl_11.obj:	std_ansi_tbl_11.h std_ansi_tbl_base.h
std_ansi_tbl_12.obj:	std_ansi_tbl_12.h std_ansi_tbl_base.h
std_ansi_tbl_13.obj:	std_ansi_tbl_13.h std_ansi_tbl_base.h
std_ansi_tbl_14.obj:	std_ansi_tbl_14.h std_ansi_tbl_base.h
std_ansi_tbl_15.obj:	std_ansi_tbl_15.h std_ansi_tbl_base.h
std_ansi_tbl_16.obj:	std_ansi_tbl_16.h std_ansi_tbl_base.h
std_ansi_tbl_21.obj:	std_ansi_tbl_21.h std_ansi_tbl_base.h
std_ansi_tbl_22.obj:	std_ansi_tbl_22.h std_ansi_tbl_base.h
std_ansi_tbl_23.obj:	std_ansi_tbl_23.h std_ansi_tbl_base.h
std_ansi_tbl_25.obj:	std_ansi_tbl_25.h std_ansi_tbl_base.h \
		std_ansi_tbl_23.h
std_ansi_tbl_27.obj:	std_ansi_tbl_27.h std_ansi_tbl_base.h
std_ansi_tbl_28.obj:	std_ansi_tbl_28.h std_ansi_tbl_base.h
std_ansi_tbl_31.obj:	std_ansi_tbl_31.h std_ansi_tbl_base.h
std_ansi_tbl_32.obj:	std_ansi_tbl_32.h std_ansi_tbl_base.h
std_ansi_tbl_33.obj:	std_ansi_tbl_33.h std_ansi_tbl_base.h
std_ansi_tbl_51.obj:	std_ansi_tbl_51.h std_ansi_tbl_base.h
std_ansi_tbl_52.obj:	std_ansi_tbl_52.h std_ansi_tbl_base.h
std_ansi_tbl_61.obj:	std_ansi_tbl_61.h std_ansi_tbl_base.h
std_ansi_tbl_62.obj:	std_ansi_tbl_62.h std_ansi_tbl_base.h
std_ansi_tbl_63.obj:	std_ansi_tbl_63.h std_ansi_tbl_base.h
std_ansi_tbl_64.obj:	std_ansi_tbl_64.h std_ansi_tbl_base.h
std_ansi_tbl_91.obj:	std_ansi_tbl_91.h std_ansi_tbl_base.h
std_ansi_tbl_92.obj:	std_ansi_tbl_92.h std_ansi_tbl_base.h
std_ansi_tbl_base.obj:	std_ansi_tbl_base.h
test_prot_klondike.obj:	prot_klondike.h prot_wrap.h prot_base.h \
		prot_idlc.h
transdata_application.obj:	transdata_application.h transdata_tracker.h \
		transdata_datalink.h prot_ymodem.h transdata_data.h
transdata_data.obj:	transdata_data.h
transdata_datalink.obj:	transdata_datalink.h
transdata_tracker.obj:	transdata_tracker.h transdata_datalink.h \
		prot_ymodem.h
#ENDUPDATE#

