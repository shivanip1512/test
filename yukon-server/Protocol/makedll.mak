include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(PROT)\include \
-I$(COMMON)\include \
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
ion_net_application.obj \
ion_net_network.obj \
ion_net_datalink.obj \
ion_rootclasses.obj \
ion_valuearraytypes.obj \
ion_value_basic_array.obj \
ion_value_basic_boolean.obj \
ion_value_basic_char.obj \
ion_value_basic_float.obj \
ion_value_basic_intsigned.obj \
ion_value_basic_intunsigned.obj \
ion_value_basic_program.obj \
ion_value_basic_time.obj \
ion_value_datastream.obj \
ion_value_statement.obj \
ion_valuestructtypes.obj \

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
prot_ansi.obj \
ansi_application.obj \
ansi_datalink.obj \
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
dll_prot.obj \


PROTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
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
		guard.h ctitypes.h types.h
ansi_datalink.obj:	ansi_datalink.h xfer.h dsm2.h mutex.h dlldefs.h \
		guard.h dialup.h yukon.h ctidbgmem.h dllbase.h os2_2w32.h \
		types.h cticalls.h logger.h thread.h
dll_prot.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h dll_prot.h \
		utility.h
dnp_application.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		numstr.h dnp_application.h message.h ctidbgmem.h \
		collectable.h dnp_objects.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h pointtypes.h dnp_transport.h dnp_datalink.h xfer.h \
		dialup.h
dnp_datalink.obj:	logger.h thread.h mutex.h dlldefs.h guard.h porter.h \
		dsm2.h dsm2err.h devicetypes.h queues.h types.h \
		dnp_datalink.h xfer.h dialup.h yukon.h ctidbgmem.h dllbase.h \
		os2_2w32.h cticalls.h
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
		ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h
expresscom.obj:	expresscom.h cmdparse.h dlldefs.h parsevalue.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h numstr.h yukon.h ctidbgmem.h
id_ctiprot.obj:	utility.h dsm2.h mutex.h dlldefs.h guard.h \
		id_ctiprot.h id_build.h id_vinfo.h
ion_net_application.obj:	ctitypes.h guard.h dlldefs.h logger.h \
		thread.h mutex.h ion_net_application.h xfer.h dsm2.h dialup.h \
		yukon.h ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h \
		ion_net_network.h ion_net_datalink.h ion_rootclasses.h
ion_net_datalink.obj:	ctitypes.h guard.h dlldefs.h logger.h thread.h \
		mutex.h ion_net_datalink.h xfer.h dsm2.h dialup.h yukon.h \
		ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h \
		ion_rootclasses.h numstr.h
ion_net_network.obj:	ctitypes.h guard.h dlldefs.h logger.h thread.h \
		mutex.h ion_net_network.h xfer.h dsm2.h dialup.h yukon.h \
		ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h \
		ion_net_datalink.h ion_rootclasses.h
ion_rootclasses.obj:	ctidbgmem.h guard.h dlldefs.h logger.h thread.h \
		mutex.h ion_rootclasses.h ion_value_basic_array.h ctitypes.h \
		ion_value_basic_boolean.h ion_value_basic_numeric.h \
		ion_value_basic_char.h ion_value_basic_float.h \
		ion_value_basic_intsigned.h ion_value_basic_intunsigned.h \
		ion_value_basic_program.h ion_value_statement.h \
		ion_value_basic_time.h
ion_valuearraytypes.obj:	ctidbgmem.h ion_valuearraytypes.h \
		ion_value_basic_array.h ion_rootclasses.h dlldefs.h \
		ctitypes.h ion_value_basic_char.h ion_value_basic_boolean.h \
		ion_value_basic_numeric.h ion_value_basic_float.h \
		ion_value_basic_intsigned.h ion_value_basic_intunsigned.h \
		ion_valuestructtypes.h ion_value_basic_time.h logger.h \
		thread.h mutex.h guard.h
ion_valuestructtypes.obj:	ion_valuestructtypes.h ion_valuearraytypes.h \
		ion_value_basic_array.h ion_rootclasses.h dlldefs.h \
		ctitypes.h ion_value_basic_char.h ion_value_basic_boolean.h \
		ion_value_basic_numeric.h ion_value_basic_float.h \
		ion_value_basic_intsigned.h ion_value_basic_intunsigned.h \
		ion_value_basic_time.h
ion_value_basic_array.obj:	ctidbgmem.h ion_value_basic_array.h \
		ion_rootclasses.h dlldefs.h ctitypes.h ion_valuestructtypes.h \
		ion_valuearraytypes.h ion_value_basic_char.h \
		ion_value_basic_boolean.h ion_value_basic_numeric.h \
		ion_value_basic_float.h ion_value_basic_intsigned.h \
		ion_value_basic_intunsigned.h ion_value_basic_time.h logger.h \
		thread.h mutex.h guard.h
ion_value_basic_boolean.obj:	ctidbgmem.h ion_value_basic_boolean.h \
		ion_value_basic_numeric.h ion_rootclasses.h dlldefs.h \
		ctitypes.h logger.h thread.h mutex.h guard.h
ion_value_basic_char.obj:	ctidbgmem.h ion_value_basic_char.h \
		ion_rootclasses.h dlldefs.h logger.h thread.h mutex.h guard.h
ion_value_basic_float.obj:	ion_value_basic_float.h \
		ion_value_basic_numeric.h ion_rootclasses.h dlldefs.h \
		ctitypes.h logger.h thread.h mutex.h guard.h
