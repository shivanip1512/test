# nmake file YUKON 1.0

include ..\common\global.inc
include ..\common\rwglobal.inc

INCLPATHS+= \
-I$(DISPATCH)\include \
-I$(DATABASE)\include \
-I$(SIGNAL)\include \
-I$(SERVER)\include \
-I$(MSG)\include \
-I$(COMMON)\include \
-I$(CPARMS)\include \
-I$(PROCLOG)\include \
-I$(RW) \
-I$(BOOST) \


.PATH.cpp = .;$(R_FDR)

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
;$(SIGNAL)\include \
;$(TCLINC) \
;$(RW)


FDRINTERFACES=\

FDRINTERFACEOBJS=\
fdrdestination.obj  \
fdrlodestarinfo.obj  \
fdrpoint.obj \
mgr_fdrpoint.obj \
fdrinterface.obj \
fdrlodestarimport.obj \
fdrsocketlayer.obj \
fdrsocketconnection.obj \
fdrserverconnection.obj \
fdrclientconnection.obj \
fdrftpinterface.obj \
fdrpointlist.obj \
fdrsocketinterface.obj \
fdrasciiimportbase.obj \
textfileinterfaceparts.obj \
fdrtextfilebase.obj \
fdrsinglesocket.obj

# socketinterface.obj \
#fdrpointidmap.obj \
#fdrprotectedmaplist.obj \

CTIFDRLIBS=\
$(COMPILEBASE)\lib\cticparms.lib \
$(COMPILEBASE)\lib\ctibase.lib \
$(COMPILEBASE)\lib\ctimsg.lib \
$(COMPILEBASE)\lib\ctivg.lib \
$(COMPILEBASE)\lib\ctisvr.lib \
$(COMPILEBASE)\lib\proclog.lib \
$(COMPILEBASE)\lib\cmdline.lib \
wininet.lib

TESTINTERFACEDLLOBJS=\
testinterface.obj \

CTIFDRDLLS=\
cti_fdr.dll \
fdrtextexport.dll \
fdrtextimport.dll \
fdrdsm2import.dll \
fdrdsm2filein.dll \
fdrrdex.dll \
fdrcygnet.dll \
fdracs.dll \
fdrvalmet.dll \
fdrinet.dll \
fdrstec.dll \
fdrtristate.dll \
fdrrccs.dll \
fdrlodestarimport_enh.dll \
fdrlodestarimport_std.dll \
fdrtelegyr.dll

 

#  commented out because of bigtime Microsoft compiler problems.  i'm compiling
#    all of the code into one .obj, because it can't handle the code being contained
#    in more than one file.  #include "fdrinterface.cpp" is ugly, but the only way
#    around it for now.
# $(FDRINTERFACEOBJS) \


ALL:            $(CTIFDRDLLS) 

cti_fdr.dll: $(FDRINTERFACEOBJS) Makefile
                @echo Building  ..\$@
                @%cd $(OBJ)
                $(CC) $(DLLFLAGS) $(FDRINTERFACEOBJS) $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrtextexport.dll: fdrtextexport.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrtextexport.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)  

fdrtextimport.dll: fdrtextimport.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrtextimport.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)
		
fdrdsm2import.dll: fdrdsm2import.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrdsm2import.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)
		
fdrdsm2filein.dll: fdrdsm2filein.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrdsm2filein.obj $(INCLPATHS) $(RWLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib $(COMPILEBASE)\lib\cparms.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrrdex.dll: fdrrdex.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrrdex.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrcygnet.dll: fdrcygnet.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\cygnet\dclnd.lib copy ..\cygnet\dclnd.lib $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrcygnet.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib $(COMPILEBASE)\lib\dclnd.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)
		
fdracs.dll: fdracs.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdracs.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrvalmet.dll: fdrvalmet.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrvalmet.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrinet.dll: fdrinet.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrinet.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrstec.dll: fdrstec.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrstec.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)
                     
fdrtristate.dll: fdrtristate.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrtristate.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)
		
