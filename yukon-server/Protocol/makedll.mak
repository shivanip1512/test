include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(PROT)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(RW) \


.PATH.cpp = .;$(R_PROT)

.PATH.H = \
.\include \
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
std_ansi_tbl_zero_zero.obj \
std_ansi_tbl_zero_one.obj \
std_ansi_tbl_one_zero.obj \
std_ansi_tbl_one_one.obj \
std_ansi_tbl_one_two.obj \
std_ansi_tbl_one_three.obj \
std_ansi_tbl_one_four.obj \
std_ansi_tbl_one_five.obj \
std_ansi_tbl_one_six.obj \
std_ansi_tbl_two_one.obj \
std_ansi_tbl_two_two.obj \
std_ansi_tbl_two_three.obj \
std_ansi_tbl_five_two.obj \
ansi_kv2_mtable_zero.obj \
ansi_kv2_mtable_seventy.obj \

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
prot_sixnet.obj \
prot_base.obj \
prot_dnp.obj \
$(DNPOBJS) \
prot_ion.obj \
$(IONOBJS) \
prot_ansi_kv2.obj \
prot_ansi.obj \
$(ANSIOBJS) \
prot_transdata.obj \
$(TRANSDATAOBJS) \
prot_ymodem.obj \
dll_prot.obj \


PROTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctimsg.lib


CTIPROGS=\
ctiprot.dll


ALL:           $(CTIPROGS)

ctiprot.dll:   $(OBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(OBJS) id_ctiprot.obj -link $(RWLIBS) $(PROTLIBS)
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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) /D_DLL_PROT -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
ansi_application.obj:	guard.h dlldefs.h logger.h thread.h mutex.h \
		ansi_application.h ansi_datalink.h xfer.h dsm2.h dialup.h \
		yukon.h ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h
ansi_billing_table.obj:	ansi_billing_table.h dlldefs.h dsm2.h mutex.h \
		guard.h ctitypes.h types.h std_ansi_tbl_base.h
ansi_datalink.obj:	ansi_datalink.h xfer.h dsm2.h mutex.h dlldefs.h \
		guard.h dialup.h yukon.h ctidbgmem.h dllbase.h os2_2w32.h \
		types.h cticalls.h logger.h thread.h
ansi_kv2_mtable_seventy.obj:	ansi_kv2_mtable_seventy.h dlldefs.h \
		std_ansi_tbl_base.h dsm2.h mutex.h guard.h ctitypes.h types.h
ansi_kv2_mtable_zero.obj:	ansi_kv2_mtable_zero.h dlldefs.h \
		std_ansi_tbl_base.h dsm2.h mutex.h guard.h ctitypes.h types.h
dll_prot.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dll_prot.h \
		utility.h
dnp_application.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		numstr.h dnp_application.h message.h ctidbgmem.h \
		collectable.h dnp_objects.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h pointtypes.h dnp_transport.h dnp_datalink.h xfer.h \
		dialup.h dnp_datalink_packet.h
dnp_datalink.obj:	logger.h thread.h mutex.h dlldefs.h guard.h porter.h \
		dsm2.h dsm2err.h devicetypes.h queues.h types.h prot_dnp.h \
		pointtypes.h prot_base.h msg_pdata.h pointdefs.h message.h \
		ctidbgmem.h collectable.h msg_signal.h yukon.h dllbase.h \
		os2_2w32.h cticalls.h xfer.h dialup.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h
dnp_objects.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		dnp_objects.h msg_pdata.h pointdefs.h message.h ctidbgmem.h \
		collectable.h msg_signal.h yukon.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h pointtypes.h \
		dnp_object_analoginput.h dnp_object_time.h \
		dnp_object_analogoutput.h dnp_object_binaryinput.h \
		dnp_object_binaryoutput.h dnp_object_class.h \
		dnp_object_counter.h
