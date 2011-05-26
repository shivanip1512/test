
# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DATABASE)\include \
-I$(PORTER)\include \
-I$(COMMON)\include \
-I$(RW) \
-I$(BOOST) \
-I$(SQLAPI)\include \

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(PORTER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROTOCOL)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)


BASEOBJS=\
tbl_rtroute.obj \
tbl_pthist.obj

TABLETESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \

EXECS=\
almtest.exe


ALL:            $(EXECS)

almtest.exe:    almtest.obj Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
almtest.obj -link $(LIBS) $(RWLIBS) $(BOOST_LIBS) $(TABLETESTLIBS) $(COMPILEBASE)\lib\ctidbsrc.lib
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               mt.exe -manifest ..\$@.manifest -outputresource:..\$@;1
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target ..\$@
                @%cd $(CWD)

copy:           $(EXECS)
               -if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist *.exe copy *.exe $(YUKONOUTPUT)


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


#UPDATE#
almtest.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h row_reader.h \
		database_connection.h tbl_pt_alarm.h dbmemobject.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h
dllyukon.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_route.h \
		dllbase.h os2_2w32.h dlldefs.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dbmemobject.h \
		row_reader.h tbl_rtcarrier.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h tbl_rtcomm.h ctibase.h ctinexus.h \
		tbl_rtmacro.h tbl_rtroute.h tbl_rtrepeater.h tbl_rtversacom.h \
		msg_pcrequest.h message.h collectable.h rwutil.h \
		database_connection.h database_reader.h boost_time.h \
		boostutil.h tbl_state_grp.h tbl_state.h
invalidreaderexception.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		InvalidReaderException.h dlldefs.h database_reader.h \
		ctitime.h database_connection.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h row_reader.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
tbl_alm_ndest.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		row_reader.h ctitime.h dlldefs.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h tbl_alm_ndest.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_alm_ngroup.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		row_reader.h ctitime.h dlldefs.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h tbl_alm_ngroup.h logger.h thread.h \
		CtiPCPtrQueue.h database_connection.h database_reader.h
tbl_base.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_base.h \
		dllbase.h os2_2w32.h dlldefs.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h logger.h thread.h \
		CtiPCPtrQueue.h dbmemobject.h row_reader.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_carrier.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_carrier.h dlldefs.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h logger.h thread.h \
		CtiPCPtrQueue.h database_connection.h database_reader.h \
		database_writer.h row_writer.h
tbl_ci_cust.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		database_connection.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h database_reader.h row_reader.h \
		logger.h thread.h CtiPCPtrQueue.h tbl_ci_cust.h
tbl_commerrhist.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_commerrhist.h row_reader.h ctitime.h dlldefs.h \
		dbmemobject.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_connection.h logger.h thread.h CtiPCPtrQueue.h \
		database_reader.h database_writer.h row_writer.h
tbl_contact_notification.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h database_connection.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h tbl_contact_notification.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_devicereadjoblog.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_devicereadjoblog.h ctitime.h dlldefs.h pointdefs.h \
		utility.h queues.h cticalls.h os2_2w32.h numstr.h logger.h \
		thread.h mutex.h guard.h CtiPCPtrQueue.h dbaccess.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		sema.h database_connection.h database_writer.h row_writer.h
tbl_devicereadrequestlog.obj:	yukon.h precompiled.h types.h \
		ctidbgmem.h tbl_devicereadrequestlog.h ctitime.h dlldefs.h \
		pointdefs.h utility.h queues.h cticalls.h os2_2w32.h numstr.h \
		logger.h thread.h mutex.h guard.h CtiPCPtrQueue.h dbaccess.h \
		dllbase.h dsm2.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h database_connection.h database_writer.h \
		row_writer.h
tbl_dialup.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_dialup.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h logger.h thread.h CtiPCPtrQueue.h \
		database_connection.h database_reader.h database_writer.h \
		row_writer.h
tbl_direct.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_direct.h \
		dbmemobject.h dlldefs.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h logger.h thread.h CtiPCPtrQueue.h \
		database_connection.h database_reader.h database_writer.h \
		row_writer.h
tbl_dv_address.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_dv_address.h logger.h dlldefs.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h dllbase.h dsm2.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		row_reader.h
tbl_dv_cbc.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_dv_cbc.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h logger.h thread.h CtiPCPtrQueue.h \
		database_connection.h database_writer.h row_writer.h
tbl_dv_emetcon.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_dv_emetcon.h dlldefs.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h logger.h thread.h \
		CtiPCPtrQueue.h database_connection.h database_writer.h \
		row_writer.h
