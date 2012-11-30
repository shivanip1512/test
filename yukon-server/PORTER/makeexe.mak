# nmake file YUKON 1.0

include $(COMPILEBASE)\global.inc
include $(COMPILEBASE)\rwglobal.inc

INCLUDE=%INCLUDE%;\$(INCPATHADDITIONS)

INCLPATHS+= \
-I$(PORTER)\include \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(RTDB)\include \
-I$(SCANNER)\include \
-I$(SERVICE)\include \
-I$(PIL)\include \
-I$(SERVER)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
-I$(PROT)\include \
-I$(BOOST_INCLUDE) \
-I$(SQLAPI)\include \
-I$(RW) \
-I$(XERCES)\include \
-I$(OPENSSL)\include


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(DEVICECONFIGURATION)\include \
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
;$(BOOST_INCLUDE) \
;$(RW) \
;$(OPENSSL)\include\openssl


BASEOBJS=\
disp_thd.obj \
phlidlc.obj \
plidlc.obj \
portconf.obj \
portdialback.obj \
portentry.obj \
porter.obj \
portersu.obj \
portfield.obj \
portfill.obj \
port_thread_tcp.obj \
tcp_connection_manager.obj \
tcp_connection.obj \
port_thread_udp.obj \
unsolicited_handler.obj \
portload.obj \
portmain.obj \
portpool.obj \
portpil.obj \
portque.obj \
portsvc.obj \
porttime.obj \
portverify.obj \
ripple.obj \
systemmsgthread.obj \
encryption_lantronix.obj \
EncodingFilterFactory.obj \
encryption_noOp.obj \
PaoStatistics.obj \
PaoStatisticsRecord.obj \
StatisticsManager.obj \
StatisticsThread.obj \

PORTERLIBS=\
advapi32.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\portglob.lib \
$(COMPILEBASE)\lib\service.lib \
$(COMPILEBASE)\lib\ctipil.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \
$(COMPILEBASE)\lib\ctiprtdb.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(COMPILEBASE)\lib\cticonfig.lib \
$(OPENSSL_LIBS)

EXECS=\
porter.exe \
traceset.exe


PORTER_EXE_FULLBUILD = $[Filename,$(OBJ),PorterExeFullBuild,target]



ALL:            $(EXECS)
                -@if exist $(COMPILEBASE)\porter\lib\libeay32.dll copy $(COMPILEBASE)\porter\lib\libeay32.dll $(YUKONOUTPUT)
                -@if exist $(COMPILEBASE)\porter\lib\ssleay32.dll copy $(COMPILEBASE)\porter\lib\ssleay32.dll $(YUKONOUTPUT)


$(PORTER_EXE_FULLBUILD) :
	@touch $@
	@echo Compiling cpp to obj
	@echo:
	$(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(BASEOBJS)]


porter.exe:     $(PORTER_EXE_FULLBUILD) $(BASEOBJS) Makefile
                @build -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling ..\$@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(BASEOBJS) id_porter.obj $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ -link $(LIBS) $(RWLIBS) $(PORTERLIBS) $(BOOST_LIBS) $(LINKFLAGS)
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target ..\$@
                @echo:
                @%cd $(CWD)

traceset.exe:   traceset.obj
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
traceset.obj -link $(LIBS) $(COMPILEBASE)\lib\portglob.lib
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
               -@copy ..\$@ $(YUKONOUTPUT)
                @%cd $(CWD)

copy:           $(EXECS)
               -if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.exe copy bin\*.exe $(YUKONOUTPUT)


clean:
                -del *.obj
                -del *.dll
                -del *.ilk
                -del *.pdb
                -del *.lib
                -del *.exp

.cpp.obj:
                @echo:
                @echo Compiling: $<
                @echo C-Options: $(CFLAGS)
                @echo Output   : $@
                @echo:
                $(RWCPPINVOKE) $(CFLAGS) $(RWCPPFLAGS) $(PCHFLAGS) $(INCLPATHS) -DWINDOWS -Fo$(OBJ)\ -c $<

deps:
                scandeps -Output makeexe.mak *.cpp



# The lines below accomplish the ID'ing of the project!
id:
            @build -nologo -f $(_InputFile) id_porter.obj

id_porter.obj:    id_porter.cpp include\id_porter.h


