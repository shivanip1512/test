include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(DEVICECONFIGURATION)\include \
-I$(PORTER)\include \
-I$(MSG)\include \
-I$(PROT)\include \
-I$(CPARMS)\include \
-I$(PROCLOG)\include \
-I$(RW) \
-I$(BOOST) \
-I$(XERCESINC) \


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
;$(PROT)\include \
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(TCLINC) \
;$(BOOST) \
;$(RW)


YUKONDEVDLLOBJS=\
mgr_config.obj \
device.obj \
dev_710.obj \
dev_base.obj \
dev_carrier.obj \
dev_macro.obj \
dev_cbc.obj \
dev_cbc6510.obj \
dev_cbc7020.obj \
dev_ccu.obj \
dev_ccu721.obj \
dev_dnp.obj \
dev_dct501.obj \
dev_davis.obj \
dev_dlcbase.obj \
dev_exclusion.obj \
dev_gateway.obj \
dev_grp_emetcon.obj \
dev_grp_expresscom.obj \
dev_grp_energypro.obj \
dev_grp_golay.obj \
dev_grp_mct.obj \
dev_grp_point.obj \
dev_grp_ripple.obj \
dev_grp_sadigital.obj \
dev_grp_sa105.obj \
dev_grp_sa205.obj \
dev_grp_sa305.obj \
dev_grp_versacom.obj \
dev_gwstat.obj \
dev_ilex.obj \
dev_lcu.obj \
dev_lgs4.obj \
dev_lmi.obj \
dev_dr87.obj \
dev_ion.obj \
dev_meter.obj \
dev_mct.obj \
dev_mct2XX.obj \
dev_mct210.obj \
dev_mct22X.obj \
dev_mct24X.obj \
dev_mct310.obj \
dev_mct31X.obj \
dev_mct4xx.obj \
dev_mct410.obj \
dev_mct470.obj \
dev_mct_broadcast.obj \
dev_mct_lmt2.obj \
dev_modbus.obj \
dev_pagingreceiver.obj \
dev_repeater.obj \
dev_repeater800.obj \
dev_rtc.obj \
dev_rtm.obj \
dev_seriesv.obj \
dev_single.obj \
dev_sixnet.obj \
dev_snpp.obj \
dev_tnpp.obj \
dev_system.obj \
dev_tap.obj \
dev_tcu.obj \
dev_wctp.obj \
dev_welco.obj \
device_queue_interface.obj \
dlldev.obj \
mgr_device.obj \
mgr_exclusion.obj \
mgr_route.obj \
rte_versacom.obj \
rte_expresscom.obj \
rte_ccu.obj \
rte_xcu.obj \
rte_macro.obj \
slctdev.obj \
dev_alpha.obj \
dev_a1.obj \
dev_aplus.obj \
dev_fulcrum.obj \
dev_quantum.obj \
dev_schlum.obj \
dev_vectron.obj \
dev_kv2.obj \
dev_sentinel.obj \
dev_mark_v.obj \
dev_foreignporter.obj \
dev_fmu.obj \



DEVDBLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\cticonfig.lib \
$(COMPILEBASE)\lib\$(XERCES_LIB).lib \



CTIPROGS=\
ctidevdb.dll


ALL:            $(CTIPROGS)