tbl_dv_expresscom.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h resolvers.h pointtypes.h db_entry_defines.h \
		tbl_dv_expresscom.h vcomdefs.h dbmemobject.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbaccess.h sema.h row_reader.h
tbl_dv_idlcremote.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h dbaccess.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h tbl_dv_idlcremote.h rwutil.h \
		database_connection.h database_reader.h row_reader.h \
		boost_time.h boostutil.h dbmemobject.h
tbl_dv_ied.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_dv_ied.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dbmemobject.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h logger.h thread.h CtiPCPtrQueue.h \
		database_connection.h database_writer.h row_writer.h
tbl_dv_lmgmct.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		row_reader.h ctitime.h dlldefs.h tbl_dv_lmgmct.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h logger.h thread.h CtiPCPtrQueue.h
tbl_dv_lmg_ripple.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h tbl_dv_lmg_ripple.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h database_connection.h \
		database_writer.h row_writer.h
tbl_dv_lmvcserial.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_dv_lmvcserial.h row_reader.h ctitime.h dlldefs.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h vcomdefs.h \
		logger.h thread.h CtiPCPtrQueue.h database_connection.h \
		database_reader.h
tbl_dv_mctiedport.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_dv_mctiedport.h dlldefs.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_dv_pagingreceiver.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_dv_pagingreceiver.h dlldefs.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h row_reader.h logger.h \
		thread.h CtiPCPtrQueue.h database_connection.h \
		database_writer.h row_writer.h
tbl_dv_rtc.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h logger.h \
		thread.h CtiPCPtrQueue.h tbl_dv_rtc.h row_reader.h
tbl_dv_scandata.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h tbl_dv_scandata.h \
		row_reader.h dbmemobject.h rwutil.h database_connection.h \
		database_reader.h boost_time.h boostutil.h database_writer.h \
		row_writer.h
tbl_dv_seriesv.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		row_reader.h ctitime.h dlldefs.h tbl_dv_seriesv.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_dv_tappaging.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_dv_tappaging.h dlldefs.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h logger.h thread.h \
		CtiPCPtrQueue.h database_connection.h database_writer.h \
		row_writer.h
tbl_dv_tnpp.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_dv_tnpp.h dlldefs.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h logger.h thread.h \
		CtiPCPtrQueue.h database_connection.h database_writer.h \
		row_writer.h
tbl_dv_versacom.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_dv_versacom.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h dbaccess.h sema.h row_reader.h \
		resolvers.h pointtypes.h db_entry_defines.h logger.h thread.h \
		CtiPCPtrQueue.h database_connection.h database_writer.h \
		row_writer.h
tbl_dv_wnd.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_dv_wnd.h \
		row_reader.h ctitime.h dlldefs.h database_connection.h \
		dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h dbmemobject.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h database_reader.h
tbl_dyn_paoinfo.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h tbl_dyn_paoinfo.h \
		dbmemobject.h database_connection.h row_reader.h \
		database_reader.h database_writer.h row_writer.h
tbl_dyn_ptalarming.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h tbl_dyn_ptalarming.h \
		ctibase.h ctinexus.h dbmemobject.h pointdefs.h row_reader.h \
		database_connection.h database_reader.h database_writer.h \
		row_writer.h
tbl_dyn_pttag.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h tbl_dyn_pttag.h ctibase.h \
		ctinexus.h dbmemobject.h pointdefs.h row_reader.h \
		database_connection.h database_reader.h database_writer.h \
		row_writer.h
tbl_lmg_golay.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_lmg_golay.h row_reader.h ctitime.h dlldefs.h \
		dbmemobject.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h
tbl_lmg_point.obj:	yukon.h precompiled.h types.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		tbl_lmg_point.h dbmemobject.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h
tbl_lmg_sa205105.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		cparms.h dlldefs.h rwutil.h database_connection.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h database_reader.h \
		row_reader.h boost_time.h boostutil.h configkey.h configval.h \
		logger.h thread.h CtiPCPtrQueue.h tbl_lmg_sa205105.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h
tbl_lmg_sa305.obj:	yukon.h precompiled.h types.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		tbl_lmg_sa305.h dbmemobject.h dllbase.h dsm2.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h row_reader.h
tbl_lmg_sasimple.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h tbl_lmg_sasimple.h dbmemobject.h dllbase.h \
		dsm2.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h
tbl_lmprogramhistory.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		row_reader.h ctitime.h dlldefs.h tbl_lmprogramhistory.h \
		dbmemobject.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h database_connection.h \
		database_reader.h database_writer.h row_writer.h