#UPDATE#
disp_thd.obj:	precompiled.h pilserver.h dsm2.h cticonnect.h yukon.h \
		types.h ctidbgmem.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h server_b.h con_mgr.h \
		connection.h exchange.h dllbase.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h message.h collectable.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h msg_multi.h msg_pdata.h \
		pointdefs.h pointtypes.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h ctibase.h ctinexus.h \
		critical_Section.h smartmap.h readers_writer_lock.h \
		msg_pcrequest.h mgr_device.h rtdb.h hashkey.h \
		hash_functions.h dev_base.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h rte_base.h dbmemobject.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h db_entry_defines.h desolvers.h \
		msg_signal.h tbl_static_paoinfo.h encryption.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		slctdev.h mgr_point.h mgr_route.h repeaterrole.h mgr_config.h \
		msg_dbchg.h msg_cmd.h mgr_port.h port_base.h tbl_port_base.h \
		xfer.h counter.h slctprt.h thread_monitor.h \
		thread_register_data.h portglob.h porter.h devicetypes.h \
		unsolicited_handler.h dev_single.h msg_pcreturn.h \
		tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		millisecond_timer.h StatisticsManager.h PaoStatistics.h \
		PaoStatisticsRecord.h ThreadStatusKeeper.h
dllmain.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h dsm2err.h \
		words.h optional.h portglob.h ctinexus.h porter.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h
encodingfilterfactory.obj:	precompiled.h EncodingFilterFactory.h \
		EncodingFilter.h port_udp.h port_serial.h port_base.h \
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
		string_utility.h tbl_static_paoinfo.h pointdefs.h \
		encryption.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h tbl_port_base.h xfer.h \
		critical_section.h counter.h tbl_port_settings.h \
		tbl_port_timing.h tbl_port_tcpip.h encryption_lantronix.h \
		encryption_noop.h
encryption_lantronix.obj:	precompiled.h encryption_lantronix.h \
		EncodingFilter.h logger.h dlldefs.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h types.h \
		numstr.h CtiPCPtrQueue.h
encryption_noop.obj:	precompiled.h encryption_noOp.h EncodingFilter.h
id_pgdll.obj:	precompiled.h id_pgdll.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h
id_porter.obj:	precompiled.h id_porter.h utility.h ctitime.h dlldefs.h \
		queues.h cticalls.h os2_2w32.h types.h numstr.h
paostatistics.obj:	precompiled.h PaoStatistics.h PaoStatisticsRecord.h \
		ctitime.h dlldefs.h database_reader.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h yukon.h types.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		row_reader.h InvalidReaderException.h logger.h thread.h \
		CtiPCPtrQueue.h
paostatisticsrecord.obj:	precompiled.h PaoStatisticsRecord.h ctitime.h \
		dlldefs.h database_writer.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h queues.h cticalls.h \
		os2_2w32.h numstr.h dsm2err.h words.h optional.h row_writer.h \
		ctidate.h logger.h thread.h CtiPCPtrQueue.h
phlidlc.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		queues.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		mutex.h guard.h utility.h ctitime.h numstr.h dsm2err.h \
		words.h optional.h porter.h devicetypes.h portdecl.h \
		rte_base.h boostutil.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h dllbase.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h port_base.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h ThreadStatusKeeper.h \
		thread_register_data.h thread_monitor.h smartmap.h \
		readers_writer_lock.h cparms.h configkey.h configval.h \
		queue.h portglob.h mgr_port.h slctprt.h trx_711.h trx_info.h
plidlc.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		cti_asmc.h queues.h dsm2.h cticonnect.h yukon.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h numstr.h \
		dsm2err.h words.h optional.h porter.h devicetypes.h \
		portglob.h ctinexus.h logger.h thread.h CtiPCPtrQueue.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h boostutil.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h \
		encryption.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h trx_info.h critical_section.h \
		cparms.h configkey.h configval.h
portconf.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h porter.h \
		devicetypes.h master.h elogger.h portglob.h ctinexus.h \
		logger.h thread.h CtiPCPtrQueue.h c_port_interface.h \
		dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h \
		encryption.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h portdecl.h port_base.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		mgr_device.h rtdb.h slctdev.h smartmap.h \
		readers_writer_lock.h mgr_route.h repeaterrole.h
