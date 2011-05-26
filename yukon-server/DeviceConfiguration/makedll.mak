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
-I$(SQLAPI)\include \
-I$(RW) \


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
;$(DISPATCH)\include \
;$(MSG)\include \
;$(BOOST) \
;$(RW)

LIBS=\
$(COMPILEBASE)\lib\ctibase.lib \

DLLOBJS = \
config_device.obj \
config_strings.obj \
da_lp_deviceconfig.obj \


CTIPROGS=\
cticonfig.dll


DEVCONF_FULLBUILD = $[Filename,$(OBJ),DevConfFullBuild,target]



ALL:            $(CTIPROGS)


$(DEVCONF_FULLBUILD) :
        @touch $@
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PARALLEL) $(PCHFLAGS) $(INCLPATHS) /D_DLL_CONFIG -Fo$(OBJ)\ -c $[StrReplace,.obj,.cpp,$(DLLOBJS)]


cticonfig.dll:  $(DEVCONF_FULLBUILD) $(DLLOBJS) Makedll.mak
                @build -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(CC) $(RWCPPFLAGS) $(DLLFLAGS) $(DLLOBJS) id_dcdll.obj $(INCLPATHS) /Fe..\$@ -link $(LIBS) $(RWLIBS) $(BOOST_LIBS) $(LINKFLAGS)
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
            @build -nologo -f $(_InputFile) id_dcdll.obj

id_dcdll.obj:    id_dcdll.cpp include\id_dcdll.h

.SUFFIXES:      .cpp .obj

########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(PCHFLAGS) $(INCLPATHS) /D_DLL_CONFIG -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
config_device.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		config_device.h logger.h dlldefs.h thread.h mutex.h guard.h \
		utility.h ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h dllbase.h dsm2.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h hashkey.h hash_functions.h
config_strings.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		config_data_cbc.h dllbase.h os2_2w32.h dlldefs.h cticalls.h \
		dsm2.h mutex.h guard.h utility.h ctitime.h queues.h numstr.h \
		cticonnect.h netports.h dsm2err.h words.h optional.h \
		config_data_mct.h
da_lp_deviceconfig.obj:	yukon.h precompiled.h types.h ctidbgmem.h \
		da_lp_deviceconfig.h da_load_profile.h config_device.h \
		logger.h dlldefs.h thread.h mutex.h guard.h utility.h \
		ctitime.h queues.h cticalls.h os2_2w32.h numstr.h \
		CtiPCPtrQueue.h dllbase.h dsm2.h cticonnect.h netports.h \
		dsm2err.h words.h optional.h hashkey.h hash_functions.h \
		config_data_mct.h boostutil.h
id_dcdll.obj:	yukon.h precompiled.h types.h ctidbgmem.h id_dcdll.h \
		utility.h ctitime.h dlldefs.h queues.h cticalls.h os2_2w32.h \
		numstr.h
precompiled.obj:	yukon.h precompiled.h types.h ctidbgmem.h
#ENDUPDATE#
