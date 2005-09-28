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

DLLOBJS = \
config_base.obj \
config_device.obj \
config_resolvers.obj \
config_type_general.obj \
config_type_mct_tou.obj \
config_type_mct_vthreshold.obj \
config_type_mct_demand_LP.obj \
config_type_mct_configuration.obj \
config_type_mct_addressing.obj \
config_type_mct_dst.obj \


CTIPROGS=\
cticonfig.dll


ALL:            $(CTIPROGS)


cticonfig.dll:   $(DLLOBJS) Makedll.mak
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(CC) $(RWCPPFLAGS) $(DLLFLAGS) $(DLLOBJS) id_dcdll.obj $(INCLPATHS) /Fe..\$@ -link $(LIBS) $(RWLIBS) $(BOOSTLIBS) 
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
config_test_a.obj:	yukon.h precompiled.h ctidbgmem.h config_test_a.h \
		config_base.h logger.h dlldefs.h thread.h mutex.h guard.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h
config_type_general.obj:	yukon.h precompiled.h ctidbgmem.h \
		config_type_general.h config_base.h logger.h dlldefs.h \
		thread.h mutex.h guard.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h
config_type_mct_addressing.obj:	yukon.h precompiled.h ctidbgmem.h \
		config_type_mct_addressing.h config_base.h logger.h dlldefs.h \
		thread.h mutex.h guard.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h
config_type_mct_configuration.obj:	yukon.h precompiled.h ctidbgmem.h \
		config_type_mct_configuration.h config_base.h logger.h \
		dlldefs.h thread.h mutex.h guard.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h
config_type_mct_demand_lp.obj:	yukon.h precompiled.h ctidbgmem.h \
		config_type_mct_demand_LP.h config_base.h logger.h dlldefs.h \
		thread.h mutex.h guard.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h
config_type_mct_dst.obj:	yukon.h precompiled.h ctidbgmem.h \
		config_type_mct_dst.h config_base.h logger.h dlldefs.h \
		thread.h mutex.h guard.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h
config_type_mct_tou.obj:	yukon.h precompiled.h ctidbgmem.h \
		config_type_mct_tou.h config_base.h logger.h dlldefs.h \
		thread.h mutex.h guard.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h
config_type_mct_vthreshold.obj:	yukon.h precompiled.h ctidbgmem.h \
		config_type_mct_vthreshold.h config_base.h logger.h dlldefs.h \
		thread.h mutex.h guard.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h
id_dcdll.obj:	yukon.h precompiled.h ctidbgmem.h id_dcdll.h utility.h \
		dsm2.h mutex.h dlldefs.h guard.h id_build.h id_vinfo.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
#ENDUPDATE#