portdialback.obj:	precompiled.h cticalls.h os2_2w32.h dlldefs.h \
		types.h cparms.h rwutil.h yukon.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h connection.h exchange.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_ptreg.h msg_reg.h queue.h mgr_device.h \
		rtdb.h hashkey.h hash_functions.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		db_entry_defines.h desolvers.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h slctdev.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		mgr_port.h port_base.h tbl_port_base.h xfer.h counter.h \
		slctprt.h msg_cmd.h pilserver.h server_b.h con_mgr.h \
		msg_pcrequest.h mgr_point.h mgr_route.h repeaterrole.h \
		mgr_config.h portglob.h porter.h devicetypes.h
portentry.obj:	precompiled.h connection.h dlldefs.h exchange.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h tbl_port_base.h xfer.h \
		critical_section.h counter.h porter.h devicetypes.h \
		StatisticsManager.h PaoStatistics.h PaoStatisticsRecord.h \
		ThreadStatusKeeper.h thread_register_data.h thread_monitor.h \
		smartmap.h readers_writer_lock.h portglob.h \
		c_port_interface.h elogger.h mgr_port.h slctprt.h \
		mgr_device.h rtdb.h slctdev.h CtiLocalConnect.h \
		msg_pcrequest.h msg_pcreturn.h prot_emetcon.h trx_711.h \
		trx_info.h
porter.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		color.h cparms.h rwutil.h yukon.h ctidbgmem.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h \
		cticonnect.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h dsm2err.h words.h optional.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		configkey.h configval.h porter.h devicetypes.h portdecl.h \
		rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h port_base.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h portverify.h queue.h \
		verification_objects.h StatisticsThread.h master.h elogger.h \
		thread_monitor.h smartmap.h readers_writer_lock.h \
		thread_register_data.h CtiLocalConnect.h systemmsgthread.h \
		connection.h exchange.h msg_multi.h msg_pdata.h msg_ptreg.h \
		msg_reg.h mgr_device.h rtdb.h slctdev.h mgr_port.h slctprt.h \
		portglob.h c_port_interface.h mgr_route.h repeaterrole.h \
		mgr_config.h mgr_point.h port_thread_udp.h \
		unsolicited_handler.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		msg_dbchg.h millisecond_timer.h port_udp.h port_serial.h \
		tbl_port_settings.h tbl_port_timing.h tbl_port_tcpip.h \
		EncodingFilterFactory.h EncodingFilter.h port_thread_tcp.h \
		packet_finder.h port_tcp.h tcp_connection_manager.h \
		tcp_connection.h port_shr.h port_shr_ip.h msg_trace.h \
		rte_macro.h tbl_rtmacro.h rte_xcu.h eventlog.h trx_711.h \
		trx_info.h dllyukon.h pilserver.h server_b.h con_mgr.h \
		dev_ccu721.h dev_remote.h tbl_dialup.h tbl_direct.h \
		tbl_dv_address.h device_queue_interface.h prot_klondike.h \
		prot_wrap.h prot_idlc.h
portersu.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		color.h connection.h exchange.h dllbase.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h dsm2err.h words.h optional.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h porter.h \
		devicetypes.h master.h elogger.h portglob.h ctinexus.h \
		portdecl.h rte_base.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h db_entry_defines.h desolvers.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h port_base.h dev_base.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h tbl_port_base.h xfer.h \
		critical_section.h counter.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h ThreadStatusKeeper.h \
		thread_register_data.h thread_monitor.h smartmap.h \
		readers_writer_lock.h c_port_interface.h rtdb.h mgr_port.h \
		slctprt.h mgr_device.h slctdev.h msg_commerrorhistory.h