dnp_object_analoginput.obj:	dnp_object_analoginput.h dnp_objects.h \
		msg_pdata.h dlldefs.h pointdefs.h message.h ctidbgmem.h \
		collectable.h msg_signal.h yukon.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h pointtypes.h \
		dnp_object_time.h logger.h thread.h
dnp_object_analogoutput.obj:	dnp_object_analogoutput.h dnp_objects.h \
		msg_pdata.h dlldefs.h pointdefs.h message.h ctidbgmem.h \
		collectable.h msg_signal.h yukon.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h pointtypes.h \
		logger.h thread.h
dnp_object_binaryinput.obj:	dnp_object_binaryinput.h dnp_objects.h \
		msg_pdata.h dlldefs.h pointdefs.h message.h ctidbgmem.h \
		collectable.h msg_signal.h yukon.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h pointtypes.h \
		dnp_object_time.h logger.h thread.h
dnp_object_binaryoutput.obj:	dnp_object_binaryoutput.h dnp_objects.h \
		msg_pdata.h dlldefs.h pointdefs.h message.h ctidbgmem.h \
		collectable.h msg_signal.h yukon.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h pointtypes.h \
		logger.h thread.h
dnp_object_class.obj:	dnp_object_class.h dnp_objects.h msg_pdata.h \
		dlldefs.h pointdefs.h message.h ctidbgmem.h collectable.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h pointtypes.h logger.h thread.h
dnp_object_counter.obj:	dnp_object_counter.h dnp_objects.h msg_pdata.h \
		dlldefs.h pointdefs.h message.h ctidbgmem.h collectable.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h pointtypes.h logger.h thread.h
dnp_object_time.obj:	dnp_object_time.h dnp_objects.h msg_pdata.h \
		dlldefs.h pointdefs.h message.h ctidbgmem.h collectable.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h pointtypes.h logger.h thread.h
dnp_transport.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		dnp_transport.h dnp_datalink.h dsm2.h xfer.h dialup.h yukon.h \
		ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h \
		dnp_datalink_packet.h
expresscom.obj:	cparms.h dlldefs.h expresscom.h cmdparse.h \
		parsevalue.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h numstr.h yukon.h \
		ctidbgmem.h
id_ctiprot.obj:	utility.h dsm2.h mutex.h dlldefs.h guard.h \
		id_ctiprot.h id_build.h id_vinfo.h
ion_datastream.obj:	ctidbgmem.h guard.h dlldefs.h logger.h thread.h \
		mutex.h ion_datastream.h ion_value.h ion_serializable.h \
		numstr.h ion_value_fixed.h ion_value_numeric.h ctitypes.h \
		ion_value_variable.h ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h
ion_net_application.obj:	ctitypes.h guard.h dlldefs.h logger.h \
		thread.h mutex.h ion_net_application.h xfer.h dsm2.h dialup.h \
		yukon.h ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h \
		ion_net_network.h ion_net_datalink.h ion_serializable.h
ion_net_datalink.obj:	ctitypes.h guard.h dlldefs.h logger.h thread.h \
		mutex.h ion_net_datalink.h xfer.h dsm2.h dialup.h yukon.h \
		ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h \
		ion_serializable.h numstr.h
ion_net_network.obj:	ctitypes.h guard.h dlldefs.h logger.h thread.h \
		mutex.h ion_net_network.h xfer.h dsm2.h dialup.h yukon.h \
		ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h \
		ion_net_datalink.h ion_serializable.h
ion_value.obj:	ctidbgmem.h guard.h dlldefs.h logger.h thread.h mutex.h \
		ion_value.h ion_serializable.h numstr.h ion_value_fixed.h \
		ion_value_numeric.h ctitypes.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_intunsigned.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_fixed.obj:	ctidbgmem.h guard.h dlldefs.h logger.h thread.h \
		mutex.h ion_value_fixed.h ion_value.h ion_serializable.h \
		numstr.h ion_value_numeric.h ctitypes.h \
		ion_value_fixed_char.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_fixed_time.h
