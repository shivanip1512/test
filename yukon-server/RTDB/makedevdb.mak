include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(DATABASE)\include \
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
device.obj \
dev_710.obj \
dev_base.obj \
dev_carrier.obj \
dev_macro.obj \
dev_cbc.obj \
dev_cbc6510.obj \
dev_ccu.obj \
dev_dnp.obj \
dev_dct501.obj \
dev_davis.obj \
dev_dlcbase.obj \
dev_grp_emetcon.obj \
dev_grp_expresscom.obj \
dev_grp_ripple.obj \
dev_grp_versacom.obj \
dev_ilex.obj \
dev_lcu.obj \
dev_lgs4.obj \
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
dev_mct_broadcast.obj \
dev_mct_lmt2.obj \
dev_repeater.obj \
dev_repeater800.obj \
dev_single.obj \
dev_sixnet.obj \
dev_system.obj \
dev_tap.obj \
dev_tcu.obj \
dev_wctp.obj \
dev_welco.obj \
dlldev.obj \
mgr_device.obj \
mgr_route.obj \
rte_versacom.obj \
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



DEVDBLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctiprot.lib \
$(COMPILEBASE)\lib\ctidbsrc.lib \
$(COMPILEBASE)\lib\ctipntdb.lib \
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\$(XERCES_LIB).lib \



CTIPROGS=\
ctidevdb.dll


ALL:            $(CTIPROGS)


ctidevdb.dll:   $(YUKONDEVDLLOBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(YUKONDEVDLLOBJS) id_devdll.obj -link $(RWLIBS) $(DEVDBLIBS)
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
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) /D_DLL_DEVDB -Fo$(OBJ)\ -c $<


######################################################################################



#UPDATE#
#ENDUPDATE#