portfield.obj:	precompiled.h c_port_interface.h dlldefs.h elogger.h \
		cti_asmc.h cticalls.h os2_2w32.h types.h dsm2.h cticonnect.h \
		yukon.h ctidbgmem.h netports.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h dsm2err.h words.h optional.h \
		master.h portdecl.h rte_base.h boostutil.h dbmemobject.h \
		cmdparse.h ctitokenizer.h parsevalue.h ctibase.h ctinexus.h \
		dllbase.h message.h collectable.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h tbl_pao_lite.h tbl_rtcomm.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h CtiPCPtrQueue.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h \
		encryption.h port_base.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h tbl_port_base.h xfer.h critical_section.h \
		counter.h porter.h devicetypes.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h ThreadStatusKeeper.h \
		thread_register_data.h thread_monitor.h smartmap.h \
		readers_writer_lock.h cparms.h configkey.h configval.h \
		queue.h portglob.h portverify.h verification_objects.h \
		mgr_port.h slctprt.h mgr_device.h rtdb.h slctdev.h dev_wctp.h \
		dev_paging.h tbl_dv_tappaging.h dev_ied.h dev_remote.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h prot_base.h tbl_dialup.h \
		tbl_direct.h tbl_dv_ied.h encryption_oneway_message.h \
		dev_ansi.h dev_meter.h tbl_metergrp.h vcomdefs.h prot_ansi.h \
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
		tbl_pt_control.h pt_status.h tbl_pt_status.h \
		tbl_pt_status_control.h dev_ccu721.h tbl_dv_address.h \
		device_queue_interface.h prot_klondike.h prot_wrap.h \
		prot_idlc.h dev_lcu.h dev_idlc.h tbl_dv_idlcremote.h \
		trx_info.h trx_711.h dev_mark_v.h prot_transdata.h \
		transdata_application.h transdata_tracker.h \
		transdata_datalink.h prot_ymodem.h ctidate.h transdata_data.h \
		msg_cmd.h dev_mct.h dev_carrier.h dev_dlcbase.h tbl_route.h \
		tbl_carrier.h prot_emetcon.h cmd_dlc.h cmd_base.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		dev_rtc.h tbl_dv_rtc.h dev_tap.h mgr_route.h repeaterrole.h \
		rte_macro.h tbl_rtmacro.h rte_ccu.h rte_xcu.h tbl_rtcarrier.h \
		tbl_rtrepeater.h prot_711.h ctilocalconnect.h portfield.h
portfill.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h porter.h \
		devicetypes.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h port_base.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h master.h portglob.h \
		c_port_interface.h elogger.h rtdb.h mgr_device.h slctdev.h \
		smartmap.h readers_writer_lock.h mgr_port.h slctprt.h \
		mgr_route.h repeaterrole.h dev_tcu.h dev_idlc.h dev_remote.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h queue.h prot_base.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h dev_tap.h dev_paging.h tbl_dv_tappaging.h dev_ied.h \
		tbl_dv_ied.h encryption_oneway_message.h dev_snpp.h \
		dev_tnpp.h tbl_dv_tnpp.h dev_pagingreceiver.h \
		tbl_dv_pagingreceiver.h dev_wctp.h prot_versacom.h \
		expresscom.h ctistring.h
portglob.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		queues.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		mutex.h guard.h utility.h ctitime.h numstr.h dsm2err.h \
		words.h optional.h porter.h devicetypes.h portglob.h \
		ctinexus.h logger.h thread.h CtiPCPtrQueue.h
portload.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		queues.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		mutex.h guard.h utility.h ctitime.h numstr.h dsm2err.h \
		words.h optional.h porter.h devicetypes.h portglob.h \
		ctinexus.h logger.h thread.h CtiPCPtrQueue.h portdecl.h \
		rte_base.h boostutil.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h dllbase.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h port_base.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h c_port_interface.h \
		elogger.h mgr_port.h smartmap.h readers_writer_lock.h \
		slctprt.h mgr_device.h rtdb.h slctdev.h mgr_route.h \
		repeaterrole.h rte_ccu.h rte_xcu.h msg_pcrequest.h \
		tbl_rtcarrier.h tbl_rtrepeater.h trx_711.h trx_info.h \
		dev_ccu.h dev_idlc.h dev_remote.h dev_single.h msg_pcreturn.h \
		msg_multi.h msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h \
		connection.h exchange.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h prot_base.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h dev_ccu_queue_interface.h \
		device_queue_interface.h dev_ccu721.h tbl_dv_address.h \
		prot_klondike.h prot_wrap.h prot_idlc.h
portmain.obj:	precompiled.h ctitime.h dlldefs.h portsvc.h cservice.h \
		CServiceConfig.h ctibase.h ctinexus.h cticonnect.h yukon.h \
		types.h ctidbgmem.h netports.h dllbase.h dsm2.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h portglob.h porter.h \
		devicetypes.h logger.h thread.h CtiPCPtrQueue.h cparms.h \
		rwutil.h database_connection.h dbaccess.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h