ion_value_fixed_char.obj:	ctidbgmem.h logger.h thread.h mutex.h \
		dlldefs.h guard.h ion_value_fixed_char.h ion_value_fixed.h \
		ion_value.h ion_serializable.h numstr.h ion_value_numeric.h \
		ctitypes.h ion_value_variable_fixedarray_element.h
ion_value_fixed_float.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		ion_value_fixed_float.h ion_value_fixed.h ion_value.h \
		ion_serializable.h numstr.h ion_value_numeric.h ctitypes.h \
		ion_value_variable_fixedarray_element.h
ion_value_fixed_intsigned.obj:	ctidbgmem.h logger.h thread.h mutex.h \
		dlldefs.h guard.h ion_value_fixed_intsigned.h \
		ion_value_fixed.h ion_value.h ion_serializable.h numstr.h \
		ion_value_numeric.h ctitypes.h \
		ion_value_variable_fixedarray_element.h
ion_value_fixed_intunsigned.obj:	ctidbgmem.h \
		ion_value_fixed_intunsigned.h ion_value_fixed.h ion_value.h \
		dlldefs.h ion_serializable.h numstr.h ion_value_numeric.h \
		ctitypes.h ion_value_variable_fixedarray_element.h logger.h \
		thread.h mutex.h guard.h
ion_value_fixed_time.obj:	ctidbgmem.h ion_value_fixed_time.h \
		ion_value_fixed.h ion_value.h dlldefs.h ion_serializable.h \
		numstr.h ion_value_numeric.h ctitypes.h logger.h thread.h \
		mutex.h guard.h
ion_value_struct.obj:	ctidbgmem.h logger.h thread.h mutex.h dlldefs.h \
		guard.h ion_value_struct.h ion_value.h ion_serializable.h \
		numstr.h ion_value_struct_types.h \
		ion_value_fixed_intunsigned.h ion_value_fixed.h \
		ion_value_numeric.h ctitypes.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_structarray.obj:	ctidbgmem.h logger.h thread.h mutex.h \
		dlldefs.h guard.h ion_value_structarray.h ion_value.h \
		ion_serializable.h numstr.h ion_value_struct_types.h \
		ion_value_struct.h ion_value_fixed_intunsigned.h \
		ion_value_fixed.h ion_value_numeric.h ctitypes.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_struct_types.obj:	ion_value_struct_types.h \
		ion_value_struct.h ion_value.h dlldefs.h ion_serializable.h \
		numstr.h ion_value_fixed_intunsigned.h ion_value_fixed.h \
		ion_value_numeric.h ctitypes.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h
ion_value_variable.obj:	ctidbgmem.h guard.h dlldefs.h logger.h \
		thread.h mutex.h ion_value_variable.h ion_value.h \
		ion_serializable.h numstr.h ion_value_numeric.h ctitypes.h \
		ion_value_variable_boolean.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_variable_program.h ion_value_variable_fixedarray.h \
		ion_value_fixed_char.h ion_value_fixed.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h
ion_value_variable_boolean.obj:	ctidbgmem.h logger.h thread.h mutex.h \
		dlldefs.h guard.h ion_value_variable_boolean.h \
		ion_value_variable.h ion_value.h ion_serializable.h numstr.h \
		ion_value_numeric.h ctitypes.h \
		ion_value_variable_fixedarray_element.h
ion_value_variable_fixedarray.obj:	logger.h thread.h mutex.h dlldefs.h \
		guard.h ctidbgmem.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value.h ion_serializable.h numstr.h \
		ion_value_numeric.h ctitypes.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_fixed.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h ion_value_fixed_intunsigned.h
ion_value_variable_program.obj:	ctidbgmem.h logger.h thread.h mutex.h \
		dlldefs.h guard.h ion_value_variable_program.h \
		ion_value_variable.h ion_value.h ion_serializable.h numstr.h \
		ion_value_numeric.h ctitypes.h
