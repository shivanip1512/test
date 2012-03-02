include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

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
-I$(XERCES)\include \
-I$(SQLAPI)\include \


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
;$(RW) \
;$(XERCES)\include



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
dnp_object_internalindications.obj \
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
ansi_focus_mtable_004.obj \
ansi_focus_mtable_013.obj \

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
prot_ansi_focus.obj \
prot_ansi.obj \
$(ANSIOBJS) \
prot_transdata.obj \
$(TRANSDATAOBJS) \
prot_ymodem.obj \
prot_gpuff.obj \
dll_prot.obj \


PROTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\saprotocol.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(XERCES_LIBS)

CTIPROGS=\
saprotocol.dll \
ctiprot.dll


PROTOCOL_FULLBUILD = $[Filename,$(OBJ),ProtocolFullBuild,target]


ALL:           $(CTIPROGS)


$(PROTOCOL_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) /D_DLL_PROT -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(OBJS)]



ctiprot.dll:   $(PROTOCOL_FULLBUILD) $(OBJS) Makefile
                @build -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(OBJS) id_ctiprot.obj -link $(RWLIBS) $(PROTLIBS) $(BOOST_LIBS) $(LINKFLAGS)
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
            @build -nologo -f $(_InputFile) id_ctiprot.obj

id_ctiprot.obj:    id_ctiprot.cpp include\id_ctiprot.h



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_PROT -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
ansi_application.obj:	precompiled.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h configparms.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		ansi_application.h ansi_datalink.h xfer.h prot_ansi.h \
		ansi_billing_table.h std_ansi_tbl_base.h std_ansi_tbl_00.h \
		std_ansi_tbl_01.h std_ansi_tbl_08.h std_ansi_tbl_10.h \
		std_ansi_tbl_11.h std_ansi_tbl_12.h std_ansi_tbl_13.h \
		std_ansi_tbl_14.h std_ansi_tbl_15.h std_ansi_tbl_16.h \
		std_ansi_tbl_21.h std_ansi_tbl_22.h std_ansi_tbl_23.h \
		std_ansi_tbl_25.h std_ansi_tbl_27.h std_ansi_tbl_28.h \
		std_ansi_tbl_31.h std_ansi_tbl_32.h std_ansi_tbl_33.h \
		std_ansi_tbl_51.h std_ansi_tbl_52.h std_ansi_tbl_61.h \
		std_ansi_tbl_62.h std_ansi_tbl_63.h std_ansi_tbl_64.h
ansi_billing_table.obj:	precompiled.h ansi_billing_table.h dlldefs.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		std_ansi_tbl_base.h
ansi_datalink.obj:	precompiled.h prot_ansi.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
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
		logger.h thread.h CtiPCPtrQueue.h
ansi_kv2_mtable_000.obj:	precompiled.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h ansi_kv2_mtable_000.h \
		std_ansi_tbl_base.h logger.h thread.h CtiPCPtrQueue.h
ansi_kv2_mtable_070.obj:	precompiled.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h ansi_kv2_mtable_070.h std_ansi_tbl_base.h
ansi_kv2_mtable_110.obj:	precompiled.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		ansi_kv2_mtable_110.h std_ansi_tbl_base.h
dll_prot.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h
dnp_application.obj:	precompiled.h dnp_application.h dnp_objects.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h prot_base.h xfer.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h prot_dnp.h packet_finder.h \
		dnp_object_binaryoutput.h logger.h thread.h CtiPCPtrQueue.h
dnp_datalink.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h porter.h devicetypes.h prot_dnp.h \
		pointtypes.h prot_base.h msg_pdata.h pointdefs.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h xfer.h packet_finder.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h
dnp_objects.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h dnp_objects.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h prot_base.h xfer.h \
		dnp_object_analoginput.h dnp_object_time.h \
		dnp_object_analogoutput.h dnp_object_binaryinput.h \
		dnp_object_binaryoutput.h dnp_object_internalindications.h \
		dnp_object_class.h dnp_object_counter.h
dnp_object_analoginput.obj:	precompiled.h dnp_object_analoginput.h \
		dnp_objects.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h prot_base.h xfer.h dnp_object_time.h logger.h \
		thread.h CtiPCPtrQueue.h
dnp_object_analogoutput.obj:	precompiled.h dnp_object_analogoutput.h \
		dnp_objects.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h prot_base.h xfer.h logger.h thread.h \
		CtiPCPtrQueue.h
