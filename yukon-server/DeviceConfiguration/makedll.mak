# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(COMMON)\include \
-I$(PIL)\include \
-I$(SERVER)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(CPARMS)\include \
-I$(MSG)\include \
-I$(BOOST) \
-I$(RW) \


.PATH.cpp = .

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
;$(BOOST) \
;$(RW)

LIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctidevdb.lib \

DLLOBJS = \
config_base.obj \
config_device.obj \
config_resolvers.obj \
mgr_config.obj \



CTIPROGS=\
configmgr.dll


ALL:            $(CTIPROGS)


configmgr.dll:   $(DLLOBJS) Makedll.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(CC) $(RWCPPFLAGS) $(DLLFLAGS) $(DLLOBJS) id_dcdll.obj $(INCLPATHS) /Fe..\$@ -link $(LIBS) $(COMPILEBASE)\lib\tcpsup.lib $(COMPILEBASE)\lib\ctibase.lib $(COMPILEBASE)\lib\ctiprtdb.lib $(COMPILEBASE)\lib\ctidbsrc.lib $(RWLIBS) $(BOOSTLIBS) 
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


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp

deps:
                scandeps -Output makedll.mak *.cpp


# The lines below accomplish the ID'ing of the project!
id:
            @$(MAKE) -nologo -f $(_InputFile) id_dcdll.obj

id_dcdll.obj:    id_dcdll.cpp include\id_dcdll.h id_vinfo.h

.SUFFIXES:      .cpp .obj

########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_CONFIG -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
config_base.obj:	yukon.h precompiled.h ctidbgmem.h config_base.h \
		logger.h dlldefs.h thread.h mutex.h guard.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h
config_device.obj:	yukon.h precompiled.h ctidbgmem.h config_device.h \
		logger.h dlldefs.h thread.h mutex.h guard.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h config_base.h
config_resolvers.obj:	yukon.h precompiled.h ctidbgmem.h \
		config_resolvers.h logger.h dlldefs.h thread.h mutex.h \
		guard.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h
id_dcdll.obj:	yukon.h precompiled.h ctidbgmem.h id_dcdll.h utility.h \
		dsm2.h mutex.h dlldefs.h guard.h id_build.h id_vinfo.h
mgr_config.obj:	yukon.h precompiled.h ctidbgmem.h dbaccess.h dlldefs.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h sema.h config_resolvers.h logger.h thread.h \
		dev_base.h cmdparse.h parsevalue.h counter.h dev_exclusion.h \
		tbl_paoexclusion.h rte_base.h dbmemobject.h ctibase.h \
		ctinexus.h netports.h message.h collectable.h tbl_pao.h \
		tbl_rtcomm.h resolvers.h pointtypes.h db_entry_defines.h \
		desolvers.h msg_signal.h tbl_base.h tbl_2way.h tbl_stats.h \
		tbl_scanrate.h tbl_dyn_paoinfo.h pointdefs.h queues.h \
		utility.h mgr_config.h config_base.h config_device.h \
		mgr_device.h rtdb.h hashkey.h slctdev.h smartmap.h
#ENDUPDATE#