portpil.obj:	precompiled.h mgr_device.h dlldefs.h rtdb.h hashkey.h \
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
		msg_signal.h tbl_static_paoinfo.h pointdefs.h encryption.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h slctdev.h smartmap.h readers_writer_lock.h \
		critical_section.h mgr_route.h repeaterrole.h mgr_point.h \
		pilserver.h server_b.h con_mgr.h connection.h exchange.h \
		msg_multi.h msg_pdata.h msg_ptreg.h msg_reg.h queue.h \
		cparms.h configkey.h configval.h msg_pcrequest.h mgr_config.h \
		dllyukon.h portglob.h porter.h devicetypes.h thread_monitor.h \
		thread_register_data.h ThreadStatusKeeper.h
portpool.obj:	precompiled.h connection.h dlldefs.h exchange.h \
		dllbase.h dsm2.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h mutex.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h numstr.h dsm2err.h words.h optional.h \
		logger.h thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h \
		mgr_device.h rtdb.h hashkey.h hash_functions.h dev_base.h \
		cmdparse.h ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		db_entry_defines.h desolvers.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h slctdev.h \
		smartmap.h readers_writer_lock.h critical_section.h \
		mgr_port.h port_base.h tbl_port_base.h xfer.h counter.h \
		slctprt.h port_pool_out.h portdecl.h porter.h devicetypes.h \
		portglob.h
portque.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		cparms.h rwutil.h yukon.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h dsm2err.h \
		words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h porter.h \
		devicetypes.h elogger.h thread_monitor.h smartmap.h \
		readers_writer_lock.h critical_section.h queue.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h \
		thread_register_data.h ThreadStatusKeeper.h portglob.h \
		ctinexus.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h port_base.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h counter.h StatisticsManager.h PaoStatistics.h \
		PaoStatisticsRecord.h c_port_interface.h mgr_device.h rtdb.h \
		slctdev.h mgr_port.h slctprt.h port_shr.h ctilocalconnect.h \
		trx_711.h trx_info.h dev_ccu.h dev_idlc.h dev_remote.h \
		dev_single.h msg_pcrequest.h msg_pcreturn.h msg_multi.h \
		msg_pdata.h tbl_dv_scandata.h tbl_dv_wnd.h connection.h \
		exchange.h msg_ptreg.h msg_reg.h prot_base.h tbl_dialup.h \
		tbl_direct.h tbl_dv_idlcremote.h dev_ccu_queue_interface.h \
		device_queue_interface.h dev_ccu721.h tbl_dv_address.h \
		prot_klondike.h prot_wrap.h prot_idlc.h prot_emetcon.h
portsvc.obj:	precompiled.h ctitime.h dlldefs.h portsvc.h cservice.h \
		ctibase.h ctinexus.h cticonnect.h yukon.h types.h ctidbgmem.h \
		netports.h dllbase.h dsm2.h mutex.h guard.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h dsm2err.h words.h \
		optional.h portglob.h porter.h devicetypes.h logger.h \
		thread.h CtiPCPtrQueue.h
porttime.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		queues.h dsm2.h cticonnect.h yukon.h ctidbgmem.h netports.h \
		mutex.h guard.h utility.h ctitime.h numstr.h dsm2err.h \
		words.h optional.h porter.h devicetypes.h portdecl.h \
		rte_base.h boostutil.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h dllbase.h \
		message.h collectable.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h boost_time.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h port_base.h \
		dev_base.h dev_exclusion.h tbl_paoexclusion.h config_device.h \
		hashkey.h hash_functions.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h tbl_port_base.h \
		xfer.h critical_section.h counter.h elogger.h portglob.h \
		c_port_interface.h mgr_port.h smartmap.h \
		readers_writer_lock.h slctprt.h mgr_device.h rtdb.h slctdev.h \
		dev_ccu.h dev_idlc.h dev_remote.h dev_single.h \
		msg_pcrequest.h msg_pcreturn.h msg_multi.h msg_pdata.h \
		tbl_dv_scandata.h tbl_dv_wnd.h connection.h exchange.h \
		msg_ptreg.h msg_reg.h queue.h cparms.h configkey.h \
		configval.h prot_base.h tbl_dialup.h tbl_direct.h \
		tbl_dv_idlcremote.h trx_info.h trx_711.h \
		dev_ccu_queue_interface.h device_queue_interface.h \
		dev_ccu721.h tbl_dv_address.h prot_klondike.h prot_wrap.h \
		prot_idlc.h dev_dnp.h prot_dnp.h packet_finder.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h dev_ilex.h dev_mct4xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h tbl_route.h tbl_carrier.h \
		prot_emetcon.h cmd_dlc.h cmd_base.h tbl_metergrp.h vcomdefs.h \
		tbl_loadprofile.h da_load_profile.h tbl_dv_mctiedport.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h \
		config_data_mct.h ctidate.h mgr_route.h repeaterrole.h \
		thread_monitor.h thread_register_data.h ThreadStatusKeeper.h \
		config_data_dnp.h prot_welco.h prot_lmi.h prot_seriesv.h \
		verification_objects.h