dnp_object_binaryinput.obj:	precompiled.h dnp_object_binaryinput.h \
		dnp_objects.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h prot_base.h xfer.h dnp_object_time.h logger.h \
		thread.h CtiPCPtrQueue.h
dnp_object_binaryoutput.obj:	precompiled.h dnp_object_binaryoutput.h \
		dnp_objects.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h prot_base.h xfer.h logger.h thread.h \
		CtiPCPtrQueue.h
dnp_object_class.obj:	precompiled.h dnp_object_class.h dnp_objects.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h prot_base.h xfer.h logger.h thread.h \
		CtiPCPtrQueue.h
dnp_object_counter.obj:	precompiled.h dnp_object_counter.h \
		dnp_objects.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h msg_pdata.h pointdefs.h pointtypes.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h prot_base.h xfer.h dnp_object_time.h logger.h \
		thread.h CtiPCPtrQueue.h
dnp_object_internalindications.obj:	precompiled.h \
		dnp_object_internalindications.h dnp_objects.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		msg_pdata.h pointdefs.h pointtypes.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		prot_base.h xfer.h logger.h thread.h CtiPCPtrQueue.h cparms.h \
		configkey.h configval.h
dnp_object_time.obj:	precompiled.h dnp_object_time.h dnp_objects.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h prot_base.h xfer.h logger.h thread.h \
		CtiPCPtrQueue.h cparms.h configkey.h configval.h
dnp_transport.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h dnp_transport.h dnp_datalink.h xfer.h \
		dnp_datalink_packet.h
expresscom.obj:	precompiled.h expresscom.h cmdparse.h ctitokenizer.h \
		dlldefs.h parsevalue.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h ctistring.h logger.h thread.h \
		CtiPCPtrQueue.h cparms.h rwutil.h database_connection.h \
		dbaccess.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h ctidate.h
fmu_application.obj:	precompiled.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h configparms.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h
fmu_datalink.obj:	precompiled.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h logger.h \
		thread.h mutex.h CtiPCPtrQueue.h
id_ctiprot.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h id_ctiprot.h
ion_datastream.obj:	precompiled.h ctidbgmem.h guard.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h logger.h thread.h mutex.h CtiPCPtrQueue.h \
		ion_datastream.h ion_value.h ion_serializable.h \
		ion_value_fixed.h ion_value_numeric.h ion_value_variable.h \
		ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h string_utility.h \
		ion_value_struct.h ion_value_structarray.h \
		ion_value_struct_types.h ion_value_fixed_time.h
ion_net_application.obj:	precompiled.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h \
		ion_net_application.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h xfer.h \
		ion_net_network.h ion_net_datalink.h ion_serializable.h
ion_net_datalink.obj:	precompiled.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h ion_net_datalink.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		dsm2err.h words.h optional.h xfer.h ion_serializable.h
ion_net_network.obj:	precompiled.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h ion_net_network.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		dsm2err.h words.h optional.h xfer.h ion_net_datalink.h \
		ion_serializable.h
ion_value.obj:	precompiled.h ctidbgmem.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h ion_value.h \
		ion_serializable.h ion_value_fixed.h ion_value_numeric.h \
		ion_value_struct.h ion_value_structarray.h \
		ion_value_struct_types.h ion_value_fixed_intunsigned.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h string_utility.h
ion_value_fixed.obj:	precompiled.h ctidbgmem.h guard.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h logger.h thread.h mutex.h CtiPCPtrQueue.h \
		ion_value_fixed.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_fixed_char.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_fixed_time.h
ion_value_fixed_char.obj:	precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		ion_value_fixed_char.h ion_value_fixed.h ion_value.h \
		ion_serializable.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h
ion_value_fixed_float.obj:	precompiled.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		ion_value_fixed_float.h ion_value_fixed.h ion_value.h \
		ion_serializable.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h
ion_value_fixed_intsigned.obj:	precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h ion_value_fixed_intsigned.h ion_value_fixed.h \
		ion_value.h ion_serializable.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h
ion_value_fixed_intunsigned.obj:	precompiled.h ctidbgmem.h \
		ion_value_fixed_intunsigned.h ion_value_fixed.h ion_value.h \
		dlldefs.h ion_serializable.h numstr.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h CtiPCPtrQueue.h
ion_value_fixed_time.obj:	precompiled.h ctidbgmem.h \
		ion_value_fixed_time.h ion_value_fixed.h ion_value.h \
		dlldefs.h ion_serializable.h numstr.h ion_value_numeric.h \
		logger.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h CtiPCPtrQueue.h
