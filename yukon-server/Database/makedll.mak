include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(MSG)\include \
-I$(PROT)\include \
-I$(CPARMS)\include \
-I$(RW) \


.PATH.cpp = .

.PATH.H = \
.\include \
;$(COMMON)\include \
;$(DATABASE)\include \
;$(SCANNER)\include \
;$(CPARMS)\include \
;$(SERVICE)\include \
;$(SERVER)\include \
;$(PIL)\include \
;$(PROT)\include \
;$(PROCLOG)\include \
;$(DISPATCH)\include \
;$(MSG)\include \
;$(RW)



YUKONDLLOBJS=\
dllyukon.obj \
tbl_2way.obj \
tbl_alm_ndest.obj \
tbl_alm_ngroup.obj \
tbl_base.obj \
tbl_carrier.obj \
tbl_ci_cust.obj \
tbl_commerrhist.obj \
tbl_contact_notification.obj \
tbl_dialup.obj \
tbl_direct.obj \
tbl_dv_cbc.obj \
tbl_dv_dnp.obj \
tbl_dv_emetcon.obj \
tbl_dv_expresscom.obj \
tbl_dv_idlcremote.obj \
tbl_dv_ied.obj \
tbl_dv_lmvcserial.obj \
tbl_dv_lmg_ripple.obj \
tbl_dv_mctiedport.obj \
tbl_dv_scandata.obj \
tbl_dv_tappaging.obj \
tbl_dv_versacom.obj \
tbl_dv_wnd.obj \
tbl_lm_controlhist.obj \
tbl_loadprofile.obj \
tbl_metergrp.obj \
tbl_pao.obj \
tbl_pt_accum.obj \
tbl_port_base.obj \
tbl_port_dialup.obj \
tbl_port_serial.obj \
tbl_port_settings.obj \
tbl_port_statistics.obj \
tbl_port_tcpip.obj \
tbl_port_timing.obj \
tbl_pt_accumhistory.obj \
tbl_pt_alarm.obj \
tbl_pt_analog.obj \
tbl_pt_base.obj \
tbl_pt_limit.obj \
tbl_pt_status.obj \
tbl_pt_unit.obj \
tbl_route.obj \
tbl_rtcarrier.obj \
tbl_rtcomm.obj \
tbl_rtmacro.obj \
tbl_rtrepeater.obj \
tbl_rtroute.obj \
tbl_rtversacom.obj \
tbl_pthist.obj \
tbl_ptdispatch.obj \
tbl_scanrate.obj \
tbl_state.obj \
tbl_state_grp.obj \
tbl_stats.obj \
tbl_unitmeasure.obj \




DBLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib \



CTIPROGS=\
ctidbsrc.dll \


ALL:            $(CTIPROGS)

ctidbsrc.dll:   $(YUKONDLLOBJS) Makefile
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ \
$(YUKONDLLOBJS) -link $(RWLIBS) $(DBLIBS)
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



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) /DCTIYUKONDB -Fo$(OBJ)\ -c $<


######################################################################################