portverify.obj:	precompiled.h boost_time.h mgr_port.h dlldefs.h \
		smartmap.h boostutil.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h guard.h \
		dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h port_base.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h database_reader.h \
		config_device.h logger.h thread.h CtiPCPtrQueue.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		tbl_port_base.h xfer.h counter.h slctprt.h cparms.h \
		configkey.h configval.h portverify.h queue.h \
		verification_objects.h ctidate.h database_transaction.h \
		database_writer.h row_writer.h
port_shr.obj:	precompiled.h ctinexus.h cticonnect.h yukon.h types.h \
		ctidbgmem.h dlldefs.h netports.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h port_shr.h thread.h logger.h \
		CtiPCPtrQueue.h port_base.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h boostutil.h \
		tbl_paoexclusion.h row_reader.h rwutil.h \
		database_connection.h dbaccess.h dllbase.h database_reader.h \
		boost_time.h config_device.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h message.h collectable.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h msg_signal.h string_utility.h \
		tbl_static_paoinfo.h pointdefs.h encryption.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		tbl_port_base.h xfer.h critical_section.h counter.h cparms.h \
		configkey.h configval.h
port_shr_ip.obj:	precompiled.h types.h cparms.h dlldefs.h rwutil.h \
		yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h port_shr_ip.h ctinexus.h port_shr.h \
		port_base.h dev_base.h cmdparse.h ctitokenizer.h parsevalue.h \
		dev_exclusion.h tbl_paoexclusion.h config_device.h hashkey.h \
		hash_functions.h rte_base.h dbmemobject.h ctibase.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h \
		encryption.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h tbl_port_base.h xfer.h \
		critical_section.h counter.h prot_welco.h porter.h \
		devicetypes.h cti_asmc.h
port_thread_tcp.obj:	precompiled.h port_thread_tcp.h \
		unsolicited_handler.h port_base.h dev_base.h dsm2.h \
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
		tbl_static_paoinfo.h pointdefs.h encryption.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		tbl_port_base.h xfer.h critical_section.h counter.h \
		mgr_device.h rtdb.h slctdev.h smartmap.h \
		readers_writer_lock.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h \
		msg_dbchg.h millisecond_timer.h packet_finder.h port_tcp.h \
		port_serial.h tbl_port_settings.h tbl_port_timing.h \
		tcp_connection_manager.h tcp_connection.h tbl_paoproperty.h \
		c_port_interface.h elogger.h portglob.h porter.h \
		devicetypes.h portfield.h prot_gpuff.h prot_dnp.h \
		dnp_application.h dnp_objects.h dnp_transport.h \
		dnp_datalink.h dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h mgr_port.h slctprt.h
port_thread_udp.obj:	precompiled.h port_thread_udp.h yukon.h types.h \
		ctidbgmem.h unsolicited_handler.h port_base.h dev_base.h \
		dsm2.h cticonnect.h dlldefs.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h boostutil.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h database_reader.h boost_time.h config_device.h \
		logger.h thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h \
		encryption.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h tbl_port_base.h xfer.h \
		critical_section.h counter.h mgr_device.h rtdb.h slctdev.h \
		smartmap.h readers_writer_lock.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h \
		msg_dbchg.h millisecond_timer.h port_udp.h port_serial.h \
		tbl_port_settings.h tbl_port_timing.h tbl_port_tcpip.h \
		EncodingFilterFactory.h EncodingFilter.h c_port_interface.h \
		elogger.h prot_gpuff.h packet_finder.h portglob.h porter.h \
		devicetypes.h mgr_port.h slctprt.h dev_dnp.h dev_remote.h \
		tbl_dialup.h tbl_direct.h prot_dnp.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h tbl_dv_address.h dev_gridadvisor.h \
		dev_rds.h encryption_oneway_message.h portfield.h
