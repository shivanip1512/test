include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(PORTER)\include \
-I$(MSG)\include \
-I$(CPARMS)\include \
-I$(PROT)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(PROT)\include \
;$(RW)



YUKONPORTDLLOBJS=\
mgr_port.obj \
port_base.obj \
port_dialable.obj \
port_dialin.obj \
port_dialout.obj \
port_direct.obj \
port_modem.obj \
port_pool_out.obj \
port_serial.obj \
port_tcpipdirect.obj \
port_tcp.obj \
port_udp.obj \
slctprt.obj \

PRTDBLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\cticparms.lib \


CTIPROGS=\
ctiprtdb.dll

RTDB_PRT_FULLBUILD = $[Filename,$(OBJ),RtdbPrtFullBuild,target]


ALL:            $(CTIPROGS)


$(RTDB_PRT_FULLBUILD) :
	@touch $@
	@echo:
	@echo Compiling cpp to obj
	$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(INCLPATHS) /D_DLL_PRTDB -Fdctiprt.pdb -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(YUKONPORTDLLOBJS)]


ctiprtdb.dll:   $(RTDB_PRT_FULLBUILD) $(YUKONPORTDLLOBJS) Makefile
                @build -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(YUKONPORTDLLOBJS) id_prtdll.obj -link $(RWLIBS) $(BOOST_LIBS) $(PRTDBLIBS) $(LINKFLAGS)
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
                scandeps -Output makeprtdb.mak *.cpp


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
            @build -nologo -f $(_InputFile) id_prtdll.obj

id_prtdll.obj:    id_prtdll.cpp include\id_prtdll.h



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) /D_DLL_PRTDB -Fdctiprt.pdb -Fo$(OBJ)\ -c $<


######################################################################################


#UPDATE#
cmd_dlc.obj:	precompiled.h cmd_dlc.h cmd_base.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		prot_emetcon.h
cmd_lcr3102_demandresponsesummary.obj:	precompiled.h \
		cmd_lcr3102_DemandResponseSummary.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_dlc.h cmd_base.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		prot_emetcon.h
cmd_lcr3102_hourlydatalog.obj:	precompiled.h \
		cmd_lcr3102_hourlyDataLog.h cmd_lcr3102.h cmd_dlc.h \
		cmd_base.h dev_single.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h database_reader.h boost_time.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h prot_emetcon.h
cmd_lcr3102_tamperread.obj:	precompiled.h cmd_lcr3102_tamperRead.h \
		cmd_lcr3102_ThreePart.h cmd_lcr3102.h cmd_dlc.h cmd_base.h \
		dev_single.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h prot_emetcon.h
cmd_lcr3102_threepart.obj:	precompiled.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_dlc.h cmd_base.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		prot_emetcon.h
cmd_mct410_hourlyread.obj:	precompiled.h cmd_mct410_hourlyRead.h \
		cmd_mct410.h cmd_dlc.h cmd_base.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		prot_emetcon.h ctidate.h dev_mct410.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h tbl_route.h tbl_carrier.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h dev_mct410_commands.h
cmd_mct420_hourlyread.obj:	precompiled.h cmd_mct420_hourlyRead.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h cmd_dlc.h cmd_base.h \
		dev_single.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h prot_emetcon.h ctidate.h \
		dev_mct420.h dev_mct410.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h tbl_route.h tbl_carrier.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h dev_mct410_commands.h
cmd_mct420_lcdconfiguration.obj:	precompiled.h \
		cmd_mct420_LcdConfiguration.h cmd_mct420.h cmd_mct410.h \
		cmd_dlc.h cmd_base.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		prot_emetcon.h
conntest.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h msg_cmd.h \
		msg_trace.h
dev_710.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dev_710.h dev_idlc.h types.h os2_2w32.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h numstr.h dsm2err.h \
		words.h optional.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h critical_section.h porter.h \
		devicetypes.h trx_711.h cti_asmc.h
dev_a1.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h devicetypes.h dev_alpha.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h dev_a1.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		msg_trace.h ctidate.h
dev_alpha.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h devicetypes.h dev_alpha.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_ansi.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h dev_ansi.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dllbase.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h prot_ansi.h \
		ansi_application.h ansi_datalink.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_00.h std_ansi_tbl_01.h \
		std_ansi_tbl_08.h std_ansi_tbl_10.h std_ansi_tbl_11.h \
		std_ansi_tbl_12.h std_ansi_tbl_13.h std_ansi_tbl_14.h \
		std_ansi_tbl_15.h std_ansi_tbl_16.h std_ansi_tbl_21.h \
		std_ansi_tbl_22.h std_ansi_tbl_23.h std_ansi_tbl_25.h \
		std_ansi_tbl_27.h std_ansi_tbl_28.h std_ansi_tbl_31.h \
		std_ansi_tbl_32.h std_ansi_tbl_33.h std_ansi_tbl_51.h \
		std_ansi_tbl_52.h std_ansi_tbl_61.h std_ansi_tbl_62.h \
		std_ansi_tbl_63.h std_ansi_tbl_64.h dllyukon.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_status.h tbl_pt_status.h ctidate.h
dev_aplus.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h devicetypes.h dev_alpha.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h dev_aplus.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_trace.h ctidate.h
dev_base.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h mgr_route.h \
		repeaterrole.h slctdev.h smartmap.h readers_writer_lock.h \
		critical_section.h mgr_point.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h porter.h devicetypes.h \
		database_writer.h row_writer.h
dev_base_lite.obj:	precompiled.h dev_base_lite.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		dbmemobject.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h
dev_carrier.obj:	precompiled.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h
dev_cbc.obj:	precompiled.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h porter.h devicetypes.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		pointdefs.h master.h mgr_route.h repeaterrole.h rte_base.h \
		boostutil.h cmdparse.h ctitokenizer.h parsevalue.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		tbl_pao_lite.h tbl_rtcomm.h logger.h thread.h CtiPCPtrQueue.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h slctdev.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h smartmap.h readers_writer_lock.h \
		critical_section.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h dev_cbc.h tbl_dv_cbc.h cparms.h configkey.h \
		configval.h
dev_cbc6510.obj:	precompiled.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h porter.h devicetypes.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		pointdefs.h pt_numeric.h boostutil.h tbl_pt_unit.h \
		tbl_unitmeasure.h master.h mgr_route.h repeaterrole.h \
		rte_base.h cmdparse.h ctitokenizer.h parsevalue.h ctibase.h \
		ctinexus.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		tbl_pao_lite.h tbl_rtcomm.h logger.h thread.h CtiPCPtrQueue.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h slctdev.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h smartmap.h readers_writer_lock.h \
		critical_section.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h dev_cbc6510.h dev_remote.h dev_single.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		dev_dnp.h prot_dnp.h packet_finder.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_object_binaryoutput.h \
		tbl_dv_address.h pointAttribute.h tbl_dv_idlcremote.h
dev_cbc7020.obj:	precompiled.h dev_cbc7020.h dev_dnp.h dev_remote.h \
		dev_single.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		prot_dnp.h packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h pointAttribute.h \
		config_data_cbc.h
dev_cbc8020.obj:	precompiled.h dev_cbc8020.h dev_cbc7020.h dev_dnp.h \
		dev_remote.h dev_single.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h database_reader.h boost_time.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		prot_dnp.h packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h pointAttribute.h \
		config_data_cbc.h pt_status.h tbl_pt_status.h
dev_ccu.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		porter.h devicetypes.h pt_base.h dbmemobject.h tbl_pt_base.h \
		row_reader.h dllbase.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h pointdefs.h master.h dev_ccu.h \
		dev_idlc.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		critical_section.h trx_711.h dev_ccu_queue_interface.h \
		device_queue_interface.h prot_711.h