fdrrccs.dll: fdrrccs.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrrccs.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib $(COMPILEBASE)\lib\fdrinet.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)

fdrlodestarimport_enh.dll: fdrlodestarimport_enh.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrlodestarimport_enh.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)


fdrlodestarimport_std.dll: fdrlodestarimport_std.obj Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) fdrlodestarimport_std.obj $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)


fdrtelegyr.dll: $(FDRINTERFACEOBJS) Makefile
                @%cd $(OBJ)
                @echo Building  ..\$@
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\telegyr\lib\apiclilib.lib copy ..\telegyr\lib\apiclilib.lib $(COMPILEBASE)\lib
                -if exist ..\telegyr\lib\pllib.lib copy ..\telegyr\lib\pllib.lib $(COMPILEBASE)\lib
                -if exist ..\telegyr\lib\psapi.lib copy ..\telegyr\lib\psapi.lib $(COMPILEBASE)\lib
                $(CC) $(DLLFLAGS) $(FDRINTERFACEOBJS) $(INCLPATHS) $(RWLIBS) $(BOOSTLIBS) $(CTIFDRLIBS) $(COMPILEBASE)\lib\cti_fdr.lib /Fe..\$@
                -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
                -if exist ..\$@ copy ..\$@ $(YUKONOUTPUT)
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist ..\bin\$(@B).lib copy ..\bin\$(@B).lib $(COMPILEBASE)\lib
                @%cd $(CWD)


apiclilib.lib:
                -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
                -if exist telegyr\lib\apiclilib.lib copy telegyr\lib\apiclilib.lib $(COMPILEBASE)\lib
                -if exist ..\telegyr\lib\pllib.lib copy ..\telegyr\lib\pllib.lib $(COMPILEBASE)\lib
                -if exist ..\telegyr\lib\psapi.lib copy ..\telegyr\lib\psapi.lib $(COMPILEBASE)\lib


copy:
               -@if not exist $(YUKONOUTPUT) md $(YUKONOUTPUT)
               -@if exist bin\*.dll copy bin\*.dll $(YUKONOUTPUT)
               -@if not exist $(COMPILEBASE)\lib md $(COMPILEBASE)\lib
               -if exist bin\*.lib copy bin\*.lib $(COMPILEBASE)\lib


clean:
                -del *.obj *.dll *.ilk *.pdb *.lib *.exp *.map


deps:
                scandeps -Output makedll.mak *.cpp



#.cpp.obj :
#                @echo:
#                @echo Compiling: $< Output: ..\$@
#                @echo:
#                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRBASE -D_DLL_ENH_FDRLODESTARIMPORT -D_DLL_STD_FDRLODESTARIMPORT -D_DLL_FDRLODESTARIMPORT -D_DLL_FDRCYGNET -D_DLL_FDRACS -D_DLL_FDRVALMET -D_DLL_FDRINET -D_DLL_FDRRCCS -D_DLL_FDRTRISTATE -D_DLL_FDRSTEC -D_DLL_FDRRDEX -D_DLL_FDRDSM2IMPORT -D_DLL_FDRTELEGYRAPI -D_DLL_FDRTEXTIMPORT -D_DLL_FDRTEXTEXPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrtextexport.obj : fdrtextexport.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRTEXTEXPORT -DWINDOWS -Fo$(OBJ)\ -c $<