ripple.obj:	precompiled.h os2_2w32.h dlldefs.h types.h cticalls.h \
		connection.h exchange.h dllbase.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h dsm2err.h words.h optional.h logger.h \
		thread.h CtiPCPtrQueue.h string_utility.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		msg_multi.h msg_pdata.h pointdefs.h pointtypes.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h configkey.h configval.h porter.h \
		devicetypes.h portdecl.h rte_base.h dbmemobject.h cmdparse.h \
		ctitokenizer.h parsevalue.h ctibase.h ctinexus.h \
		tbl_pao_lite.h tbl_rtcomm.h resolvers.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_static_paoinfo.h encryption.h \
		port_base.h dev_base.h dev_exclusion.h tbl_paoexclusion.h \
		config_device.h hashkey.h hash_functions.h tbl_base.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h \
		tbl_port_base.h xfer.h critical_section.h counter.h master.h \
		scanner.h elogger.h portglob.h c_port_interface.h dev_lcu.h \
		dev_idlc.h dev_remote.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h tbl_dv_scandata.h tbl_dv_wnd.h prot_base.h \
		tbl_dialup.h tbl_direct.h tbl_dv_idlcremote.h trx_info.h \
		trx_711.h mgr_device.h rtdb.h slctdev.h smartmap.h \
		readers_writer_lock.h mgr_port.h slctprt.h
statisticsmanager.obj:	precompiled.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h ctitime.h dlldefs.h \
		ThreadStatusKeeper.h thread_register_data.h boost_time.h \
		cticalls.h os2_2w32.h types.h boostutil.h utility.h queues.h \
		numstr.h thread_monitor.h smartmap.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h guard.h \
		dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h cparms.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h configkey.h \
		configval.h queue.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h database_writer.h row_writer.h \
		database_transaction.h InvalidReaderException.h ctidate.h \
		debug_timer.h c_port_interface.h elogger.h mgr_device.h \
		rtdb.h hashkey.h hash_functions.h dev_base.h cmdparse.h \
		ctitokenizer.h parsevalue.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h message.h collectable.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_static_paoinfo.h pointdefs.h \
		encryption.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h slctdev.h mgr_port.h port_base.h \
		tbl_port_base.h xfer.h counter.h slctprt.h
statisticsthread.obj:	precompiled.h StatisticsManager.h \
		PaoStatistics.h PaoStatisticsRecord.h ctitime.h dlldefs.h \
		ThreadStatusKeeper.h thread_register_data.h boost_time.h \
		cticalls.h os2_2w32.h types.h boostutil.h utility.h queues.h \
		numstr.h thread_monitor.h smartmap.h dllbase.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h guard.h \
		dsm2err.h words.h optional.h readers_writer_lock.h \
		critical_section.h cparms.h rwutil.h database_connection.h \
		dbaccess.h database_reader.h row_reader.h configkey.h \
		configval.h queue.h logger.h thread.h CtiPCPtrQueue.h \
		string_utility.h debug_timer.h millisecond_timer.h portglob.h \
		ctinexus.h porter.h devicetypes.h
systemmsgthread.obj:	precompiled.h cmdparse.h ctitokenizer.h dlldefs.h \
		parsevalue.h counter.h guard.h utility.h ctitime.h queues.h \
		cticalls.h os2_2w32.h types.h numstr.h mutex.h cparms.h \
		rwutil.h yukon.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h database_reader.h row_reader.h boost_time.h \
		boostutil.h configkey.h configval.h device_queue_interface.h \
		msg_cmd.h message.h collectable.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h pointdefs.h \
		pointtypes.h msg_queuedata.h msg_requestcancel.h \
		systemmsgthread.h connection.h exchange.h logger.h thread.h \
		CtiPCPtrQueue.h string_utility.h msg_ptreg.h msg_reg.h \
		queue.h ctilocalconnect.h critical_section.h mgr_device.h \
		rtdb.h hashkey.h hash_functions.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h rte_base.h dbmemobject.h \
		ctibase.h ctinexus.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		db_entry_defines.h desolvers.h msg_signal.h \
		tbl_static_paoinfo.h encryption.h tbl_base.h tbl_scanrate.h \
		tbl_dyn_paoinfo.h pt_base.h tbl_pt_base.h slctdev.h \
		smartmap.h readers_writer_lock.h mgr_port.h port_base.h \
		tbl_port_base.h xfer.h slctprt.h portdecl.h porter.h \
		devicetypes.h