dev_ccu721.obj:	precompiled.h dev_ccu721.h dev_remote.h dev_single.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_address.h device_queue_interface.h prot_klondike.h \
		prot_wrap.h prot_idlc.h critical_section.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h ctidate.h porter.h \
		devicetypes.h portdecl.h port_base.h tbl_port_base.h \
		counter.h mgr_route.h repeaterrole.h slctdev.h smartmap.h \
		readers_writer_lock.h
dev_ccu_queue_interface.obj:	precompiled.h dev_ccu_queue_interface.h \
		device_queue_interface.h dlldefs.h trx_711.h trx_info.h \
		logger.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h critical_section.h porter.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h devicetypes.h string_utility.h
dev_davis.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h connection.h exchange.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h dev_davis.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_static_paoinfo.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		xfer.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h msg_cmd.h \
		msg_lmcontrolhistory.h porter.h devicetypes.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h
dev_dct501.obj:	precompiled.h devicetypes.h tbl_ptdispatch.h ctibase.h \
		ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h dllbase.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h dbmemobject.h pointdefs.h row_reader.h \
		database_connection.h dbaccess.h dev_dct501.h dev_mct24x.h \
		dev_mct2xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		rwutil.h database_reader.h boost_time.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h porter.h
dev_dlcbase.obj:	precompiled.h dev_dlcbase.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		dev_mct.h dev_carrier.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h devicetypes.h \
		msg_cmd.h porter.h
dev_dnp.obj:	precompiled.h dev_dnp.h dev_remote.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h prot_dnp.h packet_finder.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h pointAttribute.h \
		dnp_object_analogoutput.h porter.h devicetypes.h pt_status.h \
		tbl_pt_status.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		dllyukon.h msg_cmd.h msg_lmcontrolhistory.h
dev_dr87.obj:	precompiled.h ctitime.h dlldefs.h ctidate.h logger.h \
		thread.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h porter.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h devicetypes.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_dr87.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h
dev_exclusion.obj:	precompiled.h dlldefs.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h cparms.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		dev_exclusion.h tbl_paoexclusion.h logger.h thread.h \
		CtiPCPtrQueue.h
dev_fmu.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h dev_fmu.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h verification_objects.h msg_cmd.h porter.h \
		devicetypes.h ctistring.h
dev_focus.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h dev_focus.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dllbase.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_ansi.h prot_ansi.h \
		ansi_application.h ansi_datalink.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_00.h std_ansi_tbl_01.h \
		std_ansi_tbl_08.h std_ansi_tbl_10.h std_ansi_tbl_11.h \
		std_ansi_tbl_12.h std_ansi_tbl_13.h std_ansi_tbl_14.h \
		std_ansi_tbl_15.h std_ansi_tbl_16.h std_ansi_tbl_21.h \
		std_ansi_tbl_22.h std_ansi_tbl_23.h std_ansi_tbl_25.h \
		std_ansi_tbl_27.h std_ansi_tbl_28.h std_ansi_tbl_31.h \
		std_ansi_tbl_32.h std_ansi_tbl_33.h std_ansi_tbl_51.h \
		std_ansi_tbl_52.h std_ansi_tbl_61.h std_ansi_tbl_62.h \
		std_ansi_tbl_63.h std_ansi_tbl_64.h dllyukon.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_status.h tbl_pt_status.h prot_ansi_focus.h \
		ansi_focus_mtable_004.h ansi_focus_mtable_013.h \
		ansi_focus_mtable_024.h prot_ansi_sentinel.h ctidate.h
dev_fulcrum.obj:	precompiled.h ctidate.h dlldefs.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h porter.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h devicetypes.h dev_schlum.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_fulcrum.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_gridadvisor.obj:	precompiled.h porter.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h devicetypes.h \
		tbl_ptdispatch.h ctibase.h ctinexus.h dllbase.h dbmemobject.h \
		pointdefs.h row_reader.h database_connection.h dbaccess.h \
		dev_gridadvisor.h dev_dnp.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h rwutil.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h prot_dnp.h packet_finder.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h pointAttribute.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		msg_lmcontrolhistory.h dllyukon.h
dev_grp_emetcon.obj:	precompiled.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h porter.h devicetypes.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		pointdefs.h pt_status.h boostutil.h tbl_pt_status.h master.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_route.h repeaterrole.h \
		rte_base.h cmdparse.h ctitokenizer.h parsevalue.h ctibase.h \
		ctinexus.h tbl_pao_lite.h tbl_rtcomm.h msg_signal.h \
		tbl_static_paoinfo.h slctdev.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h smartmap.h \
		readers_writer_lock.h critical_section.h msg_pcrequest.h \
		msg_pcreturn.h dev_grp_emetcon.h dev_grp.h \
		msg_lmcontrolhistory.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h tbl_dv_emetcon.h
dev_grp_expresscom.obj:	precompiled.h cmdparse.h ctitokenizer.h \
		dlldefs.h parsevalue.h dev_grp_expresscom.h dev_base.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h dev_grp.h cparms.h \
		configkey.h configval.h msg_lmcontrolhistory.h \
		msg_pcrequest.h msg_pdata.h msg_multi.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h tbl_dv_expresscom.h \
		vcomdefs.h expresscom.h ctistring.h mgr_route.h \
		repeaterrole.h slctdev.h smartmap.h readers_writer_lock.h \
		critical_section.h msg_pcreturn.h devicetypes.h
dev_grp_golay.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dev_grp_golay.h dev_base.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h dev_grp.h cparms.h \
		configkey.h configval.h msg_lmcontrolhistory.h \
		msg_pcrequest.h msg_pdata.h msg_multi.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h prot_sasimple.h prot_base.h \
		xfer.h tbl_lmg_sasimple.h expresscom.h ctistring.h \
		mgr_route.h repeaterrole.h slctdev.h smartmap.h \
		readers_writer_lock.h critical_section.h msg_pcreturn.h
dev_grp_mct.obj:	precompiled.h dev_grp_mct.h dev_grp.h cparms.h \
		dlldefs.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h msg_lmcontrolhistory.h \
		pointdefs.h message.h collectable.h msg_pcrequest.h \
		msg_signal.h msg_pdata.h pointtypes.h msg_multi.h pt_status.h \
		pt_base.h dbmemobject.h tbl_pt_base.h resolvers.h \
		db_entry_defines.h desolvers.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h \
		tbl_static_paoinfo.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h tbl_dv_lmgmct.h msg_pcreturn.h porter.h \
		devicetypes.h prot_emetcon.h
dev_grp_point.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h dev_grp_point.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		dev_grp.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_lmg_point.h mgr_route.h repeaterrole.h slctdev.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		msg_pcreturn.h
dev_grp_ripple.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		dev_grp_ripple.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h dev_grp.h \
		msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		tbl_dv_lmg_ripple.h mgr_route.h repeaterrole.h slctdev.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		msg_pcreturn.h msg_cmd.h porter.h devicetypes.h
dev_grp_sa105.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h dev_grp_sa105.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		dev_grp.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		prot_sa105.h prot_base.h xfer.h tbl_lmg_sa205105.h \
		expresscom.h ctistring.h mgr_route.h repeaterrole.h slctdev.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		msg_pcreturn.h
dev_grp_sa205.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_grp_sa205.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		dev_grp.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		prot_sa205.h prot_base.h xfer.h tbl_lmg_sa205105.h \
		expresscom.h ctistring.h mgr_route.h repeaterrole.h slctdev.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		msg_pcreturn.h prot_sa3rdparty.h protocol_sa.h
dev_grp_sa305.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_grp_sa305.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		dev_grp.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		prot_sa305.h tbl_lmg_sa305.h expresscom.h ctistring.h \
		mgr_route.h repeaterrole.h slctdev.h smartmap.h \
		readers_writer_lock.h critical_section.h msg_pcreturn.h