ion_value_basic_intsigned.obj:	ctidbgmem.h logger.h thread.h mutex.h \
		dlldefs.h guard.h ion_value_basic_intsigned.h \
		ion_value_basic_numeric.h ion_rootclasses.h ctitypes.h
ion_value_basic_intunsigned.obj:	ctidbgmem.h \
		ion_value_basic_intunsigned.h ion_value_basic_numeric.h \
		ion_rootclasses.h dlldefs.h ctitypes.h logger.h thread.h \
		mutex.h guard.h
ion_value_basic_program.obj:	ctidbgmem.h ion_value_basic_program.h \
		ion_rootclasses.h dlldefs.h ion_value_statement.h logger.h \
		thread.h mutex.h guard.h
ion_value_basic_time.obj:	ctidbgmem.h ion_value_basic_time.h \
		ion_rootclasses.h dlldefs.h ctitypes.h logger.h thread.h \
		mutex.h guard.h
ion_value_datastream.obj:	ctidbgmem.h guard.h dlldefs.h logger.h \
		thread.h mutex.h ion_value_datastream.h \
		ion_value_basic_array.h ion_rootclasses.h ctitypes.h \
		ion_valuestructtypes.h ion_valuearraytypes.h \
		ion_value_basic_char.h ion_value_basic_boolean.h \
		ion_value_basic_numeric.h ion_value_basic_float.h \
		ion_value_basic_intsigned.h ion_value_basic_intunsigned.h \
		ion_value_basic_time.h
ion_value_statement.obj:	ctidbgmem.h guard.h dlldefs.h logger.h \
		thread.h mutex.h ion_rootclasses.h ion_value_statement.h \
		ion_value_basic_array.h ctitypes.h ion_value_basic_boolean.h \
		ion_value_basic_numeric.h
prot_711.obj:	cticalls.h os2_2w32.h dlldefs.h types.h prot_711.h \
		porter.h dsm2.h mutex.h guard.h dsm2err.h devicetypes.h \
		queues.h logger.h thread.h dllbase.h
prot_ansi.obj:	guard.h dlldefs.h logger.h thread.h mutex.h prot_ansi.h \
		ansi_application.h ansi_datalink.h xfer.h dsm2.h dialup.h \
		yukon.h ctidbgmem.h dllbase.h os2_2w32.h types.h cticalls.h \
		ansi_billing_table.h ctitypes.h std_ansi_tbl_zero_zero.h \
		std_ansi_tbl_zero_one.h std_ansi_tbl_one_zero.h \
		std_ansi_tbl_one_one.h std_ansi_tbl_one_two.h \
		std_ansi_tbl_one_three.h std_ansi_tbl_one_four.h \
		std_ansi_tbl_one_five.h std_ansi_tbl_one_six.h \
		std_ansi_tbl_two_one.h std_ansi_tbl_two_two.h \
		std_ansi_tbl_two_three.h std_ansi_tbl_five_two.h
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
		dnp_datalink.h dnp_object_binaryoutput.h dnp_object_class.h \
		dnp_object_analogoutput.h
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
		prot_ion.h pointtypes.h prot_base.h msg_pdata.h pointdefs.h \
		message.h ctidbgmem.h collectable.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h cticalls.h xfer.h dialup.h \
		ion_rootclasses.h ion_value_datastream.h \
		ion_value_basic_array.h ctitypes.h ion_valuestructtypes.h \
		ion_valuearraytypes.h ion_value_basic_char.h \
		ion_value_basic_boolean.h ion_value_basic_numeric.h \
		ion_value_basic_float.h ion_value_basic_intsigned.h \
		ion_value_basic_intunsigned.h ion_value_basic_time.h \
		ion_net_application.h ion_net_network.h ion_net_datalink.h \
		ion_value_basic_program.h ion_value_statement.h
prot_sixnet.obj:	guard.h dlldefs.h logger.h thread.h mutex.h \
		prot_sixnet.h cmdparse.h parsevalue.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h
prot_versacom.obj:	ctidbgmem.h cmdparse.h dlldefs.h parsevalue.h \
		prot_versacom.h dsm2.h mutex.h guard.h dllbase.h os2_2w32.h \
		types.h cticalls.h master.h msg_pcrequest.h message.h \
		collectable.h devicetypes.h logger.h thread.h yukon.h \
		utility.h
std_ansi_tbl_five_five.obj:	std_ansi_tbl_five_five.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_five_two.obj:	std_ansi_tbl_five_two.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_one_five.obj:	std_ansi_tbl_one_five.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_one_four.obj:	std_ansi_tbl_one_four.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_one_one.obj:	std_ansi_tbl_one_one.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_one_six.obj:	std_ansi_tbl_one_six.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_one_three.obj:	std_ansi_tbl_one_three.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_one_two.obj:	std_ansi_tbl_one_two.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_one_zero.obj:	std_ansi_tbl_one_zero.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_two_one.obj:	std_ansi_tbl_two_one.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_two_three.obj:	std_ansi_tbl_two_three.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_two_two.obj:	std_ansi_tbl_two_two.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_zero_one.obj:	std_ansi_tbl_zero_one.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
std_ansi_tbl_zero_zero.obj:	std_ansi_tbl_zero_zero.h dlldefs.h dsm2.h \
		mutex.h guard.h ctitypes.h types.h
#ENDUPDATE#
