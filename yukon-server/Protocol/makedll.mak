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
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)


IONOBJS=\
ion_datastream.obj \
ion_net_application.obj \
ion_net_network.obj \
ion_net_datalink.obj \
ion_value.obj \
ion_value_fixed.obj \
ion_value_fixed_char.obj \
ion_value_fixed_float.obj \
ion_value_fixed_intsigned.obj \
ion_value_fixed_intunsigned.obj \
ion_value_fixed_time.obj \
ion_value_struct.obj \
ion_value_struct_types.obj \
ion_value_structarray.obj \
ion_value_variable.obj \
ion_value_variable_boolean.obj \
ion_value_variable_program.obj \
ion_value_variable_fixedarray.obj

DNPOBJS=\
dnp_application.obj \
dnp_transport.obj \
dnp_datalink.obj \
dnp_objects.obj \
dnp_object_analoginput.obj \
dnp_object_analogoutput.obj \
dnp_object_binaryinput.obj \
dnp_object_binaryoutput.obj \
dnp_object_class.obj \
dnp_object_counter.obj \
dnp_object_time.obj \

ANSIOBJS=\
ansi_application.obj \
ansi_datalink.obj \
std_ansi_tbl_base.obj \
ansi_billing_table.obj \
std_ansi_tbl_00.obj \
std_ansi_tbl_01.obj \
std_ansi_tbl_08.obj \
std_ansi_tbl_10.obj \
std_ansi_tbl_11.obj \
std_ansi_tbl_12.obj \
std_ansi_tbl_13.obj \
std_ansi_tbl_14.obj \
std_ansi_tbl_15.obj \
std_ansi_tbl_16.obj \
std_ansi_tbl_21.obj \
std_ansi_tbl_22.obj \
std_ansi_tbl_23.obj \
std_ansi_tbl_25.obj \
std_ansi_tbl_27.obj \
std_ansi_tbl_28.obj \
std_ansi_tbl_31.obj \
std_ansi_tbl_32.obj \
std_ansi_tbl_33.obj \
std_ansi_tbl_51.obj \
std_ansi_tbl_52.obj \
std_ansi_tbl_61.obj \
std_ansi_tbl_62.obj \
std_ansi_tbl_63.obj \
std_ansi_tbl_64.obj \
ansi_kv2_mtable_000.obj \
ansi_kv2_mtable_070.obj \
ansi_kv2_mtable_110.obj \

TRANSDATAOBJS=\
transdata_application.obj \
transdata_tracker.obj \
transdata_datalink.obj \
transdata_data.obj \

OBJS=\
expresscom.obj \
prot_emetcon.obj \
prot_versacom.obj \
prot_711.obj \
prot_fpcbc.obj \
prot_sa3rdparty.obj \
prot_sa305.obj \
prot_sixnet.obj \
prot_base.obj \
prot_dnp.obj \
prot_modbus.obj \
$(DNPOBJS) \
prot_ion.obj \
$(IONOBJS) \
prot_seriesv.obj \
prot_lmi.obj \
prot_idlc.obj \
prot_klondike.obj \
prot_ansi_kv2.obj \
prot_ansi_sentinel.obj \
prot_ansi.obj \
$(ANSIOBJS) \
prot_transdata.obj \
$(TRANSDATAOBJS) \
prot_ymodem.obj \
dll_prot.obj \


PROTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\saprotocol.lib \
$(COMPILEBASE)\lib\ctimsg.lib


CTIPROGS=\
saprotocol.dll \
ctiprot.dll


ALL:           $(CTIPROGS)

ctiprot.dll:   $(OBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(OBJS) id_ctiprot.obj -link $(RWLIBS) $(PROTLIBS) $(BOOSTLIBS) $(COMPILEBASE)\lib\clrdump.lib $(LINKFLAGS)
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @echo:
                @echo Done building Target $@
                @echo:
                @%cd $(CWD)



saprotocol.dll:
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist 3rdparty\saprotocol.lib copy 3rdparty\saprotocol.lib $(COMPILEBASE)\lib
                -if exist 3rdparty\saprotocol.dll copy 3rdparty\saprotocol.dll bin
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist $@ copy $@ $(YUKONOUTPUT)
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
$(BIN)\*.pdb \
$(BIN)\*.pch \
$(BIN)\*.ilk \
$(BIN)\*.exp \
$(BIN)\*.lib \
$(BIN)\*.dll \
$(BIN)\*.exe

# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_ctiprot.obj

id_ctiprot.obj:    id_ctiprot.cpp include\id_ctiprot.h id_vinfo.h



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_PROT -Fo$(OBJ)\ -c $<


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