dev_grp_sadigital.obj:	precompiled.h cmdparse.h ctitokenizer.h \
		dlldefs.h parsevalue.h dev_grp_sadigital.h dev_base.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h dev_grp.h cparms.h \
		configkey.h configval.h msg_lmcontrolhistory.h \
		msg_pcrequest.h msg_pdata.h msg_multi.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h prot_sasimple.h prot_base.h \
		xfer.h tbl_lmg_sasimple.h expresscom.h ctistring.h \
		mgr_route.h repeaterrole.h slctdev.h smartmap.h \
		readers_writer_lock.h critical_section.h msg_pcreturn.h
dev_grp_versacom.obj:	precompiled.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h porter.h devicetypes.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		pointdefs.h pt_status.h boostutil.h tbl_pt_status.h master.h \
		connection.h exchange.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h mgr_route.h repeaterrole.h \
		rte_base.h cmdparse.h ctitokenizer.h parsevalue.h ctibase.h \
		ctinexus.h tbl_pao_lite.h tbl_rtcomm.h msg_signal.h \
		tbl_static_paoinfo.h slctdev.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h smartmap.h \
		readers_writer_lock.h critical_section.h msg_pcrequest.h \
		msg_pcreturn.h dev_grp_versacom.h dev_grp.h \
		msg_lmcontrolhistory.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h tbl_dv_versacom.h \
		vcomdefs.h
dev_ilex.obj:	precompiled.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h porter.h devicetypes.h dev_ilex.h \
		dev_idlc.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		critical_section.h trx_711.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_lmcontrolhistory.h
dev_ion.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h devicetypes.h tbl_ptdispatch.h \
		ctibase.h ctinexus.h dllbase.h dbmemobject.h pointdefs.h \
		row_reader.h database_connection.h dbaccess.h dev_ion.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h resolvers.h \
		pointtypes.h db_entry_defines.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		rwutil.h database_reader.h boost_time.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h tbl_dv_address.h \
		prot_ion.h ion_datastream.h ion_value.h ion_serializable.h \
		ion_value_fixed.h ion_value_numeric.h ion_value_variable.h \
		ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h msg_cmd.h msg_lmcontrolhistory.h \
		dllyukon.h
dev_ipc410al.obj:	precompiled.h dev_ipc410al.h dlldefs.h dev_focus.h \
		dev_meter.h tbl_metergrp.h yukon.h types.h ctidbgmem.h \
		vcomdefs.h dbmemobject.h dllbase.h dsm2.h cticonnect.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h dev_ansi.h prot_ansi.h ansi_application.h \
		ansi_datalink.h ansi_billing_table.h std_ansi_tbl_base.h \
		std_ansi_tbl_00.h std_ansi_tbl_01.h std_ansi_tbl_08.h \
		std_ansi_tbl_10.h std_ansi_tbl_11.h std_ansi_tbl_12.h \
		std_ansi_tbl_13.h std_ansi_tbl_14.h std_ansi_tbl_15.h \
		std_ansi_tbl_16.h std_ansi_tbl_21.h std_ansi_tbl_22.h \
		std_ansi_tbl_23.h std_ansi_tbl_25.h std_ansi_tbl_27.h \
		std_ansi_tbl_28.h std_ansi_tbl_31.h std_ansi_tbl_32.h \
		std_ansi_tbl_33.h std_ansi_tbl_51.h std_ansi_tbl_52.h \
		std_ansi_tbl_61.h std_ansi_tbl_62.h std_ansi_tbl_63.h \
		std_ansi_tbl_64.h dllyukon.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h pt_status.h \
		tbl_pt_status.h prot_ansi_focus.h ansi_focus_mtable_004.h \
		ansi_focus_mtable_013.h ansi_focus_mtable_024.h \
		prot_ansi_sentinel.h
dev_ipc420ad.obj:	precompiled.h dev_ipc420ad.h dlldefs.h dev_focus.h \
		dev_meter.h tbl_metergrp.h yukon.h types.h ctidbgmem.h \
		vcomdefs.h dbmemobject.h dllbase.h dsm2.h cticonnect.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h dev_ansi.h prot_ansi.h ansi_application.h \
		ansi_datalink.h ansi_billing_table.h std_ansi_tbl_base.h \
		std_ansi_tbl_00.h std_ansi_tbl_01.h std_ansi_tbl_08.h \
		std_ansi_tbl_10.h std_ansi_tbl_11.h std_ansi_tbl_12.h \
		std_ansi_tbl_13.h std_ansi_tbl_14.h std_ansi_tbl_15.h \
		std_ansi_tbl_16.h std_ansi_tbl_21.h std_ansi_tbl_22.h \
		std_ansi_tbl_23.h std_ansi_tbl_25.h std_ansi_tbl_27.h \
		std_ansi_tbl_28.h std_ansi_tbl_31.h std_ansi_tbl_32.h \
		std_ansi_tbl_33.h std_ansi_tbl_51.h std_ansi_tbl_52.h \
		std_ansi_tbl_61.h std_ansi_tbl_62.h std_ansi_tbl_63.h \
		std_ansi_tbl_64.h dllyukon.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h pt_status.h \
		tbl_pt_status.h prot_ansi_focus.h ansi_focus_mtable_004.h \
		ansi_focus_mtable_013.h ansi_focus_mtable_024.h \
		prot_ansi_sentinel.h
dev_kv2.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h dev_kv2.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dllbase.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h prot_ansi_kv2.h \
		prot_ansi.h ansi_application.h ansi_datalink.h \
		ansi_billing_table.h std_ansi_tbl_base.h std_ansi_tbl_00.h \
		std_ansi_tbl_01.h std_ansi_tbl_08.h std_ansi_tbl_10.h \
		std_ansi_tbl_11.h std_ansi_tbl_12.h std_ansi_tbl_13.h \
		std_ansi_tbl_14.h std_ansi_tbl_15.h std_ansi_tbl_16.h \
		std_ansi_tbl_21.h std_ansi_tbl_22.h std_ansi_tbl_23.h \
		std_ansi_tbl_25.h std_ansi_tbl_27.h std_ansi_tbl_28.h \
		std_ansi_tbl_31.h std_ansi_tbl_32.h std_ansi_tbl_33.h \
		std_ansi_tbl_51.h std_ansi_tbl_52.h std_ansi_tbl_61.h \
		std_ansi_tbl_62.h std_ansi_tbl_63.h std_ansi_tbl_64.h \
		ansi_kv2_mtable_000.h ansi_kv2_mtable_070.h \
		ansi_kv2_mtable_110.h dllyukon.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h pt_status.h \
		tbl_pt_status.h
dev_lcr3102.obj:	precompiled.h dev_lcr3102.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h dev_lcr3102_commands.h \
		cmd_lcr3102_tamperRead.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_lcr3102_DemandResponseSummary.h \
		cmd_lcr3102_hourlyDataLog.h ctidate.h date_utility.h
dev_lcu.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h connection.h exchange.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_lcu.h dev_idlc.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_static_paoinfo.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		xfer.h tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h \
		trx_info.h critical_section.h porter.h devicetypes.h \
		trx_711.h master.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		elogger.h
dev_lgs4.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h devicetypes.h dev_lgs4.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		ctidate.h
dev_lmi.obj:	precompiled.h dev_lmi.h dev_remote.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_address.h tbl_dv_seriesv.h \
		prot_lmi.h prot_seriesv.h verification_objects.h porter.h \
		devicetypes.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		pt_status.h tbl_pt_status.h dllyukon.h
dev_macro.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h devicetypes.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h database_reader.h boost_time.h boostutil.h \
		msg_pcrequest.h msg_pdata.h pointdefs.h pointtypes.h \
		dev_macro.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h db_entry_defines.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		dev_grp.h cparms.h configkey.h configval.h \
		msg_lmcontrolhistory.h msg_multi.h pt_status.h \
		tbl_pt_status.h pt_analog.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_analog.h dev_grp_ripple.h \
		tbl_dv_lmg_ripple.h
dev_mark_v.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h porter.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h dev_mark_v.h \
		prot_transdata.h transdata_application.h xfer.h \
		transdata_tracker.h transdata_datalink.h prot_ymodem.h \
		ctidate.h transdata_data.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_base.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h msg_cmd.h