tbl_lm_controlhist.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_lm_controlhist.h row_reader.h ctitime.h dlldefs.h \
		dbmemobject.h dbaccess.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_connection.h logger.h thread.h CtiPCPtrQueue.h \
		ctidate.h database_reader.h database_writer.h row_writer.h
tbl_loadprofile.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_loadprofile.h vcomdefs.h dlldefs.h dbmemobject.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h da_load_profile.h \
		row_reader.h logger.h thread.h CtiPCPtrQueue.h \
		database_connection.h database_reader.h
tbl_metergrp.obj:	yukon.h precompiled.h types.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		tbl_metergrp.h vcomdefs.h dbmemobject.h dllbase.h dsm2.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbaccess.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h database_connection.h database_writer.h \
		row_writer.h
tbl_meterreadlog.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_meterreadlog.h ctitime.h dlldefs.h pointdefs.h utility.h \
		queues.h cticalls.h os2_2w32.h numstr.h row_reader.h \
		database_connection.h dbaccess.h dllbase.h dsm2.h mutex.h \
		guard.h cticonnect.h netports.h dsm2err.h words.h optional.h \
		sema.h boostutil.h logger.h thread.h CtiPCPtrQueue.h \
		database_writer.h row_writer.h
tbl_pao.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h logger.h \
		thread.h CtiPCPtrQueue.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_pao.h row_reader.h dbmemobject.h \
		database_connection.h database_reader.h database_writer.h \
		row_writer.h
tbl_paoexclusion.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h tbl_paoexclusion.h \
		row_reader.h rwutil.h database_connection.h database_reader.h \
		boost_time.h boostutil.h
tbl_paoproperty.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_paoproperty.h dlldefs.h row_reader.h ctitime.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h
tbl_pao_lite.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h logger.h \
		thread.h CtiPCPtrQueue.h resolvers.h pointtypes.h \
		db_entry_defines.h tbl_pao_lite.h dbmemobject.h row_reader.h \
		database_connection.h database_reader.h
tbl_port_base.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_port_base.h dbmemobject.h dbaccess.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		ctitime.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h row_reader.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_port_dialup.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_port_dialup.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_port_serial.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_port_serial.h dbaccess.h dlldefs.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h ctitime.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h dbmemobject.h row_reader.h logger.h \
		thread.h CtiPCPtrQueue.h
tbl_port_settings.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_port_settings.h dbmemobject.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h ctitime.h queues.h numstr.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h sema.h row_reader.h logger.h \
		thread.h CtiPCPtrQueue.h
tbl_port_tcpip.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_port_tcpip.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_port_timing.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_port_timing.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_ptdispatch.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h tbl_ptdispatch.h ctibase.h \
		ctinexus.h dbmemobject.h pointdefs.h row_reader.h \
		database_connection.h database_reader.h database_writer.h \
		row_writer.h ctidate.h
tbl_pthist.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_pthist.h \
		dlldefs.h dbmemobject.h ctibase.h ctinexus.h netports.h \
		cticonnect.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h dsm2err.h \
		words.h optional.h row_reader.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h database_connection.h \
		database_reader.h
tbl_pt_accum.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_pt_accum.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_pt_accumhistory.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_pt_accumhistory.h dbmemobject.h row_reader.h ctitime.h \
		dlldefs.h dbaccess.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h \
		database_connection.h database_reader.h database_writer.h \
		row_writer.h
tbl_pt_alarm.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		row_reader.h ctitime.h dlldefs.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h logger.h thread.h CtiPCPtrQueue.h \
		tbl_pt_alarm.h dbmemobject.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h
tbl_pt_analog.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_pt_analog.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_pt_base.obj:	yukon.h precompiled.h types.h ctidbgmem.h logger.h \
		dlldefs.h thread.h mutex.h guard.h utility.h ctitime.h \
		queues.h cticalls.h os2_2w32.h numstr.h CtiPCPtrQueue.h \
		resolvers.h pointtypes.h db_entry_defines.h tbl_pt_base.h \
		row_reader.h dllbase.h dsm2.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h dbmemobject.h dbaccess.h sema.h \
		desolvers.h pointdefs.h
tbl_pt_calc.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_pt_calc.h
tbl_pt_control.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_pt_control.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_pt_limit.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_pt_limit.h dlldefs.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h row_reader.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_pt_property.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_pt_property.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_pt_status.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		pointdefs.h tbl_pt_status.h row_reader.h ctitime.h dlldefs.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h dbmemobject.h dbaccess.h sema.h \
		resolvers.h pointtypes.h db_entry_defines.h desolvers.h \
		logger.h thread.h CtiPCPtrQueue.h
