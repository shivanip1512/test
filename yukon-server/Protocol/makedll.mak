include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(PROT)\include \
-I$(COMMON)\include \
-I$(MSG)\include \
-I$(DATABASE)\include \
-I$(RTDB)\include \
-I$(RW) \


.PATH.cpp = .;$(R_PROT)

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
;$(RW)


OBJS=\
prot_emetcon.obj \
prot_versacom.obj \
prot_711.obj \
prot_fpcbc.obj \
prot_sixnet.obj \
prot_dnp.obj \
dnp_application.obj \
dnp_transport.obj \
dnp_datalink.obj \
dll_prot.obj \


PROTLIBS=\
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib


CTIPROGS=\
ctiprot.dll


ALL:           $(CTIPROGS)

ctiprot.dll:   $(OBJS) Makefile
                @$(MAKE) -nologo -f $(_InputFile) id
                @echo:
                @echo Compiling $@
                @%cd $(OBJ)
                $(RWCPPINVOKE) $(INCLPATHS) $(RWLINKFLAGS) $(DLLFLAGS) -Fe..\$@ $(OBJS) id_ctiprot.obj -link $(RWLIBS) $(PROTLIBS)
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

# The lines below accomplish the ID'ing of the project!
id:
            @cid .\include\id_ctiprot.h
            @$(MAKE) -nologo -f $(_InputFile) id_ctiprot.obj

id_ctiprot.obj:    id_ctiprot.cpp include\id_ctiprot.h



########################### Conversions ##############################

.SUFFIXES:      .cpp .obj

.cpp.obj:
        @echo:
        @echo Compiling cpp to obj
        $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) /D_DLL_PROT -Fo$(OBJ)\ -c $<


######################################################################################

#UPDATE#
dll_prot.obj:	yukon.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h dll_prot.h utility.h
dnp_application.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		dnp_application.h message.h collectable.h dnp_objects.h \
		dnp_transport.h dnp_datalink.h dsm2.h xfer.h dialup.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h
dnp_datalink.obj:	logger.h thread.h mutex.h dlldefs.h guard.h porter.h \
		dsm2.h dsm2err.h devicetypes.h queues.h types.h \
		dnp_datalink.h xfer.h dialup.h yukon.h dllbase.h os2_2w32.h \
		cticalls.h
dnp_transport.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		prot_dnp.h pointtypes.h dnp_application.h message.h \
		collectable.h dnp_objects.h dnp_transport.h dnp_datalink.h \
		dsm2.h xfer.h dialup.h yukon.h dllbase.h os2_2w32.h types.h \
		cticalls.h
id_ctiprot.obj:	utility.h dlldefs.h dsm2.h mutex.h guard.h \
		id_ctiprot.h id_vinfo.h
ion_netlayers.obj:	ion_netlayers.h ion_rootclasses.h \
		ion_valuebasictypes.h ctitypes.h guard.h dlldefs.h logger.h \
		thread.h mutex.h
ion_rootclasses.obj:	ion_rootclasses.h ion_valuebasictypes.h \
		ctitypes.h guard.h dlldefs.h logger.h thread.h mutex.h
ion_valuearraytypes.obj:	ion_valuearraytypes.h ion_valuebasictypes.h \
		ion_rootclasses.h ctitypes.h ion_valuestructtypes.h
ion_valuebasictypes.obj:	ion_valuebasictypes.h ion_rootclasses.h \
		ctitypes.h ion_valuestructtypes.h ion_valuearraytypes.h
ion_valuestructtypes.obj:	ion_valuestructtypes.h ion_valuearraytypes.h \
		ion_valuebasictypes.h ion_rootclasses.h ctitypes.h
prot_711.obj:	cticalls.h os2_2w32.h dlldefs.h types.h prot_711.h \
		porter.h dsm2.h mutex.h guard.h dsm2err.h devicetypes.h \
		queues.h logger.h thread.h dllbase.h
prot_dnp.obj:	logger.h thread.h mutex.h dlldefs.h guard.h prot_dnp.h \
		pointtypes.h dnp_application.h message.h collectable.h \
		dnp_objects.h dnp_transport.h dnp_datalink.h dsm2.h xfer.h \
		dialup.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h
prot_emetcon.obj:	cmdparse.h dlldefs.h parsevalue.h devicetypes.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h porter.h dsm2err.h queues.h prot_emetcon.h yukon.h \
		logger.h thread.h
prot_fpcbc.obj:	cmdparse.h dlldefs.h parsevalue.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h devicetypes.h \
		logger.h thread.h master.h msg_pcrequest.h message.h \
		collectable.h prot_fpcbc.h utility.h yukon.h
prot_sixnet.obj:	guard.h dlldefs.h logger.h thread.h mutex.h \
		prot_sixnet.h cmdparse.h parsevalue.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h
prot_versacom.obj:	cmdparse.h dlldefs.h parsevalue.h prot_versacom.h \
		dsm2.h mutex.h guard.h dllbase.h os2_2w32.h types.h \
		cticalls.h master.h msg_pcrequest.h message.h collectable.h \
		devicetypes.h logger.h thread.h yukon.h utility.h
#ENDUPDATE#