dev_mct.obj:	precompiled.h numstr.h dlldefs.h devicetypes.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h dsm2err.h \
		words.h optional.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h database_reader.h boost_time.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h dev_mct210.h \
		dev_mct2xx.h dev_mct31x.h dev_mct310.h dev_mct410.h \
		dev_mct4xx.h config_data_mct.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		dev_mct470.h dev_mct_lmt2.h dev_mct22x.h msg_cmd.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h porter.h dllyukon.h
dev_mct210.obj:	precompiled.h devicetypes.h dev_mct210.h dev_mct2xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h porter.h
dev_mct22x.obj:	precompiled.h devicetypes.h dev_mct22X.h dev_mct2xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h porter.h
dev_mct24x.obj:	precompiled.h devicetypes.h dev_mct24X.h dev_mct2xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h porter.h dllyukon.h
dev_mct2xx.obj:	precompiled.h devicetypes.h dev_mct2XX.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h porter.h
dev_mct310.obj:	precompiled.h devicetypes.h dev_mct310.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h pt_status.h tbl_pt_status.h porter.h \
		dllyukon.h
dev_mct31x.obj:	precompiled.h devicetypes.h tbl_ptdispatch.h ctibase.h \
		ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h dllbase.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h dbmemobject.h pointdefs.h row_reader.h \
		database_connection.h dbaccess.h dev_mct31X.h dev_mct310.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h rwutil.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h dllyukon.h \
		ctidate.h
dev_mct410.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h dllyukon.h date_utility.h \
		ctidate.h dev_mct410.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h dev_mct410_commands.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h tbl_ptdispatch.h \
		pt_status.h tbl_pt_status.h portglob.h porter.h devicetypes.h
dev_mct420.obj:	precompiled.h dev_mct420.h dev_mct410.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		dev_mct420_commands.h cmd_mct420_LcdConfiguration.h \
		cmd_mct420.h cmd_mct420_hourlyRead.h devicetypes.h
dev_mct470.obj:	precompiled.h devicetypes.h tbl_ptdispatch.h ctibase.h \
		ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h dllbase.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h dbmemobject.h pointdefs.h row_reader.h \
		database_connection.h dbaccess.h dev_mct470.h dev_mct4xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h rwutil.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		config_data_mct.h ctidate.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h pt_status.h tbl_pt_status.h porter.h \
		dllyukon.h ctistring.h da_lp_deviceconfig.h
dev_mct4xx.obj:	precompiled.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h ctidate.h dev_mct470.h \
		dev_mct410.h dev_mct410_commands.h cmd_mct410_hourlyRead.h \
		cmd_mct410.h devicetypes.h ctistring.h date_utility.h
dev_mct_broadcast.obj:	precompiled.h dev_mct_broadcast.h dev_dlcbase.h \
		dev_single.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h porter.h devicetypes.h \
		dev_mct.h dev_carrier.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h dev_mct31x.h \
		dev_mct310.h dev_mct4xx.h config_data_mct.h ctidate.h
dev_mct_lmt2.obj:	precompiled.h devicetypes.h dev_mct_lmt2.h \
		dev_mct22x.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h
dev_meter.obj:	precompiled.h dev_meter.h tbl_metergrp.h yukon.h \
		types.h ctidbgmem.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h
dev_modbus.obj:	precompiled.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h porter.h devicetypes.h pt_base.h \
		dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		pointdefs.h pt_numeric.h boostutil.h tbl_pt_unit.h \
		tbl_unitmeasure.h pt_status.h tbl_pt_status.h pt_analog.h \
		tbl_pt_analog.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h master.h dllyukon.h mgr_route.h \
		repeaterrole.h rte_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h message.h collectable.h \
		rwutil.h database_connection.h database_reader.h boost_time.h \
		tbl_pao_lite.h tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h \
		slctdev.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h hashkey.h hash_functions.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h smartmap.h \
		readers_writer_lock.h critical_section.h mgr_point.h \
		msg_cmd.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h msg_lmcontrolhistory.h dev_modbus.h dev_remote.h \
		dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		prot_modbus.h tbl_dv_address.h
dev_paging.obj:	precompiled.h dev_paging.h tbl_dv_tappaging.h \
		dlldefs.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		rwutil.h database_connection.h database_reader.h boost_time.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h
dev_pagingreceiver.obj:	precompiled.h cparms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h dev_rtm.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h verification_objects.h porter.h devicetypes.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h port_base.h \
		tbl_port_base.h critical_section.h counter.h mgr_route.h \
		repeaterrole.h slctdev.h smartmap.h readers_writer_lock.h \
		msg_trace.h dev_pagingreceiver.h tbl_dv_pagingreceiver.h
dev_quantum.obj:	precompiled.h ctidate.h dlldefs.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h porter.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h devicetypes.h dev_schlum.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_quantum.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_rds.obj:	precompiled.h dev_rds.h dev_remote.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h encryption_oneway_message.h \
		ctistring.h
dev_repeater.obj:	precompiled.h devicetypes.h dev_repeater.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		porter.h ctistring.h
dev_repeater800.obj:	precompiled.h devicetypes.h dev_repeater800.h \
		dev_repeater.h dev_dlcbase.h dev_single.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h porter.h
dev_repeater850.obj:	precompiled.h devicetypes.h dev_repeater850.h \
		dev_repeater800.h dev_repeater.h dev_dlcbase.h dev_single.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h porter.h
dev_rtc.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h dev_rtc.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_rtc.h msg_cmd.h msg_lmcontrolhistory.h porter.h \
		devicetypes.h protocol_sa.h prot_sa305.h prot_sa3rdparty.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h pt_status.h \
		tbl_pt_status.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h verification_objects.h
dev_rtm.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h dev_rtm.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h verification_objects.h msg_cmd.h porter.h \
		devicetypes.h protocol_sa.h prot_sa3rdparty.h prot_sa305.h \
		ctistring.h
dev_schlum.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h devicetypes.h dev_schlum.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_sentinel.obj:	precompiled.h porter.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h dev_sentinel.h dev_meter.h tbl_metergrp.h \
		vcomdefs.h dbmemobject.h dllbase.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h dev_ied.h \
		dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_ansi.h prot_ansi.h \
		ansi_application.h ansi_datalink.h ansi_billing_table.h \
		std_ansi_tbl_base.h std_ansi_tbl_00.h std_ansi_tbl_01.h \
		std_ansi_tbl_08.h std_ansi_tbl_10.h std_ansi_tbl_11.h \
		std_ansi_tbl_12.h std_ansi_tbl_13.h std_ansi_tbl_14.h \
		std_ansi_tbl_15.h std_ansi_tbl_16.h std_ansi_tbl_21.h \
		std_ansi_tbl_22.h std_ansi_tbl_23.h std_ansi_tbl_25.h \
		std_ansi_tbl_27.h std_ansi_tbl_28.h std_ansi_tbl_31.h \
		std_ansi_tbl_32.h std_ansi_tbl_33.h std_ansi_tbl_51.h \
		std_ansi_tbl_52.h std_ansi_tbl_61.h std_ansi_tbl_62.h \
		std_ansi_tbl_63.h std_ansi_tbl_64.h dllyukon.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_status.h tbl_pt_status.h prot_ansi_sentinel.h ctidate.h
dev_seriesv.obj:	precompiled.h dev_seriesv.h dev_ied.h types.h \
		os2_2w32.h dlldefs.h dsm2.h cticonnect.h yukon.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h numstr.h dsm2err.h words.h optional.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h database_reader.h boost_time.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h prot_seriesv.h porter.h devicetypes.h
dev_single.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h porter.h \
		devicetypes.h tbl_ptdispatch.h ctidate.h scanglob.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h ctistring.h \
		pt_status.h tbl_pt_status.h dllyukon.h