tcp_connection.obj:	precompiled.h cparms.h dlldefs.h rwutil.h yukon.h \
		types.h ctidbgmem.h database_connection.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h tcp_connection.h
tcp_connection_manager.obj:	precompiled.h tcp_connection_manager.h \
		tcp_connection.h ctitime.h dlldefs.h packet_finder.h cparms.h \
		rwutil.h yukon.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dllbase.h dsm2.h cticonnect.h netports.h mutex.h \
		guard.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		dsm2err.h words.h optional.h database_reader.h row_reader.h \
		boost_time.h boostutil.h configkey.h configval.h logger.h \
		thread.h CtiPCPtrQueue.h
test_gen_reply.obj:	trx_711.h trx_info.h logger.h dlldefs.h thread.h \
		mutex.h guard.h utility.h ctitime.h queues.h cticalls.h \
		os2_2w32.h types.h numstr.h CtiPCPtrQueue.h \
		critical_section.h porter.h dsm2.h cticonnect.h yukon.h \
		ctidbgmem.h netports.h dsm2err.h words.h optional.h \
		devicetypes.h string_utility.h portdecl.h rte_base.h \
		boostutil.h dbmemobject.h cmdparse.h ctitokenizer.h \
		parsevalue.h ctibase.h ctinexus.h dllbase.h message.h \
		collectable.h rwutil.h database_connection.h dbaccess.h \
		database_reader.h row_reader.h boost_time.h tbl_pao_lite.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_static_paoinfo.h pointdefs.h \
		encryption.h port_base.h dev_base.h dev_exclusion.h \
		tbl_paoexclusion.h config_device.h hashkey.h hash_functions.h \
		tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h pt_base.h \
		tbl_pt_base.h tbl_port_base.h xfer.h counter.h cparms.h \
		configkey.h configval.h
test_lantronixencryption.obj:	encryption_lantronix.h EncodingFilter.h
test_paostatisticsrecord.obj:	PaoStatisticsRecord.h ctitime.h \
		dlldefs.h ctidate.h logger.h thread.h mutex.h guard.h \
		utility.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		CtiPCPtrQueue.h
traceset.obj:	precompiled.h dlldefs.h
unsolicited_handler.obj:	precompiled.h boostutil.h utility.h ctitime.h \
		dlldefs.h queues.h cticalls.h os2_2w32.h types.h numstr.h \
		unsolicited_handler.h port_base.h dev_base.h dsm2.h \
		cticonnect.h yukon.h ctidbgmem.h netports.h mutex.h guard.h \
		dsm2err.h words.h optional.h cmdparse.h ctitokenizer.h \
		parsevalue.h dev_exclusion.h tbl_paoexclusion.h row_reader.h \
		rwutil.h database_connection.h dbaccess.h dllbase.h \
		database_reader.h boost_time.h config_device.h logger.h \
		thread.h CtiPCPtrQueue.h hashkey.h hash_functions.h \
		rte_base.h dbmemobject.h ctibase.h ctinexus.h message.h \
		collectable.h tbl_pao_lite.h tbl_rtcomm.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h msg_signal.h \
		string_utility.h tbl_static_paoinfo.h pointdefs.h \
		encryption.h tbl_base.h tbl_scanrate.h tbl_dyn_paoinfo.h \
		pt_base.h tbl_pt_base.h tbl_port_base.h xfer.h \
		critical_section.h counter.h mgr_device.h rtdb.h slctdev.h \
		smartmap.h readers_writer_lock.h dev_single.h msg_pcrequest.h \
		msg_pcreturn.h msg_multi.h msg_pdata.h tbl_dv_scandata.h \
		tbl_dv_wnd.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h cparms.h configkey.h configval.h prot_base.h \
		msg_dbchg.h millisecond_timer.h prot_gpuff.h packet_finder.h \
		pt_numeric.h tbl_pt_unit.h tbl_unitmeasure.h exceptions.h \
		portglob.h porter.h devicetypes.h dev_dnp.h dev_remote.h \
		tbl_dialup.h tbl_direct.h prot_dnp.h dnp_application.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h \
		dnp_datalink_packet.h dnp_configuration.h \
		dnp_object_binaryoutput.h tbl_dv_address.h msg_trace.h \
		StatisticsManager.h PaoStatistics.h PaoStatisticsRecord.h \
		ThreadStatusKeeper.h thread_register_data.h thread_monitor.h
#ENDUPDATE#