tbl_pt_trigger.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_pt_trigger.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_pt_unit.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_pt_unit.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h tbl_unitmeasure.h \
		logger.h thread.h CtiPCPtrQueue.h
tbl_route.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_route.h \
		dllbase.h os2_2w32.h dlldefs.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h dbmemobject.h \
		row_reader.h logger.h thread.h CtiPCPtrQueue.h
tbl_rtcarrier.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_rtcarrier.h row_reader.h ctitime.h dlldefs.h dbaccess.h \
		dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h guard.h \
		utility.h queues.h numstr.h cticonnect.h netports.h dsm2err.h \
		words.h optional.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h dbmemobject.h database_connection.h \
		database_reader.h
tbl_rtcomm.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_rtcomm.h \
		row_reader.h ctitime.h dlldefs.h dbaccess.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h sema.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h logger.h thread.h CtiPCPtrQueue.h dbmemobject.h \
		ctibase.h ctinexus.h database_connection.h database_reader.h
tbl_rtmacro.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_rtmacro.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h ctibase.h ctinexus.h database_connection.h \
		database_reader.h
tbl_rtrepeater.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_rtrepeater.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbaccess.h sema.h logger.h thread.h \
		CtiPCPtrQueue.h
tbl_rtroute.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_rtroute.h dlldefs.h dbmemobject.h ctibase.h ctinexus.h \
		netports.h cticonnect.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		dsm2err.h words.h optional.h row_reader.h dbaccess.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h resolvers.h pointtypes.h \
		db_entry_defines.h database_connection.h database_reader.h
tbl_rtversacom.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h tbl_rtversacom.h \
		dbmemobject.h resolvers.h pointtypes.h db_entry_defines.h \
		row_reader.h database_connection.h database_reader.h
tbl_scanrate.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_scanrate.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h database_connection.h database_reader.h
tbl_state.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h tbl_state.h \
		row_reader.h logger.h thread.h CtiPCPtrQueue.h \
		database_connection.h database_reader.h
tbl_state_grp.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		tbl_state_grp.h rwutil.h database_connection.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		tbl_state.h logger.h thread.h CtiPCPtrQueue.h
tbl_static_paoinfo.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		logger.h thread.h CtiPCPtrQueue.h tbl_static_paoinfo.h \
		ctibase.h ctinexus.h dbmemobject.h pointdefs.h \
		database_connection.h rwutil.h database_reader.h row_reader.h \
		boost_time.h boostutil.h database_writer.h row_writer.h
tbl_stats.obj:	yukon.h precompiled.h types.h ctidbgmem.h tbl_stats.h \
		row_reader.h ctitime.h dlldefs.h dllbase.h os2_2w32.h \
		cticalls.h dsm2.h mutex.h guard.h utility.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		dbmemobject.h dbaccess.h sema.h resolvers.h pointtypes.h \
		db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h ctidate.h
tbl_tag.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h logger.h \
		thread.h CtiPCPtrQueue.h tbl_tag.h ctibase.h ctinexus.h \
		dbmemobject.h row_reader.h
tbl_taglog.obj:	yukon.h precompiled.h types.h ctidbgmem.h dbaccess.h \
		dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h mutex.h \
		guard.h utility.h ctitime.h queues.h numstr.h cticonnect.h \
		netports.h dsm2err.h words.h optional.h sema.h logger.h \
		thread.h CtiPCPtrQueue.h tbl_taglog.h ctibase.h ctinexus.h \
		dbmemobject.h pointdefs.h row_reader.h database_connection.h \
		database_reader.h database_writer.h row_writer.h
tbl_unitmeasure.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		tbl_unitmeasure.h row_reader.h ctitime.h dlldefs.h dllbase.h \
		os2_2w32.h cticalls.h dsm2.h mutex.h guard.h utility.h \
		queues.h numstr.h cticonnect.h netports.h dsm2err.h words.h \
		optional.h dbmemobject.h dbaccess.h sema.h resolvers.h \
		pointtypes.h db_entry_defines.h desolvers.h logger.h thread.h \
		CtiPCPtrQueue.h
test_tbl_dv_idlcremote.obj:	tbl_dv_idlcremote.h rwutil.h yukon.h \
		precompiled.h types.h ctidbgmem.h database_connection.h \
		dbaccess.h dlldefs.h dllbase.h os2_2w32.h cticalls.h dsm2.h \
		mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h sema.h \
		database_reader.h row_reader.h boost_time.h boostutil.h \
		dbmemobject.h
#ENDUPDATE#