dev_sixnet.obj:	precompiled.h dev_sixnet.h dev_meter.h tbl_metergrp.h \
		yukon.h types.h ctidbgmem.h vcomdefs.h dlldefs.h \
		dbmemobject.h dllbase.h dsm2.h cticonnect.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h prot_sixnet.h msg_cmd.h porter.h devicetypes.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h
dev_snpp.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h porter.h devicetypes.h cmdparse.h \
		ctitokenizer.h parsevalue.h pt_base.h dbmemobject.h \
		tbl_pt_base.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h pointdefs.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		string_utility.h port_base.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h msg_signal.h tbl_static_paoinfo.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h tbl_port_base.h \
		xfer.h critical_section.h counter.h connection.h exchange.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		mgr_route.h repeaterrole.h slctdev.h smartmap.h \
		readers_writer_lock.h msg_pcrequest.h msg_pcreturn.h \
		msg_trace.h verification_objects.h dev_snpp.h dev_paging.h \
		tbl_dv_tappaging.h dev_ied.h dev_remote.h dev_single.h \
		tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h tbl_dialup.h \
		tbl_direct.h tbl_dv_ied.h encryption_oneway_message.h
dev_system.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h dev_system.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		prot_versacom.h rte_xcu.h smartmap.h readers_writer_lock.h \
		critical_section.h porter.h devicetypes.h mgr_route.h \
		repeaterrole.h slctdev.h dev_mct410.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h tbl_dv_scandata.h \
		tbl_dv_wnd.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		config_data_mct.h ctidate.h dev_mct410_commands.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h
dev_tap.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h logger.h thread.h \
		CtiPCPtrQueue.h porter.h devicetypes.h cmdparse.h \
		ctitokenizer.h parsevalue.h pt_base.h dbmemobject.h \
		tbl_pt_base.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h pointdefs.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		string_utility.h connection.h exchange.h message.h \
		collectable.h msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h \
		queue.h mgr_route.h repeaterrole.h rte_base.h ctibase.h \
		ctinexus.h tbl_pao_lite.h tbl_rtcomm.h msg_signal.h \
		tbl_static_paoinfo.h slctdev.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h smartmap.h \
		readers_writer_lock.h critical_section.h msg_pcrequest.h \
		msg_pcreturn.h msg_trace.h verification_objects.h dev_tap.h \
		dev_paging.h tbl_dv_tappaging.h dev_ied.h dev_remote.h \
		dev_single.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		xfer.h tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		encryption_oneway_message.h
dev_tcu.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h cparms.h rwutil.h yukon.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h porter.h devicetypes.h \
		pt_base.h dbmemobject.h tbl_pt_base.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h pointdefs.h \
		master.h dev_tcu.h dev_idlc.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		critical_section.h trx_711.h
dev_tnpp.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h dev_rtm.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h verification_objects.h porter.h devicetypes.h \
		pt_accum.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h port_base.h \
		tbl_port_base.h critical_section.h counter.h mgr_route.h \
		repeaterrole.h slctdev.h smartmap.h readers_writer_lock.h \
		msg_trace.h dev_tnpp.h tbl_dv_tnpp.h
dev_vectron.obj:	precompiled.h ctidate.h dlldefs.h logger.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h porter.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h words.h \
		optional.h devicetypes.h dev_schlum.h dev_meter.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h \
		dev_ied.h dev_remote.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h rwutil.h database_connection.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h msg_pcrequest.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h dev_vectron.h \
		pt_status.h tbl_pt_status.h pt_analog.h pt_numeric.h \
		tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h
dev_wctp.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h porter.h devicetypes.h pt_base.h \
		dbmemobject.h tbl_pt_base.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h pointdefs.h pt_accum.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h string_utility.h connection.h \
		exchange.h message.h collectable.h msg_multi.h msg_pdata.h \
		msg_ptreg.h msg_reg.h queue.h mgr_route.h repeaterrole.h \
		rte_base.h cmdparse.h ctitokenizer.h parsevalue.h ctibase.h \
		ctinexus.h tbl_pao_lite.h tbl_rtcomm.h msg_signal.h \
		tbl_static_paoinfo.h slctdev.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h smartmap.h \
		readers_writer_lock.h critical_section.h msg_pcrequest.h \
		msg_pcreturn.h msg_trace.h dev_wctp.h dev_paging.h \
		tbl_dv_tappaging.h dev_ied.h dev_remote.h dev_single.h \
		tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_ied.h \
		encryption_oneway_message.h verification_objects.h ctidate.h
dev_welco.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h dllyukon.h \
		porter.h devicetypes.h dev_welco.h dev_idlc.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h critical_section.h trx_711.h \
		prot_welco.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		pt_accum.h tbl_pt_accum.h tbl_pt_accumhistory.h msg_cmd.h \
		msg_lmcontrolhistory.h
disable_entry.obj:	precompiled.h disable_entry.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h ctistring.h
dlldev.obj:	precompiled.h utility.h ctitime.h dlldefs.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h
encryption_cbcrbt.obj:	precompiled.h encryption_cbcrbt.h dlldefs.h
encryption_cmac.obj:	precompiled.h encryption_cmac.h dlldefs.h
encryption_oneway.obj:	precompiled.h encryption_oneway.h dlldefs.h \
		encryption_cbcrbt.h encryption_cmac.h CtiTime.h CtiDate.h \
		logger.h thread.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h CtiPCPtrQueue.h
encryption_oneway_message.obj:	precompiled.h encryption_cbcrbt.h \
		dlldefs.h encryption_cmac.h encryption_oneway.h \
		encryption_oneway_message.h CtiTime.h mutex.h logger.h \
		thread.h guard.h utility.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h CParms.h rwutil.h yukon.h \
		ctidbgmem.h database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h
id_devdll.obj:	precompiled.h id_devdll.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h
id_pntdll.obj:	precompiled.h id_pntdll.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h
id_prtdll.obj:	precompiled.h id_prtdll.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h
key_password_encryptor.obj:	precompiled.h encryption_cbcrbt.h \
		dlldefs.h encryption_cmac.h encryption_oneway.h
memtest.obj:	precompiled.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h
mgr_config.obj:	precompiled.h mgr_config.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h mgr_device.h \
		rtdb.h hashkey.h hash_functions.h string_utility.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h rte_base.h dbmemobject.h ctibase.h ctinexus.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		slctdev.h smartmap.h readers_writer_lock.h critical_section.h
mgr_device.obj:	precompiled.h mgr_device.h dlldefs.h rtdb.h hashkey.h \
		hash_functions.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h string_utility.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h guard.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h rte_base.h dbmemobject.h ctibase.h ctinexus.h \
		message.h collectable.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		slctdev.h smartmap.h readers_writer_lock.h critical_section.h \
		debug_timer.h cparms.h configkey.h configval.h dev_macro.h \
		dev_grp.h msg_lmcontrolhistory.h msg_pcrequest.h msg_pdata.h \
		msg_multi.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		dev_cbc.h tbl_dv_cbc.h dev_dnp.h dev_remote.h dev_single.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h prot_dnp.h packet_finder.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h pointAttribute.h \
		dev_ion.h dev_meter.h tbl_metergrp.h vcomdefs.h dev_ied.h \
		tbl_dv_ied.h prot_ion.h ion_datastream.h ion_value.h \
		ion_serializable.h ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable.h ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h dev_gridadvisor.h \
		dev_idlc.h tbl_dv_idlcremote.h trx_info.h porter.h \
		devicetypes.h trx_711.h dev_ccu721.h device_queue_interface.h \
		prot_klondike.h prot_wrap.h prot_idlc.h dev_carrier.h \
		dev_dlcbase.h tbl_route.h tbl_carrier.h prot_emetcon.h \
		cmd_dlc.h cmd_base.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h dev_lmi.h tbl_dv_seriesv.h prot_lmi.h \
		prot_seriesv.h verification_objects.h dev_mct.h dev_mct410.h \
		dev_mct4xx.h config_data_mct.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		dev_modbus.h prot_modbus.h dev_repeater.h dev_rtc.h \
		tbl_dv_rtc.h dev_rtm.h dev_fmu.h dev_tap.h dev_paging.h \
		tbl_dv_tappaging.h encryption_oneway_message.h dev_snpp.h \
		dev_tnpp.h tbl_dv_tnpp.h dev_pagingreceiver.h \
		tbl_dv_pagingreceiver.h dev_grp_emetcon.h tbl_dv_emetcon.h \
		dev_grp_expresscom.h tbl_dv_expresscom.h dev_grp_golay.h \
		prot_sasimple.h tbl_lmg_sasimple.h dev_grp_point.h \
		tbl_lmg_point.h dev_grp_ripple.h tbl_dv_lmg_ripple.h \
		dev_grp_sa105.h prot_sa105.h tbl_lmg_sa205105.h \
		dev_grp_sa305.h prot_sa305.h tbl_lmg_sa305.h dev_grp_sa205.h \
		prot_sa205.h dev_grp_sadigital.h dev_grp_versacom.h \
		tbl_dv_versacom.h dev_grp_mct.h tbl_dv_lmgmct.h \
		dev_mct_broadcast.h dev_rds.h