ctidevdb.dll:   $(YUKONDEVDLLOBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(YUKONDEVDLLOBJS) id_devdll.obj -link $(RWLIBS) $(DEVDBLIBS) $(BOOSTLIBS)
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
                scandeps -DirInc -Output makedevdb.mak *.cpp


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
            @$(MAKE) -nologo -f $(_InputFile) id_devdll.obj

id_devdll.obj:    id_devdll.cpp include\id_devdll.h id_vinfo.h



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_DEVDB -Fo$(OBJ)\ -c $<


######################################################################################



#UPDATE#
device.obj:	dev_carrier.h dev_dlcbase.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		dev_remote.h dev_meter.h dev_ied.h
device_queue_interface.obj:	trx_711.h trx_info.h \
		device_queue_interface.h
devtest.obj:	mgr_device.h dev_base.h dev_exclusion.h rte_base.h \
		pt_base.h pt_dyn_base.h slctdev.h
dev_710.obj:	dev_710.h dev_idlc.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h trx_info.h \
		trx_711.h
dev_a1.obj:	dev_alpha.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h dev_a1.h pt_status.h \
		pt_analog.h pt_numeric.h pt_accum.h
dev_alpha.obj:	dev_alpha.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h pt_status.h pt_analog.h \
		pt_numeric.h pt_accum.h
dev_aplus.obj:	dev_alpha.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h dev_aplus.h pt_status.h \
		pt_analog.h pt_numeric.h pt_accum.h
dev_base.obj:	dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_route.h slctdev.h mgr_point.h slctpnt.h
dev_base_lite.obj:	dev_base_lite.h
dev_carrier.obj:	dev_carrier.h dev_dlcbase.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h
dev_cbc.obj:	pt_base.h pt_dyn_base.h mgr_route.h rte_base.h slctdev.h \
		dev_base.h dev_exclusion.h mgr_point.h slctpnt.h dev_cbc.h
dev_cbc6510.obj:	pt_base.h pt_dyn_base.h pt_numeric.h mgr_route.h \
		rte_base.h slctdev.h dev_base.h dev_exclusion.h mgr_point.h \
		slctpnt.h dev_cbc6510.h dev_remote.h dev_single.h dev_dnp.h
dev_cbc7020.obj:	dev_cbc7020.h dev_dnp.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h
dev_ccu.obj:	pt_base.h pt_dyn_base.h dev_ccu.h dev_idlc.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h trx_info.h \
		trx_711.h mgr_point.h slctpnt.h device_queue_interface.h
dev_ccu721.obj:	dev_ccu721.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		pt_numeric.h pt_status.h
dev_davis.obj:	dev_davis.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		mgr_point.h slctpnt.h pt_status.h pt_analog.h pt_numeric.h \
		pt_accum.h
dev_dct501.obj:	dev_dct501.h dev_mct24x.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		pt_numeric.h mgr_point.h slctpnt.h
dev_dlcbase.obj:	dev_dlcbase.h dev_single.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h dev_mct.h dev_carrier.h \
		pt_numeric.h
dev_dnp.obj:	pt_base.h pt_dyn_base.h pt_numeric.h pt_status.h \
		pt_accum.h mgr_route.h rte_base.h slctdev.h dev_base.h \
		dev_exclusion.h mgr_point.h slctpnt.h dev_dnp.h dev_remote.h \
		dev_single.h
dev_dr87.obj:	dev_ied.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h dev_dr87.h \
		dev_meter.h mgr_point.h slctpnt.h pt_status.h pt_analog.h \
		pt_numeric.h pt_accum.h
dev_exclusion.obj:	dev_exclusion.h
dev_fmu.obj:	dev_fmu.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h
dev_foreignporter.obj:	dev_foreignporter.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h
dev_fulcrum.obj:	dev_schlum.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h dev_fulcrum.h pt_status.h \
		pt_analog.h pt_numeric.h pt_accum.h
dev_gateway.obj:	dev_gateway.h dev_gwstat.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h
dev_grp_emetcon.obj:	pt_base.h pt_dyn_base.h pt_status.h mgr_route.h \
		rte_base.h slctdev.h dev_base.h dev_exclusion.h \
		dev_grp_emetcon.h dev_grp.h pt_analog.h pt_numeric.h
dev_grp_energypro.obj:	dev_grp_energypro.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h dev_grp.h pt_status.h \
		pt_analog.h pt_numeric.h mgr_route.h slctdev.h
dev_grp_expresscom.obj:	dev_grp_expresscom.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h dev_grp.h \
		pt_status.h pt_analog.h pt_numeric.h mgr_route.h slctdev.h
dev_grp_golay.obj:	dev_grp_golay.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h dev_grp.h pt_status.h \
		pt_analog.h pt_numeric.h mgr_route.h slctdev.h
dev_grp_mct.obj:	dev_grp_mct.h dev_grp.h pt_status.h pt_base.h \
		pt_dyn_base.h pt_analog.h pt_numeric.h dev_base.h \
		dev_exclusion.h rte_base.h
dev_grp_point.obj:	dev_grp_point.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h dev_grp.h pt_status.h \
		pt_analog.h pt_numeric.h mgr_route.h slctdev.h
dev_grp_ripple.obj:	dev_grp_ripple.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h dev_grp.h pt_status.h \
		pt_analog.h pt_numeric.h mgr_route.h slctdev.h
dev_grp_sa105.obj:	dev_grp_sa105.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h dev_grp.h pt_status.h \
		pt_analog.h pt_numeric.h mgr_route.h slctdev.h
dev_grp_sa205.obj:	dev_grp_sa205.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h dev_grp.h pt_status.h \
		pt_analog.h pt_numeric.h mgr_route.h slctdev.h
dev_grp_sa305.obj:	dev_grp_sa305.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h dev_grp.h pt_status.h \
		pt_analog.h pt_numeric.h mgr_route.h slctdev.h
dev_grp_sadigital.obj:	dev_grp_sadigital.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h dev_grp.h pt_status.h \
		pt_analog.h pt_numeric.h mgr_route.h slctdev.h
dev_grp_versacom.obj:	pt_base.h pt_dyn_base.h pt_status.h mgr_route.h \
		rte_base.h slctdev.h dev_base.h dev_exclusion.h \
		dev_grp_versacom.h dev_grp.h pt_analog.h pt_numeric.h
dev_gwstat.obj:	dev_gwstat.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		pt_numeric.h
dev_ilex.obj:	dev_ilex.h dev_idlc.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		trx_info.h trx_711.h mgr_point.h slctpnt.h pt_status.h \
		pt_analog.h pt_numeric.h pt_accum.h
dev_ion.obj:	dev_ion.h dev_meter.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		mgr_point.h slctpnt.h pt_status.h pt_analog.h pt_numeric.h \
		pt_accum.h
dev_kv2.obj:	dev_kv2.h dev_meter.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		mgr_point.h slctpnt.h pt_analog.h pt_numeric.h pt_status.h
dev_lcu.obj:	dev_lcu.h dev_idlc.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h trx_info.h \
		trx_711.h mgr_point.h slctpnt.h pt_accum.h pt_numeric.h
dev_lgs4.obj:	dev_lgs4.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h pt_status.h pt_analog.h \
		pt_numeric.h pt_accum.h
dev_lmi.obj:	dev_lmi.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		pt_numeric.h pt_status.h
dev_macro.obj:	dev_macro.h dev_base.h dev_exclusion.h rte_base.h \
		pt_base.h pt_dyn_base.h dev_grp.h pt_status.h pt_analog.h \
		pt_numeric.h dev_grp_ripple.h
dev_mark_v.obj:	dev_mark_v.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h pt_numeric.h
dev_mct.obj:	dev_mct.h dev_carrier.h dev_dlcbase.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		pt_numeric.h dev_mct210.h dev_mct2xx.h dev_mct31x.h \
		dev_mct310.h dev_mct410.h dev_mct4xx.h dev_mct470.h \
		dev_mct_lmt2.h dev_mct22x.h mgr_point.h slctpnt.h pt_accum.h \
		pt_status.h
dev_mct210.obj:	dev_mct210.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h pt_numeric.h mgr_point.h \
		slctpnt.h
dev_mct22x.obj:	dev_mct22X.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h pt_numeric.h mgr_point.h \
		slctpnt.h
dev_mct24x.obj:	dev_mct24X.h dev_mct2xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h pt_numeric.h mgr_point.h \
		slctpnt.h
dev_mct2xx.obj:	dev_mct2XX.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h pt_numeric.h mgr_point.h slctpnt.h
dev_mct310.obj:	dev_mct310.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h pt_numeric.h mgr_point.h slctpnt.h pt_status.h
dev_mct31x.obj:	dev_mct31X.h dev_mct310.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h pt_numeric.h mgr_point.h \
		slctpnt.h
dev_mct410.obj:	dev_mct410.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h pt_numeric.h mgr_point.h \
		slctpnt.h pt_status.h
dev_mct470.obj:	dev_mct470.h dev_mct4xx.h dev_mct.h dev_carrier.h \
		dev_dlcbase.h dev_single.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h pt_numeric.h mgr_point.h \
		slctpnt.h pt_accum.h pt_status.h
dev_mct4xx.obj:	dev_mct4xx.h dev_mct.h dev_carrier.h dev_dlcbase.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h pt_numeric.h dev_mct470.h dev_mct410.h \
		pt_status.h
dev_mct_broadcast.obj:	dev_mct_broadcast.h dev_dlcbase.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		dev_mct.h dev_carrier.h pt_numeric.h dev_mct31x.h \
		dev_mct310.h dev_mct4xx.h
dev_mct_lmt2.obj:	dev_mct_lmt2.h dev_mct22x.h dev_mct2xx.h dev_mct.h \
		dev_carrier.h dev_dlcbase.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		pt_numeric.h mgr_point.h slctpnt.h
dev_meter.obj:	dev_meter.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h
dev_modbus.obj:	pt_base.h pt_dyn_base.h pt_numeric.h pt_status.h \
		pt_accum.h mgr_route.h rte_base.h slctdev.h dev_base.h \
		dev_exclusion.h mgr_point.h slctpnt.h dev_modbus.h \
		dev_remote.h dev_single.h
dev_pagingreceiver.obj:	dev_rtm.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		pt_accum.h pt_numeric.h port_base.h mgr_route.h slctdev.h \
		dev_pagingreceiver.h
dev_quantum.obj:	dev_schlum.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h dev_quantum.h pt_status.h \
		pt_analog.h pt_numeric.h pt_accum.h
dev_repeater.obj:	dev_repeater.h dev_dlcbase.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		mgr_point.h slctpnt.h
dev_repeater800.obj:	dev_repeater800.h dev_repeater.h dev_dlcbase.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h pt_numeric.h
dev_rtc.obj:	dev_rtc.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		pt_numeric.h pt_status.h pt_accum.h
dev_rtm.obj:	dev_rtm.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h
dev_schlum.obj:	dev_schlum.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h pt_status.h pt_analog.h \
		pt_numeric.h pt_accum.h
dev_sentinel.obj:	dev_sentinel.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h pt_analog.h pt_numeric.h \
		pt_status.h
dev_seriesv.obj:	dev_seriesv.h dev_ied.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h
dev_single.obj:	dev_single.h dev_base.h dev_exclusion.h rte_base.h \
		pt_base.h pt_dyn_base.h mgr_point.h slctpnt.h
dev_sixnet.obj:	dev_sixnet.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h pt_status.h pt_analog.h pt_numeric.h pt_accum.h
dev_snpp.obj:	pt_base.h pt_dyn_base.h pt_accum.h pt_numeric.h \
		port_base.h dev_base.h dev_exclusion.h rte_base.h mgr_route.h \
		slctdev.h dev_snpp.h dev_ied.h dev_remote.h dev_single.h
dev_system.obj:	dev_system.h dev_base.h dev_exclusion.h rte_base.h \
		pt_base.h pt_dyn_base.h rte_xcu.h mgr_route.h slctdev.h
dev_tap.obj:	pt_base.h pt_dyn_base.h pt_accum.h pt_numeric.h \
		mgr_route.h rte_base.h slctdev.h dev_base.h dev_exclusion.h \
		dev_tap.h dev_ied.h dev_remote.h dev_single.h
dev_tcu.obj:	pt_base.h pt_dyn_base.h dev_tcu.h dev_idlc.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h trx_info.h \
		trx_711.h mgr_point.h slctpnt.h
dev_tnpp.obj:	dev_rtm.h dev_ied.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h pt_accum.h \
		pt_numeric.h port_base.h mgr_route.h slctdev.h dev_tnpp.h
dev_vectron.obj:	dev_schlum.h dev_meter.h dev_ied.h dev_remote.h \
		dev_single.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h mgr_point.h slctpnt.h dev_vectron.h pt_status.h \
		pt_analog.h pt_numeric.h pt_accum.h
dev_wctp.obj:	pt_base.h pt_dyn_base.h pt_accum.h pt_numeric.h \
		mgr_route.h rte_base.h slctdev.h dev_base.h dev_exclusion.h \
		dev_wctp.h dev_ied.h dev_remote.h dev_single.h
dev_welco.obj:	dev_welco.h dev_idlc.h dev_remote.h dev_single.h \
		dev_base.h dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		trx_info.h trx_711.h mgr_point.h slctpnt.h pt_status.h \
		pt_analog.h pt_numeric.h pt_accum.h
disable_entry.obj:	disable_entry.h
dlldev.obj:	mgr_device.h dev_base.h dev_exclusion.h rte_base.h \
		pt_base.h pt_dyn_base.h slctdev.h mgr_route.h mgr_point.h \
		slctpnt.h rte_xcu.h rte_macro.h dev_dlcbase.h dev_single.h \
		dev_grp_emetcon.h dev_grp.h pt_status.h pt_analog.h \
		pt_numeric.h
id_devdll.obj:	include\id_devdll.h
id_pntdll.obj:	include\id_pntdll.h
id_prtdll.obj:	include\id_prtdll.h
id_tcpdll.obj:	include\id_tcpdll.h
mgr_config.obj:	mgr_config.h mgr_device.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h slctdev.h
mgr_device.obj:	mgr_device.h dev_base.h dev_exclusion.h rte_base.h \
		pt_base.h pt_dyn_base.h slctdev.h dev_macro.h dev_grp.h \
		pt_status.h pt_analog.h pt_numeric.h dev_cbc.h dev_dnp.h \
		dev_remote.h dev_single.h dev_ion.h dev_meter.h dev_ied.h \
		mgr_point.h slctpnt.h dev_idlc.h trx_info.h trx_711.h \
		dev_carrier.h dev_dlcbase.h dev_lmi.h dev_mct.h dev_mct410.h \
		dev_mct4xx.h dev_modbus.h dev_repeater.h dev_rtc.h dev_rtm.h \
		dev_fmu.h dev_tap.h dev_snpp.h dev_tnpp.h \
		dev_pagingreceiver.h dev_grp_emetcon.h dev_grp_energypro.h \
		dev_grp_expresscom.h dev_grp_golay.h dev_grp_point.h \
		dev_grp_ripple.h dev_grp_sa105.h dev_grp_sa305.h \
		dev_grp_sa205.h dev_grp_sadigital.h dev_grp_versacom.h \
		dev_grp_mct.h dev_mct_broadcast.h
mgr_disabled.obj:	mgr_disabled.h disable_entry.h
mgr_exclusion.obj:	mgr_exclusion.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h
mgr_holiday.obj:	mgr_holiday.h
mgr_point.obj:	pt_base.h pt_dyn_base.h mgr_point.h slctpnt.h \
		pt_accum.h pt_numeric.h pt_analog.h pt_status.h
mgr_port.obj:	mgr_port.h port_base.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h slctprt.h port_direct.h \
		port_serial.h port_dialable.h port_modem.h port_dialout.h \
		port_pool_out.h port_tcpip.h tcpsup.h
mgr_route.obj:	mgr_route.h rte_base.h slctdev.h dev_base.h \
		dev_exclusion.h pt_base.h pt_dyn_base.h rte_xcu.h rte_ccu.h \
		rte_versacom.h dev_remote.h dev_single.h rte_expresscom.h \
		rte_macro.h
mgr_season.obj:	mgr_season.h
points.obj:	pt_accum.h pt_numeric.h pt_base.h pt_dyn_base.h \
		pt_analog.h
pointtest.obj:	mgr_point.h pt_base.h pt_dyn_base.h slctpnt.h
porttest.obj:	mgr_port.h port_base.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h slctprt.h
port_base.obj:	port_base.h dev_base.h dev_exclusion.h rte_base.h \
		pt_base.h pt_dyn_base.h
port_dialable.obj:	port_dialable.h port_base.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		port_modem.h
port_dialin.obj:	port_dialin.h port_base.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h port_dialable.h \
		port_modem.h
port_dialout.obj:	port_dialout.h port_base.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		port_dialable.h port_modem.h
port_direct.obj:	port_direct.h port_serial.h port_base.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		port_dialable.h port_modem.h
port_modem.obj:	port_modem.h port_base.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h
port_pool_out.obj:	port_pool_out.h port_base.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h
port_serial.obj:	port_serial.h port_base.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h
port_tcpip.obj:	port_tcpip.h port_serial.h port_base.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		port_dialable.h port_modem.h tcpsup.h
pttrigger.obj:	mgr_point.h pt_base.h pt_dyn_base.h slctpnt.h \
		pt_dyn_dispatch.h pttrigger.h
pt_base.obj:	pt_base.h pt_dyn_base.h
pt_dyn_dispatch.obj:	pt_dyn_dispatch.h pt_dyn_base.h
pt_numeric.obj:	pt_numeric.h pt_base.h pt_dyn_base.h
pt_status.obj:	pt_status.h pt_base.h pt_dyn_base.h
routetest.obj:	mgr_route.h rte_base.h slctdev.h dev_base.h \
		dev_exclusion.h pt_base.h pt_dyn_base.h
rte_ccu.obj:	rte_xcu.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h dev_remote.h dev_single.h dev_ccu.h dev_idlc.h \
		trx_info.h trx_711.h mgr_point.h slctpnt.h \
		device_queue_interface.h rte_ccu.h
rte_expresscom.obj:	rte_expresscom.h rte_xcu.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h \
		dev_remote.h dev_single.h
rte_macro.obj:	rte_macro.h rte_base.h
rte_versacom.obj:	rte_versacom.h rte_xcu.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h dev_remote.h dev_single.h
rte_xcu.obj:	rte_xcu.h dev_base.h dev_exclusion.h rte_base.h pt_base.h \
		pt_dyn_base.h dev_remote.h dev_single.h dev_tap.h dev_ied.h \
		dev_snpp.h dev_tnpp.h dev_pagingreceiver.h dev_lcu.h \
		dev_idlc.h trx_info.h trx_711.h mgr_point.h slctpnt.h \
		dev_wctp.h
slctdev.obj:	dev_710.h dev_idlc.h dev_remote.h dev_single.h dev_base.h \
		dev_exclusion.h rte_base.h pt_base.h pt_dyn_base.h trx_info.h \
		trx_711.h dev_macro.h dev_grp.h pt_status.h pt_analog.h \
		pt_numeric.h dev_cbc6510.h dev_dnp.h dev_cbc.h dev_cbc7020.h \
		dev_ccu.h mgr_point.h slctpnt.h device_queue_interface.h \
		dev_ccu721.h dev_welco.h dev_ilex.h dev_seriesv.h dev_ied.h \
		dev_lmi.h dev_tcu.h dev_meter.h dev_modbus.h dev_schlum.h \
		dev_fulcrum.h dev_ion.h dev_lcu.h dev_quantum.h dev_vectron.h \
		dev_carrier.h dev_dlcbase.h dev_rtm.h dev_tap.h dev_snpp.h \
		dev_pagingreceiver.h dev_tnpp.h dev_wctp.h dev_grp_emetcon.h \
		dev_grp_expresscom.h dev_grp_energypro.h dev_grp_golay.h \
		dev_grp_mct.h dev_grp_point.h dev_grp_ripple.h \
		dev_grp_sa105.h dev_grp_sa205.h dev_grp_sa305.h \
		dev_grp_sadigital.h dev_grp_versacom.h dev_davis.h \
		dev_system.h dev_aplus.h dev_alpha.h dev_a1.h dev_lgs4.h \
		dev_dr87.h dev_dct501.h dev_mct24x.h dev_mct2xx.h dev_mct.h \
		dev_mct210.h dev_mct22X.h dev_mct310.h dev_mct31X.h \
		dev_mct410.h dev_mct4xx.h dev_mct470.h dev_mct_lmt2.h \
		dev_mct_broadcast.h dev_kv2.h dev_sentinel.h dev_mark_v.h \
		dev_repeater800.h dev_repeater.h dev_rtc.h dev_sixnet.h \
		dev_foreignporter.h rte_macro.h rte_ccu.h rte_xcu.h \
		rte_versacom.h rte_expresscom.h dev_fmu.h slctdev.h
slctpnt.obj:	slctpnt.h pt_base.h pt_dyn_base.h
slctprt.obj:	port_dialout.h port_base.h dev_base.h dev_exclusion.h \
		rte_base.h pt_base.h pt_dyn_base.h port_dialable.h \
		port_modem.h port_dialin.h port_direct.h port_serial.h \
		port_pool_out.h port_tcpip.h tcpsup.h slctprt.h
tcpsup.obj:	tcpsup.h port_base.h dev_base.h dev_exclusion.h rte_base.h \
		pt_base.h pt_dyn_base.h
#ENDUPDATE#

