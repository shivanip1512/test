
# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DATABASE)\include \
-I$(PORTER)\include \
-I$(COMMON)\include \
-I$(RW) \
-I$(BOOST) \

.PATH.cpp = .

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
tabletest.obj \
tbl_rtroute.obj \
tbl_pthist.obj

TABLETESTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\clrdump.lib

EXECS=\
tabletest.exe \
almtest.exe


ALL:            $(EXECS)

tabletest.exe:  $(BASEOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
$(BASEOBJS) -link $(LIBS) $(RWLIBS) $(BOOSTLIBS) $(TABLETESTLIBS)
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@copy ..\$@ $(YUKONOUTPUT)
                @echo Done building Target ..\$@
                @%cd $(CWD)

almtest.exe:    almtest.obj Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(CFLAGS) $(INCLPATHS) $(RWLINKFLAGS) /Fe..\$@ \
almtest.obj -link $(LIBS) $(RWLIBS) $(BOOSTLIBS) $(TABLETESTLIBS) $(COMPILEBASE)\lib\ctidbsrc.lib
                @echo:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
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
almtest.obj:	tbl_pt_alarm.h dbmemobject.h
dllyukon.obj:	tbl_route.h dbmemobject.h tbl_rtcarrier.h tbl_rtcomm.h \
		tbl_rtmacro.h tbl_rtroute.h tbl_rtrepeater.h tbl_rtversacom.h \
		tbl_state_grp.h tbl_state.h
tabletest.obj:	tbl_pthist.h dbmemobject.h tbl_rtroute.h
tbl_2way.obj:	tbl_2way.h dbmemobject.h
tbl_alm_ndest.obj:	tbl_alm_ndest.h
tbl_alm_ngroup.obj:	tbl_alm_ngroup.h tbl_alm_ndest.h
tbl_alm_nloc.obj:	tbl_alm_nloc.h
tbl_base.obj:	tbl_base.h dbmemobject.h
tbl_carrier.obj:	tbl_carrier.h dbmemobject.h
tbl_ci_cust.obj:	tbl_ci_cust.h
tbl_commerrhist.obj:	tbl_commerrhist.h dbmemobject.h
tbl_contact_notification.obj:	tbl_contact_notification.h
tbl_devicereadjoblog.obj:	tbl_devicereadjoblog.h
tbl_devicereadrequestlog.obj:	tbl_devicereadrequestlog.h
tbl_dialup.obj:	tbl_dialup.h dbmemobject.h
tbl_direct.obj:	tbl_direct.h dbmemobject.h
tbl_dv_address.obj:	tbl_dv_address.h dbmemobject.h
tbl_dv_cbc.obj:	tbl_dv_cbc.h dbmemobject.h
tbl_dv_cicust.obj:	tbl_dv_cicust.h
tbl_dv_emetcon.obj:	tbl_dv_emetcon.h dbmemobject.h
tbl_dv_expresscom.obj:	tbl_dv_expresscom.h dbmemobject.h
tbl_dv_idlcremote.obj:	tbl_dv_idlcremote.h dbmemobject.h
tbl_dv_ied.obj:	tbl_dv_ied.h dbmemobject.h
tbl_dv_lmgmct.obj:	tbl_dv_lmgmct.h
tbl_dv_lmg_ripple.obj:	tbl_dv_lmg_ripple.h dbmemobject.h
tbl_dv_lmvcserial.obj:	tbl_dv_lmvcserial.h dbmemobject.h
tbl_dv_mctiedport.obj:	tbl_dv_mctiedport.h dbmemobject.h
tbl_dv_pagingreceiver.obj:	tbl_dv_pagingreceiver.h dbmemobject.h
tbl_dv_rtc.obj:	tbl_dv_rtc.h
tbl_dv_scandata.obj:	tbl_dv_scandata.h dbmemobject.h
tbl_dv_seriesv.obj:	tbl_dv_seriesv.h dbmemobject.h
tbl_dv_tappaging.obj:	tbl_dv_tappaging.h dbmemobject.h
tbl_dv_tnpp.obj:	tbl_dv_tnpp.h dbmemobject.h
tbl_dv_versacom.obj:	tbl_dv_versacom.h dbmemobject.h
tbl_dv_wnd.obj:	tbl_dv_wnd.h dbmemobject.h
tbl_dyn_paoinfo.obj:	tbl_dyn_paoinfo.h dbmemobject.h
tbl_dyn_ptalarming.obj:	tbl_dyn_ptalarming.h dbmemobject.h
tbl_dyn_pttag.obj:	tbl_dyn_pttag.h dbmemobject.h
tbl_gateway_end_device.obj:	tbl_gateway_end_device.h dbmemobject.h
tbl_lmg_golay.obj:	tbl_lmg_golay.h dbmemobject.h
tbl_lmg_point.obj:	tbl_lmg_point.h dbmemobject.h
tbl_lmg_sa205105.obj:	tbl_lmg_sa205105.h dbmemobject.h
tbl_lmg_sa305.obj:	tbl_lmg_sa305.h dbmemobject.h
tbl_lmg_sasimple.obj:	tbl_lmg_sasimple.h dbmemobject.h
tbl_lm_controlhist.obj:	tbl_lm_controlhist.h dbmemobject.h
tbl_loadprofile.obj:	tbl_loadprofile.h dbmemobject.h
tbl_metergrp.obj:	tbl_metergrp.h dbmemobject.h
tbl_meterreadlog.obj:	tbl_meterreadlog.h
tbl_pao.obj:	tbl_pao.h dbmemobject.h
tbl_paoexclusion.obj:	tbl_paoexclusion.h
tbl_pao_lite.obj:	tbl_pao_lite.h dbmemobject.h
tbl_port_base.obj:	tbl_port_base.h dbmemobject.h
tbl_port_dialup.obj:	tbl_port_dialup.h dbmemobject.h
tbl_port_serial.obj:	tbl_port_serial.h dbmemobject.h
tbl_port_settings.obj:	tbl_port_settings.h dbmemobject.h
tbl_port_statistics.obj:	tbl_port_statistics.h dbmemobject.h
tbl_port_tcpip.obj:	tbl_port_tcpip.h dbmemobject.h
tbl_port_timing.obj:	tbl_port_timing.h dbmemobject.h
tbl_ptdispatch.obj:	tbl_ptdispatch.h dbmemobject.h
tbl_pthist.obj:	tbl_pthist.h dbmemobject.h
tbl_pt_accum.obj:	tbl_pt_accum.h dbmemobject.h
tbl_pt_accumhistory.obj:	tbl_pt_accumhistory.h dbmemobject.h
tbl_pt_alarm.obj:	tbl_pt_alarm.h dbmemobject.h
tbl_pt_analog.obj:	tbl_pt_analog.h dbmemobject.h
tbl_pt_base.obj:	tbl_pt_base.h dbmemobject.h
tbl_pt_calc.obj:	tbl_pt_calc.h
tbl_pt_control.obj:	tbl_pt_control.h dbmemobject.h
tbl_pt_limit.obj:	tbl_pt_limit.h dbmemobject.h
tbl_pt_property.obj:	tbl_pt_property.h dbmemobject.h
tbl_pt_status.obj:	tbl_pt_status.h dbmemobject.h
tbl_pt_trigger.obj:	tbl_pt_trigger.h dbmemobject.h
tbl_pt_unit.obj:	tbl_pt_unit.h dbmemobject.h tbl_unitmeasure.h
tbl_route.obj:	tbl_route.h dbmemobject.h
tbl_rtcarrier.obj:	tbl_rtcarrier.h dbmemobject.h
tbl_rtcomm.obj:	tbl_rtcomm.h dbmemobject.h
tbl_rtmacro.obj:	tbl_rtmacro.h dbmemobject.h
tbl_rtrepeater.obj:	tbl_rtrepeater.h dbmemobject.h
tbl_rtroute.obj:	tbl_rtroute.h dbmemobject.h
tbl_rtversacom.obj:	tbl_rtversacom.h dbmemobject.h
tbl_scanrate.obj:	tbl_scanrate.h dbmemobject.h
tbl_state.obj:	tbl_state.h
tbl_state_grp.obj:	tbl_state_grp.h tbl_state.h
tbl_stats.obj:	tbl_stats.h dbmemobject.h
tbl_tag.obj:	tbl_tag.h dbmemobject.h
tbl_taglog.obj:	tbl_taglog.h dbmemobject.h
tbl_unitmeasure.obj:	tbl_unitmeasure.h dbmemobject.h
#ENDUPDATE#