prot_711.obj:	cticalls.h os2_2w32.h dlldefs.h types.h prot_711.h \
		porter.h dsm2.h mutex.h guard.h dsm2err.h devicetypes.h \
		queues.h logger.h thread.h dllbase.h
prot_ansi.obj:	guard.h dlldefs.h logger.h thread.h mutex.h prot_ansi.h \
		ansi_application.h ansi_datalink.h xfer.h dsm2.h dialup.h \
		yukon.h ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h \
		ansi_billing_table.h ctitypes.h std_ansi_tbl_base.h \
		std_ansi_tbl_zero_zero.h std_ansi_tbl_zero_one.h \
		std_ansi_tbl_one_zero.h std_ansi_tbl_one_one.h \
		std_ansi_tbl_one_two.h std_ansi_tbl_one_three.h \
		std_ansi_tbl_one_four.h std_ansi_tbl_one_five.h \
		std_ansi_tbl_one_six.h std_ansi_tbl_two_one.h \
		std_ansi_tbl_two_two.h std_ansi_tbl_two_three.h \
		std_ansi_tbl_five_two.h
prot_ansi_kv2.obj:	guard.h dlldefs.h logger.h thread.h mutex.h \
		prot_ansi_kv2.h prot_ansi.h ansi_application.h \
		ansi_datalink.h xfer.h dsm2.h dialup.h yukon.h ctidbgmem.h \
		dllbase.h os2_2w32.h types.h cticalls.h ansi_billing_table.h \
		ctitypes.h std_ansi_tbl_base.h std_ansi_tbl_zero_zero.h \
		std_ansi_tbl_zero_one.h std_ansi_tbl_one_zero.h \
		std_ansi_tbl_one_one.h std_ansi_tbl_one_two.h \
		std_ansi_tbl_one_three.h std_ansi_tbl_one_four.h \
		std_ansi_tbl_one_five.h std_ansi_tbl_one_six.h \
		std_ansi_tbl_two_one.h std_ansi_tbl_two_two.h \
		std_ansi_tbl_two_three.h std_ansi_tbl_five_two.h \
		ansi_kv2_mtable_zero.h ansi_kv2_mtable_seventy.h
prot_base.obj:	logger.h thread.h mutex.h dlldefs.h guard.h utility.h \
		dsm2.h porter.h dsm2err.h devicetypes.h queues.h types.h \
		prot_base.h msg_pdata.h pointdefs.h message.h ctidbgmem.h \
		collectable.h msg_signal.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h xfer.h dialup.h
prot_dnp.obj:	logger.h thread.h mutex.h dlldefs.h guard.h utility.h \
		dsm2.h porter.h dsm2err.h devicetypes.h queues.h types.h \
		prot_dnp.h pointtypes.h prot_base.h msg_pdata.h pointdefs.h \
		message.h ctidbgmem.h collectable.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h cticalls.h xfer.h dialup.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h dnp_object_class.h \
		dnp_object_analogoutput.h dnp_object_time.h
prot_emetcon.obj:	cmdparse.h dlldefs.h parsevalue.h devicetypes.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h porter.h dsm2err.h queues.h prot_emetcon.h \
		ctidbgmem.h yukon.h logger.h thread.h
prot_fpcbc.obj:	cmdparse.h dlldefs.h parsevalue.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h devicetypes.h \
		logger.h thread.h master.h msg_pcrequest.h message.h \
		ctidbgmem.h collectable.h prot_fpcbc.h utility.h yukon.h
prot_ion.obj:	logger.h thread.h mutex.h dlldefs.h guard.h utility.h \
		dsm2.h porter.h dsm2err.h devicetypes.h queues.h types.h \
		cparms.h prot_ion.h pointtypes.h prot_base.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h collectable.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h cticalls.h xfer.h dialup.h \
		ion_datastream.h ion_value.h ion_serializable.h numstr.h \
		ion_value_fixed.h ion_value_numeric.h ctitypes.h \
		ion_value_variable.h ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h \
		ion_value_variable_program.h