ion_value_struct.obj:	precompiled.h ctidbgmem.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		ion_value_struct.h ion_value.h ion_serializable.h \
		ion_value_struct_types.h ion_value_fixed_intunsigned.h \
		ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h string_utility.h
ion_value_structarray.obj:	precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h ion_value_structarray.h ion_value.h \
		ion_serializable.h ion_value_struct_types.h \
		ion_value_struct.h ion_value_fixed_intunsigned.h \
		ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h string_utility.h
ion_value_struct_types.obj:	precompiled.h ion_value_struct_types.h \
		ion_value_struct.h ion_value.h dlldefs.h ion_serializable.h \
		numstr.h ion_value_fixed_intunsigned.h ion_value_fixed.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h \
		ion_value_fixed_time.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value_fixed_char.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h string_utility.h
ion_value_variable.obj:	precompiled.h ctidbgmem.h guard.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h logger.h thread.h mutex.h CtiPCPtrQueue.h \
		ion_value_variable.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_variable_boolean.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_variable_program.h ion_value_variable_fixedarray.h \
		ion_value_fixed_char.h ion_value_fixed.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h string_utility.h
ion_value_variable_boolean.obj:	precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h ion_value_variable_boolean.h \
		ion_value_variable.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h
ion_value_variable_fixedarray.obj:	precompiled.h logger.h dlldefs.h \
		thread.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		dsm2err.h words.h optional.h ion_value_variable_fixedarray.h \
		ion_value_variable.h ion_value.h ion_serializable.h \
		ion_value_numeric.h ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_fixed.h \
		ion_value_variable_boolean.h ion_value_fixed_float.h \
		ion_value_fixed_intsigned.h ion_value_fixed_intunsigned.h \
		string_utility.h
ion_value_variable_program.obj:	precompiled.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h ion_value_variable_program.h \
		ion_value_variable.h ion_value.h ion_serializable.h \
		ion_value_numeric.h
prot_711.obj:	precompiled.h cticalls.h os2_2w32.h dlldefs.h types.h \
		prot_711.h porter.h dsm2.h cticonnect.h yukon.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		numstr.h dsm2err.h words.h optional.h devicetypes.h logger.h \
		thread.h CtiPCPtrQueue.h dllbase.h
prot_ansi.obj:	precompiled.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h configparms.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h pointdefs.h prot_ansi.h \
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
prot_ansi_focus.obj:	precompiled.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h prot_ansi_focus.h \
		prot_ansi.h dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h \
		netports.h dsm2err.h words.h optional.h ansi_application.h \
		ansi_datalink.h xfer.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_00.h std_ansi_tbl_01.h \
		std_ansi_tbl_08.h std_ansi_tbl_10.h std_ansi_tbl_11.h \
		std_ansi_tbl_12.h std_ansi_tbl_13.h std_ansi_tbl_14.h \
		std_ansi_tbl_15.h std_ansi_tbl_16.h std_ansi_tbl_21.h \
		std_ansi_tbl_22.h std_ansi_tbl_23.h std_ansi_tbl_25.h \
		std_ansi_tbl_27.h std_ansi_tbl_28.h std_ansi_tbl_31.h \
		std_ansi_tbl_32.h std_ansi_tbl_33.h std_ansi_tbl_51.h \
		std_ansi_tbl_52.h std_ansi_tbl_61.h std_ansi_tbl_62.h \
		std_ansi_tbl_63.h std_ansi_tbl_64.h ctidate.h
prot_ansi_kv2.obj:	precompiled.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h logger.h \
		thread.h mutex.h CtiPCPtrQueue.h prot_ansi_kv2.h prot_ansi.h \
		dllbase.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		dsm2err.h words.h optional.h ansi_application.h \
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
prot_ansi_sentinel.obj:	precompiled.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h \
		prot_ansi_sentinel.h prot_ansi.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h ansi_application.h ansi_datalink.h xfer.h \
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
prot_base.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h porter.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		devicetypes.h prot_base.h msg_pdata.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		xfer.h
prot_dnp.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h prot_dnp.h pointtypes.h \
		prot_base.h msg_pdata.h yukon.h ctidbgmem.h pointdefs.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h xfer.h packet_finder.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h dnp_object_class.h \
		dnp_object_binaryinput.h dnp_object_time.h \
		dnp_object_analoginput.h dnp_object_analogoutput.h \
		dnp_object_counter.h