mgr_holiday.obj:	precompiled.h ctidbgmem.h mgr_holiday.h ctidate.h \
		dlldefs.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h netports.h dsm2err.h words.h optional.h \
		database_reader.h database_connection.h row_reader.h
mgr_point.obj:	precompiled.h ctidbgmem.h pt_base.h dbmemobject.h \
		tbl_pt_base.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		dsm2.h cticonnect.h yukon.h types.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h pointdefs.h \
		mgr_point.h smartmap.h boostutil.h readers_writer_lock.h \
		critical_section.h devicetypes.h logger.h thread.h \
		CtiPCPtrQueue.h pt_accum.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h tbl_pt_accum.h tbl_pt_accumhistory.h \
		string_utility.h pt_analog.h tbl_pt_analog.h pt_status.h \
		tbl_pt_status.h cparms.h rwutil.h database_connection.h \
		database_reader.h boost_time.h configkey.h configval.h
mgr_port.obj:	precompiled.h mgr_port.h dlldefs.h smartmap.h \
		boostutil.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h dllbase.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h mutex.h guard.h dsm2err.h \
		words.h optional.h readers_writer_lock.h critical_section.h \
		port_base.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h counter.h slctprt.h port_direct.h port_serial.h \
		tbl_port_settings.h tbl_port_timing.h port_dialable.h \
		port_modem.h tbl_port_dialup.h tbl_port_serial.h \
		port_dialout.h port_pool_out.h port_tcpipdirect.h \
		tbl_port_tcpip.h port_tcp.h port_udp.h
mgr_route.obj:	precompiled.h mgr_route.h repeaterrole.h dlldefs.h \
		rte_base.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h mutex.h guard.h dsm2err.h \
		words.h optional.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h dllbase.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h CtiPCPtrQueue.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h slctdev.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h smartmap.h \
		readers_writer_lock.h critical_section.h rte_xcu.h \
		msg_pcrequest.h rte_ccu.h tbl_rtcarrier.h tbl_rtrepeater.h \
		rte_versacom.h tbl_rtversacom.h master.h dev_remote.h \
		dev_single.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		rte_expresscom.h rte_macro.h tbl_rtmacro.h
mgr_season.obj:	precompiled.h ctidbgmem.h mgr_season.h ctidate.h \
		dlldefs.h logger.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		yukon.h netports.h dsm2err.h words.h optional.h \
		database_reader.h database_connection.h row_reader.h
points.obj:	precompiled.h pt_accum.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		logger.h thread.h mutex.h guard.h CtiPCPtrQueue.h \
		pt_numeric.h row_reader.h yukon.h ctidbgmem.h pt_base.h \
		dbmemobject.h tbl_pt_base.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dbaccess.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		pointdefs.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h string_utility.h pt_analog.h \
		tbl_pt_analog.h
port_base.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h port_base.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		prot_emetcon.h color.h porter.h devicetypes.h msg_trace.h
port_dialable.obj:	precompiled.h port_dialable.h port_base.h \
		dev_base.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		port_modem.h tbl_port_dialup.h
port_dialin.obj:	precompiled.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h logger.h thread.h CtiPCPtrQueue.h \
		port_dialin.h port_base.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		port_dialable.h port_modem.h tbl_port_dialup.h
port_dialout.obj:	precompiled.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h logger.h thread.h CtiPCPtrQueue.h \
		port_dialout.h port_base.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		port_dialable.h port_modem.h tbl_port_dialup.h
port_direct.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		port_direct.h port_serial.h port_base.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h tbl_port_settings.h \
		tbl_port_timing.h port_dialable.h port_modem.h \
		tbl_port_dialup.h tbl_port_serial.h
port_modem.obj:	precompiled.h numstr.h dlldefs.h port_modem.h \
		port_base.h dev_base.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h dsm2err.h words.h optional.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h
port_pool_out.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		port_pool_out.h port_base.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h
port_serial.obj:	precompiled.h port_serial.h port_base.h dev_base.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h tbl_port_settings.h \
		tbl_port_timing.h
port_tcp.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h port_tcp.h port_serial.h \
		port_base.h dev_base.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		tbl_port_settings.h tbl_port_timing.h
port_tcpipdirect.obj:	precompiled.h cparms.h dlldefs.h rwutil.h \
		yukon.h types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h port_tcpipdirect.h port_serial.h \
		port_base.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h tbl_port_settings.h \
		tbl_port_timing.h port_dialable.h port_modem.h \
		tbl_port_dialup.h tbl_port_tcpip.h
port_udp.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h port_udp.h port_serial.h \
		port_base.h dev_base.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		tbl_port_settings.h tbl_port_timing.h tbl_port_tcpip.h
pttrigger.obj:	precompiled.h boostutil.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h mgr_point.h \
		pt_base.h dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h \
		guard.h dsm2err.h words.h optional.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h pointdefs.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		pt_dyn_dispatch.h tbl_pt_alarm.h tbl_ptdispatch.h ctibase.h \
		ctinexus.h database_connection.h tbl_pt_trigger.h pttrigger.h \
		logger.h thread.h CtiPCPtrQueue.h database_reader.h
pt_base.obj:	precompiled.h pt_base.h dbmemobject.h tbl_pt_base.h \
		row_reader.h ctitime.h dlldefs.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h pointdefs.h \
		tbl_pt_alarm.h logger.h thread.h CtiPCPtrQueue.h
pt_dyn_dispatch.obj:	precompiled.h pt_dyn_dispatch.h tbl_pt_alarm.h \
		dlldefs.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h row_reader.h tbl_ptdispatch.h \
		ctibase.h ctinexus.h pointdefs.h database_connection.h
pt_numeric.obj:	precompiled.h pt_numeric.h boostutil.h utility.h \
		ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h row_reader.h yukon.h ctidbgmem.h pt_base.h \
		dbmemobject.h tbl_pt_base.h dllbase.h dsm2.h cticonnect.h \
		netports.h mutex.h guard.h dsm2err.h words.h optional.h \
		dbaccess.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h pointdefs.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_alarm.h logger.h thread.h CtiPCPtrQueue.h
pt_status.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h pt_status.h boostutil.h \
		pt_base.h dbmemobject.h tbl_pt_base.h row_reader.h dllbase.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h dsm2err.h \
		words.h optional.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h pointdefs.h tbl_pt_status.h \
		tbl_pt_alarm.h
queuetest.obj:	precompiled.h queent.h dlldefs.h queue.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h
routetest.obj:	precompiled.h logger.h dlldefs.h thread.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		types.h numstr.h CtiPCPtrQueue.h mgr_route.h repeaterrole.h \
		rte_base.h boostutil.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		dbmemobject.h cmdparse.h ctitokenizer.h parsevalue.h \
		ctibase.h ctinexus.h dllbase.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h slctdev.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h smartmap.h readers_writer_lock.h \
		critical_section.h rtdb.h