prot_sixnet.obj:	guard.h dlldefs.h logger.h thread.h mutex.h \
		prot_sixnet.h cmdparse.h parsevalue.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h
prot_transdata.obj:	guard.h dlldefs.h logger.h thread.h mutex.h \
		prot_transdata.h transdata_application.h xfer.h dsm2.h \
		dialup.h yukon.h ctidbgmem.h dllbase.h os2_2w32.h types.h \
		cticalls.h utility.h transdata_tracker.h numstr.h \
		transdata_datalink.h prot_ymodem.h transdata_data.h
prot_versacom.obj:	ctidbgmem.h cparms.h dlldefs.h cmdparse.h \
		parsevalue.h prot_versacom.h dsm2.h mutex.h guard.h dllbase.h \
		os2_2w32.h types.h cticalls.h master.h msg_pcrequest.h \
		message.h collectable.h devicetypes.h logger.h thread.h \
		yukon.h utility.h
prot_ymodem.obj:	guard.h dlldefs.h logger.h thread.h mutex.h \
		prot_ymodem.h xfer.h dsm2.h dialup.h yukon.h ctidbgmem.h \
		dllbase.h os2_2w32.h types.h cticalls.h
std_ansi_tbl_base.obj:	std_ansi_tbl_base.h dlldefs.h dsm2.h mutex.h \
		guard.h ctitypes.h types.h
std_ansi_tbl_five_five.obj:	std_ansi_tbl_five_five.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h
std_ansi_tbl_five_two.obj:	std_ansi_tbl_five_two.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h
std_ansi_tbl_one_five.obj:	std_ansi_tbl_one_five.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h
std_ansi_tbl_one_four.obj:	std_ansi_tbl_one_four.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h
std_ansi_tbl_one_one.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		std_ansi_tbl_one_one.h dsm2.h ctitypes.h types.h \
		std_ansi_tbl_base.h
std_ansi_tbl_one_six.obj:	std_ansi_tbl_one_six.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h
std_ansi_tbl_one_three.obj:	std_ansi_tbl_one_three.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h
std_ansi_tbl_one_two.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		std_ansi_tbl_one_two.h dsm2.h ctitypes.h types.h \
		std_ansi_tbl_base.h
std_ansi_tbl_one_zero.obj:	std_ansi_tbl_one_zero.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h
std_ansi_tbl_two_one.obj:	std_ansi_tbl_two_one.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h
std_ansi_tbl_two_three.obj:	std_ansi_tbl_two_three.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h
std_ansi_tbl_two_two.obj:	std_ansi_tbl_two_two.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h
std_ansi_tbl_zero_one.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		std_ansi_tbl_zero_one.h dsm2.h ctitypes.h types.h \
		std_ansi_tbl_base.h
std_ansi_tbl_zero_zero.obj:	std_ansi_tbl_zero_zero.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h std_ansi_tbl_base.h \
		logger.h thread.h
transdata_application.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		transdata_application.h xfer.h dsm2.h dialup.h yukon.h \
		ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h utility.h \
		transdata_tracker.h numstr.h transdata_datalink.h \
		prot_ymodem.h transdata_data.h
transdata_data.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		transdata_data.h xfer.h dsm2.h dialup.h yukon.h ctidbgmem.h \
		dllbase.h os2_2w32.h types.h cticalls.h
transdata_datalink.obj:	guard.h dlldefs.h logger.h thread.h mutex.h \
		transdata_datalink.h xfer.h dsm2.h dialup.h yukon.h \
		ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h
transdata_tracker.obj:	transdata_tracker.h xfer.h dsm2.h mutex.h \
		dlldefs.h guard.h dialup.h yukon.h ctidbgmem.h dllbase.h \
		os2_2w32.h types.h cticalls.h numstr.h transdata_datalink.h \
		prot_ymodem.h logger.h thread.h
#ENDUPDATE#