prot_emetcon.obj:	precompiled.h prot_emetcon.h dlldefs.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h porter.h devicetypes.h
prot_fpcbc.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h devicetypes.h logger.h thread.h CtiPCPtrQueue.h \
		master.h msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h prot_fpcbc.h
prot_gpuff.obj:	precompiled.h prot_gpuff.h dlldefs.h msg_pdata.h \
		yukon.h types.h ctidbgmem.h pointdefs.h pointtypes.h \
		message.h ctitime.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h packet_finder.h pt_base.h dbmemobject.h \
		resolvers.h db_entry_defines.h tbl_pt_base.h desolvers.h \
		tbl_pt_property.h tbl_pt_trigger.h cparms.h configkey.h \
		configval.h logger.h thread.h CtiPCPtrQueue.h
prot_idlc.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h prot_idlc.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h xfer.h prot_wrap.h prot_base.h msg_pdata.h \
		pointdefs.h pointtypes.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		cti_asmc.h
prot_ion.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h porter.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		devicetypes.h cparms.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h msg_signal.h \
		message.h collectable.h prot_ion.h pointtypes.h prot_base.h \
		msg_pdata.h pointdefs.h xfer.h ion_datastream.h ion_value.h \
		ion_serializable.h ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable.h ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h string_utility.h \
		ion_value_struct.h ion_value_structarray.h \
		ion_value_struct_types.h ion_value_fixed_time.h \
		ion_net_application.h ion_net_network.h ion_net_datalink.h \
		ion_value_variable_program.h
prot_klondike.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h prot_klondike.h prot_wrap.h \
		prot_base.h msg_pdata.h yukon.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		xfer.h prot_idlc.h critical_section.h
prot_lmi.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h porter.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		devicetypes.h msg_pdata.h pointdefs.h pointtypes.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h prot_lmi.h prot_seriesv.h prot_base.h xfer.h \
		verification_objects.h prot_sa3rdparty.h cmdparse.h \
		ctitokenizer.h parsevalue.h protocol_sa.h cparms.h \
		configkey.h configval.h ctidate.h
prot_modbus.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h prot_modbus.h pointtypes.h \
		prot_base.h msg_pdata.h yukon.h ctidbgmem.h pointdefs.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h xfer.h
prot_sa105.obj:	precompiled.h prot_sa105.h cmdparse.h ctitokenizer.h \
		dlldefs.h parsevalue.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h pointtypes.h prot_base.h msg_pdata.h pointdefs.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h xfer.h
prot_sa205.obj:	precompiled.h prot_sa205.h cmdparse.h ctitokenizer.h \
		dlldefs.h parsevalue.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h pointtypes.h prot_base.h msg_pdata.h pointdefs.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h dllbase.h sema.h database_reader.h row_reader.h \
		boost_time.h boostutil.h xfer.h
prot_sa305.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h prot_sa305.h \
		cmdparse.h ctitokenizer.h parsevalue.h pointtypes.h
prot_sa3rdparty.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h prot_sa3rdparty.h \
		cmdparse.h ctitokenizer.h parsevalue.h pointtypes.h \
		prot_base.h msg_pdata.h pointdefs.h message.h collectable.h \
		xfer.h protocol_sa.h
prot_sasimple.obj:	precompiled.h
prot_seriesv.obj:	precompiled.h prot_seriesv.h dlldefs.h prot_base.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h pointdefs.h \
		pointtypes.h message.h ctitime.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h xfer.h logger.h thread.h CtiPCPtrQueue.h porter.h \
		devicetypes.h
prot_sixnet.obj:	precompiled.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h mutex.h \
		logger.h thread.h CtiPCPtrQueue.h prot_sixnet.h cmdparse.h \
		ctitokenizer.h parsevalue.h dllbase.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h dsm2err.h words.h optional.h
prot_transdata.obj:	precompiled.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h prot_transdata.h \
		transdata_application.h xfer.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		transdata_tracker.h transdata_datalink.h prot_ymodem.h \
		dllbase.h ctidate.h transdata_data.h
prot_versacom.obj:	precompiled.h ctidbgmem.h cparms.h dlldefs.h \
		rwutil.h yukon.h types.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		cmdparse.h ctitokenizer.h parsevalue.h prot_versacom.h \
		master.h msg_pcrequest.h message.h collectable.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h
prot_ymodem.obj:	precompiled.h guard.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h logger.h \
		thread.h mutex.h CtiPCPtrQueue.h prot_ymodem.h xfer.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h dllbase.h
std_ansi_tbl_00.obj:	precompiled.h std_ansi_tbl_00.h dlldefs.h types.h \
		std_ansi_tbl_base.h logger.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