rte_ccu.obj:	precompiled.h rte_ccu.h rte_xcu.h dev_base.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		tbl_rtcarrier.h tbl_rtrepeater.h dev_ccu.h dev_idlc.h \
		dev_remote.h dev_single.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h porter.h devicetypes.h \
		trx_711.h dev_ccu_queue_interface.h device_queue_interface.h \
		prot_versacom.h prot_emetcon.h expresscom.h ctistring.h
rte_expresscom.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		logger.h thread.h CtiPCPtrQueue.h porter.h devicetypes.h \
		rte_expresscom.h rte_xcu.h dev_base.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h smartmap.h readers_writer_lock.h \
		critical_section.h tbl_rtversacom.h master.h dev_remote.h \
		dev_single.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		expresscom.h ctistring.h
rte_macro.obj:	precompiled.h row_reader.h ctitime.h dlldefs.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h boostutil.h msg_pcrequest.h rte_macro.h \
		tbl_rtmacro.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h ctibase.h ctinexus.h rte_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h tbl_pao_lite.h tbl_rtcomm.h \
		msg_signal.h string_utility.h tbl_static_paoinfo.h \
		pointdefs.h porter.h devicetypes.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h
rte_versacom.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		logger.h thread.h CtiPCPtrQueue.h porter.h devicetypes.h \
		rte_versacom.h rte_xcu.h dev_base.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h smartmap.h readers_writer_lock.h \
		critical_section.h tbl_rtversacom.h master.h dev_remote.h \
		dev_single.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		prot_versacom.h
rte_xcu.obj:	precompiled.h desolvers.h dlldefs.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h expresscom.h cmdparse.h \
		ctitokenizer.h parsevalue.h dllbase.h ctistring.h rte_xcu.h \
		dev_base.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		smartmap.h readers_writer_lock.h critical_section.h master.h \
		dev_remote.h dev_single.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		dev_tap.h dev_paging.h tbl_dv_tappaging.h dev_ied.h \
		tbl_dv_ied.h encryption_oneway_message.h dev_snpp.h \
		dev_tnpp.h tbl_dv_tnpp.h dev_pagingreceiver.h \
		tbl_dv_pagingreceiver.h dev_lcu.h dev_idlc.h \
		tbl_dv_idlcremote.h trx_info.h porter.h devicetypes.h \
		trx_711.h dev_wctp.h prot_versacom.h prot_fpcbc.h \
		prot_sa305.h prot_sa3rdparty.h protocol_sa.h prot_lmi.h \
		prot_seriesv.h verification_objects.h
slctdev.obj:	precompiled.h dev_710.h dev_idlc.h types.h os2_2w32.h \
		dlldefs.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		numstr.h dsm2err.h words.h optional.h dev_remote.h \
		dev_single.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h database_reader.h boost_time.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h critical_section.h porter.h \
		devicetypes.h trx_711.h dev_macro.h dev_grp.h \
		msg_lmcontrolhistory.h pt_status.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h dev_cbc6510.h dev_dnp.h prot_dnp.h \
		packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h pointAttribute.h \
		dev_cbc.h tbl_dv_cbc.h dev_cbc7020.h config_data_cbc.h \
		dev_cbc8020.h dev_ccu.h dev_ccu_queue_interface.h \
		device_queue_interface.h dev_ccu721.h prot_klondike.h \
		prot_wrap.h prot_idlc.h dev_welco.h prot_welco.h dev_ilex.h \
		dev_seriesv.h dev_ied.h tbl_dv_ied.h prot_seriesv.h dev_lmi.h \
		tbl_dv_seriesv.h prot_lmi.h verification_objects.h dev_tcu.h \
		dev_meter.h tbl_metergrp.h vcomdefs.h dev_gridadvisor.h \
		dev_modbus.h prot_modbus.h dev_schlum.h dev_fulcrum.h \
		dev_ion.h prot_ion.h ion_datastream.h ion_value.h \
		ion_serializable.h ion_value_fixed.h ion_value_numeric.h \
		ion_value_variable.h ion_value_variable_fixedarray.h \
		ion_value_variable_fixedarray_element.h \
		ion_value_fixed_char.h ion_value_variable_boolean.h \
		ion_value_fixed_float.h ion_value_fixed_intsigned.h \
		ion_value_fixed_intunsigned.h ion_value_struct.h \
		ion_value_structarray.h ion_value_struct_types.h \
		ion_value_fixed_time.h ion_net_application.h \
		ion_net_network.h ion_net_datalink.h dev_lcu.h dev_quantum.h \
		dev_vectron.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		dev_rtm.h dev_tap.h dev_paging.h tbl_dv_tappaging.h \
		encryption_oneway_message.h dev_snpp.h dev_pagingreceiver.h \
		tbl_dv_pagingreceiver.h dev_tnpp.h tbl_dv_tnpp.h dev_wctp.h \
		dev_grp_emetcon.h tbl_dv_emetcon.h dev_grp_expresscom.h \
		tbl_dv_expresscom.h dev_grp_golay.h prot_sasimple.h \
		tbl_lmg_sasimple.h dev_grp_mct.h tbl_dv_lmgmct.h \
		dev_grp_point.h tbl_lmg_point.h dev_grp_ripple.h \
		tbl_dv_lmg_ripple.h dev_grp_sa105.h prot_sa105.h \
		tbl_lmg_sa205105.h dev_grp_sa205.h prot_sa205.h \
		dev_grp_sa305.h prot_sa305.h tbl_lmg_sa305.h \
		dev_grp_sadigital.h dev_grp_versacom.h tbl_dv_versacom.h \
		dev_davis.h dev_system.h dev_aplus.h dev_alpha.h dev_a1.h \
		dev_lgs4.h dev_lcr3102.h dev_lcr3102_commands.h \
		cmd_lcr3102_tamperRead.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_lcr3102_DemandResponseSummary.h \
		cmd_lcr3102_hourlyDataLog.h dev_dr87.h dev_dct501.h \
		dev_mct24x.h dev_mct2xx.h dev_mct.h dev_mct210.h dev_mct22X.h \
		dev_mct310.h dev_mct31X.h dev_mct410.h dev_mct4xx.h \
		config_data_mct.h ctidate.h dev_mct410_commands.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h dev_mct420.h \
		dev_mct470.h dev_mct_lmt2.h dev_mct_broadcast.h dev_kv2.h \
		prot_ansi_kv2.h prot_ansi.h ansi_application.h \
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
		ansi_kv2_mtable_110.h dllyukon.h dev_sentinel.h dev_ansi.h \
		prot_ansi_sentinel.h dev_focus.h prot_ansi_focus.h \
		ansi_focus_mtable_004.h ansi_focus_mtable_013.h \
		ansi_focus_mtable_024.h dev_ipc410al.h dev_ipc420ad.h \
		dev_mark_v.h prot_transdata.h transdata_application.h \
		transdata_tracker.h transdata_datalink.h prot_ymodem.h \
		transdata_data.h msg_cmd.h dev_rds.h dev_repeater800.h \
		dev_repeater.h dev_repeater850.h dev_rtc.h tbl_dv_rtc.h \
		dev_sixnet.h prot_sixnet.h rte_macro.h tbl_rtmacro.h \
		rte_ccu.h rte_xcu.h smartmap.h readers_writer_lock.h \
		tbl_rtcarrier.h tbl_rtrepeater.h rte_versacom.h \
		tbl_rtversacom.h master.h rte_expresscom.h dev_fmu.h rtdb.h \
		slctdev.h
slctprt.obj:	precompiled.h port_dialout.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h port_base.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h port_dialable.h \
		port_modem.h tbl_port_dialup.h port_dialin.h port_direct.h \
		port_serial.h tbl_port_settings.h tbl_port_timing.h \
		tbl_port_serial.h port_pool_out.h port_tcpipdirect.h \
		tbl_port_tcpip.h port_tcp.h port_udp.h devicetypes.h \
		slctprt.h