fdrtextimport.obj : fdrtextimport.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRTEXTIMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrdsm2import.obj : fdrdsm2import.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRDSM2IMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrdsm2filein.obj : fdrdsm2filein.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRDSM2FILEIN -DWINDOWS -Fo$(OBJ)\ -c $<
fdrrdex.obj : fdrrdex.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRRDEX -DWINDOWS -Fo$(OBJ)\ -c $<
fdrcygnet.obj : fdrcygnet.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRCYGNET -DWINDOWS -Fo$(OBJ)\ -c $<
fdracs.obj : fdracs.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRACS -DWINDOWS -Fo$(OBJ)\ -c $<
fdrvalmet.obj : fdrvalmet.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRVALMET -DWINDOWS -Fo$(OBJ)\ -c $<
fdrinet.obj : fdrinet.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRINET -DWINDOWS -Fo$(OBJ)\ -c $<
fdrstec.obj : fdrstec.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRSTEC -DWINDOWS -Fo$(OBJ)\ -c $<
fdrtristate.obj : fdrtristate.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRTRISTATE -DWINDOWS -Fo$(OBJ)\ -c $<
fdrrccs.obj : fdrrccs.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRRCCS -DWINDOWS -Fo$(OBJ)\ -c $<
fdrlodestarimport_enh.obj : fdrlodestarimport_enh.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_ENH_FDRLODESTARIMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrlodestarimport_std.obj : fdrlodestarimport_std.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_STD_FDRLODESTARIMPORT -DWINDOWS -Fo$(OBJ)\ -c $<
fdrtelegyr.obj : fdrtelegyr.cpp
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
		$(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRTELEGYRAPI -DWINDOWS -Fo$(OBJ)\ -c $<
 

.cpp.obj :
		@echo:
                @echo Compiling: $< Output: ..\$@
                @echo:
                $(RWCPPINVOKE) $(RWCPPFLAGS) $(DLLFLAGS) $(INCLPATHS) -D_DLL_FDRBASE -DWINDOWS -Fo$(OBJ)\ -c $<
	      
	        						


#UPDATE#
fdr.obj:	dlldefs.h ctibase.h ctinexus.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h cparms.h \
		CServiceConfig.h fdrservice.h cservice.h id_ctibase.h \
		utility.h id_build.h
fdracs.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdracs.h
fdrasciiimportbase.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h fdrasciiimportbase.h \
		fdrinterface.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
fdrclientconnection.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h fdrinterface.h message.h collectable.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h cparms.h \
		fdrdebuglevel.h fdrpointlist.h queues.h fdrsocketlayer.h \
		device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h
fdrcygnet.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h fdrcygnet.h \
		fdrinterface.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		fdrdestination.h fdr.h fdrdebuglevel.h fdrpointlist.h \
		queues.h device.h devicetypes.h
fdrdestination.obj:	fdrdestination.h dlldefs.h fdr.h pointdefs.h
fdrdsm2import.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrdsm2import.h fdrasciiimportbase.h fdrinterface.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h queues.h
fdrftpinterface.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h fdrftpinterface.h \
		fdrinterface.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
fdrinet.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h fdrinet.h
fdrinterface.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h cparms.h dbaccess.h \
		sema.h ctinexus.h resolvers.h pointtypes.h db_entry_defines.h \
		msg_multi.h collectable.h msg_pdata.h pointdefs.h message.h \
		msg_signal.h msg_ptreg.h msg_cmd.h msg_dbchg.h fdrinterface.h \
		connection.h exchange.h logger.h thread.h msg_reg.h queue.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h fdrdestination.h \
		fdr.h fdrdebuglevel.h fdrpointlist.h queues.h
fdrio.obj:	fdrio.h
fdriosocket.obj:	fdriosocket.h fdrio.h
fdrlodestarimport.obj:	cparms.h dlldefs.h msg_cmd.h message.h \
		ctidbgmem.h collectable.h pointtypes.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h TextFileInterfaceParts.h \
		fdrlodestarimport.h
fdrpoint.obj:	fdrpoint.h dlldefs.h pointtypes.h fdrdestination.h fdr.h \
		pointdefs.h types.h logger.h thread.h mutex.h guard.h
fdrpointidmap.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h fdrpointidmap.h \
		pointtypes.h resolvers.h yukon.h ctidbgmem.h \
		db_entry_defines.h logger.h thread.h
fdrpointlist.obj:	dllbase.h os2_2w32.h dlldefs.h types.h cticalls.h \
		dsm2.h mutex.h guard.h logger.h thread.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		fdrpointlist.h queues.h mgr_fdrpoint.h rtdb.h hashkey.h
fdrprotectedmaplist.obj:	dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		fdrprotectedmaplist.h queues.h
fdrrccs.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrserverconnection.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrclientconnection.h fdrinet.h fdrrccs.h
fdrrdex.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h fdrrdex.h
fdrserverconnection.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h \
		os2_2w32.h types.h cticalls.h dsm2.h mutex.h guard.h logger.h \
		thread.h fdrinterface.h message.h collectable.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h cparms.h \
		fdrdebuglevel.h fdrpointlist.h queues.h fdrsocketlayer.h \
		device.h devicetypes.h fdrsocketconnection.h \
		fdrserverconnection.h
fdrservice.obj:	cparms.h dlldefs.h logger.h thread.h mutex.h guard.h \
		fdrservice.h cservice.h
fdrsinglesocket.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsocketlayer.h device.h \
		devicetypes.h fdrsocketconnection.h fdrserverconnection.h \
		fdrsinglesocket.h
fdrsocketconnection.obj:	logger.h thread.h mutex.h dlldefs.h guard.h \
		fdrsocketlayer.h queues.h types.h device.h devicetypes.h \
		fdrsocketconnection.h
fdrsocketinterface.obj:	dllbase.h os2_2w32.h dlldefs.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrpointlist.h queues.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h pointdefs.h \
		fdrsocketconnection.h fdrsocketinterface.h fdrinterface.h \
		message.h ctidbgmem.h collectable.h connection.h exchange.h \
		msg_multi.h msg_pdata.h msg_signal.h yukon.h msg_ptreg.h \
		msg_reg.h queue.h cparms.h fdrdebuglevel.h
fdrsocketlayer.obj:	yukon.h ctidbgmem.h dlldefs.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrinterface.h message.h collectable.h connection.h \
		exchange.h msg_multi.h msg_pdata.h pointdefs.h msg_signal.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h cparms.h \
		fdrdebuglevel.h fdrpointlist.h queues.h fdrsocketinterface.h \
		fdrclientconnection.h fdrsocketlayer.h device.h devicetypes.h \
		fdrsocketconnection.h fdrserverconnection.h
fdrstec.obj:	cparms.h dlldefs.h msg_multi.h collectable.h msg_pdata.h \
		pointdefs.h message.h ctidbgmem.h msg_signal.h yukon.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h fdrstec.h \
		fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		hashkey.h fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
fdrtelegyr.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h dbaccess.h \
		sema.h hashkey.h resolvers.h db_entry_defines.h fdr.h \
		fdrdebuglevel.h fdrinterface.h mgr_fdrpoint.h rtdb.h \
		fdrpoint.h fdrdestination.h fdrpointlist.h queues.h \
		fdrtelegyr.h fdrasciiimportbase.h device.h devicetypes.h \
		telegyrgroup.h telegyrcontrolcenter.h
fdrtextexport.obj:	cparms.h dlldefs.h msg_cmd.h message.h ctidbgmem.h \
		collectable.h pointtypes.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h TextFileInterfaceParts.h \
		fdrtextexport.h
fdrtextfilebase.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h logger.h thread.h fdrtextfilebase.h \
		fdrinterface.h connection.h exchange.h msg_ptreg.h msg_reg.h \
		queue.h mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h \
		pointtypes.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h TextFileInterfaceParts.h
fdrtextimport.obj:	cparms.h dlldefs.h msg_cmd.h message.h ctidbgmem.h \
		collectable.h pointtypes.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h TextFileInterfaceParts.h \
		fdrtextimport.h
fdrtristate.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrtristate.h fdrftpinterface.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h queues.h
fdrvalmet.obj:	cparms.h dlldefs.h msg_multi.h collectable.h \
		msg_pdata.h pointdefs.h message.h ctidbgmem.h msg_signal.h \
		yukon.h dllbase.h os2_2w32.h types.h cticalls.h dsm2.h \
		mutex.h guard.h msg_ptreg.h msg_cmd.h msg_reg.h connection.h \
		exchange.h logger.h thread.h queue.h pointtypes.h \
		fdrsocketinterface.h queues.h fdrinterface.h mgr_fdrpoint.h \
		rtdb.h hashkey.h fdrpoint.h fdrdestination.h fdr.h \
		fdrdebuglevel.h fdrpointlist.h fdrsinglesocket.h \
		fdrsocketlayer.h device.h devicetypes.h fdrsocketconnection.h \
		fdrvalmet.h
inetinterface.obj:	dlldefs.h socketinterface.h fdrinterface.h \
		message.h ctidbgmem.h collectable.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h cparms.h fdrdebuglevel.h \
		fdrpointlist.h queues.h inetinterface.h fdriosocket.h fdrio.h
mgr_fdrpoint.obj:	dbaccess.h dlldefs.h dllbase.h os2_2w32.h types.h \
		cticalls.h dsm2.h mutex.h guard.h sema.h hashkey.h \
		resolvers.h pointtypes.h yukon.h ctidbgmem.h \
		db_entry_defines.h fdr.h pointdefs.h fdrdebuglevel.h \
		mgr_fdrpoint.h rtdb.h fdrpoint.h fdrdestination.h logger.h \
		thread.h utility.h
socketinterface.obj:	socketinterface.h dlldefs.h fdrinterface.h \
		message.h ctidbgmem.h collectable.h connection.h exchange.h \
		dllbase.h os2_2w32.h types.h cticalls.h dsm2.h mutex.h \
		guard.h logger.h thread.h msg_multi.h msg_pdata.h pointdefs.h \
		msg_signal.h yukon.h msg_ptreg.h msg_reg.h queue.h \
		mgr_fdrpoint.h rtdb.h hashkey.h fdrpoint.h pointtypes.h \
		fdrdestination.h fdr.h cparms.h fdrdebuglevel.h \
		fdrpointlist.h queues.h
telegyrcontrolcenter.obj:	telegyrcontrolcenter.h dlldefs.h fdr.h \
		pointdefs.h telegyrgroup.h fdrpoint.h pointtypes.h \
		fdrdestination.h types.h mutex.h logger.h thread.h guard.h
telegyrgroup.obj:	telegyrgroup.h dlldefs.h fdr.h pointdefs.h \
		fdrpoint.h pointtypes.h fdrdestination.h types.h
testinterface.obj:	dlldefs.h fdrinterface.h message.h ctidbgmem.h \
		collectable.h connection.h exchange.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h pointtypes.h fdrdestination.h fdr.h cparms.h \
		fdrdebuglevel.h fdrpointlist.h queues.h testinterface.h \
		fdrinterface.cpp dbaccess.h sema.h ctinexus.h resolvers.h \
		db_entry_defines.h msg_cmd.h msg_dbchg.h
textfileinterfaceparts.obj:	cparms.h dlldefs.h msg_multi.h \
		collectable.h msg_pdata.h pointdefs.h message.h ctidbgmem.h \
		msg_signal.h yukon.h dllbase.h os2_2w32.h types.h cticalls.h \
		dsm2.h mutex.h guard.h logger.h thread.h \
		TextFileInterfaceParts.h
fdrlodestarimport_enh.obj:	cparms.h dlldefs.h msg_cmd.h message.h \
		ctidbgmem.h collectable.h pointtypes.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h TextFileInterfaceParts.h \
		fdrlodestarinfo.h fdrlodestarimport.h fdrlodestarimport_enh.h
fdrlodestarimport_std.obj:	cparms.h dlldefs.h msg_cmd.h message.h \
		ctidbgmem.h collectable.h pointtypes.h dllbase.h os2_2w32.h \
		types.h cticalls.h dsm2.h mutex.h guard.h logger.h thread.h \
		fdrtextfilebase.h fdrinterface.h connection.h exchange.h \
		msg_multi.h msg_pdata.h pointdefs.h msg_signal.h yukon.h \
		msg_ptreg.h msg_reg.h queue.h mgr_fdrpoint.h rtdb.h hashkey.h \
		fdrpoint.h fdrdestination.h fdr.h fdrdebuglevel.h \
		fdrpointlist.h queues.h TextFileInterfaceParts.h \
		fdrlodestarinfo.h fdrlodestarimport.h fdrlodestarimport_std.h
		
#ENDUPDATE#
