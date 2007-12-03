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
$(COMPILEBASE)\lib\clrdump.lib

DLLOBJS = \
config_device.obj \
config_strings.obj \


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
config_device.obj:	yukon.h precompiled.h ctidbgmem.h config_device.h \
		logger.h dlldefs.h thread.h mutex.h guard.h numstr.h \
		clrdump.h ctitime.h CtiPCPtrQueue.h utility.h queues.h \
		types.h sorted_vector.h dllbase.h os2_2w32.h cticalls.h \
		dsm2.h cticonnect.h netports.h hashkey.h hash_functions.h
config_resolvers.obj:	yukon.h precompiled.h ctidbgmem.h rwutil.h \
		ctitime.h dlldefs.h boost_time.h logger.h thread.h mutex.h \
		guard.h numstr.h clrdump.h CtiPCPtrQueue.h utility.h queues.h \
		types.h sorted_vector.h
config_strings.obj:	yukon.h precompiled.h ctidbgmem.h \
		config_data_cbc.h dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h dsm2.h mutex.h guard.h numstr.h clrdump.h \
		cticonnect.h netports.h config_data_mct.h
config_test_a.obj:	yukon.h precompiled.h ctidbgmem.h config_test_a.h
id_dcdll.obj:	yukon.h precompiled.h ctidbgmem.h id_dcdll.h utility.h \
		ctitime.h dlldefs.h queues.h types.h numstr.h sorted_vector.h \
		id_vinfo.h
precompiled.obj:	yukon.h precompiled.h ctidbgmem.h
#ENDUPDATE#