std_ansi_tbl_01.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_01.h \
		std_ansi_tbl_base.h
std_ansi_tbl_08.obj:	precompiled.h std_ansi_tbl_08.h dlldefs.h types.h \
		std_ansi_tbl_base.h logger.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h
std_ansi_tbl_10.obj:	precompiled.h std_ansi_tbl_10.h dlldefs.h types.h \
		std_ansi_tbl_base.h
std_ansi_tbl_11.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_11.h \
		std_ansi_tbl_base.h
std_ansi_tbl_12.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_12.h \
		std_ansi_tbl_base.h
std_ansi_tbl_13.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_13.h \
		std_ansi_tbl_base.h
std_ansi_tbl_14.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_14.h \
		std_ansi_tbl_base.h
std_ansi_tbl_15.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_15.h \
		std_ansi_tbl_base.h
std_ansi_tbl_16.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_16.h \
		std_ansi_tbl_base.h
std_ansi_tbl_21.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_21.h \
		std_ansi_tbl_base.h
std_ansi_tbl_22.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_22.h \
		std_ansi_tbl_base.h
std_ansi_tbl_23.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h ctidate.h std_ansi_tbl_23.h \
		std_ansi_tbl_base.h
std_ansi_tbl_25.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_25.h \
		std_ansi_tbl_base.h std_ansi_tbl_23.h
std_ansi_tbl_27.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_27.h \
		std_ansi_tbl_base.h
std_ansi_tbl_28.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_28.h \
		std_ansi_tbl_base.h
std_ansi_tbl_31.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_31.h \
		std_ansi_tbl_base.h
std_ansi_tbl_32.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_32.h \
		std_ansi_tbl_base.h
std_ansi_tbl_33.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_33.h \
		std_ansi_tbl_base.h
std_ansi_tbl_51.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_51.h \
		std_ansi_tbl_base.h
std_ansi_tbl_52.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_52.h \
		std_ansi_tbl_base.h ctidate.h
std_ansi_tbl_55.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h
std_ansi_tbl_61.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_61.h \
		std_ansi_tbl_base.h
std_ansi_tbl_62.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_62.h \
		std_ansi_tbl_base.h std_ansi_tbl_61.h
std_ansi_tbl_63.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_63.h \
		std_ansi_tbl_base.h
std_ansi_tbl_64.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_64.h \
		std_ansi_tbl_base.h
std_ansi_tbl_91.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_91.h \
		std_ansi_tbl_base.h
std_ansi_tbl_92.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h std_ansi_tbl_92.h \
		std_ansi_tbl_base.h
std_ansi_tbl_base.obj:	precompiled.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		std_ansi_tbl_base.h ctidate.h
test_dnp_transport.obj:	precompiled.h dnp_transport.h dnp_datalink.h \
		xfer.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dnp_datalink_packet.h boostutil.h
test_prot_dnp.obj:	precompiled.h prot_dnp.h dlldefs.h pointtypes.h \
		prot_base.h msg_pdata.h yukon.h types.h ctidbgmem.h \
		pointdefs.h message.h ctitime.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h xfer.h packet_finder.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h \
		dnp_object_internalindications.h boost_test_helpers.h
test_prot_expresscom.obj:	precompiled.h expresscom.h cmdparse.h \
		ctitokenizer.h dlldefs.h parsevalue.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h ctistring.h
test_prot_klondike.obj:	prot_klondike.h prot_wrap.h prot_base.h \
		msg_pdata.h yukon.h types.h ctidbgmem.h dlldefs.h pointdefs.h \
		pointtypes.h message.h ctitime.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		sema.h database_reader.h row_reader.h boost_time.h \
		boostutil.h xfer.h prot_idlc.h critical_section.h \
		boost_test_helpers.h
transdata_application.obj:	precompiled.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		transdata_application.h xfer.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		transdata_tracker.h transdata_datalink.h prot_ymodem.h \
		dllbase.h ctidate.h transdata_data.h
transdata_data.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h transdata_data.h xfer.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h \
		words.h optional.h ctidate.h
transdata_datalink.obj:	precompiled.h guard.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h CtiPCPtrQueue.h \
		transdata_datalink.h xfer.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h
transdata_tracker.obj:	precompiled.h transdata_tracker.h xfer.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		transdata_datalink.h prot_ymodem.h dllbase.h ctidate.h \
		logger.h thread.h CtiPCPtrQueue.h
#ENDUPDATE#