test_cmd_dlc.obj:	cmd_dlc.h cmd_base.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		prot_emetcon.h
test_cmd_lcr3102_demandresponsesummary.obj:	\
		cmd_lcr3102_DemandResponseSummary.h cmd_lcr3102_ThreePart.h \
		cmd_lcr3102.h cmd_dlc.h cmd_base.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		prot_emetcon.h ctidate.h
test_cmd_lcr3102_hourlydatalog.obj:	cmd_lcr3102_hourlyDataLog.h \
		cmd_lcr3102.h cmd_dlc.h cmd_base.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		prot_emetcon.h ctidate.h
test_cmd_lcr3102_tamperread.obj:	cmd_lcr3102_tamperRead.h \
		cmd_lcr3102_ThreePart.h cmd_lcr3102.h cmd_dlc.h cmd_base.h \
		dev_single.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h prot_emetcon.h ctidate.h
test_cmd_mct410_hourlyread.obj:	cmd_mct410_hourlyread.h cmd_mct410.h \
		cmd_dlc.h cmd_base.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		prot_emetcon.h ctidate.h
test_cmd_mct420_hourlyread.obj:	cmd_mct420_hourlyread.h \
		cmd_mct410_hourlyRead.h cmd_mct410.h cmd_dlc.h cmd_base.h \
		dev_single.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h prot_emetcon.h ctidate.h
test_cmd_mct420_lcdconfiguration.obj:	cmd_mct420_LcdConfiguration.h \
		cmd_mct420.h cmd_mct410.h cmd_dlc.h cmd_base.h dev_single.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h prot_emetcon.h ctidate.h
test_dev_cbc8020.obj:	dev_cbc8020.h dev_cbc7020.h dev_dnp.h \
		dev_remote.h dev_single.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h database_reader.h boost_time.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		prot_dnp.h packet_finder.h dnp_application.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dnp_datalink_packet.h \
		dnp_object_binaryoutput.h tbl_dv_address.h pointAttribute.h \
		config_data_cbc.h ctidate.h
test_dev_ccu721.obj:	dev_ccu721.h dev_remote.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h tbl_dv_address.h \
		device_queue_interface.h prot_klondike.h prot_wrap.h \
		prot_idlc.h critical_section.h prot_emetcon.h
test_dev_dct501.obj:	dev_dct501.h dev_mct24x.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h
test_dev_dlcbase.obj:	dev_dlcbase.h dev_single.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		dev_mct.h dev_carrier.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h
test_dev_grp.obj:	dev_grp.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h \
		msg_lmcontrolhistory.h pointdefs.h message.h collectable.h \
		msg_pcrequest.h msg_signal.h msg_pdata.h pointtypes.h \
		msg_multi.h pt_status.h pt_base.h dbmemobject.h tbl_pt_base.h \
		resolvers.h db_entry_defines.h desolvers.h tbl_pt_status.h \
		pt_analog.h pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		tbl_pt_analog.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h hashkey.h hash_functions.h rte_base.h \
		ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h \
		tbl_static_paoinfo.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h dev_grp_expresscom.h tbl_dv_expresscom.h \
		vcomdefs.h devicetypes.h mgr_point.h smartmap.h \
		readers_writer_lock.h critical_section.h expresscom.h \
		ctistring.h
test_dev_mct.obj:	dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h \
		dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h ctidate.h
test_dev_mct210.obj:	dev_mct210.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h
test_dev_mct22x.obj:	dev_mct22x.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h
test_dev_mct24x.obj:	dev_mct24x.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h
test_dev_mct2xx.obj:	dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h
test_dev_mct310.obj:	dev_mct310.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h devicetypes.h
test_dev_mct31x.obj:	dev_mct31x.h dev_mct310.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h devicetypes.h
test_dev_mct410.obj:	dev_mct410.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		pt_analog.h tbl_pt_analog.h pt_accum.h tbl_pt_accum.h \
		tbl_pt_accumhistory.h pt_status.h tbl_pt_status.h \
		devicetypes.h boost_test_helpers.h
test_dev_mct420.obj:	dev_mct420.h dev_mct410.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h ctidate.h \
		dev_mct410_commands.h cmd_mct410_hourlyRead.h cmd_mct410.h \
		devicetypes.h pt_analog.h tbl_pt_analog.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h pt_status.h \
		tbl_pt_status.h boost_test_helpers.h
test_dev_mct470.obj:	dev_mct470.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h ctidate.h devicetypes.h
test_dev_mct4xx.obj:	dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h config_data_mct.h ctidate.h
test_dev_mct_lmt2.obj:	dev_mct_lmt2.h dev_mct22x.h dev_mct2xx.h \
		dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_route.h tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_metergrp.h vcomdefs.h tbl_loadprofile.h da_load_profile.h \
		tbl_dv_mctiedport.h pt_numeric.h tbl_pt_unit.h \
		tbl_unitmeasure.h
test_dev_rds.obj:	dev_rds.h dev_remote.h dev_single.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h dlldefs.h netports.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		boostutil.h tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h \
		tbl_dialup.h tbl_direct.h encryption_oneway_message.h \
		test_reader.h
test_dev_rtm.obj:	dev_rtm.h dev_ied.h types.h os2_2w32.h dlldefs.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h numstr.h \
		dsm2err.h words.h optional.h dev_remote.h dev_single.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_dialup.h tbl_direct.h \
		tbl_dv_ied.h verification_objects.h
test_dev_single.obj:	devicetypes.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h \
		words.h optional.h dev_single.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h xfer.h
test_encryption_cbcrbt.obj:	encryption_cbcrbt.h dlldefs.h
test_encryption_cmac.obj:	encryption_cmac.h dlldefs.h
test_encryption_oneway.obj:	encryption_oneway.h dlldefs.h CtiDate.h \
		logger.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
test_encryption_oneway_message.obj:	encryption_oneway_message.h \
		dlldefs.h CtiTime.h CtiDate.h logger.h thread.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h CtiPCPtrQueue.h
test_lcr3102.obj:	dev_lcr3102.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h xfer.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		dev_lcr3102_commands.h cmd_lcr3102_tamperRead.h \
		cmd_lcr3102_ThreePart.h cmd_lcr3102.h \
		cmd_lcr3102_DemandResponseSummary.h \
		cmd_lcr3102_hourlyDataLog.h
test_mgr_point.obj:	mgr_point.h pt_base.h dbmemobject.h tbl_pt_base.h \
		row_reader.h ctitime.h dlldefs.h dllbase.h dsm2.h \
		cticonnect.h yukon.h types.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h dbaccess.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h pointdefs.h \
		smartmap.h boostutil.h readers_writer_lock.h \
		critical_section.h pt_status.h tbl_pt_status.h pt_analog.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h tbl_pt_analog.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h pt_accum.h \
		tbl_pt_accum.h tbl_pt_accumhistory.h
test_pt_base.obj:	pt_base.h dbmemobject.h tbl_pt_base.h row_reader.h \
		ctitime.h dlldefs.h dllbase.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h netports.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h dbaccess.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h pointdefs.h
test_rtdb_sql.obj:	database_reader.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		dlldefs.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h row_reader.h ctidate.h logger.h thread.h \
		CtiPCPtrQueue.h tbl_pt_property.h dbmemobject.h
test_rte_ccu.obj:	rte_ccu.h rte_xcu.h dev_base.h dsm2.h cticonnect.h \
		yukon.h types.h ctidbgmem.h dlldefs.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h cticalls.h os2_2w32.h \
		numstr.h dsm2err.h words.h optional.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h logger.h thread.h \
		CtiPCPtrQueue.h hashkey.h hash_functions.h rte_base.h \
		dbmemobject.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h msg_pcrequest.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		tbl_rtcarrier.h tbl_rtrepeater.h devicetypes.h
test_slctdev.obj:	slctdev.h dev_base.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h database_reader.h boost_time.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		devicetypes.h
#ENDUPDATE#
